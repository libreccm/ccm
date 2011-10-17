package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectConfig;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    public final static String ID = "SciProjectEdit";
    private final static SciProjectConfig config = SciProject.getConfig();

    public SciProjectPropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciProjectPropertyForm(final ItemSelectionModel itemModel,
                                  final SciProjectPropertiesStep step) {
        super(itemModel, step);
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.begin")));
        ParameterModel beginParam = new DateParameter(SciProject.BEGIN);
        Calendar today = new GregorianCalendar();
        Date begin = new Date(beginParam);
        begin.setYearRange(1970, (today.get(Calendar.YEAR) + 2));
        add(begin);

        add(new Label(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.end")));
        ParameterModel endParam = new DateParameter(SciProject.END);
        Date end = new Date(endParam);
        end.setYearRange(1970, (today.get(Calendar.YEAR) + 8));
        add(end);

        add(new Label(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.shortdesc")));
        ParameterModel shortDescParam = new StringParameter(
                SciProject.PROJECT_SHORT_DESCRIPTION);
        TextArea shortDesc = new TextArea(shortDescParam);
        shortDesc.addValidationListener(
                new StringInRangeValidationListener(0,
                                                    config.getShortDescMaxLength()));
        shortDesc.setCols(75);
        shortDesc.setRows(5);
        add(shortDesc);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        final FormData data = fse.getFormData();
        final SciProject project = (SciProject) super.initBasicWidgets(fse);

        data.put(SciProject.BEGIN, project.getBegin());
        data.put(SciProject.END, project.getEnd());
        data.put(SciProject.PROJECT_SHORT_DESCRIPTION,
                 project.getProjectShortDescription());
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final SciProject project = (SciProject) super.processBasicWidgets(fse);

        if ((project != null)
            && getSaveCancelSection().getCancelButton().isSelected(state)) {
            project.setBegin((java.util.Date) data.get(SciProject.BEGIN));
            project.setEnd((java.util.Date) data.get(SciProject.END));
            project.setProjectShortDescription((String) data.get(
                    SciProject.PROJECT_SHORT_DESCRIPTION));

            project.save();
        }

        init(fse);
    }

    @Override
    public String getTitleLabel() {
        return (String) SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.title").localize();
    }
}
