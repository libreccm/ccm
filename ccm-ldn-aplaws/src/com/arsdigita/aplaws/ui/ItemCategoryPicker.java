/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.ui.ACSObjectCategoryForm;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ui.authoring.*;
import com.arsdigita.kernel.ACSObject;

/**
 * 
 * 
 * cms specific Concrete implementation of ACSObjectCategoryPicker 
 * 
 */


public class ItemCategoryPicker extends ACSObjectCategoryPicker {
    private static final Logger s_log = Logger.getLogger(ItemCategoryPicker.class);

   
    public ItemCategoryPicker(BigDecimalParameter root,
                              StringParameter mode) {
                      super(root, mode);
           s_log.debug("instantiating ItemCategoryPicker");
        
    }


    /*
     * @see com.arsdigita.aplaws.ui.ACSObjectCategoryPicker#getForm(com.arsdigita.bebop.parameters.BigDecimalParameter, com.arsdigita.bebop.parameters.StringParameter)
     */
    protected ACSObjectCategoryForm getForm(BigDecimalParameter root, StringParameter mode) {
	s_log.debug("getForm");
	return new ItemCategoryForm(root, mode, new TermWidget(mode, this));
    }


    /* 
     * @see com.arsdigita.aplaws.ui.ACSObjectCategoryPicker#getObject()
     */
    protected ACSObject getObject(PageState state) {
	ContentItem item = CMS.getContext().getContentItem();
	return item.getParent();

    }
    
   
}
