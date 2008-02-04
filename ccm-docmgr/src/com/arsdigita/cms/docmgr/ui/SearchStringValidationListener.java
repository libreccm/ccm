/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.ui;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.cms.docmgr.search.IntermediaSearcher;
import com.arsdigita.cms.docmgr.search.SearchUtils;
import com.arsdigita.search.SimpleSearchSpecification;
import com.arsdigita.util.StringUtils;


/** Performs IntermediaSearcher.cleanSearchString() on search string
 * and adds an error if the cleaned search string is null */

public class SearchStringValidationListener implements ParameterListener {
    
    private static final Logger s_log 
        = Logger.getLogger(SearchStringValidationListener.class);

    public SearchStringValidationListener() {}
    
    public void validate(ParameterEvent event) throws FormProcessException {
        s_log.debug("asdfsdaadsfsdaf");
        if ( SearchUtils.getSearcher().getClass().equals(IntermediaSearcher.class)) {
            s_log.debug("dfadfsadfasdfsdafasdfasdfasdf");
            ParameterData data = event.getParameterData();
            String terms  = (String)data.getValue();
            PageState state = event.getPageState();
            
            String clean = SimpleSearchSpecification.cleanSearchString(
                terms, "and");
            if (clean == null 
                || StringUtils.emptyString(clean)) {
                
                data.addError("Invalid character \"" + terms 
                              + "\" in search string");
                throw new FormProcessException("Invalid term");
            }
        }
    }
}
