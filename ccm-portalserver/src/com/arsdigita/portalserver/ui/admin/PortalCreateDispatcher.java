/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.ui.admin;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.ui.login.UserAuthenticationListener;

import java.util.Map;
import java.util.HashMap;

public class PortalCreateDispatcher extends BebopMapDispatcher {

    public static final String versionId = "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/PortalCreateDispatcher.java#4 $ by $Author: dennis $, $DateTime: 2004/08/17 23:19:25 $";

    public PortalCreateDispatcher() {
        Map m = new HashMap();
        Page prtlCreatePage = new PortalCreatePage();

        prtlCreatePage.addRequestListener(new UserAuthenticationListener());
        prtlCreatePage.lock();

        m.put("", prtlCreatePage);
        m.put("index", prtlCreatePage);
        setMap(m);
    }
}
