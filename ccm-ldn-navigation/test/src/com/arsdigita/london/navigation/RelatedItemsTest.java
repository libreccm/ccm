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

package com.arsdigita.london.navigation;

import com.arsdigita.london.util.junit.BaseTestCase;

import com.arsdigita.cms.Article;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.web.URL;
import com.arsdigita.web.WebContextExposer;
import java.util.HashSet;
import java.util.Set;

import java.util.Date;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class RelatedItemsTest extends BaseTestCase {

    private final static Logger s_log = Logger.getLogger(RelatedItemsTest.class);
    
    private static final int NITEMS = 7;

    private ContentBundle[] m_bundles;
    private ContentPage[] m_items;
    private ContentPage[] m_live;
    private Category m_root;
    private Set m_expectedItemIDs;
    private Set m_expectedWorkingIDs;
    
    private Application m_app;

    public RelatedItemsTest( String name ) {
        super( name );
    }

    public void setUp() {
        m_expectedItemIDs = new HashSet();
        m_expectedWorkingIDs = new HashSet();

        m_items = new ContentPage[NITEMS];
        m_live = new ContentPage[NITEMS];
        m_bundles = new ContentBundle[NITEMS];

        ContentSection section = createContentSection();
        ContentType type = createContentType();
        for (int i= 0 ; i < NITEMS ; i++) {
            m_items[i] = createItem(type, i);
            m_bundles[i] = createBundle(section, m_items[i]);
        }

        Category nav = createCategory(null, "nav");
        Category sub = createCategory(null, "sub");

        Category mainNav = createCategory(nav, "mainNAv");
        Category otherNav = createCategory(nav, "otherNav");

        Category mainSubject = createCategory(sub, "mainSubject");
        Category mainSubSubject = createCategory(mainSubject, "mainSubSubject");
        Category otherSubject = createCategory(sub, "otherSubject");

        Category.setRootForObject(section, sub, 
                                  Navigation.getConfig()
                                  .getRelatedItemsContext());

        m_root = nav;
        m_app = section;

        // Main item
        mainNav.addChild(m_bundles[0]);
        mainSubject.addChild(m_bundles[0]);

        // Positive: Item in same nav cat with matching subject
        mainNav.addChild(m_bundles[1]);
        mainSubject.addChild(m_bundles[1]);

        // Negative: Item in same nav cat with non-matching subject
        mainNav.addChild(m_bundles[2]);
        otherSubject.addChild(m_bundles[2]);

        // Positive: Item in diff nav cat with matching subject
        otherNav.addChild(m_bundles[3]);
        mainSubject.addChild(m_bundles[3]);

        // Negative: Item in diff nav cat with non-matching subject
        otherNav.addChild(m_bundles[4]);
        otherSubject.addChild(m_bundles[4]);

        // Positive: Item in same nav cat with matching sub-subject
        mainNav.addChild(m_bundles[5]);
        mainSubSubject.addChild(m_bundles[5]);

        // Positive: Item in diff nav cat with matching sub-subject
        mainNav.addChild(m_bundles[6]);
        mainSubSubject.addChild(m_bundles[6]);
        
        LifecycleDefinition def = createLifecycleDefintion();

        for (int i = 0 ; i < m_items.length ; i++) {
            m_live[i] = (ContentPage)m_items[i].publish(def, new Date());
            m_live[i].getLifecycle().start();
        }

        m_expectedWorkingIDs.add(m_items[1].getID());
        m_expectedItemIDs.add(m_live[1].getID());
        m_expectedWorkingIDs.add(m_items[3].getID());
        m_expectedItemIDs.add(m_live[3].getID());
        // We disabled hiearchy scans
        //m_expectedWorkingIDs.add(m_items[5].getID());
        //m_expectedItemIDs.add(m_live[5].getID());
        //m_expectedWorkingIDs.add(m_items[6].getID());
        //m_expectedItemIDs.add(m_live[6].getID());
    }

    private LifecycleDefinition createLifecycleDefintion() {
        LifecycleDefinition def = new LifecycleDefinition();
        def.setLabel("Test infinite");

        def.addPhaseDefinition("Forever", "A long time", null, null, null);
        
        return def;
    }

    private Category createCategory(Category parent,
                                    String name) {
        Category cat = new Category();
        cat.setName(name);
        if (parent != null) {
            parent.addChild(cat);
        }
        return cat;
    }
    
    private ContentSection createContentSection() {
        return ContentSection.create("test-section");
    }

    private ContentType createContentType() {
        ContentType type = new ContentType();
        type.setAssociatedObjectType(Article.BASE_DATA_OBJECT_TYPE);
        type.setLabel("Test Article");
        type.setClassName(PageCreate.class.getName());
        return type;
    }

    private ContentPage createItem(ContentType type,
                                   int i) {
        ContentPage page = new Article();
        page.setLanguage("en");
        page.setName("item-" + i);
        page.setTitle("Item " + i);
        page.setContentType(type);
        return page;
    }

    private ContentBundle createBundle(ContentSection section,
                                       ContentPage page) {
        ContentBundle bundle = new ContentBundle(page);
        bundle.setParent(section.getRootFolder());
        return bundle;
    }


    public void tearDown() {
    }

    public void testRelatedItems() {
        
        new WebContextExposer(Web.getContext()).init(m_app, new URL("http",
                                                                    "test.example.com",
                                                                    8080,
                                                                    "",
                                                                    "/ccm",
                                                                    "/test",
                                                                    null));

        RelatedItemsQueryFactoryImpl impl = new RelatedItemsQueryFactoryImpl();
        RelatedItemsQueryImpl query = (RelatedItemsQueryImpl)
            impl.getRelatedItems(m_items[0],
                                 m_root);
        
        int i = 0;
        while (query.next()) {
            BigDecimal workingId = query.getWorkingID();
            BigDecimal itemId = query.getItemID();
            System.out.println("WO" + workingId + " " + itemId);
            assertTrue(m_expectedItemIDs.contains(itemId));
            assertTrue(m_expectedWorkingIDs.contains(workingId));

            i++;
        }
        assertTrue(i == m_expectedWorkingIDs.size());        
    }    

}
