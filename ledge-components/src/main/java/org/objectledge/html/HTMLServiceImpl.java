package org.objectledge.html;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.DefaultFilter;
import org.cyberneko.html.filters.ElementRemover;
import org.cyberneko.html.filters.Purifier;
import org.dom4j.Branch;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.VisitorSupport;
import org.dom4j.io.OutputFormat;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;

/**
 * Implementation of the DocumentService.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HTMLServiceImpl.java,v 1.8 2005-12-30 11:46:03 rafal Exp $
 */
public class HTMLServiceImpl
    implements HTMLService
{
    /**
     * Configured HTML cleanup profiles.
     */
    final private Map<String, Configuration> cleanupProfiles = new HashMap<String, Configuration>();
    
    private final Set<List<String>> ignoredErrors = new HashSet<List<String>>();
  

    /**
     * Creates an instance of HTMLService
     * 
     * @param config service configuration.
     * @throws ConfigurationException if the service configuration is malformed.
     */
    public HTMLServiceImpl(Configuration config)
        throws ConfigurationException
    {
        Configuration[] profilesDefs = config.getChildren("cleanupProfile");

        for(Configuration profileDef : profilesDefs)
        {
            String name = profileDef.getAttribute("name");
            cleanupProfiles.put(name, profileDef);
        }
        
        for(Configuration ignoreConf : config.getChildren("ignoreErrors"))
        {
            for(Configuration domainConf : ignoreConf.getChildren("domain"))
            {
                for(Configuration errorConf : domainConf.getChildren("error"))
                {
                    ignoredErrors.add(asList(domainConf.getAttribute("name"), errorConf.getAttribute("key")));
                }
            }
            for(Configuration errorConf : ignoreConf.getChildren("error"))
            {
                ignoredErrors.add(asList(errorConf.getAttribute("domain"), errorConf.getAttribute("key")));
            }
        }
    }

    /**
     * Creates an instance of NekoHTML ElementRemover filter according to the specified cleanup
     * profile.
     * <p>
     * ElementRemover has the ability eliminate all tags except a predefined set from the document,
     * leaving the text contained with them verbatim (this is useful for striping text from
     * &lt;font&gt; tags and the like) or remove certain tags completely including their content
     * (notably &lt;script&gt; and &lt;style&gt tags). Also the attributes on tags that are accepted
     * are limited to a predefined set (allow &lt;a href="..."&gt; but disallow &lt;a
     * target="..."&gt;).
     * </p>
     * 
     * @param profileName cleanup profile name.
     * @return configured ElementRemover filter.
     */
    private ElementRemover getElementRemover(String profileName)
    {
        ElementRemover elementRemover = new ElementRemover();
        Configuration[] acceptElements = cleanupProfiles.get(profileName)
            .getChild("acceptElements").getChildren("element");
        Configuration[] removeElements = cleanupProfiles.get(profileName)
            .getChild("removeElements").getChildren("element");

        try
        {
            for(int i = 0; i < acceptElements.length; i++)
            {

                String element = acceptElements[i].getAttribute("name");
                String[] attrs = null;

                Configuration[] attrDefs = acceptElements[i].getChildren("attribute");
                if(attrDefs.length > 0)
                {
                    attrs = new String[attrDefs.length];
                    for(int j = 0; j < attrDefs.length; j++)
                    {
                        attrs[j] = attrDefs[j].getAttribute("name");
                    }
                }
                elementRemover.acceptElement(element, attrs);
            }

            for(int i = 0; i < removeElements.length; i++)
            {
                String element = removeElements[i].getAttribute("name");
                elementRemover.removeElement(element);
            }

            return elementRemover;

        }
        catch(ConfigurationException e)
        {
            throw new IllegalArgumentException("Invalid configuration", e);
        }
    }
    
    private XMLDocumentFilter getElementReplacer(String profileName)
    {
        if(cleanupProfiles.get(profileName).getChild("replaceElements", false) != null)
        {
            try
            {
                Configuration[] replaceElements = cleanupProfiles.get(profileName).getChild(
                "replaceElements").getChildren("element");
                Map<String, String> nameMap = new HashMap<String, String>();
                for(Configuration replaceElement : replaceElements)
                {
                    nameMap.put(replaceElement.getAttribute("from"), replaceElement.getAttribute("to"));
                }
                return new ElementReplacer(nameMap);
            }
            catch(ConfigurationException e)
            {
                throw new IllegalArgumentException("Invalid configuration", e);
            }
        }
        else
        {
            return null;
        }
    }

    private Set<Cleanup> getCleanups(String profileName)
    {
        Set<Cleanup> cleanups = EnumSet.noneOf(Cleanup.class);
        if(cleanupProfiles.get(profileName).getChild("additionalCleanups", false) != null)
        {
            try
            {
                Configuration[] cleanupElements = cleanupProfiles.get(profileName)
                    .getChild("additionalCleanups").getChildren("cleanup");
                for(Configuration cleanupElement : cleanupElements)
                {
                    cleanups.add(Cleanup.valueOf(cleanupElement.getValue()));
                }
            }
            catch(ConfigurationException e)
            {
                throw new IllegalArgumentException("Invalid configuration", e);
            }
        }
        return cleanups;
    }

    /**
     * Return the configured profile names.
     * 
     * @return
     */
    public Set<String> getCleanupProfileNames()
    {
        return unmodifiableSet(cleanupProfiles.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.dom4j.Document emptyDom4j()
    {
        DocumentFactory factory = DocumentFactory.getInstance();
        org.dom4j.Document document = factory.createDocument();
        Element html = document.addElement("HTML");
        html.addElement("HEAD").addElement("TITLE");
        html.addElement("BODY");
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document textToDom4j(String html)
        throws HTMLException
    {
        try
        {
            XMLParserConfiguration parser = new HTMLConfiguration();
            Dom4jDocumentBuilder dom4jBuilder = new Dom4jDocumentBuilder();
            XMLDocumentFilter[] filters = { new Purifier(), dom4jBuilder };
            parser.setProperty("http://cyberneko.org/html/properties/filters", filters);

            XMLInputSource source = new XMLInputSource("", "", "", new StringReader(html), "UTF-8");
            parser.parse(source);

            Document doc = dom4jBuilder.getDocument();
            if(doc.getRootElement().content().isEmpty())
            {
                doc = emptyDom4j();
            }
            mergeAdjecentTextNodes(doc);
            return doc;
        }
        catch(Exception e)
        {
            throw new HTMLException("failed to parse HTML document", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document textToDom4j(String html, Writer errorWriter, String cleanupProfile)
        throws HTMLException
    {
        try
        {
            XMLParserConfiguration parser = new HTMLConfiguration();
            List<XMLDocumentFilter> filters = new ArrayList<XMLDocumentFilter>(2);
            filters.add(new Purifier());
            if(cleanupProfile != null)
            {
                XMLDocumentFilter replacer = getElementReplacer(cleanupProfile);
                if(replacer != null)
                {
                    filters.add(replacer);
                }
                filters.add(getElementRemover(cleanupProfile));
            }
            Dom4jDocumentBuilder dom4jBuilder = new Dom4jDocumentBuilder();
            filters.add(dom4jBuilder);
            parser.setProperty("http://cyberneko.org/html/properties/filters", filters
                .toArray(new XMLDocumentFilter[filters.size()]));
            parser.setFeature("http://cyberneko.org/html/features/report-errors", true);
            ValidationErrorCollector errorCollector = new ValidationErrorCollector(errorWriter, ignoredErrors);
            parser.setErrorHandler(errorCollector);

            XMLInputSource source = new XMLInputSource("", "", "", new StringReader(html), "UTF-8");
            parser.parse(source);

            if(!errorCollector.errorDetected())
            {
                Document doc = dom4jBuilder.getDocument();
                if(doc.getRootElement().content().isEmpty())
                {
                    doc = emptyDom4j();
                }
                mergeAdjecentTextNodes(doc);
                if(cleanupProfile != null)
                {
                    applyCleanups(doc, getCleanups(cleanupProfile));
                }
                return doc;
            }
            else
            {
                return null;
            }
        }
        catch(Exception e)
        {
            throw new HTMLException("failed to parse HTML document", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void dom4jToText(org.dom4j.Document dom4jDoc, Writer writer, boolean bodyContentOnly)
        throws HTMLException
    {
        OutputFormat format = new OutputFormat();
        format.setXHTML(true);
        format.setExpandEmptyElements(true);
        format.setTrimText(false);
        format.setIndent(false);
        HTMLWriter htmlWriter = new HTMLWriter(writer, format);
        try
        {
            if(!bodyContentOnly)
            {
                htmlWriter.write(dom4jDoc);
            }
            else
            {
                if(!"HTML".equals(dom4jDoc.getRootElement().getName()) || dom4jDoc.getRootElement().element("BODY") == null)
                {
                    throw new HTMLException("invalid document: missing HTML and/or BODY tags");
                }
                for(Node node : (List<Node>)dom4jDoc.getRootElement().element("BODY").content())
                {
                    if(node instanceof Element)
                    {
                        htmlWriter.write((Element)node);
                    }
                    if(node instanceof Text)
                    {
                        writer.append(node.getText());
                    }
                    if(node instanceof Comment)
                    {
                        writer.append("<!--").append(node.getText()).append("-->");
                    }
                }
            }
            writer.flush();
        }
        catch(IOException e)
        {
            throw new HTMLException("Could not serialize the document", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String collectText(Document html)
    {
        HTMLTextCollectorVisitor collector = new HTMLTextCollectorVisitor();
        html.accept(collector);
        return collector.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEmptyParas(Document html)
    {
        List<Element> emptyParas = new ArrayList<Element>();
        outer: for(Element para : (List<Element>)html.selectNodes("//P"))
        {
        	for(Text node : (List<Text>)para.selectNodes("descendant::text()"))
        	{
        		if(!isWhitespace(node.getText()))
        		{
        			continue outer;
        		}
        	} 
        	emptyParas.add(para);
        }        
        
        for(Element para : emptyParas)
        {
            Branch parent = para.getParent();
            Node followingSibling = parent.node(parent.indexOf(para) + 1);
            if(followingSibling != null && followingSibling instanceof Text
                && isWhitespace(followingSibling.getText()))
            {
                followingSibling.detach();
            }
            para.detach();
        }
    }
    
    public void trimBreaksFromParas(Document html)
    {
    	for(Element para : (List<Element>)html.selectNodes("//P"))
    	{
    		List<Element> brs = para.selectNodes("BR");
    		if(brs.size() > 0)
    		{
    			Element leading = brs.get(0);
    			boolean retain = false;
    			for(int i = 0; i < para.indexOf(leading); i++)
    			{
    				if(!isWhitespace(para.node(i).getText()))
    				{
    					retain = true;
    				}
    			}
    			if(!retain)
    			{
    				leading.detach();
    			}
    			Element trailing = brs.get(brs.size()-1);
    			retain = false;
    			{
    				for(int i = para.indexOf(trailing)+1; i < brs.size(); i++)
    				{
    					if(!isWhitespace(para.node(i).getText()))
        				{
        					retain = true;
        				}
    				}    		
    			}
    			if(!retain)
    			{
    				trailing.detach();
    			}
    		}
    	}
    }
    
    public void collapseSubsequentBreaksInParas(Document html)
    {        
        for(Element para : (List<Element>)html.selectNodes("//P"))
        {
            List<List<Node>> contentSequences = new ArrayList<List<Node>>();
            int numBreaks = 0;
            int contentStart = 0;
            int contentEnd = 0;
            boolean leadIn = true;
            for(int i = 0; i < para.nodeCount(); i++)
            {
                if(para.node(i) instanceof Element && para.node(i).getName().equals("BR"))
                {
                    numBreaks++;
                }
                else if(para.node(i) instanceof Text && isWhitespace(para.node(i).getText()))
                {
                    // blank
                }
                else
                {
                    if(leadIn)
                    {
                        contentStart = i;
                        contentEnd = i+1;
                        leadIn = false;
                    }
                    if(numBreaks > 1)
                    {
                        List<Node> contentSequence = new ArrayList<Node>(para.content().subList(contentStart, contentEnd));
                        contentSequences.add(contentSequence);
                        contentStart = i;
                    }
                    contentEnd = i+1;
                    numBreaks = 0;
                }                
            }
            if(contentStart < para.nodeCount())
            {
                List<Node> contentSequence = new ArrayList<Node>(para.content().subList(contentStart, contentEnd));
                contentSequences.add(contentSequence);
            }
            List<Node> parentContent = para.getParent().content();
            int paraPosition = parentContent.indexOf(para);
            parentContent.remove(paraPosition);
            for(List<Node> contentSequence : contentSequences)
            {
                Element paraCopy = para.createCopy();
                paraCopy.clearContent();
                for(Node contentNode : contentSequence)
                {
                    contentNode.detach();
                    paraCopy.add(contentNode);
                }
                parentContent.add(paraPosition++, paraCopy);
            }
        }
    }

    private boolean isWhitespace(String text)
    {
        // U+00AO = &nbsp;
        return text.replace('\u00A0', ' ').trim().length() == 0;
    }

    public void mergeAdjecentTextNodes(Document doc)
    {
        doc.accept(new VisitorSupport()
            {
                @Override
                public void visit(Element element)
                {
                    Node cur = null;
                    StringBuilder buff = new StringBuilder();
                    List<Node> newContent = new ArrayList<Node>(element.content().size());
                    for(Node node : (List<Node>)element.content())
                    {
                        if(node instanceof Text)
                        {
                            if(cur == null)
                            {
                                cur = node;
                                buff.setLength(0);
                                buff.append(node.getText());
                            }
                            else
                            {
                                buff.append(node.getText());
                            }
                        }
                        else
                        {
                            if(cur != null)
                            {
                                newContent.add(new DefaultText(buff.toString()));
                                cur = null;
                            }
                            newContent.add((Node)node.clone());
                        }
                    }
                    if(cur != null)
                    {
                        newContent.add(new DefaultText(buff.toString()));
                    }
                    element.setContent(newContent);
                }
            });
    }

    public void bulletParasToLists(Document doc)
    {
        doc.accept(new VisitorSupport()
            {
                @Override
                public void visit(Element element)
                {
                    Element ul = null;
                    List<Node> newContent = new ArrayList<Node>(element.content().size());
                    for(Node node : (List<Node>)element.content())
                    {
                        if(isBullet(node))
                        {
                            if(ul == null)
                            {
                                ul = new DefaultElement("UL");
                                newContent.add(ul);
                            }
                            ul.add(toListItem(node));
                        }
                        else if(ul != null && node instanceof Text)
                        {
                            ul.add((Text)(node.clone()));
                        }
                        else
                        {
                            ul = null;
                            newContent.add((Node)node.clone());
                        }
                    }
                    element.setContent(newContent);
                }

                /**
                 * Check if the node is a P element, having Text node as a first child, with content
                 * beginning with U+00B7 (middot) character.
                 * 
                 * @param node node to be checked.
                 * @return if node is a paragraph beginning with a bullet.
                 */
                private boolean isBullet(Node node)
                {
                    return node instanceof Element
                        && node.getName().equals("P")
                        && node.selectObject("child::node()[1]") instanceof Text
                        && ((String)node.selectObject("string(child::text()[1])"))
                            .matches("^[\\r\\n\\s\u00A0]*[\u00B7].*");
                }

                /**
                 * Converts bulleted paragraph into list item. Bullet and following whitespace is
                 * stripped from the first Text child node.
                 * 
                 * @param node node to be converted.
                 * @return LI element.
                 */
                private Node toListItem(Node node)
                {
                    Node firstText = (Node)node.selectSingleNode("child::text()[1]");
                    String text = firstText.getText();
                    text = text.replaceAll("^[\\r\\n\\s\u00A0]*[\u00B7][\\s\u00A0]*", "");
                    Element listItem = (Element)(node.clone());
                    listItem.setName("LI");
                    listItem.content().set(0, new DefaultText(text));
                    return listItem;
                }
            });
    }

    public void collapseWhitespace(Document html)
    {
        List<Node> textNodes = (List<Node>)html.selectNodes("//text()");
        for(Node textNode : textNodes)
        {
            String text = textNode.getText();
            text = text.replaceAll("[ \u00A0\t]+", " ");
            textNode.setText(text);
        }
    }

    @Override
    public void applyCleanups(Document doc, Set<Cleanup> cleanupMethods)
    {
        for(Cleanup cleanup : cleanupMethods)
        {
            switch(cleanup)
            {
            case REMOVE_EMPTY_PARAS:
                removeEmptyParas(doc);
                break;
            case TRIM_BREAKS_FROM_PARAS:
                trimBreaksFromParas(doc);
                break;
            case COLLAPSE_SUBSEQUENT_BREAKS_IN_PARAS:
                collapseSubsequentBreaksInParas(doc);
                break;
            case COLLAPSE_WHITESPACE:
                collapseWhitespace(doc);
                break;
            case BULLET_PARAS_TO_LISTS:
                bulletParasToLists(doc);
                break;
            }
        }
    }

    // helper classes

    private static final class ValidationErrorCollector
        implements XMLErrorHandler
    {
        final Set<List<String>> ignored;
  
        private final Writer errorWriter;

        private boolean errorDetected = false;

        private ValidationErrorCollector(Writer errorWriter, Set<List<String>> ignored)
        {
            this.errorWriter = errorWriter;
            this.ignored = ignored;
        }

        @Override
        public void fatalError(String domain, String key, XMLParseException exception)
            throws XNIException
        {
            throw exception;
        }

        @Override
        public void error(String domain, String key, XMLParseException exception)
            throws XNIException
        {
            if(!ignored.contains(asList(domain, key)))
            {
                errorDetected = true;
                report("error", domain, key, exception);
            }
        }

        @Override
        public void warning(String domain, String key, XMLParseException exception)
            throws XNIException
        {
            if(!ignored.contains(asList(domain, key)))
            {
                report("warning", domain, key, exception);
            }                
        }

        public boolean errorDetected()
        {
            return errorDetected;
        }

        private void report(String severity, String domain, String key, XMLParseException exception)
            throws XNIException
        {
            try
            {
                errorWriter.append(severity).append(" ");
                errorWriter.append(domain).append(" ").append(key).append(" at ");
                if(exception.getExpandedSystemId().length() > 0)
                {
                    errorWriter.append(exception.getExpandedSystemId()).append(" ");
                }
                errorWriter.append("line ").append(Integer.toString(exception.getLineNumber()));
                errorWriter.append(" column ")
                    .append(Integer.toString(exception.getColumnNumber())).append(": ");
                errorWriter.append(exception.getMessage()).append("\n");
                errorWriter.flush();
            }
            catch(IOException e)
            {
                throw new XNIException(e);
            }
        }
    }
    
    private static final class ElementReplacer extends DefaultFilter
    {
        private final Map<String, String> nameMap;

        public ElementReplacer(Map<String,String> nameMap)
        {
            this.nameMap = nameMap;            
        }
        
        @Override
        public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
            throws XNIException
        {
            super.startElement(getQName(element), attributes, augs);
        }

        @Override
        public void endElement(QName element, Augmentations augs)
            throws XNIException
        {
            super.endElement(getQName(element), augs);
        }

        private QName getQName(QName oldQName)
        {
            String oldName = oldQName.localpart;
            String newName = nameMap.get(oldName);
            if(newName != null)
            {
                return new QName(oldQName.prefix, newName, oldQName.rawname.replace(oldName, newName), oldQName.uri);
            }
            else
            {
                return oldQName;
            }
        }
    }
}
