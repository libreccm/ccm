package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.contenttypes.ui.panels.SelectFilter;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
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
public class SciInstitutePublicationsTab implements GenericOrgaUnitTab {

    private final Logger logger = Logger.getLogger(
            SciInstitutePublicationsTab.class);
    private static final SciInstitutePublicationsTabConfig config =
                                                           new SciInstitutePublicationsTabConfig();
    private static final String YEAR_PARAM = "year";
    private static final String TITLE_PARAM = "title";
    private static final String AUTHOR_PARAM = "author";
    /*private static final String SORT_PARAM = "sortBy";
    private static final String SORT_BY_YEAR_ASC = "yearAsc";
    private static final String SORT_BY_YEAR_DESC = "yearDesc";
    private static final String SORT_BY_TITLE = "title";
    private static final String SORT_BY_AUTHOR = "author";*/
    private final SelectFilter yearFilter = new SelectFilter(YEAR_PARAM,
                                                             YEAR_PARAM,
                                                             true,
                                                             true,
                                                             false,
                                                             true,
                                                             true);
    private final TextFilter titleFilter = new TextFilter(TITLE_PARAM,
                                                          ContentPage.TITLE);
    private final TextFilter authorFilter;

    static {
        config.load();
    }

    public SciInstitutePublicationsTab() {
        super();

        if (config.getOneRowPerAuthor()) {
            authorFilter = new TextFilter(AUTHOR_PARAM, "authorSurname");
        } else {
            authorFilter = new TextFilter(AUTHOR_PARAM, "authors");
        }
    }

    public boolean hasData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();

        final ContentTypeCollection types = ContentType.getAllContentTypes();
        types.addFilter(
                "associatedObjectType = 'com.arsdigita.cms.contenttypes.Publication'");

        if (types.size() == 0) {
            return false;
        }
        types.close();

        final boolean result = !getData(orgaunit).isEmpty();

        logger.debug(String.format("Needed %d ms to determine if institute "
                                   + "'%s' has publications.",
                                   System.currentTimeMillis() - start,
                                   orgaunit.getName()));
        return result;
    }

    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final DataQuery publications = getData(orgaunit);
        final HttpServletRequest request = state.getRequest();

        final Element publicationsElem = parent.newChildElement(
                "institutePublications");

        final String yearValue = request.getParameter(YEAR_PARAM);
        final String titleValue = request.getParameter(TITLE_PARAM);
        final String authorValue = request.getParameter(AUTHOR_PARAM);
        //final String sortValue = request.getParameter(SORT_PARAM);

        final Element filtersElem = publicationsElem.newChildElement(
                "filters");

        if (((yearValue == null) || yearValue.trim().isEmpty())
            && ((titleValue == null) || titleValue.trim().isEmpty())
            && ((authorValue == null) || authorValue.trim().isEmpty())) {
            // && ((sortValue == null) || sortValue.trim().isEmpty())) {

            publicationsElem.newChildElement("greeting");

            publications.addOrder("year");
            if (config.getOneRowPerAuthor()) {
                publications.addOrder("surname");
            } else {
                publications.addOrder("authors");
            }
            publications.addOrder("title");

            publications.setRange(1, config.getGreetingSize() + 1);

            yearFilter.setDataQuery(publications, "year");
            
            yearFilter.generateXml(filtersElem);
            titleFilter.generateXml(filtersElem);
            authorFilter.generateXml(filtersElem);

        } else {

            /*if (SORT_BY_AUTHOR.equals(sortValue)) {
            if (config.getOneRowPerAuthor()) {
            publications.addOrder("surname");
            } else {
            publications.addOrder("authors");
            }
            publications.addOrder("title");
            publications.addOrder("year asc");
            } else if (SORT_BY_TITLE.equals(sortValue)) {
            publications.addOrder("title");
            if (config.getOneRowPerAuthor()) {
            publications.addOrder("surname");
            } else {
            publications.addOrder("authors");
            }
            publications.addOrder("year asc");
            } else if (SORT_BY_YEAR_ASC.equals(sortValue)) {
            publications.addOrder("year asc");
            if (config.getOneRowPerAuthor()) {
            publications.addOrder("surname");
            } else {
            publications.addOrder("authors");
            }
            publications.addOrder("title");
            } else if (SORT_BY_YEAR_DESC.equals(sortValue)) {
            publications.addOrder("year desc");
            if (config.getOneRowPerAuthor()) {
            publications.addOrder("surname");
            } else {
            publications.addOrder("authors");
            }
            publications.addOrder("title");
            } else {
            if (config.getOneRowPerAuthor()) {
            publications.addOrder("surname");
            } else {
            publications.addOrder("authors");
            }
            publications.addOrder("title");
            publications.addOrder("year asc");
            }*/

            if (config.getOneRowPerAuthor()) {
                publications.addOrder("surname");
                publications.addOrder("title");
            } else {
                publications.addOrder("authors");
                publications.addOrder("title");
            }

            yearFilter.setDataQuery(publications, "year");

            applyYearFilter(publications, request);
            applyTitleFilter(publications, request);
            applyAuthorFilter(publications, request);

            final Paginator paginator = new Paginator(request,
                                                      (int) publications.size(),
                                                      config.getPageSize());

            if (paginator.getPageCount() > config.getEnableSearchLimit()) {
                yearFilter.generateXml(filtersElem);
                titleFilter.generateXml(filtersElem);
                authorFilter.generateXml(filtersElem);
            }

            paginator.applyLimits(publications);
            paginator.generateXml(publicationsElem);
        }

        /*final Element sortFieldsElem = publicationsElem.newChildElement(
        "sortFields");
        sortFieldsElem.addAttribute("sortBy", sortValue);
        
        sortFieldsElem.newChildElement("sortField").addAttribute("label",
        SORT_BY_AUTHOR);
        sortFieldsElem.newChildElement("sortField").addAttribute("label",
        SORT_BY_TITLE);
        sortFieldsElem.newChildElement("sortField").addAttribute("label",
        SORT_BY_YEAR_ASC);
        sortFieldsElem.newChildElement("sortField").addAttribute("label",
        SORT_BY_YEAR_DESC);*/

        while (publications.next()) {
            generatePublicationXml(
                    (BigDecimal) publications.get("publicationId"),
                    (String) publications.get("objectType"),
                    publicationsElem,
                    state);
        }

        logger.debug(String.format("Generated publications list of institute '%s' "
                                   + "in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected DataQuery getData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();

        if (!(orgaunit instanceof SciInstitute)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciInstitute'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }


        final DataQuery publicationsQuery;
        if (config.getOneRowPerAuthor()) {
            publicationsQuery =
            SessionManager.getSession().
                    retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfPublicationsForOrgaUnitOneRowPerAuthor");
        } else {
            publicationsQuery =
            SessionManager.getSession().
                    retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfPublicationsForOrgaUnit");
        }
        //final StringBuffer publicationsFilter = new StringBuffer();
        final List<String> orgaunitIds = new ArrayList<String>();

        if (config.isMergingPublications()) {
            final DataQuery departmentsQuery =
                            SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
            departmentsQuery.setParameter("orgaunitId",
                                          orgaunit.getID().toString());
            departmentsQuery.setParameter("assocType",
                                          SciInstituteDepartmentsStep.ASSOC_TYPE);
            while (departmentsQuery.next()) {
                /*if (publicationsFilter.length() > 0) {
                publicationsFilter.append(" or ");
                }
                publicationsFilter.append(String.format("orgaunitId = %s",
                departmentsQuery.get(
                "orgaunitId").toString()));*/
                orgaunitIds.add(departmentsQuery.get("orgaunitId").toString());
            }
        } else {
            /*publicationsFilter.append(String.format("orgaunitId = %s",
                                                    orgaunit.getID().toString()));*/
            orgaunitIds.add(orgaunit.getID().toString());
        }

        //publicationsQuery.addFilter(publicationsFilter.toString());
        publicationsQuery.setParameter("orgaunitIds", orgaunitIds);

        logger.debug(String.format(
                "Got publications of institute '%s'"
                + "in '%d ms'. MergeProjects is set to '%b'.",
                orgaunit.getName(),
                System.currentTimeMillis() - start,
                config.isMergingPublications()));
        return publicationsQuery;
    }

    private void applyYearFilter(final DataQuery publications,
                                 final HttpServletRequest request) {
        final String yearValue = request.getParameter(YEAR_PARAM);
        if ((yearValue != null) && !(yearValue.trim().isEmpty())) {
            yearFilter.setValue(yearValue);
        }

         if ((yearFilter.getFilter() != null)
            && !(yearFilter.getFilter().isEmpty())) {
            publications.addFilter(yearFilter.getFilter());
        }
    }

    private void applyTitleFilter(final DataQuery publications,
                                  final HttpServletRequest request) {
        final String titleValue = request.getParameter(TITLE_PARAM);
        if ((titleValue != null) && !(titleValue.trim().isEmpty())) {
            titleFilter.setValue(titleValue);
        }

        if ((titleFilter.getFilter() != null)
            && !(titleFilter.getFilter().isEmpty())) {
            publications.addFilter(titleFilter.getFilter());
        }
    }

    private void applyAuthorFilter(final DataQuery publications,
                                   final HttpServletRequest request) {
        final String authorValue = request.getParameter(AUTHOR_PARAM);
        if ((authorValue != null) && !(authorValue.trim().isEmpty())) {
            authorFilter.setValue(authorValue);
        }

        if ((authorFilter.getFilter() != null)
            && !(authorFilter.getFilter().isEmpty())) {
            publications.addFilter(authorFilter.getFilter());
        }
    }

    private void generatePublicationXml(final BigDecimal publicationId,
                                        final String objectType,
                                        final Element parent,
                                        final PageState state) {
        final long start = System.currentTimeMillis();
        final ContentPage publication = (ContentPage) DomainObjectFactory.
                newInstance(new OID(objectType, publicationId));
        logger.debug(String.format("Got domain object for publication '%s' "
                                   + "in %d ms.",
                                   publication.getName(),
                                   System.currentTimeMillis() - start));
        generatePublicationXml(publication, parent, state);
    }

    private void generatePublicationXml(final ContentPage publication,
                                        final Element parent,
                                        final PageState state) {
        final long start = System.currentTimeMillis();
        final XmlGenerator generator = new XmlGenerator(publication);
        generator.setUseExtraXml(false);
        generator.setItemElemName("publication", "");
        generator.generateXML(state, parent, "");
        logger.debug(String.format(
                "Generated XML for publication '%s' in %d ms.",
                publication.getName(),
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
