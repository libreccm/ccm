/*
 * Copyright (C) 2001 - 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Created on Dec 9, 2003
 *
 */

package com.arsdigita.cms.docmgr.ui.authoring;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.docmgr.DocLink;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author <a href="mailto:sshinde@redhat.com">Shashin Shinde</a>
 * 
 * $Id: //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/ui/authoring/DocLinkStep.java#1 $
 *
 */

public class DocLinkStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    private final static String EDIT_SHEET_NAME = "edit";

    public DocLinkStep( ItemSelectionModel itemModel,
                                  AuthoringKitWizard parent ) {
        super( itemModel, parent );

        BasicPageForm editSheet;

        editSheet = new DocLinkPropertyForm( itemModel );
        add( EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel),
             editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent( getDocumentPropertySheet( itemModel ) );
    }

    /**
     * Returns a component that displays the properties of the
     * Document specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getDocumentPropertySheet( ItemSelectionModel
                                                     itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel,
                                                                false);
        sheet.add( "Name:", DocLink.NAME );
        sheet.add( "Title:", DocLink.TITLE );
        sheet.add( "Description:", DocLink.DESCRIPTION );
        return sheet;
    }
}
