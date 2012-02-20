package org.objectledge.visitor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to enforce dispatch ordering of the visit method in case of inexact matches.
 * 
 * <p>Methods that define this annotation will be prefered over those that do not.
 * Methods with the lower order value will be prefered over those with higher values.</p>
 * 
 * <p>In general you should assign lower order to methods with more specific argument type
 * than those with less specific argument type.</p>
 * 
 * <p>The ordering could be determined automatically in many, but not all cases. Unfortunately
 * the algorithm is complex, therefore we resort to simpler explicit scenario.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DispatchOrder
{
    int value();
}