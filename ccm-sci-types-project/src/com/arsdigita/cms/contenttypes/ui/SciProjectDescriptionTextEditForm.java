package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectConfig;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciProjectDescriptionTextEditForm extends BasicItemForm implements FormProcessListener,
                                                                                FormInitListener {

    private final static SciProjectConfig CONFIG = SciProject.getConfig();

    public SciProjectDescriptionTextEditForm(final ItemSelectionModel itemModel) {
        super("SciProjectDescriptionTextEditForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.description")));
        final ParameterModel descParam = new StringParameter(
                SciProject.PROJECT_DESCRIPTION);
        final TextArea desc;
        if (CONFIG.getEnableDescriptionDhtml()) {
            desc = new CMSDHTMLEditor(descParam);
        } else {
            desc = new TextArea(descParam);
        }
        desc.setCols(75);
        desc.setRows(25);
        add(desc);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final SciProject project = (SciProject) getItemSelectionModel().getSelectedObject(state);

        data.put(SciProject.PROJECT_DESCRIPTION, project.getProjectDescription());

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final SciProject project = (SciProject) getItemSelectionModel().getSelectedObject(state);

        if ((project != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            
            project.setProjectDescription((String) data.get(SciProject.PROJECT_DESCRIPTION));
            
            project.save();
        }
        
        init(event);
    }

}
