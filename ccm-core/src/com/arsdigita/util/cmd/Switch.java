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

import com.arsdigita.util.Assert;
import java.util.List;

/**
 * @deprecated Use CLI (http://jakarta.apache.org/commons/cli/index.html) instead.
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

public abstract class Switch {

    public final static String versionId = "$Id: Switch.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static abstract class Type {
        abstract String[] parse(CommandLine cmd, List args);
    }

    public static final Type FLAG = new Type() {
            String[] parse(CommandLine cmd, List args) {
                return null;
            }
        };
    public static final Type PARAMETER = new Type() {
            String[] parse(CommandLine cmd, List args) {
                return new String[] { (String) args.remove(0) };
            }
        };

    private String m_name;
    private Type m_type;
    private String m_usage;
    private Object m_default;

    protected Switch(String name, Type type, String usage, Object defValue) {
        m_name = name;
        m_type = type;
        m_usage = usage;
        m_default = defValue;
    }

    public String getName() {
        return m_name;
    }

    public Type getType() {
        return m_type;
    }

    public String getUsage() {
        return m_usage;
    }

    public String usage() {
        String line = "    " + m_name;
        StringBuffer result = new StringBuffer(line);
        for (int i = 0; i < 26 - line.length(); i++) {
            result.append(' ');
        }
        result.append(m_usage);
        return result.toString();
    }

    public Object getDefault() {
        return m_default;
    }

    Object parse(CommandLine cmd, List args) {
        Assert.assertEquals(m_name, args.get(0));
        args.remove(0);

        String[] values = m_type.parse(cmd, args);
        return decode(values);
    }

    protected abstract Object decode(String[] values);

}
