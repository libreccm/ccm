package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.cms.contenttypes.HealthCareFacility;

import org.apache.log4j.Logger;

/**
 * Form for editing the basic properties of a basic contact.
 */
public class HealthCareFacilityPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(HealthCareFacilityPropertyForm.class);

    private HealthCareFacilityPropertiesStep m_step;

    public static final String ADRESS = HealthCareFacility.ADDRESS;
    public static final String CONTACTS= HealthCareFacility.CONTACTS;

    /**
     * ID of the form
     */
    public static final String ID = "HealthCareFacility_edit";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public HealthCareFacilityPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of HealthCareFacilityPropertiesStep.
     * 
     * 
     * @param itemModel
     * @param step
     */
    public HealthCareFacilityPropertyForm(ItemSelectionModel itemModel, HealthCareFacilityPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

/*
        add(new Label((String)HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.basic_properties.description").localize())));
        TextArea description = new TextArea(DESCRIPTION);
        description.setRows(5);
        description.setCols(30);
        add(description);
*/
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        HealthCareFacility healthCareFacility = (HealthCareFacility)super.initBasicWidgets(e);

//        data.put(DESCRIPTION, healthCareFacility.getDescription());
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

        HealthCareFacility healthCareFacility = (HealthCareFacility)super.processBasicWidgets(e);

        if((healthCareFacility != null) && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
//            healthCareFacility.setDescription((String)data.get(DESCRIPTION));

            healthCareFacility.save();
        }

        if(m_step != null) {
            m_step.maybeForwardToNextStep(e.getPageState());
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if((m_step != null) && (getSaveCancelSection().getCancelButton().isSelected(e.getPageState()))) {
            m_step.cancelStreamlinedCreation(e.getPageState());
        }
    }
}