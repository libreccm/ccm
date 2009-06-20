package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.OrganizationalUnit;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationalUnitPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private final static Logger logger = Logger.getLogger(OrganizationalUnitPropertyForm.class);
    private OrganizationalUnitPropertiesStep m_step;
    public final static String ORGANIZATIONALUNIT_NAME = OrganizationalUnit.ORGANIZATIONALUNIT_NAME;
    public final static String ORGANIZATIONALUNIT_DESCRIPTION = OrganizationalUnit.ORGANIZATIONALUNIT_DESCRIPTION;
    public final static String ID = "OrganizationalUnit_edit";

    public OrganizationalUnitPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public OrganizationalUnitPropertyForm(ItemSelectionModel itemModel, OrganizationalUnitPropertiesStep step) {
        super(ID, itemModel);
        this.m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {        
        super.addWidgets();

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.organizationalunit.ui.unitname")));
        ParameterModel orgaUnitNameParam = new StringParameter(ORGANIZATIONALUNIT_NAME);
        TextField orgaUnitName = new TextField(orgaUnitNameParam);
        orgaUnitName.addValidationListener(new NotNullValidationListener());
        add(orgaUnitName);

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.organizationalunit.ui.description")));
        TextArea description = new TextArea(ORGANIZATIONALUNIT_DESCRIPTION);
        description.setRows(5);
        description.setCols(30);
        add(description);        
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {        
        FormData data = e.getFormData();
        OrganizationalUnit orgaUnit = (OrganizationalUnit) super.initBasicWidgets(e);

        data.put(ORGANIZATIONALUNIT_NAME, orgaUnit.getOrganizationalUnitName());
        data.put(ORGANIZATIONALUNIT_DESCRIPTION, orgaUnit.getOrganizationalUnitDescription());       
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {        
        FormData data = e.getFormData();

        OrganizationalUnit orgaUnit = (OrganizationalUnit) super.processBasicWidgets(e);

        if ((orgaUnit != null) && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
            orgaUnit.setOrganizationalUnitName((String) data.get(ORGANIZATIONALUNIT_NAME));
            orgaUnit.setOrganizationalUnitDescription((String) data.get(ORGANIZATIONALUNIT_DESCRIPTION));

            orgaUnit.save();
        }

        if (this.m_step != null) {
            this.m_step.maybeForwardToNextStep(e.getPageState());
        }        
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {       
        if ((this.m_step != null) &&
                (getSaveCancelSection().getCancelButton().isSelected(e.getPageState()))) {
            this.m_step.cancelStreamlinedCreation(e.getPageState());
        }        
    }
}
