package org.objectledge.fax;

import gnu.hylafax.HylaFAXClient;
import gnu.hylafax.HylaFAXClientProtocol;
import gnu.hylafax.Job;
import gnu.inet.ftp.FtpClient;
import gnu.inet.ftp.ServerResponseException;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;

/**
 * Fax manager implementation based on gnu.hylafax.* library.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski </a>
 * @version $Id: HylafaxManager.java,v 1.2 2005-02-09 22:02:13 rafal Exp $
 */
public class HylafaxManager implements FaxManager
{
    /** logging facility */
    private Logger logger;

    /** fax server ip */
    private String host;

    /** fax server port */
    private int port;

    /** fax user login */
    private String login;

    /** fax user password */
    private String password;

    /** default page size */
    private String pageSize;

    /** default vertical resolution */
    private int resolution;

    /** debug on switch */
    private boolean verbose;

    /** default kill time */
    private String killtime;

    /** default max dials */
    private int maxdials;

    /** default max tries */
    private int maxtries;

    /** default priority */
    private int priority;

    /** default pagechop */
    private String pagechop;

    /** default chop treshold */
    private int chopthreshold;

    /** default notification address */
    private String notificationAddress;

    /** fax client */
    private HylaFAXClient client;

    /** ftp client */
    private FtpClient ftpClient;

    /** The thread pool component */
    private ThreadPool threadPool;

    /** synchronization object */
    private Object pingObject;

    /** ping delay */
    private long pingDelay;

    /** connected flag */
    private boolean connected;

    /**
     * The component constructor.
     * 
     * @param config
     *            the configuration.
     * @param logger
     *            the logger.
     * @param threadPool
     *            the thread pool.
     */
    public HylafaxManager(Configuration config, Logger logger, ThreadPool threadPool)
    {
        this.logger = logger;
        this.threadPool = threadPool;
        host = config.getChild("host").getValue("localhost");
        port = config.getChild("port").getValueAsInteger(12345);
        login = config.getChild("login").getValue("top");
        password = config.getChild("password").getValue("secret");
        pageSize = config.getChild("pagesize").getValue("");
        verbose = config.getChild("verbose").getValueAsBoolean(true);
        resolution = config.getChild("resolution").getValue("high").equals("high") ? 196 : 98;
        killtime = config.getChild("killtime").getValue("000259");
        maxdials = config.getChild("maxdials").getValueAsInteger(12);
        maxtries = config.getChild("maxtries").getValueAsInteger(3);
        priority = config.getChild("priority").getValueAsInteger(127);
        pagechop = config.getChild("pagechop").getValue("default");
        chopthreshold = config.getChild("chopthreshold").getValueAsInteger(3);
        notificationAddress = config.getChild("notification_address").getValue("root@localhost");
        pingDelay = config.getChild("ping_delay").getValueAsLong(600000);
        connected = false;
    }

    /**
     * {@inheritDoc}
     */
    public void sendFax(String destinationAddress, String content, String encoding, boolean notify,
        String notificationAddress, boolean highResolution, String from) throws FaxManagerException
    {
        try
        {
            Parameters parameters = new DefaultParameters();
            parameters.add("notify", notify);
            parameters.add("notificationAddress", notificationAddress);
            if(highResolution)
            {
                parameters.add("resolution", 196);
            }
            else
            {
                parameters.add("resolution", 98);
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes(encoding));
            sendFax(destinationAddress, from, bais, parameters);
        }
        catch(Exception e)
        {
            throw new FaxManagerException("Failed to queue fax", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sendFax(String destinationAddress, String from, InputStream content,
        Parameters parameters) throws FaxManagerException
    {
        prepare();
        try
        {
            String remoteFilename = client.putTemporary(content);
            Job job = client.createJob();
            job.addDocument(remoteFilename);
            job.setDialstring(destinationAddress);
            job.setFromUser(from);
            job.setVerticalResolution(parameters.getInt("resolution", resolution));

            if(parameters.getBoolean("notify", false))
            {
                job.setNotifyType(Job.NOTIFY_DONE);
            }
            else
            {
                job.setNotifyType(Job.NOTIFY_NONE);
            }
            job.setNotifyAddress(parameters.get("notificationAddress", notificationAddress));
            job.setKilltime(parameters.get("killtime", killtime));
            job.setMaximumDials(parameters.getInt("maxdials", maxdials));
            job.setMaximumTries(parameters.getInt("maxtries", maxtries));
            job.setPriority(parameters.getInt("priority", priority));
            Dimension dimension = (Dimension)Job.pagesizes
                .get(parameters.get("pagesize", pageSize));
            job.setPageDimension(dimension);
            job.setChopThreshold(parameters.getInt("chopthreshold", chopthreshold));
            client.submit(job);
        }
        catch(Exception e)
        {
            throw new FaxManagerException("Failed to queue fax", e);
        }
    }

    /**
     * Initializes the service.
     */
    public void start()
    {
        threadPool.runDaemon(new PingTask());
    }

    /**
     * Initializes the service.
     */
    public void stop()
    {
        //TODO stop the task.
    }
    
    
    /**
     * Prepare the fax manager.
     * 
     * @throws FaxManagerException
     */
    synchronized void prepare() throws FaxManagerException
    {
        if(!connected)
        {
            try
            {
                client = new HylaFAXClient();
                client.setDebug(verbose);
                client.open(host, port);

                if(client.user(login))
                {
                    client.pass(password);
                }
                client.noop();
                client.tzone(HylaFAXClientProtocol.TZONE_LOCAL);

                ftpClient = new FtpClient();
                ftpClient.setDebug(verbose);
                ftpClient.open(host, port);
                if(ftpClient.user(login))
                {
                    ftpClient.pass(password);
                }
                ftpClient.noop();
            }
            catch(ServerResponseException e)
            {
                throw new FaxManagerException("Couldn't start fax service", e);
            }
            catch(IOException e)
            {
                throw new FaxManagerException("Couldn't start fax service", e);
            }
            connected = true;
        }
    }

    /**
     * Disconnect from server.
     * 
     * @throws FaxManagerException
     */
    synchronized void destroy() throws FaxManagerException
    {
        if(connected)
        {
            connected = false;
            try
            {
                client.quit();
                ftpClient.quit();
            }
            catch(Exception e)
            {
                throw new FaxManagerException("Couldn't disconnect from server", e);
            }
        }
    }

    private class PingTask extends Task
    {
        /**
         * Main processing loop.
         */
        public void process(Context context)
        {
            loop: while(!Thread.interrupted())
            {
                {
                    try
                    {
                        try
                        {
                            prepare();
                            client.noop();
                        }
                        catch(ServerResponseException e)
                        {
                            logger.error("Couldn't ping to server", e);
                            destroy();
                        }
                        catch(IOException e)
                        {
                            logger.error("Couldn't ping to server", e);
                            destroy();
                        }
                    }
                    catch(FaxManagerException e)
                    {
                        logger.error("Couldn't manage the hylafax connection");
                    }
                    synchronized(pingObject)
                    {
                        try
                        {
                            pingObject.wait(pingDelay);
                        }
                        catch(InterruptedException e)
                        {
                            break loop;
                        }
                    }
                }
            }
        }
        
        public String getName()
        {
            return "Hylafax Task";
        }
    }
}