/**
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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
 */

package com.arsdigita.aplaws.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.terms.ui.AbstractAssignedTerms;

import org.apache.log4j.Logger;

public class AssignedIndexItemTerms extends AbstractAssignedTerms {

    private static final Logger s_log = Logger.getLogger(AssignedIndexItemTerms.class);

    protected ACSObject getObject(PageState state) {

        ACSObject obj = Navigation.getConfig().getDefaultModel().getObject();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Dealing with item " + obj);
        }

        return obj;
    }
}
