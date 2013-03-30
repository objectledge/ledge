package org.objectledge.web.rest;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.objectledge.i18n.I18nLocale;
import org.objectledge.i18n.LocaleLoaderValve;

/**
 * Binder which can be configured in {@link JerseyRestValve} which allows {@link I18nLocale} to be
 * injected. In order it to work correctly {@link LocaleLoaderValve} must be in the pipeline. Binder
 * should be appended to sequence of binders for {@link JerseyRestValve} in pico container.
 * 
 * @author Marek Lewandowski
 */
public class I18nLocaleFactoryBinder
    extends AbstractBinder
{

    @Override
    protected void configure()
    {
        bindFactory(I18nLocaleFactory.class).to(I18nLocale.class).in(RequestScoped.class);
    }

}
