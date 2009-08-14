package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ResearchNetwork;
import com.arsdigita.cms.contenttypes.ResearchNetworkGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import java.text.DateFormat;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkPropertiesStep extends SimpleEditStep {

    private final static Logger s_log = Logger.getLogger(ResearchNetworkPropertiesStep.class);
    public final static String EDIT_SHEET_NAME = "edit";

    public ResearchNetworkPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new ResearchNetworkPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel), editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getResearchNetworkPropertySheet(itemModel));
    }

    public static Component getResearchNetworkPropertySheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        //Display the properties
        sheet.add(ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.researchnetwork.ui.title"), ResearchNetwork.RESEARCHNETWORK_TITLE);
        sheet.add(ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.researchnetwork.ui.direction"), ResearchNetwork.RESEARCHNETWORK_DIRECTION);
        sheet.add(ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.researchnetwork.ui.coordination"), ResearchNetwork.RESEARCHNETWORK_COORDINATION);
        sheet.add(ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.researchnetwork.ui.website"), ResearchNetwork.RESEARCHNETWORK_WEBSITE);
        sheet.add(ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.researchnetwork.ui.description"), ResearchNetwork.RESEARCHNETWORK_DESCRIPTION);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date"), ContentPage.LAUNCH_DATE, new DomainObjectPropertySheet.AttributeFormatter() {

                public String format(DomainObject obj, String attribute, PageState state) {
                    ContentPage page = (ContentPage) obj;
                    if (page.getLaunchDate() != null) {
                        return DateFormat.getDateInstance(DateFormat.LONG).format(page.getLaunchDate());
                    } else {
                        return (String) GlobalizationUtil.globalize("cms.ui.unknown").localize();
                    }
                }
            });
        }

        return sheet;
    }
}
