/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui.authoring;

import com.arsdigita.cms.contenttypes.GlossaryItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.ui.GlossaryItemWidgetBuilder;

/*
 * A page that will create a new GlossaryItem.
 * 
 * @author Dirk Gomez
 * @see com.arsdigita.intranet.cms.GlossaryItem
 * @version $Revision: #5 $
 */
public class GlossaryItemCreate extends PageCreate {

    public static final String DEFINITION = "definition";

    private CreationSelector m_parent;

    public GlossaryItemCreate(ItemSelectionModel itemModel,
                              CreationSelector parent) {

        super(itemModel, parent);
        m_parent = parent;
    }

    protected void addWidgets() {
        super.addWidgets();
        
        GlossaryItemWidgetBuilder builder = new GlossaryItemWidgetBuilder();
        add(builder.makeDefinitionLabel());
        add(builder.makeDefinitionArea());
    }

    public void process(FormSectionEvent e) throws FormProcessException {

        FormData data = e.getFormData();
        PageState state = e.getPageState();

        // Try to get the content section from the state parameter
        Folder f = m_parent.getFolder(state);
        GlossaryItem item = (GlossaryItem)createContentPage(state);
        item.setLanguage((String) data.get(LANGUAGE));                        
        item.setName((String)data.get(NAME));
        item.setTitle((String)data.get(TITLE));
        item.setDefinition((String)data.get(DEFINITION));
        item.save();

        final ContentBundle bundle = new ContentBundle(item);
        bundle.setParent(f);
        bundle.setContentSection(m_parent.getContentSection(state));
        bundle.save();
        
        // Apply default workflow
        getWorkflowSection().applyWorkflow(state, item);
        
        // Start edititng the component right away
        m_parent.editItem(state, item);
    }
}
