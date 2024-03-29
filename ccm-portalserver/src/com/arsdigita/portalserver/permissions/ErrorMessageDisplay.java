/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.permissions;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;

/**
 * 
 *
 */
class ErrorMessageDisplay extends Label {
    public ErrorMessageDisplay(final RequestLocal errorMessageRL) {
        // deprecated / forbidden"
        // Bebop must not specify design properties but just logical / semantic
        // porperties. The theme decides how to display an error messages.
        // setColor(Color.red);
        setClassAttr("pageErrorDisplay");
        addPrintListener(new PrintListener() {
                @Override
                public void prepare(PrintEvent ev) {
                    Label target = (Label)ev.getTarget();
                    String errMsg =
                        (String)errorMessageRL.get(ev.getPageState());
                    if (errMsg != null) {
                        target.setLabel(errMsg);
                    }
                }
            });
    }
}
