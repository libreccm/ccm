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
package com.arsdigita.portalserver;

import com.arsdigita.bebop.PageState;
// import com.arsdigita.web.Web;
import com.arsdigita.web.LoginSignal;
// import com.arsdigita.web.URL;
// import com.arsdigita.web.ParameterMap;
// import com.arsdigita.ui.login.LoginHelper;

// import javax.servlet.http.HttpServletRequest;
// import java.util.Map;

public class Util {
    public static void redirectToLoginPage(PageState ps) {
        throw new LoginSignal(ps.getRequest());
    }
}
