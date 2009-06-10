// 
// Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 
package org.objectledge.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Captures a full stack trace in a multi-level <code>Throwable</code> sequence.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: StackTrace.java,v 1.8 2006-02-08 18:25:08 zwierzem Exp $
 */
public class StackTrace
{    
    private static final Class[] EMPTY_FORMAL_ARGS = new Class[0];

    private static final Object[] EMPTY_ACTUAL_ARGS = new Object[0];

    private static final String[] ROOT_CAUSE_METHOD_NAMES = { "getRootCause", "getException", 
                    "getNextException", "getTargetException", "getWrappedThrowable" };
    
    private static final String EOL = System.getProperty("line.separator");

    /** The exception that we crate trace for. */
    private Throwable exception;

    /** Lazily created cached stack trace as an array lines. */
    private String[] trace = null;
    
    /** Lazily created cached stack trace as a rendered string. */
    private String traceString = null;
    
    /**
     * Creates a new StackTrace instance.
     * 
     * @param t the exception to create a trace for.
     */
    public StackTrace(Throwable t)
    {
        exception = new TracingException(t);
    }
    
    /**
     * Return the rendered stack trace.
     * 
     * @return the rendered stack trace.
     */
    public String toString()
    {
        if(traceString == null)
        {
            StringBuilder buff = new StringBuilder();
            appendTo(buff);
            traceString = buff.toString();
        }
        return traceString;
    }
    
    /**
     * Returns the stack trace as an array of lines.
     * 
     * @return the stack trace as an array of lines.
     */
    public String[] toStringArray()
    {
        if(trace == null)
        {
            List<String> messages = new ArrayList<String>();
            List<StackTraceElement[]> traces = new ArrayList<StackTraceElement[]>();

            boolean tracing = false;
            int tracingDepth = 0;
            if(exception.getCause() instanceof TracingException)
            {
                tracing = true;
                tracingDepth = ((TracingException)exception.getCause()).getDepth();
            }

            Throwable ex = exception;
            do
            {
                messages.add(ex.toString());
                traces.add(ex.getStackTrace());
                ex = getCause(ex);
            }
            while(ex != null);
            int[] skip = new int[traces.size()];
            for (int i = 1; i < traces.size(); i++)
            {
                StackTraceElement[] frames = (StackTraceElement[])traces.get(i);
                StackTraceElement[] prevFrames = (StackTraceElement[])traces.get(i - 1);
                skip[i] = frames.length;
                inner: for (int j = 0; j < frames.length && j < prevFrames.length; j++)
                {
                    if(!frames[frames.length - j - 1].equals(prevFrames[prevFrames.length - j - 1]))
                    {
                        skip[i] = j;
                        break inner;
                    }
                }
            }
            if(tracing)
            {
                if(tracingDepth > 0)
                {
                    skip[1] = skip[1] - tracingDepth;
                }
                else
                {
                    skip[1] = 0;
                }
            }

            List<String> traceLines = new ArrayList<String>();
            StringBuilder buff = new StringBuilder();

            for (int i = traces.size() - 1; i > 0; i--)
            {
                StackTraceElement[] frames = (StackTraceElement[])traces.get(i);
                if(i < traces.size() - 1)
                {
                    buff.append("rethrown as ");
                }
                buff.append((String)messages.get(i));
                traceLines.add(buff.toString());
                buff.setLength(0);
                for (int j = 0; j < frames.length - skip[i]; j++)
                {
                    buff.append("    ");
                    buff.append(frames[j].toString());
                    traceLines.add(buff.toString());
                    buff.setLength(0);
                }
            }

            trace = new String[traceLines.size()];
            traceLines.toArray(trace);
        }
        return trace;
    }
    
    /**
     * Appends the stack trace to the provided StringBuilder.
     * 
     * @param buff the buffer.
     * @return the StringBuilder passed in, for chaining.
     */
    public StringBuilder appendTo(StringBuilder buff)
    {
        String[] traceArray = toStringArray();
        for(int i = 0; i < traceArray.length; i++)
        {
            buff.append(traceArray[i]).append(EOL);
        }
        return buff;
    }

    private Throwable getCause(Throwable t)
    {
        Throwable cause = t.getCause();
        if(cause != null && cause != t)
        {
            return cause;
        }
        // try using reflection to deal with legacy (pre 1.4) exception classes.
        Method method = null;
        for(int i=0; i < ROOT_CAUSE_METHOD_NAMES.length; i++)
        {
            try 
            {
                method = t.getClass().getMethod(ROOT_CAUSE_METHOD_NAMES[i], EMPTY_FORMAL_ARGS);
            } 
            catch(NoSuchMethodException e) 
            {
                continue;
            }
            if(method != null) 
            {
                try 
                {
                    cause = (Throwable)method.invoke(t, EMPTY_ACTUAL_ARGS);
                    if(cause != null && cause != t)
                    {
                        return cause;
                    }
                } 
                catch(Exception e) 
                {
                    continue;
                }
            }
        }
        return null;
    }
}
