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

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationsConfig extends AbstractConfig {

    private static final Logger LOGGER = Logger.getLogger(PublicationsConfig.class);
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
    private final Parameter defaultAuthorsFolderID;
    private final Parameter defaultAuthorsFolderPath;
    private final Parameter defaultSeriesFolderID;
    private final Parameter defaultSeriesFolderPath;
    private final Parameter defaultPublisherFolderID;
    private final Parameter defaultPublisherFolderPath;
    private final Parameter defaultCollectedVolumesFolderID;
    private final Parameter defaultCollectedVolumesFolderPath;
    private final Parameter defaultJournalsFolderID;
    private final Parameter defaultJournalsFolderPath;
    private final Parameter defaultArticlesInCollectedVolumeFolderID;
    private final Parameter defaultArticlesInCollectedVolumeFolderPath;
    private final Parameter defaultOrganizationsFolderID;
    private final Parameter defaultOrganizationsFolderPath;
    private final Parameter defaultProceedingsFolderID;
    private final Parameter defaultProceedingsFolderPath;
    private final Parameter defaultInProceedingsFolderID;
    private final Parameter defaultInProceedingsFolderPath;
    private final Parameter defaultArticlesInJournalFolderID;
    private final Parameter defaultArticlesInJournalFolderPath;
    private final Parameter defaultPublicationsFolderID;
    private final Parameter defaultPublicationsFolderPath;
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

        defaultAuthorsFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_authors_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultAuthorsFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_authors_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultSeriesFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_series_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultSeriesFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_series_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultPublisherFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_publisher_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultPublisherFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_publisher_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultCollectedVolumesFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_collectedvolumes_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultCollectedVolumesFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_collectedvolumes_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultJournalsFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_journals_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultJournalsFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_journals_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultArticlesInCollectedVolumeFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_articlesincollectedvolume_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultArticlesInCollectedVolumeFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_articlesincollectedvolume_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultOrganizationsFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_organizations_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultOrganizationsFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_organizations_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultProceedingsFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_proceedings_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultProceedingsFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_proceedings_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultInProceedingsFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_inproccedings_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultInProceedingsFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_inproceedings_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultArticlesInJournalFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_articlesinjournal_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultArticlesInJournalFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_articlesinjournal_folder_path",
                Parameter.OPTIONAL,
                null);

        defaultPublicationsFolderID = new IntegerParameter(
                "com.arsdigita.cms.contenttypes.publications.default_publications_folder_id",
                Parameter.OPTIONAL,
                null);

        defaultPublicationsFolderPath = new StringParameter(
                "com.arsdigita.cms.contenttypes.publications.default_publications_folder_path",
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
        register(defaultAuthorsFolderID);
        register(defaultAuthorsFolderPath);
        register(defaultSeriesFolderID);
        register(defaultSeriesFolderPath);
        register(defaultPublisherFolderID);
        register(defaultPublisherFolderPath);
        register(defaultCollectedVolumesFolderID);
        register(defaultCollectedVolumesFolderPath);
        register(defaultJournalsFolderID);
        register(defaultJournalsFolderPath);
        register(defaultArticlesInCollectedVolumeFolderID);
        register(defaultArticlesInCollectedVolumeFolderPath);
        register(defaultOrganizationsFolderID);
        register(defaultOrganizationsFolderPath);
        register(defaultProceedingsFolderID);
        register(defaultProceedingsFolderPath);
        register(defaultInProceedingsFolderID);
        register(defaultInProceedingsFolderPath);
        register(defaultArticlesInJournalFolderID);
        register(defaultArticlesInJournalFolderPath);
        register(defaultPublicationsFolderID);
        register(defaultPublicationsFolderPath);
        register(orgaType);
        register(orgaBundleType);
        register(enableFirstPublishedProperty);
        register(enableLanguageProperty);

        loadInfo();
    }

    /**
     * Helper method for retrieving the default folders. The method takes two parameters (for now):
     * the path of the folder to retrieve and the id of the folder to retrieve. If the path
     * parameter is not {@code null}, the path is used. If the path is invalid (the path does not
     * point to a existing folder), the root folder of the default content section is returned.
     *
     * If the folder path is {@code null}, the second parameter, the Id is used. Please note that
     * the ID settings for default folders have been marked as deprecated. For new default folder
     * settings there should be no ID settings, only a path setting and corresponding method which
     * retrieves the folder using this method. The {@code folderId} parameter will be removed
     * together with the folder id settings. If there is no folder with the given id, the root
     * folder of the default content section is returned.
     *
     * If both parameters are {@code null} the method returns {@code null} which indicates that no
     * default folder has been set.
     *
     * @param folderPath Path of the default folder.
     * @param folderId ID of the default folder.
     * @return
     */
    private Folder getDefaultFolder(final String folderPath, final Integer folderId) {
        if (folderPath != null) {
            final Folder folder = Folder.retrieveFolder(folderPath);
            if (folder == null) {
                LOGGER.warn(String.format("There is no folder with the path '%s'.",
                                          folderPath));
                return ContentSection.getDefaultSection().getRootFolder();
            } else {
                return folder;
            }
        }

        if (folderId != null) {
            try {
                return new Folder(new BigDecimal(folderId));
            } catch (DataObjectNotFoundException ex) {
                LOGGER.warn(String.format("Failed to retrieve folder with id %s.",
                                          folderId.toString()),
                            ex);
                return ContentSection.getDefaultSection().getRootFolder();
            }
        }

        return null;
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

    /**
     *
     * @return @deprecated Use {@link #getDefaultAuthorsFolderPath()} or
     * {@link #getDefaultAuthorsFolder()} instead.
     */
    @Deprecated
    public Integer getDefaultAuthorsFolderID() {
        if (get(defaultAuthorsFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultAuthorsFolderID);
        }
    }

    public String getDefaultAuthorsFolderPath() {
        if (get(defaultAuthorsFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultAuthorsFolderPath);
        }
    }

    /**
     * Retrieves the default folder for storing authors created using the
     * {@link ItemSearchCreateItemPane}.
     *
     * If both {@link #getDefaultAuthorsFolderPath()} and {@link #getDefaultAuthorsFolderID()} are
     * not set, the method will return {@code null}. If {@link #getDefaultAuthorsFolderPath()} is
     * set the path returned by that method is used to retrieve the folder. If there is no folder
     * with this path, the method will return the root folder of the default content section.
     *
     * If the default authors folder path property is not set, the
     * {@link #getDefaultAuthorsFolderID()} is used as a fallback. If there is no folder with the
     * provided id, the root folder of the default content section is returned. Please note that
     * {@link #getDefaultAuthorsFolderID()} is marked as deprecated and will be removed in one of
     * the next releases. Instead the path property should be used.
     *
     * @return {@code null} if {@link #getDefaultAuthorsFolderPath()} and
     * {@link #getDefaultAuthorsFolderID()} both are not set, otherwise a {@link Folder} object.
     */
    public Folder getDefaultAuthorsFolder() {
        return getDefaultFolder(getDefaultAuthorsFolderPath(), getDefaultAuthorsFolderID());

//        if (getDefaultAuthorsFolderPath() != null) {
//            final Folder folder = Folder.retrieveFolder(getDefaultAuthorsFolderPath());
//            if (folder == null) {
//                LOGGER.warn(String.format("There is no folder with the path '%s'.",
//                                          getDefaultAuthorsFolderPath()));
//                return ContentSection.getDefaultSection().getRootFolder();
//            } else {
//                return folder;
//            }
//        }
//
//        if (getDefaultAuthorsFolderID() != null) {
//            try {
//                return new Folder(new BigDecimal(getDefaultAuthorsFolderID()));
//            } catch (DataObjectNotFoundException ex) {
//                LOGGER.warn(String.format("Failed to retrieve folder with id %s.",
//                                          getDefaultAuthorsFolderID().toString()),
//                            ex);
//                return ContentSection.getDefaultSection().getRootFolder();
//            }
//        }
//
//        return null;
    }

    /**
     *
     * @return @deprecated Use {@link #getDefaultSeriesFolder()} instead
     */
    @Deprecated
    public Integer getDefaultSeriesFolderID() {
        if (get(defaultSeriesFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultSeriesFolderID);
        }
    }

    public String getDefaultSeriesFolderPath() {
        if (get(defaultSeriesFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultSeriesFolderPath);
        }
    }

    /**
     * Retrieves the default folder for storing series items created using the ItemSearchCreatePane.
     * The method works like the method for authors (see {@link #getDefaultAuthorsFolder()} and
     * {@link #getDefaultFolder(java.lang.String, java.lang.Integer)}).
     *
     * @return The default folder for series items, if set, or {@code null}.
     */
    public Folder getDefaultSeriesFolder() {
        return getDefaultFolder(getDefaultSeriesFolderPath(), getDefaultSeriesFolderID());
    }

    /**
     *
     * @return @deprecated Use {@link #getDefaultPublisherFolder()} instead.
     */
    @Deprecated
    public Integer getDefaultPublisherFolderID() {
        if (get(defaultPublisherFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultPublisherFolderID);
        }
    }

    public String getDefaultPublisherFolderPath() {
        if (getDefaultPublisherFolderPath() == null) {
            return null;
        } else {
            return (String) get(defaultPublisherFolderPath);
        }
    }

    public Folder getDefaultPublisherFolder() {
        return getDefaultFolder(getDefaultPublisherFolderPath(), getDefaultPublisherFolderID());
    }

    /**
     *
     * @return @deprecated Use {@link #getDefaultCollectedVolumesFolder()}
     */
    @Deprecated
    public Integer getDefaultCollectedVolumesFolderID() {
        if (get(defaultCollectedVolumesFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultCollectedVolumesFolderID);
        }
    }

    public String getDefaultCollectedVolumesFolderPath() {
        if (get(defaultCollectedVolumesFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultCollectedVolumesFolderPath);
        }
    }

    public Folder getDefaultCollectedVolumesFolder() {
        return getDefaultFolder(getDefaultCollectedVolumesFolderPath(),
                                getDefaultCollectedVolumesFolderID());
    }

    /**
     *
     * @return @deprecated Use {@link #getDefaultJournalsFolder() }
     */
    @Deprecated
    public Integer getDefaultJournalsFolderID() {
        if (get(defaultJournalsFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultJournalsFolderID);
        }
    }

    public String getDefaultJournalsFolderPath() {
        if (get(defaultJournalsFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultJournalsFolderPath);
        }
    }

    public Folder getDefaultJournalsFolder() {
        return getDefaultFolder(getDefaultJournalsFolderPath(), getDefaultJournalsFolderID());
    }

    /**
     *
     * @return @deprecated Use {@link #getDefaultArticlesInCollectedVolumeFolder() }
     */
    @Deprecated
    public Integer getDefaultArticlesInCollectedVolumeFolderID() {
        if (get(defaultArticlesInCollectedVolumeFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultArticlesInCollectedVolumeFolderID);
        }
    }

    public String getDefaultArticlesInCollectedVolumeFolderPath() {
        if (get(defaultArticlesInCollectedVolumeFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultArticlesInCollectedVolumeFolderPath);
        }
    }

    public Folder getDefaultArticlesInCollectedVolumeFolder() {
        return getDefaultFolder(getDefaultArticlesInCollectedVolumeFolderPath(),
                                getDefaultArticlesInCollectedVolumeFolderID());
    }

    /**
     *
     * @return @deprecated Use {@link #getDefaultOrganizationsFolder() }
     */
    @Deprecated
    public Integer getDefaultOrganizationsFolderID() {
        if (get(defaultOrganizationsFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultOrganizationsFolderID);
        }
    }

    public String getDefaultOrganizationsFolderPath() {
        if (get(defaultOrganizationsFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultOrganizationsFolderPath);
        }
    }

    public Folder getDefaultOrganizationsFolder() {
        return getDefaultFolder(getDefaultOrganizationsFolderPath(),
                                getDefaultOrganizationsFolderID());
    }

    /**
     *
     * @return @deprecated Use {@link #getDefaultProceedingsFolder() }
     */
    @Deprecated
    public Integer getDefaultProceedingsFolderID() {
        if (get(defaultProceedingsFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultProceedingsFolderID);
        }
    }

    public String getDefaultProceedingsFolderPath() {
        if (get(defaultProceedingsFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultProceedingsFolderPath);
        }
    }

    public Folder getDefaultProceedingsFolder() {
        return getDefaultFolder(getDefaultProceedingsFolderPath(),
                                getDefaultProceedingsFolderID());
    }

    /**
     * 
     * @return
     * @deprecated Use {@link getDefaultInProccedingsFolder}
     */
    @Deprecated
    public Integer getDefaultInProceedingsFolderID() {
        if (get(defaultInProceedingsFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultInProceedingsFolderID);
        }
    }
    
    public String getDefaultInProceedingsFolderPath() {
        if (get(defaultInProceedingsFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultInProceedingsFolderPath);
        }
    }
    
    public Folder getDefaultInProceedingsFolder() {
        return getDefaultFolder(getDefaultInProceedingsFolderPath(),
                                getDefaultInProceedingsFolderID());
    }

    /**
     *
     * @return @deprecated Use {@link #getDefaultArticlesInJournalFolder() }
     */
    @Deprecated
    public Integer getDefaultArticlesInJournalFolderID() {
        if (get(defaultArticlesInJournalFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultArticlesInJournalFolderID);
        }
    }

    public String getDefaultArticlesInJournalFolderPath() {
        if (get(defaultArticlesInJournalFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultArticlesInJournalFolderPath);
        }
    }

    public Folder getDefaultArticlesInJournalFolder() {
        return getDefaultFolder(getDefaultArticlesInJournalFolderPath(),
                                getDefaultArticlesInJournalFolderID());
    }

    /**
     * 
     * @return
     * @deprecated Use {@link #getDefaultPublicationsFolder() }
     */
    @Deprecated
    public Integer getDefaultPublicationsFolderID() {
        if (get(defaultPublicationsFolderID) == null) {
            return null;
        } else {
            return (Integer) get(defaultPublicationsFolderID);
        }
    }
    
    public String getDefaultPublicationsFolderPath() {
        if (get(defaultPublicationsFolderPath) == null) {
            return null;
        } else {
            return (String) get(defaultPublicationsFolderPath);
        }
    }
    
    public Folder getDefaultPublicationsFolder() {
        return getDefaultFolder(getDefaultPublicationsFolderPath(),
                                getDefaultPublicationsFolderID());
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
