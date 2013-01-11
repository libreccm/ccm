package com.arsdigita.cms.scipublications.importer.ris;

import com.arsdigita.categorization.Category;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisImporterConfig extends AbstractConfig {

    private Parameter defaultCategoryId;

    public RisImporterConfig() {
        super();
        defaultCategoryId = new IntegerParameter("com.arsdigita.cms.scipublications.importer.ris.default_category_id",
                                                 Parameter.REQUIRED,
                                                 0);

        register(defaultCategoryId);

        loadInfo();
    }

    public Integer getDefaultCategoryId() {
        return (Integer) get(defaultCategoryId);
    }

    public Category getDefaultCategory() {
        final Integer categoryId = getDefaultCategoryId();

        if (categoryId == 0) {
            return null;
        } else {
            return new Category(new BigDecimal(categoryId));
        }
    }

}
