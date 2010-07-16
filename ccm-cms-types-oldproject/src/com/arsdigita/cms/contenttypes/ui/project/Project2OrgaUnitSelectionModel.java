package com.arsdigita.cms.contenttypes.ui.project;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contenttypes.Project2OrgaUnit;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Project2OrgaUnitSelectionModel extends ACSObjectSelectionModel {

    public Project2OrgaUnitSelectionModel(BigDecimalParameter param) {
        super(Project2OrgaUnit.class.getName(),
                Project2OrgaUnit.BASE_DATA_OBJECT_TYPE,
                param);
    }

    public Project2OrgaUnitSelectionModel(
            String itemClass,
            String objectType,
            BigDecimalParameter param) {
        super(itemClass, objectType, param);
    }

    public Project2OrgaUnit getSelectedP2OU(PageState s) {
        return (Project2OrgaUnit) getSelectedObject(s);
    }

}
