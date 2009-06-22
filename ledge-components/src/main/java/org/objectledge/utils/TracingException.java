package org.objectledge.utils;

/**
 * An exception type that can be used with {@link StackTrace} class to trace call path for a 
 * specific code localtion. 
 *
 * <p>Usually, the <code>StackTrace</code> object is used to prettyprint a
 * stack trace of a caught exception that is not going to be rethrown, but
 * rather is reported to the user, or written to the logs. In such situation
 * the important information are those frames in the stack trace that lie above
 * the <em>current</em> that is the frame of the method that caught the
 * excepetion an built a <code>StackTrace</code> upon it. They provide
 * information on what  sequence of events caused the exception to be
 * thrown. In such case frames below the point of catching (then reporting and
 * discarding) the exception are just noise.</p> 
 *
 * <p>However there is another situation where Exceptions are useful. Haven't
 * you ever wondered "What the heck is calling this method now?", "Or why is
 * this method being called twice?". This is where
 * <code>TracingException</code> comes in. Just write <code>String trace =
 * StackTrace(new TracingExceception()).toString()</code> to capture the stack
 * frames below the current frame. No other type of exception can be used this
 * way, because they would be truncated to nothing (except the exception type
 * and message), because they have <em>no</em> frames above the current
 * frame, and your valuable information would be discarded as 'noise'.</p>
 *
 * <p>Sometimes you don't need your trace all the way back to the main()
 * method. If the main() in your belongs to Tomcat 4.x servlet container you
 * will get rather large amount of noise in your stack trace too. To avoid that
 * you can pass <code>depth</code> parameter to the exception's constructor,
 * to cut down the stactrace to the few important frames.</p>
 *
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 * @version $Id: TracingException.java,v 1.2 2004-12-22 08:35:04 rafal Exp $
 */
public class TracingException
	extends Exception
{
    /** The requested depth of the stack trace. */
    private int depth = 0;

    /**
     * Constructs a new <code>TracingException</code> with unlimited depth.
     */
    public TracingException()
    {
        super();
    }

    /**
     * Constructs a new <code>TracingException</code> with the specified depth.
     * 
     * @param depth desired tracing depth.
     */
    public TracingException(int depth)
    {
        super();
        this.depth = depth;
    }

    /**
     * Construcs a new <code>TracingException</code> with the specific root
     * cause.
     *
     * @param rootCause the rootCause.
     */
    public TracingException(Throwable rootCause)
    {
        super(rootCause);
    }

    /**
     * Construcs a new <code>TracingException</code> with the specific root
     * cause and tracing depth.
     *
     * @param rootCause the rootCause.
     * @param depth the requested depth of the stack trace.
     */
    public TracingException(Throwable rootCause, int depth)
    {
        super(rootCause);
        this.depth = depth;
    }
    
    /**
     * Returns the requested depth of the stack trace.
     *
     * @return the requested depth of the stack trace.
     */
    public int getDepth()
    {
        return depth;
    }
}

