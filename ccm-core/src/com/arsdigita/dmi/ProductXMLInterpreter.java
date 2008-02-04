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
 *
 */
package com.arsdigita.dmi;

import com.arsdigita.xmlinterp.XMLInterpreter;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Node;
import org.apache.log4j.Logger;

/*
 *
 * @author Bryan Che (bche@redhat.com)
 * @version $Revision: #12 $ $Date: 2004/08/16 $
 * @since CCM Core 5.2
 *
 */

public class ProductXMLInterpreter implements XMLInterpreter {

    public final static String versionId = "$Id: ProductXMLInterpreter.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(ProductXMLInterpreter.class);

    //product information
    private String m_sProdName = null;
    private String m_sProdDesc = null;
    private String m_sCurrentVersion = null;

    //version history information
    private String m_sDatabase = null;
    private String m_sInstallFile = null;

    //version information
    private boolean m_bVersionUpgradeDM = false;
    private String m_sVersionName = null;
    private String m_sVersionUpgradeFile = null;
    private String m_sVersionDesc = null;
    private String m_sVersionPreviousVersion = null;

    private HashMap m_VersionHistories = new HashMap();
    private ArrayList m_AppVersions = new ArrayList();

    // Use the following variables to keep track of what type of node we are
    // currently processing.
    private static final int PRODUCT = 0;
    private static final int VERSION_HISTORY = 1;
    private static final int VERSION = 2;
    private int m_iNodeType = PRODUCT;

    //cache error messages
    private String m_sError = "";

    /**
     * A <b>shift</b> is executed when a node contains a single #text child, and
     * no attributes. This node is not pushed or reduced.
     */
    public void shift(String node, String value) {
        //make this upper case
        node = node.toUpperCase().trim();

        switch ( m_iNodeType ) {
        case PRODUCT:
            // we are processing the product itself valid values for the node
            // name (s) are: CURRENT_VERSION, NAME, PRODUCT_DESCRIPTION

            if (node.compareTo("CURRENT_VERSION") == 0) {
                m_sCurrentVersion = value;
            } else if (node.compareTo("NAME") == 0) {
                m_sProdName = value;
            } else if (node.compareTo("PRODUCT_DESCRIPTION") == 0) {
                m_sProdDesc = value;
            } else {
                //some unsupported node name
                m_sError += "Unsupported Node: " + node + " with value " + value + "\n";
                s_log.error(m_sError);
            }
            break;
        case VERSION_HISTORY:
            // Processing a product version history.  Valid values for node
            // names are DATABASE, INSTALL_FILE

            if (node.compareTo("DATABASE") == 0) {
                m_sDatabase = value;
            } else if (node.compareTo("INSTALL_FILE") == 0) {
                m_sInstallFile = value;
            } else {
                //some unsupported node name
                m_sError += "Unsupported Node: " + node + " with value " + value + "\n";
                s_log.error(m_sError);
            }

            break;
        case VERSION:

            // Processing a product version. Valid values for the node names are: NAME,
            // PREVIOUS_VERSION, DATA_MODEL_CHANGE, UPGRADE_FILE,
            // VERSION_DESCRIPTION

            if (node.compareTo("NAME") == 0) {
                m_sVersionName = value;
            } else if (node.compareTo("PREVIOUS_VERSION") == 0) {
                m_sVersionPreviousVersion = value;
            } else if (node.compareTo("DATA_MODEL_CHANGE")==0) {
                value = value.toUpperCase();
                if (value.compareTo("YES") == 0) {
                    m_bVersionUpgradeDM = true;
                } else if (value.compareTo("NO") == 0) {
                    m_bVersionUpgradeDM = false;
                } else {
                    //some unsupported value
                    m_sError += "Unsupported Node: " + node + " with value " + value + "\n";
                    s_log.error(m_sError);
                }
            } else if (node.compareTo("UPGRADE_FILE") == 0) {
                m_sVersionUpgradeFile = value;
            } else if (node.compareTo("VERSION_DESCRIPTION") == 0) {
                m_sVersionDesc = value;
            } else {
                //some unsupported node name
                m_sError += "Unsupported Node: " + node + " with value " + value + "\n";
                s_log.error(m_sError);
            }
            break;
        }
    }

    /**
     * A <b>reduce</b> is executed at the end of a pushed node.
     */
    public void reduce(String node) {
        //finished processing version information
        if (node.toUpperCase().compareTo("VERSION_HISTORY") == 0) {
            m_iNodeType = PRODUCT;

            VersionHistory vh = new VersionHistory (m_sInstallFile, (ArrayList)m_AppVersions.clone());
            m_VersionHistories.put (m_sDatabase, vh);

            //XXX
            s_log.warn ("m_VersionHistories.put: " + m_sDatabase + " : " + m_VersionHistories.get(m_sDatabase));

            // reset the member vars
            m_sDatabase = null;
            m_sInstallFile = null;
            m_AppVersions = new ArrayList();

        } else if (node.toUpperCase().compareTo("VERSION") == 0) {
            m_iNodeType = VERSION_HISTORY;

            ApplicationVersion av = new ApplicationVersion(m_sVersionName,
                                                           m_bVersionUpgradeDM,
                                                           m_sVersionUpgradeFile,
                                                           m_sVersionDesc,
                                                           m_sVersionPreviousVersion);
            m_AppVersions.add(av);

            // reset the member vars
            m_sVersionName = null;
            m_bVersionUpgradeDM = false;
            m_sVersionUpgradeFile = null;
            m_sVersionDesc = null;
            m_sVersionPreviousVersion = null;
        }

    }

    /**
     * A <b>push</b> is executed when a node has more than one child. The effect
     * of a push is to put a part of the interpreter in scope, and may cause a
     * dispatch to another XMLInterpreter.
     */
    public void push(Node n) {
        if (n.getNodeName().toUpperCase().compareTo("PRODUCT") == 0) {
            m_iNodeType = PRODUCT;
        } else if (n.getNodeName().toUpperCase().compareTo("VERSION_HISTORY") == 0) {
            m_iNodeType = VERSION_HISTORY;
        } else if (n.getNodeName().toUpperCase().compareTo("VERSION") == 0) {
            m_iNodeType = VERSION;
        }
    }

    public String getError() {
        return m_sError;
    }

    ////////////////////////////////////////////////////////////////////
    //methods for getting product info once the xml file has been parsed
    ////////////////////////////////////////////////////////////////////

    public String getProductName() {
        return m_sProdName;
    }

    public String getProductDescription() {
        return m_sProdDesc;
    }

    public String getCurrentVersion() {
        return m_sCurrentVersion;
    }

    public String[] getDatabase() {
        return (String[])m_VersionHistories.keySet().toArray();
    }

    public String getInstallFile (String database) {

        if ( m_VersionHistories.containsKey(database) ) {
            VersionHistory vh = (VersionHistory)m_VersionHistories.get (database);
            if ( vh == null ) {
                return null;
            } else {
                return vh.getInstallFile();
            }
        }

        return null;
    }

    public ApplicationVersion[] getVersions(String database) {

        if ( m_VersionHistories.containsKey(database) ) {
            VersionHistory vh = (VersionHistory)m_VersionHistories.get (database);
            if ( vh == null ) {
                return null;
            } else {
                s_log.warn ("vh: " + vh);
                s_log.warn ("database: " + database);
                s_log.warn ("vh.getApplicationVersions(): " + vh.getApplicationVersions());
                return vh.getApplicationVersions();
            }
        }

        return null;
    }

    private class VersionHistory {

        private String installFile = null;
        private ApplicationVersion[] applicationVersions = null;

        VersionHistory (String installFile, ArrayList appVersions) {
            this.installFile = installFile;

            ApplicationVersion[] av = (ApplicationVersion[])appVersions.toArray(new ApplicationVersion[0]);
            this.applicationVersions = av;
        }

        protected String getInstallFile () {
            return installFile;
        }

        protected ApplicationVersion[] getApplicationVersions () {
            return applicationVersions;
        }
    }

}
