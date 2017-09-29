package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        if (overallSize <= 0) {
            personalPubsElem.newChildElement("noPublications");
            return;
        }

        try (final ResultSet mainQueryResult = publicationsQueryStatement
            .executeQuery()) {

            final List<Map<String, Object>> publications = new LinkedList<>();
            while (mainQueryResult.next()) {
                final Map<String, Object> publication = new HashMap<>();
                publication.put("contentType", 
                                mainQueryResult.getString("content_type"));
                publication.put("title", 
                                mainQueryResult.getString("title"));
                publication.put("reviewed", 
                                mainQueryResult.getBoolean("reviewed"));
                
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
                             if (publication1.get("yearOfPublication") == null) {
                                 year1 = 0;
                             } else {
                                 year1 = (Integer) publication1
                                     .get("yearOfPublication");
                             }
                             if (publication2.get("yearOfPublication") == null) {
                                 year2 = 0;
                             } else {
                                 year2 = (Integer) publication2
                                     .get("yearOfPublication");
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
        
        final Element titleElem = publicationElem.newChildElement("title");
        titleElem.setText((String) publication.get("title"));
        
        final Element contentTypeElem = publicationElem.newChildElement("contentType");
        contentTypeElem.setText((String) publication.get("contentType"));
        
        final Element reviewedElem = publicationElem.newChildElement("reviewed");
        reviewedElem.setText(Objects.toString(publication.get("reviewed")));
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

}
