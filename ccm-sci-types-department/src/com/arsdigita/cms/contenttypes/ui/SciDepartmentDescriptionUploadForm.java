package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.globalization.GlobalizedMessage;

/**
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
