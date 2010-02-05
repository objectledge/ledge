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

package org.objectledge.table.comparator;

import java.util.Comparator;
import java.util.List;

/**
 * An implementation of <code>Comparator</code> interface for using lists as
 * data objects.
 *
 * <p>You can use this class to add sorting support to your table when
 * idividual rows of your data are represented as Java Lists. You can create
 * an instance of this class and pass it to TableColumn constructor,
 * specifying the index of the column. You can also specify a custom
 * comparator for you column values if you need one (you do need it when the
 * colum is not of a  basic type like String or Integer and does not implent
 * Comparable interface itself)</p>
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ListComparator.java,v 1.1 2005-02-07 21:05:13 zwierzem Exp $
 */
public class ListComparator<T>
    implements Comparator<List<T>>
{
    private int index;

    private Comparator<T> comparator;

	/**
	 * Creates a list comparator which compares lists by using <code>Comparable</code> objects
	 * stored in lists at a given index.
	 * @param index index which points to compared objects.
	 */
    public ListComparator(int index)
    {
        this.index = index;
    }

	/**
	 * Creates a list comparator which compares lists by using objects stored in lists at a given
	 * index and a provided comparator.
	 * @param index index which points to compared objects.
	 * @param comparator comparator to be used in comparisons.
	 */
    public ListComparator(int index, Comparator<T> comparator)
    {
        this(index);
        this.comparator = comparator;
    }

	/** 
	 * {@inheritDoc}
	 */
    @SuppressWarnings("unchecked")
    public int compare(List<T> l1, List<T> l2)
    {
        T o1 = l1.get(index);
        T o2 = l2.get(index);
        if(comparator != null)
        {
            return comparator.compare(o1, o2);
        }
        else
        {
            return ((Comparable<T>)o1).compareTo(o2);
        }
    }
}
