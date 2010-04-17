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
package com.arsdigita.cms.ui.staff;


import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.toolbox.ui.DataQueryListModelBuilder;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;


/**
 * <p>This panel displays information for a particular party.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Michael Bryzek (mbryzek@arsdigita.com)
 * @version $Id: PartyInfo.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PartyInfo extends SimpleContainer {

    private final SingleSelectionModel m_parties;

    private List m_partyRoles;
    private ActionLink m_back;

    private boolean m_useViewersGroup;


    /**
     * Constructor.
     *
     * @param parties A selection model for parties
     */
    public PartyInfo(SingleSelectionModel parties) {
        this(parties, false);
    }
    /**
     * Constructor.
     *
     * @param parties A selection model for users
     * @param useViewersGroup Whether to use viewers roles instead of staff admin roles
     */
    public PartyInfo(SingleSelectionModel parties, boolean useViewersGroup) {
        super();

        m_parties = parties;
        m_useViewersGroup = useViewersGroup;

        // The roles to which the selected party belongs.
        m_partyRoles = new List(new RolesListModelBuilder());
        m_partyRoles.setIdAttr("party_roles_list");
        m_partyRoles.setCellRenderer(new CellRenderer());
        add(m_partyRoles);

        // The back link.
        m_back = new ActionLink( (String) GlobalizationUtil.globalize("cms.ui.staff.back").localize());
        m_back.setClassAttr("actionLink");
        m_back.setIdAttr("back_link");
        m_back.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    PageState state = event.getPageState();
                    m_parties.clearSelection(state);
                }
            });
        add(m_back);
    }


    /**
     * Return the list of roles.
     *
     * @return The list of roles
     */
    protected List getList() {
        return m_partyRoles;
    }

    /**
     * Return the back link.
     *
     * @return The back link
     */
    protected ActionLink getBackLink() {
        return m_back;
    }

    /**
     * Gets the party ID.
     *
     * @param state The page state
     * @return The party ID
     * @pre ( state != null )
     */
    protected BigDecimal getPartyId(PageState state) {
        Assert.isTrue(m_parties.isSelected(state));
        String partyId = (String) m_parties.getSelectedKey(state);
        return new BigDecimal(partyId);
    }

    /**
     * Adds a roleMemberInfo element to the DOM.
     *
     * <p> The XML generated has the form
     * <pre>
     *   &lt;cms:roleMemberInfo party_id="id" name="Name" email="email">
     *     &lt;bebop:list id="party_roles_list">
     *       ... XML generated for component returned by renderer ...
     *     &lt;/bebop:list>
     *     &lt;bebop:link id="back_link">
     *       ... XML generated for component returned by renderer ...
     *     &lt;/bebop:link>
     *   &lt;/cms:roleMemberInfo></pre>
     *
     * @param state the state of the current request
     * @param parent the element into which XML is generated
     * @pre state != null
     * @pre parent != null
     */
    public void generateXML(PageState state, Element parent) {
        if ( isVisible(state) ) {
            Party party = null;
            try {
                party = (Party) DomainObjectFactory.newInstance
                    (new OID(Party.BASE_DATA_OBJECT_TYPE, getPartyId(state)));
            } catch (DataObjectNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }

            Element element = new Element("cms:roleMemberInfo", CMS.CMS_XML_NS);
            element.addAttribute("party_id", party.getID().toString());
	    element.addAttribute("partyType", party instanceof Group ? "group" : "person");
            element.addAttribute("name", party.getName());
	    String email = party.getPrimaryEmail() == null ? "no email address" :
		party.getPrimaryEmail().getEmailAddress();
            element.addAttribute("email", email);

            m_partyRoles.generateXML(state, element);
            m_back.generateXML(state, element);
            parent.addContent(element);
        }
    }


    /**
     * Renders the party roles as a non-linked, non-bulleted list.
     */
    private class CellRenderer implements ListCellRenderer {

        public CellRenderer() {}

        public Component getComponent(List list, PageState state, Object value,
                                      String key, int index, boolean isSelected) {
            SimpleContainer container = new SimpleContainer();
            Label roleName = new Label(value.toString());
            container.add(roleName);
            return container;
        }
    }


    /**
     * Builds the ListModel for CMS staff admin roles which this party belongs.
     */
    private class RolesListModelBuilder extends DataQueryListModelBuilder {

        private final static String KEY_NAME = "groupId";
        private final static String VALUE_NAME = "name";
        private final static String QUERY_NAME =
            "com.arsdigita.cms.getUserStaffRoles";
        private final static String VIEWERS_QUERY_NAME =
            "com.arsdigita.cms.getUserViewerRoles";
        private final static String GROUP_QUERY_NAME =
            "com.arsdigita.cms.getGroupStaffRoles";
        private final static String GROUP_VIEWERS_QUERY_NAME =
            "com.arsdigita.cms.getGroupViewerRoles";

        private final static String SECTION_ID = "sectionId";
        private final static String USER_ID = "userId";
        private final static String GROUP_ID = "subgroupId";
        private final static String FILTER =
            "sectionId = :sectionId and userId = :userId";
        private final static String GROUP_FILTER =
            "sectionId = :sectionId and subgroupId = :subgroupId";

        protected RolesListModelBuilder() {
            super(KEY_NAME, VALUE_NAME);
        }

        protected DataQuery getDataQuery(PageState ps) {
            ContentSection section = CMS.getContext().getContentSection();
            BigDecimal partyId = new BigDecimal((String) m_parties.getSelectedKey(ps));
            Party party = null;
            try {
                party = (Party) DomainObjectFactory.newInstance
                    (new OID(Party.BASE_DATA_OBJECT_TYPE, partyId));
            } catch (DataObjectNotFoundException e) {
                e.printStackTrace();
            }

            // Prepare query, add content section filter, order by name.
            DataQuery query;
            Filter filter;
            if (party instanceof Group) {
                if (m_useViewersGroup) {
                    query = SessionManager.getSession().retrieveQuery(GROUP_VIEWERS_QUERY_NAME);
                } else {
                    query = SessionManager.getSession().retrieveQuery(GROUP_QUERY_NAME);
                }
                filter = query.addFilter(GROUP_FILTER);
                filter.set(GROUP_ID, partyId);
            } else {
                if (m_useViewersGroup) {
                    query = SessionManager.getSession().retrieveQuery(VIEWERS_QUERY_NAME);
                } else {
                    query = SessionManager.getSession().retrieveQuery(QUERY_NAME);
                }
                filter = query.addFilter(FILTER);
                filter.set(USER_ID, partyId);
            }
            filter.set(SECTION_ID, section.getID());
            query.addOrder(VALUE_NAME);
            return query;
        }
    }

}
