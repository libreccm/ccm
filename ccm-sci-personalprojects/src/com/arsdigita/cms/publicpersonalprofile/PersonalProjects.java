package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.GenericContactEntryCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectBundle;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.xml.Element;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
                                final PageState state,
                                final String profileLanguage) {
        final List<SciProjectBundle> projects = collectProjects(person,
                                                                profileLanguage);

        final Element personalProjectsElem = parent.newChildElement(
                "personalProjects");

        if ((projects == null) || projects.isEmpty()) {
            personalProjectsElem.newChildElement("noProjects");
        } else {
            final List<SciProjectBundle> currentProjects =
                                         new ArrayList<SciProjectBundle>();
            final List<SciProjectBundle> finishedProjects =
                                         new ArrayList<SciProjectBundle>();

            String sortBy = config.getSortBy();
            String sortByParam = state.getRequest().getParameter("sortBy");
            if ((sortByParam != null) && !(sortByParam.trim().isEmpty())) {
                sortBy = sortByParam;
            }

            processProjects(projects, currentProjects, finishedProjects, sortBy);
            generateGroupsXml(personalProjectsElem, currentProjects,
                              finishedProjects);
            generateProjectsXml(personalProjectsElem,
                                currentProjects,
                                finishedProjects,
                                state);
        }
    }

    private List<SciProjectBundle> collectProjects(final GenericPerson person,
                                                   final String language) {
        final List<SciProjectBundle> projects =
                                     new LinkedList<SciProjectBundle>();
        final DataCollection collection = (DataCollection) person.
                getGenericPersonBundle().get("organizationalunits");     
        collection.addEqualsFilter("version", "live");
        DomainObject obj;
        while (collection.next()) {
            obj = DomainObjectFactory.newInstance(collection.getDataObject());
            if (obj instanceof SciProjectBundle) {
                projects.add((SciProjectBundle) obj);
            }
        }
     
        if (person.getAlias() != null) {
            collectProjects(person.getAlias(), projects, language);

        }
        return projects;
    }

    private void collectProjects(final GenericPerson alias,
                                 final List<SciProjectBundle> projects,
                                 final String language) {
        final DataCollection collection = (DataCollection) alias.
                getGenericPersonBundle().get("organizationalunits");    
        collection.addEqualsFilter("version", "live");
        DomainObject obj;
        while (collection.next()) {
            obj = DomainObjectFactory.newInstance(collection.getDataObject());
            if (obj instanceof SciProjectBundle) {                
                projects.add((SciProjectBundle) obj);
            }
        }

        if (alias.getAlias() != null) {
            collectProjects(alias.getAlias(), projects, language);

        }
    }

    private void processProjects(final List<SciProjectBundle> projects,
                                 final List<SciProjectBundle> currentProjects,
                                 final List<SciProjectBundle> finishedProjects,
                                 final String sortBy) {
        final Calendar today = new GregorianCalendar();
        final Date todayDate = today.getTime();
        for (SciProjectBundle project : projects) {
            if ((project.getProject().getEnd() != null)
                && project.getProject().getEnd().before(todayDate)) {
                finishedProjects.add(project);
            } else {
                currentProjects.add(project);
            }
        }

        Comparator<SciProjectBundle> comparator;
        if ("date".equals(sortBy)) {
            comparator = new ProjectByDateComparator();
        } else {
            comparator = new ProjectByTitleComparator();

        }
        Collections.sort(currentProjects, comparator);
        Collections.sort(finishedProjects, comparator);
    }

    private void generateGroupsXml(final Element parent,
                                   final List<SciProjectBundle> currentProjects,
                                   final List<SciProjectBundle> finishedProjects) {
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
                                     final List<SciProjectBundle> currentProjects,
                                     final List<SciProjectBundle> finishedProjects,
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
                                          final List<SciProjectBundle> projects,
                                          final PageState state) {
        if (projects == null) {
            return;
        }

        final Element groupElem = projectsElem.newChildElement("projectGroup");
        groupElem.addAttribute("name", groupName);

        for (SciProjectBundle project : projects) {
            generateProjectXml(groupElem, 
                               project.getProject(GlobalizationHelper.getNegotiatedLocale().getLanguage()), 
                               state);
        }
    }

    private void generateProjectXml(final Element projectGroupElem,
                                    final SciProject project,
                                    final PageState state ) {
        final XmlGenerator generator = new XmlGenerator(project);
        generator.setItemElemName("project", "");
        generator.setListMode(true);
        generator.generateXML(state, projectGroupElem, "");
    }
    
    private void generateProjectXml(final Element projectGroupElem,
                                    final SciProjectBundle projectBundle,
                                    final PageState state) {                        
        final SciProject project = projectBundle.getProject(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
       
        if (project == null) {
            return;
        }
        
        Element projectElem = projectGroupElem.newChildElement("project");
        projectElem.addAttribute("oid", project.getOID().toString());

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

        if ((project.getBegin() != null)) {
            final Element durationElem = projectElem.newChildElement("duration");
            final Element beginElem = durationElem.newChildElement("begin");
            addDateAttributes(beginElem, project.getBegin());
            final Element beginSkipDayElem = durationElem.newChildElement(
                    "beginSkipDay");
            if (project.getBeginSkipDay()) {
                beginSkipDayElem.setText("true");
            } else {
                beginSkipDayElem.setText("false");
            }
            final Element beginSkipMonthElem = durationElem.newChildElement(
                    "beginSkipMonth");
            if (project.getBeginSkipMonth()) {
                beginSkipMonthElem.setText("true");
            } else {
                beginSkipMonthElem.setText("false");
            }


            if (project.getEnd() != null) {
                final Element endElement = durationElem.newChildElement("end");
                addDateAttributes(endElement, project.getEnd());
                final Element endSkipDayElem = durationElem.newChildElement(
                        "endSkipDay");
                if (project.getEndSkipDay()) {
                    endSkipDayElem.setText("true");
                } else {
                    endSkipDayElem.setText("false");
                }
                final Element endSkipMonthElem = durationElem.newChildElement(
                        "endSkipMonth");
                if (project.getEndSkipMonth()) {
                    endSkipMonthElem.setText("true");
                } else {
                    endSkipMonthElem.setText("false");
                }
            }
        }

        GenericOrganizationalUnitPersonCollection members;
        members = project.getPersons();
        //members.addOrder("surname asc, givenname asc");

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

        Element givenName = memberElem.newChildElement("givenName");
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

    private class ProjectByTitleComparator
            implements Comparator<SciProjectBundle> {

        public int compare(final SciProjectBundle project1,
                           final SciProjectBundle project2) {
            return project1.getProject().getTitle().compareTo(project2.
                    getProject().getTitle());
        }
    }

    private class ProjectByDateComparator implements
            Comparator<SciProjectBundle> {

        public int compare(final SciProjectBundle project1,
                           final SciProjectBundle project2) {
            int ret = 0;
            if ((project2.getProject().getBegin() != null) && (project1.
                                                               getProject().
                                                               getBegin()
                                                               != null)) {
                ret = project2.getProject().getBegin().compareTo(project1.
                        getProject().getBegin());
            }
            if ((ret == 0)
                && (project2.getProject().getEnd() != null)
                && (project1.getProject().getEnd() != null)) {
                ret = project2.getProject().getEnd().compareTo(project1.
                        getProject().getEnd());
            }
            if (ret == 0) {
                ret = project1.getProject().getTitle().compareTo(project2.
                        getProject().getTitle());
            }

            return ret;
        }
    }

    private void addDateAttributes(final Element elem, final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        elem.addAttribute("year",
                          Integer.toString(cal.get(
                Calendar.YEAR)));
        elem.addAttribute("month",
                          Integer.toString(cal.get(
                Calendar.MONTH) + 1));
        elem.addAttribute("day",
                          Integer.toString(cal.get(
                Calendar.DAY_OF_MONTH)));
        elem.addAttribute("hour",
                          Integer.toString(cal.get(
                Calendar.HOUR_OF_DAY)));
        elem.addAttribute("minute",
                          Integer.toString(cal.get(
                Calendar.MINUTE)));
        elem.addAttribute("second",
                          Integer.toString(cal.get(
                Calendar.SECOND)));

        final Locale negLocale = GlobalizationHelper.getNegotiatedLocale();
        final DateFormat dateFormat = DateFormat.getDateInstance(
                DateFormat.MEDIUM, negLocale);
        final DateFormat longDateFormat = DateFormat.getDateInstance(
                DateFormat.LONG, negLocale);
        final DateFormat timeFormat = DateFormat.getDateInstance(
                DateFormat.SHORT, negLocale);
        elem.addAttribute("date", dateFormat.format(date));
        elem.addAttribute("longDate", longDateFormat.format(date));
        elem.addAttribute("time", timeFormat.format(date));
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
