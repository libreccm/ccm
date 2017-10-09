package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.DataCollection;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@link ContentGenerator} for personal profiles which displays all
 * publications of a person. The person and be organized in groups. The groups
 * are configured via the configuration.
 *
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PersonalPublications implements ContentGenerator {

    private final static String MISC = "misc";
    private final static PersonalPublicationsConfig CONFIG
                                                        = new PersonalPublicationsConfig();
    private static final String REVIEWED = "_reviewed";
    private static final String NOT_REVIEWED = "_notreviewed";

    /**
     * Template for the query to fetch the publications assigned to the
     * category.
     */
    private final static String PUBLIATIONS_QUERY_TEMPLATE
                                    = "SELECT cms_items.item_id, name, version, language, object_type, "
                                      + "content_types.object_type AS content_type, "
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
                                          + "WHERE parent_id IN (SELECT bundle_id FROM ct_publication_bundles JOIN ct_publications_authorship ON ct_publication_bundles.bundle_id = ct_publications_authorship.publication_id WHERE person_id = ?) AND language = ? AND version = 'live' %s"
                                      + "%s ";
//                                          + "LIMIT ? OFFSET ?";
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
                                          + "WHERE parent_id IN (SELECT bundle_id FROM ct_publication_bundles JOIN ct_publications_authorship ON ct_publication_bundles.bundle_id = ct_publications_authorship.publication_id WHERE person_id = ?) AND version = 'live' %s";

    static {
        CONFIG.load();
    }

    @Override
    public void generateContent(final Element parent,
                                final GenericPerson person,
                                final PageState state,
                                final String language) {
        if (CONFIG.getUseNativeSql()) {
            generateContentNativeSql(parent, person, state, language);
        } else {
            generateContentPDL(parent, person, state, language);
        }
    }

    public void generateContentPDL(final Element parent,
                                   final GenericPerson person,
                                   final PageState state,
                                   final String language) {
        //final long start = System.currentTimeMillis();

        final List<PublicationBundle> publications = collectPublications(person,
                                                                         language);

        final Element personalPubsElem = parent.newChildElement(
            "personalPublications");
        final long overallSize;
        if (publications == null) {
            overallSize = 0;
        } else {
            overallSize = publications.size();
        }
        if (overallSize <= 0) {
            personalPubsElem.newChildElement("noPublications");
        } else {
            final Map<String, List<String>> groupsConfig = getGroupsConfig();
            final Map<String, List<PublicationBundle>> publicationsByGroup
                                                           = new LinkedHashMap<>();

            for (Map.Entry<String, List<String>> entry : groupsConfig.entrySet()) {
                filterPublicationsByGroup(entry.getKey(),
                                          entry.getValue(),
                                          publications,
                                          publicationsByGroup);
            }

            final List<PublicationBundle> miscGroup
                                              = filterPublicationsForMiscGroup(
                    publications, groupsConfig);
            publicationsByGroup.put(MISC, miscGroup);

            final Element availableGroupsElem = personalPubsElem
                .newChildElement("availablePublicationGroups");
            final Element publicationsElem = personalPubsElem.newChildElement(
                "publications");

            if (overallSize < CONFIG.getGroupSplit()) {
                publicationsElem.addAttribute("all", "all");
                for (Map.Entry<String, List<PublicationBundle>> group
                         : publicationsByGroup.entrySet()) {
                    generateXmlForGroup(group.getKey(),
                                        availableGroupsElem,
                                        publicationsElem,
                                        group.getValue(),
                                        false,
                                        true,
                                        state);
                }
            } else {
                final List<String> availableGroups = new LinkedList<>();
                for (Map.Entry<String, List<String>> entry : groupsConfig
                    .entrySet()) {
                    if ((publicationsByGroup.get(entry.getKey()) != null)
                            && !(publicationsByGroup.get(entry.getKey())
                                     .isEmpty())) {
                        generateAvailableForGroup(entry.getKey(),
                                                  availableGroupsElem);
                        availableGroups.add(entry.getKey());
                    }
                }

                if (!(publicationsByGroup.get(MISC).isEmpty())) {
                    generateAvailableForGroup(MISC, availableGroupsElem);
                    availableGroups.add(MISC);
                }

                final HttpServletRequest request = state.getRequest();
                final String group = selectGroup(request,
                                                 CONFIG.getDefaultGroup(),
                                                 availableGroups);

                generateXmlForGroup(group,
                                    availableGroupsElem,
                                    publicationsElem,
                                    publicationsByGroup.get(group),
                                    true,
                                    false,
                                    state);
            }
        }

        /*if (logger.isDebugEnabled()) {
            logger.warn(String.format("Generated publications of %d publications "
                                      + "for '%s' (%s) in %d ms.",
                                      overallSize,
                                      person.getFullName(),
                                      person.getID().toString(),
                                      System.currentTimeMillis() - start));
        }*/
    }

    public void generateContentNativeSql(final Element parent,
                                         final GenericPerson person,
                                         final PageState state,
                                         final String language) {

        final Connection connection = SessionManager
            .getSession()
            .getConnection();

        final PreparedStatement publicationsQueryStatement;
        final PreparedStatement authorsQueryStatement;
        final PreparedStatement publisherQueryStatement;
        final PreparedStatement journalQueryStatement;
        final PreparedStatement collectedVolumeQueryStatement;
        final PreparedStatement proceedingsQueryStatement;
        final PreparedStatement organizationQueryStatement;
        final PreparedStatement seriesQueryStatement;

        final StringBuffer whereBuffer = new StringBuffer();
//        final int page;
//        final int offset;
//        int limit = 20;

        final String personId = person.getParent().getID().toString();

        final int overallSize;
        try {
            authorsQueryStatement = connection
                .prepareStatement(
                    "SELECT surname, givenname, titlepre, titlepost, editor, authorship_order "
                    + "FROM cms_persons "
                        + "JOIN cms_items ON cms_persons.person_id = cms_items.item_id "
                    + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                    + "JOIN ct_publications_authorship ON cms_bundles.bundle_id = ct_publications_authorship.person_id "
                    + "WHERE publication_id = ? "
                        + "ORDER BY authorship_order");
            publisherQueryStatement = connection
                .prepareStatement(
                    "SELECT publishername, ct_publisher.place "
                        + "FROM ct_publisher "
                        + "JOIN cms_items ON ct_publisher.publisher_id = cms_items.item_id "
                    + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                    + "JOIN ct_publication_with_publisher_publisher_map ON cms_bundles.bundle_id = ct_publication_with_publisher_publisher_map.publisher_id "
                    + "WHERE publication_id = ?");
            journalQueryStatement = connection
                .prepareStatement(
                    "SELECT title "
                        + "FROM cms_items "
                        + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                    + "JOIN ct_journal ON cms_items.item_id = ct_journal.journal_id "
                    + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                    + "JOIN ct_journal_article_map ON cms_bundles.bundle_id = ct_journal_article_map.journal_id "
                    + "WHERE article_in_journal_id = ?");
            collectedVolumeQueryStatement = connection
                .prepareStatement(
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
                    + "WHERE ct_collected_volume_article_map.article_id = ?");
            proceedingsQueryStatement = connection
                .prepareStatement(
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
            organizationQueryStatement = connection
                .prepareStatement(
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

            final String orderBy = "ORDER BY year DESC, authors, title ";

            final PreparedStatement countAllStatement = connection
                .prepareStatement(String.format(
                    COUNT_PUBLICATIONS_QUERY_TEMPLATE,
                    "",
                    whereBuffer.toString()));
            countAllStatement.setString(1, personId);
            final ResultSet countAllResultSet = countAllStatement
                .executeQuery();
            if (countAllResultSet.next()) {
                overallSize = countAllResultSet.getInt(1);
            } else {
                overallSize = 0;
            }

            boolean split = overallSize < CONFIG.getGroupSplit();

            publicationsQueryStatement = connection
                .prepareStatement(String.format(PUBLIATIONS_QUERY_TEMPLATE,
                                                "",
                                                whereBuffer.toString(),
                                                orderBy));

            publicationsQueryStatement.setString(1, personId);
            publicationsQueryStatement.setString(2, language);
//            publicationsQueryStatement.setInt(3, limit);

//            if (state.getRequest().getParameter("page") == null) {
//                page = 1;
//                publicationsQueryStatement.setInt(4, 0);
//                offset = 0;
//            } else {
//                page = Integer.parseInt(state.getRequest().getParameter("page"));
//                offset = (page - 1) * limit;
//
//                publicationsQueryStatement.setInt(4, offset);
//            }
        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }

        final Element personalPubsElem = parent.newChildElement(
            "personalPublications");
        personalPubsElem.addAttribute("native-sql", "true");
        if (overallSize <= 0) {
            personalPubsElem.newChildElement("noPublications");
            return;
        }

        try (final ResultSet mainQueryResult = publicationsQueryStatement
            .executeQuery()) {

            final List<Map<String, Object>> publications = new LinkedList<>();
            while (mainQueryResult.next()) {
                final Map<String, Object> publication = new HashMap<>();
                publication.put("item_id",
                                mainQueryResult.getBigDecimal("item_id"));
                publication.put("parent_id",
                                mainQueryResult.getBigDecimal("parent_id"));
                publication.put("object_type",
                                mainQueryResult.getString("object_type"));
                publication.put("contentType",
                                mainQueryResult.getString("content_type"));
                publication.put("title",
                                mainQueryResult.getString("title"));
                publication.put("description",
                                mainQueryResult.getString("description"));
                publication.put("year",
                                mainQueryResult.getInt("year"));
                publication.put("abstract",
                                mainQueryResult.getString("abstract"));
                publication.put("misc",
                                mainQueryResult.getString("misc"));
                publication.put("reviewed",
                                mainQueryResult.getBoolean("reviewed"));
                publication.put("authorsStr",
                                mainQueryResult.getString("authors"));
                publication.put("lang",
                                mainQueryResult.getString("lang"));
                publication.put("isbn",
                                mainQueryResult.getString("isbn"));
                publication.put("volume",
                                mainQueryResult.getString("volume"));
                publication.put("number_of_volumes",
                                mainQueryResult.getString("number_of_volumes"));
                publication.put("number_of_pages",
                                mainQueryResult.getString("_number_of_pages"));
                publication.put("edition",
                                mainQueryResult.getString("edition"));
                if (Proceedings.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))) {

                    publication.put("name_of_conference",
                                    mainQueryResult.getString(
                                        "name_of_conference"));
                    publication.put("place_of_conference",
                                    mainQueryResult.getString(
                                        "place_of_conference"));
                    if (mainQueryResult.getDate("date_from_of_conference")
                            != null) {

                        final Calendar dateFromConference = Calendar
                            .getInstance();
                        dateFromConference.setTime(mainQueryResult.getDate(
                            "date_from_of_conference"));
                        publication.put("date_from_of_conference",
                                        dateFromConference);
                    }

                    if (mainQueryResult.getDate("date_to_of_conference")
                            != null) {

                        final Calendar dateFromConference = Calendar
                            .getInstance();
                        dateFromConference.setTime(mainQueryResult.getDate(
                            "date_to_of_conference"));
                        publication.put("date_to_of_conference",
                                        dateFromConference);
                    }
                }

                if (ArticleInCollectedVolume.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))) {

                    publication.put("collvol_pages_from",
                                    mainQueryResult.getInt("collvol_pages_from"));
                    publication.put("collvol_pages_to",
                                    mainQueryResult.getInt("collvol_pages_to"));
                    publication.put("chapter",
                                    mainQueryResult.getString("chapter"));
                }

                if (ArticleInJournal.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))) {

                    publication.put("journal_pages_from",
                                    mainQueryResult.getInt("journal_pages_from"));
                    publication.put("journal_pages_to",
                                    mainQueryResult.getInt("journal_pages_to"));
                    publication.put("journal_volume",
                                    mainQueryResult.getInt("journal_volume"));

                    if (mainQueryResult.getDate("publication_date") != null) {

                        final Calendar publicationDate = Calendar.getInstance();
                        publicationDate.setTime(mainQueryResult.getDate(
                            "publication_date"));

                        publication.put("publication_date",
                                        publicationDate);
                    }
                }

                if (Expertise.BASE_DATA_OBJECT_TYPE.equals(mainQueryResult
                    .getString("object_type"))) {

                    publication.put("expertise_place",
                                    mainQueryResult.getString("expertise_place"));
                    publication.put("expertise_number_of_pages",
                                    mainQueryResult.getInt(
                                        "expertise_number_of_pages"));
                }

                if (InProceedings.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))) {

                    publication.put("inproceedings_pages_from",
                                    mainQueryResult.getInt(
                                        "inproceedings_pages_from"));
                    publication.put("inproceedings_pages_to",
                                    mainQueryResult.getInt(
                                        "inproceedings_pages_to"));
                }

                if (InternetArticle.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))) {

                    publication.put("internet_article_place",
                                    mainQueryResult.getString(
                                        "internet_article_place"));

                    publication.put("internet_article_number",
                                    mainQueryResult.getInt(
                                        "internet_article_number"));

                    publication.put("internet_article_number_of_pages",
                                    mainQueryResult.getInt(
                                        "internet_article_number_of_pagess"));

                    publication.put("internet_article_edition",
                                    mainQueryResult.getString(
                                        "internet_article_edition"));

                    publication.put("internet_article_issn",
                                    mainQueryResult.getString(
                                        "internet_article_issn"));

                    publication.put("internet_article_url",
                                    mainQueryResult.getString(
                                        "internet_article_url"));

                    publication.put("internet_article_urn",
                                    mainQueryResult.getString(
                                        "internet_article_urn"));

                    publication.put("internet_article_doi",
                                    mainQueryResult.getString(
                                        "internet_article_doi"));

                    if (mainQueryResult
                        .getDate("internet_article_last_accessed") != null) {

                        final Calendar publicationDate = Calendar.getInstance();
                        publicationDate.setTime(mainQueryResult
                            .getDate("internet_article_last_accessed"));
                    }

                    if (mainQueryResult
                        .getDate("internet_article_publication_date") != null) {

                        final Calendar lastAccessed = Calendar.getInstance();
                        lastAccessed.setTime(mainQueryResult
                            .getDate("internet_article_publication_date"));
                    }
                }

                if (UnPublished.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))
                        || GreyLiterature.BASE_DATA_OBJECT_TYPE
                        .equals(mainQueryResult.getString("object_type"))
                        || WorkingPaper.BASE_DATA_OBJECT_TYPE
                        .equals(mainQueryResult.getString("object_type"))) {

                    publication.put("unpublished_place",
                                    mainQueryResult.getString(
                                        "unpublished_place"));
                    publication.put("unpublished_number",
                                    mainQueryResult.getString(
                                        "unpublished_number"));
                    publication.put("unpublished_number_of_pages",
                                    mainQueryResult.getString(
                                        "unpublished_number_of_pages"));

                    if (GreyLiterature.BASE_DATA_OBJECT_TYPE
                        .equals(mainQueryResult.getString("object_type"))) {

                        publication.put("grey_literature_pages_from",
                                        mainQueryResult.getInt(
                                            "grey_literature_pages_from"));
                        publication.put("grey_literature_pages_to",
                                        mainQueryResult.getInt(
                                            "grey_literature_pages_to"));
                    }
                }

                generateAuthorsNativeSql(
                    mainQueryResult.getBigDecimal("parent_id"),
                    publication,
                    authorsQueryStatement);
                generatePublisherNativeSql(
                    mainQueryResult.getBigDecimal("parent_id"),
                    publication,
                    publisherQueryStatement);
                generateSeriesNativeSql(
                    mainQueryResult.getBigDecimal("parent_id"),
                    publication,
                    seriesQueryStatement);

                if (ArticleInJournal.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))) {

                    generateJournalNativeSql(
                        mainQueryResult.getBigDecimal("parent_id"),
                        publication,
                        journalQueryStatement);
                }

                if (ArticleInCollectedVolume.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))) {

                    generateCollectedVolumeNativeSql(
                        mainQueryResult.getBigDecimal("parent_id"),
                        publication,
                        collectedVolumeQueryStatement,
                        authorsQueryStatement,
                        publisherQueryStatement);
                }

                if (InProceedings.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))) {

                    generateProceedingsNativeSql(
                        mainQueryResult.getBigDecimal("parent_id"),
                        publication,
                        proceedingsQueryStatement,
                        authorsQueryStatement,
                        publisherQueryStatement);
                }

                if (UnPublished.BASE_DATA_OBJECT_TYPE
                    .equals(mainQueryResult.getString("object_type"))
                        || GreyLiterature.BASE_DATA_OBJECT_TYPE
                        .equals(mainQueryResult.getString("object_type"))
                        || WorkingPaper.BASE_DATA_OBJECT_TYPE
                        .equals(mainQueryResult.getString("object_type"))) {

                    generateOrganizationNativeSql(
                        mainQueryResult.getBigDecimal("object_type"),
                        publication,
                        organizationQueryStatement);
                }

                publications.add(publication);
            }

            final Map<String, List<String>> groupsConfig = getGroupsConfig();
            final Map<String, List<Map<String, Object>>> publicationsByGroup
                                                             = new LinkedHashMap<>();

            for (Map.Entry<String, List<String>> entry : groupsConfig.entrySet()) {
                filterPublicationsByGroupNativeSql(entry.getKey(),
                                                   entry.getValue(),
                                                   publications,
                                                   publicationsByGroup);
            }

            final List<Map<String, Object>> miscGroup
                                                = filterPublicationsForMiscGroupNativeSql(
                    publications, groupsConfig);
            publicationsByGroup.put(MISC, miscGroup);

            final Element availableGroupsElem = personalPubsElem
                .newChildElement("availablePublicationGroups");
            final Element publicationsElem = personalPubsElem
                .newChildElement("publications");

            if (overallSize < CONFIG.getGroupSplit()) {
                publicationsElem.addAttribute("all", "all");
                for (final Map.Entry<String, List<Map<String, Object>>> group
                         : publicationsByGroup.entrySet()) {
                    generateXmlForGroupNativeSql(group.getKey(),
                                                 availableGroupsElem,
                                                 publicationsElem,
                                                 group.getValue(),
                                                 false,
                                                 true,
                                                 state);
                }
            } else {
                final List<String> availableGroups = new LinkedList<>();
                for (final Map.Entry<String, List<String>> entry
                         : groupsConfig.entrySet()) {

                    if ((publicationsByGroup.get(entry.getKey()) != null)
                            && !(publicationsByGroup.get(entry.getKey())
                                     .isEmpty())) {
                        generateAvailableForGroup(entry.getKey(),
                                                  availableGroupsElem);
                        availableGroups.add(entry.getKey());
                    }
                }

                if (!(publicationsByGroup.get(MISC).isEmpty())) {
                    generateAvailableForGroup(MISC, availableGroupsElem);
                    availableGroups.add(MISC);
                }

                final HttpServletRequest request = state.getRequest();
                final String group = selectGroup(request,
                                                 CONFIG.getDefaultGroup(),
                                                 availableGroups);

                generateXmlForGroupNativeSql(group,
                                             availableGroupsElem,
                                             publicationsElem,
                                             publicationsByGroup.get(group),
                                             true,
                                             false,
                                             state);
            }

        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }

//        try (final ResultSet mainQueryResult = publicationsQueryStatement
//            .executeQuery()) {
//
//            final Element personalPubsElem = parent
//                .newChildElement("personalPublications");
//            final Element paginatorElem = personalPubsElem
//                .newChildElement("paginator");
//
//            final PreparedStatement countPublicationsQueryStatement = connection
//                .prepareStatement(String.format(
//                    COUNT_PUBLICATIONS_QUERY_TEMPLATE,
//                    "",
//                    whereBuffer.toString()));
//            countPublicationsQueryStatement.setString(1, personId);
//            final ResultSet countResultSet = countPublicationsQueryStatement
//                .executeQuery();
//            final int count;
//            if (countResultSet.next()) {
//                count = countResultSet.getInt(1);
//                paginatorElem.addAttribute("count", Integer.toString(count));
//            } else {
//                count = 0;
//            }
//
//            final int maxPages = (int) Math.ceil(count / (double) limit);
//
//            paginatorElem.addAttribute("maxPages", Integer.toString(maxPages));
//            paginatorElem.addAttribute("currentPage", Integer.toString(page));
//            paginatorElem.addAttribute("offset", Integer.toString(offset));
//            paginatorElem.addAttribute("limit", Integer.toString(limit));
//            final Map<String, List<String>> groupsConfig = getGroupsConfig();
//            final String group = selectGroup(request,
//                                             Config.getDefaultGroup(),
//                                             availableGroups);
//
//            final Element availableGroupsElem = personalPubsElem
//                .newChildElement("availablePublicationGroups");
//            final Element publicationsElem = personalPubsElem.newChildElement(
//                "publications");
//
//            while (mainQueryResult.next()) {
//
//                if (split) {
//
//                }
//
//                final Element elem = publicationsElem.newChildElement(
//                    "publication");
//                final Element title = elem.newChildElement("title");
//                title.setText(mainQueryResult.getString("title"));
//
//            }
//
//        } catch (SQLException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
    }

    private List<PublicationBundle> collectPublications(
        final GenericPerson author, final String language) {
        final List<PublicationBundle> publications = new LinkedList<>();
        //final List<BigDecimal> processed = new ArrayList<BigDecimal>();
        final DataCollection collection = (DataCollection) author
            .getGenericPersonBundle().get("publication");
        collection.addEqualsFilter("version", "live");
        DomainObject obj;
        while (collection.next()) {
            obj = DomainObjectFactory.newInstance(collection.getDataObject());
            if (obj instanceof PublicationBundle) {
                //processed.add(((PublicationBundle) obj).getParent().getID());
                publications.add((PublicationBundle) obj);
            }
        }

        if (author.getAlias() != null) {
            collectPublications(author.getAlias(), publications, language);
        }

        return publications;
    }

    private void collectPublications(final GenericPerson alias,
                                     final List<PublicationBundle> publications,
                                     final String language) {
        final DataCollection collection = (DataCollection) alias
            .getGenericPersonBundle().get("publication");
        collection.addEqualsFilter("version", "live");
        DomainObject obj;
        while (collection.next()) {
            obj = DomainObjectFactory.newInstance(collection.getDataObject());
            if (obj instanceof PublicationBundle) {
                publications.add((PublicationBundle) obj);
            }
        }

        if (alias.getAlias() != null) {
            collectPublications(alias.getAlias(), publications, language);
        }
    }

    private Map<String, List<String>> getGroupsConfig() {
        final String conf = CONFIG.getPublictionGroups();

        final Map<String, List<String>> groups = new LinkedHashMap<>();
        final String[] groupTokens = conf.split(";");

        for (String groupToken : groupTokens) {
            processGroupToken(groupToken, groups);
        }

        return groups;
    }

    private void processGroupToken(final String groupToken,
                                   final Map<String, List<String>> groups) {
        final String[] tokens = groupToken.split(":");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Failed to parse group config.");
        }

        final List<String> types = new ArrayList<>();
        final String[] typeTokens = tokens[1].split(",");
        for (String typeToken : typeTokens) {
            types.add(typeToken.trim());
        }

        groups.put(tokens[0], types);
    }

    private void generateAvailableForGroup(final String groupName,
                                           final Element availableGroupsElem) {
        final Element group = availableGroupsElem.newChildElement(
            "availablePublicationGroup");
        group.addAttribute("name", groupName);
    }

    private void generateXmlForGroup(final String groupName,
                                     final Element availableGroupsElem,
                                     final Element publicationsElem,
                                     final List<PublicationBundle> publications,
                                     final boolean withPaginator,
                                     final boolean generateAvailable,
                                     final PageState state) {

        List<PublicationBundle> publicationList = publications;

        Collections.sort(publicationList, new Comparator<PublicationBundle>() {

                         @Override
                         public int compare(final PublicationBundle bundle1,
                                            final PublicationBundle bundle2) {
                             final Publication publication1 = bundle1
                                 .getPublication(GlobalizationHelper
                                     .getNegotiatedLocale().
                                     getLanguage());
                             final Publication publication2 = bundle2
                                 .getPublication(GlobalizationHelper
                                     .getNegotiatedLocale().
                                     getLanguage());

                             final int year1;
                             final int year2;
                             if (publication1.getYearOfPublication() == null) {
                                 year1 = 0;
                             } else {
                                 year1 = publication1.getYearOfPublication();
                             }
                             if (publication2.getYearOfPublication() == null) {
                                 year2 = 0;
                             } else {
                                 year2 = publication2.getYearOfPublication();
                             }

                             if (year1 < year2) {
                                 return 1;
                             } else if (year1 > year2) {
                                 return -1;
                             }

                             final String authorsStr1 = (String) publication1
                                 .get("authorsStr");
                             final String authorsStr2 = (String) publication2
                                 .get("authorsStr");

                             if ((authorsStr1 != null) && (authorsStr2 != null)
                                     && (authorsStr1.compareTo(authorsStr2) != 0)) {
                                 return authorsStr1.compareTo(authorsStr2);
                             }

                             return publication1.getTitle().compareTo(
                                 publication2.getTitle());

                         }

                     });

        if ((publications == null) || publications.isEmpty()) {
            return;
        }

        if (generateAvailable) {
            generateAvailableForGroup(groupName, availableGroupsElem);
        }

        final Element groupElem = publicationsElem.newChildElement(
            "publicationGroup");
        groupElem.addAttribute("name", groupName);

        if (withPaginator) {
            final Paginator paginator = new Paginator(state.getRequest(),
                                                      publications.size(),
                                                      CONFIG.getPageSize());
            //publicationList = publicationList.subList(paginator.getBegin() - 1, paginator.getEnd() - 1);
            publicationList = paginator.applyListLimits(publicationList,
                                                        PublicationBundle.class);
            paginator.generateXml(groupElem);
        }

        for (PublicationBundle publication : publicationList) {
            generatePublicationXml(publication.getPublication(
                GlobalizationHelper.getNegotiatedLocale().getLanguage()),
                                   groupElem, state);
        }
    }

    private void generatePublicationXml(final Publication publication,
                                        final Element parent,
                                        final PageState state) {
        final PersonalPublications.XmlGenerator generator
                                                    = new PersonalPublications.XmlGenerator(
                publication);
        generator.setItemElemName("publications", "");
        generator.setListMode(true);
        generator.generateXML(state, parent, "");
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

    private void generateXmlForGroupNativeSql(
        final String groupName,
        final Element availableGroupsElem,
        final Element publicationsElem,
        final List<Map<String, Object>> publications,
        final boolean withPaginator,
        final boolean generateAvailable,
        final PageState state) {

        List<Map<String, Object>> publicationList = publications;
        Collections.sort(publicationList,
                         new Comparator<Map<String, Object>>() {

                         @Override
                         public int compare(
                             final Map<String, Object> publication1,
                             final Map<String, Object> publication2) {

                             final int year1;
                             final int year2;
                             if (publication1.get("year") == null) {
                                 year1 = 0;
                             } else {
                                 year1 = (Integer) publication1
                                     .get("year");
                             }
                             if (publication2.get("year") == null) {
                                 year2 = 0;
                             } else {
                                 year2 = (Integer) publication2
                                     .get("year");
                             }

                             if (year1 < year2) {
                                 return 1;
                             } else if (year1 > year2) {
                                 return -1;
                             }

                             final String authorsStr1 = (String) publication1
                                 .get("authorsStr");
                             final String authorsStr2 = (String) publication2
                                 .get("authorsStr");

                             if ((authorsStr1 != null) && (authorsStr2 != null)
                                     && (authorsStr1.compareTo(authorsStr2) != 0)) {
                                 return authorsStr1.compareTo(authorsStr2);
                             }

                             return ((String) publication1
                                     .get("title"))
                                 .compareTo((String) publication2.get("title"));

                         }

                     });

        if ((publications == null) || publications.isEmpty()) {
            return;
        }

        if (generateAvailable) {
            generateAvailableForGroup(groupName, availableGroupsElem);
        }

        final Element groupElem = publicationsElem.newChildElement(
            "publicationGroup");
        groupElem.addAttribute("name", groupName);

        if (withPaginator) {
            final Paginator paginator = new Paginator(state.getRequest(),
                                                      publications.size(),
                                                      CONFIG.getPageSize());
            publicationList = (List<Map<String, Object>>) paginator
                .applyListLimits(publicationList);
            paginator.generateXml(groupElem);
        }

        for (final Map<String, Object> publication : publicationList) {
            generatePublicationXmlNativeSql(publication, groupElem, state);
        }
    }

    private void generatePublicationXmlNativeSql(
        final Map<String, Object> publication,
        final Element parent,
        final PageState state) {

        final Element publicationElem = parent.newChildElement("publication");

        final Element itemIdElem = publicationElem
            .newChildElement("item-id");
        itemIdElem.setText(publication.get("item_id").toString());

        final Element parentIdElem = publicationElem
            .newChildElement("parent-id");
        parentIdElem.setText(publication.get("parent_id").toString());

        final Element nameElem = publicationElem.newChildElement("name");
        nameElem.setText((String) publication.get("name"));

        publicationElem.addAttribute("object-type",
                                     (String) publication.get("object_type"));

        final Element titleElem = publicationElem.newChildElement("title");
        titleElem.setText((String) publication.get("title"));

        final Object description = publication.get("description");
        if (description != null && !((String) description).trim().isEmpty()) {
            final Element descriptionElem = publicationElem
                .newChildElement("description");
            descriptionElem.setText((String) description);
        }

        final Element yearElem = publicationElem.newChildElement("year");
        yearElem.setText(Integer.toString((Integer) publication.get("year")));

        final Element abstractElem = publicationElem.newChildElement("abstract");
        abstractElem.setText(Objects.toString(publication.get("abstract")));

        final Element miscElem = publicationElem.newChildElement("misc");
        miscElem.setText(Objects.toString(publication.get("misc")));

//        final Element contentTypeElem = publicationElem.newChildElement(
//            "contentType");
//        contentTypeElem.setText((String) publication.get("contentType"));
        final Element reviewedElem = publicationElem.newChildElement("reviewed");
        reviewedElem.setText(Objects.toString(publication.get("reviewed")));

        final Element authorsStrElem = publicationElem.newChildElement(
            "authorsStr");
        authorsStrElem.setText((String) publication.get("authorsStr"));

        if (publication.get("firstPublished") != null) {
            final Element firstPublishedElem = publicationElem
                .newChildElement("firstPublished");
            firstPublishedElem.setText(Integer.toString((Integer) publication
                .get("firstPublished")));
        }

        final Element langElem = publicationElem.newChildElement("lang");
        langElem.setText(Objects.toString(publication.get("lang")));

        final Element isbnElem = publicationElem.newChildElement("isbn");
        isbnElem.setText(Objects.toString(publication.get("isbn")));

        final Element volumeElem = publicationElem.newChildElement("volume");
        volumeElem.setText(Objects.toString(publication.get("volume")));

        final Element numberOfVolumesElem = publicationElem
            .newChildElement("number-of-volumes");
        numberOfVolumesElem.setText(Objects.toString(publication.get(
            "number_of_volumes")));

        final Element numberOfPagesElem = publicationElem
            .newChildElement("number-of-pages");
        numberOfPagesElem.setText(Objects.toString(publication.get(
            "number_of_pages")));

        final Element editionElem = publicationElem.newChildElement("edition");
        editionElem.setText(Objects.toString(publication.get("volume")));

        if (Proceedings.BASE_DATA_OBJECT_TYPE
            .equals(publication.get("object_type"))) {

            final Element nameOfConferenceElem = publicationElem
                .newChildElement("name-of-conference");
            nameOfConferenceElem.setText(Objects.toString(publication.get(
                "nameofconference")));

            final Element placeOfConferenceElem = publicationElem
                .newChildElement("place-of-conference");
            placeOfConferenceElem
                .setText(Objects
                    .toString(publication.get("place_of_conference")));

            if (publication.containsKey("date_from_of_conference")) {

                final Element dateFromOfConferenceElem = publicationElem
                    .newChildElement("date-from-of-conference");
                final Calendar dateFromOfConference = (Calendar) publication
                    .get("dateFromOfConference");

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

            if (publication.containsKey("date_to_of_conference")) {

                final Element dateToOfConferenceElem = publicationElem
                    .newChildElement("date-to-of-conference");
                final Calendar dateToOfConference = (Calendar) publication
                    .get("dateToOfConference");

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

        if (ArticleInCollectedVolume.BASE_DATA_OBJECT_TYPE
            .equals(publication.get("object_type"))) {

            final Element pagesFromElem = publicationElem.newChildElement(
                "pages-from");
            pagesFromElem
                .setText(Objects.toString(publication.get("collvol_pages_from")));

            final Element pagesToElem = publicationElem.newChildElement(
                "pages-to");
            pagesToElem
                .setText(Objects.toString(publication.get("collvol_pages_to")));

            final Element chapterElem = publicationElem.newChildElement(
                "chapter");
            chapterElem.setText(Objects.toString(publication.get("chapter")));
        }

        if (ArticleInJournal.BASE_DATA_OBJECT_TYPE.equals(publication.get(
            "object_type"))) {

            final Element pagesFromElem = publicationElem.newChildElement(
                "pages-from");
            pagesFromElem.setText(Objects.toString(publication.get(
                "journal_pages_from")));

            final Element pagesToElem = publicationElem.newChildElement(
                "pages-to");
            pagesToElem.setText(Objects.toString(publication.get(
                "journal_pages_to")));

            final Element issueElem = publicationElem.newChildElement("issue");
            issueElem.setText(Objects.toString(publication.get(
                "issue")));

            final Element journalVolumeElem = publicationElem.newChildElement(
                "volume-of-journal");
            journalVolumeElem.setText(Objects.toString(publication.get(
                "journal_volume")));

            if (publication.containsKey("publication_date")) {

                final Element publicationDateElem = publicationElem
                    .newChildElement("publication-date");
                final Calendar publicationDate = (Calendar) publication.get(
                    "publication_date");

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

        if (Expertise.BASE_DATA_OBJECT_TYPE.equals(publication
            .get("object_type"))) {

            final Element expertisePlaceElem = publicationElem.newChildElement(
                "expertise-place");
            expertisePlaceElem.setText(Objects.toString(publication.get(
                "expertise_place")));

            final Element expertiseNumberOfPagesElem = publicationElem
                .newChildElement("expertise-number-of-pages");
            expertiseNumberOfPagesElem.setText(Objects.toString(publication
                .get("expertise_number_of_pages")));
        }

        if (InProceedings.BASE_DATA_OBJECT_TYPE.equals(publication
            .get("object_type"))) {

            final Element pagesFromElem = publicationElem.newChildElement(
                "inproceedings-pages-from");
            pagesFromElem.setText(Objects.toString(publication.get(
                "inproceedings_pages_from")));

            final Element pagesToElem = publicationElem.newChildElement(
                "inproceedings-pages-to");
            pagesToElem.setText(Objects.toString(publication.get(
                "inproceedings_pages_to")));
        }

        if (InternetArticle.BASE_DATA_OBJECT_TYPE.equals(publication.get(
            "object_type"))) {

            final Element placeElem = publicationElem.newChildElement(
                "internet-article-place");
            placeElem.setText(Objects.toString(publication
                .get("internet_article_place")));

            final Element numberElem = publicationElem.newChildElement(
                "internet-article-number");
            numberElem.setText(Objects.toString(publication
                .get("internet_article_number")));

            final Element internetArticleNumberOfPagesElem = publicationElem
                .newChildElement(
                    "internet-article-number-of-pages");
            internetArticleNumberOfPagesElem
                .setText(Objects.toString(publication
                    .get("internet_article_number_of_pages")));

            final Element internetArticleEditionElem = publicationElem
                .newChildElement(
                    "internet-article-edition");
            internetArticleEditionElem.setText(
                Objects.toString(publication.get(
                    "internet_article_edition")));

            final Element issnElem = publicationElem.newChildElement(
                "internet-article-issn");
            issnElem.setText(Objects.toString(publication
                .get("internet_article_issn")));

            final Element urlElem = publicationElem.newChildElement(
                "internet-article-url");
            urlElem.setText(Objects.toString(publication
                .get("internet_article_url")));

            final Element urnElem = publicationElem.newChildElement(
                "internet-article-urn");
            urnElem.setText(Objects.toString(publication
                .get("internet_article_urn")));

            final Element doiElem = publicationElem.newChildElement(
                "internet-article-doi");
            doiElem.setText(Objects.toString(publication
                .get("internet_article_doi")));

            if (publication.containsKey("internet_article_last_accessed")) {

                final Element lastAccessedElem = publicationElem
                    .newChildElement("internet-article-last-accessed");
                final Calendar lastAccessed = (Calendar) publication
                    .get("internet_article_last_accessed");

                lastAccessedElem.addAttribute("year", Integer.toString(
                                              lastAccessed.get(Calendar.YEAR)));
                lastAccessedElem.addAttribute("month", Integer.toString(
                                              lastAccessed.get(
                                                  Calendar.MONTH)));
                lastAccessedElem.addAttribute("day", Integer.toString(
                                              lastAccessed.get(
                                                  Calendar.DAY_OF_MONTH)));
            }

            if (publication.containsKey("internet_article_publication_date")) {

                final Element publicationDateElem = publicationElem
                    .newChildElement("internet-article-publication-date");
                final Calendar publicationDate = (Calendar) publication
                    .get("internet_article_publication_date");

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

        if (UnPublished.BASE_DATA_OBJECT_TYPE.equals(publication.get(
            "object_type"))
                || GreyLiterature.BASE_DATA_OBJECT_TYPE.equals(publication.get(
                "object_type"))
                || WorkingPaper.BASE_DATA_OBJECT_TYPE.equals(publication.get(
                "object_type"))) {

            final Element unpublishedPlaceElem = publicationElem
                .newChildElement("unpublished-place");
            unpublishedPlaceElem.setText(Objects.toString(
                publication.get("unpublished_place")));

            final Element unpublishedNumberElem = publicationElem
                .newChildElement("unpublished-number");
            unpublishedNumberElem.setText(Objects.toString(publication.get(
                "unpublished_number")));

            final Element unpublishedNumberOfPagesElem = publicationElem
                .newChildElement("unpublished-number-of-pages");
            unpublishedNumberOfPagesElem.setText(Objects.toString(publication
                .get("unpublished_number_of_pages")));

            if (GreyLiterature.BASE_DATA_OBJECT_TYPE
                .equals(publication.get("object_type"))) {

                final Element greyLiteraturePagesFromElem = publicationElem
                    .newChildElement("grey-literature-pages-from");
                greyLiteraturePagesFromElem.setText(Objects.toString(
                    publication.get("grey_literature_pages_from")));

                final Element greyLiteraturePagesToElem = publicationElem
                    .newChildElement("grey-literature-pages-to");
                greyLiteraturePagesToElem.setText(Objects.toString(publication
                    .get("grey_literature_pages_to")));
            }
        }

        generateAuthorsXmlNativeSql(publication, publicationElem);
        generatePublisherXmlNativeSql(publication, publicationElem);

        if (publication.containsKey("series")) {

            @SuppressWarnings("unchecked")
            final Map<String, Object> series = (Map<String, Object>) publication
                .get("series");

            final Element seriesElem = publicationElem
                .newChildElement("series");

            seriesElem.addAttribute("title", Objects.toString(series
                                    .get("title")));
            seriesElem.addAttribute("volume-of-series",
                                    Objects.toString(series.get(
                                        "volume-of-series")));
        }

        if (ArticleInJournal.BASE_DATA_OBJECT_TYPE
            .equals(publication.get("object_type"))) {

            @SuppressWarnings("unchecked")
            final Map<String, Object> journal
                                          = (Map<String, Object>) publication
                    .get("journal");

            final Element journalElem = publicationElem
                .newChildElement("journal");

            journalElem.addAttribute("name",
                                     Objects.toString(journal.get("title")));

        }

        if (ArticleInCollectedVolume.BASE_DATA_OBJECT_TYPE
            .equals(publication.get("object_type"))) {

            @SuppressWarnings("unchecked")
            final Map<String, Object> collectedVolume
                                          = (Map<String, Object>) publication
                    .get(
                        "collectedVolume");

            final Element collectedVolumeElem = publicationElem
                .newChildElement("collected-volume");

            final Element collVolTitleElem = collectedVolumeElem
                .newChildElement("title");
            collVolTitleElem.setText(Objects.toString(collectedVolume.get(
                "title")));
            final Element collVolYearElem = collectedVolumeElem
                .newChildElement("year");
            collVolYearElem.setText(Objects
                .toString(collectedVolume.get("year")));
            final Element collVolEditionElem = collectedVolumeElem
                .newChildElement("edition");
            collVolEditionElem.setText(Objects.toString(collectedVolume.get(
                "edition")));

            generateAuthorsXmlNativeSql(collectedVolume, collectedVolumeElem);
            generatePublisherXmlNativeSql(collectedVolume, collectedVolumeElem);
        }

        if (InProceedings.BASE_DATA_OBJECT_TYPE
            .equals(publication.get("object_type"))) {

            @SuppressWarnings("unchecked")
            final Map<String, Object> proceedings
                                          = (Map<String, Object>) publication
                    .get(
                        "proceedings");

            final Element proceedingsElem = publicationElem
                .newChildElement("proceedings");

            final Element proceedingsTitleElem = proceedingsElem
                .newChildElement("title");
            proceedingsTitleElem.setText(Objects.toString(proceedings.get(
                "title")));
            final Element proceedingsYearElem = proceedingsElem
                .newChildElement("year");
            proceedingsYearElem.setText(Objects
                .toString(proceedings.get("year")));
//                final Element proceedingsEditionElem = proceedingsElem
//                    .newChildElement("edition");
//                proceedingsEditionElem.setText(Objects.toString(proceedings.get("edition")));

            final Element nameOfConferenceElem = publicationElem
                .newChildElement(
                    "name-of-conference");
            nameOfConferenceElem
                .setText(Objects.toString(proceedings.get("nameofconference")));

            final Element placeOfConferenceElem = publicationElem
                .newChildElement(
                    "place-of-conference");
            placeOfConferenceElem.setText(Objects.toString(proceedings.get(
                "place_of_conference")));

            if (proceedings.containsKey("date_from_of_conference")) {

                final Element dateFromOfConferenceElem = publicationElem
                    .newChildElement("date-from-of-conference");
                final Calendar dateFromOfConference = (Calendar) proceedings
                    .get("date_from_of_conference");
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

            if (proceedings.containsKey("date_to_of_conference")) {

                final Element dateToOfConferenceElem = publicationElem
                    .newChildElement("date-from-of-conference");
                final Calendar dateToOfConference = (Calendar) proceedings
                    .get("date_to_of_conference");
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

            generateAuthorsXmlNativeSql(proceedings, proceedingsElem);
            generatePublisherXmlNativeSql(proceedings, proceedingsElem);
        }

        if (UnPublished.BASE_DATA_OBJECT_TYPE
            .equals(publication.get("object_type"))
                || GreyLiterature.BASE_DATA_OBJECT_TYPE
                .equals(publication.get("object_type"))
                || WorkingPaper.BASE_DATA_OBJECT_TYPE
                .equals(publication.get("object_type"))) {

            if (publication.containsKey("organization")) {

                @SuppressWarnings("unchecked")
                final Map<String, Object> organization
                                              = (Map<String, Object>) publication
                        .get("organization");

                final Element organizationElem = publicationElem
                    .newChildElement("organization");

                final Element orgaTitleElem = organizationElem
                    .newChildElement("title");
                orgaTitleElem.setText(Objects
                    .toString(organization.get("title")));
            }
        }
    }

    private String selectGroup(final HttpServletRequest request,
                               final String defaultGroupConfig,
                               final List<String> availableGroups) {
        String group = request.getParameter("group");
        if ((group == null) || group.trim().isEmpty() || !(availableGroups
                                                           .contains(group))) {
            String defaultGroups[] = defaultGroupConfig.split(",");

            for (String defaultGroup : defaultGroups) {
                if (availableGroups.contains(defaultGroup)) {
                    group = defaultGroup;
                    break;
                }
            }
        }

        return group;
    }

    private void filterPublicationsByGroup(
        final String groupName,
        final List<String> typeTokens,
        final List<PublicationBundle> publications,
        final Map<String, List<PublicationBundle>> publicationsByGroup) {

        final List<PublicationBundle> group = new LinkedList<>();

        for (PublicationBundle publication : publications) {
            for (String typeToken : typeTokens) {
//                if (publication.getContentType().getAssociatedObjectType().equals(typeToken)) {
//                    group.add(publication);
//                    break;
//                }
                if (addPublicationToGroup(publication, typeToken, group)) {
                    break;
                }
            }
        }

        if (!group.isEmpty()) {
            publicationsByGroup.put(groupName, group);
        }
    }

    private void filterPublicationsByGroupNativeSql(
        final String groupName,
        final List<String> typeTokens,
        final List<Map<String, Object>> publications,
        final Map<String, List<Map<String, Object>>> publicationsByGroup) {

        final List<Map<String, Object>> group = new LinkedList<>();

        for (final Map<String, Object> publication : publications) {
            for (String typeToken : typeTokens) {

                if (addPublicationToGroupNativeSql(publication,
                                                   typeToken,
                                                   group)) {
                    break;
                }
            }
        }

        if (!group.isEmpty()) {
            publicationsByGroup.put(groupName, group);
        }
    }

    private boolean addPublicationToGroup(final PublicationBundle publication,
                                          final String typeToken,
                                          final List<PublicationBundle> group) {
        final String type;
        final Boolean reviewed;
        if (typeToken.endsWith(REVIEWED)) {
            type = typeToken.substring(0, typeToken.indexOf(REVIEWED));
            reviewed = Boolean.TRUE;
        } else if (typeToken.endsWith(NOT_REVIEWED)) {
            type = typeToken.substring(0, typeToken.indexOf(NOT_REVIEWED));
            reviewed = Boolean.FALSE;
        } else {
            type = typeToken;
            reviewed = null;
        }

        if (reviewed == null) {
            if (publication.getContentType().getAssociatedObjectType().equals(
                type)) {
                group.add(publication);
                return true;
            }
        } else {
            final Boolean pubReviewed = ((Publication) publication
                                         .getPrimaryInstance()).getReviewed();
            if (publication.getContentType().getAssociatedObjectType().equals(
                type)
                    && (reviewed.equals(pubReviewed) || (pubReviewed == null))) {
                group.add(publication);
                return true;
            }
        }

        return false;
    }

    private boolean addPublicationToGroupNativeSql(
        final Map<String, Object> publication,
        final String typeToken,
        final List<Map<String, Object>> group) {

        final String type;
        final Boolean reviewed;
        if (typeToken.endsWith(REVIEWED)) {
            type = typeToken.substring(0, typeToken.indexOf(REVIEWED));
            reviewed = Boolean.TRUE;
        } else if (typeToken.endsWith(NOT_REVIEWED)) {
            type = typeToken.substring(0, typeToken.indexOf(NOT_REVIEWED));
            reviewed = Boolean.FALSE;
        } else {
            type = typeToken;
            reviewed = null;
        }

        if (reviewed == null) {
            if (publication.get("contentType").equals(type)) {
                group.add(publication);
                return true;
            }
        } else {
            final Boolean pubReviewed = (Boolean) publication.get("reviewed");
            if (publication.get("contentType").equals(type)
                    && (reviewed.equals(pubReviewed) || (pubReviewed == null))) {
                group.add(publication);
                return true;
            }
        }

        return false;
    }

    private List<PublicationBundle> filterPublicationsForMiscGroup(
        final List<PublicationBundle> publications,
        final Map<String, List<String>> groupsConfig) {
        final List<PublicationBundle> misc = new LinkedList<>();

        boolean found = false;
        for (PublicationBundle publication : publications) {
            for (Map.Entry<String, List<String>> entry : groupsConfig.entrySet()) {
                for (String type : entry.getValue()) {
                    if (publication.getContentType().getAssociatedObjectType()
                        .equals(getTypeFromTypeToken(type))) {
                        found = true;
                    }
                }
            }
            if (!found) {
                misc.add(publication);
            }
            found = false;
        }

        return misc;
    }

    private List<Map<String, Object>> filterPublicationsForMiscGroupNativeSql(
        final List<Map<String, Object>> publications,
        final Map<String, List<String>> groupsConfig) {
        final List<Map<String, Object>> misc = new LinkedList<>();

        boolean found = false;
        for (final Map<String, Object> publication : publications) {
            for (final Map.Entry<String, List<String>> entry : groupsConfig
                .entrySet()) {
                for (final String type : entry.getValue()) {
                    if (publication
                        .get("contentType")
                        .equals(getTypeFromTypeToken(type))) {
                        found = true;
                    }
                }
            }
            if (!found) {
                misc.add(publication);
            }
            found = false;
        }

        return misc;
    }

    private String getTypeFromTypeToken(final String typeToken) {
        if (typeToken.endsWith(REVIEWED)) {
            return typeToken.substring(0, typeToken.indexOf(REVIEWED));
        } else if (typeToken.endsWith(NOT_REVIEWED)) {
            return typeToken.substring(0, typeToken.indexOf(NOT_REVIEWED));
        } else {
            return typeToken;
        }
    }

    private void generateAuthorsNativeSql(
        final BigDecimal publicationId,
        final Map<String, Object> publication,
        final PreparedStatement authorsQueryStatement)
        throws SQLException {

        authorsQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = authorsQueryStatement.executeQuery()) {

            final List<Map<String, Object>> authors = new ArrayList<>();

            while (resultSet.next()) {
                final Map<String, Object> author = new HashMap<>();

                author.put("surname",
                           resultSet.getString("surname"));
                author.put("givenname",
                           resultSet.getString("givenname"));
                author.put("titlepre",
                           resultSet.getString("titlepre"));
                author.put("titlepost",
                           resultSet.getString("titlepost"));
                author.put("order",
                           resultSet.getInt("authorship_order"));
                author.put("editor",
                           resultSet.getBoolean("editor"));

                authors.add(author);
            }

            publication.put("authors", authors);
        }
    }

    private void generatePublisherNativeSql(
        final BigDecimal publicationId,
        final Map<String, Object> publication,
        final PreparedStatement publisherQueryStatement)
        throws SQLException {

        publisherQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = publisherQueryStatement.executeQuery()) {

            if (resultSet.next()) {

                final Map<String, Object> publisher = new HashMap<>();
                publisher.put("name",
                              resultSet.getString("publishername"));
                publisher.put("place",
                              resultSet.getString("place"));

                publication.put("publisher", publisher);
            }
        }
    }

    private void generateSeriesNativeSql(
        final BigDecimal publicationId,
        final Map<String, Object> publication,
        final PreparedStatement seriesQueryStatement)
        throws SQLException {

        seriesQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = seriesQueryStatement.executeQuery()) {

            if (resultSet.next()) {
                final Map<String, Object> series = new HashMap<>();
                series.put("title", resultSet.getString("title"));
                series.put("volume-of-series", resultSet
                           .getString("volumeofseries"));

                publication.put("series", series);
            }
        }
    }

    private void generateJournalNativeSql(
        final BigDecimal publicationId,
        final Map<String, Object> publication,
        final PreparedStatement journalQueryStatement)
        throws SQLException {

        journalQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = journalQueryStatement.executeQuery()) {

            if (resultSet.next()) {

                final Map<String, Object> journal = new HashMap<>();

                journal.put("name", resultSet.getString("title"));

                publication.put("journal", journal);
            }
        }
    }

    private void generateCollectedVolumeNativeSql(
        final BigDecimal publicationId,
        final Map<String, Object> publication,
        final PreparedStatement collectedVolumeQueryStatement,
        final PreparedStatement authorsQueryStatement,
        final PreparedStatement publisherQueryStatement)
        throws SQLException {

        collectedVolumeQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = collectedVolumeQueryStatement
            .executeQuery()) {

            if (resultSet.next()) {

                final Map<String, Object> collectedVolume = new HashMap<>();

                collectedVolume.put("title", resultSet.getString("title"));
                collectedVolume.put("year", resultSet.getInt("year"));
                collectedVolume.put("edition", resultSet.getString("edition"));

                generateAuthorsNativeSql(resultSet.getBigDecimal("parent_id"),
                                         collectedVolume,
                                         authorsQueryStatement);
                generatePublisherNativeSql(publicationId,
                                           collectedVolume,
                                           publisherQueryStatement);

                publication.put("collectedVolume", collectedVolume);
            }
        }
    }

    private void generateProceedingsNativeSql(
        final BigDecimal publicationId,
        final Map<String, Object> publication,
        final PreparedStatement proceedingsQueryStatement,
        final PreparedStatement authorsQueryStatement,
        final PreparedStatement publisherQueryStatement)
        throws SQLException {

        proceedingsQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = proceedingsQueryStatement
            .executeQuery()) {

            if (resultSet.next()) {

                final Map<String, Object> proceedings = new HashMap<>();

                proceedings.put("title", resultSet.getString("title"));
                proceedings.put("year", resultSet.getInt("year"));
                proceedings.put("nameofconference",
                                resultSet.getString("nameofconference"));
                proceedings.put("place_of_conference",
                                resultSet.getString("place_of_conference"));

                if (resultSet.getDate("date_from_of_conference") != null) {

                    final Calendar dateFromOfConference = Calendar.getInstance();
                    dateFromOfConference.setTime(resultSet.getDate(
                        "date_from_of_conference"));
                    proceedings.put("date_from_of_conference",
                                    dateFromOfConference);
                }

                if (resultSet.getDate("date_to_of_conference") != null) {

                    final Calendar dateToOfConference = Calendar.getInstance();
                    dateToOfConference.setTime(resultSet.getDate(
                        "date_to_of_conference"));
                    proceedings.put("date_to_of_conference",
                                    dateToOfConference);
                }

                generateAuthorsNativeSql(resultSet.getBigDecimal("parent_id"),
                                         proceedings,
                                         authorsQueryStatement);
                generatePublisherNativeSql(resultSet.getBigDecimal("parent_id"),
                                           proceedings,
                                           publisherQueryStatement);

                publication.put("proceedings", proceedings);

            }
        }
    }

    private void generateOrganizationNativeSql(
        final BigDecimal publicationId,
        final Map<String, Object> publication,
        final PreparedStatement organziationQueryStatement)
        throws SQLException {

        organziationQueryStatement.setBigDecimal(1, publicationId);

        try (final ResultSet resultSet = organziationQueryStatement
            .executeQuery()) {

            if (resultSet.next()) {

                final Map<String, Object> organization = new HashMap<>();

                organization.put("title", resultSet.getString("title"));

                publication.put("organization", organization);
            }
        }
    }

    private void generateAuthorsXmlNativeSql(
        final Map<String, Object> publication,
        final Element publicationElem) {

        if (publication.containsKey("authors")) {
            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> authors
                                                = (List<Map<String, Object>>) publication
                    .get("authors");

            final Element authorsElem = publicationElem.newChildElement(
                "authors");

            for (final Map<String, Object> author : authors) {

                final Element authorElem = authorsElem.newChildElement("author");
                authorElem.addAttribute("surname",
                                        Objects.toString(author.get("surname")));
                authorElem.addAttribute("givenname",
                                        Objects
                                            .toString(author.get("givenname")));
                authorElem.addAttribute("titlepre",
                                        Objects.toString(author.get("titlepre")));
                authorElem.addAttribute("titlepost",
                                        Objects
                                            .toString(author.get("titlepost")));
                authorElem.addAttribute("order",
                                        Objects.toString(author.get("order")));
                authorElem.addAttribute("editor",
                                        Objects.toString(author.get("editor")));
            }
        }
    }

    private void generatePublisherXmlNativeSql(
        final Map<String, Object> publication,
        final Element publicationElem) {

        @SuppressWarnings("unchecked")
        final Map<String, Object> publisher
                                      = (Map<String, Object>) publication
                .get("publisher");

        final Element publisherElem = publicationElem
            .newChildElement("publisher");

        publisherElem.addAttribute(
            "name",
            Objects.toString(publisher.get("name")));
        publisherElem.addAttribute(
            "place",
            Objects.toString(publisher.get("place")));
    }

}
