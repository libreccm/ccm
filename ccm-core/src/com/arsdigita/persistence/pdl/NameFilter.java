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

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An implementation of {@link PDLFilter} that filters based on
 * extension and suffix.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

public class NameFilter implements PDLFilter {

    public final static String versionId = "$Id: NameFilter.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private final String m_suffix;
    private final String m_extension;

    /**
     * Constructs a name filter that only accepts pdl files with the
     * given suffix and extension.
     *
     * @param suffix the allowed suffix
     * @param extension the allowed extension
     **/

    public NameFilter(String suffix, String extension) {
        m_suffix = suffix;
        m_extension = extension;
    }

    /**
     * Tests <code>name</code> against this NameFilters suffix and
     * extension.
     */
    public Collection accept(Collection names) {
        // map from basename to full filename. accept the longest match.
        HashMap accepted = new HashMap();

        for (Iterator it = names.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            int idx = name.lastIndexOf('.');

            if (idx < 0) { continue; }

            String ext = name.substring(idx + 1);
            String base = name.substring(0, idx);

            if (!ext.equals(m_extension)) { continue; }

            idx = base.lastIndexOf('.');
            int idx2 = base.lastIndexOf(File.separatorChar);
            if (idx > -1 && idx > idx2) {
                String sfx = base.substring(idx + 1);

                if (!sfx.equals(m_suffix)) { continue; }

                base = base.substring(0, idx);
            }

            String cur = (String) accepted.get(base);
            if (cur == null || (cur.length() < name.length())) {
                accepted.put(base, name);
            }
        }

        return accepted.values();
    }

}
