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
            (String) PublicationGlobalizationUtil.globalize(
                "genericorganizationalunit.ui.add_publication").localize(),
            new WorkflowLockedComponentAccess(addPublicationSheet, itemModel),
            addPublicationSheet.getSaveCancelSection().getCancelButton());
        
        final GenericOrganizationalUnitPublicationsTable publicationsTable = new GenericOrganizationalUnitPublicationsTable(itemModel);
        setDisplayComponent(publicationsTable);

    }
}
