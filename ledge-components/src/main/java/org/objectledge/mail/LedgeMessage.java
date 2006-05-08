package org.objectledge.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.jcontainer.dna.Logger;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplateNotFoundException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;

/**
 * Wraping class around the javaMail message.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:rkrzewsk@caltha.pl">Rafal Krzewski</a>
 * @version $Id: LedgeMessage.java,v 1.11 2006-05-08 12:24:02 rafal Exp $
 */
public class LedgeMessage
{
    // instance variables ////////////////////////////////////////////////////

    /** The embeded JavaMail message. */
    private Message message;

    /** Message content already prepared? */
    private boolean prepared = false;

    /** The template. */
    private Template template = null;

    /** The template context. */
    private TemplatingContext context;

    /** Message body. */
    private String text;
    
    /** Media type. */
    private String media = "PLAIN";

    /** Message body character encoding. */
    private String encoding = "UTF-8";

    /** The logger. */
    private Logger logger;

    /** The MailSystem. */
    private MailSystem mailSystem;

    /** The Templating system. */
    private Templating templating;

    /** Attachment list. */
    private List<DataSource> attachments = new ArrayList<DataSource>();
    
    /** Related content map. */
    private Map related = new HashMap();

    // initialization ////////////////////////////////////////////////////////

    /**
     * Constructs a new message.
     * 
     * @param mailSystem the mailSystem.
     * @param logger the logger.
     * @param templating the templating system.
     * @param session the mail session.
     */
    public LedgeMessage(MailSystem mailSystem, Logger logger,
    					Templating templating, Session session) 
    {
        this.mailSystem = mailSystem;
        this.logger = logger;
        this.templating = templating;
        message = new MimeMessage(session);
    }
    
    /**
     * Constructs a new message.
     * 
     * @param mailSystem the mailSystem.
     * @param logger the logger.
     * @param templating the templating system.
     * @param session the mail session.
     * @param message the JavaMail Message 
     */
    public LedgeMessage(MailSystem mailSystem, Logger logger,
                        Templating templating, Session session, Message message)
    {
        this.mailSystem = mailSystem;
        this.logger = logger;
        this.templating = templating;
        this.message = message;
        this.prepared = true;
    }
    
    /**
     * Directly set text instead of using template. The media will be set to
     * <code>text/plain</code>.
     *
     * @param text the message body
     */
    public void setText(String text)
    {
        setText(text,"PLAIN");
    }
    
    /**
     * Directly set text instead of using template.
     *
     * <p>Valid media identifiers are HTML and PLAIN.</p>
     *
     * @param text the data source.
     * @param media type of media.
     */
    public void setText(String text, String media)
    {
        this.text = text;
        this.media = media;
    }

    /**
     * Sets the character encoding for the message body.
     *
     * @param encoding the encoding
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Use template for generating message body.
     *
     * <p>The media identifier will be used for building full template
     * pathname, and setting message content type. Valid media types are HTML
     * and PLAIN.</p>
     *
     * @param locale the locale.
     * @param media the media type.
     * @param templateName the name of the template.
     * @throws TemplateNotFoundException thrown if template not found.
     */
    public void setTemplate(Locale locale, String media, String templateName) 
        throws TemplateNotFoundException
    {
        template = templating.
        	getTemplate("/messages/"+templateName+"_"+locale.toString()+"_"+media);
        this.media = media;
    }    

    /**
     * Return the template context that will be used for rendering the message
     * body.
     *
     * @return the context
     */
    public TemplatingContext getContext()
    {
        if(context == null)
        {
            context = templating.createContext();
        }
        return context;
    }

    /**
     * Return the embeded <code>javax.mail.Message</code>.
     *
     * @return the message
     */
    public Message getMessage()
    {
        return message;
    }

    /**
     * Sets the embedded message.
     * 
     * @param message the message.
     */
    public void setMessage(Message message)
    {
        this.message = message;
        prepared = true;
    }

    /**
     * Returns the attachment list for external modification.
     *
     * <p>Add <code>DataSource</code> objects containing the message
     * attchments to this list.</p>
     * 
     * @return the attachemnt list.
     */
    public List<DataSource> getAttachments()
    {
        return attachments;
    }

    /**
     * Returns the related content map.
     *
     * <p>Put mappings of content identifiers to <code>DataSource</code>
     * objects containg the message body's related content into this map.</p>
     *
     * @return the related content map.
     */
    public Map getRelatedContent()
    {
        return related;
    }

    /**
     * Prepares the message contents for sending.
     *
     * <p>This method will render the template if was set, and encapsulate
     * message body in a proper MIME multipart message if any related content
     * and/or attachments are present.</p>
     *
     * <p>This method is implicitly called before the message is sent for the
     * first time. If you wish to send the same message again modifying some
     * of the headers (most notably the recipient), you can set the headers
     * of the embeded JavaMail Message and call {@link #send()} again. If you
     * want to resend the message, but you wish to modify the contents of the
     * templating context, and / or attachment lists, you must explicitly call
     * this method before calling <code>send</code> so that he message content
     * is rendered and encapsulated again.</p>
     * 
     * @throws MergingException thrown when merging failed.
     * @throws MessagingException thrown when unsupported charset found.
     * @return contents of the Message-Id header of the generated message.  
     */
    public String prepare() 
        throws MergingException, MessagingException
    {
        String content = null;
        String contentType = null;
        
        if (template != null)
        {
            content = template.merge(getContext());
        }
        else 
        {
            content = text;
        }
        
        if (media == null || media.equals(""))
        {
            contentType = "text/plain";
        }
        else
        {
            contentType = "text/"+media.toLowerCase();
        }
        String charset = MimeUtility.mimeCharset(encoding);
        if(charset != null && !charset.equals(""))
        {
            contentType = contentType + "; charset="+charset;
        }

        if(template != null)
        {
            String subject = getMessage().getSubject();
            if(subject == null || subject.equals(""))
            {
                subject = (String)getContext().get("subject");
                if(subject != null)
                {
                    try
                    {
                        subject = MimeUtility.encodeWord(subject, charset, "Q");
                    }
                    catch(Exception e)
                    {
                        throw new MessagingException("unsupported encoding "+charset);
                    }
                    getMessage().setSubject(subject);
                }
            }
        }

        MimeMultipart relatedMultipart = null;
        MimeMultipart mixedMultipart = null;
        if(related.size() > 0)
        {
            relatedMultipart = new MimeMultipart("related");
            BodyPart relatedBodyPart = new MimeBodyPart();
            relatedBodyPart.setContent(content, contentType);
            relatedMultipart.addBodyPart(relatedBodyPart);
            Iterator i = related.entrySet().iterator();
            while(i.hasNext())
            {
                Map.Entry mapping = (Map.Entry)i.next();
                String cid = (String)mapping.getKey();
                DataSource part = (DataSource)mapping.getValue();
                relatedBodyPart = new MimeBodyPart();
                relatedBodyPart.setDisposition(Part.INLINE);
                relatedBodyPart.setHeader("Content-ID", cid);
                relatedBodyPart.setDataHandler(new DataHandler(part));
                relatedMultipart.addBodyPart(relatedBodyPart);
            }
        }
        if(attachments.size() > 0)
        {
            mixedMultipart = new MimeMultipart("mixed");
            BodyPart mixedBodyPart = new MimeBodyPart();
            mixedMultipart.addBodyPart(mixedBodyPart);
            if(related.size() > 0)
            {
                mixedBodyPart.setContent(relatedMultipart);
            }
            else
            {
                mixedBodyPart.setContent(content, contentType);
            }
            Iterator i = attachments.iterator();
            while(i.hasNext())
            {
                DataSource part = (DataSource)i.next();
                mixedBodyPart = new MimeBodyPart();
                mixedBodyPart.setDisposition(Part.ATTACHMENT);
                mixedBodyPart.setDataHandler(new DataHandler(part));
                mixedBodyPart.setFileName(part.getName());
                mixedMultipart.addBodyPart(mixedBodyPart);
            }
        }
        if(attachments.size() > 0)
        {
            message.setContent(mixedMultipart);
        }
        else if(related.size() > 0)
        {
            message.setContent(relatedMultipart);
        }
        else
        {
            message.setContent(content, contentType);
        }
        prepared = true;
        try
        {
            // trigger Message-Id generation
            message.writeTo(new ByteArrayOutputStream());
            return message.getHeader("Message-Id")[0];
        }
        catch(IOException e)
        {
            throw new MessagingException("failed to serialize message", e);
        }
    }
    
    /**
     * Serialize the message into an array of bytes.
     * 
     * @return the serialized message.
     * @throws MergingException if there is a problem mergin the template.
     * @throws MessagingException if there is a problem preparing the message.
     * @throws IOException if there is a problem serializing the message.
     */
    public byte[] getMessageBytes()
        throws MessagingException, MergingException, IOException
    {
        if(!prepared)
        {
            prepare();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ((MimeMessage)message).writeTo(baos);
        return baos.toByteArray();
    }
    
    /**
     * Send the message and continue processing asynchronously.
     *
     * <p>This method will implicitly call {@link #prepare()} method if it was
     * not called before.</p>
     * 
     * @throws MergingException thrown when merging failed.
     * @throws MessagingException thrown when unsupported charset found.
     */
    public void send() 
        throws MessagingException, MergingException
    {
        if(!prepared)
        {
            prepare();
        }
        mailSystem.send(this, false);
    }

    /**
     * Send the message. 
     *
     * <p>If the <code>wait</code> parameter is false, the sending process will
     * proceed asynchronosly, and the method will return immediately,
     * otherwise the method will return only after the sending process is
     * complete.</p> 
     *
     * @param wait <code>true</code> to wait for operation completion.
     * @throws MergingException thrown when merging failed.
     * @throws MessagingException thrown when unsupported charset found.
     */
    public void send(boolean wait) 
        throws MessagingException, MergingException
    {
        if(!prepared)
        {
            prepare();
        }
        mailSystem.send(this,wait);
    }
}
