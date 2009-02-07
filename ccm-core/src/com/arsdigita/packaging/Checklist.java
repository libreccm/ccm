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

import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.Classes;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.XML;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Checklist
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

class Checklist {

    public final static String versionId = 
            "$Id: Checklist.java 736 2005-09-01 10:46:05Z sskracic $" +
            " by $Author: sskracic $, " +
            "$DateTime: 2004/08/16 18:10:38 $";

    public static Checklist get(String pkg) {
        ClassLoader ldr = Checklist.class.getClassLoader();
        InputStream is = ldr.getResourceAsStream(pkg + ".checklist");
        if (is == null) { return null; }
        Checklist list = new Checklist(is);
        try { is.close(); }
        catch (IOException e) { throw new UncheckedWrapperException(e); }
        return list;
    }

    public static final int SCHEMA = 0;
    public static final int DATA = 1;
    public static final int STARTUP = 2;

    private List m_groups = new ArrayList();

    public Checklist(InputStream is) {
        XML.parse(is, new ChecklistHandler());
    }

    public boolean run(int type, ScriptContext ctx) {
        boolean result = true;
        for (Iterator it = m_groups.iterator(); it.hasNext(); ) {
            Group group = (Group) it.next();
            if (group.getType() == type) {
                result &= group.run(ctx);
            }
        }
        return result;
    }

    private class Group {

        private int m_type;
        private List m_checks;

        public Group(int type) {
            m_type = type;
            m_checks = new ArrayList();
        }

        public int getType() {
            return m_type;
        }

        void addCheck(Check check) {
            m_checks.add(check);
        }

        public List getChecks() {
            return m_checks;
        }

        public boolean run(ScriptContext ctx) {
            for (Iterator it = m_checks.iterator(); it.hasNext(); ) {
                Check check = (Check) it.next();
                check.run(ctx);
                Check.Status status = check.getStatus();
                if (status == null) {
                    throw new IllegalStateException
                        (check.getClass().getName() + ": check failed to " +
                         "report exit status");
                }
                if (status.equals(Check.FAIL)) {
                    return false;
                }
            }

            return true;
        }

    }

    private static final String CHECKS = "checks";
    private static final String TYPE = "type";
    private static final String CHECK = "check";
    private static final String CLASS = "class";

    private class ChecklistHandler extends DefaultHandler {

        private Group m_group = null;

        public void startElement(String uri, String name, String qn,
                                 Attributes attrs) {
            if (name.equals(CHECKS)) {
                String type = attrs.getValue(uri, TYPE);
                if (type == null) {
                    throw new IllegalStateException
                        (CHECKS + " requires a " + TYPE + " attribute");
                } else if (type.equals("schema")) {
                    m_group = new Group(SCHEMA);
                } else if (type.equals("data")) {
                    m_group = new Group(DATA);
                } else if (type.equals("startup")) {
                    m_group = new Group(STARTUP);
                } else {
                    throw new IllegalStateException
                        ("unrecognized value for " + TYPE + " attribute: " +
                         type);
                }
            }

            if (name.equals(CHECK)) {
                if (m_group == null) {
                    throw new IllegalStateException
                        (CHECK + " element cannot appear " +
                         "outside of a " + CHECKS + " group");
                }

                String klass = attrs.getValue(uri, CLASS);
                if (klass == null) {
                    throw new IllegalStateException
                        (CHECK + " element requires a " + CLASS +
                         " attribute");
                }

                m_group.addCheck((Check) Classes.newInstance(klass));
            }
        }

        public void endElement(String uri, String name, String qn) {
            if (name.equals(CHECKS)) {
                m_groups.add(m_group);
                m_group = null;
            }
        }
    }

}
