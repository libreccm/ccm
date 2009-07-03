package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contenttypes.OrgaUnit2OrgaUnit;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrgaUnit2OrgaUnitSelectionModel extends ACSObjectSelectionModel {

    public OrgaUnit2OrgaUnitSelectionModel(BigDecimalParameter param) {
        super(OrgaUnit2OrgaUnit.class.getName(), OrgaUnit2OrgaUnit.BASE_DATA_OBJECT_TYPE, param);
    }

    public OrgaUnit2OrgaUnitSelectionModel(String itemClass, String objectType, BigDecimalParameter param) {
        super(itemClass, objectType, param);
    }

    public OrgaUnit2OrgaUnit getSelectedOU2OU(PageState state) {
        return (OrgaUnit2OrgaUnit) getSelectedObject(state);
    }
}
