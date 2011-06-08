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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
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
import com.arsdigita.cms.contenttypes.ui.panels.Filter;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.xml.Element;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
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
    private static final String TTILE = "title";
    private String show;
    private boolean displayDescription = true;
    private boolean displayDepartments = true;
    private boolean displayProjects = true;
    private final Map<String, Filter> projectFilters =
                                      new LinkedHashMap<String, Filter>();

    public SciOrganizationPanel() {
        projectFilters.put(TTILE, new TextFilter(TTILE, TTILE));
    }

    @Override
    protected String getDefaultShowParam() {
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

    protected boolean hasOngoingProjects(final SciOrganization orga) {
        return orga.hasProjects(SciOrganization.getConfig().
                getOrganizationProjectsMerge(),
                                SciOrganization.ProjectStatus.ONGOING);
    }

    protected boolean hasFinishedProjects(final SciOrganization orga) {
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
            final List<String> filters,
            final PageState state) {

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
        Element controls = parent.newChildElement("filterControls");
        controls.addAttribute("customName", "sciOrganizationProjects");
        controls.addAttribute("show", show);
        
        if (SciOrganization.getConfig().getOrganizationProjectsMerge()) {
            List<SciProject> projects;
            projects = new LinkedList<SciProject>();
            SciOrganizationProjectsCollection orgaProjects;
            orgaProjects = orga.getProjects();

            applyProjectFilters(filters, state.getRequest());
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

            mergeProjects(departments, projects, filters, state);

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

            generateProjectFiltersXml(projectsWithoutDoubles, controls);
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

            applyProjectFilters(filters, state.getRequest());
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

            generateProjectFiltersXml(projects, controls);
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

    protected void generateProjectFiltersXml(
            final List<SciProject> projects,
            final Element element) {
        final Element filterElement = element.newChildElement("filters");

        for (Map.Entry<String, Filter> filterEntry : projectFilters.entrySet()) {
            filterEntry.getValue().generateXml(filterElement);
        }
    }

    protected void applyProjectFilters(
            final List<String> filters,
            final HttpServletRequest request) {
        //Get parameters from HTTP request
        for (Map.Entry<String, Filter> filterEntry : projectFilters.entrySet()) {
            String value = request.getParameter(
                    filterEntry.getValue().getLabel());

            if ((value != null) && !(value.trim().isEmpty())) {
                filterEntry.getValue().setValue(value);
            }
        }

        //Apply filters to DomainCollection
        final StringBuilder filterBuilder = new StringBuilder();
        for (Map.Entry<String, Filter> filterEntry : projectFilters.entrySet()) {
            if ((filterEntry.getValue().getFilter() == null)
                || (filterEntry.getValue().getFilter().isEmpty())) {
                continue;
            }

            if (filterBuilder.length() > 0) {
                filterBuilder.append(" AND ");
            }
            filterBuilder.append(filterEntry.getValue().getFilter());
            s_log.debug(String.format("filters: %s", filterBuilder));
            if (filterBuilder.length() > 0) {
                filters.add(filterBuilder.toString());
            }
        }
    }

    @Override
    protected void generateAvailableDataXml(final GenericOrganizationalUnit orga,
                                            final Element element,
                                            final PageState state) {
        SciOrganizationConfig config;
        config = SciOrganization.getConfig();

        SciOrganization organization = (SciOrganization) orga;

        if ((organization.getOrganizationDescription() != null)
            && !(organization.getOrganizationDescription().isEmpty())
            && displayDescription) {
            element.newChildElement("description");
        }
        if (organization.hasContacts()
            && isDisplayContacts()) {
            element.newChildElement("contacts");
        }
        if (organization.hasDepartments()
            && displayDepartments) {
            element.newChildElement("departments");
        }
        if (config.getOrganizationMembersAllInOne()) {
            if (hasMembers(organization)
                && isDisplayMembers()) {
                element.newChildElement("members");
            }
        } else {
            if (hasActiveMembers(organization)
                && isDisplayMembers()) {
                element.newChildElement("membersActive");
            }
            if (hasAssociatedMembers(organization)
                && isDisplayMembers()) {
                element.newChildElement("membersAssociated");
            }
            if (hasFormerMembers(organization)
                && isDisplayMembers()) {
                element.newChildElement("membersFormer");
            }
        }
        if (config.getOrganizationProjectsAllInOne()) {
            if (hasProjects(organization)
                && displayProjects) {
                element.newChildElement("projects");
            }
        } else {
            if (hasOngoingProjects(organization)
                && displayProjects) {
                element.newChildElement("projectsOngoing");
            }
            if (hasFinishedProjects(organization)
                && displayProjects) {
                element.newChildElement("projectsFinished");
            }
        }
    }

    @Override
    protected void generateDataXml(final GenericOrganizationalUnit orga,
                                   final Element element,
                                   final PageState state) {
        show = getShowParam(state);

        SciOrganization organization = (SciOrganization) orga;

        if (SHOW_DESCRIPTION.equals(show)) {
            String desc;
            desc = organization.getOrganizationDescription();

            Element description = element.newChildElement("description");
            description.setText(desc);
        } else if (SHOW_CONTACTS.equals(show)) {
            generateContactsXML(organization, element, state);
        } else if (SHOW_DEPARTMENTS.equals(show)) {
            generateDepartmentsXML(organization, element, state);
        } else if (SHOW_MEMBERS.equals(show)) {
            generateMembersXML(organization, element, state,
                               new LinkedList<String>());
        } else if (SHOW_MEMBERS_ACTIVE.equals(show)) {
            generateMembersXML(organization, element, state,
                               getFiltersForActiveMembers());
        } else if (SHOW_MEMBERS_ASSOCIATED.equals(show)) {
            generateMembersXML(organization, element, state,
                               getFiltersForAssociatedMembers());
        } else if (SHOW_MEMBERS_FORMER.equals(show)) {
            generateMembersXML(organization, element, state,
                               getFiltersForFormerMembers());
        } else if (SHOW_PROJECTS.equals(show)) {
            generateProjectsXML(organization, element, state,
                                new LinkedList<String>());
        } else if (SHOW_PROJECTS_ONGOING.equals(show)) {
            generateProjectsXML(
                    organization, element, state, getFiltersForOngoingProjects());
        } else if (SHOW_PROJECTS_FINISHED.equals(show)) {
            generateProjectsXML(
                    organization, element, state,
                    getFiltersForFinishedProjects());
        }
    }

    /*@Override
    public void generateXML(ContentItem item,
    Element element,
    PageState state) {
    Element content = generateBaseXML(item, element, state);
    
    SciOrganization orga = (SciOrganization) item;
    Element availableData = content.newChildElement("availableData");
    
    generateAvailableDataXml(orga, availableData, state);
    
    generateDataXml(orga, content, state);
    }*/
}
