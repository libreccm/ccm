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
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.OrgaUnit2OrgaUnit;
import com.arsdigita.cms.contenttypes.OrganizationalUnit;
import com.arsdigita.cms.contenttypes.OrganizationalUnitGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrgaUnit2OrgaUnitPropertyForm extends FormSection implements FormInitListener, FormProcessListener, FormValidationListener, FormSubmissionListener {

    private final static Logger logger = Logger.getLogger(OrgaUnit2OrgaUnitPropertyForm.class);
    public final static String ID = "orgaUnit2OrgaUnit_edit";
    private ItemSelectionModel m_itemModel;
    private OrgaUnit2OrgaUnitSelectionModel m_ou2ouModel;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "orgaUnit2OrgaUnit";

    public OrgaUnit2OrgaUnitPropertyForm(ItemSelectionModel itemModel, OrgaUnit2OrgaUnitSelectionModel ou2ouModel) {
        super(new ColumnPanel(2));
        this.m_itemModel = itemModel;
        this.m_ou2ouModel = ou2ouModel;


        addWidgets();
        addSaveCancelSection();

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);
        addSubmissionListener(this);
    }

    protected void addWidgets() {
        add(new Label(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit")));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType("com.arsdigita.cms.contenttypes.OrganizationalUnit"));
        add(this.m_itemSearch);
    }

    protected void addSaveCancelSection() {
        this.m_saveCancelSection = new SaveCancelSection();
        try {
            this.m_saveCancelSection.getCancelButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_ou2ouModel.isSelected(e.getPageState())) {
                        target.setButtonLabel(GlobalizationUtil.globalize("cancel"));
                    } else {
                        target.setButtonLabel(GlobalizationUtil.globalize("reset"));
                    }
                }
            });

            this.m_saveCancelSection.getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_ou2ouModel.isSelected(e.getPageState())) {
                        target.setButtonLabel(GlobalizationUtil.globalize("save"));
                    } else {
                        target.setButtonLabel(GlobalizationUtil.globalize("create"));
                    }
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH);
    }

    /**
     *  Returns the SavaCancelSection of the form.
     *
     * @return The SavaCancelSection of the form.
     */
    public SaveCancelSection getSaveCancelSection() {
        return this.m_saveCancelSection;
    }

    protected OrgaUnit2OrgaUnitSelectionModel getOU2OUSelectionModel() {
        return this.m_ou2ouModel;
    }

    protected OrganizationalUnit getOrganizationalUnit(PageState state) {
        return (OrganizationalUnit) this.m_itemModel.getSelectedItem(state);
    }

    protected OrgaUnit2OrgaUnit createOrgaUnit2OrgaUnit(PageState state) {
        OrganizationalUnit ou = this.getOrganizationalUnit(state);
        Assert.exists(ou);
        OrgaUnit2OrgaUnit ou2ou = new OrgaUnit2OrgaUnit();
        ou2ou.setUnitOwner(ou);
        return ou2ou;
    }

    protected void setOrgaUnit2OrgaUnitProperties(OrgaUnit2OrgaUnit ou2ou, FormSectionEvent event) {        
        FormData data = event.getFormData();

        ou2ou.setTargetItem((OrganizationalUnit) data.get(ITEM_SEARCH));

        ou2ou.save();
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        setVisible(state, true);

        OrgaUnit2OrgaUnit ou2ou;
        if (this.m_ou2ouModel.isSelected(state)) {
            ou2ou = this.m_ou2ouModel.getSelectedOU2OU(state);
            try {
                data.put(ITEM_SEARCH, ou2ou.getTargetItem());
            } catch(IllegalStateException ex) {
                throw ex;
            }
        } else {
            data.put(ITEM_SEARCH, null);
        }
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        OrgaUnit2OrgaUnit ou2ou;

        if (this.m_saveCancelSection.getCancelButton().isSelected(state)) {
            this.m_ou2ouModel.clearSelection(state);
        } else {
            if (this.m_ou2ouModel.isSelected(state)) {
                ou2ou = m_ou2ouModel.getSelectedOU2OU(state);
            } else {
                ou2ou = createOrgaUnit2OrgaUnit(state);
            }
            setOrgaUnit2OrgaUnitProperties(ou2ou, e);
        }

        this.m_ou2ouModel.clearSelection(state);
        this.init(e);
    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        if (e.getFormData().get(ITEM_SEARCH) == null) {
            throw new FormProcessException("OrganizationalUnit selection is requiered");
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (this.m_saveCancelSection.getCancelButton().isSelected(e.getPageState())) {
            this.m_ou2ouModel.clearSelection(e.getPageState());
            this.init(e);
            throw new FormProcessException("cancelled");
        }
    }
}
