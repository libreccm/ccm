/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.SchemaLoader;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.jdbc.Connections;
import com.arsdigita.xml.XML;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Upgrade
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Upgrade.java 736 2005-09-01 10:46:05Z sskracic $
 */
class Upgrade extends Command {

    private static final Logger logger = Logger.getLogger(Upgrade.class);
    private static final Options s_options = getOptions();

    private String m_from;
    private String m_to;
    private final List m_scripts;

    static {
        logger.debug("Static initalizer starting...");
        s_options.addOption
            (OptionBuilder
             .isRequired()
             .hasArg()
             .withLongOpt("from-version")
             .withDescription("Upgrade from version VERSION")
             .create());
        s_options.addOption
            (OptionBuilder
             .isRequired()
             .hasArg()
             .withLongOpt("to-version")
             .withDescription("Upgrade to version VERSION")
             .create());
        s_options.addOption
            (OptionBuilder
             .hasArg()
             .withLongOpt("parameters")
             .withDescription("Parameters to pass to upgrade scripts")
             .create());
        logger.debug("Static initalizer finished.");
    }

    public Upgrade() {
        super("upgrade",
              "Upgrade a CCM package");

        m_scripts = new ArrayList();
    }

    public boolean run(final String[] args) {
        final CommandLine line;

        if (args.length == 0) {
            usage(s_options, System.err, "PACKAGE-KEY");
            return false;
        }

        try {
            line = new PosixParser().parse(s_options, args);
        } catch (MissingOptionException moe) {
            System.err.println("Missing option " + moe.getMessage());
            usage(s_options, System.err, "PACKAGE-KEY");
            return false;
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return false;
        }

        if (line.hasOption("usage") || line.hasOption("help")) {
            usage(s_options, System.out, "PACKAGE-KEY");
            return true;
        }

        final String home = System.getProperty("ccm.home");

        if (home == null) {
            System.err.println("ccm.home system property is undefined");
            return false;
        }

        final String[] keys = line.getArgs();

        if (keys.length == 0 || keys.length > 1) {
            usage(s_options, System.err, "PACKAGE-KEY");
            return false;
        }

        final String key = keys[0];
        m_from = line.getOptionValue("from-version");
        m_to = line.getOptionValue("to-version");

        if (m_from == null || m_to == null) {
            usage(s_options, System.err, "PACKAGE-KEY");
            return false;
        }

        final String spec = key + ".upgrade";

        final InputStream in = Thread.currentThread().getContextClassLoader
            ().getResourceAsStream(spec);

        if (in == null) {
            System.err.println("Cannot find " + spec);
            return false;
        }

        XML.parse(in, new Parser());

        if (m_scripts.isEmpty()) {
            System.err.println("No appropriate upgrades found; make sure " +
                               "that your 'to' and 'from' versions match " +
                               "the intended upgrade exactly");
            return false;
        } else {
            System.out.println("Number of scripts: " + m_scripts.size() );
        }

        Iterator iter = m_scripts.iterator();
        while (iter.hasNext()) {

            final String[] parts = (String[]) iter.next();

            final String classname = parts[0];
            final String sql = parts[1];

            if (classname != null) {
                final Class clacc = Classes.loadClass(classname);

                System.out.println("Running Java upgrade " + clacc);

                final Method method;

                try {
                    method = clacc.getMethod("main",
                                             new Class[] {String[].class});
                } catch (NoSuchMethodException nsme) {
                    throw new UncheckedWrapperException(nsme);
                } catch (SecurityException se) {
                    throw new UncheckedWrapperException(se);
                }

                String[] params = line.getOptionValues("parameters");
                LinkedList ll = new LinkedList();
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        String[] split = StringUtils.split(params[i],',');
                        for (int j = 0; j < split.length; j++) {
                            ll.add(split[j]);
                        }
                    }
                }

                try {
                    method.invoke(null, new Object[] {ll.toArray(new String[] {})});
                } catch (IllegalAccessException iae) {
                    throw new UncheckedWrapperException(iae);
                } catch (InvocationTargetException ite) {
                    throw new UncheckedWrapperException(ite);
                }

            } else if (sql != null) {
                final SchemaLoader loader = new SchemaLoader(sql);

                System.out.println("Running SQL upgrade " + loader + ", " +
                                   "loaded from the classpath");

                final Connection conn = Connections.acquire
                    (RuntimeConfig.getConfig().getJDBCURL());

                loader.run(conn);

                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new UncheckedWrapperException(e);
                }
            } else {
                throw new IllegalStateException();
            }

        }

        return true;
    }

    private class Parser extends DefaultHandler {
        private String m_version;

        @Override
        public final void startElement(final String uri,
                                       final String lname,
                                       final String qname,
                                       final Attributes attrs) {
            if (lname.equals("version")) {
                final String from = attrs.getValue(uri, "from");
                final String to = attrs.getValue(uri, "to");

                Assert.exists(from);
                Assert.exists(to);

                m_version = from + "/" + to;
            }

            if (lname.equals("script")) {
                Assert.exists(m_version);
                Assert.exists(m_from);
                Assert.exists(m_to);

                if (m_version.equals(m_from + "/" + m_to)) {
                    final String classname = attrs.getValue(uri, "class");
                    final String sql = attrs.getValue(uri, "sql");

                    if (classname == null && sql == null
                            || classname != null && sql != null) {
                        throw new IllegalArgumentException
                            ("The script element must have a 'class' " +
                             "argument or a 'sql' argument; it may not " +
                             "have both");
                    }

                    m_scripts.add(new String[] {classname, sql});
                }
            }
        }

        @Override
        public final void endElement(final String uri,
                                     final String lname,
                                     final String qname) {
            if (qname.equals("version")) {
                m_version = null;
            }
        }
    }
}
