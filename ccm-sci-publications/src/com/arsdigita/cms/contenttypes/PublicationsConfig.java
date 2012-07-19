package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;

import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationsConfig extends AbstractConfig {

    private final Parameter attachOrgaUnitsStep;
    private final Parameter attachPublicationsStepTo;
    private final Parameter defaultAuthorsFolder;
    private final Parameter defaultSeriesFolder;
    private final Parameter defaultPublisherFolder;
    private final Parameter defaultCollectedVolumesFolder;
    private final Parameter defaultJournalsFolder;
    private final Parameter defaultArticlesInCollectedVolumeFolder;
    private final Parameter defaultOrganizationsFolder;
    private final Parameter defaultProceedingsFolder;
    private final Parameter defaultInProceedingsFolder;
    private final Parameter defaultArticlesInJournalFolder;

    public PublicationsConfig() {
        attachOrgaUnitsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_orgaunits_step",
                Parameter.REQUIRED,
                Boolean.FALSE);

        attachPublicationsStepTo =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_publications_step_to",
                Parameter.REQUIRED,
                "");

        defaultAuthorsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_authors_folder",
                Parameter.REQUIRED,
                null);

        defaultSeriesFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_series_folder",
                Parameter.REQUIRED,
                null);

        defaultPublisherFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_publisher_folder",
                Parameter.REQUIRED,
                null);

        defaultCollectedVolumesFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_collectedvolumes_folder",
                Parameter.REQUIRED,
                null);

        defaultJournalsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_journals_folder",
                Parameter.REQUIRED,
                null);

        defaultArticlesInCollectedVolumeFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_articlesincollectedvolume_folder",
                Parameter.REQUIRED,
                null);

        defaultOrganizationsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_organizations_folder",
                Parameter.REQUIRED,
                null);

        defaultProceedingsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_proceedings_folder",
                Parameter.REQUIRED,
                null);

        defaultInProceedingsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_inproccedings_folder",
                Parameter.REQUIRED,
                null);

        defaultArticlesInJournalFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_articlesinjournal_folder",
                Parameter.REQUIRED,
                null);

        register(attachOrgaUnitsStep);
        register(attachPublicationsStepTo);
        register(defaultAuthorsFolder);
        register(defaultSeriesFolder);
        register(defaultPublisherFolder);
        register(defaultCollectedVolumesFolder);
        register(defaultJournalsFolder);
        register(defaultArticlesInCollectedVolumeFolder);
        register(defaultOrganizationsFolder);
        register(defaultProceedingsFolder);
        register(defaultInProceedingsFolder);
        register(defaultArticlesInJournalFolder);

        loadInfo();
    }

    public Boolean getAttachOrgaUnitsStep() {
        return (Boolean) get(attachOrgaUnitsStep);
    }

    public String getAttachPublicationsStepTo() {
        return (String) get(attachPublicationsStepTo);
    }

    public Integer getDefaultAuthorsFolder() {
        if (get(defaultAuthorsFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultAuthorsFolder);
        }
    }

    public Integer getDefaultSeriesFolder() {
        if (get(defaultSeriesFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultSeriesFolder);
        }
    }
    
    public Integer getDefaultPublisherFolder() {
        if (get(defaultPublisherFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultPublisherFolder);
        }
    }
    
    public Integer getDefaultCollectedVolumesFolder() {
        if (get(defaultCollectedVolumesFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultCollectedVolumesFolder);
        }
    }
    
    public Integer getDefaultJournalsFolder() {
        if (get(defaultJournalsFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultJournalsFolder);
        }
    }

    public Integer getDefaultArticlesInCollectedVolumeFolder() {
        if (get(defaultArticlesInCollectedVolumeFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultArticlesInCollectedVolumeFolder);
        }
    }
    
    public Integer getDefaultOrganizationsFolder() {
        if (get(defaultOrganizationsFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultOrganizationsFolder);
        }
    }
    
    public Integer getDefaultProceedingsFolder() {
        if (get(defaultProceedingsFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultProceedingsFolder);
        }
    }
    
    public Integer getDefaultInProceedingsFolder() {
        if (get(defaultInProceedingsFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultInProceedingsFolder);
        }
    }
    
     public Integer getDefaultArticlesInJournalFolder() {
        if (get(defaultArticlesInJournalFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultArticlesInJournalFolder);
        }
    }
}
