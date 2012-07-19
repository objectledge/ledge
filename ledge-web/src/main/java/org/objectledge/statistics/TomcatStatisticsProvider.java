package org.objectledge.statistics;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

public class TomcatStatisticsProvider
    implements StatisticsProvider
{
    private final MuninGraph[] graphs;

    private static final String[] STAGES = { "new", "parse", "prepare", "service", "endInput",
                    "endOutput", "keepAlive", "ended" };

    public TomcatStatisticsProvider(FileSystem fileSystem, Logger log)
    {
        graphs = new MuninGraph[] { new RequestStages(fileSystem, log) };
    }

    @Override
    public MuninGraph[] getGraphs()
    {
        return graphs;
    }

    private static class RequestStages
        extends AbstractMuninGraph
    {
        private final Logger log;

        public RequestStages(FileSystem fs, Logger log)
        {
            super(fs);
            this.log = log;
        }

        @Override
        public String getId()
        {
            return "requestStages";
        }

        @Override
        public Map<String, Number> getValues()
        {
            Map<String, Number> values = new HashMap<String, Number>();

            int[] counters = new int[STAGES.length];

            try
            {
                MBeanServerConnection mbs = ManagementFactory.getPlatformMBeanServer();
                Set<ObjectInstance> reqProcs = mbs.queryMBeans(
                    ObjectName.getInstance("Catalina:type=RequestProcessor,*"), null);
                for(ObjectInstance reqProc : reqProcs)
                {
                    int stage = (Integer)mbs.getAttribute(reqProc.getObjectName(), "stage");
                    if(stage < STAGES.length)
                    {
                        counters[stage]++;
                    }
                }
            }
            catch(Exception e)
            {
                log.error("failed to retrieve data from tomcat, e");
            }

            for(int i = 0; i < STAGES.length; i++)
            {
                values.put(STAGES[i], Integer.valueOf(counters[i]));
            }

            return values;
        }
    }
}
