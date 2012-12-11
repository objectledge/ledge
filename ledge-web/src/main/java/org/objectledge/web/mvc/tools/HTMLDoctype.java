package org.objectledge.web.mvc.tools;

public enum HTMLDoctype
{
    HTML5,

    HTML4,

    XHTML;

    public static final String HTML5_ = "<!DOCTYPE html>";

    public static final String HTML4_ = "<!DOCTYPE HTML PUBLIC " + "-"
        + "//W3C//DTD HTML 4.01 Transitional//EN" + "http://www.w3.org/TR/html4/loose.dtd" + ">";

    public static final String XHTML_ = "<!DOCTYPE html PUBLIC" + "-"
        + "//W3C//DTD XHTML 1.0 Transitional//EN"
        + "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" + ">";

    public static final HTMLDoctype DEFAULT = HTML5;

    public static String getDoctypeDecl(HTMLDoctype type)
    {
        switch(type)
        {
        case HTML4:
            return HTML4_;
        case HTML5:
            return HTML5_;
        case XHTML:
            return XHTML_;
        default:
            throw new RuntimeException();
        }
    }
}
