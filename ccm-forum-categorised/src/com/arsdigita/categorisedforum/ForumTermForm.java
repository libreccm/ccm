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
package com.arsdigita.categorisedforum;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.ui.ACSObjectCategoryForm;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.kernel.ACSObject;

public class ForumTermForm extends ACSObjectCategoryForm {
	
	private static Logger s_log = Logger.getLogger(ForumTermForm.class);
	
	
	public ForumTermForm(BigDecimalParameter root, StringParameter mode, Widget widget) {
		super(root, mode, widget);
		s_log.debug("creating new ForumTerm Form with widget " + widget);
		
		
		
	}	

	
	/* (non-Javadoc)
	 * @see com.arsdigita.categorization.ui.ACSObjectCategoryForm#getObject()
	 */
	protected ACSObject getObject(PageState state) {
		
		return ForumContext.getContext(state).getForum();     
                   
	}

}
