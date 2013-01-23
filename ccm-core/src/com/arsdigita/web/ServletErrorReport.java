/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.web;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.logging.ErrorReport;
import com.arsdigita.util.Exceptions;
import com.arsdigita.util.ExceptionUnwrapper;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.TreeSet;
import java.text.Collator;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
 * This is an advanced servlet error report generator
 * which dumps practically all the information it can
 * find about the servlet request to the logs. It also
 * sets a request attribute containing the ACS Error Report
 * (guru meditation) code.
 */
public class ServletErrorReport extends ErrorReport {

    private static final Logger logger = Logger.getLogger(ServletErrorReport.class);
    /**
     * The name of the Servlet request attribute which will
     * contain the guru meditation code
     */
    public static final String GURU_MEDITATION_CODE = "guruMeditationCode";
    public static final String GURU_ERROR_REPORT = "guruErrorReport";

    static {
       logger.debug("Static initalizer starting...");
        Exceptions.registerUnwrapper(
                ServletException.class,
                new ExceptionUnwrapper() {

                    public Throwable unwrap(Throwable t) {
                        ServletException ex = (ServletException) t;
                        return ex.getRootCause();
                    }
                });
        logger.debug("Static initalizer finished.");
    }
    private HttpServletRequest m_request;
    private HttpServletResponse m_response;

    public ServletErrorReport(Throwable throwable,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        super(throwable);

        m_request = request;
        m_response = response;

        // Take great care such that if something goes
        // wrong while creating the error report, we don't
        // let the new exception propagate thus loosing the
        // one we're actually trying to report on.
        try {
            addRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            addCookies();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            addUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            addAttributes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            addHeaders();
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute(GURU_MEDITATION_CODE, getGuruMeditationCode());
        request.setAttribute(GURU_ERROR_REPORT, getReport());
    }

    private void addRequest() {
        ArrayList lines = new ArrayList();

        lines.add("Context path: " + m_request.getContextPath());
        lines.add("Request URI: " + m_request.getRequestURI());
        lines.add("Query string: " + m_request.getQueryString());
        lines.add("Method: " + m_request.getMethod());
        lines.add("Remote user: " + m_request.getRemoteUser());

        addSection("Request summary", lines);
    }

    private void addCookies() {
        Cookie cookies[] = m_request.getCookies();
        if (cookies == null) {
            return;
        }

        String lines[] = new String[cookies.length];

        for (int i = 0; i < lines.length; i++) {
            lines[i] = cookies[i].getName() + ": " + cookies[i].getValue()
                       + " (expires: " + cookies[i].getMaxAge() + ")";
        }

        addSection("Cookies", lines);
    }

    private void addUser() {
        User user;
        Party party = Kernel.getContext().getParty();

        if (party == null) {
            addSection("CCM User", "Party not logged in");
        } else {
            String lines[] = new String[5];
            lines[0] = "Party ID: " + party.getID();
            lines[1] = "Email address: " + party.getPrimaryEmail().toString();

            if (party instanceof User) {
                user = (User) party;

                PersonName name = null;
                // Under postgres, once a DB error has occurred,
                // you'll no longer be able to execute queries, thus
                // the call to getPersonName could fail. It can also
                // fail under any DB, if we've just lost the oracle
                // connection. Since we cannot afford to loose the
                // error report, we take care to catch any exception
                // thrown here.
                try {
                    name = user.getPersonName();
                } catch (Exception e) {
                    // Nada
                }
                if (name != null) {
                    lines[2] = "Family name: " + name.getFamilyName();
                    lines[3] = "Given name: " + name.getGivenName();
                } else {
                    lines[2] = "Family name: not available";
                    lines[3] = "Given name: not available";
                }
                lines[4] = "Screen name: " + user.getScreenName();
            } else {
                lines[2] = "Family name: party is not a user";
                lines[3] = "Given name: party is not a user";
                lines[4] = "Screen name: party is not a user";
            }

            addSection("CCM User", lines);
        }

    }

    private void addAttributes() {
        TreeSet data = new TreeSet(Collator.getInstance());

        Enumeration props = m_request.getAttributeNames();
        while (props.hasMoreElements()) {
            String key = (String) props.nextElement();
            if (GURU_ERROR_REPORT.equals(key)
                || GURU_MEDITATION_CODE.equals(key)) {
                continue;
            }
            Object value = m_request.getAttribute(key);
            data.add(key + ": " + value);
        }

        addSection("Servlet attributes", data);
    }

    private void addHeaders() {
        TreeSet data = new TreeSet(Collator.getInstance());

        Enumeration props = m_request.getHeaderNames();
        while (props.hasMoreElements()) {
            String key = (String) props.nextElement();
            String value = m_request.getHeader(key);
            data.add(key + ": " + value);
        }

        addSection("HTTP headers", data);
    }
}
