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
package com.redhat.persistence.profiler.rdbms;

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.redhat.persistence.Event;
import com.redhat.persistence.Signature;
import com.redhat.persistence.engine.rdbms.RDBMSProfiler;
import com.redhat.persistence.engine.rdbms.RDBMSStatement;
import com.redhat.persistence.engine.rdbms.StatementLifecycle;
import com.redhat.persistence.metadata.ObjectType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class StatementProfiler implements RDBMSProfiler {
    private boolean m_isEnabled;
    private PrintWriter m_out;
    private final ArrayList m_texts;
    private final ArrayList m_tables;
    private final ArrayList m_types;
    private boolean m_inPhase;

    public StatementProfiler() {
        m_isEnabled = false;
        m_texts = new ArrayList();
        m_tables = new ArrayList();
        m_types = new ArrayList();
        m_inPhase = false;
    }

    public StatementLifecycle getLifecycle(final Connection conn, final RDBMSStatement statement) {
        if (m_isEnabled) {
            Assert.exists(m_out, PrintWriter.class);

            final String text = statement.getText();

            if (!m_texts.contains(text)) {
                m_texts.add(text);
            }

            SQLSummary summary = SQLSummary.get(text);
            String[] tables = summary.getTables();
            for (int i = 0; i < tables.length; i++) {
                if (!m_tables.contains(tables[i])) {
                    m_tables.add(tables[i]);
                }
            }

            ObjectType type = null;
            Signature sig = statement.getSignature();
            if (sig != null) {
                type = sig.getObjectType();
            } else {
                for (Iterator it = statement.getEvents().iterator();
                     it.hasNext(); ) {
                    Event ev = (Event) it.next();
                    type = ev.getSession().getObjectType(ev.getObject());
                    break;
                }
            }

            if (type != null && !m_types.contains(type)) {
                m_types.add(type);
            }

            final int textid = m_texts.indexOf(text);

            final Lifecycle lifecycle = new Lifecycle
                (statement, textid, summary, type);

            return lifecycle;
        } else {
            return null;
        }
    }

    public void start() {
        if (Assert.isEnabled()) {
            Assert.truth(m_out == null);
        }

        try {
            m_out = new PrintWriter
                (new BufferedWriter(new FileWriter(file())));
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }

        m_out.write("<?xml version=\"1.0\"?>");
        m_out.write("<profile>");

        m_isEnabled = true;
    }

    public void stop() {
        Assert.exists(m_out, PrintWriter.class);

        try {
            m_isEnabled = false;

            final Iterator texts = m_texts.iterator();

            for (int i = 0; texts.hasNext(); i++) {
                final String text = (String) texts.next();

                m_out.write("\n");
                m_out.write("<text id=\"" + i + "\">");
                m_out.write("<![CDATA[" + text + "]]>");
                m_out.write("</text>");
            }

            for (Iterator it = m_types.iterator(); it.hasNext(); ) {
                ObjectType ot = (ObjectType) it.next();
                m_out.write("\n");
                m_out.write("<type>");
                m_out.write(ot.getQualifiedName());
                m_out.write("</type>");
            }

            for (Iterator it = m_tables.iterator(); it.hasNext(); ) {
                m_out.write("\n");
                m_out.write("<table>");
                m_out.write((String) it.next());
                m_out.write("</table>");
            }

            m_out.write("\n");
            m_out.write("</profile>");
            m_out.flush();
        } finally {
            m_out.close();
            m_out = null;
        }
    }

    //
    // Private utility classes and methods
    //

    private File file() {
        try {
            return File.createTempFile("profile", ".xml");
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    private void elem(final String tag, final String value) {
        m_out.write("<" + tag + ">" + value + "</" + tag + ">");
    }

    private final class Lifecycle implements StatementLifecycle {
        private final RDBMSStatement m_statement;
        private final int m_text;
        private final SQLSummary m_summary;
        private final ObjectType m_type;
        private long m_begin;

        Lifecycle(final RDBMSStatement statement,
                  final int text, final SQLSummary summary,
                  final ObjectType type) {
            m_statement = statement;
            m_text = text;
            m_summary = summary;
            m_type = type;
        }

        public void beginPrepare() {
            m_out.write("\n");
            m_out.write("<statement");
            m_out.write(" text=\"" + m_text + "\"");
            m_out.write(" type=\"" + m_summary.getType() + "\"");
            m_out.write(">");

            if (m_type != null) {
                elem("objectType", m_type.getQualifiedName());
            }

            final String[] tables = m_summary.getTables();

            for (int i = 0; i < tables.length; i++) {
                elem("table", tables[i]);
            }

            //elem("query", "<![CDATA[" + m_statement.getQuery() + "]]>");

            final Iterator events = m_statement.getEvents().iterator();

            while (events.hasNext()) {
                final Event event = (Event) events.next();

                elem("event", "<![CDATA[" + event.toString() + "]]>");
            }

            m_out.write("<lifecycle>");

            begin("prepare");
        }

        public void endPrepare() {
            end("prepare");
        }

        public void endPrepare(SQLException e) {
            end("prepare", e);
        }

        public void beginSet(final int pos, final int type,
                             final Object object) {
            begin("set");

            elem("pos", Integer.toString(pos));
            elem("type", Integer.toString(type));
        }

        public void endSet() {
            end("set");
        }

        public void endSet(SQLException e) {
            end("set", e);
        }

        public void beginExecute() {
            begin("execute");
        }

        public void endExecute(final int updateCount) {
            m_out.write("<updated>");
            m_out.write(Integer.toString(updateCount));
            m_out.write("</updated>");

            end("execute");
        }

        public void endExecute(SQLException e) {
            end("execute", e);
        }

        public void beginNext() {
            begin("next");
        }

        public void endNext(final boolean hasMore) {
            end("next");
        }

        public void endNext(SQLException e) {
            end("next", e);
        }

        public void beginGet(final String column) {
            begin("get");

            elem("column", column);
        }

        public void endGet(final Object result) {
            // XXX Do something with result

            end("get");
        }

        public void endGet(SQLException e) {
            end("get", e);
        }

        public void beginClose() {
            begin("close");
        }

        public void endClose() {
            end("close");

            m_out.write("</lifecycle>");
            m_out.write("</statement>");
        }

        public void endClose(SQLException e) {
            end("close", e);

            m_out.write("</lifecycle>");
            m_out.write("</statement>");
        }

        //
        // Private utility classes and methods
        //

        private long now() {
            return System.currentTimeMillis();
        }

        private void begin() {
            m_begin = now();
        }

        private void begin(final String tag) {
            if (m_inPhase) {
                throw new IllegalStateException("nested begin");
            }
            m_inPhase = true;

            begin();

            m_out.write("<" + tag + ">");
        }

        private long end() {
            if (!m_inPhase) {
                throw new IllegalStateException("end called without begin");
            }
            m_inPhase = false;

            final long elapsed = now() - m_begin;

            return elapsed;
        }

        private void end(final String tag, SQLException e) {
            elem("millis", Long.toString(end()));

            if (e != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                elem("exception", sw.toString());
            }

            m_out.write("</" + tag + ">");
        }

        private void end(final String tag) {
            end(tag, null);
        }
    }
}
