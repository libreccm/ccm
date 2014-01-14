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
package com.arsdigita.kernel;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

public class HashPasswords {

    private static final Logger s_log =
        Logger.getLogger(HashPasswords.class);

    static void hashPasswords() {
        DataCollection dc = SessionManager.getSession()
            .retrieve(UserAuthentication.BASE_DATA_OBJECT_TYPE);
        dc.addEqualsFilter("salt", null);
        for (int i = 1; dc.next(); i ++) {
            s_log.info("HashPasswords: updating user "+i);
            UserAuthentication auth =
                new UserAuthentication(dc.getDataObject());
            auth.hashPassword();
            auth.save();
        }
        dc.close();
    }
}
