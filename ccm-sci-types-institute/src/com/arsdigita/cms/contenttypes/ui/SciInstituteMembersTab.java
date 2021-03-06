/*
 * Copyright (c) 2011, 2012, 2013 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.contenttypes.ui.panels.CompareFilter;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Tab for showing a list of members of an institute.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteMembersTab implements GenericOrgaUnitTab {

    private final Logger logger = Logger.getLogger(SciInstituteMembersTab.class);
    private static final SciInstituteMembersTabConfig config = new SciInstituteMembersTabConfig();
    private static final String STATUS_PARAM = "memberStatus";
    private static final String SURNAME_PARAM = "memberSurname";
    private final CompareFilter statusFilter = new CompareFilter(
            STATUS_PARAM,
            "status",
            false,
            false,
            false);
    private final TextFilter surnameFilter = new TextFilter(SURNAME_PARAM, GenericPerson.SURNAME);
    private String key;

    static {
        config.load();
    }

    public SciInstituteMembersTab() {
        super();
        final String[] statusValues = config.getStatusValues();

        for (String status : statusValues) {
            statusFilter.addOption(status, status);
        }
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(final String key) {
        this.key = key;
    }

    @Override
    public boolean hasData(final GenericOrganizationalUnit orgaunit,
                           final PageState state) {
        if ((orgaunit.getPersons() != null) && orgaunit.getPersons().size() > 0) {
            return true;
        } else if (config.isMergingMembers()) {
            final DataCollection persons = getData(orgaunit, state);
            return (persons != null) && !persons.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final DataCollection persons = getData(orgaunit, state);
        final HttpServletRequest request = state.getRequest();

        //applyStatusFilter(persons, request);
        applySurnameFilter(persons, request);

        final Element depMembersElem = parent.newChildElement(
                "instituteMembers");

        final Element filtersElem = depMembersElem.newChildElement("filters");

        statusFilter.generateXml(filtersElem);

        if ((persons == null) || persons.isEmpty()) {
            if ((surnameFilter != null)
                && (surnameFilter.getFilter() != null)
                && (!surnameFilter.getFilter().trim().isEmpty())) {
                surnameFilter.generateXml(filtersElem);
            }
            depMembersElem.newChildElement("noMembers");
            return;
        }

        final Paginator paginator = new Paginator(request,
                                                  (int) persons.size(),
                                                  config.getPageSize());

        if (paginator.getPageCount() > config.getEnableSearchLimit()
            || ((surnameFilter.getFilter() != null)
                && !(surnameFilter.getFilter().trim().isEmpty()))) {
            surnameFilter.generateXml(filtersElem);
        }

        paginator.applyLimits(persons);
        paginator.generateXml(depMembersElem);

        while (persons.next()) {
            generateMemberXml(new GenericPerson(persons.getDataObject()),
                              depMembersElem,
                              state);
        }

        logger.debug(String.format("Generated members list of institute'%s' "
                                   + "in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected DataCollection getData(final GenericOrganizationalUnit orgaunit,
                                     final PageState state) {
        final long start = System.currentTimeMillis();

        if (!(orgaunit instanceof SciInstitute)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciInstitute'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final DataQuery personBundlesQuery = SessionManager.getSession().
                retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfMembersOfOrgaUnits");
        final List<String> orgaunitsIds = new ArrayList<String>();

        if (config.isMergingMembers()) {
            final DataQuery departmentsQuery = SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
            departmentsQuery.setParameter("orgaunitId",
                                          orgaunit.getContentBundle().getID().toString());
            departmentsQuery.setParameter("assocType",
                                          SciInstituteDepartmentsStep.ASSOC_TYPE);

            while (departmentsQuery.next()) {
                orgaunitsIds.add(departmentsQuery.get("orgaunitId").toString());
            }
        } else {
            orgaunitsIds.add(orgaunit.getContentBundle().getID().toString());
        }

        personBundlesQuery.setParameter("orgaunitIds", orgaunitsIds);
        applyStatusFilter(personBundlesQuery, state.getRequest());

        final StringBuilder filterBuilder = new StringBuilder();
        while (personBundlesQuery.next()) {
            if (filterBuilder.length() > 0) {
                filterBuilder.append(',');
            }
            filterBuilder.append(personBundlesQuery.get("memberId").toString());
        }
        final DataCollection membersQuery = SessionManager.getSession().retrieve(
                GenericPerson.BASE_DATA_OBJECT_TYPE);

        if (filterBuilder.length() == 0) {
            //No members, return null to indicate
            return null;
        }

        membersQuery.addFilter(String.format("parent.id in (%s)", filterBuilder.toString()));

        membersQuery.addOrder(GenericPerson.SURNAME);
        membersQuery.addOrder(GenericPerson.GIVENNAME);

        logger.debug(String.format(
                "Got members of institute '%s'"
                + "in '%d ms'. MergeMembers is set to '%b'.",
                orgaunit.getName(),
                System.currentTimeMillis() - start,
                config.isMergingMembers()));

        return membersQuery;
    }

    private void applyStatusFilter(final DataQuery persons,
                                   final HttpServletRequest request) {
        final String statusValue = Globalization.decodeParameter(request, STATUS_PARAM);
        if ((statusValue != null) && !(statusValue.trim().isEmpty())) {
            statusFilter.setValue(statusValue);
        }

        final String filter = statusFilter.getFilter();
        if ((filter != null) && !(filter.trim().isEmpty())) {
            persons.addFilter(filter);
        }
    }

    private void applySurnameFilter(final DataQuery persons,
                                    final HttpServletRequest request) {
        final String surnameValue = Globalization.decodeParameter(request, SURNAME_PARAM);
        if ((surnameValue != null) && !(surnameValue.trim().isEmpty())) {
            surnameFilter.setValue(surnameValue);
        }

        final String filter = surnameFilter.getFilter();
        if ((filter != null) && !(filter.trim().isEmpty())) {
            persons.addFilter(filter);
        }
    }

    protected void generateMemberXml(final BigDecimal memberId,
                                     final Element parent,
                                     final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericPerson member = new GenericPerson(memberId);
        logger.debug(String.format("Got domain object for member '%s' "
                                   + "in %d ms.",
                                   member.getFullName(),
                                   System.currentTimeMillis() - start));
        generateMemberXml(member, parent, state);
    }

    protected void generateMemberXml(final GenericPerson member,
                                     final Element parent,
                                     final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(member);
        //generator.setUseExtraXml(false);
        generator.setItemElemName("member", "");
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for member '%s' in %d ms.",
                                   member.getFullName(),
                                   System.currentTimeMillis() - start));
    }

    private class XmlGenerator extends SimpleXMLGenerator {

        private final ContentItem item;

        public XmlGenerator(final ContentItem item) {
            super();
            this.item = item;
        }

        @Override
        protected ContentItem getContentItem(final PageState state) {
            return item;
        }

    }
}
