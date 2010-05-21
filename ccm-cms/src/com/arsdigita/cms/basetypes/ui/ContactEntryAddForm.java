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
package com.arsdigita.cms.basetypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.basetypes.Contact;
import com.arsdigita.cms.basetypes.ContactEntry;
import com.arsdigita.cms.basetypes.util.BasetypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.bebop.parameters.StringParameter;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * @author Sören Bernstein (quasimodo) quasi@barkhof.uni-bremen.de
 */
public class ContactEntryAddForm extends BasicItemForm {
    private static final Logger s_log = Logger.getLogger(ContactEntryAddForm.class);
    
    private ItemSelectionModel m_itemModel;
    
    /** Creates a new instance of CategoryLocalizationAddForm */
    public ContactEntryAddForm(ItemSelectionModel itemModel) {
        
        super("ContactEntryAddForm",itemModel);
        m_itemModel = itemModel;
        
    }
    
    @Override
    protected void addWidgets() {
        
        // Key field
        add(new Label(BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.key")));
        ParameterModel contactEntryKeyParam = new StringParameter(ContactEntry.KEY);
        SingleSelect contactEntryKey = new SingleSelect(contactEntryKeyParam);
        contactEntryKey.addValidationListener(new NotNullValidationListener());
        contactEntryKey.addOption(new Option("", new Label((String)BasetypesGlobalizationUtil.globalize("cms.ui.select_one").localize())));
        
        // Add the Options to the SingleSelect widget
        StringTokenizer keyList = Contact.getConfig().getContactEntryKeys();
        while(keyList.hasMoreElements()) {
            String currentKey = keyList.nextToken();
            contactEntryKey.addOption(new Option(currentKey, ((String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.key." + currentKey).localize())));
        }
        
        add(contactEntryKey);
        
        // Value field
        add(new Label(BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.value")));
        ParameterModel contactEntryValueParam = new StringParameter(ContactEntry.VALUE);
        TextField contactEntryValue = new TextField(contactEntryValueParam);
        contactEntryValue.addValidationListener(new NotNullValidationListener());
        add(contactEntryValue);
        
        // Description field, only for internal usage
        add(new Label(BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.description")));
        ParameterModel contactEntryDescriptionParam = new StringParameter(ContactEntry.DESCRIPTION);
        TextField contactEntryDescription = new TextField(contactEntryDescriptionParam);
        add(contactEntryDescription);
        
    }
    
    public void init(FormSectionEvent fse) {
        
    }
    
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        Contact contact = (Contact)m_itemModel.getSelectedObject(fse.getPageState());
        
        // save only if save button was pressed
        if (contact != null
                && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
            
            ContactEntry contactEntry = new ContactEntry(contact,
                    (String)data.get(ContactEntry.KEY),
                    (String)data.get(ContactEntry.VALUE),
                    (String)data.get(ContactEntry.DESCRIPTION));
            
            contact.addContactEntry(contactEntry);
        }
    }
}
