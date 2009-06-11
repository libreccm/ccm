package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.cms.contenttypes.OrganizationRole;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRoleSelectionModel extends ACSObjectSelectionModel {

    public OrganizationRoleSelectionModel(BigDecimalParameter param) {
        super(OrganizationRole.class.getName(), OrganizationRole.BASE_DATA_OBJECT_TYPE, param);
    }

    public OrganizationRoleSelectionModel(String itemClass, String objectType, BigDecimalParameter parameter) {
        super(itemClass, objectType, parameter);
    }

    public OrganizationRole getSelectedRole(PageState state) {
        return (OrganizationRole) getSelectedObject(state);
    }
}