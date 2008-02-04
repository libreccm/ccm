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
package com.arsdigita.logging;


import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;


public class ErrorReportAppender extends FileAppender {
    
    private String m_directory;

    public ErrorReportAppender(String directory) {
        m_directory = directory;

        setLayout(new Layout() {
                public void activateOptions() {}
                public boolean ignoresThrowable() { return true; }
                
                public String format(LoggingEvent event) {
                    return ((ErrorReport)event.getMessage()).getReport();
                }
            });
        setName("ErrorReportAppender");
    }

    public void append(LoggingEvent event) {
        if (event.getMessage() instanceof ErrorReport) {
            ErrorReport report = (ErrorReport)event.getMessage();

            setFile(m_directory + "/" + report.getGuruMeditationCode() + ".txt");
            setAppend(false);
            setBufferedIO(false);
            activateOptions();

            super.append(event);
        }
    }

}
