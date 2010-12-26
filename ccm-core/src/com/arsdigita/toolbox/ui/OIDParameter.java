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
package com.arsdigita.toolbox.ui;

import javax.servlet.http.HttpServletRequest;
import com.arsdigita.persistence.OID;
import com.arsdigita.bebop.parameters.ParameterModel;

/**
 * A {@link com.arsdigita.bebop.parameters.ParameterModel} for an
 * {@link com.arsdigita.persistence.OID}
 *
 * @author Patrick McNeill
 * @version $Id: OIDParameter.java 287 2005-02-22 00:29:02Z sskracic $
 * @since Iteration 1
 */
public class OIDParameter extends ParameterModel {

    public OIDParameter(String name) {
        super(name);
    }

    public Object transformValue(HttpServletRequest request) {
        return transformSingleValue(request);
    }

    public Object unmarshal(String encoded) {
        if (encoded.equals("")) {
            return null;
        } else {
            return OID.valueOf(java.net.URLDecoder.decode(encoded));
        }
    }

    public Class getValueClass() {
        return OID.class;
    }
}
