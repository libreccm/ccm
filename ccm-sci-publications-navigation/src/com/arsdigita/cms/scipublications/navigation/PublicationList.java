package com.arsdigita.cms.scipublications.navigation;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.ui.AbstractComponent;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This navigation component provides a faster way to create a list of items of
 * the type {@link Publication} than the usual object lists.
 *
 * This component uses native SQL queries to create the list.
 *
 * Note: This is only a temporary solution which will be removed in Version
 * 7.0.0 when we completed the migration to JPA/Hibernate.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PublicationList extends AbstractComponent {

    /**
     * Template for the query to fetch the publications assigned to the
     * category.
     */
    private final static String PUBLIATIONS_QUERY_TEMPLATE
                                    = "SELECT cms_items.item_id, name, version, language, object_type, "
                                      + "master_id, parent_id, title, cms_pages.description, "
                                      + "year, abstract, misc, reviewed, authors, firstPublished, lang, "
                                      + "isbn, ct_publication_with_publisher.volume, number_of_volumes, _number_of_pages, ct_publication_with_publisher.edition, "
                                      + "nameofconference, place_of_conference, date_from_of_conference, date_to_of_conference, "
                                      + "ct_article_in_collected_volume.pages_from AS collvol_pages_from, ct_article_in_collected_volume.pages_to AS collvol_pages_to, chapter, "
                                      + "ct_article_in_journal.pages_from AS journal_pages_from, ct_article_in_journal.pages_to AS journal_pages_to, ct_article_in_journal.volume AS journal_volume, issue, publication_date, "
                                      + "ct_expertise.place AS expertise_place, ct_expertise.number_of_pages AS expertise_number_of_pages, "
                                      + "ct_inproceedings.pages_from AS inproceedings_pages_from, ct_inproceedings.pages_to AS inproceedings_pages_to, "
                                      + "ct_internet_article.place AS internet_article_place, "
                                      + "ct_internet_article.number AS internet_article_number, "
                                      + "ct_internet_article.number_of_pages AS internet_article_number_of_pages, "
                                      + "ct_internet_article.edition AS internet_article_edition, "
                                      + "ct_internet_article.issn AS internet_article_issn, "
                                      + "ct_internet_article.last_accessed AS internet_article_last_accessed, "
                                      + "ct_internet_article.publicationdate AS internet_article_publication_date, "
                                      + "ct_internet_article.url AS internet_article_url, "
                                      + "ct_internet_article.urn AS internet_article_urn, "
                                      + "ct_internet_article.doi AS internet_article_doi, "
                                      + "ct_unpublished.place AS unpublished_place, "
                                      + "ct_unpublished.number AS unpublished_number, "
                                      + "ct_unpublished.number_of_pages AS unpublished_number_of_pages, "
                                      + "ct_grey_literature.pagesfrom AS grey_literature_pages_from, "
                                      + "ct_grey_literature.pagesto AS grey_literature_pages_to "
                                      + "FROM cms_items "
                                          + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                                      + "JOIN content_types ON cms_items.type_id = content_types.type_id "
                                      + "JOIN ct_publications ON cms_items.item_id = ct_publications.publication_id "
                                      + "LEFT JOIN ct_publication_with_publisher ON ct_publications.publication_id = ct_publication_with_publisher.publication_with_publisher_id "
                                      + "LEFT JOIN ct_proceedings ON ct_publications.publication_id = ct_proceedings.proceedings_id "
                                      + "LEFT JOIN ct_article_in_collected_volume ON ct_publications.publication_id = ct_article_in_collected_volume.article_id "
                                      + "LEFT JOIN ct_article_in_journal ON ct_publications.publication_id = ct_article_in_journal.article_in_journal_id "
                                      + "LEFT JOIN ct_expertise ON ct_publications.publication_id = ct_expertise.expertise_id "
                                      + "LEFT JOIN ct_inproceedings ON ct_publications.publication_id = ct_inproceedings.inproceedings_id "
                                      + "LEFT JOIN ct_internet_article ON ct_publications.publication_id = ct_internet_article.internet_article_id "
                                      + "LEFT JOIN ct_unpublished ON ct_publications.publication_id = ct_unpublished.unpublished_id "
                                      + "LEFT JOIN ct_grey_literature ON ct_unpublished.unpublished_id = ct_grey_literature.grey_literature_id "
                                      + "%s"
                                          + "WHERE parent_id IN (SELECT object_id FROM cat_object_category_map WHERE category_id = ?) AND language = ? AND version = 'live' %s"
                                      + "%s "
                                          + "LIMIT ? OFFSET ?";
    /**
     * Template for the query for counting the publications assigned to the
     * category.
     */
    private final static String COUNT_PUBLICATIONS_QUERY_TEMPLATE
                                    = "SELECT COUNT(*) "
                                          + "FROM cms_items "
                                          + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                                      + "JOIN content_types ON cms_items.type_id = content_types.type_id "
                                      + "JOIN ct_publications ON cms_items.item_id = ct_publications.publication_id "
                                      + "LEFT JOIN ct_publication_with_publisher ON ct_publications.publication_id = ct_publication_with_publisher.publication_with_publisher_id "
                                      + "LEFT JOIN ct_proceedings ON ct_publications.publication_id = ct_proceedings.proceedings_id "
                                      + "LEFT JOIN ct_article_in_collected_volume ON ct_publications.publication_id = ct_article_in_collected_volume.article_id "
                                      + "LEFT JOIN ct_article_in_journal ON ct_publications.publication_id = ct_article_in_journal.article_in_journal_id "
                                      + "LEFT JOIN ct_expertise ON ct_publications.publication_id = ct_expertise.expertise_id "
                                      + "LEFT JOIN ct_inproceedings ON ct_publications.publication_id = ct_inproceedings.inproceedings_id "
                                      + "LEFT JOIN ct_internet_article ON ct_publications.publication_id = ct_internet_article.internet_article_id "
                                      + "LEFT JOIN ct_unpublished ON ct_publications.publication_id = ct_unpublished.unpublished_id "
                                      + "LEFT JOIN ct_grey_literature ON ct_unpublished.unpublished_id = ct_grey_literature.grey_literature_id "
                                      + "%s"
                                          + "WHERE parent_id IN (SELECT object_id FROM cat_object_category_map WHERE category_id = ?) AND language = ? AND version = 'live' %s";

    /**
     * Prepared statement for fetching the available years of publication for
     * the year select filter.
     */
    private final PreparedStatement availableYearsQueryStatement;
    /**
     * Prepared statement for fetching the authors of a publication.
     */
    private final PreparedStatement authorsQueryStatement;
    /**
     * Prepared statement for fetching the publishers of a publication
     */
    private final PreparedStatement publisherQueryStatement;
    /**
     * Prepared statement for fetching the journal of a article in journal.
     */
    private final PreparedStatement journalQueryStatement;
    /**
     * Prepared statement for fetching the collected volume of article in a
     * collected volume.
     */
    private final PreparedStatement collectedVolumeQueryStatement;
    /**
     * Prepared statement for fetching the proceedings of a paper from a
     * proceedings publications (InProceedings).
     */
    private final PreparedStatement proceedingsQueryStatement;

    /**
     * Prepared statement for fetching the organisation of grey literature
     * publications.
     */
    private final PreparedStatement organizationQueryStatement;

    /**
     * Prepared statement for fetching the series of publication.
     */
    private final PreparedStatement seriesQueryStatement;

    /**
     * Default limit per of publications per page.
     */
    private int limit = 20;

    /**
     * Additional join statements for the publications query. Maybe required if
     * you are using non standard types.
     */
    private final List<String> additionalJoins = new ArrayList<>();

    /**
     * The constructor creates to prepared statements.
     */
    public PublicationList() {
        try {
            final Connection connection = SessionManager
                .getSession()
                .getConnection();

            availableYearsQueryStatement = connection.prepareStatement(
                "SELECT DISTINCT year "
                    + "FROM cms_items "
                    + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                    + "JOIN content_types ON cms_items.type_id = content_types.type_id "
                + "JOIN ct_publications ON cms_items.item_id = ct_publications.publication_id "
                + "WHERE parent_id IN (SELECT object_id FROM cat_object_category_map WHERE category_id = ?) AND version = 'live' "
                + "ORDER BY year DESC");

            authorsQueryStatement = connection.prepareStatement(
                "SELECT surname, givenname, titlepre, titlepost, editor, authorship_order "
                + "FROM cms_persons "
                    + "JOIN cms_items ON cms_persons.person_id = cms_items.item_id "
                + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                + "JOIN ct_publications_authorship ON cms_bundles.bundle_id = ct_publications_authorship.person_id "
                + "WHERE publication_id = ? "
                    + "ORDER BY authorship_order");

            publisherQueryStatement = connection.prepareStatement(
                "SELECT publishername, ct_publisher.place "
                    + "FROM ct_publisher "
                    + "JOIN cms_items ON ct_publisher.publisher_id = cms_items.item_id "
                + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                + "JOIN ct_publication_with_publisher_publisher_map ON cms_bundles.bundle_id = ct_publication_with_publisher_publisher_map.publisher_id "
                + "WHERE publication_id = ?");

            journalQueryStatement = connection.prepareStatement(
                "SELECT title "
                    + "FROM cms_items "
                    + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                    + "JOIN ct_journal ON cms_items.item_id = ct_journal.journal_id "
                + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                + "JOIN ct_journal_article_map ON cms_bundles.bundle_id = ct_journal_article_map.journal_id "
                + "WHERE article_in_journal_id = ?"
            );

            collectedVolumeQueryStatement = connection.prepareStatement(
                "SELECT cms_items.item_id, name, version, language, master_id, "
                    + "parent_id, title, cms_pages.description, year, abstract, "
                + "misc, reviewed, authors, firstPublished, lang, isbn, "
                    + "ct_publication_with_publisher.volume, number_of_volumes, "
                + "_number_of_pages, ct_publication_with_publisher.edition "
                    + "FROM cms_items "
                    + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                    + "JOIN ct_publications ON cms_items.item_id = ct_publications.publication_id "
                + "JOIN ct_publication_with_publisher ON ct_publications.publication_id = ct_publication_with_publisher.publication_with_publisher_id "
                + "JOIN ct_collected_volume ON ct_publication_with_publisher.publication_with_publisher_id = ct_collected_volume.collected_volume_id "
                + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                + "JOIN ct_collected_volume_article_map ON cms_bundles.bundle_id = ct_collected_volume_article_map.collected_volume_id "
                + "WHERE ct_collected_volume_article_map.article_id = ?"
            );

            proceedingsQueryStatement = connection.prepareStatement(
                "SELECT cms_items.item_id, name, version, language, master_id, "
                    + "parent_id, title, cms_pages.description, year, abstract, "
                + "misc, reviewed, authors, firstPublished, lang, isbn, "
                    + "ct_publication_with_publisher.volume, number_of_volumes, "
                + "_number_of_pages, ct_publication_with_publisher.edition "
                    + "nameofconference, place_of_conference, date_from_of_conference, date_to_of_conference "
                + "FROM cms_items "
                    + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                    + "JOIN ct_publications ON cms_items.item_id = ct_publications.publication_id "
                + "JOIN ct_publication_with_publisher ON ct_publications.publication_id = ct_publication_with_publisher.publication_with_publisher_id "
                + "JOIN ct_proceedings ON ct_publication_with_publisher.publication_with_publisher_id = ct_proceedings.proceedings_id "
                + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                + "JOIN ct_proceedings_papers_map ON cms_bundles.bundle_id = ct_proceedings_papers_map.proceedings_id "
                + "WHERE ct_proceedings_papers_map.paper_id = ?"
            );

            organizationQueryStatement = connection.prepareStatement(
                "SELECT cms_items.item_id, name, version, language, master_id, "
                    + "parent_id, title, cms_pages.description "
                    + "FROM cms_items "
                    + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                    + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                + "JOIN ct_unpublished_organization_map ON cms_bundles.bundle_id = ct_unpublished_organization_map.organization_id "
                + "WHERE ct_unpublished_organization_map.unpublished_id = ?");

            seriesQueryStatement = connection.prepareStatement(
                "SELECT cms_items.item_id, name, version, language, master_id, "
                    + "parent_id, title, cms_pages.description, ct_publications_volume_in_series.volumeofseries "
                + "FROM cms_items "
                    + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                    + "JOIN ct_series ON cms_items.item_id = ct_series.series_id "
                + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                + "JOIN ct_publications_volume_in_series ON cms_bundles.bundle_id = ct_publications_volume_in_series.series_id "
                + "WHERE ct_publications_volume_in_series.publication_id = ?");

        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    /**
     * The {@code generateXML} does most of the work, including executing the
     * queries and creating the XML.
     *
     * @param request
     * @param response
     *
     * @return
     */
    @Override
    public Element generateXML(final HttpServletRequest request,
                               final HttpServletResponse response) {

        final Connection connection = SessionManager
            .getSession()
            .getConnection();

        final String categoryId = getCategory().getID().toString();

        final Element listElem = Navigation.newElement(
            "sci-publication-list");
        final Element filtersElem = listElem.newChildElement("filters");
        final Element sortElem = filtersElem.newChildElement("sort");

        final PreparedStatement publicationsQueryStatement;
        final StringBuffer whereBuffer = new StringBuffer();
        final int page;
        final int offset;
        final String titleFilter;
        final Integer yearFilter;
        final String authorsFilter;
        final String orderByParam;
        try {
//            final String 
            titleFilter = Globalization.decodeParameter(request,
                                                        "title");
//            final String titleFilter = request.getParameter("title");
//            final Integer yearFilter;
            if (request.getParameter("yearOfPublication") == null) {
                yearFilter = null;
            } else if (request.getParameter("yearOfPublication").matches("\\d*")) {
                yearFilter = Integer.parseInt(request.getParameter(
                    "yearOfPublication"));
            } else {
                yearFilter = null;
            }
//            final String 
            authorsFilter = request.getParameter("authorsStr");
            if ((titleFilter != null && !titleFilter.trim().isEmpty())
                    || yearFilter != null
                    || (authorsFilter != null && !authorsFilter.trim().isEmpty())) {

                whereBuffer.append(" AND ");
            }
            if (titleFilter != null && !titleFilter.trim().isEmpty()) {
                whereBuffer
                    .append("LOWER(title) LIKE '%%")
                    .append(titleFilter.toLowerCase())
                    .append("%%' ");
                final Element titleFilterElem = filtersElem
                    .newChildElement("title");
                titleFilterElem.setText(titleFilter);
            }
            if (yearFilter != null) {
                if (titleFilter != null && !titleFilter.trim().isEmpty()) {
                    whereBuffer.append(" AND ");
                }
                whereBuffer.append("year = ").append(yearFilter);
                final Element yearFilterElem = filtersElem
                    .newChildElement("year");
                yearFilterElem.setText(Integer.toString(yearFilter));
            }
            if (authorsFilter != null && !authorsFilter.trim().isEmpty()) {
                if ((titleFilter != null && !titleFilter.trim().isEmpty())
                        || yearFilter != null) {
                    whereBuffer.append(" AND ");
                }
                whereBuffer
                    .append("LOWER(authors) LIKE '%%")
                    .append(authorsFilter.toLowerCase())
                    .append("%%' ");
                final Element authorsFilterElem = filtersElem
                    .newChildElement("authors");
                authorsFilterElem.setText(authorsFilter);
            }

//            final String orderByParam
            if (request.getParameter("sort") == null) {
                orderByParam = "yearDesc";
            } else {
                orderByParam = request.getParameter("sort");
            }
            final String orderBy;
            switch (orderByParam) {
                case "title":
                    orderBy = "ORDER BY title, authors, year DESC ";
                    sortElem.setText("title");
                    break;
                case "authors":
                    orderBy = "ORDER BY authors, title, year DESC ";
                    sortElem.setText("authors");
                    break;
                case "yearAsc":
                    orderBy = "ORDER BY year ASC, authors, title ";
                    sortElem.setText("yearAsc");
                    break;
                default:
                    orderBy = "ORDER BY year DESC, authors, title ";
                    sortElem.setText("yearDesc");
                    break;
            }

            final String additionalJoinsStr = String
                .join("",
                      this.additionalJoins.toArray(
                          new String[this.additionalJoins.size()]));

            publicationsQueryStatement = connection
                .prepareStatement(String.format(PUBLIATIONS_QUERY_TEMPLATE,
                                                additionalJoinsStr,
                                                whereBuffer.toString(),
                                                orderBy));

            publicationsQueryStatement.setString(1, categoryId);
            publicationsQueryStatement.setString(2, GlobalizationHelper
                                                 .getNegotiatedLocale()
                                                 .getLanguage());
            publicationsQueryStatement.setInt(3, limit);

            if (request.getParameter("page") == null) {
                page = 1;
                publicationsQueryStatement.setInt(4, 0);
                offset = 0;
            } else {
                page = Integer.parseInt(request.getParameter("page"));
                offset = (page - 1) * limit;

                publicationsQueryStatement.setInt(4, offset);
            }

        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }

        try (final ResultSet mainQueryResult = publicationsQueryStatement
            .executeQuery()) {

            availableYearsQueryStatement.setString(1, categoryId);
            final ResultSet availableYearsResultSet
                                = availableYearsQueryStatement.executeQuery();
            final Element availableYearsElem = filtersElem
                .newChildElement("available-years");
            while (availableYearsResultSet.next()) {
                final Element yearElem = availableYearsElem
                    .newChildElement("year");
                yearElem.setText(Integer.toString(availableYearsResultSet
                    .getInt("year")));
            }

            final Element paginatorElem = listElem.newChildElement("paginator");

            final String additionalJoinsStr = String
                .join("",
                      this.additionalJoins.toArray(
                          new String[this.additionalJoins.size()]));
            final PreparedStatement countPublicationsQueryStatement = connection
                .prepareStatement(String.format(
                    COUNT_PUBLICATIONS_QUERY_TEMPLATE,
                    additionalJoinsStr,
                    whereBuffer.toString()));

            countPublicationsQueryStatement.setString(1, categoryId);
            countPublicationsQueryStatement.setString(2, GlobalizationHelper
                                                      .getNegotiatedLocale()
                                                      .getLanguage());
            final ResultSet countResultSet = countPublicationsQueryStatement
                .executeQuery();
            final int count;
            if (countResultSet.next()) {
                count = countResultSet.getInt(1);
                paginatorElem.addAttribute("count", Integer.toString(count));
            } else {
                count = 0;
            }

            final int maxPages = (int) Math.ceil(count / (double) limit);

            paginatorElem.addAttribute("maxPages", Integer.toString(maxPages));
            paginatorElem.addAttribute("currentPage", Integer.toString(page));
            paginatorElem.addAttribute("offset", Integer.toString(offset));
            paginatorElem.addAttribute("limit", Integer.toString(limit));

            if (page < maxPages) {
                final StringBuffer nextPageLinkBuffer = new StringBuffer(
                    "?page=");
                nextPageLinkBuffer.append(page + 1);
                if (authorsFilter != null) {
                    nextPageLinkBuffer
                        .append("&authorsStr=")
                        .append(authorsFilter);
                }
                if (yearFilter != null) {
                    nextPageLinkBuffer
                        .append("&yearOfPublication=")
                        .append(yearFilter);
                }
                if (titleFilter != null) {
                    nextPageLinkBuffer
                        .append("&title=")
                        .append(titleFilter);
                }
                if (orderByParam != null) {
                    nextPageLinkBuffer
                        .append("&sort=")
                        .append(orderByParam);
                }
                paginatorElem.addAttribute("nextPageLink",
                                           nextPageLinkBuffer.toString());
            }

            if (page > 1) {
                final StringBuffer prevPageLinkBuffer = new StringBuffer(
                    "?page=");
                prevPageLinkBuffer.append(page - 1);
                if (authorsFilter != null) {
                    prevPageLinkBuffer
                        .append("&authorsStr=")
                        .append(authorsFilter);
                }
                if (yearFilter != null) {
                    prevPageLinkBuffer
                        .append("&yearOfPublication=")
                        .append(yearFilter);
                }
                if (titleFilter != null) {
                    prevPageLinkBuffer
                        .append("&title=")
                        .append(titleFilter);
                }
                if (orderByParam != null) {
                    prevPageLinkBuffer
                        .append("&sort=")
                        .append(orderByParam);
                }
                paginatorElem.addAttribute("prevPageLink",
                                           prevPageLinkBuffer.toString());
            }

            while (mainQueryResult.next()) {

                generateResultEntry(mainQueryResult, listElem);

            }

            return listElem;

        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    private void generateResultEntry(final ResultSet resultSet,
                                     final Element parent) throws SQLException {

        final Element publicationElem = parent.newChildElement("publication");

        final Element itemIdElem = publicationElem.newChildElement("item-id");
        itemIdElem.setText(resultSet.getBigDecimal("item_id").toString());

        final Element parentIdElem = publicationElem
            .newChildElement("parent-id");
        parentIdElem.setText(resultSet.getBigDecimal("parent_id").toString());

        final Element nameElem = publicationElem.newChildElement("name");
        nameElem.setText(resultSet.getString("name"));

        publicationElem.addAttribute("object-type",
                                     resultSet.getString("object_type"));

        final Element titleElem = publicationElem.newChildElement("title");
        titleElem.setText(resultSet.getString("title"));

        final String description = resultSet.getString("description");
        if (description != null && !description.trim().isEmpty()) {
            final Element descriptionElem = publicationElem
                .newChildElement("description");
            descriptionElem.setText(description);
        }

        final Element yearElem = publicationElem.newChildElement("year");
        yearElem.setText(Integer.toString(resultSet.getInt("year")));

        final Element abstractElem = publicationElem.newChildElement("abstract");
        abstractElem.setText(resultSet.getString("abstract"));

        final Element miscElem = publicationElem.newChildElement("misc");
        miscElem.setText(resultSet.getString("misc"));

        final Element reviewedElem = publicationElem.newChildElement("reviewed");
        reviewedElem.setText(Boolean.toString(resultSet.getBoolean("reviewed")));

        final Element authorsStrElem = publicationElem.newChildElement(
            "authorsStr");
        authorsStrElem.setText(resultSet.getString("authors"));

        final Element firstPublishedElem = publicationElem.newChildElement(
            "firstPublished");
        firstPublishedElem.setText(Integer.toString(resultSet.getInt(
            "firstPublished")));

        final Element langElem = publicationElem.newChildElement("lang");
        langElem.setText(resultSet.getString("lang"));

        final Element isbnElem = publicationElem.newChildElement("isbn");
        isbnElem.setText(resultSet.getString("isbn"));

        final Element volumeElem = publicationElem.newChildElement("volume");
        volumeElem.setText(Integer.toString(resultSet.getInt("volume")));

        final Element numberOfVolumesElem = publicationElem.newChildElement(
            "volume-of-volumes");
        numberOfVolumesElem.setText(Integer.toString(resultSet.getInt(
            "number_of_volumes")));

        final Element numberOfPagesElem = publicationElem.newChildElement(
            "number-of-pages");
        numberOfPagesElem.setText(Integer.toString(resultSet.getInt(
            "_number_of_pages")));

        final Element editionElem = publicationElem.newChildElement("edition");
        editionElem.setText(resultSet.getString("edition"));

        if (Proceedings.BASE_DATA_OBJECT_TYPE.equals(resultSet.getString(
            "object_type"))) {
            final Element nameOfConferenceElem = publicationElem
                .newChildElement(
                    "name-of-conference");
            nameOfConferenceElem
                .setText(resultSet.getString("nameofconference"));

            final Element placeOfConferenceElem = publicationElem
                .newChildElement(
                    "place-of-conference");
            placeOfConferenceElem.setText(resultSet.getString(
                "place_of_conference"));

            if (resultSet.getDate("date_from_of_conference") != null) {
                final Element dateFromOfConferenceElem = publicationElem
                    .newChildElement("date-from-of-conference");
                final Calendar dateFromOfConference = Calendar.getInstance();
                dateFromOfConference.setTime(resultSet.getDate(
                    "date_from_of_conference"));

                dateFromOfConferenceElem.addAttribute("year", Integer.toString(
                                                      dateFromOfConference.get(
                                                          Calendar.YEAR)));
                dateFromOfConferenceElem.addAttribute("month", Integer.toString(
                                                      dateFromOfConference.get(
                                                          Calendar.MONTH)));
                dateFromOfConferenceElem.addAttribute("day", Integer.toString(
                                                      dateFromOfConference.get(
                                                          Calendar.DAY_OF_MONTH)));
            }

            if (resultSet.getDate("date_to_of_conference") != null) {
                final Element dateToOfConferenceElem = publicationElem
                    .newChildElement("date-from-of-conference");
                final Calendar dateToOfConference = Calendar.getInstance();
                dateToOfConference.setTime(resultSet.getDate(
                    "date_to_of_conference"));

                dateToOfConferenceElem.addAttribute("year", Integer.toString(
                                                    dateToOfConference.get(
                                                        Calendar.YEAR)));
                dateToOfConferenceElem.addAttribute("month", Integer.toString(
                                                    dateToOfConference.get(
                                                        Calendar.MONTH)));
                dateToOfConferenceElem.addAttribute("day", Integer.toString(
                                                    dateToOfConference.get(
                                                        Calendar.DAY_OF_MONTH)));
            }
        }

        if (ArticleInCollectedVolume.BASE_DATA_OBJECT_TYPE.equals(resultSet
            .getString("object_type"))) {

            final Element pagesFromElem = publicationElem.newChildElement(
                "pages-from");
            pagesFromElem.setText(Integer.toString(resultSet.getInt(
                "collvol_pages_from")));

            final Element pagesToElem = publicationElem.newChildElement(
                "pages-to");
            pagesToElem.setText(Integer.toString(resultSet.getInt(
                "collvol_pages_to")));

            final Element chapterElem = publicationElem.newChildElement(
                "chapter");
            chapterElem.setText(resultSet.getString("chapter"));
        }

        if (ArticleInJournal.BASE_DATA_OBJECT_TYPE.equals(resultSet.getString(
            "object_type"))) {

            final Element pagesFromElem = publicationElem.newChildElement(
                "pages-from");
            pagesFromElem.setText(Integer.toString(resultSet.getInt(
                "journal_pages_from")));

            final Element pagesToElem = publicationElem.newChildElement(
                "pages-to");
            pagesToElem.setText(Integer.toString(resultSet.getInt(
                "journal_pages_to")));

            final Element issueElem = publicationElem.newChildElement("issue");
            issueElem.setText(resultSet.getString("issue"));

            final Element journalVolumeElem = publicationElem.newChildElement(
                "volume-of-journal");
            journalVolumeElem.setText(Integer.toString(resultSet.getInt(
                "journal_volume")));

            if (resultSet.getDate("publication_date") != null) {
                final Element publicationDateElem = publicationElem
                    .newChildElement("publication-date");
                final Calendar publicationDate = Calendar.getInstance();
                publicationDate.setTime(resultSet.getDate("publication_date"));

                publicationDateElem.addAttribute("year", Integer.toString(
                                                 publicationDate.get(
                                                     Calendar.YEAR)));
                publicationDateElem.addAttribute("month", Integer.toString(
                                                 publicationDate.get(
                                                     Calendar.MONTH)));
                publicationDateElem.addAttribute("day", Integer.toString(
                                                 publicationDate.get(
                                                     Calendar.DAY_OF_MONTH)));

            }
        }

        if (Expertise.BASE_DATA_OBJECT_TYPE.equals(resultSet.getString(
            "object_type"))) {

            final Element expertisePlaceElem = publicationElem.newChildElement(
                "expertise-place");
            expertisePlaceElem.setText(resultSet.getString("expertise_place"));

            final Element expertiseNumberOfPagesElem = publicationElem
                .newChildElement("expertise-number-of-pages");
            expertiseNumberOfPagesElem.setText(Integer.toString(resultSet
                .getInt("expertise_number_of_pages")));
        }

        if (InProceedings.BASE_DATA_OBJECT_TYPE.equals(resultSet.getString(
            "object_type"))) {

            final Element pagesFromElem = publicationElem.newChildElement(
                "inproceedings-pages-from");
            pagesFromElem.setText(Integer.toString(resultSet.getInt(
                "inproceedings_pages_from")));

            final Element pagesToElem = publicationElem.newChildElement(
                "inproceedings-pages-to");
            pagesToElem.setText(Integer.toString(resultSet.getInt(
                "inproceedings_pages_to")));
        }

        if (InternetArticle.BASE_DATA_OBJECT_TYPE.equals(resultSet.getString(
            "object_type"))) {

            final Element placeElem = publicationElem.newChildElement(
                "internet-article-place");
            placeElem.setText(resultSet.getString("internet_article_place"));

            final Element numberElem = publicationElem.newChildElement(
                "internet-article-number");
            numberElem.setText(resultSet.getString("internet_article_number"));

            final Element internetArticleNumberOfPagesElem = publicationElem
                .newChildElement(
                    "internet-article-number-of-pages");
            internetArticleNumberOfPagesElem.setText(Integer.toString(resultSet
                .getInt(
                    "internet_article_number_of_pages")));

            final Element internetArticleEditionElem = publicationElem
                .newChildElement(
                    "internet-article-edition");
            internetArticleEditionElem.setText(resultSet.getString(
                "internet_article_edition"));

            final Element issnElem = publicationElem.newChildElement(
                "internet-article-issn");
            issnElem.setText(resultSet.getString("internet_article_issn"));

            final Element urlElem = publicationElem.newChildElement(
                "internet-article-url");
            urlElem.setText(resultSet.getString("internet_article_url"));

            final Element urnElem = publicationElem.newChildElement(
                "internet-article-urn");
            urnElem.setText(resultSet.getString("internet_article_urn"));

            final Element doiElem = publicationElem.newChildElement(
                "internet-article-doi");
            doiElem.setText(resultSet.getString("internet_article_doi"));

            if (resultSet.getDate("internet_article_last_accessed") != null) {

                final Element lastAccessedElem = publicationElem
                    .newChildElement("internet-article-last-accessed");
                final Calendar lastAccessed = Calendar.getInstance();
                lastAccessed.setTime(resultSet.getDate(
                    "internet_article_last_accessed"));

                lastAccessedElem.addAttribute("year", Integer.toString(
                                              lastAccessed.get(Calendar.YEAR)));
                lastAccessedElem.addAttribute("month", Integer.toString(
                                              lastAccessed.get(
                                                  Calendar.MONTH)));
                lastAccessedElem.addAttribute("day", Integer.toString(
                                              lastAccessed.get(
                                                  Calendar.DAY_OF_MONTH)));
            }

            if (resultSet.getDate("internet_article_publication_date") != null) {

                final Element publicationDateElem = publicationElem
                    .newChildElement("internet-article-publication-date");
                final Calendar lastAccessed = Calendar.getInstance();
                lastAccessed.setTime(resultSet.getDate(
                    "internet_article_publication_date"));

                publicationDateElem.addAttribute("year", Integer.toString(
                                                 lastAccessed.get(
                                                     Calendar.YEAR)));
                publicationDateElem.addAttribute("month", Integer.toString(
                                                 lastAccessed.get(
                                                     Calendar.MONTH)));
                publicationDateElem.addAttribute("day", Integer.toString(
                                                 lastAccessed.get(
                                                     Calendar.DAY_OF_MONTH)));
            }
        }

        if (UnPublished.BASE_DATA_OBJECT_TYPE
            .equals(resultSet.getString("object_type"))
                || GreyLiterature.BASE_DATA_OBJECT_TYPE
                .equals(resultSet.getString("object_type"))
                || WorkingPaper.BASE_DATA_OBJECT_TYPE
                .equals(resultSet.getString("object_type"))) {

            final Element unpublishedPlaceElem = publicationElem
                .newChildElement("unpublished-place");
            unpublishedPlaceElem.setText(resultSet
                .getString("unpublished_place"));

            final Element unpublishedNumberElem = publicationElem
                .newChildElement("unpublished-number");
            unpublishedNumberElem.setText(resultSet.getString(
                "unpublished_number"));

            final Element unpublishedNumberOfPagesElem = publicationElem
                .newChildElement("unpublished-number-of-pages");
            unpublishedNumberOfPagesElem.setText(Integer.toString(resultSet
                .getInt("unpublished_number_of_pages")));

            if (GreyLiterature.BASE_DATA_OBJECT_TYPE.equals(resultSet.getString(
                "object_type"))) {

                final Element greyLiteraturePagesFromElem = publicationElem
                    .newChildElement("grey-literature-pages-from");
                greyLiteraturePagesFromElem.setText(Integer.toString(resultSet
                    .getInt("grey_literature_pages_from")));

                final Element greyLiteraturePagesToElem = publicationElem
                    .newChildElement("grey-literature-pages-to");
                greyLiteraturePagesToElem.setText(Integer.toString(resultSet
                    .getInt("grey_literature_pages_to")));

            }

        }

        generateAuthors(resultSet.getBigDecimal("parent_id"),
                        publicationElem);
        generatePublishers(resultSet.getBigDecimal("parent_id"),
                           publicationElem);
        generateSeries(resultSet.getBigDecimal("parent_id"),
                       publicationElem);

        if (ArticleInJournal.BASE_DATA_OBJECT_TYPE.equals(resultSet.getString(
            "object_type"))) {
            generateJournal(resultSet.getBigDecimal("parent_id"),
                            publicationElem);
        }

        if (ArticleInCollectedVolume.BASE_DATA_OBJECT_TYPE.equals(resultSet
            .getString("object_type"))) {

            generateCollectedVolume(resultSet.getBigDecimal("parent_id"),
                                    publicationElem);
        }

        if (InProceedings.BASE_DATA_OBJECT_TYPE.equals(resultSet
            .getString("object_type"))) {

            generateProceedings(resultSet.getBigDecimal("parent_id"),
                                publicationElem);
        }

        if (UnPublished.BASE_DATA_OBJECT_TYPE
            .equals(resultSet.getString("object_type"))
                || GreyLiterature.BASE_DATA_OBJECT_TYPE
                .equals(resultSet.getString("object_type"))
                || WorkingPaper.BASE_DATA_OBJECT_TYPE
                .equals(resultSet.getString("object_type"))) {

            generateOrganization(resultSet.getBigDecimal("parent_id"),
                                 publicationElem);

        }

    }

    private void generateAuthors(final BigDecimal publicationId,
                                 final Element publicationElem)
        throws SQLException {

        authorsQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = authorsQueryStatement.executeQuery()) {

            final Element authorsElem = publicationElem.newChildElement(
                "authors");

            while (resultSet.next()) {
                final Element authorElem = authorsElem.newChildElement("author");
                authorElem.addAttribute("surname", resultSet
                                        .getString("surname"));
                authorElem.addAttribute("givenname",
                                        resultSet.getString("givenname"));
                authorElem.addAttribute("titlepre", resultSet.getString(
                                        "titlepre"));
                authorElem.addAttribute("titlepost",
                                        resultSet.getString("titlepost"));
                authorElem.addAttribute("order",
                                        Integer.toString(resultSet.getInt(
                                            "authorship_order")));
                authorElem.addAttribute("editor",
                                        Boolean.toString(resultSet.getBoolean(
                                            "editor")));
            }
        }
    }

    public void generatePublishers(final BigDecimal publicationId,
                                   final Element publicationElem)
        throws SQLException {

        publisherQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = publisherQueryStatement.executeQuery()) {

            if (resultSet.next()) {
                final Element publisherElem = publicationElem
                    .newChildElement("publisher");

                publisherElem.addAttribute("name",
                                           resultSet.getString("publishername"));
                publisherElem
                    .addAttribute("place", resultSet.getString("place"));
            }

        }
    }

    public void generateSeries(final BigDecimal publicationId,
                               final Element publicationElem)
        throws SQLException {

        seriesQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = seriesQueryStatement.executeQuery()) {

            if (resultSet.next()) {
                final Element seriesElem = publicationElem
                    .newChildElement("series");

                seriesElem.addAttribute("title", resultSet.getString("title"));
                seriesElem.addAttribute(
                    "volume-of-series",
                    resultSet.getString("volumeofseries"));
            }
        }
    }

    public void generateJournal(final BigDecimal articleId,
                                final Element publicationElem)
        throws SQLException {

        journalQueryStatement.setBigDecimal(1, articleId);

        try (final ResultSet resultSet = journalQueryStatement.executeQuery()) {

            if (resultSet.next()) {
                final Element journalElem = publicationElem
                    .newChildElement("journal");

                journalElem.addAttribute("name",
                                         resultSet.getString("title"));
            }
        }
    }

    public void generateCollectedVolume(final BigDecimal articleId,
                                        final Element publicationElem)
        throws SQLException {

        collectedVolumeQueryStatement.setBigDecimal(1, articleId);

        try (final ResultSet resultSet = collectedVolumeQueryStatement
            .executeQuery()) {

            if (resultSet.next()) {
                final Element collectedVolumeElem = publicationElem
                    .newChildElement("collected-volume");

                final Element titleElem = collectedVolumeElem
                    .newChildElement("title");
                titleElem.setText(resultSet.getString("title"));
                final Element yearElem = collectedVolumeElem
                    .newChildElement("year");
                yearElem.setText(Integer.toString(resultSet.getInt("year")));
                final Element editionElem = collectedVolumeElem
                    .newChildElement("edition");
                editionElem.setText(resultSet.getString("edition"));

                generateAuthors(resultSet.getBigDecimal("parent_id"),
                                collectedVolumeElem);
                generatePublishers(resultSet.getBigDecimal("parent_id"),
                                   collectedVolumeElem);
            }
        }
    }

    public void generateProceedings(final BigDecimal paperId,
                                    final Element publicationElem)
        throws SQLException {

        proceedingsQueryStatement.setBigDecimal(1, paperId);

        try (final ResultSet resultSet = proceedingsQueryStatement
            .executeQuery()) {

            if (resultSet.next()) {
                final Element proceedingsElem = publicationElem
                    .newChildElement("proceedings");

                final Element titleElem = proceedingsElem
                    .newChildElement("title");
                titleElem.setText(resultSet.getString("title"));
                final Element yearElem = proceedingsElem
                    .newChildElement("year");
                yearElem.setText(Integer.toString(resultSet.getInt("year")));
//                final Element editionElem = proceedingsElem
//                    .newChildElement("edition");
//                editionElem.setText(resultSet.getString("edition"));

                final Element nameOfConferenceElem = publicationElem
                    .newChildElement(
                        "name-of-conference");
                nameOfConferenceElem
                    .setText(resultSet.getString("nameofconference"));

                final Element placeOfConferenceElem = publicationElem
                    .newChildElement(
                        "place-of-conference");
                placeOfConferenceElem.setText(resultSet.getString(
                    "place_of_conference"));

                if (resultSet.getDate("date_from_of_conference") != null) {
                    final Element dateFromOfConferenceElem = publicationElem
                        .newChildElement("date-from-of-conference");
                    final Calendar dateFromOfConference = Calendar.getInstance();
                    dateFromOfConference.setTime(resultSet.getDate(
                        "date_from_of_conference"));

                    dateFromOfConferenceElem.addAttribute("year", Integer
                                                          .toString(
                                                              dateFromOfConference
                                                                  .get(
                                                                      Calendar.YEAR)));
                    dateFromOfConferenceElem.addAttribute("month", Integer
                                                          .toString(
                                                              dateFromOfConference
                                                                  .get(
                                                                      Calendar.MONTH)));
                    dateFromOfConferenceElem.addAttribute("day", Integer
                                                          .toString(
                                                              dateFromOfConference
                                                                  .get(
                                                                      Calendar.DAY_OF_MONTH)));
                }

                if (resultSet.getDate("date_to_of_conference") != null) {
                    final Element dateToOfConferenceElem = publicationElem
                        .newChildElement("date-from-of-conference");
                    final Calendar dateToOfConference = Calendar.getInstance();
                    dateToOfConference.setTime(resultSet.getDate(
                        "date_to_of_conference"));

                    dateToOfConferenceElem.addAttribute("year", Integer
                                                        .toString(
                                                            dateToOfConference
                                                                .get(
                                                                    Calendar.YEAR)));
                    dateToOfConferenceElem.addAttribute("month", Integer
                                                        .toString(
                                                            dateToOfConference
                                                                .get(
                                                                    Calendar.MONTH)));
                    dateToOfConferenceElem.addAttribute("day", Integer.toString(
                                                        dateToOfConference.get(
                                                            Calendar.DAY_OF_MONTH)));
                }

                generateAuthors(resultSet.getBigDecimal("parent_id"),
                                proceedingsElem);
                generatePublishers(resultSet.getBigDecimal("parent_id"),
                                   proceedingsElem);
            }
        }
    }

    public void generateOrganization(final BigDecimal publicationId,
                                     final Element publicationElem)
        throws SQLException {

        organizationQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = organizationQueryStatement
            .executeQuery()) {

            if (resultSet.next()) {
                final Element organizationElem = publicationElem
                    .newChildElement("organization");

                final Element titleElem = organizationElem
                    .newChildElement("title");
                titleElem.setText(resultSet.getString("title"));
            }
        }

    }

    public void addAdditionalJoin(final String join) {
        if (join.trim().toLowerCase().startsWith("join")
                || join.trim().toLowerCase().startsWith("left join")
                || join.trim().toLowerCase().startsWith("right join")) {

            additionalJoins.add(join);

        } else {
            throw new IllegalArgumentException("Join Statement must start with "
                                                   + "\"JOIN\", \"LEFT JOIN\" or \"RIGHT JOIN\".");
        }
    }

}
