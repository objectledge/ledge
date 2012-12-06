package org.objectledge.web.mvc.tools;

public enum HTMLDoctype
{
    HTML5TYPE()
    {
        @Override
        public boolean checkDoctype(String state)
        {
            return HTML5.equals(state);
        }

        @Override
        public String getName()
        {
            return HTML5;
        }
    },
    
    HTML4TYPE()
    {
        @Override
        public boolean checkDoctype(String state)
        {
            return HTML4.equals(state);
        }

        @Override
        public String getName()
        {
            return HTML4;
        }
    },
    
    XHTMLTYPE()
    {
        @Override
        public boolean checkDoctype(String state)
        {
            return HTML4.equals(state);
        }

        @Override
        public String getName()
        {
            return HTML4;
        }
    },
    
    DEFAULT_TYPE()
    {
        @Override
        public boolean checkDoctype(String doctype)
        {
            return DEFAULT.equals(doctype);
        }

        @Override
        public String getName()
        {        
            return DEFAULT;
        }   
    };
    
    public static final String HTML5 = "<!DOCTYPE html>";

    public static final String HTML4 = "<!DOCTYPE HTML PUBLIC " + "-" + "//W3C//DTD HTML 4.01 Transitional//EN" + "http://www.w3.org/TR/html4/loose.dtd"+">";

    public static final String XHTML = "<!DOCTYPE html PUBLIC" + "-" + "//W3C//DTD XHTML 1.0 Transitional//EN" + "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" +">";
    
    public static final String DEFAULT = HTML5;
    
    public abstract boolean checkDoctype(String doctype);
    
    public abstract String getName();
}
