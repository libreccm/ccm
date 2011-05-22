/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.installer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

//Bimport com.arsdigita.initializer.Configuration;
// import com.arsdigita.initializer.InitializationException;
import com.arsdigita.mimetypes.MimeType;


/**
 * Initializes mime-type to icon map.
 *
 * @author Crag Wolfe
 *
 */

public class MimeIconInitializer { // implements com.arsdigita.initializer.Initializer {

    // private Configuration m_conf = new Configuration();

    private static final String SEPARATOR = java.io.File.separator;

    public static String ICON_URL_ROOT = "iconURLRoot";
    public static final String MIME_ICON_MAP = "mimeIconMap";
    public static final String DEFAULT_ICON = "defaultIcon";

    private static Logger s_log =
        Logger.getLogger(MimeIconInitializer.class);

    private static HashMap m_iconMap = new HashMap();
    private static String m_iconUrlRoot = null;
    private static String m_defaultIcon = null;

    public MimeIconInitializer() { 
    //  m_conf.initParameter(ICON_URL_ROOT,
    //                       "location of mime-type icons",
    //                       String.class);
    //  m_conf.initParameter(DEFAULT_ICON,
    //                       "icon to use if no match found",
    //                       String.class);
    //  m_conf.initParameter(MIME_ICON_MAP,
    //                       "mime-type to icon map",
    //                       List.class);
    }

    /**
     * Returns the configuration object used by this initializer.
     */
//  public Configuration getConfiguration() {
//      return m_conf;
//  }

    /**
     * Called on startup.
     */
    public void startup() {
        s_log.warn("Mime Icon Initializer beginning");

    //  m_iconUrlRoot = (String) m_conf.getParameter
    //      (ICON_URL_ROOT);
    //  m_defaultIcon = (String) m_conf.getParameter
    //      (DEFAULT_ICON);

    //  List mimeTypes = (List) m_conf.getParameter
    //      (MIME_ICON_MAP);

/*        Iterator i = mimeTypes.iterator();
        while(i.hasNext()) {
            List values = (List) i.next();
            if(values.size() != 2) {
                s_log.fatal("expected two elements in row, but "+
                            "found "+
                            (new Integer(values.size())).toString());
                throw new InitializationException
                    (MIME_ICON_MAP+" parameter not formatted correctly");
            }
            String mimeTypeName = (String) values.get(0);
            String iconName = (String) values.get(1);
            MimeType mt = MimeType.loadMimeType(mimeTypeName);
            if (mimeTypeName == null) {
                s_log.error
                    ("mime type "+mimeTypeName+" does not exist in "+
                     "com.arsdigita.mimetypes.MimeTypeInitializer,"+
                     "so this icon will not be used");
                continue;
            }
            m_iconMap.put(mimeTypeName, m_iconUrlRoot + iconName);            
        }
*/
        s_log.info("Mime Icon Initializer completed.");
    }

/*    public static String getMimeIconURL(String mimeTypeName) {
        if (m_iconMap.get(mimeTypeName) != null) {
            return (String) m_iconMap.get(mimeTypeName);
        }
        return m_iconUrlRoot + m_defaultIcon;
    }
*/
    /**
     * Shutdown the document manager.
     */
//  public void shutdown() { }

}
