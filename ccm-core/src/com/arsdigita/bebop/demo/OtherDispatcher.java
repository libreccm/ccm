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
 *
 */
package com.arsdigita.bebop.demo;

import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.db.ConnectionManager;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.templating.Templating;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Demonstration dispatcher class.  Shows how you would build pages to
 * display dynamic data from the database, building
 * a DOM document and styling it with XSLT.
 *
 * <p> this class doesn't use Bebop.
 */
public class OtherDispatcher extends BebopMapDispatcher {

    public static final String versionId = 
            "$Id: OtherDispatcher.java 287 2005-02-22 00:29:02Z sskracic $" +
            " by $Author: sskracic $, " +
            "$DateTime: 2004/08/16 18:10:38 $";

    public OtherDispatcher() {
        super();
        setNotFoundDispatcher(new Dispatcher() {
                public void dispatch(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     RequestContext ctx) throws ServletException,
                                                                java.io.IOException {

                    final String BEBOP_XML_NS =
                        "http://www.arsdigita.com/bebop/1.0";

                    try {
                        // build document manually
                        Element page = new Element("bebop:page", BEBOP_XML_NS);
                        Element title = new Element("bebop:title", BEBOP_XML_NS);
                        title.setText("Other dispatcher");
                        page.addContent(title);
                        Element message = new Element("bebop:label", BEBOP_XML_NS);
                        page.addContent(message);
                        message.setText("Hello!  Oracle says the current time is "
                                        + timeFromOracle());
                        Document doc = new Document(page);

                        // now we have a DOM
                        Templating.getPresentationManager()
                            .servePage(doc, req, resp);
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }

                private String timeFromOracle() {
                    Connection conn = null;
                    try {
                        // This use of straight JDBC is obsolete, do not
                        // use it as a good example.
                        conn = ConnectionManager.getConnection();
                        Statement s = conn.createStatement();
                        ResultSet rs = s.executeQuery
                            ("select to_char(sysdate, 'Month dd, yyyy') from dual");
                        rs.next();
                        String retval = rs.getString(1);
                        rs.close();
                        s.close();
                        return retval;
                    } catch (Exception e) {
                        return e.toString();
                    } finally {
                        try { ConnectionManager.returnConnection(conn); } catch (Exception e2) {}
                    }
                }
            });
    }
}
