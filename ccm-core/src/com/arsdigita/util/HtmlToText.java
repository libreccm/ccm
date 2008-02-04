/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util;

import java.util.Vector;
import java.util.HashMap;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Generates a best-guess plain text version of an HTML fragment.
 * Parses the HTML and does some simple formatting. The parser and
 * formatting are pretty stupid, but it's better than nothing.
 *
 * <p>Based on the ACS 4.0 Tcl conversion routines by Lars Pind and
 * Aaron Swartz. In fact, its a direct port of that code to Java with
 * very few changes.
 *
 * <p>Intended usage is allocate an HtmlToText object statically and
 * then reuse it by calling its convert method.  The class is not
 * thread-safe for simultaneous access, so you should synchronize on
 * your conversion object if collisions are possible.
 *
 * <p>Example:
 *
 * <pre>
 * static HtmlToText htmlToText = new HtmlToText();
 *
 * synchronize(htmlToText) {
 *     String html = htmlToText.convert(text);
 * }
 * </pre>
 *
 * @version $Id: HtmlToText.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class HtmlToText {

    // Buffer for assembling the converted text

    private StringBuffer m_output;

    // Do we display unknown tags?

    private boolean m_showtags = true;

    // Maximum line length for wrapping

    private int m_maxlen = 70;

    // For maintaining the internal state of the parser

    private int m_linelen;
    private int m_pre;
    private int m_p;
    private int m_br;
    private int m_space;
    private int m_blockquote;

    // Pattern matcher (used heavily)

    private static Perl5Util s_re = new Perl5Util();

    /**
     * Constructor.
     */

    public HtmlToText() {
        reset();
    }

    /**
     * Sets the maximum line length for wrapping text.  The value of
     * maxlen must be greater than zero, otherwise it is simply
     * ignored.  Must be set prior to calling convert().
     *
     * @param maxlen the maximum number of character in an output
     * line.
     */

    public void setMaxLength(int maxlen) {
        if (maxlen > 0) {
            m_maxlen = maxlen;
        }
    }

    /**
     * Sets the flags for whether unrecognized HTML tags are copied to
     * the output.  If set to false, these tags are simply ignored.
     * Must be set prior to calling convert().
     */

    public void setShowTags(boolean showtags) {
        m_showtags = showtags;
    }

    /**
     * Returns the last converted text block as a String.
     */

    public String toString() {
        return m_output.toString();
    }

    /**
     * Reset the internal state of the parser and allocate a new
     * output buffer.
     */

    private void reset() {
        m_linelen    = 0;
        m_pre        = 0;
        m_p          = 0;
        m_br         = 0;
        m_space      = 0;
        m_blockquote = 0;

        m_output = new StringBuffer();
    }

    /**
     * Writes a newline to the output buffer followed by enough
     * whitespace to correctly indent to the current blockquote level.
     */

    private void putNewline() {
        m_output.append('\n');
        m_output.append(StringUtils.repeat("    ", m_blockquote));
        m_linelen = 0;
    }

    /**
     * Expands special HTML codes into regular character sequences.
     */

    private static String expandEntities(String s) {
        s = s_re.substitute("s/&lt;/</gi", s);
        s = s_re.substitute("s/&gt;/>/gi", s);
        s = s_re.substitute("s/&quot;/'/gi", s);
        s = s_re.substitute("s/&mdash;/--/gi", s);
        s = s_re.substitute("s/&#151;/--/gi", s);
        return s_re.substitute("s/&amp;/&/gi", s);
    }

    /**
     * Writes a block of text to the output buffer, after appropriate
     * processing and wrapping.
     */

    private void put(String text) {

        // Expand entities before outputting
        text = expandEntities(text);

        // If we're not in a PRE...
        if (m_pre <= 0) {

            // collapse all whitespace
            text = s_re.substitute("s/\\s+/ /g", text);

            // if there's only spaces in the string, wait until later
            if (text.equals(" ")) {
                m_space = 1;
                return;
            }

            // if it's nothing, do nothing
            if (text.equals("")) {
                return;
            }

            // if the first character is a space, set the space bit
            if (text.charAt(0) == ' ') {
                m_space = 1;
                text = StringUtils.trimleft(text);
            }
        } else {
            // we're in a PRE: clean line breaks and tabs
            text = s_re.substitute("s/\r\n/\n/g", text);
            text = s_re.substitute("s/\r/\n/g", text);
            text = s_re.substitute("s/\t/    /g", text);
        }

        // output any pending paragraph breaks, line breaks or spaces,  as
        // long as we're not at the beginning of the document

        if (m_p != 0 || m_br != 0 || m_space != 0) {
            if (!text.equals("")) {

                if (m_p > 0) {
                    putNewline();
                    putNewline();
                }

                else if (m_br > 0) {
                    putNewline();
                }

                else {

                    // Don't add the space if we're at the beginning of a
                    // line, unless we're in a PRE

                    if (m_pre > 0 || m_linelen != 0) {
                        m_output.append(' ');
                        m_linelen++;
                    }
                }
            }
        }

        m_p = 0;
        m_br = 0;
        m_space = 0;

        // if the last character is a space, save it until the next
        // time
        if (s_re.match("/^(.*) $/",text)) {
            m_space = 1;
            text = s_re.group(1);
        }

        // If there's a blockquote in the beginning of the text, we
        // wouldn't have caught it before

        if (text.equals("")) {
            m_output.append(StringUtils.repeat("    ", m_blockquote));
        }

        // Now output the text

        String word;
        while (s_re.match("/^( +|\\s|\\S+)/", text)) {

            word = s_re.toString();
            text = s_re.postMatch();

            // convert &nbsp;'s
            // We do this now, so that they're displayed, but not
            // treated, whitespace.

            word = s_re.substitute("s/&nbsp;/ /g", word);

            if (word.equals("\n")) {
                if (m_output.length() > 0) {
                    putNewline();
                }
            }

            else if (word.charAt(0) == ' ') {
                m_output.append(word);
                m_linelen += word.length();
            }

            else {
                if (m_maxlen > 0 &&
                    m_linelen + word.length() > m_maxlen) {
                    putNewline();
                }

                m_output.append(word);
                m_linelen += word.length();
            }
        }
    }

    /**
     * Parse attributes in an HTML fragment and return them as a HashMap.
     *
     * The HashMap uses the attribute name as a key and maps it to
     * either the attribue value or null if the attribute has no
     * value.  The attribute names are all converted to lowercase.
     */

    private static final HashMap parseAttributes(String html) {

        HashMap attrs = new HashMap();

        String name;
        String value;

        // Loop over the attributes.
        // We maintain counter is so that we don't accidentally enter
        // an infinite loop

        int i = 0;
        int count = 0;

        while (i < html.length() && html.charAt(i) != '>') {

            if (count++ > 100) {
                throw new RuntimeException
                    ("Infinite loop in HtmlToText.parseAttributes");
            }

            if (html.startsWith("/>", i)) {
                // This is an XML-style tag ending: <... />
                break;
            }

            // This regexp matches an attribute name and an equal
            // sign, if present.  Also eats whitespace before or
            // after.

            if (!s_re.match("/\\s*([^\\s=>]+)\\s*(=?)\\s*/", html.substring(i))) {

                // Apparantly there's no attribute name here. Let's
                // eat all whitespace and lonely equal signs.

                s_re.match("/[\\s=]*/", html.substring(i));
                i += s_re.end(0);

            } else {

                name = s_re.group(1).toLowerCase();

                // Move past the current match
                i += s_re.end(0);

                // If there is an equal sign, we're expecting the next
                // token to be a value
                if (s_re.group(2) == "") {
                    // No equal sign, no value
                    attrs.put(name,null);
                } else {

                    // is there a single or double quote sign as the
                    // first character?

                    String exp;
                    if (html.charAt(i) == '"') {
                        exp = "/\"([^\"]*)\"\\s*/";
                    }
                    else if (html.charAt(i) == '\'') {
                        exp = "/\'([^\']*)\'\\s*/";
                    }
                    else {
                        exp = "/([^\\s>]*)\\s*/";
                    }

                    if (!s_re.match(exp,html.substring(i))) {
                        // No end quote.
                        value = html.substring(i);
                        i = html.length();
                    } else {
                        value = s_re.group(1);
                        i += s_re.end(0);
                    }

                    attrs.put(name,expandEntities(value));
                }
            }
        }

        return attrs;
    }

    /**
     * Convert HTML input to plain text output.  If the input does not
     * contain any embedded  HTML tags this will return a new String
     * that is equal to the input String, with an optional newline
     * character at the end.
     */

    public String convert(String input) {

        reset();

        Vector hrefURLs  = new Vector();
        Vector hrefStack = new Vector();

        int lastTagEnd = 0;
        int i = input.indexOf('<');

        while (i != -1) {

            // append everything up to and not including the
            // tag-opening <

            put(input.substring(lastTagEnd, i));

            // we're inside a tag now. Find the end of it. Make i
            // point to the char after the <

            int tagStart = ++i;

            int count = 0;
            while (true) {

                if (count++ > 100) {
                    throw new RuntimeException
                        ("HtmlToText: infinite loop");
                }

                // Find the positions of the first quote, apostrophe
                // and greater-than sign

                int quoteIdx = input.indexOf('"',i);
                int apostropheIdx = input.indexOf('\'', i);
                int gtIdx = input.indexOf('>',i);

                // If there is no greater-than sign, then the tag
                // isn't closed.

                if (gtIdx == -1) {
                    i = input.length();
                    break;
                }

                // Find the first of the quote and the apostrophe

                int stringDelimiterIdx = Math.min(quoteIdx, apostropheIdx);

                // If the greater than sign appears before any of the
                // string delimters, we've found the tag end.

                if (gtIdx < stringDelimiterIdx ||
                    stringDelimiterIdx == -1) {
                    // we found the tag end
                    i = gtIdx;
                    break;
                }

                // Otherwise, we'll have to skip past the ending
                // string delimiter

                char stringDelimiter = input.charAt(stringDelimiterIdx);

                i = input.indexOf(stringDelimiter, ++stringDelimiterIdx);
                if (i == -1) {
                    // Missing string end delimiter
                    i = input.length();
                    break;
                }
                i++;
            }

            String fullTag = input.substring(tagStart,i);

            if (!s_re.match("/(\\/?)([^\\s]+)/", fullTag)) {
                // A malformed tag -- just delete it
            } else {

                String slash      = s_re.group(1);
                String tagname    = s_re.group(2);
                String attributes = s_re.postMatch();

                tagname = tagname.toLowerCase();

                if (tagname.equals("p") ||
                    tagname.equals("ul") ||
                    tagname.equals("ol") ||
                    tagname.equals("table")) {
                    m_p = 1;
                }

                else if (tagname.equals("br")) {
                    putNewline();
                }

                else if (tagname.equals("tr") ||
                         tagname.equals("td") ||
                         tagname.equals("th")) {
                    m_br = 1;
                }

                else if (tagname.equals("h1") ||
                         tagname.equals("h2") ||
                         tagname.equals("h3") ||
                         tagname.equals("h4") ||
                         tagname.equals("h5") ||
                         tagname.equals("h6")) {
                    m_p = 1;
                    if (slash.equals("")) {
                        int level = Integer.valueOf
                            (tagname.substring(1)).intValue();
                        put(StringUtils.repeat("*", level));
                    }
                }

                else if (tagname.equals("li")) {
                    m_br = 1;
                    if (slash.equals("")) {
                        put("- ");
                    }
                }

                else if (tagname.equals("strong") ||
                         tagname.equals("b")) {
                    put("*");
                }

                else if (tagname.equals("em") ||
                         tagname.equals("i") ||
                         tagname.equals("cite") ||
                         tagname.equals("u")) {
                    put("_");
                }

                else if (tagname.equals("a")) {
                    if (slash.equals("")) {
                        HashMap attrs = parseAttributes(attributes);
                        String title;
                        if (attrs.containsKey("href")) {
                            if (attrs.containsKey("title")) {
                                title = ": " + attrs.get("title");
                            } else {
                                title = "";
                            }

                            int n = hrefURLs.size()+1;

                            hrefURLs.add("["+n+"] " + attrs.get("href"));
                            hrefStack.add("["+n+title+"]");
                        } else if (attrs.containsKey("title")) {
                            hrefStack.add("[" + attrs.get("title") + "]");
                        } else {
                            hrefStack.add("");
                        }
                    } else {
                        if (hrefStack.size() > 0) {
                            if (!((String) hrefStack.lastElement()).equals("")) {
                                put((String) hrefStack.lastElement());
                            }
                            hrefStack.removeAllElements();
                        }
                    }
                }

                else if (tagname.equals("pre")) {
                    m_p = 1;
                    if (slash.equals("")) {
                        m_pre++;
                    } else {
                        m_pre--;
                    }
                }

                else if (tagname.equals("blockquote")) {
                    m_p = 1;
                    if (slash.equals("")) {
                        m_blockquote++;
                        m_maxlen -= 4;
                    } else {
                        m_blockquote--;
                        m_maxlen += 5;
                    }
                }

                else if (tagname.equals("hr")) {
                    m_p = 1;
                    put(StringUtils.repeat('-',m_maxlen));
                    m_p = 1;
                }

                else if (tagname.equals("q")) {
                    put("\"");
                }

                else if (tagname.equals("img")) {
                    if (slash.equals("")) {
                        HashMap attrs = parseAttributes(attributes);
                        StringBuffer imgInfo = new StringBuffer();
                        if (attrs.containsKey("alt")) {
                            imgInfo.append('\'');
                            imgInfo.append((String)attrs.get("alt"));
                            imgInfo.append('\'');
                        }
                        if (attrs.containsKey("src")) {
                            imgInfo.append(' ');
                            imgInfo.append((String)attrs.get("src"));
                        }
                        if (imgInfo.length() == 0) {
                            put("[IMAGE]");
                        } else {
                            put("[IMAGE: " + imgInfo.toString() + "]");
                        }
                    }
                }

                else {
                    // Other tag
                    if (m_showtags == true) {
                        put("&lt;" + slash + tagname + attributes + "&gt;");
                    }
                }
            }

            // set end of last tag to the character following the >
            lastTagEnd = ++i;
            i = input.indexOf('<',i);
        }

        // append everything after the last tag
        put(input.substring(lastTagEnd));

        // Close any unclosed tags
        m_pre = 0;
        while (m_blockquote > 0) {
            m_blockquote--;
            m_maxlen += 4;
        }

        // write out URLs, if necessary:

        if (hrefURLs.size() > 0) {
            m_output.append("\n\n");
            for (i = 0; i < hrefURLs.size(); i++) {
                m_output.append((String) hrefURLs.get(i));
                m_output.append("\n");
            }
        }

        // always end with a newline

        if (m_output.charAt(m_output.length()-1) != '\n') {
            m_output.append('\n');
        }

        return m_output.toString();
    }

    /**
     * Returns HTML text, converted from the following:
     * <ul>
     *  <li>HTML -- returns the input
     *  <li>pre-formatted - returns the input wrapped in &lt;pre&gt; tags
     *  <li>plain - returns the input converted to HTML.
     * </ul>
     *
     * @param formatType one of the types defined in MessageType.
     */
    public static String generateHTMLText(String text, String formatType) {
        if (text == null) {
            return "";
        }

        if (formatType == null || MessageType.TEXT_HTML.equals(formatType)) {
            return text;
        } else if (formatType.equals(MessageType.TEXT_PREFORMATTED)) {
            return "<pre>" + StringUtils.quoteHtml(text) + "</pre>";
        } else if (formatType.equals(MessageType.TEXT_SMART)) {
            return StringUtils.smartTextToHtml(text);
        } else { /*format is plain*/
            return StringUtils.textToHtml(text);
        }
    }

}
