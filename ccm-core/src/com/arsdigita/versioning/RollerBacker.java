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
package com.arsdigita.versioning;

import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/**
 * <p>Logs operations performed during rollback.  The output goes to the writer
 * specified via {@link #setWriter(Writer)}.</p>
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-07-02
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/
final class RollerBacker implements VersioningServlet.RollbackLogger {
    private final static Logger s_log = Logger.getLogger(RollerBacker.class);

    private final OID m_oid;
    private final BigInteger m_txnID;
    private final RLImpl m_rbl;

    RollerBacker(OID oid, BigInteger txnID) {
        m_oid = oid;
        m_txnID = txnID;
        m_rbl = new RLImpl();
    };

    public OID getOID() {
        return m_oid;
    }

    public BigInteger getTxnID() {
        return m_txnID;
    }

    public void setWriter(Writer writer) {
        m_rbl.setWriter(writer);
    }

    public void rollback() {
        if ( m_rbl.getWriter() == null ) {
            throw new IllegalStateException("no writer");
        }
        Versions.rollback(m_oid, m_txnID, m_rbl);
    }

    public void printException(Throwable throwable) throws ServletException {
        PrintWriter writer = new PrintWriter(m_rbl.getWriter(), true);
        writer.println("");
        writer.println("-----====== EXCEPTION RAISED ======------");
        writer.println("");
        writer.println("Last event logged at at:");
        m_rbl.getLastStack().printStackTrace(writer);
        writer.println("");
        writer.println("Exception raised:");
        throwable.printStackTrace(writer);
        writer.close();
        throw new ServletException(throwable);
    }


    private static class RLImpl implements RollbackListener {
        final static String LINESEP = System.getProperty("line.separator");
        final static String INDENT = "  ";

        private final Throwable m_throwable;
        private int m_level;

        public RLImpl() {
            m_throwable = new Throwable();
            m_level = 0;
        }

        private Writer m_writer;        

        public void setWriter(Writer writer) {
            m_writer = writer;
        }

        public Writer getWriter() {
            return m_writer;
        }

        private RLImpl indent(Object msg) {
            if ( s_log.isDebugEnabled() ) { s_log.debug(msg); }

            try {
                for (int ii=0; ii<m_level; ii++) {
                    m_writer.write(INDENT);
                }
                m_writer.write(String.valueOf(msg));
            } catch (IOException ex) {
                throw new UncheckedWrapperException("can't write", ex);
            }
            return this;
        }

        private RLImpl indentln(Object msg) {
            if ( s_log.isDebugEnabled() ) { s_log.debug(msg); }

            return indent(msg).writeln("");
        }

        private RLImpl deeperIndent(Object msg) {
            return write(INDENT).indent(msg);
        }

        private RLImpl write(Object msg) {
            try {
                m_writer.write(String.valueOf(msg));
            } catch (IOException ex) {
                throw new UncheckedWrapperException("can't write", ex);
            }
            return this;
        }

        private RLImpl writeln(Object msg) {
            try {
                m_writer.write(String.valueOf(msg));
                m_writer.write(LINESEP);
                m_throwable.fillInStackTrace();
            } catch (IOException ex) {
                throw new UncheckedWrapperException("Can't write", ex);
            }
            return this;
        }

        public Throwable getLastStack() {
            return m_throwable;
        }

        /*
         * Implementation of the RollbackListener interface.
         */

        public void onStart() {
            indentln("onStart");
            m_level++;
        }

        public void onFinish() {
            m_level--;
            indentln("onFinish: ");
        }

        public void onEnqueue(OID oid) {
            indent("onEnqueue: ").writeln(oid);
        }

        public void onDequeue(OID oid) {
            indent("onDequeue: ").writeln(oid);
        }

        public void onUndoSet(OID oid, String property, Object value) {
            deeperIndent("onUndoSet: oid=").write(oid);
            write(", property=").write(property);
            write(", value=").writeln(value);
        }

        public void onUndoAdd(OID oid, String property, Object value) {
            deeperIndent("onUndoAdd: oid=").write(oid);
            write(", property=").write(property);
            write(", value=").writeln(value);
        }

        public void onUndoRemove(OID oid, String property, Object value) {
            indent("onUndoRemove: oid=").write(oid).write(", property=");
            write(property).writeln(", value=" + value);
        }

        public void onDiffStart() {
            indentln("onDiffStart");
            m_level++;
        }

        public void onDiffFinish() {
            m_level--;
            indentln("onDiffFinish");
        }

        public void onReifyStart() {
            indentln("onReifyStart");
            m_level++;
        }

        public void onReifyFinish() {
            m_level--;
            indentln("onReifyFinish");
        }

        public void onReifyStart(OID oid, String state) {
            indent("onReifyStart: oid=").write(oid).write(", state=");
            writeln(state);
        }

        public void onReifyFinish(OID oid, String state) {
            indent("onReifyFinish: oid=").write(oid).write(", state=");
            writeln(state);
        }

        public void onApplyStart() {
            indentln("onApplyStart");
            m_level++;
        }

        public void onApplyFinish() {
            m_level--;
            indentln("onApplyFinish");
        }

        public void onApplyStart(OID oid, String state) {
            indent("onApplyStart, oid=").write(oid).write(", state=");
            writeln(state);
        }

        public void onApplyFinish(OID oid) {
            indent("onApplyFinish, oid=").writeln(oid);
        }

        public void onSet(OID oid, String property, Object value) {
            deeperIndent("onSet: oid=").write(oid).write(", property=");
            write(property).write(", value=").writeln(value);
        }

        public void onAdd(OID oid, String property, Object value) {
            deeperIndent("onAdd: oid=").write(oid).write(", property=");
            write(property).write(", value=").writeln(value);
        }

        public void onRemove(OID oid, String property, Object value) {
            deeperIndent("onRemove: oid=").write(oid).write(", property=");
            write(property).write(", value=").writeln(value);
        }

        public void onTerminalStart() {
            indentln("onTerminalStart");
            m_level++;
        }

        public void onTerminalFinish() {
            m_level--;
            indentln("onTerminalFinish");
        }

        public void onDelete(OID oid) {
            deeperIndent("onDelete: oid=").writeln(oid);
        }

        public void onCreate(OID oid) {
            deeperIndent("onCreate: oid=").writeln(oid);
        }
    }
}
