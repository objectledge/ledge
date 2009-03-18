package org.objectledge.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.ElementRemover;
import org.cyberneko.html.filters.Purifier;
import org.cyberneko.html.filters.Writer;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;

/**
 * Implementation of the DocumentService.
 * 
 * @author <a href="mailto:lukasz@caltha.pl">Łukasz Urbański</a>
 * @version $Id:
 */
public class HTMLService
{
    final private Logger log;

    final private Map<String, Configuration> profilesMap = new HashMap<String, Configuration>();

    public HTMLService(Logger logger, Configuration config)
        throws ComponentInitializationError, ConfigurationException
    {
        this.log = logger;

        Configuration[] profilesDefs = config.getChildren("cleanupProfile");

        for (int i = 0; i < profilesDefs.length; i++)
        {
            String name = profilesDefs[i].getAttribute("name");
            profilesMap.put(name, profilesDefs[i]);
        }
    }

    private ElementRemover getElementRemover(String profileName)
    {
        ElementRemover elementRemover = new ElementRemover();
        Configuration[] acceptElements = profilesMap.get(profileName).getChild("acceptElements")
            .getChildren("element");
        Configuration[] removeElements = profilesMap.get(profileName).getChild("removeElements")
            .getChildren("element");

        try
        {
            for (int i = 0; i < acceptElements.length; i++)
            {

                String element = acceptElements[i].getAttribute("name");
                String[] attrs = null;

                Configuration[] attrDefs = acceptElements[i].getChildren("attribute");
                if(attrDefs.length > 0)
                {
                    attrs = new String[attrDefs.length];
                    for (int j = 0; j < attrDefs.length; j++)
                    {
                        attrs[j] = attrDefs[j].getAttribute("name");
                    }
                }
                elementRemover.acceptElement(element, attrs);
            }

            for (int i = 0; i < removeElements.length; i++)
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
    
    public List<String> getProfileNamesList()
    {   
        return new ArrayList<String>(profilesMap.keySet());
    }        
    
    public void parse(String profileName, XMLInputSource source, Writer writer)
    {   
        if(profilesMap.containsKey(profileName))
        {

            Purifier purifier = new Purifier();
            ElementRemover remover = getElementRemover(profileName);
            XMLParserConfiguration parser = new HTMLConfiguration();
            XMLDocumentFilter[] filters = { purifier, remover, writer, };
            
            try
            {
                parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment",true);  
                parser.setProperty("http://cyberneko.org/html/properties/names/elems","upper");    
                parser.setProperty("http://cyberneko.org/html/properties/names/attrs","lower");
                parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
                parser.parse(source);
            }
            catch(XNIException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

        }
        else{
            throw new IllegalArgumentException("Invalid profile name");
        }
    }
}
