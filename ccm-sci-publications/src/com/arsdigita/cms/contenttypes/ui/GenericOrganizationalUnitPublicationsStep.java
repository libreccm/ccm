/*
 * Copyright (c) 2010 Jens Pelzetter
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

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrganizationalUnitPublicationsStep extends SimpleEditStep {

    private String ADD_PUBLICATION_SHEET_NAME =
                   "GenericOrganizationalUnitAddPublication";

    public GenericOrganizationalUnitPublicationsStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public GenericOrganizationalUnitPublicationsStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent,
            final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addPublicationSheet =
                      new GenericOrganizationalUnitPublicationAddForm(itemModel);
        add(ADD_PUBLICATION_SHEET_NAME,
            PublicationGlobalizationUtil.globalize(
                "genericorganizationalunit.ui.add_publication"),
            new WorkflowLockedComponentAccess(addPublicationSheet, itemModel),
            addPublicationSheet.getSaveCancelSection().getCancelButton());
        
        final GenericOrganizationalUnitPublicationsTable publicationsTable = new GenericOrganizationalUnitPublicationsTable(itemModel);
        setDisplayComponent(publicationsTable);

    }
}
