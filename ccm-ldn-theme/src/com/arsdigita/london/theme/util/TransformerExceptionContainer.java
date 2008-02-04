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

package com.arsdigita.london.theme.util;


import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import jd.xml.xslt.parser.XsltParseException;

/**
 *  This class wraps a TransformerException and it "preserves" its
 *  values.  This is needed because the TransformerException can
 *  have its values changes between being passed to the error listener
 *  and when the actual page is registered.  So, this class is required
 *  to "preserve" the important values for use when the page is registered.
 *  Hopefully there is a better way to do this but I do not see one at the
 *  time.
 *
 *  @author Randy Graebner &lt;randyg@redhat.com&gt;
 */
public class TransformerExceptionContainer {
    private static final Logger s_log = 
        Logger.getLogger(TransformerExceptionContainer.class);

    private TransformerException m_ex;
    private TransformerException m_originalTransformerException = null;
    private SAXParseException m_originalSaxParseException = null;
    private Exception m_originalException;
    private String m_location;
    private String m_message;
    private String m_causeMessage;
    private String m_messageAndLocation;
    private int m_line = -1;
    private int m_column = -1;

    public TransformerExceptionContainer(TransformerException ex) {
        m_ex = ex;
        m_originalException = getOriginalException(ex);

        if (m_originalException instanceof TransformerException) {
            m_originalTransformerException = (TransformerException) m_originalException;

            SourceLocator locator = m_originalTransformerException.getLocator();
            if (locator != null) {
                m_line = locator.getLineNumber();
                m_column = locator.getColumnNumber();
            }

            String tempLocation = m_originalTransformerException.getLocationAsString();
            if (tempLocation != null) {
                m_location = new String(tempLocation);
            } else {
                m_location = null;
            }

            String tempMessageAndLocation = m_originalTransformerException.getMessageAndLocation();

            if (tempMessageAndLocation != null) {
                m_messageAndLocation = new String(tempMessageAndLocation);
            } else {
                m_messageAndLocation = null;
            }

        } else if (m_originalException instanceof SAXParseException) {
            m_originalSaxParseException = (SAXParseException) m_originalException;

            m_line = m_originalSaxParseException.getLineNumber();
            m_column = m_originalSaxParseException.getColumnNumber();

            if (m_originalSaxParseException.getSystemId() != null) {
                m_location = m_originalSaxParseException.getSystemId();
            } else {
                String tempLocation = m_ex.getLocationAsString();
                if (tempLocation != null) {
                    m_location = new String(tempLocation);
                } else {
                    m_location = null;
                }
            }

        }

        String tempMessage = m_ex.getMessage();
        if (tempMessage != null) {
            m_message = new String(tempMessage);
        } else {
            m_message = null;
        }
        if (!m_ex.equals(m_originalException)) {
            String tempCauseMessage = m_originalException.getMessage();
            if (tempCauseMessage != null) {
                m_causeMessage = new String(tempCauseMessage);
            } else {
                m_causeMessage = null;
            }
        } else {
            m_causeMessage = null;
        }

    }

    public TransformerException getTransformerException() {
        return m_ex;
    }

    public String getOriginalLocation() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Original Location: " + m_location + 
                        "; location from exception: " + 
                        m_originalException);
        }
        return m_location;
    }

    public String getMessage() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Message: " + m_message + 
                        "; message from exception: " + m_ex);
        }
        return m_message;
    }

    public String getCauseMessage() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Cause Message: " + m_causeMessage + 
                        "; message from exception: " + m_originalException);
        }
        return m_causeMessage;
    }

    public String getOriginalMessageAndLocation() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Original MessageAndLocation: " + 
                        m_messageAndLocation + 
                        "; messageAndLocation from exception: " + 
                        m_originalException);
        }
        return m_message;
    }

    public int getOriginalColumnNumber() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Original Column: " + m_column + 
                        "; column from exception: " + 
                        m_originalException);
        }
        return m_column;
    }

    public int getOriginalLineNumber() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Original Line: " + m_line + 
                        "; line from exception: " + 
                        m_originalException);
        }
        return m_line;
    }

    private static Exception getOriginalException(Exception ex) {
        Throwable cause = null;
        if (ex instanceof TransformerException) {
            cause = ((TransformerException)ex).getException();
        } else if (ex instanceof SAXException) {
            // we use SAXException instead of SAXParseException
            // because some factories such as resin throw
            // com.caucho.xml.XmlParseException instead of the
            // SAXParseException
            cause = ((SAXException)ex).getException();
        }

        if (cause != null && (cause instanceof TransformerException ||
                              cause instanceof SAXException)) {
            return getOriginalException((Exception) cause);
        } else if (cause instanceof XsltParseException) {
            return getOriginalException(((XsltParseException)cause).getException());
        } else {
            return ex;
        }
    }

}
