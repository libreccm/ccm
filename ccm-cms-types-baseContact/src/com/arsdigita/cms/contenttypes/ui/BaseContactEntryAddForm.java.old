/*
 * Copyright (C) 2008 Sören Bernstein All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.BaseContact;
import com.arsdigita.cms.contenttypes.BaseContactEntry;
import com.arsdigita.cms.contenttypes.util.BaseContactGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.bebop.parameters.StringParameter;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Generates a form for creating new localisations for the given category.
 *
 * This class is part of the admin GUI of CCM and extends the standard form
 * in order to present forms for managing the multi-language categories.
 *
 * @author Sören Bernstein (quasimodo) quasi@zes.uni-bremen.de
 */
public class BaseContactEntryAddForm extends BasicItemForm {
    private static final Logger s_log = Logger.getLogger(BaseContactEntryAddForm.class);
    
    private ItemSelectionModel m_itemModel;
    
    /** Creates a new instance of CategoryLocalizationAddForm */
    public BaseContactEntryAddForm(ItemSelectionModel itemModel) {
        
        super("BaseContactEntryAddForm",itemModel);
        m_itemModel = itemModel;
        
    }
    
    protected void addWidgets() {
        
        // Key field
        add(new Label(BaseContactGlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.contactEntry.key")));
        ParameterModel contactEntryKeyParam = new StringParameter(BaseContactEntry.KEY);
        SingleSelect contactEntryKey = new SingleSelect(contactEntryKeyParam);
        contactEntryKey.addValidationListener(new NotNullValidationListener());
        contactEntryKey.addOption(new Option("", new Label((String)BaseContactGlobalizationUtil.globalize("cms.ui.select_one").localize())));
        
        // Add the Options to the SingleSelect widget
        StringTokenizer keyList = BaseContact.getConfig().getContactEntryKeys();
        while(keyList.hasMoreElements()) {
            String currentKey = keyList.nextToken();
            contactEntryKey.addOption(new Option(currentKey, ((String)BaseContactGlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.contactEntry.key." + currentKey).localize())));
        }
        
        add(contactEntryKey);
        
        // Value field
        add(new Label(BaseContactGlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.contactEntry.value")));
        ParameterModel contactEntryValueParam = new StringParameter(BaseContactEntry.VALUE);
        TextField contactEntryValue = new TextField(contactEntryValueParam);
        contactEntryValue.addValidationListener(new NotNullValidationListener());
        add(contactEntryValue);
        
        // Description field, only for internal usage
        add(new Label(BaseContactGlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.contactEntry.description")));
        ParameterModel contactEntryDescriptionParam = new StringParameter(BaseContactEntry.DESCRIPTION);
        TextField contactEntryDescription = new TextField(contactEntryDescriptionParam);
        add(contactEntryDescription);
        
    }
    
    public void init(FormSectionEvent fse) {
        
    }
    
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        BaseContact baseContact = (BaseContact)m_itemModel.getSelectedObject(fse.getPageState());
        
        // save only if save button was pressed
        if (baseContact != null
                && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
            
            BaseContactEntry contactEntry = new BaseContactEntry(baseContact,
                    (String)data.get(BaseContactEntry.KEY),
                    (String)data.get(BaseContactEntry.VALUE),
                    (String)data.get(BaseContactEntry.DESCRIPTION));
            
            baseContact.addContactEntry(contactEntry);
        }
    }
}
