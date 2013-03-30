package org.objectledge.web.rest;

import org.glassfish.hk2.api.Factory;
import org.objectledge.context.Context;
import org.objectledge.i18n.I18nContext;
import org.objectledge.i18n.I18nLocale;

/**
 * Factory which creates injectable instances of {@link I18nLocale}
 * 
 * @author Marek Lewandowski
 */
public class I18nLocaleFactory
    implements Factory<I18nLocale>
{

    @Override
    public void dispose(I18nLocale instance)
    {

    }

    @Override
    public I18nLocale provide()
    {
        final I18nContext i18nContext = I18nContext.getI18nContext(new Context());
        return i18nContext;
    }

}
