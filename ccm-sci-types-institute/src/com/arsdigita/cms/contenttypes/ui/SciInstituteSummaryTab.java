package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSubordinateCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteSummaryTab implements GenericOrgaUnitTab {

    private final Logger logger =
                         Logger.getLogger(SciInstituteSummaryTab.class);
    private final static SciInstituteSummaryTabConfig config =
                                                      new SciInstituteSummaryTabConfig();

    static {
        config.load();
    }

    public boolean hasData(final GenericOrganizationalUnit orgaunit) {
        //Some of the the data shown by this tab will ever be there
        return true;
    }

    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        if (!(orgaunit instanceof SciInstitute)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of SciInstitute."
                    + "The provided object is of type '%s',",
                    orgaunit.getClass().getName()));
        }

        final long start = System.currentTimeMillis();
        final SciInstitute institute = (SciInstitute) orgaunit;

        final Element instituteSummaryElem = parent.newChildElement(
                "instituteSummary");

        generateShortDescXml(institute, instituteSummaryElem);

        if (config.isShowingHead()) {
            generateHeadOfInstituteXml(institute, instituteSummaryElem, state);
        }

        logger.debug(String.format("Generated XML for summary tab of institute "
                                   + "'%s' in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateShortDescXml(final SciInstitute department,
                                        final Element parent) {
        final long start = System.currentTimeMillis();

        if ((department != null)
            && !department.getInstituteShortDescription().trim().isEmpty()) {
            final Element shortDescElem = parent.newChildElement("shortDesc");
            shortDescElem.setText(department.getInstituteShortDescription());
        }

        logger.debug(String.format("Generated short desc XML for institute '%s' "
                                   + "in %d ms",
                                   department.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateHeadOfInstituteXml(final SciInstitute institute,
                                              final Element parent,
                                              final PageState state) {
        final long start = System.currentTimeMillis();
        final String headRoleStr = config.getHeadRole();
        final String activeStatusStr = config.getActiveStatus();

        final String[] headRoles = headRoleStr.split(",");
        final String[] activeStatuses = activeStatusStr.split(",");

        final StringBuffer roleFilter = new StringBuffer();
        for (String headRole : headRoles) {
            if (roleFilter.length() > 0) {
                roleFilter.append(',');
            }
            roleFilter.append(String.format("%s = '%s'",
                                            GenericOrganizationalUnitPersonCollection.LINK_PERSON_ROLE,
                                            headRole));
        }

        final StringBuffer statusFilter = new StringBuffer();
        for (String activeStatus : activeStatuses) {
            if (statusFilter.length() > 0) {
                statusFilter.append(",");
            }
            statusFilter.append(String.format("%s = '%s'",
                                              GenericOrganizationalUnitPersonCollection.LINK_STATUS,
                                              activeStatus));
        }

        final Element headsElem = parent.newChildElement("heads");

        final GenericOrganizationalUnitPersonCollection heads = institute.
                getPersons();
        heads.addFilter(roleFilter.toString());
        heads.addFilter(statusFilter.toString());
        heads.addOrder("surname");
        heads.addOrder("givenname");

        while (heads.next()) {
            generateMemberXml(heads.getPerson(), headsElem, state);
        }

        logger.debug(String.format("Generated head of department XML for institute '%s' "
                                   + "in %d ms",
                                   institute.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateHeadOfDepartmentXml(
            final GenericOrganizationalUnit department,
            final Element parent,
            final PageState state) {
        final long start = System.currentTimeMillis();
        final String headRoleStr = config.getHeadRole();
        final String activeStatusStr = config.getActiveStatus();

        final String[] headRoles = headRoleStr.split(",");
        final String[] activeStatuses = activeStatusStr.split(",");

        final StringBuffer roleFilter = new StringBuffer();
        for (String headRole : headRoles) {
            if (roleFilter.length() > 0) {
                roleFilter.append(',');
            }
            roleFilter.append(String.format("%s = '%s'",
                                            GenericOrganizationalUnitPersonCollection.LINK_PERSON_ROLE,
                                            headRole));
        }

        final StringBuffer statusFilter = new StringBuffer();
        for (String activeStatus : activeStatuses) {
            if (statusFilter.length() > 0) {
                statusFilter.append(",");
            }
            statusFilter.append(String.format("%s = '%s'",
                                              GenericOrganizationalUnitPersonCollection.LINK_STATUS,
                                              activeStatus));
        }

        final Element headsElem = parent.newChildElement("heads");

        final GenericOrganizationalUnitPersonCollection heads = department.
                getPersons();
        heads.addFilter(roleFilter.toString());
        heads.addFilter(statusFilter.toString());
        heads.addOrder("surname");
        heads.addOrder("givenname");

        while (heads.next()) {
            generateMemberXml(heads.getPerson(), headsElem, state);
        }

        logger.debug(String.format("Generated head of department XML for department '%s' "
                                   + "in %d ms",
                                   department.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateDepartmentsXml(final SciInstitute institute,
                                          final Element parent,
                                          final PageState state) {
        final long start = System.currentTimeMillis();

        final GenericOrganizationalUnitSubordinateCollection departments =
                                                             institute.
                getSubordinateOrgaUnits();
        departments.addFilter(
                String.format("%s = '%s",
                              GenericOrganizationalUnitSubordinateCollection.LINK_ASSOCTYPE,
                              SciInstituteDepartmentsStep.ASSOC_TYPE));

        final Element subDepsElem = parent.newChildElement("departments");

        while (departments.next()) {
            generateDepartmentXml(
                    departments.getGenericOrganizationalUnit(),
                    subDepsElem,
                    state);
        }

        logger.debug(String.format("Generated sub departments XML for institute '%s' "
                                   + "in %d ms",
                                   institute.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateDepartmentXml(
            final GenericOrganizationalUnit orgaunit,
            final Element parent,
            final PageState state) {
        final long start = System.currentTimeMillis();

        if (!(orgaunit.getClass().getName().equals(
              "com.arsdigita.cms.contenttypes.SciDepartment"))) {
            throw new IllegalArgumentException(String.format(
                    "Can't process "
                    + "orgaunit '%s' as department because the orgaunit is "
                    + "not a SciDepartment but of type '%s'.",
                    orgaunit.getName(),
                    orgaunit.getClass().
                    getName()));
        }

        final GenericOrganizationalUnit department = orgaunit;

        final Element subDepElem = parent.newChildElement("department");
        subDepElem.addAttribute("oid", department.getOID().toString());
        final Element nameElem = subDepElem.newChildElement("title");
        nameElem.setText(department.getTitle());

        generateHeadOfDepartmentXml(department, subDepElem, state);

        logger.debug(String.format("Generated XML for department '%s' "
                                   + "in %d ms",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
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
        generator.setUseExtraXml(false);
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for member '%s' in %d ms.",
                                   member.getFullName(),
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
