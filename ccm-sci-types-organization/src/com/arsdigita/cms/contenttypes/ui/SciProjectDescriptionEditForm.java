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
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectDescriptionEditForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    public SciProjectDescriptionEditForm(ItemSelectionModel itemModel) {
        super("sciprojectEditDescForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizazion.ui.project.description")));
        ParameterModel descParam = new StringParameter(
                SciProject.PROJECT_DESCRIPTION);
        CMSDHTMLEditor desc = new CMSDHTMLEditor(descParam);
        desc.setCols(75);
        desc.setRows(25);
        add(desc);


        if (!SciProject.getConfig().getProjectFundingHide()) {
            add(new Label(SciOrganizationGlobalizationUtil.globalize(
                    "sciorganizazion.ui.project.funding")));
            ParameterModel fundingParam = new StringParameter(
                    SciProject.FUNDING);
            TextArea funding;
            if (SciProject.getConfig().getProjectFundingDhtml()) {
                funding = new CMSDHTMLEditor(fundingParam);
            } else {
                funding = new TextArea(fundingParam);
            }

            funding.setCols(75);
            funding.setRows(25);
            add(funding);
        }
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();
        SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        data.put(SciProject.PROJECT_DESCRIPTION,
                 project.getProjectDescription());
        if (!SciProject.getConfig().getProjectFundingHide()) {
            data.put(SciProject.FUNDING, project.getFunding());
        }

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();
        SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        if ((project != null) && this.getSaveCancelSection().getSaveButton().
                isSelected(state)) {
            project.setProjectDescription((String) data.get(
                    SciProject.PROJECT_DESCRIPTION));
            if (!SciProject.getConfig().getProjectFundingHide()) {
                project.setFunding((String) data.get(
                        SciProject.FUNDING));
            }

            project.save();

            init(fse);
        }
    }
}
