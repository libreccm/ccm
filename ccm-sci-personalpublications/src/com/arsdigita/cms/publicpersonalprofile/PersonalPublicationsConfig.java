package com.arsdigita.cms.publicpersonalprofile;

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
     * Groups of publications. See {@link PersonalPublications} for a detailed 
     * explanation.
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

    public PersonalPublicationsConfig() {
        publicationGroups =
        new StringParameter(
                "com.arsdigita.cms.publicpersonalprofile.publications.groups",
                Parameter.REQUIRED,
                "monographs:com.arsdigita.cms.contenttypes.Monograph;"
                + "collectedVolumeArticles:com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;"
                + "journalArticles:com.arsdigita.cms.contenttypes.ArticleInJournal;"
                + "journalArticlesRef:com.arsdigita.cms.contenttypes.ArticleInJournal_ref;"
                + "collectedVolumes:com.arsdigita.cms.contenttypes.CollectedVolume");

        groupSplit = new IntegerParameter(
                "com.arsdigita.cms.publicpersonlprofile.publications.groupSplit",
                Parameter.REQUIRED,
                32);

        defaultGroup = new StringParameter(
                "com.arsdigita.cms.publicpersonalprofile.publications.defaultGroup",
                Parameter.REQUIRED,
                "monographs,journalArticlesRef,journalArticles,misc");
               
        register(publicationGroups);
        register(groupSplit);
        register(defaultGroup);

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
}
