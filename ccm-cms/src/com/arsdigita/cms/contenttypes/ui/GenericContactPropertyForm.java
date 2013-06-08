package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.cms.contenttypes.GenericContact;

import org.apache.log4j.Logger;

/**
 * Form for editing the basic properties of a basic contact.
 */
public class GenericContactPropertyForm extends BasicPageForm 
                                        implements FormProcessListener, 
                                                   FormInitListener, 
                                                   FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(
                                         GenericContactPropertyForm.class);

    private GenericContactPropertiesStep m_step;

    public static final String PERSON = GenericContact.PERSON;
    public static final String ADRESS = GenericContact.ADDRESS;
    public static final String CONTACT_ENTRIES= GenericContact.CONTACT_ENTRIES;

    /**
     * ID of the form
     */
    public static final String ID = "Contact_edit";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public GenericContactPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constructor taking an ItemSelectionModel and an instance of 
     * BaseContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public GenericContactPropertyForm(ItemSelectionModel itemModel, 
                                      GenericContactPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    /**
     * 
     */
    @Override
    public void addWidgets() {
        super.addWidgets();

/*
        add(new Label(BaseContactGlobalizationUtil.globalize(
                      "cms.contenttypes.ui.contact.basic_properties.description")
                      )));
        TextArea description = new TextArea(DESCRIPTION);
        description.setRows(5);
        description.setCols(30);
        add(description);
*/
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        GenericContact contact = (GenericContact)super.initBasicWidgets(e);

//        data.put(DESCRIPTION, contact.getDescription());
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

        GenericContact contact = (GenericContact)super.processBasicWidgets(e);

        if((contact != null) 
           && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
//            contact.setDescription((String)data.get(DESCRIPTION));
            contact.save();
        }

        if(m_step != null) {
            m_step.maybeForwardToNextStep(e.getPageState());
        }
    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        if((m_step != null) 
           && (getSaveCancelSection().getCancelButton().isSelected(e.getPageState()))) {
            m_step.cancelStreamlinedCreation(e.getPageState());
        }
    }
}