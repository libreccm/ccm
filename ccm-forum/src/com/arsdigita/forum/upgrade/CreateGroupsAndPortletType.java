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

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.portlet.MyForumsPortlet;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.packaging.Program;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.portal.PortletType;

/**
 * @author cgyg9330
 *
 * Used for upgrade from legacy forum
 */
public class CreateGroupsAndPortletType extends Program {

	public CreateGroupsAndPortletType() {
		super("CreateGroups", "1.0.0", "");
	}

	public void doRun(CommandLine cmdLine) {
	    new KernelExcursion() {
            	public void excurse() {
            	setEffectiveParty(Kernel.getSystemParty());
       
		final Session session = SessionManager.getSession();

		final TransactionContext tc = session.getTransactionContext();
		tc.beginTxn();

		DataCollection existingForums =
			session.retrieve(Forum.BASE_DATA_OBJECT_TYPE);
		while (existingForums.next()) {
			Forum forum =
				(Forum) DomainObjectFactory.newInstance(
					existingForums.getDataObject());
			Group admin = new Group();
			admin.setName(forum.getTitle() + " Administrators");
			forum.setAdminGroup(admin);
			
			Group threadCreators = new Group();
			threadCreators.setName(forum.getTitle() + " Thread Creators");
			forum.setThreadCreatorGroup(threadCreators);
			threadCreators.addMember(Kernel.getPublicUser());
			Group threadResponders = new Group();
			threadResponders.setName(forum.getTitle() + " Thread Responders");
			forum.setThreadResponderGroup(threadResponders);
			Group threadReaders = new Group();
			threadReaders.setName(forum.getTitle() + " Readers");
			forum.setReaderGroup(threadReaders);
			
		}
		
		
		PortletType type = PortletType
				   .createPortletType("My Forums", 
									  PortletType.WIDE_PROFILE,
		MyForumsPortlet.BASE_DATA_OBJECT_TYPE);
			   type.setDescription("Lists forums that user has access to, with last posting date");
			
		tc.commitTxn();
		}
            }.run();
	}

	public static void main(final String[] args) {
		new CreateGroupsAndPortletType().run(args);
	}

}

