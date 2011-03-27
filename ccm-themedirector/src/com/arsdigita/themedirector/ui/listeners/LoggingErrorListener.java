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

package com.arsdigita.themedirector.ui.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import com.arsdigita.themedirector.util.TransformerExceptionContainer;
import javax.xml.transform.ErrorListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import javax.xml.transform.TransformerException;

/**
 *  This class listens for errors thrown by transformers and it allows
 *  the exceptions to be easily passed around from class to class
 *  so that some classes can add information while other classes read
 *  the information
 *
 *  @author Randy Graebner &lt;randyg@redhat.com&gt;
 */
public class LoggingErrorListener implements ErrorListener {
    private static final Logger s_log = 
        Logger.getLogger(LoggingErrorListener.class);
    
    private ArrayList m_warnings;
    private ArrayList m_errors;
    private ArrayList m_fatals;
    private ArrayList m_loggers;

    public LoggingErrorListener() {
        m_warnings = new ArrayList();
        m_errors = new ArrayList();
        m_fatals = new ArrayList();
        m_loggers = new ArrayList();
        addLogger(s_log);
    }

    public void addLogger(Logger logger) {
        m_loggers.add(logger);
    }

    public void removeLogger(Logger logger) {
        m_loggers.remove(logger);
    }

    public Collection getLoggers() {
        return m_loggers;
    }

    /**
     *  this returns true if no errors have been recorded to this listener
     */
    public boolean hasErrors() {
        return m_errors.size() != 0 || m_fatals.size() != 0 
            || m_warnings.size() != 0;
    }

    public void warning(TransformerException e) throws TransformerException {
        log(Level.WARN, e);
        m_warnings.add(new TransformerExceptionContainer(e));
    }

    public Collection getWarnings() {
        return m_warnings;
    }

    public void error(TransformerException e) throws TransformerException {
        log(Level.ERROR, e);
        m_errors.add(new TransformerExceptionContainer(e));
    }

    public Collection getErrors() {
        return m_errors;
    }

    public void fatalError(TransformerException e) throws TransformerException {
        log(Level.FATAL, e);
        m_fatals.add(new TransformerExceptionContainer(e));
    }

    public Collection getFatals() {
        return m_fatals;
    }

    private void log(Level level, TransformerException ex) {
        // this could probably be sped up by making use of arrays and iterating
        // through them but since this is not a function that happens
        // very often, we have opted for simplicity over speed.
        Iterator iter = m_loggers.iterator();
        String message = "Transformer " + level + ": " +
            ex.getLocationAsString() + ": " + ex.getMessage();
        
        while (iter.hasNext()) {
            ((Logger)iter.next()).log(level, message, ex);
        }
    }
}
