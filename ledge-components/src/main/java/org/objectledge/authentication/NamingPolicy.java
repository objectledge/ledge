// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 
package org.objectledge.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.parameters.Parameters;

/**
 * Specifies a policy of naming accounts in the system.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: NamingPolicy.java,v 1.4 2005-02-21 16:14:36 zwierzem Exp $
 */
public class NamingPolicy
{
    private String loginProperty;
    
    private Properties syntax = new Properties();
    
    private Token[] tokens;
    
    /**
     * Creates an instance of the NamingPolicyComponent.
     * 
     * @param config the configuration.
     * @throws ConfigurationException if the configuration is invalid.
     */
    public NamingPolicy(Configuration config)
        throws ConfigurationException
    {
        loginProperty = config.getChild("login-property").getAttribute("name", null);
        if(loginProperty == null)
        { 
           loginProperty = config.getChild("login-property").getValue(null);
        }
        
        Configuration syntaxConfig = config.getChild("syntax");
        Configuration tokensConfig = config.getChild("tokens");
        if(config.getChild("syntax").getChild("ldap-syntax", false) != null)
        {
            syntax.put("jndi.syntax.direction", "right_to_left");
            syntax.put("jndi.syntax.separator", ",");
            syntax.put("jndi.syntax.separator.ava", ",");
            syntax.put("jndi.syntax.separator.typeval", "=");
            syntax.put("jndi.syntax.ignorecase", "true");
            syntax.put("jndi.syntax.escape", "\\");
        }
        else
        {
            Configuration[] syntaxProperties = syntaxConfig.getChildren("property");
            for(int i=0; i<syntaxProperties.length; i++)
            {
                syntax.put(syntaxProperties[i].getAttribute("name"), 
                    syntaxProperties[i].getValue(syntaxProperties[i].getAttribute("value", null)));
            }
        }
        Configuration[] tokenConfig = tokensConfig.getChildren("token");
        tokens = new Token[tokenConfig.length];
        boolean loginFound = false;
        for(int i=0; i<tokenConfig.length; i++)
        {
            tokens[i] = new Token(tokenConfig[i]);
            if(tokens[i].specifies(loginProperty))
            {
                loginFound = true;
            }
        }
        if(tokens.length > 1 && syntax.get("jndi.syntax.separator") == null)
        {
            throw new ConfigurationException("multiple tokens defined, but syntax does not "+
                "specify jndi.syntax.separator", 
                 syntaxConfig.getPath(), syntaxConfig.getLocation());
        }
        if(!loginFound)
        {
            throw new ConfigurationException("no token specifies "+loginProperty+" property", 
                 tokensConfig.getPath(), tokensConfig.getLocation());
        }
    }

    /**
     * Returns a distinguished name constructed from provided parameters in conformance to 
     * configured syntax.
     * 
     * @param parameters the parameters to compose name of.
     * @return the distinguished name.
     */    
    public String getDn(Parameters parameters)
    {
        StringBuilder target = new StringBuilder();
        String sep = (String)syntax.get("jndi.syntax.separator");
        for(int i=0; i<tokens.length; i++)
        {
            tokens[i].render(parameters, target);
            if(i<tokens.length-1)
            {
                target.append(sep);
            }
        }
        return target.toString();
    }
    
    /**
     * Retrieves the login name from the distinguished name.
     * 
     * @param dn the distinguished name.
     * @return the login name.
     * @throws InvalidNameException if the name does not conform to the defined syntax.
     */
    public String getLogin(String dn)
        throws InvalidNameException
    {
        Name name = new CompoundName(dn, syntax);
        if(name.size() != tokens.length)
        {
            throw new InvalidNameException("invalid name, expecting "+tokens.length+" elements");
        }
        for(int i=0; i<name.size(); i++)
        {
            if(tokens[i].specifies(loginProperty))
            {
                return tokens[i].get(name.get(name.size()-1-i), loginProperty);
            }
            else
            {
                if(!tokens[i].match(name.get(name.size()-1-i)))
                {
                    throw new InvalidNameException("invalied name, element "+
                        name.get(name.size()-1-i)+ " does not match "+tokens[i].toString());
                }
            }
        }
        ///CLOVER:ON
        throw new IllegalStateException();
        ///CLOVER:OFF
    }

    /**
     * Represents syntax of a compound disthinguished name element. 
     * 
     * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
     * @version $Id: NamingPolicy.java,v 1.4 2005-02-21 16:14:36 zwierzem Exp $
     */    
    public static class Token
    {
        /** constant string elements, first and last may be null. */
        private String[] strings;
        
        /** embedded property names, alwas has string.length - 1 elements. */
        private String[] properties;
        
        /**
         * Creates token instance from a configuration element.
         * 
         * @param config the configuration element.
         * @throws ConfigurationException if the configuration is invalid.
         */
        public Token(Configuration config)
            throws ConfigurationException
        {
            Configuration[] elements = config.getChildren();
            if(elements.length == 0)
            {
                throw new ConfigurationException("at least one element required", config.getPath(),
                    config.getLocation());
            }
            List stringSpecs = new ArrayList(elements.length/2);
            List propertySpecs = new ArrayList(elements.length/2);
            for(int i=0; i<elements.length; i++)
            {
                if(elements[i].getName().equals("string"))
                {
                    String value = elements[i].getAttribute("value", null);
                    if(value == null)
                    {
                        value = elements[i].getValue();
                    }
                    stringSpecs.add(value);
                }
                else
                {
                    if(stringSpecs.size() == 0)
                    {
                        stringSpecs.add(null);
                    }
                    String name = elements[i].getAttribute("name", null);
                    if(name == null)
                    {
                        name = elements[i].getValue();
                    }
                    propertySpecs.add(name);
                }
            }
            if(stringSpecs.size() == propertySpecs.size())
            {
                stringSpecs.add(null);
            }
            strings = new String[stringSpecs.size()];
            stringSpecs.toArray(strings);
            properties = new String[propertySpecs.size()];
            propertySpecs.toArray(properties);
        }
        
        /**
         * Renders token image based on provided parameters into an output buffer.
         * 
         * @param parameters the parameters to use for rendering.
         * @param target the output buffer.
         */
        public void render(Parameters parameters, StringBuilder target)
        {
            for(int i=0; i<strings.length + properties.length; i++)
            {
                if(i%2 == 0)
                {
                    if(strings[i/2] != null)
                    {
                        target.append(strings[i/2]);
                    }
                }
                else
                {
                    String value = parameters.get(properties[i/2],null);
                    if(value != null)
                    { 
                        target.append(value);
                    }
                    else
                    {
                        throw new NoSuchElementException("undefined property "+properties[i/2]);
                    }
                }
            }
        }

        /**
         * Checks if the given token image matches the specified syntax.
         * 
         * @param image the token image.
         * @return <code>true</code> if the given token image matches the specified syntax.
         */        
        public boolean match(String image)
        {
            int lastPos = 0;
            for(int i=0; i<strings.length; i++)
            {
                if(strings[i] != null)
                {
                    int pos = image.indexOf(strings[i], lastPos);
                    if(pos >= 0)
                    {
                        lastPos = pos+strings[i].length();
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Retrieves a property from given token image.
         * 
         * @param image the token image.
         * @param propertyName the property name.
         * @return the property value.
         * @throws IllegalArgumentException if the token does not specify this property.
         * @throws InvalidNameException if the token does not conform to defined syntax.
         */        
        public String get(String image, String propertyName)
            throws IllegalArgumentException, InvalidNameException
        {
            int lastPos = 0;
            int nextPos = image.length();
            for(int i=0; i<strings.length+properties.length; i++)
            {
                if(i%2 == 0)
                {
                    if(strings[i/2] != null)
                    {
                        int pos = image.indexOf(strings[i/2], lastPos);
                        if(pos >= 0)
                        {
                            lastPos = pos+strings[i/2].length();
                        }
                        else
                        {
                            throw new InvalidNameException("invalid token "+image+" "+
                                strings[i/2]+" is missing");
                        }
                    }
                    if(i/2+1 < strings.length)
                    {
                        if(strings[i/2+1] != null)
                        {
                            int pos = image.indexOf(strings[i/2+1], lastPos);
                            if(pos >= 0)
                            {
                                nextPos = pos;
                            }
                            else
                            {
                                throw new InvalidNameException("invalid token "+image+" "+
                                    strings[i/2+1]+" is missing");
                            }
                        }
                        else
                        {
                            nextPos = image.length();
                        }
                    }
                }
                else
                {
                    if(properties[i/2].equals(propertyName))
                    {
                        return image.substring(lastPos, nextPos);
                    }
                }
            }
            throw new IllegalArgumentException("token does not specify "+propertyName+" property");
        }
    
        /**
         * Checks if this token specifies a property.
         * 
         * @param propertyName the name of the property.
         * @return <code>true</code> if this token specifies a property.
         */        
        public boolean specifies(String propertyName)
        {
            for(int i=0; i<properties.length; i++)
            {
                if(properties[i].equals(propertyName))
                {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Returns a string representation of the token's syntax.
         * 
         * @return a string representation of the token's syntax.
         */
        public String toString()
        {
            StringBuilder target = new StringBuilder();
            for(int i=0; i<strings.length + properties.length; i++)
            {
                if(i%2 == 0)
                {
                    if(strings[i/2] != null)
                    {
                        target.append(strings[i/2]);
                    }
                }
                else
                {
                    target.append('<').append(properties[i/2]).append('>');
                }
            }
            return target.toString();            
        }
    }
}
