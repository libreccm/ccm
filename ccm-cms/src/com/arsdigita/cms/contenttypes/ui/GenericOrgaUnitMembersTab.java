package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
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
    
    public boolean hasData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();
        final boolean result = !getData(orgaunit).isEmpty();
        logger.debug(String.format(
                "Needed %d ms to determine if "
                + "organizational unit '%s' has members. Merge is set to '%b'.",
                System.currentTimeMillis() - start,
                orgaunit.getName(),
                isMergingMembers()));
        return result;
    }
    
    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        
        final DataQuery persons = getData(orgaunit, state);
        
        final Element personsElem = parent.newChildElement(getXmlElementName());
        
        if (getPageSize() != 0) {
            final GenericOrgaUnitPaginator<DataQuery> paginator =
                                                      new GenericOrgaUnitPaginator<DataQuery>(
                    persons, state, getPageSize());
            paginator.setRange(persons);
            paginator.generateXml(personsElem);
        }
        
        while (persons.next()) {
            /*generatePersonXml((BigDecimal) persons.get("memberId"),
                              parent,
                              state);*/
            generatePersonXml(((GenericOrganizationalUnitPersonCollection) persons).getPerson().getID(),
                              parent,
                              state);
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
        generator.generateXML(state, parent, "");
    }
    
    protected abstract String getXmlElementName();
    
    protected abstract boolean isMergingMembers();
    
    protected abstract List<String> getAssocTypesToMerge();
    
    protected abstract List<String> getRolesToInclude();
    
    protected abstract List<String> getStatusesToInclude();
    
    protected abstract int getPageSize();
    
    protected DataQuery getData(final GenericOrganizationalUnit orgaunit,
                                final PageState state) {
        return getData(orgaunit);
    }
    
    protected DataQuery getData(final GenericOrganizationalUnit orgaunit) {
        return orgaunit.getPersons();
        
        /*final long start = System.currentTimeMillis();
        
        final DataQuery personsQuery = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfMembersOfOrgaUnits");
        
        if (isMergingMembers()) {
            final DataQuery subOrgaUnitsQuery =
                            SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivly");
            subOrgaUnitsQuery.setParameter("orgaunitId", orgaunit.getID().
                    toString());
            final StringBuffer assocTypeFilter = new StringBuffer();
            for (String assocType : getAssocTypesToMerge()) {
                if (assocTypeFilter.length() > 0) {
                    assocTypeFilter.append(" or ");
                }
                assocTypeFilter.append(String.format("assocType = '%s'",
                                                     assocType));
            }
            subOrgaUnitsQuery.addFilter(assocTypeFilter.toString());
            
            final StringBuffer buffer = new StringBuffer();
            while (subOrgaUnitsQuery.next()) {
                if (buffer.length() > 0) {
                    buffer.append(" or ");
                }
                buffer.append(String.format("orgaunitId = %s",
                                            subOrgaUnitsQuery.get("orgaunitId").
                        toString()));
            }
          
            personsQuery.addFilter(buffer.toString());
        } else {
            personsQuery.addFilter(String.format("orgaunitId = %s",
                                                 orgaunit.getID().toString()));
        }
        
        if ((getRolesToInclude() != null) && !getRolesToInclude().isEmpty()) {
            final StringBuffer roleFilter = new StringBuffer();
            for (String role : getRolesToInclude()) {
                if (roleFilter.length() > 0) {
                    roleFilter.append(" or ");
                }
                roleFilter.append(String.format("roleName = '%s'", role));
            }
            personsQuery.addFilter(roleFilter.toString());
        }
        
        if ((getStatusesToInclude() != null)
            && !getStatusesToInclude().isEmpty()) {
            final StringBuffer statusFilter = new StringBuffer();
            for (String status : getStatusesToInclude()) {
                if (statusFilter.length() > 0) {
                    statusFilter.append(" or ");
                }
                statusFilter.append(String.format("status = '%s'", status));
            }
            personsQuery.addFilter(statusFilter.toString());
        }
        
        personsQuery.addOrder("surname");
        personsQuery.addOrder("givenname");
        
        logger.debug(String.format(
                "Got persons for organizational unit '%s'"
                + "in %d ms. isMergingMembers is set to '%b'.",
                orgaunit.getName(),
                System.currentTimeMillis() - start,
                isMergingMembers()));
        return personsQuery;*/
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
