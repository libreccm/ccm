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
package com.arsdigita.cms.search;


import com.arsdigita.search.ContentType;
import com.arsdigita.search.ContentProvider;

import org.apache.log4j.Logger;

public class RawContentProvider implements ContentProvider {
    private static final Logger s_log =
        Logger.getLogger( RawContentProvider.class );

    private byte[] m_content;
    private String m_context;

    public RawContentProvider(String context,
                              byte[] content) {
        m_context = context;
        m_content = content;
    }

    public String getContext() {
        return m_context;
    }

    public ContentType getType() {
        return ContentType.RAW;
    }

    public byte[] getBytes() {
        if( s_log.isDebugEnabled() ) {
            int length = m_content.length > 512 ? 512 : m_content.length;

            s_log.debug( "RAW Content is: " +
                         new String( m_content, 0, length ) );
        }

        return m_content;
    }

}
