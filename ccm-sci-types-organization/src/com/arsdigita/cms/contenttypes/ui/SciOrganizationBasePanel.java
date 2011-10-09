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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentProjectsCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentSubDepartmentsCollection;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.xml.Element;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

        private OID oid;
        private String surname;
        private String givenName;
        private String titlePre;
        private String titlePost;
        private Date birthdate;
        private String gender;
        private DataCollection contacts;
        //private GenericPerson member;
        private String role;
        private String status;

        public MemberListItem(final GenericPerson member,
                              final String role,
                              final String status) {
            /*this.member = member;
            this.role = role;
            this.status = status;*/
            this(member.getOID(),
                 member.getSurname(),
                 member.getGivenName(),
                 member.getTitlePre(),
                 member.getTitlePost(),
                 member.getBirthdate(),
                 member.getGender(),
                 null,
                 role,
                 status);

        }

        public MemberListItem(final OID oid,
                              final String surname,
                              final String givenName,
                              final String titlePre,
                              final String titlePost,
                              final Date birthdate,
                              final String gender,
                              final DataCollection contacts,
                              final String role,
                              final String status) {
            this.oid = oid;
            this.surname = surname;
            this.givenName = givenName;
            this.titlePre = titlePre;
            this.titlePost = titlePost;
            this.birthdate = birthdate;
            this.gender = gender;
            this.contacts = contacts;
            this.role = role;
            this.status = status;
        }

        /*public GenericPerson getMember() {
        return member;
        }*/
        public OID getOID() {
            return oid;
        }

        public Date getBirthdate() {
            return birthdate;
        }

        public DataCollection getContacts() {
            return contacts;
        }

        public String getGender() {
            return gender;
        }

        public String getGivenName() {
            return givenName;
        }

        public String getSurname() {
            return surname;
        }

        public String getTitlePost() {
            return titlePost;
        }

        public String getTitlePre() {
            return titlePre;
        }

        public String getRole() {
            return role;
        }

        public String getStatus() {
            return status;
        }

        /*@Override
        public boolean equals(Object obj) {            
        if (obj instanceof MemberListItem) {
        MemberListItem other = (MemberListItem) obj;
        
        return member.equals(other.getMember());
        } else {
        return false;
        }                        
        }*/
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MemberListItem other = (MemberListItem) obj;
            if ((this.surname == null) ? (other.surname != null)
                : !this.surname.equals(other.surname)) {
                return false;
            }
            if ((this.givenName == null) ? (other.givenName != null)
                : !this.givenName.equals(other.givenName)) {
                return false;
            }
            if ((this.titlePre == null) ? (other.titlePre != null)
                : !this.titlePre.equals(other.titlePre)) {
                return false;
            }
            if ((this.titlePost == null) ? (other.titlePost != null)
                : !this.titlePost.equals(other.titlePost)) {
                return false;
            }
            if (this.birthdate != other.birthdate && (this.birthdate == null
                                                      || !this.birthdate.equals(
                                                      other.birthdate))) {
                return false;
            }
            if ((this.gender == null) ? (other.gender != null)
                : !this.gender.equals(other.gender)) {
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
            return true;
        }

        /*@Override
        public int hashCode() {       
        return member.hashCode();
        }*/
        @Override
        public int hashCode() {
            int hash = 3;
            hash =
            41 * hash + (this.surname != null ? this.surname.hashCode() : 0);
            hash =
            41 * hash + (this.givenName != null ? this.givenName.hashCode() : 0);
            hash =
            41 * hash + (this.titlePre != null ? this.titlePre.hashCode() : 0);
            hash =
            41 * hash + (this.titlePost != null ? this.titlePost.hashCode() : 0);
            hash =
            41 * hash + (this.birthdate != null ? this.birthdate.hashCode() : 0);
            hash =
            41 * hash + (this.gender != null ? this.gender.hashCode() : 0);
            hash = 41 * hash + (this.role != null ? this.role.hashCode() : 0);
            hash =
            41 * hash + (this.status != null ? this.status.hashCode() : 0);
            return hash;
        }
    }

    protected class MemberListItemComparator
            implements Comparator<MemberListItem> {

        public int compare(MemberListItem member1, MemberListItem member2) {
            int result = 0;

            /*result =
            member1.getMember().getSurname().compareToIgnoreCase(member2.
            getMember().
            getSurname());
            
            if (result == 0) {
            result = member1.getMember().getGivenName().compareTo(
            member2.getMember().getGivenName());
            }*/

            result =
            member1.getSurname().compareToIgnoreCase(member2.getSurname());

            if (result == 0) {
                result =
                member1.getGivenName().compareTo(member2.getGivenName());
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

    protected List<String> getFiltersForFormerAssociatedMembers() {
        List<String> filters;
        filters = new LinkedList<String>();

        filters.add("link.status = 'associatedFormer'");

        return filters;
    }

    protected List<String> getFiltersForFormerMembers() {
        List<String> filters;
        filters = new LinkedList<String>();

        //filters.add("link.status = 'former'");        
        filters.add("lower(link.status) like lower('%former%')");

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

    protected void generateMemberXML(final MemberListItem person,
                                     final Element parent,
                                     final String roleName,
                                     final String status,
                                     final PageState state) {
        Element memberElem = parent.newChildElement("member");

        memberElem.addAttribute("role", roleName);
        memberElem.addAttribute("status", status);
        memberElem.addAttribute("oid", person.getOID().toString());

        //Element title = memberElem.newChildElement("title");
        //title.setText(person.getTitle());

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
            contacts = new GenericPersonContactCollection(person.getContacts());

            Element contactsElem =
                    memberElem.newChildElement("contacts");

            while (contacts.next()) {
                generateContactXML(
                        contacts.getContactType(),
                        contacts.getPerson(),
                        contacts.getContactEntries(),
                        contacts.getAddress(),
                        contactsElem,
                        state,
                        contacts.getContactOrder(),
                        false);
            }
        }
    }

    @Deprecated
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
                        contacts.getContactType(),
                        contacts.getPerson(),
                        contacts.getContactEntries(),
                        contacts.getAddress(),
                        contactsElem,
                        state,
                        contacts.getContactOrder(),
                        false);
            }
        }
    }

    protected void addMember(
            final GenericOrganizationalUnitPersonCollection persons,
            final List<MemberListItem> members) {
        addMember(persons.getOID(),
                  persons.getSurname(),
                  persons.getGivenName(),
                  persons.getTitlePre(),
                  persons.getTitlePost(),
                  persons.getBirthdate(),
                  persons.getGender(),
                  persons.getContacts(),
                  persons.getRoleName(),
                  persons.getStatus(),
                  members);
    }

    private void addMember(final OID oid,
                           final String surname,
                           final String givenName,
                           final String titlePre,
                           final String titlePost,
                           final Date birthdate,
                           final String gender,
                           final DataCollection contacts,
                           final String role,
                           final String status,
                           final List<MemberListItem> members) {
        MemberListItem listItem = new MemberListItem(oid,
                                                     surname,
                                                     givenName,
                                                     titlePre,
                                                     titlePost,
                                                     birthdate,
                                                     gender,
                                                     contacts,
                                                     role,
                                                     status);

        if (!members.contains(listItem)) {
            members.add(listItem);
        }
    }

    @Deprecated
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
                addMember(departmentMembers.getOID(),
                          departmentMembers.getSurname(),
                          departmentMembers.getGivenName(),
                          departmentMembers.getTitlePre(),
                          departmentMembers.getTitlePost(),
                          departmentMembers.getBirthdate(),
                          departmentMembers.getGender(),
                          null,
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
            generateMemberXML(memberItem,
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
        projectElem.addAttribute("oid", project.getOID().toString());

        Element title = projectElem.newChildElement("title");
        title.setText(project.getTitle());

        Element beginElem = projectElem.newChildElement("projectbegin");

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
            Element membersElem = projectElem.newChildElement("members");

            while (members.next()) {
                generateMemberXML(new MemberListItem(members.getOID(),
                                                     members.getSurname(),
                                                     members.getGivenName(),
                                                     members.getTitlePre(),
                                                     members.getTitlePost(),
                                                     members.getBirthdate(),
                                                     members.getGender(),
                                                     null, members.getRoleName(),
                                                     members.getStatus()),
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
                generateContactXML(contacts.getContactType(),
                                   contacts.getPerson(),
                                   contacts.getContactEntries(),
                                   contacts.getAddress(),
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
    /* protected void generatePublicationsXML(final DataCollection links,
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
    }*/
}
