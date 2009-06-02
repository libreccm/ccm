package com.arsdigita.cms.contenttypes.ui.genericorganization;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * Form for deleting a role.
 *
 * @author Jens Pelzetter
 */
public class RoleDeleteForm extends Form implements FormInitListener, FormSubmissionListener, FormProcessListener {

    private static final Logger logger = Logger.getLogger(RoleDeleteForm.class);
    /**
     * ItemSelectionModel for the organization.
     */
    protected ItemSelectionModel m_selectionOrganization;
    /**
     * ItemSelectionModle for the role
     */
    protected ItemSelectionModel m_selectionRole;
    /**
     * SaveCancelSection of this form
     */
    protected SaveCancelSection m_saveCancelSection;
    private Label m_roleNameLabel;

    /**
     * Constructor.
     *
     * @param selectionOrganization
     * @param selectionRole
     */
    public RoleDeleteForm(ItemSelectionModel selectionOrganization, ItemSelectionModel selectionRole) {
        super("RoleDeleteForm", new ColumnPanel(2));

        m_selectionOrganization = selectionOrganization;
        m_selectionRole = selectionRole;

        ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#ffffff");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        m_roleNameLabel = new Label("Role Name");
        add(m_roleNameLabel, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        addSaveCancelSection();

        addInitListener(this);
        addSubmissionListener(this);
        addProcessListener(this);
    }

    /**
     * Adds the SaveCancelSection of this form.
     *
     * @return
     */
    protected SaveCancelSection addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        m_saveCancelSection.getSaveButton().setButtonLabel("Delete");
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        return m_saveCancelSection;
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        OrganizationRole role = (OrganizationRole) m_selectionRole.getSelectedObject(state);

        if(role == null) {
            logger.error("No role selected");
        }
        else {
            m_roleNameLabel.setLabel(role.getRolename(), state);
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        if (m_saveCancelSection.getCancelButton().isSelected(state)) {
            throw new FormProcessException((String) GlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.submission_canceled").localize());
        }
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        GenericOrganization orga = (GenericOrganization) m_selectionOrganization.getSelectedObject(state);
        OrganizationRole role = (OrganizationRole) m_selectionRole.getSelectedObject(state);

        Assert.exists(orga, GenericOrganization.class);
        Assert.exists(role, OrganizationRole.class);

        orga.removeOrganizationRole(role);

        logger.info(String.format("role %s delete", m_selectionRole.getSelectedKey(state)));
    }
}