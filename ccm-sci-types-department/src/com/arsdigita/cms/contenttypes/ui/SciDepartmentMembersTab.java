package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.ui.panels.CompareFilter;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentMembersTab implements GenericOrgaUnitTab {

    private final Logger logger =
                         Logger.getLogger(SciDepartmentMembersTab.class);
    private static final SciDepartmentMembersTabConfig config =
                                                       new SciDepartmentMembersTabConfig();
    private static final String STATUS_PARAM = "memberStatus";
    private static final String SURNAME_PARAM = "memberSurname";
    private final CompareFilter statusFilter = new CompareFilter(
            STATUS_PARAM,
            "status",
            false,
            false,
            false);
    private final TextFilter surnameFilter =
                             new TextFilter(SURNAME_PARAM,
                                            GenericPerson.SURNAME);

    static {
        config.load();
    }

    public SciDepartmentMembersTab() {
        super();
        final String[] statusValues = config.getStatusValues();

        for (String status : statusValues) {
            statusFilter.addOption(status, status);
        }
    }

    public boolean hasData(final GenericOrganizationalUnit orgaunit) {
        if ((orgaunit.getPersons() != null)
            && orgaunit.getPersons().size() > 0) {
            return true;
        } else if (config.isMergingMembers()) {
            final DataQuery persons = getData(orgaunit);
            return persons.isEmpty();
        } else {
            return false;
        }
    }

    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final DataQuery persons = getData(orgaunit);
        final HttpServletRequest request = state.getRequest();

        applyStatusFilter(persons, request);
        applySurnameFilter(persons, request);

        final Element depMembersElem = parent.newChildElement(
                "departmentMembers");

        final Element filtersElem = depMembersElem.newChildElement("filters");

        statusFilter.generateXml(filtersElem);

        if (persons.isEmpty()) {
            if ((surnameFilter != null)
                && (surnameFilter.getFilter() != null)
                && !(surnameFilter.getFilter().trim().isEmpty())) {
                surnameFilter.generateXml(filtersElem);
            }
            depMembersElem.newChildElement("noMembers");
            return;
        }

        final Paginator paginator = new Paginator(request,
                                                  (int) persons.size(),
                                                  config.getPageSize());

        if ((paginator.getPageCount() > config.getEnableSearchLimit())
            || ((surnameFilter.getFilter() != null)
                && !(surnameFilter.getFilter().trim().isEmpty()))) {
            surnameFilter.generateXml(filtersElem);
        }

        paginator.applyLimits(persons);
        paginator.generateXml(depMembersElem);

        while (persons.next()) {
            generateMemberXml((BigDecimal) persons.get("memberId"),
                              depMembersElem,
                              state);
        }

        logger.debug(String.format("Generated members list of department '%s' "
                                   + "in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected DataQuery getData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();

        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciDepartment'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final DataQuery personsQuery = SessionManager.getSession().
                retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfMembersOfOrgaUnits");
        //final StringBuffer personsFilter = new StringBuffer();
        final List<String> orgaUnitIds = new ArrayList<String>();

        if (config.isMergingMembers()) {
            final DataQuery subDepartmentsQuery =
                            SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
            subDepartmentsQuery.setParameter("orgaunitId",
                                             orgaunit.getID().toString());
            subDepartmentsQuery.setParameter("assocType",
                                             SciDepartmentSubDepartmentsStep.ASSOC_TYPE);

            while (subDepartmentsQuery.next()) {
                /*if (personsFilter.length() > 0) {
                personsFilter.append(" or ");
                }
                personsFilter.append(String.format("orgaunitId = %s",
                subDepartmentsQuery.get(
                "orgaunitId").toString()));*/
                orgaUnitIds.add(subDepartmentsQuery.get("orgaunitId").toString());
            }
        } else {
            /*personsFilter.append(String.format("orgaunitId = %s",
            orgaunit.getID().toString()));*/
            orgaUnitIds.add(orgaunit.getID().toString());
        }

        //personsQuery.addFilter(personsFilter.toString());        
        personsQuery.setParameter("orgaunitIds", orgaUnitIds);

        /**
         * Filter for language independent items
         */
        /*if (Kernel.getConfig().languageIndependentItems()) {
            FilterFactory ff = personsQuery.getFilterFactory();
            Filter filter = ff.or().
                    addFilter(ff.equals("language",
                                        com.arsdigita.globalization.GlobalizationHelper.
                    getNegotiatedLocale().getLanguage())).
                    addFilter(ff.and().
                    addFilter(ff.equals("language",
                                        GlobalizationHelper.LANG_INDEPENDENT)).
                    addFilter(ff.notIn("parentId",
                                       "com.arsdigita.cms.contenttypes.getParentIDsOfMatchedItems").
                    set("language",
                        com.arsdigita.globalization.GlobalizationHelper.
                    getNegotiatedLocale().getLanguage())));
            personsQuery.addFilter(filter);
        } else {
            personsQuery.addEqualsFilter("language",
                                         com.arsdigita.globalization.GlobalizationHelper.
                    getNegotiatedLocale().getLanguage());
        }*/


        personsQuery.addOrder(GenericPerson.SURNAME);
        personsQuery.addOrder(GenericPerson.GIVENNAME);

        logger.debug(String.format(
                "Got members of department '%s'"
                + "in '%d ms'. MergeMembers is set to '%b'.",
                orgaunit.getName(),
                System.currentTimeMillis() - start,
                config.isMergingMembers()));
        return personsQuery;
    }

    private void applyStatusFilter(final DataQuery persons,
                                   final HttpServletRequest request) {
        final String statusValue = Globalization.decodeParameter(request,
                                                                 STATUS_PARAM);
        if ((statusValue != null) && !(statusValue.trim().isEmpty())) {
            statusFilter.setValue(statusValue);
        }

        final String filter = statusFilter.getFilter();
        if ((filter != null) && !(filter.trim().isEmpty())) {
            persons.addFilter(filter);
        }
    }

    private void applySurnameFilter(final DataQuery persons,
                                    final HttpServletRequest request) {
        final String surnameValue = Globalization.decodeParameter(request,
                                                                  SURNAME_PARAM);
        if ((surnameValue != null) && !(surnameValue.trim().isEmpty())) {
            surnameFilter.setValue(surnameValue);
        }

        final String filter = surnameFilter.getFilter();
        if ((filter != null) && !(filter.trim().isEmpty())) {
            persons.addFilter(filter);
        }
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
        generator.setItemElemName("member", "");
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
