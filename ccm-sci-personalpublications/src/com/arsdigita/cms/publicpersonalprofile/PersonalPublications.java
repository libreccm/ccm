package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
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

    public void generateContent(final Element parent,
                                final GenericPerson person) {
        DataCollection publications = (DataCollection) person.get("publication");

        if ((publications == null) || publications.size() == 0) {
            final Element publicationsElem = parent.newChildElement(
                    "publications");
            publicationsElem.newChildElement("noPublications");

            return;
        } else {
            final Map<String, List<Publication>> groupedPublications =
                                                 processPublications(
                    publications);
            
            generateGroupsXml(parent, groupedPublications);
            generatePublicationsXml(parent, groupedPublications);
        }
    }

    /**
     * Processes the publications and puts them into the groups.
     * 
     * @param publications The publications to process
     * @param typeGroupMap The group-type map
     * @return A map with the group names as keys and a list of publications
     * as value.
     */
    private Map<String, List<Publication>> processPublications(
            final DataCollection publications) {

        final GroupConfig groupConfig = new GroupConfig(config.
                getPublictionGroups());
        final Map<String, List<Publication>> pubGroups =
                                             new LinkedHashMap<String, List<Publication>>();

        for (String group : groupConfig.getGroups()) {
            initalizePubGroupMap(pubGroups, group);
        }
        initalizePubGroupMap(pubGroups, MISC);

        DataObject dobj;
        Publication publication;
        String type;
        String groupName;
        Boolean reviewed;
        List<Publication> group;
        while (publications.next()) {
            dobj = publications.getDataObject();
            publication = (Publication) DomainObjectFactory.newInstance(dobj);
            type = publication.getClass().getName();

            if (dobj.getObjectType().hasProperty("reviewed")) {
                reviewed = (Boolean) dobj.get("reviewed");
                if (reviewed) {
                    groupName = groupConfig.getTypeGroupMap().get(String.format(
                            "%s_ref", type));
                } else {
                    groupName = groupConfig.getTypeGroupMap().get(String.format(
                            "%s_noref", type));
                }

                if (groupName == null) {
                    groupName = groupConfig.getTypeGroupMap().get(type);
                }
            } else {
                groupName = groupConfig.getTypeGroupMap().get(type);
            }

            if (groupName == null) {
                groupName = MISC;
            }

            group = pubGroups.get(groupName);
            group.add(publication);
        }

        final PublicationGroupComparator comparator =
                                         new PublicationGroupComparator();
        for (List<Publication> currentGroup : pubGroups.values()) {
            Collections.sort(currentGroup, comparator);
        }

        return pubGroups;
    }

    private void initalizePubGroupMap(
            final Map<String, List<Publication>> pubGroups,
            final String groupName) {
        pubGroups.put(groupName, new ArrayList<Publication>());
    }

    private void generateGroupsXml(final Element parent,
                                   final Map<String, List<Publication>> publications) {
    }

    private void generatePublicationsXml(final Element parent,
                                         final Map<String, List<Publication>> publications) {
    }

    private void generatePublicationXml(final Element publicationsElem,
                                        final Publication publication) {
    }

    /**
     * Processes the publications and puts them into the groups.
     * 
     * @param publications The publications to process
     * @param typeGroupMap The group-type map
     * @return A map with the group names as keys and a list of publications
     * as value.
     */
    private class GroupConfig {

        private final Map<String, String> typeGroupMap =
                                          new HashMap<String, String>();
        private final List<String> groups = new ArrayList<String>();

        /**
         * Processes the configuration string and puts the result into the 
         * collections.
         * 
         * @param groupStr 
         */
        public GroupConfig(final String groupStr) {
            final String[] groupTokens = groupStr.split(";");
            String[] groupTokenSplit;
            String groupName;
            String publicationTypeTokens;
            String[] publicationTypeTokensSplit;
            List<String> types;
            for (String groupToken : groupTokens) {
                groupTokenSplit = groupToken.split(":");
                if (groupTokenSplit.length != 2) {
                    logger.debug(String.format(
                            "Invalid entry in publication group config: '%s'. "
                            + "Ignoring.",
                            groupToken));
                    continue;
                }

                groupName = groupTokenSplit[0];
                groups.add(groupName);
                publicationTypeTokens = groupTokenSplit[1];
                publicationTypeTokensSplit = publicationTypeTokens.split(",");
                for (String publicationTypeToken : publicationTypeTokensSplit) {
                    typeGroupMap.put(publicationTypeToken, groupName);
                }
            }
        }

        public Map<String, String> getTypeGroupMap() {
            return Collections.unmodifiableMap(typeGroupMap);
        }

        public List<String> getGroups() {
            return Collections.unmodifiableList(groups);
        }
    }

    private class PublicationGroupComparator implements Comparator<Publication> {

        public int compare(final Publication publication1,
                           final Publication publication2) {
            AuthorshipCollection authors1;
            AuthorshipCollection authors2;
            GenericPerson author;
            String authorsStr1;
            String authorsStr2;
            final StringBuffer authors1Buffer = new StringBuffer();
            final StringBuffer authors2Buffer = new StringBuffer();

            authors1 = publication1.getAuthors();
            while (authors1.next()) {
                author = authors1.getAuthor();
                authors1Buffer.append(author.getSurname());
                authors1Buffer.append(author.getGivenName());
            }
            authors2 = publication2.getAuthors();
            while (authors2.next()) {
                author = authors1.getAuthor();
                authors2Buffer.append(author.getSurname());
                authors2Buffer.append(author.getGivenName());
            }

            authorsStr1 = authors1Buffer.toString();
            authorsStr2 = authors2Buffer.toString();

            return authorsStr1.compareTo(authorsStr2);
        }
    }
}
