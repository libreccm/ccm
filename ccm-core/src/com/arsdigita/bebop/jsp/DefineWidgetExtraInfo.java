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
package com.arsdigita.bebop.jsp;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * Declares scripting variable for the parameter model
 * created in a widget-creation tag (e.g., define:text).
 * This allows validation listeners to be added conveniently.
 * example:
 * <pre>
 * &lt;define:text name="inputFieldName"/>
 & &lt;% inputFieldName.addParameterListener(new NotEmptyValidationListener()); %>
 * </pre>
 * 
 * @version $Id: DefineWidgetExtraInfo.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DefineWidgetExtraInfo extends TagExtraInfo {

    public VariableInfo[] getVariableInfo(TagData data) {
        return new VariableInfo[] {
            new VariableInfo(data.getAttributeString("name"),
                             "com.arsdigita.bebop.parameters.ParameterModel",
                             true,
                             VariableInfo.AT_END)
        };
    }
}
