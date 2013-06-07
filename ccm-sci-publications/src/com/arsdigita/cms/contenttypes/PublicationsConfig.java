/*
 * Copyright (c) 2010 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
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
    private final Parameter orgaUnitsStepSortKey;
    private final Parameter attachOrganizationPublicationsStepTo;
    private final Parameter organizationPublicationsStepSortKey;
    private final Parameter attachPersonPublicationsStep;
    private final Parameter personPublicationsStepSortKey;
    private final Parameter attachPublicationsStepTo;
    private final Parameter publicationsStepSortKey;
    private final Parameter attachPublisherPublicationsStep;
    private final Parameter publisherPublicationsStepSortKey;
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
    private final Parameter enableFirstPublishedProperty;
    private final Parameter enableLanguageProperty;

    public PublicationsConfig() {
        attachOrgaUnitsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_orgaunits_step",
                Parameter.REQUIRED,
                Boolean.FALSE);

        orgaUnitsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.orgaunits_step_sort_key",
                Parameter.REQUIRED,
                10);

        attachOrganizationPublicationsStepTo =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_organization_publications_step_to",
                Parameter.REQUIRED,
                "");

        organizationPublicationsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.organization_publications_step_sort_key",
                Parameter.REQUIRED,
                10);

        attachPersonPublicationsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_person_publications_step",
                Parameter.REQUIRED,
                Boolean.TRUE);

        personPublicationsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.person_publications_step_sort_key",
                Parameter.REQUIRED,
                10);

        attachPublicationsStepTo =
        new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_publications_step_to",
                Parameter.REQUIRED,
                "");

        publicationsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.publications_step_sort_key",
                Parameter.REQUIRED,
                11);

        attachPublisherPublicationsStep =
        new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.attach_publisher_publications_step",
                Parameter.REQUIRED,
                Boolean.TRUE);
        
        publisherPublicationsStepSortKey =
        new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.publisher_publications_step_sort_key",
                Parameter.REQUIRED,
                10);

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

        enableFirstPublishedProperty = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.enable_first_published_property",
                Parameter.REQUIRED,
                true);

        enableLanguageProperty = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.enable_language_property",
                Parameter.REQUIRED,
                true);

        register(attachOrgaUnitsStep);
        register(orgaUnitsStepSortKey);
        register(attachOrganizationPublicationsStepTo);
        register(organizationPublicationsStepSortKey);
        register(attachPersonPublicationsStep);
        register(personPublicationsStepSortKey);
        register(attachPublicationsStepTo);
        register(publicationsStepSortKey);
        register(attachPublisherPublicationsStep);
        register(publisherPublicationsStepSortKey);        
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
        register(enableFirstPublishedProperty);
        register(enableLanguageProperty);

        loadInfo();
    }

    public Boolean getAttachOrgaUnitsStep() {
        return (Boolean) get(attachOrgaUnitsStep);
    }
    
    public Integer getOrgaUnitsStepSortKey() {
        return (Integer) get(orgaUnitsStepSortKey);
    }

    public String getAttachOrganizationPublicationsStepTo() {
        return (String) get(attachOrganizationPublicationsStepTo);
    }
    
    public Integer getOrganizationPublicationsStepSortKey() {
        return (Integer) get(organizationPublicationsStepSortKey);
    }

    public Boolean getAttachPersonPublicationsStep() {
        return (Boolean) get(attachPersonPublicationsStep);
    }
    
    public Integer getPersonPublicationsStepSortKey() {
        return (Integer) get(personPublicationsStepSortKey);
    }

    public String getAttachPublicationsStepTo() {
        return (String) get(attachPublicationsStepTo);
    }

    public Integer getPublicationsStepSortKey() {
        return (Integer) get(publicationsStepSortKey);
    }
    
    public Boolean getPublisherPublicationsStep() {
        return (Boolean) get(attachPublisherPublicationsStep);
    }
    
    public Integer getPublisherPublicationsStepSortKey() {
        return (Integer) get(publisherPublicationsStepSortKey);
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

    public Boolean getEnableFirstPublishedProperty() {
        return (Boolean) get(enableFirstPublishedProperty);
    }

    public Boolean getEnableLanguageProperty() {
        return (Boolean) get(enableLanguageProperty);
    }

}
