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

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterInfo;
import com.arsdigita.util.parameter.ParameterReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.log4j.Logger;

/**
 * InteractiveParameterReader, implementation o ParameterReader which
 *
 * 
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt; using code by
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 * @version $Id: InteractiveParameterReader.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class InteractiveParameterReader implements ParameterReader {

    private static final Logger s_log = Logger.getLogger
        (InteractiveParameterReader.class);

    private final InputStream m_in;
    private final PrintStream m_out;
    private final BufferedReader m_lines;

    public InteractiveParameterReader(InputStream in, PrintStream out) {
        m_in = in;
        m_out = out;
        m_lines = new BufferedReader(new InputStreamReader(m_in));
    }

    public final String read(final Parameter param, final ErrorList errors) {
        if (!param.isRequired()) { return null; }
        final Object dephault = param.getDefaultValue();
        if (dephault != null) { return null; }

        m_out.println("Parameter: " + param.getName());

        final ParameterInfo info = param.getInfo();

        if (info != null) {
            String str = info.getTitle();
            if (str != null) {
                m_out.println("Title: " + str);
            }
            str = info.getPurpose();
            if (str != null) {
                m_out.println("Purpose: " + str);
            }
            str = info.getExample();
            if (str != null) {
                m_out.println("Example: " + str);
            }
            str = info.getFormat();
            if (str != null) {
                m_out.println("Format: " + str);
            }
        }

        while (true) {
            m_out.print("Value: ");
            m_out.flush();

            try {
                final String line = m_lines.readLine();

                if (line.equals("")) {
                    continue;
                } else {
                    return line;
                }
            } catch (IOException e) {
                throw new UncheckedWrapperException(e);
            }
        }
    }
}
