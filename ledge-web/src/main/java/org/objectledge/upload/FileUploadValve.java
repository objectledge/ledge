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
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.WebConstants;

/**
 * Analize the request and lookup the uploaded resources.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FileUploadValve.java,v 1.8 2004-02-03 11:30:39 pablo Exp $
 */
public class FileUploadValve 
    implements Valve, WebConstants
{
    /** the web configurator */
    private WebConfigurator webConfigurator;
    
    /** the logger */
    private Logger logger;
    
    /** the mail system */
    private MailSystem mailSystem;

    /** web configurator */
    private FileUpload fileUpload; 
    
    /**
     * Constructs the component.
     * 
     * @param logger the logger.
     * @param fileUpload the file upload component.
     * @param mailSystem the mailSystem.
     */
    public FileUploadValve(WebConfigurator webConfigurator, Logger logger, 
                           FileUpload fileUpload, MailSystem mailSystem)
    {
        this.webConfigurator = webConfigurator;
    	this.logger = logger;
        this.fileUpload = fileUpload;
    	this.mailSystem = mailSystem;
    }
    
    /**
     * Run the valve.
     *
     * @param context the context.
     * @throws ProcessingException if the processing fails.
     */
    public void process(Context context)
        throws ProcessingException
    {
        Map uploadMap = new HashMap();
        HttpContext httpContext = HttpContext.getHttpContext(context);
        if(httpContext.getRequest().getContentLength() > fileUpload.getUploadLimit())
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
                session.setDebug(true);
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
                        parsePart(context, part, uploadMap);
                    }
                }
                
                // store the upload map into the RunData
                context.setAttribute(FileUpload.UPLOAD_CONTEXT_KEY, uploadMap);
                logger.debug("UploadValve has stored the upload map in the context");
            }
            catch (IOException e) 
            {
                throw new ProcessingException("UploadValve exception",e);
            }
            catch (MessagingException e) 
            {
				throw new ProcessingException("UploadValve exception",e);
            }
        }
        else
        {
            logger.debug("UploadValve found "+contentType+" - finish analize");
        }
    }

    private void parsePart(Context context, Part part, Map uploadMap)
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
           
			HttpContext httpContext = HttpContext.getHttpContext(context); 
            String encoding = (String)httpContext.getRequest().getSession().
                getAttribute(ENCODING_SESSION_KEY);
            if(encoding == null)
            {
                encoding = webConfigurator.getDefaultEncoding();
            }
            String field = new String(contents, encoding);
            Parameters parameters = RequestParameters.getRequestParameters(context);
            parameters.add(name, field); 
        } 
        else 
        {
            UploadContainer container = new UploadContainer(name, filename, mimeType,
                part.getSize(), part.getInputStream());
            uploadMap.put(name, container);
            logger.debug("Upload Hook - craeted container "+name);
        }
    }
    
}

