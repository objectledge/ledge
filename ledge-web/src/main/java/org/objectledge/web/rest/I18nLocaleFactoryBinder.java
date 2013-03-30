package org.objectledge.web.rest;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.objectledge.i18n.I18nLocale;

public class I18nLocaleFactoryBinder
    extends AbstractBinder
{

    @Override
    protected void configure()
    {
        bindFactory(I18nLocaleFactory.class).to(I18nLocale.class).in(RequestScoped.class);
    }


}
