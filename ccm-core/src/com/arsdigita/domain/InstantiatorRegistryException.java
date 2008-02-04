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
package com.arsdigita.domain;

import org.apache.log4j.Logger;

/**
 * InstantiatorRegistryException
 *
 * An unchecked exception thrown when there is an error registering a
 * DomainObjectInstantiator.
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *
 */
public class InstantiatorRegistryException extends RuntimeException {
    private static Logger s_log = Logger.getLogger(InstantiatorRegistryException.class);

    /**
     * Constructs exception
     *
     * @param typeName - Name of the ObjectType
     * @param instantiator - The instantiator that was being registered.
     */
    public InstantiatorRegistryException(String typeName, DomainObjectInstantiator instantiator) {
        super(buildMessage(typeName, instantiator));
    }


    /**
     * Creates the error message, and sends to the system log.
     *
     * @param typeName - Name of the ObjectType
     * @param instantiator - The instantiator that was being registered.
     *
     * @return error message
     */
    private static String buildMessage(String typeName, DomainObjectInstantiator instantiator) {
        String msg = "Registering non existent ObjectType " +
                              typeName + " against instantiator " +
                              instantiator.getClass().toString();
        msg += System.getProperty("line.separator");
        msg += "Possible causes are a missing PDL file, or an incorrect BASE_DATA_OBJECT_TYPE";

        s_log.error(msg);
        return msg;
    }
}
