/*
 * HealthCareFacilityAddressPropertiesStep.java
 *
 * Created on 4. Juli 2009, 15:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.contenttypes.HealthCareFacility;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.contenttypes.util.HealthCareFacilityGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author quasi
 */
public class HealthCareFacilityAddressPropertiesStep extends SimpleEditStep {

    public static final String ADD_ADDRESS_SHEET_NAME = "addAddress";
    public static final String EDIT_ADDRESS_SHEET_NAME = "editAddress";
    public static final String CHANGE_ADDRESS_SHEET_NAME = "changeAddress";
    public static final String DELETE_ADDRESS_SHEET_NAME = "deleteAddress";

    /**
     * Creates a new instance of HealthCareFacilityAddressPropertiesStep
     */
    public HealthCareFacilityAddressPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    public HealthCareFacilityAddressPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent, String prefix) {
        super(itemModel, parent, prefix);

//        HealthCareFacility healthCareFacility = (HealthCareFacility)itemModel.getSelectedObject(state);

//XXX
//        if(/*healthCareFacility.getAddress() == null*/ true) {
        BasicPageForm attachAddressSheet = new HealthCareFacilityAttachAddressPropertyForm(itemModel, this);
        add(ADD_ADDRESS_SHEET_NAME, (String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.attach_address").localize(), new WorkflowLockedComponentAccess(attachAddressSheet, itemModel), attachAddressSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
//            setDisplayComponent(getEmptyBaseAddressPropertySheet(itemModel));

//        } else {

        // editAddress
        BasicPageForm editAddressSheet = new HealthCareFacilityEditAddressPropertyForm(itemModel, this);
        add(EDIT_ADDRESS_SHEET_NAME, (String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.edit_address").localize(), new WorkflowLockedComponentAccess(editAddressSheet, itemModel), editAddressSheet.getSaveCancelSection().getCancelButton());

//            BasicPageForm attachAddressSheet = new HealthCareFacilityAttachAddressPropertyForm(itemModel, this);
//            add(CHANGE_ADDRESS_SHEET_NAME, (String)HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.reattach_address").localize(), new WorkflowLockedComponentAccess(attachAddressSheet, itemModel), attachAddressSheet.getSaveCancelSection().getCancelButton());

        BasicPageForm deleteAddressSheet = new HealthCareFacilityDeleteAddressForm(itemModel, this);
        add(DELETE_ADDRESS_SHEET_NAME, (String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.delete_address").localize(), new WorkflowLockedComponentAccess(deleteAddressSheet, itemModel), deleteAddressSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        setDisplayComponent(getAddressPropertySheet(itemModel));
//        }

    }

    public static Component getAddressPropertySheet(ItemSelectionModel itemModel) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.address.address").localize(), "address." + com.arsdigita.cms.contenttypes.GenericAddress.ADDRESS);
        if (!HealthCareFacility.getConfig().getHideAddressPostalCode()) {
            sheet.add((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.address.postal_code").localize(), "address." + com.arsdigita.cms.contenttypes.GenericAddress.POSTAL_CODE);
        }
        sheet.add((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.address.city").localize(), "address." + com.arsdigita.cms.contenttypes.GenericAddress.CITY);
        if (!HealthCareFacility.getConfig().getHideAddressState()) {
            sheet.add((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.address.state").localize(), "address." + com.arsdigita.cms.contenttypes.GenericAddress.STATE);
        }

        if (!HealthCareFacility.getConfig().getHideAddressCountry()) {
            sheet.add((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.address.iso_country_code").localize(),
                    "address." + com.arsdigita.cms.contenttypes.GenericAddress.ISO_COUNTRY_CODE,
                    new DomainObjectPropertySheet.AttributeFormatter() {

                        public String format(DomainObject item,
                                String attribute,
                                PageState state) {
                            com.arsdigita.cms.contenttypes.GenericAddress address = ((HealthCareFacility) item).getAddress();
                            if (address != null && address.getIsoCountryCode() != null) {
                                return com.arsdigita.cms.contenttypes.GenericAddress.getCountryNameFromIsoCode(address.getIsoCountryCode());
                            } else {
                                return (String) ContenttypesGlobalizationUtil.globalize("cms.ui.unknown").localize();
                            }
                        }
                    });
        }

        return sheet;

    }

    public static Component getEmptyAddressPropertySheet(ItemSelectionModel itemModel) {
        return new Label(((String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.emptyAddress").localize()));
    }
}
