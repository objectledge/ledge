package org.objectledge.i18n;

public class Translation
{
    /**
     * IETF BCP 47 format
     */
    private String locale;

    private String label;

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

}
