package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener {

    public static final String ID = "SciDepartmentEdit";

    public SciDepartmentPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciDepartmentPropertyForm(ItemSelectionModel itemModel,
                                     SciDepartmentPropertiesStep step) {
        super(itemModel, step);
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        Label descLabel = new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizations.ui.department.shortdescription"));
        add(descLabel);
        ParameterModel descParam = new StringParameter(
                SciDepartment.DEPARTMENT_SHORT_DESCRIPTION);
        TextArea desc = new TextArea(descParam);
        desc.setCols(75);
        desc.setRows(5);
        add(desc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        SciDepartment department = (SciDepartment) super.initBasicWidgets(fse);

        data.put(SciDepartment.DEPARTMENT_SHORT_DESCRIPTION,
                 department.getDepartmentShortDescription());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        SciDepartment department =
                      (SciDepartment) super.processBasicWidgets(fse);

        if ((department != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            department.setDepartmentShortDescription(
                    (String) data.get(SciDepartment.DEPARTMENT_SHORT_DESCRIPTION));

            department.save();

            init(fse);
        }


    }
}
