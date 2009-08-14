package com.arsdigita.cms.contenttypes.ui;

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
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Person;
import com.arsdigita.cms.contenttypes.ResearchNetwork;
import com.arsdigita.cms.contenttypes.ResearchNetworkGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ResearchNetworkMembership;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkMembershipPropertyForm  extends FormSection implements FormInitListener, FormProcessListener, FormValidationListener, FormSubmissionListener {

    private final static Logger s_log = Logger.getLogger(ResearchNetworkMembershipPropertyForm.class);
    public final static String ID = "researchnetwork_membership_edit";
    private ItemSelectionModel m_itemModel;
    private ResearchNetworkMembershipSelectionModel m_membershipModel;
    private ItemSearchWidget m_personSearch;
    private final static String PERSON_SEARCH = "membership";
    private SaveCancelSection m_saveCancelSection;
    private ResearchNetworkMembershipPropertiesStep m_properties_step;

    public ResearchNetworkMembershipPropertyForm(ItemSelectionModel itemModel, ResearchNetworkMembershipSelectionModel membershipModel) {
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

    protected void setPropertiesStep(ResearchNetworkMembershipPropertiesStep propertiesStep) {
        this.m_properties_step = propertiesStep;
    }

    protected void addWidgets() {
        add(new Label(ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.ui.researchnetwork.membership.person")));
        this.m_personSearch = new ItemSearchWidget(PERSON_SEARCH, ContentType.findByAssociatedObjectType("com.arsdigita.cms.contenttypes.Person"));
        add(this.m_personSearch);
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
                    if(m_membershipModel.isSelected(e.getPageState())) {
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
    }

    public SaveCancelSection getSaveCancelSection() {
        return this.m_saveCancelSection;
    }

    protected ResearchNetworkMembershipSelectionModel getMembershipSelectionModel() {
        return this.m_membershipModel;
    }

    protected ResearchNetwork getResearchNetwork(PageState state) {
        return (ResearchNetwork) m_itemModel.getSelectedItem(state);
    }

    protected ResearchNetworkMembership createMembership(PageState state) {
        ResearchNetwork network = this.getResearchNetwork(state);
        s_log.debug ("Owning network " + network.getResearchNetworkTitle());
        Assert.exists(network);
        ResearchNetworkMembership membership = new ResearchNetworkMembership();
        membership.setResearchNetworkMembershipOwner(network);
        s_log.debug("set network to: " + membership.getResearchNetworkMembershipOwner().getResearchNetworkTitle());
        return membership;
    }

    protected void setMembershipProperties(ResearchNetworkMembership membership, FormSectionEvent event) {
        PageState state = event.getPageState();
        FormData data = event.getFormData();

        membership.setTargetItem((Person) data.get(PERSON_SEARCH));

        membership.save();
    }

    public void init(FormSectionEvent event) throws FormProcessException {
        FormData data = event.getFormData();
        PageState state = event.getPageState();

        setVisible(state, true);

        ResearchNetworkMembership membership;
        if (m_membershipModel.isSelected(state)) {
            membership = m_membershipModel.getSelectedMembership(state);
            try {
                data.put(PERSON_SEARCH, membership.getTargetItem());
            } catch(IllegalStateException ex) {
                throw ex;
            }
        } else {
            data.put(PERSON_SEARCH, null);
        }
    }

    public void process(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();
        ResearchNetworkMembership membership;

        if (this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            this.m_membershipModel.clearSelection(state);
        } else {
            if (this.m_membershipModel.isSelected(state)) {
                membership = m_membershipModel.getSelectedMembership(state);
            } else {
                membership = createMembership(state);
            }
            setMembershipProperties(membership, event);
        }

        this.m_membershipModel.clearSelection(state);
        init(event);
    }

    public void validate(FormSectionEvent event) throws FormProcessException {
        if (event.getFormData().get(PERSON_SEARCH) == null) {
            throw new FormProcessException("Person selection is required");
        }
    }

    public void submitted(FormSectionEvent event) throws FormProcessException {
        if (this.m_saveCancelSection.getCancelButton().isSelected(event.getPageState())) {
            m_membershipModel.clearSelection(event.getPageState());
            init(event);
            throw new FormProcessException("cancelled");
        }
    }
}
