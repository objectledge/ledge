package org.objectledge.modules.components.system;

import java.util.jar.Manifest;

import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.FileSystemProvider;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.components.AbstractComponent;

public class WebappManifest
    extends AbstractComponent
{
    private final FileSystem fileSystem;

    public WebappManifest(Context context, FileSystem fileSystem)
    {
        super(context);
        this.fileSystem = fileSystem;

    }

    @Override
    public void process(TemplatingContext templatingContext)
        throws ProcessingException
    {
        try
        {
            FileSystemProvider provider = fileSystem.getProvider("servlet");
            if(provider.exists("META-INF/MANIFEST.MF"))
            {
                Manifest manifest = new Manifest(provider.getInputStream("META-INF/MANIFEST.MF"));
                templatingContext.put("manifest", manifest);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }

}
