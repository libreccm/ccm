package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenericPersonOrgaUnitsStep extends SimpleEditStep {

    public GenericPersonOrgaUnitsStep(final ItemSelectionModel itemModel, final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public GenericPersonOrgaUnitsStep(final ItemSelectionModel itemModel,
                                      final AuthoringKitWizard parent,
                                      final String prefix) {
        super(itemModel, parent, prefix);

        final GenericPersonOrgaUnitsTable table = new GenericPersonOrgaUnitsTable(itemModel);
        setDisplayComponent(table);
    }

}
