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
package com.arsdigita.developersupport;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * <p>A helper class that lets you dump the contents of a table into a file in XML
 * format.</p>
 *
 * <p>Note that this uses direct JDBC calls bypassing persistence. Therefore, no
 * additional persistence flushes are triggered by this class.  The current API
 * is very primitive.</p>
 *
 * <p>Example usage:</p>
 *
 * <pre>
 * // specify the output file name
 * SQLDebugger.startNewFile("txns");
 * 
 * // select the specified columns from a table
 * // and append the result set to /tmp/debug.xml
 * SQLDebugger.dumpTable("after initializing obj and compObj",
 *                 new String[] {"transaction_id", "master_id", "object_id", "tag"},
 *                 "vcx_transactions");
 * 
 * SQLDebugger.dumpTable("after initializing obj and compObj",
 *                 new String[] {"id", "j_big_integer", "j_big_decimal", "j_string"},
 *                 "tx_versioned_datatypes");
 * 
 * // do some computations and dump the data again
 * SQLDebugger.dumpTable("after setting obj to initVersionedTstObj",
 *                 new String[] {"transaction_id", "master_id", "object_id", "tag"},
 *                 "vcx_transactions");
 * 
 * SQLDebugger.dumpTable("after setting obj to initVersionedTstObj",
 *                 new String[] {"id", "j_big_integer", "j_big_decimal", "j_string"},
 *                 "tx_versioned_datatypes");
 * 
 * // close the file
 * SQLDebugger.close();
 * </pre>
 *
 * <p>The .xml files thus generated can be converted to HTML for easier viewing
 * by using the <a href="doc-files/sql-debugger.sh">sql-debugger.sh</a> script.
 * The script relies on the <a
 * href="doc-files/sql-debugger.xsl">sql-debugger.xsl</a> stylesheet.  and
 * requires <code>xsltproc</code> to be installed on your system.</p>
 *
 * <p>Some of the known numerous shortcomings:</p>
 *
 * <ol>
 *   <li>Doesn't escape the selected values to ensure the well-formedness
 *    of the XML output.</li>
 * </ol>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003/02/06
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/
public final class SQLDebugger {

    private static final Logger s_log = Logger.getLogger(SQLDebugger.class);

    private static final ThreadLocal s_debug = new ThreadLocal() {
            public Object initialValue() {
                return new SQLDebugger();
            }
        };

    private PrintWriter m_writer;

    private SQLDebugger() {}

    private void setWriter(PrintWriter writer) {
        Assert.exists(writer, PrintWriter.class);
        m_writer = writer;
        m_writer.println("<?xml version=\"1.0\"?>");
        m_writer.println("<debug>");
    }

    private PrintWriter getWriter() {
        Assert.exists(m_writer, PrintWriter.class);
        return m_writer;
    }

    private static SQLDebugger debugger() {
        return (SQLDebugger) s_debug.get();
    }

    private static void setOutputFile(File outputFile) {
        try {
            debugger().setWriter
                (new PrintWriter
                 (new OutputStreamWriter
                  (new FileOutputStream(outputFile), "UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new UncheckedWrapperException("This can't happen.", ex);
        } catch (IOException ex) {
            throw new UncheckedWrapperException
                ("Error opening '" + outputFile.getAbsolutePath() + "'", ex);
        }
    }

    /**
     * Starts a new file for debug output. The file name will start with the
     * specified <code>prefix</code>. The file is created in the directory
     * specified by the system property <code>"java.io.tmpdir"</code>.
     *
     * @see java.io.File#createTempFile(String, String)
     * @param prefix the prefix (initial characters) for the new file name. Must
     * be at least three characters long.
     **/
    public static void startNewFile(String prefix) {
        Assert.exists(prefix, String.class);
        Assert.truth(prefix.length() > 2,
                     "'" + prefix + "' is at least 3 characters long.");

        if ( debugger().m_writer != null ) {
            close();
        }
        try {
            File out = File.createTempFile(prefix, ".xml", getTmpDir());
            s_log.debug("starting new file: " + out.getAbsolutePath());
            setOutputFile(out);
        } catch (IOException ex) {
            throw new UncheckedWrapperException
                ("Error opening a debug file prefixed with " + prefix, ex);
        }
    }

    private static File getTmpDir() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if ( tmpDir == null || "".equals(tmpDir.trim()) ) {
            tmpDir = "/tmp";
        }
        File result = new File(tmpDir);
        Assert.truth(result.isDirectory(), tmpDir + " is a directory");
        return result;
    }

    /**
     * Closes the output file previously opened with {@link
     * #startNewFile(String)}.  If you forget to call this method or if the
     * application crashes before this method is called, the output file will
     * miss a closing tag, thus rendering it ill-formed. You can add the missing
     * tag manually, if you have to.
     **/ 
    public static void close() {
        println("</debug>");
        debugger().getWriter().close();
        debugger().m_writer = null;
    }

    private static void print(String str) {
        debugger().getWriter().print(str);
    }

    private static void println(String str) {
        debugger().getWriter().println(str);
    }

    private static void flush() {
        debugger().getWriter().flush();
    }

    /**
     * @see #dumpQuery(String, String[], String)
     **/
    public static void dumpTable(String debugMsg,
                                 String[] columns,
                                 String tableName) {

        dumpQuery(debugMsg, columns, makeQuery(columns, tableName));
    }

    /**
     * Runs the query <code>sqlQuery</code> and dumps the result to the print
     * writer in the following format:
     *
     * <pre>
     * &lt;tableName>
     *   &lt;row>
     *     &lt;column0>value00&lt;/column0>
     *     &lt;column1>value01&lt;/column1>
     *     ...
     *   &lt;/row>
     *   &lt;row>
     *     &lt;column0>value10&lt;/column0>
     *     &lt;column1>value11&lt;/column1>
     *     ...
     *   &lt;/row>
     *   ...
     * &lt;/tableName>
     * </pre>
     *
     * @param debugMsg a short message that explains the purpose or
     * circumstances in which the results of this query were dumped.
     * @param columns the names of columns returned by the query
     * @param sqlQuery the query to execute
     **/
    public static void dumpQuery(String debugMsg,
                                 String[] columns,
                                 String sqlQuery) {

        try {
            Connection conn = SessionManager.getSession().getConnection();
            PreparedStatement st = conn.prepareStatement(sqlQuery);
            ResultSet rs = st.executeQuery();
            StringBuffer row = new StringBuffer();
            println("<queryDump>");
            println("  <message>" + debugMsg + "</message>");
            println("  <timestamp>" + new Date() + "</timestamp>");
            println("  <query>" + sqlQuery + "</query>");
            println("  <header>");
            for (int ii=0; ii<columns.length; ii++) {
                println("    <column>" + columns[ii] + "</column>");
            }
            println("  </header>");

            while ( rs.next() ) {
                println("  <row>");
                for (int ii=0; ii<columns.length; ii++) {
                    row = new StringBuffer();
                    String columnName = columns[ii];
                    row.append("    <column>");
                    row.append(rs.getObject(columnName));
                    row.append("</column>");
                    println(row.toString());
                }
                println("  </row>");
            }
            println("</queryDump>");
            flush();
            rs.close();
            st.close();
        } catch (SQLException ex) {
            s_log.error("debugMsg: '" + debugMsg + "', query='" + sqlQuery + "'",
                        ex);
        }
    }

    private static String makeQuery(String[] columns, String tableName) {
        StringBuffer query = new StringBuffer();
        query.append("select\n  ");
        for (int ii=0; ii<columns.length; ii++) {
            query.append(columns[ii]);
            if ( ii < columns.length-1 ) {
                query.append(", ");
            }
        }
        query.append("\nfrom ").append(tableName);
        return query.toString();
    }
}
