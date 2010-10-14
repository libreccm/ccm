/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Form for uploading the description of a {@link SciOrganization}.
 *
 * @author Jens Pelzetter
 * @see SciOrganization
 * @see AbstractTextUploadForm
 */
public class SciOrganizationDescriptionUploadForm
        extends AbstractTextUploadForm {

    public SciOrganizationDescriptionUploadForm(ItemSelectionModel itemModel) {
        super(itemModel);
    }

    @Override
    public GlobalizedMessage getLabelText() {
        return SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.description.upload");
    }

    @Override
    public GlobalizedMessage getMimeTypeLabel() {
        return SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.description.upload.mimetype");
    }

    @Override
    public void setText(ItemSelectionModel itemModel,
                        PageState state,
                        String text) {
        SciOrganization orga = (SciOrganization) itemModel.getSelectedObject(
                state);
        orga.setOrganizationDescription(text);
        orga.save();
    }
}
