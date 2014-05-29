package org.objectledge.upload;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Configuration for a specific upload bucket. Each form that employs file upload may specify it's
 * particular restrictions while creating an upload bucket.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class UploadBucketConfig
{
    private final int maxCount;

    private final int maxSize;

    private final String allowedFormats;

    private final Collection<Pattern> patterns;

    private final int thumbnailSize;

    /**
     * Creates a new UploadBucket configuration.
     * 
     * @param maxCount Maximum number of files that may be uploaded into the bucket, or -1 for no
     *        restriction.
     * @param maxSize Maximum size of a file that may be uploaded into the bucket in bytes, or -1
     *        for no restriction.
     * @param allowedFormats space separated list of file name extensions, or empty string for no
     *        restriction.
     * @param thumbnailSize maximum size of server-side generated image thumbnails. It might be
     *        width or height dependent on image orientation.
     */
    public UploadBucketConfig(int maxCount, int maxSize, String allowedFormats, int thumbnailSize)
    {
        this.maxCount = maxCount;
        this.maxSize = maxSize;
        this.allowedFormats = allowedFormats;
        this.thumbnailSize = thumbnailSize;
        this.patterns = allowedFormats != null && allowedFormats.trim().length() > 0 ? Collections2
            .transform(Arrays.asList(allowedFormats.split(" ")), new Function<String, Pattern>()
                {
                    @Override
                    public Pattern apply(String input)
                    {
                        return Pattern.compile("\\." + input + "$");
                    }
                }) : Collections.<Pattern> emptyList();
    }

    /**
     * Maximum number of files that may be uploaded into the bucket, or -1 for no restriction.
     * 
     * @return Maximum number of files
     */
    public int getMaxCount()
    {
        return maxCount;
    }

    /**
     * Maximum size of a file that may be uploaded into the bucket in bytes, or -1 for no
     * restriction.
     * 
     * @return Maximum size of a file
     */
    public int getMaxSize()
    {
        return maxSize;
    }

    /**
     * Allowed file name extensions.
     * 
     * @return space separated list of file name extensions, or empty string for no restriction.
     */
    public String getAllowedFormats()
    {
        return allowedFormats;
    }

    /**
     * Checks if a specific file name is acceptable according to allowedFormats setting.
     * 
     * @param fileName File name
     * @return {@code true} if file name is acceptable.
     */
    public boolean isAllowed(String fileName)
    {
        if(patterns.size() > 0)
        {
            for(Pattern p : patterns)
            {
                if(p.matcher(fileName).find())
                {
                    return true;
                }
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Maximum size of server-side generated image thumbnails. It might be width or height dependent
     * on image orientation.
     * 
     * @return Maximum size of server-side generated image thumbnails.
     */
    public int getThumbnailSize()
    {
        return thumbnailSize;
    }

    public static final UploadBucketConfig DEFAULT = new UploadBucketConfig(-1, -1, "", 64);
}
