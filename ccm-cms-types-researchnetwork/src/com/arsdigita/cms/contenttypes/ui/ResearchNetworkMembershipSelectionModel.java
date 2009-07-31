package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contenttypes.ResearchNetworkMembership;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkMembershipSelectionModel extends ACSObjectSelectionModel {

    public ResearchNetworkMembershipSelectionModel(BigDecimalParameter param) {
        super(ResearchNetworkMembership.class.getName(), ResearchNetworkMembership.BASE_DATA_OBJECT_TYPE, param);
    }

    public ResearchNetworkMembershipSelectionModel(String itemClass, String objectType, BigDecimalParameter param) {
        super(itemClass, objectType, param);
    }

    public ResearchNetworkMembership getSelectedMembership(PageState state) {
        return (ResearchNetworkMembership) getSelectedObject(state);
    }

}
