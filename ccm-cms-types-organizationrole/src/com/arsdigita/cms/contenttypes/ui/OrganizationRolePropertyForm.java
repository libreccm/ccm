package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormData;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRolePropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(OrganizationRolePropertyForm.class);
    private OrganizationRolePropertiesStep m_step;
    public static final String ROLENAME = OrganizationRole.ROLENAME;
    public static final String ID = "OrganizationRole_edit";

    public OrganizationRolePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public OrganizationRolePropertyForm(ItemSelectionModel itemModel, OrganizationRolePropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String)GlobalizationUtil.globalize("cms.contenttypes.ui.organizationrole.rolename").localize()));
        //ParameterModel rolenameParameter = new StringParameter(ROLENAME);
        //TextField rolename = new TextField(rolenameParameter);
        TextField rolename = new TextField(ROLENAME);
        add(rolename);
    }

    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        OrganizationRole role = (OrganizationRole)super.initBasicWidgets(fse);

        data.put(ROLENAME, role.getRoleName());
    }

    public void submitted(FormSectionEvent fse) {
        logger.error("submitted");
        if((m_step != null) &&
                (getSaveCancelSection().getCancelButton().isSelected(fse.getPageState()))) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();

        OrganizationRole role = (OrganizationRole)super.processBasicWidgets(fse);

        if((role != null) &&
                (getSaveCancelSection().getSaveButton().isSelected(fse.getPageState()))) {
            logger.error(String.format("Setting rolename to %s", data.get(ROLENAME)));
            role.setRoleName((String)data.get(ROLENAME));

            role.save();
        }

        if(m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
