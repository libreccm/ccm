package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contenttypes.Membership;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class MembershipSelectionModel extends ACSObjectSelectionModel {

    public MembershipSelectionModel(BigDecimalParameter param) {
        super(Membership.class.getName(), Membership.BASE_DATA_OBJECT_TYPE, param);
    }

    public MembershipSelectionModel(String itemClass, String objectType, BigDecimalParameter param) {
        super(itemClass, objectType, param);
    }

    public Membership getSelectedMembership(PageState state) {
        return (Membership) getSelectedObject(state);
    }
}
