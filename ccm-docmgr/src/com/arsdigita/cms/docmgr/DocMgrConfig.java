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

    private static final Logger s_log = Logger.getLogger(DocMgrConfig.class);

    /** Private Object to hold one's own instance to return to users. */
    private static DocMgrConfig s_config;

    /**
     * Returns the singleton configuration record for DocMgr
     *
     * @return The <code>DocMgrConfig</code> record; it cannot be null
     */
    public static synchronized DocMgrConfig getInstance() {
        if (s_config == null) {
            s_config = new DocMgrConfig();
            s_config.load();
        }

        return s_config;
    }

    // /////////////////////////////////////////////////////////////////////////
    //
    //   Parameter Sektion
    
    private Parameter m_contentSection = new StringParameter
            ("com.arsdigita.cms.docmgr.content_section", 
             Parameter.REQUIRED, 
             "info");
    private Parameter m_legacyFolderName = new StringParameter
            ("com.arsdigita.cms.docmgr.legacy_folder_name", 
             Parameter.REQUIRED, 
             "legacy");
    private Parameter m_legacyFolderID = new IntegerParameter
            ("com.arsdigita.cms.docmgr.legacy_folder_id", 
             Parameter.REQUIRED, 
             -200);
    private Parameter m_internalGroupID = new IntegerParameter
            ("com.arsdigita.cms.docmgr.internal_group_id", 
             Parameter.REQUIRED, 
             -200);
    private Parameter m_rowsPerPage = new IntegerParameter
            ("com.arsdigita.cms.docmgr.rows_per_page", 
             Parameter.OPTIONAL, 
             20);

    /**
     * Constructor registers Parameters and reads repository file if exist.
     * 
     * Do NOT instantiate this class directly using its constructor but use
     * the provided getDocMgrConfig method above.
     */
    public DocMgrConfig() {

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
