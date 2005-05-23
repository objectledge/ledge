package org.objectledge.modules.actions.logging;

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
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.mvc.builders.PolicyProtectedAction;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * An action that creates an Appender with a Layout and attaches it to a Logger. 
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafał Krzewski</a>
 */
public class CreateAppender
    extends PolicyProtectedAction
{
    private final FileSystem fileSystem;
    
    /**
     * Create a new CreateAppender action instance.
     * 
     * @param policySystemArg the PolicySystem component.
     * @param fileSystem the FileSystem component.
     */
    public CreateAppender(PolicySystem policySystemArg, FileSystem fileSystem)
    {
        super(policySystemArg);
        this.fileSystem = fileSystem;
    }

    /**
     * {@inheritDoc}
     */
    public void process(Context context)
        throws ProcessingException
    {
        RequestParameters parameters = RequestParameters.getRequestParameters(context);

        Appender appender = AppenderBuilder.valueOf(parameters.get("appender")).
            getAppender(parameters, fileSystem);
        Layout layout = LayoutBuilder.valueOf(parameters.get("layout")).
            getLayout(parameters);
        appender.setLayout(layout);

        String id = parameters.get("id");
        Logger logger;
        if(id.equals("root"))
        {
            logger = LogManager.getRootLogger();
        }
        else
        {
            if(LogManager.exists(id) == null)
            {
                throw new ProcessingException("invalid logger id "+id);
            }
            logger = LogManager.getLogger(id);
        }
        logger.addAppender(appender);
    }
    
    private enum LayoutBuilder 
    {
        Pattern
        {
            public Layout getLayout(Parameters parameters)
            {
                PatternLayout l = new PatternLayout();
                l.setConversionPattern(parameters.get("Pattern_conversionPattern"));
                l.activateOptions();
                return l;
            }
        },
        
        TTCC
        {
            public Layout getLayout(Parameters parameters)
            {
                TTCCLayout l = new TTCCLayout();
                l.activateOptions();
                return l;
            }
        },
        
        Simple
        {
            public Layout getLayout(Parameters parameters)
            {
                SimpleLayout l = new SimpleLayout();
                l.activateOptions();
                return l;
            }            
        },
        
        XML
        {
            public Layout getLayout(Parameters parameters)
            {
                XMLLayout l = new XMLLayout();
                l.setLocationInfo(parameters.getBoolean("XML_locationInfo", false));
                l.activateOptions();
                return l;
            }            
        };
     
        public abstract Layout getLayout(Parameters parameters);

    }

    private enum AppenderBuilder
    {
        File
        {
            public Appender getAppender(Parameters parameters, FileSystem fileSystem)
            {
                LedgeFileAppender a = new LedgeFileAppender(fileSystem);
                a.setFile(parameters.get("File_file"));
                a.setAppend(parameters.getBoolean("File_append", false));
                a.setBufferedIO(parameters.getBoolean("File_bufferedIO", false));
                a.setBufferSize(parameters.getInt("File_bufferSize"));
                a.setName(parameters.get("name"));
                a.activateOptions();
                return a;
            }
        },
        
        RollingFile
        {
            public Appender getAppender(Parameters parameters, FileSystem fileSystem)
            {
                LedgeRollingFileAppender a = new LedgeRollingFileAppender(fileSystem);
                a.setFile(parameters.get("RollingFile_file"));
                a.setAppend(parameters.getBoolean("RollingFile_append", false));
                a.setBufferedIO(parameters.getBoolean("RollingFile_bufferedIO", false));
                a.setBufferSize(parameters.getInt("RollingFile_bufferSize"));
                a.setMaxBackupIndex(parameters.getInt("RollingFile_maxBackupIndex"));
                a.setMaxFileSize(parameters.get("RollingFile_maxFileSize"));
                a.setName(parameters.get("name"));
                a.activateOptions();
                return a;
            }            
        },
        
        Console
        {
            public Appender getAppender(Parameters parameters, FileSystem fileSystem)
            {
                ConsoleAppender a = new ConsoleAppender();
                a.setTarget(parameters.get("Console_target"));
                a.setName(parameters.get("name"));
                a.activateOptions();
                return a;
            }            
        },
        
        SMTP
        {
            public Appender getAppender(Parameters parameters, FileSystem fileSystem)
            {
                SMTPAppender a = new SMTPAppender();
                a.setBufferSize(parameters.getInt("SMTP_bufferSize"));
                if(parameters.isDefined("STMP_evaluatorClass"))
                {
                    a.setEvaluatorClass(parameters.get("STMP_evaluatorClass"));
                }
                a.setLocationInfo(parameters.getBoolean("SMTP_locationInfo", false));
                a.setSMTPHost(parameters.get("SMTP_smtpHost"));
                a.setSubject(parameters.get("SMTP_subject"));
                a.setFrom(parameters.get("SMTP_from"));
                a.setTo(parameters.get("SMTP_to"));
                a.setName(parameters.get("name"));
                a.activateOptions();
                return a;
            }                        
        };
        
        public abstract Appender getAppender(Parameters parameters, FileSystem fileSystem);
    }
}
