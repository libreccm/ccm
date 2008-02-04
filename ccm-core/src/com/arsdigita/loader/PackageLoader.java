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
package com.arsdigita.loader;

import com.arsdigita.installer.SQLLoader;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.runtime.AbstractScript;
import com.arsdigita.runtime.InteractiveParameterLoader;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.config.JavaPropertyLoader;
import com.arsdigita.util.parameter.CompoundParameterLoader;
import com.arsdigita.util.parameter.ParameterLoader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * PackageLoader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public abstract class PackageLoader extends AbstractScript {

    public final static Logger s_log = Logger.getLogger(PackageLoader.class);

    public final static String versionId = "$Id: PackageLoader.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static boolean exists(Connection conn, String table) {
        try {
            DatabaseMetaData md = conn.getMetaData();
            if (md.storesLowerCaseIdentifiers()) {
                table = table.toLowerCase();
            } else if (md.storesUpperCaseIdentifiers()) {
                table = table.toUpperCase();
            }

            ResultSet tables = md.getTables
                (null, null, table, new String[] { "TABLE" });
            try { return tables.next(); }
            finally { tables.close(); }
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    public static void requires(Connection conn, String table) {
        if (!exists(conn, table)) {
            throw new IllegalStateException("table required: " + table);
        }
    }

    public static boolean exists(Session ssn, OID oid) {
        return ssn.retrieve(oid) != null;
    }

    public static void requires(Session ssn, OID oid) {
        if (!exists(ssn, oid)) {
            throw new IllegalStateException("oid required: " + oid);
        }
    }

    public static boolean exists(Session ssn, String type) {
        return ssn.getMetadataRoot().getObjectType(type) != null;
    }

    public static void requires(Session ssn, String type) {
        if (!exists(ssn, type)) {
            throw new IllegalStateException("type required: " + type);
        }
    }

    public static void load(Connection conn, String script) {
        SQLLoader loader = new SQLLoader(conn) {
            protected Reader open(String name) {
                ClassLoader cload = getClass().getClassLoader();
                InputStream is = cload.getResourceAsStream(name);
                if (is == null) {
                    return null;
                } else {
                    s_log.info("Loading: " + name);
                    return new InputStreamReader(is);
                }
            }
        };

        loader.load(script);
        s_log.info("Loading: Done");
    }

    public static ParameterLoader loader(String[] args) {
        CompoundParameterLoader result = new CompoundParameterLoader();
        result.add(new JavaPropertyLoader(props(args)));
        result.add(new InteractiveParameterLoader(System.in, System.out));
        return result;
    }

    private static Properties props(String[] args) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(baos);
            for (int i = 0; i < args.length; i++) {
                w.write(args[i]);
                w.write("\n");
            }

            w.flush();

            Properties props = new Properties();
            props.load(new ByteArrayInputStream(baos.toByteArray()));
            return props;
        } catch (IOException e) {
            throw new UncheckedWrapperException(e);
        }
    }

}
