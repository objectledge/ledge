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

package org.objectledge.web.mvc.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectledge.web.HttpContext;

/**
 * A context tool for management of head section of generated webpages.
 * <p>It provides support for:</p>
 * <ul>
 * <li>Inclusion of JavaScript files.</li>
 * <li>Inclusion of CSS files.</li>
 * <li>Control of auto included JavaScript files
 *   (included on a condition that any other JS file has been included).</li> 
 * <li>Meta tags creation.</li>
 * <li>Control of the <code>&lt;title&gt;</code> tag content.</li>
 * </ul>
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PageTool.java,v 1.15 2005-12-10 02:38:56 pablo Exp $
 */
public class PageTool
{
    //------------------------------------------------------------------------
    // Page data fields

    /** Represents contents of title header tag. */
    protected StringBuilder title = new StringBuilder(100);

    /** Contains {@link StyleLink} objects. */
    protected ArrayList<StyleLink> styleLinks = new ArrayList<StyleLink>();
    
    /** Contains {@link StyleLink} objects' signatures, it is used to prevent multiple additions of
     * the same style link. */
    protected Set<ContentLink> styleLinksSet = new HashSet<ContentLink>();

    /** Contains {@link ScriptLink} objects autoloaded on addition of script links. */
    protected ArrayList<ScriptLink> autoLoadScriptLinks = new ArrayList<ScriptLink>();

    /**
     * Contains autoload {@link ScriptLink} objects' signatures, it is used to prevent multiple
     * additions of the same autoload script link.
     */
    protected Set<ContentLink> autoLoadScriptLinksSet = new HashSet<ContentLink>();
    
    /** Contains {@link ScriptLink} objects. */
    protected ArrayList<ScriptLink> scriptLinks = new ArrayList<ScriptLink>();
    
    /** Contains {@link ScriptLink} objects' signatures, it is used to prevent multiple additions of
     * the same script link. */
    protected Set<ContentLink> scriptLinksSet = new HashSet<ContentLink>();

    /** Contains {@link Meta} objects. */
    protected ArrayList<Meta> nameMetas = new ArrayList<Meta>();
    
    /** Contains {@link Meta} objects. */
    protected ArrayList<Meta> httpEquivMetas = new ArrayList<Meta>();

    /** Parent of LinkTool used to generate content resource links. */
    protected LinkTool parentLinkTool;
    
	/** LinkTool used to generate content resource links. */
	protected LinkTool linkTool;

    /** The http context. */
    protected HttpContext httpContext;

	/** 
	 * Component constructor.
	 * @param parentLinkTool the link tool used to generate links to page content resources.
	 * @param httpContext the http context
	 */
	public PageTool(LinkTool parentLinkTool, HttpContext httpContext)
	{
		this.parentLinkTool = parentLinkTool;
        this.linkTool = parentLinkTool.sessionless();
        this.httpContext = httpContext;
    }

	/** 
     * Used to recycle {@link LinkTool}s.
     * 
     * @return the parent link tool of this pagetool.
     */
	public LinkTool getLinkTool()
	{
		return parentLinkTool;
	}

	/** Should be used to reinitialize the page tool. */
    public void reset()
    {
        this.title.setLength(0);

        this.styleLinks.clear();
        this.styleLinksSet.clear();

        this.scriptLinks.clear();
        this.scriptLinksSet.clear();

        this.nameMetas.clear();
        this.httpEquivMetas.clear();
    }

    //------------------------------------------------------------------------
    // Page data access methods

    //-------------------------------
    // TITLE

    /**
     * Sets a title tag content.
     * @param title a new title content.
     */
    public void setTitle(String title)
    {
        this.title.setLength(0);
        this.title.append(title);
    }

    /**
     * Appends a string to the end of title tag content.
     * @param titleSuffix a suffix to be appended to the current title.
     */
    public void appendTitleSuffix(String titleSuffix)
    {
        this.title.append(titleSuffix);
    }

    /**
     * Inserts a string at the begining of title tag content.
     * @param titlePrefix a prefix to be inserted before currently set title.
     */
    public void insertTitlePrefix(String titlePrefix)
    {
        this.title.insert(0, titlePrefix);
    }

    /**
     * Returns a title tag content, used in HTML head section in top level layout view.
     * @return contents of a set title.
     */
    public String getTitle()
    {
        return this.title.toString();
    }

    //-------------------------------
    // LINK BASE CLASS

    /**
     * This class represents basic property of tags which have an URI reference. It conforms to the
     * {@link Object#equals(Object)} and {@link Object#hashCode()} contracts to avoid multiple links
     * to the same content resource.
     */
    public class ContentLink
    {
		/** Relative URI of a linked content resource. */ 
        protected String href;

		/**
		 * Constructs a content resource link. 
		 * 
		 * @param href relative URI of a linked content resource 
		 */
        public ContentLink(String href)
        {
            this.href = linkTool.content(href).toString();
        }
        
        /**
         * Constructs a content resource link.
         * 
         * @param link link tool instance.
         */
        public ContentLink(LinkTool link)
        {
            this.href = link.toString();
        }

		/**
		 * {@inheritDoc}
		 */
		public int hashCode()
		{
			return href.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean equals(Object o)
		{
			return (o instanceof ContentLink) && ((ContentLink)o).href.equals(this.href);
		}
		
		/** Generates a link for this content resource link.
		 * @return generated link string (relative URI).
		 */
        public String toString()
        {
            return href;
        }
    }

    //-------------------------------
    // STYLE LINKS

    /**
     * Adds a style link with a default priority equal to <code>0</code>.
     * @param href a link to the style
     */
    public void addStyleLink(String href)
    {
        addStyleLink(href, 0);
    }

    /**
     * Adds a style link with a given priority.
     * @param href a link to the style
     * @param priority priority of a style link
     */
    public void addStyleLink(String href, int priority)
    {
        addStyleLink(new ContentLink(href), priority, null, null);
    }
    
    /**
     * Adds a style link with a given priority.
     * @param href a link to the style
     * @param priority priority of a style link
     * @param media media of a style link
     */
    public void addStyleLink(String href, int priority, String media)
    {
        addStyleLink(new ContentLink(href), priority, media, null);
    }
    
    /**
     * Adds a style link with a given priority.
     * @param href a link to the style
     * @param priority priority of a style link
     * @param media media of a style link
     * @param rel ...
     */
    public void addStyleLink(String href, int priority, String media, String rel)
    {
        addStyleLink(new ContentLink(href), priority, media, rel);
    }
    
    /**
     * Adds a style link with a default priority equal to <code>0</code>.
     * @param link a link to the style
     */
    public void addStyleLink(LinkTool link)
    {
        addStyleLink(link, 0);
    }

    /**
     * Adds a style link with a given priority.
     * @param link a link to the style
     * @param priority priority of a style link
     */
    public void addStyleLink(LinkTool link, int priority)
    {
        addStyleLink(new ContentLink(link), priority, null, null);
    }
    
    /**
     * Adds a style link with a given priority.
     * @param link a link to the style
     * @param priority priority of a style link
     * @param media media of a style link
     */
    public void addStyleLink(LinkTool link, int priority, String media)
    {
        addStyleLink(new ContentLink(link), priority, media, null);
    }
    
    /**
     * Adds a style link with a given priority.
     * @param link a link to the style
     * @param priority priority of a style link
     * @param media media of a style link
     * @param rel ...
     */
    public void addStyleLink(LinkTool link, int priority, String media, String rel)
    {
        addStyleLink(new ContentLink(link), priority, media, rel);
    }
    
    /** 
     * Adds a style link with a given priority.
     * @param resLink a content resource link to be added
     * @param priority priority of a style link
     */
    protected void addStyleLink(ContentLink resLink, int priority, String media, String rel)
    {
        if(!styleLinksSet.contains(resLink))
        {
            styleLinksSet.add(resLink);
            styleLinks.add(new StyleLink(resLink, priority, media, rel));
        }
    }

    /** 
     * Returns a collection of CSS <code>&lt;link&gt;</code> tag definitions,
     * links are sorted according to their priorities.
     * @return a list of {@link StyleLink} objects
     */
    public List<StyleLink> getStyleLinks()
    {
        Collections.sort(styleLinks, new Comparator<StyleLink>()
	        {
				public int compare(StyleLink l1, StyleLink l2)
				{
					return l1.getPriority() - l2.getPriority();
				}
	        }
        );
        return styleLinks;
    }

    /**
     * This class represents properties of a <code>link</code> tag which is
     * used to include CSS stylesheet files. It is simplified, and it does not
     * have <code>media</code> property (attribute). If You need to use this
     * property, then set it inside Your CSS file - it will make your CSS
     * files more understandable and maintainable.
     */
    public class StyleLink
    {
        private ContentLink resLink;
        private int priority;
        private String media;
        private String rel;

        StyleLink(ContentLink resLink, int priority, String media, String rel)
        {
            this.resLink = resLink;
            this.priority = priority;
            this.media = media;
            this.rel = rel;
        }

        /** Getter for <code>href</code> attribute value.
         * @return Value of <code>href</code> attribute.
         */
        public String getHref()
        {
            return resLink.toString();
        }

        /** Getter for property priority.
         * @return Value of property priority.
         */
        public int getPriority()
        {
            return priority;
        }
        
        public String getMedia()
        {
            return media;
        }
        
        public String getRel()
        {
            if(rel == null || rel.length() == 0) 
            {
                return "stylesheet";
            }
            return rel;
        }
    }


    //-------------------------------
    // SCRIPT LINKS

    /** 
     * Adds an autoload script link, with no charset attribute defined.
     * @param src a link to the script source
     */
    public void addAutoLoadScriptLink(String src)
    {
        this.addAutoLoadScriptLink(src, null);
    }

    /**
     * Adds an autoload script link, with charset attribute defined.
     * @param src a link to the script source
     * @param charset charset of a linked source file
     */
    public void addAutoLoadScriptLink(String src, String charset)
    {
        this.addAutoLoadScriptLink(new ContentLink(src), charset);
    }

    /** 
     * Adds an autoload script link, with no charset attribute defined.
     * @param link a link to the script source
     */
    public void addAutoLoadScriptLink(LinkTool link)
    {
        this.addAutoLoadScriptLink(link, null);
    }

    /**
     * Adds an autoload script link, with charset attribute defined.
     * @param link a link to the script source
     * @param charset charset of a linked source file
     */
    public void addAutoLoadScriptLink(LinkTool link, String charset)
    {
        this.addAutoLoadScriptLink(new ContentLink(link), charset);
    }    
    
    /**
     * Adds an autoload script link with a given type and charset attribute defined.
     * @param srcLink a link to the script source
     * @param charset charset of a linked source file
     */
    protected void addAutoLoadScriptLink(ContentLink srcLink, String charset)
    {
        if(!autoLoadScriptLinksSet.contains(srcLink))
        {
            autoLoadScriptLinksSet.add(srcLink);
            autoLoadScriptLinks.add(new ScriptLink(srcLink, charset));
        }
    }
    
    /** 
     * Adds a script link, with no charset attribute defined.
     * @param src a link to the script source
     */
    public void addScriptLink(String src)
    {
        this.addScriptLink(src, null);
    }

    /**
     * Adds a script link, with charset attribute defined.
     * @param src a link to the script source
     * @param charset charset of a linked source file
     */
    public void addScriptLink(String src, String charset)
    {
        this.addScriptLink(new ContentLink(src), charset);
    }

    /** 
     * Adds a script link, with no charset attribute defined.
     * @param link a link to the script source
     */
    public void addScriptLink(LinkTool link)
    {
        this.addScriptLink(link, null);
    }

    /**
     * Adds a script link, with charset attribute defined.
     * @param link a link to the script source
     * @param charset charset of a linked source file
     */
    public void addScriptLink(LinkTool link, String charset)
    {
        this.addScriptLink(new ContentLink(link), charset);
    }    
    
    /** 
     * Checks if any scripts have been added. This is useful for including template code
     * for scripts configuration.
     * @return <code>true</code> if any scripts have been added.
     */
    public boolean hasScripts()
    {
        return this.scriptLinksSet.size() > 0;
    }

    /**
     * Adds a script link with a given type and charset attribute defined.
     * @param srcLink a link to the script source
     * @param charset charset of a linked source file
     */
    protected void addScriptLink(ContentLink srcLink, String charset)
    {
        if(!scriptLinksSet.contains(srcLink))
        {
            scriptLinksSet.add(srcLink);
            scriptLinks.add(new ScriptLink(srcLink, charset));
        }
    }

    /**
     * Returns a collection of <code>&lt;script&gt;</code> tag definitions,
     * with <b>defined sources</b> (<code>src</code> attribute).
     * @return a list of script links added to this page tool including added autoload scripts 
     */
    public List<ScriptLink> getScriptLinks()
    {
        ArrayList<ScriptLink> returnScriptLinks = new ArrayList<ScriptLink>();
        if(scriptLinks.size() > 0)
        {
            returnScriptLinks.addAll(autoLoadScriptLinks);
            returnScriptLinks.addAll(scriptLinks);
        }
        return returnScriptLinks;
    }

    /**
     * This class represents properties of a <code>script</code> tag with
     * <code>src</code> atribute set. It is used to include JavaScript files
     * in HTML documents. It is simplified, and it does not have
     * <code>defer</code> property. If You need to use deffered script
     * execution (ie. after the page loads) then use <code>onload</code> event
     * handlers - it will make your JavaScript files more understandable and
     * maintainable.
     */
    public class ScriptLink
    {
        private ContentLink resLink;
        private String charset;

        ScriptLink(ContentLink srcLink, String charset)
        {
            this.resLink = srcLink;
            this.charset = charset != null ? charset : "UTF-8";
        }

        /** Getter for <code>src</code> attribute value.
         * @return Value of <code>src</code> attribute.
         */
        public String getSrc()
        {
            return resLink.toString();
        }

        /** Getter for charset attribute, <code>UTF-8</code> by default.
         * @return Value of charset attribute value.
         */
        public String getCharset()
        {
            return charset;
        }
    }

    //-------------------------------
    // META

    /**
     * Adds a meta tag with name attribute.
     * @param name value of <code>name</code> attribute of the meta tag
     * @param content value of <code>content</code> attribute of the meta tag
     */
    public void addNameMeta(String name, String content)
    {
        nameMetas.add(new Meta(name, null, content));
    }

    /**
     * Returns a collection of <code>&lt;meta&gt;</code> tag definitions,
     * with <b>defined names</b> (<code>name</code> attribute).
     * @return a list of <i>name</i> meta tags added to this page tool 
     */
    public List<Meta> getNameMetas()
    {
        return nameMetas;
    }

    /**
     * Adds a meta tag with <code>http-equiv</code> attribute.
     * @param httpEquiv value of <code>http-equiv</code> attribute of the meta tag
     * @param content value of <code>content</code> attribute of the meta tag
     */
    public void addHttpEquivMeta(String httpEquiv, String content)
    {
        httpEquivMetas.add(new Meta(null, httpEquiv, content));
    }

    /**
     * Returns a collection of <code>&lt;meta&gt;</code> tag definitions,
     * with <b>defined http-equivs</b> (<code>http-equiv</code> attribute).
     * @return a list of <i>http-equiv</i> meta tags added to this page tool 
     */
    public List<Meta> getHttpEquivMetas()
    {
        return httpEquivMetas;
    }

	/**
	 * This class represents properties of a <code>meta</code> tag with <code>name</code>
	 * or <code>http-equiv</code> atribute set. It also contains a value of <code>content</code>\
	 * attribute.  
	 */
    public class Meta
    {
        private String name;
        private String httpEquiv;
        private String content;

        Meta(String name, String httpEquiv, String content)
        {
            this.name = name;
            this.httpEquiv = httpEquiv;
            this.content = content;
        }

        /**
         * Getter for <code>name</code> attribute value.
         * @return Value of <code>name</code> attribute.
         */
        public String getName()
        {
            return name;
        }

		/**
		 * Getter for <code>http-equiv</code> attribute value.
		 * @return Value of <code>http-equiv</code> attribute.
		 */
        public String getHttpEquiv()
        {
            return httpEquiv;
        }

		/**
		 * Getter for <code>content</code> attribute value.
		 * @return Value of <code>content</code> attribute.
		 */
        public String getContent()
        {
            return content;
        }
    }

    /**
     * Returns the content type of the current response, useful for generation of HTML content
     * headers. 
     * 
     * @return the content type.
     */
    public String getContentType()
    {
        return httpContext.getContentType();
    }
    
    /**
     * Returns the character encoding of the current reponse, useful for generation of HTML
     * content headers and XML declaration.
     * 
     * @return the encoding.
     */
    public String getEncoding()
    {
        return httpContext.getEncoding();
    }
    
    /**
     * Sets the status code of the HTTP response.
     * 
     * @param status the status code to set.
     */
    public void setStatus(int status)
    {
        httpContext.getResponse().setStatus(status);
    }
}
