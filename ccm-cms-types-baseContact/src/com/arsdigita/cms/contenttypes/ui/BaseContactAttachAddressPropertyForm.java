/*
 * BaseContactEditAddressPropertyForm.java
 *
 * Created on 8. Juli 2009, 10:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes.ui;

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
import com.arsdigita.cms.contenttypes.BaseAddress;
import com.arsdigita.cms.contenttypes.BaseContact;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ContentType;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class BaseContactAttachAddressPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {
    
    private static final Logger logger = Logger.getLogger(BaseContactPropertyForm.class);

    private BaseContactAddressPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "baseContactAddress";

    /**
     * ID of the form
     */
    public static final String ID = "BaseContactAttachAddress";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public BaseContactAttachAddressPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of BaseContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public BaseContactAttachAddressPropertyForm(ItemSelectionModel itemModel, BaseContactAddressPropertiesStep step) {
        super(ID, itemModel);
        addSubmissionListener(this);

        addSaveCancelSection();

        addInitListener(this);
        addSubmissionListener(this);
        
    }

    @Override
    public void addWidgets() {
        add(new Label("BaseContact.address"));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType("com.arsdigita.cms.contenttypes.BaseAddress"));
        add(this.m_itemSearch);       
    }
    
    public void init(FormSectionEvent fse) {
	FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        BaseContact baseContact = (BaseContact)getItemSelectionModel().getSelectedObject(state);
       
        setVisible(state, true);

        if (baseContact != null) {
            data.put(ITEM_SEARCH, baseContact.getAddress());
        }
    }

    public void process(FormSectionEvent fse) {
	FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        BaseContact baseContact = (BaseContact)getItemSelectionModel().getSelectedObject(state);

        if (!this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            baseContact.setAddress((BaseAddress)data.get(ITEM_SEARCH));
        }
        init(fse);
    }


    /**
     * Creates the section with the save and the cancel button.
     */
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    BaseContact baseContact = (BaseContact)getItemSelectionModel().getSelectedObject(e.getPageState());
                    Submit target = (Submit) e.getTarget();

                    if (baseContact.getAddress() != null) {
                        target.setButtonLabel("Change");
                    } else {
                        target.setButtonLabel("Add");
                    }
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        if (e.getFormData().get(ITEM_SEARCH) == null) {
            throw new FormProcessException("BaseAddress selection is required");
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(e.getPageState())) {
            init(e);
            throw new FormProcessException("cancelled");
        }
    }
}