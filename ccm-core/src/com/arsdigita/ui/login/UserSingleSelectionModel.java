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
package com.arsdigita.ui.login;

import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.web.Web;
import com.arsdigita.kernel.security.UserContext;

/**
 * A SingleSelectionModel that returns the user id.
 *
 * @author Phong Nguyen
 * @author Sameer Ajmani
 * @version 1.0
 */
public class UserSingleSelectionModel extends AbstractSingleSelectionModel {
    public static final String versionId =
        "$Id: UserSingleSelectionModel.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final String s_userIdName = "userId";
    private BigDecimalParameter m_parameter;

    public UserSingleSelectionModel() {
        super();
        m_parameter = new BigDecimalParameter(s_userIdName);
    }

    public Object getSelectedKey(PageState state) {
        UserContext ctx = Web.getUserContext();

        if (ctx.isLoggedIn()) {
            return ctx.getUserID();
        }

        return null;
    }

    public void setSelectedKey(PageState state, Object key) {
        // TODO:
    }

    public ParameterModel getStateParameter() {
        return m_parameter;
    }
}
