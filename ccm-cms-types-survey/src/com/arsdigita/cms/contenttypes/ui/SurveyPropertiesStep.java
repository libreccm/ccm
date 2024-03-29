package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.cms.contenttypes.Survey;
import com.arsdigita.cms.contenttypes.util.SurveyGlobalizationUtil;

import java.text.DateFormat;
import org.apache.log4j.Logger;

/**
 * AuthoringStep for the basic properties of a basic contact
 */
public class SurveyPropertiesStep extends SimpleEditStep {

    private static final Logger logger = Logger.getLogger(SurveyPropertiesStep.class);
    /**
     * Name of the this edit sheet (Don't know if this this really needed.
     * It has the same value in almost all PropertiesStep classes)
     */
    public static final String EDIT_BASIC_SHEET_NAME = "editBasic";

    /**
     * Constructor for the PropertiesStep.
     *
     * @param itemModel
     * @param parent
     */
    public SurveyPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        /* Use a Segmented Panel for the multiple parts of data */
        SegmentedPanel segmentedPanel = new SegmentedPanel();

        setDefaultEditKey(EDIT_BASIC_SHEET_NAME);

        /* The different parts of information are displayed in seperated segments each containing a SimpleEditStep */
        /* Well, not so simple anymore... */

        /* A new SimpleEditStep */
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel, parent, EDIT_BASIC_SHEET_NAME);

        /* Create the edit component for this SimpleEditStep and the corresponding link */
        BasicPageForm editBasicSheet = new SurveyPropertiesForm(itemModel, this);
        basicProperties.add(EDIT_BASIC_SHEET_NAME, 
                            (String) SurveyGlobalizationUtil
                            .globalize("cms.contenttypes.ui.survey.edit_basic_properties")
                            .localize(), 
                            new WorkflowLockedComponentAccess(editBasicSheet, itemModel), 
                            editBasicSheet.getSaveCancelSection().getCancelButton());

        /* Set the displayComponent for this step */
        basicProperties.setDisplayComponent(SurveyPropertiesStep.getSurveyPropertiesSheet(itemModel));

        /* Add the SimpleEditStep to the segmented panel */
        segmentedPanel.addSegment(new Label((String) SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.basic_properties").localize()), basicProperties);

        /* Sets the composed segmentedPanel as display component */
        setDisplayComponent(segmentedPanel);
    }

    /**
     * Creates and returns the sheet for editing the basic properties
     * of a survey. (@see SurveyPropertiesForm).
     * 
     * @param itemModel
     * @return The sheet for editing the properties of the organization.
     */
    public static Component getSurveyPropertiesSheet(ItemSelectionModel itemModel) {


        /* The DisplayComponent for the Basic Properties */
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"), "title");
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"), "name");
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.description"), ContentPage.DESCRIPTION);

        sheet.add(SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.start_date"), Survey.START_DATE);
        sheet.add(SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.end_date"), Survey.END_DATE);

        /* Display the Status of ResponsesPublic in localized form */
        sheet.add(SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.should_quiz_responses_be_public"), Survey.RESPONSES_PUBLIC, new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(DomainObject obj, String attribute, PageState state) {
                Survey survey = (Survey) obj;
                if (survey.getResponsesPublic() != null) {
                    if (survey.getResponsesPublic().booleanValue() == true) {
                        return (String) SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.Yes").localize();
                    } else {
                        return (String) SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.No").localize();
                    }
                } else {
                    return (String) GlobalizationUtil.globalize("cms.ui.unknown").localize();
                }
            }
        });

        /* Display the Status of ResultsDuringSurvey in localized form */
        sheet.add(SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.show_results_during_survey"), Survey.RESULTS_DURING_SURVEY, new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(DomainObject obj, String attribute, PageState state) {
                Survey survey = (Survey) obj;
                if (survey.getResultsDuringSurvey() != null) {
                    if (survey.getResultsDuringSurvey().booleanValue() == true) {
                        return (String) SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.Yes").localize();
                    } else {
                        return (String) SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.No").localize();
                    }
                } else {
                    return (String) GlobalizationUtil.globalize("cms.ui.unknown").localize();
                }
            }
        });

        /* Display the Status of ResponsesAnonym in localized form */
        sheet.add(SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.should_quiz_responses_be_anonym"), Survey.RESPONSES_ANONYM, new DomainObjectPropertySheet.AttributeFormatter() {

            public String format(DomainObject obj, String attribute, PageState state) {
                Survey survey = (Survey) obj;
                if (survey.getResponsesAnonym() != null) {
                    if (survey.getResponsesAnonym().booleanValue() == true) {
                        return (String) SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.Yes").localize();
                    } else {
                        return (String) SurveyGlobalizationUtil.globalize("cms.contenttypes.ui.survey.No").localize();
                    }
                } else {
                    return (String) GlobalizationUtil.globalize("cms.ui.unknown").localize();
                }
            }
        });


        return sheet;
    }
}
