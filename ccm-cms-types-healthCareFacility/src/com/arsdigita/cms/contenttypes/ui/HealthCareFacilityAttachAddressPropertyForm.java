/*
 * HealthCareFacilityEditAddressPropertyForm.java
 *
 * Created on 8. Juli 2009, 10:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.contenttypes.BaseAddress;
import com.arsdigita.cms.contenttypes.HealthCareFacility;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.contenttypes.util.HealthCareFacilityGlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class HealthCareFacilityAttachAddressPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {
    
    private static final Logger logger = Logger.getLogger(HealthCareFacilityPropertyForm.class);
    
    private HealthCareFacilityAddressPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "healthCareFacilityAddress";
    
    /**
     * ID of the form
     */
    public static final String ID = "HealthCareFacilityAttachAddress";
    
    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public HealthCareFacilityAttachAddressPropertyForm(ItemSelectionModel itemModel)    {
        this(itemModel, null);
    }
    
    /**
     * Constrctor taking an ItemSelectionModel and an instance of HealthCareFacilityPropertiesStep.
     *
     * @param itemModel
     * @param step
     */
    public HealthCareFacilityAttachAddressPropertyForm(ItemSelectionModel itemModel, HealthCareFacilityAddressPropertiesStep step) {
        super(ID, itemModel);
        addSubmissionListener(this);
        
        addSaveCancelSection();
        
        addInitListener(this);
        addSubmissionListener(this);
        
    }
    
    @Override
    public void addWidgets() {
        add(new Label((String)HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.select_address").localize()));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType("com.arsdigita.cms.contenttypes.BaseAddress"));
        add(this.m_itemSearch);
    }
    
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        HealthCareFacility healthCareFacility = (HealthCareFacility)getItemSelectionModel().getSelectedObject(state);
        
        setVisible(state, true);
        
        if (healthCareFacility != null) {
            data.put(ITEM_SEARCH, healthCareFacility.getAddress());
        }
    }
    
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        HealthCareFacility healthCareFacility = (HealthCareFacility)getItemSelectionModel().getSelectedObject(state);
        
        if (!this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            healthCareFacility.setAddress((BaseAddress)data.get(ITEM_SEARCH));
        }
        init(fse);
    }
    
    
    /**
     * Creates the section with the save and the cancel button.
     */
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {
                
                public void prepare(PrintEvent e) {
                    HealthCareFacility healthCareFacility = (HealthCareFacility)getItemSelectionModel().getSelectedObject(e.getPageState());
                    Submit target = (Submit) e.getTarget();
                    
                    if (healthCareFacility.getAddress() != null) {
                        target.setButtonLabel((String)HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.select_address.change").localize());
                    } else {
                        target.setButtonLabel((String)HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.select_address.add").localize());
                    }
                }
            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }
    
    public void validate(FormSectionEvent e) throws FormProcessException {
        if (e.getFormData().get(ITEM_SEARCH) == null) {
            throw new FormProcessException((String)HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.select_address.wrong_type").localize());
        }
    }
    
    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(e.getPageState())) {
            init(e);
            throw new FormProcessException((String)HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.select_address.cancelled").localize());
        }
    }
}