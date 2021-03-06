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
public class SciInstitutePublicationsTab implements GenericOrgaUnitTab {

    private final Logger logger = Logger.getLogger(
            SciInstitutePublicationsTab.class);
    private static final SciInstitutePublicationsTabConfig config
                                                           = new SciInstitutePublicationsTabConfig();
    private static final String YEAR_PARAM = "yearOfPublication";
    private static final String TITLE_PARAM = "title";
    private static final String AUTHOR_PARAM = "author";
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
    private boolean excludeWorkingPapers = false;
    private boolean onlyWorkingPapers = false;
    private String key;

    static {
        config.load();
    }

    public SciInstitutePublicationsTab() {
        super();

        authorFilter = new TextFilter(AUTHOR_PARAM, "authorsStr");
    }

    public void setExcludeWorkingPapers(final boolean excludeWorkingPapers) {
        this.excludeWorkingPapers = excludeWorkingPapers;
    }

    public void setOnlyWorkingPapers(final boolean onlyWorkingPapers) {
        this.onlyWorkingPapers = onlyWorkingPapers;
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

        logger.debug(String.format("Needed %d ms to determine if institute "
                                   + "'%s' has publications.",
                                   System.currentTimeMillis() - start,
                                   orgaunit.getName()));
        return result;
    }

    @Override
    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final long start = System.currentTimeMillis();
        final DataCollection publications = getData(orgaunit);
        final HttpServletRequest request = state.getRequest();

        final Element publicationsElem = parent.newChildElement(
                "institutePublications");

        final String yearValue = Globalization.decodeParameter(request, YEAR_PARAM);
        final String titleValue = Globalization.decodeParameter(request, TITLE_PARAM);
        final String authorValue = Globalization.decodeParameter(request, AUTHOR_PARAM);

        final Element filtersElem = publicationsElem.newChildElement(
                "filters");

        if (excludeWorkingPapers && onlyWorkingPapers) {
            throw new IllegalStateException(
                    "onlyWorkingPapers and excludeWorkingPapers are both set "
                    + "to true. This is not possible.");
        }

        if (excludeWorkingPapers) {
            publications.addFilter(
                    "objectType != 'com.arsdigita.cms.contenttypes.WorkingPaper'");
        }

        if (onlyWorkingPapers) {
            publications.addFilter(
                    "objectType = 'com.arsdigita.cms.contenttypes.WorkingPaper'");
        }

        if (((yearValue == null)
             || yearValue.trim().isEmpty()
             || SelectFilter.NONE.equals(yearValue))
            && ((titleValue == null) || titleValue.trim().isEmpty())
            && ((authorValue == null) || authorValue.trim().isEmpty())) {

            publicationsElem.newChildElement("greeting");

            publications.addOrder("yearOfPublication desc");
            publications.addOrder("case when (authorsStr is null) "
                                  + "then 'zzzz' "
                                  + "else authorsStr "
                                  + "end asc");
            publications.addOrder("title asc");

            yearFilter.setDataQuery(publications, YEAR_PARAM);

            yearFilter.generateXml(filtersElem);
            titleFilter.generateXml(filtersElem);
            authorFilter.generateXml(filtersElem);

            publications.setRange(1, config.getGreetingSize() + 1);
        } else {

            publications.addOrder("case when (authorsStr is null) "
                                  + "then 'zzzz' "
                                  + "else authorsStr "
                                  + "end asc");
            publications.addOrder("title asc");

            final DataQuery yearQuery = getData(orgaunit);
            yearFilter.setDataQuery(yearQuery, YEAR_PARAM);

            applyYearFilter(publications, request);
            applyTitleFilter(publications, request);
            applyAuthorFilter(publications, request);

            applyTitleFilter(yearQuery, request);
            applyAuthorFilter(yearQuery, request);

            if (publications.isEmpty()) {
                yearFilter.generateXml(filtersElem);
                titleFilter.generateXml(filtersElem);
                authorFilter.generateXml(filtersElem);

                publicationsElem.newChildElement("noPublications");

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
            paginator.generateXml(publicationsElem);
        }

        while (publications.next()) {
            generatePublicationXml(
                    (BigDecimal) publications.get("id"),
                    (String) publications.get("objectType"),
                    publicationsElem,
                    state);
        }

        logger.debug(String.format("Generated publications list of institute '%s' "
                                   + "in %d ms.",
                                   orgaunit.getName(),
                                   System.currentTimeMillis() - start));
    }

    protected DataCollection getData(final GenericOrganizationalUnit orgaunit) {
        final long start = System.currentTimeMillis();

        if (!(orgaunit instanceof SciInstitute)) {
            throw new IllegalArgumentException(String.format(
                    "This tab can only process instances of "
                    + "'com.arsdigita.cms.contenttypes.SciInstitute'. Provided "
                    + "object is of type '%s'",
                    orgaunit.getClass().getName()));
        }

        final DataQuery publicationBundlesQuery;
        publicationBundlesQuery = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationsForOrgaUnit");

        final List<String> orgaunitIds = new ArrayList<String>();

        if (config.isMergingPublications()) {
            final DataQuery departmentsQuery = SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getIdsOfSubordinateOrgaUnitsRecursivlyWithAssocType");
            departmentsQuery.setParameter("orgaunitId",
                                          orgaunit.getContentBundle().getID().toString());
            departmentsQuery.setParameter("assocType",
                                          SciInstituteDepartmentsStep.ASSOC_TYPE);
            while (departmentsQuery.next()) {
                orgaunitIds.add(departmentsQuery.get("orgaunitId").toString());
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
                    addFilter(filterFactory.equals("language", GlobalizationHelper.
                                    getNegotiatedLocale().getLanguage())).
                    addFilter(filterFactory.and().
                            addFilter(filterFactory.equals("language",
                                                           GlobalizationHelper.LANG_INDEPENDENT)).
                            addFilter(filterFactory.notIn("parent",
                                                          "com.arsdigita.navigation.getParentIDsOfMatchedItems").
                                    set(
                                            "language", GlobalizationHelper.getNegotiatedLocale().
                                            getLanguage())));
            publicationsQuery.addFilter(filter);
        } else {
            publicationsQuery.addEqualsFilter("language", GlobalizationHelper.getNegotiatedLocale().
                    getLanguage());
        }

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
        final ContentPage publication = (ContentPage) DomainObjectFactory.newInstance(new OID(
                objectType, publicationId));
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
