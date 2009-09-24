// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
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
package org.objectledge.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;


/**
 * Tests for the general-purpose visitor.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: VisitorTest.java,v 1.4 2006-02-08 18:26:51 zwierzem Exp $
 */
public class VisitorTest
    extends TestCase
{
    private List<String> recorder = new ArrayList<String>();
    
    public void setUp()
    {
        recorder.clear();
    }
    
    public void testBreadthFirst()
    {
        Item g = new Item("a", new Item("b", new Item("c", null)));
        ItemVisitor iv = new ItemRecorderVisitor();
        iv.traverseBreadthFirst(g);
        assertEquals("[a, b, c]", recorder.toString());
    }

    public void testDepthFirst()
    {
        Item g = new Item("a", new Item("b", new Item("c", null)));
        ItemVisitor iv = new ItemRecorderVisitor();
        iv.traverseDepthFirst(g);
        assertEquals("[c, b, a]", recorder.toString());
    }
    
    public void testSelective()
    {
        Item g = new Item("a", new SubItem("b", new Item("c", null)));
        ItemVisitor iv = new SubItemOnlyRecorderVisitor();
        iv.traverseBreadthFirst(g);
        assertEquals("[b]", recorder.toString());        
    }
    
    public void testPolymorphic()
    {
        Item g = new Item("a", new SubItem("b", new Item("c", null)));
        ItemVisitor iv = new PolymorphicRecorderVisitor();
        iv.traverseBreadthFirst(g);
        assertEquals("[a, B, c]", recorder.toString());                
    }

    public void testInexactPolymorphic()
    {
        Item g = new Item("a", new SubItem("b", new SubSubItem("c", null)));
        ItemVisitor iv = new InexactPolymorphicRecorderVisitor();
        iv.traverseBreadthFirst(g);
        assertEquals("[a, B, c]", recorder.toString());
    }   
    
    public void testInnerVisitor()
    {
        Item g = new Item("a", new SubItem("b", new SubSubItem("c", null)));
        final List<String> localRecorder = new ArrayList<String>();
        ItemVisitor iv = new ItemVisitor()
        {
            @SuppressWarnings("unused")
            public void visit(Item i)
            {
                localRecorder.add(i.value());
            }
        };
        iv.traverseBreadthFirst(g);
        assertEquals("[a, b, c]", localRecorder.toString());
    }
    
    public class ItemRecorderVisitor
        extends ItemVisitor
    {
        public void visit(Item i)
        {
            recorder.add(i.value());
        }
    }
    
    public class SubItemOnlyRecorderVisitor
        extends ItemVisitor
    {
        public void visit(SubItem i)
        {
            recorder.add(i.value());
        }
    }
    
    public class PolymorphicRecorderVisitor
        extends ItemVisitor
    {        
        public void visit(Item i)
        {
            recorder.add(i.value());
        }        

        public void visit(SubItem i)
        {
            recorder.add(i.value().toUpperCase());
        }
    }
    
    public class InexactPolymorphicRecorderVisitor
        extends ItemVisitor
    {
        // in case of inexact match will be prefered over visit(SubItem) due to lower order value
        @DispatchOrder(1)
        public void visit(Item i)
        {
            recorder.add(i.value());
        }        

        @DispatchOrder(2)
        public void visit(SubItem i)
        {
            recorder.add(i.value().toUpperCase());
        }        
    }
    
    public static class ItemVisitor
        extends Visitor<Item>
    {
        @Override
        protected Iterator<Item> successors(Item o)
        {
            Item next = o.next();
            if(next == null)
            {
                List<Item> list = Collections.emptyList(); 
                return list.iterator();
            }
            else
            {
                return Collections.singletonList(next).iterator();
            }
        }
    }

    public static class Item
    {
        private final String value;
        
        private final Item next;
        
        public Item(String valueArg, Item nextArg)
        {
            value = valueArg;
            next = nextArg;
        }
        
        public String value()
        {
            return value;
        }
        
        public Item next()
        {
           return next;
        }
    }
    
    public static class SubItem
        extends Item
    {
        public SubItem(String valueArg, Item nextArg)
        {
            super(valueArg, nextArg);
        }
    }

    public static class SubSubItem
        extends SubItem
    {
        public SubSubItem(String valueArg, Item nextArg)
        {
            super(valueArg, nextArg);
        }
    }
}
