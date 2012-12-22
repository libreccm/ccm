package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PersonPublicationsStep extends SimpleEditStep {

    //private String ADD_PUBLICATION_TO_PERSON_SHEET_NAME = "PersonPublicationStep";
    
    public PersonPublicationsStep(final ItemSelectionModel itemModel, final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }
    
    public PersonPublicationsStep(final ItemSelectionModel itemModel, 
                                 final AuthoringKitWizard parent, 
                                 final String prefix) {
        super(itemModel, parent, prefix);                
        
        final PersonPublicationsTable publicationsTable = new PersonPublicationsTable(itemModel);
        setDisplayComponent(publicationsTable);
    }
    
    
    
}
