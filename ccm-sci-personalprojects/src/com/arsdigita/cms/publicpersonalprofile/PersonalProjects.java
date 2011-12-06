package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.GenericContactEntryCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * 
 * @author Jens Pelzetter (jensp)
 * @version $Id$
 */
public class PersonalProjects implements ContentGenerator {

    /*
     * Note 2011-10-09: This class contains some code which has been copied from 
     * SciOrganizationBasePanel and GenericOrganizationalUnitPanel. This copied
     * code will be reworked after the ccm-sci-types-organization module has 
     * been refactored and split into independent content types.
     */
    private final static String CURRENT_PROJECTS = "currentProjects";
    private final static String FINISHED_PROJECTS = "finishedProjects";
    private final static PersonalProjectsConfig config =
            new PersonalProjectsConfig();
    private final static Logger logger =
            Logger.getLogger(PersonalProjects.class);

    static {
        config.load();
    }

    public void generateContent(final Element parent,
            final GenericPerson person,
            final PageState state) {
        final List<SciProject> projects = collectProjects(person);

        final Element personalProjectsElem = parent.newChildElement(
                "personalProjects");

        if ((projects == null) || projects.isEmpty()) {
            personalProjectsElem.newChildElement("noProjects");

            return;
        } else {
            final List<SciProject> currentProjects = new ArrayList<SciProject>();
            final List<SciProject> finishedProjects =
                    new ArrayList<SciProject>();

            processProjects(projects, currentProjects, finishedProjects);
            generateGroupsXml(personalProjectsElem, currentProjects, finishedProjects);
            generateProjectsXml(personalProjectsElem,
                    currentProjects,
                    finishedProjects,
                    state);
        }
    }

    private List<SciProject> collectProjects(final GenericPerson person) {
        final List<SciProject> projects = new ArrayList<SciProject>();

        final DataCollection collection = (DataCollection) person.get(
                "organizationalunit");
        if (Kernel.getConfig().languageIndependentItems()) {
            FilterFactory ff = collection.getFilterFactory();
            Filter filter = ff.or().
                    addFilter(ff.equals("language", com.arsdigita.globalization.GlobalizationHelper.getNegotiatedLocale().getLanguage())).
                    addFilter(ff.and().
                    addFilter(ff.equals("language", GlobalizationHelper.LANG_INDEPENDENT)).
                    addFilter(ff.notIn("parent", "com.arsdigita.london.navigation.getParentIDsOfMatchedItems").set("language", com.arsdigita.globalization.GlobalizationHelper.getNegotiatedLocale().getLanguage())));
            collection.addFilter(filter);
        } else {
            collection.addEqualsFilter("language", com.arsdigita.globalization.GlobalizationHelper.getNegotiatedLocale().getLanguage());
        }
        DomainObject obj;
        while (collection.next()) {
            obj = DomainObjectFactory.newInstance(collection.getDataObject());
            if (obj instanceof SciProject) {
                projects.add((SciProject) obj);
            }
        }

        if (person.getAlias() != null) {
            collectProjects(person.getAlias(), projects);

        }


        return projects;
    }

    private void collectProjects(final GenericPerson alias,
            final List<SciProject> projects) {
        final DataCollection collection = (DataCollection) alias.get(
                "organizationalunit");
        DomainObject obj;
        while (collection.next()) {
            obj = DomainObjectFactory.newInstance(collection.getDataObject());
            if (obj instanceof SciProject) {
                projects.add((SciProject) obj);
            }
        }

        if (alias.getAlias() != null) {
            collectProjects(alias.getAlias(), projects);

        }
    }

    private void processProjects(final List<SciProject> projects,
            final List<SciProject> currentProjects,
            final List<SciProject> finishedProjects) {
        final Calendar today = new GregorianCalendar();
        final Date todayDate = today.getTime();
        for (SciProject project : projects) {
            if ((project.getEnd() != null)
                    && project.getEnd().before(todayDate)) {
                finishedProjects.add(project);
            } else {
                currentProjects.add(project);
            }
        }

        final ProjectComparator comparator = new ProjectComparator();
        Collections.sort(currentProjects, comparator);
        Collections.sort(finishedProjects, comparator);
    }

    private void generateGroupsXml(final Element parent,
            final List<SciProject> currentProjects,
            final List<SciProject> finishedProjects) {
        final Element availableGroups = parent.newChildElement(
                "availableProjectGroups");

        if (currentProjects.size() > 0) {
            createAvailableProjectGroupXml(availableGroups, CURRENT_PROJECTS);
        }

        if (finishedProjects.size() > 0) {
            createAvailableProjectGroupXml(availableGroups, FINISHED_PROJECTS);
        }
    }

    private void createAvailableProjectGroupXml(final Element parent,
            final String name) {
        final Element group = parent.newChildElement("availableProjectGroup");
        group.addAttribute("name", name);
    }

    private void generateProjectsXml(final Element parent,
            final List<SciProject> currentProjects,
            final List<SciProject> finishedProjects,
            final PageState state) {
        final Element projectsElem = parent.newChildElement("projects");

        final int numberOfProjects = currentProjects.size()
                + finishedProjects.size();
        final int groupSplit = config.getGroupSplit();

        if (numberOfProjects < groupSplit) {
            projectsElem.addAttribute("all", "all");

            generateProjectsGroupXml(projectsElem,
                    CURRENT_PROJECTS,
                    currentProjects,
                    state);
            generateProjectsGroupXml(projectsElem,
                    FINISHED_PROJECTS,
                    finishedProjects,
                    state);
        } else {
            final HttpServletRequest request = state.getRequest();

            String groupToShow = request.getParameter("group");
            if (groupToShow == null) {
                groupToShow = CURRENT_PROJECTS;
            }

            if (currentProjects.isEmpty()
                    && CURRENT_PROJECTS.equals(groupToShow)) {
                groupToShow = FINISHED_PROJECTS;
            }

            if (CURRENT_PROJECTS.equals(groupToShow)) {
                generateProjectsGroupXml(projectsElem,
                        CURRENT_PROJECTS,
                        currentProjects,
                        state);
            } else if (FINISHED_PROJECTS.equals(groupToShow)) {
                generateProjectsGroupXml(projectsElem,
                        FINISHED_PROJECTS,
                        finishedProjects,
                        state);
            }
        }

    }

    private void generateProjectsGroupXml(final Element projectsElem,
            final String groupName,
            final List<SciProject> projects,
            final PageState state) {
        if (projects == null) {
            return;
        }

        final Element groupElem = projectsElem.newChildElement("projectGroup");
        groupElem.addAttribute("name", groupName);

        for (SciProject project : projects) {
            generateProjectXml(groupElem, project, state);
        }
    }

    private void generateProjectXml(final Element projectGroupElem,
            final SciProject project,
            final PageState state) {
        /*final PublicPersonalProfileXmlGenerator generator =
        new PublicPersonalProfileXmlGenerator(
        project);
        generator.generateXML(state, projectGroupElem, "");*/
        Element projectElem = projectGroupElem.newChildElement("project");
        projectElem.addAttribute("oid", project.getOID().toString());

        Element title = projectElem.newChildElement("title");
        title.setText(project.getTitle());

        //Element beginElem = projectElem.newChildElement("projectbegin");

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

    protected void generateContactXML(
            final String contactType,
            final GenericPerson person,
            final GenericContactEntryCollection contactEntries,
            final GenericAddress address,
            final Element parent,
            final PageState state,
            final String order,
            final boolean withPerson) {
        Element contactElem = parent.newChildElement("contact");
        contactElem.addAttribute("order", order);

        //Element title = contactElem.newChildElement("title");
        //title.setText(contact.getTitle());

        Element typeElem = contactElem.newChildElement("type");
        typeElem.setText(contactType);

        if (withPerson) {
            if (person != null) {
                Element personElem = contactElem.newChildElement("person");
                if ((person.getTitlePre() != null) && !person.getTitlePre().
                        isEmpty()) {
                    Element titlePre =
                            personElem.newChildElement("titlePre");
                    titlePre.setText(person.getTitlePre());
                }

                Element givenName = contactElem.newChildElement("givenname");
                givenName.setText(person.getGivenName());

                Element surname = contactElem.newChildElement("surname");
                surname.setText(person.getSurname());

                if ((person.getTitlePost() != null)
                        && !person.getTitlePost().isEmpty()) {
                    Element titlePost = contactElem.newChildElement(
                            "titlePost");
                    titlePost.setText(person.getTitlePost());
                }
            }
        }

        if ((contactEntries != null)
                && (contactEntries.size() > 0)) {
            Element contactEntriesElem =
                    contactElem.newChildElement("contactEntries");
            while (contactEntries.next()) {
                GenericContactEntry contactEntry =
                        contactEntries.getContactEntry();
                Element contactEntryElem =
                        contactEntriesElem.newChildElement(
                        "contactEntry");
                contactEntryElem.addAttribute("key",
                        contactEntry.getKey());
                Element valueElem = contactEntryElem.newChildElement(
                        "value");
                valueElem.setText(contactEntry.getValue());

                if ((contactEntry.getDescription() != null)
                        && !contactEntry.getDescription().isEmpty()) {
                    Element descElem = contactEntryElem.newChildElement(
                            "description");
                    descElem.setText(contactEntry.getDescription());
                }
            }
        }

        if (address != null) {
            Element addressElem = contactElem.newChildElement(
                    "address");
            Element postalCode = addressElem.newChildElement(
                    "postalCode");
            postalCode.setText(address.getPostalCode());
            Element city = addressElem.newChildElement("city");
            city.setText(address.getCity());
            Element data = addressElem.newChildElement("address");
            data.setText(address.getAddress());
            Element country = addressElem.newChildElement("country");
            country.setText(address.getIsoCountryCode());
            Element theState = addressElem.newChildElement("state");
            theState.setText(address.getState());
        }
    }

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

    private class ProjectComparator implements Comparator<SciProject> {

        public int compare(final SciProject project1,
                final SciProject project2) {
            return project1.getTitle().compareTo(project2.getTitle());
        }
    }
}
