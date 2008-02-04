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

/**
 * A DataInitEvent is passed to the {@link
 * Initializer#init(DataInitEvent)} method in order to provide the
 * target Initializer access to the objects necessary to properly
 * initialize the data layer.
 *
 * @author Rafael Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: DataInitEvent.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class DataInitEvent {
    public final static String versionId =
        "$Id: DataInitEvent.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private final PDLCompiler m_compiler;

    DataInitEvent(final PDLCompiler compiler) {
        m_compiler = compiler;
    }

    /**
     * Returns an instance of a PDLCompiler object that may be used to
     * parse PDL.
     *
     * @return a PDLCompiler
     *
     * @see com.arsdigita.persistence.pdl.PDLSource
     **/

    public final PDLCompiler getCompiler() {
        return m_compiler;
    }

}
