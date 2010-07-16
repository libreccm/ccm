package com.arsdigita.cms.contenttypes.ui.project;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contenttypes.Project2Person;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Project2PersonSelectionModel extends ACSObjectSelectionModel {

    public Project2PersonSelectionModel(BigDecimalParameter param) {
        super(Project2Person.class.getName(),
                Project2Person.BASE_DATA_OBJECT_TYPE,
                param);
    }

    public Project2PersonSelectionModel(
            String itemClass,
            String objectType,
            BigDecimalParameter param) {
        super(itemClass, objectType, param);
    }

    public Project2Person getSelectedP2P(PageState s) {
        return (Project2Person) getSelectedObject(s);
    }

}
