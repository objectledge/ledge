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
import org.jcontainer.dna.Logger;
import org.jmock.Mock;
import org.objectledge.configuration.ConfigurationFactory;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LoggerFactory;
import org.objectledge.parameters.RequestParametersLoaderValve;
import org.objectledge.templating.Templating;
import org.objectledge.templating.velocity.VelocityTemplating;
import org.objectledge.utils.LedgeTestCase;
import org.objectledge.utils.ReturnArgument;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCInitializerValve;
import org.objectledge.xml.XMLValidator;

/**
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PageToolTest.java,v 1.4 2004-05-28 13:34:28 fil Exp $
 */
public class PageToolTest extends LedgeTestCase
{
	private LinkToolFactory linkToolFactory;

	private Context context;

	private Mock mockHttpServletRequest;
    private HttpServletRequest httpServletRequest;
    private Mock mockHttpServletResponse;
    private HttpServletResponse httpServletResponse;

	public void setUp()
		throws Exception
	{
		//prepare test
		String root = System.getProperty("ledge.root");
		if (root == null)
		{
			throw new Exception("system property ledge.root undefined. " +
					 "use -Dledge.root=.../ledge-container/src/test/resources");
		}
		FileSystem fs = FileSystem.getStandardFileSystem(root + "/tools");
		context = new Context();
		XMLValidator validator = new XMLValidator();
		ConfigurationFactory configFactory = new ConfigurationFactory(fs, validator, ".");

		Configuration config = configFactory.getConfig(Templating.class, VelocityTemplating.class);
		LoggerFactory loggerFactory = new LoggerFactory();
		Logger logger = loggerFactory.getLogger(Templating.class);
		Templating templating = new VelocityTemplating(config, logger, fs);
		config = configFactory.getConfig(WebConfigurator.class, WebConfigurator.class);
		WebConfigurator webConfigurator = new WebConfigurator(config);
		config = configFactory.getConfig(LinkToolFactory.class, LinkToolFactory.class);
		linkToolFactory = new LinkToolFactory(config, context, webConfigurator);

        mockHttpServletRequest = mock(HttpServletRequest.class);
        httpServletRequest = (HttpServletRequest)mockHttpServletRequest.proxy();
        mockHttpServletRequest.stubs().method("getContentType").will(returnValue("text/html"));
        mockHttpServletRequest.stubs().method("getParameterNames").will(returnValue((new Vector()).elements()));
        mockHttpServletRequest.stubs().method("getPathInfo").will(returnValue("view/Default"));
        mockHttpServletRequest.stubs().method("getContextPath").will(returnValue("/test"));
        mockHttpServletRequest.stubs().method("getServletPath").will(returnValue("ledge"));
        mockHttpServletRequest.stubs().method("getRequestURI").will(returnValue(""));
        mockHttpServletRequest.stubs().method("getServerName").will(returnValue("objectledge.org"));

        mockHttpServletResponse = mock(HttpServletResponse.class);
        httpServletResponse = (HttpServletResponse)mockHttpServletResponse.proxy();
        mockHttpServletResponse.stubs().method("encodeURL").with(ANYTHING).will(new ReturnArgument());

        HttpContext httpContext = new HttpContext(httpServletRequest, httpServletResponse);

        httpContext.setEncoding(webConfigurator.getDefaultEncoding());
		context.setAttribute(HttpContext.class, httpContext);
		RequestParametersLoaderValve paramsLoader = new RequestParametersLoaderValve();
		paramsLoader.process(context);
		MVCInitializerValve mVCInitializer = new MVCInitializerValve(webConfigurator);
		mVCInitializer.process(context);

	}

	public void testFactory()
	{
		PageToolFactory pageToolFactory = new PageToolFactory(linkToolFactory);
		PageTool pageTool1 = (PageTool) pageToolFactory.getTool();
		PageTool pageTool2 = (PageTool) pageToolFactory.getTool();
		assertNotNull(pageTool1);
		assertNotNull(pageTool2);
		assertNotSame(pageTool1, pageTool2);
		pageToolFactory.recycleTool(pageTool1);
		pageToolFactory.recycleTool(pageTool2);
		assertEquals(pageToolFactory.getKey(), "page_tool");
	}

    public void testSetTitle()
    {
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
    	PageTool pageTool = new PageTool(linkTool);
    	pageTool.setTitle("test title");
    	assertEquals(pageTool.getTitle(), "test title");
    }

    public void testAppendTitleSuffix()
    {
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool);
		pageTool.setTitle("test title");
		pageTool.appendTitleSuffix(" suffix");
		assertEquals(pageTool.getTitle(), "test title suffix");
    }

    public void testInsertTitlePrefix()
    {
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool);
		pageTool.setTitle("test title");
		pageTool.insertTitlePrefix("prefix ");
		assertEquals(pageTool.getTitle(), "prefix test title");
    }

    public void testGetTitle()
    {
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool);
		pageTool.setTitle("test title");
		pageTool.insertTitlePrefix("prefix ");
		pageTool.appendTitleSuffix(" suffix");
		assertEquals(pageTool.getTitle(), "prefix test title suffix");
    }

	public void testAddStyleLink()
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool);
		pageTool.addStyleLink("style/style1.css",1);
		pageTool.addStyleLink("style/style2.css",2);
		pageTool.addStyleLink("style/style1.css");
		List links = pageTool.getStyleLinks();
		assertEquals(links.size(), 2);
		Iterator iter = links.iterator();
		PageTool.StyleLink link1 = (PageTool.StyleLink) iter.next(); 
		PageTool.StyleLink link2 = (PageTool.StyleLink) iter.next(); 
		assertEquals(link1.getPriority(), 1);
		assertEquals(link2.getPriority(), 2);
		assertEquals(link1.getHref(), "/test/content/style/style1.css");
		assertEquals(link2.getHref(), "/test/content/style/style2.css");
	}

	public void testAddScriptLink()
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool);
		pageTool.addScriptLink("js/script1.js", "ISO-8859-1");
		pageTool.addScriptLink("js/script2.js");
		pageTool.addScriptLink("js/script1.js");
		List links = pageTool.getScriptLinks();
		assertEquals(links.size(), 2);
		Iterator iter = links.iterator();
		PageTool.ScriptLink link1 = (PageTool.ScriptLink) iter.next(); 
		PageTool.ScriptLink link2 = (PageTool.ScriptLink) iter.next(); 
		assertEquals(link1.getCharset(), "ISO-8859-1");
		assertNull(link2.getCharset());
		assertEquals(link1.getSrc(), "/test/content/js/script1.js");
		assertEquals(link2.getSrc(), "/test/content/js/script2.js");
	}

	public void testAddNameMeta()
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool);
		pageTool.addNameMeta("author", "Damian Gajda");
		pageTool.addNameMeta("author", "Dodo");
		pageTool.addNameMeta("contributor", "ZwieRzem");
		List metas = pageTool.getNameMetas();
		assertEquals(metas.size(), 3);
		Iterator iter = metas.iterator();
		PageTool.Meta meta1 = (PageTool.Meta) iter.next(); 
		PageTool.Meta meta2 = (PageTool.Meta) iter.next(); 
		PageTool.Meta meta3 = (PageTool.Meta) iter.next(); 
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
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool);
		pageTool.addHttpEquivMeta("Header", "Value1");
		pageTool.addHttpEquivMeta("Header", "Value2");
		List metas = pageTool.getHttpEquivMetas();
		assertEquals(metas.size(), 2);
		Iterator iter = metas.iterator();
		PageTool.Meta meta1 = (PageTool.Meta) iter.next(); 
		PageTool.Meta meta2 = (PageTool.Meta) iter.next(); 
		assertEquals(meta1.getHttpEquiv(), "Header");
		assertEquals(meta2.getHttpEquiv(), "Header");
		assertEquals(meta1.getContent(), "Value1");
		assertEquals(meta2.getContent(), "Value2");
		assertNull(meta1.getName());
		assertNull(meta2.getName());
	}

	public void testGetLinkTool()
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool);
		assertEquals(pageTool.getLinkTool(), linkTool);
	}

	public void testReset()
	{
		LinkTool linkTool = (LinkTool)linkToolFactory.getTool();
		PageTool pageTool = new PageTool(linkTool);
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
		List links = pageTool.getStyleLinks();
		assertEquals(links.size(), 0);
		links = pageTool.getScriptLinks();
		assertEquals(links.size(), 0);
		List metas = pageTool.getNameMetas();
		assertEquals(metas.size(), 0);
		metas = pageTool.getHttpEquivMetas();
		assertEquals(metas.size(), 0);
	}
}
