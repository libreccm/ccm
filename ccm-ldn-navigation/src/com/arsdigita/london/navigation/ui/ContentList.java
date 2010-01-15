/*
 * Copyright (C) 2002-2005 Runtime Collective Ltd. All Rights Reserved.
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

package com.arsdigita.london.navigation.ui;

import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.cms.ContentItemXMLRenderer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.xml.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This generate a list of all live content items which:
 * <ul>
 * <li> when viewing an item: are in first category assigned to the the item currently viewed, including the item currently viewed
 * <li> or, when viewing a category: are in the category currently viewed
 * </ul>
 * The list is split in sub-lists, one for each content type.
 * <p>
 * To activate this component on categories:
 * <ul>
 * <li> add the following to 'web/package/navigation/templates/default.jsp':<br>
 * &lt;define:component name="contentList"
 * classname="com.arsdigita.cms.dispatcher.ContentList" /&gt;
 * <li> add the following to 'web/__ccm__/apps/navigation/xsl/index.xsl':<br>
 * &lt;xsl:include href="content-lists.xsl" /&gt;
 * </ul>
 * <p>
 * To activate this component on items:
 * <ul>
 * <li> add the following to 'web/package/content-section/templates/default/item.jsp':<br>
 * &lt;define:component name="contentList"
 * classname="com.arsdigita.cms.dispatcher.ContentList" /&gt;
 * <li> add the following to 'web/__ccm__/apps/content-section/xsl/index.xsl':<br>
 * &lt;xsl:include href="../../navigation/xsl/content-lists.xsl" /&gt;
 * </ul>
 *
 * @version $Revision: $ $Date: $
 */
public class ContentList extends AbstractComponent {
    // extends SimpleComponent
    
    public static final String versionId = "$Id: ContentList.java 1173 2006-06-14 13:54:39Z fabrice $";
    
    private static Logger log = Logger.getLogger(ContentList.class);;
    
    private static final String TAG_ITEMLISTS = "cms:contentLists";
    
    private static final String TAG_ITEMLIST = "cms:contentList";
    private static final String TAG_ITEMLIST_TYPE = "type";
    
    private static final String TAG_ITEM = "cms:item";
    private static final String TAG_ITEM_ISINDEX = "name";
    private static final String TAG_ITEM_ISINDEX_VALUE = "index";
    
    private static final String NAVIGATION_ROOT_NAME = "Navigation";
    private static final String CATEGORYID_PARAM = "categoryID";
    private static final String CONTENTITEM_CLASS_NAME = ContentItem.class.getName();
    
    public ContentList() {
        super();
    }
    
    /**
     * Generates the XML.
     *
     * @param state The page state
     * @param parent The parent DOM element
     */
    public Element generateXML(HttpServletRequest request, HttpServletResponse response) {
        //PageState state, Element parent
        
        Element parent = new Element(TAG_ITEMLISTS, CMS.CMS_XML_NS);
        
        //if (isVisible(state)) {
        
        String indexItemIdString = null;
        
        // check if we are currently displaying an item
        HashMap items = new HashMap();
        HashMap sortKeys = new HashMap();
        ContentItem currentItem = null;
        try {
            currentItem = CMS.getContext().getContentItem();
            log.debug("ContentList: CMS.getContext().getContentItem() returned : "+currentItem);
        } catch (IllegalStateException e) {
            // we're dealing with a category
        }
        
        if (currentItem != null) {
            
            // take the first category in the list, should do the trick????
            Iterator categories = currentItem.getCategories(null);
            if (categories.hasNext()) {
                Category category = (Category) categories.next();
                String currentItemClassName = currentItem.getClass().getName();
                String currentItemIdString = currentItem.getID().toString();
                log.debug("ContentList: looking at the first category of the item : "+category.getName());
                // only navigation categories (not sure this was truely necessary in fact)
                // if (getNavigationRootCategory().isMemberOfSubtree(category)) {
                processCategory(category, currentItemClassName, currentItemIdString, items, sortKeys);
                // }
                
                // now that we look at only one category, also mark the indexItem as such
                // but don't process it on top of what has already been done
                ACSObject indexItem = category.getIndexObject();
                if ((indexItem != null) && (indexItem instanceof ContentBundle)) {
                    
                    /*Fix by Quasimodo*/
                    /* getPrimaryInstance doesn't negotiate the language of the content item */
                    /* ContentItem cIndexItem = ((ContentBundle) indexItem).getPrimaryInstance().getLiveVersion(); */
                    ContentItem cItem = ((ContentBundle) indexItem).negotiate(request.getLocales());
                    // If there is no matching language version for this content item
                    if(cItem == null) {
                        // get the primary instance instead (fallback)
                        cItem = ((ContentBundle) indexItem).getPrimaryInstance();
                    }
                    
                    ContentItem cIndexItem = cItem.getLiveVersion();
                    
                    if (cIndexItem != null) {
                        indexItemIdString = cIndexItem.getID().toString();
                    }
                }
                
            } else {
                log.warn("ContentList: item has no category, contentList will be empty.");
            }
            
        } else {
            // we're viewing a category. process it
            //String categoryIdString = state.getRequest().getParameter(CATEGORYID_PARAM);
            //Category category = new Category(new OID(Category.class.getName(), new BigDecimal(categoryIdString)));
            Category category = getCategory();
            log.debug("ContentList: getCategory returned : "+getCategory());
            
            if (category != null) {
                
                processCategory(category, null, null, items, sortKeys);
                
                // process the index item, just in case it isn't in the lists
                // above (ie inherited from parent category)
                ACSObject indexItem = category.getIndexObject();
                if ((indexItem != null) && (indexItem instanceof ContentBundle)) {
                    /*Fix by Quasimodo*/
                    /* getPrimaryInstance doesn't negotiate the language of the content item */
                    /* ContentItem cIndexItem = ((ContentBundle) indexItem).getPrimaryInstance().getLiveVersion(); */
                    ContentItem cItem = ((ContentBundle) indexItem).negotiate(request.getLocales());
                    // If there is no matching language version for this content item
                    if(cItem == null) {
                        // get the primary instance instead (fallback)
                        cItem = ((ContentBundle) indexItem).getPrimaryInstance();
                    }
                    
                    ContentItem cIndexItem = cItem.getLiveVersion();
                    //log.debug("indexItem : "+cIndexItem);
                    if (cIndexItem != null) {
                        processItem(cIndexItem, null, null, items);
                        indexItemIdString = cIndexItem.getID().toString();
                    }
                    //log.debug("indexItemIdString is : "+indexItemIdString);
                }
            }
        }
        
        // output the XML
        ContentItem item;
        Element itemElement;
        ContentItemXMLRenderer renderer;
        String itemIdString;
        Iterator classes = items.keySet().iterator();
        String className;
        List list;
        Element clElement;
        while (classes.hasNext()) {
            className = (String) classes.next();
            list = (List) items.get(className);
            //log.debug("looking at items of type : "+className);
            
            clElement = parent.newChildElement(TAG_ITEMLIST, CMS.CMS_XML_NS);
            //exportAttributes(content); ???
            clElement.addAttribute(TAG_ITEMLIST_TYPE, className);
            
            for (int i=0; i<list.size(); i++) {
                
                itemIdString = (String) list.get(i);
                item = new ContentItem(new OID(ContentItem.class.getName(), new BigDecimal(itemIdString)));
                //log.debug("adding to contentList : "+item.getDisplayName()+", id : "+itemIdString);
                
                itemElement = clElement.newChildElement(TAG_ITEM, CMS.CMS_XML_NS);
                
                // mark the item as being the index item, if it is
                if (itemIdString.equals(indexItemIdString)) {
                    itemElement.addAttribute(TAG_ITEM_ISINDEX, TAG_ITEM_ISINDEX_VALUE);
                }
                
                itemElement.addAttribute("sortKey", ""+sortKeys.get(item.getID().toString()));
                
                
                renderer = new ContentItemXMLRenderer(itemElement);
                // not sure these are necessary
                renderer.setWrapAttributes(true);
                renderer.setWrapRoot(false);
                renderer.setWrapObjects(false);
                renderer.walk(item, SimpleXMLGenerator.ADAPTER_CONTEXT);
            }
        }
        //}
        
        return parent;
    }
    
    public void processCategory(Category category, String currentItemClassName, String currentItemIdString, HashMap items, HashMap sortKeys) {
        
        log.debug("ContentList: processing category : "+category.getName());
        // items don't come out properly ordered... adding "parent" as optional parameter gives back nothing
        CategorizedCollection catcol = category.getObjects(CONTENTITEM_CLASS_NAME);
        //  DataCollection os = SessionManager.getSession().retrieve(CONTENTITEM_CLASS_NAME);
//  	os.addOrder("parent.categories.link.sortKey");
        
//  	CategorizedCollection catcol = new CategorizedCollection(os);
        // catcol.
        catcol.sort(true);
        ContentItem item;
        int sortKey = 0;
        
        while (catcol.next()) {
            item = (ContentItem) catcol.getDomainObject();
            //log.debug("looking at bundle : "+item);
            if (item instanceof ContentBundle) {
                log.debug("looking at item : "+item);
                
                /*Fix by Quasimodo*/
                /* getPrimaryInstance doesn't negotiate the language of the content item */
                /* item = ((ContentBundle) item).getPrimaryInstance(); */
                item = ((ContentBundle) item).negotiate(DispatcherHelper.getRequest().getLocales());
                // If there is no matching language version for this content item
                if(item == null) {
                // get the primary instance instead (fallback)
                item = ((ContentBundle) item).getPrimaryInstance();
                }
                
                //This can cause item to become null, if there is no instance of it for the default language
                if (item != null) {
                    processItem(item, currentItemClassName, currentItemIdString, items);
                }
            }
            log.debug("adding sortKey " + sortKey + " to item " + item);
            // This item can be null, so check first...
            if (item != null) {
                sortKeys.put(item.getID().toString(), ""+sortKey);
                sortKey++;
            } else {
                log.warn("ContentList: Item " + sortKey + " in category \"" + category.getName() + "\" was null. Ignoring.");
            }
        }
    }
    
    public void processItem(ContentItem item, String currentItemClassName, String currentItemIdString, HashMap items) {
        
        log.debug("ContentList: processing item : "+item.getDisplayName());
        
        // only take the live version (all versions are linked to categories, so avoid duplicate)
        if (item.isLiveVersion() && item.isLive()) {
            
            String itemClassName = item.getClass().getName();
            List list = (List) items.get(itemClassName);
            
            if (list == null) {
                list = new ArrayList();
                items.put(itemClassName, list);
            }
            
            // We do not want to add the latest version of the item to our list.  This reference may become
            // stale if the item changes, meaning that search engines are directed to an item that is no longer
            // available.
            String itemIdString = item.getMaster().getID().toString();
            if (!list.contains(itemIdString)) {
                list.add(itemIdString);
            }
        }
    }
}
