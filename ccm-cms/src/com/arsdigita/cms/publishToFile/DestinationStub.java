/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.publishToFile;


import java.io.File;

/***
 * A publishing destination, consisting of an absolute document root, a
 * directory in the file system, and the URL under which this directory is
 * accessed on the live server.
 * 
 * @author Jeff Teeters (teeters@arsdigita.com)
 * @author <a href="mailto:dlutter@redhat.com">David Lutterkort</a>
 * @version $Revision: #10 $ $DateTime: 2004/08/17 23:15:09 $
 */
class DestinationStub {

    private String m_documentRoot;
    private boolean m_sharedRoot;
    private String m_urlStub;
    private File m_file;

    /***
     * Constructor.  Ensures that document root is an absolute path.
     * @param docRoot either a relative or absolute path to the document root.
     * @param url url stub for associated server.
     ***/
    DestinationStub(String docRoot,
                    boolean sharedRoot,
                    String urlStub) {
        m_documentRoot = docRoot;
        m_sharedRoot = sharedRoot;
        m_urlStub = urlStub;

        m_file = new File(docRoot);
    }

    /**
     * The file representing the directory root
     */
    public File getFile() {
        return m_file;
    }

    /**
     * Returns true if the publishing root is on 
     * shared storage (NFS)
     */
    public boolean isSharedRoot() {
        return m_sharedRoot;
    }
    
    /**
     * Returns the URL stub for published items
     * eg /p2fs
     */
    public String getURLStub() {
        return m_urlStub;
    }

    public String toString() {
        return "Doc Root " + m_documentRoot +
            (isSharedRoot() ? " (shared)" : " (not shared)") +
            " live URL stub is " + getURLStub();
    }
}
