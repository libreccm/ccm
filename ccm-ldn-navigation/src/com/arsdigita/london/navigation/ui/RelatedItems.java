/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.navigation.ui;

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.NavigationModel;
import com.arsdigita.london.navigation.RelatedItemsQuery;
import com.arsdigita.london.navigation.RelatedItemsQueryFactory;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.xml.Element;
import com.arsdigita.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * A Bebop component that takes a the {@link ContentPage} from the navigation
 * model generates a component that contains related items.
 *
 * @author <a href="mailto:tzumainn@arsdigita.com">Tzu-Mainn Chen</a>
 * @version $Id: RelatedItems.java 2011 2009-10-04 01:03:56Z pboy $
 *
 */

public class RelatedItems extends AbstractComponent {
    
    private static final Logger s_log = Logger.getLogger(RelatedItems.class);
    
    private int m_howMany = 15;
    
    public void setHowMany(int howMany) {
        Assert.isUnlocked(this);
        m_howMany = howMany;
    }
    
    public Element generateXML(HttpServletRequest request,
            HttpServletResponse response) {
        Assert.isLocked(this);
        
        NavigationModel model = getModel();
        ACSObject obj = model.getObject();
        
        // on category pages, we obtain a ContentBundle
        if (obj instanceof ContentBundle) {
            
            /*Fix by Quasimodo*/
            /* getPrimaryInstance doesn't negotiate the language of the content item */
            /* obj = ((ContentBundle) obj).getPrimaryInstance(); */
            ContentItem cItem = ((ContentBundle) obj).negotiate(request.getLocales());
            // if there is no matching language version of the content item
            if(cItem == null) {
                // get the primary instance instead (fallback)
                cItem = ((ContentBundle) obj).getPrimaryInstance();
            }
            obj = cItem;
        }
        
        if (!(obj instanceof ContentPage)) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Cannot generate related items " +
                        "for non-content item " + obj);
            }
            return null;
        }
        
        ContentPage item = (ContentPage)obj;
        if (ContentItem.DRAFT.equals(item.getVersion())) {
            if (!item.isLive()) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Bundle for " + item + " is not live");
                }
                return null;
            }
            item = (ContentPage)item.getLiveVersion();
        }
        
        Element element = Navigation.newElement("relatedItems");
        element.addAttribute("id", getIdAttr());
        
        RelatedItemsQueryFactory factory = RelatedItemsQueryFactory.getInstance();
        RelatedItemsQuery items = factory.getRelatedItems(item,
                getModel().getCategory());
        
        if (items == null) {
            return null;
        }
        
        items.setRange(new Integer(1), new Integer(m_howMany));
        items.addOrder(RelatedItemsQuery.TITLE);
        
        while (items.next()) {
            OID oid = new OID( items.getObjectType(), items.getWorkingID() );
            String path = Navigation.redirectURL(oid);
            
            Element itemEl = Navigation.newElement("relatedItem");
            
            itemEl.addAttribute("title", items.getTitle());
            itemEl.addAttribute("type", items.getTypeName());
            itemEl.addAttribute("id", items.getItemID().toString());
            itemEl.addAttribute("path", path);
            
            element.addContent(itemEl);
        }
        
        return element;
    }
}
