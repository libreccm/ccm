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
 *
 */
package com.arsdigita.cms.workflow;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.workflow.TaskURLGenerator;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.web.URL;
import com.arsdigita.workflow.simple.Task;

import java.math.BigDecimal;



/**
 * Generates a mail client friendly link to the item in the front end. Suitable for 
 * finish deploy task event.
 *
 * Note - folder based url required because of invalid format of oid representation
 * which means mail clients don't all render a link. New format of oid 
 * introduced by Bristol may have resolved this issue
 *
 * @author Chris Gilbert (chris.gilbert at westsussex.gov.uk)
 * @version $Id: DeployTaskURLGenerator.java 285 2005-02-22 00:29:02Z sskracic $
 * */

public class PublishedItemURLGenerator implements TaskURLGenerator {

    public PublishedItemURLGenerator() {}

	/**
	 * returns a url based on the folder structure. 
	 */
	public String generateURL(BigDecimal itemId, BigDecimal taskId) {
		
		// note redirect url not used because invalid characters mean it is not recognised as a url 
		// by mail clients and so not automatically rendered as a link
		ContentItem item = new ContentItem(itemId).getLiveVersion();
		    
		String url = "";
		if (item != null) {
			ContentSection section = item.getContentSection();
        
			url = "/" + section.getName() + "/" + ((ContentItem)item.getParent()).getPath();
															
    		
		}
    	
    	
		
    	
		return url;
	}
    	
    	
	
}
