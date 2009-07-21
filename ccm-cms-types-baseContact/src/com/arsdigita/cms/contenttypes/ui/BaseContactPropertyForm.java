package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.cms.contenttypes.BaseContact;

import org.apache.log4j.Logger;

/**
 * Form for editing the basic properties of a basic contact.
 */
public class BaseContactPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(BaseContactPropertyForm.class);

    private BaseContactPropertiesStep m_step;

    public static final String PERSON = BaseContact.PERSON;
    public static final String ADRESS = BaseContact.ADDRESS;
    public static final String CONTACT_ENTRIES= BaseContact.CONTACT_ENTRIES;

    /**
     * ID of the form
     */
    public static final String ID = "BaseContact_edit";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public BaseContactPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of BaseContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public BaseContactPropertyForm(ItemSelectionModel itemModel, BaseContactPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

/*
        add(new Label((String)BaseContactGlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.basic_properties.description").localize())));
        TextArea description = new TextArea(DESCRIPTION);
        description.setRows(5);
        description.setCols(30);
        add(description);
*/
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        BaseContact baseContact = (BaseContact)super.initBasicWidgets(e);

//        data.put(DESCRIPTION, baseContact.getDescription());
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

        BaseContact baseContact = (BaseContact)super.processBasicWidgets(e);

        if((baseContact != null) && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
//            baseContact.setDescription((String)data.get(DESCRIPTION));

            baseContact.save();
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