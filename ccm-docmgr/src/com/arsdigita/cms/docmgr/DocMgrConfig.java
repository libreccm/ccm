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

package com.arsdigita.cms.docmgr;

import org.apache.log4j.Logger;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;


/* Class to store configuration parameters for Document Manager
 *
 * @author Crag Wolfe
 */ 
public final class DocMgrConfig extends AbstractConfig {

    public static final String versionId =
        "$Id: //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/DocMgrConfig.java#4 $" +
        "$Author: cwolfe $" +
        "$DateTime: 2004/01/14 15:24:15 $";

    private static final Logger s_log = Logger.getLogger(DocMgrConfig.class);

    private Parameter m_contentSection;
    private Parameter m_legacyFolderName;
    private Parameter m_internalGroupID;
    private Parameter m_rowsPerPage;
    private Parameter m_legacyFolderID;

    public DocMgrConfig() {

        m_contentSection = new StringParameter
            ("com.arsdigita.cms.docmgr.content_section", 
             Parameter.REQUIRED, 
             "documents");

        m_legacyFolderName = new StringParameter
            ("com.arsdigita.cms.docmgr.legacy_folder_name", 
             Parameter.REQUIRED, 
             "legacy");

        m_legacyFolderID = new IntegerParameter
            ("com.arsdigita.cms.docmgr.legacy_folder_id", 
             Parameter.REQUIRED, 
             "-200");


        m_internalGroupID = new IntegerParameter
            ("com.arsdigita.cms.docmgr.internal_group_id", 
             Parameter.REQUIRED, 
             "-200");

        m_rowsPerPage = new IntegerParameter
            ("com.arsdigita.cms.docmgr.rows_per_page", 
             Parameter.OPTIONAL, 
             "20");

        register(m_contentSection);
        register(m_legacyFolderName);
	register(m_legacyFolderID);
        register(m_internalGroupID);
        register(m_rowsPerPage);

        loadInfo();
    }

    public String getContentSection() {
        return (String) get(m_contentSection);
    }

    public String getLegacyFolderName() {
        return (String) get(m_legacyFolderName);
    }

    public int getLegacyFolderID() {
        return ((Integer) get(m_legacyFolderID)).intValue();
    }



    public int getInternalGroupID() {
        return ((Integer) get(m_internalGroupID)).intValue();
    }

    public int getRowsPerPage() {
        return ((Integer) get(m_rowsPerPage)).intValue();
    }
}
