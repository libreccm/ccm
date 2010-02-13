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

import com.arsdigita.util.parameter.ParameterContext;

/**
 * The Script interface provides an extension point for CCM Developers
 * to write paramaterizable chunks of code intended for execution in
 * various contexts. The Script interface builds on the {@link
 * com.arsdigita.util.parameter parameter} system in order to allow a
 * context independent means for Script implementers to access
 * parameter values. Implementors of the Script interface should
 * extend the {@link AbstractScript} class.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: Script.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public interface Script extends ParameterContext {

    /**
     * This method is invoked in order to execute the script. The
     * implementor of the script should use the persistent session
     * provided by the given ScriptContext.
     *
     * @param context The context in which the script is to execute.
     **/

    public void run(ScriptContext context);

}
