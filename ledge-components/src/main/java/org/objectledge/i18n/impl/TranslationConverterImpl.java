package org.objectledge.i18n.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.objectledge.i18n.I18nTranslation;
import org.objectledge.i18n.Translation;
import org.objectledge.i18n.TranslationConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class TranslationConverterImpl
    implements TranslationConverter
{

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String toJson(Collection<Translation> translations)
    {
        try
        {
            return mapper.writeValueAsString(translations);
        }
        catch(JsonProcessingException e)
        {
            throw new RuntimeException("unexpected error", e);
        }
    }

    @Override
    public Collection<Translation> fromJson(String translations)
    {
        final CollectionType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Translation.class);
        try
        {
            Collection<Translation> collection = mapper.readValue(translations, type);
            return collection;
        }
        catch(IOException e)
        {
            throw new RuntimeException("unexpected error", e);
        }
        
    }

    @Override
    public Collection<I18nTranslation> toI18nTranslation(Collection<Translation> translations)
    {
        return Collections2.transform(translations, new Function<Translation, I18nTranslation>()
            {
                @Override
                public I18nTranslation apply(Translation translation)
                {
                    final Locale locale = Locale.forLanguageTag(translation.getLocale());
                    return new I18nTranslation(locale, translation.getLabel());
                }
            });
    }

    @Override
    public void validate(Collection<Translation> translations)
        throws IllegalArgumentException
    {
        try
        {
            toI18nTranslation(translations);
        }
        catch(Exception e)
        {
            throw new IllegalArgumentException("Translations are invalid", e);
        }

    }

}
