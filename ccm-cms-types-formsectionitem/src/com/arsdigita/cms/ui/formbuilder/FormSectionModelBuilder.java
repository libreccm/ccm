/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.formbuilder;

import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.formbuilder.FormSectionItem;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;




/**
 * 
 * 
 */
public class FormSectionModelBuilder implements PrintListener {

    /**    */
    private ItemSelectionModel m_item;

    /**
     * Constructor.
     * @param item 
     */
    public FormSectionModelBuilder(ItemSelectionModel item) {
        m_item = item;
    }

    /**
     * 
     * @param e 
     */
    public void prepare(PrintEvent e) {
        ContentItem item = (ContentItem)m_item.getSelectedObject(e.getPageState());
        ContentSection section = item.getContentSection();
        
        Session session = SessionManager.getSession();
        DataCollection sections = session.retrieve(FormSectionItem.BASE_DATA_OBJECT_TYPE);
        sections.addEqualsFilter(ContentItem.VERSION, ContentItem.DRAFT);
        sections.addOrder(ContentPage.TITLE);

        OptionGroup group = (OptionGroup)e.getTarget();

        while (sections.next()) {
            DataObject obj = sections.getDataObject();
            
            group.addOption(new Option(obj.get(ContentItem.ID).toString(), 
                                       obj.get(ContentPage.TITLE).toString()));
        }
    }
}
