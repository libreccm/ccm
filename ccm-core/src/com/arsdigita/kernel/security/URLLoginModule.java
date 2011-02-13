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
package com.arsdigita.kernel.security;

import com.arsdigita.bebop.parameters.StringParameter;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Logs in a user if the user has a valid authentication URL parameter.
 *
 * @author Sameer Ajmani
 * @version $Id: URLLoginModule.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class URLLoginModule extends UserLoginModule {

    private static final Logger s_log =
        Logger.getLogger(URLLoginModule.class.getName());

    /**
     * ParameterModel for non-secure user authentication URL parameter.
     **/
    private static final StringParameter NORMAL_PARAM
        = new StringParameter(NORMAL_CREDENTIAL_NAME);

    /**
     * ParameterModel for secure user authentication URL parameter.
     **/
    private static final StringParameter SECURE_PARAM
        = new StringParameter(SECURE_CREDENTIAL_NAME);

    private static Set s_models = new HashSet();
    static {
        s_log.debug("Static initalizer starting...");
        s_models.add(NORMAL_PARAM);
        s_models.add(SECURE_PARAM);
        s_log.debug("Static initalizer finished...");
    }

    /**
     * Creates a UserLoginModule that uses a URLManager to manage the
     * credential value.
     **/
    public URLLoginModule() {
        super(new URLManager(s_models));
    }
}
