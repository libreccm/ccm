package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.MembershipStatus;
import com.arsdigita.cms.contenttypes.OrganizationalUnitGlobalizationUtil;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class MembershipStatusPropertyForm extends FormSection implements FormInitListener, FormProcessListener, FormValidationListener, FormSubmissionListener {

    private final static Logger logger = Logger.getLogger(MembershipStatusPropertyForm.class);

    public final static String ID = "membershipStatusEditForm_edit";
    private ItemSelectionModel m_itemModel;
    private MembershipStatusSelectionModel m_statusModel;
    private TextField m_statusName;
    private SaveCancelSection m_saveCancelSection;

    public MembershipStatusPropertyForm(ItemSelectionModel itemModel, MembershipStatusSelectionModel statusModel) {
        super(new ColumnPanel(2));
        logger.debug("Constructor...");
        this.m_itemModel = itemModel;
        this.m_statusModel = statusModel;

        addWidgets();
        addSaveCancelSection();

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);
        addSubmissionListener(this);
        logger.debug("Constructor finished...");
    }

    protected void addWidgets() {
        logger.debug("Adding widgets...");
        add(new Label(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.membershipstatus.warning_changes_here_significant_for_all")), ColumnPanel.FULL_WIDTH);
        this.m_statusName = new TextField("statusName");
        this.m_statusName.addValidationListener(new NotNullValidationListener());
        add(new Label(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.membershipstatus.statusname")));
        add(this.m_statusName);
        logger.debug("widgets added");
    }

    protected void addSaveCancelSection() {
        logger.debug("adding savecancelsec...");
        this.m_saveCancelSection = new SaveCancelSection();
        try {
            this.m_saveCancelSection.getCancelButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_statusModel.isSelected(e.getPageState())) {
                        target.setButtonLabel(GlobalizationUtil.globalize("cancel"));
                    } else {
                        target.setButtonLabel(GlobalizationUtil.globalize("reset"));
                    }
                }
            });

            this.m_saveCancelSection.getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_statusModel.isSelected(e.getPageState())) {
                        target.setButtonLabel(GlobalizationUtil.globalize("save"));
                    } else {
                        target.setButtonLabel(GlobalizationUtil.globalize("create"));
                    }
                }
            });
        } catch(Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
        add(this.m_saveCancelSection, ColumnPanel.FULL_WIDTH);
        logger.debug("savecancelsec added.");
    }

    public SaveCancelSection getSaveCancelSection() {
        return this.m_saveCancelSection;
    }

    protected MembershipStatusSelectionModel getMembershipStatusSelectionModel() {
        return this.m_statusModel;
    }

    protected MembershipStatus createMembershipStatus(PageState state) {
        logger.debug("creating new membership status...");
        MembershipStatus status = new MembershipStatus();
        logger.debug("new membership status created.");
        return status;
    }

    protected void setMembershipStatusProperties(MembershipStatus status, FormSectionEvent e) {
        logger.debug("setting properties...");
        PageState state = e.getPageState();
        //FormData data = e.getFormData();

        status.setStatusName((String) m_statusName.getValue(state));
        status.save();        
        logger.debug("properties set");
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        logger.debug("init Listener invoked.");
        PageState state = e.getPageState();
        //FormData data = e.getFormData();

        setVisible(state, true);

        MembershipStatus status;
        if (m_statusModel.isSelected(state)) {
            status = m_statusModel.getSelectedMembershipStatus(state);
            try {
                m_statusName.setValue(state, status.getStatusName());
            } catch (IllegalStateException ex) {
                throw ex;
            }
        } else {
            m_statusName.setValue(state, null);
        }
        logger.debug("init Listener finished.");
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        logger.debug("process Listener invoked.");
        PageState state = e.getPageState();
        MembershipStatus status;

        if (this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            m_statusModel.clearSelection(state);
        } else {
            if (m_statusModel.isSelected(state)) {
                status = m_statusModel.getSelectedMembershipStatus(state);
            } else {
                status = createMembershipStatus(state);
            }
            setMembershipStatusProperties(status, e);
        }

        m_statusModel.clearSelection(state);
        init(e);
        logger.debug("process Listener finished.");
    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        //Nothing yet
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        logger.debug("submit Listener invoked.");
        if (this.m_saveCancelSection.getCancelButton().isSelected(e.getPageState())) {
            m_statusModel.clearSelection(e.getPageState());
            init(e);
            throw new FormProcessException("cancelled");
        }
        logger.debug("submit Listener finished.");
    }
}
