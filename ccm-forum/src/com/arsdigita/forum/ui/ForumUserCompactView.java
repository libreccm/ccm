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

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ui.admin.ModerationView;
import com.arsdigita.forum.ui.admin.PermissionsView;
import com.arsdigita.forum.ui.admin.SetupView;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

/**
 * A Bebop Page which provides the complete UI for the forum application in a
 * CCM specific compact style. Using this style the different components
 * (view modes or forum modes) as the threads lists, the list of topics or the
 * alerts management panel etc. are presented onto one page in a condensed form.
 *
 * Main task is to provide a <i>mode selection facility</i> (by default styled
 * as a tabulator bar) and to switch to a page component based on the active
 * selection (by default displayed beyond the mode selection facility).
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 * @author Chris Gilbert (chrisg23)
 * @author Jens Pelzetter (jensp)
 * @version $Revision: 1.3 $ $Author: chrisg23 $ $Date: 2006/03/09 13:48:15 $
 */
public class ForumUserCompactView extends ModalContainer implements Constants {

    /** Private logger instance for debugging purpose. */
    private static Logger s_log = Logger.getLogger(ForumUserCompactView.class);
    // Denotes the 6 panels of the user interface, also used as marker to store
    // and select the active panel
    /** Denotation of the 'threads' forum mode */
    public static final String MODE_THREADS = "threads";
    /** Denominator of the 'topics' forum mode */
    public static final String MODE_TOPICS = "topics";
    /** Denominator of the 'alerts handling' forum mode */
    public static final String MODE_ALERTS = "alerts";
    /** Denominator of the 'moderation handling' forum mode */
    public static final String MODE_MODERATION = "moderation";
    /** Denominator of the 'permission administration' forum mode
     *  (administrators only) */
    public static final String MODE_PERMISSIONS = "permissions";
    /** Denominator of the 'configuration' forum mode (administrators only)*/
    public static final String MODE_SETUP = "setup";
    /** Holds the current active mode */
    private StringParameter m_mode;
    /** Object containing the threads panel (main working panel for users) */
    private ThreadsPanel m_threadsView;
    /** Object containing the topics panel */
    private TopicsPanel m_topicsView;
    /** Object containing the alerts management panel */
    private ForumAlertsView m_alertsView;
    /** Object containing the moderation panel */
    private ModerationView m_moderationView;
    /** Object containing the setup panel */
    private SetupView m_setupView;
    /** Object containing the permission management panel*/
    private PermissionsView m_permissionsView;

    /**
     * Default Constructor. Initializes the forum user interface elements.
     */
    public ForumUserCompactView() {

        // determine namespace
        super(FORUM_XML_PREFIX + ":forum", FORUM_XML_NS);

        m_mode = new StringParameter("mode");

        // setup panels which make up the forum
        m_threadsView = new ThreadsPanel();
        m_topicsView = new TopicsPanel();
        m_alertsView = new ForumAlertsView();
        // administration section
        m_moderationView = new ModerationView();
        m_setupView = new SetupView();
        m_permissionsView = new PermissionsView();

        add(m_threadsView);
        add(m_topicsView);
        add(m_alertsView);
        // administration section
        add(m_moderationView);
        add(m_setupView);
        add(m_permissionsView);

        setDefaultComponent(m_threadsView);
    }

    /** 
     * 
     * @param p
     */
    @Override
    public void register(Page p) {

        super.register(p);
        p.addGlobalStateParam(m_mode);
    }

    /**
     *
     * @param state
     * @throws ServletException
     */
    @Override
    public void respond(PageState state) throws ServletException {

        super.respond(state);

        Party party = Kernel.getContext().getParty();
        Forum forum = ForumContext.getContext(state).getForum();

        String mode = (String) state.getControlEventValue();
        state.setValue(m_mode, mode);

        setVisible(state, party, forum, mode);
    }

    /**
     * Checks for the given forum mode (parameter value) whether its prerequisites
     * are given (currently permission, but additional properties may be added here).
     * If positive the panel is set visible, otherwise a login screen is
     * presented.
     *
     * 
     * @param state
     * @param party  currently logged in user (or null if none)
     * @param forum  the forum instance to handle
     * @param mode   forum mode to check visibility for
     */
    protected void setVisible(PageState state, Party party,
                              Forum forum, String mode) {

        PermissionDescriptor forumAdmin =
                             new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                                      forum, party);

        if (MODE_TOPICS.equals(mode)) {
            if (Forum.getConfig().topicCreationByAdminOnly()) {
                if (party == null) {
                    UserContext.redirectToLoginPage(state.getRequest());
                }
                PermissionService.assertPermission(forumAdmin);
            }
            setVisibleComponent(state, m_topicsView);
        } else if (MODE_ALERTS.equals(mode)) {
            if (party == null) {
                UserContext.redirectToLoginPage(state.getRequest());
            }
            setVisibleComponent(state, m_alertsView);
        } else if (MODE_MODERATION.equals(mode)) {
            if (party == null) {
                UserContext.redirectToLoginPage(state.getRequest());
            }
            PermissionService.assertPermission(forumAdmin);
            setVisibleComponent(state, m_moderationView);
        } else if (MODE_PERMISSIONS.equals(mode)) {
            if (party == null) {
                UserContext.redirectToLoginPage(state.getRequest());
            }
            PermissionService.assertPermission(forumAdmin);
            setVisibleComponent(state, m_permissionsView);
        } else if (MODE_SETUP.equals(mode)) {
            if (party == null) {
                UserContext.redirectToLoginPage(state.getRequest());
            }
            PermissionService.assertPermission(forumAdmin);
            setVisibleComponent(state, m_setupView);
        } else if (MODE_THREADS.equals(mode)) {
            if (party == null) {
                UserContext.redirectToLoginPage(state.getRequest());
            }
            setVisibleComponent(state, m_threadsView);
        }
    }

    /**
     * Generate the page XML.
     * Overwrites SimpleContainer standard method
     * 
     * @param state
     * @param parent
     */
    @Override
    public void generateXML(PageState state, Element parent) {

        Element content = generateParent(parent);
        Forum forum = ForumContext.getContext(state).getForum();
        content.addAttribute("title", forum.getTitle());
        content.addAttribute(
                "noticeboard",
                (new Boolean(forum.isNoticeboard())).toString());

        // If visitor not logged in, set user (=party) to publicUser
        Party party = Kernel.getContext().getParty();
        if (party == null) {
            party = Kernel.getPublicUser();
        }

        // generate tab bar for panel (mode) selection
        generateModes(state, content, party, forum);
        // generate 
        generateChildrenXML(state, content);
    }

    /**
     * Initializes the mode selection facility (usually a tab bar)
     *
     * @param state
     * @param content
     * @param party
     * @param forum
     */
    protected void generateModes(PageState state, Element content,
                                 Party party, Forum forum) {

        PermissionDescriptor adminPermission = new PermissionDescriptor(
                PrivilegeDescriptor.ADMIN,
                forum,
                party);
        PermissionDescriptor readPermission = new PermissionDescriptor(
                PrivilegeDescriptor.get(Forum.FORUM_READ_PRIVILEGE),
                forum,
                party);


        // currently thread panel is always shown. If read access should be
        // bound to logged in users, additional logic is required here.
        // jensp 2011-10-02: Additional logic added
        if (forum.isPublic()) {
            generateModeXML(state, content, MODE_THREADS,
                            Text.gz("forum.ui.modeThreads"));
        } else {
            if (PermissionService.checkPermission(readPermission)) {
                generateModeXML(state, content, MODE_THREADS,
                                Text.gz("forum.ui.modeThreads"));
            }
        }
        // topics panel is always shown as well if not restricted to admins.
        if (!Forum.getConfig().topicCreationByAdminOnly()) {
            generateModeXML(state, content, MODE_TOPICS,
                            Text.gz("forum.ui.modeTopics"));
        }
        // alerts panel is always shown as well, no private read access avail.
        generateModeXML(state, content, MODE_ALERTS,
                        Text.gz("forum.ui.modeAlerts"));

        // admin section
        if (PermissionService.checkPermission(adminPermission)) {
//          generateModeXML(state, content, MODE_MODERATION,
//                          Text.gz("forum.ui.modeAlerts"));
            if (Forum.getConfig().showNewTabs()) {
                generateModeXML(state, content, MODE_SETUP,
                                Text.gz("forum.ui.modeSetup"));
                generateModeXML(state, content, MODE_PERMISSIONS,
                                Text.gz("forum.ui.modePermissions"));
            } else {
                // Generate old moderation panel instead of setup
                generateModeXML(state, content, MODE_MODERATION,
                                Text.gz("forum.ui.modeAlerts"));
                
            }
            // In case topic creation is bound to admin (and therefore not
            // created above) we must create xml here.
            if (Forum.getConfig().topicCreationByAdminOnly()) {
                generateModeXML(state, content, MODE_TOPICS,
                                Text.gz("forum.ui.modeTopics"));
            }
        }
    }

    /**
     * Generates a forum mode selection entry (usually a tab).
     * 
     * Currently the mode string is used to create the label in xsl file.
     * 
     * @param state
     * @param parent
     * @param mode forum mode (threadspanel, topicspanel, alertpanel, ...) to
     *             create entry for
     * @deprecated use generateModeXML(PageState, Element, String,
     *                                 GlobalizedMessage)  instead
     */
    protected void generateModeXML(PageState state,
                                   Element parent,
                                   String mode) {
        generateModeXML(state, parent, mode, null);
    }

    /**
     * Generates a forum mode selection entry (usually a tab).
     *
     *
     * @param state
     * @param parent
     * @param mode forum mode (threadspanel, topicspanel, alertpanel, ...) to
     *             create entry for
     * @param label to denominate the mode
     */
    protected void generateModeXML(PageState state,
                                   Element parent,
                                   String mode,
                                   GlobalizedMessage label) {

        String current = (String) state.getValue(m_mode);
        if (current == null) {
            current = MODE_THREADS;  // used as default mode
        }

        Element content =
                parent.newChildElement(FORUM_XML_PREFIX + ":forumMode",
                                       FORUM_XML_NS);

        state.setControlEvent(this, "mode", mode);

        content.addAttribute("mode",
                             mode);

        // add link to switch to 'mode'
        try {
            content.addAttribute("url",
                                 state.stateAsURL());
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot create url", ex);
        }
        // add localized mode label
        // if-else should be removed when deprecated method above is removed!
        if (label == null) {
            content.addAttribute("label",
                                 "UNAVAILABLE");
        } else {
            content.addAttribute("label",
                                 (String) label.localize());
        }
        // add if current mode is actually selected
        content.addAttribute("selected",
                             current.equals(mode) ? "1" : "0");
        state.clearControlEvent();
    }
}
