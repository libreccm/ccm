package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contenttypes.MembershipStatus;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class MembershipStatusSelectionModel extends ACSObjectSelectionModel {

    public MembershipStatusSelectionModel(BigDecimalParameter param) {
        super(MembershipStatus.class.getName(), MembershipStatus.BASE_DATA_OBJECT_TYPE, param);
    }

    public MembershipStatusSelectionModel(String itemClass, String objectType, BigDecimalParameter parameter) {
        super(itemClass, objectType, parameter);
    }

    public MembershipStatus getSelectedMembershipStatus(PageState state) {
        return (MembershipStatus) getSelectedObject(state);
    }
}
