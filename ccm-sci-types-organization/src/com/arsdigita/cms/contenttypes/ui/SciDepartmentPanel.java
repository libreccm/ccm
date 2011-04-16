/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentProjectsCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentSubDepartmentsCollection;
import com.arsdigita.cms.contenttypes.SciOrganizationConfig;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.xml.Element;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentPanel extends SciOrganizationBasePanel {

    private static final Logger s_log = Logger.getLogger(
            SciDepartmentPanel.class);
    public static final String SHOW_DESCRIPTION = "description";
    public static final String SHOW_MEMBERS_ACTIVE = "membersActive";
    public static final String SHOW_MEMBERS_ASSOCIATED = "membersAssociated";
    public static final String SHOW_MEMBERS_FORMER = "membersFormer";
    public static final String SHOW_SUBDEPARTMENTS = "subdepartments";
    public static final String SHOW_PROJECTS = "projects";
    public static final String SHOW_PROJECTS_ONGOING = "projectsOngoing";
    public static final String SHOW_PROJECTS_FINISHED = "projectsFinished";
    public static final String SHOW_PUBLICATIONS = "publications";

    @Override
    protected String getDefaultForShowParam() {
        return SHOW_DESCRIPTION;
    }

    @Override
    protected Class<? extends ContentItem> getAllowedClass() {
        return SciDepartment.class;
    }

    protected boolean hasMembers(final SciDepartment department,
                                 final List<String> filters) {
        if (department.getPersons() != null) {
            GenericOrganizationalUnitPersonCollection persons;
            persons = department.getPersons();
            for (String filter : filters) {
                persons.addFilter(filter);
            }
            if (persons.size() > 0) {
                return true;
            }
        }

        boolean hasMembers;
        hasMembers = false;

        if (SciDepartment.getConfig().getOrganizationMembersMerge()) {
            SciDepartmentSubDepartmentsCollection subDepartments;
            subDepartments = department.getSubDepartments();
            while (subDepartments.next()) {
                SciDepartment subDepartment = subDepartments.getSubDepartment();

                hasMembers = hasMembers(subDepartment, filters);
                if (hasMembers) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean hasProjects(final SciDepartment department,
                                  final List<String> filters) {
        if (department.getProjects() != null) {
            SciDepartmentProjectsCollection projects;
            projects = department.getProjects();
            for (String filter : filters) {
                projects.addFilter(filter);
            }
            if (projects.size() > 0) {
                return true;
            }
        }

        boolean hasProjects;
        hasProjects = false;

        if (SciDepartment.getConfig().getOrganizationProjectsMerge()) {
            SciDepartmentSubDepartmentsCollection subDepartments;
            subDepartments = department.getSubDepartments();

            while (subDepartments.next()) {
                SciDepartment subDepartment = subDepartments.getSubDepartment();

                hasProjects = hasProjects(subDepartment, filters);
                if (hasProjects) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void generateSubDepartmentsXML(final SciDepartment department,
                                             final Element parent,
                                             final PageState state) {
        SciDepartmentSubDepartmentsCollection subDepartments;
        subDepartments = department.getSubDepartments();
        subDepartments.addOrder("link.subDepartmentOrder asc");

        long pageNumber = getPageNumber(state);

        Element subDepartmentsElem = parent.newChildElement("subDepartments");

        long pageCount = getPageCount(subDepartments.size());
        long begin = getPaginatorBegin(pageNumber);
        long count = getPaginatorCount(begin, subDepartments.size());
        long end = getPaginatorEnd(begin, count);
        pageNumber = normalizePageNumber(pageCount, pageNumber);

        createPaginatorElement(
                parent, pageNumber, pageCount, begin, end, count, subDepartments.
                size());
        subDepartments.setRange((int) begin, (int) end);

        while (subDepartments.next()) {
            SciDepartment subDepartment;
            subDepartment = subDepartments.getSubDepartment();

            Element subDepartmentElem = subDepartmentsElem.newChildElement(
                    "department");
            subDepartmentElem.addAttribute("order", Integer.toString(subDepartments.
                    getSubDepartmentOrder()));
            subDepartmentElem.addAttribute("oid", subDepartment.getOID().
                    toString());

            Element title = subDepartmentElem.newChildElement("title");
            title.setText(subDepartment.getTitle());

            if ((subDepartment.getAddendum() != null)
                && !(subDepartment.getAddendum().isEmpty())) {
                Element addendum = subDepartmentElem.newChildElement("addendum");
                addendum.setText(subDepartment.getAddendum());
            }

            if ((subDepartment.getDepartmentShortDescription() != null)
                && !(subDepartment.getDepartmentShortDescription().isEmpty())) {
                Element shortDesc = subDepartmentElem.newChildElement(
                        "shortDescription");
                shortDesc.setText(subDepartment.getDepartmentShortDescription());
            }

            GenericOrganizationalUnitPersonCollection heads;
            heads = subDepartment.getPersons();
            heads.addFilter(("link.role_name = 'head'"));
            heads.addOrder("surname asc, givenname asc");

            if (heads.size() > 0) {
                Element headsElem = subDepartmentElem.newChildElement("heads");

                while (heads.next()) {
                    GenericPerson head = heads.getPerson();
                    Element headElem = headsElem.newChildElement("head");
                    Element titlePre = headElem.newChildElement("titlePre");
                    titlePre.setText(head.getTitlePre());
                    Element givenName = headElem.newChildElement("givenname");
                    givenName.setText(head.getGivenName());
                    Element surname = headElem.newChildElement("surname");
                    surname.setText(head.getSurname());
                    Element titlePost = headElem.newChildElement("titlePost");
                    titlePost.setText(head.getTitlePost());
                }
            }

            GenericOrganizationalUnitContactCollection contacts;
            contacts = subDepartment.getContacts();
            if (contacts.size() > 0) {
                Element contactsElem = subDepartmentElem.newChildElement(
                        "contacts");

                while (contacts.next()) {
                    generateContactXML(contacts.getContact(),
                                       contactsElem,
                                       state,
                                       Integer.toString(
                            contacts.getContactOrder()),
                                       true);
                }
            }
        }
    }

    protected void generateMembersXML(final SciDepartment department,
                                      final Element parent,
                                      final PageState state,
                                      final List<String> filters) {
        if (SciDepartment.getConfig().getOrganizationMembersMerge()) {
            List<MemberListItem> members;
            members = new LinkedList<MemberListItem>();
            GenericOrganizationalUnitPersonCollection departmentMembers;
            departmentMembers = department.getPersons();
            for (String filter : filters) {
                departmentMembers.addFilter(filter);
            }

            SciDepartmentSubDepartmentsCollection subDepartments;
            subDepartments = department.getSubDepartments();

            while (departmentMembers.next()) {
                addMember(departmentMembers.getPerson(),
                          departmentMembers.getRoleName(),
                          departmentMembers.getStatus(),
                          members);
            }

            mergeMembers(subDepartments, members, filters);

            Collections.sort(members, new MemberListItemComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(members.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, members.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);


            createPaginatorElement(
                    parent, pageNumber, pageCount, begin, end, count, members.
                    size());
            List<MemberListItem> membersToShow = members.subList((int) begin,
                                                                 (int) end);

            Element membersElem = parent.newChildElement("members");

            for (MemberListItem memberItem : membersToShow) {
                generateMemberXML(memberItem.getMember(),
                                  membersElem,
                                  memberItem.getRole(),
                                  memberItem.getStatus(),
                                  state);
            }
        } else {
            GenericOrganizationalUnitPersonCollection departmentMembers;
            departmentMembers = department.getPersons();
            for (String filter : filters) {
                departmentMembers.addFilter(filter);
            }

            List<MemberListItem> members = new LinkedList<MemberListItem>();

            while (departmentMembers.next()) {
                addMember(departmentMembers.getPerson(),
                          departmentMembers.getRoleName(),
                          departmentMembers.getStatus(),
                          members);
            }

            Collections.sort(members, new MemberListItemComparator());

            long pageNumber = getPageNumber(state);

            Element membersElem = parent.newChildElement("members");

            long pageCount = getPageCount(members.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, members.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            createPaginatorElement(
                    parent, pageNumber, pageCount, begin, end, count, members.
                    size());
            List<MemberListItem> membersToShow = members.subList((int) begin,
                                                                 (int) end);

            for (MemberListItem memberItem : membersToShow) {
                generateMemberXML(memberItem.getMember(),
                                  membersElem,
                                  memberItem.getRole(),
                                  memberItem.getStatus(),
                                  state);
            }
        }
    }

    protected void generateProjectsXML(final SciDepartment department,
                                       final Element parent,
                                       final PageState state,
                                       final List<String> filters) {
        if (SciDepartment.getConfig().getOrganizationProjectsMerge()) {
            List<SciProject> projects;
            projects = new LinkedList<SciProject>();
            SciDepartmentProjectsCollection departmentProjects;
            departmentProjects = department.getProjects();

            if ((filters != null)
                && !(filters.isEmpty())) {
                for (String filter : filters) {
                    departmentProjects.addFilter(filter);
                }
            }

            SciDepartmentSubDepartmentsCollection subDepartments;
            subDepartments = department.getSubDepartments();

            while (departmentProjects.next()) {
                projects.add(departmentProjects.getProject());
            }

            mergeProjects(subDepartments, projects, filters);

            Collections.sort(projects, new SciProjectComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(projects.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, projects.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            createPaginatorElement(
                    parent, pageNumber, pageCount, begin, end, count, projects.
                    size());
            List<SciProject> projectsToShow = projects.subList((int) begin,
                                                               (int) end);



            Element projectsElem = parent.newChildElement("projects");
            for (SciProject project : projectsToShow) {
                generateProjectXML(project, projectsElem, state);
            }
        } else {
            SciDepartmentProjectsCollection departmentProjects;
            departmentProjects = department.getProjects();

            if ((filters != null)
                && !(filters.isEmpty())) {
                for (String filter : filters) {
                    departmentProjects.addFilter(filter);
                }
            }

            List<SciProject> projects = new LinkedList<SciProject>();

            while (departmentProjects.next()) {
                projects.add(departmentProjects.getProject());
            }

            Collections.sort(projects, new SciProjectComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(projects.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, projects.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            createPaginatorElement(
                    parent, pageNumber, pageCount, begin, end, count, projects.
                    size());
            List<SciProject> projectsToShow = projects.subList((int) begin,
                                                               (int) end);

            Element projectsElem = parent.newChildElement("projects");
            for (SciProject project : projectsToShow) {
                generateProjectXML(project, projectsElem, state);
            }
        }
    }

    @Override
    public void generateXML(ContentItem item,
                            Element element,
                            PageState state) {
        Element content = generateBaseXML(item, element, state);

        Element availableData = content.newChildElement("availableData");

        SciDepartment department = (SciDepartment) item;

        SciOrganizationConfig config = SciDepartment.getConfig();

        if ((department.getDepartmentDescription() != null)
            && !department.getDepartmentDescription().isEmpty()) {
            availableData.newChildElement("description");
        }
        if ((department.getContacts() != null)
            && (department.getContacts().size() > 0)) {
            availableData.newChildElement("contacts");
        }
        if ((department.getSubDepartments() != null)
            && (department.getSubDepartments().size() > 0)) {
            availableData.newChildElement("subDepartments");
        }
        if (config.getOrganizationMembersAllInOne()) {
            if (hasMembers(department, new LinkedList<String>())) {
                availableData.newChildElement("members");
            }
        } else {
            if (hasMembers(department, getFiltersForActiveMembers())) {
                availableData.newChildElement("membersActive");
            }
            if (hasMembers(department, getFiltersForAssociatedMembers())) {
                availableData.newChildElement("membersAssociated");
            }
            if (hasMembers(department, getFiltersForFormerMembers())) {
                availableData.newChildElement("membersFormer");
            }
        }
        if (config.getOrganizationProjectsAllInOne()) {
            if (hasProjects(department, new LinkedList<String>())) {
                availableData.newChildElement("projects");
            }
        } else {
            if (hasProjects(department, getFiltersForOngoingProjects())) {
                availableData.newChildElement("projectsOngoing");
            }
            if (hasProjects(department, getFiltersForFinishedProjects())) {
                availableData.newChildElement("projectsFinished");
            }
        }
        DataCollection publicationLinks =
                       RelatedLink.getRelatedLinks(department,
                                                   "SciDepartmentPublications");
        if ((publicationLinks != null) && (publicationLinks.size() > 0)) {
            availableData.newChildElement("publications");
        }

        String show = getShowParam(state);

        if (SHOW_DESCRIPTION.equals(show)) {
            String desc;
            desc = department.getDepartmentDescription();

            Element description = content.newChildElement("description");
            description.setText(desc);
        } else if (SHOW_CONTACTS.equals(show)) {
            generateContactsXML(department, content, state);
        } else if (SHOW_MEMBERS.equals(show)) {
            generateMembersXML(department, content, state,
                               new LinkedList<String>());
        } else if (SHOW_MEMBERS_ACTIVE.equals(show)) {
            generateMembersXML(department, content, state,
                               getFiltersForActiveMembers());
        } else if (SHOW_MEMBERS_ASSOCIATED.equals(show)) {
            generateMembersXML(department, content, state,
                               getFiltersForAssociatedMembers());
        } else if (SHOW_MEMBERS_FORMER.equals(show)) {
            generateMembersXML(department, content, state,
                               getFiltersForFormerMembers());
        } else if (SHOW_PROJECTS.equals(show)) {
            generateProjectsXML(department, content, state,
                                new LinkedList<String>());
        } else if (SHOW_PROJECTS_ONGOING.equals(show)) {
            generateProjectsXML(department, content, state,
                                getFiltersForOngoingProjects());
        } else if (SHOW_PROJECTS_FINISHED.equals(show)) {
            generateProjectsXML(department, content, state,
                                getFiltersForFinishedProjects());
        } else if (SHOW_SUBDEPARTMENTS.equals(show)) {
            generateSubDepartmentsXML(department, content, state);
        } else if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXML(
                    RelatedLink.getRelatedLinks(item,
                                                "SciDepartmentPublications"),
                    content,
                    state);
        }
    }
}
