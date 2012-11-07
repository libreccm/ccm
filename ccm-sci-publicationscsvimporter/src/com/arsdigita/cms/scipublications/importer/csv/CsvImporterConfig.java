package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.categorization.Category;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CsvImporterConfig extends AbstractConfig {

    private static final Logger LOGGER = Logger.getLogger(CsvImporterConfig.class);
    private Parameter defaultCategoryId;
    private Parameter departmentCategoryIds;

    public CsvImporterConfig() {
        super();
        defaultCategoryId = new IntegerParameter("com.arsdigita.cms.scipublications.importer.csv.default_category_id",
                                                 Parameter.REQUIRED,
                                                 0);
        departmentCategoryIds = new StringParameter(
                "com.arsdigita.cms.scipublications.importer.csv.department_category_ids",
                Parameter.REQUIRED,
                "");

        register(defaultCategoryId);
        register(departmentCategoryIds);

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

    public String getDepartmentCategoryIds() {
        return (String) get(departmentCategoryIds);
    }

    public Map<String, Category> getDepartmentCategories() {
        final String categoryIds = getDepartmentCategoryIds();
                
        final Map<String, Category> categories = new HashMap<String, Category>();
        
        if ((categoryIds == null) || categoryIds.isEmpty()) {
            return categories;
        }

        final String[] departmentTokens = categoryIds.split(";");
        for (String departmentToken : departmentTokens) {
            processDepartmentToken(departmentToken, categories);
        }

        return categories;
    }

    private void processDepartmentToken(final String departmentToken, final Map<String, Category> categories) {
        final String[] tokens = departmentToken.split(":");

        if (tokens.length != 2) {
            LOGGER.warn("Failed to parse department categories id property. Invalid department token.");
            return;
        }

        final BigDecimal categoryId = new BigDecimal(tokens[1]);
        final Category category = new Category(categoryId);

        categories.put(tokens[0], category);
    }

}
