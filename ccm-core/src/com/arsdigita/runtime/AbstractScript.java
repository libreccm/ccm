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

import com.arsdigita.util.parameter.AbstractParameterContext;

/**
 * The AbstractScript class serves as a base class for scripts
 * intended to execute within the runtime environment provided by CCM.
 * A developer may write one of these scripts by extending
 * AbstractScript and providing a noargs constructor that registers
 * any {@link com.arsdigita.util.parameter parameters} required by the
 * script's run method.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: AbstractScript.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public abstract class AbstractScript extends AbstractParameterContext
    implements Script {

    /**
     * Default constructor for derived classes.
     **/

    protected AbstractScript() {}

    /**
     * The run method is inoked to execute the script. Before calling
     * this method any required parameters registered by the noargs
     * constructer should be set.
     *
     * @param context the context in which to run the script
     **/

    public abstract void run(ScriptContext context);

}
