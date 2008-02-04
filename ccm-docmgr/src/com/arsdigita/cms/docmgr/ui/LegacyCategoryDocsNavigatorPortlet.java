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

package com.arsdigita.cms.docmgr.ui;

import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.docmgr.DocMgr;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.web.Application;
import com.arsdigita.xml.Element;

/**
 * Portlet showing the category tree used by Documents.
 *
 * @author Crag Wolfe
 */
public class LegacyCategoryDocsNavigatorPortlet extends AppPortlet 
    implements DMConstants {
    public static final String versionId =
        "$Id: //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/ui/LegacyCategoryDocsNavigatorPortlet.java#1 $" +
        "$Author: cwolfe $" +
        "$DateTime: 2004/01/14 15:24:15 $";

    protected static org.apache.log4j.Category s_log = 
        org.apache.log4j.Category.getInstance
        (LegacyCategoryDocsNavigatorPortlet.class.getName());

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.docmgr.ui.LegacyCategoryDocsNavigatorPortlet";

    private Category m_rootCategory;

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public LegacyCategoryDocsNavigatorPortlet(DataObject dataObject) {
        super(dataObject);

        ContentSectionCollection csl = ContentSection.getAllSections();
        csl.addEqualsFilter("label",DocMgr.getConfig().getContentSection());
        if (!csl.next()) {
                csl.close(); return;
        }
        final ContentSection cs = csl.getContentSection();
        csl.close();
        m_rootCategory = cs.getRootCategory();
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new LegacyCategoryDocsNavigatorPortletRenderer(this);
    }

    class LegacyCategoryDocsNavigatorPortletRenderer extends AbstractPortletRenderer
        implements DMConstants {
        private LegacyCategoryDocsNavigatorPortlet m_portlet;
        
        public LegacyCategoryDocsNavigatorPortletRenderer
            (LegacyCategoryDocsNavigatorPortlet docsPortlet) {
            
            m_portlet = docsPortlet;
        }
        
        
        protected void generateBodyXML(PageState pageState,
                                   Element parentElement) {
            
            Application application  = m_portlet.getParentApplication();

            String fileURL = application.getPath();
            //Repository rep = (Repositoryx) application;
            //HttpServletRequest req = pageState.getRequest();
            
            SimpleContainer mainContainer = new SimpleContainer();

            Label info_header = new Label
                (new GlobalizedMessage("ui.cat.portlet.browse.header", 
                                       BUNDLE_NAME));

            GridPanel catPanel = new GridPanel(1);

	    CategoryCollection cats = m_rootCategory.getChildren();
	    if (cats.next()) {
		Category cat = cats.getCategory();
		catPanel.add
                    (new Link
                     (cat.getDisplayName(),
                      getParentApplication().getPath()+"?"+
                      CAT_TREE_INIT_ID_PARAM_NAME+
                      "="+cat.getID().toString()));
            }


            mainContainer.add(info_header);
            mainContainer.add(catPanel);
            mainContainer.generateXML(pageState, parentElement);
        }

    }
}
