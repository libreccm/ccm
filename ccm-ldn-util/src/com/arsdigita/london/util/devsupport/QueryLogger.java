/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.london.util.devsupport;

import com.arsdigita.webdevsupport.QueryInfo;
import com.arsdigita.webdevsupport.QueryLog;
import com.arsdigita.developersupport.DeveloperSupportListener;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

public class QueryLogger extends DeveloperSupportListener {

    private final static Logger s_log = Logger.getLogger(QueryLogger.class);

    private int m_id;
    private boolean m_captureStackTrace;
    private Writer m_writer;
    private File m_directory;

    public QueryLogger(String directory) {
        this(new File(directory));
    }
    
    public QueryLogger(File directory) {
        m_directory = directory;
    }

    public void setCaptureStackTrace(boolean capture) {
        m_captureStackTrace = capture;
    }
    
    public void requestStart(Object request) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-DD-HH-mm-ss");
        

        File log = new File(m_directory, 
                            "log-" +
                            request.getClass().getName() + 
                            fmt.format(new Date()) + ".sql");
        
        try {
            m_writer = new FileWriter(log);
            m_writer.write("begin;\n\n");
        } catch (IOException ex) {
            throw new UncheckedWrapperException("Cannot open file " + log, ex);
        }
    }

    public void requestEnd(Object request) {
        try {
            m_writer.close();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("Cannot close file", ex);
        }
        m_writer = null;
    }

    public void logQuery(String connection_id,
                         String type,
                         String query,
                         HashMap bindvars,
                         long time,
                         java.sql.SQLException sqle) {
        if (m_writer == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Query " + query + " executed outside of request");
            } else {
                s_log.warn("Query executed outside of request");
            }
            return;
        } 
        try {
            m_writer.write("-- ID: #" + m_id + "\n");
            m_writer.write("-- Duration: " + time + "ms\n");
            if (m_captureStackTrace) {
                StringWriter sw = new StringWriter();
                new Throwable("trace").printStackTrace(new PrintWriter(sw));
                Perl5Util perl5 = new Perl5Util();
                m_writer.write("-- Stack trace: " + 
                               perl5.substitute("s/\n/\n--/g", sw.toString()) +
                               "\n");
            }
            m_writer.write(QueryLog.substituteSQL(
                                  new QueryInfo(m_id++, 
                                                connection_id, 
                                                type, 
                                                query,
                                                bindvars, 
                                                time, 
                                                sqle)) + ";\n\n\n");
        } catch (IOException ex) {
            throw new UncheckedWrapperException("damn", ex);
        }
    }
}
