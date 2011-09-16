
package org.objectledge.pico.xml;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectledge.pico.AliasComponentAdapter;
import org.objectledge.pico.SequenceParameter;
import org.objectledge.pico.StringParameter;
import org.objectledge.pico.customization.CustomizingComponentParameter;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler for the composition markup.
 */
class CompositionContentHandler extends DefaultHandler
{
    private static final Class<?>[] PICOCONTAINER_ARGS = new Class[] { ComponentAdapterFactory.class,
        PicoContainer.class };
    private static final Class<? extends MutablePicoContainer> DEFAULT_CONTAINER_IMPL = DefaultPicoContainer.class;
    private static final Parameter[] NO_PARAMETERS = new Parameter[] { };
    
    private final PicoContainer topLevelParentContainer;
    private final ClassLoader classLoader;
    
    private PicoContainer result;
    
    private final LinkedList<State> stateStack = 
        new LinkedList<State>();
    private final LinkedList<MutablePicoContainer> containerStack =
        new LinkedList<MutablePicoContainer>();
    private final LinkedList<ComponentAdapterFactory> factoryStack = 
        new LinkedList<ComponentAdapterFactory>();
    private final LinkedList<ComponentInfo> componentStack = 
        new LinkedList<ComponentInfo>();
    private final LinkedList<SequenceInfo> sequenceStack = 
        new LinkedList<SequenceInfo>();
    private Locator locator;
    
    /**
     * Creates new LedgeCompositionContentHandler instance.
     * 
     * @param parentContainer the top level parent container.
     * @param assemblyScope assembly scope.
     */
    public CompositionContentHandler(final PicoContainer parentContainer,
        final ClassLoader classLoader, final Object assemblyScope)
    {
        this.topLevelParentContainer = parentContainer;
        this.classLoader = classLoader;
        
        stateStack.add(State.TOP);
        ComponentAdapterFactory factory = (ComponentAdapterFactory)parentContainer.
            getComponentInstance(ComponentAdapterFactory.class);
        if(factory == null)
        {
            factory = new DefaultComponentAdapterFactory();
        }
        factoryStack.add(factory);
    }

    /**
     * Return the composed container.
     * 
     * @return the composed container.
     */
    public PicoContainer getResult()
    {
        return result;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException
    {
        State current = stateStack.getLast();
        State target = State.fromTag(localName);
        if(target == null)
        {
            throw new SAXParseException("unknown tag " + localName, locator);
        }
        if(State.allowedTransition(current, target))
        {
            target.start(this, attributes);
            stateStack.add(target);
        }
        else
        {
            throw new SAXParseException("unexpected opening tag " + target.getTag() + " inside " + 
                current.getTag(), locator);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void endElement(String uri, String localName, String qName)
        throws SAXException
    {
        State current = stateStack.removeLast();
        if(State.fromTag(localName).equals(current))
        {
            current.end(this);
        }
        else
        {
            throw new SAXParseException("expecting closing tag " + current.getTag(), locator);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentLocator(final Locator locator) 
    {
        this.locator = locator;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    
    void startContainer(Attributes attributes)
        throws SAXException
    {
        if(result != null)
        {
            throw new SAXParseException("top level container already defined", locator);
        }
        MutablePicoContainer container;
        if(containerStack.isEmpty())
        {
            container = makeContainer(topLevelParentContainer, factoryStack.getLast(), attributes);
        }
        else
        {
            container = makeContainer(containerStack.getLast(), factoryStack.getLast(), attributes);
            containerStack.getLast().addChildContainer(container);
            Object key = makeKey(attributes, false);
            if(key != null)
            {
                containerStack.getLast().registerComponentInstance(key, container);
            }
        }
        containerStack.add(container);
    }

    void endContainer()
    {
        PicoContainer container = containerStack.removeLast();
        if(containerStack.isEmpty())
        {
            result = container;
        }
    }

    void startFactory(Attributes attributes) throws SAXException
    {
        Object key = makeKey(attributes, false);
        Object factory = containerStack.getLast().getComponentInstance(key);
        if(factory == null)
        {
            throw new SAXParseException("component "+key+" not found", locator);
        }
        else if(!ComponentAdapterFactory.class.isAssignableFrom(factory.getClass()))
        {
            throw new SAXParseException(factory.getClass() + " does not implement " + 
                ComponentAdapterFactory.class, locator);
        }
        factoryStack.add((ComponentAdapterFactory)factory);
    }

    void endFactory()
    {
        factoryStack.removeLast();
    }

    void startComponent(Attributes attributes)
        throws SAXException
    {
        Object key = makeKey(attributes, true);
        Class<?> implClass = loadClass(attributes, null);
        if(implClass == null)
        {
            throw new SAXParseException("missing class attribute", locator);
        }
        componentStack.add(new ComponentInfo(key, implClass));
    }

    void endComponent()
        throws SAXException
    {
        ComponentInfo info = componentStack.removeLast();
        ComponentAdapter adapter = info.makeAdapter(factoryStack.getLast());
        if(ComponentAdapter.class.isAssignableFrom(adapter.getComponentImplementation()))
        {
            adapter = (ComponentAdapter)adapter.getComponentInstance(containerStack.getLast());
        }
        containerStack.getLast().registerComponent(adapter);
        int i = stateStack.size() - 1;
        while(true)
        {
            switch(stateStack.get(i))
            {
            case FACTORY:
                i--;
                continue;
            case CONTAINER:
                break;
            case COMPONENT:
                componentStack.getLast().addParameter(makeComponentParameter(info.getKey()));
                break;
            case SEQUENCE:
                sequenceStack.getLast().addParameter(makeComponentParameter(info.getKey()));
                break;
            default:
                throw new SAXParseException("unexpected tag parent tag " + 
                    stateStack.get(i).getTag(), locator);
            }
            return;
        }
    }
    
    void startParameter(Attributes attributes)
        throws SAXException
    {
        Parameter parameter;
        String value = attributes.getValue("value");
        if(value != null)
        {
            Class<?> valueClass = loadClass(attributes, null);
            parameter = new StringParameter(value, valueClass);
        }
        else
        {
            Object key = makeKey(attributes, false);
            parameter = makeComponentParameter(key);
        }
        switch(stateStack.getLast())
        {
        case COMPONENT:
            componentStack.getLast().addParameter(parameter);
            break;
        case SEQUENCE:
            sequenceStack.getLast().addParameter(parameter);
            break;
        default:
            throw new SAXParseException("unexpected parent tag "+stateStack.getLast().getTag(), 
                locator);
        }
    }
    
    void endParameter()
    {
        // nothing to do
    }
    
    void startSequence(Attributes attributes)
        throws SAXException
    {
        SequenceInfo info = new SequenceInfo(loadClass(attributes, null));
        sequenceStack.add(info);
    }
    
    void endSequence()
        throws SAXException
    {
        SequenceInfo info = sequenceStack.removeLast();
        Parameter sequence = info.makeSequence();
        switch(stateStack.getLast())
        {
        case COMPONENT:
            componentStack.getLast().addParameter(sequence);
            break;
        case SEQUENCE:
            sequenceStack.getLast().addParameter(sequence);
            break;
        default:
            throw new SAXParseException("unexpected parent tag "+stateStack.getLast().getTag(), 
                locator);
        }
    }
    
    void startAlias(Attributes attributes)
        throws SAXException
    {
        Object key = makeKey(attributes, false);
        if(key == null)
        {
            throw new SAXParseException("class-key or key expected", locator);
        }
        Object ref = makeRef(attributes);
        ComponentAdapter adapter = containerStack.getLast().getComponentAdapter(ref);
        if(adapter == null)
        {
            throw new SAXParseException("missing component " + ref, locator);
        }
        ComponentAdapter alias = new AliasComponentAdapter(key, adapter);
        containerStack.getLast().registerComponent(alias);
    }
    
    void endAlias()
    {
        // nothing to do
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instantiate a container.
     * 
     * @param parent parent container.
     * @param attributes element attributes.
     * @return a MutablePicoContainer.
     * @throws SAXException if the input data is incorrect.
     */
    private MutablePicoContainer makeContainer(PicoContainer parent, 
        ComponentAdapterFactory factory, Attributes attributes)
        throws SAXException
    {
        Class<? extends MutablePicoContainer> implClass = loadClass(attributes,
            DEFAULT_CONTAINER_IMPL);
        Constructor<? extends MutablePicoContainer> ctor;
        try
        {
            ctor = implClass.getConstructor(PICOCONTAINER_ARGS);
        }
        catch(NoSuchMethodException e)
        {
            throw new SAXParseException("constructor " + implClass.getName()
                + ".<init>(PicoContainer) not found", locator, e);
        }
        try
        {
            return ctor.newInstance(factory, parent);
        }
        catch(Exception e)
        {
            throw new SAXParseException("constructor invocation failed", locator, e);
        }
    }
    
    /**
     * Prepare a key based on element attributes.
     * 
     * @param attributes element attributes.
     * @param useClass true to use "class" attribute if "class-key" and "key" undefined.
     * @return component key.
     * @throws SAXException
     */
    private Object makeKey(Attributes attributes, boolean useClass)
        throws SAXException
    {
        String classKey = attributes.getValue("class-key");
        if(classKey != null)
        {
            return loadClass(classKey);
        }
        String key = attributes.getValue("key");
        if(key != null)
        {
            return key;
        }
        if(attributes.getValue("anon") != null)
        {
            return new AnonymousKey();
        }
        String implicitClassKey = attributes.getValue("class");
        if(useClass && implicitClassKey != null)
        {
            return loadClass(implicitClassKey);
        }
        return null;
    }
    
    private Object makeRef(Attributes attributes)
        throws SAXException
    {
        String classRef = attributes.getValue("class-ref");
        if(classRef != null)
        {
            return loadClass(classRef);
        }
        String ref = attributes.getValue("ref");
        if(ref != null)
        {
            return ref;
        }
        throw new SAXParseException("class-ref or ref attribute expected", locator);
    }
    
    /**
     * Prepare a component parameter.
     * 
     * @param key the key or null for wildcard component parameter.
     * @return a Parameter designating the component with the given key.
     */
    private Parameter makeComponentParameter(Object key)
    {
        if(key != null)
        {
            return new CustomizingComponentParameter(key);
        }
        else
        {
            return new CustomizingComponentParameter();
        }
    }
    
    /**
     * Load a class.
     * 
     * @param attributes element attributes.
     * @param defaultClass class name to return if "class" attribute is not defined.
     * @return the class.
     * @throws SAXException if the class could not be loaded.
     */  
    @SuppressWarnings("unchecked")
    private <T> Class<T> loadClass(Attributes attributes, Class<T> defaultClass)
        throws SAXException
    {
        String className = attributes.getValue("class");
        if(className == null)
        {
            return defaultClass;
        }
        return (Class<T>)loadClass(className);
    }
    
    private static final Map<String,String> PRIMITIVE_TO_BOXED = new HashMap<String,String>();
    static 
    {
        PRIMITIVE_TO_BOXED.put("int", Integer.class.getName());
        PRIMITIVE_TO_BOXED.put("byte", Byte.class.getName());
        PRIMITIVE_TO_BOXED.put("short", Short.class.getName());
        PRIMITIVE_TO_BOXED.put("long", Long.class.getName());
        PRIMITIVE_TO_BOXED.put("float", Float.class.getName());
        PRIMITIVE_TO_BOXED.put("double", Double.class.getName());
        PRIMITIVE_TO_BOXED.put("boolean", Boolean.class.getName());
    }

    private static String getClassName(String primitiveOrClass) 
    {
        String fromMap = PRIMITIVE_TO_BOXED.get(primitiveOrClass);
        return fromMap != null ? fromMap : primitiveOrClass;
    }
    
    /**
     * Load a class.
     * 
     * @param className class name.
     * @return the class.
     * @throws SAXException if the class could not be loaded.
     */   
    private Class<?> loadClass(String className)
        throws SAXException
    {
        try
        {
            return classLoader.loadClass(getClassName(className));
        }
        catch(ClassNotFoundException e)
        {
            throw new SAXParseException("could not load class " + className, locator, e);
        }
    }
        
    /**
     * Simple data object for constructed comonents.
     */
    private static class ComponentInfo
    {
        private final Object key;
        private final Class<?> implClass;
        private List<Parameter> parameters;
        
        public ComponentInfo(final Object key, final Class<?> implClass)
        {
            this.key = key;
            this.implClass = implClass;
        }
        
        public ComponentAdapter makeAdapter(ComponentAdapterFactory factory)
        {
            Parameter[] array = null;
            if(parameters != null)
            {
                array = parameters.toArray(NO_PARAMETERS);
            }
            return factory.createComponentAdapter(key, implClass, array);
        }

        public Object getKey()
        {
            return key;
        }
        
        public void addParameter(Parameter parameter)
        {
            if(parameters == null)
            {
                parameters = new ArrayList<Parameter>();
            }
            parameters.add(parameter);
        }
    }
    
    /**
     * Simple data object for constructed sequences
     */
    private static class SequenceInfo
    {
        private final List<Parameter> parameters = new ArrayList<Parameter>();
        private final Class<?> implClass;
        
        public SequenceInfo(final Class<?> implClass)
        {
            this.implClass = implClass;
        }
        
        public void addParameter(Parameter parameter)
        {
            parameters.add(parameter);
        }
        
        public Parameter makeSequence()
        {
            return new SequenceParameter(parameters.toArray(NO_PARAMETERS), implClass);
        }
    }
    
    /**
     * A key for stitching anonymous components to the component paremeters that define them.
     *
     * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
     * @version $Id: CompositionContentHandler.java,v 1.2 2005-02-08 19:11:23 rafal Exp $
     */
    private static class AnonymousKey
    {
        private static volatile int counter;
        
        private final String id;
        
        public AnonymousKey()
        {
            id = "anon #"+(counter++);
        }
        
        public String toString()
        {
            return id;  
        }
    }
    
    private static enum State
    {
        TOP(null)
        {
            public void start(CompositionContentHandler handler, Attributes attributes)
            {
                
            }
            
            public void end(CompositionContentHandler handler)
            {
                
            }
        },
        
        CONTAINER("container")
        {
            public void start(CompositionContentHandler handler, Attributes attributes)
                throws SAXException
            {
                handler.startContainer(attributes);
            }
            
            public void end(CompositionContentHandler handler)
                throws SAXException
            {
                handler.endContainer();
            }
        },
        
        FACTORY("factory")
        {
            public void start(CompositionContentHandler handler, Attributes attributes)
                throws SAXException
            {
                handler.startFactory(attributes);
            }
            
            public void end(CompositionContentHandler handler)
                throws SAXException
            {
                handler.endFactory();
            }
        },
        
        COMPONENT("component")
        {
            public void start(CompositionContentHandler handler, Attributes attributes)
                throws SAXException
            {
                handler.startComponent(attributes);
            }
            
            public void end(CompositionContentHandler handler)
                throws SAXException
            {
                handler.endComponent();
            }
        },
        
        PARAMETER("parameter")
        {
            public void start(CompositionContentHandler handler, Attributes attributes)
                throws SAXException
            {
                handler.startParameter(attributes);
            }
            
            public void end(CompositionContentHandler handler)
                throws SAXException
            {
                handler.endParameter();
            }
        },
        
        SEQUENCE("sequence")
        {
            public void start(CompositionContentHandler handler, Attributes attributes)
                throws SAXException
            {
                handler.startSequence(attributes);
            }
            
            public void end(CompositionContentHandler handler)
                throws SAXException
            {
                handler.endSequence();
            }
        },
        
        ALIAS("alias")
        {
            public void start(CompositionContentHandler handler, Attributes attributes)
                throws SAXException
            {
                handler.startAlias(attributes);
            }
            
            public void end(CompositionContentHandler handler)
                throws SAXException
            {
                handler.endAlias();
            }
        };
        
        /** The tag associated with this state. */
        private final String tag;

        /**
         * Private State constructor.
         * 
         * @param tag the associated tag.
         */
        State(final String tag)
        {
            this.tag = tag;
        }
        
        /**
         * Returns the associated tag.
         * 
         * @return the associated tag.
         */
        public String getTag()
        {
            return tag;
        }
        
        public static State fromTag(String tag)
        {
            return TAG_TO_STATE.get(tag);
        }
        
        /**
         * Check if given transition is allowed.
         * 
         * @param from the source state.
         * @param to the destination state.
         * @return true if transition is allowed.
         */
        public static boolean allowedTransition(State from, 
            State to)
        {
            return TRANSITIONS.get(from).contains(to);
        }
        
        private static final Map<String,State> TAG_TO_STATE = 
            new HashMap<String,State>();
        private static final Map<State,Set<State>> 
            TRANSITIONS = 
                new HashMap<State,Set<State>>();
        
        static
        {
            for(State s : EnumSet.allOf(State.class))
            {
                TAG_TO_STATE.put(s.tag, s);
            }
            
            TRANSITIONS.put(TOP, EnumSet.of(CONTAINER));
            TRANSITIONS.put(CONTAINER, EnumSet.of(CONTAINER, FACTORY, COMPONENT, ALIAS));
            TRANSITIONS.put(FACTORY, EnumSet.of(CONTAINER, FACTORY, COMPONENT, ALIAS));
            TRANSITIONS.put(COMPONENT, EnumSet.of(PARAMETER, SEQUENCE, COMPONENT));
            TRANSITIONS.put(SEQUENCE, EnumSet.of(PARAMETER, SEQUENCE, COMPONENT));
            TRANSITIONS.put(PARAMETER, EnumSet.noneOf(State.class));
        }
        
        /**
         * Start processing of the element.
         * 
         * @param handler the CompositionContentHandler.
         * @param attibutes the element attributes.
         * @throws SAXException if the processing fails.
         */
        public abstract void start(CompositionContentHandler handler, Attributes attibutes)
            throws SAXException;
        
        /**
         * End processing of the element.
         * 
         * @param handler the CompositionContentHandler.
         * @throws SAXException if the processing fails.
         */
        public abstract void end(CompositionContentHandler handler)
            throws SAXException;
    }    
}