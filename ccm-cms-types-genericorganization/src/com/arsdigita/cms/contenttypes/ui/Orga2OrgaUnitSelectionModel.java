package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contenttypes.Orga2OrgaUnit;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 * The custom SelectionModel used by the {@see Orga2OrgaUnitPropertiesStep} and 
 * {@see Orga2OrgaUnitPropertyForm}.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Orga2OrgaUnitSelectionModel extends ACSObjectSelectionModel {

    /**
     *  Creates an new instance.
     *
     * @param param The parameter to use.
     */
    public Orga2OrgaUnitSelectionModel(BigDecimalParameter param) {
        super(Orga2OrgaUnit.class.getName(), Orga2OrgaUnit.BASE_DATA_OBJECT_TYPE, param);
    }

    /**
     *
     * @param itemClass
     * @param objectType
     * @param param
     */
    public Orga2OrgaUnitSelectionModel(String itemClass, String objectType, BigDecimalParameter param) {
        super(itemClass, objectType, param);
    }

    /**
     *
     * @param s
     * @return
     */
    public Orga2OrgaUnit getSelectedO2OU(PageState s) {
        return (Orga2OrgaUnit) getSelectedObject(s);
    }
}
