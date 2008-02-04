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

import org.apache.log4j.Logger;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.search.Search;
import com.arsdigita.util.StringUtils;

/**
 * This class provides methods to make it easier
 * to specify a search.  These methods may be used
 * in two ways, depending on the complexity of
 * the search.
 * <p>
 * If the search is simple (searching for a single string
 * in either the xml or raw content fields, with no joined
 * tables, and only the standard fields selected) then a
 * SimpleSearchSpecification object can be
 * created to do the search.  Search results can then
 * be accessed by method getSearchPage.  This allows using
 * accessor methods in class SearchDataQuery
 * to easily retrieve the returned fields.
 * <p>
 * If the search cannot be done by creating a
 * <code>SimpleSearchSpecification</code> object, then
 * the more general <code>SearchSpecification</code>
 * object will have to be created to do the search.
 * However, in that case, two static methods in
 * this class (<code>cleanSearchString</code> and
 * <code>containsClause</code>)
 * can be used to help build the search query that
 * is passed into <code>SearchSpecification</code>.
 * <p>
 * @see com.arsdigita.search.intermedia.Searchable
 * @see com.arsdigita.search.intermedia.SearchSpecification
 * @see com.arsdigita.search.intermedia.SearchDataQuery
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version 1.0
 **/
public class SimpleSearchSpecification extends SearchSpecification {
    public static final String versionId = "$Id: SimpleSearchSpecification.java 1431 2007-02-05 16:40:16Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/16 18:10:38 $";
    
    private static final Logger s_log = Logger.getLogger(SimpleSearchSpecification.class);
    final static String WORD_ESCAPE_BEGIN = "\"";

    final static String WORD_ESCAPE_END = "\"";

    final static String[] s_columns = {
        "object_id",
        "object_type",
        "summary",
        "link_text",
        "url_stub",
        "score"
    };

    /**
     * Helper method to quote a String.  This puts quotes around the
     * entire source string, and expands each internal quote into
     * quotequote.
     *
     * @param text is the string to be quoted
     */

    protected static String quote (String text) {

        // our special character
        char q = '\'';

        // a string buffer to store the result
        StringBuffer result = new StringBuffer(text.length());
        result.append(q);

        // a character array to convert from
        char[] c = text.toCharArray();

        // expand the internal quotes, copy the rest
        for (int i = 0; i < c.length; i++) {
            if (c[i] == q) {
                result.append(q);
            }
            result.append(c[i]);
        }

        // add the final quote and convert to a String
        return result.append(q).toString();
    }

    /**
     * Build a containsClause for a search query.  The returned contains
     * clause will be of the form:
     * <pre>
     *   (contains(<em>sc</em>.xml_content, '<em>searchString</em>', <em>xml_label</em>) > 0 OR
     *    contains(<em>sc</em>.raw_content, '<em>searchString</em>', <em>raw_label</em>) > 0)
     * </pre>
     *<p>
     * Specifying null for either the label parameters inhibits the generation
     *        of the contains clause for the corresponding column.  For example,
     *        if the xml_label was null, only the contains for the raw_content
     *        field would be generated.  If both xml_label and raw_label are null
     *        an empty string is returned.
     *
     * @param sc alias for search_content table name.  For example, if
     *        the query contained:
     *        <pre>
     *           select ... from search_content c ...
     *        </pre>
     *        then sc would be the string "c".
     * @param searchString The string to be searched.  It should have either
     *        been processed by <code>cleanSearchString</code> before being
     *        passed into this method or otherwise known not to contain invalid
     *        characters or words.
     *        Single quote characters (i.e. ') are allowed in the search string
     *        (each single quote character is escaped by replacing it with two
     *        single quote characters when the contains clause is generated).
     * @param xml_label  A label used to identify the score for matching on
     *        the xml_content field.  Would normally be "1".  If null, then the
     *        contains clause for the xml_content field is not created.
     * @param raw_label  A label used to identify the score for matching on
     *        the raw_content field.  Would normally be "2".  If null, then the
     *        contains clause for the raw_content field is not created.
     * @return An oracle interMedia sql contains clause for performing the search.
     ***/
    public static String containsClause(String sc,
                                        String searchString,
                                        String xml_label,
                                        String raw_label) {
        String clause = "";
        String quotedSS = quote(searchString);

        if (xml_label != null) {
            clause = "contains(" + sc + ".xml_content, " + quotedSS +
                ", " + xml_label + ") > 0";
        }
        if (raw_label != null) {
            if (!clause.equals("")) {
                clause = clause + " or ";
            }
            clause = clause + "contains(" + sc + ".raw_content, " + quotedSS +
                ", " + raw_label + ") > 0";
        }
        if (!clause.equals("")) {
            clause = "(" + clause + ")";
        }
        s_log.debug(clause);
        return clause;
    }


    private static String createSearchString(String object_type,
                                             String searchString) {
        StringBuffer sb = new StringBuffer("select c.object_id, c.object_type, ")
        	.append("c.link_text, c.url_stub, c.summary, (score(1)+score(2)) as score ")
        	.append("from search_content c where ")
        	.append(containsClause("c", searchString, "1", "2"));
        if (object_type != null) {
            sb.append( " and c.object_type = " + quote(object_type));
        }
        sb.append(" order by score desc");
        return sb.toString();
    }

    private String m_object_type;

    /**
     * Create a SimpleSearchSpecification object to search for objects
     * of the given type that contain the specified searchString.
     * If the string contains more than one word, the search will be
     * for objects that contain all of the words.
     * @param object_type The type of object to search.
     * @param searchString The string to search for.
     ***/
    public SimpleSearchSpecification(String object_type,
                                     String searchString) {
        super(createSearchString(object_type, cleanSearchString(searchString, " and ")), s_columns);
        m_object_type = object_type;
    }

    /**
     * Create a SimpleSearchSpecification object to search for objects
     * of any type that contain the specified searchString.
     * If the string contains more than one word, the search will be
     * for objects that contain all of the words.
     * @param searchString The string to search for.
     ***/
    public SimpleSearchSpecification(String searchString) {
        this(null, searchString);
    }

    private static final char cleaned_chars[] = {
        '|', '&', ',', '-', '*', ';', '{', '}',
        '%', '_', '$', '?', '!', '(', ')', '\'',
        ':', '@', '.', '<', '>', '#', '^', '+', '=', '[', ']', '~', '`'};

    private static final String cleaned_words[] = {
        "the", "of", "to", "with", "and", "or", "for", "this", "in" // 'in' causes oracle exception
    };

    private static boolean isCleanChar(char at) {
        for (int j = 0; j<cleaned_chars.length; j++) {
            if (at==cleaned_chars[j]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCleanWord(String word) {
        if (StringUtils.emptyString(word)) {
            // it is possible to have an empty 'word' if all  characters were filtered out
            return false;
        } else {
            for (int j = 0; j<cleaned_words.length; j++) {
                if (word.equalsIgnoreCase(cleaned_words[j])) {
                    return false;
                }
            }
            return true;
        }
    }

    private static boolean maybeAppendWord(StringBuffer sb, StringBuffer word_sb, String joinString, String stemmingOp, boolean exactPhrase) {
        s_log.debug("maybe appending word " + word_sb + " exact phrase? " + exactPhrase);
        String word = new String(word_sb);
        if (isCleanWord(word)) {
            sb.append(joinString).append(WORD_ESCAPE_BEGIN);
            if (!exactPhrase) {
            	sb.append(stemmingOp);
            }
            sb.append(word)
                .append(WORD_ESCAPE_END);
            return true;
        }
        return false;
    }

    /**
     * Cleanup the search string by removing characters |&amp;,-*;{}%_$?!()\:@.&lt;&gt;#^+=[]~`
     * and by combining multiple words with a join string.
     * @param searchString Input search string to be cleaned.
     * @param joinString The join string.  Normally this will be " and " to generate
     *        a search for objects containing all words in the searchString.
     * @return The cleaned up string.
     ***/
    public static String cleanSearchString(String searchString, String joinString) {
        StringBuffer sb = new StringBuffer();
        StringBuffer word_sb = new StringBuffer();
        String stemmingOperator = Search.getConfig().includeStemming() ? "$" : "";
        boolean skip_whitespace = true;
        boolean in_quotes = false;
        String next_joinString = "";
        for (int i = 0; i<searchString.length(); i++) {
            char at = searchString.charAt(i);
            if (skip_whitespace && (at == ' ' || at == '\t')) {
                continue;
            }
            if (!isCleanChar(at)) {
                continue;
            }
            if (at == ' ' || at == '\t') {
                if ( !in_quotes ) {
                    if (maybeAppendWord(sb, word_sb, next_joinString, stemmingOperator, false)) {
                        next_joinString = joinString;
                    }
                    word_sb = new StringBuffer();
                    skip_whitespace = true;
                } else {
                    skip_whitespace = true;
                    word_sb.append(' ');
                }
            } else if (at == '"') {
                if (in_quotes) {
                    //word_sb.append(at);
                    if (maybeAppendWord(sb, word_sb, next_joinString, stemmingOperator, true)) {
                        next_joinString = joinString;
                    }
                    word_sb = new StringBuffer();
                    skip_whitespace = true;
                    in_quotes = false;
                } else {
                    if (word_sb.length() > 0) {
                        if (maybeAppendWord(sb, word_sb, next_joinString, stemmingOperator, false)) {
                            next_joinString = joinString;
                        }
                    }
                    in_quotes = true;
                    skip_whitespace = true;
                    //word_sb.append(at);
                }
            } else {
                skip_whitespace = false;
                word_sb.append(at);
            }
        }
        maybeAppendWord(sb, word_sb, next_joinString, stemmingOperator, false);
        return sb.toString();
    }

    public void setSelect(String sql, String[] columns) {
        throw new UnsupportedOperationException();
    }

    /**
     * Execute a search, returning a page of search results.  If there is a
     * "next page" of results after the returned page, the number of rows
     * returned will be the number of rows per page <em>plus one</em>.
     * This allows determining if a "next page" link should be generated by
     * counting the number of rows returned; i.e. if the total is greater than
     * rowsPerPage) then there is a next page, otherwise there is not.
     * A row that flags the presence of a next page, should <em>not</em> be displayed
     * to the user because it will be the first row returned on the next page.
     *
     * @param page The page of search results to retrieve
     *  (page==1 means first page).
     **/

    public DataQuery getPage(int page) {
        // Reformat SQL to specify the rows on the page
        String sql_for_page = reformatSqlForPage(page);
        SearchDataQuery sdq = new com.arsdigita.search.SearchDataQuery(
            SessionManager.getSession(),
            sql_for_page, s_columns);
        // XXX: Removed by rhs@mit.edu. See apology above.
        //        sdq.dobind("object_type", m_object_type);
        return sdq;
    }

    /**
     * Similar to getPage but returns results as a SearchDataQuery object.
     * SearchDataQuery provides get methods for the fields returned.
     *
     * @param page The page of search results to retrieve
     *  (page==1 means first page).
     **/
    public SearchDataQuery getSearchPage(int page) {
        return (SearchDataQuery)getPage(page);
    }
}
