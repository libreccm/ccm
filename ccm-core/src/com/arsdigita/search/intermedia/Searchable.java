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
 * This interface is deprecated since it is Intermedia specific.
 * All code should now be using the generic search API found
 * in the com.arsdigita.search package.
 * @deprecated register a com.arsdigita.search.MetadataProvider instead
 **/
public interface Searchable {
    public static final String versionId = "$Id: Searchable.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Returns a short summary description of the object. This
     * information can then be displayed on a search results
     * page. Note that the summary is not automatically included in
     * the text that is indexed.
     **/
    public String getSearchSummary();

    /**
     * Returns a string to use as the text inside the link in the
     * search results
     **/
    public String getSearchLinkText();

    /**
     * Returns a url stub used to link to more information about this
     * object.
     **/
    public String getSearchUrlStub();

    /**
     * Returns an xml string of the text to index for this object. The
     * xml tags are used as section groups. If both
     * getSearchXMLContent and getSearchRawContent return empty
     * strings, the object will not be indexed.
     **/
    public String getSearchXMLContent();

    /**
     * Return the entire text body for indexing. If both
     * getSearchXMLContent and getSearchRawContent return empty
     * strings, the object will not be indexed.
     **/
    public byte[] getSearchRawContent();

    /**
     * Returns the language type of document.  According to the
     * example at: <a href="http://www.oradoc.com/ora817/inter.817/a77063/cdatai5.htm#43659">Oracle
     * interMedia text reference</a> the language code follows
     * <a href="http://www.loc.gov/standards/iso639-2/langhome.html">ISO 639-2</a>.
     * "eng" for English.
     **/
    public String getSearchLanguage();
}
