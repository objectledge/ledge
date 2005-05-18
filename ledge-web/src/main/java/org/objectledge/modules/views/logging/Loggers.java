// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//	 this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//	 this list of conditions and the following disclaimer in the documentation  
//	 and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//	 nor the names of its contributors may be used to endorse or promote products  
//	 derived from this software without specific prior written permission. 
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
package org.objectledge.modules.views.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.PathTreeTableModel;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.PolicyProtectedBuilder;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * A view that displays loggers active in the system.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Loggers.java,v 1.1 2005-05-18 05:33:29 rafal Exp $
 */
public class Loggers
    extends PolicyProtectedBuilder
{
    private static final Comparator LOGGER_NAME_COMPARATOR = new Comparator()
    {
        public int compare(Object o1, Object o2)
        {
            Logger l1 = (Logger)o1;
            Logger l2 = (Logger)o2;
            return l1.getName().compareTo(l2.getName());
        }
    };

    private static final Comparator LOGGER_DESCRIPTOR_COMPARATOR = new Comparator()
    {
        public int compare(Object o1, Object o2)
        {
            LoggerDescriptor l1 = (LoggerDescriptor)o1;
            LoggerDescriptor l2 = (LoggerDescriptor)o2;
            return l1.getName().compareTo(l2.getName());
        }
    };
    
    private final TableStateManager tableStateManager; 
    
    /**
     * Creates new Loggers instance.
     * 
     * @param context the Context component.
     * @param policySystem PolicySystem component
     * @param tableStateManager the TableStateManager component.
     */
    public Loggers(Context context, PolicySystem policySystem, TableStateManager tableStateManager)
    {
        super(context, policySystem);
        this.tableStateManager = tableStateManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        List<Logger> loggerList = getLoggers();
        try
        {
            TableModel model = getLoggerHierarchyModel(loggerList, LogManager.getRootLogger());
            TableState state = tableStateManager.getState(context, getClass().getName());
            if(state.isNew())
            {
                state.setTreeView(true);
                state.setSortColumnName("name");
            }
            TableTool tableTool = new TableTool(state, null, model);
            templatingContext.put("table", tableTool);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to build logger hierarchy model", e);
        }
        templatingContext.put("loggerList", loggerList);
        return super.build(template, embeddedBuildResults);
    }

    private List<Logger> getLoggers()
    {
        Enumeration<Logger> loggerEnumeration = LogManager.getCurrentLoggers();
        List<Logger> loggerList = new ArrayList<Logger>();
        while(loggerEnumeration.hasMoreElements())
        {
            loggerList.add(loggerEnumeration.nextElement());
        }
        return loggerList;
    }
    
    private TableModel getLoggerHierarchyModel(List<Logger> loggerList, Logger rootLogger)
        throws TableException
    {
        TableColumn[] columns = new TableColumn[1];
        columns[0] = new TableColumn("name", LOGGER_DESCRIPTOR_COMPARATOR);
        PathTreeTableModel model = new PathTreeTableModel(columns);
        model.bind("/", new LoggerDescriptor("root", "root", rootLogger.getEffectiveLevel()
            .toString()));
        Collections.sort(loggerList, LOGGER_NAME_COMPARATOR);
        for(Logger logger : loggerList)
        {
            String[] tokens = logger.getName().split("\\.");
            bindLogger(model, tokens, logger.getEffectiveLevel().toString());
        }
        return model;
    }

    private void bindLogger(PathTreeTableModel model, String[] tokens, String level)
    {
        for(int i = 1; i <= tokens.length; i++)
        {
            String path = "/" + getName(tokens, i, '/');
            String id = getName(tokens, i, '.');
            if(i == 0)
            {
                continue;
            }
            else if(i < tokens.length)
            {
                if(model.getObjectByPath(path) == null)
                {
                    model.bind(path, new LoggerDescriptor(tokens[i-1], id, null));
                }
            }
            else
            {
                model.bind(path, new LoggerDescriptor(tokens[i-1], id, level));
            }
        }
    }

    private String getName(String[] tokens, int i, char separator)
    {
        StringBuilder buff = new StringBuilder();
        for(int j = 0; j < i; j++)
        {
            buff.append(tokens[j]);
            if(j < i - 1)
            {
                buff.append(separator);
            }
        }
        return buff.toString();
    }
    
    /**
     * Simple descriptor of Log4j logger for model use. 
     */
    public class LoggerDescriptor
    {
        private final String name;
        private final String level;
        private final String id;

        /**
         * Creates new LoggerDescriptor instance.
         * 
         * @param name name of the logger.
         * @param id the logger identifier.
         * @param level verbosity level (null for stubs).
         */
        public LoggerDescriptor(final String name, final String id, final String level)
        {
            this.name = name;
            this.id = id;
            this.level = level;
        }

        /**
         * Returns the level.
         *
         * @return the level.
         */
        public String getLevel()
        {
            return level;
        }

        /**
         * Returns the name.
         *
         * @return the name.
         */
        public String getName()
        {
            return name;
        }

        /**
         * Returns the id.
         *
         * @return the id.
         */
        public String getId()
        {
            return id;
        }
    }
}
