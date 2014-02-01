/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.oql;

import java.io.IOException;
import java.io.Writer;

/**
 * Indentor
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

class Indentor {

    

    int level = 0;

    private Writer m_out;
    private String m_indent;
    private boolean m_start;

    Indentor(Writer out, String indent) {
        m_out = out;
        m_indent = indent;
        m_start = true;
    }

    private void write(String str) {
        try { m_out.write(str); }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    void print(String str) {
        if (m_start) {
            for (int i = 0; i < level; i++) {
                write(m_indent);
            }
            m_start = false;
        }
        write(str);
    }

    void println(String str) {
        print(str);
        println();
    }

    void println() {
        write(System.getProperty("line.separator"));
        m_start = true;
    }

}
