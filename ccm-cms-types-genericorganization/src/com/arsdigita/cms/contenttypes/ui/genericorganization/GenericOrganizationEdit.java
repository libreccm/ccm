package com.arsdigita.cms.contenttypes.ui.genericorganization;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.ui.util.GlobalizationUtil;

/**
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationEdit extends SimpleEditStep {

    public GenericOrganizationEdit(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey("edit");
        GenericOrganizationForm form = getForm(itemModel);
        add("edit", "Edit", new WorkflowLockedComponentAccess(form, itemModel), form.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getGenericOrganizationPropertiesSheet(itemModel));
    }

    protected GenericOrganizationForm getForm(ItemSelectionModel model) {
        return new GenericOrganizationEditForm(model, this);
    }

    public Component getGenericOrganizationPropertiesSheet(ItemSelectionModel model) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(model);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.organizationname"), GenericOrganization.ORGANIZATIONNAME);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.organizationnameaddendum"), GenericOrganization.ORGANIZATIONNAMEADDENDUM);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.description"), GenericOrganization.DESCRIPTION);

        return sheet;
    }
}
