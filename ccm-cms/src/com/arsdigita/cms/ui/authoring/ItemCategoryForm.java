/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.ui.ACSObjectCategoryForm;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.kernel.ACSObject;

public class ItemCategoryForm extends ACSObjectCategoryForm {

    private static Logger s_log = Logger.getLogger(ItemCategoryForm.class);


    public ItemCategoryForm(BigDecimalParameter root, StringParameter mode, Widget widget) {
	super(root, mode, widget);
	s_log.debug("creating new ItemTerm Form with widget " + widget);
                    
    }

    public ItemCategoryForm(BigDecimalParameter root, StringParameter mode) {
	this(root, mode, new CategoryWidget("category", root, mode));
    }
                    
                    
    /* 
     * @see com.arsdigita.categorization.ui.ACSObjectCategoryForm#getObject()
     */
    protected ACSObject getObject(PageState state) {
	ContentItem item = CMS.getContext().getContentItem();
	return item.getParent();
                        
    }

}
