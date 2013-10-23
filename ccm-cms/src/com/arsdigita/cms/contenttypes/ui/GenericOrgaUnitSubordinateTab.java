package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitSubordinateCollection;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public abstract class GenericOrgaUnitSubordinateTab
        implements GenericOrgaUnitTab {

    private final static Logger logger = Logger.getLogger(
            GenericOrgaUnitSubordinateTab.class);
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
        return !getData(orgaunit).isEmpty();
    }

    @Override
    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final GenericOrganizationalUnitSubordinateCollection subOrgaUnits = getData(orgaunit,
                                                                                    state);

        processFilters(subOrgaUnits, state);

        final Element subOrgaUnitsElem = parent.newChildElement(getXmlElementName());

        if (getPageSize() != 0) {
            final GenericOrgaUnitPaginator<GenericOrganizationalUnitSubordinateCollection> paginator
                                                                                           = new GenericOrgaUnitPaginator<GenericOrganizationalUnitSubordinateCollection>(
                    subOrgaUnits, state, getPageSize());
            paginator.setRange(subOrgaUnits);
            paginator.generateXml(subOrgaUnitsElem);
        }

        while (subOrgaUnits.next()) {
            generateSubOrgaUnitXml(subOrgaUnits.getGenericOrganizationalUnit(),
                                   parent,
                                   state);
        }
        logger.debug(String.format("XML for subordinate organizational units of "
                                   + "organizational unit '%s' with assoctype '%s' generated in"
                                   + "%d ms",
                                   orgaunit.getName(),
                                   getAssocType(),
                                   System.currentTimeMillis() - start));
    }

    private void generateSubOrgaUnitXml(
            final GenericOrganizationalUnit orgaunit,
            final Element parent,
            final PageState state) {
        final XmlLGenerator generator = new XmlLGenerator(
                orgaunit);
        generator.generateXML(state, parent, "");
    }

    protected GenericOrganizationalUnitSubordinateCollection getData(
            final GenericOrganizationalUnit orgaunit,
            final PageState state) {
        return getData(orgaunit);
    }

    protected GenericOrganizationalUnitSubordinateCollection getData(
            final GenericOrganizationalUnit orgaunit) {
        final GenericOrganizationalUnitSubordinateCollection subOrgaUnits = orgaunit.
                getSubordinateOrgaUnits();
        subOrgaUnits.addFilter(
                String.format("%s = '%s'",
                              GenericOrganizationalUnitSubordinateCollection.LINK_ASSOCTYPE,
                              getAssocType()));

        return subOrgaUnits;
    }

    protected abstract String getXmlElementName();

    /**
     * The collection of subordinate organization units is filtered for
     * the value of the {@code assocType} property returned by this method.
     * If you don't want to filter for an assocType simply return '{@code %}'.
     * 
     * @return 
     */
    protected abstract String getAssocType();

    /**
     * Page size for the paginator. If you want to disable the paginator, 
     * return 0.
     * 
     * @return 
     */
    protected abstract int getPageSize();

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
            final GenericOrganizationalUnitSubordinateCollection subOrgaUnits,
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
            final GenericOrganizationalUnitSubordinateCollection subOrgaUnits,
            final PageState state) {
        //Nothing now
    }

    private class XmlLGenerator extends SimpleXMLGenerator {

        private final GenericOrganizationalUnit orgaunit;

        public XmlLGenerator(final GenericOrganizationalUnit orgaunit) {
            super();
            this.orgaunit = orgaunit;
        }

        @Override
        protected ContentItem getContentItem(final PageState state) {
            return orgaunit;
        }

    }
}
