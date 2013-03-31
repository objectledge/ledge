package org.objectledge.i18n;

import java.util.Locale;

public final class I18nTranslation
{
    private final Locale locale;

    private final String translation;

    public I18nTranslation(Locale locale, String translation)
    {
        this.locale = locale;
        this.translation = translation;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public String getTranslation()
    {
        return translation;
    }

    @Override
    public String toString()
    {
        return "I18nTranslation [locale=" + locale + ", translation=" + translation + "]";
    }

}
