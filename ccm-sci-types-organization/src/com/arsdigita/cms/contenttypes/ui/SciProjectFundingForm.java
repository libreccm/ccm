package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
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
public class SciProjectFundingForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    public SciProjectFundingForm(ItemSelectionModel itemModel) {
        super("sciprojectEditFundingForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        ParameterModel fundingParam = new StringParameter(
                SciProject.FUNDING);
        CMSDHTMLEditor funding = new CMSDHTMLEditor(fundingParam);
        funding.setCols(75);
        funding.setRows(25);
        add(funding);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();
        SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        data.put(SciProject.FUNDING, project.getFunding());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();
        SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        if ((project != null) && this.getSaveCancelSection().getSaveButton().
                isSelected(state)) {
            project.setFunding((String) data.get(SciProject.FUNDING));

            project.save();

            init(fse);
        }
    }
}
