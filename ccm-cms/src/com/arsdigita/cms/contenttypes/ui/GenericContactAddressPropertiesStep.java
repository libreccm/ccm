/*
 * GenericContactAddressPropertiesStep.java
 *
 * Created on 4. Juli 2009, 15:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author quasi
 */
public class GenericContactAddressPropertiesStep extends SimpleEditStep {

    public static final String ADD_ADDRESS_SHEET_NAME = "addAddress";
    public static final String EDIT_ADDRESS_SHEET_NAME = "editAddress";
    public static final String CHANGE_ADDRESS_SHEET_NAME = "changeAddress";
    public static final String DELETE_ADDRESS_SHEET_NAME = "deleteAddress";
    //private ItemSelectionModel itemModel;
    private final WorkflowLockedComponentAccess addAddress;

    /**
     * Creates a new instance of GenericContactAddressPropertiesStep
     */
    public GenericContactAddressPropertiesStep(final ItemSelectionModel itemModel,
                                               final AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    public GenericContactAddressPropertiesStep(final ItemSelectionModel itemModel,
                                               final AuthoringKitWizard parent,
                                               final String prefix) {
        super(itemModel, parent, prefix);

        final BasicPageForm addAddressSheet = new GenericContactAttachAddressPropertyForm(itemModel,
                                                                                          this);
        addAddress = new WorkflowLockedComponentAccess(addAddressSheet, itemModel);
        add(ADD_ADDRESS_SHEET_NAME, 
            ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.attach_address"),
            addAddress,
            addAddressSheet.getSaveCancelSection().getCancelButton());
            
        
        setDisplayComponent(getAddressPropertySheet(itemModel));

//        this.itemModel = itemModel;
//
//        BasicPageForm attachAddressSheet = new GenericContactAttachAddressPropertyForm(itemModel,
//                                                                                       this);
//        BasicPageForm reattachAddressSheet = new GenericContactAttachAddressPropertyForm(itemModel,
//                                                                                         this);
//        BasicPageForm editAddressSheet = new GenericContactEditAddressPropertyForm(itemModel, this);
//        BasicPageForm deleteAddressSheet = new GenericContactDeleteAddressForm(
//            itemModel, this);
//
//        add(ADD_ADDRESS_SHEET_NAME,
//            ContenttypesGlobalizationUtil.globalize(
//            "cms.contenttypes.ui.contact.attach_address"),
//            new AttachAddressWorkflowLockedComponentAccess(attachAddressSheet,
//                                                           itemModel),
//            attachAddressSheet.getSaveCancelSection().getCancelButton());
//        add(CHANGE_ADDRESS_SHEET_NAME,
//            ContenttypesGlobalizationUtil.globalize(
//            "cms.contenttypes.ui.contact.reattach_address"),
//            new EditAddressWorkflowLockedComponentAccess(reattachAddressSheet,
//                                                         itemModel),
//            reattachAddressSheet.getSaveCancelSection().getCancelButton());
//        
//        add(DELETE_ADDRESS_SHEET_NAME,
//            ContenttypesGlobalizationUtil.globalize(
//            "cms.contenttypes.ui.contact.delete_address"),
//            new EditAddressWorkflowLockedComponentAccess(deleteAddressSheet,
//                                                         itemModel),
//            deleteAddressSheet.getSaveCancelSection().getCancelButton());
//
//        /* Set the displayComponent for this step */
//        setDisplayComponent(getAddressPropertySheet(itemModel));
    }

    public static Component getAddressPropertySheet(final ItemSelectionModel itemModel) {
        final GenericContactAddressSheet sheet = new GenericContactAddressSheet(itemModel);
        return sheet;
    }
    
    /**
     *
     * @param itemModel
     *
     * @return
     */
//    public static Component getAddressPropertySheet(ItemSelectionModel itemModel) {
//
//        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
//            itemModel);
//
//        sheet.add(ContenttypesGlobalizationUtil.globalize(
//            "cms.contenttypes.ui.address.address"),
//                  "address." + GenericAddress.ADDRESS,
//                  new DomainObjectPropertySheet.AttributeFormatter() {
//
//                      @Override
//                      public String format(final DomainObject obj,
//                                           final String attribute,
//                                           final PageState state) {
//                          final GenericAddress address = ((GenericContact) obj).getAddress();
//                          if ((address == null) || (address.getAddress() == null)) {
//                              return (String) GlobalizationUtil.globalize(
//                                  "cms.ui.unknown").localize();
//                          } else {
//                              return address.getAddress();
//
//                          }
//                      }
//
//                  });
//        if (!GenericContact.getConfig().getHideAddressPostalCode()) {
//            sheet.add(ContenttypesGlobalizationUtil.globalize(
//                "cms.contenttypes.ui.address.postal_code"),
//                      "address." + GenericAddress.POSTAL_CODE,
//                      new DomainObjectPropertySheet.AttributeFormatter() {
//
//                          @Override
//                          public String format(final DomainObject obj,
//                                               final String attribute,
//                                               final PageState state) {
//                              final GenericAddress address = ((GenericContact) obj).getAddress();
//                              if ((address == null) || (address.getPostalCode() == null)) {
//                                  return (String) GlobalizationUtil.globalize(
//                                      "cms.ui.unknown").localize();
//                              } else {
//                                  return address.getPostalCode();
//
//                              }
//
//                          }
//
//                      });
//        }
//        sheet.add(ContenttypesGlobalizationUtil.globalize(
//            "cms.contenttypes.ui.address.city"),
//                  "address." + GenericAddress.CITY,
//                  new DomainObjectPropertySheet.AttributeFormatter() {
//
//                      @Override
//                      public String format(final DomainObject obj,
//                                           final String attribute,
//                                           final PageState state) {
//                          final GenericAddress address = ((GenericContact) obj).getAddress();
//                          if ((address == null) || (address.getCity() == null)) {
//                              return (String) GlobalizationUtil.globalize(
//                                  "cms.ui.unknown").localize();
//                          } else {
//                              return address.getCity();
//
//                          }
//
//                      }
//
//                  });
//        if (!GenericContact.getConfig().getHideAddressState()) {
//            sheet.add(ContenttypesGlobalizationUtil.globalize(
//                "cms.contenttypes.ui.address.state"),
//                      "address." + GenericAddress.STATE,
//                      new DomainObjectPropertySheet.AttributeFormatter() {
//
//                          @Override
//                          public String format(final DomainObject obj,
//                                               final String attribute,
//                                               final PageState state) {
//                              final GenericAddress address = ((GenericContact) obj).getAddress();
//                              if ((address == null) || (address.getState() == null)) {
//                                  return (String) GlobalizationUtil.globalize(
//                                      "cms.ui.unknown").localize();
//                              } else {
//                                  return address.getState();
//
//                              }
//
//                          }
//
//                      });
//        }
//
//        if (!GenericContact.getConfig().getHideAddressCountry()) {
//            sheet.add(ContenttypesGlobalizationUtil.globalize(
//                "cms.contenttypes.ui.address.iso_country_code"),
//                      "address." + GenericAddress.ISO_COUNTRY_CODE,
//                      new DomainObjectPropertySheet.AttributeFormatter() {
//
//                          @Override
//                          public String format(DomainObject item,
//                                               String attribute,
//                                               PageState state) {
//                              GenericAddress address = ((GenericContact) item).getAddress();
//                              if (address != null && address.getIsoCountryCode() != null) {
//                                  return GenericAddress.getCountryNameFromIsoCode(address.
//                                      getIsoCountryCode());
//                              } else {
//                                  return (String) GlobalizationUtil.globalize(
//                                      "cms.ui.unknown").localize();
//                              }
//                          }
//
//                      });
//        }
//
//        return sheet;
//
//    }
//
//    public static Component getEmptyBaseAddressPropertySheet(
//        ItemSelectionModel itemModel) {
//        return new Label(
//            (ContenttypesGlobalizationUtil.globalize(
//             "cms.contenttypes.ui.contact.emptyAddress")));
//    }
//
//    private class EditAddressWorkflowLockedComponentAccess
//        extends WorkflowLockedComponentAccess {
//
//        public EditAddressWorkflowLockedComponentAccess(Component c,
//                                                        ItemSelectionModel i) {
//            super(c, i);
//        }
//
//        @Override
//        public boolean isVisible(PageState state) {
//            GenericContact contact = (GenericContact) itemModel.
//                getSelectedObject(state);
//
//            return contact.hasAddress();
//        }
//
//    }
//
//    private class AttachAddressWorkflowLockedComponentAccess extends WorkflowLockedComponentAccess {
//
//        public AttachAddressWorkflowLockedComponentAccess(Component c,
//                                                          ItemSelectionModel i) {
//            super(c, i);
//        }
//
//        @Override
//        public boolean isVisible(PageState state) {
//            GenericContact contact = (GenericContact) itemModel.
//                getSelectedObject(state);
//
//            return !contact.hasAddress();
//        }
//
//    }
}
