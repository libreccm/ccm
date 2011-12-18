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
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.IncompleteDateParameter;
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

        final ParameterModel beginSkipMonthParam =
                             new BooleanParameter(SciProject.BEGIN_SKIP_MONTH);
        final Hidden beginSkipMonth = new Hidden(beginSkipMonthParam);
        add(beginSkipMonth);

        final ParameterModel beginSkipDayParam =
                             new BooleanParameter(SciProject.BEGIN_SKIP_DAY);
        final Hidden beginSkipDay = new Hidden(beginSkipDayParam);
        add(beginSkipDay);

        add(new Label(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.begin")));
        final IncompleteDateParameter beginParam =
                             new IncompleteDateParameter(SciProject.BEGIN);
        beginParam.allowSkipMonth(true);
        beginParam.allowSkipDay(true);                
        final Calendar today = new GregorianCalendar();
        final Date begin = new Date(beginParam);        
        begin.setAutoCurrentYear(false);
        begin.setYearRange(1970, (today.get(Calendar.YEAR) + 2));
        add(begin);

        final ParameterModel endSkipMonthParam =
                             new BooleanParameter(SciProject.END_SKIP_MONTH);
        final Hidden endSkipMonth = new Hidden(endSkipMonthParam);
        add(endSkipMonth);

        final ParameterModel endSkipDayParam = new BooleanParameter(
                SciProject.END_SKIP_DAY);
        final Hidden endSkipDay = new Hidden(endSkipDayParam);
        add(endSkipDay);

        add(new Label(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.end")));
        final IncompleteDateParameter endParam = new IncompleteDateParameter(SciProject.END);
        endParam.allowSkipMonth(true);
        endParam.allowSkipDay(true);
        Date end = new Date(endParam);
        end.setAutoCurrentYear(false);
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
        data.put(SciProject.BEGIN_SKIP_MONTH, project.getBeginSkipMonth());
        data.put(SciProject.BEGIN_SKIP_DAY, project.getBeginSkipDay());
        data.put(SciProject.END, project.getEnd());
        data.put(SciProject.END_SKIP_MONTH, project.getEndSkipMonth());
        data.put(SciProject.END_SKIP_DAY, project.getEndSkipDay());
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
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            project.setBeginSkipMonth(((IncompleteDateParameter) data.
                                       getParameter(SciProject.BEGIN).
                                       getModel()).isMonthSkipped());
            project.setBeginSkipDay(
                    ((IncompleteDateParameter) data.getParameter(
                     SciProject.BEGIN).getModel()).isDaySkipped());
            project.setBegin((java.util.Date) data.get(SciProject.BEGIN));            
            project.setEndSkipMonth(
                    ((IncompleteDateParameter) data.getParameter(
                     SciProject.END).getModel()).isMonthSkipped());
            project.setEndSkipDay(((IncompleteDateParameter) data.getParameter(
                                   SciProject.END).getModel()).isDaySkipped());
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
