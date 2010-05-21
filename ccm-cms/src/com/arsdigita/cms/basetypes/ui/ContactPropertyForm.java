package com.arsdigita.cms.basetypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.cms.basetypes.Contact;

import org.apache.log4j.Logger;

/**
 * Form for editing the basic properties of a basic contact.
 */
public class ContactPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(ContactPropertyForm.class);

    private ContactPropertiesStep m_step;

    public static final String PERSON = Contact.PERSON;
    public static final String ADRESS = Contact.ADDRESS;
    public static final String CONTACT_ENTRIES= Contact.CONTACT_ENTRIES;

    /**
     * ID of the form
     */
    public static final String ID = "Contact_edit";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public ContactPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of BaseContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public ContactPropertyForm(ItemSelectionModel itemModel, ContactPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

/*
        add(new Label((String)BaseContactGlobalizationUtil.globalize("cms.basetypes.ui.contact.basic_properties.description").localize())));
        TextArea description = new TextArea(DESCRIPTION);
        description.setRows(5);
        description.setCols(30);
        add(description);
*/
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        Contact contact = (Contact)super.initBasicWidgets(e);

//        data.put(DESCRIPTION, contact.getDescription());
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

        Contact contact = (Contact)super.processBasicWidgets(e);

        if((contact != null) && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
//            contact.setDescription((String)data.get(DESCRIPTION));

            contact.save();
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