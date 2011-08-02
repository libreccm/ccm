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
package com.arsdigita.cms;

import java.util.StringTokenizer;

/**
 * Contains utility methods which are useful for constructing
 * search queries
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: SearchFormatter.java 2090 2010-04-17 08:04:14Z pboy $
 */

public class SearchFormatter {

    /**
     * A list of all the special characters which will
     * be filtered out
     */
    public static final String SPECIAL_CHARS =
        "|&-*;{}%_$?!()\':@.<>#^+=[]~`\", \t\r\n";

    /**
     * A list of common words which will be filtered out
     */
    public static final String[] COMMON_WORDS = {
        "the", "of", "to", "with", "and", "or", "for", "this"
    };

    /**
     * An Intermedia wildcard that matches any sequence of
     * characters
     */
    public static final String WILD_CARD = "%";

    /**
     * An "and" join clause
     */
    public static final String AND = "and";

    /**
     * An "or" join clause
     */
    public static final String OR = "or";


    /**
     * Determine if the string represents a common word.
     * Common words, such as "the", "of", etc., should not be
     * included in search clauses.
     *
     * @param s a search keyword
     * @return true if <code>s</code> is a common word, false otherwise
     */
    public boolean isCommonWord(String s) {
        for(int i=0; i<COMMON_WORDS.length; i++) {
            if(COMMON_WORDS[i].equalsIgnoreCase(s))
                return true;
        }

        return false;
    }


    /**
     * Convert some keywords which are typed in by the user to
     * an Intermedia search clause. Clean out all the special
     * characters, and remove common words. Surround each word
     * with the '%' wildcard. For example, the search string
     * <code>"cat, fish and bird"</code> will be converted to
     * <code>"%cat% and %fish% and %bird%"</code>
     *
     * @param words a string which contains some search keywords
     */
    public static String createIntermediaClause(String words) {
        return createIntermediaClause(words, AND, WILD_CARD);
    }

    /**
     * Convert some keywords which are typed in by the user to
     * an Intermedia search clause. Clean out all the special
     * characters, and remove common words. Surround each word
     * with the '%' wildcard. For example, the search string
     * <code>"cat, fish and bird"</code> will be converted to
     * <code>"%cat% and %fish% and %bird%"</code>
     *
     * @param words a string which contains some search keywords
     * @param joinClause a string which will be used to combine the
     *   keywords. Should be <code>"and"</code>, <code>"or"</code>,
     *   or a similar boolean operator.
     * @param wildcard a wildcard which will be appened to the right
     *   and to the left of each keyword
     */
    public static String createIntermediaClause(
                             String words, String joinClause, String wildcard
                                                ) {

        StringTokenizer tokenizer = new StringTokenizer(words, SPECIAL_CHARS);
        StringBuffer result = new StringBuffer();
        String nextJoin = "";

        // Normalize the join clause
        StringBuffer joinBuf = new StringBuffer();
        joinBuf.append(" ").append(joinClause.trim()).append(" ");
        joinClause = joinBuf.toString();

        // Append words
        while(tokenizer.hasMoreTokens()) {
            result.append(nextJoin);
            String token = tokenizer.nextToken();
            result.append(token).append(wildcard);
            nextJoin = joinClause;
        }

        return result.toString();
    }
}
