package org.objectledge.i18n;

import java.util.Collection;

public interface TranslationConverter
{
    String toJson(Collection<Translation> translations);

    Collection<Translation> fromJson(String translations);

    Collection<I18nTranslation> toI18nTranslation(Collection<Translation> translations);

    void validate(Collection<Translation> altTranslations)
        throws IllegalArgumentException;
}
