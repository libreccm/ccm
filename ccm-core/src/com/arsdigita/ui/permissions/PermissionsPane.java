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
package com.arsdigita.ui.permissions;


import com.arsdigita.ui.util.GlobalizationUtil ; 

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.web.URL;
import com.arsdigita.web.RedirectSignal;

/**
 * A pane used to administer the permissions of one {@link
 * ACSObject}. This is a reusable component that can be embedded into
 * a page to provide a generic UI.  The page must have the "?po_id="
 * parameter to supply to ACSObject id of the item one is managing
 * permissions for.  This component attaches a
 * {@link com.arsdigita.ui.login.UserAuthenticationListener}
 * to restrict access to users who are logged in.
 *
 * @version $Id: PermissionsPane.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class PermissionsPane extends SimpleContainer
    implements Resettable,
               ActionListener,
               PermissionsConstants
{

    // non-shared parameter models; leave package scope for access from its members.


    private ParameterModel m_object_id_param = new BigDecimalParameter(OBJECT_ID);
    private ParameterModel m_searchString = new StringParameter(SEARCH_QUERY);
    private ParameterModel m_privArray = new ArrayParameter(PRIV_SET);

    private PrivilegeDescriptor[] m_privileges;
    private PermissionsTables m_allPermissions;
    private PermissionsHeader m_PermissionsHeader;
    private SimpleContainer   m_DirectPermissions;
    private Form              m_UserSearchForm;
    private SimpleContainer   m_InheritedPermissions;
    private SimpleComponent   m_ContextPanel;
    private SimpleContainer   m_PermissionsGrantPanel;
    private SimpleContainer   m_NoResultsPanel;

    private RequestLocal m_UserObjectInfo;


    /**
     * Default constructor creates components that
     * show the default privileges as defined in
     * PermissionsConstants interface
     */

    public PermissionsPane() {
        this(DEFAULT_PRIVILEGES);
    }

    /**
     * Creates a PermissionsPane with components showing the
     * privileges that are passed in as argument.
     */

    public PermissionsPane(PrivilegeDescriptor[] privs) {

        m_UserObjectInfo = new RequestLocal() {
                protected Object initialValue(PageState s) {
                    return new UserObjectStruct(s);
                }
            };

        m_privileges = privs;
    }

    /**
     * Overwrite this method to construct your default Permissions Pane
     * with the components you need. You can subclass anonymously overwriting
     * just the register method.
     * Note: the getXXX methods are lazy instantiators, i.e. they produce
     * the components only if not already there. (You can even overwrite the
     * getXXX components with your own implementation, e.g., if you want to
     * show a List instead of a Table for the direct permissions, but still
     * use a Table for the inherited permissions.
     *
     */

    public void register(Page p) {
        super.register(p);

        // add permissions components to this specific implementation
        add(getPermissionsHeader());
        add(getDirectPermissionsPanel());
        add(getUserSearchForm());
        add(getInheritedPermissionsPanel());
        add(getContextPanel());
        add(getPermissionGrantPanel());
        add(getNoSearchResultPanel());

        // set initial visibility of components
        p.setVisibleDefault(getPermissionsHeader(), true);
        p.setVisibleDefault(getDirectPermissionsPanel(), true);
        p.setVisibleDefault(getUserSearchForm(), true);
        p.setVisibleDefault(getInheritedPermissionsPanel(), true);
        p.setVisibleDefault(getContextPanel(), true);
        p.setVisibleDefault(getPermissionGrantPanel(), false);
        p.setVisibleDefault(getNoSearchResultPanel(),false);


        p.addActionListener(this);

        // add state parameters
        p.addGlobalStateParam(m_object_id_param);
        p.addGlobalStateParam(m_searchString);
        p.addGlobalStateParam(m_privArray);

    }

    /**
     * Implementation of interface bebop.Resettable.
     * Use <function>reset</function>
     * to reset permissions component to initial state,
     * e.g. if you embed it into another container.
     */

    public void reset(PageState ps) {
        showAdmin(ps);
    }

    /**
     * Utility method to get the authenicated user or group
     */

    public Party getRequestingUser(PageState s) {
        return ((UserObjectStruct)m_UserObjectInfo.get(s)).getParty();
    }

    /**
     * Utility method to get the ACSObject from the page state
     */

    public ACSObject getObject(PageState s) {
        return ((UserObjectStruct)m_UserObjectInfo.get(s)).getObject();
    }

    /**
     * Returns the title "Permissions on object articles", e.g.
     */

    public Label getTitle() {
        return ((PermissionsHeader)getPermissionsHeader()).getTitle();
    }

    /**
     * Returns a string array of privilege names as defined in the constructor
     */

    public String[] getPrivileges() {
        String[] p = new String[m_privileges.length];
        for (int i=0; i<p.length; i++) {
            p[i]=m_privileges[i].getName();
        }
        return p;
    }

    /**
     * Produces the direct and inherited permission tables to the privileges
     * defined in the constructor.
     * @see getDirectPermissionsPanel(), getInheritedPermissionsPanel()
     */

    private PermissionsTables getPermissionsTables() {
        if (m_allPermissions == null) {
            m_allPermissions = new PermissionsTables(m_privileges, this);
        }
        return  m_allPermissions;
    }

    /**
     * Returns the bebop component with a table for the direct permission on the
     * privileges defined in the constructor
     * @see #getInheritedPermissionsPanel()
     */

    public SimpleContainer getDirectPermissionsPanel() {
        m_DirectPermissions = getPermissionsTables().getPermissions(DIRECT);
        return m_DirectPermissions;
    }

    /**
     * Returns the bebop component with a table for the inherited permission on the
     * privileges defined in the constructor. The table is non-editable.
     * @see #getDirectPermissionsPanel()
     */

    public SimpleContainer getInheritedPermissionsPanel() {
        m_InheritedPermissions = getPermissionsTables().getPermissions(INHERITED);
        return m_InheritedPermissions;
    }

    /**
     * This is an outstanding item.
     */

    public SegmentedPanel getUniversalPermissionsPanel() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a bebop form for user and group search.
     */

    public Form getUserSearchForm() {
        if (m_UserSearchForm==null) {
            m_UserSearchForm = new UserSearchForm(this);
        }
        return m_UserSearchForm;
    }

    /**
     * Returns a panel with a form with 2 checkbox groups,
     * one for parties to choose, one for privileges to assign.
     */

    public SimpleContainer getPermissionGrantPanel() {
        if (m_PermissionsGrantPanel==null) {
            PermissionsGrant permGrant = new PermissionsGrant(this);
            m_PermissionsGrantPanel = permGrant.getPanel();
        }
        return  m_PermissionsGrantPanel;
    }

    /**
     * Returns a bebop container with the title to this object
     * and a navigation bar, specific for the UI at /permissions/.
     */

    public SimpleContainer getPermissionsHeader() {
        if (m_PermissionsHeader == null) {
            m_PermissionsHeader = new PermissionsHeader(this);
        }
        return  m_PermissionsHeader;
    }

    /**
     * Returns a bebop panel indicating that the user search
     * yielded no results. It is customized in the xsl stylesheet.
     */

    public SimpleContainer getNoSearchResultPanel() {
        if (m_NoResultsPanel==null) {
            Label errorMsg = new Label(NO_RESULTS.localize()+"");
            errorMsg.setClassAttr("errorBullet");
            BoxPanel bp = new BoxPanel();
            bp.add(errorMsg);
            bp.add(new UserSearchForm(this));
            m_NoResultsPanel = new SegmentedPanel().addSegment(new Label(" "),bp);
        }
        return m_NoResultsPanel;
    }

    /**
     * Returns a bebop panel with a link to the permissions administration page
     * of the object's direct ancestor (parent).
     */

    public SimpleComponent getContextPanel() {
        if (m_ContextPanel == null) {
            m_ContextPanel = getPermissionsTables().makeContextPanel();
        }
        return  m_ContextPanel;
    }

    ParameterModel getObjectIDParam() {
        return m_object_id_param;
    }

    ParameterModel getSearchString() {
        return m_searchString;
    }

    ParameterModel getPrivilegeParam() {
        return m_privArray;
    }

    /**
     *  Shows panel with no results to user search.
     */

    public void showNoResults(PageState s) {
        getDirectPermissionsPanel().setVisible(s, false);
        getInheritedPermissionsPanel().setVisible(s, false);
        getContextPanel().setVisible(s, false);
        getUserSearchForm().setVisible(s, false);
        getPermissionGrantPanel().setVisible(s,false);
        getNoSearchResultPanel().setVisible(s, true);
    }

    /**
     * Show the Grant privileges panel
     */

    public void showGrant(PageState s) {
        getDirectPermissionsPanel().setVisible(s, false);
        getInheritedPermissionsPanel().setVisible(s, false);
        getContextPanel().setVisible(s, false);
        getUserSearchForm().setVisible(s, false);
        getNoSearchResultPanel().setVisible(s, false);
        getPermissionGrantPanel().setVisible(s,true);
    }

    /**
     * Shows the administration page of permissions to
     * one object.
     */

    public void showAdmin(PageState s) {
        getDirectPermissionsPanel().setVisible(s, true);
        getInheritedPermissionsPanel().setVisible(s, true);
        getContextPanel().setVisible(s, true);
        getUserSearchForm().setVisible(s, true);
        getPermissionGrantPanel().setVisible(s, false);
    }

    public void actionPerformed(ActionEvent e) {
        PageState s = e.getPageState();

        /**
         * check if viewing user has admin privilege on this Object, after
         * Action Event fires everytime the component is visible.
         *
         */
        if (this.isVisible(s)) {
            PermissionDescriptor admin = new PermissionDescriptor
                (PrivilegeDescriptor.ADMIN,
                 getObject(s),
                 getRequestingUser(s));

            if (!PermissionService.checkPermission(admin)) {
                final URL url = URL.there(s.getRequest(), 
                                          "/permissions/denied");

                throw new RedirectSignal(url, false);
            }
        }
    }
}
