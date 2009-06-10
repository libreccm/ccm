package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.ui.util.GlobalizationUtil;

import java.text.DateFormat;


/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRolePropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "edit";

    public OrganizationRolePropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new OrganizationRolePropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel), editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getOrganizationRolePropertySheet(itemModel));
    }

    public static Component getOrganizationRolePropertySheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.organizationrole.rolename"), OrganizationRole.ROLENAME);

        if(!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date"), ContentPage.LAUNCH_DATE, new DomainObjectPropertySheet.AttributeFormatter() {
                public String format(DomainObject item, String attribute, PageState state) {
                    ContentPage page = (ContentPage)item;
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
