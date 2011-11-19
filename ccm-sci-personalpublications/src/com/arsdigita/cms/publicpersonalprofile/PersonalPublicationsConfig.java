package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PersonalPublicationsConfig extends AbstractConfig {

    /**
     * <p>
     * Groups of publications. The syntax for this string is as follows 
     * (as EBNF):
     * </p>
     * <pre>
     * publicationGroupsConfig = groupDefinition {";"groupDefinition};
     * groupDefinition = groupName":"typeDef{";"type};
     * typeDef = typeName["_ref"];
     * </pre>
     * <p>
     * The groups definition string consists of one or more group definitions. 
     * Group definitions are separated by the semicolon {@code ;}.
     * Each groups definition consists of a group name and a comma separated 
     * lists of type names. Group name and types names are separated by a colon.
     * </p>
     * <p>
     * {@code groupName} and {@code typeName} are not shown in the above 
     * grammar. A group name may contain all letters (uppercase and 
     * lowercase), all numbers and the underscore "{@code _}". A type name
     * is the fully qualified name of content type derived from 
     * {@link Publication}. A type name can be followed by the literals
     * {@code _reviewed} and {@code _notriewed}. If a type name is not followed by
     * one of this literals, all publications of the type will be put into the 
     * group. If the type name is followed by one of this literals, the property
     * {@code reviewed} is checked. If the type has this property, publications
     * can be split into reviewed and not reviewed publications.
     * </p>
     */
    private final Parameter publicationGroups;
    /**
     * If a person has more ({@code >=}) publications then this number, only one 
     * group of publications will be shown. If a person has less publications, 
     * all groups are shown.
     */
    private final Parameter groupSplit;
    /**
     * The group of publications to show if no group has been requested or the 
     * requested group is invalid. The values of this parameter 
     * <strong>must be</strong> a group defined in the {@code publicationGroups}
     * parameter. Otherwise, the {@link PersonalPublications} generator may
     * crashes! The parameter accepts a comma separated list of groups which
     * will used in order. If a person has no publications of the first group,
     * the {@link PersonalPublications} generator will try the next entry in
     * this list, then the third etc. The predefined group {@code misc} can also
     * be used here.
     */
    private final Parameter defaultGroup;
    private final Parameter pageSize;
    private final Parameter order;

    public PersonalPublicationsConfig() {
        publicationGroups =
        new StringParameter(
                "com.arsdigita.cms.publicpersonalprofile.publications.groups",
                Parameter.REQUIRED,
                "monographs:com.arsdigita.cms.contenttypes.Monograph;"
                + "collectedVolumeArticles:com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;"
                + "journalArticlesReviewed:com.arsdigita.cms.contenttypes.ArticleInJournal_reviewed;"
                + "journalArticles:com.arsdigita.cms.contenttypes.ArticleInJournal_notreviewed;"
                + "collectedVolumes:com.arsdigita.cms.contenttypes.CollectedVolume");

        groupSplit = new IntegerParameter(
                "com.arsdigita.cms.publicpersonlprofile.publications.groupSplit",
                Parameter.REQUIRED,
                12);

        defaultGroup =
        new StringParameter(
                "com.arsdigita.cms.publicpersonalprofile.publications.defaultGroup",
                Parameter.REQUIRED,
                "monographs,journalArticlesReviewed,journalArticles,misc");

        pageSize = new IntegerParameter(
                "com.arsdigita.cms.publicpersonlprofile.publications.pageSize",
                Parameter.REQUIRED,
                10);

        order = new StringParameter(
                "com.arsdigita.cms.publicpersonlprofile.publications.order",
                Parameter.REQUIRED,
                "year,authors,title");

        register(publicationGroups);
        register(groupSplit);
        register(defaultGroup);
        register(pageSize);
        register(order);

        loadInfo();
    }

    public final String getPublictionGroups() {
        return (String) get(publicationGroups);
    }

    public final Integer getGroupSplit() {
        return (Integer) get(groupSplit);
    }

    public final String getDefaultGroup() {
        return (String) get(defaultGroup);
    }

    public final Integer getPageSize() {
        return (Integer) get(pageSize);
    }

    public final String getOrder() {
        return (String) get(order);
    }
}
