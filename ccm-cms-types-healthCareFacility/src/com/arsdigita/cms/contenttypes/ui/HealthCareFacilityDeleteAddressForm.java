/*
 * HealthCareFacilityDeleteAddressForm.java
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
import com.arsdigita.cms.contenttypes.HealthCareFacility;
import com.arsdigita.cms.contenttypes.util.HealthCareFacilityGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.util.UncheckedWrapperException;

/**
 *
 * @author quasi
 */
public class HealthCareFacilityDeleteAddressForm extends BasicPageForm implements FormProcessListener {
    
    /**
     * ID of the form
     */
    public static final String ID = "HealthCareFacilityDeleteAddress";

    HealthCareFacilityDeleteAddressForm(ItemSelectionModel itemModel, HealthCareFacilityAddressPropertiesStep step) {
        super(ID, itemModel);
        addSaveCancelSection();
    }

    public void init(FormSectionEvent fse) {
       
    }
    
    public void addWidgets() {
        add(new Label((String)HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.delete_address.label").localize()));
    }
    
    /**
     * Creates the section with the save and the cancel button.
     */
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    HealthCareFacility healthCareFacility = (HealthCareFacility)getItemSelectionModel().getSelectedObject(e.getPageState());
                    Submit target = (Submit) e.getTarget();
                        target.setButtonLabel((String)HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.delete_address.button_label").localize());
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }
    
    public final void process(final FormSectionEvent fse) throws FormProcessException {
        
        final PageState state = fse.getPageState();
        final HealthCareFacility healthCareFacility = (HealthCareFacility)getItemSelectionModel().getSelectedObject(state);
        
        if (healthCareFacility != null && healthCareFacility.getAddress() != null) {
            healthCareFacility.unsetAddress();
        }
    }

}
