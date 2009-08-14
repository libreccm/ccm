package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.contenttypes.GenericOrganizationGlobalizationUtil;
import com.arsdigita.bebop.Component;

import com.arsdigita.cms.util.GlobalizationUtil;
import java.text.DateFormat;
import org.apache.log4j.Logger;

/**
 * AuthoringStep for the basic properties of an organization (name, name addendum and
 * a short description).
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class GenericOrganizationPropertiesStep extends SimpleEditStep {

    private static final Logger logger = Logger.getLogger(GenericOrganizationPropertiesStep.class);

    /**
     * Name of the this edit sheet (Don't know if this this really needed.
     * It has the same value in almost all PropertiesStep classes)
     */
    public static final String EDIT_SHEET_NAME = "edit";

    /**
     * Constructor for the PropertiesStep.
     *
     * @param itemModel
     * @param parent
     */
    public GenericOrganizationPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new GenericOrganizationPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel), editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getGenericOrganizationPropertySheet(itemModel));
    }

    /**
     * Creates and returns the sheet for editing the basic properties
     * of an organization. (@see GenericOrganizationPropertyForm).
     *
     * @param itemModel
     * @return The sheet for editing the properties of the organization.
     */
    public static Component getGenericOrganizationPropertySheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GenericOrganizationGlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.organizationname"), GenericOrganization.ORGANIZATIONNAME);
        sheet.add(GenericOrganizationGlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.organizationnameaddendum"), GenericOrganization.ORGANIZATIONNAMEADDENDUM);
        sheet.add(GenericOrganizationGlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.description"), GenericOrganization.DESCRIPTION);

        if(!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date"), ContentPage.LAUNCH_DATE, new DomainObjectPropertySheet.AttributeFormatter() {

                public String format(DomainObject obj, String attribute, PageState state) {
                    ContentPage page = (ContentPage)obj;
                    if(page.getLaunchDate() != null) {
                        return DateFormat.getDateInstance(DateFormat.LONG).format(page.getLaunchDate());
                    }
                    else {
                        return (String)GlobalizationUtil.globalize("cms.ui.unknown").localize();
                    }
                }
            });
        }

        return sheet;
    }
}
