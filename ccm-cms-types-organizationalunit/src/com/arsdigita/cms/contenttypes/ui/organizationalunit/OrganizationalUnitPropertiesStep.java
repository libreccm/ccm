package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.OrganizationalUnit;
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
public class OrganizationalUnitPropertiesStep extends SimpleEditStep {

    private final static Logger logger = Logger.getLogger(OrganizationalUnitPropertiesStep.class);
    public final static String EDIT_SHEET_NAME = "edit";

    public OrganizationalUnitPropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {        
        super(itemModel, parent);       

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new OrganizationalUnitPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel), editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getOrganizationalUnitPropertySheet(itemModel));        
    }

    public static Component getOrganizationalUnitPropertySheet(ItemSelectionModel itemModel) {        
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.organizatialunit.unitname"), OrganizationalUnit.ORGANIZATIONALUNIT_NAME);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.organizatialunit.unitdescription"), OrganizationalUnit.ORGANIZATIONALUNIT_DESCRIPTION);

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