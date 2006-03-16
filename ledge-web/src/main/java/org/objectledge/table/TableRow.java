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

package org.objectledge.table;

/** Container for displayed row of data.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TableRow.java,v 1.7 2006-03-16 17:57:04 zwierzem Exp $
 */
public class TableRow<T>
{
    /** Object id */
    private String id = "";

    /** Object table row */
    private T object;

    /** nesting depth of this node. */
    private int depth;

    /** Number of children of this row (tree node). */
    private int childCount;

    /** Number of visible children of this row (tree node). */
    private int visibleChildCount;

    /**
     * Creates a table row object.
     *
     * @param id a string which identifies this row's object
     * @param object an object related to this table row
     * @param depth a tree depth of this row (node), for root row it is equal to <code>0</code>
     * 		(zero)
     * @param childCount number of children under this tree node
     * @param visibleChildCount number of children currently visible under this tree node
     */
    public TableRow(String id, T object, int depth, int childCount, int visibleChildCount)
    {
        if(id != null)
        {
            this.id = id;
        }
        this.object = object;
        this.depth = depth;
        this.childCount = childCount;
        this.visibleChildCount = visibleChildCount;
    }

    // getters ///////////////////////////////////////////////////////////////

    /**
     * Returns the id of the row.
     *
     * @return the Id of the row.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the objects representing the resource.
     *
     * @return the object.
     */
    public T getObject()
    {
        return object;
    }

    /**
     * Returns the nesting depth of this row.
     *
     * @return the nesting depth of this row.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Returns the number of children of this node which could be viewed with current
     * filtering settings.
     *
     * @return the number of this row's children.
     */
    public int getChildCount()
    {
        return childCount;
    }

    /**
     * Returns the number of children of this node visible within current filtering and maximal
     * depth settings.
     *
     * @return the number of visible children.
     */
    public int getVisibleChildCount()
    {
        return visibleChildCount;
    }

    /**
     * Equals overriden.
     *
     * @param object the object to be compared
     * @return the comparison result
     */
    public boolean equals(Object object)
    {
        if(object instanceof TableRow)
        {
            return ((TableRow)object).getId().equals(id);
        }
        return false;
    }

	/** 
	 * {@inheritDoc}
	 */
	public int hashCode()
	{
		return id.hashCode();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public String toString()
	{
		return "TableRow("+id+")";
		//+", depth="+depth+", chldCnt="+childCount+", visChldnt="+visibleChildCount+")";
	}
}
