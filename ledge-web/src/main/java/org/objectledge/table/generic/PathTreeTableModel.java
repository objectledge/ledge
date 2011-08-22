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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableFilter;
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
 * @version $Id: PathTreeTableModel.java,v 1.8 2009-01-09 16:16:38 rafal Exp $
 */
public class PathTreeTableModel<T>
    implements ExtendedTableModel<T>
{
    // instance variables ////////////////////////////////////////////////////

    /** The columns of the list. */
    protected TableColumn<T>[] columns;

    /** Maps names to objects. */
    protected Map<String, T> objectByPath = new HashMap<String, T>();

    /** Maps ids to objects. */
    protected Map<String, T> objectById = new HashMap<String, T> ();

    /** Maps objects to ids. */
    protected Map<T, String>  idByObject = new HashMap<T, String>();

    /** Maps objects to sets of children objects. */
    protected Map<T, Set<T>> childrenByObject = new HashMap<T, Set<T>>();
    
    /** Used for generating entry ids */
    private int entryCount = 0;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Creates a model object.
     *
     * @param columns the table columns.
     */
    public PathTreeTableModel(TableColumn<T> ... columns)
    {
        this.columns = columns;
    }

    // TableModel interface //////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public TableRowSet<T> getRowSet(TableState state, TableFilter<T>[] filters)
    {
        if(state.getTreeView())
        {
			return new GenericTreeRowSet<T>(state, filters, this);
        }
        else
        {
			return new GenericListRowSet<T>(state, filters, this);
        }
    }

    /**
     * Returns array of column definitions. They are created on every call,
     * because they can get modified durig it's lifecycle.
     *
     * @return array of <code>TableColumn</code> objects
     */
    public TableColumn<T>[] getColumns()
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
    public T[] getChildren(T parent)
    {
        // TODO this does not seem quite right. Should we require Class<T> ctor parameter? Is the model expected to be type-homogenous?
        if(parent == null)
        {
            T rootObject = getObjectByPath("/");
            T[] root = (T[])Array.newInstance(rootObject.getClass(), 1);
            root[0] = rootObject;
            return root;
        }
        else
        {
            Set<T> children = childrenByObject.get(parent);
            T[] result = (T[])Array.newInstance(parent.getClass(), children.size());
            children.toArray(result);
            return result;
        }
    }

    /**
     * Returns the model dependent object by its id.
     *
     * @param id the id of the object
     * @return model object
     */
    public T getObject(String id)
    {
        return objectById.get(id);
    }

    /**
     * Returns the id of the object.
     * 
     * @param parent the id of the parent.
     * @param child model object.
     * @return the id of the object.
     */
    public String getId(String parent, T child)
    {
        return idByObject.get(child);
    }

    // PathTreeTableModel specific public interface //////////////////////////

    /**
     * Binds an object with a location in the tree.
     *
     * @param path the path in the tree.
     * @param object the object.
     */
    public void bind(String path, T object)
    {
        T old = getObjectByPath(path);
        if(old != null)
        {
            // rebinding
            String id = idByObject.remove(old);
            idByObject.put(object, id);
            objectById.put(id, object);
            Set<T> children = childrenByObject.remove(old);
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
            String id = Integer.toString(++entryCount);
            idByObject.put(object, id);
            objectById.put(id, object);
            childrenByObject.put(object, new HashSet<T>());
            if(!path.equals("/"))
            {
                Set<T> siblings = childrenByObject.get(objectByPath.get(parent));
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
        T obj = objectByPath.get(path);
        if(obj == null)
        {
            throw new IllegalArgumentException(path+" is not bound");
        }
        Set<T> children = childrenByObject.get(obj);
        if(children.size() > 0)
        {
            throw new IllegalStateException(path+" has "+children.size()+" child bindings");
        }
        Object id = idByObject.remove(obj);
        objectById.remove(id);
        childrenByObject.remove(obj);
        Set<T> siblings = childrenByObject.get(objectByPath.get(parentPath(path)));
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
    public T getObjectByPath(String path)
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
