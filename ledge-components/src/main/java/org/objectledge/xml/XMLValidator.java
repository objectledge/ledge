/*
 * Copyright (c) 2003 Caltha Sp.J., All rights reserved
 * 
 * Created on Dec 2, 2003
 */
package org.objectledge.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.objectledge.filesystem.FileSystem;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.validate.rng.RngProperty;
import com.thaiopensource.xml.sax.DraconianErrorHandler;
import com.thaiopensource.xml.sax.Jaxp11XMLReaderCreator;

/**
 *
 *
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Id: XMLValidator.java,v 1.1 2003-12-02 13:08:56 fil Exp $
 */
public class XMLValidator
{
    private FileSystem fileSystem;
    
    private SchemaReader schemaReader;
    
    private SAXParser saxParser;
    
    private PropertyMap properties;

    private Map schemas = new HashMap();
    
    private Map validators = new HashMap();

    /** Pathname of the relaxng schema.*/
    public static final String RELAXNG_SCHEMA = "org/objectledge/xml/relaxng.rng";

    /**
     * Creates a new instance of the validator.
     * 
     * @param fileSystem the filesystem to read files from.
     * @throws ParserConfigurationException if the JAXP parser factory is misconfigured.
     * @throws SAXException if the JAXP parser factory is misconfigured.
     */
    public XMLValidator(FileSystem fileSystem) 
        throws ParserConfigurationException, SAXException
    {
        this.fileSystem = fileSystem;
        schemaReader = new AutoSchemaReader();
        PropertyMapBuilder propertyMapBuilder = new PropertyMapBuilder();
        ErrorHandler eh = new DraconianErrorHandler();
        ValidateProperty.ERROR_HANDLER.put(propertyMapBuilder, eh);
        ValidateProperty.XML_READER_CREATOR.put(propertyMapBuilder, new Jaxp11XMLReaderCreator());
        RngProperty.CHECK_ID_IDREF.add(propertyMapBuilder);
        properties = propertyMapBuilder.toPropertyMap();
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        saxParser = parserFactory.newSAXParser();
    }
    
    /**
     * Validates given XML file using specified schema.
     * 
     * @param path the path of the file to be validated.
     * @param schemaPath the path of the schema to be used.
     * @throws IOException if any of the files cannot be read.
     * @throws SAXException if any of the files are malformed.
     * @throws IncorrectSchemaException if the schema is invalid.
     */
    public void validate(String path, String schemaPath)
        throws IOException, SAXException, IncorrectSchemaException
    {
        Validator validator = null;
        try
        {
            validator = getValidator(schemaPath);
            XMLReader reader = saxParser.getXMLReader();
            InputStream is = fileSystem.getInputStream(path);
            if(is == null)
            {
                throw new IOException(path+" does not exist");
            }
            InputSource source = new InputSource(is);
            source.setSystemId(path);
            reader.setContentHandler(validator.getContentHandler());
            reader.setDTDHandler(validator.getDTDHandler());
            reader.parse(source);
             
        }
        finally
        {
            if(validator != null)
            {
                releaseValidator(validator, schemaPath);
            }
        }
    }
    
    /**
     * Returns a thread-exclusive validator instance.
     * 
     * @param schemaPath the path of the schema to be used.
     * @return a thread-exclusive validator instance.
     * @throws IOException if the schema does not exist.
     * @throws SAXException if the schema is malformed.
     * @throws IncorrectSchemaException if the schema is invalid.
     */
    public synchronized Validator getValidator(String schemaPath)
        throws IOException, SAXException, IncorrectSchemaException
    {
        Schema schema;
        if(validators.containsKey(schemaPath))
        {
            List list = (List)validators.get(schemaPath);
            if(!list.isEmpty())
            {
                return (Validator)list.remove(0);
            }
            else
            {
                schema = (Schema)schemas.get(schemaPath);
            }
        }
        else
        {
            InputStream is = fileSystem.getInputStream(schemaPath);
            if(is == null)
            {
                throw new IOException(schemaPath+" does not exist");
            }
            schema = schemaReader.createSchema(new InputSource(is), properties);
            schemas.put(schemaPath, schema);
            validators.put(schemaPath, new ArrayList());
        }
        return schema.createValidator(properties);        
    }
    
    /**
     * Returns a validator to the pool.
     * 
     * @param validator the validator instance.
     * @param schemaPath the path of the assoicated schema.
     */
    public synchronized void releaseValidator(Validator validator, String schemaPath)
    {
        List list = (List)validators.get(schemaPath);
        list.add(validator);
    }
}
