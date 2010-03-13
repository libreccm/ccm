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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.Label;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
// import com.arsdigita.ui.admin.AdminConstants;
import java.math.BigDecimal;

/**
 * A label that initializes itself to the user's full name.
 *
 * @version $Id: UserNameLabel.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class UserNameLabel extends Label {

    public UserNameLabel() {

        super();

        PrintListener p = new PrintListener() {
                public void prepare (PrintEvent e) {
                    PageState s = e.getPageState();

                    Label t = (Label) e.getTarget();

                    User user;
                    try {
                        // Deprecated, use getValue instead
                        BigDecimal id = (BigDecimal) s.getGlobalValue("user_id");
//                      BigDecimal id = (BigDecimal) s.getValue(USER_ID_PARAM);
                        user = User.retrieve(id);
                        t.setLabel(user.getName());
                    } catch (DataObjectNotFoundException ex) {
                        t.setLabel("");
                    }
                }
            };

        addPrintListener(p);
    }
}
