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
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericContact;
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
    private WorkflowLockedComponentAccess addPerson;
    private WorkflowLockedComponentAccess editPerson;
    private WorkflowLockedComponentAccess delPerson;

    /**
     * Creates a new instance of GenericContactPersonPropertiesStep
     */
    public GenericContactPersonPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    public GenericContactPersonPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent, String prefix) {
        super(itemModel, parent, prefix);

        BasicPageForm addPersonSheet = new GenericContactAttachPersonPropertyForm(itemModel, this);
        addPerson = new WorkflowLockedComponentAccess(addPersonSheet, itemModel);
        add(ADD_PERSON_SHEET_NAME, (String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.attach_person").localize(), addPerson, addPersonSheet.getSaveCancelSection().getCancelButton());

        BasicPageForm editPersonSheet = new GenericContactEditPersonPropertyForm(itemModel, this);
        editPerson = new WorkflowLockedComponentAccess(editPersonSheet, itemModel);
        add(EDIT_PERSON_SHEET_NAME, (String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.edit_person").localize(), editPerson, editPersonSheet.getSaveCancelSection().getCancelButton());

        BasicPageForm deletePersonSheet = new GenericContactDeletePersonForm(itemModel, this);
        delPerson = new WorkflowLockedComponentAccess(deletePersonSheet, itemModel);
        add(DELETE_PERSON_SHEET_NAME, (String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.delete_person").localize(), delPerson, deletePersonSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        setDisplayComponent(getPersonPropertySheet(itemModel));

    }

    public static Component getPersonPropertySheet(ItemSelectionModel itemModel) {
        /*DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.surname"), "person." + GenericPerson.SURNAME);
        sheet.add(ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.givenname"), "person." + GenericPerson.GIVENNAME);
        sheet.add(ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepre"), "person." + GenericPerson.TITLEPRE);
        sheet.add(ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.titlepost"), "person." + GenericPerson.TITLEPOST);*/

        GenericContactPersonSheet sheet = new GenericContactPersonSheet(
                itemModel);

        return sheet;
    }

    public static Component getEmptyPersonPropertySheet(ItemSelectionModel itemModel) {
        return new Label(((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contact.emptyPerson").localize()));
    }

    @Override
    public boolean isVisible(PageState ps) {

        if(((GenericContact) getItemSelectionModel().getSelectedItem(ps)).hasPerson() == false) {
            addPerson.getComponent().setVisible(ps, true);
            editPerson.getComponent().setVisible(ps, false);
            delPerson.getComponent().setVisible(ps, false);
        } else {
            addPerson.getComponent().setVisible(ps, false);
            editPerson.getComponent().setVisible(ps, true);
            delPerson.getComponent().setVisible(ps, true);
        }

        return super.isVisible(ps);
    }
}
