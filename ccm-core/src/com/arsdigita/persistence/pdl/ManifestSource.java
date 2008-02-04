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
package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The ManifestSource class provides an implementation of the {@link
 * PDLSource} interface that loads object-relational metadata from a
 * manifest file that lists resources located in the java classpath.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

public class ManifestSource implements PDLSource {

    public final static String versionId = "$Id: ManifestSource.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private final String m_manifest;
    private final PDLFilter m_filter;
    private final ClassLoader m_loader;

    /**
     * Constructs a new ManifestSource from the resources listed in
     * <code>manifest</code>. This source will be filtered by
     * <code>filter</code>, and <code>loader</code> will be used to
     * load all resources.
     *
     * @param manifest a resource path referring to a manifest file
     * @param filter a filter on the names in the manifest file
     * @param loader the loader used to locate resources
     **/

    public ManifestSource(String manifest, PDLFilter filter,
                          ClassLoader loader) {
        m_manifest = manifest;
        m_filter = filter;
        m_loader = loader;
    }

    /**
     * Invokes {@link #ManifestSource(String, PDLFilter, ClassLoader)}
     * with the current context class loader.
     *
     * @param manifest a resource path referring to a manifest file
     * @param filter a filter on the names in the manifest file
     *
     * @see Thread#getContextClassLoader()
     **/

    public ManifestSource(String manifest, PDLFilter filter) {
        this(manifest, filter, Thread.currentThread().getContextClassLoader());
    }

    /**
     * An implementation of {@link PDLSource#parse(PDLCompiler)} that
     * parses the resource listed in the manifest passed to the
     * constructor of this ManifestSource.
     *
     * @param compiler the compiler used to parse
     **/

    public void parse(PDLCompiler compiler) {
        InputStream is = m_loader.getResourceAsStream(m_manifest);
        if (is == null) {
            throw new IllegalStateException("no such resource: " + m_manifest);
        }

        try {
            LineNumberReader lines =
                new LineNumberReader(new InputStreamReader(is));

            ArrayList names = new ArrayList();

            while (true) {
                String line = lines.readLine();
                if (line == null) { break; }
                line = line.trim();
                names.add(line);
            }

            for (Iterator accepted = m_filter.accept(names).iterator();
                 accepted.hasNext(); ) {

                String line = (String) accepted.next();

                InputStream pdl = m_loader.getResourceAsStream(line);
                if (pdl == null) {
                    throw new IllegalStateException
                        (m_manifest + ": " + lines.getLineNumber() +
                         ": no such resource '" + line + "'");
                }
                try {
                    compiler.parse(new InputStreamReader(pdl), line);
                } finally {
                    pdl.close();
                }
            }
        } catch (IOException e) {
            throw new UncheckedWrapperException(e);
        } finally {
            try { is.close(); }
            catch (IOException e) { throw new UncheckedWrapperException(e); }
        }
    }

}
