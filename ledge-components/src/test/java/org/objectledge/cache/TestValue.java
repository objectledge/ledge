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

package org.objectledge.cache;

import java.sql.SQLException;

import org.objectledge.database.persistence.InputRecord;
import org.objectledge.database.persistence.OutputRecord;
import org.objectledge.database.persistence.Persistent;

/**
 * @author <a href="mailto:rafal@caltha.pl">Pawel Potempski</a>
 * @version $Id: TestValue.java,v 1.3 2006-02-08 18:26:00 zwierzem Exp $
 */
public class TestValue implements Persistent
{
    private static String table = "test_value";

    private static String[] keyColumns = new String[] { "test_value_id" };

    private boolean saved = false;

    private String name;

    private int quantity;

    public TestValue()
    {
        // default constructor
    }

    public TestValue(String name, int quantity)
    {
        this.name = name;
        this.quantity = quantity;
    }

    public String getTable()
    {
        return table;
    }

    public String[] getKeyColumns()
    {
        return keyColumns;
    }

    public void setSaved(long id)
    {
        saved = true;
    }

    public boolean getSaved()
    {
        return saved;
    }

    public void getData(OutputRecord out)
        throws SQLException
    {
        out.setString("name", name);
        out.setInteger("quantity", quantity);
    }

    public void setData(InputRecord in)
        throws SQLException
    {
        name = in.getString("name");
        quantity = in.getInteger("quantity");
    }

    public String getName()
    {
        return name;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

}
