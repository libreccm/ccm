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
package com.arsdigita.ui.admin;

// Apache regexp
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.StringSubstitution;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Util;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.log4j.Logger;

/**
 * Utilities class for the Admin UI package.  Provides functionality like
 * preparing search query strings and, well, not much else.
 *
 * @author Kevin Scaldeferri 
 */

final class Utilities {
    public static final String versionId = "$Id: Utilities.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(Utilities.class);
    /**
     * Helper method to prepare the search string.  Currently removes
     * extraneous spaces and quotes the string.  should live somewhere
     * more general
     */

    static final String prepare(String input) {
        Perl5Compiler compiler = new Perl5Compiler();
        Perl5Matcher matcher = new Perl5Matcher();

        Pattern pattern;

        StringSubstitution emptySub = new StringSubstitution("");

        // remove spaces at beginning
        try {
            pattern = compiler.compile("^ +");
        } catch (MalformedPatternException e) {
            pattern = null;
            s_log.warn("Perl5Compiler choked on '^ +'", e);
        }

        String preTrimmed = Util.substitute(
                                            matcher, pattern, emptySub, input);

        // remove spaces at the end
        try {
            pattern = compiler.compile(" +$");
        } catch (MalformedPatternException e) {
            pattern = null;
            s_log.warn("Perl5Compiler choked on ' +$'");
        }

        String allTrimmed = Util.substitute(
                                            matcher, pattern, emptySub, preTrimmed);

        // remove multiple spaces
        try {
            pattern = compiler.compile("  +");
        } catch (MalformedPatternException e) {
            pattern = null;
        }

        String noExtras = Util.substitute(
                                          matcher, pattern, new StringSubstitution(" "), allTrimmed);

        return quote(noExtras);
    }

    /**
     * Helper method to quote a String.  Converts every internal
     * occurence of "'" with "''" so the resulting string can be
     * passed to the SQL processor.  Needs to move into some generic
     * utility package.
     *
     * @param text is the string to be quoted
     */

    static final String quote(String input) {

        String q = "'";

        Perl5Compiler compiler = new Perl5Compiler();
        Perl5Matcher  matcher  = new Perl5Matcher();
        StringSubstitution sub = new StringSubstitution("''");

        Pattern pattern;

        try {
            pattern = compiler.compile(q);
        } catch (MalformedPatternException e) {
            pattern = null;
        }

        return Util.substitute
            (matcher, pattern, sub, input, Util.SUBSTITUTE_ALL);
    }

}
