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

package org.objectledge.parameters;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class DefaultParametersTest extends TestCase
{
    /** parameter container */
    protected DefaultParameters params;

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        params = new DefaultParameters();
    }

    /**
     * Constructor for ParametersImplTest.
     * 
     * @param arg0 default arg.
     */
    public DefaultParametersTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Test for void ParametersImpl()
     */
    public void testParametersImpl()
    {
        //self tested
    }

    /**
     * Test for void ParametersImpl(String)
     */
    public void testParametersImplString()
    {
        params = new DefaultParameters("foo=bar\n");
        assertEquals(params.get("foo"), "bar");
        params = new DefaultParameters("foo=bar,buzz,foo\nbar=foo");
        assertEquals(params.getStrings("foo").length, 3);
        assertEquals(params.get("bar"), "foo");
        params = new DefaultParameters("");
        assertEquals(params.get("foo", "bar"), "bar");
        try
        {
            params = new DefaultParameters("foo");
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            //expected
        }
        try
        {
            params = new DefaultParameters((String)null);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            //expected
        }
        
        
        String source = "foo=bar,foo\nbar=foo\n";
        params = new DefaultParameters(source);
        assertEquals(params.get("bar","bar"),"foo");
        assertEquals(params.getParameterNames().length,2);
        
        source = "foo=bar,f\\\noo\nbar=foo\n";
        params = new DefaultParameters(source);
        assertEquals(params.get("bar","bar"),"foo");
        assertEquals(params.getParameterNames().length,2);
        
    }

    /**
     * Test for void ParametersImpl(InputStream, String)
     * 
     */
    public void testParametersImplInputStreamString()
    {
        InputStream is = new ByteArrayInputStream("foo=bar\n".getBytes());
        params = new DefaultParameters(is, "ISO-8859-1");
        assertEquals(params.get("foo"), "bar");
        params = new DefaultParameters("foo=bar,buzz,foo\nbar=foo");
        assertEquals(params.getStrings("foo").length, 3);
        assertEquals(params.get("bar"), "foo");
        params = new DefaultParameters("");
        assertEquals(params.get("foo", "bar"), "bar");
    }

    /**
     * Test for void ParametersImpl(Parameters)
     */
    public void testParametersImplParameters()
    {
        params = new DefaultParameters();
        params.add("foo", "bar");
        params.add("foo", "buz");
        params.add("bar", "foo");
        params = new DefaultParameters(params);
        assertEquals(params.getParameterNames().length, 2);
    }

    /**
     * Test for String get(String)
     */
    public void testGet()
    {
        try
        {
            assertEquals(params.get("foo"), "bar");
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", "bar");
        assertEquals(params.get("foo"), "bar");
        params.add("foo", "true");
        try
        {
            assertEquals(params.get("foo"), "bar");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            // expected
        }
    }

    /**
     * Test for String get(String, String)
     */
    public void testGetStringString()
    {
        assertEquals(params.get("foo", "bar"), "bar");
        params.add("foo", "bar");
        assertEquals(params.get("foo", "buzz"), "bar");
        params.add("foo", "buzz");
        try
        {
            assertEquals(params.get("foo", "buzz"), "bar");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            //expected
        }
    }

    /**
     * Test for String getStrings(String)
     */
    public void testGetStrings()
    {
        assertEquals(params.getStrings("foo").length, 0);
        params.add("foo", "bar");
        params.add("foo", "true");
        assertEquals(params.getStrings("foo").length, 2);
        assertEquals(params.getStrings("foo")[0], "bar");
        assertEquals(params.getStrings("foo")[1], "true");
    }

    /**
     * Test for boolean getBoolean(String)
     */
    public void testGetBooleanString()
    {
        try
        {
            assertEquals(params.getBoolean("foo"), false);
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", "bar");
        assertEquals(params.getBoolean("foo"), false);
        params.set("foo", "true");
        assertEquals(params.getBoolean("foo"), true);
    }

    /**
     * Test for boolean getBoolean(String, boolean)
     */
    public void testGetBooleanStringboolean()
    {
        assertEquals(params.getBoolean("foo", false), false);
        params.add("foo", "bar");
        assertEquals(params.getBoolean("foo"), false);
        params.set("foo", "true");
        assertEquals(params.getBoolean("foo", false), true);
        params.set("foo", true);
        assertEquals(params.getBoolean("foo", false), true);
    }

    /**
     * Test for boolean getBooleans(String)
     */
    public void testGetBooleans()
    {
        assertEquals(params.getBooleans("foo").length, 0);
        params.add("foo", "bar");
        params.add("foo", "true");
        assertEquals(params.getBooleans("foo").length, 2);
        assertEquals(params.getBooleans("foo")[0], false);
        assertEquals(params.getBooleans("foo")[1], true);
    }

    /**
     * Test for float getFloat(String)
     */
    public void testGetFloatString()
    {
        try
        {
            assertEquals(params.getFloat("foo"), 1, 1);
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", 1);
        assertEquals(params.getFloat("foo"), 1, 1);
        params.add("foo", 2);
        try
        {
            params.getFloat("foo");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            // expected
        }
        params.set("foo", "bar");
        try
        {
            params.getFloat("foo");
            fail("Should throw NumberFormatException");
        }
        catch (NumberFormatException e)
        {
            // expected
        }
    }

    /**
     * Test for float getFloat(String, float)
     */
    public void testGetFloatStringfloat()
    {
        assertEquals(params.getFloat("foo", 1.5F), 1.5F, 1.5F);
        params.add("foo", 2.5F);
        assertEquals(params.getFloat("foo", 1.5F), 2.5F, 2.5F);
    }

    /**
     * Test for float getFloats(String)
     */
    public void testGetFloats()
    {
        params.add("foo", 2.5F);
        assertEquals(params.getFloats("foo")[0], 2.5F, 3.5F);
        params.add("foo", 2.5F);
        assertEquals(params.getFloats("foo")[1], 2.5F, 2.5F);
    }

    /**
     * Test for int getInt(String)
     */
    public void testGetIntString()
    {
        try
        {
            assertEquals(params.getInt("foo"), 1, 1);
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", 1);
        assertEquals(params.getInt("foo"), 1, 1);
        params.add("foo", 2);
        try
        {
            params.getInt("foo");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            // expected
        }
        params.set("foo", "bar");
        try
        {
            params.getInt("foo");
            fail("Should throw NumberFormatException");
        }
        catch (NumberFormatException e)
        {
            // expected
        }

    }

    /**
     * Test for int getInt(String, int)
     */
    public void testGetIntStringint()
    {
        assertEquals(params.getInt("foo", 1), 1, 1);
        params.add("foo", 1);
        assertEquals(params.getInt("foo", 1), 1, 1);
    }

    /**
     * Test for get ints.
     */
    public void testGetInts()
    {
        params.add("foo", 2);
        assertEquals(params.getInts("foo")[0], 2, 2);
        params.add("foo", 2);
        assertEquals(params.getInts("foo")[1], 2, 2);
    }

    /**
     * Test for long getLong(String)
     */
    public void testGetLongString()
    {
        try
        {
            assertEquals(params.getLong("foo"), 1, 1);
            fail("Should throw UndefinedParameterException");
        }
        catch (UndefinedParameterException e)
        {
            // expected
        }
        params.add("foo", 1);
        assertEquals(params.getLong("foo"), 1, 1);
        params.add("foo", 2);
        try
        {
            params.getLong("foo");
            fail("Should throw AmbiguousParameterException");
        }
        catch (AmbiguousParameterException e)
        {
            // expected
        }
        params.set("foo", "bar");
        try
        {
            params.getLong("foo");
            fail("Should throw NumberFormatException");
        }
        catch (NumberFormatException e)
        {
            // expected
        }
    }

    /**
     * Test for long getLong(String, long)
     */
    public void testGetLongStringlong()
    {
        assertEquals(params.getLong("foo", 1), 1, 1);
        params.add("foo", 1);
        assertEquals(params.getLong("foo", 1), 1, 1);
    }

    /**
     * Test for long getLongs()
     */
    public void testGetLongs()
    {
        params.add("foo", 2);
        assertEquals(params.getLongs("foo")[0], 2, 2);
        params.add("foo", 2);
        assertEquals(params.getLongs("foo")[1], 2, 2);
    }

    /**
     * Test for getParameterNames()
     */
    public void testGetParameterNames()
    {
        params.set("foo", "bar");
        params.set("foo", "buzz");
        params.set("bar", "buzz");
        params.set("buzz", "bar");
        assertEquals(params.getParameterNames().length, 3);
    }

    /**
     * Test for boolean isDefined()
     */
    public void testIsDefined()
    {
        assertEquals(params.isDefined("foo"), false);
        params.set("foo", "bar");
        assertEquals(params.isDefined("foo"), true);
        params.remove("foo");
        assertEquals(params.isDefined("foo"), false);
    }

    /**
     * Test for void remove()
     */
    public void testRemove()
    {
        params.set("foo", "bar");
        params.set("bar", "foo");
        params.remove();
        assertEquals(params.isDefined("foo"), false);
        assertEquals(params.isDefined("foo"), false);
    }

    /**
     * Test for void remove(String)
     */
    public void testRemoveString()
    {
        //already tested
    }

    /**
     * Test for void remove(String, String)
     */
    public void testRemoveStringString()
    {
        params.set("foo", "bar");
        params.set("foo", "foo");
        params.remove("foo", "bar");
        params.remove("bar", "foo");
        assertEquals(params.isDefined("foo"), true);
        assertEquals(params.get("foo", "bar"), "foo");
    }

    /**
     * Test for void remove(String, float)
     */
    public void testRemoveStringfloat()
    {
        params.set("foo", 1F);
        params.set("foo", 2F);
        params.remove("foo", 1F);
        assertEquals(params.isDefined("foo"), true);
        assertEquals(params.getFloat("foo", 1F), 2F, 2F);
    }

    /**
     * Test for void remove(String, int)
     */
    public void testRemoveStringint()
    {
        params.set("foo", 1);
        params.set("foo", 2);
        params.remove("foo", 1);
        assertEquals(params.isDefined("foo"), true);
        assertEquals(params.getFloat("foo", 1), 2, 2);
    }

    /**
     * Test for void remove(String, long)
     */
    public void testRemoveStringlong()
    {
        params.set("foo", 1L);
        params.set("foo", 2L);
        params.remove("foo", 1L);
        assertEquals(params.isDefined("foo"), true);
        assertEquals(params.getFloat("foo", 1L), 2L, 2L);
    }

    /**
     * Test for void remove(Set)
     */
    public void testRemoveSet()
    {
        params.set("foo", "bar");
        params.set("bar", "foo");
        Set set = new HashSet();
        set.add("foo");
        params.remove(set);
        assertEquals(params.isDefined("foo"), false);
        assertEquals(params.isDefined("bar"),true);
    }

    /**
     * Test for void remove(Set)
     */
    public void testRemoveExcept()
    {
        params.set("foo", "bar");
        params.set("bar", "foo");
        Set set = new HashSet();
        set.add("foo");
        params.removeExcept(set);
        assertEquals(params.isDefined("bar"), false);
        assertEquals(params.isDefined("foo"),true);
    }


    /**
     * Test for void set(String, String)
     */
    public void testSetStringString()
    {
        params.set("foo", "bar");
        assertEquals("bar".equals(params.get("foo")), true);
        params.set("foo", "buzz");
        assertEquals("bar".equals(params.get("foo")), false);
        params.add("foo", "bar");
        try
        {
            params.get("foo");
            fail("should throw the exception");
        }
        catch (AmbiguousParameterException e)
        {
            //was expected
        }
    }

    /**
     * Test for void set(String, String[])
     */
    public void testSetStringStringArray()
    {
        params.set("foo", new String[] { "foo", "bar" });
        String[] result = params.getStrings("foo");
        if (result[0].equals("foo"))
        {
            assertEquals(result[1], "bar");
        }
        else
        {
            assertEquals(result[0], "bar");
            assertEquals(result[1], "foo");
        }
    }

    /**
     * Test for void set(String, boolean)
     */
    public void testSetStringboolean()
    {
        params.set("foo", true);
        assertEquals(params.getBoolean("foo", false), true);
        params.set("foo", false);
        assertEquals(params.getBoolean("foo", true), false);
    }

    /**
     * Test for void set(String, boolean[])
     */
    public void testSetStringbooleanArray()
    {
        params.set("foo", new boolean[] { true, false, true });
        boolean[] result = params.getBooleans("foo");
        assertEquals(result.length, 3);
    }

    /**
     * Test for void set(String, float)
     */
    public void testSetStringfloat()
    {
        params.set("foo", 1F);
        assertEquals(params.getFloat("foo", 2F), 1F, 1F);
    }

    /**
     * Test for void set(String, float[])
     */
    public void testSetStringfloatArray()
    {
        params.set("foo", new float[] { 1, 2, 3 });
        assertEquals(params.getFloats("foo").length, 3);
    }

    /**
     * Test for void set(String, int)
     */
    public void testSetStringint()
    {
        params.set("foo", 1);
        assertEquals(params.getInt("foo", 2), 1);
    }

    /**
     * Test for void set(String, int[])
     */
    public void testSetStringintArray()
    {
        params.set("foo", new int[] { 1, 2, 3 });
        assertEquals(params.getInts("foo").length, 3);
    }

    /**
     * Test for void set(String, long)
     */
    public void testSetStringlong()
    {
        params.set("foo", 1L);
        assertEquals(params.getLong("foo", 2L), 1L, 1L);
    }

    /**
     * Test for void set(String, long[])
     */
    public void testSetStringlongArray()
    {
        params.set("foo", new long[] { 1, 2, 3 });
        assertEquals(params.getInts("foo").length, 3);
    }

    /**
     * Test for void add(String, String)
     */
    public void testAddStringString()
    {
        params.add("foo", "bar");
        params.add("foo", "bar");
        params.add("bar", "foo");
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getStrings("foo").length, 2);
    }

    /**
     * Test for void add(String, String[])
     */
    public void testAddStringStringArray()
    {
        params.add("foo", new String[] { "bar" });
        params.add("foo", new String[] { "foo", "buz" });
        params.add("bar", new String[] { "foo" });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getStrings("foo").length, 3);
    }

    /**
     * Test for void add(String, boolean)
     */
    public void testAddStringboolean()
    {
        params.add("foo", true);
        params.add("foo", false);
        params.add("bar", true);
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getBooleans("foo").length, 2);
        assertEquals(params.getBoolean("bar",false),true);
    }

    /**
     * Test for void add(String, boolean[])
     */
    public void testAddStringbooleanArray()
    {
        params.add("foo", new boolean[] { true });
        params.add("foo", new boolean[] { false, true });
        params.add("bar", new boolean[] { true });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getBooleans("foo").length, 3);
    }

    /**
     * Test for void add(String, float)
     */
    public void testAddStringfloat()
    {
        params.add("foo", 1F);
        params.add("foo", 2F);
        params.add("bar", 1F);
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getFloats("foo").length, 2);
        assertEquals(params.getFloat("bar",2F),1F,1F);
    }

    /**
     * Test for void add(String, float[])
     */
    public void testAddStringfloatArray()
    {
        params.add("foo", new float[] { 1 });
        params.add("foo", new float[] { 2, 3 });
        params.add("bar", new float[] { 1 });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getFloats("foo").length, 3);
    }

    /**
     * Test for void add(String, int)
     */
    public void testAddStringint()
    {
        params.add("foo", 1);
        params.add("foo", 2);
        params.add("bar", 1);
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getInts("foo").length, 2);
        assertEquals(params.getInt("bar",2),1);
    }

    /**
     * Test for void add(String, int[])
     */
    public void testAddStringintArray()
    {
        params.add("foo", new int[] { 1 });
        params.add("foo", new int[] { 2, 3 });
        params.add("bar", new int[] { 1 });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getInts("foo").length, 3);
    }

    /**
     * Test for void add(String, long)
     */
    public void testAddStringlong()
    {
        params.add("foo", 1L);
        params.add("foo", 2L);
        params.add("bar", 1L);
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getLongs("foo").length, 2);
        assertEquals(params.getLong("bar",2L),1L,1L);
    }

    /**
     * Test for void add(String, long[])
     */
    public void testAddStringlongArray()
    {
        params.add("foo", new long[] { 1 });
        params.add("foo", new long[] { 2, 3 });
        params.add("bar", new long[] { 1 });
        assertEquals(params.getParameterNames().length, 2);
        assertEquals(params.getLongs("foo").length, 3);
    }

    /**
     * Test for void add(Parameters, boolean)
     */
    public void testAddParametersboolean()
    {
        Parameters temp = new DefaultParameters();
        temp.add("foo",2);
        temp.add("bar",2);
        params.add("foo",1);
        params.add("bar",1);
        params.add(temp,false);
        assertEquals(params.getInts("foo").length,2);
        assertEquals(params.getInts("bar").length,2);
        params = new DefaultParameters();
        params.add("foo",1);
        params.add("bar",1);
        params.add(temp,true);
        assertEquals(params.getInts("foo").length,1);
        assertEquals(params.getInts("bar").length,1);
        assertEquals(params.getInt("foo"),2);
        assertEquals(params.getInt("bar"),2);
    }

    /**
     * Test for void toString()
     */
    public void testToString()
    {
        String source = "foo=bar,foo=foo,bar=foo\n";
        params = new DefaultParameters(source);
        assertEquals(params.toString(),source);
        
        source = "foo=bar,foo\nbar=foo\n";
        params = new DefaultParameters(source);
        assertEquals(params.toString(),source);
        
        source = "foo=ba\\,r,foo\nbar=foo\n";
        params = new DefaultParameters(source);
        System.out.println("WYNIK:"+params.toString());
        assertEquals(params.toString(),source);
    }

    /**
     * Test for void getChild(String)
     */
    public void testGetChild()
    {
        params.add("foo.bar","foo");
        params.add("foo.buz","bar");
        Parameters children = params.getChild("foo.");
        assertEquals(children.get("bar"),"foo");
    }
}
