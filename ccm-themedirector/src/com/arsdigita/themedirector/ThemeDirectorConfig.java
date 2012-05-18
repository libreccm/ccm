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

package com.arsdigita.themedirector;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.runtime.AbstractConfig;

import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.IntegerParameter;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.arsdigita.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 *  This is the configuration file for the Theme application
 */
public class ThemeDirectorConfig extends AbstractConfig {
    
    /** A logger instance.  */
    private static final Logger s_log = Logger.getLogger(ThemeDirectorConfig.class);

    /** Singelton config object.  */
    private static ThemeDirectorConfig s_conf;

    /**
     * Gain a ThemeDirectorConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized ThemeDirectorConfig getConfig() {
        if (s_conf == null) {
            s_conf = new ThemeDirectorConfig();
            s_conf.load();
        }

        return s_conf;
    }

    // set of configuration parameters
    // /////////////////////////////////////////////////////////////////
    
    /** Directory that all of the default themes are copied from. */
    private final Parameter m_defaultThemePath =
            new StringParameter
                ("themedirector.default_theme_path",
                 Parameter.OPTIONAL, "/themes/master/");

    /** Servlet context path containing the default theme.
        Previously ccm-themedirector used to be installed in its own 
        web context. In this case the appropriate web context should 
        be specified.
        Currently, it is installed as part of the main application,
        therefore it is empty by default.                                     */
    private final Parameter m_defaultThemeContext =
            new StringParameter
                ("themedirector.default_theme_context",
                 Parameter.OPTIONAL, "");
//               Parameter.OPTIONAL, "/ccm-themedirector/");

    /** File containing the default themes directory. Used in conjuntion with
        com.arsdigita.themedirectory.default_directory_filter to dictate the
        final default directory.                                             */
    private final Parameter m_defaultThemeManifest =
            new StringParameter
                ("themedirector.default_theme_manifest",
                 Parameter.OPTIONAL, "ccm-themedirector.web.mf");

    /** list of file extensions that should be included when the designer 
        requests 'all graphics files' */
    private final Parameter m_fileExtParam =
            new StringParameter
                ("themedirector.graphics_file_extensions",
                  Parameter.REQUIRED, "bmp css gif jpeg jpg js png xml xsl");

    /** number of seconds before checking for updated development files.
        in a multi-jvm installation. (0 means never start)                   */
    private final Parameter m_themeDevFileWatchStartupDelay =
            new IntegerParameter
                ("themedirector.theme_dev_file_watch_startup_delay",
                 Parameter.REQUIRED, new Integer(60*2));

    /** Number of seconds between checks for updated development files
        in a multi-jvm installation.                                         */
    private final Parameter m_themeDevFileWatchPollDelay =
            new IntegerParameter
                ("themedirector.theme_dev_file_watch_poll_delay",
                 Parameter.REQUIRED, new Integer(60*90));   //once every 90 minutes

    /** Number of seconds before checking for update of published theme files
        in a multi-jvm installation. (0 means never start)                   */
    private final Parameter m_themePubFileWatchStartupDelay =
            new IntegerParameter
                ("themedirector.theme_pub_file_watch_startup_delay",
                 Parameter.REQUIRED, new Integer(60*2));

    /** number of seconds between checks for recently published theme files
        in a multi-jvm installation.                                         */
    private final Parameter m_themePubFileWatchPollDelay =
            new IntegerParameter
                ("themedirector.theme_pub_file_watch_poll_delay",
                 Parameter.REQUIRED, new Integer(60*60));  // default to once an hour

    
    /** */
    private Collection m_downloadFileExtensions = null;

    /** 
     * Constructor.
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly! Use getConfig() instead.
     */
    public ThemeDirectorConfig() {

        register(m_fileExtParam);
        register(m_defaultThemeContext);
        register(m_defaultThemeManifest);
        register(m_defaultThemePath);
        register(m_themeDevFileWatchStartupDelay);
        register(m_themeDevFileWatchPollDelay);
        register(m_themePubFileWatchStartupDelay);
        register(m_themePubFileWatchPollDelay);

        loadInfo();
    }

    public Collection getDownloadFileExtensions() {
        if (m_downloadFileExtensions == null) {
            String extensions = (String)get(m_fileExtParam);
            if (extensions != null) {
                extensions = StringUtils.stripWhiteSpace(extensions.trim());
                String[] extArray = StringUtils.split(extensions, ' ');
                m_downloadFileExtensions = Arrays.asList(extArray);
            } else {
                m_downloadFileExtensions = new ArrayList();
            }
        }
        return m_downloadFileExtensions;
    }


    /**
     *  The number of seconds to wait before checking the database
     *  for the first time.  A value of 0 means that the thread
     *  should not be started. This checks for published files.
     */
    public Integer getThemePubFileWatchStartupDelay() {
        return (Integer)get(m_themePubFileWatchStartupDelay);
    }

    /**
     *  Returns the number of seconds between checking for updated
     *  files in the file system. This checks for published files.
     */
    public Integer getThemePubFileWatchPollDelay() {
        return (Integer)get(m_themePubFileWatchPollDelay);
    }

    /**
     *  The number of seconds to wait before checking the database
     *  for the first time.  A value of 0 means that the thread
     *  should not be started.  This checks for development files.
     */
    public Integer getThemeDevFileWatchStartupDelay() {
        return (Integer)get(m_themeDevFileWatchStartupDelay);
    }

    /**
     *  Returns the number of seconds between checking for updated
     *  files in the file system.  This checks for development files.
     */
    public Integer getThemeDevFileWatchPollDelay() {
        return (Integer)get(m_themeDevFileWatchPollDelay);
    }

    /**
     * This returns the name of the servlet context containing
     * the default theme
     */
    public String getDefaultThemeContext() {
        String ctx = (String)get(m_defaultThemeContext);
        if (ctx == null) {
            ctx = "/";
        }
        if (!ctx.endsWith("/")) {
            ctx = ctx + "/";
        }
        if (!ctx.startsWith("/")) {
            ctx = "/" + ctx;
        }
        return ctx;
    }

    /**
     *  This returns the name of the manifest file containing
     * the default theme.
     */
    public String getDefaultThemeManifest() {
        return (String)get(m_defaultThemeManifest);
    }

    /**
     *  This returns a string that can be used as a file filter for
     *  the default directory.  Specifically, if this is not null
     *  (or the empty string) than any file that is used as part of
     *  the default directory must start with this string
     */
    public String getDefaultThemePath() {
        String defaultThemePath = (String)get(m_defaultThemePath);
        if (defaultThemePath == null || defaultThemePath.trim().length() == 0) {
            return null;
        }
        if (defaultThemePath.startsWith("/")) {
            defaultThemePath = defaultThemePath.substring(1);
        }
        return defaultThemePath;
    }


    private static final String DEFAULT_THEME_URL =
        ThemeDirector.DEFAULT_THEME + "." + Theme.URL;
    private static final String DEFAULT_THEME_URL_ATTRIBUTE =
        "defaultThemeURLAttribute";

    /** 
     * 
     * @param req
     * @return
     */
    public static String getDefaultThemeURL( HttpServletRequest req ) {
        String themeURL = (String) req.getAttribute( DEFAULT_THEME_URL_ATTRIBUTE );
        if( null != themeURL ) return themeURL;

        DataCollection themeApps = SessionManager.getSession().retrieve
            ( ThemeDirector.BASE_DATA_OBJECT_TYPE );
        themeApps.addPath( DEFAULT_THEME_URL );

        if( themeApps.next() ) {
            themeURL = (String) themeApps.get( DEFAULT_THEME_URL );

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Theme app: " + themeApps.get( "id" ) );
                s_log.debug( "Default Theme URL: " + themeApps.get( DEFAULT_THEME_URL ) );
            }

            if( themeApps.next() ) {
                s_log.warn( "There appear to be multiple themes applications loaded" );
                themeApps.close();
            }
        } else {
            s_log.warn( "There doesn't appear to be a themes application loaded" );
        }

        req.setAttribute( DEFAULT_THEME_URL_ATTRIBUTE, themeURL );
        return themeURL;
    }
}
