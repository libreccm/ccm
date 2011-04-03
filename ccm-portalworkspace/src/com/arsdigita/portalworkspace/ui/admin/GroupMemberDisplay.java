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
 */

package com.arsdigita.portalworkspace.ui.admin;

import java.math.BigDecimal;

import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.london.util.ui.MembersDisplay;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;

public abstract class GroupMemberDisplay extends MembersDisplay {

	protected abstract Group getGroup(PageState ps);

	protected DataQuery getUsers(PageState ps) {
		Group group = getGroup(ps);
		return group.getContainedParties();
	}

	protected String getDisplayName(DataQuery q) {
		Party party = ((PartyCollection) q).getParty();
		return party.getDisplayName();
	}

	protected String getKey(DataQuery q) {
		Party party = ((PartyCollection) q).getParty();
		return party.getID().toString();
	}

	protected void removeUser(PageState ps, BigDecimal userID) {
		Party party = null;

		try {
			party = (Party) DomainObjectFactory.newInstance(new OID(
					Party.BASE_DATA_OBJECT_TYPE, userID));
		} catch (DataObjectNotFoundException ex) {
			throw new UncheckedWrapperException("cannot find user", ex);
		}

		Group group = getGroup(ps);
		group.removeMemberOrSubgroup(party);
		group.save();
	}

}
