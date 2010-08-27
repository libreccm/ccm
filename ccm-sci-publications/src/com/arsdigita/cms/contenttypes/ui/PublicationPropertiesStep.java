package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import java.text.DateFormat;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";
    private SegmentedPanel segmentedPanel;

    public PublicationPropertiesStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent) {
        super(itemModel, parent);

        segmentedPanel = new SegmentedPanel();
        setDefaultEditKey(EDIT_SHEET_NAME);

        addBasicProperties(itemModel, parent);
        addSteps(itemModel, parent);

        setDisplayComponent(segmentedPanel);
    }

    public static Component getPublicationPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.title"),
                  Publication.NAME);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.year_of_publication"),
                  Publication.YEAR_OF_PUBLICATION);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.abstract"),
                  Publication.ABSTRACT);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.misc"),
                  Publication.MISC);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(ContenttypesGlobalizationUtil.globalize(
                    "cms.ui.authoring.page_launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new DomainObjectPropertySheet.AttributeFormatter() {

                public String format(DomainObject item,
                                     String attribute,
                                     PageState state) {
                    ContentPage page = (ContentPage) item;
                    if (page.getLaunchDate() != null) {
                        return DateFormat.getDateInstance(DateFormat.LONG).
                                format(page.getLaunchDate());
                    } else {
                        return (String) ContenttypesGlobalizationUtil.globalize(
                                "cms.ui.unknown").localize();
                    }
                }
            });
        }

        return sheet;
    }

    protected SegmentedPanel getSegmentedPanel() {
        return segmentedPanel;
    }

    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet = new PublicationPropertyForm(itemModel,
                                                                   this);
        basicProperties.add(EDIT_SHEET_NAME, (String) PublicationGlobalizationUtil.
                globalize("publications.ui.publication.edit_basic_sheet").
                localize(), new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel), editBasicSheet.
                getSaveCancelSection().getCancelButton());

        basicProperties.setDisplayComponent(getPublicationPropertySheet(
                itemModel));

        segmentedPanel.addSegment(new Label((String) PublicationGlobalizationUtil.
                globalize("publications.ui.publication.basic_properties").
                localize()), basicProperties);
    }

    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        addStep(new PublicationAuthorsPropertyStep(itemModel, parent),
                "publications.ui.publication.authors");
    }

    protected void addStep(SimpleEditStep step, String labelKey) {
        segmentedPanel.addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                labelKey).localize()),
                step);
    }
}
