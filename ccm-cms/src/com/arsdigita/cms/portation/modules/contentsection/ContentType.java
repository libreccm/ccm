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
import com.arsdigita.cms.portation.modules.contentsection.util.ContentTypeModeMapper;
import com.arsdigita.cms.portation.modules.lifecycle.LifecycleDefinition;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.arsdigita.portation.modules.core.workflow.Workflow;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 2/21/18
 */
public class ContentType extends CcmObject implements Portable {
    private String contentItemClass;
    @JsonIdentityReference(alwaysAsId = true)
    private ContentSection contentSection;
    private LocalizedString label;
    private LocalizedString description;
    private String ancestors;
    private String descendants;
    private ContentTypeMode mode;
    @JsonIdentityReference(alwaysAsId = true)
    private LifecycleDefinition defaultLifecycle;
    @JsonIdentityReference(alwaysAsId = true)
    private Workflow defaultWorkflow;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkContentType the trunk object
     */
    public ContentType(final com.arsdigita.cms.ContentType trunkContentType) {
        super(trunkContentType);

        this.contentItemClass = trunkContentType.getClassName();
        //this.contentSection

        this.label = new LocalizedString();
        this.description = new LocalizedString();
        final Locale locale = Locale.getDefault();
        label.addValue(locale,
                (String) trunkContentType.getLabel().localize(locale));
        description.addValue(locale, trunkContentType.getDescription());

        this.ancestors = trunkContentType.getAncestors();
        this.descendants = trunkContentType.getDescendants();

        this.mode = ContentTypeModeMapper
                .mapContentTypeMode(trunkContentType.getMode());

        //this.defaultLifecycle
        //this.defaultWorkflow

        NgCmsCollection.contentTypes.put(this.getObjectId(), this);
    }

    public String getContentItemClass() {
        return contentItemClass;
    }

    public void setContentItemClass(final String contentItemClass) {
        this.contentItemClass = contentItemClass;
    }

    public ContentSection getContentSection() {
        return contentSection;
    }

    public void setContentSection(final ContentSection contentSection) {
        this.contentSection = contentSection;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {
        this.label = label;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(final String ancestors) {
        this.ancestors = ancestors;
    }

    public String getDescendants() {
        return descendants;
    }

    public void setDescendants(final String descendants) {
        this.descendants = descendants;
    }

    public ContentTypeMode getMode() {
        return mode;
    }

    public void setMode(final ContentTypeMode mode) {
        this.mode = mode;
    }

    public LifecycleDefinition getDefaultLifecycle() {
        return defaultLifecycle;
    }

    public void setDefaultLifecycle(final LifecycleDefinition defaultLifecycle) {
        this.defaultLifecycle = defaultLifecycle;
    }

    public Workflow getDefaultWorkflow() {
        return defaultWorkflow;
    }

    public void setDefaultWorkflow(final Workflow defaultWorkflow) {
        this.defaultWorkflow = defaultWorkflow;
    }
}
