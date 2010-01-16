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

package com.arsdigita.london.portal;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionManager;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.london.portal.ui.PersonalPortalPage;
import com.arsdigita.london.portal.ui.WorkspaceTheme;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject; 
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.Web;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Workspace domain class, a workspace represents an area containing 0...n portals
 * each arranged as a pane of page. Each portal (or pane) manages a number of portlets.
 * 
 * 
 */
public class Workspace extends Application {

	private static final Logger s_log = Logger.getLogger(Workspace.class);

	private static final WorkspaceConfig s_config = new WorkspaceConfig();

	static {
		s_config.load();
	}

	public static WorkspaceConfig getConfig() {
		return s_config;
	}

	public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.london.portal.Workspace";

	public static final String PARTY = "party";

	public static final String PARTY_ID = PARTY + "." + ACSObject.ID;

	public static final String DEFAULT_LAYOUT = "defaultLayout";

	public static final String PAGES = "pages";

	/**
	 * store this as a static variable as it cannot change during the lifetime
	 * of the ccm service
	 */
	private static Workspace defaultHomepageWorkspace = null;

	/**
     * Constructor
     * @param obj
     */
    public Workspace(DataObject obj) {
		super(obj);
	}

	/**
     * Constructor, retrieves the workspace from database using its OID
     * @param oid
     * @throws com.arsdigita.domain.DataObjectNotFoundException
     */
    public Workspace(OID oid) throws DataObjectNotFoundException {

		super(oid);
	}

	/*
	 * public String getContextPath() { return "ccm-ldn-portal"; }
	 */

	/**
     * 
     * @return ServletPath (constant) probably should be synchron with web.xml
     *                     entry
     */
    public String getServletPath() {
		// return "/files";
		return "/ccm-ldn-portal/files";
	}

	/**
     * Wrapper class to handle a limited set of parameters, 
     * here: page layout is set to default layout.
     *  
     * @param url
     * @param title
     * @param parent
     * @param isPublic
     * @return
     */
    public static Workspace createWorkspace(String url, String title,
			Application parent, boolean isPublic) {
		return createWorkspace(url, title, PageLayout.getDefaultLayout(),
				parent, isPublic);
	}

	/**
     * 
     * @param url
     * @param title
     * @param parent  parent application
     * @param owner
     * @return
     */
    public static Workspace createWorkspace(String url, String title,
			Application parent, User owner) {
		return createWorkspace(url, title, PageLayout.getDefaultLayout(),
				parent, owner);
	}

	/**
     * Does the real work to create a workspace in the storage (db)
     * 
     * @param url
     * @param title
     * @param layout
     * @param parent
     * @param isPublic
     * @return
     */
    public static Workspace createWorkspace(String url, String title,
                                            PageLayout layout, Application parent,
                                            boolean isPublic) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Creating group workspace, isPublic:" + isPublic
                       + " on " + url + " with parent "
                       + (parent == null ? "none" : parent.getOID().toString()));
        }

        Workspace workspace = (Workspace) Application.createApplication(
                              BASE_DATA_OBJECT_TYPE, url, title, parent);
        workspace.setupGroups(title, isPublic);
        workspace.setDefaultLayout(layout);
        return workspace;
    }

	/**
     * Does the real work to create a workspace in the storage (db)
     * 
     * @param url
     * @param title
     * @param layout
     * @param parent
     * @param owner
     * @return
     */
    public static Workspace createWorkspace(String url, String title,
			PageLayout layout, Application parent, User owner) {
		if (s_log.isDebugEnabled()) {
			s_log.debug("Creating personal workspace for " + owner.getOID()
					+ " on " + url + " with parent "
					+ (parent == null ? "none" : parent.getOID().toString()));
		}

		Workspace workspace = (Workspace) Application.createApplication(
				BASE_DATA_OBJECT_TYPE, url, title, parent);
		workspace.setParty(owner);
		workspace.setDefaultLayout(layout);
		return workspace;
    }

    /**
     *
     * retrieve the workspace that is created during loading of the
     * ccm-ldn-portal application and is set as the defaultworkspace for the
     * site.
     *
     * Returns null if there are no workspaces (though presumably if that is the
     * case, ccm-ldn-portal hasn't been loaded and so I don't know how you are
     * invoking this method!)
     *
     * @return
     */
    public static Workspace getDefaultHomepageWorkspace() {

        if (null == defaultHomepageWorkspace) {
            // default homepage workspace is created during
            // com.arsdigita.london.portal.Loader
            // it's attributes are all load variables that are lost as soon as
            // the application
            // is loaded. So, finding the default homepage workspace is a bit
            // tricky - can't filter on name
            // or anything else that is site specific - best bet is to assume
            // that it is the first workspace created (a reasonable assumption
            // at the moment because
            // it is created straight after the Workspace application type is
            // created.
            WorkspaceCollection workspaces = Workspace.retrieveAll();
            workspaces.addOrder(Application.TIMESTAMP);
            if (workspaces.next()) {
                defaultHomepageWorkspace = workspaces.getWorkspace();
            }
            workspaces.close();
        }
        return defaultHomepageWorkspace;

    }

	public void beforeSave() {
		// If no permissions are configured, then setup empty groups
		if (get(PARTY) == null) {
			if (s_log.isDebugEnabled()) {
				s_log.debug("No party is set, creating shared workspace "
						+ getOID());
			}
			setupGroups(getTitle(), false);
		}
		// Setup the default layout.
		if (isNew() && getDefaultLayout() == null) {
			setDefaultLayout(PageLayout.getDefaultLayout());
		}
		super.beforeSave();
	}

	// public void beforeDelete() {
	// super.beforeDelete();
	// Category.clearRootForObject(this);
	// }

	// This method wouldn't need to exist if permissioning used
	// the associations rather than direct queries which require
	// the object to be saved
	public void afterSave() {
		super.afterSave();

		Party party = getParty();
		s_log.debug("Party is " + party.getDisplayName() + " for "
				+ this.getTitle());

		if (party instanceof User) {
			s_log.debug("Party is a user");

			// Personal workspace, so give user full admin
			PermissionDescriptor admin = new PermissionDescriptor(
					PrivilegeDescriptor.ADMIN, this, party);
			PermissionService.grantPermission(admin);

		} else if (party instanceof Group) {
			s_log.debug("Party is a group");

			// Ensure main group has the required permission
			PermissionDescriptor pd = new PermissionDescriptor(
					getConfig().getWorkspacePartyPrivilege(), this, party);
			PermissionService.grantPermission(pd);

			// Now get (or create) the administrators role & grant it admin
			Group members = (Group) party;
			Role admins = members.getRole("Administrators");
			if (admins == null) {
				admins = members.createRole("Administrators");
				admins.save();
			}
			admins.grantPermission(this, PrivilegeDescriptor.ADMIN);
		}

		// Set permission context for %all pages
		WorkspacePageCollection pages = getPages();
		while (pages.next()) {
			WorkspacePage page = pages.getPage();
			PermissionService.setContext(page, this);
		}
	}

	private void setupGroups(String title, boolean isPublic) {
		Group members = new Group();
		members.setName(title);
		members.save();
		// set this group as subgroup of workspace application type
		// although workspace applications can be nested, place all 
		// member groups directly under main container (rather than reflecting
		// hierarchical structure) because (a) workspace already manages its
		// own groups so doesn't need a hierarchy and (b) hierarchy would
		// mean for a given workspace, role would be on the same level 
		// as member groups of sub workspaces - messy & confusing
		getApplicationType().getGroup().addSubgroup(members);

		Role admins = members.createRole("Administrators");
		admins.save();

		if (isPublic) {
			Party thePublic;
			try {
				thePublic = (Party) DomainObjectFactory.newInstance(new OID(
						User.BASE_DATA_OBJECT_TYPE,
						PermissionManager.VIRTUAL_PUBLIC_ID));
			} catch (DataObjectNotFoundException ex) {
				throw new UncheckedWrapperException("cannot find the public",
						ex);
			}

			members.addMemberOrSubgroup(thePublic);
			members.save();
		}

		setParty(members);
	}

	public static WorkspaceCollection retrieveAll() {
		return retrieveAll(null);
	}

	public static WorkspaceCollection retrieveAll(Application parent) {
		DataCollection wks = SessionManager.getSession().retrieve(
				BASE_DATA_OBJECT_TYPE);
		if (parent != null) {
			wks.addEqualsFilter("parentResource.id", parent.getID());
		}

		return new WorkspaceCollection(wks);
	}

	public Workspace retrieveSubworkspaceForParty(Party owner)
			throws DataObjectNotFoundException {

		DataCollection wks = SessionManager.getSession().retrieve(
				BASE_DATA_OBJECT_TYPE);

		wks.addEqualsFilter("parentResource.id", getID());
		wks.addEqualsFilter(PARTY_ID, owner.getID());

		if (wks.next()) {
			Workspace workspace = (Workspace) Application
					.retrieveApplication(wks.getDataObject());

			wks.close();

			return workspace;
		}

		throw new DataObjectNotFoundException("cannot find workspace for party");
	}

	public void delete() {
		clearPages();

		super.delete();
	}

	public void clearPages() {
		WorkspacePageCollection pages = getPages();
		while (pages.next()) {
			WorkspacePage page = pages.getPage();
			page.delete();
		}
	}

	public void setDefaultLayout(PageLayout layout) {
		setAssociation(DEFAULT_LAYOUT, layout);
	}

	public PageLayout getDefaultLayout() {
		return (PageLayout) DomainObjectFactory
				.newInstance((DataObject) get(DEFAULT_LAYOUT));
	}

	public void setParty(Party party) {
		setAssociation(PARTY, party);
	}

	public Party getParty() {
		return (Party) DomainObjectFactory.newInstance((DataObject) get(PARTY));
	}

    public RoleCollection getRoles() {
        Party party = getParty();

        if (!(party instanceof Group)) {
            return null;
        }

        return ((Group) party).getRoles();
    }

    public long getParticipantCount() {
        Party party = getParty();

        if (!(party instanceof Group)) {
            return 0;
        }

        return ((Group) party).countMembers();
    }

    public void addParticipant(Party p) {
        Party party = getParty();

        if (!(party instanceof Group)) {
            return;
        }

        Group group = (Group) party;
        group.addMemberOrSubgroup(p);
        group.save();
    }

    public void removeParticipant(Party p) {
        Party party = getParty();

        if (!(party instanceof Group)) {
            return;
        }

        Group group = (Group) party;
        group.removeMemberOrSubgroup(p);
        group.save();
    }

    /** Participants are the members of the Party group. */
    public PartyCollection getParticipants() {
        Party party = getParty();

        if (!(party instanceof Group)) {
            return null;
        }

        return ((Group) party).getMembers();
    }

    public PartyCollection getNonParticipants() {
        DataCollection dc =
            SessionManager.getSession()
            .retrieve("com.arsdigita.kernel.User");
        //            .retrieve("com.arsdigita.kernel.Party");
        Filter f = dc
            .addNotInSubqueryFilter
            ("id", "com.arsdigita.london.portal.WorkspaceParticipantIDs");
        f.set("workspaceID", getID());
        return new PartyCollection(dc);
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
            ("com.arsdigita.london.portal.WorkspaceParticipantInitials");
        query.setParameter("workspaceID", getID());

        LinkedList result = new LinkedList();
        while (query.next()) {
            result.add(query.get("initial"));
        }

        return result.listIterator();
    }

    /**
     * <p>Get a collection of all participants in the Workspace whose
     * initial (see {@link getParticipantInitials
     * getParticipantInitials} for a definition of a participant's
     * initial) is the specified value.</p>
     *
     * @param initial Single-character string, must be uppercase
     **/
    public PartyCollection getParticipantsWithInitial(String initial) {
        Assert.assertNotNull(initial);
        Assert.assertTrue(initial.length() == 1, "Initial needs length 1");
        Assert.assertTrue(initial.equals(initial.toUpperCase()),
                          "Initial must be uppercase");

        //DataAssociationCursor dac =
        //    ((DataAssociation) get("participants")).cursor();
        //Filter f = dac.addInSubqueryFilter
        //    ("id", "com.arsdigita.london.portal.WorkspaceParticipantsWithInitial");

        DataCollection dc =
            SessionManager.getSession()
            .retrieve("com.arsdigita.kernel.User");
        Filter f = dc.addInSubqueryFilter
            ("id", "com.arsdigita.london.portal.WorkspaceParticipantsWithInitial");
        f.set("workspaceID", getID());
        f.set("nameInitial", initial);

        return new PartyCollection(dc);
    }

	public WorkspacePageCollection getPages() {
		DataAssociation pages = (DataAssociation) get(PAGES);
		return new WorkspacePageCollection(pages.cursor());
	}

	public void movePageLeft(WorkspacePage workspacePage) {

        // loop through the pages and look for the previous one
		WorkspacePageCollection pages = getPages();
		pages.addOrder(WorkspacePage.SORT_KEY);
		int i = 0;
        WorkspacePage previous = null;
        WorkspacePage current = null;

		while (pages.next()) {
			current = pages.getPage();
            if (current.equals(workspacePage)) {
                if (previous == null) {
                    // current is first
                    break;
                }
                int currentKey = current.getSortKey();
                int previousKey = previous.getSortKey();
                current.setSortKey(previousKey);
                previous.setSortKey(currentKey);
                break;
            }
            previous = current;
        }
	}

	public void movePageRight(WorkspacePage workspacePage) {

        // loop through the pages and look for the next one
		WorkspacePageCollection pages = getPages();
		pages.addOrder(WorkspacePage.SORT_KEY);
		int i = 0;
        WorkspacePage previous = null;
        WorkspacePage current = null;

		while (pages.next()) {
			current = pages.getPage();
            if (previous != null && previous.equals(workspacePage)) {
                int currentKey = current.getSortKey();
                int previousKey = previous.getSortKey();
                current.setSortKey(previousKey);
                previous.setSortKey(currentKey);
                break;
            }
            previous = current;
        }
	}

	public void removePage(WorkspacePage workspacePage) {
		WorkspacePageCollection pages = getPages();
		pages.addOrder(WorkspacePage.SORT_KEY);
		int i = 0;
		while (pages.next()) {
			WorkspacePage page = pages.getPage();
			if (page.equals(workspacePage)) {
				page.delete();
			} else {
				page.setSortKey(i++);
			}
		}
	}

	public WorkspacePage addPage(String title, String description) {
		DataAssociationCursor pages = ((DataAssociation) get(PAGES)).cursor();
		pages.addOrder(WorkspacePage.SORT_KEY + " desc");
		int max = -1;
		if (pages.next()) {
			DataObject dobj = pages.getDataObject();
			Integer tab = (Integer) dobj.get(WorkspacePage.SORT_KEY);
			max = tab.intValue();
			pages.close();
		}

		return addPage(title, description, getDefaultLayout(), max + 1);
	}

	public WorkspacePage addPage(String title, String description,
			PageLayout layout, int sortKey) {
		return WorkspacePage.create(title, description, layout, this, sortKey);
	}

	public void setTheme(WorkspaceTheme theme) {
		set("theme", theme);
	}

	public WorkspaceTheme getTheme() {
		DataObject dobj;

		dobj = (DataObject) get("theme");

		if (dobj == null)
			return null;
		else {
			WorkspaceTheme theme = new WorkspaceTheme(dobj);
			return theme;
		}
	}

	public static Workspace getCurrentlySelectedWorkspace() {
		Application current = Web.getContext().getApplication();
		if (current instanceof Workspace) {
			return (Workspace) current;
		}
		return null;
	}
	
    public WorkspaceCollection getChildWorkspaces() {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.london.portal.childWorkspacesForApplicationID");
        query.setParameter("applicationID", getID());

        DataCollection collection =
            new DataQueryDataCollectionAdapter(query, "workspace");
        //collection.addEqualsFilter("isArchived", Boolean.FALSE);

        query.close();

        WorkspaceCollection wc = new WorkspaceCollection(collection);
        return wc;
    }

    public WorkspaceCollection getRelatedWorkspaces() {
        DataAssociation association = (DataAssociation) get("relatedWorkspace");

        DataAssociationCursor cursor = association.cursor();
        //cursor.addEqualsFilter("isArchived", Boolean.FALSE);

        WorkspaceCollection ws = new WorkspaceCollection(cursor);
        //ws.filterForUnarchived();
        return ws;
    }

    public ApplicationCollection getFullPageWorkspaceApplications() {
        ApplicationCollection applications = getChildApplications();
        applications.filterToWorkspaceApplications();
        applications.filterToHasFullPageView();
        return applications;
    }

	/**
	 * Get's the description for a Workspace.
	 */
//	public String getDescription() {
//		return (String) get("description");
//	}

	/**
	 * Set's the description for a Workspace.
	 */
//	public void setDescription(String description) {
//		set("description", description);
//	}

	/**
	 * Sets the user who owns the workspace
	 * 
	 * @param user
	 */
	public void setOwner(User user) {
		s_log.debug("setOwner called for " + this.getDisplayName());
		set("owner", user);
	}

	public User getOwner() {
		return (User) get("owner");
	}

	public static Workspace createPersonalWorkspace(final User owner) {

		s_log.debug("creating the personal portal for "
				+ owner.getDisplayName());

		String url = "" + owner.getID();
		String title = "Personal Workspace for " + owner.getDisplayName();
		Application parent = Application
				.retrieveApplicationForPath(PersonalPortalPage.PERSONAL_PORTAL_PATH);
		final Workspace workspace = createWorkspace(url, title, parent, false);

		// TODO the setOwner method should probably deal with all the
		// permissions then later on ownership could be changed
		Group group = (Group) workspace.getParty();
		group.addMember(owner);
		workspace.setOwner(owner);
		new KernelExcursion() {
			public void excurse() {
				setEffectiveParty(Kernel.getSystemParty());
				PermissionDescriptor pd = new PermissionDescriptor(
						PrivilegeDescriptor.ADMIN, workspace, owner);
				PermissionService.grantPermission(pd);
			}
		}.run();
		// check that the user is no longer system user
		Party party = KernelHelper.getCurrentEffectiveParty();
		s_log.debug("party after excurse is " + party.getDisplayName());

		Role admins = group.getRole("Administrators");
		if (admins == null) {
			admins = group.createRole("Administrators");
			admins.add(owner);
			admins.save();
		}

		workspace.setDefaultLayout(PageLayout.getDefaultLayout());

        // set the parent app to null
        // so that the base personal portal doesn't appear anywhere
        workspace.setParentApplication(null);

		return workspace;
	}

	public static Workspace retrievePersonalWorkspace(User owner) {
		DataCollection personalWorkspaces = SessionManager.getSession()
				.retrieve(BASE_DATA_OBJECT_TYPE);
		personalWorkspaces.addEqualsFilter("owner", owner.getID());

		if (personalWorkspaces.next()) {
			Workspace workspace = (Workspace) Application
					.retrieveApplication(personalWorkspaces.getDataObject());
			if (personalWorkspaces.next())
				s_log.error("more than one personal workspaces for this user!!");
			personalWorkspaces.close();

			return workspace;
		}
		return null;
	}

}
