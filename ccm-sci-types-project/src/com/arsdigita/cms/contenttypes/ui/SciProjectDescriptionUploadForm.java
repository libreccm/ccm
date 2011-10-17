package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectDescriptionUploadForm extends AbstractTextUploadForm  {
    
    public SciProjectDescriptionUploadForm(final ItemSelectionModel itemModel) {
        super(itemModel);
    }
    
    @Override
    public GlobalizedMessage getLabelText() {
        return SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.description.upload");
    }

    @Override
    public GlobalizedMessage getMimeTypeLabel() {
        return SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.description.upload.mimetype");
    }

    @Override
    public void setText(final ItemSelectionModel itemModel,
                        final PageState state,
                        final String text) {
        final SciProject project = (SciProject) itemModel.getSelectedObject(state);
        project.setProjectDescription(text);
        project.save();
    }         
}
