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
package com.arsdigita.navigation.ui;

import com.arsdigita.bebop.PageState;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentItemXMLRenderer;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.xml.Element;

/**
 * GreetingItem component displays a list of items in a category, ordered by
 * title.  The provided links allow the list of items to be restricted by
 * a letter of the alphabet.
 * The number, type, and attributes of the returned items can be configured
 * from jsp page using setHowMany( int ), * setObjectType( String ),
 * and addAttribute( String ) methods.  The object type must be either that of
 * ContentPage or extend the ContentPage.
 *
 * @author <a href="mailto:dominik@redhat.com">Dominik Kacprzak</a>
 * @author Jens Pelzetter (jensp)
 * @version $Id: GreetingItem.java 1473 2007-03-12 15:16:39Z chrisgilbert23 $
 */
public class GreetingItem extends AbstractComponent {

    private static final Logger s_log = Logger.getLogger(GreetingItem.class);

    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        long start = System.currentTimeMillis();
        ContentItem item = (ContentItem) getObject();
        if (null == item || !item.isLive()) {
            return null;
        }

        if (!ContentItem.LIVE.equals(item.getVersion())) {
            item = item.getLiveVersion();
        }


        Element content = Navigation.newElement("greetingItem");
        Party currentParty = Kernel.getContext().getParty();
        if (currentParty == null) {
            currentParty = Kernel.getPublicUser();
        }
        // check if current user can edit the current index item (nb privilege is granted on draft item, but live item
        // has draft as its permission context
        //
        // Note that the xml that is generated is only of use if you DO NOT CACHE index pages.
        // cg.

        PermissionDescriptor edit = new PermissionDescriptor(
                PrivilegeDescriptor.get(SecurityManager.CMS_EDIT_ITEM),
                item,
                currentParty);
        if (PermissionService.checkPermission(edit)) {
            content.addAttribute("canEdit", "true");
        }
        PermissionDescriptor publish = new PermissionDescriptor(
                PrivilegeDescriptor.get(SecurityManager.CMS_PUBLISH),
                item,
                currentParty);
        if (PermissionService.checkPermission(publish)) {
            content.addAttribute("canPublish", "true");
        }

        /**
         * jensp 2011-10-02: GreetingItem/IndexItem was displayed even if the
         * current party has no access to the item.
         */
        PermissionDescriptor read = new PermissionDescriptor(
                PrivilegeDescriptor.get(SecurityManager.CMS_READ_ITEM),
                item,
                currentParty);
        if (!PermissionService.checkPermission(read)) {
            throw new LoginSignal(request);
        }

        ContentBundle bundle = (ContentBundle) item;
        
        /* Fix by Jens Pelzetter, 2009-08-28
         * bundle.getPrimaryInstance() does not care about the preferred
         * languages
         * of the User Client, instead it returns the primary instance of
         * a ContentItem (the first language created).
         *
         * Fixed by using negotiate() instead, which takes the locale
         * send by the User Client in account and tries to find a suitable
         * language version.
         */ //ContentItem baseItem = bundle.getPrimaryInstance();
        ContentItem baseItem = bundle.getInstance(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
        // If there is no matching language version for this content item
        if (baseItem == null) {
            // get the primary instance instead (fallback)
            baseItem = bundle.getPrimaryInstance();
        }
        Element itemEl = content.newChildElement("cms:item",
                                                 CMS.CMS_XML_NS);

        //Moved to seperate method generateGreetingItemXml to make to
        //XML generation extendable (use another renderer etc.)
        /*ContentItemXMLRenderer renderer =
        new ContentItemXMLRenderer(itemEl);
        
        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setWrapObjects(false);
        
        renderer.walk(baseItem, SimpleXMLGenerator.ADAPTER_CONTEXT);*/

        generateGreetingItemXml(itemEl, baseItem);

        for (ExtraXMLGenerator generator : baseItem.getExtraXMLGenerators()) {
            try {
                generator.generateXML(baseItem, itemEl, new PageState(null,
                                                                       request,
                                                                       response));
            } catch (ServletException ex) {
                s_log.error(ex);
            }
        }

        return content;
    }

    /**
     * Creates the XML for the greeting item.
     *
     * @param parent The parent element
     * @param item The item to render
     */
    protected void generateGreetingItemXml(Element parent, ContentItem item) {
        ContentItemXMLRenderer renderer = new ContentItemXMLRenderer(parent);
        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setWrapObjects(false);

        renderer.walk(item, SimpleXMLGenerator.ADAPTER_CONTEXT);
    }
}
