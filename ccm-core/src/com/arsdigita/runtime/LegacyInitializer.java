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

import com.arsdigita.initializer.Script;
import com.arsdigita.packaging.ConfigRegistry;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The LegacyInitializer class provides an implementation of the
 * Initializer interface that can be used to adapt a set of
 * initializers written for the old Initializer system for use with
 * the new Initializer system. This class works by using the
 * deprecated {@link Initializer#init(LegacyInitEvent)} method. It is
 * not possible to adapt old Initializers into the new Initialization
 * system without using the legacy method because the new
 * initialization system intentionally addresses only a subset of the
 * requirements addressed by the old system.
 *
 * In particular the purpose of the new initialization system is to
 * prepare the CCM runtime for interaction with the database. The old
 * initialization system served not only to prepare the CCM runtime
 * for interaction with the database, but also to do on demand data
 * loading, read in configuration parameters, and perform context
 * specific initialization such as spawing background threads.
 *
 * In order to facilitate easy packaging and configuration, these
 * responsibilities have been divided into different areas in the
 * latest version of CCM. For these reasons this class should only be
 * used as an interim step towards adapting a CCM package to work with
 * the new initialization and packaging APIs. Configuration parameters
 * previously read in using the old Initializer classes should be made
 * to use subclasses of {@link AbstractConfig}. On demand data loading
 * should be moved into its own data loading script that is a subclass
 * of {@link AbstractScript}. Data and domain initialization should be
 * moved into a package specific implementation of the new Initializer
 * interface.
 *
 * @deprecated
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

public class LegacyInitializer implements Initializer {

    public final static String versionId = "$Id: LegacyInitializer.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    protected String m_init;
    protected ConfigRegistry m_reg;

    /**
     * Constructs a new legacy initializer that will locate the
     * enterprise.init resource pointed to by <code>init</code> and
     * invoke it using the old initialization system. The default
     * configuration registry will be searched first, and if the
     * resource is not found there, it will be searched for using
     * <code>loader</code>
     *
     * @param init The name of a resource referring to a legacy
     * enterprise.init file.
     *
     * @param loader The class loader to fallback on if the
     * enterprise.init file is not found in the default configuration
     * registry.
     **/

    public LegacyInitializer(String init, ClassLoader loader) {
        m_init = init;
        m_reg = new ConfigRegistry(loader);
    }

    /**
     * Invokes {@link #LegacyInitializer(String, ClassLoader)} passing
     * in the context class loader for the current thread as the
     * fallback loader.
     *
     * @param init The name of a resource referring to a legacy
     * enterprise.init file.
     *
     * @see Thread#getContextClassLoader()
     **/

    public LegacyInitializer(String init) {
        this(init, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Implentation of {@link Initializer#init(DataInitEvent evt)}
     * that does nothing.
     **/

    public void init(DataInitEvent evt) {
        // do nothing
    }

    /**
     * Implentation of {@link Initializer#init(DomainInitEvent evt)}
     * that does nothing.
     **/

    public void init(DomainInitEvent evt) {
        // do nothing
    }

    /**
     * Implementation of {@link Initializer#init(LegacyInitEvent evt)}
     * that locates the enterprise.init resource referenced in the
     * constructor of this LegacyInitializer and invokes the old
     * initialization code on the contents of the resource.
     **/

    public void init(LegacyInitEvent evt) {
        InputStream is = m_reg.load(m_init);
        if (is == null) {
            throw new IllegalStateException("no such resource: " + m_init);
        }
        try {
            Script init = new Script(is, m_init);
            init.startup();
        } finally {
            try { is.close(); }
            catch (IOException e) { throw new UncheckedWrapperException(e); }
        }
    }

}
