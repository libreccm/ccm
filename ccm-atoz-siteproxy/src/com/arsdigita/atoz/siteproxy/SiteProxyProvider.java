package com.arsdigita.atoz.siteproxy;

import com.arsdigita.atoz.AtoZGenerator;
import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.categorization.Category;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

public class SiteProxyProvider extends AtoZProvider {

    public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.atoz.siteproxy.SiteProxyProvider";

    public static final String CATEGORY = "category";

    private static final String ATOMIC_SITEPROXY_ENTRIES_QUERY = 
                                "com.arsdigita.atoz.getAtomicSiteProxyEntries";

    public SiteProxyProvider() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SiteProxyProvider(String type) {
        super(type);
    }

    public SiteProxyProvider(DataObject obj) {
        super(obj);
    }

    public SiteProxyProvider(OID oid) {
        super(oid);
    }

    public AtoZGenerator getGenerator() {
        return new SiteProxyGenerator(this);
    }

    public static SiteProxyProvider create(String title,
                                           String description, 
                                           Category category) {
        SiteProxyProvider siteProxyProvider = new SiteProxyProvider();
        siteProxyProvider.setup(title, description, category);
        return siteProxyProvider;
    }

    protected void setup(String title, String description, Category category) {
        super.setup(title, description);
        setCategory(category);
    }

    public Category getCategory() {
        if (get(CATEGORY) == null) {
            return null;
        } else {
            return new Category((DataObject) get(CATEGORY));
        }
    }

    public void setCategory(Category category) {
        Assert.exists(category, Category.class);
        set(CATEGORY, category);
    }

    public DataQuery getAtomicEntries(String letter) {
        DataQuery items = SessionManager.getSession().retrieveQuery(
                                             ATOMIC_SITEPROXY_ENTRIES_QUERY);
        items.setParameter("providerID", getID());
        items.setParameter("letter", letter + '%');
        return items;
    }

}
