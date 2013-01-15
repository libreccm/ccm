package com.arsdigita.cms.scipublications.importer.bibtex;

import com.arsdigita.categorization.Category;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class BibTeXImporterConfig extends AbstractConfig {

    private Parameter defaultCategoryId;

    public BibTeXImporterConfig() {
        super();

        defaultCategoryId = new IntegerParameter("com.arsdigita.cms.scipublications.importer.bibtex.default_category_id",
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