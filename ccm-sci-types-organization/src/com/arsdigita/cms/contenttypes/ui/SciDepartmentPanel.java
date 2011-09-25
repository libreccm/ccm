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
    private static final String TITLE = "title";
    private String show;
    private boolean displayDescription = true;
    private boolean displaySubDepartments = true;
    private boolean displayProjects = true;
    private Map<String, Filter> projectFilters =
                                new LinkedHashMap<String, Filter>();

    public SciDepartmentPanel() {
        projectFilters.put(TITLE, new TextFilter(TITLE, TITLE));
    }

    @Override
    protected String getDefaultShowParam() {
        return SHOW_DESCRIPTION;
    }

    @Override
    protected Class<? extends ContentItem> getAllowedClass() {
        return SciDepartment.class;
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

    public boolean isDisplaySubDepartments() {
        return displaySubDepartments;
    }

    public void setDisplaySubDepartments(boolean displaySubDepartments) {
        this.displaySubDepartments = displaySubDepartments;
    }

    protected boolean hasMembers(final SciDepartment department) {
        return department.hasMembers(SciDepartment.getConfig().
                getOrganizationMembersMerge(),
                                     SciDepartment.MemberStatus.ALL);
    }

    protected boolean hasActiveMembers(final SciDepartment department) {
        return department.hasMembers(SciDepartment.getConfig().
                getOrganizationMembersMerge(),
                                     SciDepartment.MemberStatus.ACTIVE);
    }

    protected boolean hasAssociatedMembers(final SciDepartment department) {
        return department.hasMembers(SciDepartment.getConfig().
                getOrganizationMembersMerge(),
                                     SciDepartment.MemberStatus.ASSOCIATED);
    }

    protected boolean hasFormerMembers(final SciDepartment department) {
        return department.hasMembers(SciDepartment.getConfig().
                getOrganizationMembersMerge(),
                                     SciDepartment.MemberStatus.FORMER);
    }

    protected boolean hasProjects(final SciDepartment department) {
        return department.hasProjects(SciDepartment.getConfig().
                getOrganizationProjectsMerge(),
                                      SciDepartment.ProjectStatus.ALL);
    }

    protected boolean hasOngoingProjects(final SciDepartment department) {
        return department.hasProjects(SciDepartment.getConfig().
                getOrganizationProjectsMerge(),
                                      SciDepartment.ProjectStatus.ONGOING);
    }

    protected boolean hasFinishedProjects(final SciDepartment department) {
        return department.hasProjects(SciDepartment.getConfig().
                getOrganizationProjectsMerge(),
                                      SciDepartment.ProjectStatus.FINISHED);
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
                parent, pageNumber, pageCount, begin, end, count,
                subDepartments.size());
        subDepartments.setRange((int) begin + 1, (int) end + 1);

        while (subDepartments.next()) {
            SciDepartment subDepartment;
            subDepartment = subDepartments.getSubDepartment();

            Element subDepartmentElem = subDepartmentsElem.newChildElement(
                    "department");
            subDepartmentElem.addAttribute("order",
                                           Integer.toString(subDepartments.
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
                    Element headElem = headsElem.newChildElement("head");
                    Element titlePre = headElem.newChildElement("titlePre");
                    titlePre.setText(heads.getTitlePre());
                    Element givenName = headElem.newChildElement("givenname");
                    givenName.setText(heads.getGivenName());
                    Element surname = headElem.newChildElement("surname");
                    surname.setText(heads.getSurname());
                    Element titlePost = headElem.newChildElement("titlePost");
                    titlePost.setText(heads.getTitlePost());
                }
            }

            GenericOrganizationalUnitContactCollection contacts;
            contacts = subDepartment.getContacts();
            if (contacts.size() > 0) {
                Element contactsElem = subDepartmentElem.newChildElement(
                        "contacts");

                while (contacts.next()) {
                    generateContactXML(contacts.getContactType(),
                                       contacts.getPerson(),
                                       contacts.getContactEntries(),
                                       contacts.getAddress(),
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
                addMember(departmentMembers,
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
                generateMemberXML(memberItem,
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
                addMember(departmentMembers,
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
                generateMemberXML(memberItem,
                                  membersElem,
                                  memberItem.getRole(),
                                  memberItem.getStatus(),
                                  state);
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
    }

    protected void generateProjectsXML(final SciDepartment department,
                                       final Element parent,
                                       final PageState state,
                                       final List<String> filters) {
        Element controls = parent.newChildElement("filterControls");
        controls.addAttribute("customName", "sciDepartmentProjects");
        controls.addAttribute("show", show);

        if (SciDepartment.getConfig().getOrganizationProjectsMerge()) {
            List<SciProject> projects;
            projects = new LinkedList<SciProject>();
            SciDepartmentProjectsCollection departmentProjects;
            departmentProjects = department.getProjects();

            applyProjectFilters(filters, state.getRequest());
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
            createPaginatorElement(parent,
                                   pageNumber,
                                   pageCount,
                                   begin,
                                   end,
                                   count,
                                   projectsWithoutDoubles.size());
            List<SciProject> projectsToShow =
                             projectsWithoutDoubles.subList((int) begin,
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

    @Override
    protected void generateAvailableDataXml(final GenericOrganizationalUnit orga,
                                            final Element element,
                                            final PageState state) {
        final SciOrganizationConfig config = SciOrganization.getConfig();

        SciDepartment department = (SciDepartment) orga;

        if ((department.getDepartmentDescription() != null)
            && !department.getDepartmentDescription().isEmpty()
            && displayDescription) {
            element.newChildElement("description");
        }
        if (department.hasContacts()
            && isDisplayContacts()) {
            element.newChildElement("contacts");
        }
        if (department.hasSubDepartments()
            && displaySubDepartments) {
            element.newChildElement("subDepartments");
        }
        if (config.getOrganizationMembersAllInOne()) {
            if (hasMembers(department)
                && isDisplayMembers()) {
                element.newChildElement("members");
            }
        } else {
            if (hasActiveMembers(department)
                && isDisplayMembers()) {
                element.newChildElement("membersActive");
            }
            if (hasAssociatedMembers(department)
                && isDisplayMembers()) {
                element.newChildElement("membersAssociated");
            }
            if (hasFormerMembers(department)
                && isDisplayMembers()) {
                element.newChildElement("membersFormer");
            }
        }
        if (config.getOrganizationProjectsAllInOne()) {
            if (hasProjects(department)
                && displayProjects) {
                element.newChildElement("projects");
            }
        } else {
            if (hasOngoingProjects(department)
                && displayProjects) {
                element.newChildElement("projectsOngoing");
            }
            if (hasFinishedProjects(department)
                && displayProjects) {
                element.newChildElement("projectsFinished");
            }
        }
    }

    @Override
    protected void generateDataXml(GenericOrganizationalUnit orga,
                                   Element element,
                                   PageState state) {
        show = getShowParam(state);

        SciDepartment department = (SciDepartment) orga;

        if (SHOW_DESCRIPTION.equals(show)) {
            String desc;
            desc = department.getDepartmentDescription();

            Element description = element.newChildElement("description");
            description.setText(desc);
        } else if (SHOW_CONTACTS.equals(show)) {
            generateContactsXML(department, element, state);
        } else if (SHOW_MEMBERS.equals(show)) {
            generateMembersXML(department, element, state,
                               new LinkedList<String>());
        } else if (SHOW_MEMBERS_ACTIVE.equals(show)) {
            generateMembersXML(department, element, state,
                               getFiltersForActiveMembers());
        } else if (SHOW_MEMBERS_ASSOCIATED.equals(show)) {
            generateMembersXML(department, element, state,
                               getFiltersForAssociatedMembers());
        } else if (SHOW_MEMBERS_FORMER.equals(show)) {
            generateMembersXML(department, element, state,
                               getFiltersForFormerMembers());
        } else if (SHOW_PROJECTS.equals(show)) {
            generateProjectsXML(department, element, state,
                                new LinkedList<String>());
        } else if (SHOW_PROJECTS_ONGOING.equals(show)) {
            generateProjectsXML(department, element, state,
                                getFiltersForOngoingProjects());
        } else if (SHOW_PROJECTS_FINISHED.equals(show)) {
            generateProjectsXML(department, element, state,
                                getFiltersForFinishedProjects());
        } else if (SHOW_SUBDEPARTMENTS.equals(show)) {
            generateSubDepartmentsXML(department, element, state);
        }
    }

    /*@Override
    public void generateXML(ContentItem item,
    Element element,
    PageState state) {
    Element content = generateBaseXML(item, element, state);
    
    Element availableData = content.newChildElement("availableData");
    
    SciDepartment department = (SciDepartment) item;
    
    generateAvailableDataXml(department, availableData, state);
    
    generateDataXml(department, content, state);
    }*/
}
