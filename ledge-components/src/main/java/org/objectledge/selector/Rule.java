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
package org.objectledge.selector;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.jcontainer.dna.Configuration;


/**
 * A Rule can be evaluated against a set of Variables.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
class Rule
{
    // instance variables ///////////////////////////////////////////////////////////////////
    
    /** The expression tree's root node. */
    private Configuration rootNode;
    
    // initialization ///////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a Rule instance.
     * 
     * @param rootNode the root node of the expression tree.
     */
    public Rule(Configuration rootNode)
    {
        this.rootNode = rootNode;
    }
    
    /**
     * Checks if the Variables satisfy this Rule.
     * 
     * @param varaibles the Varaibles to evaluate the rule against. 
     * @return <code>true</code> if the Variables satisfy the Rule.
     * @throws EvaluationException if the evaluation is impossible due to an undefined
     *         variable being refernced, or a semantic error in the expression.
     */
    public boolean evaluate(Variables varaibles)
        throws EvaluationException
    {
        return evaluate(rootNode, varaibles);
    }
    
    // expressions //////////////////////////////////////////////////////////////////////////
    
    protected boolean evaluate(Configuration node, Variables variables)
        throws EvaluationException
    {
        if("or".equals(node.getName()))
        {
            return evaluateOr(node, variables);
        }
        else if("and".equals(node.getName()))
        {
            return evaluateAnd(node, variables);
        }
        else if("not".equals(node.getName()))
        {
            return evaluateNot(node, variables);
        }
        else
        {
            return evaluateCondition(node, variables);
        }
    }
    
    protected boolean evaluateOr(Configuration node, Variables variables)
        throws EvaluationException
    {
        Configuration[] children = node.getChildren();
        boolean gotTrue = false;
        for (int i = 0; i < children.length && !gotTrue; i++)
        {
            gotTrue = evaluate(children[i], variables);
        }
        return gotTrue;
    }

    protected boolean evaluateAnd(Configuration node, Variables variables)
        throws EvaluationException
    {
        Configuration[] children = node.getChildren();
        boolean gotFalse = false;
        for (int i = 0; i < children.length && !gotFalse; i++)
        {
            gotFalse = !evaluate(children[i], variables);
        }
        return !gotFalse;
    }

    protected boolean evaluateNot(Configuration node, Variables variables)
        throws EvaluationException
    {
        Configuration[] children = node.getChildren();
        if(children.length != 1)
        {
            throw new EvaluationException("not must have only one argument at "+
                node.getLocation());
        }
        return !evaluate(children[0], variables);
    }
    
    // conditions ///////////////////////////////////////////////////////////////////////////
    
    protected boolean evaluateCondition(Configuration node, Variables variables)
        throws EvaluationException
    {
        if("defined".equals(node.getName()))
        {
            return evaluateDefined(node, variables);
        }
        else if("instanceof".equals(node.getName()))
        {
            return evaluateInstanceof(node, variables);
        }
        else if("same".equals(node.getName()))
        {
            return evaluateSame(node, variables);
        }
        else if("equals".equals(node.getName()))
        {
            return evaluateEquals(node, variables);
        }
        else if("lesser".equals(node.getName()))
        {
            return evaluateLesser(node, variables);
        }
        else if("lesser-or-equals".equals(node.getName()))
        {
            return evaluateLesserEquals(node, variables);
        }
        else if("greater".equals(node.getName()))
        {
            return evaluateGreater(node, variables);
        }
        else if("greater-or-equals".equals(node.getName()))
        {
            return evaluateGreaterEquals(node, variables);
        }
        else if("matches".equals(node.getName()))
        {
            return evaluateMatches(node, variables);
        }
        else if("true".equals(node.getName()))
        {
            return evaluateBooleanLiteral(node).booleanValue();        
        }
        else if("false".equals(node.getName()))
        {
            return evaluateBooleanLiteral(node).booleanValue();        
        }
        else
        {
            throw new EvaluationException("unrecognized element "+node.getName()+" at "+
                node.getLocation());
        }
    }
    
    protected boolean evaluateDefined(Configuration node, Variables variables)
        throws EvaluationException
    {
        Configuration[] children = node.getChildren();
        if(children.length != 1)
        {
            throw new EvaluationException("defined must have only one argument at "+
                node.getLocation());
        }
        if(!children[0].getName().equals("variable"))
        {
            throw new EvaluationException("need variable argument at "+node.getLocation());
        }
        String name = evaluateLiteralName(children[0]);
        return variables.isDefined(name);
    }

    protected boolean evaluateInstanceof(Configuration node, Variables variables)
        throws EvaluationException
    {
        Object[] args = evaluateArguments(node, variables, Object.class, Class.class);
        return ((Class)args[1]).isAssignableFrom(args[0].getClass());
    }
    
    protected boolean evaluateSame(Configuration node, Variables variables)
        throws EvaluationException
    {
        Object[] args = evaluateArguments(node, variables, null, null);
        return args[0] == args[1];
    }

    protected boolean evaluateEquals(Configuration node, Variables variables)
        throws EvaluationException
    {
        Object[] args = evaluateArguments(node, variables, Object.class, Object.class);
        return args[0].equals(args[1]); 
    }

    protected boolean evaluateLesser(Configuration node, Variables variables)
        throws EvaluationException
    {
        Object[] args = evaluateArguments(node, variables, Comparable.class, 
            Comparable.class);
        return ((Comparable)args[0]).compareTo(args[1]) < 0; 
    }
                
    protected boolean evaluateLesserEquals(Configuration node, Variables variables)
        throws EvaluationException        
    {
        Object[] args = evaluateArguments(node, variables, Comparable.class, 
            Comparable.class);
        return ((Comparable)args[0]).compareTo(args[1]) <= 0; 
    }

    protected boolean evaluateGreater(Configuration node, Variables variables)
        throws EvaluationException
    {
        Object[] args = evaluateArguments(node, variables, Comparable.class, 
            Comparable.class);
        return ((Comparable)args[0]).compareTo(args[1]) > 0; 
    }
                
    protected boolean evaluateGreaterEquals(Configuration node, Variables variables)
        throws EvaluationException
    {
        Object[] args = evaluateArguments(node, variables, Comparable.class, 
            Comparable.class);
        return ((Comparable)args[0]).compareTo(args[1]) >= 0; 
    }

    protected boolean evaluateMatches(Configuration node, Variables variables)
        throws EvaluationException
    {
        Object[] args = evaluateArguments(node, variables, String.class, String.class);
        return Pattern.matches((String)args[0], (String)args[1]);
    }

    // arguments ////////////////////////////////////////////////////////////////////////////
                
    protected Object[] evaluateArguments(Configuration node, Variables variables,
        Class expectedLType, Class expectedRType)
        throws EvaluationException
    {
        Configuration[] children = node.getChildren();
        if(children.length != 2)
        {
            throw new EvaluationException("expected two arguments at "+node.getLocation());
        }
        Object[] values = new Object[2];
        values[0] = evaluateArgument(children[0], variables);
        if(expectedLType != null)
        {
            if(values[0] == null)
            {
                throw new EvaluationException("non null first argument expected at "+
                    node.getLocation());
            }
            if(!expectedLType.isAssignableFrom(values[0].getClass()))
            {
                throw new EvaluationException("first argument of type "+
                    expectedLType.getName()+" expected at "+node.getLocation());
            }
        }
        values[1] = evaluateArgument(children[1], variables);
        if(expectedRType != null)
        {
            if(values[1] == null)
            {
                throw new EvaluationException("non null second argument expected at "+
                    node.getLocation());
            }
            if(!expectedRType.isAssignableFrom(values[1].getClass()))
            {
                throw new EvaluationException("second argument of type "+
                    expectedLType.getName()+" expected at "+node.getLocation());
            }
        }
        return values;
    }

    protected Object evaluateArgument(Configuration node, Variables variables)
        throws EvaluationException
    {
        if("variable".equals(node.getName()))
        {
            return evaluateVariable(node, variables);        
        }
        else if("string".equals(node.getName()))
        {
            return evaluateStringLiteral(node);        
        }
        else if("int".equals(node.getName()))
        {
            return evaluateIntLiteral(node);        
        }
        else if("long".equals(node.getName()))
        {
            return evaluateLongLiteral(node);        
        }
        else if("decimal".equals(node.getName()))
        {
            return evaluateDecimalLiteral(node);        
        }
        else if("true".equals(node.getName()))
        {
            return evaluateBooleanLiteral(node);        
        }
        else if("false".equals(node.getName()))
        {
            return evaluateBooleanLiteral(node);        
        }
        else if("class".equals(node.getName()))
        {
            return evaluateClassLiteral(node);        
        }
        else if("null".equals(node.getName()))
        {
            return evaluateNullLiteral(node);        
        }
        else
        {
            throw new EvaluationException("unrecognized element "+node.getName()+" at "+
                node.getLocation());
        }
    }

    protected Object evaluateVariable(Configuration node, Variables variables)
        throws EvaluationException
    {
        return variables.get(evaluateLiteralName(node));
    }

    // literals /////////////////////////////////////////////////////////////////////////////
    
    protected String evaluateStringLiteral(Configuration node)
        throws EvaluationException
    {
        return evaluateLiteralValue(node);        
    }

    protected Integer evaluateIntLiteral(Configuration node)
        throws EvaluationException
    {
        try
        {
            return new Integer(Integer.parseInt(evaluateLiteralValue(node)));      
        }
        catch(NumberFormatException e)
        {
            throw new EvaluationException("illeal integer constant at "+node.getLocation(), e);
        }
    }

    protected Long evaluateLongLiteral(Configuration node)
        throws EvaluationException
    {
        try
        {
            return new Long(Long.parseLong(evaluateLiteralValue(node)));      
        }
        catch(NumberFormatException e)
        {
            throw new EvaluationException("illeal long constant at "+node.getLocation(), e);
        }
    }

    protected BigDecimal evaluateDecimalLiteral(Configuration node)
        throws EvaluationException
    {
        try
        {
            return new BigDecimal(evaluateLiteralValue(node));      
        }
        catch(NumberFormatException e)
        {
            throw new EvaluationException("illeal decimal constant at "+node.getLocation(), e);
        }
    }

    protected Boolean evaluateBooleanLiteral(Configuration node)
        throws EvaluationException
    {
        if(node.getChildren().length > 0)
        {
            throw new EvaluationException("unexpected child elements at "+node.getLocation());
        }
        if(node.getName().equals("true"))
        {
            return Boolean.TRUE;
        }
        else
        {
            return Boolean.FALSE;
        }
    }
    
    protected Class evaluateClassLiteral(Configuration node)
        throws EvaluationException
    {
        try
        {
            return Class.forName(evaluateLiteralName(node));        
        }
        catch(ClassNotFoundException e)
        {
            throw new EvaluationException("illegal Class constant at "+node.getLocation(), e);
        }
    }

    protected Object evaluateNullLiteral(Configuration node)
        throws EvaluationException
    {
        if(node.getChildren().length > 0)
        {
            throw new EvaluationException("unexpected child elements at "+node.getLocation());
        }
        return null;
    }
    
    // literals representation //////////////////////////////////////////////////////////////
    
    protected String evaluateLiteralName(Configuration node)
        throws EvaluationException
    {
        if(node.getChildren().length > 0)
        {
            throw new EvaluationException("unexpected child elements at "+node.getLocation());
        }
        if(node.getAttribute("name", null) != null && node.getValue(null) != null)
        {
            throw new EvaluationException("name attribute and text content defined "+
                "simulataneously at "+node.getLocation());
        }
        if(node.getAttribute("name", null) != null)
        {
            return node.getAttribute("name", null);
        }
        else
        {
            return node.getValue(null);
        }
    }

    protected String evaluateLiteralValue(Configuration node)
        throws EvaluationException
    {
        if(node.getChildren().length > 0)
        {
            throw new EvaluationException("unexpected child elements at "+node.getLocation());
        }
        if(node.getAttribute("value", null) != null && node.getValue(null) != null)
        {
            throw new EvaluationException("value attribute and text content defined "+
                "simulataneously at "+node.getLocation());
        }
        if(node.getAttribute("value", null) != null)
        {
            return node.getAttribute("value", null);
        }
        else
        {
            return node.getValue(null);
        }
    }
}