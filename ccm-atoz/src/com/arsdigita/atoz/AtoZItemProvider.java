/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.arsdigita.atoz;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import com.arsdigita.categorization.Category;


public class AtoZItemProvider extends AtoZProvider {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.atoz.AtoZItemProvider";

    public static final String CATEGORY = "category";
    public static final String LOAD_PATHS = "loadPaths";

    public static final String ATOMIC_ENTRIES = 
                               "com.arsdigita.atoz.getAtomicItemEntries";

    public AtoZItemProvider() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected AtoZItemProvider(String type) {
        super(type);
    }

    public AtoZItemProvider(DataObject obj) {
        super(obj);
    }

    public AtoZItemProvider(OID oid) {
        super(oid);
    }

    public static AtoZItemProvider create(String title,
                                          String description,
                                          Category category) {
        AtoZItemProvider provider = new AtoZItemProvider();
        provider.setup(title, description, category);
        return provider;
    }

    public DataQuery getAtomicEntries() {
	DataQuery items = SessionManager.getSession().retrieveQuery(ATOMIC_ENTRIES);
	items.setParameter("providerID", getID());
        return items;
    }

    protected void setup(String title, String description, Category category) {
        super.setup(title, description);
        setCategory(category);
    }

    public void setCategory( Category category ) {
        Assert.exists( category, Category.class );
        set( CATEGORY, category );
    }

    public Category getCategory() {
        if (get(CATEGORY) == null) {
            return null;
        } else {
            return new Category( (DataObject) get(CATEGORY));
        }
    }

    public DomainCollection getAliases() {
	DomainCollection aliases = new DomainCollection(SessionManager.getSession()
                                   .retrieve(AtoZItemAlias.BASE_DATA_OBJECT_TYPE));
	aliases.addFilter("atozItemProvider = :providerId").set("providerId", getID());
	aliases.addOrder("title");
        return aliases;
    }
    
    public void setLoadPaths( String loadPaths ) {
        set( LOAD_PATHS, loadPaths );
    }

    public String getLoadPaths() {
        return (String) get( LOAD_PATHS );
    }

    public AtoZGenerator getGenerator() {
        return new AtoZItemGenerator(this);
    }

}
