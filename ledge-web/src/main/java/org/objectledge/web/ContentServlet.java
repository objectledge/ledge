package org.objectledge.web;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.objectledge.filesystem.FileSystem;

/**
 * A Servlet that allows serving content from combined ObjectLedge file system.
 * <p>
 * Only <code>GET</code> method is supported directly.
 * </p>
 * <p>
 * The supported HTTP headers are <code>Content-Type</code>, <code>Content-Length</code> and
 * <code>Last-Modified</code> / <code>If-Modified-Since</code>.
 * </p>
 * <p>
 * Returned status code are <code>404<code> for non-existent pathnames, <code>403</code> for
 * directories and non-readable files, <code>304</code> for files that have not been changed later
 * than <code>If-Modified-Since</code> specified date, <code>200</code> otherwise.
 * </p>
 * 
 * @author rafal
 * @since 1.0.10
 */
public class ContentServlet
    extends HttpServlet
{
    private FileSystem fileSystem;

    private ServletContext servletContext;

    @Override
    public void init()
    {
        fileSystem = LedgeServlet.fileSystem(getServletConfig(), getClass().getClassLoader());
        servletContext = getServletConfig().getServletContext();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        String path = req.getServletPath() + req.getPathInfo();

        if(fileSystem.exists(path))
        {
            if(fileSystem.isFile(path) && fileSystem.canRead(path))
            {
                if(req.getHeader("If-Modified-Since") != null)
                {
                    long clientDate = req.getDateHeader("If-Modified-Since");
                    if(fileSystem.lastModified(path) <= clientDate)
                    {
                        resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                        return;
                    }
                }
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setHeader("Content-Type", servletContext.getMimeType(path));
                resp.setIntHeader("Content-Length", (int)fileSystem.length(path));
                resp.setDateHeader("Last-Modified", fileSystem.lastModified(path));
                fileSystem.read(path, resp.getOutputStream());
                resp.flushBuffer();
            }
            else
            {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            }
        }
        else
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        }
    }
}
