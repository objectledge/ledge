package org.objectledge.parameters;

/**
 * A container of parameters.
 *
 *
 * @author <a href="mailto:pablo@caltha.org">Pawel Potempski</a>
 * @version $Id: Parameters.java,v 1.2 2003-11-27 12:31:22 pablo Exp $
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
     * Return all the parameters with specified name as an array. 
     * 
     * @param name the name of the parameters.
     * @return the array of the string values of the parameters.
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
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the float value of the parameter.
     * @throws NumberFormatException.
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
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the integer value of the parameter.
     * @throws NumberFormatException.
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
     * Return the parameter with specified name. 
     * 
     * @param name the name of the parameter.
     * @return the integer value of the parameter.
     * @throws NumberFormatException.
     */
    public long getLong(String name) throws NumberFormatException;

	/**
	 * Return the parameter as array of long values. 
	 * 
	 * @param name the name of the parameter.
	 * @return the array of parameter values.
	 * @throws NumberFormatException.
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
    public boolean isDefinied(String name);

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
     * @param value the parameter value.
     */
    public void set(String name, boolean value);

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
     * @param value the parameter value.
     */
    public void set(String name, int value);

    /**
     * Set the parameter.
     * 
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void set(String name, long value);

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
	 * @param value the parameter values.
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
     * @param value the parameter value.
     */
    public void add(String name, float value);

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
     * @param value the parameter value.
     */
    public void add(String name, long value);

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
    public void add(Parameters parameters, boolean override);

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
