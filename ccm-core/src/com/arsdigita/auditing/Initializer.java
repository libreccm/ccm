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
package com.arsdigita.auditing;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;

/**
 * Initializer for the auditing service. Currently the only available
 * configuration is AuditingSaveInfoClass, which must implement the
 * AuditingSaveInfo interface.
 *
 * <p>
 * 
 * </p>
 *
 * @author Joseph Bank 
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */

public class Initializer
    implements com.arsdigita.initializer.Initializer
{

    public static final String versionId = "$Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    // configuration

    private Configuration m_conf = new Configuration();

    // constants

    static final String AUDITING_CLASS_NAME =
        "AuditingSaveInfoClass";
    static final String AUDITING_CLASS_DESC =
        "The names of the classes implementing Auditing Save Info";

    /**
     * Default constructor.
     */

    public Initializer() throws InitializationException {
        m_conf.initParameter(AUDITING_CLASS_NAME,
                             AUDITING_CLASS_DESC,
                             String.class);
    }

    /**
     * Returns the current configuration.
     * @return the current configuration.
     */

    public final Configuration getConfiguration() {
        return m_conf;
    }

    /**
     * Sets up the class that grabs AuditingSaveInfo.
     */

    public void startup() {
        try {
            String classname =
                (String)m_conf.getParameter(AUDITING_CLASS_NAME);
            AuditingSaveInfo sinfo =
                (AuditingSaveInfo) Class.forName(classname).newInstance();
            AuditingSaveFactory.setPrototype(sinfo);
        } catch (Exception e) {
            throw new InitializationException
                ("Exception initializing auiting: " + e.getMessage());
        }
    }

    /**
     * Shuts down the auditing service.
     */

    public void shutdown() {
        // empty
    }
}
