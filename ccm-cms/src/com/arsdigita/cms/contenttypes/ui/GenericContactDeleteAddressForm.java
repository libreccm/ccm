/*
 * GenericContactDeleteAddressForm.java
 *
 * Created on 17. Juli 2009, 10:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.util.UncheckedWrapperException;

/**
 *
 * @author quasi
 */
public class GenericContactDeleteAddressForm extends BasicPageForm implements FormProcessListener {

    /**
     * ID of the form
     */
    public static final String ID = "BaseContactDeleteAddress";

    GenericContactDeleteAddressForm(ItemSelectionModel itemModel, GenericContactAddressPropertiesStep step) {
        super(ID, itemModel);
        addSaveCancelSection();
        addProcessListener(this);
    }

    public void init(FormSectionEvent fse) {
    }

    @Override
    public void addWidgets() {
        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.contact.delete_address.label")));
    }

    /**
     * Creates the section with the save and the cancel button.
     */
    @Override
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {

                @Override
                public void prepare(PrintEvent e) {
                    GenericContact contact = (GenericContact) 
                                             getItemSelectionModel()
                                             .getSelectedObject(e.getPageState());
                    Submit target = (Submit) e.getTarget();
                    target.setButtonLabel(ContenttypesGlobalizationUtil
                        .globalize(
                        "cms.contenttypes.ui.contact.delete_address.button_label"));
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }

    @Override
    public final void process(final FormSectionEvent fse) throws FormProcessException {

        final PageState state = fse.getPageState();
        final GenericContact contact = (GenericContact) getItemSelectionModel()
                                       .getSelectedObject(state);

        if (contact != null && contact.getAddress() != null) {
            contact.unsetAddress();
        }
    }
}
