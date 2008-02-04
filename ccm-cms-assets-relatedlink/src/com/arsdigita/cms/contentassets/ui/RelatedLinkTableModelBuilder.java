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

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.ui.LinkTableModelBuilder;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * Reusable TableModel for displaying RelatedLinks associated to a
 * ContentItem
 *
 * @version $Revision: #2 $ $Date: 2004/03/30 $
 * @author Scott Seago (sseago@redhat.com)
 */

public class RelatedLinkTableModelBuilder 
    extends LinkTableModelBuilder {
    private static final Logger s_log = 
        Logger.getLogger(RelatedLinkTableModelBuilder.class);

    private ItemSelectionModel m_itemModel;

    /**
     * Constructor. Creates a <code>LinkTableModelBuilder</code> given an
     * <code>ItemSelectionModel</code>  
     *
     * @param item The <code>ItemSelectionModel</code> for the current page.
     * current link
     */
    public RelatedLinkTableModelBuilder(ItemSelectionModel item) {
        m_itemModel = item;
        s_log.debug("RelatedLinkTableModelBuilder");
    }

    /**
     * Returns the DataCollection of RelatedLinks for the current
     * TableModel. 
     *
     * @param s The <code>PageState</code> for the current request
     * @return The DataCollection of RelatedLinks
     */
    public DataCollection getLinks(PageState s) {
        Assert.truth(m_itemModel.isSelected(s), "item selected");
        ContentItem item = m_itemModel.getSelectedItem(s);
        s_log.debug("Getting related links for " + item.getName());
        return RelatedLink.getRelatedLinks(item);
        
    }
}
