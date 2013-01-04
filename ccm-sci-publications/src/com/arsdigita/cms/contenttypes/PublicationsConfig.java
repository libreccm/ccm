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
    private final Parameter attachOrganizationPublicationsStepTo;
    private final Parameter attachPersonPublicationsStep;
    private final Parameter attachPublicationsStepTo;
    private final Parameter attachPublisherPublicationsStep;
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
    private final Parameter defaultPublicationsFolder;
    private final Parameter orgaType;
    private final Parameter orgaBundleType;

    public PublicationsConfig() {
        attachOrgaUnitsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_orgaunits_step",
                Parameter.REQUIRED,
                Boolean.FALSE);

        attachOrganizationPublicationsStepTo =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_organization_publications_step_to",
                Parameter.REQUIRED,
                "");

        attachPersonPublicationsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_person_publications_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        attachPublicationsStepTo =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_publications_step_to",
                Parameter.REQUIRED,
                "");

        attachPublisherPublicationsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_publisher_publications_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        defaultAuthorsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_authors_folder",
                Parameter.OPTIONAL,
                null);

        defaultSeriesFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_series_folder",
                Parameter.OPTIONAL,
                null);

        defaultPublisherFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_publisher_folder",
                Parameter.OPTIONAL,
                null);

        defaultCollectedVolumesFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_collectedvolumes_folder",
                Parameter.OPTIONAL,
                null);

        defaultJournalsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_journals_folder",
                Parameter.OPTIONAL,
                null);

        defaultArticlesInCollectedVolumeFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_articlesincollectedvolume_folder",
                Parameter.OPTIONAL,
                null);

        defaultOrganizationsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_organizations_folder",
                Parameter.OPTIONAL,
                null);

        defaultProceedingsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_proceedings_folder",
                Parameter.OPTIONAL,
                null);

        defaultInProceedingsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_inproccedings_folder",
                Parameter.OPTIONAL,
                null);

        defaultArticlesInJournalFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_articlesinjournal_folder",
                Parameter.OPTIONAL,
                null);

        defaultPublicationsFolder = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_publications_folder",
                Parameter.OPTIONAL,
                null);

        orgaType = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.organization_type",
                Parameter.OPTIONAL,
                Publisher.BASE_DATA_OBJECT_TYPE);
        orgaBundleType = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.organization_bundle_type",
                Parameter.OPTIONAL,
                PublisherBundle.BASE_DATA_OBJECT_TYPE);

        register(attachOrgaUnitsStep);
        register(attachOrganizationPublicationsStepTo);
        register(attachPersonPublicationsStep);
        register(attachPublicationsStepTo);
        register(attachPublisherPublicationsStep);
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
        register(defaultPublicationsFolder);
        register(orgaType);
        register(orgaBundleType);

        loadInfo();
    }

    public Boolean getAttachOrgaUnitsStep() {
        return (Boolean) get(attachOrgaUnitsStep);
    }

    public String getAttachOrganizationPublicationsStepTo() {
        return (String) get(attachOrganizationPublicationsStepTo);
    }

    public Boolean getAttachPersonPublicationsStep() {
        return (Boolean) get(attachPersonPublicationsStep);
    }

    public String getAttachPublicationsStepTo() {
        return (String) get(attachPublicationsStepTo);
    }

    public Boolean getPublisherPublicationsStep() {
        return (Boolean) get(attachPublisherPublicationsStep);
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

    public Integer getDefaultPublicationsFolder() {
        if (get(defaultPublicationsFolder) == null) {
            return null;
        } else {
            return (Integer) get(defaultPublicationsFolder);
        }
    }

    public String getOrganizationType() {
        return (String) get(orgaType);
    }

    public String getOrganizationBundleType() {
        return (String) get(orgaBundleType);
    }

}
