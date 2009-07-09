/*
 * BaseContactEditPersonPropertyForm.java
 *
 * Created on 8. Juli 2009, 10:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

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
 *
 * @author quasi
 */
public class BaseContactEditPersonPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {
    
    private static final Logger logger = Logger.getLogger(BaseContactPropertyForm.class);

    private BaseContactPersonPropertiesStep m_step;

//    public static final String 
//    public static final String 
//    public static final String 

    /**
     * ID of the form
     */
    public static final String ID = "BaseContactEditPerson";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public BaseContactEditPersonPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }

    /**
     * Constrctor taking an ItemSelectionModel and an instance of BaseContactPropertiesStep.
     * 
     * @param itemModel
     * @param step
     */
    public BaseContactEditPersonPropertyForm(ItemSelectionModel itemModel, BaseContactPersonPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

/*
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
*/
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
//        BaseContact baseContact = (BaseContact)super.initBasicWidgets(e);

//        data.put(DESCRIPTION, baseContact.getDescription());
    }

    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

//        BaseContact baseContact = (BaseContact)super.processBasicWidgets(e);

//        if((baseContact != null) && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
//            baseContact.setDescription((String)data.get(DESCRIPTION));

//            baseContact.save();
//        }

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
