/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.workflow.simple.WorkflowTemplate;

/**
 * This component displays basic attributes of a content type
 * including:
 *
 * label, description, default lifecycle definition, default workflow
 * template
 *
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ContentTypePropertyList.java 1942 2009-05-29 07:53:23Z terry $
 */
class ContentTypePropertyList extends PropertyList {

    private final ContentTypeRequestLocal m_type;

    public ContentTypePropertyList(final ContentTypeRequestLocal type) {
        m_type = type;
    }

    protected final java.util.List properties(final PageState state) {
        final java.util.List props = super.properties(state);
        final ContentType type = m_type.getContentType(state);
        final ContentSection section =
            CMS.getContext().getContentSection();

        props.add(new Property(gz("cms.ui.name"),
                               type.getLabel()));
        props.add(new Property(gz("cms.ui.description"),
                               type.getDescription()));
        props.add(new Property(gz("cms.ui.type.parent"),
                               getParent(type)));
        props.add(new Property(gz("cms.ui.type.lifecycle"),
                               getLifecycle(section, type)));
        props.add(new Property(gz("cms.ui.type.workflow"),
                               getWorkflow(section, type)));

        return props;
    }

    // XXX domlay: this stuff seems unnecessarily verbose.  why is so
    // much indirection involved in finding a content type's parent
    // type, default lifecyle, and default workflow?

    private String getParent(final ContentType type) {
        ObjectType ot = MetadataRoot.getMetadataRoot
            ().getObjectType(type.getAssociatedObjectType());
        ObjectType parent = ot.getSupertype();

        if (parent == null) {
            return lz("cms.ui.type.parent.none");
        } else {
            try {
                return ContentType.findByAssociatedObjectType
                    (parent.getQualifiedName()).getLabel();
            } catch (DataObjectNotFoundException donfe) {
                return parent.getName();
            }
        }
    }

    private String getLifecycle(final ContentSection section,
                                final ContentType type) {
        final LifecycleDefinition cycle =
            ContentTypeLifecycleDefinition.getLifecycleDefinition
            (section, type);

        if (cycle == null) {
            return lz("cms.ui.type.lifecycle.none");
        } else {
            return cycle.getLabel();
        }
    }

    private String getWorkflow(final ContentSection section,
                               final ContentType type) {
        final WorkflowTemplate template =
            ContentTypeWorkflowTemplate.getWorkflowTemplate
            (section, type);

        if (template == null) {
            return lz("cms.ui.type.workflow.none");
        } else {
            return template.getLabel();
        }
    }

    private static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
