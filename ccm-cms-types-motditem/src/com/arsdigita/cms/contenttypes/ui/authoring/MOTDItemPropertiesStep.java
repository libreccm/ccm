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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;

/*
 * Form to edit MOTDItem.
 *
 * @author Aingaran Pillai
 * @see com.arsdigita.cms.contenttypes.MOTDItem
 * @version $Revision: #6 $
 */
public class MOTDItemPropertiesStep extends SimpleEditStep {

    public static String EDIT_SHEET_NAME = "edit";

    public MOTDItemPropertiesStep(ItemSelectionModel itemModel,
                                  AuthoringKitWizard parent) {

        super(itemModel, parent);
        
        BasicPageForm editSheet;
        editSheet = new MOTDItemPropertyForm(itemModel);
        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            editSheet,
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getMOTDDomainObjectPropertySheet(itemModel));
    }

    public static Component getMOTDDomainObjectPropertySheet
        (ItemSelectionModel itemModel) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);
        sheet.add(GlobalizationUtil
                  .globalize("cms.contenttypes.ui.title"), 
                  MOTDItem.TITLE);
        sheet.add(GlobalizationUtil
                  .globalize("cms.contenttypes.ui.name"), 
                  MOTDItem.NAME);
        sheet.add(MOTDGlobalizationUtil
                  .globalize("cms.contenttypes.ui.motd.message"), 
                  MOTDItem.MESSAGE);

        return sheet;
    }

    private class MOTDItemPropertyForm extends BasicPageForm
        implements FormProcessListener, FormInitListener, FormSubmissionListener {

        public static final String ID = "motd_item_edit";
        public static final String MESSAGE = "message";

        public MOTDItemPropertyForm(ItemSelectionModel itemModel) {
            super(ID, itemModel);
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

        public void init(FormSectionEvent e) throws FormProcessException {
            
            FormData data = e.getFormData();
            MOTDItem item = (MOTDItem) super.initBasicWidgets(e);
            data.put(MESSAGE, item.getMessage());
        }

        public void submitted(FormSectionEvent e) throws FormProcessException {
            
            if (getSaveCancelSection().getSaveButton()
                .isSelected(e.getPageState())) {
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "cms.ui.submission_cancelled"));
            }
        }

        public void process(FormSectionEvent e) throws FormProcessException {

            FormData data = e.getFormData();
            MOTDItem item = (MOTDItem) super.processBasicWidgets(e);

            if (item != null) {
                item.setMessage((String) data.get(MESSAGE));
                item.save();
            }
        }
    }

}



