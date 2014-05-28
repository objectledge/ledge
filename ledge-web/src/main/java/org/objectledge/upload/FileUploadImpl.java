// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

package org.objectledge.upload;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.threads.ThreadPool;
import org.objectledge.web.HttpContext;
import org.picocontainer.Startable;

/**
 * An application access point to the HTML form file upload functionality. For more information see
 * {@link org.objectledge.upload.FileUploadValve}.
 * 
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: FileUpload.java,v 1.8 2005-05-30 09:10:13 pablo Exp $
 */
public class FileUploadImpl
    implements Startable, FileUpload
{
    // constants ////////////////////////////////////////////////////////////////////////////////

    /** context key to store the upload map. */
    public static final String UPLOAD_CONTEXT_KEY = "org.objectledge.upload.FileUpload.uploadMap";

    /** HTTP session key to the upload bucket holder. */
    private static final String BUCKET_HOLDER_SESSION_KEY = "org.objectledge.upload.FileUpload.bucketHolder";

    /** the default upload limit. */
    public static final int DEFAULT_UPLOAD_LIMIT = 4194304;

    /** Default temporary directory for upload buckets, relative to Ledge filesystem root. */
    public static final String DEFAULT_WORKING_DIRECTORY = "data/upload";

    // instance variables ///////////////////////////////////////////////////////////////////////

    /** the thread's processing context. */
    private Context context;

    /** the upload size limit */
    private int uploadLimit;

    private String workingDirectory;

    private Map<String, UploadBucket> allBuckets = new ConcurrentHashMap<>();

    private Random bucketIdGen = new SecureRandom();

    private UploadBucketCleaner bucketCleaner;

    private final FileSystem fileSystem;

    private final Logger logger;

    // initialization ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a FileUpload component.
     * 
     * @param config the configuration.
     * @param context the context.
     * @param fileSystem Ledge file system.
     * @param threadPool Ledge thread pool.
     */
    public FileUploadImpl(Configuration config, Context context, FileSystem fileSystem,
        ThreadPool theradPool, Logger logger)
    {
        uploadLimit = config.getChild("upload_limit").getValueAsInteger(DEFAULT_UPLOAD_LIMIT);
        workingDirectory = config.getChild("working_directory").getValue(DEFAULT_WORKING_DIRECTORY);
        this.context = context;
        this.fileSystem = fileSystem;
        this.logger = logger;
        this.bucketCleaner = new UploadBucketCleaner(workingDirectory, fileSystem, logger);
        theradPool.runDaemon(bucketCleaner);
    }

    // public API ///////////////////////////////////////////////////////////////////////////////

    @Override
    public void start()
    {
        cleanupStaleBuckets();
    }

    @Override
    public void stop()
    {
    }

    @Override
    public void limitExceeded()
    {
        context.setAttribute(UPLOAD_CONTEXT_KEY,
            new UploadLimitExceededException(Integer.toString(getUploadLimit())));
    }

    @Override
    @SuppressWarnings("unchecked")
    public UploadContainer getContainer(String name)
        throws UploadLimitExceededException
    {
        Object value = context.getAttribute(UPLOAD_CONTEXT_KEY);

        // no upload performed
        if(value == null)
        {
            return null;
        }
        else if(value instanceof UploadLimitExceededException)
        {
            // upload limit exceeded - message is the limit value
            throw (UploadLimitExceededException)value;
        }
        else if(value instanceof Map<?, ?>)
        {
            // upload successful - return a requested container
            // (it may also be null for not uploaded items)
            Map<String, UploadContainer> map = (Map<String, UploadContainer>)value;
            return map.get(name);
        }
        else
        {
            throw new RuntimeException("Probably a valve conflicting with FileUploadValve exists");
        }
    }

    @Override
    public int getUploadLimit()
    {
        return uploadLimit;
    }

    @Override
    public UploadBucket createBucket(UploadBucketConfig config)
    {
        String id;
        synchronized(bucketIdGen)
        {
            do
            {
                long i = bucketIdGen.nextLong() & 0x7ffffffl;
                id = Long.toString(i, 36);
            }
            while(allBuckets.containsKey(id));
        }
        UploadBucket bucket = new UploadBucket(fileSystem, workingDirectory, id, config);
        allBuckets.put(id, bucket);
        getHolder().addBucket(bucket);
        return bucket;
    }

    @Override
    public void releaseBucket(UploadBucket bucket)
    {
        bucketCleaner.schedule(bucket);
        allBuckets.remove(bucket.getId());
        getHolder().removeBucket(bucket);
    }

    /**
     * Deletes temporary files and directories left after previous server run.
     */
    private void cleanupStaleBuckets()
    {
        try
        {
            final String[] ids = fileSystem.list(workingDirectory);
            logger.info("cleaning up " + ids + " upload bucket from prevoios run");
            for(String id : ids)
            {
                bucketCleaner.schedule(new UploadBucket(fileSystem, workingDirectory, id,
                    UploadBucketConfig.DEFAULT));
            }
        }
        catch(IOException e)
        {
            logger.error("failed to read the working directory", e);
        }
    }

    /**
     * Returns upload bucket holder for the current session, creating it if necessary.
     * 
     * @return
     */
    private UploadBucketHolder getHolder()
    {
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        UploadBucketHolder holder = (UploadBucketHolder)httpContext
            .getSessionAttribute(BUCKET_HOLDER_SESSION_KEY);
        if(holder == null)
        {
            holder = new UploadBucketHolder(this);
            httpContext.setSessionAttribute(BUCKET_HOLDER_SESSION_KEY, holder);
        }
        return holder;
    }

    /*
     * (non-Javadoc)
     * @see org.objectledge.upload.FileUpload#getBuckets()
     */
    @Override
    public Collection<UploadBucket> getBuckets()
    {
        return getHolder().getBuckets();
    }

    /*
     * (non-Javadoc)
     * @see org.objectledge.upload.FileUpload#getBucket(java.lang.String)
     */
    @Override
    public UploadBucket getBucket(String id)
    {
        return allBuckets.get(id);
    }
}
