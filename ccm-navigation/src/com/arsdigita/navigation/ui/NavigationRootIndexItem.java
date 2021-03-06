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

package com.arsdigita.navigation.ui;

import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentItemXMLRenderer;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.xml.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * This generates the XML of the Index item of the Root of the Navigation category tree.
 * <br />
 * To set it up in one of your JSP, simply add the following:
 * &lt;define:component name="navRootIndex" classname="com.arsdigita.navigation.ui.NavigationRootIndexItem" /&gt;
 *
 * @version $Revision: $ $Date: $
 * @version $Id: $
 */
public class NavigationRootIndexItem extends AbstractComponent {

    private static Logger log = Logger.getLogger(NavigationRootIndexItem.class);;
    
    private static final String TAG_PARENT = "navrootindex";
    private static final String TAG_ITEM = "cms:item";
    
    public NavigationRootIndexItem() {
        super();
    }
    
    /**
     * Generates the XML.
     *
     * @param state The page state
     * @param parent The parent DOM element
     */
    public Element generateXML(HttpServletRequest request, HttpServletResponse response) {
        
        Element parentElement = Navigation.newElement(TAG_PARENT);
        
        try {
            /*Fix by Quasimodo*/
            /* getPrimaryInstance doesn't negotiate the language of the content item */
            /* ContentItem indexItem = ((ContentBundle) Navigation.getConfig().getDefaultCategoryRoot().getIndexObject()).getPrimaryInstance().getLiveVersion(); */
            ContentItem indexItem = ((ContentBundle) Navigation.getConfig().
                    getDefaultCategoryRoot().getIndexObject()).
                    getInstance(GlobalizationHelper.getNegotiatedLocale(), true);
            // if there is no matching language version for this content item
            if(indexItem == null) {
                // get the primary instance instead (fallback)
                indexItem = ((ContentBundle) Navigation.getConfig().getDefaultCategoryRoot().getIndexObject()).getPrimaryInstance();
            }
            indexItem = indexItem.getLiveVersion();
            Element itemElement = parentElement.newChildElement(TAG_ITEM, CMS.CMS_XML_NS);
            ContentItemXMLRenderer renderer = new ContentItemXMLRenderer(itemElement);
            // not sure these are necessary
            renderer.setWrapAttributes(true);
            renderer.setWrapRoot(false);
            renderer.setWrapObjects(false);
            renderer.walk(indexItem, SimpleXMLGenerator.ADAPTER_CONTEXT);
        } catch (Exception e) {
            log.warn("Could not get index ContentItem of the root navigation category.", e);
        }
        
        return parentElement;
    }
}
