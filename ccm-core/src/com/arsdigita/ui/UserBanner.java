/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import com.arsdigita.ui.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * 
 * 
 */
public class UserBanner extends SimpleComponent {

    private static GlobalizedMessage s_help;
    private static GlobalizedMessage s_signout;
    private static GlobalizedMessage s_portal;
    private static GlobalizedMessage s_greet;

    private static boolean initialized = false;

   /**
    * 
    */
    static void init() {
        if (initialized) {
            return;
        }
        
        s_help = GlobalizationUtil.globalize("ui.admin.help");
        s_signout = GlobalizationUtil.globalize("ui.admin.signout");
        s_portal = GlobalizationUtil.globalize("ui.admin.portal");
        s_greet = GlobalizationUtil.globalize("ui.admin.greeting");

        initialized = true;
    }

    /**
     * 
     * @param state
     * @param parent
     */
    public void generateXML(PageState state,
                            Element parent) {
        init();
        Party party = Kernel.getContext().getParty();

        Element content = parent.newChildElement("ui:userBanner",
                                                 UIConstants.UI_XML_NS);
        exportAttributes(content);

        if (party != null && party instanceof User) {
            User user = (User)party;

            content.addAttribute("givenName", 
                                 user.getPersonName().getGivenName());
            content.addAttribute("familyName", 
                                 user.getPersonName().getFamilyName());
            content.addAttribute("screenName", 
                                 user.getScreenName());
            content.addAttribute("primaryEmail", 
                                 user.getPrimaryEmail().toString());
            content.addAttribute("userID", 
                                 user.getOID().toString());
        }

        content.addAttribute("helpLabel",
                             (String)s_help.localize(state.getRequest())); 
      
        content.addAttribute("portalLabel",
                             (String)s_portal.localize(state.getRequest())); 
      
        content.addAttribute("signoutLabel",
                             (String)s_signout.localize(state.getRequest())); 
      
        content.addAttribute("greeting",
                             (String)s_greet.localize(state.getRequest())); 
      

        content.addAttribute(
            "workspaceURL",
            URL.there(state.getRequest(),
                      UI.getWorkspaceURL()).toString());
        //            LegacyInitializer.getFullURL(LegacyInitializer.WORKSPACE_PAGE_KEY,
        //                                   state.getRequest())).toString());

        content.addAttribute(
            "loginURL",
            URL.there(state.getRequest(),
                      UI.getLoginPageURL()).toString());
            //        LegacyInitializer.getFullURL(LegacyInitializer.LOGIN_PAGE_KEY,
            //                               state.getRequest())).toString());

        content.addAttribute(
            "loginExcursionURL",
            URL.excursion(state.getRequest(),
                          UI.getLoginPageURL()).toString());
            //        LegacyInitializer.getFullURL(LegacyInitializer.LOGIN_PAGE_KEY,
            //                               state.getRequest())).toString());

        content.addAttribute(
            "logoutURL",
            URL.there(state.getRequest(),UI.getLoginPageURL()).toString());

    }
}
