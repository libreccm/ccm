/*
 * Copyright (c) 2013 Jens Pelzetter
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
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Form for uploading the description of a SciDepartment.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentDescriptionUploadForm extends AbstractTextUploadForm {

    public SciDepartmentDescriptionUploadForm(
            final ItemSelectionModel itemModel) {
        super(itemModel);
    }

    @Override
    public GlobalizedMessage getLabelText() {
        return SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.description.upload");
    }

    @Override
    public GlobalizedMessage getMimeTypeLabel() {
        return SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.description.upload.mimetype");
    }

    @Override
    public void setText(final ItemSelectionModel itemModel,
                        final PageState state,
                        final String text) {
        final SciDepartment department = (SciDepartment) itemModel.
                getSelectedObject(state);
        department.setDepartmentShortDescription(text);
        department.save();
    }
}
