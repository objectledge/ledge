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

/**
 * A context tool for applications using JavaScript and CSS files..
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PageTool.java,v 1.10 2005-03-24 14:28:33 zwierzem Exp $
 */
public class PageTool
{
    //------------------------------------------------------------------------
    // Page data fields

    /** Represents contents of title header tag. */
    private StringBuilder title = new StringBuilder(100);

    /** Contains {@link StyleLink} objects. */
    protected ArrayList styleLinks = new ArrayList();
    
    /** Contains {@link StyleLink} objects' signatures, it is used to prevent multiple additions of
     * the same style link. */
    protected Set styleLinksSet = new HashSet();

    /** Contains {@link ScriptLink} objects. */
    protected ArrayList scriptLinks = new ArrayList();
    
    /** Contains {@link ScriptLink} objects' signatures, it is used to prevent multiple additions of
     * the same script link. */
    protected Set scriptLinksSet = new HashSet();

    /** Contains {@link Meta} objects. */
    protected ArrayList nameMetas = new ArrayList();
    
    /** Contains {@link Meta} objects. */
    protected ArrayList httpEquivMetas = new ArrayList();

    /** Parent of LinkTool used to generate content resource links. */
    protected LinkTool parentLinkTool;
	/** LinkTool used to generate content resource links. */
	protected LinkTool linkTool;

	/** 
	 * Component constructor.
	 * @param parentLinkTool the link tool used to generate links to page content resources.
	 */
	public PageTool(LinkTool parentLinkTool)
	{
		this.parentLinkTool = parentLinkTool;
        this.linkTool = parentLinkTool.sessionless();
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
     * @param titlePrefix a prefix to be inserted befor current title.
     */
    public void insertTitlePrefix(String titlePrefix)
    {
        this.title.insert(0, titlePrefix);
    }

    /**
     * Returns a title tag content.
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
		 * @param href relative URI of a linked content resource 
		 */
        public ContentLink(String href)
        {
            this.href = href;
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
            return linkTool.content(href).toString();
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
        addStyleLink(new ContentLink(href), priority);
    }

    /** 
     * Adds a style link with a given priority.
     * @param resLink a content resource link to be added
     * @param priority priority of a style link
     */
    protected void addStyleLink(ContentLink resLink, int priority)
    {
        if(!styleLinksSet.contains(resLink))
        {
            styleLinksSet.add(resLink);
            styleLinks.add(new StyleLink(resLink, priority));
        }
    }

    /** 
     * Returns a collection of CSS <code>&lt;link&gt;</code> tag definitions,
     * links are sorted according to their priorities.
     * @return a list of {@link StyleLink} objects
     */
    public List getStyleLinks()
    {
        Collections.sort(styleLinks, new Comparator()
	        {
				public int compare(Object o1, Object o2)
				{
					return ((StyleLink)o1).getPriority() - ((StyleLink)o2).getPriority();
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

        StyleLink(ContentLink resLink, int priority)
        {
            this.resLink = resLink;
            this.priority = priority;
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
    }


    //-------------------------------
    // SCRIPT LINKS

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
     * @return a list of script links added to this page tool 
     */
    public List getScriptLinks()
    {
        return scriptLinks;
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
            if(charset == null)
            {
                charset = "UTF-8";
            }
            this.charset = charset;
        }

        /** Getter for <code>src</code> attribute value.
         * @return Value of <code>src</code> attribute.
         */
        public String getSrc()
        {
            return resLink.toString();
        }

        /** Getter for charset attribute.
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
    public List getNameMetas()
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
    public List getHttpEquivMetas()
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
}
