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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;

/**
 * A model that can be used for building trees dynamically from arbitrary
 * data.
 *
 * <p>You can use any kind of business objects as tree data. For building
 * the UI on the fly, the toolkit provides {@link PathTreeElement} class.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: PathTreeTableModel.java,v 1.3 2004-06-14 12:03:28 fil Exp $
 */
public class PathTreeTableModel
    implements ExtendedTableModel
{
    // instance variables ////////////////////////////////////////////////////

    /** The columns of the list. */
    protected TableColumn[] columns;

    /** Maps names to objects. */
    protected Map objectByPath = new HashMap();

    /** Maps ids to objects. */
    protected Map objectById = new HashMap();

    /** Maps objects to ids. */
    protected Map idByObject = new HashMap();

    /** Simple identifier generator. */
    protected int nextId = 0;

    /** Maps objects to sets of children objects. */
    protected Map childrenByObject = new HashMap();

    // initialization ////////////////////////////////////////////////////////

    /**
     * Creates a model object.
     *
     * @param columns the table columns.
     */
    public PathTreeTableModel(TableColumn[] columns)
    {
        this.columns = columns;
    }

    // TableModel interface //////////////////////////////////////////////////

    /**
     * Returns a {@link TableRowSet} object initialised by this model
     * and a given {@link TableState}.
     *
     * @param state the parent
     * @return table of children
     */
    public TableRowSet getRowSet(TableState state)
    {
        if(state.getTreeView())
        {
			return new GenericTreeRowSet(state, this);
        }
        else
        {
			return new GenericListRowSet(state, this);
        }
    }

    /**
     * Returns array of column definitions. They are created on every call,
     * because they can get modified durig it's lifecycle.
     *
     * @return array of <code>TableColumn</code> objects
     */
    public TableColumn[] getColumns()
    {
        return columns;
    }

    // ExtendedTableModel interface //////////////////////////////////////////

    /**
     * Gets all children of the parent, may return empty array.
     *
     * @param parent the parent
     * @return table of children
     */
    public Object[] getChildren(Object parent)
    {
        if(parent == null)
        {
            Object[] root = new Object[1];
            root[0] = getObjectByPath("/");
            return root;
        }
        Set children = (Set)childrenByObject.get(parent);
        Object[] result = new Object[children.size()];
        children.toArray(result);
        return result;
    }

    /**
     * Returns the model dependent object by its id.
     *
     * @param id the id of the object
     * @return model object
     */
    public Object getObject(String id)
    {
        return objectById.get(id);
    }

    /**
     * Returns the id of the object.
     * @param child model object.
     *
     * @return the id of the object.
     */
    public String getId(Object parent, Object child)
    {
        return (String)idByObject.get(child);
    }

    // PathTreeTableModel specific public interface //////////////////////////

    /**
     * Binds an object with a location in the tree.
     *
     * @param path the path in the tree.
     * @param object the object.
     */
    public void bind(String path, Object object)
    {
        Object old = getObjectByPath(path);
        if(old != null)
        {
            // rebinding
            Object id = idByObject.remove(old);
            idByObject.put(object, id);
            objectById.put(id, object);
            Object children = childrenByObject.remove(old);
            childrenByObject.put(object, children);
            objectByPath.put(path, object);
        }
        else
        {
            String parent = parentPath(path);
            if(getObjectByPath(parent) == null && !path.equals(parent))
            {
                throw new IllegalStateException("cannot bind "+path+
                                                " because "+parent+" is not bound");
            }
            String id = ""+(nextId++);
            idByObject.put(object, id);
            objectById.put(id, object);
            childrenByObject.put(object, new HashSet());
            if(!path.equals("/"))
            {
                Set siblings = (Set)childrenByObject.get(objectByPath.get(parent));
                siblings.add(object);
            }
            objectByPath.put(path, object);
        }
    }

    /**
     * Removes binding from a specified location.
     *
     * @param path the path in the tree.
     * @return unbound object
     */
    public Object unbind(String path)
    {
        Object obj = objectByPath.get(path);
        if(obj == null)
        {
            throw new IllegalArgumentException(path+" is not bound");
        }
        Set children = (Set)childrenByObject.get(obj);
        if(children.size() > 0)
        {
            throw new IllegalStateException(path+" has "+children.size()+" child bindings");
        }
        Object id = idByObject.remove(obj);
        objectById.remove(id);
        childrenByObject.remove(obj);
        Set siblings = (Set)childrenByObject.get(objectByPath.get(parentPath(path)));
        siblings.remove(obj);
        objectByPath.remove(path);
        return obj;
    }

    /**
     * Returns an object bound to a specific location.
     *
     * @param path the path in the tree.
     * @return object bound by given path
     */
    public Object getObjectByPath(String path)
    {
        return objectByPath.get(path);
    }

    // implementation ////////////////////////////////////////////////////////

	/**
	 * Creates a parent path from a given object path.
	 * @param path a path to be truncated to parent path  
	 * @return parent path
	 */
    protected String parentPath(String path)
    {
        int i = path.lastIndexOf('/');
        if(i > 0)
        {
            return path.substring(0,i);
        }
        if(i == 0)
        {
            return "/";
        }
        else
        {
            throw new IllegalArgumentException("invalid name: at least one / is required");
        }
    }
}
