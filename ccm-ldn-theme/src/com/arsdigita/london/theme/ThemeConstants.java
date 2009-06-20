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

package com.arsdigita.london.theme;


/**
 *  Constants shared by the theme application.  
 */
public interface ThemeConstants {
    public final static String PROD_DIR_STUB = "themes-prod";
    public final static String DEV_DIR_STUB = "themes-dev";
    // TODO: it would be nice if this value in com.arsdigita.web.URL
    // was public
    public final static String CCM_PREFIX = "__ccm__";
    public final static String WEB_APP_NAME = "ROOT";

    // The location of the sync jsp used to sync up the multiple servers.
    public final static String SYNC_JSP = "sync-theme.jsp";

    //  This can be used to find the root webapp directory that is used
    //  by default for most of the applications in WAF
    public final static String ROOT_WEBAPP_PATH = "/ROOT";

    public final static String PROD_THEMES_BASE_DIR = 
        CCM_PREFIX + "/" + PROD_DIR_STUB+ "/";
    public final static String DEV_THEMES_BASE_DIR = 
        CCM_PREFIX + "/" + DEV_DIR_STUB + "/";
    public static final String THEME_XML_PREFIX = "theme:";
    public final static String XML_NS = 
        "http://ccm.redhat.com/london/theme/1.0";

    public final static String PREVIEW_PREFIX = "/theme";

    // this is the file name when the user downloads all files
    public final static String ALL_STYLES_ZIP_NAME = "all-styles.zip";

    public final static String XSL_VALIDATION_WARNINGS = "theme:xslWarnings";
    public final static String XSL_VALIDATION_ERRORS = "theme:xslErrors";
    public final static String XSL_VALIDATION_FATALS = "theme:xslFatals";
    public final static String XSL_ERROR_INFO = "theme:xslErrorInfo";
}
