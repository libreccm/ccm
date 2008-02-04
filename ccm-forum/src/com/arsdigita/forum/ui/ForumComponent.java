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
 *
 */
package com.arsdigita.forum.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.forum.ui.admin.ModerationView;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.xml.Element;
import com.arsdigita.util.UncheckedWrapperException;

import javax.servlet.ServletException;
import org.apache.log4j.Logger;
import java.io.IOException;

/**
 * The Bebop Page which provides the complete UI for the bboard application
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 *
 * @version $Revision: #11 $ $Author: sskracic $ $Date: 2004/08/17 $
 */
public class ForumComponent extends ModalContainer implements Constants {

    private static Logger s_log = Logger.getLogger(ForumComponent.class);

    public static final String MODE_THREADS = "threads";
    public static final String MODE_TOPICS = "topics";
    public static final String MODE_ALERTS = "alerts";
    public static final String MODE_MODERATION = "moderation";

    private StringParameter m_mode;

    /**
     * Constructs the bboard use interface
     */

    private ModerationView m_moderationView;
    private ForumAlertsView m_alertsView;
    private CategoryView  m_topicView;
    private ForumUserView m_userView;

    public ForumComponent() {
        super("forum:forum", FORUM_XML_NS);

        m_mode = new StringParameter("mode");

        m_moderationView = new ModerationView();
        m_alertsView     = new ForumAlertsView();
        m_topicView = new CategoryView();
        m_userView     = new ForumUserView();

        add(m_moderationView);
        add(m_alertsView);
        add(m_topicView);
        add(m_userView);

        setDefaultComponent(m_userView);
    }

    public void register(Page p) {
        super.register(p);

        p.addGlobalStateParam(m_mode);
    }

    public void respond(PageState state)
        throws ServletException {

        super.respond(state);

        String mode = (String)state.getControlEventValue();
        state.setValue(m_mode, mode);
        if (MODE_TOPICS.equals(mode)) {
            setVisibleComponent(state, m_topicView);
        } else if (MODE_ALERTS.equals(mode)) {
            if (Kernel.getContext().getParty() == null) {
                UserContext.redirectToLoginPage(state.getRequest());
            }
            setVisibleComponent(state, m_alertsView);
        } else if (MODE_MODERATION.equals(mode)) {
            Party party = Kernel.getContext().getParty();
            if (party == null) {
                UserContext.redirectToLoginPage(state.getRequest());
            }
            Forum forum = ForumContext.getContext(state).getForum();

            PermissionDescriptor permission
                = new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                           forum,
                                           party);

            PermissionService.assertPermission(permission);

            setVisibleComponent(state, m_moderationView);
        } else {
            setVisibleComponent(state, m_userView);
        }
    }

    public void generateXML(PageState state,
                            Element parent) {
        Element content = generateParent(parent);

        generateModeXML(state, content, MODE_THREADS);
        generateModeXML(state, content, MODE_TOPICS);
        generateModeXML(state, content, MODE_ALERTS);
        Forum forum = ForumContext.getContext(state).getForum();
        content.addAttribute("title", forum.getTitle());
        content.addAttribute("noticeboard", (new Boolean(forum.isNoticeboard())).toString());

        Party party = Kernel.getContext().getParty();
        if (party != null) {

            PermissionDescriptor permission
                = new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                           forum,
                                           party);
            if (PermissionService.checkPermission(permission)) {
                generateModeXML(state, content, MODE_MODERATION);
            }
        }

        generateChildrenXML(state, content);
    }

    protected void generateModeXML(PageState state,
                                   Element parent,
                                   String mode) {
        String current = (String)state.getValue(m_mode);
        if (current == null) {
            current = MODE_THREADS;
        }

        Element content = parent.newChildElement("forum:forumMode", FORUM_XML_NS);

        state.setControlEvent(this, "mode", mode);

        content.addAttribute("mode",
                             mode);
        try {
            content.addAttribute("url",
                                 state.stateAsURL());
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot create url", ex);
        }
        content.addAttribute("selected",
                             current.equals(mode) ? "1" : "0");
        state.clearControlEvent();
    }
}


