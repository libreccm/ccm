/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.cms.contentitem;

import com.arsdigita.categorization.Category;
// import com.arsdigita.categorization.CategoryPurpose;
//import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import org.apache.log4j.Logger;

//import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/** 
 * Parses and XML file definition of content items in a folder.
 * the XML configuration should look like this:
 */
public class ContentPageHelper extends ContentBundleHelper {

    private static final Logger s_log = Logger
                                        .getLogger(ContentPageHelper.class);

    private Category m_category;
    private List m_categories = new LinkedList();
    private Random m_random;

    /**
     * Constructor.
     * 
     * @param section 
     */
    public ContentPageHelper(ContentSection section) {

        super(section);
        m_category = section.getRootCategory();

        // Look for categories CategoryPurpose.NAVIGATION
        // CategoryPurpose is deprecated and not used anymore
   //   CategoryPurpose nav = CategoryPurpose
   //       .getPurpose(CategoryPurpose.NAVIGATION);
   //   if (null != nav) {
   //       s_log.debug("purpose key " + nav.getKey() + " name " + nav.getName());
   //
   //       Iterator navs =  nav.getCategories().iterator();
   //
   //       while (navs.hasNext()) {
   //           Category cat = (Category) navs.next();
   //           CategoryCollection scions = cat.getDescendants();
   //           while ( scions.next() ) {
   //               m_categories.add(scions.getCategory());
   //           }
   //           scions.close();
   //       }
   //
   //   }

        if ( m_categories.isEmpty() ) {
            s_log.debug("Category list is empty, adding root");
            m_categories.add(m_category);
        }
        m_random = new Random();
    }

    @Override
    public void setBodyText(String body) {
        // do nothing
    }

    public ContentPage getContentPage () {
        ContentItem l_item = super.getContentItem ();
        if ( l_item != null ) {
            return (ContentPage) l_item;
        } else {
            return null;
        }
    }
    
    @Override
    public ContentItem create() {
        return createContentPage();
    }

    public ContentPage createContentPage () {
        return createContentPage ( false );
    }
    
    public ContentPage createContentPage( boolean save ) {
        ContentPage m_page = (ContentPage) super.createContentItem (save);
        return m_page;
    }

//    public void clear() {
//        super.clear();
//        m_category = null;
//     }

    // Save everything at the very end
    @Override
    public void save() {
        super.save();
        Category cat = getCategory();
        cat.addChild(getContentPage ());
        cat.save();
    }

    /** Assigning pages to random categories */
    @Override
    public ContentItem cloneItem ( String name, Folder parent, boolean save ) {
        ContentPage page = (ContentPage)super.cloneItem(name, parent, save); 
        page.save();

        Category cat = getRandomCategory();
        cat.addChild(page);
        cat.save();
        return page;
    }

    public Category getRandomCategory() {
        int index = m_random.nextInt(m_categories.size());
        s_log.debug("getting category index : "  + index 
                    + " out of: " + m_categories.size());
        return (Category)m_categories.get(index);
    }

    public void setCategory(Category category) {
        m_category = category;
        ContentPage page = getContentPage ();
        if (page != null) {
            m_category.addChild(page);
            m_category.save();
        }
    }

    public Category getCategory() {
        return m_category;
    }

}

