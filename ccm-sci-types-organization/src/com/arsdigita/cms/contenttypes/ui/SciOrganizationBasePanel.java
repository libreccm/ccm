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
import com.arsdigita.cms.ContentItemXMLRenderer;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentProjectsCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentSubDepartmentsCollection;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Base class for all panels of the sci-types-organization module. The panels
 * are displaying the information of an organization type (
 * <code>SciOrganization</code>, <code>SciDepartment</code>,
 * <code>SciProject</code>) similar to the <code>MultiPartArticle</code>. For
 * each information, e.g. members, there is a link, which replaces the
 * information shown. To use this as an index item, a special template is
 * necessary. An example of such a template can be found in the ccm-zes-aplaws
 * module.
 *
 * @see SciOrganizationPanel
 * @see SciDepartmentPanel
 * @see SciProjectPanel
 * @author Jens Pelzetter
 */
public abstract class SciOrganizationBasePanel
        extends GenericOrganizationalUnitPanel {

    private final static Logger s_log = Logger.getLogger(
            SciOrganizationBasePanel.class);

    protected class MemberListItem {

        private GenericPerson member;
        private String role;
        private String status;

        public MemberListItem(final GenericPerson member,
                              final String role,
                              final String status) {
            this.member = member;
            this.role = role;
            this.status = status;
        }

        public GenericPerson getMember() {
            return member;
        }

        public String getRole() {
            return role;
        }

        public String getStatus() {
            return status;
        }

        @Override
        public boolean equals(Object obj) {
            /*if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MemberListItem other = (MemberListItem) obj;
            if (this.member != other.member && (this.member == null
                                                || !this.member.equals(
                                                other.member))) {
                return false;
            }
            if ((this.role == null) ? (other.role != null)
                : !this.role.equals(other.role)) {
                return false;
            }
            if ((this.status == null) ? (other.status != null)
                : !this.status.equals(other.status)) {
                return false;
            }
            return true;*/
            if (obj instanceof MemberListItem) {
                MemberListItem other = (MemberListItem) obj;
                
                return member.equals(other.getMember());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            /*int hash = 7;
            hash =
            79 * hash + (this.member != null ? this.member.hashCode() : 0);
            hash = 79 * hash + (this.role != null ? this.role.hashCode() : 0);
            hash =
            79 * hash + (this.status != null ? this.status.hashCode() : 0);
            return hash;*/
            return member.hashCode();
        }
    }

    protected class MemberListItemComparator
            implements Comparator<MemberListItem> {

        public int compare(MemberListItem member1, MemberListItem member2) {
            int result = 0;

            result =
            member1.getMember().getSurname().compareToIgnoreCase(member2.
                    getMember().
                    getSurname());

            if (result == 0) {
                result = member1.getMember().getGivenName().compareTo(
                        member2.getMember().getGivenName());
            }

            return result;
        }
    }

    protected class SciProjectComparator implements Comparator<SciProject> {

        public int compare(SciProject project1, SciProject project2) {
            /*int result = 0;
            
            if (project1.getBegin() == null) {
            return -1;
            } else if (project2.getBegin() == null) {
            return 1;
            } else {
            result = project1.getBegin().compareTo(project2.getBegin());
            }
            
            if (result == 0) {
            if (project1.getEnd() == null) {
            return -1;
            } else if (project2.getEnd() == null) {
            return 1;
            } else if ((project1.getEnd() == null) && (project2.getEnd()
            == null)) {
            return 0;
            } else {
            result = project1.getEnd().compareTo(project2.getEnd());
            }
            }
            
            return result;*/

            return project1.getTitle().compareTo(project2.getTitle());
        }
    }

    protected List<String> getFiltersForActiveMembers() {
        List<String> filters;
        filters = new LinkedList<String>();

        filters.add("link.status = 'active'");

        return filters;
    }

    protected List<String> getFiltersForAssociatedMembers() {
        List<String> filters;
        filters = new LinkedList<String>();

        filters.add("link.status = 'associated'");

        return filters;
    }

    protected List<String> getFiltersForFormerMembers() {
        List<String> filters;
        filters = new LinkedList<String>();

        filters.add("link.status = 'former'");

        return filters;
    }

    protected List<String> getFiltersForOngoingProjects() {
        List<String> filters;
        Calendar today;

        filters = new LinkedList<String>();
        today = new GregorianCalendar();
        filters.add(String.format(
                "(projectbegin IS NOT null) AND (projectend > '%d-%02d-%02d' OR projectend IS null)",
                today.get(java.util.Calendar.YEAR),
                today.get(java.util.Calendar.MONTH)
                + 1,
                today.get(java.util.Calendar.DAY_OF_MONTH)));
        return filters;
    }

    protected List<String> getFiltersForFinishedProjects() {
        List<String> filters;
        Calendar today;

        filters = new LinkedList<String>();
        today = new GregorianCalendar();
        filters.add(String.format("projectend <= '%d-%02d-%02d'",
                                  today.get(java.util.Calendar.YEAR),
                                  today.get(java.util.Calendar.MONTH)
                                  + 1,
                                  today.get(java.util.Calendar.DAY_OF_MONTH)));
        return filters;
    }

    protected void generateMemberXML(final GenericPerson person,
                                     final Element parent,
                                     final String roleName,
                                     final String status,
                                     final PageState state) {
        Element memberElem = parent.newChildElement("member");

        memberElem.addAttribute("role", roleName);
        memberElem.addAttribute("status", status);
        memberElem.addAttribute("oid", person.getOID().toString());

        Element title = memberElem.newChildElement("title");
        title.setText(person.getTitle());

        if ((person.getTitlePre() != null)
            && !person.getTitlePre().isEmpty()) {
            Element titlePre = memberElem.newChildElement("titlePre");
            titlePre.setText(person.getTitlePre());
        }

        Element surname = memberElem.newChildElement("surname");
        surname.setText(person.getSurname());

        Element givenName = memberElem.newChildElement("givenname");
        givenName.setText(person.getGivenName());

        if ((person.getTitlePost() != null)
            && !person.getTitlePost().isEmpty()) {
            Element titlePost = memberElem.newChildElement("titlePost");
            titlePost.setText(person.getTitlePost());
        }

        if ((person.getContacts() != null)
            && (person.getContacts().size() > 0)) {
            GenericPersonContactCollection contacts;
            contacts = person.getContacts();

            Element contactsElem =
                    memberElem.newChildElement("contacts");

            while (contacts.next()) {
                generateContactXML(
                        contacts.getContact(),
                        contactsElem,
                        state,
                        contacts.getContactOrder(),
                        false);
            }
        }
    }

    protected void addMember(final GenericPerson person,
                             final String roleName,
                             final String status,
                             final List<MemberListItem> members) {
        MemberListItem listItem = new MemberListItem(person, roleName, status);

        if (!members.contains(listItem)) {
            members.add(listItem);
        }
    }

    protected void mergeMembers(
            final SciDepartmentSubDepartmentsCollection subDepartments,
            final List<MemberListItem> members,
            final List<String> filters) {
        while (subDepartments.next()) {
            SciDepartment subDepartment = subDepartments.getSubDepartment();
            GenericOrganizationalUnitPersonCollection departmentMembers;
            departmentMembers = subDepartment.getPersons();
            for (String filter : filters) {
                departmentMembers.addFilter(filter);
            }

            while (departmentMembers.next()) {
                addMember(departmentMembers.getPerson(),
                          departmentMembers.getRoleName(),
                          departmentMembers.getStatus(),
                          members);
            }

            SciDepartmentSubDepartmentsCollection subSubDepartments;
            subSubDepartments = subDepartment.getSubDepartments();

            if ((subSubDepartments != null)
                && (subSubDepartments.size() > 0)) {
                mergeMembers(subSubDepartments, members, filters);
            }
        }
    }

    protected void generateMembersListXML(final List<MemberListItem> members,
                                          final Element parent,
                                          final PageState state) {
        Set<MemberListItem> membersSet;
        List<MemberListItem> membersWithoutDoubles;
        membersSet = new HashSet<MemberListItem>(members);
        membersWithoutDoubles = new LinkedList<MemberListItem>(membersSet);

        Collections.sort(membersWithoutDoubles, new MemberListItemComparator());

        long pageNumber = getPageNumber(state);
        long pageCount = getPageCount(membersWithoutDoubles.size());
        long begin = getPaginatorBegin(pageNumber);
        long count = getPaginatorCount(begin, membersWithoutDoubles.size());
        long end = getPaginatorEnd(begin, count);
        pageNumber = normalizePageNumber(pageCount, pageNumber);

        createPaginatorElement(
                parent, pageNumber, pageCount, begin, end, count,
                membersWithoutDoubles.size());
        List<MemberListItem> membersWithoutDoublesToShow =
                             membersWithoutDoubles.subList((int) begin,
                                                           (int) end);

        Element membersWithoutDoublesElem = parent.newChildElement(
                "members");

        for (MemberListItem memberItem : membersWithoutDoublesToShow) {
            generateMemberXML(memberItem.getMember(),
                              membersWithoutDoublesElem,
                              memberItem.getRole(),
                              memberItem.getStatus(),
                              state);
        }
    }

    protected void generateProjectXML(final SciProject project,
                                      final Element parent,
                                      final PageState state) {
        Element projectElem = parent.newChildElement("project");
        projectElem.addAttribute("oid", project.toString());

        Element title = projectElem.newChildElement("title");
        title.setText(project.getTitle());

        if ((project.getAddendum() != null)
            && !(project.getAddendum().isEmpty())) {
            Element addendum = projectElem.newChildElement("addendum");
            addendum.setText(project.getAddendum());
        }

        if ((project.getProjectShortDescription() != null)
            && !(project.getProjectShortDescription().isEmpty())) {
            Element shortDesc = projectElem.newChildElement("shortDescription");
            shortDesc.setText(project.getProjectShortDescription());
        }

        GenericOrganizationalUnitPersonCollection members;
        members = project.getPersons();
        members.addOrder("surname asc, givenname asc");

        if (members.size() > 0) {
            Element membersElem = projectElem.newChildElement("mebers");

            while (members.next()) {
                generateMemberXML(members.getPerson(),
                                  membersElem,
                                  members.getRoleName(),
                                  members.getStatus(),
                                  state);
            }
        }

        GenericOrganizationalUnitContactCollection contacts;
        contacts = project.getContacts();

        if (contacts.size() > 0) {
            Element contactsElem = projectElem.newChildElement("contacts");

            while (contacts.next()) {
                generateContactXML(contacts.getContact(),
                                   contactsElem,
                                   state,
                                   Integer.toString(contacts.getContactOrder()),
                                   true);
            }
        }
    }

    protected void mergeProjects(
            final SciDepartmentSubDepartmentsCollection subDepartments,
            final List<SciProject> projects,
            final List<String> filters) {
        while (subDepartments.next()) {
            SciDepartmentProjectsCollection departmentProjects;
            departmentProjects = subDepartments.getSubDepartment().getProjects();

            if ((filters != null)
                && !(filters.isEmpty())) {
                for (String filter : filters) {
                    departmentProjects.addFilter(filter);
                }
            }

            while (departmentProjects.next()) {
                projects.add(departmentProjects.getProject());
            }

            SciDepartmentSubDepartmentsCollection subSubDepartments;
            subSubDepartments = subDepartments.getSubDepartment().
                    getSubDepartments();

            if ((subSubDepartments != null)
                && subSubDepartments.size() > 0) {
                mergeProjects(subSubDepartments, projects, filters);
            }
        }
    }

    /**
     * Create the XML for the list of publications, using the special
     * RelatedLinks passed by the caller. To avoid a dependency to the
     * sci-publications module, we are using only methods from
     * {@link DataObject}, {@link DomainObject}, {@link DataCollection} and
     * {@link DomainCollection}.
     *
     * @param links Links to the publications
     * @param parent  The parent XML element for the XML created by this method
     * @param state The current page state.
     */
    protected void generatePublicationsXML(final DataCollection links,
                                           final Element parent,
                                           final PageState state) {
        RelatedLink link;
        ContentItem publication;
        List<ContentItem> publications;

        publications = new ArrayList<ContentItem>();

        while (links.next()) {
            link = new RelatedLink(links.getDataObject());
            publication = link.getTargetItem();

            publications.add(publication);
        }

        Collections.sort(publications, new Comparator<ContentItem>() {

            public int compare(ContentItem o1, ContentItem o2) {
                Integer year1;
                Integer year2;

                if ((o1 == null) && o2 == null) {
                    return 0;
                } else if ((o1 == null) && (o2 != null)) {
                    return -1;
                } else if ((o1 != null) && o2 == null) {
                    return 1;
                }

                year1 = (Integer) o1.get("yearOfPublication");
                year2 = (Integer) o2.get("yearOfPublication");


                if (year1.compareTo(year2)
                    == 0) {
                    String title1;
                    String title2;

                    title1 = (String) o1.get("title");
                    title2 = (String) o2.get("title");

                    return title1.compareTo(title2);
                } else {
                    return (year1.compareTo(year2)) * -1;
                }
            }
        });

        long pageNumber = getPageNumber(state);
        long pageCount = getPageCount(publications.size());
        long begin = getPaginatorBegin(pageNumber);
        long count = getPaginatorCount(begin, publications.size());
        long end = getPaginatorEnd(begin, count);
        pageNumber = normalizePageNumber(pageCount, pageNumber);

        createPaginatorElement(parent, pageNumber, pageCount, begin, end, count,
                               end);
        List<ContentItem> publicationsToShow = publications.subList((int) begin,
                                                                    (int) end);

        for (ContentItem pub : publicationsToShow) {
            generatePublicationXML(pub, parent, state);
        }
    }

    protected void generatePublicationXML(final ContentItem publication,
                                          final Element parent,
                                          final PageState state) {
        Element publicationElem;
        ContentItemXMLRenderer renderer;

        if (publication == null) {
            return;
        }
        
        publicationElem = parent.newChildElement("publications");

        renderer = new ContentItemXMLRenderer(publicationElem);
        renderer.setWrapAttributes(true);

        renderer.walk(publication, SimpleXMLGenerator.class.getName());
    }
}
