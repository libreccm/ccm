/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel.ui;

import com.arsdigita.kernel.Resource;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.PageState;
import org.apache.log4j.Logger;

/**
 * <p>A base class used to implement the UI callbacks on {@link
 * com.arsdigita.kernel.ResourceTypeConfig}.</p>
 *
 * @see com.arsdigita.kernel.ResourceTypeConfig
 * @see com.arsdigita.kernel.ResourceType
 * @see com.arsdigita.kernel.Resource
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @author Eric Lorenzo
 */
public abstract class ResourceConfigFormSection extends FormSection {
    public static final String versionId =
        "$Id: ResourceConfigFormSection.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (ResourceConfigFormSection.class);

    public ResourceConfigFormSection() {
        super();
    }

    public ResourceConfigFormSection(Container panel) {
        super(panel);
    }

    protected ResourceConfigFormSection(Container panel, FormModel model) {
        super(panel, model);
    }

    /**
     * This method is only called on ResourceConfigFormSections
     * that have been retrieved through the getCreateFormSection
     * method on ResourceTypeConfig. The application should be
     * initialized, but not saved.
     */
    public Resource createResource(PageState ps) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is only called on ResourceConfigFormSections
     * that have been retrieved through the getModifyFormSection
     * method on ResourceTypeConfig. The application modified is
     * the one specified in the RequestLocal argument to the
     * getModifyFormSection method.  The application can be modified
     * and saved, but it does not have to be saved.
     */
    public void modifyResource(PageState ps) {
        throw new UnsupportedOperationException();
    }
}
