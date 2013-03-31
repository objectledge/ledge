package org.objectledge.i18n.impl;

import java.util.Collection;
import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.objectledge.i18n.I18n;
import org.objectledge.i18n.I18nTranslation;
import org.objectledge.i18n.LabelFinder;
import org.objectledge.i18n.Translation;
import org.objectledge.i18n.TranslationConverter;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Marek Lewandowski
 */
public class LabelFinderImpl
    implements LabelFinder
{

    private final TranslationConverter translationConverter;

    private final I18n i18n;

    private final Logger logger;

    public LabelFinderImpl(Logger logger, TranslationConverter translationConverter, I18n i18n)
    {
        this.logger = logger;
        this.translationConverter = translationConverter;
        this.i18n = i18n;
    }

    @Override
    public String findLabelGivenJson(String jsonTranslations, Locale locale)
    {
        return findLabelGivenTranslations(translationConverter.fromJson(jsonTranslations), locale);
    }

    @Override
    public String findLabelGivenTranslations(Collection<Translation> translations, Locale locale)
    {
        return findLabelGivenI18nTranslations(translationConverter.toI18nTranslation(translations),
            locale);
    }

    @Override
    public String findLabelGivenI18nTranslations(
        final Collection<I18nTranslation> i18nTranslations, final Locale locale)
    {
        final I18nTranslation found = Iterables.tryFind(i18nTranslations,
            new TranslationPredicate(locale)).orNull();

        if(found != null)
        {
            return found.getTranslation();
        }
        else
        {
            final Locale preferedLocale = i18n.getPreferedLocale();
            final I18nTranslation fallback = Iterables.tryFind(i18nTranslations,
                new TranslationPredicate(preferedLocale)).orNull();
            if(fallback != null)
            {
                return fallback.getTranslation();
            }
            else
            {
                logger.error("No translation has been found for requested locale '"
                    + locale.toString() + "'nor for preferred locale: '"
                    + preferedLocale.toString() + "' Translations were: '"
                    + i18nTranslations.toString() + "'");
                // returning anything because it's better than server error for the client
                return i18nTranslations.iterator().next().getTranslation();
            }
        }

    }

    private static final class TranslationPredicate
        implements Predicate<I18nTranslation>
    {

        private Locale localeToBeFound;

        public TranslationPredicate(Locale localeToBeFound)
        {
            this.localeToBeFound = localeToBeFound;
        }

        @Override
        public boolean apply(I18nTranslation i18nTranslation)
        {
            return i18nTranslation.getLocale().equals(localeToBeFound);
        }

    }

}
