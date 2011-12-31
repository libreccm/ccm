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
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentSummaryTab implements GenericOrgaUnitTab {

    private final Logger logger =
                         Logger.getLogger(SciDepartmentSummaryTab.class);
    private final static SciDepartmentSummaryTabConfig config =
                                                       new SciDepartmentSummaryTabConfig();

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
        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of SciDpartment."
                    + "The provided object is of type '%s',",
                    orgaunit.getClass().getName()));
        }

        final long start = System.currentTimeMillis();
        final SciDepartment department = (SciDepartment) orgaunit;

        final Element departmentSummaryElem = parent.newChildElement(
                "departmentSummary");

        generateShortDescXml(department, departmentSummaryElem);

        if (config.isShowingHead()) {
            generateHeadOfDepartmentXml(department, departmentSummaryElem, state);
        }

        if (config.isShowingSubDepartment()) {
            generateSubDepartmentsXml(department, departmentSummaryElem, state);
        }

        if (config.isShowingContacts()) {
            generateContactsXml(department, departmentSummaryElem, state);
        }

        logger.debug(String.format("Generated XML for summary tab of department "
                                   + "'%s' in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateShortDescXml(final SciDepartment department,
                                        final Element parent) {
        final long start = System.currentTimeMillis();

        if ((department != null)
            && (department.getDepartmentShortDescription() != null)
            && !department.getDepartmentShortDescription().trim().isEmpty()) {
            final Element shortDescElem = parent.newChildElement("shortDesc");
            shortDescElem.setText(department.getDepartmentShortDescription());
        }

        logger.debug(String.format("Generated short desc XML for department '%s' "
                                   + "in %d ms",
                                   department.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateHeadOfDepartmentXml(final SciDepartment department,
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
            generateHeadXml(heads.getPerson(), headsElem, state);
        }

        logger.debug(String.format("Generated head of department XML for department '%s' "
                                   + "in %d ms",
                                   department.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateSubDepartmentsXml(final SciDepartment department,
                                             final Element parent,
                                             final PageState state) {
        final long start = System.currentTimeMillis();

        final GenericOrganizationalUnitSubordinateCollection subDepartments =
                                                             department.
                getSubordinateOrgaUnits();
        subDepartments.addFilter(
                String.format("%s = '%s'",
                              GenericOrganizationalUnitSubordinateCollection.LINK_ASSOCTYPE,
                              SciDepartmentSubDepartmentsStep.ASSOC_TYPE));

        final Element subDepsElem = parent.newChildElement("subDepartments");

        while (subDepartments.next()) {
            generateSubDepartmentXml(
                    subDepartments.getGenericOrganizationalUnit(),
                    subDepsElem,
                    state);
        }

        logger.debug(String.format("Generated sub departments XML for department '%s' "
                                   + "in %d ms",
                                   department.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateSubDepartmentXml(
            final GenericOrganizationalUnit orgaunit,
            final Element parent,
            final PageState state) {
        final long start = System.currentTimeMillis();

        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "Can't process "
                    + "orgaunit '%s' as sub department because the orgaunit is "
                    + "not a SciDepartment but of type '%s'.",
                    orgaunit.getName(),
                    orgaunit.getClass().
                    getName()));
        }

        final SciDepartment subDepartment = (SciDepartment) orgaunit;

        final Element subDepElem = parent.newChildElement("subDepartment");
        subDepElem.addAttribute("oid", subDepartment.getOID().toString());
        final Element nameElem = subDepElem.newChildElement("title");
        nameElem.setText(subDepartment.getTitle());

        generateHeadOfDepartmentXml(subDepartment, subDepElem, state);

        logger.debug(String.format("Generated XML for sub department '%s' "
                                   + "in %d ms",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateHeadXml(final BigDecimal memberId,
                                   final Element parent,
                                   final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericPerson member = new GenericPerson(memberId);
        logger.debug(String.format("Got domain object for member '%s' "
                                   + "in %d ms.",
                                   member.getFullName(),
                                   System.currentTimeMillis() - start));
        generateHeadXml(member, parent, state);
    }

    protected void generateHeadXml(final GenericPerson member,
                                   final Element parent,
                                   final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(member);
        generator.setUseExtraXml(false);
        generator.setItemElemName("head", "");
        generator.generateXML(state, parent, "");
        logger.debug(String.format("Generated XML for member '%s' in %d ms.",
                                   member.getFullName(),
                                   System.currentTimeMillis() - start));
    }

    protected void generateContactsXml(final SciDepartment department,
                                       final Element parent,
                                       final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericOrganizationalUnitContactCollection contacts = department.
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
        final RelationAttributeCollection relAttrs =
                                          new RelationAttributeCollection();
        relAttrs.addFilter(String.format("attribute = '%s'",
                                         "GenericContactTypes"));
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
