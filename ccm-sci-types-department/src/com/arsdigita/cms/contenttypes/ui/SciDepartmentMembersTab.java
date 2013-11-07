/*
 * Copyright (c) 2013 Jens Pelzetter
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
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.ui.panels.CompareFilter;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * This tab displays all person items associated with a SciDepartment.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentMembersTab implements GenericOrgaUnitTab {

    private static final SciDepartmentMembersTabConfig config = new SciDepartmentMembersTabConfig();
    private static final String STATUS_PARAM = "memberStatus";
    private static final String SURNAME_PARAM = "memberSurname";
    private final CompareFilter statusFilter = new CompareFilter(STATUS_PARAM,
                                                                 "status",
                                                                 false,
                                                                 false,
                                                                 false);
    private final TextFilter surnameFilter = new TextFilter(SURNAME_PARAM,
                                                            GenericPerson.SURNAME);
    private String key;

    static {
        config.load();
    }

    public SciDepartmentMembersTab() {
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
        if ((orgaunit.getPersons() != null)
            && orgaunit.getPersons().size() > 0) {
            return true;
        } else if (config.isMergingMembers()) {
            final DataQuery persons = getData(orgaunit, state).getMembers();
            return (persons != null) && persons.isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        //final DataCollection persons = getData(orgaunit, state);
        final MembersData membersData = getData(orgaunit, state);
        final DataCollection persons = membersData.getMembers();
        final Map<String, String> membersRoles = membersData.getMembersRoles();
        final Map<String, String> membersStatus = membersData.getMembersStatus();
        final HttpServletRequest request = state.getRequest();

        applySurnameFilter(persons, request);

        final Element depMembersElem = parent.newChildElement(
                "departmentMembers");

        final Element filtersElem = depMembersElem.newChildElement("filters");

        statusFilter.generateXml(filtersElem);

        if ((persons == null) || persons.isEmpty()) {
            if ((surnameFilter != null)
                && (surnameFilter.getFilter() != null)
                && !(surnameFilter.getFilter().trim().isEmpty())) {
                surnameFilter.generateXml(filtersElem);
            }
            depMembersElem.newChildElement("noMembers");
            return;
        }

        final RelationAttributeCollection roles = new RelationAttributeCollection(
                SciDepartment.ROLE_ENUM_NAME);
        roles.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().getLanguage());
        final Element rolesElem = depMembersElem.newChildElement("roles");
        while (roles.next()) {
            generateRoleValueElem(rolesElem, roles.getKey(), roles.getName());
        }

        final RelationAttributeCollection statusValues = new RelationAttributeCollection(
                "GenericOrganizationalUnitMemberStatus");
        statusValues.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().getLanguage());
        final Element statusValuesElem = depMembersElem.newChildElement("statusValues");
        while (statusValues.next()) {
            generateStatusValueElem(statusValuesElem, statusValues.getKey(), statusValues.getName());
        }

        final Paginator paginator = new Paginator(request,
                                                  (int) persons.size(),
                                                  config.getPageSize());

        if ((paginator.getPageCount() > config.getEnableSearchLimit())
            || ((surnameFilter.getFilter() != null)
                && !(surnameFilter.getFilter().trim().isEmpty()))) {
            surnameFilter.generateXml(filtersElem);
        }

        paginator.applyLimits(persons);
        paginator.generateXml(depMembersElem);

        while (persons.next()) {
            final GenericPerson person = new GenericPerson(persons.getDataObject());
            generateMemberXml(person,
                              membersRoles.get(person.getContentBundle().getID().toString()),
                              membersStatus.get(person.getContentBundle().getID().toString()),
                              depMembersElem,
                              state);
        }
    }

    private void generateRoleValueElem(final Element parent, final String key, final String value) {
        final Element roleValueElem = parent.newChildElement("role");
        roleValueElem.addAttribute("key", key);
        roleValueElem.setText(value);
    }

    private void generateStatusValueElem(final Element parent, final String key, final String value) {
        final Element statusValueElem = parent.newChildElement("status");
        statusValueElem.addAttribute("key", key);
        statusValueElem.setText(value);
    }

    protected class MembersData {

        private final DataCollection members;
        private final Map<String, String> membersRoles;
        private final Map<String, String> membersStatus;

        public MembersData(final DataCollection members,
                           final Map<String, String> membersRoles,
                           final Map<String, String> membersStatus) {
            this.members = members;
            this.membersRoles = membersRoles;
            this.membersStatus = membersStatus;
        }

        public DataCollection getMembers() {
            return members;
        }

        public Map<String, String> getMembersRoles() {
            return Collections.unmodifiableMap(membersRoles);
        }

        public Map<String, String> getMembersStatus() {
            return Collections.unmodifiableMap(membersStatus);
        }

    }

    protected MembersData getData(final GenericOrganizationalUnit orgaunit,
                                  final PageState state) {
        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciDepartment'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final DataQuery personBundlesQuery = SessionManager.getSession().
                retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfMembersOfOrgaUnits");
        final List<String> orgaUnitIds = new ArrayList<String>();

        if (config.isMergingMembers()) {
            final DataQuery subDepartmentsQuery = SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
            subDepartmentsQuery.setParameter("orgaunitId",
                                             orgaunit.getContentBundle().getID().toString());
            subDepartmentsQuery.setParameter("assocType",
                                             SciDepartmentSubDepartmentsStep.ASSOC_TYPE);

            while (subDepartmentsQuery.next()) {
                orgaUnitIds.add(subDepartmentsQuery.get("orgaunitId").toString());
            }
        } else {
            orgaUnitIds.add(orgaunit.getContentBundle().getID().toString());
        }

        personBundlesQuery.setParameter("orgaunitIds", orgaUnitIds);
        applyStatusFilter(personBundlesQuery, state.getRequest());

        final Map<String, String> membersRoles = new HashMap<String, String>();
        final Map<String, String> membersStatus = new HashMap<String, String>();

        final StringBuilder filterBuilder = new StringBuilder();
        while (personBundlesQuery.next()) {
            if (filterBuilder.length() > 0) {
                filterBuilder.append(',');
            }
            final String memberId = personBundlesQuery.get("memberId").toString();
            filterBuilder.append(memberId);
            membersRoles.put(memberId, (String) personBundlesQuery.get("roleName"));
            membersStatus.put(memberId, (String) personBundlesQuery.get("status"));
        }
        final DataCollection membersQuery = SessionManager.getSession().retrieve(
                GenericPerson.BASE_DATA_OBJECT_TYPE);

        if (filterBuilder.length() == 0) {
            //No member return null to indicate
            return null;
        }

        membersQuery.addFilter(String.format("parent.id in (%s)", filterBuilder.toString()));

        membersQuery.addOrder(GenericPerson.SURNAME);
        membersQuery.addOrder(GenericPerson.GIVENNAME);

        //return membersQuery;
        return new MembersData(membersQuery, membersRoles, membersStatus);
    }

    private void applyStatusFilter(final DataQuery persons,
                                   final HttpServletRequest request) {
        final String statusValue = Globalization.decodeParameter(request,
                                                                 STATUS_PARAM);
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
        final String surnameValue = Globalization.decodeParameter(request,
                                                                  SURNAME_PARAM);
        if ((surnameValue != null) && !(surnameValue.trim().isEmpty())) {
            surnameFilter.setValue(surnameValue);
        }

        final String filter = surnameFilter.getFilter();
        if ((filter != null) && !(filter.trim().isEmpty())) {
            persons.addFilter(filter);
        }
    }

//    protected void generateMemberXml(final BigDecimal memberId,
//                                     final Element parent,
//                                     final PageState state) {
//        final long start = System.currentTimeMillis();
//        final GenericPerson member = new GenericPerson(memberId);
//        logger.debug(String.format("Got domain object for member '%s' "
//                                   + "in %d ms.",
//                                   member.getFullName(),
//                                   System.currentTimeMillis() - start));
//        generateMemberXml(member, parent, state);
//    }
    protected void generateMemberXml(final GenericPerson member,
                                     final String role,
                                     final String status,
                                     final Element parent,
                                     final PageState state) {
        final XmlGenerator generator = new XmlGenerator(member);
        //generator.setUseExtraXml(false);
        //generator.setListMode(true);
        generator.setItemElemName("member", "");
        generator.addItemAttribute("role", role);
        generator.addItemAttribute("status", status);
        generator.generateXML(state, parent, "");
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
