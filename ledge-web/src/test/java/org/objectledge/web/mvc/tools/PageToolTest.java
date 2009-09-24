// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
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
package org.objectledge.web.mvc.tools;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Configuration;
import org.jmock.Mock;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.test.LedgeTestCase;
import org.objectledge.test.ReturnArgument;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCInitializerValve;
import org.objectledge.xml.XMLGrammarCache;
import org.objectledge.xml.XMLValidator;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PageToolTest.java,v 1.14 2005-10-10 14:06:44 rafal Exp $
 */
public class PageToolTest extends LedgeTestCase
{
	private LinkToolFactory linkToolFactory;

	private Context context;
    private HttpContext httpContext = null;

	private Mock mockHttpServletRequest;
    private HttpServletRequest httpServletRequest;
    private Mock mockHttpServletResponse;
    private HttpServletResponse httpServletResponse;

	public void setUp()
		throws Exception
	{
		FileSystem fs = FileSystem.getStandardFileSystem("src/test/resources/tools");
		context = new Context();
		XMLValidator validator = new XMLValidator(new XMLGrammarCache());
		ConfigurationFactory configFactory = new ConfigurationFactory(fs, validator, ".");

		Configuration config = configFactory.getConfig(WebConfigurator.class, WebConfigurator.class);
		WebConfigurator webConfigurator = new WebConfigurator(config);
		config = configFactory.getConfig(LinkToolFactory.class, LinkToolFactoryImpl.class);
		linkToolFactory = new LinkToolFactoryImpl(config, context, webConfigurator);

        mockHttpServletRequest = mock(HttpServletRequest.class);
        httpServletRequest = (HttpServletRequest)mockHttpServletRequest.proxy();
        mockHttpServletRequest.stubs().method("getContentType").will(returnValue("text/html"));
        mockHttpServletRequest.stubs().method("getParameterNames").
            will(returnValue((new Vector<String>()).elements()));
        mockHttpServletRequest.stubs().method("getQueryString").will(returnValue(""));
        mockHttpServletRequest.stubs().method("getPathInfo").will(returnValue("view/Default"));
        mockHttpServletRequest.stubs().method("getContextPath").will(returnValue("/test"));
        mockHttpServletRequest.stubs().method("getServletPath").will(returnValue("ledge"));
        mockHttpServletRequest.stubs().method("getRequestURI").will(returnValue(""));
        mockHttpServletRequest.stubs().method("getServerName").will(returnValue("objectledge.org"));

        mockHttpServletResponse = mock(HttpServletResponse.class);
        httpServletResponse = (HttpServletResponse)mockHttpServletResponse.proxy();
        mockHttpServletResponse.stubs().method("encodeURL").with(ANYTHING).
            will(new ReturnArgument());

        HttpContext httpContext = new HttpContext(httpServletRequest, httpServletResponse);
		context.setAttribute(HttpContext.class, httpContext);
		RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
		paramsLoader.process(context);
		MVCInitializerValve mVCInitializer = new MVCInitializerValve(webConfigurator);
		mVCInitializer.process(context);

	}

	public void testFactory()
        throws Exception
	{
		PageToolFactory pageToolFactory = new PageToolFactory(linkToolFactory, context);
		PageTool pageTool1 = (PageTool) pageToolFactory.getTool();
		PageTool pageTool2 = (PageTool) pageToolFactory.getTool();
		assertNotNull(pageTool1);
		assertNotNull(pageTool2);
		assertNotSame(pageTool1, pageTool2);
		pageToolFactory.recycleTool(pageTool1);
		pageToolFactory.recycleTool(pageTool2);
		assertEquals(pageToolFactory.getKey(), "pageTool");
	}

    public void testSetTitle()
        throws Exception    
    {
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
    	PageTool pageTool = new PageTool(linkTool, httpContext);
    	pageTool.setTitle("test title");
    	assertEquals(pageTool.getTitle(), "test title");
    }

    public void testAppendTitleSuffix()
        throws Exception
    {
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool, httpContext);
		pageTool.setTitle("test title");
		pageTool.appendTitleSuffix(" suffix");
		assertEquals(pageTool.getTitle(), "test title suffix");
    }

    public void testInsertTitlePrefix()
        throws Exception
    {
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool, httpContext);
		pageTool.setTitle("test title");
		pageTool.insertTitlePrefix("prefix ");
		assertEquals(pageTool.getTitle(), "prefix test title");
    }

    public void testGetTitle()
        throws Exception
    {
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool, httpContext);
		pageTool.setTitle("test title");
		pageTool.insertTitlePrefix("prefix ");
		pageTool.appendTitleSuffix(" suffix");
		assertEquals(pageTool.getTitle(), "prefix test title suffix");
    }

	public void testAddStyleLink()
        throws Exception
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool, httpContext);
		pageTool.addStyleLink("style/style1.css",1);
		pageTool.addStyleLink("style/style2.css",2);
		pageTool.addStyleLink("style/style1.css");
		List<PageTool.StyleLink> links = pageTool.getStyleLinks();
		assertEquals(links.size(), 2);
		Iterator<PageTool.StyleLink> iter = links.iterator();
		PageTool.StyleLink link1 = iter.next(); 
		PageTool.StyleLink link2 = iter.next(); 
		assertEquals(link1.getPriority(), 1);
		assertEquals(link2.getPriority(), 2);
		assertEquals(link1.getHref(), "/test/content/style/style1.css");
		assertEquals(link2.getHref(), "/test/content/style/style2.css");
	}

	public void testAddScriptLink()
        throws Exception
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool, httpContext);
		pageTool.addScriptLink("js/script1.js", "ISO-8859-1");
		pageTool.addScriptLink("js/script2.js");
		pageTool.addScriptLink("js/script1.js");
		List<PageTool.ScriptLink> links = pageTool.getScriptLinks();
		assertEquals(links.size(), 2);
		Iterator<PageTool.ScriptLink> iter = links.iterator();
		PageTool.ScriptLink link1 = iter.next(); 
		PageTool.ScriptLink link2 = iter.next(); 
		assertEquals(link1.getCharset(), "ISO-8859-1");
		assertEquals(link2.getCharset(), "UTF-8");
		assertEquals(link1.getSrc(), "/test/content/js/script1.js");
		assertEquals(link2.getSrc(), "/test/content/js/script2.js");
	}

	public void testAddNameMeta()
        throws Exception
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool, httpContext);
		pageTool.addNameMeta("author", "Damian Gajda");
		pageTool.addNameMeta("author", "Dodo");
		pageTool.addNameMeta("contributor", "ZwieRzem");
		List<PageTool.Meta> metas = pageTool.getNameMetas();
		assertEquals(metas.size(), 3);
		Iterator<PageTool.Meta> iter = metas.iterator();
		PageTool.Meta meta1 = iter.next(); 
		PageTool.Meta meta2 = iter.next(); 
		PageTool.Meta meta3 = iter.next(); 
		assertEquals(meta1.getName(), "author");
		assertEquals(meta2.getName(), "author");
		assertEquals(meta3.getName(), "contributor");
		assertEquals(meta1.getContent(), "Damian Gajda");
		assertEquals(meta2.getContent(), "Dodo");
		assertEquals(meta3.getContent(), "ZwieRzem");
		assertNull(meta1.getHttpEquiv());
		assertNull(meta2.getHttpEquiv());
		assertNull(meta3.getHttpEquiv());
	}

	public void testAddHttpEquivMeta()
        throws Exception
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool, httpContext);
		pageTool.addHttpEquivMeta("Header", "Value1");
		pageTool.addHttpEquivMeta("Header", "Value2");
		List<PageTool.Meta> metas = pageTool.getHttpEquivMetas();
		assertEquals(metas.size(), 2);
		Iterator<PageTool.Meta> iter = metas.iterator();
		PageTool.Meta meta1 = iter.next(); 
		PageTool.Meta meta2 = iter.next(); 
		assertEquals(meta1.getHttpEquiv(), "Header");
		assertEquals(meta2.getHttpEquiv(), "Header");
		assertEquals(meta1.getContent(), "Value1");
		assertEquals(meta2.getContent(), "Value2");
		assertNull(meta1.getName());
		assertNull(meta2.getName());
	}

	public void testGetLinkTool()
        throws Exception
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool, httpContext);
		assertEquals(pageTool.getLinkTool(), linkTool);
	}

	public void testReset()
        throws Exception
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool, httpContext);
		pageTool.setTitle("test title");
		pageTool.insertTitlePrefix("prefix ");
		pageTool.appendTitleSuffix(" suffix");
		pageTool.addStyleLink("style/style1.css",1);
		pageTool.addStyleLink("style/style2.css",2);
		pageTool.addStyleLink("style/style1.css");
		pageTool.addScriptLink("js/script1.js", "ISO-8859-1");
		pageTool.addScriptLink("js/script2.js");
		pageTool.addScriptLink("js/script1.js");
		pageTool.addNameMeta("author", "Damian Gajda");
		pageTool.addNameMeta("contributor", "ZwieRzem");
		pageTool.addHttpEquivMeta("Header", "Value1");
		pageTool.addHttpEquivMeta("Header", "Value2");
		
		pageTool.reset();

		assertEquals(pageTool.getTitle(), "");
		List<PageTool.StyleLink> stylLinks = pageTool.getStyleLinks();
		assertEquals(stylLinks.size(), 0);
		List<PageTool.ScriptLink> scriptLinks = pageTool.getScriptLinks();
		assertEquals(scriptLinks.size(), 0);
		List<PageTool.Meta> metas = pageTool.getNameMetas();
		assertEquals(metas.size(), 0);
		metas = pageTool.getHttpEquivMetas();
		assertEquals(metas.size(), 0);
	}
}
