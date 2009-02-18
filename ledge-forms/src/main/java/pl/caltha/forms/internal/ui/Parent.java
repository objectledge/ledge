package pl.caltha.forms.internal.ui;

import pl.caltha.forms.ConstructionException;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Parent.java,v 1.1 2005-01-19 06:55:28 pablo Exp $
 */
public abstract class Parent
{
    protected abstract void addChild(Node child)
    throws ConstructionException;
}

