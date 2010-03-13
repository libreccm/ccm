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
package com.arsdigita.cms;

import com.arsdigita.cms.dispatcher.SimpleCache;
import com.arsdigita.cms.publishToFile.LocalRequestPassword;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.toolbox.Security;
import com.arsdigita.ui.login.LoginHelper;
import com.arsdigita.util.Assert;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.Workflow;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * <p>Security class used for checking and granting privileges in
 * CMS.</p>
 *
 * @author Michael Pih
 * @version $Revision: #24 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class SecurityManager implements Security, SecurityConstants {
    public static final String versionId =
        "$Id: SecurityManager.java 1344 2006-10-10 09:41:56Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (SecurityManager.class);

    public static final PrivilegeDescriptor CMS_PREVIEW_ITEM_DESCRIPTOR =
        new PrivilegeDescriptor(CMS_PREVIEW_ITEM);

    private ContentSection m_section;

    // MP: Use this.
    private SimpleCache m_cache;

    public SecurityManager(ContentSection section) {
        m_section = section;
        m_cache = new SimpleCache();
    }

    public final boolean canAccess(final String action) {
        return canAccess(Kernel.getContext().getParty(), action);
    }

    /**
     * Determine whether a party has access to a particular action.
     *
     * @param party The party
     * @param action The action
     * @return true if the party has access, false otherwise
     * @pre (action != null)
     */
    public boolean canAccess(final Party party, final String action) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Access check: party " + party + ", action " + action);
        }

        if (action.equals(WORKFLOW_ADMIN)) {
            return canAdministerWorkflow(party);
        } else if (action.equals(LIFECYCLE_ADMIN)) {
            return canAdministerLifecycles(party);
        } else if (action.equals(STAFF_ADMIN)) {
            return canAdministerRoles(party);
        } else if (action.equals(CONTENT_TYPE_ADMIN)) {
            return canAdministerContentTypes(party);
        } else if (action.equals(CATEGORY_ADMIN)) {
            return canAdministerCategories(party);
        } else if (action.equals(PUBLISH)) {
            return canPublishItems(party);
        } else if (action.equals(NEW_ITEM)) {
            return canCreateItems(party);
        } else if (action.equals(ADMIN_PAGES)) {
            return canViewAdminPages(party);
        } else if (action.equals(PUBLIC_PAGES)) {
            return canViewPublicPages(party);
        } else if (action.equals(PREVIEW_PAGES)) {
            return canViewPreviewPages(party);
        } else if (action.equals(DELETE_IMAGES)) {
            return canDeleteImages(party);
        } else if (action.equals(APPLY_ALTERNATE_WORKFLOWS)) {
            return canApplyAlternateWorkflows(party);
        } else {
            throw new IllegalArgumentException
                ("Unknown action for access check: " + action);
        }
    }

    /**
     * Determine whether the current user has access to a particular action.
     *
     * @param request The HTTP request
     * @param action The action
     * @return true if the logged-in user has access, false otherwise
     */
    public boolean canAccess(final HttpServletRequest request,
                             final String action) {
        final Party party = Kernel.getContext().getParty();
        boolean canAccess = canAccess(party, action);

        if (!canAccess) {
            canAccess = LocalRequestPassword.validLocalRequest(request);
        }

        return canAccess;
    }

    public boolean canAccess(final User user,
                             final String action,
                             final ContentItem item) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Access check: user " + user + ", object " +
                        item + ", action " + action);
        }

        if (action.equals(EDIT_ITEM)) {
            return canEditItem(user, item);
        } else if (action.equals(APPLY_WORKFLOW)) {
            return canApplyWorkflow(user, item);
        } else if (action.equals(DELETE_ITEM)) {
            return canDeleteItem(user, item);
        } else if (action.equals(SCHEDULE_PUBLICATION)) {
            return canSchedulePublication(user, item);
        } else if (action.equals(PUBLISH)) {
            return canPublishItems(user, item);
        } else if (action.equals(PUBLIC_PAGES)) {
            return canViewPublicPages(user, item);
        } else if (action.equals(PREVIEW_PAGES)) {
            return canViewPreviewPages(user, item);
        } else if (action.equals(NEW_ITEM)) {
            // this should really only be called if the ContentItem is
            // a folder...
            return canCreateItems(user, item);
        } else if (action.equals(APPLY_ALTERNATE_WORKFLOWS)) {
            return canApplyAlternateWorkflows(user, item);
        } else if (action.equals(STAFF_ADMIN)) {
            // this should really only be called if the ContentItem is
            // a folder...
            return canAdministerRoles(user, item);
            // section levels -- call non-item-specific version
        } else if (action.equals(WORKFLOW_ADMIN)) {
            return canAdministerWorkflow(user);
        } else if (action.equals(LIFECYCLE_ADMIN)) {
            return canAdministerLifecycles(user);
        } else if (action.equals(STAFF_ADMIN)) {
            return canAdministerRoles(user);
        } else if (action.equals(CONTENT_TYPE_ADMIN)) {
            return canAdministerContentTypes(user);
        } else if (action.equals(ADMIN_PAGES)) {
            return canViewAdminPages(user);
        } else {
            throw new IllegalArgumentException
                ("Unknown action for access check: " + action);
        }
    }

    public boolean canAccess(HttpServletRequest request, String action,
                             ContentItem item) {
        User user = KernelHelper.getCurrentUser(request);
        boolean canAccess = canAccess(user, action, item);
        if (!canAccess) {
            canAccess = LocalRequestPassword.validLocalRequest(request);
        }
        return canAccess;
    }

    /**
     * Checking privileges.
     **/

    protected boolean canAdministerLifecycles(Party party) {
        return (hasPermission(party, CMS_LIFECYCLE_ADMIN));
    }

    protected boolean canAdministerWorkflow(Party party) {
        return (hasPermission(party, CMS_WORKFLOW_ADMIN));
    }

    protected boolean canAdministerRoles(Party party) {
        return (hasPermission(party, CMS_STAFF_ADMIN));
    }
    protected boolean canAdministerRoles(Party party, ContentItem item) {
        return (hasPermission(party, CMS_STAFF_ADMIN, item));
    }

    protected boolean canAdministerContentTypes(Party party) {
        return (hasPermission(party, CMS_CONTENT_TYPE_ADMIN));
    }

    protected boolean canAdministerCategories(Party party) {
        return (hasPermission(party, CMS_CATEGORY_ADMIN));
    }

    protected boolean canPublishItems(Party party) {
        return (hasPermission(party, CMS_PUBLISH));
    }
    protected boolean canPublishItems(Party party, ContentItem item) {
	return (hasPermission(party, CMS_PUBLISH, item));
    }

    protected boolean canCreateItems(Party party) {
        return (hasPermission(party, CMS_NEW_ITEM));
    }
    protected boolean canCreateItems(User user, ContentItem item) {
        return (hasPermission(user, CMS_NEW_ITEM, item));
    }

    protected boolean canApplyAlternateWorkflows(Party party) {
        return (hasPermission(party, CMS_APPLY_ALTERNATE_WORKFLOWS));
    }
    protected boolean canApplyAlternateWorkflows(User user, ContentItem item) {
        return (hasPermission(user, CMS_APPLY_ALTERNATE_WORKFLOWS, item));
    }

    /**
     * Returns true if the specified user has the CMS_READ_ITEM permission on the
     * current content section. False otherwise.
     *
     * @pre m_section != null
     **/
    protected boolean canViewPublicPages(Party party) {
        return (hasPermission(party, CMS_READ_ITEM));
        //    return true;
    }

    /**
     * Returns true if the specified user has the CMS_READ_ITEM permission on the
     * current content item. False otherwise.
     *
     * For now, just call the section-specific version. Must modify when we
     * implement folder-level permissions.
     *
     * @pre m_section != null
     **/
    protected boolean canViewPublicPages(User user, ContentItem item) {
        return (hasPermission(user, CMS_READ_ITEM, item));
    }

    /**
     * Returns true if the specified user has the CMS_PREVIEW_ITEM permission on the
     * current content section. False otherwise.
     *
     * @pre m_section != null
     **/
    protected boolean canViewPreviewPages(Party party) {
        return (hasPermission(party, CMS_PREVIEW_ITEM) ||
                 hasPermission(party, CMS_EDIT_ITEM));
        //    return true;
    }

    /**
     * Returns true if the specified user has the CMS_PREVIEW_ITEM permission on the
     * current content item. False otherwise.
     *
     * For now, just call the section-specific version. Must modify when we
     * implement folder-level permissions.
     *
     * @pre m_section != null
     **/
    protected boolean canViewPreviewPages(User user, ContentItem item) {
        return (hasPermission(user, CMS_PREVIEW_ITEM, item) ||
                 hasPermission(user, CMS_EDIT_ITEM, item));
    }

    /**
     * Returns true if the specified party can access authoring UI in the
     * current content section. False otherwise.
     *
     * @pre m_section != null
     **/
    protected boolean canViewAdminPages(Party party) {
        return (hasPermission(party, CMS_PREVIEW_ITEM));
    }

    /**
     * Returns true if the specified user has the CMS_ITEM_ADMIN permission on the
     * current content item. False otherwise.
     *
     *
     * @pre m_section != null
     **/
    protected boolean canDeleteImages(Party party) {
        return (hasPermission(party, CMS_ITEM_ADMIN));
    }


    /**
     * <p>Check if:</p>
     * <ul>
     *   <li>User is logged in</li>
     *   <li>User has the "edit items" privilege OR
     *   <li>User is assigned to and owns the lock on an active task in the
     *     workflow applied to the item.</li>
     * </ul>
     *
     * @param user The user
     * @param item The content item
     * @return true if the user is allowed to edit an item, false otherwise
     * @pre (item != null)
     */
    protected boolean canEditItem(User user, ContentItem item) {
        if (user == null) {
            return false;
        }

        // If the user does not have the edit items permission, then there is no
        // need to check for workflow task assignments.
        if (!hasPermission(user, CMS_EDIT_ITEM, item)) {
            return false;
        }

        Workflow wf = Workflow.getObjectWorkflow(item);
        if (wf == null) {
            return hasPermission(user, CMS_ITEM_ADMIN, item);
        } else {
            if (wf.isFinished() || wf.getProcessState() == Workflow.STOPPED) {
                return hasPermission(user, CMS_ITEM_ADMIN, item);
            }

            Engine engine = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE);
            Iterator i = engine.getEnabledTasks(user, wf.getID()).iterator();

             while (i.hasNext()) {
                 CMSTask t = (CMSTask) i.next();

                 if (t.isLocked() && user.equals(t.getLockedUser())) {
                     // The user owns the lock on the task.
                     return true;
                }
             }
             // The user does not own the lock on any tasks.
             return false;
        }
    }
    /**
     * <p>Check if:</p>
     * <ul>
     *   <li>User is logged in</li>
     *   <li>If the user has publish privileges, verify the current
     *    workflow is null, stopped or finished
     *   <li>If the user is assigned a task of type schedule/deploy
     * </ul>
     *
     * @param user The user
     * @param item The content item
     * @return true if the user is allowed to edit an item, false otherwise
     * @pre (item != null) */

    protected boolean canSchedulePublication(User user, ContentItem item) {

        // If the user does not have the publish items permission, then there is no
        // need to check for workflow task assignments.
        if (user != null && canPublishItems(user, item)) {
            Workflow wf = Workflow.getObjectWorkflow(item);
            if (wf == null || wf.getProcessState() == Workflow.STOPPED ||
                 wf.isFinished()) {
                return true;
            } else {
                Engine engine = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE);
                Iterator i = engine.getEnabledTasks(user, wf.getID()).iterator();
                int j =0;
                while (i.hasNext()) {
                    CMSTask t = (CMSTask) i.next();
                    if (t.getTaskType().getID().equals(CMSTaskType.DEPLOY)) {
                        User lockingUser = t.getLockedUser();
                        if (lockingUser == null  ||  user.equals(lockingUser)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if the user has CMS_DELETE_ITEM privelege.
     *
     * @param user The user
     * @param item The content item
     * @return true if the user is allowed to edit an item, false otherwise
     * @pre (item != null)
     */
    protected boolean canDeleteItem(User user, ContentItem item) {
        return (hasPermission(user, CMS_DELETE_ITEM, item));
    }

    /**
     * <p>Check if:</p>
     * <ul>
     *   <li>User is logged in</li>
     *   <li>User has "edit items" permission on the item (or section) OR</li>
     *   <li>User has cms_new_item or cms_workflow_admin on the item (or
     *     section)</li>
     * </ul>
     *
     * @param user The user
     * @param item The content item
     * @return true if the user is allowed to apply a workflow to the item,
     *   false otherwise
     * @pre (item != null)
     */
    protected boolean canApplyWorkflow(User user, ContentItem item) {

        if (user == null) {
            return false;
        }

        if (!hasPermission(user, CMS_EDIT_ITEM)) {
            return true;
        }

        if (Workflow.getObjectWorkflowID(item) != null) {
            return false;
        }

        return (hasPermission(user, CMS_NEW_ITEM, item) ||
                 hasPermission(user, CMS_WORKFLOW_ADMIN));
    }



    /**
     * Helper method for checking permissions within a content section.
     */
    private boolean hasPermission(Party party, String privilege) {
        PrivilegeDescriptor pd = PrivilegeDescriptor.get(privilege);
        Assert.exists(pd, "PrivilegeDescriptor.get(\"" + privilege + "\")");
        return hasPermission(party, pd);
    }

    /**
     * Helper method for checking permissions within a content section.
     */
    private boolean hasPermission(Party party, PrivilegeDescriptor pd) {
        PermissionDescriptor perm = new PermissionDescriptor(pd, m_section, party);
        return PermissionService.checkPermission(perm);
    }

    /**
     * Helper method for checking permissions on a content item.
     */
    private boolean hasPermission(Party party, String privilege,
                                  ContentItem item) {
        PrivilegeDescriptor pd = PrivilegeDescriptor.get(privilege);
        Assert.exists(pd, "PrivilegeDescriptor.get(\"" + privilege + "\")");
        return hasPermission(party, pd, item);
    }

    /**
     * Helper method for checking permissions on a content item.
     * @pre pd != null
     */
    private boolean hasPermission(Party party, PrivilegeDescriptor pd,
                                  ContentItem item) {
        Assert.exists(pd, "PrivilegeDescriptor");
        PermissionDescriptor perm = new PermissionDescriptor(pd, item, party);
        return (PermissionService.checkPermission(perm));
    }

    /**
     * Redirects the user to the login page if not already signed in,
     * setting the return url to the current request URI.
     *
     * @exception ServletException If there is an exception thrown while
     * trying to redirect, wrap that exception in a ServletException
     **/
    public static void requireSignIn(HttpServletRequest request,
                                     HttpServletResponse response)
        throws IOException, ServletException {

        if (KernelHelper.getCurrentUser(request) != null) { return; }
        String url = com.arsdigita.kernel.security.Initializer
            .getSecurityHelper().getLoginURL(request)
            + "?" + UserContext.RETURN_URL_PARAM_NAME
            + "=" + UserContext.encodeReturnURL(request);

        LoginHelper.sendRedirect(request, response, url);
    }
}
