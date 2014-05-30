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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciPublicationsDramaticArtsGlobalisationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsMovieProductionCompanyStep extends SimpleEditStep {

    private String SET_PRODUCTION_COMPANY_STEP = "setProductionCompanyStep";

    public SciPublicationsMovieProductionCompanyStep(final ItemSelectionModel itemModel,
                                                      final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciPublicationsMovieProductionCompanyStep(final ItemSelectionModel itemModel,
                                                      final AuthoringKitWizard parent,
                                                      final String prefix) {
        super(itemModel, parent, prefix);

        final SciPublicationsDramaticArtsGlobalisationUtil globalisationUtil
                                                               = new SciPublicationsDramaticArtsGlobalisationUtil();
        final BasicItemForm setProductionCompanyForm
                            = new SciPublicationsMovieProductionCompanyForm(itemModel);
        add(SET_PRODUCTION_COMPANY_STEP,
            globalisationUtil.globalise("publications.ui.dramaticarts.movie.producationcompany"),
            new WorkflowLockedComponentAccess(setProductionCompanyForm, itemModel),
            setProductionCompanyForm.getSaveCancelSection().getCancelButton());

        final SciPublicationsMovieProductionCompanySheet sheet
                                                         = new SciPublicationsMovieProductionCompanySheet(
                itemModel);
        setDisplayComponent(this);
    }

}
