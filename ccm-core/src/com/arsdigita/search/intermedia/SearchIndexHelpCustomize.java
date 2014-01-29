/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.intermedia;

/**
 * This class is now deprecated, use the generic search API
 * in the com.arsdigita.search package instead.
 * @deprecated see the com.arsdigita.search package
 **/
public interface SearchIndexHelpCustomize {

    static final String RAW = "RAW";
    static final String XML = "XML";

    /**
     * Method <code>searchIndexHelpFields</code> returns
     * a String array which
     * specifies the customization of the fields to be indexed
     * using the <code>SearchIndexHelp</code>.
     * (<code>SearchIndexHelp</code> methods
     * are used to index the content if the string
     * "Use_SearchIndexHelp" is returned by
     * either the <code>getSearchXMLContent</code>
     * or <code>getSearchRawContent</code> methods).
     * <p>
     * For example, if a DomainObject
     * contains four fields: title, author, start_date,
     * and description; and the "title" and "author"
     * fields are to be indexed using
     * XML, "description" is to be indexed as raw content, and
     * "start_date" should not be indexed; then
     * the String array returned would be the following:
     *<p>
     *<pre>
     *    String[] indexFields = {
     *       "title" + XML,
     *       "author" + XML,
     *       "description + RAW
     *    }
     *</pre>
     * Any field not in the string (such as "start_date" in the above
     * example) is not indexed.
     *
     * @see com.arsdigita.search.SearchIndexHelp
     * @see com.arsdigita.search.Searchable
     * @see com.arsdigita.search.SearchableObserver
     * @see com.arsdigita.search.SearchableACSObject
     *
     * @author Jeff Teeters
     * @version 1.0
     **/
    public String[] searchIndexHelpFields();
}
