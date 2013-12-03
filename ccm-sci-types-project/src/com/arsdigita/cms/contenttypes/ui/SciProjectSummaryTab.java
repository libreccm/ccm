package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSubordinateCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSuperiorCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectSponsorCollection;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * Summary tab for projects, displays lifespan of the project, the short
 * description, the project team (aka members), a contact, the involved
 * organizations and the information about the funding of the project.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciProjectSummaryTab implements GenericOrgaUnitTab {

    private final Logger logger = Logger.getLogger(SciProjectSummaryTab.class);
    private final static SciProjectSummaryTabConfig config =
                                                    new SciProjectSummaryTabConfig();
    private String key;

    static {
        config.load();
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
        //Some of the data shown by this tab will ever be there
        return true;
    }

    @Override
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

        generateBasicDataXml(project, projectSummaryElem);

        if (config.isShowingMembers()) {
            generateMembersXml(project, projectSummaryElem, state);
        }

        if (config.isShowingContacts()) {
            generateContactsXml(project, projectSummaryElem, state);
        }

        if (config.isShowingInvolvedOrgas()) {
            generateInvolvedOrgasXml(project, projectSummaryElem, state);
        }

        if (config.isShowingSubProjects()) {
            generateSubProjectsXml(project, projectSummaryElem, state);
        }

        if (config.isShowingFunding()) {
            generateFundingXml(project, projectSummaryElem, state);
        }

        logger.debug(String.format("Generated XML for summary tab of project "
                                   + "'%s' in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    /**
     * Generates the XML for the basic data (addendum, begin, end, shortDesc)
     *
     * @param project
     * @param parent
     */
    protected void generateBasicDataXml(final SciProject project,
                                        final Element parent) {
        final long start = System.currentTimeMillis();
        if ((project.getAddendum() != null)
            && !project.getAddendum().trim().isEmpty()) {
            final Element addendumElem = parent.newChildElement("addendum");
            addendumElem.setText(project.getAddendum());
        }

        if ((project.getBegin() != null) || (project.getEnd() != null)) {
            final Element lifeSpanElem = parent.newChildElement("lifeSpan");

            if (project.getBegin() != null) {
                final Element beginElem = lifeSpanElem.newChildElement("begin");
                addDateAttributes(beginElem, project.getBegin());

                final Element beginSkipMonthElem = lifeSpanElem.newChildElement(
                        "beginSkipMonth");
                beginSkipMonthElem.setText(
                        project.getBeginSkipMonth().toString());

                final Element beginSkipDayElem = lifeSpanElem.newChildElement(
                        "beginSkipDay");
                beginSkipDayElem.setText(project.getBeginSkipDay().toString());
            }
            if (project.getEnd() != null) {
                final Element endElem = lifeSpanElem.newChildElement("end");
                addDateAttributes(endElem, project.getEnd());

                final Element endSkipMonthElem = lifeSpanElem.newChildElement(
                        "endSkipMonth");
                endSkipMonthElem.setText(project.getEndSkipMonth().toString());

                final Element endSkipDayElem = lifeSpanElem.newChildElement(
                        "endSkipDay");
                endSkipDayElem.setText(project.getEndSkipDay().toString());
            }
        }

        if ((project.getProjectShortDescription() != null)
            && !project.getProjectShortDescription().trim().isEmpty()) {
            final Element shortDescElem = parent.newChildElement("shortDesc");
            shortDescElem.setText(project.getProjectShortDescription());
        }

        logger.debug(String.format("Generated basic data XML for project '%s' "
                                   + "in %d ms",
                                   project.getName(),
                                   System.currentTimeMillis() - start));
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

    protected void generateMembersXml(final SciProject project,
                                      final Element parent,
                                      final PageState state) {
        final long start = System.currentTimeMillis();

        if ((project.getPersons() == null) || project.getPersons().isEmpty()) {
            return;
        }

        final Element membersElem = parent.newChildElement("members");

        final GenericOrganizationalUnitPersonCollection members = project.
                getPersons();

        while (members.next()) {
            generateMemberXml(members.getPerson(),
                              membersElem,
                              members.getRoleName(),
                              members.getStatus(),
                              state);
        }

        /*
         * if (config.isMergingMembers()) { final DataQuery subProjectsQuery =
         * SessionManager.getSession().retrieveQuery(
         * "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
         * subProjectsQuery.setParameter("orgaunitId",
         * project.getID().toString());
         * subProjectsQuery.setParameter("assocType",
         * SciProjectSubProjectsStep.ASSOC_TYPE);
         *
         * final DataQuery personsQuery = SessionManager.getSession().
         * retrieveQuery(
         * "com.arsdigita.cms.contenttypes.getIdsOfMembersOfOrgaUnits");
         *
         * final List<String> projectIds = new ArrayList<String>(); while
         * (subProjectsQuery.next()) {
         * projectIds.add(subProjectsQuery.get("orgaunitId").toString()); }
         * personsQuery.setParameter("orgaunitIds", projectIds);
         *
         * personsQuery.addOrder(GenericPerson.SURNAME);
         * personsQuery.addOrder(GenericPerson.GIVENNAME);
         *
         * while (personsQuery.next()) { generateMemberXml((BigDecimal)
         * personsQuery.get("memberId"), membersElem, (String)
         * personsQuery.get("roleName"), state); } } else { final
         * GenericOrganizationalUnitPersonCollection members = project.
         * getPersons();
         *
         * members.addOrder("surname"); members.addOrder("givenname");
         *
         * while (members.next()) { generateMemberXml(members.getPerson(),
         * membersElem, members.getRoleName(), state); }
         }
         */

        logger.debug(String.format("Generated members XML for project '%s'"
                                   + "in '%d ms'. MergeMembers is set to '%b'.",
                                   project.getName(),
                                   System.currentTimeMillis() - start,
                                   config.isMergingMembers()));
    }

    protected void generateMemberXml(final BigDecimal memberId,
                                     final Element parent,
                                     final String role,
                                     final String status,
                                     final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericPerson member = new GenericPerson(memberId);
        logger.debug(String.format("Got domain object for member '%s' "
                                   + "in %d ms.",
                                   member.getFullName(),
                                   System.currentTimeMillis() - start));
        generateMemberXml(member, parent, role, status, state);
    }

    protected void generateMemberXml(final GenericPerson member,
                                     final Element parent,
                                     final String role,
                                     final String status,
                                     final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(member);
        generator.setUseExtraXml(true);
        generator.setItemElemName("member", "");
        generator.addItemAttribute("role", role);
        generator.addItemAttribute("status", status);
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
        logger.debug(String.format("Generated XML for contacts of project '%s'"
                                   + " in %d ms.",
                                   project.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateContactXml(final GenericContact contact,
                                      final String contactType,
                                      final Element parent,
                                      final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(contact);
        generator.setUseExtraXml(true);
        generator.setItemElemName("contact", "");
        generator.addItemAttribute("contactType",
                                   getContactTypeName(contactType));
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for contact '%s' in %d ms.",
                                   contact.getName(),
                                   System.currentTimeMillis() - start));
    }

    private String getContactTypeName(final String contactTypeKey) {
        final RelationAttributeCollection relAttrs =
                                          new RelationAttributeCollection();
        relAttrs.addFilter(String.format("attribute = '%s'",
                                         "GenericOrganizationContactTypes"));
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

    protected void generateInvolvedOrgasXml(final SciProject project,
                                            final Element parent,
                                            final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericOrganizationalUnitSuperiorCollection orgas = project.
                getSuperiorOrgaUnits();

        if (orgas == null) {
            return;
        }

        orgas.addFilter(
                String.format("link.assocType = '%s'",
                              SciProjectInvolvedOrganizationsStep.ASSOC_TYPE));

        if (orgas.isEmpty()) {
            return;
        }

        final Element involvedElem = parent.newChildElement(
                "involvedOrganizations");
        while (orgas.next()) {
            generateInvolvedOrgaXml(orgas.getGenericOrganizationalUnit(),
                                    involvedElem,
                                    state);
        }
        logger.debug(String.format("Generated XML for involved organizations "
                                   + "of project '%s' in %d ms.",
                                   project.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateInvolvedOrgaXml(
            final GenericOrganizationalUnit involved,
            final Element parent,
            final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(involved);
        generator.setUseExtraXml(false);
        generator.setItemElemName("organization", "");
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for involved organization "
                                   + "'%s' in %d ms.",
                                   involved.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateSubProjectsXml(final SciProject project,
                                          final Element parent,
                                          final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericOrganizationalUnitSubordinateCollection subProjects =
                                                             project.
                getSubordinateOrgaUnits();

        if (subProjects == null) {
            return;
        }

        subProjects.addFilter(
                String.format("link.assocType = '%s'",
                              SciProjectSubProjectsStep.ASSOC_TYPE));

        if (subProjects.isEmpty()) {
            return;
        }

        final Element subProjectsElem = parent.newChildElement("subProjects");
        while (subProjects.next()) {
            generateSubProjectXml(
                    (SciProject) subProjects.getGenericOrganizationalUnit(),
                    subProjectsElem,
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
        final XmlGenerator generator = new XmlGenerator(subProject);
        generator.setUseExtraXml(true);
        generator.setListMode(true);
        generator.setItemElemName("subProject", "");
        generator.generateXML(state, parent, "");

        logger.debug(String.format("Generated XML for subproject '%s' in"
                                   + "%d ms",
                                   subProject.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateFundingXml(final SciProject project,
                                      final Element parent,
                                      final PageState state) {
        if ((project.getSponsors() != null)
            && !project.getSponsors().isEmpty()) {
            final SciProjectSponsorCollection sponsors = project.getSponsors();
            final Element sponsorsElem = parent.newChildElement("sponsors");
            while (sponsors.next()) {
                final Element sponsorElem = sponsorsElem.newChildElement("sponsor");
                final GenericOrganizationalUnit sponsor = sponsors.getSponsor();
                sponsorElem.setText(sponsor.getTitle());
                if ((sponsors.getFundingCode() != null) && !sponsors.isEmpty()) {
                    sponsorElem.addAttribute("fundingCode", sponsors.getFundingCode());
                }
            }
        }

        if ((project.getFunding() != null)
            && !project.getFunding().trim().isEmpty()) {
            final Element fundingElem = parent.newChildElement("funding");
            fundingElem.setText(project.getFunding());
        }

        if ((project.getFundingVolume() != null)
            && !project.getFundingVolume().trim().isEmpty()) {
            final Element fundingVolumeElem = parent.newChildElement(
                    "fundingVolume");
            fundingVolumeElem.setText(project.getFundingVolume());
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
