package org.objectledge.modules.views.longops;

import static org.objectledge.longops.LongRunningOperationOrdering.sortOperations;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.apache.activemq.jaas.UserPrincipal;
import org.codehaus.jackson.JsonNode;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.longops.LongRunningOperation;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.json.AbstractJsonView;

public class ActiveOperationsDummy
    extends AbstractJsonView
{

    public ActiveOperationsDummy(Context context, Logger log)
    {
        super(context, log);
    }

    @Override
    protected JsonNode buildJsonTree()
        throws ProcessingException
    {
        Collection<LongRunningOperation> activeOperations;

        LongRunningOperation op1 = new LongRunningOperationDummy("1",
            "bazy.organizations.autocat.UpdateAll", "automatic categorization", new UserPrincipal(
                "root"), 20, 4, false, new Date().getTime(), new Date().getTime() + 1500000L);
        LongRunningOperation op2 = new LongRunningOperationDummy("2",
            "bazy.organizations.autocat.UpdateAll", "automatic categorization", new UserPrincipal(
                "root"), 20, 8, false, new Date().getTime(), new Date().getTime() + 1000000L);
        activeOperations = Arrays.asList(op1, op2);
        return objectMapper.valueToTree(sortOperations(activeOperations));
    }

    private static class LongRunningOperationDummy
        implements LongRunningOperation
    {

        private final String identifier;

        private final String code;

        private final String description;

        private final Principal user;

        private volatile int totalUnitsOfWork;

        private volatile int completedUnitsOfWork;

        private volatile boolean canceled;

        private final long startTime;

        private volatile long lastUpdateTime;

        public LongRunningOperationDummy(String identifier, String code, String description,
            Principal user, int totalUnitsOfWork, int completedUnitsOfWork, boolean canceled,
            long startTime, long lastUpdateTime)
        {
            super();
            this.identifier = identifier;
            this.code = code;
            this.description = description;
            this.user = user;
            this.totalUnitsOfWork = totalUnitsOfWork;
            this.completedUnitsOfWork = completedUnitsOfWork;
            this.canceled = canceled;
            this.startTime = startTime;
            this.lastUpdateTime = lastUpdateTime;
        }

        @Override
        public String getIdentifier()
        {
            return identifier;
        }

        @Override
        public String getCode()
        {
            return code;
        }

        @Override
        public String getDescription()
        {
            return description;
        }

        @Override
        public Principal getUser()
        {
            return user;
        }

        @Override
        public int getTotalUnitsOfWork()
        {
            return totalUnitsOfWork;
        }

        @Override
        public int getCompletedUnitsOfWork()
        {
            return completedUnitsOfWork;
        }

        @Override
        public boolean isCanceled()
        {
            return canceled;
        }

        @Override
        public Date getStartTime()
        {
            return new Date(startTime);
        }

        @Override
        public Date getLastUpdateTime()
        {
            return new Date(lastUpdateTime);
        }

        @Override
        public Date getEstimatedEndTime()
        {
            return new Date(startTime + 3600000L);
        }

    }
}
