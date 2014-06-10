package org.objectledge.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public interface UploadContainer
{

    /**
     * get the name of the item.
     * 
     * @return the name.
     */
    String getName();

    /**
     * get the uploaded file name.
     * 
     * @return the file name.
     */
    String getFileName();

    /**
     * get the size of the uploaded item.
     * 
     * @return the size.
     */
    long getSize();

    /**
     * get the byte array of the uploaded item.
     * 
     * @return the data.
     * @throws IOException
     */
    byte[] getBytes()
        throws IOException;

    /**
     * returns the uploaded item as a string.
     * 
     * @return the uploaded item.
     * @throws IOException
     */
    String getString()
        throws IOException;

    /**
     * returns the uploaded item as a string with a given encoding.
     * 
     * @param encoding the encoding of the item.
     * @return the uploaded item.
     * @throws UnsupportedEncodingException if not supported.
     * @throws IOException
     */
    String getString(String encoding)
        throws UnsupportedEncodingException, IOException;

    /**
     * get the stream of bytes of the uploaded data.
     * 
     * @return the input stream of the item.
     */
    InputStream getInputStream();

    /**
     * get the mime type of the uploaded item.
     * 
     * @return the mime type of the item.
     */
    String getMimeType();

    /**
     * Add chunk of data to the container;
     * 
     * @param offset offset of the chunk to be added
     * @param length length of the chunk
     * @param is stream to load chunk from
     * @return updated chunk
     * @throws IOException
     */
    UploadContainer addChunk(int offset, int length, InputStream is)
        throws IOException;

    /**
     * Returns JPEG image of the desired maximum size.
     * 
     * @param size desired maximum size. If the image is already smaller than provided size, it size
     *        will not be changed.
     * @throws UnsupportedOperationException if the container does contain an image.
     * @return image data, converted to JPEG format.
     * @throws IOException when a problem during image processing occurs
     */
    byte[] getThumbnail(int size)
        throws IOException;

    /**
     * Dispose this container's contents.
     * 
     * @throws IOException
     */
    void dispose()
        throws IOException;
}
