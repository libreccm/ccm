
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublisherPublicationsStep extends SimpleEditStep {
    
    public PublisherPublicationsStep(final ItemSelectionModel itemModel, final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }
    
    public PublisherPublicationsStep(final ItemSelectionModel itemModel, 
                                     final AuthoringKitWizard parent, 
                                     final String prefix) {
        super(itemModel, parent, prefix);
        
        final PublisherPublicationsTable publicationsTable = new PublisherPublicationsTable(itemModel);
        setDisplayComponent(publicationsTable);
    }
    
}
