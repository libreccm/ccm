package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class OrganizationPublicationsStep extends SimpleEditStep {
    
    public OrganizationPublicationsStep(final ItemSelectionModel itemModel, final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }
    
    public OrganizationPublicationsStep(final ItemSelectionModel itemModel, 
                                        final AuthoringKitWizard parent,
                                        final String prefix) {
        super(itemModel, parent, prefix);
        
        final OrganizationPublicationsTable publicationsTable = new OrganizationPublicationsTable(itemModel);
        setDisplayComponent(publicationsTable);
    }
    
}
