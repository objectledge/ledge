package org.objectledge.upload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.mail.MailSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.PipelineProcessingException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.WebConstants;
import org.objectledge.web.parameters.RequestParameters;

/**
 * Analize the request and lookup the uploaded resources.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UploadValve.java,v 1.1 2004-01-13 13:06:39 pablo Exp $
 */
public class UploadValve implements Runnable, WebConstants
{
	/** context key to store the upload map */
	public static final String UPLOAD_CONTEXT_KEY = "upload_map";
	
	/**
	 * Retrieve the upload container.
	 *
	 * @param context the context.
	 * @param name the name of the item.
	 * @return the upload container, or <code>null</code> if not available.
	 */
	public UploadContainer getItem(Context context, String name) 
	{
		Map map =(Map)context.getAttribute(UPLOAD_CONTEXT_KEY);
		if (map == null) 
		{
			return null;
		}
		else
		{
			return (UploadContainer)uploadMap.get(name);
		}
	}
	
	/** web configurator */
	private WebConfigurator config; 
	
    /** the logger */
    private Logger logger;
    
    /** the mail system */
    private MailSystem mailSystem;
    
    /** the context */
    private Context context;

    /** upload map */
    private Map uploadMap;
    
    /**
     * Constructs the component.
     * 
     * @param config the config.
     * @param logger the logger.
     * @param mailSystem the mailSystem.
     * @param context the context.
     */
    public UploadValve(WebConfigurator config, Logger logger,  
     				    MailSystem mailSystem, Context context)
    {
    	this.config = config;
    	this.logger = logger;
    	this.mailSystem = mailSystem;
    	this.context = context;
    }
    
    /**
     * Run the valve.
     *
     */
    public void run()
    {
        uploadMap = new HashMap();
        HttpContext httpContext = HttpContext.retrieve(context);
        if(httpContext.getRequest().getContentLength() > config.getUploadLimit())
        {
            logger.debug("The request size exceeds upload limits");
            return;
        }
        String contentType = httpContext.getRequest().getContentType();
        if (contentType!=null && contentType.startsWith("multipart/form-data")) 
        {
            try
            {
                Session session = mailSystem.getSession();
                String headers = "Content-Type: " + 
                	   httpContext.getRequest().getContentType() +"\n\n";
                InputStream uploadStream = new SequenceInputStream(
                    new ByteArrayInputStream(
                	    headers.getBytes(httpContext.getRequest().getCharacterEncoding())),
                    httpContext.getRequest().getInputStream());
                MimeMessage message = new MimeMessage(session, uploadStream);
                
                // parse the rest of the parts
                Object content = message.getContent();
                if (content instanceof MimeMultipart)
                {
                    MimeMultipart mm = (MimeMultipart) content;
                    int partsCounter = mm.getCount();
                    logger.debug("UploadValve has detected more than one item - parse the rest");
                    for (int i = 0; i < partsCounter; i++) 
                    {
                        BodyPart part = mm.getBodyPart(i);
                        parsePart(context, part);
                    }
                }
                
                // store the upload map into the RunData
                context.setAttribute(UPLOAD_CONTEXT_KEY,uploadMap);
                logger.debug("UploadValve has stored the upload map in the context");
            }
            catch (IOException e) 
            {
                throw new PipelineProcessingException("UploadValve exception",e);
            }
            catch (MessagingException e) 
            {
				throw new PipelineProcessingException("UploadValve exception",e);
            }
        }
        else
        {
            logger.debug("UploadValve found "+contentType+" - finish analize");
        }
    }

    private void parsePart(Context context, Part part)
        throws MessagingException, IOException
    {
		//HttpContext httpContext;
    	
        String filename = part.getFileName();
        // retrieve mime-type
        String[] mimeTypeTab = part.getHeader("Content-Type");
        String mimeType = "";
        if (mimeTypeTab != null && mimeTypeTab.length > 0) 
        {
            mimeType = mimeTypeTab[0];
        }
            
        // retrieve from header html name for that input
        String[] head = part.getHeader("Content-Disposition");
        String name="";
        for (int j=0; head != null && j< head.length ; j++) 
        {
            String header = head[j];
            logger.debug("HEADER " + header);
            if (header != null && header.startsWith("form-data")) 
            {
                int position = header.indexOf(" name=");
                if (position != -1) 
                {
                    name = header.substring(position+7,header.indexOf("\"",position+7));
                }
                position = header.indexOf(" filename=");
                if (position != -1) 
                {
                    String fname = header.substring(position+11,header.indexOf("\"",position+11));
                    if(fname!=null && (!fname.equals("")))
                    {
                        int backslash = fname.lastIndexOf("\\");
                        if(backslash != -1)
                        {
                            filename = fname.substring(backslash+1);
                        }
                    }
                }
            }
        }
			    
        // if no filename given - it's an ordinary form field
        if (filename == null || filename.equals("")) 
        {
            InputStream is = part.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[is.available() > 0 ? is.available() : 32];
            int count = 0;
            while(count >= 0)
            {
                count = is.read(buffer,0,buffer.length);
                if(count > 0)
                {
                    baos.write(buffer, 0, count);
                }
            }
            byte[] contents = baos.toByteArray();
           
			HttpContext httpContext = HttpContext.retrieve(context); 
            String encoding = (String)httpContext.getRequest().getSession().
                getAttribute(ENCODING_SESSION_KEY);
            if(encoding == null)
            {
                encoding = config.getDefaultEncoding();
            }
            String field = new String(contents, encoding);
            Parameters parameters = RequestParameters.retrieve(context);
            parameters.add(name, field); 
        } 
        else 
        {
            int size = part.getSize();
            UploadContainer container = new UploadContainer(name,filename,
                                                            (size != -1 ? size : 0),mimeType);
            int result = container.load(part.getInputStream());
			if (result == -1) 
			{
				logger.error("UploadValve - couldn't write the uploaded data to the container");
		    }
			else
			{
				logger.debug("UploadValve - the uploaded data has been successfully written " +							 "to the container");
			}
            uploadMap.put(name,container);
            logger.debug("Upload Hook - craeted container "+name);
        }
    }
    
}

