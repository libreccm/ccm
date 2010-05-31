/*
 * GenericContactPersonPropertiesStep.java
 *
 * Created on 4. Juli 2009, 15:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;

/**
 *
 * @author quasi
 */
public class GenericContactPersonPropertiesStep extends SimpleEditStep {
    
    public static final String ADD_PERSON_SHEET_NAME = "addPerson";
    public static final String EDIT_PERSON_SHEET_NAME = "editPerson";
    public static final String CHANGE_PERSON_SHEET_NAME = "changePerson";
    public static final String DELETE_PERSON_SHEET_NAME = "deletePerson";

    /**
     * Creates a new instance of GenericContactPersonPropertiesStep
     */
    public GenericContactPersonPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }
    
    public GenericContactPersonPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent, String prefix) {
        super(itemModel, parent, prefix);
    
//XXX
//        if(false/*EMPTY*/) {
            
            BasicPageForm addPersonSheet = new GenericContactAttachPersonPropertyForm(itemModel, this);
            add(ADD_PERSON_SHEET_NAME, (String)ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.attach_person").localize(), new WorkflowLockedComponentAccess(addPersonSheet, itemModel), addPersonSheet.getSaveCancelSection().getCancelButton());

            /* Set the displayComponent for this step */
//            setDisplayComponent(getEmptyPersonPropertySheet(itemModel));

//        } else {
            
            BasicPageForm editPersonSheet = new GenericContactEditPersonPropertyForm(itemModel, this);
            add(EDIT_PERSON_SHEET_NAME, (String)ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.edit_person").localize(), new WorkflowLockedComponentAccess(editPersonSheet, itemModel), editPersonSheet.getSaveCancelSection().getCancelButton());
            
//            BasicPageForm changePersonSheet = new GenericContactEditPersonPropertyForm(itemModel, this);
//            add(CHANGE_PERSON_SHEET_NAME, (String)ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.reattach_person").localize(), new WorkflowLockedComponentAccess(changePersonSheet, itemModel), changePersonSheet.getSaveCancelSection().getCancelButton());
            
            BasicPageForm deletePersonSheet = new GenericContactDeletePersonForm(itemModel, this);
            add(DELETE_PERSON_SHEET_NAME, (String)ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.delete_person").localize(), new WorkflowLockedComponentAccess(deletePersonSheet, itemModel), deletePersonSheet.getSaveCancelSection().getCancelButton());

            /* Set the displayComponent for this step */
            setDisplayComponent(getPersonPropertySheet(itemModel));
//        }

    }

    public static Component getPersonPropertySheet(ItemSelectionModel itemModel) {
	DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

	sheet.add((String)ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.surname").localize(), "person." + GenericPerson.SURNAME);
	sheet.add((String)ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.givenname").localize(), "person." + GenericPerson.GIVENNAME);
	sheet.add((String)ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepre").localize(), "person." + GenericPerson.TITLEPRE);
	sheet.add((String)ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepost").localize(), "person." + GenericPerson.TITLEPOST);
	
	return sheet;
    }
    
    public static Component getEmptyPersonPropertySheet(ItemSelectionModel itemModel) {
        return new Label(((String)ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.emptyPerson").localize()));
    }
}
