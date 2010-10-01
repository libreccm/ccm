package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    public static final String ID = "SciProjectEdit";

    public SciProjectPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciProjectPropertyForm(ItemSelectionModel itemModel,
                                  SciProjectPropertiesStep step) {
        super(itemModel, step);
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        Label descLabel = new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizations.ui.project.description"));
        add(descLabel);
        ParameterModel descParam = new StringParameter(SciProject.PROJECT_DESCRIPTION);
        TextArea desc = new TextArea(descParam);
        desc.setCols(60);
        desc.setRows(18);
        add(desc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        SciProject project = (SciProject) super.initBasicWidgets(fse);

        data.put(SciProject.PROJECT_DESCRIPTION, project.getDescription());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        SciProject project = (SciProject) super.processBasicWidgets(fse);

        if ((project != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            project.setDescription((String) data.get(SciProject.PROJECT_DESCRIPTION));

            project.save();

            init(fse);
        }
    }
}
