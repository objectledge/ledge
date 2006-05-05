package org.objectledge.mail;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.mail.impl.FileSystemDataSource;
import org.objectledge.templating.Templating;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;

/**
 * Mail system component.
 *
 * @author <a href="mailto:rkrzewsk@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: MailSystem.java,v 1.14 2006-05-05 08:43:11 rafal Exp $
 */
public class MailSystem
{
    // constants /////////////////////////////////////////////////////////////
    
    /** The default session name (default). */
    public static final String DEFAULT_SESSION = "default";
    
	/** The regex email address patter. */
	public static final String EMAIL_ADDRESS_PATTERN = 
        "^[-!\\.\\w]+@[^\\.][-a-zA-Z0-9]+(\\.[-a-zA-Z0-9]+)+";
        
    /** logger */
    private Logger logger;

    /** thread pool */
    private ThreadPool threadPool;

    /** file service */
    private FileSystem fileSystem;
    
    /** templating */
    private Templating templating;
    
    /** map with mime types */
    private MimetypesFileTypeMap mimeMap;
    
    /** mail queue */
    private LinkedList<LedgeMessage> mailQueue = new LinkedList<LedgeMessage>();
    
    /** server list - not implemented */
    private Map<String,Session> sessionsMap = new HashMap<String,Session>();

    /** default provider */
    private String defaultSession;

    /** system mail logger */
    private String debugAddress;
    
    /** system address */
    private String systemAddress;

	/** email address validator pattern */
	private Pattern emailAddressPattern; 
	
    /**
     * No-arg ctor for mocking.
     */
    protected MailSystem()
    {
        // intentionally left blank
    }
    
	/**
     * Initializes the component.
     * 
     * @param config the configuration.
     * @param logger the logger.
     * @param fileSystem the file system.
     * @param templating the templating.
     * @param threadPool the threadPool.
     */
    public MailSystem(Configuration config, Logger logger,
    				   FileSystem fileSystem, Templating templating,
                       ThreadPool threadPool)
    {
		this.logger = logger;
		this.fileSystem = fileSystem;
		this.templating = templating;
        this.threadPool = threadPool;
        try
        {
            String mimeTypeFilePath = config.getChild("mime_type_file_path")
            	.getValue("/config/mime.types");
            mimeMap = new MimetypesFileTypeMap(fileSystem.getInputStream(mimeTypeFilePath));
            debugAddress = config.getChild("big_brother_email").getValue("");
            systemAddress = config.getChild("system_email").getValue("");
            String pattern = config.getChild("email_pattern")
            	.getValue(EMAIL_ADDRESS_PATTERN);
            emailAddressPattern = Pattern.compile(pattern);

            Configuration sessionsNode = config.getChild("sessions");
            if (sessionsNode != null)
            {
                Configuration[] sessions = sessionsNode.getChildren("session");
                for (int i = 0; i < sessions.length; i++)
                {
                    String name = sessions[i].getAttribute("name");
                    boolean isDefault = sessions[i].getAttributeAsBoolean("default", false);
                    if (i == 0 || isDefault)
                    {
                        defaultSession = name;
                    }
                    Properties sessionProperties = new Properties();
                    Configuration[] properties = sessions[i].getChildren("property");
                    for (int j = 0; j < properties.length; j++)
                    {
                        String propertyName = properties[j].getAttribute("name");
                        String propertyValue = properties[j].getAttribute("value", null);
                        if (propertyValue == null)
                        {
                            propertyValue = properties[j].getValue();
                        }
                        sessionProperties.setProperty("mail." + propertyName, propertyValue);
                    }
                    Session session = Session.getInstance(sessionProperties,
                    	new LedgeAuthenticator(sessions[i].getChild("credentials")));
                    sessionsMap.put(name, session);
                }
                if(sessionsMap.size() == 0)
                {
                    Properties props = new Properties();
                    Session session = Session.getDefaultInstance(props, null);
                    sessionsMap.put("default", session);
                    defaultSession = "default";
                }
            }
            threadPool.runDaemon(new DeliverMailTask());
        }
        catch (ConfigurationException e)
        {
            throw new ComponentInitializationError("invalid configuration", e);
        }
    }

    /**
     * Returns the default mail session.
     *
     * @return default mail session.
     */
    public Session getSession() 
    {
        return getSession(defaultSession);
    }
    
    /**
     * Returns a mail session declared in the service configuration.
     *
     * @param name the name of the server.
     * @return the mail session.
     */
    public Session getSession(String name) 
    {
        return (Session)sessionsMap.get(name);
    }

    /**
     * Returns a <code>DataSource</code> backed by the <code>FileSystem</code>.
     *
     * @param name the pathname of the file.
     * @return the data source.
     */
    public DataSource getDataSource(String name)
    {
    	String contentType = getContentType(name);
        return new FileSystemDataSource(fileSystem, name, contentType);
    }

    /**
     * Create a new message based on default session.
     *
     * @return the mail message wrapper.
     */
    public LedgeMessage newMessage()
    {
        return newMessage(defaultSession);
    }
    
    /**
     * Create a new message based on session defined in the configuration file.
     *
     * @param sessionName the name of the server.
     * @return the mail message wrapper.
     */
    public LedgeMessage newMessage(String sessionName)
    {
        return new LedgeMessage(this, logger, templating, getSession(sessionName));
    }
    
    /**
     * Create a new message based on a pre-existing JavaMali message object and the default session.
     *
     * @param message the JavaMail message.
     * @return the mail message wrapper.
     */
    public LedgeMessage newMessage(Message message)
    {
        return newMessage(defaultSession, message);
    }
    
    /**
     * Create a new message based on a pre-existing JavaMali message object.
     *
     * @param sessionName the name of the session.
     * @param message the JavaMail message.
     * @return the mail message wrapper.
     */
    public LedgeMessage newMessage(String sessionName, Message message)
    {
        return new LedgeMessage(this, logger, templating, getSession(sessionName), message);
    }
    
    /**
     * Send the message.
     *
     * <p>If the <code>wait</code> parameter is false, the sending process will
     * proceed asynchronosly, and the method will return immediately,
     * otherwise the method will return only after the sending process is
     * complete.</p> 
     *
     * @param message the ledge mail message wrapper.
     * @param wait <code>true</code> to wait for operation completion.
     * @throws MessagingException thrown if message cannot be send.
     */
    public void send(LedgeMessage message, boolean wait) 
        throws MessagingException
    {
        if (wait)
        {
            Message msg = message.getMessage();
            if (debugAddress != null && (!debugAddress.equals("")))
            {
                msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(debugAddress));
            }
            Transport.send(msg);
        }
        
        else 
        {
            synchronized(mailQueue)
            {
                mailQueue.addLast(message);
                mailQueue.notify();
            }
        }
    }

    /**
     * Guess MIME type from file name extension.
     *
     * <p>If the extension is missing or unknown
     * <code>application/octet-stream</code> type will be returned.</p>
     * 
     * @param filename the filename with extension.
     * @return the content type.
     */
    public String getContentType(String filename)
    {
        if (mimeMap != null) 
        {
            return mimeMap.getContentType(filename);
        }
        else
        {
            return "application/octet-stream";
        }
    }

	/**
	 * Checks if given address is valid.
	 * 
	 * @param address the address
	 * @return <code>true</code> if address is valid.
	 */
	public boolean isValidEmailAddress(String address)
	{
		Matcher m = emailAddressPattern.matcher(address);
		return m.matches();
	}
    
    /**
     * Get the system address.
     * 
     * @return the system address.
     */
    public String getSystemAddress()
    {
        return systemAddress;
    }
    
	    
    /**
     * The authenticator .
     */
    public static class LedgeAuthenticator
        extends Authenticator
    {
        private Map<String,Map<String,String>> authInfo = new HashMap<String,Map<String,String>>();

		/**
		 * Create new Ledge authentication.
		 * 
		 * @param credentialsConfig the configuration.
		 * @throws ConfigurationException if happen.
		 */
        public LedgeAuthenticator(Configuration credentialsConfig)
        	throws ConfigurationException
        {
			if (credentialsConfig != null)
			{
				Configuration[] credentials = credentialsConfig.getChildren("credential");
				for (int i = 0; i < credentials.length; i++)
				{
					String protocol = credentials[i].getAttribute("protocol");
					String userName = credentials[i].getAttribute("username");
					String password = credentials[i].getAttribute("password");
					addCredentials(protocol, userName, password);
				}
			}
        }

		/**
		 * Add credentials for the specified protocol.
         * 
         * @param protocol the protocol.
         * @param username the user name.
         * @param password the password.
		 */
        public void addCredentials(String protocol, String username, String password)
        {
			Map<String,String> protocolCredentials = authInfo.get(protocol);
			if(protocolCredentials == null)
			{
				protocolCredentials = new HashMap<String,String>();
				authInfo.put(protocol, protocolCredentials);
			}
			protocolCredentials.put(username, password);
       	}

		/**
		 * {@inheritDoc}
		 */
        public PasswordAuthentication getPasswordAuthentication()
        {
            Map credentials = (Map)authInfo.get(getRequestingProtocol());
            if(credentials == null)
            {
                credentials = (Map)authInfo.get("");
            }
            if(credentials != null)
            {
                String user = getDefaultUserName();
                if((user == null || user.equals("")) && credentials.size() == 1)
                {
                    user = (String)credentials.keySet().toArray()[0];
                }
                String pass = (String)credentials.get(user);
                if(pass != null)
                {
                    return new PasswordAuthentication(user, pass);
                }
            }
            return null;
        }
    }
    
    /**
     * Deaemon task responsible for sending enqueued e-mail messages.
     */
    private class DeliverMailTask extends Task
    {
        /**
         * {@inheritDoc}
         */
        public String getName()
        {
            return "E-mail delivery task";
        }
        
        /**
         * {@inheritDoc}
         */
        public void process(Context context)
        {
            loop : while (!Thread.interrupted())
            {
                Object object;
                synchronized (mailQueue)
                {
                    if (mailQueue.size() == 0)
                    {
                        try
                        {
                            mailQueue.wait();
                        }
                        catch (InterruptedException e)
                        {
                            break loop;
                        }
                    }
                    object = mailQueue.removeFirst();
                }
                try
                {
                    ((LedgeMessage)object).send(true);
                }
                catch (Exception e)
                {
                    logger.error("DeliverMailTask couldn't send enqueued e-mail", e);
                }
            }
        }
    }
}
