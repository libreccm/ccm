package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectPropertiesStep
        extends GenericOrganizationalUnitPropertiesStep {

    public SciProjectPropertiesStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getSciProjectPropertySheet(
            final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet =
                                        (DomainObjectPropertySheet) GenericOrganizationalUnitPropertiesStep.
                getGenericOrganizationalUnitPropertySheet(itemModel);

        sheet.add(SciProjectGlobalizationUtil.globalize("sciproject.ui.begin"),
                  SciProject.BEGIN,
                  new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(final DomainObject obj,
                                 final String attribute,
                                 final PageState state) {
                final SciProject project = (SciProject) obj;
                if (project.getBegin() == null) {
                    return (String) ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                } else if (project.getBeginSkipMonth()
                           || project.getBeginSkipDay()) {
                    String month = "";
                    final Calendar begin = new GregorianCalendar();
                    begin.setTime(project.getBegin());
                    if (!project.getBeginSkipMonth()) {
                        final Locale locale = GlobalizationHelper.
                                getNegotiatedLocale();
                        if (locale != null) {
                            final DateFormatSymbols dfs = new DateFormatSymbols(
                                    locale);
                            String[] months = dfs.getMonths();
                            month = String.format("%s ",
                                                  months[begin.get(
                                    Calendar.MONTH)]);
                        }
                    }
                    return String.format("%s %s",
                                         month,
                                         Integer.toString(begin.get(
                            Calendar.YEAR)));
                } else {
                    return DateFormat.getDateInstance(DateFormat.LONG).format(
                            project.getBegin());
                }
            }
        });
        sheet.add(SciProjectGlobalizationUtil.globalize("sciproject.ui.end"),
                  SciProject.END,
                  new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(final DomainObject obj,
                                 final String attribute,
                                 final PageState state) {
                final SciProject project = (SciProject) obj;
                if (project.getEnd() == null) {
                    return (String) ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.unknown").localize();
                } else if (project.getEndSkipMonth()
                           || project.getEndSkipDay()) {
                    String month = "";
                    final Calendar end = new GregorianCalendar();
                    end.setTime(project.getEnd());
                    if (!project.getEndSkipMonth()) {
                        final Locale locale = GlobalizationHelper.
                                getNegotiatedLocale();
                        if (locale != null) {
                            final DateFormatSymbols dfs = new DateFormatSymbols(
                                    locale);
                            String[] months = dfs.getMonths();
                            month = String.format("%s ",
                                                  months[end.get(
                                    Calendar.MONTH)]);
                        }
                    }
                    return String.format("%s %s",
                                         month,
                                         Integer.toString(end.get(
                            Calendar.YEAR)));
                } else {
                    return DateFormat.getDateInstance(DateFormat.LONG).format(project.
                            getEnd());
                }
            }
        });
        sheet.add(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.shortdesc"),
                  SciProject.PROJECT_SHORT_DESCRIPTION);

        return sheet;
    }

    @Override
    protected void addBasicProperties(final ItemSelectionModel itemModel,
                                      final AuthoringKitWizard parent) {
        final SimpleEditStep basicProperties =
                             new SimpleEditStep(itemModel,
                                                parent,
                                                EDIT_SHEET_NAME);

        final BasicPageForm editBasicSheet = new SciProjectPropertyForm(
                itemModel,
                this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.edit_basic_sheet").localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getSciProjectPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.edit_basic_properties").
                localize()),
                basicProperties);
    }

    @Override
    protected void addSteps(final ItemSelectionModel itemModel,
                            final AuthoringKitWizard parent) {
        addStep(new GenericOrganizationalUnitContactPropertiesStep(itemModel,
                                                                   parent),
                SciProjectGlobalizationUtil.globalize("sciproject.ui.contacts"));


    }
}
