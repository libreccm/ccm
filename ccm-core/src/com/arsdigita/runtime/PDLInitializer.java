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

import com.arsdigita.persistence.pdl.PDLCompiler;
import com.arsdigita.persistence.pdl.PDLSource;


/**
 * Provides an implementation of the {@link Initializer} interface that works
 * in conjunction with the {@link PDLSource} interface and implementations
 * thereof in order to provide a convenient way to initialize object-relational
 * metadata.
 *
 * This class is most convenient when used in conjunction with the
 * {@link CompoundInitializer} class. For example:
 *
 * <blockquote>
 * package com.arsdigita.exampleApp;
 *
 * public class Initializer extends CompoundInitializer {
 *
 *     public Initializer() {
 *         final String url = RuntimeConfig.getConfig().getJDBCURL();
 *         final int database = DbHelper.getDatabaseFromURL(url);
 *
 *         add(new PDLInitializer
 *             (new ManifestSource
 *              ("example-app.pdl.mf",
 *               new NameFilter(DbHelper.getDatabaseSuffix(database),
 *                              "pdl"))));
 *     }
 *
 * }
 * </blockquote>
 *
 * The class defined above will load all the object-relational metadata for the
 * current database referenced from the example-app.pdl.mf manifest file.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: PDLInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class PDLInitializer implements Initializer {

    private final PDLSource m_source;

    /**
     * Constructs a new implementation of the {@link Initializer} interface
     * that will upon invokation load any object-relational metadata
     * referenced by the given PDLSource.
     *
     * @param source The object-relational metadata to load.
     **/

    public PDLInitializer(PDLSource source) {
        m_source = source;
    }

    /**
     * An implementation of the {@link Initializer#init(DataInitEvent)} that
     * loads the object-relational metadata from the PDLSource specified
     * during construction of this PDLInitializer.
     **/

    public void init(DataInitEvent evt) {
        PDLCompiler compiler = evt.getCompiler();
        m_source.parse(compiler);
    }

    /**
     * An empty implementation of {@link Initializer#init(DomainInitEvent)}.
     **/

    public void init(DomainInitEvent evt) {
        // do nothing
    }

    /**
     * An empty implementation of {@link Initializer#init(LegacyInitEvent)}.
     **/

//  public void init(LegacyInitEvent evt) {
        // do nothing
//  }

    /**
     * An empty implementation of {@link Initializer#init(ContextInitEvent)}.
     **/

    public void init(ContextInitEvent evt) {
        // do nothing
    }

    /**
     * An empty implementation of {@link Initializer#close()}.
     **/

    public void close(ContextCloseEvent evt) {
        // do nothing
    }

}
