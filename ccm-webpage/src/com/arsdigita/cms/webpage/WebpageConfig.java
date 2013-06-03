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

package com.arsdigita.cms.webpage;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.Assert;
import com.arsdigita.util.AssertionError;
//import com.arsdigita.util.parameter.BigDecimalParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import java.math.BigDecimal;
import org.apache.log4j.Logger;


/* Class to store configuration parameters for Webpage
 *
 * @author Crag Wolfe
 * @version $Id: WebpageConfig.java,v 1.2 2004/03/05 04:32:56 tzumainn Exp $
 */ 
public final class WebpageConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(WebpageConfig.class);

    private StringParameter m_contentSection = null;
    //private BigDecimalParameter m_internalGroupID = null;
    //private StringParameter m_legacyFolderName = null;
    //private StringParameter m_legacyFolderID = null;
    

    public WebpageConfig() {

        m_contentSection = new StringParameter(
            "com.arsdigita.cms.webpage.content_section",
            Parameter.OPTIONAL,
            "main");   // TODO: remove hardcoded value
        
        //m_internalGroupID = new BigDecimalParameter(
        //    "com.arsdigita.cms.webpage.internal_group_id",
        //    Parameter.REQUIRED,
        //    null);
        
        //m_legacyFolderName = new StringParameter(
        //    "com.arsdigita.cms.webpage.legacy_folder_name",
        //    Parameter.REQUIRED,
        //    null);

        //m_legacyFolderID = new StringParameter(
        //    "com.arsdigita.cms.webpage.legacy_folder_id",
        //    Parameter.REQUIRED,
        //    null);

        register(m_contentSection);
        //register(m_internalGroupID);
        //register(m_legacyFolderName);
        //register(m_legacyFolderID);

        loadInfo();
    }

    public final String getContentSection() {
        return (String) get(m_contentSection);
    }

    public final void setContentSection(final String section) {
        set(m_contentSection, section);
    }

    public final ContentSection getWebpageSection() {
	ContentSection section = null;
	ContentSectionCollection csl = ContentSection.getAllSections();
        csl.addEqualsFilter("label",getContentSection());
        if (csl.next()) {
            section = csl.getContentSection();
        }
	csl.close();

	return section;
    }
}
