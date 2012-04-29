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

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.navigation.DataCollectionDefinition;
import com.arsdigita.subsite.Site;
import com.arsdigita.subsite.Subsite;
import com.arsdigita.subsite.SubsiteContext;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

import org.apache.log4j.Logger;

/**
 * 
 * 
 */
public class CategoryProvider extends AtoZProvider {

    /** Private logger instance to assist debugging.                         */
    private static final Logger s_log = 
                                Logger.getLogger(CategoryProvider.class);

    public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.atoz.CategoryProvider";

    public static final String IS_COMPOUND = "isCompound";

    public static final String IS_VISIBLE = "isVisible";

    public static final String BLACK_LIST = "atozBlackList";

    public static final String CT_BLACK_LIST = "atozContentTypeBlackList";

    public static final String ALIASES = "aliases";

    public static final String CT_TYPE_ID = "type_id";

    public static final String ATOMIC_ENTRIES = 
            "com.arsdigita.atoz.getAtomicCategoryEntries";

    public static final String ATOMIC_ENTRIES_FOR_ROOT_CATEGORY = 
            "com.arsdigita.atoz.getAtomicCategoryEntriesForRootCategory";

    public static final String FILTERED_ATOMIC_ENTRIES = 
            "com.arsdigita.atoz.getAtomicFilteredCategoryEntries";

    public static final String FILTERED_ATOMIC_ENTRIES_FOR_ROOT_CATEGORY = 
            "com.arsdigita.atoz.getAtomicFilteredCategoryEntriesForRootCategory";
    
    public static final String ALL_BLACK_LIST_TYPES = 
            "com.arsdigita.atoz.getAllBlackListTypes";
    
    /**
     * 
     */
    public CategoryProvider() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor
     * @param type 
     */
    protected CategoryProvider(String type) {
        super(type);
    }

    public CategoryProvider(DataObject obj) {
        super(obj);
    }

    public CategoryProvider(OID oid) {
        super(oid);
    }

    @Override
    public void delete() {
        Category.clearRootForObject(this);

        super.delete();
    }

    /**
     * 
     * @param title
     * @param description
     * @param isCompound
     * @return 
     */
    public static CategoryProvider create(String title, 
                                          String description,
                                          boolean isCompound) {
        CategoryProvider provider = new CategoryProvider();
        provider.setup(title, description, isCompound);
        return provider;
    }

    protected void setup(String title, String description, boolean isCompound) {
        super.setup(title, description);
        setCompound(isCompound);
    }

    public boolean isCompound() {
        return Boolean.TRUE.equals(get(IS_COMPOUND));
    }

    public void setCompound(boolean isCompound) {
        set(IS_COMPOUND, new Boolean(isCompound));
    }

    public void addAlias(Category cat, String letter, String title) {
    	CategoryAlias alias = new CategoryAlias();
    	alias.setup(cat, letter, title);
        add(ALIASES, alias);
    }

    public void removeAlias(CategoryAlias alias) {
        remove(ALIASES, alias);
    }

    public void addBlock(Category cat) {
        add(BLACK_LIST, cat);
    }

    public void removeBlock(Category cat) {
        remove(BLACK_LIST, cat);
    }

    public DomainCollection getAliases() {
        DataAssociation entries = (DataAssociation) get(ALIASES);
        return new DomainCollection(entries);
    }

    public DomainCollection getBlackList() {
        DataAssociation entries = (DataAssociation) get(BLACK_LIST);
        return new DomainCollection(entries);
    }

    public DomainCollection getContentTypeBlackList() {
        DataAssociation entries = (DataAssociation) get(CT_BLACK_LIST);
        return new DomainCollection(entries);
    }
    
    public static void excludeBlackListTypes(DataCollectionDefinition definition) {
    	DataQuery types = 
    		SessionManager.getSession().retrieveQuery(ALL_BLACK_LIST_TYPES);
    	
    	while (types.next()) {
    		String objectType = (String)types.get("objectType");
    		s_log.debug("Excluding object type from DataCollection: " 
                        + objectType);
    		definition.excludeSpecificObjectType(objectType);
    	}
    	types.close();
    }

    public DataQuery getAtomicEntries() {
        DataQuery cats = null;
        SubsiteContext subsiteContext = Subsite.getContext();
        boolean useSubsiteSpecificNavigationCategory = AtoZ.getConfig()
                .useSubsiteSpecificNavigationCategory(); // configured using
        // ccm set
        boolean hasSite = subsiteContext.hasSite();
        boolean filterCats = AtoZ.getConfig().filterCategoryProviders();
        
        if (hasSite && useSubsiteSpecificNavigationCategory) {
            Site site = subsiteContext.getSite();
            Category root = site.getRootCategory();
            if(filterCats){
            cats = SessionManager.getSession().retrieveQuery(
            		FILTERED_ATOMIC_ENTRIES_FOR_ROOT_CATEGORY);
            }else{
            	cats = SessionManager.getSession().retrieveQuery(
                        ATOMIC_ENTRIES_FOR_ROOT_CATEGORY);
            }
            cats.setParameter("providerID", getID());
            cats.setParameter("rootCategoryID", root.getID());
        } else {
        	if(filterCats){
        		cats = SessionManager.getSession()
                                     .retrieveQuery(FILTERED_ATOMIC_ENTRIES);
        	}else{
        		cats = SessionManager.getSession().retrieveQuery(ATOMIC_ENTRIES);
        	}
            cats.setParameter("providerID", getID());
        }
        return cats;
    }

    public Category getRootCategory() {
        return Category.getRootForObject(this);
    }

    public void setRootCategory(Category root) {
        Category.setRootForObject(this, root);
    }

    public AtoZGenerator getGenerator() {
        return new CategoryGenerator(this);
    }

    public void addContentTypeBlock(ContentType contentType) {
        if (!isBlocked(contentType))
            add(CT_BLACK_LIST, contentType);
    }

    private boolean isBlocked(ContentType contentType) {
        DataAssociation da = (DataAssociation) get(CT_BLACK_LIST);
        DataAssociationCursor cursor = da.cursor();
        while (cursor.next()) {
            if (cursor.getDataObject().getOID() == contentType.getOID()) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public void removeContentTypeBlock(ContentType contentType) {
        if (isBlocked(contentType))
            remove(CT_BLACK_LIST, contentType);
    }

}
