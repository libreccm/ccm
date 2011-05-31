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
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.SciOrganizationConfig;
import com.arsdigita.cms.contenttypes.SciOrganizationDepartmentsCollection;
import com.arsdigita.cms.contenttypes.SciOrganizationProjectsCollection;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.xml.Element;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Panel for rendering the properties of a SciOrganization. The property to show
 * is selected via the <code>show</code> parameter.
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationPanel extends SciOrganizationBasePanel {

    private static final Logger s_log = Logger.getLogger(
            SciOrganizationPanel.class);
    public static final String SHOW_DESCRIPTION = "description";
    public static final String SHOW_MEMBERS_ACTIVE = "membersActive";
    public static final String SHOW_MEMBERS_ASSOCIATED = "membersAssociated";
    public static final String SHOW_MEMBERS_FORMER = "membersFormer";
    public static final String SHOW_DEPARTMENTS = "departments";
    public static final String SHOW_PROJECTS = "projects";
    public static final String SHOW_PROJECTS_ONGOING = "projectsOngoing";
    public static final String SHOW_PROJECTS_FINISHED = "projectsFinished";
    public static final String SHOW_PUBLICATIONS = "publications";
    private boolean displayDescription = true;
    private boolean displayDepartments = true;
    private boolean displayProjects = true;
    private boolean displayPublications = true;

    @Override
    protected String getDefaultForShowParam() {
        return SHOW_DESCRIPTION;
    }

    @Override
    protected Class<? extends ContentItem> getAllowedClass() {
        return SciOrganization.class;
    }

    public boolean isDisplayDepartments() {
        return displayDepartments;
    }

    public void setDisplayDepartments(boolean displayDepartments) {
        this.displayDepartments = displayDepartments;
    }

    public boolean isDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(boolean displayDescription) {
        this.displayDescription = displayDescription;
    }

    public boolean isDisplayProjects() {
        return displayProjects;
    }

    public void setDisplayProjects(boolean displayProjects) {
        this.displayProjects = displayProjects;
    }

    protected boolean hasMembers(final SciOrganization orga) {
        return orga.hasMembers(true, SciOrganization.MemberStatus.ALL);
    }

    protected boolean hasActiveMembers(final SciOrganization orga) {
        return orga.hasMembers(true, SciOrganization.MemberStatus.ACTIVE);
    }

    protected boolean hasAssociatedMembers(final SciOrganization orga) {
        return orga.hasMembers(true, SciOrganization.MemberStatus.ASSOCIATED);
    }

    protected boolean hasFormerMembers(final SciOrganization orga) {
        return orga.hasMembers(true, SciOrganization.MemberStatus.FORMER);
    }

    protected boolean hasProjects(final SciOrganization orga) {
        return orga.hasProjects(SciOrganization.getConfig().
                getOrganizationProjectsMerge(),
                                SciOrganization.ProjectStatus.ALL);
    }
    
    protected  boolean hasOngoingProjects(final SciOrganization orga) {
        return orga.hasProjects(SciOrganization.getConfig().
                getOrganizationProjectsMerge(),
                                SciOrganization.ProjectStatus.ONGOING);
    }
    
    protected  boolean hasFinishedProjects(final SciOrganization orga) {
        return orga.hasProjects(SciOrganization.getConfig().
                getOrganizationProjectsMerge(),
                                SciOrganization.ProjectStatus.FINISHED);
    }
    
    protected void generateDepartmentsXML(final SciOrganization orga,
                                          final Element parent,
                                          final PageState state) {
        SciOrganizationDepartmentsCollection departments;
        departments = orga.getDepartments();
        departments.addOrder("link.departmentOrder asc");

        long pageNumber = getPageNumber(state);

        Element departmentsElem = parent.newChildElement("departments");

        long pageCount = getPageCount(departments.size());
        long begin = getPaginatorBegin(pageNumber);
        long count = getPaginatorCount(begin, departments.size());
        long end = getPaginatorEnd(begin, count);
        pageNumber = normalizePageNumber(pageCount, pageNumber);

        createPaginatorElement(
                parent, pageNumber, pageCount, begin, end, count, departments.
                size());
        departments.setRange((int) begin + 1, (int) end + 1);

        while (departments.next()) {
            SciDepartment department;
            department = departments.getDepartment();

            Element departmentElem = departmentsElem.newChildElement(
                    "department");
            departmentElem.addAttribute("order", Integer.toString(departments.
                    getDepartmentOrder()));
            departmentElem.addAttribute("oid", department.getOID().toString());

            Element title = departmentElem.newChildElement("title");
            title.setText(department.getTitle());

            if ((department.getAddendum() != null)
                && !(department.getAddendum().isEmpty())) {
                Element addendum = departmentElem.newChildElement("addendum");
                addendum.setText(department.getAddendum());
            }

            if ((department.getDepartmentShortDescription() != null)
                && !(department.getDepartmentShortDescription().isEmpty())) {
                Element shortDesc = departmentElem.newChildElement(
                        "shortDescription");
                shortDesc.setText(department.getDepartmentShortDescription());
            }

            GenericOrganizationalUnitPersonCollection heads;
            heads = department.getPersons();
            heads.addFilter("link.role_name = 'head'");
            heads.addOrder("surname asc, givenname asc");

            if (heads.size() > 0) {
                Element headsElem = departmentElem.newChildElement("heads");

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
            contacts = department.getContacts();
            if (contacts.size() > 0) {
                Element contactsElem =
                        departmentElem.newChildElement("contacts");

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

    protected void mergeMembers(
            final SciOrganizationDepartmentsCollection departments,
            final List<MemberListItem> members,
            final List<String> filters) {

        while (departments.next()) {
            SciDepartment department = departments.getDepartment();
            GenericOrganizationalUnitPersonCollection departmentsMembers;
            departmentsMembers = department.getPersons();
            for (String filter : filters) {
                departmentsMembers.addFilter(filter);
            }

            while (departmentsMembers.next()) {
                addMember(departmentsMembers.getPerson(),
                          departmentsMembers.getRoleName(),
                          departmentsMembers.getStatus(),
                          members);
            }

            SciDepartmentSubDepartmentsCollection subDepartments;
            subDepartments = department.getSubDepartments();


            if ((subDepartments != null)
                && subDepartments.size() > 0) {
                mergeMembers(subDepartments, members, filters);
            }
        }
    }

    protected void generateMembersXML(final SciOrganization orga,
                                      final Element parent,
                                      final PageState state,
                                      final List<String> filters) {
        if (SciOrganization.getConfig().getOrganizationMembersMerge()) {
            List<MemberListItem> members;
            members = new LinkedList<MemberListItem>();
            GenericOrganizationalUnitPersonCollection orgaMembers;
            orgaMembers = orga.getPersons();
            for (String filter : filters) {
                orgaMembers.addFilter(filter);
            }
                        
            SciOrganizationDepartmentsCollection departments;
            departments = orga.getDepartments();

            while (orgaMembers.next()) {
                addMember(orgaMembers.getPerson(),
                          orgaMembers.getRoleName(),
                          orgaMembers.getStatus(),
                          members);
            }

            mergeMembers(departments, members, filters);

            generateMembersListXML(members, parent, state);
        } else {
            GenericOrganizationalUnitPersonCollection orgaMembers;
            orgaMembers = orga.getPersons();
            for (String filter : filters) {
                orgaMembers.addFilter(filter);
            }
            List<MemberListItem> members = new LinkedList<MemberListItem>();

            while (orgaMembers.next()) {
                addMember(orgaMembers.getPerson(),
                          orgaMembers.getRoleName(),
                          orgaMembers.getStatus(),
                          members);
            }

            generateMembersListXML(members, parent, state);
        }
    }

    protected void mergeProjects(
            final SciOrganizationDepartmentsCollection departments,
            final List<SciProject> projects,
            final List<String> filters) {

        while (departments.next()) {
            SciDepartmentProjectsCollection departmentProjects;
            departmentProjects = departments.getDepartment().getProjects();

            if ((filters != null)
                && !(filters.isEmpty())) {
                for (String filter : filters) {
                    departmentProjects.addFilter(filter);
                }
            }

            while (departmentProjects.next()) {
                projects.add(departmentProjects.getProject());
            }

            SciDepartmentSubDepartmentsCollection subDepartments;
            subDepartments = departments.getDepartment().getSubDepartments();

            if ((subDepartments != null)
                && subDepartments.size() > 0) {
                mergeProjects(subDepartments, projects, filters);
            }
        }
    }

    protected void generateProjectsXML(final SciOrganization orga,
                                       final Element parent,
                                       final PageState state,
                                       final List<String> filters) {
        if (SciOrganization.getConfig().getOrganizationProjectsMerge()) {
            List<SciProject> projects;
            projects = new LinkedList<SciProject>();
            SciOrganizationProjectsCollection orgaProjects;
            orgaProjects = orga.getProjects();

            if ((filters != null)
                && !(filters.isEmpty())) {
                for (String filter : filters) {
                    orgaProjects.addFilter(filter);
                }
            }

            SciOrganizationDepartmentsCollection departments;
            departments = orga.getDepartments();

            while (orgaProjects.next()) {
                projects.add(orgaProjects.getProject());
            }

            mergeProjects(departments, projects, filters);

            Set<SciProject> projectsSet;
            List<SciProject> projectsWithoutDoubles;
            projectsSet = new HashSet<SciProject>(projects);
            projectsWithoutDoubles = new LinkedList<SciProject>(projectsSet);

            Collections.sort(projectsWithoutDoubles, new SciProjectComparator());

            long pageNumber = getPageNumber(state);
            long pageCount = getPageCount(projectsWithoutDoubles.size());
            long begin = getPaginatorBegin(pageNumber);
            long count = getPaginatorCount(begin, projectsWithoutDoubles.size());
            long end = getPaginatorEnd(begin, count);
            pageNumber = normalizePageNumber(pageCount, pageNumber);

            createPaginatorElement(
                    parent, pageNumber, pageCount, begin, end, count,
                    projectsWithoutDoubles.size());
            List<SciProject> projectsWithoutDoublesToShow =
                             projectsWithoutDoubles.subList((int) begin,
                                                            (int) end);

            Element projectsWithoutDoublesElem = parent.newChildElement(
                    "projects");
            for (SciProject project : projectsWithoutDoublesToShow) {
                generateProjectXML(project, projectsWithoutDoublesElem, state);
            }
        } else {
            SciOrganizationProjectsCollection orgaProjects;
            orgaProjects = orga.getProjects();

            if ((filters != null)
                && !(filters.isEmpty())) {
                for (String filter : filters) {
                    orgaProjects.addFilter(filter);
                }
            }

            List<SciProject> projects = new LinkedList<SciProject>();

            while (orgaProjects.next()) {
                projects.add(orgaProjects.getProject());
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

        SciOrganization orga = (SciOrganization) item;

        SciOrganizationConfig config;
        config = SciOrganization.getConfig();

        if ((orga.getOrganizationDescription() != null)
            && !(orga.getOrganizationDescription().isEmpty())
            && displayDescription) {
            availableData.newChildElement("description");
        }
        if (orga.hasContacts()
            && isDisplayContacts()) {
            availableData.newChildElement("contacts");
        }
        if (orga.hasDepartments()
            && displayDepartments) {
            availableData.newChildElement("departments");
        }
        if (config.getOrganizationMembersAllInOne()) {
            if (hasMembers(orga)
                && isDisplayMembers()) {
                availableData.newChildElement("members");
            }
        } else {
            if (hasActiveMembers(orga)
                && isDisplayMembers()) {
                availableData.newChildElement("membersActive");
            }
            if (hasAssociatedMembers(orga)
                && isDisplayMembers()) {
                availableData.newChildElement("membersAssociated");
            }
            if (hasFormerMembers(orga)
                && isDisplayMembers()) {
                availableData.newChildElement("membersFormer");
            }
        }
        if (config.getOrganizationProjectsAllInOne()) {
            if (hasProjects(orga)
                && displayProjects) {
                availableData.newChildElement("projects");
            }
        } else {
            if (hasOngoingProjects(orga)
                && displayProjects) {
                availableData.newChildElement("projectsOngoing");
            }
            if (hasFinishedProjects(orga)
                && displayProjects) {
                availableData.newChildElement("projectsFinished");
            }
        }
        if (orga.hasPublications() 
                && displayPublications) {
            availableData.newChildElement("publications");
        }                

        String show = getShowParam(state);

        if (SHOW_DESCRIPTION.equals(show)) {
            String desc;
            desc = orga.getOrganizationDescription();

            Element description = content.newChildElement("description");
            description.setText(desc);
        } else if (SHOW_CONTACTS.equals(show)) {
            generateContactsXML(orga, content, state);
        } else if (SHOW_DEPARTMENTS.equals(show)) {
            generateDepartmentsXML(orga, content, state);
        } else if (SHOW_MEMBERS.equals(show)) {
            generateMembersXML(orga, content, state, new LinkedList<String>());
        } else if (SHOW_MEMBERS_ACTIVE.equals(show)) {
            generateMembersXML(orga, content, state,
                               getFiltersForActiveMembers());
        } else if (SHOW_MEMBERS_ASSOCIATED.equals(show)) {
            generateMembersXML(orga, content, state,
                               getFiltersForAssociatedMembers());
        } else if (SHOW_MEMBERS_FORMER.equals(show)) {
            generateMembersXML(orga, content, state,
                               getFiltersForFormerMembers());
        } else if (SHOW_PROJECTS.equals(show)) {
            generateProjectsXML(orga, content, state, new LinkedList<String>());
        } else if (SHOW_PROJECTS_ONGOING.equals(show)) {
            generateProjectsXML(
                    orga, content, state, getFiltersForOngoingProjects());
        } else if (SHOW_PROJECTS_FINISHED.equals(show)) {
            generateProjectsXML(
                    orga, content, state, getFiltersForFinishedProjects());
        } else if (SHOW_PUBLICATIONS.equals(show)) {
            generatePublicationsXML(
                    RelatedLink.getRelatedLinks(item,
                                                "SciOrganizationPublications"),
                    content,
                    state);
        }
    }
}
