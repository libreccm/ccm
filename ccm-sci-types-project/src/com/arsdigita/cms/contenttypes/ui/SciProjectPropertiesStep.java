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
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import java.text.DateFormat;

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

        final BasicPageForm editBasicSheet = new SciProjectPropertyForm(itemModel,
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
