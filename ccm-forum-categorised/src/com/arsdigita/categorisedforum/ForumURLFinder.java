/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.categorisedforum;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.forum.Forum;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;
import com.arsdigita.web.URL;

public class ForumURLFinder implements URLFinder {
    
    private static final Logger s_log = Logger.getLogger(ForumURLFinder.class);

    public String find(OID oid,String context) throws NoValidURLException {
       
		
       return find(oid);
        
        
    }

	/* (non-Javadoc)
	 * @see com.arsdigita.kernel.URLFinder#find(com.arsdigita.persistence.OID)
	 */
	public String find(OID oid) throws NoValidURLException {
		Application forum;
		
	   try {
			forum = (Forum)DomainObjectFactory.newInstance(oid);
			
		
		} catch (DataObjectNotFoundException ex) {
			throw new NoValidURLException(
				"cannot instantiate application " + oid + 
				" message: " + ex.getMessage()
			);
		}
         
		 return URL.getDispatcherPath() + forum.getPath();     
	}
    
   
}
