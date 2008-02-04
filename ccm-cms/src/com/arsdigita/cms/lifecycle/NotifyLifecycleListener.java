/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.lifecycle;

import java.text.DateFormat;

import org.apache.log4j.Logger;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.messaging.Message;
import com.arsdigita.notification.Notification;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.URL;
import com.arsdigita.workflow.simple.UserTask;

/**
 * LifecycleListener implementation which sends out a notification
 * that the item is about to expire.
 * Does nothing at the end of the phase.
 *@author <a href="mailto:mhanisch@redhat.com">Michael Hanisch</a>
 *@version $Id: NotifyLifecycleListener.java 1583 2007-05-25 15:32:13Z chrisgilbert23 $
 */
public class NotifyLifecycleListener implements LifecycleListener {

    private static final Logger s_log = 
        Logger.getLogger(NotifyLifecycleListener.class);

    // TO DO: Move this constant somewhere else!
    private static final String ALERT_RECIPIENT_ROLE = "Alert Recipient";

    /**
     * Handle the begin event. This listener reacts by sending
     * out a notification that the item is about to expire.
     */
    public void begin(LifecycleEvent event) {

        s_log.debug("Beginning listener action");

	if (event.getEventType() != LifecycleEvent.PHASE) {
            s_log.error("NotifyLifecycleListener called for "
                        + " LifecycleEvent.LIFECYCLE event, but this class"
                        + " only handles LifecycleEvent.PHASE events!");
	    // only handle begin event for a single phase!
            return;
        }

        final ContentItem item = getContentItem( event.getOID() );
        final ContentSection section = item.getContentSection();
        if (s_log.isDebugEnabled()) {
            s_log.debug("item = " + (item == null ? "null" : item.toString()));
            s_log.debug("section = "+ (section == null ? "null" : section.toString()));
        }
        
        Assert.exists(item);
        Assert.exists(section);
        
        UserCollection spamVictims = getRecipients(item);
        User user = null;
        Message message = createMessage(item,event);

        if (message != null) {

            // spam the staff members of this content section
            if (spamVictims != null) {
                while (spamVictims.next()) {
                    user = (User) spamVictims.getDomainObject();
                    sendOneEmail(user, message);
                }
            } else {
                s_log.debug("No non-author users to notify");
            }

            // notify the author too: optional, defaulted to true
	    if (ContentSection.getConfig().getNotifyAuthorOnLifecycle()) {
		User author = item.getCreationUser();
		if (author != null) {
		    sendOneEmail(author, message);
		} else {
		    s_log.debug("No author user to notify");
		}
	    }

        } else {
            s_log.debug("No message to send");
        }
    }

    /** Send one email. */
    public void sendOneEmail(User user, Message message) {

        Notification notif = new Notification(user, message);
        if (ContentSection.getConfig().deleteExpiryNotifications()) {
        	notif.setIsPermanent(Boolean.FALSE);
        	// true is set as default column value in DB for all
        	// notifications, but set explicitly here in case that 
        	// changes
        	notif.setMessageDelete(Boolean.TRUE);
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("notifying user " + user.getPrimaryEmail());
        }
    }

    /**
     * 100% bug-free, empty method.
     *@param event <em>ignored</em>
     */
    public void end(LifecycleEvent event) {
        // no bugs here
    }

    private ContentItem getContentItem(OID oid) {
        s_log.debug("getting content item"); // !!
        // borrowed from PublishLifecycleListener
        ContentItem item = null;
        try {
            item = (ContentItem)DomainObjectFactory.newInstance(oid);
        } catch (DataObjectNotFoundException e) {
            e.printStackTrace();
            throw new UncheckedWrapperException(e);
        }
        return item;
    }

    /**
     * Determine the set of recipients for the alerts sent by this
     * listener, based on the content item.
     * This methods has to apply any filters etc. to restrict the
     * result to the correct set.
     *
     *@param item the ContentItem which the alerts are about
     *@return a UserCollection which should receive alerts about the given
     * <tt>item</tt>; can be <tt>null</tt>
     */
    protected UserCollection getRecipients(ContentItem item) {

        /* NOTE:
         * There is a problem with the permission denormalization/hierarchy
         * in CMS.
         * When creating a live/pending version "IL" of an item "I",
         * the folder (F) contaiening the item is also published, 
         * i.e. a live version (FL) of the folder is created..
         *
         * I.getFolder() ==> F
         * IL.getFolder() ==> FL
         *    *BUT*
         * securityContext of I ==> F
         * securityContext of IL ==> F
         * securityContext of FL ==> (ContentSection)
         * Thus, checking permissions on IL.getFolder() will fail since
         * no permissions have been granted on the live version of the folder.
         * So even though items always inherit permissions from their folder
         * permissions(FL) != permissions(F).
         *
         * Even worse, the permission denormalization seems FUBARed -
         * permissions(I) != permissions(F), even though F is set as
         * I's security context.
         * (Could also be that the object filter query doesn't handle the
         * denormalization correctly.)
         * (This is the reason why the permission filter here acts on the
         * folder rather than the item, otherwise I wouldn't have had
         * this problem.)
         *
         * WORKAROUND:
         * check permissions on IL.getWorkingVersion().getFolder(), i.e. 
         * check permissions on F directly
         */
        ContentSection section = item.getContentSection();
        Group alertsGroup = section.getStaffGroup();
        Role alertRole = alertsGroup.getRole(ALERT_RECIPIENT_ROLE);
        
        UserCollection usersToAlert = null;
        if (alertRole != null) {
            usersToAlert = alertRole.getContainedUsers();
            Filter pFilter = PermissionService
                .getObjectFilterQuery(usersToAlert.getFilterFactory(),
                                      "id",
                                      PrivilegeDescriptor.
                                      get(SecurityManager.CMS_EDIT_ITEM),
                                      item.getDraftVersion().getParent().getOID()); // !! see above!!
            usersToAlert.addFilter(pFilter);
        }
        return usersToAlert;
    }
    
    /**
     * Create the message object.
     *@param item the item which the created Message relates to; 
     * must not be null
     *@param lc the lifecycle event 
     *@return a Message concerning <tt>item</tt
     */
    protected Message createMessage(ContentItem item,
                                   LifecycleEvent lc) {
        Assert.exists(item);
        Message message = null;
        Party sender = UserTask.getAlertsSender();
        if (sender == null) {
            s_log.error("Failed to get 'from' party from UserTask ");

        } else {     
            ContentSection section = item.getContentSection();
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
            
            String name = null;
            if (item instanceof ContentPage) {
                name = ((ContentPage)item).getTitle();
            } else {
                name = item.getName();
            }

            String subject = "Content Item " + name + " is about to expire";

				
			
            String publicURL = URL.there(section.getSiteNode().getURL(DispatcherHelper.getWebappContext())
                                         + ((ContentItem)item.getParent()).getPath(),
                                         null).getURL();

            // link to the trunk version of the item, which can be edited.
            // the live item cannot!
            //String adminURL = URL.there(ContentItemPage.getItemURL(item,ContentItemPage.AUTHORING_TAB), null).getURL();
            ContentItem adminItem = item.getDraftVersion();
            if (adminItem == null) {
                adminItem = item;
            }
            String adminURL = URL.there(ContentItemPage.getItemURL(adminItem,
                                                                   ContentItemPage.AUTHORING_TAB)
                                        , null).getURL();

            StringBuffer body = new StringBuffer(300);
            body.append("Content Item ");
            body.append(name);
            body.append(" at URL \n");
            body.append(publicURL);
            body.append(" is going to expire at ");
            body.append(df.format( lc.getEndDate()));
            body.append("\nAccess item administration here: ");
            body.append(adminURL);
            
            message = new Message(sender, subject, body.toString());
        }
        return message;
    }
}
