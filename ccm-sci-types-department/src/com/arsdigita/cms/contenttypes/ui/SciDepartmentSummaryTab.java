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
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSubordinateCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentConfig;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 * The summary tab for SciDepartment. Shows several informations about a SciDepartment.
 * 
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciDepartmentSummaryTab implements GenericOrgaUnitTab {

    private final static SciDepartmentConfig CONFIG = SciDepartment.getConfig();
    private final static SciDepartmentSummaryTabConfig TAB_CONFIG =
                                                       new SciDepartmentSummaryTabConfig();
    private String key;

    static {
        TAB_CONFIG.load();
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
        //Some of the the data shown by this tab will ever be there
        return true;
    }

    @Override
    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of SciDpartment."
                    + "The provided object is of type '%s',",
                    orgaunit.getClass().getName()));
        }

        final SciDepartment department = (SciDepartment) orgaunit;

        final Element departmentSummaryElem = parent.newChildElement(
                "departmentSummary");

        generateShortDescXml(department, departmentSummaryElem);

        if (TAB_CONFIG.isShowingHead()) {
            generateHeadOfDepartmentXml(department, departmentSummaryElem, state);
        }

        if (TAB_CONFIG.isShowingViceHead()) {
            generateViceHeadOfDepartmentXml(department, departmentSummaryElem, state);
        }

        if (TAB_CONFIG.isShowingSecretriat()) {
            generateSecretariatOfDepartmentXml(department, departmentSummaryElem, state);
        }

        if (TAB_CONFIG.isShowingSubDepartment()) {
            generateSubDepartmentsXml(department, departmentSummaryElem, state);
        }

        if (TAB_CONFIG.isShowingContacts()) {
            generateContactsXml(department, departmentSummaryElem, state);
        }
    }

    protected void generateShortDescXml(final SciDepartment department,
                                        final Element parent) {
        if ((department != null)
            && (department.getDepartmentShortDescription() != null)
            && !department.getDepartmentShortDescription().trim().isEmpty()) {
            final Element shortDescElem = parent.newChildElement("shortDesc");
            shortDescElem.setText(department.getDepartmentShortDescription());
        }
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

        final Element elem = parent.newChildElement(String.format("%ss", elemName));

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
                                            "head");
    }

    protected void generateViceHeadOfDepartmentXml(final SciDepartment department,
                                                   final Element parent,
                                                   final PageState state) {
        generateSpecialRolesOfDepartmentXml(department,
                                            parent,
                                            state,
                                            CONFIG.getViceHeadRole(),
                                            "vicehead");
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

    protected void generateSubDepartmentsXml(final SciDepartment department,
                                             final Element parent,
                                             final PageState state) {
        final GenericOrganizationalUnitSubordinateCollection subDepartments = department.
                getSubordinateOrgaUnits();
        subDepartments.addFilter(
                String.format("%s = '%s'",
                              GenericOrganizationalUnitSubordinateCollection.LINK_ASSOCTYPE,
                              SciDepartmentSubDepartmentsStep.ASSOC_TYPE));

        final Element subDepsElem = parent.newChildElement("subDepartments");

        while (subDepartments.next()) {
            generateSubDepartmentXml(
                    subDepartments.getGenericOrganizationalUnit(),
                    subDepsElem,
                    state);
        }
    }

    protected void generateSubDepartmentXml(
            final GenericOrganizationalUnit orgaunit,
            final Element parent,
            final PageState state) {
        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "Can't process "
                    + "orgaunit '%s' as sub department because the orgaunit is "
                    + "not a SciDepartment but of type '%s'.",
                    orgaunit.getName(),
                    orgaunit.getClass().
                    getName()));
        }

        final SciDepartment subDepartment = (SciDepartment) orgaunit;

        final Element subDepElem = parent.newChildElement("subDepartment");
        subDepElem.addAttribute("oid", subDepartment.getOID().toString());
        final Element nameElem = subDepElem.newChildElement("title");
        nameElem.setText(subDepartment.getTitle());

        generateHeadOfDepartmentXml(subDepartment, subDepElem, state);
    }

    protected void generateSpecialRoleXml(final GenericPerson member,
                                          final Element parent,
                                          final PageState state,
                                          final String elemName) {
        final XmlGenerator generator = new XmlGenerator(member);
        if (TAB_CONFIG.isShowingRoleContacts()) {
            generator.setUseExtraXml(true);
        } else {
            generator.setUseExtraXml(false);
        }
        generator.setItemElemName(elemName, "");
        generator.generateXML(state, parent, "");
    }

    protected void generateContactsXml(final SciDepartment department,
                                       final Element parent,
                                       final PageState state) {
        final GenericOrganizationalUnitContactCollection contacts = department.
                getContacts();

        if ((contacts == null) || contacts.isEmpty()) {
            return;
        }

        final Element contactsElem = parent.newChildElement("contacts");

        while (contacts.next()) {
            generateContactXml(contacts.getContact(),
                               contacts.getContactType(),
                               contactsElem,
                               state);
        }
    }

    protected void generateContactXml(final GenericContact contact,
                                      final String contactType,
                                      final Element parent,
                                      final PageState state) {
        final XmlGenerator generator = new XmlGenerator(contact);
        generator.setUseExtraXml(false);
        generator.setItemElemName("contact", "");
        generator.addItemAttribute("contactType",
                                   getContactTypeName(contactType));
        generator.generateXML(state, parent, "");
    }

    private String getContactTypeName(final String contactTypeKey) {
        final RelationAttributeCollection relAttrs = new RelationAttributeCollection();
        relAttrs.addFilter(String.format("attribute = '%s'",
                                         "GenericContactTypes"));
        relAttrs.addFilter(String.format("attr_key = '%s'", contactTypeKey));
        relAttrs.addFilter(String.format("lang = '%s'", GlobalizationHelper.
                getNegotiatedLocale().getLanguage()));

        if (relAttrs.isEmpty()) {
            return contactTypeKey;
        } else {
            relAttrs.next();
            final String result = relAttrs.getName();
            relAttrs.close();
            return result;
        }

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
