package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contenttypes.Orga2OrgaUnit;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Orga2OrgaUnitSelectionModel extends ACSObjectSelectionModel {

    public Orga2OrgaUnitSelectionModel(BigDecimalParameter param) {
        super(Orga2OrgaUnit.class.getName(), Orga2OrgaUnit.BASE_DATA_OBJECT_TYPE, param);
    }

    public Orga2OrgaUnitSelectionModel(String itemClass, String objectType, BigDecimalParameter param) {
        super(itemClass, objectType, param);
    }

    public Orga2OrgaUnit getSelectedO2OU(PageState s) {
        return (Orga2OrgaUnit) getSelectedObject(s);
    }
}
