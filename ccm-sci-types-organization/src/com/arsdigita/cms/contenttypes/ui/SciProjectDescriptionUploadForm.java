package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectDescriptionUploadForm extends AbstractTextUploadForm {

    public SciProjectDescriptionUploadForm(ItemSelectionModel itemModel) {
        super(itemModel);
    }

    @Override
    public GlobalizedMessage getLabelText() {
        return SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.description.upload");
    }

    @Override
    public GlobalizedMessage getMimeTypeLabel() {
        return SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.description.upload.mimetype");
    }

    @Override
    public void setText(ItemSelectionModel itemModel, PageState state,
                        String text) {
        SciProject project = (SciProject) itemModel.getSelectedObject(state);
        project.setProjectDescription(text);
        project.save();
    }
}
