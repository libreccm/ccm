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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.contenttypes.GenericOrganizationGlobalizationUtil;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.cms.contenttypes.Person;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

/**
 * Form for editing a role.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRolePropertyForm extends FormSection implements FormInitListener, FormProcessListener, FormValidationListener, FormSubmissionListener {

    /**
     * Logger for this class.
     */
    public final static Logger logger = Logger.getLogger(OrganizationRolePropertyForm.class);
    /**
     * ID of the form
     */
    public final static String ID = "organizationrole_edit";
    //public final static String SSL_PROTOCOL = "https://";
    //public final static String HTTP_PROTOCOL = "http://";
    private ItemSelectionModel m_itemModel;
    private OrganizationRoleSelectionModel m_roleModel;
    private TextField m_rolename;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "organizationRole";

    /**
     * Creates an new instance of the form.
     *
     * @param itemModel
     * @param roleModel
     */
    public OrganizationRolePropertyForm(ItemSelectionModel itemModel, OrganizationRoleSelectionModel roleModel) {
        super(new ColumnPanel(2));
        this.m_itemModel = itemModel;
        this.m_roleModel = roleModel;

        addWidgets();
        addSaveCancelSection();

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);
        addSubmissionListener(this);
    }

    /**
     * Adds the widgets to the form. For choosing the associated person,
     * an ItemSearchWidget is used.
     */
    protected void addWidgets() {
        this.m_rolename = new TextField("rolename");
        this.m_rolename.addValidationListener(new NotNullValidationListener());
        add(new Label(GenericOrganizationGlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.rolename")));
        add(this.m_rolename);

        add(new Label(GenericOrganizationGlobalizationUtil.globalize("cms.contenttypes.ui.genericorganization.person")));
        /* Create the ItemSearchWidget. The ContentType.findByAssociatedObjecType
         * gets the ContentType of com.arsdigita.cms.contenttypes.Person and passes
         * it to the constructor of the ItemSearchWidget. The ItemSearchWidget will only
         * display object of type Person or derivated types.
         */
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType("com.arsdigita.cms.contenttypes.Person"));
        add(this.m_itemSearch);
    }

    /**
     * Adds the Save and Cancel buttons.
     */
    public void addSaveCancelSection() {
        this.m_saveCancelSection = new SaveCancelSection();
        try {
            this.m_saveCancelSection.getCancelButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_roleModel.isSelected(e.getPageState())) {
                        target.setButtonLabel("Cancel");
                    } else {
                        target.setButtonLabel("Reset");
                    }
                }
            });

            this.m_saveCancelSection.getSaveButton().addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    Submit target = (Submit) e.getTarget();
                    if (m_roleModel.isSelected(e.getPageState())) {
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
     *
     * @return The section with the Save and Cancel buttons.
     */
    public SaveCancelSection getSaveCancelSection() {
        return this.m_saveCancelSection;
    }

    /**
     *
     * @return The RoleSelectionModel of the form.
     */
    protected OrganizationRoleSelectionModel getRoleSelectionModel() {
        return this.m_roleModel;
    }

    /*protected Person getPerson(PageState s) {
    return (Person) m_itemModel.getSelectedObject(s);
    }*/
    /**
     *
     * @param s
     * @return * The organization which roles are edited.
     */
    protected GenericOrganization getOrganization(PageState s) {
        return (GenericOrganization) m_itemModel.getSelectedObject(s);
    }

    /**
     * Creates a new OrganizationRole.
     *
     * @param s
     * @return Newly created OrganizationRole.
     */
    protected OrganizationRole createOrganizationRole(PageState s) {
        //Person person = this.getPerson(s);
        //Assert.exists(person);
        GenericOrganization orga = this.getOrganization(s);
        Assert.exists(orga);
        OrganizationRole role = new OrganizationRole();
        role.setRoleOwner(orga);
        return role;
    }

    /**
     * Sets the properties of an instance of OrganizationRole.
     *
     * @param role
     * @param e
     */
    protected void setOrganizationRoleProperties(OrganizationRole role, FormSectionEvent e) {
        PageState state = e.getPageState();
        FormData data = e.getFormData();

        role.setRolename((String) m_rolename.getValue(state));
        role.setTargetItem((Person) data.get(ITEM_SEARCH));

        role.save();
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        setVisible(state, true);

        OrganizationRole role;
        if (m_roleModel.isSelected(state)) {
            role = m_roleModel.getSelectedRole(state);
            try {
                m_rolename.setValue(state, role.getRolename());
                data.put(ITEM_SEARCH, role.getTargetItem());
            } catch (IllegalStateException ex) {
                logger.error(ex.getMessage());
                throw ex;
            }
        } else {
            m_rolename.setValue(state, null);
            data.put(ITEM_SEARCH, null);
        }
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        OrganizationRole role;

        if (this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            m_roleModel.clearSelection(state);
        } else {
            if (m_roleModel.isSelected(state)) {
                role = m_roleModel.getSelectedRole(state);
            } else {
                role = createOrganizationRole(state);
            }
            setOrganizationRoleProperties(role, e);
        }

        m_roleModel.clearSelection(state);
        init(e);
    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        if (e.getFormData().get(ITEM_SEARCH) == null) {
            throw new FormProcessException("Person selection is required.");
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (this.m_saveCancelSection.getCancelButton().isSelected(e.getPageState())) {
            logger.debug("Cancel in submission listener");
            m_roleModel.clearSelection(e.getPageState());
            init(e);
            throw new FormProcessException("cancelled");
        }
    }
}