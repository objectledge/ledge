package org.objectledge.modules.rest.upload;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DeleteMessage
{
    private final Map<String, Boolean> files = new HashMap<>();

    public DeleteMessage(String name)
    {
        files.put(name, true);
    }

    public DeleteMessage(Set<String> names)
    {
        for(String name : names)
        {
            files.put(name, true);
        }
    }

    public Map<String, Boolean> getFiles()
    {
        return files;
    }
}
