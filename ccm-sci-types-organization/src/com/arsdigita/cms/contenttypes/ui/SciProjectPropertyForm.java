package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.begin")));
        ParameterModel beginParam = new DateParameter(SciProject.BEGIN);
        Calendar today = new GregorianCalendar();
        Date begin = new Date(beginParam);
        begin.setYearRange(1970, (today.get(Calendar.YEAR) + 2));
        add(begin);

        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.end")));
        ParameterModel endParam = new DateParameter(SciProject.END);
        Date end = new Date(endParam);
        end.setYearRange(1970, (today.get(Calendar.YEAR) + 8));
        add(end);

        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizations.ui.projectshortdesc")));
        ParameterModel shortDescParam = new StringParameter(
                SciProject.PROJECT_SHORT_DESCRIPTION);
        TextArea shortDesc = new TextArea(shortDescParam);
        shortDesc.setCols(60);
        shortDesc.setRows(20);
        add(shortDesc);

        /*add(new Label(SciOrganizationGlobalizationUtil.globalize(
        "sciorganization.ui.project.description")));
        ParameterModel descParam = new StringParameter(
        SciProject.PROJECT_DESCRIPTION);
        CMSDHTMLEditor desc = new CMSDHTMLEditor(descParam);
        desc.setCols(75);
        desc.setRows(25);
        add(desc);*/

        /*add(new Label(SciOrganizationGlobalizationUtil.globalize(
        "sciorganization.ui.project.funding")));
        ParameterModel fundingParam = new StringParameter(
        SciProject.FUNDING);
        CMSDHTMLEditor funding = new CMSDHTMLEditor(fundingParam);
        funding.setCols(60);
        funding.setRows(18);
        add(funding);*/
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        SciProject project = (SciProject) super.initBasicWidgets(fse);

        data.put(SciProject.BEGIN, project.getBegin());
        data.put(SciProject.END, project.getEnd());
        data.put(SciProject.PROJECT_SHORT_DESCRIPTION,
                 project.getProjectShortDescription());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        SciProject project = (SciProject) super.processBasicWidgets(fse);

        if ((project != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            project.setBegin((java.util.Date) data.get(
                    SciProject.BEGIN));
            project.setEnd((java.util.Date) data.get(
                    SciProject.END));
            project.setProjectShortDescription((String) data.get(
                    SciProject.PROJECT_SHORT_DESCRIPTION));

            project.save();

            init(fse);
        }
    }
}
