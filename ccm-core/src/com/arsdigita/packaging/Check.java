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
package com.arsdigita.packaging;

import com.arsdigita.runtime.AbstractScript;

/**
 * The Check class provides a developer callback for performing
 * validation checks during various points in the package loading and
 * server startup process. This functionality is accessed by creating
 * a <i>package-key</i>.checklist file in the src dir of the package
 * that wishes to add checks. This must be a valid xml file that
 * conforms to the following format:
 *
 * <blockquote><pre>
 * &lt;checklist&gt;
 *   &lt;checks type="schema"&gt;
 *     &lt;check class="com.example.Check1"/&gt;
 *     &lt;check class="com.example.Check2"/&gt;
 *     ...
 *   &lt;/checks&gt;
 *   &lt;checks type="data"&gt;
 *     ...
 *   &lt;/checks&gt;
 *   &lt;checks type="startup"&gt;
 *     ...
 *   &lt;/checks&gt;
 * &lt;/checklist&gt;
 * </pre></blockquote>
 *
 * Checks of type "schema" will be run before the schema for a package
 * is loaded. Checks of type "data" will be run before the data for a
 * package is loaded. Checks of type "startup" will be run before the
 * server is started. All these checks will be run by the "ccm status"
 * command.
 *
 * The classes referred to from the checklist file must be concrete
 * subclasses of the Check class. They must also provide a public
 * noargs constructor. A check is performed by first creating an
 * instance of the specified class using the public noargs constructor
 * and then invoking the {@link #run(ScriptContext)} method. The check
 * must report success or failure using the {@link #status(Status)}
 * method.
 *
 * Checks will be invoked in the order in which they appear inside the
 * <checks> tags. If any one of the checks fail, the remaining checks
 * that appear within the containing <checks> tags will not be
 * performed.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public abstract class Check extends AbstractScript {

    public final static String versionId = "$Id: Check.java 736 2005-09-01 10:46:05Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static final class Status {

        private String m_name;

        private Status(String name) {
            m_name = name;
        }

        public String toString() {
            return m_name;
        }

    };

    public static final Status PASS = new Status("pass");
    public static final Status FAIL = new Status("fail");
    public static final Status WARN = new Status("warn");

    private Status m_status = null;

    /**
     * Used by subclasses to report the result of the Check. This
     * method can be called at most once by a particular instance of
     * the Check class.
     *
     * @param status The status, one of PASS, FAIL, or WARN.
     **/

    protected void status(Status status) {
        if (m_status == null) {
            m_status = status;
        } else {
            throw new IllegalStateException("status called twice");
        }
    }

    /**
     * Accesses the status of the previous invocation of the {@link
     * #run(ScriptContext)} method.
     *
     * @return The status, one of PASS, FAIL, or WARN.
     **/

    public Status getStatus() {
        return m_status;
    }

}
