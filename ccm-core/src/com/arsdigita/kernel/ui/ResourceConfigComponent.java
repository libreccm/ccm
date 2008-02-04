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
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import org.apache.log4j.Logger;

/**
 * <p>A base class used to implement the UI callbacks on {@link
 * com.arsdigita.kernel.ResourceTypeConfig}.</p>
 *
 * A component for advanced configuration of a resource.
 * The subclass should take whatever steps are required to
 * accumulate configuration info, and then fire a completion
 * event. Upon receiving the completion event, the container
 * of this component will then invoke the createResource
 * or modifyResource methods to persist the changes.
 *
 * @see com.arsdigita.kernel.ResourceTypeConfig
 * @see com.arsdigita.web.ApplicationType
 * @see com.arsdigita.web.Application
 * @author Daniel Berrange &lt;<a href="mailto:berrange@redhat.com">berrange@redhat.com</a>&gt;
 */
public class ResourceConfigComponent extends SimpleContainer {
    public static final String versionId =
        "$Id: ResourceConfigComponent.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (ResourceConfigComponent.class);

    public ResourceConfigComponent() {
        super();
    }

    public ResourceConfigComponent(String name,
                                   String xmlns) {
        super(name,
              xmlns);
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
