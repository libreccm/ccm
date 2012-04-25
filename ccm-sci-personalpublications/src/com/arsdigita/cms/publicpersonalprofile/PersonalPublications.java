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
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
                                final PageState state,
                                final String language) {
        final long start = System.currentTimeMillis();

        final List<PublicationBundle> publications =
                                      collectPublications(person, language);

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

            return;
        } else {
            final Map<String, List<String>> groupsConfig = getGroupsConfig();
            final Map<String, List<PublicationBundle>> publicationsByGroup =
                                                       new HashMap<String, List<PublicationBundle>>();

            for (Map.Entry<String, List<String>> entry : groupsConfig.entrySet()) {
                filterPublicationsByGroup(entry.getKey(),
                                          entry.getValue(),
                                          publications,
                                          publicationsByGroup);
            }

            final List<PublicationBundle> miscGroup =
                                          filterPublicationsForMiscGroup(
                    publications, groupsConfig);
            publicationsByGroup.put(MISC, miscGroup);

            final Element availableGroupsElem =
                          personalPubsElem.newChildElement(
                    "availablePublicationGroups");
            final Element publicationsElem = personalPubsElem.newChildElement(
                    "publications");

            if (overallSize < config.getGroupSplit()) {
                publicationsElem.addAttribute("all", "all");
                for (Map.Entry<String, List<PublicationBundle>> group :
                     publicationsByGroup.entrySet()) {
                    generateXmlForGroup(group.getKey(),
                                        availableGroupsElem,
                                        publicationsElem,
                                        group.getValue(),
                                        false,
                                        true,
                                        state);
                }
            } else {
                final List<String> availableGroups = new LinkedList<String>();
                for (Map.Entry<String, List<String>> entry : groupsConfig.
                        entrySet()) {
                    if (!(publicationsByGroup.get(entry.getKey()).isEmpty())) {
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
                                                 config.getDefaultGroup(),
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

        if (logger.isDebugEnabled()) {
            logger.warn(String.format("Generated publications of %d publications "
                                      + "for '%s' (%s) in %d ms.",
                                      overallSize,
                                      person.getFullName(),
                                      person.getID().toString(),
                                      System.currentTimeMillis() - start));
        }
    }

    private List<PublicationBundle> collectPublications(
            final GenericPerson author,
            final String language) {
        final List<PublicationBundle> publications =
                                      new LinkedList<PublicationBundle>();
        final List<BigDecimal> processed = new ArrayList<BigDecimal>();
        final DataCollection collection = (DataCollection) author.
                getGenericPersonBundle().get("publication");
        DomainObject obj;
        while (collection.next()) {
            obj = DomainObjectFactory.newInstance(collection.getDataObject());
            if (obj instanceof PublicationBundle) {
                processed.add(((PublicationBundle) obj).getParent().getID());
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
        final DataCollection collection = (DataCollection) alias.
                getGenericPersonBundle().get("publication");
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
                                     final List<PublicationBundle> publications,
                                     final boolean withPaginator,
                                     final boolean generateAvailable,
                                     final PageState state) {
        List<PublicationBundle> publicationList = publications;

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
                                                      config.getPageSize());
            publicationList = publicationList.subList(paginator.getBegin(),
                                                      paginator.getEnd());
            paginator.generateXml(groupElem);
        }

        for (PublicationBundle publication : publicationList) {
            generatePublicationXml(publication.getPublication(
                    GlobalizationHelper.getNegotiatedLocale().getLanguage()),
                                   groupElem,
                                   state);
        }
    }

    private void generatePublicationXml(final Publication publication,
                                        final Element parent,
                                        final PageState state) {
        final XmlGenerator generator = new XmlGenerator(publication);
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

    private void filterPublicationsByGroup(
            final String groupName,
            final List<String> typeTokens,
            final List<PublicationBundle> publications,
            final Map<String, List<PublicationBundle>> publicationsByGroup) {
        final List<PublicationBundle> group =
                                      new LinkedList<PublicationBundle>();

        for (PublicationBundle publication : publications) {
            for (String typeToken : typeTokens) {
                if (publication.getContentType().getAssociatedObjectType().
                        equals(
                        typeToken)) {
                    group.add(publication);
                    break;
                }
            }
        }

        if (group.size() > 0) {
            publicationsByGroup.put(groupName, group);
        }
    }

    private List<PublicationBundle> filterPublicationsForMiscGroup(
            final List<PublicationBundle> publications,
            final Map<String, List<String>> groupsConfig) {
        final List<PublicationBundle> misc = new LinkedList<PublicationBundle>();

        boolean found = false;
        for (PublicationBundle publication : publications) {
            for (Map.Entry<String, List<String>> entry : groupsConfig.entrySet()) {
                for (String type : entry.getValue()) {
                    if (publication.getContentType().getAssociatedObjectType().
                            equals(type)) {
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
}
