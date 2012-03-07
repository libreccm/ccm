/*
 * GenericContactAddressPropertiesStep.java
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
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author quasi
 */
public class GenericContactAddressPropertiesStep extends SimpleEditStep {

    public static final String ADD_ADDRESS_SHEET_NAME = "addAddress";
    public static final String EDIT_ADDRESS_SHEET_NAME = "editAddress";
    public static final String CHANGE_ADDRESS_SHEET_NAME = "changeAddress";
    public static final String DELETE_ADDRESS_SHEET_NAME = "deleteAddress";
    private ItemSelectionModel itemModel;

    /** Creates a new instance of GenericContactAddressPropertiesStep */
    public GenericContactAddressPropertiesStep(ItemSelectionModel itemModel,
                                               AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    public GenericContactAddressPropertiesStep(ItemSelectionModel itemModel,
                                               AuthoringKitWizard parent,
                                               String prefix) {
        super(itemModel, parent, prefix);

        this.itemModel = itemModel;

        BasicPageForm attachAddressSheet =
                      new GenericContactAttachAddressPropertyForm(itemModel,
                                                                  this);
        BasicPageForm reattachAddressSheet =
                      new GenericContactAttachAddressPropertyForm(itemModel,
                                                                  this);
        BasicPageForm editAddressSheet =
                      new GenericContactEditAddressPropertyForm(itemModel, this);
        BasicPageForm deleteAddressSheet = new GenericContactDeleteAddressForm(
                itemModel, this);

        add(ADD_ADDRESS_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.contact.attach_address").localize(),
            new AttachAddressWorkflowLockedComponentAccess(attachAddressSheet,
                                                           itemModel),
            attachAddressSheet.getSaveCancelSection().getCancelButton());
        add(CHANGE_ADDRESS_SHEET_NAME, (String) ContenttypesGlobalizationUtil.
                globalize("cms.contenttypes.ui.contact.reattach_address").
                localize(), new EditAddressWorkflowLockedComponentAccess(
                reattachAddressSheet, itemModel), reattachAddressSheet.
                getSaveCancelSection().getCancelButton());
        /*add(EDIT_ADDRESS_SHEET_NAME, (String) ContenttypesGlobalizationUtil.
                globalize("cms.contenttypes.ui.contact.edit_address").localize(),
            new EditAddressWorkflowLockedComponentAccess(editAddressSheet,
                                                         itemModel),
            editAddressSheet.getSaveCancelSection().getCancelButton());*/
        add(DELETE_ADDRESS_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.contact.delete_address").localize(),
            new EditAddressWorkflowLockedComponentAccess(deleteAddressSheet,
                                                         itemModel),
            deleteAddressSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        setDisplayComponent(getAddressPropertySheet(itemModel));

    }

    public static Component getAddressPropertySheet(ItemSelectionModel itemModel) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.address.address").localize(),
                  "address." + GenericAddress.ADDRESS);
        if (!GenericContact.getConfig().getHideAddressPostalCode()) {
            sheet.add((String) ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.address.postal_code").localize(),
                      "address." + GenericAddress.POSTAL_CODE);
        }
        sheet.add((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.address.city").localize(),
                  "address." + GenericAddress.CITY);
        if (!GenericContact.getConfig().getHideAddressState()) {
            sheet.add((String) ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.address.state").localize(),
                      "address." + GenericAddress.STATE);
        }

        if (!GenericContact.getConfig().getHideAddressCountry()) {
            sheet.add((String) ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.address.iso_country_code").localize(),
                      "address." + GenericAddress.ISO_COUNTRY_CODE,
                      new DomainObjectPropertySheet.AttributeFormatter() {

                @Override
                public String format(DomainObject item,
                                     String attribute,
                                     PageState state) {
                    GenericAddress Address =
                                   ((GenericContact) item).getAddress();
                    if (Address != null && Address.getIsoCountryCode() != null) {
                        return GenericAddress.getCountryNameFromIsoCode(Address.
                                getIsoCountryCode());
                    } else {
                        return (String) ContenttypesGlobalizationUtil.globalize(
                                "cms.ui.unknown").localize();
                    }
                }
            });
        }

        return sheet;

    }

    public static Component getEmptyBaseAddressPropertySheet(
            ItemSelectionModel itemModel) {
        return new Label(
                ((String) ContenttypesGlobalizationUtil.globalize(
                 "cms.contenttypes.ui.contact.emptyAddress").localize()));
    }

    private class EditAddressWorkflowLockedComponentAccess extends WorkflowLockedComponentAccess {

        public EditAddressWorkflowLockedComponentAccess(Component c,
                                                        ItemSelectionModel i) {
            super(c, i);
        }

        @Override
        public boolean isVisible(PageState state) {
            GenericContact contact = (GenericContact) itemModel.
                    getSelectedObject(state);

            return contact.hasAddress();
        }
    }

    private class AttachAddressWorkflowLockedComponentAccess extends WorkflowLockedComponentAccess {

        public AttachAddressWorkflowLockedComponentAccess(Component c,
                                                          ItemSelectionModel i) {
            super(c, i);
        }

        @Override
        public boolean isVisible(PageState state) {
            GenericContact contact = (GenericContact) itemModel.
                    getSelectedObject(state);

            return !contact.hasAddress();
        }
    }
}
