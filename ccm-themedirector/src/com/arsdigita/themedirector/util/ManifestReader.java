/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.arsdigita.themedirector.util;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import com.arsdigita.web.Web;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.Collection;
import com.arsdigita.themedirector.ThemeDirector;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;


/**
 *  This is a utility class that will take in a manifest file and
 *  read it and then make calls to methods for each file that is found.
 *  In a typical usage, code will subclass this so that certain methods
 *  will write to different places.  For instance, some code may
 *  override the "processManifestFileLine" to write the contents to
 *  the file system while another may write it to a zip file.
 */
public abstract class ManifestReader implements ThemeDirectorConstants {

    private static final Logger s_log = 
        Logger.getLogger(ManifestReader.class);

    private InputStream m_stream;
    private String m_fileName;
    private String m_possibleServletContext;
    private HashMap m_actualContextList;

    /**
     *  This takes in the actual input stream that is the Manifest File
     *  so that the input stream can be correctly read
     */
    public ManifestReader(InputStream stream) {
        this(stream, null);
    }

    /**
     *  This takes in the actual input stream that is the Manifest File
     *  so that the input stream can be correctly read.  It also
     *  takes in the fileName so that it can be used in error messages
     *  if there is an error.
     */
    public ManifestReader(InputStream stream, String fileName) {
        this(stream, fileName, null);
    }

    /**
     *  This takes in the actual input stream that is the Manifest File
     *  so that the input stream can be correctly read.  It also
     *  takes in the fileName so that it can be used in error messages
     *  if there is an error.
     *  @param stream The input stream to read
     *  @param fileName The name of the file we are reading that will
     *                  be displayed in the case of an error.
     *  @param possibleServletContext The servlet context to try to use
     *         when looking for files listed in the Manifest. This should be
     *         set when it is know that the manifest file specifies files
     *         that are located under a different webapps.  If the
     *         file is not found under this context or this context is null
     *         then the file tries to use the default ServletContext
     */
    public ManifestReader(InputStream stream, String fileName, 
                          String possibleServletContext) {        
        m_stream = stream;
        m_possibleServletContext = possibleServletContext;
        m_actualContextList = new HashMap();
        setFileName(fileName);
    }


    /**
     *  this is the name of the file that is being parsed.  This will
     *  return null if the name has not been set
     */
    public String getFileName() {
        return m_fileName;
    }

    public void setFileName(String fileName) {
        m_fileName = fileName;
    }

    /**
     *  This method will iterate through the lines of the input stream
     *  and call processLine(String) for each line that is read.  Since
     *  This goes through the InputStream and closes it when it is done,
     *  this method will only really do anything once.
     */
    public void processFile() {
            LineNumberReader lines =
                new LineNumberReader(new InputStreamReader(m_stream));
            Collection extensions = ThemeDirector.getConfig()
                .getDownloadFileExtensions();

            try {
                String line = lines.readLine();
                while (line != null) {
                    line = line.trim();

                    int fileExtensionIndex = line.lastIndexOf(".");

                    // We check the following things to set the boolean
                    // indicating if it is a file that should
                    // be used for downloading purposes
                    // 1. there actually is a file extension (it contains a ".")
                    // 2. the file does not end with a "." which would mean no
                    //    file extension
                    // 3. the extension that has been found is one of the desired
                    //    extensions as specified in the config file
                    // 4. the file starts with the default directory and is
                    //    not just the directory itself
                    boolean fileForDownload = fileExtensionIndex > -1 && 
                        line.length() > (fileExtensionIndex+1) &&
                        extensions.contains(line.substring(fileExtensionIndex+1)
                                            .toLowerCase());

                    // get the stream from the WAR or file system
                    InputStream stream = 
                        getResourceAsStream(line, m_possibleServletContext);

                    if (stream == null) {
                        s_log.debug
                            (m_fileName + ": " + 
                             lines.getLineNumber() +
                             ": no such resource '" + line + "'");
                    } else {
                        processManifestFileLine(stream, line, fileForDownload);

                        stream.close();
                    }
                    line = lines.readLine();
                }
            } catch (IOException e) {
                throw new UncheckedWrapperException
                    ("Error with " + m_fileName + ": " + 
                     lines.getLineNumber(), e);
            } finally {
                try { 
                    m_stream.close(); 
                }
                catch (IOException e) { 
                    throw new UncheckedWrapperException(e); 
                }
            }
    }


    /**
     *  This provides a way for child classes to look for the resource
     *  in multiple places.  By default, it only looks in the 
     *  ServletContext
     */
    protected InputStream getResourceAsStream(String line, 
                                              String possibleServletContext) {
        InputStream stream = null;
        if (possibleServletContext != null) {
            stream = Web.getServletContext().getContext(possibleServletContext)
                .getResourceAsStream(line);
        }
        if (stream != null) {
            setActualContext
                (line, 
                 Web.getServletContext().getContext(possibleServletContext));
        } else {
            stream = Web.getServletContext().getResourceAsStream(line);

            if (stream == null) {
                // this means that the file is not under the passed in 
                // context or the default context so let's check the "ROOT"
                // context
                stream = Web.getServletContext().getContext(ROOT_WEBAPP_PATH)
                    .getResourceAsStream(line);                
                if (stream != null) {
                    setActualContext(line, Web.getServletContext().getContext(ROOT_WEBAPP_PATH));
                }
            } else {
                setActualContext(line, Web.getServletContext());
            }
        }
        return stream;
    }


    /**
     *  This provides subclasses with access to the actual ServletContext
     *  where the line is found.  The info for the line should be available
     *  when processManifestFileLine is called for a given line
     */
    protected ServletContext getActualContext(String line) {
        return (ServletContext)m_actualContextList.get(line);
    }

    protected void setActualContext(String line, ServletContext context) {
        m_actualContextList.put(line, context);
    }


    /**
     *  This is called exactly once for every line in the Manifest file
     *  and can be overridden by subclasses if needed
     *
     *  @param stream The InputStream that was located when locating the
     *                file indicated by "filePath"
     *  @param filePath The full path of the file that is represented by the
     *                  input stream
     *  @param isStyleFile This indicates if the file has an 
     *                     extension allowed by the system config
     *                     If true, it should be included in the 
     *                     styling downloads.
     */
    public abstract void processManifestFileLine(InputStream stream,
                                                 String filePath,
                                                 boolean isStyleFile);
}
