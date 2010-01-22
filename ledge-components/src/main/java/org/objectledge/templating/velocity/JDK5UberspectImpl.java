package org.objectledge.templating.velocity;

import java.util.Iterator;

import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.UberspectImpl;

/**
 * JDK 1.5 extension of the Uberspector that allows Iterable Objects to be put into the Context to
 * be used with #foreach
 * <p>
 * See http://issues.apache.org/jira/browse/VELOCITY-443,
 * http://svn.apache.org/viewvc?view=rev&revision=417808
 * </p>
 * <p>
 * To use put &lt;property name="runtime.introspector.uberspect"
 * value="org.objectledge.templating.velocity.JDK5UberspectImpl"/&gt; into properties section of
 * org.objectledge.templating.Templating.xml config file.
 * </p>
 * 
 * @author <a href="mailto:henning@apache.org">Henning P. Schmiedehausen</a>
 */
public class JDK5UberspectImpl
    extends UberspectImpl
{
    /**
     * To support iterative objects used in a <code>#foreach()</code> loop.
     * 
     * @param obj The iterative object.
     * @param i Info about the object's location.
     */
    @SuppressWarnings("unchecked")
    public Iterator getIterator(Object obj, Info i)
        throws Exception
    {
        if(obj instanceof Iterable)
        {
            return ((Iterable)obj).iterator();
        }
        else
        {
            return super.getIterator(obj, i);
        }
    }

}
