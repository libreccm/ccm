package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class GenericOrganizationPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(GenericOrganizationPropertyForm.class);

    private GenericOrganizationPropertiesStep m_step;

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

    public static final String ID = "GenericOrganization_edit";

    public GenericOrganizationPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    public GenericOrganizationPropertyForm(ItemSelectionModel itemModel, GenericOrganizationPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label(GlobalizationUtil.globalize("cms.contenttypes.genericorganization.ui.organizationname")));
        ParameterModel organizationNameParam = new StringParameter(ORGANIZATIONAME);
        TextField organizationName = new TextField(organizationNameParam);
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

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        GenericOrganization orga = (GenericOrganization)super.initBasicWidgets(e);

        data.put(ORGANIZATIONAME, orga.getOrganizationName());
        data.put(ORGANIZATIONNAMEADDENDUM, orga.getOrganizationNameAddendum());
        data.put(DESCRIPTION, orga.getDescription());
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        logger.error("proccessing...");
        FormData data = e.getFormData();

        GenericOrganization orga = (GenericOrganization)super.processBasicWidgets(e);

        if((orga != null)
                && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
            orga.setOrganizationName((String)data.get(ORGANIZATIONAME));
            orga.setOrganizationNameAddendum((String)data.get(ORGANIZATIONNAMEADDENDUM));
            orga.setDescription((String)data.get(DESCRIPTION));

            logger.error("Saving new values of orga");
            orga.save();
        }

        if(m_step != null) {
            m_step.maybeForwardToNextStep(e.getPageState());
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        logger.error("submitted");
        if((m_step != null) &&
                (getSaveCancelSection().getCancelButton().isSelected(e.getPageState()))) {
            m_step.cancelStreamlinedCreation(e.getPageState());
        }
    }
}