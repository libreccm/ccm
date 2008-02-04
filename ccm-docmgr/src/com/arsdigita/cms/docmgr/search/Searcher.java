package com.arsdigita.cms.docmgr.search;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import com.arsdigita.kernel.User;


public interface Searcher {

    public SearchResults simpleSearch ( String terms, User user );

    public SearchResults advancedSearch (String terms, String author,
                                         String mimeType,
                                         BigDecimal workspaceID,
                                         Date lastModifiedStartDate,
                                         Date lastModifiedEndDate,                                         
                                         String[] types, String[] sections, 
                                            User user, Collection categoryIDs);
}
