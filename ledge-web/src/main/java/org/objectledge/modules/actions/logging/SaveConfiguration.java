package org.objectledge.modules.actions.logging;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.xml.XMLLayout;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.logging.LedgeFileAppender;
import org.objectledge.logging.LedgeRollingFileAppender;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.mvc.builders.PolicyProtectedAction;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * Saves log4j configuration to a file, so that changes are persistent across restarts.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafał Krzewski</a>
 */
public class SaveConfiguration
    extends PolicyProtectedAction
{
    private static final String CONFIG_PATH = 
        "/config/org.objectledge.logging.LoggingConfigurator.xml";
    
    private final FileSystem fileSystem;

    /**
     * Creates new SaveConfiguration action instance.
     * 
     * @param policySystemArg PolicySystem component.
     * @param fileSystemArg FileSystem component.
     */
    public SaveConfiguration(PolicySystem policySystemArg, FileSystem fileSystemArg)
    {
        super(policySystemArg);
        this.fileSystem = fileSystemArg;
    }

    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        try
        {
            emitConfig(new PrintWriter(fileSystem.getWriter(CONFIG_PATH, "UTF-8")));
        }
        catch(Exception e)
        {
            throw new ProcessingException("saving configuration failed", e);
        }
    }

    // ------------------------------------------------------------------------------------------
    
    /**
     * Emit log4j configuration to a file.
     * 
     * @param pw print writer to write to.
     */
    public void emitConfig(PrintWriter pw)
    {
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pw.println("<log4j:configuration xmlns:log4j=\"http://jakarta.apache.org/log4j/\">");
        List<Logger> loggers = getLoggers();
        Logger rootLogger = LogManager.getRootLogger();
        List<Logger> loggersWithRoot = new ArrayList<Logger>(loggers);
        loggersWithRoot.add(rootLogger);
        List<Appender> appenders = getAppenders(loggersWithRoot);
        for(Appender a : appenders)
        {
            emitAppender(a, pw);
        }
        for(Logger l : loggers)
        {
            if(l.getEffectiveLevel() != l.getParent().getEffectiveLevel() || 
                            l.getAllAppenders().hasMoreElements())
            {
                emitLogger(l, false, pw);
            }
        }
        emitLogger(rootLogger, true, pw);
        pw.println("</log4j:configuration>");
        pw.flush();
    }

    private void emitAppender(Appender a, PrintWriter pw)
    {
        pw.println("  <appender name=\""+a.getName()+"\" class=\""+a.getClass().getName()+"\">");
        AppenderEmitter.valueOf(a.getClass()).emitAppender(a, pw);
        if(a.getLayout() != null)
        {
            emitLayout(a.getLayout(), pw);
        }
        pw.println("  </appender>");
    }
    
    @SuppressWarnings("unchecked")
    private void emitLogger(Logger logger, boolean root, PrintWriter pw)
    {
        if(root)
        {
            pw.println("  <root>");
        }
        else
        {
            pw.println("  <logger name=\""+logger.getName()+"\">");
        }
        if(logger.getParent() == null
            || !logger.getParent().getEffectiveLevel().equals(logger.getEffectiveLevel()))
        {
            pw.println("    <level value=\""+logger.getEffectiveLevel().toString()+"\"/>");
        }
        Enumeration<Appender> appenders = logger.getAllAppenders();
        while(appenders.hasMoreElements())
        {
            Appender appender = appenders.nextElement();
            pw.println("    <appender-ref ref=\""+ appender.getName()+ "\"/>");
        }
        if(root)
        {
            pw.println("  </root>");
        }
        else
        {
            pw.println("  </logger>");
        }
    }

    private void emitLayout(Layout l, PrintWriter pw)
    {
        pw.println("    <layout class=\""+l.getClass().getName()+"\">");
        LayoutEmitter.valueOf(l.getClass()).emitLayout(l, pw);
        pw.println("    </layout>");
    }
    
    private static void emitParam(String name, String value, PrintWriter pw)
    {
        pw.println("<param name=\""+name+"\" value=\""+value+"\"/>");
    }    
    
    // ------------------------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    private List<Logger> getLoggers()
    {
        Enumeration<Logger> loggerEnumeration = LogManager.getCurrentLoggers();
        List<Logger> loggerList = new ArrayList<Logger>();
        while(loggerEnumeration.hasMoreElements())
        {
            loggerList.add(loggerEnumeration.nextElement());
        }
        Collections.sort(loggerList, new Comparator<Logger>()
            {
            public int compare(Logger l1, Logger l2)
            {
                return l1.getName().compareTo(l2.getName());
            }
        });
        return loggerList;
    }
    
    @SuppressWarnings("unchecked")
    private List<Appender> getAppenders(List<Logger> loggers)
    {
        List<Appender> appenderList = new ArrayList<Appender>();
        for(Logger l : loggers)
        {
            Enumeration<Appender> allAppenders = l.getAllAppenders();
            while(allAppenders.hasMoreElements())
            {
                appenderList.add(allAppenders.nextElement());
            }
        }
        Collections.sort(appenderList, new Comparator<Appender>()
            {
            public int compare(Appender a1, Appender a2)
            {
                return a1.getName().compareTo(a2.getName());
            }
        });
        return appenderList;
    }

    // ------------------------------------------------------------------------------------------

    private enum AppenderEmitter
    {
        File(LedgeFileAppender.class)
        {
            public void emitAppender(Appender appender, PrintWriter pw)
            {
                LedgeFileAppender a = (LedgeFileAppender)appender;
                emitParam("File", a.getFile(), pw);
                if(!a.getAppend())
                {
                    emitParam("Append", "false", pw);
                }
                if(a.getBufferedIO())
                {
                    emitParam("BufferedIO", "true", pw);
                }
                if(a.getBufferSize() != 8*1024)
                {
                    emitParam("BufferSize", Integer.toString(a.getBufferSize()), pw);
                }
            }
        },
        
        RollingFile(LedgeRollingFileAppender.class)        
        {
            public void emitAppender(Appender appender, PrintWriter pw)
            {
                LedgeRollingFileAppender a = (LedgeRollingFileAppender)appender;
                emitParam("File", a.getFile(), pw);
                if(!a.getAppend())
                {
                    emitParam("Append", "false", pw);
                }
                if(a.getBufferedIO())
                {
                    emitParam("BufferedIO", "true", pw);
                }
                if(a.getBufferSize() != 8*1024)
                {
                    emitParam("BufferSize", Integer.toString(a.getBufferSize()), pw);
                }
                if(a.getMaxBackupIndex() != 1)
                {
                    emitParam("MaxBackupIndex", Integer.toString(a.getMaxBackupIndex()), pw);
                    
                }
                if(a.getMaximumFileSize() != 10*1024*1024)
                {
                    emitParam("MaximumFileSize", Long.toString(a.getMaximumFileSize()), pw);
                }
            }
        },

        Console(ConsoleAppender.class)        
        {
            public void emitAppender(Appender appender, PrintWriter pw)
            {
                ConsoleAppender a = (ConsoleAppender)appender;
                if(a.getTarget().equals("System.err"))
                {
                    emitParam("Target", "System.err", pw);
                }
            }
        },

        SMTP(SMTPAppender.class)        
        {
            public void emitAppender(Appender appender, PrintWriter pw)
            {
                SMTPAppender a = (SMTPAppender)appender;
                if(a.getSMTPHost().length() > 0)
                {
                    emitParam("SMPTHost", a.getSMTPHost(), pw);
                }
                emitParam("From", a.getFrom(), pw);
                emitParam("To", a.getTo(), pw);
                emitParam("Subject", a.getSubject(), pw);
                if(a.getBufferSize() != 512)
                {
                    emitParam("BufferSize", Integer.toString(a.getBufferSize()), pw);
                }
                if(a.getLocationInfo())
                {
                    emitParam("LocationInfo", "true", pw);
                }
            }
        };
        
        public abstract void emitAppender(Appender appender, PrintWriter pw);
        
        private final Class<? extends Appender> appenderClass;
        
        private AppenderEmitter(final Class<? extends Appender> appenderClass)
        {
            this.appenderClass = appenderClass;
        }
        
        public static AppenderEmitter valueOf(Class<? extends Appender> cl)
        {
            for(AppenderEmitter ae : EnumSet.allOf(AppenderEmitter.class))
            {
                if(ae.appenderClass.equals(cl))
                {
                    return ae;
                }
            }
            throw new IllegalArgumentException("unsupported Appender implementation "+cl.getName());
        }
        
        private static void emitParam(String name, String value, PrintWriter pw)
        {
            pw.print("    ");
            SaveConfiguration.emitParam(name, value, pw);
        }
    }
    
    private enum LayoutEmitter
    {
        Simple(SimpleLayout.class)
        {
            public void emitLayout(Layout layout, PrintWriter pw)
            {
                // no configurable properties
            }
        },
        
        TTCC(TTCCLayout.class)
        {
            public void emitLayout(Layout layout, PrintWriter pw)
            {
                TTCCLayout l = (TTCCLayout)layout;
                if(!l.getThreadPrinting())
                {
                    emitParam("ThreadPrinting", "false", pw);
                }
                if(!l.getCategoryPrefixing())
                {
                    emitParam("CategoryPrefixing", "false", pw);
                }
                if(!l.getContextPrinting())
                {
                    emitParam("ContextPrinting", "false", pw);
                }
            }
        },
        
        Pattern(PatternLayout.class)
        {
            public void emitLayout(Layout layout, PrintWriter pw)
            {
                PatternLayout l = (PatternLayout)layout;
                emitParam("ConversionPattern", l.getConversionPattern(), pw);
            }
        },
        
        XML(XMLLayout.class)
        {
            public void emitLayout(Layout layout, PrintWriter pw)
            {
                XMLLayout l = (XMLLayout)layout;
                if(l.getLocationInfo())
                {
                    emitParam("LocationInfo", "true", pw);
                }
            }
        };
        
        public abstract void emitLayout(Layout layout, PrintWriter pw);
        
        private final Class<? extends Layout> layoutClass;
        
        private LayoutEmitter(final Class<? extends Layout> layoutClass)
        {
            this.layoutClass = layoutClass;
        }
        
        public static LayoutEmitter valueOf(Class<? extends Layout> cl)
        {
            for(LayoutEmitter le : EnumSet.allOf(LayoutEmitter.class))
            {
                if(le.layoutClass.equals(cl))
                {
                    return le;
                }
            }
            throw new IllegalArgumentException("unsupported Layout implementation "+cl.getName());
        }
        
        private static void emitParam(String name, String value, PrintWriter pw)
        {
            pw.print("      ");
            SaveConfiguration.emitParam(name, value, pw);
        }
    }
}
