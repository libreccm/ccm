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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * A reusable Bebop component to display a list of users that
 * are registered on your site.
 */
public class UserList extends com.arsdigita.bebop.List {

    public static final String versionId = "$Id: UserList.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(UserList.class.getName());

    /**
     * A UserList is a List with a model builder that generates a list of
     * the Users registered in the system.
     */
    public UserList() {
        setClassAttr("user-list");
        setModelBuilder(new UserListModelBuilder());
        setCellRenderer(new UserListCellRenderer());
    }

    private class UserListModelBuilder extends LockableImpl
        implements ListModelBuilder {

        /**
         * Returns a ListModel filled from an Oracle query on
         * our users data model tables.
         */
        public ListModel makeModel(List l, PageState ps) {
            UserListModel result = null;
            try {
                DataQuery dq = SessionManager.getSession()
                    .retrieveQuery("com.arsdigita.kernel.RetrieveUsers");
                dq.setParameter("excludeGroupId", new Integer(0));
                result = new UserListModel(dq);
            } catch (Exception e) {
                s_log.error(e);
            }
            return result;
        }
    }

    // The Object that the ListModel.getElement() returns is passed into
    // the ListCellRenderer as the 'value' argument.
    // Hence, the ListCellRenderer first converts its value argument back
    // into a string array (because that is really what
    // UserListModel.getElement) returns.
    // In a more complex application, getElement() would probably return
    // some form of domain object ...

    /**
     * List model representing a list of users.
     */
    private class UserListModel implements ListModel {
        private DataQuery m_dq;

        public UserListModel(DataQuery dq) {
            m_dq = dq;
        }

        public boolean next() {
            boolean result = false;

            try {
                result = m_dq.next();
            } catch (Exception e) {
                s_log.error(e);
            }

            return result;
        }

        public String getKey() {
            try {
                return ((BigDecimal)m_dq.get("userID")).toString();
            }  catch (Exception e) {
                s_log.error(e);
            }
            return null;
        }

        public Object getElement() {
            String[] result = null;

            try {
                String id = ((BigDecimal)m_dq.get("userID")).toString();
                String email = (String)m_dq.get("primaryEmail");
                String given = (String)m_dq.get("firstName");
                String family = (String)m_dq.get("lastName");

                result = new String[] {id, email, given, family};
            } catch (Exception e) {
                s_log.error("getElement returning null", e);
            }
            return result;
        }
    }

    /**
     * Takes a 4-tuple from a UserListModel and renders it into
     * a domain-specific XML element
     * <pre>
     *   &lt;demo:user email="asdf@asdf.asdf" first-name="First"
     *        last-name="last"/>
     * </pre>
     */
    private class UserListCellRenderer implements ListCellRenderer {
        public Component getComponent(List list, PageState state,
                                      Object value, String key, int idx,
                                      boolean isSelected) {
            // hidden dependency on UserListModel!
            final String[] tuple = (String[])value;

            return new SimpleComponent() {
                    public void generateXML(PageState ps, Element parent) {
                        javax.xml.parsers.DocumentBuilderFactory dbf =
                            javax.xml.parsers.DocumentBuilderFactory.newInstance();
                        dbf.setNamespaceAware(true);
                        try {
                            javax.xml.parsers.DocumentBuilder db =
                                dbf.newDocumentBuilder();
                            org.w3c.dom.Document domDoc =
                                db.parse(new org.xml.sax.InputSource
                                         (new java.io.StringReader(getXMLString())));
                            com.arsdigita.xml.Document doc =
                                new com.arsdigita.xml.Document(domDoc);
                            parent.addContent(doc.getRootElement());
                        } catch (Exception e) {
                            throw new com.arsdigita.util.UncheckedWrapperException(e);
                        }
                        /*
                          final String namespaceURI =
                          "http://www.arsdigita.com/demo/1.0";
                          Element user = new Element("demo:user", namespaceURI);
                          user.addAttribute("email", tuple[1]);
                          user.addAttribute("first-name", tuple[2]);
                          user.addAttribute("last-name", tuple[3]);
                          parent.addContent(user);
                        */
                    }

                    public String getXMLString() {
                        return "<demo:user xmlns:demo=\"http://www.arsdigita.com/demo/1.0\" email=\"" + tuple[1] + "\" first-name=\"" + tuple[2] + "\" last-name=\"" + tuple[3] + "\"/>";
                    }
                };
        }
    }
}
