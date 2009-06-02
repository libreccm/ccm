package com.arsdigita.cms.contenttypes.ui.genericorganization;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.ui.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 * A form section for editing an orgnization.
 *
 * @author Jens Pelzetter
 */
public abstract class GenericOrganizationForm extends FormSection implements FormInitListener, FormProcessListener, FormValidationListener {

    /**
     * ItemSelectionModel for the form section
     */
    protected ItemSelectionModel m_itemModel;
    /**
     * SaveCancelSection (Save and Cancel buttons) for this sections.
     */
    protected SaveCancelSection m_saveCancelSection;

    private Label m_script = new Label("<script language=\"javascript\" src=\"/javascript/manipulate-input.js\"></script>");

    /**
     * Organizationname.
     */
    public static final String ORGANIZATIONAME = GenericOrganization.ORGANIZATIONNAME;
    /**
     * Addedum
     */
    public static final String ORGANIZATIONNAMEADDENDUM = GenericOrganization.ORGANIZATIONNAMEADDENDUM;
    /**
     * Description
     */
    public static final String DESCRIPTION = GenericOrganization.DESCRIPTION;
    private static final Logger logger = Logger.getLogger(GenericOrganizationForm.class);

    /**
     * Creates the columnpanel, adds the widgets, the SaveCancelSection and the listeners.
     *
     * @param formName
     * @param itemModel
     */
    public GenericOrganizationForm(String formName, ItemSelectionModel itemModel) {
        super(new ColumnPanel(2));

        m_itemModel = itemModel;

        ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#ffffff");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        addWidgets();

        addSaveCancelSection();

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
    }

    /**
     * Adds the SavaCancelSection.
     */
    public void addSaveCancelSection() {
        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
    }

    /**
     *
     * @return The SaveCancelSection of the form section.
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    /**
     * Adds the widgets to the form.
     */
    protected void addWidgets() {
        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.genericorganization.ui.organizationname")));
        TextField organizationName = new TextField(ORGANIZATIONAME);
        organizationName.addValidationListener(new NotNullValidationListener());
        add(organizationName);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.genericorganization.ui.organizationnameaddendum")));
        TextField organizationNameAddendum = new TextField(ORGANIZATIONNAMEADDENDUM);
        add(organizationNameAddendum);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.genericorganzation.ui.description")));
        TextArea description = new TextArea(DESCRIPTION);
        description.setRows(5);
        description.setCols(30);
        add(description);
    }

    public abstract void init (FormSectionEvent e) throws FormProcessException;
    public abstract void process (FormSectionEvent e) throws FormProcessException;
    public abstract void validate (FormSectionEvent e) throws FormProcessException;

    /**
     * Inits the widgets.
     *
     * @param e
     * @return
     */
    public GenericOrganization initBasicWidgets(FormSectionEvent e) {
        Assert.exists(m_itemModel, ItemSelectionModel.class);

        FormData data = e.getFormData();
        PageState state = e.getPageState();
        GenericOrganization orga = (GenericOrganization)m_itemModel.getSelectedObject(state);

        if (orga != null) {
            data.put(ORGANIZATIONAME, orga.getOrganizationName());
            data.put(ORGANIZATIONNAMEADDENDUM, orga.getOrganizationNameAddendum());
            data.put(DESCRIPTION, orga.getDescription());
        }

        return orga;
    }

    /**
     * Processes the widgets.
     *
     * @param e
     * @return
     */
    public GenericOrganization processBasicWidgets(FormSectionEvent e) {
        Assert.exists(m_itemModel, ItemSelectionModel.class);

        FormData data = e.getFormData();
        PageState state = e.getPageState();
        GenericOrganization orga = (GenericOrganization)m_itemModel.getSelectedObject(state);

        if(orga != null) {
            orga.setOrganizationName((String)data.get(ORGANIZATIONAME));
            orga.setOrganizationNameAddendum((String)data.get(ORGANIZATIONNAMEADDENDUM));
            orga.setDescription((String)data.get(DESCRIPTION));
        }

        return orga;
    }

    /**
     * Creates a new organization.
     *
     * @param state
     * @return
     * @throws com.arsdigita.bebop.FormProcessException
     */
    public GenericOrganization createGenericOrganization(PageState state) throws FormProcessException {
        Assert.exists(m_itemModel, ItemSelectionModel.class);

        GenericOrganization orga = null;

        try {
            orga = (GenericOrganization)m_itemModel.createItem();
        } catch(ServletException e) {
            logger.error("ServletException: " + e.getMessage(), e);
            throw new FormProcessException(e.getMessage(), e);
        }

        if(m_itemModel.getSelectedKey(state) == null) {
            m_itemModel.setSelectedObject(state, orga);
        }

        return orga;
    }
}
