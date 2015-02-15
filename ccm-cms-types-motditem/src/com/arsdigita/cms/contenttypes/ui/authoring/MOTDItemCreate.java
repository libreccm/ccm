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

import com.arsdigita.cms.contenttypes.MOTDItem;
import com.arsdigita.cms.contenttypes.util.MOTDGlobalizationUtil;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;

import java.math.BigDecimal;
import java.lang.Exception;
import org.apache.log4j.Logger;

/*
 * A page that will create a new MOTDItem.
 * 
 * @author Aingaran Pillai
 * @see com.arsdigita.cms.contenttypes.MOTDItem
 * @version $Revision: #5 $
 */
public class MOTDItemCreate extends PageCreate {

    private static final Logger s_log =
        Logger.getLogger(MOTDItemCreate.class);

    public static final String MESSAGE = "message";

    private CreationSelector m_parent;

    public MOTDItemCreate(ItemSelectionModel itemModel,
                          CreationSelector parent) {

        super(itemModel, parent);
        m_parent = parent;
    }

    protected void addWidgets() {
        
        super.addWidgets();
        
        TextArea msg = new TextArea(MESSAGE);
        msg.addValidationListener(new NotNullValidationListener());
        msg.addValidationListener(new StringLengthValidationListener
            (MOTDItem.MESSAGE_LENGTH));
        msg.setCols(40);
        msg.setRows(5);

        add(new Label(MOTDGlobalizationUtil
                      .globalize("cms.contenttypes.ui.motd.message")));
        add(msg);
    }

    public void process(FormSectionEvent e) throws FormProcessException {

        FormData data = e.getFormData();
        PageState state = e.getPageState();
        ItemSelectionModel m = getItemSelectionModel();

        // Create new item
        try {
            BigDecimal id =  (BigDecimal)m.getSelectedKey(state);

            // Try to get the content section from the state parameter
            Folder f = m_parent.getFolder(state);
            ContentSection sec = m_parent.getContentSection(state);
            MOTDItem item = (MOTDItem)createContentPage(state);
            item.setLanguage((String) data.get(LANGUAGE));      
            item.setName((String)data.get(NAME));
            item.setTitle((String)data.get(TITLE));
            item.setMessage((String)data.get(MESSAGE));
            item.save();
            
            final ContentBundle bundle = new ContentBundle(item);
            bundle.setParent(f);
            bundle.setContentSection(m_parent.getContentSection(state));
            bundle.save();
            
            // Apply default workflow
            getWorkflowSection().applyWorkflow(state, item);
      
            // Start edititng the component right away
            m_parent.editItem(state, item);

        } catch (Exception ex) {
            s_log.error(ex);
            throw new FormProcessException(ex);
        }
    }

}








