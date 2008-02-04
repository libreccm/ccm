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
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * An implementation of {@link PDLSource} that loads the contents of a
 * zip or jar file.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

class ZipSource implements PDLSource {

    public final static String versionId = "$Id: ZipSource.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private final ZipFile m_file;
    private final PDLFilter m_filter;

    /**
     * @param zis the ZipInputStream to load
     **/

    public ZipSource(ZipFile file, PDLFilter filter) {
        m_file = file;
        m_filter = filter;
    }

    /**
     * Parses the contents of this PDLSource using the given
     * PDLCompiler.
     *
     * @param compiler the compiler used to parse this PDLSource
     **/

    public void parse(PDLCompiler compiler) {
        Enumeration entries = m_file.entries();
        HashSet entrynames = new HashSet();
        while (entries.hasMoreElements()) {
            entrynames.add(((ZipEntry)entries.nextElement()).getName());
        }
        Collection accepted = m_filter.accept(entrynames);

        Iterator iter = accepted.iterator();
        while (iter.hasNext()) {
            try {
                String entryname = (String)iter.next();
                ZipEntry entry = new ZipEntry (entryname);
                if (entry.isDirectory()) { continue; }
                String name = entry.getName();
                compiler.parse(new InputStreamReader(m_file.getInputStream(entry)) {
                        public void close() {
                            // We need to override close here to do
                            // nothing since compiler.parse appears to
                            // close the input stream from underneath us,
                            // and that passes through to close the
                            // underlying zip input stream which is a
                            // problem when we try to read the next entry.
                        }
                    }, name);
            } catch (IOException e) {
                throw new UncheckedWrapperException(e);
            } catch (IllegalStateException e) {
                throw new UncheckedWrapperException(e);
            }
        }
    }
}
