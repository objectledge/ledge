package pl.caltha.forms.internal.model;

import org.xml.sax.Attributes;

import pl.caltha.forms.internal.util.Util;

/**
 * Form submitInfo element.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: SubmitInfo.java,v 1.1 2005-01-19 06:55:35 pablo Exp $
 */
public class SubmitInfo
{
   protected String id;
   protected String action;
   protected String encType;
   protected String method;
   protected String encoding;
   protected String mediaType;

   public SubmitInfo(Attributes atts)
   {
        this.id =        Util.getSAXAttributeVal(atts, "id");
        this.action =    Util.getSAXAttributeVal(atts, "action");
        this.encType =   Util.getSAXAttributeVal(atts, "encType");
        this.method =    Util.getSAXAttributeVal(atts, "method");
        this.encoding =  Util.getSAXAttributeVal(atts, "encoding");
        this.mediaType = Util.getSAXAttributeVal(atts, "mediaType");
   }

   public String getId()
   {
       return id;
   }

   public String getAction()
   {
       return action;
   }

   public String getEncType()
   {
       return encType;
   }

   public String getMethod()
   {
       return method;
   }

   public String getEncoding()
   {
       return encoding;
   }

   public String getMediaType()
   {
       return mediaType;
   }
}
