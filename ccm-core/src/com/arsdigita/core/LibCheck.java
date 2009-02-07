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
package com.arsdigita.core;

import com.arsdigita.packaging.BaseCheck;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.Assert;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * LibCheck uses the checklist mechanism to perform additional checks for
 * libraries specifically required by ccm-core.
 * (@see com.arsdigita.packaging.Check.java)
 * The check is activated during initial setup by the file /ccm-core.checklist!
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 */

public class LibCheck extends BaseCheck {

    public final static String versionId = 
            "$Id: LibCheck.java 736 2005-09-01 10:46:05Z sskracic $" +
            " by $Author: sskracic $, " +
            "$DateTime: 2004/08/16 18:10:38 $";

    // Integrating the packaging.MessageMap service class providing a
    // package specific message file by overriding the variable in BaseCheck.
    static {
        final InputStream in = LibCheck.class.getResourceAsStream
            ("libcheck.messages_linux");
        Assert.exists(in, InputStream.class);
        s_messages.load(new InputStreamReader(in));
    }

    private boolean checkJAAS() {
        final String classname = "javax.security.auth.Policy";

        if (isClassFound(classname)) {
            m_out.println(message("jaas_found"));

            checkDuplicates(classname);
            return true;
        } else {
            m_out.println(message("jaas_not_found"));
            m_out.println();
            m_out.println(message("security_jars"));
            m_out.println();
            m_out.println(message("classpath"));
            return false;
        }
    }

    private boolean checkJCE() {
        final String classname = "javax.crypto.Cipher";

        if (isClassFound(classname)) {
            m_out.println(message("jce_found"));

            checkDuplicates(classname);
            return true;
        } else {
            m_out.println(message("jce_not_found"));
            m_out.println();
            m_out.println(message("security_jars"));
            m_out.println();
            m_out.println(message("classpath"));
            return false;
        }
    }

    public void run(ScriptContext ctx) {
        if (checkJAAS() && checkJCE()) {
            status(PASS);
        } else {
            status(FAIL);
        }
    }

}
