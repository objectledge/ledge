package org.objectledge.i18n;

import java.util.Locale;

/**
 * I18nLocale interface is for injection purposes. There is and should be only one implementation -
 * {@link I18nContext}
 * 
 * @see I18nContext
 * @author Marek Lewandowski
 */
public interface I18nLocale
{
    Locale get();
}
