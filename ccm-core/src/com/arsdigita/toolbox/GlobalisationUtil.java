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
package com.arsdigita.toolbox;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Utility class for simplify the handling of {@link GlobalizedMessage}. This class is intended as a
 more object orientated replacement for the numerous GlobalisationUtil classes with static
 methods.

 This class should not used directly. Instead create a subclass of this class with a parameter
 less constructor which calls the constructor of this class providing the name of the bundle.
 *
 * @see GlobalizedMessage
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class GlobalisationUtil {

    private final transient String bundleName;

    /**
     * Constructor for a new {@link GlobalisationUtil} instance. For normal use cases don't use this
     * constructor directly to create an instance of this class. Instead a subclass should be
     * created which provides a parameterless constructor which class this constructor with the
     * fully qualified name of the appropriate bundle. For some use cases, for example a resource
     * bundle which is only used in a single class may be used by simply using this constructor.
     *
     * @param bundleName Name of the bundle to be used by this {@code GlobalisationUtil} instance.
     */
    public GlobalisationUtil(final String bundleName) {
        this.bundleName = bundleName;
    }

    /**
     * Lookup the globalized message identified by the provided key in the resource bundle.
     *
     * @param key Message key
     * @return The globalized message.
     */
    public GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, bundleName);
    }

    /**
     * Lookup the globalized message identified by the provided key in the resource bundle and
 replace the placeholders in the message with the provided arguments.
     *
     * @param key The key of the message to lookup.
     * @param args Arguments for the placeholders in the message.
     * @return The globalized message.
     */
    public GlobalizedMessage globalize(final String key,
                                       final Object[] args) {
        return new GlobalizedMessage(key, bundleName, args);
    }

}
