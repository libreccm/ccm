package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentDescriptionUploadForm extends AbstractTextUploadForm {

    public SciDepartmentDescriptionUploadForm(ItemSelectionModel itemModel) {
        super(itemModel);
    }

    @Override
    public GlobalizedMessage getLabelText() {
        return SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.description.upload");
    }

    @Override
    public GlobalizedMessage getMimeTypeLabel() {
        return SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.description.upload.mimetype");
    }

    @Override
    public void setText(ItemSelectionModel itemModel,
            PageState state,
                        String text) {
        SciDepartment department = (SciDepartment) itemModel.getSelectedObject(
                state);
        department.setDepartmentDescription(text);
        department.save();
    }
}
