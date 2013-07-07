/*
 * Copyright (C) 2009-2013 Sören Bernstein, University of Bremen. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Property step provides the UI to display a {@link GenericPerson Person}
 * attached to a Contact.
 * It is part of the central entry point of contact's authoring step
 * {@link GenericContactPropertiesStep}
 * 
 * Class builds the display component, delegates the edit component to
 * {@link GenericContactAttachPersonPropertyForm}
 * 
 * @author quasi
 */
public class GenericContactPersonPropertiesStep extends SimpleEditStep {

    public static final String ADD_PERSON_SHEET_NAME = "addPerson";
    public static final String EDIT_PERSON_SHEET_NAME = "editPerson";
    public static final String CHANGE_PERSON_SHEET_NAME = "changePerson";
    public static final String DELETE_PERSON_SHEET_NAME = "deletePerson";
    private WorkflowLockedComponentAccess addPerson;
    // private WorkflowLockedComponentAccess editPerson;
    // private WorkflowLockedComponentAccess delPerson;

    /**
     * Creates a new instance of GenericContactPersonPropertiesStep
     */
    public GenericContactPersonPropertiesStep(ItemSelectionModel itemModel, 
                                              AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }

    public GenericContactPersonPropertiesStep(ItemSelectionModel itemModel, 
                                              AuthoringKitWizard parent, 
                                              String prefix) {
        super(itemModel, parent, prefix);

        BasicPageForm addPersonSheet = new 
                      GenericContactAttachPersonPropertyForm(itemModel, this);
        addPerson = new WorkflowLockedComponentAccess(addPersonSheet, itemModel);
        add(ADD_PERSON_SHEET_NAME, 
            ContenttypesGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.contact.attach_person"), 
            addPerson, 
            addPersonSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        setDisplayComponent(getPersonPropertySheet(itemModel));

    }

    public static Component getPersonPropertySheet(ItemSelectionModel itemModel) {
        GenericContactPersonSheet sheet = new GenericContactPersonSheet(itemModel);
        return sheet;
    }

    public static Component getEmptyPersonPropertySheet(ItemSelectionModel itemModel) {
        return new Label((ContenttypesGlobalizationUtil.globalize(
                          "cms.contenttypes.ui.contact.emptyPerson") ));
    }
}
