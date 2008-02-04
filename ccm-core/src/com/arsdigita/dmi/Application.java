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

import com.arsdigita.xmlinterp.XMLWalker;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/*
 *
 * @author Bryan Che (bche@redhat.com)
 * @version $Revision: #12 $ $Date: 2004/08/16 $
 * @since CCM Core 5.2
 *
 */

public class Application {

    public final static String versionId = "$Id: Application.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(Application.class);

    private HashMap m_versionMap = new HashMap();
    private String m_sCurrentVersionName = null;
    private String m_sDatabase = null;
    private String m_sDescription = null;
    private String m_sInstallFile = null;
    private String m_sName = null;

    public Application(File prodFile, String database) throws ParserConfigurationException, SAXException, IOException {
        m_sDatabase = database;

        //read in the XML file and store the information

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        ProductXMLInterpreter pxi = new ProductXMLInterpreter();
        XMLWalker xmlwalker = new XMLWalker();

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(prodFile);
        xmlwalker.walk(doc, pxi);

        m_sName = pxi.getProductName();
        m_sDescription = pxi.getProductDescription();
        m_sCurrentVersionName = pxi.getCurrentVersion();
        m_sInstallFile = pxi.getInstallFile(m_sDatabase);

        //store version info
        ApplicationVersion[] avArray = pxi.getVersions(m_sDatabase);

        //XXX
        if ( avArray == null ) {
            s_log.warn ("No version history was found");
            return;
        } else {
            s_log.warn ("avArray: " + avArray);
            for (int i=0; i < avArray.length; i++) {
                ApplicationVersion av = avArray[i];

                m_versionMap.put(av.getName(), av);
            }
        }
    }

    public String getDatabase() {
        return m_sDatabase;
    }

    public String getName() {
        return m_sName;
    }

    public String getDescription() {
        return m_sDescription;
    }

    public String getCurrentVersion() {
        return m_sCurrentVersionName;
    }

    public String getDataModelInstallFileName() {
        return m_sInstallFile;
    }

    public ApplicationVersion[] getUpgradePath(String sVersion) {
        //find the path from the current version to the version from which we're upgrading

        Vector v = new Vector();

        ApplicationVersion av = (ApplicationVersion)m_versionMap.get(m_sCurrentVersionName);

        while ( av.getName().compareTo(sVersion) != 0 ) {
            v.insertElementAt(av, 0);
            av = (ApplicationVersion)m_versionMap.get(av.getPreviousVersionName());
        }

        ApplicationVersion avArray[] = new ApplicationVersion[0];
        return (ApplicationVersion[])v.toArray(avArray);
    }

}
