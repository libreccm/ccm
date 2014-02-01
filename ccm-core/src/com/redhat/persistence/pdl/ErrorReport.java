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
package com.redhat.persistence.pdl;

import com.redhat.persistence.pdl.nodes.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * ErrorReport
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

class ErrorReport {

    

    private ArrayList m_messages = new ArrayList();
    private boolean m_fatal = false;

    public void warn(Node node, String message) {
        m_messages.add(node.getLocation() + " [warning]: " + message);
    }

    public void fatal(Node node, String message) {
        m_fatal = true;
        m_messages.add(node.getLocation() + " [error]: " + message);
    }

    public Collection getMessages() {
        return m_messages;
    }

    public void check() {
        if (m_messages.size() == 0) {
            return;
        }

        StringBuffer buf = new StringBuffer();
        for (Iterator it = getMessages().iterator(); it.hasNext(); ) {
            buf.append(it.next() + "\n");
        }

        m_messages.clear();

        if (m_fatal) {
            throw new Error(buf.toString());
        } else {
            System.err.println(buf.toString());
        }
    }

}
