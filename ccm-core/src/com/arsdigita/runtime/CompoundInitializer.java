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
package com.arsdigita.runtime;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * The CompoundInitializer class provides a convenient way to group
 * together a number of individual initializers into a single object
 * that implements the Initializer interface. Using this class to
 * group initializers will guarantee that the various init methods on
 * the contained initializers will be invoked in the proper order,
 * i.e. all data init methods will be invoked first in order, followed
 * by all domain init methods, followed by all legacy init methods.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: CompoundInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class CompoundInitializer implements Initializer {

    private static final Logger s_log = 
        Logger.getLogger(CompoundInitializer.class);

    private Logger m_log;

    private final List m_inits = new ArrayList();

    /**
     * Creates a new and empty compound initializer.
     */
    public CompoundInitializer() {
        this(s_log);
    }

    /**
     * Creates a new and empty compound initializer that uses the
     * <code>log</code> Logger to log progress through initialization.
     *
     * @param log A logger to be used for logging progress through
     *            initialization.
     */
    public CompoundInitializer(Logger log) {
        m_log = log;
    }

    /**
     * Adds <code>init</code> to the set of initializers to be initialized by
     * this CompoundInitializer. 
     * 
     * The most recently added initializers will be invoked last.
     *
     * @param init The initializer to add to this CompoundInitializer
     */
    public void add(Initializer init) {
        m_inits.add(init);
    }

    /**
     * Implementation of the {@link Initializer#init(DataInitEvent)} method.
     * This implementation proceeds through the list of sub initializers in order
     * and invokes the init(DataInitEvent) method of each sub initializer in turn.
     *
     * @param evt The data init event.
     */
    public void init(DataInitEvent evt) {
        int i = 1;
        for (Iterator it = m_inits.iterator(); it.hasNext(); i++) {
            Initializer init = (Initializer) it.next();
            if (m_log.isInfoEnabled()) {
                m_log.info("Running Data Init for " + init.getClass() + 
                           " (" + i + " out of " + m_inits.size() + ")");
            }
            init.init(evt);
        }
    }

    /**
     * Implementation of the {@link Initializer#init(DomainInitEvent)} method.
     * This implementation proceeds through the list of sub initializers in
     * order and invokes the init(DomainInitEvent) method of each sub initializer
     * in turn.
     *
     * @param evt The domain init event.
     */
    public void init(DomainInitEvent evt) {
        int i = 1;
        for (Iterator it = m_inits.iterator(); it.hasNext(); i++) {
            Initializer init = (Initializer) it.next();
            if (m_log.isInfoEnabled()) {
                m_log.info("Running Domain Init for " + init.getClass() + 
                           " (" + i + " out of " + m_inits.size() + ")");
            }
            init.init(evt);
        }
    }

    /**
     * Implementation of the {@link Initializer#init(LegacyInitEvent)} method.
     * This implementation proceeds through the list of sub initializers in
     * order and invokes the init(LegacyInitEvent) method of each sub initializer
     * in turn.
     *
     * @param evt The legacy init event.
     */
    public void init(LegacyInitEvent evt) {
        int i = 1;
        for (Iterator it = m_inits.iterator(); it.hasNext(); i++) {
            Initializer init = (Initializer) it.next();
            if (m_log.isInfoEnabled()) {
                m_log.info("Running Legacy Init for " + init.getClass() + 
                           " (" + i + " out of " + m_inits.size() + ")");
            }
            init.init(evt);
        }
    }

    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)} method.
     * This implementation proceeds through the list of sub initializers in
     * order and invokes the init(LegacyInitEvent) method of each sub initializer
     * in turn.
     *
     * @param evt The legacy init event.
     */
    public void init(ContextInitEvent evt) {
        int i = 1;
        for (Iterator it = m_inits.iterator(); it.hasNext(); i++) {
            Initializer init = (Initializer) it.next();
            if (m_log.isInfoEnabled()) {
                m_log.info("Running Context Init for " + init.getClass() +
                           " (" + i + " out of " + m_inits.size() + ")");
            }
            init.init(evt);
        }
    }

    /**
     * Implementation of the {@link Initializer#close(ContextCloseEvent)} method.
     *
     * This implementation proceeds through the list of sub initializers in order
     * and invokes the close(ContextCloseEvent) method of each sub initializer
     * in turn.
     *
     *
     * @param evt The context close event.
     */
    public void close(ContextCloseEvent evt) {
        s_log.info("CompoundInitializer.close(ContextCloseEvent) invoked");

        if (m_inits.isEmpty()) {
            s_log.info("m_inits is empty");
        }
        int i = 1;
        for (Iterator it = m_inits.iterator(); it.hasNext(); i++) {
            Initializer init = (Initializer) it.next();
            if (m_log.isInfoEnabled()) {
                m_log.info("Closing " + init.getClass() +
                           " (" + i + " out of " + m_inits.size() + ")");
            }
            init.close(evt);
        }
        s_log.info("CompoundInitializer.close(ContextCloseEvent) completed");
    }

}
