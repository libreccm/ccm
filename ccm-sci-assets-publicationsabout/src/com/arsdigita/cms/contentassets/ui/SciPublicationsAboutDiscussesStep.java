/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.SciPublicationsAboutGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class SciPublicationsAboutDiscussesStep extends SimpleEditStep {

    protected static final String ADD_DISCUSSED = "add_discussed_publication";

    public SciPublicationsAboutDiscussesStep(final ItemSelectionModel itemModel,
                                             final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciPublicationsAboutDiscussesStep(final ItemSelectionModel itemModel,
                                             final AuthoringKitWizard parent,
                                             final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addDiscussedSheet = 
                  new SciPublicationsAboutDiscussesForm(itemModel);
        add(ADD_DISCUSSED,
            SciPublicationsAboutGlobalizationUtil.globalize(
            "com.arsdigita.cms.contentassets.about.discusses.add"),
            new WorkflowLockedComponentAccess(addDiscussedSheet, itemModel),
            addDiscussedSheet.getSaveCancelSection().getCancelButton());
        
        final SciPublicationsAboutDiscussesTable discussedTable = 
                  new SciPublicationsAboutDiscussesTable(itemModel);
        setDisplayComponent(discussedTable);
    }

}
