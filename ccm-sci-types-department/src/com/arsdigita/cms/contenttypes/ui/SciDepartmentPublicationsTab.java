package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.contenttypes.ui.panels.SelectFilter;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
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
public class SciDepartmentPublicationsTab implements GenericOrgaUnitTab {

    private final Logger logger = Logger.getLogger(
            SciDepartmentPublicationsTab.class);
    private static final SciDepartmentPublicationsTabConfig config =
                                                            new SciDepartmentPublicationsTabConfig();
    private static final String YEAR_PARAM = "yearOfPublication";
    private static final String TITLE_PARAM = "title";
    private static final String AUTHOR_PARAM = "author";
    private final SelectFilter yearFilter = new SelectFilter("year",
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

    public SciDepartmentPublicationsTab() {
        super();

        authorFilter = new TextFilter(AUTHOR_PARAM, "authorsStr");
    }

    public boolean hasData(final GenericOrganizationalUnit orgaunit,
                           final PageState state) {
        final long start = System.currentTimeMillis();

        //Check if ccm-sci-publications is installed
        final ContentTypeCollection types = ContentType.getAllContentTypes();
        types.addFilter(
                "associatedObjectType = 'com.arsdigita.cms.contenttypes.Publication'");

        if (types.size() == 0) {
            return false;
        }
        types.close();

        final DataCollection data = getData(orgaunit);
        final boolean result = (data != null) && !data.isEmpty();

        logger.debug(String.format("Needed %d ms to determine if department "
                                   + "'%s' has publications.",
                                   System.currentTimeMillis() - start,
                                   orgaunit.getName()));
        return result;
    }

    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final DataCollection publications = getData(orgaunit);
        final HttpServletRequest request = state.getRequest();

        final Element depPublicationsElem = parent.newChildElement(
                "departmentPublications");

        final String yearValue = Globalization.decodeParameter(request, YEAR_PARAM);
        final String titleValue = Globalization.decodeParameter(request, TITLE_PARAM);
        final String authorValue = Globalization.decodeParameter(request, AUTHOR_PARAM);

        final Element filtersElem = depPublicationsElem.newChildElement(
                "filters");

        if (((yearValue == null) || yearValue.trim().isEmpty())
            && ((titleValue == null) || titleValue.trim().isEmpty())
            && ((authorValue == null) || authorValue.trim().isEmpty())) {
            //&& ((sortValue == null) || sortValue.trim().isEmpty())) {

            depPublicationsElem.newChildElement("greeting");

            publications.addOrder("yearOfPublication desc");
            publications.addOrder("case when (authorsStr is null) "
                                  + "then 'zzzz' "
                                  + "else authorsStr "
                                  + "end asc");

            publications.addOrder("title asc");

            publications.setRange(1, config.getGreetingSize() + 1);

            yearFilter.setDataQuery(publications, YEAR_PARAM);

            yearFilter.generateXml(filtersElem);
            titleFilter.generateXml(filtersElem);
            authorFilter.generateXml(filtersElem);

        } else {

            publications.addOrder("yearOfPublication desc");
            publications.addOrder("case when (authorsStr is null) "
                                  + "then 'zzzz' "
                                  + "else authorsStr "
                                  + "end asc");
            publications.addOrder("title");

            final DataQuery yearQuery = getData(orgaunit);
            yearFilter.setDataQuery(yearQuery, "year");

            applyYearFilter(publications, request);
            applyTitleFilter(publications, request);
            applyAuthorFilter(publications, request);

            applyTitleFilter(yearQuery, request);
            applyAuthorFilter(yearQuery, request);

            if (publications.isEmpty()) {
                yearFilter.generateXml(filtersElem);
                titleFilter.generateXml(filtersElem);
                authorFilter.generateXml(filtersElem);

                depPublicationsElem.newChildElement("noPublications");

                return;
            }

            final Paginator paginator = new Paginator(request,
                                                      (int) publications.size(),
                                                      config.getPageSize());

            yearFilter.generateXml(filtersElem);
            if ((paginator.getPageCount() > config.getEnableSearchLimit())
                || ((Globalization.decodeParameter(request, TITLE_PARAM) != null)
                    && !(Globalization.decodeParameter(request, TITLE_PARAM).trim().isEmpty()))) {
                titleFilter.generateXml(filtersElem);
            }

            if ((paginator.getPageCount() > config.getEnableSearchLimit())
                || ((Globalization.decodeParameter(request, AUTHOR_PARAM) != null)
                    && !(Globalization.decodeParameter(request, AUTHOR_PARAM).trim().isEmpty()))) {
                authorFilter.generateXml(filtersElem);
            }

            paginator.applyLimits(publications);
            paginator.generateXml(depPublicationsElem);
        }

        while (publications.next()) {
            generatePublicationXml(
                    (BigDecimal) publications.get("id"),
                    (String) publications.get("objectType"),
                    depPublicationsElem,
                    state);
        }

        logger.debug(String.format("Generated publications list of department '%s' "
                                   + "in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected DataCollection getData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();

        if (!(orgaunit instanceof SciDepartment)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciDepartment'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final DataQuery publicationBundlesQuery;

        publicationBundlesQuery =
        SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationsForOrgaUnit");
        final List<String> orgaunitIds = new ArrayList<String>();

        if (config.isMergingPublications()) {
            final DataQuery subDepartmentsQuery =
                            SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
            subDepartmentsQuery.setParameter("orgaunitId",
                                             orgaunit.getContentBundle().getID().toString());
            subDepartmentsQuery.setParameter("assocType",
                                             SciDepartmentSubDepartmentsStep.ASSOC_TYPE);
            while (subDepartmentsQuery.next()) {
                orgaunitIds.add(subDepartmentsQuery.get("orgaunitId").toString());
            }
        } else {
            orgaunitIds.add(orgaunit.getID().toString());
        }

        publicationBundlesQuery.setParameter("orgaunitIds", orgaunitIds);

        final StringBuilder filterBuilder = new StringBuilder();
        while (publicationBundlesQuery.next()) {
            if (filterBuilder.length() > 0) {
                filterBuilder.append(',');
            }
            filterBuilder.append(publicationBundlesQuery.get("publicationId").toString());
        }
        final DataCollection publicationsQuery = SessionManager.getSession().retrieve(
                "com.arsdigita.cms.contenttypes.Publication");

        if (filterBuilder.length() == 0) {
            //No publications return null to indicate
            return null;
        }

        publicationsQuery.addFilter(String.format("parent.id in (%s)", filterBuilder.toString()));

        if (Kernel.getConfig().languageIndependentItems()) {
            final FilterFactory filterFactory = publicationsQuery.getFilterFactory();
            final Filter filter = filterFactory.or().
                    addFilter(filterFactory.equals("language", GlobalizationHelper.getNegotiatedLocale().getLanguage())).
                    addFilter(filterFactory.and().
                    addFilter(filterFactory.equals("language", GlobalizationHelper.LANG_INDEPENDENT)).
                    addFilter(filterFactory.notIn("parent", "com.arsdigita.navigation.getParentIDsOfMatchedItems").set(
                    "language", GlobalizationHelper.getNegotiatedLocale().getLanguage())));
            publicationsQuery.addFilter(filter);
        } else {
            publicationsQuery.addEqualsFilter("language", GlobalizationHelper.getNegotiatedLocale().getLanguage());
        }

        logger.debug(String.format(
                "Got publications of department '%s'"
                + "in '%d ms'. MergeProjects is set to '%b'.",
                orgaunit.getName(),
                System.currentTimeMillis() - start,
                config.isMergingPublications()));
        return publicationsQuery;
    }

    private void applyYearFilter(final DataQuery publications,
                                 final HttpServletRequest request) {
        final String yearValue = Globalization.decodeParameter(request, YEAR_PARAM);
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
        final String titleValue = Globalization.decodeParameter(request, TITLE_PARAM);
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
        final String authorValue = Globalization.decodeParameter(request, AUTHOR_PARAM);
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
        final ContentPage publication =
                          (ContentPage) DomainObjectFactory.newInstance(new OID(objectType, publicationId));
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
        generator.setListMode(true);
        generator.setItemElemName("publications", "");
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
