//
//Copyright (c) 2003,2004 , Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
//
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
//
//* Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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
package org.objectledge.filesystem.table;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.objectledge.filesystem.FileSystem;
import org.objectledge.table.ExtendedTableModel;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;
import org.objectledge.table.generic.GenericListRowSet;
import org.objectledge.table.generic.GenericTreeRowSet;

/**
 * Implementation of Table service based on file service
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FileTableModel.java,v 1.4 2005-02-25 14:27:42 zwierzem Exp $
 */
public class FileTableModel implements ExtendedTableModel
{
    private final FileSystem fileSystem;
    private final Map<String, Comparator> comparatorByColumnName =
        new HashMap<String, Comparator>();

    /**
     * Creates new FileTableModel instance.
     * @param fileSystem file system component.
     * @param locale locale to use.
     */
	public FileTableModel(FileSystem fileSystem, Locale locale)
	{
		this.fileSystem = fileSystem;

		comparatorByColumnName.put("name", new NameComparator(locale));
		comparatorByColumnName.put("modification.time", new ModificationTimeComparator());
		comparatorByColumnName.put("path", new PathComparator(locale));
		comparatorByColumnName.put("length", new LengthComparator());
	}

    /**
     * Returns a {@link TableRowSet} object initialised by this model
     * and a given {@link TableState}.
     *
     * @param state the parent
     * @param filters the filters to apply.
     * @return table of children
     */
    public TableRowSet getRowSet(TableState state, TableFilter[] filters)
    {
        if(state.getTreeView())
        {
            return new GenericTreeRowSet(state, filters, this);
        }
        else
        {
            return new GenericListRowSet(state, filters, this);
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
        TableColumn[] columns = new TableColumn[comparatorByColumnName.size()];
        int i=0;
        for(Iterator iter = comparatorByColumnName.keySet().iterator(); iter.hasNext(); i++)
        {
            String columnName = (String)(iter.next());
            Comparator comparator =  (Comparator)(comparatorByColumnName.get(columnName));
            try
            {
                columns[i] = new TableColumn(columnName, comparator);
            }
            catch(TableException e)
            {
                throw new RuntimeException("Problem creating a column object: "+e.getMessage());
            }
        }
        return columns;
    }

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
            return new Object[0];
        }
        
        FileObject parentObject = (FileObject)parent;
        if(!parentObject.isDirectory())
        {
            return new Object[0];
        }
        
        String parentPath = normalizeDirPath(parentObject.getPath());
        String[] fileNames;
        try
        {
            fileNames = fileSystem.list(parentPath);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        if(fileNames == null)
        {
			return new Object[0];
        }

		FileObject[] files = new FileObject[fileNames.length]; 
        for(int i=0; i<fileNames.length; i++)
        {
        	String filePath = parentPath+'/'+fileNames[i];
        	files[i] = new FileObject(fileSystem, filePath);
        }
        return files;
    }

    /**
     * Returns the model dependent object by its id.
     *
     * @param id the id of the object
     * @return model object
     */
    public Object getObject(String id)
    {
        if(fileSystem.exists(id))
        {
        	return new FileObject(fileSystem, id);
        }
        else
        {
        	return null;
        }
    }

    /**
     * Returns the id of the object.
     * 
     * @param parentId id of the parent model object
     * @param child model object.
     *
     * @return the id of the object.
     */
    public String getId(String parentId, Object child)
    {
        if(child == null)
        {
            return parentId;    // TODO ???
        }
        return ((FileObject)child).getPath();
    }
    
    // implementation //////////////////////////////////////////////////////////////////////////////

	private String normalizeDirPath(String path)
	{
		if(path.charAt(path.length()-1) == '/')
		{
			path = path.substring(0, path.length()-2);
		}
		return path;
	}
}
