package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ItemSelectionModel;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SimpleOrganizationPropertyForm extends GenericOrganizationalUnitPropertyForm {
    
     private SimpleOrganizationPropertiesStep m_step;
    public static final String ID = "SimpleOrganization_edit";
    
    public SimpleOrganizationPropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }
    
    public SimpleOrganizationPropertyForm(
            final ItemSelectionModel itemModel,
                                          final SimpleOrganizationPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);        
    }
    
    @Override
    public void addWidgets() {
        super.addWidgets();
    }
    
    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        super.init(fse);
        super.initBasicWidgets(fse);
    }
    
    public void process(final FormSectionEvent fse) throws FormProcessException {
        super.process(fse);
    }
}
