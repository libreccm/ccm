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

import com.arsdigita.portation.categories.User.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.portation.Format;
import com.arsdigita.portation.categories.User.UserMarshaller;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 25.05.16
 */
class UserExport {
    private final static Logger logger = Logger.getLogger(UserExport.class);

    private final UserCollection userCollection = com.arsdigita.kernel.User.retrieveAll();
    private List<User> users = new ArrayList<>();

    UserExport() {
        prepare();
    }

    private void prepare() {
        if (userCollection != null) {
            while (userCollection.next()) {
                users.add(new User(userCollection.getUser()));
            }
            userCollection.close();
        } else {
            logger.error("A Failed to export, due to empty user list.");
        }

        Arrays.stream(com.arsdigita.kernel.User.class.getDeclaredFields()).forEach(l -> System.out.println(l.toString()));
        Arrays.stream(com.arsdigita.kernel.User.class.getFields()).forEach(l -> System.out.println(l.toString()));
    }

    public void export() {
        UserMarshaller userMarshaller = new UserMarshaller();
        userMarshaller.prepare(Format.XML, "PortationTestFiles", "test1", true);
        userMarshaller.exportList(users);
    }
}
