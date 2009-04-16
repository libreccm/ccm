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


package com.arsdigita.london.theme.dispatcher;

import com.arsdigita.cms.ContentType;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.UniversalPermissionDescriptor;
import com.arsdigita.london.theme.Theme;
import com.arsdigita.london.theme.ThemeConstants;
import com.arsdigita.london.theme.util.ManifestReader;
import com.arsdigita.london.theme.util.WhiteListFilenameFilter;
import com.arsdigita.runtime.RegistryConfig;
import com.arsdigita.util.Files;
import com.arsdigita.util.IO;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.BaseServlet;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;


/**
 * A servlet that multiplexes all XSL files registered
 * against content types into one.
 */
public class ThemeDownloadServlet extends BaseServlet implements ThemeConstants {

    private static final Logger s_log =
        Logger.getLogger(ThemeDownloadServlet.class);

    public static final String THEME_ID = "themeID";
    public static final String PUBLISHED_PREFIX = "theme-published-";
    public static final String DEVELOPMENT_PREFIX = "theme-dev-";

    private void checkPermission(HttpServletRequest sreq) {
        Party party = Kernel.getContext().getParty();
        if (party == null) {
            throw new LoginSignal(sreq);
        }

        UniversalPermissionDescriptor universalPermission =
            new UniversalPermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                              party.getOID());

        if (! PermissionService.checkPermission(universalPermission)) {
            throw new AccessDeniedException(
                "user " + party.getOID() + " doesn't have the " +
                PrivilegeDescriptor.ADMIN.getName() + " admin privileges");
        }
    }

    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp)
        throws ServletException, IOException {
        checkPermission(sreq);

        BigDecimal themeID = null;
        String themeIDString = sreq.getParameter(THEME_ID);
        if (themeIDString != null && themeIDString.trim().length() > 0) {
            try {
                themeID = new BigDecimal(themeIDString);
            } catch (NumberFormatException e) {
                s_log.error("Unable to convert the themeID in to a BigDecimal " +
                            sreq.getParameter(THEME_ID), e);
                throw new UncheckedWrapperException
                    ("Unable to convert the themeID in to a BigDecimal " +
                     sreq.getParameter(THEME_ID), e);
            }
        }
        Theme theme = null;
        if (themeID != null) {
            try {
                theme = new Theme(themeID);
            } catch (DataObjectNotFoundException e) {
                s_log.error("Unable to locate the theme with id " + themeID, e);
                throw new UncheckedWrapperException
                    ("Unable to find the theme with the id " + themeID);
            }
        }


        DispatcherHelper.maybeCacheDisable(sresp);
        sresp.setContentType("application/zip; charset=UTF-8");

        // The WebAppRoot should be something like this:
        // /var/ccm-devel/web/<username>/<projectname>/webapps/ccm-ldn-theme;
        // and we actually want the webapps directory
        File currentRoot = new File(Web.getServletContext().getRealPath("/"));
        File webapps = currentRoot.getParentFile();

        // if we have a Theme, we send back either prod or dev, depending on
        // the url.  If we don't have a theme, then we return everything.
        if (theme != null) {
            File downloadFiles = null;
            String prefix = null;
            // If "themes-prod" is ever moved to a different directory,
            // we could then get rid of the "type" argument (see below in
            // DirectoryFilter)
            String type = null;
            if (sreq.getPathInfo().indexOf("/" + PUBLISHED_PREFIX) > -1) {
                prefix = PUBLISHED_PREFIX;
                downloadFiles = new File(currentRoot, PROD_THEMES_BASE_DIR);
                type = PROD_DIR_STUB;
            } else {
                downloadFiles = new File(currentRoot, DEV_THEMES_BASE_DIR);
                prefix = DEVELOPMENT_PREFIX;
                type = DEV_DIR_STUB;
            }
            sresp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + prefix + theme.getURL() +
                            ".zip\"");

            Files.writeZipFile
                (sresp.getOutputStream(),
                 Files.listFilesInTree(downloadFiles,
                                       new DirectoryFilter(theme.getURL(), type)),
                 downloadFiles);
        } else if (theme == null) {
            // we have to create our own config becuase no other
            // classes provide access to one
            RegistryConfig rc = new RegistryConfig();
            rc.load();
            String[] packages = rc.getPackages();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            sresp.setHeader("Content-Disposition",
                            "attachment; filename=\"" + ALL_STYLES_ZIP_NAME +
                            "\"");
            ZipOutputStream out = new ZipOutputStream(sresp.getOutputStream());

            for (int i = 0; i < packages.length; i++) {
                String current = packages[i] + ".web.mf";
                InputStream is = loader.getResourceAsStream(current);
                if (is != null) {
                    ZipWriterManifestReader reader =
                        new ZipWriterManifestReader(is, out, current,
                                                    packages[i]);
                    reader.setFileName(packages[i]);
                    reader.processFile();
                } else {
                    s_log.warn("Unable to open up resource " + current);
                }
            }

            // Special case add the auto-generated XSL fiel for content types
            Iterator paths = ContentType.getXSLFileURLs();
            out.putNextEntry(new ZipEntry("waf-xsl/__ccm__/servlet/content-type/index.xsl"));
            IO.copy(multiplexXSLFiles(paths),
                    out);


            out.close();
        }
    }

    // XXX copy+paste of Templating.multiplexXSLFiles but with some
    // URL munging. Should be reconciled later
    public static InputStream multiplexXSLFiles(Iterator paths) {
        StringBuffer buf = new StringBuffer();
        Element root = new Element("xsl:stylesheet",
                                   "http://www.w3.org/1999/XSL/Transform");
        root.addAttribute("version", "1.0");

        while (paths.hasNext()) {
            URL path = (URL)paths.next();

            Element imp = root.newChildElement(
                "xsl:import",
                "http://www.w3.org/1999/XSL/Transform");
            imp.addAttribute("href", "../../../resource/ROOT" + path.getPath());
        }

        Document doc = null;
        try {
            doc = new Document(root);
        } catch (ParserConfigurationException ex) {
            throw new UncheckedWrapperException("cannot build document", ex);
        }

        return new ByteArrayInputStream(doc.toString(true).getBytes());
    }

    /**
     *  This provides a filter so that only files in a given directory
     *  are used.  This is necessary so that we can be efficient when
     *  creating our zip.  Without this, we would have to iterate through
     *  the file list an extra time.  If the
     */
    private class DirectoryFilter extends WhiteListFilenameFilter implements FilenameFilter {
        // there has to be a better way to do this...I have a hard
        // time believing that this filter is actually necessary...can we
        // do this by adding some magic to the Files.createZipFile class?
        // we could always iterate through the results ot listFilesInTree
        // but that seems like a worse solution than this.
        private String m_directoryName;
        // If "themes-prod" is ever moved to a different directory,
        // we could then get rid of the "type" argument
        private String m_type;
        DirectoryFilter(String directoryName, String type) {
            m_directoryName = directoryName;
            m_type = type;
        }

        public boolean accept(File dir, String name) {
            return super.accept(dir, name)  &&
                (dir.getAbsolutePath().indexOf("/" + m_type + "/" + m_directoryName) > -1
                || (m_directoryName.equals(name) && dir.getAbsolutePath().endsWith("/" + m_type)));

        }
    }


    private class ZipWriterManifestReader extends ManifestReader {
        private ZipOutputStream m_out;

        ZipWriterManifestReader(InputStream stream,
                                ZipOutputStream out,
                                String fileName,
                                String possibleServletContext) {
            super(stream, fileName, possibleServletContext);
            m_out = out;
        }

        public void processManifestFileLine(InputStream stream,
                                            String filePath,
                                            boolean isStyleFile) {
            if (!isStyleFile) {
                return;
            }

            try {
                ServletContext actualContext = getActualContext(filePath);

                if (actualContext != null) {
                    String realPath = actualContext.getRealPath("/");
                    String packageName = getFileName();

                    // if the real path includes the package name then
                    // that should be the beginning of the path.
                    if (realPath.indexOf(packageName) > -1) {
                        filePath = realPath.substring
                            (realPath.indexOf(packageName)) + filePath;
                    } else {
                        // we want to get the upermost directory to include
                        // as part of the file path
                        File file = new File(realPath);
                        filePath = file.getName() + "/" + filePath;
                    }
                }
                ZipEntry zipEntry = new ZipEntry("waf-xsl/resource/" + filePath);
                m_out.putNextEntry(zipEntry);

                // Transfer bytes from the stream to the ZIP file
                IO.copy(stream, m_out);

                // Close the streams
                m_out.closeEntry();
                stream.close();

                if (s_log.isDebugEnabled()) {
                    long compressed = zipEntry.getCompressedSize();
                    long original = zipEntry.getSize();
                    long ratio = ((original-compressed)*100) / original;
                    s_log.debug("Compressing file " + filePath +
                                "; original size = " + original + "; compressed = " +
                                compressed + "; ratio = " + ratio);
                }
            } catch (IOException e) {
                throw new UncheckedWrapperException("Error reading from " +
                              filePath + " or writing to the zip file");
            }
        }
    }
}

