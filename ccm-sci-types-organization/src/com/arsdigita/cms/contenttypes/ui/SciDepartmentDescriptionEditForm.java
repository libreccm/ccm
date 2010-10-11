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
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentDescriptionEditForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    public SciDepartmentDescriptionEditForm(ItemSelectionModel itemModel) {
        super("scidepartmentEditDescForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.description")));
        ParameterModel descParam = new StringParameter(
                SciDepartment.DEPARTMENT_DESCRIPTION);
        TextArea desc;
        if (SciDepartment.getConfig().getDepartmentDescriptionDhtml()) {
            desc = new CMSDHTMLEditor(descParam);
        } else {
            desc = new TextArea(descParam);
        }
        desc.setCols(75);
        desc.setRows(25);
        add(desc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();
        SciDepartment department = (SciDepartment) getItemSelectionModel().
                getSelectedObject(state);

        data.put(SciDepartment.DEPARTMENT_DESCRIPTION,
                 department.getDepartmentDescription());

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        FormData data = fse.getFormData();
        SciDepartment department = (SciDepartment) getItemSelectionModel().
                getSelectedObject(state);

        if ((department != null) && getSaveCancelSection().getSaveButton().
                isSelected(state)) {
            department.setDepartmentDescription((String) data.get(
                    SciDepartment.DEPARTMENT_DESCRIPTION));

            department.save();

            init(fse);
        }
    }
}
