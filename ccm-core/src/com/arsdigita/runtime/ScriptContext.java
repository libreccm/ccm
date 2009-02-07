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

import com.arsdigita.persistence.Session;
import com.arsdigita.util.parameter.ParameterReader;

/**
 * The ScriptContext class is passed into the {@link
 * Script#run(ScriptContext)} method in order to provide an instance
 * of the {@link Script} interface access to the proper context in
 * which to execute.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public class ScriptContext {

    public final static String versionId = 
            "$Id: ScriptContext.java 287 2005-02-22 00:29:02Z sskracic $" + 
            "by $Author: sskracic $, " +
            "$DateTime: 2004/08/16 18:10:38 $";

    private final Session m_ssn;
    private final ParameterReader m_params;

    /**
     * Constructs a new ScriptContext object with the given Session
     * and ParameterReader.
     *
     * @param ssn The persistent session to provide to the executing
     * script.
     *
     * @param params The parameter reader to provide to the executing
     * script.
     **/

    public ScriptContext(Session ssn, ParameterReader params) {
        m_ssn = ssn;
        m_params = params;
    }

    /**
     * Returns the persistent session for this ScriptContext.
     *
     * @return a persistent session
     **/

    public Session getSession() {
        return m_ssn;
    }

    /**
     * The original ParameterLoader is deprecated now. As an alternative
     * was recommended here:
     * Use {@link com.arsdigita.util.parameter.ParameterContext#load(
     *                   com.arsdigita.util.parameter.ParameterReader, 
     *                   com.arsdigita.util.parameter.ErrorList      )} instead.
     **/

    public ParameterReader getParams() {
        return m_params;
    }

}
