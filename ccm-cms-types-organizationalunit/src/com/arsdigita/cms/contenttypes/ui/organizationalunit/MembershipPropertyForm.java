package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
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
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Membership;
import com.arsdigita.cms.contenttypes.MembershipStatus;
import com.arsdigita.cms.contenttypes.OrganizationalUnit;
import com.arsdigita.cms.contenttypes.OrganizationalUnitGlobalizationUtil;
import com.arsdigita.cms.contenttypes.Member;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class MembershipPropertyForm extends FormSection implements FormInitListener, FormProcessListener, FormValidationListener, FormSubmissionListener {

    private final static Logger logger = Logger.getLogger(MembershipPropertyForm.class);
    public final static String ID = "membership_edit";
    private ItemSelectionModel m_itemModel;
    private MembershipSelectionModel m_membershipModel;
    private ItemSearchWidget m_personSearch;
    private final static String PERSON_SEARCH = "membership";
    private ChangeableSingleSelect m_status;
    private com.arsdigita.bebop.form.Date m_from;
    private com.arsdigita.bebop.form.Date m_to;
    private SaveCancelSection m_saveCancelSection;
    private MembershipPropertiesStep propertiesStep;

    public MembershipPropertyForm(ItemSelectionModel itemModel, MembershipSelectionModel membershipModel) {
        super(new ColumnPanel(2));
        this.m_itemModel = itemModel;
        this.m_membershipModel = membershipModel;

        addWidgets();
        addSaveCancelSection();

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);
        addSubmissionListener(this);
    }

    protected void setPropertiesStep(MembershipPropertiesStep propertiesStep) {
        this.propertiesStep = propertiesStep;
    }

    protected void addWidgets() {
        logger.debug("adding widgets...");

        add(new Label(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.orgnizationalunit.membership.Person")));
        this.m_personSearch = new ItemSearchWidget(PERSON_SEARCH, ContentType.findByAssociatedObjectType("com.arsdigita.cms.contenttypes.Person"));
        add(this.m_personSearch);

        add(new Label(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.orgnizationalunit.membership.Status")));
        ParameterModel statusParam = new StringParameter((Membership.STATUS));
        this.m_status = new ChangeableSingleSelect(statusParam);
        add(this.m_status);
        //MembershipStatusCollection statusValues = MembershipStatusCollection.getMembershipStatusCollection();        
        this.addStatusOptions();

        add(new Label(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.membership.from")));
        ParameterModel fromParam = new DateParameter(Membership.FROM);
        //fromParam.addParameterListener(new NotNullValidationListener());
        this.m_from = new com.arsdigita.bebop.form.Date(fromParam);
        add(this.m_from);

        add(new Label(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.membership.to")));
        ParameterModel toParam = new DateParameter(Membership.TO);
        this.m_to = new com.arsdigita.bebop.form.Date(toParam);
        add(this.m_to);
    }

    private void addStatusOptions() {
        DataCollection statusValues = SessionManager.getSession().retrieve(MembershipStatus.BASE_DATA_OBJECT_TYPE);
        while (statusValues.next()) {
            //this.m_status.addOption(new Option(statusValues.getMembershipStatusId().toString(), statusValues.getMembershipStatusName()));
            MembershipStatus status = (MembershipStatus) DomainObjectFactory.newInstance(statusValues.getDataObject());
            this.m_status.addOption(new Option(status.getID().toString(), status.getStatusName()));
        }
    }

    protected void addSaveCancelSection() {
        this.m_saveCancelSection = new SaveCancelSection();
        try {
            this.m_saveCancelSection.getCancelButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_membershipModel.isSelected(e.getPageState())) {
                        target.setButtonLabel(GlobalizationUtil.globalize("cancel"));
                    } else {
                        target.setButtonLabel(GlobalizationUtil.globalize("reset"));
                    }
                }
            });

            this.m_saveCancelSection.getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_membershipModel.isSelected(e.getPageState())) {
                        target.setButtonLabel(GlobalizationUtil.globalize("save"));
                    } else {
                        target.setButtonLabel(GlobalizationUtil.globalize("create"));
                    }
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
        add(this.m_saveCancelSection, ColumnPanel.FULL_WIDTH);
    }

    public SaveCancelSection getSaveCancelSection() {
        return this.m_saveCancelSection;
    }

    protected MembershipSelectionModel getMembershipSelectionModel() {
        return this.m_membershipModel;
    }

    protected OrganizationalUnit getOrganizationalUnit(PageState state) {
        return (OrganizationalUnit) m_itemModel.getSelectedItem(state);
    }

    protected Membership createMembership(PageState state) {
        OrganizationalUnit ou = this.getOrganizationalUnit(state);
        logger.debug("Owning ou: " + ou.getOrganizationalUnitName());
        Assert.exists(ou);
        Membership membership = new Membership();
        membership.setMembershipOwner(ou);
        logger.debug("set ou to: " + membership.getMembershipOwner().getOrganizationalUnitName());
        return membership;
    }

    protected void setMembershipProperties(Membership membership, FormSectionEvent event) {
        PageState state = event.getPageState();
        FormData data = event.getFormData();

        membership.setTargetItem((Member) data.get(PERSON_SEARCH));

        MembershipStatus status = new MembershipStatus(new BigDecimal((String) this.m_status.getValue(state)));
        logger.debug("this.m_status.getValues() = " + this.m_status.getValue(state));
        membership.setStatus(status);

        membership.setFrom((Date) this.m_from.getValue(state));
        membership.setTo((Date) this.m_to.getValue(state));

        membership.save();
        logger.debug("Adding membership to collection of orgaunit...");
        OrganizationalUnit orgaunit = this.getOrganizationalUnit(state);
        //orgaunit.addMembership(membership);
        logger.debug("saving orgaunit...");
        //this.getOrganizationalUnit(state).save();
        logger.debug("done.");
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        logger.debug("Init listener invoked.");

        FormData data = e.getFormData();
        PageState state = e.getPageState();

        //Iterator it = this.m_status.getOptions();
        //while(it.hasNext()) {
        //    Option o = (Option) it.next();
        //    //this.m_status.removeOption(o);
        //}

        this.m_status.clearOptions();
        this.addStatusOptions();

        setVisible(state, true);

        Membership membership;
        if (m_membershipModel.isSelected(state)) {
            membership = m_membershipModel.getSelectedMembership(state);
            try {
                data.put(PERSON_SEARCH, membership.getTargetItem());
                data.put(Membership.STATUS, membership.getStatus());
                data.put(Membership.FROM, membership.getFrom());
                data.put(Membership.TO, membership.getTo());
            } catch (IllegalStateException ex) {
                throw ex;
            }
        } else {
            data.put(PERSON_SEARCH, null);
        }
        logger.debug("init listener finished.");
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        Membership membership;

        if (this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            this.m_membershipModel.clearSelection(state);
        } else {
            if (this.m_membershipModel.isSelected(state)) {
                membership = m_membershipModel.getSelectedMembership(state);
            } else {
                membership = createMembership(state);
            }
            setMembershipProperties(membership, e);
        }

        this.m_membershipModel.clearSelection(state);
        init(e);
    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        if (e.getFormData().get(PERSON_SEARCH) == null) {
            throw new FormProcessException("Person selection is required");
        }

        if (e.getFormData().get(Membership.STATUS) == null) {
            throw new FormProcessException("Status of membership is required");
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (this.m_saveCancelSection.getCancelButton().isSelected(e.getPageState())) {
            m_membershipModel.clearSelection(e.getPageState());
            init(e);
            throw new FormProcessException("cancelled");
        }
    }
}
