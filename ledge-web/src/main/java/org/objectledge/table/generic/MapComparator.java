// 
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
// 
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//  
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
// 
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
// 

package org.objectledge.table.generic;

import java.util.Comparator;
import java.util.Map;

/**
 * An implementation of <code>Comparator</code> interface for using maps as
 * data objects.
 *
 * <p>You can use this class to add sorting support to your table when
 * idividual rows of your data are represented as Java Maps. You can create
 * an instance of this class and pass it to TableColumn constructor,
 * specifying the key of the column. You can also specify a custom
 * comparator for you column values if you need one (you do need it when the
 * colum is not of a  basic type like String or Integer and does not implement
 * Comparable interface itself)</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: MapComparator.java,v 1.2 2004-03-16 15:35:08 zwierzem Exp $
 */
public class MapComparator
    implements Comparator
{
    private Object key;

    private Comparator comparator;

	/**
	 * Creates a map comparator which compares maps by using <code>Comparable</code> objects
	 * stored in maps under a given key.
	 * @param key key which maps to compared objects.
	 */
    public MapComparator(Object key)
    {
        this.key = key;
    }

	/**
	 * Creates a map comparator which compares maps by using objects stored in maps under a given
	 * key and a provided comparator.
	 * @param key key which maps to compared objects.
	 * @param comparator comparator to be used in comparisons.
	 */
    public MapComparator(Object key, Comparator comparator)
    {
        this(key);
        this.comparator = comparator;
    }

	/** 
	 * {@inheritDoc}
	 */
    public int compare(Object o1, Object o2)
    {
        Map l1 = (Map)o1;
        Map l2 = (Map)o2;
        o1 = l1.get(key);
        o2 = l2.get(key);
        if(comparator != null)
        {
            return comparator.compare(o1, o2);
        }
        else
        {
            if(o1 instanceof Boolean)
            {
                if(o2 != null && o2 instanceof Boolean )
                {
                    if(((Boolean)o1).booleanValue() == ((Boolean)o2).booleanValue())
                    {
                        return 0;
                    }
                    if(((Boolean)o1).booleanValue())
                    {
                        return 1;
                    }
                    return -1;
                }
                return 1;
            }
            return ((Comparable)o1).compareTo(o2);
        }
    }
}
