package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteDescriptionUploadForm extends AbstractTextUploadForm {
    
    public SciInstituteDescriptionUploadForm(
            final ItemSelectionModel itemModel) {
        super(itemModel);
    }

    @Override
    public GlobalizedMessage getLabelText() {
        return SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.description.upload");
    }

    @Override
    public GlobalizedMessage getMimeTypeLabel() {
        return SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.description.upload.mimetype");
    }

    @Override
    public void setText(final ItemSelectionModel itemModel,
                        final PageState state,
                        final String text) {
        final SciInstitute institute = (SciInstitute) itemModel.
                getSelectedObject(state);
        institute.setInstituteShortDescription(text);
        institute.save();
    }
    
}
