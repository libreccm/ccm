/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.portation.conversion.contentsection;

import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.cms.portation.modules.contentsection.ContentSection;
import com.arsdigita.cms.portation.modules.contentsection.ContentType;
import com.arsdigita.cms.portation.modules.contentsection.Folder;
import com.arsdigita.cms.portation.modules.contentsection.FolderType;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.cmd.ExportLogger;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.workflow.Workflow;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.WorkflowTemplate;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/16/18
 */
public class ContentSectionConversion extends AbstractConversion {
    private static ContentSectionConversion instance;

    static {
        instance = new ContentSectionConversion();
    }

    /**
     * Retrieves all trunk-{@link com.arsdigita.cms.ContentSection}s from the
     * persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link ContentSection}s focusing on keeping
     * all the associations in tact.
     */
    @Override
    public void convertAll() {
        ExportLogger.fetching("content sections");
        List<com.arsdigita.cms.ContentSection> trunkContentSections = com
                .arsdigita.cms.ContentSection.getAllObjects();

        ExportLogger.converting("content sections");
        createContentSectionsAndSetAssociations(trunkContentSections);

        ExportLogger.newLine();
    }

    /**
     * Creates the equivalent ng-class of the {@code ContentSection} and
     * restores the associations to other classes.
     *
     * @param trunkContentSections List of all
     *                             {@link com.arsdigita.cms.ContentSection}s
     *                             from this old trunk-system.
     */
    private void createContentSectionsAndSetAssociations(final List<com
            .arsdigita.cms.ContentSection> trunkContentSections) {
        int processed = 0, pDocFolders = 0, pAssetFolders = 0;
        for (com.arsdigita.cms.ContentSection trunkContentSection :
                trunkContentSections) {

            // create content section
            ContentSection contentSection =
                    new ContentSection(trunkContentSection);

            // set root documents folders and opposed association
            Folder rootDocumentsFolder = NgCmsCollection
                    .folders
                    .get(trunkContentSection
                            .getRootFolder()
                            .getID()
                            .longValue());
            if (rootDocumentsFolder == null) {
                rootDocumentsFolder = new Folder(FolderType.DOCUMENTS_FOLDER,
                        contentSection.getDisplayName());
                pDocFolders++;
            }
            contentSection.setRootDocumentsFolder(rootDocumentsFolder);
            rootDocumentsFolder.setSection(contentSection);

            // set root assets folder and opposed association
            Folder rootAssetsFolder = new Folder(FolderType.ASSETS_FOLDER,
                    contentSection.getDisplayName());
            pAssetFolders++;
            contentSection.setRootAssetsFolder(rootAssetsFolder);
            rootAssetsFolder.setSection(contentSection);

            // set roles
            RoleCollection roleCollection = trunkContentSection
                    .getStaffGroup().getRoles();
            while (roleCollection.next()) {
                contentSection.addRole(NgCoreCollection
                        .roles
                        .get(roleCollection
                                .getRole()
                                .getID()
                                .longValue()));
            }

            // set content types and opposed association
            ContentTypeCollection contentTypeCollection = trunkContentSection
                    .getContentTypes();
            while (contentTypeCollection.next()) {
                final com.arsdigita.cms.ContentType trunkContentType =
                        contentTypeCollection.getContentType();

                ContentType contentType = NgCmsCollection
                        .contentTypes
                        .get(trunkContentType
                                .getID()
                                .longValue());
                contentSection.addContentType(contentType);
                contentType.setContentSection(contentSection);

                // set content type's missing associations
                setContentTypesMissingAssociations(trunkContentSection,
                                                   trunkContentType);
            }


            // set lifecycle definitions
            LifecycleDefinitionCollection lifecycleDefinitionCollection =
                    trunkContentSection.getLifecycleDefinitions();
            while (lifecycleDefinitionCollection.next()) {
                contentSection.addLifecycleDefinition(NgCmsCollection
                        .lifecycleDefinitions
                        .get(lifecycleDefinitionCollection
                                .getLifecycleDefinition()
                                .getID()
                                .longValue()));
            }

            // set workflow templates
            TaskCollection workflowTemplateCollection = trunkContentSection
                    .getWorkflowTemplates();
            while (workflowTemplateCollection.next()) {
                final Workflow template = NgCoreCollection
                        .workflows
                        .get(workflowTemplateCollection
                                .getTask()
                                .getID()
                                .longValue());
                if (template != null && template.isAbstractWorkflow())
                    contentSection.addWorkflowTemplate(template);
            }

            processed++;
        }
        ExportLogger.created("content sections", processed);
        ExportLogger.created("folders (root document)", pDocFolders);
        ExportLogger.created("folders (root asset)", pAssetFolders);
    }

    /**
     * Sets the missing associations {@code defaultLifecycle} and
     * {@code defaultWorkflow} for the given content type in that given
     * content section. This wasn't possible in {@link ContentTypeConversion}
     * because the content sections had still been unknown.
     *
     * @param trunkContentSection The content section
     * @param trunkContentType The content type
     */
    private void setContentTypesMissingAssociations(
            final com.arsdigita.cms.ContentSection trunkContentSection,
            final com.arsdigita.cms.ContentType trunkContentType) {
        ContentType contentType = NgCmsCollection
                .contentTypes
                .get(trunkContentType
                        .getID()
                        .longValue());

        // set content type's default lifecycle
        final LifecycleDefinition trunkLifecycleDefinition =
                ContentTypeLifecycleDefinition.getLifecycleDefinition(
                        trunkContentSection, trunkContentType);
        if (trunkLifecycleDefinition != null)
            contentType.setDefaultLifecycle(NgCmsCollection
                    .lifecycleDefinitions
                    .get(trunkLifecycleDefinition
                            .getID()
                            .longValue()));

        // set content type's default workflow (template)
        final WorkflowTemplate trunkWorkflowTemplate =
                ContentTypeWorkflowTemplate.getWorkflowTemplate(
                        trunkContentSection, trunkContentType);
        if (trunkWorkflowTemplate != null) {
            final Workflow defaultWorkflow = NgCoreCollection
                    .workflows
                    .get(trunkWorkflowTemplate
                            .getID()
                            .longValue());
            if (defaultWorkflow != null && defaultWorkflow.isAbstractWorkflow())
                contentType.setDefaultWorkflow(defaultWorkflow);
        }
    }

    public static ContentSectionConversion getInstance() {
        return instance;
    }
}
