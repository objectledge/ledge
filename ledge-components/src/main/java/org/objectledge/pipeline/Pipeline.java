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
 * @version $Id: Pipeline.java,v 1.3 2003-11-25 08:18:31 fil Exp $
 */
public class Pipeline
    implements Runnable
{
    private static final String CONTEXT_EXCEPTIONS = "org.objectledge.pipeline.Pipeline.exceptions";
    
    private Context context;
    
    private Runnable[] tryValves;
    
    private Runnable[] catchValves;
    
    private Runnable[] finalyValves;
    
    /**
     * Constructs a new instance of the pipeline.
     * 
     * @param context the context.
     * @param tryValves the valves to be used in the try stage.
     * @param catchValves the valves to be used in the catch stage.
     * @param finalyValves the valves to be used in the finaly stage.
     */
    public Pipeline(Context context, Runnable[] tryValves, Runnable[] catchValves, 
        Runnable[] finalyValves)
    {
        this.context = context;
        this.tryValves = tryValves;
        this.catchValves = finalyValves;
        this.finalyValves = finalyValves;
    }

    /**
     * Runs the connected valves.
     */    
    public void run()
    {
        
    }
}
