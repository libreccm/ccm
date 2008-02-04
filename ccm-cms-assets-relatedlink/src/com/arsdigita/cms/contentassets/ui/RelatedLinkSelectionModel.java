/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.cms.contenttypes.ui.LinkSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contentassets.RelatedLink;

/**
 * SelectionModel to track the current RelatedLink object for view/edit purposes.
 */
public class RelatedLinkSelectionModel extends LinkSelectionModel {
    
    /**
     * Construct a new <code>RelatedLinkSelectionModel</code>
     *
     * @param itemClass The name of the Java class which represents
     *    the content item. Must be a subclass of Link. In
     *    addition, the class must have a constructor with a single
     *    OID parameter.
     * @param objectType The name of the persistence metadata object type
     *    which represents the content item. In practice, will often be
     *    the same as the itemClass.
     * @param parameter The state parameter which should be used by this item
     */
    public RelatedLinkSelectionModel(BigDecimalParameter param) {
        super(RelatedLink.class.getName(),
              RelatedLink.BASE_DATA_OBJECT_TYPE,
              param);
    }

    /**
     * Returns the currently-selected RelatedLink
     *
     * @param state the PageState for the current request.
     * @return The current RelatedLink
     */
    public RelatedLink getSelectedRelatedLink(PageState state) {
        return (RelatedLink)getSelectedLink(state);
    }
}
