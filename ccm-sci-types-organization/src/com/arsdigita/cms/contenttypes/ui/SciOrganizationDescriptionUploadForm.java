package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter
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
