package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.runtime.AbstractConfig;
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

    public PersonalPublicationsConfig() {
        publicationGroups =
        new StringParameter(
                "com.arsdigita.cms.publicpersonalprofile.publications.groups",
                Parameter.REQUIRED,
                "monographs:com.arsdigita.cms.contenttypes.Monograph;"
                + "collectedVolumeArticles:com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;"
                + "journalArticles:com.arsdigita.cms.contenttypes.ArticleInJournal;"
                + "journalArticlesRef:com.arsdigita.cms.contenttypes.ArticleInJournal_ref"
                + "collectedVolumes:com.arsdigita.cms.contenttypes.CollectedVolume");
        
        register(publicationGroups);
        
        loadInfo();
    }
    
    public final String getPublictionGroups() {
        return (String) get(publicationGroups);
    }
}
