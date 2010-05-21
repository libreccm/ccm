/*
 * ContactDeletePersonForm.java
 *
 * Created on 17. Juli 2009, 10:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.basetypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.basetypes.Contact;
import com.arsdigita.cms.basetypes.util.BasetypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.util.UncheckedWrapperException;

/**
 *
 * @author quasi
 */
public class ContactDeletePersonForm extends BasicPageForm implements FormProcessListener {
    
    /**
     * ID of the form
     */
    public static final String ID = "ContactDeletePerson";

    ContactDeletePersonForm(ItemSelectionModel itemModel, ContactPersonPropertiesStep step) {
        super(ID, itemModel);
        addSaveCancelSection();
    }

    public void init(FormSectionEvent fse) {
       
    }
    
    @Override
    public void addWidgets() {
        add(new Label((String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.delete_person.label").localize()));
    }
    
    /**
     * Creates the section with the save and the cancel button.
     */
    @Override
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Contact contact = (Contact)getItemSelectionModel().getSelectedObject(e.getPageState());
                    Submit target = (Submit) e.getTarget();
                        target.setButtonLabel((String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.delete_person").localize());
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }
    
    public final void process(final FormSectionEvent fse) throws FormProcessException {
        
        final PageState state = fse.getPageState();
        final Contact contact = (Contact)getItemSelectionModel().getSelectedObject(state);
        
        if (contact != null && contact.getPerson() != null) {
            contact.unsetPerson();
        }
    }

}
