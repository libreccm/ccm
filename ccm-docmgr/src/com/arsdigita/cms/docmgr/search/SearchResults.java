/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.search;

import java.util.Iterator;

/**
 * @author hbrock@redhat.com
 * @version $Revision: #1 $ $Date: 2003/08/20 $
 */
public interface SearchResults {

    /**
     * Returns the number of search results
     */
    public long getTotalSize();
    
    /**
     * Sets the indexes of the first and last results returned.
     * Result numbering begins with 0
     * @param first
     * @param last
     */
    public void setRange(Integer first, Integer last);
    
    /**
     * Returns an iterator over the desired range of Results,
     * or all the Results if setRange has not been called.
     */
    public Iterator getResults();
}
