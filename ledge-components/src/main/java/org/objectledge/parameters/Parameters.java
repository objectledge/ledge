package org.objectledge.parameters;

/**
 * A container of parameters.
 *
 *
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: Parameters.java,v 1.5 2003-11-28 14:40:08 pablo Exp $
 */
public interface Parameters
{
    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the string value of the parameter.
     */
    public String get(String name);

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the string value of the parameter.
     */
    public String get(String name, String defaultValue);

    /**
     * Return all values of the parameter with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the string values of the parameter.
    */
    public String[] getStrings(String name);

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the boolean value of the parameter.
     */
    public boolean getBoolean(String name);

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the boolean value of the parameter.
     */
    public boolean getBoolean(String name, boolean defaultValue);

	/**
	 * Return all values of the parameter with specified name as an array. 
	 * 
	 * @param name the name of the parameters.
	 * @return the array of the boolean values of the parameter.
	*/
	public boolean[] getBooleans(String name);

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the float value of the parameter.
     * @throws NumberFormatException if parameter is not a number.
     */
    public float getFloat(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the float value of the parameter.
     */
    public float getFloat(String name, float defaultValue);

	/**
	 * Return all values of the parameter with specified name as an array. 
	 * 
	 * @param name the name of the parameters.
	 * @return the array of the float values of the parameter.
	 * @throws NumberFormatException if anyone of the values is not a number. 
	*/
	public float[] getFloats(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the integer value of the parameter.
     * @throws NumberFormatException if parameter is not a number.
     */
    public int getInt(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the integer value of the parameter.
     */
    public int getInt(String name, int defaultValue);

	/**
	 * Return all values of the parameter with specified name as an array. 
	 * 
	 * @param name the name of the parameters.
	 * @return the array of the integer values of the parameter.
	 * @throws NumberFormatException if anyone of the values is not a number.
	*/
	public int[] getInts(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the integer value of the parameter.
     * @throws NumberFormatException if parameter is not a number.
     */
    public long getLong(String name) throws NumberFormatException;

	/**
	 * Return the parameter as array of long values. 
	 * 
	 * @param name the name of the parameter.
	 * @return the array of parameter values.
	 * @throws NumberFormatException if parameter is not a number.
	 */
	public long[] getLongs(String name) throws NumberFormatException;

    /**
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @param defaultValue the default value of the parameter.
     * @return the integer value of the parameter.
     */
    public long getLong(String name, long defaultValue);

    /**
     * Return the names of all parameters.
     * 
     * @return the parameter names.
     */
    public String[] getParameterNames();

    /**
     * Checks whether parameter is defined.
     * 
     * @param name the name of the parameter.
     * @return <code>true</code> if parameter is defined.
     */
    public boolean isDefined(String name);

    /**
     * Remove all parameters.
     */
    public void remove();

    /**
     * Remove all parameters with a specified name.
     *
     * @param name the parameter name.
     */
    public void remove(String name);

    /**
     * Remove all parameters with a specified name and string value.
     *
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void remove(String name, String value);
    
	/**
	 * Remove all parameters with a specified name and value.
	 *
	 * @param name the parameter name.
	 * @param value the parameter value.
	 */
	public void remove(String name, float value);
    
	/**
	 * Remove all parameters with a specified name and value.
	 *
	 * @param name the parameter name.
	 * @param value the parameter value.
	 */
	public void remove(String name, int value);
        
	/**
	 * Remove all parameters with a specified name and value.
	 *
	 * @param name the parameter name.
	 * @param value the parameter value.
	 */
	public void remove(String name, long value);
    
    /**
     * Set the parameter.
     * 
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void set(String name, String value);
    
	/**
	 * Set the parameter.
	 * 
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void set(String name, String[] values);
    
    /**
     * Set the parameter.
     * 
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void set(String name, boolean value);

	/**
	 * Set the parameter.
	 * 
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void set(String name, boolean[] values);

    /**
     * Set the parameter.
     * 
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void set(String name, float value);

	/**
	 * Set the parameter.
	 * 
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void set(String name, float[] values);

    /**
     * Set the parameter.
     * 
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void set(String name, int value);

	/**
	 * Set the parameter.
	 * 
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void set(String name, int[] values);

    /**
     * Set the parameter.
     * 
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void set(String name, long value);
    
	/**
	 * Set the parameter.
	 * 
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void set(String name, long[] values);

    /**
     * Add the parameter.
     *
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void add(String name, String value);
    
	/**
	 * Add the parameter.
	 *
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void add(String name, String[] values);

    /**
     * Add the parameter.
     *
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void add(String name, boolean value);
    
	/**
	 * Add the parameter.
	 *
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void add(String name, boolean[] values);

    /**
     * Add the parameter.
     *
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void add(String name, float value);
    
	/**
	 * Add the parameter.
	 *
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void add(String name, float[] values);

    /**
     * Add the parameter.
     *
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void add(String name, int value);
    
	/**
	 * Add the parameter.
	 *
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void add(String name, int[] values);

    /**
     * Add the parameter.
     *
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void add(String name, long value);

	/**
	 * Add the parameter.
	 *
	 * @param name the parameter name.
	 * @param values the parameter values.
	 */
	public void add(String name, long[] values);
	
    /**
     * Adds all parameters from another container to this container.
     *
     * If overwrite is set to <code>true</code> all conflicting 
     * parameters from this container will be replaced,
     * otherwise all parameters from another container will be added.   
     *
     * @param parameters the parameters object.
     * @param overwrite the overwrite switch.
     */
    public void add(Parameters parameters, boolean overwrite);

    /**
     * Returns the contained properties as a parsable String.
     *
     * @return parsable String representation of the contained properties.
     */
    public String getString();

    /**
     * Return a parameters object that represents a subset of parameters with specified prefix.
     * 
     * @param prefix the prefix. 
     * @return the nested parameters object.
     */
    public Parameters getChild(String prefix);
}
