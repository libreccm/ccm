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


/**
 *  Constants shared by the theme application.  
 */
public interface ThemeDirectorConstants {

    /** Name of the base directory for all themes (usually themes).  
     *  According to JavaEE spec with leading but without trailing "/"!       */
    public final static String THEMES_DIR = "/themes";
    /** Name of the directory for production themes (sub-dir of THEMES_DIR).  
     *  According to JavaEE spec with leading but without trailing "/"!       */
    public final static String PROD_DIR_STUB = "/published-themedir";
    /** Name of the directory for themes under development (sub-dir of THEMES_DIR)
     *  According to JavaEE spec with leading but without trailing "/"!       */
    public final static String DEV_DIR_STUB = "/devel-themedir";

    /** Path stub into directory for production themes (sub-dir of THEMES_DIR).  
     *  According to JavaEE spec with leading "/", but deviating from the
     *  JavaEE spec we add a trailing "/" for backwards compatibility to
     *  versions of CCM!                                                      */
    public final static String
                 PROD_THEMES_BASE_DIR = THEMES_DIR  + PROD_DIR_STUB + "/";
    /** Path stub into directory for production themes (sub-dir of THEMES_DIR).  
     *  According to JavaEE spec with leading "/", but deviating from the
     *  JavaEE spec we add a trailing "/" for backwards compatibility to
     *  versions of CCM!                                                      */
    public final static String 
                 DEV_THEMES_BASE_DIR =  THEMES_DIR  + DEV_DIR_STUB + "/";

    // Developers NOTE:
    // ================
    // We should consider to use the theme's url as entered by the user with
    // a leading slash according to the specification and for sake of 
    // consistency within CCM.
    // We would have to adjust the validation listener and the process listener
    // in class ui/ThemeForm and to update the existing themes in the database.

    /** The location of the sync jsp used to sync up the multiple servers.  */
    public final static String SYNC_JSP = "sync-theme.jsp";

    public static final String THEME_XML_PREFIX = "theme:";
    public final static String XML_NS = 
        "http://ccm.redhat.com/themedirector/1.0";

    public final static String PREVIEW_PREFIX = "/theme";

    // this is the file name when the user downloads all files
    public final static String ALL_STYLES_ZIP_NAME = "all-styles.zip";

    public final static String XSL_VALIDATION_WARNINGS = "theme:xslWarnings";
    public final static String XSL_VALIDATION_ERRORS = "theme:xslErrors";
    public final static String XSL_VALIDATION_FATALS = "theme:xslFatals";
    public final static String XSL_ERROR_INFO = "theme:xslErrorInfo";

}
