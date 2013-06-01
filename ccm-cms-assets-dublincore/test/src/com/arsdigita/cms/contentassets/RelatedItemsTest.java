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

package com.arsdigita.cms.contentassets;

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
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.HashMap;

import java.util.Date;
import java.net.URL;
import java.net.MalformedURLException;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class RelatedItemsTest extends BaseTestCase {

    private final static Logger s_log = Logger.getLogger(RelatedItemsTest.class);
    
    private static final int NITEMS = 9;

    private ContentBundle[] m_bundles;
    private ContentPage[] m_items;
    private ContentPage[] m_live;
    private Category m_mainNavCat;

    public RelatedItemsTest( String name ) {
        super( name );
    }

    public void setUp() {
        m_items = new ContentPage[NITEMS];
        m_live = new ContentPage[NITEMS];
        m_bundles = new ContentBundle[NITEMS];

        ContentSection section = createContentSection();
        ContentType type = createContentType();
        for (int i= 0 ; i < NITEMS ; i++) {
            m_items[i] = createItem(type, i);
            m_bundles[i] = createBundle(section, m_items[i]);
        }

        DublinCoreItem.getConfig().setRelatedItemsSubjectDomain("TEST-LGCL");

        Domain lgcl = createDomain("TEST-LGCL");
        Domain nav = createDomain("TEST-NAV");

        Term mainNav = createTerm(nav, 1);
        Term mainSubNav = createTerm(nav, 2);
        Term otherNav = createTerm(nav, 3);

        mainNav.addNarrowerTerm(mainSubNav, true, true);
        
        Term mainLGCL = createTerm(lgcl, 1);
        Term otherLGCL = createTerm(lgcl, 2);
        

        // Main item
        mainNav.addObject(m_bundles[0]);
        CategoryCollection cats = m_bundles[0].getCategoryCollection();
        assertTrue(cats.next());
        m_mainNavCat = cats.getCategory();
        assertTrue(!cats.next());

        mainLGCL.addObject(m_bundles[0]);
        createDublin(m_items[0], "foo");

        // Negative: Item in same nav cat with matching lgcl & matching 1st keyword
        mainNav.addObject(m_bundles[1]);
        mainLGCL.addObject(m_bundles[1]);
        createDublin(m_items[1], "foo");

        // Negative: Item in sub nav cat with matching lgcl & matching 1st keyword
        mainSubNav.addObject(m_bundles[2]);
        mainLGCL.addObject(m_bundles[2]);
        createDublin(m_items[2], "foo");

        // Positive: Item in diff nav cat with matching lgcl & matching 1st keyword
        otherNav.addObject(m_bundles[3]);
        mainLGCL.addObject(m_bundles[3]);
        createDublin(m_items[3], "foo");

        // Positive: Item in diff nav cat with matching 
        // lgcl & matching 1st keyword, non-match second
        otherNav.addObject(m_bundles[4]);
        mainLGCL.addObject(m_bundles[4]);
        createDublin(m_items[4], "foo,bar");

        // Negative: Item in diff nav cat with matching 
        // lgcl & non-match 1st keyword, matching 2nd
        otherNav.addObject(m_bundles[5]);
        mainLGCL.addObject(m_bundles[5]);
        createDublin(m_items[5], "bar,foo");

        // Negative: Item in diff nav cat with non-matching 
        // lgcl & matching 1st keyword
        otherNav.addObject(m_bundles[6]);
        otherLGCL.addObject(m_bundles[6]);
        createDublin(m_items[6], "foo");

        // Negative: Item in diff nav cat with non-matching 
        // lgcl & matching 1st keyword, non-match second
        otherNav.addObject(m_bundles[7]);
        otherLGCL.addObject(m_bundles[7]);
        createDublin(m_items[7], "foo,bar");

        // Negative: Item in diff nav cat with non-matching 
        // lgcl & non-match 1st keyword, matching 2nd
        otherNav.addObject(m_bundles[8]);
        otherLGCL.addObject(m_bundles[8]);
        createDublin(m_items[8], "bar,foo");
        
        LifecycleDefinition def = createLifecycleDefintion();

        for (int i = 0 ; i < m_items.length ; i++) {
            m_live[i] = (ContentPage)m_items[i].publish(def, new Date());
            m_live[i].getLifecycle().start();
        }

    }

    private LifecycleDefinition createLifecycleDefintion() {
        LifecycleDefinition def = new LifecycleDefinition();
        def.setLabel("Test infinite");

        def.addPhaseDefinition("Forever", "A long time", null, null, null);
        
        return def;
    }

    private Domain createDomain(String name) {
        try {
            Domain domain = Domain.create(
                name,
                new URL("http://www.example.com/test/" + name),
                name,
                name,
                "1.0",
                new Date());
            return domain;
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Oh no you don't");
        }
    }

    private Term createTerm(Domain domain,
                            int i) {
        return Term.create(new Integer(i),
                           "term" + i,
                           false,
                           null,
                           domain);
    }
    
    public DublinCoreItem createDublin(ContentPage item,
                                       String keywords) {
        DublinCoreItem dc = DublinCoreItem.create(item);
        dc.setKeywords(keywords);
        return dc;
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
        RelatedItemsQueryFactoryImpl impl = new RelatedItemsQueryFactoryImpl();
        RelatedItemsQueryImpl query = (RelatedItemsQueryImpl)
            impl.getRelatedItems(m_items[0],
                                 m_mainNavCat);
        
        int i = 3;
        while (query.next()) {
            BigDecimal workingId = query.getWorkingID();
            BigDecimal itemId = query.getItemID();
            System.out.println("WO" + workingId + " " + itemId);
            assertTrue(workingId.equals(m_items[i].getID()));
            assertTrue(itemId.equals(m_live[i].getID()));

            i++;
        }
        assertTrue(i == 5);        
    }    

}
