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
package com.arsdigita.portalserver;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Filter;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.AgentPortlet;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Category;

/**
 * <p>A <code>PortalSite</code> is a mountable Portal instance, complete 
 * with URL. A PortalSite is made up of one or more <code>PortalTabs</code>.
 * A <code>PortalTab</code> is in turn comprised of zero or more Portlets.</p>
 *
 * <p>A <code>PortalSite</code> presents what the User considers to be
 * a "Portal". For the sake of clarity, however, in the source code side 
 * of things, a <code>Portal</code> is actually a domain class in CCM Core, 
 * used as a foundation class by <code>PortalSite</code>.</p>
 *
 * @author Justin Ross
 * @author Archit Shah
 * @author Jim Parsons
 * @version $Id: portalserver/PortalSite.java  pboy $
 */
public class PortalSite extends Application {

    /** Logger instance for debugging */
    private static Category s_log = Category.getInstance(PortalSite.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.Workspace";

    private static final String PARTICIPANT_ID_QUERY =
        "com.arsdigita.workspace.WorkspaceParticipantIDs";

    private static final int SORT_KEY_JUMP = 10;

    private Role m_memberRole;
    private boolean m_wasNew = false;

    /**
     *
     * @return
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

  /**
   * Constructor for PortalSite. Access is protected. Use a 
   * <code>createPortalSite()</code> method for a new PortalSite object.
   */
    protected PortalSite(DataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected void initialize() {
        super.initialize();

        if (isNew()) {

            setReady(false);

            setDraft(false);

            setPersonalizable(false);

            setUnarchived();

            setCreationDate();
        }
    }

    public static PortalSite createPortalSite
        (String urlName, String title, PortalSite parent,
         boolean inheritPermissions) {

        ApplicationType type =
            ApplicationType.retrieveApplicationTypeForApplication
            (BASE_DATA_OBJECT_TYPE);

        PortalSite ps = (PortalSite) Application.createApplication
            (type, urlName, title, parent);

        ps.setUnarchived();
        ps.setDraft(false);
        ps.setCreationDate();
        ps.setPersonalizable(false);

        if (!inheritPermissions) {
            PermissionService.setContext(ps, null);
        }

        return ps;
    }


    /**
     * Creates an Instance ot ApplicationType PortalSite as a legacy free
     * application.
     * @param urlName
     * @param title
     * @param parent
     * @return
     */
    public static PortalSite createPortalSite(String urlName, 
                                              String title,
                                              PortalSite parent) {

        ApplicationType type =
            ApplicationType.retrieveApplicationTypeForApplication
            (BASE_DATA_OBJECT_TYPE);

        PortalSite ps = (PortalSite) Application.createApplication
            (type, urlName, title, parent);

        ps.setUnarchived();

        ps.setDraft(false);

        ps.setCreationDate();

        ps.setPersonalizable(false);

        return ps;

    }

    /**
     * @deprecated Use PortalSite.retrieveAllPortalSites().
     */
    public static PortalSiteCollection retrieveAll() {
        return PortalSite.retrieveAllPortalSites();
    }

    /**
     * Returns a collection of all PortalSite's currently mounted on 
     * the server. Cannot return null. The collection returned includes
     * archived portals as well. Most applications will want to call
     * retrieveAllActivePortalSites()
     */
    public static PortalSiteCollection retrieveAllPortalSites() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        PortalSiteCollection portalsiteCollection = new PortalSiteCollection
            (dataCollection);

        return portalsiteCollection;
    }

    /**
     * Returns a collection of all active PortalSite's currently mounted on 
     * the server. Cannot return null. The collection returned does not include
     * archived portals.
     */
    public static PortalSiteCollection retrieveAllActivePortalSites() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        PortalSiteCollection portalsiteCollection = new PortalSiteCollection
            (dataCollection);

        portalsiteCollection.filterForArchived();

        return portalsiteCollection;
    }

    /**
     * 
     * @param siteNode
     * @return Can return null.
     */
/*  OBVIOUSLY NO LONGER USED
    public static PortalSite retrievePortalSiteForSiteNode(SiteNode siteNode) {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.workspace.workspaceForSiteNodeID");

        query.setParameter("siteNodeID", siteNode.getID());

        PortalSite portalsite = null;

        if (query.next()) {
            DataObject dataObject = (DataObject) query.get("workspace");
            portalsite = PortalSite.retrievePortalSite(dataObject);
        }

        query.close();

        return portalsite;
    }
*/
    // Can return null.
    public static PortalSite retrievePortalSite(BigDecimal id) {
        return (PortalSite) Application.retrieveApplication(id);
    }

    // Can return null.
    public static PortalSite retrievePortalSite(OID oid) {
        return (PortalSite) Application.retrieveApplication(oid);
    }

    // Can return null.
    public static PortalSite retrievePortalSite(DataObject dataObject) {
        return (PortalSite) Application.retrieveApplication(dataObject);
    }

    @Override
    protected void beforeSave() {
        m_wasNew = isNew();

        super.beforeSave();

    }

    @Override
    protected void afterSave() {
        super.afterSave();

        if (m_wasNew) {
            Role role = Role.createRole
                (this, "Members", "Member", "", true, Role.MEMBER_TYPE);
            setMemberRole(role);
            role.save();

            PermissionService.grantPermission
                (new PermissionDescriptor
                 (PrivilegeDescriptor.READ, this, getMemberRole()));

            PortalSite parent = getPortalSiteForApplication(this);

            if (parent != null) {
                parent.addMember(getMemberRole());
                parent.save();
            }

            m_wasNew = false;
        }
    }

  //protected void beforeDelete() {
    @Override
    public void beforeDelete() {
        //First, make certain that portalsite to be deleted has no children
        PortalSiteCollection psc = getAllChildPortalSites();
        if(!psc.isEmpty())
            throw new UnsupportedOperationException
              ("Deletion of PortalSites does not support recursion: you" + 
                "must delete children of this portal site first.");

    }

    //
    // Association properties
    //

    public ApplicationCollection getFullPagePortalSiteApplications() {
        ApplicationCollection applications = getChildApplications();

        applications.filterToPortalSiteApplications();
        applications.filterToHasFullPageView();

        return applications;
    }

    /**
     * This method is used by portlet create and app create form flow
     * to determine if an apptype is already present for a PortalSite.
     */
    public boolean isAppTypeInPortalSite(ApplicationType appType) {
        ApplicationCollection apps = getFullPagePortalSiteApplications();
        ApplicationType atp;

        while (apps.next()) {
            atp = apps.getApplication().getApplicationType();

            if (atp.getID().equals(appType.getID())) {
                apps.close();
                return true;
            }
        }

        apps.close();

        return false;
    }

    public PortalSiteCollection getChildPortalSites() {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.workspace.childWorkspacesForApplicationID");

        query.setParameter("applicationID", getID());

        DataCollection collection =
            new DataQueryDataCollectionAdapter(query, "workspace");

        query.close();

        PortalSiteCollection psc = new PortalSiteCollection(collection);
    
        psc.filterForUnarchived();

        return psc;
    }

    public PortalSiteCollection getAllChildPortalSites() {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.workspace.childWorkspacesForApplicationID");

        query.setParameter("applicationID", getID());

        DataCollection collection =
            new DataQueryDataCollectionAdapter(query, "workspace");

        query.close();

        return new PortalSiteCollection(collection);
    }

    public PortalSiteCollection getRelatedPortalSites() {
        DataAssociation association = (DataAssociation) get("relatedWorkspace");

        PortalSiteCollection psc = 
                              new PortalSiteCollection(association.cursor());
        psc.filterForUnarchived();
        return psc;
    }

    public PortalSiteCollection getAllRelatedPortalSites() {
        DataAssociation association = (DataAssociation) get("relatedWorkspace");

        PortalSiteCollection psc = 
                              new PortalSiteCollection(association.cursor());
        return psc;
    }

    public static PortalSite getPortalSiteForApplication(Application app) {
        Application application = app.getParentApplication();

        if (application == null) {
            return null;
        }

        // Sometimes a Portalsite has an ordinary application as its
        // parent.  This is true, for instance, of personal
        // portals.  In these cases we return null, because there
        // is no parent *Portal*.

        if (!(application instanceof PortalSite)) {
            return null;
        }

        return (PortalSite) application;
    }

    public static PortalSite getPortalSiteForAppPortlet(AppPortlet portlet) {
        Resource parentRes = portlet.getParentResource();

        if (parentRes == null) {
            return null;
        }
       
        //Now we have (possibly) the parent application of the portlet...

        if (!(parentRes instanceof Application)) {
            return null;
        }
	
	//if the parent of this portlet is the PortalSite, return the PortalSite.
	//otherwise, try to fetch the parent application's parent PortalSite
	if  (parentRes instanceof PortalSite) {
	    return (PortalSite)parentRes;
	} else {
	    return getPortalSiteForApplication((Application)parentRes);
	}
    }

    // Can return null.
    public static PortalSite getCurrentPortalSite(HttpServletRequest request) {
        String key = "com.arsdigita.workspace.Workspace.currentWorkspace";

        PortalSite currentPortalSite = null;

        synchronized (request) {
            currentPortalSite = (PortalSite) request.getAttribute(key);

            if (currentPortalSite == null) {
                currentPortalSite = doGetCurrentPortalSite(request);

                request.setAttribute(key, currentPortalSite);
            }
        }

        return currentPortalSite;
    }

    private static PortalSite 
        doGetCurrentPortalSite(HttpServletRequest request) {
        // First, assume that the user is at a PortalSite already,
        // since we can save a query if we're right.  This logic will
        // not make sense if we find that this method is called mostly
        // from sub applications of a portal site, since it incurs an
        // extra query in that case.

        Application application = Application.getCurrentApplication(request);

        if (application instanceof PortalSite) {
            return (PortalSite) application;
        }

        while (true) {
            if (application == null) {
                return null;
            }

            application = application.getParentApplication();

            if (application instanceof PortalSite) {
                return (PortalSite) application;
            }
        }
    }

    public static PortalSiteCollection getRootPortalSites() {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.workspace.rootWorkspaces");

        DataCollection collection =
            new DataQueryDataCollectionAdapter(query, "workspace");

        query.close();

        PortalSiteCollection psc = new PortalSiteCollection(collection);
      
        psc.filterForUnarchived();

        return psc;
    }

    public static PortalSiteCollection getAllRootPortalSites() {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.workspace.rootWorkspaces");
        Application app = 
          Application.retrieveApplicationForPath("/personal-portal/");

        if (app != null)
          query.setParameter("parentID",app.getID());

        DataCollection collection =
            new DataQueryDataCollectionAdapter(query, "workspace");

        query.close();

        return new PortalSiteCollection(collection);
    }

    public void addRelatedPortalSite(PortalSite portalsite) {
        add("relatedWorkspace", portalsite);
    }

    public void removeRelatedPortalSite(PortalSite portalsite) {
        remove("relatedWorkspace", portalsite);
    }

    public boolean isDirectParticipant(Party party) {
        DataAssociationCursor dac =
            ((DataAssociation) get("participants")).cursor();
        dac.addEqualsFilter("id", party.getID());
        if (dac.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isParticipant(Party party) {
        throw new UnsupportedOperationException();
    }

    public boolean isDirectMember(Party party) {
        Group g = getMemberRole();
        return g.hasDirectMemberOrSubgroup(party);
    }

    public boolean isMember(Party party) {
        Group g = getMemberRole();
        if (party instanceof User) {
            return g.hasMember((User) party);
        } else if (party instanceof Group) {
            return g.hasSubgroup((Group) party);
        } else {
            throw new IllegalArgumentException(
                                        "party must be a user or a group");
        }
    }

    /**
     * add a participant as a member, the party should not already be a
     * participant. adds specified party to member group, and makes the
     * specified party a participant
     **/
    public void addMember(Party member) {
        addParticipant(member);
        Group g = getMemberRole();
        if (member instanceof User) {
            User u = (User) member;
            g.addMember(u);
        } else if (member instanceof Group) {
            Group pGroup = (Group) member;
            g.addSubgroup(pGroup);
        } else {
            throw new IllegalArgumentException(
                                     "party must be a user or a group");
        }
    }

    /**
     * Adds participant and grants participant read privilege on the workspace.
     **/
    public void addParticipant(Party participant) {
        addParticipant(participant, true);
    }

    /**
     * Adds participant and optionally grants participant read privilege on
     * the PortalSite.
     *
     * @param addDefaultPriv whether or not the read privilege should be
     * granted
     **/
    public void addParticipant(Party participant, boolean addDefaultPriv) {
        add("participants", participant);
        if (addDefaultPriv) {
            PermissionService.grantPermission(new PermissionDescriptor(
                                                                       PrivilegeDescriptor.READ, this, participant));
        }
    }

    /**
     * remove a participant or member from workspace and revoke privileges on
     * the Portal Site 
     **/
    public void removeParticipant(Party participant) {
        RoleCollection rc = getRoles();
        if (participant instanceof User) {
            while (rc.next()) {
                Role r = rc.getRole();
                r.removeMember((User) participant);
                r.save();
            }
        } else if (participant instanceof Group) {
            while (rc.next()) {
                Role r = rc.getRole();
                r.removeSubgroup((Group) participant);
                r.save();
            }
        } else {
            throw new IllegalArgumentException(
                              "received party that was neither user nor group");
        }

        remove("participants", participant);

        // revoke direct grants on this
        for (Iterator privIter = PermissionService.getDirectPrivileges
                 (getOID(), participant.getOID());
             privIter.hasNext(); ) {
            PrivilegeDescriptor priv = (PrivilegeDescriptor) privIter.next();
            PermissionService.revokePermission
                (new PermissionDescriptor(priv, this, participant));
        }

        // revoke direct grants on applications in the portalsite
        ApplicationCollection ac = getFullPagePortalSiteApplications();
        while (ac.next()) {
            OID appOID = new OID(ac.getSpecificObjectType(), ac.getID());
            Iterator appPrivs = PermissionService.getDirectPrivileges
                (appOID, participant.getOID());
            while (appPrivs.hasNext()) {
                PrivilegeDescriptor priv = (PrivilegeDescriptor) appPrivs.next();
                PermissionService.revokePermission
                    (new PermissionDescriptor(
                        priv, appOID, participant.getOID()));
            }
        }
    }

    public PartyCollection getParticipants() {
        return new PartyCollection(
                                   ((DataAssociation) get("participants")).cursor());
    }


    public long getParticipantCount() {
        return ((DataAssociation)get("participants")).cursor().size();
    }


    public PartyCollection getNonParticipants() {
        DataCollection dc =
            SessionManager.getSession()
            .retrieve("com.arsdigita.kernel.Party");
        Filter f = dc.addNotInSubqueryFilter("id", PARTICIPANT_ID_QUERY);
        f.set("workspaceID", getID());
        return new PartyCollection(dc);
    }


    /**
     * <p>Get a collection of all participants in the Portal Site whose
     * initial (see {@link getParticipantInitials
     * getParticipantInitials} for a definition of a participant's
     * initial) is the specified value.</p>
     *
     * @param initial Single-character string, must be uppercase
     **/
    public PartyCollection getParticipantsWithInitial(String initial) {
     // Assert.assertNotNull(initial);
        Assert.exists(initial);
     // Assert.assertTrue(initial.length() == 1, "Initial needs length 1");
        Assert.isTrue(initial.length() == 1, "Initial needs length 1");
     // Assert.assertTrue(initial.equals(initial.toUpperCase()),
        Assert.isTrue(initial.equals(initial.toUpperCase()),
                          "Initial must be uppercase");

        DataAssociationCursor dac =
            ((DataAssociation)get("participants")).cursor();
        Filter f = dac.addInSubqueryFilter
            ("id", "com.arsdigita.workspace.WorkspaceParticipantsWithInitial");
        f.set("workspaceID", getID());
        f.set("nameInitial", initial);

        return new PartyCollection(dac);
    }

    /**
     * Returns the member role. This role can be modified directly to change
     * the parties in the role.
     **/
    public Role getMemberRole() {
        if (m_memberRole == null) {
            DataAssociation roles = (DataAssociation) get("roles");
            DataAssociationCursor rolesCursor
                = roles.getDataAssociationCursor();
            rolesCursor.addEqualsFilter("type", Role.MEMBER_TYPE);

            if ( rolesCursor.next() ) {
                m_memberRole = (Role) DomainObjectFactory.
                    newInstance(rolesCursor.getDataObject());
            }

            rolesCursor.close();
        }

        return m_memberRole;
    }

    private void setMemberRole(Role role) {
        m_memberRole = role;
    }

    /**
     * Returns the collection of roles in this portal site. The returned roles
     * can be modified directly to change the parties assigned to each role.
     **/
    public RoleCollection getRoles() {
        return new RoleCollection(
                            ((DataAssociation) get("roles")).cursor(), this);
    }

    public RoleCollection getDirectRolesFor(Party p) {
        DataAssociationCursor dac = ((DataAssociation) get("roles")).cursor();
        if (p instanceof User) {
            Filter f = dac.addInSubqueryFilter(
                           "id", "com.arsdigita.workspace.DirectRolesForUser");
            f.set("participantID", p.getID());
        } else if (p instanceof Group) {
            Filter f = dac.addInSubqueryFilter(
                           "id", "com.arsdigita.workspace.DirectRolesForGroup");
            f.set("participantID", p.getID());
        } else {
            throw new IllegalArgumentException(
                           "received party that was neither user nor group");
        }
        return new RoleCollection(dac);
    }

    /**
     * Returns the collection of roles in this workspace that the specified
     * party is contained in.
     **/
    public RoleCollection getRolesFor(Party p) {
        DataAssociationCursor dac = ((DataAssociation) get("roles")).cursor();
        if (p instanceof User) {
            Filter f = dac.addInSubqueryFilter(
                             "id", "com.arsdigita.workspace.RolesForUser");
            f.set("participantID", p.getID());
        } else if (p instanceof Group) {
            Filter f = dac.addInSubqueryFilter(
                             "id", "com.arsdigita.workspace.RolesForGroup");
            f.set("participantID", p.getID());
        } else {
            throw new IllegalArgumentException(
                             "received party that was neither user nor group");
        }
        return new RoleCollection(dac);
    }

    /////Tab Management

    public void addPortalTab(PortalTab ptab) {
        add("workspaceTab",ptab);
        moveTabToTail(ptab);
    }

    public void removePortalTab(PortalTab ptab) {
        remove("workspaceTab",ptab);
    }

    // Can return null.
    public PortalTabCollection getTabsForPortalSite() {
        DataAssociation association = (DataAssociation)get("workspaceTab");

        if (association == null) {
            return null;
        }
        DataAssociationCursor tabsCursor
            = association.getDataAssociationCursor();
        tabsCursor.addOrder("sortKey");

        return new PortalTabCollection(tabsCursor);
    }

    public void swapTabWithPrevious(PortalTab ptab)
        throws com.arsdigita.persistence.PersistenceException
    {
        int newKey = ptab.getSortKey() - (SORT_KEY_JUMP + 1);
        ptab.setSortKey(newKey);

        ptab.save();

        normalizeTabSortKeys();

    }

    public void swapTabWithNext(PortalTab ptab)
        throws com.arsdigita.persistence.PersistenceException
    {
        int newKey = ptab.getSortKey() + (SORT_KEY_JUMP + 1);

        ptab.setSortKey(newKey);

        ptab.save();

        normalizeTabSortKeys();
    }

    public void moveTabToHead(PortalTab ptab)
    {
        ptab.setSortKey(Integer.MIN_VALUE);

        ptab.save();

        normalizeTabSortKeys();
    }

    public void moveTabToTail(PortalTab ptab)
    {
        ptab.setSortKey(Integer.MAX_VALUE);

        ptab.save();

        normalizeTabSortKeys();
    }

    public void normalizeTabSortKeys()
    {
        PortalTab ptab;

        PortalTabCollection wtcoll = getTabsForPortalSite();

        for  (int index = SORT_KEY_JUMP; wtcoll.next(); index += SORT_KEY_JUMP)
            {
                ptab = wtcoll.getPortalTab();

                ptab.setSortKey(index);

                ptab.save();
            }


    }

    //
    // Member properties
    //

    public boolean isReady() {
        return ((Boolean)get("isReady")).booleanValue();
    }

    public void setReady(boolean isReady) {
        set("isReady", new Boolean(isReady));
    }

    public boolean isDraft() {
        return ((Boolean)get("isDraft")).booleanValue();
    }

    public void setDraft(boolean isDraft) {
        set("isDraft", new Boolean(isDraft));
    }

    public boolean isPersonalizable() {
        return ((Boolean)get("isPersonalizable")).booleanValue();
    }

    public void setPersonalizable(boolean isPersonalizable) {
        set("isPersonalizable", new Boolean(isPersonalizable));
    }

    public boolean isSubPortal() {
        return ((Boolean)get("isSubPortal")).booleanValue();
    }

    public void setIsSubPortal(boolean issubportal) {
        set("isSubPortal", new Boolean(issubportal));
    }

   public boolean isArchived() {
        return ((Boolean)get("isArchived")).booleanValue();
   }

   public void setArchived() {
        set("isArchived", Boolean.TRUE);
   }

   public void setUnarchived() {
        set("isArchived", Boolean.FALSE);
   }
   
   public void setCreationDate( Date cdate) {
        set("creationDate",cdate);
   }

   public void setCreationDate() {
        Calendar rightNow = GregorianCalendar.getInstance();
        Date cdate = rightNow.getTime();
        set("creationDate",cdate);
   }

   public Date getCreationDate() {
     return (Date)get("creationDate");
   }

   public void setArchiveDate(Date adate) {
      set("archiveDate",adate);
   }

   public void setArchiveDate() {
        Calendar rightNow = GregorianCalendar.getInstance();
        Date adate = rightNow.getTime();
        set("archiveDate",adate);
   }

   public Date getArchiveDate() {
      return (Date)get("archiveDate");
   }

   public void archive() {
      Party p;
  
      //1 - set archive bit
      setArchived();

      //2 - drop permissions
      PartyCollection pc = getParticipants();
      while(pc.next()) {
          p = pc.getParty();
          removeParticipant(p);
      }

      //3 set archivedate
      setArchiveDate();

   }

   public void archiveRecurse() {

        PortalSite portal;

        archive();

        PortalSiteCollection psc = getChildPortalSites();
        psc.filterForUnarchived();
        while(psc.next()) {
            portal = psc.getPortalSite();
            portal.archiveRecurse();
        }

   }

   public void unarchive() {
        this.setUnarchived();
        this.setArchiveDate();
   }


    /**
      * Returns the Mission statement for the Portal
      */
    public String getMission() {
        return (String)get("mission");
    }

    /** 
      * Set's the mission statement for a Portal. 
      */
    public void setMission(String mission) {
        set("mission", mission);
    }

    /**
      * Associates a Theme object with this Portal.
      */
    public void setTheme(Theme theme) {
        set("theme",theme);
    }

    /**
      * Returns the Theme object for this Portal
      */
    public Theme getTheme() {
        DataObject dobj;

        dobj = (DataObject)get("theme");

        if( dobj == null)
            return null;
        else
        {
          Theme theme = new Theme(dobj);
          return theme;
        }
    }



    /**
     * <p>Get a list of all the distinct "initials" of participants in
     * a Portal Site.</p>
     *
     * <p>A participant's initial is defined as the first letter of
     * their family name when the participant is a user (i.e. a
     * person), and the first letter of the group name if the
     * participant is a group.</p>
     *
     * <p>The returned Iterator contains the initials in increasing
     * alphabetical order.</p>
     **/
    public Iterator getParticipantInitials() {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.workspace.WorkspaceParticipantInitials");
        query.setParameter("workspaceID", getID());

        LinkedList result = new LinkedList();
        while (query.next()) {
            result.add(query.get("initial"));
        }

        return result.listIterator();
    }

    public static PortalSite createSubPortal(PortalSite ps, User user) {
        String urlfrag = "U-" + user.getID().toString();
        PortalSite psite = createPortalSite(urlfrag,ps.getDisplayName(),ps);
        psite.setIsSubPortal(true);

        //Grab collection of tabs for ps
        PortalTabCollection ptc = ps.getTabsForPortalSite();

        //for each, create duplicate tab for new psite
        PortalTab ptab;
        PortalTab newtab;
        String layout;
        Portlet portlet;
        AgentPortlet aportlet;
        SubPortalTab subtab;
        PortletCollection portletcollection;
        while(ptc.next()) {
          ptab = ptc.getPortalTab();
          layout = ptab.getLayout();
          if(layout != null) {
           //If tab layout has a lowercase W or N, make that tab a SubPortalTab.
            if((layout.indexOf('w') != (-1)) || (layout.indexOf('n') != (-1))) {
              //create subtab
              portletcollection = ptab.getPortlets();
              for(int i = 0; i < layout.length(); i++) {
                if(layout.charAt(i) == 'n' || layout.charAt(i) == 'w')
                    portletcollection.
                       addNotEqualsFilter("cellNumber",new Integer(i + 1));
              }
              subtab = SubPortalTab.createSubTab(ptab.getTitle(), psite);
              subtab.setSuperPortalTab(ptab);
              subtab.setLayout(ptab.getLayout());
              while(portletcollection.next()) {
                portlet = portletcollection.getPortlet();
                aportlet = AgentPortlet.createAgentPortlet(portlet,subtab,subtab);
                subtab.addPortlet(aportlet,portlet.getCellNumber());
              }
              subtab.setPortalSite(psite);
              subtab.save();
            } else {
              //create normal tab
              portletcollection = ptab.getPortlets();
              newtab = PortalTab.createTab(ptab.getTitle(), psite);
              newtab.setLayout(ptab.getLayout());
              while(portletcollection.next()) {
                portlet = portletcollection.getPortlet();
                aportlet = AgentPortlet.createAgentPortlet(portlet,newtab,newtab);
                newtab.addPortlet(aportlet,portlet.getCellNumber());
              }
            newtab.setPortalSite(psite);
            newtab.save();  
            }
          }
        }
        psite.save();

        return psite;
    }

}
