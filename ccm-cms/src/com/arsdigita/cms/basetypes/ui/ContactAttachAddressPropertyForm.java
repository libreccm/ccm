/*
 * ContactEditAddressPropertyForm.java
 *
 * Created on 8. Juli 2009, 10:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.basetypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.basetypes.Address;
import com.arsdigita.cms.basetypes.Contact;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.basetypes.util.BasetypesGlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class ContactAttachAddressPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {
    
    private static final Logger logger = Logger.getLogger(ContactPropertyForm.class);

    private ContactAddressPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "contactAddress";

    /**
     * ID of the form
     */
    public static final String ID = "ContactAttachAddress";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public ContactAttachAddressPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of ContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public ContactAttachAddressPropertyForm(ItemSelectionModel itemModel, ContactAddressPropertiesStep step) {
        super(ID, itemModel);
        addSubmissionListener(this);

        addSaveCancelSection();

        addInitListener(this);
        addSubmissionListener(this);
        
    }

    @Override
    public void addWidgets() {
        add(new Label((String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.select_address").localize()));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType("com.arsdigita.cms.basetypes.BaseAddress"));
        add(this.m_itemSearch);       
    }
    
    public void init(FormSectionEvent fse) {
	FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        Contact contact = (Contact)getItemSelectionModel().getSelectedObject(state);
       
        setVisible(state, true);

        if (contact != null) {
            data.put(ITEM_SEARCH, contact.getAddress());
        }
    }

    public void process(FormSectionEvent fse) {
	FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        Contact contact = (Contact)getItemSelectionModel().getSelectedObject(state);

        if (!this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            contact.setAddress((Address)data.get(ITEM_SEARCH));
        }
        init(fse);
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

                    if (contact.getAddress() != null) {
                        target.setButtonLabel((String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.select_address.change").localize());
                    } else {
                        target.setButtonLabel((String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.select_address.add").localize());
                    }
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }

    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        if (e.getFormData().get(ITEM_SEARCH) == null) {
            throw new FormProcessException((String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.select_address.wrong_type").localize());
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(e.getPageState())) {
            init(e);
            throw new FormProcessException((String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.select_address.cancelled").localize());
        }
    }
}