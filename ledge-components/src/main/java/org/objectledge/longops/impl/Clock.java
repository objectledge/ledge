package org.objectledge.longops.impl;

/**
 * A provider of current time, useful for unit testing.
 * 
 * @author rafal.krzewski@caltha.pl
 */
interface Clock
{
    long currentTimeMillis();
}
