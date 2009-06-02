package com.arsdigita.cms.contenttypes.ui.genericorganization;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class RoleEditForm extends Form {

    private static final Logger logger = Logger.getLogger(RoleEditForm.class);

    private ItemSelectionModel m_selectionOrganization;
    private ItemSelectionModel m_selectionRole;

    //private BigDecimalParameter m_textParam;
    private GenericOrganizationViewRoles m_container;

    private SaveCancelSection m_saveCancelSection;

    public static final String ROLENAME = "rolename";

    public RoleEditForm(ItemSelectionModel selectionOrganization, ItemSelectionModel selectionRole) {
        this(selectionOrganization, selectionRole, null);
    }

    public RoleEditForm(ItemSelectionModel selectionOrganization, ItemSelectionModel selectionRole, GenericOrganizationViewRoles container) {
        super("RoleEditForm", new ColumnPanel(2));

        m_selectionOrganization = selectionOrganization;
        m_selectionRole = selectionRole;
        m_container = container;

        setMethod(Form.POST);
        setEncType("multipart/form-data");

        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#ffffff");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        addWidgets();
        addSaveCancelSection();

        addInitListener(new RoleInitListener());
        addSubmissionListener(new RoleSubmissionListener());
        addProcessListener(new RoleProcessListener());
    }

    protected SaveCancelSection addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        return m_saveCancelSection;
    }

    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    protected void addWidgets() {
        logger.info("Adding widgets for role form...");
        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.rolename")));
        TextField nameWidget = new TextField(new TrimmedStringParameter(ROLENAME));
        nameWidget.addValidationListener(new NotNullValidationListener());
        add(nameWidget);
    }

    private class RoleInitListener implements FormInitListener {
        public void init(FormSectionEvent event) throws FormProcessException {
            PageState state = event.getPageState();
            FormData data = event.getFormData();

            if(m_selectionRole.getSelectedKey(state) != null) {
                BigDecimal id = new BigDecimal(m_selectionRole.getSelectedKey(state).toString());

                try {
                    OrganizationRole role = new OrganizationRole(id);

                    data.put(ROLENAME, role.getRolename());
                } catch(DataObjectNotFoundException e) {
                    logger.error(String.format("Role(%d) could not be found", id));
                }
            }
        }
    }

    private class RoleSubmissionListener implements FormSubmissionListener {
        public void submitted(FormSectionEvent event) throws FormProcessException {
            PageState state = event.getPageState();

            if((m_saveCancelSection.getCancelButton().isSelected(state)) &&
                    (m_container != null)) {
                m_container.onlyShowComponent(state, GenericOrganizationViewRoles.ROLES_TABLE + m_container.getTypeIdStr());
                throw new FormProcessException((String)GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.submission_canceled").localize());
            }
        }
    }

    private class RoleProcessListener implements FormProcessListener {
        public void process(FormSectionEvent event) throws FormProcessException {
            logger.info("Processing edit event...");
            PageState state = event.getPageState();
            FormData data = event.getFormData();

            BigDecimal id = new BigDecimal(m_selectionOrganization.getSelectedKey(state).toString());
            GenericOrganization orga = null;
            try {
                orga = new GenericOrganization(id);
            } catch(DataObjectNotFoundException e) {
                throw new UncheckedWrapperException(e);
            }

            //Get role or create new one if role is not existing yet
            OrganizationRole role = (OrganizationRole)m_selectionRole.getSelectedObject(state);
            if(role == null) {
                role = createRole(event, orga);
                orga.addOrganizationRole(role);
            }

            role.setRolename((String)data.get(OrganizationRole.ROLENAME));
        }
    }

    protected OrganizationRole createRole(FormSectionEvent event, GenericOrganization orga) {
        logger.info("creating new role...");

        PageState state = event.getPageState();
        FormData data = event.getFormData();

        OrganizationRole role = new OrganizationRole();

        role.setRolename((String)data.get(ROLENAME));
        role.setName(orga.getName() + ": " + (String)data.get(OrganizationRole.ROLENAME));

        logger.info("new role created");
        return role;
    }

    @Override
    public void register(Page page) {
        super.register(page);
    }
}
