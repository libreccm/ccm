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
package com.arsdigita.portation.cmd;

import com.arsdigita.portation.Format;
import com.arsdigita.portation.modules.core.security.Group;
import com.arsdigita.portation.modules.core.security.GroupMarshaller;
import com.arsdigita.portation.modules.core.security.Party;
import com.arsdigita.portation.modules.core.security.PartyMarshaller;
import com.arsdigita.portation.modules.core.security.User;
import com.arsdigita.portation.modules.core.security.UserMarshaller;
import com.arsdigita.portation.modules.utils.CollectionConverter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 25.05.16
 */
class CoreExport {
    private final static Logger logger = Logger.getLogger(CoreExport.class);

    private static List<Party> parties = new ArrayList<>();
    private static List<User> users = new ArrayList<>();
    private static List<Group> groups = new ArrayList<>();

    static void retrieveParties() {
        System.out.println("\n...0...\n");
        parties = CollectionConverter.convertParties(com.arsdigita.kernel.Party.retrieveAllParties());
    }
    static void exportParties() {
        PartyMarshaller partyMarshaller = new PartyMarshaller();
        partyMarshaller.prepare(Format.XML, "PortationTestFiles", "partyExport_test01", true);
        partyMarshaller.exportList(parties);
    }

    static void retrieveUsers() {
        System.out.println("\n...1...\n");
        users = CollectionConverter.convertUsers(com.arsdigita.kernel.User.retrieveAll());
    }
    static void exportUsers() {
        UserMarshaller userMarshaller = new UserMarshaller();
        userMarshaller.prepare(Format.XML, "PortationTestFiles", "userExport_test01", true);
        userMarshaller.exportList(users);
    }

    static void retrieveGroups() {
        System.out.println("\n...2...\n");
        groups = CollectionConverter.convertGroups(com.arsdigita.kernel.Group.retrieveAll());
    }
    static void exportGroups() {
        GroupMarshaller groupMarshaller = new GroupMarshaller();
        groupMarshaller.prepare(Format.XML, "PortationTestFiles", "groupExport_test01", true);
        groupMarshaller.exportList(groups);
    }
}
