package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSubordinateCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteSummaryTab implements GenericOrgaUnitTab {

    private final Logger logger = Logger.getLogger(SciInstituteSummaryTab.class);
    private final static SciInstituteSummaryTabConfig config = new SciInstituteSummaryTabConfig();
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
        //Some of the the data shown by this tab will ever be there
        return true;
    }

    @Override
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

        if (config.isShowingDepartments()) {
            generateDepartmentsXml(institute, instituteSummaryElem, state);
        }

        if (config.isShowingContacts()) {
            generateContactsXml(institute, instituteSummaryElem, state);
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
            && (department.getInstituteShortDescription() != null)
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

        final GenericOrganizationalUnitPersonCollection heads = institute.getPersons();
        heads.addFilter(roleFilter.toString());
        heads.addFilter(statusFilter.toString());
        heads.addOrder("name");

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

        final GenericOrganizationalUnitPersonCollection headsCollection = department.getPersons();
        headsCollection.addFilter(roleFilter.toString());
        headsCollection.addFilter(statusFilter.toString());

        final List<GenericPerson> heads = new ArrayList<GenericPerson>();
        while (headsCollection.next()) {
            heads.add(headsCollection.getPerson());
        }
        Collections.sort(heads, new Comparator<GenericPerson>() {

            public int compare(final GenericPerson person1,
                               final GenericPerson person2) {
                final String name1 = String.format("%s %s", person1.getSurname(), person1.
                        getGivenName());
                final String name2 = String.format("%s %s", person2.getSurname(), person2.
                        getGivenName());
                return name1.compareTo(name2);
            }

        });

        if (!heads.isEmpty()) {
            final Element headsElem = parent.newChildElement("heads");
            //while (headsCollection.next()) {
            for (GenericPerson head : heads) {
                generateMemberXml(head, headsElem, state);
            }
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

        final GenericOrganizationalUnitSubordinateCollection departments = institute.
                getSubordinateOrgaUnits();
        departments.addFilter(
                String.format("%s = '%s'",
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
        generator.setItemElemName("head", "");
        generator.setUseExtraXml(false);
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for member '%s' in %d ms.",
                                   member.getFullName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateContactsXml(final SciInstitute department,
                                       final Element parent,
                                       final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericOrganizationalUnitContactCollection contacts = department.getContacts();

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
                                   department.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateContactXml(final GenericContact contact,
                                      final String contactType,
                                      final Element parent,
                                      final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(contact);
        generator.setUseExtraXml(false);
        generator.setItemElemName("contact", "");
        generator.addItemAttribute("contactType",
                                   getContactTypeName(contactType));
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for contact '%s' in %d ms.",
                                   contact.getName(),
                                   System.currentTimeMillis() - start));
    }

    private String getContactTypeName(final String contactTypeKey) {
        final RelationAttributeCollection relAttrs = new RelationAttributeCollection();
        relAttrs.addFilter(String.format("attribute = '%s'",
                                         "GenericOrganizationContactTypes"));
        relAttrs.addFilter(String.format("attr_key = '%s'", contactTypeKey));
        relAttrs.addFilter(String.format("lang = '%s'", GlobalizationHelper.getNegotiatedLocale().
                getLanguage()));

        if (relAttrs.isEmpty()) {
            return contactTypeKey;
        } else {
            relAttrs.next();
            final String result = relAttrs.getName();
            relAttrs.close();
            return result;
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
