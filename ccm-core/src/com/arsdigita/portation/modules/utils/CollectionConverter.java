/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.modules.utils;

import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.portation.modules.core.security.Group;
import com.arsdigita.portation.modules.core.security.Party;
import com.arsdigita.portation.modules.core.security.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 01.06.16
 */
public class CollectionConverter {
    private static final Logger logger = Logger.getLogger(CollectionConverter.class);





    public static List<Party> convertParties(PartyCollection partyCollection) {
        List<Party> parties = new ArrayList<>();
        if (partyCollection != null) {
            while (partyCollection.next()) {
                parties.add(new Party());
            }
            partyCollection.close();
        } else {
            logger.error("A Failed to exportUsers, cause party collection is null.");
        }
        return parties;
    }

    public static List<User> convertUsers(UserCollection userCollection) {
        List<User> users = new ArrayList<>();
        if (userCollection != null) {
            while (userCollection.next()) {
                users.add(new User());
            }
            userCollection.close();
        } else {
            logger.error("A Failed to exportUsers, cause user collection is null.");
        }
        return users;
    }

    public static List<Group> convertGroups(GroupCollection groupCollection) {
        List<Group> groups = new ArrayList<>();
        if (groupCollection != null) {
            while (groupCollection.next()) {
                groups.add(new Group());
            }
            groupCollection.close();
        } else {
            logger.error("A Failed to exportUsers, cause group collection is null.");
        }
        return groups;
    }

    public static List<String> convertMailAddresses(Iterator it) {
        List<String> mailAddresses = new ArrayList<>();
        if (it != null) {
            while (it.hasNext()) {
                mailAddresses.add(((EmailAddress) it.next()).getEmailAddress());
            }
        } else {
            logger.error("A Failed to exportUsers, cause mail collection is null.");
        }
        return mailAddresses;
    }
}
