/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum.upgrade;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumSubscription;
import com.arsdigita.forum.ThreadCollection;
import com.arsdigita.forum.ThreadSubscription;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.packaging.Program;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.web.ApplicationType;

/**
 * @author cgyg9330
 *
 * Tidy up existing Forum groups  
 * 
 * 
 */
public class CreateContainerGroups extends Program {

	public CreateContainerGroups() {
		super("CreateGroups", "1.0.0", "");
	}

	private static Logger s_log = Logger.getLogger(CreateContainerGroups.class);

	public void doRun(CommandLine cmdLine) {
		try {
		
		final Session session = SessionManager.getSession();

		final TransactionContext tc = session.getTransactionContext();

			DataCollection types = session.retrieve(ApplicationType.BASE_DATA_OBJECT_TYPE);
			types.addEqualsFilter("objectType", Forum.BASE_DATA_OBJECT_TYPE);
			if (types.next()) {
				ApplicationType forumApps = (ApplicationType)DomainObjectFactory.newInstance(types.getDataObject());
				if (forumApps.getGroup() == null) {
				
 				forumApps.createGroup();
				types.close();
				s_log.debug("created app type group");
				System.out.println("created app type group");
				} else {
					s_log.debug("Forum app type group exists");
					System.out.println("Forum app type group exists");
				}
			}
			
		DataCollection existingForums =
			session.retrieve(Forum.BASE_DATA_OBJECT_TYPE);
		while (existingForums.next()) {
			
			Forum forum =
				(Forum) DomainObjectFactory.newInstance(
					existingForums.getDataObject());
			s_log.debug("****************" + forum.getTitle());
			System.out.println("****************" + forum.getTitle());
			Group existingGroup = forum.getGroup();
			if (existingGroup == null) {

				tc.beginTxn();

				forum.createGroup();

				Group container = forum.getGroup();
				forum.getAdminGroup().setName(forum.getTitle() + " Administrators");
				forum.getModerationGroup().setName(forum.getTitle() + " Moderators");
				forum.getThreadCreateGroup().setName(forum.getTitle() + " Thread Creators");
				forum.getThreadResponderGroup().setName(forum.getTitle() + " Thread Responders");
				forum.getReadGroup().setName(forum.getTitle() + " Readers");
				container.addSubgroup(forum.getAdminGroup());
				container.addSubgroup(forum.getModerationGroup());
				container.addSubgroup(forum.getThreadCreateGroup());
				container.addSubgroup(forum.getThreadResponderGroup());
				container.addSubgroup(forum.getReadGroup());
				DataCollection subscriptions = forum.getSubscriptions();
				while (subscriptions.next()) {
					ForumSubscription forumSubscription =
						(ForumSubscription) DomainObjectFactory.newInstance(
							subscriptions.getDataObject());
					Group subscriptionGroup = forumSubscription.getGroup();
					subscriptionGroup.setName(
						forumSubscription.getSubscriptionGroupName());
					container.addSubgroup(subscriptionGroup);
				}
				GroupCollection subgroups = container.getSubgroups();
				subgroups.addEqualsFilter(
					"name",
					Forum.THREAD_SUBSCRIPTION_GROUPS_NAME);
				if (subgroups.size() == 0) {

					Group threadSubscriptions = new Group();
					threadSubscriptions.setName(
						Forum.THREAD_SUBSCRIPTION_GROUPS_NAME);
					container.addSubgroup(threadSubscriptions);
					ThreadCollection threads = forum.getThreads();
					while (threads.next()) {
						MessageThread thread = threads.getMessageThread();
						ThreadSubscription subscription =
							ThreadSubscription.getThreadSubscription(thread);
						if (subscription != null) {
							Group group = subscription.getGroup();
							group.setName(subscription.getSubscriptionGroupName(forum));
							threadSubscriptions.addSubgroup(group);
						}
					}
				}
				tc.commitTxn();
			} else {
				System.out.println(
					"group exists for "
						+ forum.getTitle()
						+ " - skipping to next forum");

			}
		}
		} catch (Throwable e) {
			e.printStackTrace();
			s_log.error("error occured", e);
		}
	}

	public static final void main(final String[] args) {
		new CreateContainerGroups().run(args);
	}

}
