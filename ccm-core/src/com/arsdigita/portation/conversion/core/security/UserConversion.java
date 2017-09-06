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
package com.arsdigita.portation.conversion.core.security;

import com.arsdigita.portation.modules.core.security.User;

import java.util.List;

/**
 * Class for converting all trunk-{@link com.arsdigita.kernel.User}s into
 * ng-{@link User}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 4.7.16
 */
public class UserConversion {

    /**
     * Retrieves all trunk-{@link com.arsdigita.kernel.User}s from the
     * persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link User}s.
     */
    public static void convertAll() {
        System.err.printf("\tFetching users from database...");
        List<com.arsdigita.kernel.User> trunkUsers = com.arsdigita.kernel
                .User.getAllObjectUsers();
        System.err.println("done.");

        System.err.printf("\tConverting users...\n");
        // create users
        long processed = 0;
        for (com.arsdigita.kernel.User trunkUser : trunkUsers) {
            new User(trunkUser);
            processed++;
        }
        System.out.printf("\t\tCreated %d users.\n", processed);
        System.err.println("\tdone.\n");
    }
}
