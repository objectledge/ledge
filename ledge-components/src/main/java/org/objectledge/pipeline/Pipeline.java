/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Nov 24, 2003
 */
package org.objectledge.pipeline;

import org.objectledge.context.Context;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: Pipeline.java,v 1.1 2003-11-24 10:25:14 fil Exp $
 */
public class Pipeline
    implements Runnable
{
    private static final String CONTEXT_EXCEPTIONS = "org.objectledge.pipeline.Pipeline.exceptions";
    
    private Context context;
    
    private Runnable[] tryValves;
    
    private Runnable[] catchValves;
    
    private Runnable[] finalyValves;
    
    public Pipeline(Context context, Runnable[] tryValves, Runnable[] catchValves, Runnable[] finalyValves)
    {
        this.context = context;
        this.tryValves = tryValves;
        this.catchValves = finalyValves;
        this.finalyValves = finalyValves;
    }
    
    public void run()
    {
        
    }
}
