package org.objectledge.modules.views.longops;

import org.codehaus.jackson.JsonNode;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.longops.LongRunningOperationRegistry;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.json.AbstractJsonView;

public class ActiveOperations
    extends AbstractJsonView
{
    private final LongRunningOperationRegistry registry;

    public ActiveOperations(LongRunningOperationRegistry registry, Context context, Logger log)
    {
        super(context, log);
        this.registry = registry;
    }

    @Override
    protected JsonNode buildJsonTree()
        throws ProcessingException
    {
        return objectMapper.valueToTree(registry.getActiveOperations());
    }
}
