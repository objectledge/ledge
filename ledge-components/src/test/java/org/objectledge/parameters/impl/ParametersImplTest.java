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

package org.objectledge.parameters.impl;

import junit.framework.TestCase;

import org.objectledge.parameters.AmbiguousParameterException;
import org.objectledge.parameters.UndefinedParameterException;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 *
 */
public class ParametersImplTest extends TestCase
{
	/** parameter container */
    protected ParametersImpl params;

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        params = new ParametersImpl();
    }

    /**
     * Constructor for ParametersImplTest.
     * 
     * @param arg0 default arg.
     */
    public ParametersImplTest(String arg0)
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
        params = new ParametersImpl("foo=bar\n");
        assertEquals(params.get("foo"),"bar");
		params = new ParametersImpl("foo=bar,buzz,foo\nbar=foo");
		assertEquals(params.getStrings("foo").length,3);
		assertEquals(params.get("bar"),"foo");
		params = new ParametersImpl("");
		assertEquals(params.get("foo","bar"),"bar");
		try
		{
			params = new ParametersImpl("foo");
			fail("Should throw IllegalArgumentException");
		}
		catch(IllegalArgumentException e)
		{
			//expected
		}
    }

    /**
     * Test for void ParametersImpl(InputStream, String)
     * 
     */
    public void testParametersImplInputStreamString()
    {
        //Tested well by testParametersImplString
    }

    /**
     * Test for void ParametersImpl(Parameters)
     */
    public void testParametersImplParameters()
    {
        //TODO Implement ParametersImpl().
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
		catch(UndefinedParameterException e)
		{
			// expected
		}
		params.add("foo","bar");
		assertEquals(params.get("foo"), "bar");
		params.add("foo","true");
		try
		{
			assertEquals(params.get("foo"), "bar");
			fail("Should throw AmbiguousParameterException");
		}
		catch(AmbiguousParameterException e)
		{
			// expected
		}
    }

    /**
     * Test for String get(String, String)
     */
    public void testGetStringString()
    {
		assertEquals(params.get("foo","bar"), "bar");
		params.add("foo","bar");
		assertEquals(params.get("foo","buzz"), "bar");
		params.add("foo","buzz");
		try
		{
			assertEquals(params.get("foo","buzz"), "bar");
			fail("Should throw AmbiguousParameterException");
		}
		catch(AmbiguousParameterException e)
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
		params.add("foo","bar");
		params.add("foo","true");
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
		catch(UndefinedParameterException e)
		{
			// expected
		}
		params.add("foo","bar");
		assertEquals(params.getBoolean("foo"), false);
		params.set("foo","true");
		assertEquals(params.getBoolean("foo"), true);
    }

    /**
     * Test for boolean getBoolean(String, boolean)
     */
    public void testGetBooleanStringboolean()
    {
		assertEquals(params.getBoolean("foo",false), false);
		params.add("foo","bar");
		assertEquals(params.getBoolean("foo"), false);
		params.set("foo","true");
		assertEquals(params.getBoolean("foo"), true);
    }

	/**
	 * Test for boolean getBooleans(String)
	 */
    public void testGetBooleans()
    {
		assertEquals(params.getBooleans("foo").length, 0);
		params.add("foo","bar");
		params.add("foo","true");
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
    		assertEquals(params.getFloat("foo"),1,1);
			fail("Should throw UndefinedParameterException");
		}
		catch(UndefinedParameterException e)
		{
			// expected
		}
        params.add("foo",1);
        assertEquals(params.getFloat("foo"),1,1);
		params.add("foo",2);
		try
		{
			params.getFloat("foo");
			fail("Should throw AmbiguousParameterException");
		}
		catch(AmbiguousParameterException e)
		{
			// expected
		}
		params.set("foo","bar");
		try
		{
			params.getFloat("foo");
			fail("Should throw NumberFormatException");
		}
		catch(NumberFormatException e)
		{
			// expected
		}
    }

    /**
     * Test for float getFloat(String, float)
     */
    public void testGetFloatStringfloat()
    {
        //TODO Implement getFloat().
    }

	/**
	 * Test for float getFloats(String)
	 */
    public void testGetFloats()
    {
        //TODO Implement getFloats().
    }

    /**
     * Test for int getInt(String)
     */
    public void testGetIntString()
    {
        //TODO Implement getInt().
    }

    /**
     * Test for int getInt(String, int)
     */
    public void testGetIntStringint()
    {
        //TODO Implement getInt().
    }

	/**
	 * Test for get ints.
	 */
    public void testGetInts()
    {
        //TODO Implement getInts().
    }

    /**
     * Test for long getLong(String)
     */
    public void testGetLongString()
    {
        //TODO Implement getLong().
    }

    /**
     * Test for long getLong(String, long)
     */
    public void testGetLongStringlong()
    {
        //TODO Implement getLong().
    }

	/**
	 * Test for long getLongs()
	 */
    public void testGetLongs()
    {
        //TODO Implement getLongs().
    }

	/**
	 * Test for getParameterNames()
	 */
    public void testGetParameterNames()
    {
    	params.set("foo","bar");
    	params.set("foo","buzz");
		params.set("bar","buzz");
		params.set("buzz","bar");
		assertEquals(params.getParameterNames().length, 3);
    }

	/**
	 * Test for boolean isDefined()
	 */
    public void testIsDefined()
    {
		assertEquals(params.isDefined("foo"),false);
    	params.set("foo","bar");
		assertEquals(params.isDefined("foo"),true);
		params.remove("foo");
		assertEquals(params.isDefined("foo"),false);
    }

    /**
     * Test for void remove()
     */
    public void testRemove()
    {
        //TODO Implement remove().
    }

    /**
     * Test for void remove(String)
     */
    public void testRemoveString()
    {
        //TODO Implement remove().
    }

    /**
     * Test for void remove(String, String)
     */
    public void testRemoveStringString()
    {
        //TODO Implement remove().
    }

    /**
     * Test for void remove(String, float)
     */
    public void testRemoveStringfloat()
    {
        //TODO Implement remove().
    }

    /**
     * Test for void remove(String, int)
     */
    public void testRemoveStringint()
    {
        //TODO Implement remove().
    }

    /**
     * Test for void remove(String, long)
     */
    public void testRemoveStringlong()
    {
        //TODO Implement remove().
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
        //TODO Implement set().
    }

    /**
     * Test for void set(String, boolean)
     */
    public void testSetStringboolean()
    {
        //TODO Implement set().
    }

    /**
     * Test for void set(String, boolean[])
     */
    public void testSetStringbooleanArray()
    {
        //TODO Implement set().
    }

    /**
     * Test for void set(String, float)
     */
    public void testSetStringfloat()
    {
        //TODO Implement set().
    }

    /**
     * Test for void set(String, float[])
     */
    public void testSetStringfloatArray()
    {
        //TODO Implement set().
    }

    /**
     * Test for void set(String, int)
     */
    public void testSetStringint()
    {
        //TODO Implement set().
    }

    /**
     * Test for void set(String, int[])
     */
    public void testSetStringintArray()
    {
        //TODO Implement set().
    }

    /**
     * Test for void set(String, long)
     */
    public void testSetStringlong()
    {
        //TODO Implement set().
    }

    /**
     * Test for void set(String, long[])
     */
    public void testSetStringlongArray()
    {
        //TODO Implement set().
    }

    /**
     * Test for void add(String, String)
     */
    public void testAddStringString()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(String, String[])
     */
    public void testAddStringStringArray()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(String, boolean)
     */
    public void testAddStringboolean()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(String, boolean[])
     */
    public void testAddStringbooleanArray()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(String, float)
     */
    public void testAddStringfloat()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(String, float[])
     */
    public void testAddStringfloatArray()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(String, int)
     */
    public void testAddStringint()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(String, int[])
     */
    public void testAddStringintArray()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(String, long)
     */
    public void testAddStringlong()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(String, long[])
     */
    public void testAddStringlongArray()
    {
        //TODO Implement add().
    }

    /**
     * Test for void add(Parameters, boolean)
     */
    public void testAddParametersboolean()
    {
        //TODO Implement add().
    }

	/**
	 * Test for void getString()
	 */
    public void testGetString()
    {
        //TODO Implement getString().
    }

	/**
	 * Test for void getChild(String)
	 */
    public void testGetChild()
    {
        //TODO Implement getChild().
    }
}
