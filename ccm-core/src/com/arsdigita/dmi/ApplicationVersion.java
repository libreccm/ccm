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

import org.apache.log4j.Logger;

/*
 *
 * @author Bryan Che (bche@redhat.com)
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 * @since CCM Core 5.2
 *
 */

public class ApplicationVersion {
    public final static String versionId = "$Id: ApplicationVersion.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(ApplicationVersion.class);

    private String m_sName;
    private String m_sUpgradeFile;
    private String m_sDescription;
    private String m_sPreviousVersionName;
    private boolean m_bUpgradeDataModel;

    public ApplicationVersion(String sName, boolean bUpgradeDataModel, String sUpgradeFile,
                              String sDescription, String sPreviousVersionName) {
        m_sName = sName;
        m_bUpgradeDataModel = bUpgradeDataModel;
        m_sUpgradeFile = sUpgradeFile;
        m_sDescription = sDescription;
        m_sPreviousVersionName = sPreviousVersionName;
    }

    public String getName() {
        return m_sName;
    }

    //does this version upgrade the data model from the previous version
    public boolean hasUpgradeDataModel() {
        return m_bUpgradeDataModel;
    }

    public String getUpgradeFile() {
        return m_sUpgradeFile;
    }

    public String getDescription() {
        return m_sDescription;
    }

    public String getPreviousVersionName() {
        return m_sPreviousVersionName;
    }
}
