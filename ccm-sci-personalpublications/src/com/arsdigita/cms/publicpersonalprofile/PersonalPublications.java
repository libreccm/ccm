package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.ui.PublicationXmlHelper;
import com.arsdigita.cms.contenttypes.ui.panels.Paginator;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

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
    private final static PersonalPublicationsConfig config =
                                                    new PersonalPublicationsConfig();
    private final static Logger logger = Logger.getLogger(
            PersonalPublications.class);

    static {
        config.load();
    }

    @Override
    public void generateContent(final Element parent,
                                final GenericPerson person,
                                final PageState state) {
        final long start = System.currentTimeMillis();

        final DataQuery allQuery = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getPublicationsForAuthor");
        applyAuthorFilter(person, allQuery, true);

        final Element personalPubsElem = parent.newChildElement(
                "personalPublications");
        final long overallSize;
        if (allQuery == null) {
            overallSize = 0;
        } else {
            overallSize = allQuery.size();
        }
        if (overallSize <= 0) {
            personalPubsElem.newChildElement("noPublications");

            return;
        } else {
            logger.debug(String.format("1: %d ms until now...", System.
                    currentTimeMillis() - start));
            final Element availableGroupsElem =
                          personalPubsElem.newChildElement(
                    "availablePublicationGroups");
            final Element publicationsElem = personalPubsElem.newChildElement(
                    "publications");

            final Map<String, List<String>> groupsConfig = getGroupsConfig();
            final Map<String, DataQuery> groupQueries =
                                         new LinkedHashMap<String, DataQuery>();
            logger.debug(String.format("2: %d ms until now...", System.
                    currentTimeMillis() - start));
            for (Map.Entry<String, List<String>> entry : groupsConfig.entrySet()) {
                createGroupQuery(person,
                                 entry.getKey(),
                                 entry.getValue(),
                                 groupQueries);
                logger.debug(String.format("3: %d ms until now...", System.
                        currentTimeMillis() - start));
            }

            final String miscFilter = generateFilterForMiscGroup(groupsConfig);
            final DataQuery miscQuery = SessionManager.getSession().
                    retrieveQuery(
                    "com.arsdigita.cms.contenttypes.getPublicationsForAuthor");
            applyAuthorFilter(person, miscQuery, true);
            miscQuery.addFilter(miscFilter);
            groupQueries.put(MISC, miscQuery);
            logger.debug(String.format("4: %d ms until now...", System.
                    currentTimeMillis() - start));
            logger.debug(String.format("5: %d ms until now...", System.
                    currentTimeMillis() - start));

            if (overallSize < config.getGroupSplit()) {
                publicationsElem.addAttribute("all", "all");
                for (Map.Entry<String, List<String>> entry : groupsConfig.
                        entrySet()) {
                    generateXmlForGroup(entry.getKey(),
                                        availableGroupsElem,
                                        publicationsElem,
                                        groupQueries.get(entry.getKey()),
                                        state,
                                        false,
                                        true);
                }

                generateXmlForGroup(MISC,
                                    availableGroupsElem,
                                    publicationsElem,
                                    groupQueries.get(MISC),
                                    state,
                                    false,
                                    true);
            } else {

                final List<String> availableGroups = new ArrayList<String>();
                logger.debug(String.format("6: %d ms until now...", System.
                        currentTimeMillis() - start));
                for (Map.Entry<String, List<String>> entry : groupsConfig.
                        entrySet()) {
                    if (!(groupQueries.get(entry.getKey()).isEmpty())) {
                        generateAvailableForGroup(entry.getKey(),
                                                  availableGroupsElem);
                        availableGroups.add(entry.getKey());
                    }
                    logger.debug(String.format("7: %d ms until now...", System.
                            currentTimeMillis() - start));
                }
                logger.debug(String.format("8: %d ms until now...", System.
                        currentTimeMillis() - start));

                final long b1 = System.currentTimeMillis();
                if (!(groupQueries.get(MISC).isEmpty())) {
                    generateAvailableForGroup(MISC,
                                              availableGroupsElem);
                    availableGroups.add(MISC);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("9: %d ms until now...", System.
                            currentTimeMillis() - start));
                    logger.debug(String.format(
                            "Determined if misc group is available in %d ms",
                            System.currentTimeMillis() - b1));
                    logger.debug(String.format("Determined available groups "
                                               + "in %d ms.",
                                               System.currentTimeMillis()
                                               - start));
                }

                final HttpServletRequest request = state.getRequest();
                String group = selectGroup(request, config.getDefaultGroup(),
                                           availableGroups);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Selected group: '%s'", group));
                }
                logger.debug(String.format("10: %d ms until now...", System.
                        currentTimeMillis() - start));
                generateXmlForGroup(group,
                                    availableGroupsElem,
                                    publicationsElem,
                                    groupQueries.get(group),
                                    state,
                                    true,
                                    false);
                logger.debug(String.format("11: %d ms until now...", System.
                        currentTimeMillis() - start));
            }

            allQuery.close();
            logger.debug(String.format("12: %d ms until now...", System.
                    currentTimeMillis() - start));
        }

        if (logger.isDebugEnabled()) {
            logger.warn(String.format("Generated publications of %d publications "
                                      + "for '%s' (%s) in %d ms.",
                                      overallSize,
                                      person.getFullName(),
                                      person.getID().toString(),
                                      System.currentTimeMillis() - start));
        }
    }

    private void applyAuthorFilter(final GenericPerson person,
                                   final DataQuery query,
                                   final boolean addOrders) {
        final StringBuilder authorFilterBuilder = new StringBuilder();
        authorFilterBuilder.append('(');
        authorFilterBuilder.append(String.format("authorId = %s",
                                                 person.getID().toString()));
        if (person.getAlias() != null) {
            addAliasToFilter(authorFilterBuilder, person.getAlias());
        }

        authorFilterBuilder.append(')');

        query.addFilter(authorFilterBuilder.toString());

        /*query.addFilter(String.format("authorId = %s",
        person.getID().toString()));*/
        if (Kernel.getConfig().languageIndependentItems()) {
            /*FilterFactory ff = query.getFilterFactory();
            Filter filter = ff.or().
            addFilter(ff.equals("language", com.arsdigita.globalization.GlobalizationHelper.getNegotiatedLocale().getLanguage())).
            addFilter(ff.and().
            addFilter(ff.equals("language", GlobalizationHelper.LANG_INDEPENDENT)).
            addFilter(ff.notIn("parent", "com.arsdigita.navigation.getParentIDsOfMatchedItems")
            .set("language", com.arsdigita.globalization.GlobalizationHelper.getNegotiatedLocale().getLanguage())));
            query.addFilter(filter);*/
            query.addFilter(
                    String.format("(language = '%s' or language = '%s')",
                                  GlobalizationHelper.getNegotiatedLocale().
                    getLanguage(),
                                  GlobalizationHelper.LANG_INDEPENDENT));
        } else {
            query.addEqualsFilter("language",
                                  com.arsdigita.globalization.GlobalizationHelper.
                    getNegotiatedLocale().getLanguage());
        }
        if (addOrders) {
            final String[] orders = config.getOrder().split(",");
            for (String order : orders) {
                query.addOrder(order);
            }
        }
    }

    private void addAliasToFilter(final StringBuilder builder,
                                  final GenericPerson alias) {
        builder.append(String.format("or authorId = %s", alias.getID().toString()));

        if (alias.getAlias() != null) {
            addAliasToFilter(builder, alias.getAlias());
        }
    }

    private Map<String, List<String>> getGroupsConfig() {
        final String conf = config.getPublictionGroups();

        final Map<String, List<String>> groups =
                                        new LinkedHashMap<String, List<String>>();
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

        final List<String> types = new ArrayList<String>();
        final String[] typeTokens = tokens[1].split(",");
        for (String typeToken : typeTokens) {
            types.add(typeToken.trim());
        }

        groups.put(tokens[0], types);
    }

    private String generateFilterForTypeToken(String typeToken) {
        if (typeToken.endsWith("_reviewed")) {
            return String.format("(objectType = '%s' and reviewed = 'true')",
                                 typeToken.substring(0, typeToken.length() - 9));
        } else if (typeToken.endsWith("_notreviewed")) {
            return String.format(
                    "(objectType = '%s' and (reviewed = 'false' or reviewed is null))",
                    typeToken.substring(0, typeToken.length() - 12));
        } else {
            return String.format("(objectType = '%s')", typeToken);
        }
    }

    private String generateFilterForTypeTokens(final List<String> typeTokens) {
        final StringBuffer buffer = new StringBuffer();
        for (String typeToken : typeTokens) {
            if (buffer.length() > 0) {
                buffer.append(" or ");
            }
            buffer.append(generateFilterForTypeToken(typeToken));
        }

        return buffer.toString();
    }

    private String generateFilterForMiscGroup(
            final Map<String, List<String>> groups) {
        final StringBuffer buffer = new StringBuffer();

        for (Map.Entry<String, List<String>> entry : groups.entrySet()) {
            if (buffer.length() > 0) {
                buffer.append(" and ");
            }
            buffer.append(String.format("not (%s)",
                                        generateFilterForTypeTokens(entry.
                    getValue())));
        }

        return buffer.toString();
    }

    private void applyFiltersForTypeTokens(final List<String> typeTokens,
                                           final DataQuery query) {
        query.addFilter(generateFilterForTypeTokens(typeTokens));
    }

    private void generateAvailableForGroup(final String groupName,
                                           final Element availableGroupsElem) {
        final Element group =
                      availableGroupsElem.newChildElement(
                "availablePublicationGroup");
        group.addAttribute("name", groupName);
    }

    private void generateXmlForGroup(final String groupName,
                                     final Element availableGroupsElem,
                                     final Element publicationsElem,
                                     final DataQuery query,
                                     final PageState state,
                                     final boolean withPaginator,
                                     final boolean generateAvailable) {
        if ((query == null) || query.isEmpty()) {
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
                                                      (int) query.size(),
                                                      config.getPageSize());
            paginator.applyLimits(query);
            paginator.generateXml(groupElem);
        }

        while (query.next()) {
            generatePublicationXml((BigDecimal) query.get("publicationId"),
                                   (String) query.get("objectType"),
                                   groupElem,
                                   state);
        }

    }

    private void generatePublicationXml(final BigDecimal publicationId,
                                        final String objectType,
                                        final Element parent,
                                        final PageState state) {
        final long start = System.currentTimeMillis();
        final ContentItem publication = (ContentItem) DomainObjectFactory.
                newInstance(new OID(
                objectType, publicationId));
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got domain object for publication "
                                       + "'%s' in %d ms.",
                                       publication.getName(),
                                       System.currentTimeMillis() - start));
        }
        /*final XmlGenerator generator = new XmlGenerator(publication);
        generator.setItemElemName("publications", "");
        generator.generateXML(state, parent, "");*/
        final PublicationXmlHelper xmlHelper =
                                   new PublicationXmlHelper(parent,
                                                            (Publication) publication);
        xmlHelper.generateXml();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Generated XML for publication '%s' "
                                       + "in %d ms.",
                                       publication.getName(),
                                       System.currentTimeMillis() - start));
        }
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

    private String selectGroup(final HttpServletRequest request,
                               final String defaultGroupConfig,
                               final List<String> availableGroups) {
        String group = request.getParameter("group");
        if ((group == null)
            || group.trim().isEmpty()
            || !(availableGroups.contains(group))) {
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

    private void createGroupQuery(final GenericPerson author,
                                  final String groupName,
                                  final List<String> typeTokens,
                                  final Map<String, DataQuery> groupQueries) {
        final DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getPublicationsForAuthor");
        applyAuthorFilter(author, query, true);
        applyFiltersForTypeTokens(typeTokens, query);

        groupQueries.put(groupName, query);
    }

    /* ------------ */
//  public void generateContentOld(final Element parent,
//                                   final GenericPerson person,
//                                   final PageState state) {
//        final long start = System.currentTimeMillis();
//        final List<DataObject> publications = collectPublications(person);
//
//        final Element personalPubsElem = parent.newChildElement(
//                "personalPublications");
//
//        if ((publications == null) || publications.isEmpty()) {
//            personalPubsElem.newChildElement("noPublications");
//
//            return;
//        } else {
//            final Map<String, List<Publication>> groupedPublications =
//                                                 processPublications(
//                    publications);
//
//            generateGroupsXml(personalPubsElem, groupedPublications);
//            generatePublicationsXml(personalPubsElem, groupedPublications, state);
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Generated publications of %d publications "
//                                       + "for '%s' in %d ms.",
//                                       publications.size(),
//                                       person.getFullName(),
//                                       System.currentTimeMillis() - start));
//        }
//    }
//
//    private List<DataObject> collectPublications(final GenericPerson person) {
//        final long start = System.currentTimeMillis();
//        final List<DataObject> publications = new ArrayList<DataObject>();
//        final DataCollection collection = (DataCollection) person.get(
//                "publication");
//
//        while (collection.next()) {
//            publications.add(collection.getDataObject());
//        }
//
//        if (person.getAlias() != null) {
//            collectPublications(person, publications);
//        }
//
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format(
//                    "Collected publications of '%s' in %d ms.",
//                    person.getFullName(),
//                    System.currentTimeMillis() - start));
//        }
//        return publications;
//    }
//
//    private void collectPublications(final GenericPerson alias,
//                                     final List<DataObject> publications) {
//        final DataCollection collection = (DataCollection) alias.get(
//                "publication");
//
//        while (collection.next()) {
//            publications.add(collection.getDataObject());
//        }
//
//        if (alias.getAlias() != null) {
//            collectPublications(alias, publications);
//        }
//    }
//
//    /**
//     * Processes the publications and puts them into the groups.
//     * 
//     * @param publications The publications to process
//     * @param typeGroupMap The group-type map
//     * @return A map with the group names as keys and a list of publications
//     * as value.
//     */
//    private Map<String, List<Publication>> processPublications(
//            final List<DataObject> publications) {
//
//        final long start = System.currentTimeMillis();
//        final GroupConfig groupConfig = new GroupConfig(config.
//                getPublictionGroups());
//        final Map<String, List<Publication>> pubGroups =
//                                             new LinkedHashMap<String, List<Publication>>();
//
//        for (String group : groupConfig.getGroups()) {
//            initalizePubGroupMap(pubGroups, group);
//        }
//        initalizePubGroupMap(pubGroups, MISC);
//
//        Publication publication;
//        String type;
//        String groupName;
//        Boolean reviewed;
//        List<Publication> group;
//        int i = 1;
//        for (DataObject dobj : publications) {
//            if (logger.isDebugEnabled()) {
//                logger.debug(String.format("Processing publications %d "
//                                           + "of %d...",
//                                           i,
//                                           publications.size()));
//            }
//            i++;
//            publication = (Publication) DomainObjectFactory.newInstance(dobj);
//            type = publication.getClass().getName();
//
//            if (dobj.getObjectType().hasProperty("reviewed")) {
//                reviewed = (Boolean) dobj.get("reviewed");
//                if (reviewed == null) {
//                    reviewed = Boolean.FALSE;
//                }
//                if (reviewed) {
//                    groupName = groupConfig.getTypeGroupMap().get(String.format(
//                            "%s_ref", type));
//                } else {
//                    groupName = groupConfig.getTypeGroupMap().get(String.format(
//                            "%s_noref", type));
//                }
//
//                if (groupName == null) {
//                    groupName = groupConfig.getTypeGroupMap().get(type);
//                }
//            } else {
//                groupName = groupConfig.getTypeGroupMap().get(type);
//            }
//
//            if (groupName == null) {
//                groupName = MISC;
//            }
//
//            group = pubGroups.get(groupName);
//            group.add(publication);
//        }
//
//        final PublicationGroupComparator comparator =
//                                         new PublicationGroupComparator();
//        for (List<Publication> currentGroup : pubGroups.values()) {
//            Collections.sort(currentGroup, comparator);
//        }
//
//        if (logger.isDebugEnabled()) {
//            logger.debug(String.format("Proceessed %d publications in %d ms.",
//                                       publications.size(),
//                                       System.currentTimeMillis() - start));
//        }
//        return pubGroups;
//    }
//
//    private void initalizePubGroupMap(
//            final Map<String, List<Publication>> pubGroups,
//            final String groupName) {
//        pubGroups.put(groupName, new ArrayList<Publication>());
//    }
//
//    private void generateGroupsXml(final Element parent,
//                                   final Map<String, List<Publication>> publications) {
//        final Element availableGroups = parent.newChildElement(
//                "availablePublicationGroups");
//
//        for (Map.Entry<String, List<Publication>> entry :
//             publications.entrySet()) {
//            if (!entry.getValue().isEmpty()) {
//                createAvailablePublicationGroupXml(availableGroups,
//                                                   entry.getKey());
//            }
//        }
//    }
//
//    private void createAvailablePublicationGroupXml(final Element parent,
//                                                    final String name) {
//        final Element group =
//                      parent.newChildElement("availablePublicationGroup");
//        group.addAttribute("name", name);
//    }
//
//    private void generatePublicationsXml(
//            final Element parent,
//            final Map<String, List<Publication>> publications,
//            final PageState state) {
//        final Element publicationsElem = parent.newChildElement("publications");
//
//        int numberOfPubs = 0;
//        final int groupSplit = config.getGroupSplit();
//
//        for (List<Publication> list : publications.values()) {
//            numberOfPubs += list.size();
//        }
//
//        if (numberOfPubs < groupSplit) {
//            publicationsElem.addAttribute("all", "all");
//
//            for (Map.Entry<String, List<Publication>> entry : publications.
//                    entrySet()) {
//                if (entry.getValue().size() > 0) {
//                    generatePublicationGroupXml(publicationsElem,
//                                                entry.getKey(),
//                                                entry.getValue(),
//                                                state);
//                }
//            }
//        } else {
//            final HttpServletRequest request = state.getRequest();
//            final String[] defaultGroup = config.getDefaultGroup().split(",");
//
//            String groupToShow = request.getParameter("group");
//            if ((groupToShow == null)
//                || groupToShow.isEmpty()
//                || !(publications.containsKey(groupToShow))) {
//                int i = 0;
//                groupToShow = defaultGroup[i];
//                while ((publications.get(groupToShow).isEmpty())
//                       && i < defaultGroup.length) {
//                    groupToShow = defaultGroup[i];
//                    i++;
//                }
//            }
//
//            if (groupToShow == null) {
//                groupToShow = MISC;
//            }
//
//            generatePublicationGroupXml(publicationsElem,
//                                        groupToShow,
//                                        publications.get(groupToShow),
//                                        state);
//        }
//    }
//
//    private void generatePublicationGroupXml(final Element publicationsElem,
//                                             final String groupName,
//                                             final List<Publication> publications,
//                                             final PageState state) {
//        if (publications == null) {
//            return;
//        }
//
//        final Element groupElem = publicationsElem.newChildElement(
//                "publicationGroup");
//        groupElem.addAttribute("name", groupName);
//
//        for (Publication publication : publications) {
//            generatePublicationXml(groupElem, publication, state);
//        }
//    }
//
//    private void generatePublicationXml(final Element publicationGroupElem,
//                                        final Publication publication,
//                                        final PageState state) {
//        /*final PublicPersonalProfileXmlGenerator generator =
//        new PublicPersonalProfileXmlGenerator(
//        publication);
//        generator.generateXML(state, publicationGroupElem, "");*/
//        final PublicationXmlHelper xmlHelper = new PublicationXmlHelper(
//                publicationGroupElem, publication);
//        xmlHelper.generateXml();
//    }
//
//    /**
//     * Processes the publications and puts them into the groups.
//     * 
//     * @param publications The publications to process
//     * @param typeGroupMap The group-type map
//     * @return A map with the group names as keys and a list of publications
//     * as value.
//     */
//    private class GroupConfig {
//
//        private final Map<String, String> typeGroupMap =
//                                          new HashMap<String, String>();
//        private final List<String> groups = new ArrayList<String>();
//
//        /**
//         * Processes the configuration string and puts the result into the 
//         * collections.
//         * 
//         * @param groupStr 
//         */
//        public GroupConfig(final String groupStr) {
//            final String[] groupTokens = groupStr.split(";");
//            String[] groupTokenSplit;
//            String groupName;
//            String publicationTypeTokens;
//            String[] publicationTypeTokensSplit;
//            List<String> types;
//            for (String groupToken : groupTokens) {
//                groupTokenSplit = groupToken.split(":");
//                if (groupTokenSplit.length != 2) {
//                    logger.warn(String.format(
//                            "Invalid entry in publication group config: '%s'. "
//                            + "Ignoring.",
//                            groupToken));
//                    continue;
//                }
//
//                groupName = groupTokenSplit[0];
//                groups.add(groupName);
//                publicationTypeTokens = groupTokenSplit[1];
//                publicationTypeTokensSplit = publicationTypeTokens.split(",");
//                for (String publicationTypeToken : publicationTypeTokensSplit) {
//                    typeGroupMap.put(publicationTypeToken, groupName);
//                }
//            }
//        }
//
//        public Map<String, String> getTypeGroupMap() {
//            return Collections.unmodifiableMap(typeGroupMap);
//        }
//
//        public List<String> getGroups() {
//            return Collections.unmodifiableList(groups);
//        }
//    }
//
//    private class PublicationGroupComparator implements Comparator<Publication> {
//
//        public int compare(final Publication publication1,
//                           final Publication publication2) {
//            AuthorshipCollection authors1;
//            AuthorshipCollection authors2;
//            GenericPerson author;
//            String authorsStr1;
//            String authorsStr2;
//            final StringBuffer authors1Buffer = new StringBuffer();
//            final StringBuffer authors2Buffer = new StringBuffer();
//
//            authors1 = publication1.getAuthors();
//            while (authors1.next()) {
//                author = authors1.getAuthor();
//                authors1Buffer.append(author.getSurname());
//                authors1Buffer.append(author.getGivenName());
//            }
//            authors2 = publication2.getAuthors();
//            while (authors2.next()) {
//                author = authors2.getAuthor();
//                authors2Buffer.append(author.getSurname());
//                authors2Buffer.append(author.getGivenName());
//            }
//
//            authorsStr1 = authors1Buffer.toString();
//            authorsStr2 = authors2Buffer.toString();
//
//            return authorsStr1.compareTo(authorsStr2);
//        }
//    }
}
