package com.arsdigita.cms.contenttypes.ui;

import java.text.DateFormat;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicationList;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationListPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";
    private SegmentedPanel segmentedPanel;

    public PublicationListPropertiesStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent) {
        super(itemModel, parent);

        segmentedPanel = new SegmentedPanel();
        setDefaultEditKey(EDIT_SHEET_NAME);

        addBasicProperties(itemModel, parent);
        addSteps(itemModel, parent);

        setDisplayComponent(segmentedPanel);
    }

    public static Component getPublicationListPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet =
                                  new DomainObjectPropertySheet(itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publicationlist.name"),
                  PublicationList.NAME);
        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.publicationlist.title"),
                  PublicationList.TITLE);

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

        BasicPageForm editBasicSheet =
                      new PublicationListPropertyForm(itemModel,
                                                      this);
        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publcations.ui.publicationlist_edit_basic_sheet").
                localize(), new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getPublicationListPropertySheet(itemModel));

        segmentedPanel.addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publicationlist.basic_properties").
                localize()), basicProperties);
    }

    protected void addSteps(ItemSelectionModel itemModel,
                            AuthoringKitWizard parent) {
        addStep(new PublicationListPublicationsStep(itemModel, parent),
                "publications.ui.publicationlist.publications");
    }

    protected void addStep(SimpleEditStep step, String labelKey) {
        segmentedPanel.addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                labelKey).localize()),
                step);

    }
}
