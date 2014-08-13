package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ItemSelectionModel;

/**
 *
 * @author Jens Pelzetter
 * @version $Id: OrganizationPropertyForm.java 1611 2012-04-23 08:57:28Z
 jensp $
 */
public class OrganizationPropertyForm extends GenericOrganizationalUnitPropertyForm {

    private OrganizationPropertiesStep m_step;
    public static final String ID = "Organization_edit";

    public OrganizationPropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public OrganizationPropertyForm(
            final ItemSelectionModel itemModel,
            final OrganizationPropertiesStep step) {
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
