package org.objectledge.upload;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.threads.Task;

public class UploadBucketCleaner
    extends Task
{
    private final BlockingQueue<UploadBucket> queue = new LinkedBlockingQueue<>();

    private final FileSystem fileSystem;

    private final String workAreaPath;

    private final Logger logger;

    public UploadBucketCleaner(String workAreaPath, FileSystem fileSystem, Logger logger)
    {
        this.fileSystem = fileSystem;
        this.workAreaPath = workAreaPath;
        this.logger = logger;
    }

    public void schedule(UploadBucket bucket)
    {
        queue.add(bucket);
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        while(!Thread.interrupted())
        {
            try
            {
                cleanUp(queue.take());
            }
            catch(InterruptedException e)
            {
                return;
            }
        }
    }

    private void cleanUp(UploadBucket bucket)
    {
        try
        {
            String path = workAreaPath + "/" + bucket.getId();
            if(fileSystem.exists(path))
            {
                fileSystem.deleteRecursive(path);
            }
        }
        catch(IOException e)
        {
            logger.error("Unable to clean up upload bucket " + bucket.getId(), e);
        }
    }
}
