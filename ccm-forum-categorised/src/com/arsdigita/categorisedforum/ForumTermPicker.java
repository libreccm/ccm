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

package com.arsdigita.categorisedforum;

import org.apache.log4j.Logger;

import com.arsdigita.aplaws.ui.ACSObjectCategoryPicker;
import com.arsdigita.aplaws.ui.TermWidget;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.ui.ACSObjectCategoryForm;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.kernel.ACSObject;

public class ForumTermPicker extends ACSObjectCategoryPicker {
    private static final Logger s_log = Logger.getLogger(ForumTermPicker.class);

   
    public ForumTermPicker(BigDecimalParameter root,
                              StringParameter mode) {
                      super(root, mode);
           s_log.debug("instantiating ForumCategoryPicker");
        
    }


	/* (non-Javadoc)
	 * @see com.arsdigita.aplaws.ui.ACSObjectCategoryPicker#getForm(com.arsdigita.bebop.parameters.BigDecimalParameter, com.arsdigita.bebop.parameters.StringParameter)
	 */
	protected ACSObjectCategoryForm getForm(BigDecimalParameter root, StringParameter mode) {
		s_log.debug("getForm");
		return new ForumTermForm(root, mode, new TermWidget(mode, this));
	}


	/* (non-Javadoc)
	 * @see com.arsdigita.aplaws.ui.ACSObjectCategoryPicker#getObject()
	 */
	protected ACSObject getObject(PageState state) {
		return ForumContext.getContext(state).getForum();     
        


	}
    
   
}
