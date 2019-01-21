package org.librecms.pages;

import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.subsite.Site;
import com.arsdigita.web.Application;

import java.math.BigDecimal;

public class Pages extends Application {

    public static final String BASE_DATA_OBJECT_TYPE = "org.librecms.cms.Pages";

    public static final String SITE = "site";
    public static final String DOMAIN_CATEGORY = "domainCategory";

    public Pages(final OID oid) {
        super(oid);
    }

    public Pages(final DataObject dataObject) {
        super(dataObject);
    }

    public Pages(final BigDecimal pagesId) throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, pagesId));
    }

    public Site getSite() {

        final Object obj = get(SITE);

        if (obj == null) {
            return null;
        } else {
            return (Site) obj;
        }

    }

    public void setSite(final Site site) {

        setAssociation(SITE, site);
    }

    public Domain getCategoryDomain() {

        final Object dataObject = get(DOMAIN_CATEGORY);

        if (dataObject == null) {
            return null;
        } else {
            final Category domainCategory = (Category) dataObject;

            return Domain.findByModel(domainCategory);
        }
    }

    protected void setCategoryDomain(final Domain domain) {

        final Category domainCategory;
        if (domain == null) {
            domainCategory = null;
        } else {
            domainCategory = domain.getModel();
        }
        
        setAssociation(DOMAIN_CATEGORY, domainCategory);
    }

    @Override
    public String getServletPath() {
        return "/";
    }

}
