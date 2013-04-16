package org.objectledge.i18n;

import java.util.Collection;
import java.util.Locale;

/**
 * Finds label given json or translations. If translation for requested Locale was not found then
 * translation for preferred locale is looked up. If it's also not found then first translation
 * available is being returned and error logged.
 * 
 * @author Marek Lewandowski
 */
public interface LabelFinder
{
    String findLabelGivenJson(String jsonTranslations, Locale locale);

    String findLabelGivenTranslations(Collection<Translation> translations, Locale locale);

    String findLabelGivenI18nTranslations(Collection<I18nTranslation> i18nTranslations,
        Locale locale);
}
