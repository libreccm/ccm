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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentConfig;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;

/**
 * ExtraXMLGenerator for SciDepartment. Adds data from several associations to the XML output.
 * 
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciDepartmentExtraXmlGenerator extends GenericOrgaUnitExtraXmlGenerator {

    private final static SciDepartmentConfig CONFIG = SciDepartment.getConfig();

    @Override
    public void generateXML(final ContentItem item, final Element element, final PageState state) {
        super.generateXML(item, element, state);

        if (!(item instanceof SciDepartment)) {
            throw new IllegalArgumentException(
                    "This ExtraXMLGenerator supports "
                    + "only instances of SciDepartment only.");
        }

        final SciDepartment department = (SciDepartment) item;

        if (getListMode()) {
            if (CONFIG.getShowHeadInList()) {
                generateHeadOfDepartmentXml(department, element, state);
            }

            if (CONFIG.getShowViceHeadInList()) {
                generateViceHeadOfDepartmentXml(department, element, state);
            }

            if (CONFIG.getShowSecretariatInList()) {
                generateSecretariatOfDepartmentXml(department, element, state);
            }
        }
    }

    @Override
    public String getTabConfig() {
        final SciDepartmentConfig config = SciDepartment.getConfig();

        return config.getTabs();
    }

    protected void generateSpecialRolesOfDepartmentXml(final SciDepartment department,
                                                       final Element parent,
                                                       final PageState state,
                                                       final String role,
                                                       final String elemName) {
        final String activeStatusStr = CONFIG.getActiveStatus();
        final String[] roles = role.split(",");
        final String[] activeStatuses = activeStatusStr.split(",");

        final StringBuffer roleFilter = new StringBuffer();
        for (String currentRole : roles) {
            if (roleFilter.length() > 0) {
                roleFilter.append(',');
            }
            roleFilter.append(String.format("%s = '%s'",
                                            GenericOrganizationalUnitPersonCollection.LINK_PERSON_ROLE,
                                            currentRole));
        }

        final StringBuffer statusFilter = new StringBuffer();
        for (String activeStatus : activeStatuses) {
            if (statusFilter.length() > 0) {
                statusFilter.append(',');
            }
            statusFilter.append(String.format("%s = '%s'",
                                              GenericOrganizationalUnitPersonCollection.LINK_STATUS,
                                              activeStatus));
        }

        final Element elem = parent.newChildElement(elemName);

        final GenericOrganizationalUnitPersonCollection persons = department.getPersons();
        persons.addFilter(roleFilter.toString());
        persons.addFilter(statusFilter.toString());
        persons.addOrder("name");

        while (persons.next()) {
            generateSpecialRoleXml(persons.getPerson(), elem, state, elemName);
        }

    }

    protected void generateHeadOfDepartmentXml(final SciDepartment department,
                                               final Element parent,
                                               final PageState state) {
        generateSpecialRolesOfDepartmentXml(department,
                                            parent,
                                            state,
                                            CONFIG.getHeadRole(),
                                            "heads");
    }

    protected void generateViceHeadOfDepartmentXml(final SciDepartment department,
                                                   final Element parent,
                                                   final PageState state) {
        generateSpecialRolesOfDepartmentXml(department,
                                            parent,
                                            state,
                                            CONFIG.getViceHeadRole(),
                                            "viceheads");
    }

    protected void generateSecretariatOfDepartmentXml(final SciDepartment department,
                                                      final Element parent,
                                                      final PageState state) {
        generateSpecialRolesOfDepartmentXml(department,
                                            parent,
                                            state,
                                            CONFIG.getSecretariatRole(),
                                            "secretariat");
    }

    protected void generateSpecialRoleXml(final GenericPerson member,
                                          final Element parent,
                                          final PageState state,
                                          final String elemName) {
        final XmlGenerator generator = new XmlGenerator(member);
        if (CONFIG.getListShowRoleContacts()) {
            generator.setUseExtraXml(true);
        } else {
            generator.setUseExtraXml(false);
        }
        generator.setItemElemName(elemName, "");
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
