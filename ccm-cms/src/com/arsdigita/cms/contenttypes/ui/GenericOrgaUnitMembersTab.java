package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSubordinateCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public abstract class GenericOrgaUnitMembersTab implements GenericOrgaUnitTab {

    private final static Logger logger =
                                Logger.getLogger(GenericOrgaUnitMembersTab.class);
    private String key;
    
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
        /*final long start = System.currentTimeMillis();
         final boolean result = !getData(orgaunit).isEmpty();
         logger.debug(String.format(
         "Needed %d ms to determine if "
         + "organizational unit '%s' has members. Merge is set to '%b'.",
         System.currentTimeMillis() - start,
         orgaunit.getName(),
         isMergingMembers()));
         return result;*/
        return !orgaunit.getPersons().isEmpty();
    }

    @Override
    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();

        List<GenericPerson> persons = getPersons(orgaunit, state);

        final Element personsElem = parent.newChildElement(getXmlElementName());

        if (getPageSize() != 0) {
            final Paginator paginator = new Paginator(
                    state.getRequest(), persons.size(), getPageSize());
            paginator.generateXml(personsElem);
            if (paginator.getEnd() < persons.size()) {
                persons = persons.subList(paginator.getBegin(), paginator.getEnd());
            }
        }

        for (GenericPerson person : persons) {
            generatePersonXml(person, personsElem, state);
        }

        logger.debug(String.format("Generated member list of organizational "
                                   + "unit '%s' in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    private void generatePersonXml(final BigDecimal personId,
                                   final Element parent,
                                   final PageState state) {
        final GenericPerson person = new GenericPerson(personId);

        final XmlGenerator generator = new XmlGenerator(person);
        generator.setItemElemName("person", "");
        generator.generateXML(state, parent, "");
    }

    private void generatePersonXml(final GenericPerson person,
                                   final Element parent,
                                   final PageState state) {
        final XmlGenerator generator = new XmlGenerator(person);
        generator.generateXML(state, parent, "");
    }

    protected abstract String getXmlElementName();

    protected abstract boolean isMergingMembers();

    protected abstract List<String> getAssocTypesToMerge();

    protected abstract List<String> getRolesToInclude();

    protected abstract List<String> getStatusesToInclude();

    protected abstract int getPageSize();

    protected List<GenericPerson> getPersons(
            final GenericOrganizationalUnit orgaunit,
            final PageState state) {
        final long start = System.currentTimeMillis();

        final List<GenericPerson> persons = new LinkedList<GenericPerson>();

        final GenericOrganizationalUnitPersonCollection personColl = orgaunit.getPersons();

        if ((getRolesToInclude() != null) && !getRolesToInclude().isEmpty()) {
            final StringBuffer roleFilter = new StringBuffer();
            for (String role : getRolesToInclude()) {
                if (roleFilter.length() > 0) {
                    roleFilter.append(" or ");
                }
                roleFilter.append(String.format("link.roleName = '%s'",
                                                role));
            }
            personColl.addFilter(roleFilter.toString());
        }

        if ((getStatusesToInclude() != null)
            && !getStatusesToInclude().isEmpty()) {
            final StringBuffer statusFilter = new StringBuffer();
            for (String status : getStatusesToInclude()) {
                if (statusFilter.length() > 0) {
                    statusFilter.append(" or ");
                }
                statusFilter.append(String.format("link.status = '%s'", status));
            }
            personColl.addFilter(statusFilter.toString());
        }

        while (personColl.next()) {
            persons.add(personColl.getPerson());
        }

        if (isMergingMembers()) {
            getPersonsFromSubordinateOrgaUnits(orgaunit, persons, state);
        }

        logger.debug(String.format(
                "Got members of orgaunit '%s'"
                + "in '%d ms'. MergeMembers is set to '%b'.",
                orgaunit.getName(),
                System.currentTimeMillis() - start,
                isMergingMembers()));
        return persons;
    }

    protected void getPersonsFromSubordinateOrgaUnits(
            final GenericOrganizationalUnit orgaunit,
            final List<GenericPerson> persons,
            final PageState state) {
        final GenericOrganizationalUnitSubordinateCollection subOrgaUnits =
                                       orgaunit.getSubordinateOrgaUnits();
        final StringBuffer assocTypeFilter = new StringBuffer();
        for (String assocType : getAssocTypesToMerge()) {
            if (assocTypeFilter.length() > 0) {
                assocTypeFilter.append(" or ");
            }
            assocTypeFilter.append(String.format("assocType = '%s'", assocType));
        }
        subOrgaUnits.addFilter(assocTypeFilter.toString());

        while (subOrgaUnits.next()) {
            getPersonsFromSubordinateOrgaUnit(orgaunit, persons, state);
        }
    }

    protected void getPersonsFromSubordinateOrgaUnit(
            final GenericOrganizationalUnit subOrgaUnit,
            final List<GenericPerson> persons,
            final PageState state) {
        final List<GenericPerson> subOrgaUnitMembers = getPersons(subOrgaUnit,
                                                                  state);
        persons.addAll(subOrgaUnitMembers);
    }

    /**
     * Overwrite to create filters for the list.
     * 
     * @param orgaunit
     * @param subOrgaUnits
     * @param element
     * @param state 
     */
    protected void generateFiltersXml(
            final GenericOrganizationalUnit orgaunit,
            final GenericOrganizationalUnitPersonCollection subOrgaUnits,
            final Element element,
            final PageState state) {
        //Nothing now
    }

    /**
     * If you have filters for the list of subordinate organizational units,
     * overwrite this method to process them. 
     *     
     * @param subOrgaUnits
     * @param state 
     */
    protected void processFilters(
            final GenericOrganizationalUnitPersonCollection subOrgaUnits,
            final PageState state) {
        //Nothing now
    }

    private class XmlGenerator extends SimpleXMLGenerator {

        private final GenericPerson person;

        public XmlGenerator(final GenericPerson person) {
            super();
            this.person = person;
        }

        @Override
        protected ContentItem getContentItem(final PageState state) {
            return person;
        }

    }
}
