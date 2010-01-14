/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.runtime;

/**
 * Provides an empty implementation of the initializer interface.
 *
 * Sub-initializers of a module, which provide specific initialization for a
 * Java package of the module, should extend this class if they do not need
 * to implement each of the initializer interface classes.
 *
 * The main initializer of module should extend {@link CompoundInitializer} as
 * it provides a way to add sub-initializers to the list of initializers during
 * startup and a generic pdl initialization, which most main-initializers will
 * need, but sub-subinitializers generally not.
 *
 * It also facilitates the migration to the new initializer system.
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 */
public abstract class GenericInitializer implements Initializer {

    // private final Configuration m_config = new Configuration();
    // private final String m_name;

    public GenericInitializer() {
    }


    /**
     * An empty implementation of {@link Initializer#init(DataInitEvent)}.
     *
     * @param evt The data init event.
     **/
    public void init(DataInitEvent evt) {
    }


    /**
     * An empty implementation of {@link Initializer#init(DomainInitEvent)}.
     *
     * @param evt The domain init event.
     **/
    public void init(DomainInitEvent evt) {
    }


    /**
     * An empty implementation of {@link Initializer#init(LegacyInitEvent)}
     * method.
     *
     * @param evt The legacy init event.
     **/
    public void init(LegacyInitEvent evt) {
    }

    /**
     * An empty implementation of {@link Initializer#init(ContextInitEvent)}
     * method.
     *
     * @param evt The context init event.
     **/
    public void init(ContextInitEvent evt) {
    }

    /**
     * An empty implementation of {@link Initializer#close(ContextCloseEvent)}
     * method.
     *
     * @param evt The context close event.
     **/
    public void close(ContextCloseEvent evt) {
    }

}
