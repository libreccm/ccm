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
package com.arsdigita.cms.portation.modules.contentsection;

import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.cms.portation.modules.lifecycle.LifecycleDefinition;
import com.arsdigita.portation.modules.core.web.CcmApplication;
import com.arsdigita.portation.modules.core.security.Role;
import com.arsdigita.portation.modules.core.workflow.Workflow;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 2/21/18
 */
public class ContentSection extends CcmApplication {
    private String label;
    @JsonIdentityReference(alwaysAsId = true)
    private Folder rootDocumentsFolder;
    @JsonIdentityReference(alwaysAsId = true)
    private Folder rootAssetsFolder;
    private String pageResolverClass;
    private String itemResolverClass;
    private String templateResolverClass;
    private String xmlGeneratorClass;
    @JsonIdentityReference(alwaysAsId = true)
    private List<Role> roles;
    private Locale defaultLocale;
    @JsonIgnore
    private List<ContentType> contentTypes;
    @JsonIdentityReference(alwaysAsId = true)
    private List<LifecycleDefinition> lifecycleDefinitions;
    @JsonIdentityReference(alwaysAsId = true)
    private List<Workflow> workflowTemplates;

    public ContentSection(final com.arsdigita.cms.ContentSection
                                  trunkContentSection) {
        super(trunkContentSection);

        this.label = trunkContentSection.getName();

        //this.rootDocumentsFolder
        //this.rootAssetsFolder

        this.pageResolverClass = trunkContentSection.getPageResolverClassName();
        this.itemResolverClass = trunkContentSection.getItemResolverClassName();
        this.templateResolverClass = trunkContentSection
                .getTemplateResolverClassName();
        this.xmlGeneratorClass = trunkContentSection.getXMLGeneratorClassName();

        this.roles = new ArrayList<>();

        this.defaultLocale = trunkContentSection
                .getDefaultLocale().toJavaLocale();

        this.contentTypes = new ArrayList<>();
        this.lifecycleDefinitions = new ArrayList<>();
        this.workflowTemplates = new ArrayList<>();

        NgCmsCollection.contentSections.put(this.getObjectId(), this);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Folder getRootDocumentsFolder() {
        return rootDocumentsFolder;
    }

    public void setRootDocumentsFolder(final Folder rootDocumentsFolder) {
        this.rootDocumentsFolder = rootDocumentsFolder;
    }

    public Folder getRootAssetsFolder() {
        return rootAssetsFolder;
    }

    public void setRootAssetsFolder(final Folder rootAssetsFolder) {
        this.rootAssetsFolder = rootAssetsFolder;
    }

    public String getPageResolverClass() {
        return pageResolverClass;
    }

    public void setPageResolverClass(final String pageResolverClass) {
        this.pageResolverClass = pageResolverClass;
    }

    public String getItemResolverClass() {
        return itemResolverClass;
    }

    public void setItemResolverClass(final String itemResolverClass) {
        this.itemResolverClass = itemResolverClass;
    }

    public String getTemplateResolverClass() {
        return templateResolverClass;
    }

    public void setTemplateResolverClass(final String templateResolverClass) {
        this.templateResolverClass = templateResolverClass;
    }

    public String getXmlGeneratorClass() {
        return xmlGeneratorClass;
    }

    public void setXmlGeneratorClass(final String xmlGeneratorClass) {
        this.xmlGeneratorClass = xmlGeneratorClass;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(final List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(final Role role) {
        this.roles.add(role);
    }

    public void removeRole(final Role role) {
        this.roles.remove(role);
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(final Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public List<ContentType> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(final List<ContentType> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public void addContentType(final ContentType contentType) {
        this.contentTypes.add(contentType);
    }

    public void removeContentType(final ContentType contentType) {
        this.contentTypes.remove(contentType);
    }

    public List<LifecycleDefinition> getLifecycleDefinitions() {
        return lifecycleDefinitions;
    }

    public void setLifecycleDefinitions(final List<LifecycleDefinition>
                                                lifecycleDefinitions) {
        this.lifecycleDefinitions = lifecycleDefinitions;
    }

    public void addLifecycleDefinition(final LifecycleDefinition
                                                lifecycleDefinition) {
        this.lifecycleDefinitions.add(lifecycleDefinition);
    }

    public void removeLifecycleDefinition(final LifecycleDefinition
                                                lifecycleDefinition) {
        this.lifecycleDefinitions.remove(lifecycleDefinition);
    }

    public List<Workflow> getWorkflowTemplates() {
        return workflowTemplates;
    }

    public void setWorkflowTemplates(final List<Workflow> workflowTemplates) {
        this.workflowTemplates = workflowTemplates;
    }

    public void addWorkflowTemplate(final Workflow workflowTemplate) {
        this.workflowTemplates.add(workflowTemplate);
    }

    public void removeWorkflowTemplate(final Workflow workflowTemplate) {
        this.workflowTemplates.remove(workflowTemplate);
    }
}
