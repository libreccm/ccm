/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 * @version $Id: CommandLine.java 287 2005-02-22 00:29:02Z sskracic $
 * @deprecated Use CLI (http://jakarta.apache.org/commons/cli/index.html) instead.
 */
public class CommandLine {

    private String m_name;
    private String m_usage;
    private Map m_switches = new HashMap();

    public CommandLine(String name, String usage) {
        m_name = name;
        m_usage = usage;
    }

    public void addSwitch(Switch s) {
        m_switches.put(s.getName(), s);
    }

    public Switch getSwitch(String name) {
        return (Switch) m_switches.get(name);
    }

    public String[] parse(Map result, String[] argv) {
        for (Iterator it = m_switches.values().iterator(); it.hasNext(); ) {
            Switch s = (Switch) it.next();
            result.put(s.getName(), s.getDefault());
        }

        List args = new ArrayList();
        args.addAll(Arrays.asList(argv));
        List remaining = new ArrayList();
        while (args.size() > 0) {
            String arg = (String) args.get(0);
            if (arg.startsWith("-")) {
                if (m_switches.containsKey(arg)) {
                    Switch s = (Switch) m_switches.get(arg);
                    result.put(s.getName(), s.parse(this, args));
                } else {
                    throw new Error(usage());
                }
            } else {
                args.remove(0);
                remaining.add(arg);
            }
        }

        return (String[]) remaining.toArray(new String[0]);
    }

    public String usage() {
        StringBuffer result = new StringBuffer();

        result.append("Usage: " + m_name);

        if (m_usage != null) {
            result.append(" " + m_usage);
        }

        for (Iterator it = m_switches.values().iterator(); it.hasNext(); ) {
            Switch s = (Switch) it.next();
            result.append('\n');
            result.append(s.usage());
        }

        return result.toString();
    }

    public String usage(String name) {
        return getSwitch(name).usage();
    }

}
