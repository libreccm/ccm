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
package com.arsdigita.web;

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.AbstractScript;
// InteractiveParameterLoader extents ParameterLoader, which is deprecated.
// InteractiveParameterReader should be used instead.
// import com.arsdigita.runtime.InteractiveParameterLoader;
import com.arsdigita.runtime.InteractiveParameterReader;
import com.arsdigita.runtime.Script;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.runtime.Startup;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
// ParameterLoader is deprecated, use ParameterReader instead.
// import com.arsdigita.util.parameter.ParameterLoader;
import com.arsdigita.util.parameter.ParameterReader;
import com.arsdigita.util.parameter.StringParameter;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: HostLoader.java 738 2005-09-01 12:36:52Z sskracic $
 */
public final class HostLoader extends AbstractScript {

    private static final Logger s_log = Logger.getLogger(HostLoader.class);

    private final Parameter m_name;
    private final Parameter m_port;

    public HostLoader() {
        m_name = new StringParameter("name");
        m_port = new IntegerParameter("port");

        register(m_name);
        register(m_port);
    }

    private final String getName() {
        return (String) get(m_name);
    }

    private final int getPort() {
        return ((Integer) get(m_port)).intValue();
    }

    public final void run(final ScriptContext context) {
	Host.create(getName(), getPort());
    }

    public static final void main(final String[] args) {
	new Startup().run();

	final Session session = SessionManager.getSession();
	// final ParameterLoader loader = new InteractiveParameterLoader
	//     (System.in, System.out);
	final ParameterReader reader = new InteractiveParameterReader
	    (System.in, System.out);

	final TransactionContext transaction = session.getTransactionContext();
	transaction.beginTxn();

        Script script = new HostLoader();
        // script.load(loader, new ErrorList());
        // script.run(new ScriptContext(session, loader));
        script.load(reader, new ErrorList());
        script.run(new ScriptContext(session, reader));

	transaction.commitTxn();
    }

}
