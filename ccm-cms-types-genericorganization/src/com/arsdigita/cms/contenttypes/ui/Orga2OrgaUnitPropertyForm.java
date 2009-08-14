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
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.contenttypes.GenericOrganizationGlobalizationUtil;
import com.arsdigita.cms.contenttypes.Orga2OrgaUnit;
import com.arsdigita.cms.contenttypes.OrganizationalUnit;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

/**
 * The form for adding, editing and removing associations between an organization
 * and an organizational unit.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Orga2OrgaUnitPropertyForm extends FormSection implements FormInitListener, FormProcessListener, FormValidationListener, FormSubmissionListener {

    private final static Logger logger = Logger.getLogger(Orga2OrgaUnitPropertyForm.class);
    /**
     * ID String.
     */
    public final static String ID = "orga2orgaunit_edit";
    private ItemSelectionModel m_itemModel;
    private Orga2OrgaUnitSelectionModel m_o2ouModel;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "orga2OrgaUnit";

    /**
     * Creates the new form using the ItemSelectionModel and Orga2OrgaUnitSelectionModel proviveded.
     *
     * @param itemModel
     * @param o2ouModel
     */
    public Orga2OrgaUnitPropertyForm(ItemSelectionModel itemModel, Orga2OrgaUnitSelectionModel o2ouModel) {
        super(new ColumnPanel(2));
        this.m_itemModel = itemModel;
        this.m_o2ouModel = o2ouModel;

        addWidgets();
        addSaveCancelSection();

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);
        addSubmissionListener(this);
    }

    /**
     * Creates the widgets for the form.
     */
    protected void addWidgets() {
        add(new Label(GenericOrganizationGlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.organizationalunit")));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType("com.arsdigita.cms.contenttypes.OrganizationalUnit"));
        add(this.m_itemSearch);       
    }

    /**
     * Creates the section with the save and the cancel button.
     */
    public void addSaveCancelSection() {
        this.m_saveCancelSection = new SaveCancelSection();
        try {
            this.m_saveCancelSection.getCancelButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_o2ouModel.isSelected(e.getPageState())) {
                        target.setButtonLabel("Cancel");
                    } else {
                        target.setButtonLabel("Reset");
                    }
                }
            });

            this.m_saveCancelSection.getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_o2ouModel.isSelected(e.getPageState())) {
                        target.setButtonLabel("Save");
                    } else {
                        target.setButtonLabel("Create");
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

    /**
     * Returns the Orga2OrgaUnitSelectionModel used.
     *
     * @return The Orga2OrgaUnitSelectionModel used.
     */
    protected Orga2OrgaUnitSelectionModel getO2OUSelectionModel() {
        return this.m_o2ouModel;
    }

    /**
     * Returns the Organization of the assocication displayed by the form.
     *
     * @param s
     * @return The Organization of the assocication displayed by the form.
     */
    protected GenericOrganization getOrganization(PageState s) {
        return (GenericOrganization) m_itemModel.getSelectedItem(s);
    }

    /**
     * Creates a new Orga2OrgaUnit assoication form the values of the form.
     *
     * @param s
     * @return
     */
    protected Orga2OrgaUnit createOrga2OrgaUnit(PageState s) {
        GenericOrganization orga = this.getOrganization(s);
        Assert.exists(orga);
        Orga2OrgaUnit o2ou = new Orga2OrgaUnit();
        o2ou.setUnitOwner(orga);
        return o2ou;
    }

    /**
     * Sets the properties of an Orga2OrgaUnit instance.
     *
     * @param o2ou
     * @param e
     */
    protected void setOrga2OrgaUnitProperties(Orga2OrgaUnit o2ou, FormSectionEvent e) {
        PageState state = e.getPageState();
        FormData data = e.getFormData();

        o2ou.setTargetItem((OrganizationalUnit) data.get(ITEM_SEARCH));

        o2ou.save();
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        setVisible(state, true);

        Orga2OrgaUnit o2ou;
        if (m_o2ouModel.isSelected(state)) {
            o2ou = m_o2ouModel.getSelectedO2OU(state);
            try {
                data.put(ITEM_SEARCH, o2ou.getTargetItem());
            } catch (IllegalStateException ex) {
                throw ex;
            }
        } else {
            data.put(ITEM_SEARCH, null);
        }
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        Orga2OrgaUnit o2ou;

        if (this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            this.m_o2ouModel.clearSelection(state);
        } else {
            if (this.m_o2ouModel.isSelected(state)) {
                o2ou = m_o2ouModel.getSelectedO2OU(state);
            } else {
                o2ou = createOrga2OrgaUnit(state);
            }
            setOrga2OrgaUnitProperties(o2ou, e);
        }

        m_o2ouModel.clearSelection(state);
        init(e);
    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        if (e.getFormData().get(ITEM_SEARCH) == null) {
            throw new FormProcessException("OrganiztionalUnit selection is required");
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (this.m_saveCancelSection.getCancelButton().isSelected(e.getPageState())) {
            m_o2ouModel.clearSelection(e.getPageState());
            init(e);
            throw new FormProcessException("cancelled");
        }
    }
}