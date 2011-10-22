package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSubordinateCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectSummaryTab implements GenericOrgaUnitTab {

    private final Logger logger = Logger.getLogger(SciProjectSummaryTab.class);
    private final static SciProjectSummaryTabConfig config =
                                                    new SciProjectSummaryTabConfig();

    static {
        config.load();
    }

    public boolean hasData(final GenericOrganizationalUnit orgaunit) {
        //Some of the data shown by this tab will ever be there
        return true;
    }

    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        if (!(orgaunit instanceof SciProject)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of SciProject."
                    + "The provided object is of type '%s'.",
                    orgaunit.getClass().getName()));
        }

        final long start = System.currentTimeMillis();
        final SciProject project = (SciProject) orgaunit;

        final Element projectSummaryElem = parent.newChildElement(
                "projectSummary");

        generateBasicDataXml(project, parent);
        
        if(config.isShowingMembers()) {
            generateMembersXml(project, parent, state);
        }
        
        if (config.isShowingContacts()) {
            generateContactsXml(project, parent, state);
        }
        
        if (config.isShowingSubProjects()) {
            generateSubProjectsXml(project, parent, state);
        }


        logger.debug(String.format("Generated XML for summary tab of project "
                                   + "'%s' in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    /**
     * Generates the XML for the basic data (addendum, begin, end, shortDesc)
     * @param project
     * @param parent  
     */
    protected void generateBasicDataXml(
            final SciProject project,
            final Element parent) {
        final long start = System.currentTimeMillis();
        final Element addendumElem = parent.newChildElement("addendum");
        if ((project.getAddendum() != null) && !project.getAddendum().isEmpty()) {
            addendumElem.setText(project.getAddendum());
        }

        final Element lifeSpanElem = parent.newChildElement("lifeSpan");
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (project.getBegin() != null) {
            lifeSpanElem.addAttribute("begin",
                                      dateFormat.format(project.getBegin()));
        }
        if (project.getEnd() != null) {
            lifeSpanElem.addAttribute("end",
                                      dateFormat.format(project.getEnd()));
        }

        if ((project.getProjectShortDescription() != null)
            && !project.getProjectShortDescription().isEmpty()) {
            final Element shortDescElem = parent.newChildElement("shortDesc");
            shortDescElem.setText(project.getProjectShortDescription());
        }

        logger.debug(String.format("Generated basic data XML for project '%s' "
                                   + "in %d ms",
                                   project.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateMembersXml(final SciProject project,
                                      final Element parent,
                                      final PageState state) {
        final long start = System.currentTimeMillis();

        if ((project.getPersons() == null) || project.getPersons().isEmpty()) {
            return;
        }

        final Element membersElem = parent.newChildElement("members");

        if (config.isMergingMembers()) {
            final DataQuery subProjectsQuery =
                            SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
            subProjectsQuery.setParameter("orgaunitId",
                                          project.getID().toString());
            subProjectsQuery.setParameter("assocType",
                                          SciProjectSubProjectsStep.ASSOC_TYPE);

            final DataQuery personsQuery = SessionManager.getSession().
                    retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfMembersOfOrgaUnits");

            final StringBuffer personsFilter = new StringBuffer();
            while (subProjectsQuery.next()) {
                if (personsFilter.length() > 0) {
                    personsFilter.append(" or ");
                }
                personsFilter.append(String.format("orgaunitId = %s",
                                                   subProjectsQuery.get(
                        "orgaunitId").toString()));
            }
            personsQuery.addFilter(personsFilter.toString());

            personsQuery.addOrder("surname");
            personsQuery.addOrder("givenname");

            while (personsQuery.next()) {
                generateMemberXml((BigDecimal) personsQuery.get("memberId"),
                                  parent,
                                  state);
            }
        } else {
            final GenericOrganizationalUnitPersonCollection members = project.
                    getPersons();

            members.addOrder("surname");
            members.addOrder("givenname");

            while (members.next()) {
                generateMemberXml(members.getPerson(), membersElem, state);
            }
        }

        logger.debug(String.format("Generated members XML for project '%s'"
                                   + "in '%d ms'. MergeMembers is set to '%b'.",
                                   project.getName(),
                                   System.currentTimeMillis() - start,
                                   config.isMergingMembers()));
    }

    protected void generateMemberXml(final BigDecimal memberId,
                                     final Element parent,
                                     final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericPerson member = new GenericPerson(memberId);
        logger.debug(String.format("Got domain object for member '%s' "
                                   + "in %d ms.",
                                   member.getFullName(),
                                   System.currentTimeMillis() - start));
        generateMemberXml(member, parent, state);
    }

    protected void generateMemberXml(final GenericPerson member,
                                     final Element parent,
                                     final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(member);
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for member '%s' in %d ms.",
                                   member.getFullName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateContactsXml(final SciProject project,
                                       final Element parent,
                                       final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericOrganizationalUnitContactCollection contacts = project.
                getContacts();

        final Element contactsElem = parent.newChildElement("contacts");

        while (contacts.next()) {
            generateContactXml(contacts.getContact(), contactsElem, state);
        }
        logger.debug(String.format("Generated XML for contacts of project '%s'"
                                   + " in %d ms.",
                                   project.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateContactXml(final GenericContact contact,
                                      final Element parent,
                                      final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(contact);
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for contact '%s' in %d ms.",
                                   contact.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateSubProjectsXml(final SciProject project,
                                          final Element parent,
                                          final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericOrganizationalUnitSubordinateCollection subProjects =
                                                             project.
                getSubordinateOrgaUnits();
        subProjects.addFilter(
                String.format("link.assocType = '%s'",
                              SciProjectSubProjectsStep.ASSOC_TYPE));
        while (subProjects.next()) {
            generateSubProjectXml(
                    (SciProject) subProjects.getGenericOrganizationalUnit(),
                    parent,
                    state);
        }
        logger.debug(String.format("Generated XML for subprojects of "
                                   + "project '%s' in %d ms.",
                                   project.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateSubProjectXml(final SciProject subProject,
                                         final Element parent,
                                         final PageState state) {
        final long start = System.currentTimeMillis();
        final Element subProjectElem = parent.newChildElement("subProject");
        final Element subProjectTitle = subProjectElem.newChildElement("title");
        subProjectTitle.setText(subProject.getTitle());
        logger.debug(String.format("Generated XML for subproject '%s' in"
                                   + "%d ms",
                                   subProject.getName(),
                                   System.currentTimeMillis() - start));
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
