/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.cms.dublin.ui;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.Form;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.bebop.PageState;

import org.apache.log4j.Logger;

public class DublinCoreForm extends Form {

    private static final Logger s_log = Logger.getLogger(DublinCoreFormSection.class);
    
    private ItemSelectionModel m_itemModel;

    private final DublinCoreFormSection m_section;


    public DublinCoreForm(ItemSelectionModel itemModel) {
        super("dublin");
        m_itemModel = itemModel;

        m_section = new DublinCoreFormSection(false) {
            @Override
            protected String getInitialDescription(ContentItem item) {
                return ((ContentPage) item).getSearchSummary();
            }

            @Override
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
