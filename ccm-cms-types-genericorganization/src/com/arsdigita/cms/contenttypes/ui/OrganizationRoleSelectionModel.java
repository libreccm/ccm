package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.cms.contenttypes.OrganizationRole;

/**
 * SelectionModel for OrganizationRole.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRoleSelectionModel extends ACSObjectSelectionModel {

    /**
     * Constructor taking a parameter.
     *
     * @param param
     */
    public OrganizationRoleSelectionModel(BigDecimalParameter param) {
        super(OrganizationRole.class.getName(), OrganizationRole.BASE_DATA_OBJECT_TYPE, param);
    }

    /**
     * Constructor taking an itemClass, an objectType and a parameter.
     *
     * @param itemClass
     * @param objectType
     * @param parameter
     */
    public OrganizationRoleSelectionModel(String itemClass, String objectType, BigDecimalParameter parameter) {
        super(itemClass, objectType, parameter);
    }

    /**
     *
     * @param state
     * @return The selected role.
     */
    public OrganizationRole getSelectedRole(PageState state) {
        return (OrganizationRole) getSelectedObject(state);
    }
}