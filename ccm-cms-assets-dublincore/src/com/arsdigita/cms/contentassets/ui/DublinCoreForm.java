/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemSelectionModel;

import org.apache.log4j.Logger;

/**
 * 
 * 
 */
public class DublinCoreForm extends Form {

    /** A logger instance to assist debugging.                                */
    private static final Logger s_log = 
                                Logger.getLogger(DublinCoreFormSection.class);
    
    private ItemSelectionModel m_itemModel;
    private DublinCoreFormSection m_section;


    /**
     * Constructor
     * @param itemModel 
     */
    public DublinCoreForm(ItemSelectionModel itemModel) {

        super("dublin");

        m_itemModel = itemModel;
        m_section = new DublinCoreFormSection(false) {
            @Override
            protected String getInitialDescription(ContentItem item) {
                return ((ContentPage) item).getSearchSummary();
            }

            protected ContentItem getSelectedItem(PageState state) {
                return m_itemModel.getSelectedItem(state);
            }
        };
        add(m_section, ColumnPanel.FULL_WIDTH);
    }

    public Submit getCancelButton() {
        return m_section.getCancelButton();
    }

}
