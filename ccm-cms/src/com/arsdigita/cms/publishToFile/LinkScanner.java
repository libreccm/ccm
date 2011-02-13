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
package com.arsdigita.cms.publishToFile;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.util.Assert;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * Scan a string for a fixed set of HTML tags and find the tags that
 * reference items on the editorial server. The scanner looks for the tags
 * <tt>&lt;a&gt;</tt>, <tt>&lt;img&gt;</tt>, and <tt>&lt;link&gt;</tt>
 * which have an <tt>oid</tt> attribute. The method {@link #size} returns
 * the number of tags found, and the method {@link #getTarget getTarget}
 * can be used to find the exact item that is linked to by the
 * <tt>i</tt>-th tag. The user of the class is required to call {@link
 * #setTargetURL setTargetURL} for each of the targets, and set the URL to
 * the new target for that item, usually a URL on the live server. After
 * all target URLs have been set, a call to {@link #transform transform}
 * will write out the changed text; each tag that was found during scanning
 * will be changed to point to the target URL that has been set, and
 * <tt>oid</tt> attributes will have been removed.
 *
 * <p> The scanner uses the following regular grammar to find tags and
 * attributes (spaces below are only there for better readability):</p>
 * <pre>
 *   TEXT   = ([^<]*<WS ELEM WS (ATTR WS)*>)*
 *   ELEM   = (a|img|link)
 *   ATTR   = NAME WS = WS VALUE
 *   NAME   = [a-z][a-z0-9_-:.]*
 *   VALUE  = ([^\s'"]+ | '[^']*' | "[^"]*")
 *   WS = \s*
 * @author <a href="mailto:dlutter@redhat.com">David Lutterkort</a>
 * @version $Id: LinkScanner.java 287 2005-02-22 00:29:02Z sskracic $
 */
class LinkScanner {

    private static final Logger s_log = Logger.getLogger(LinkScanner.class);
    // Regexp magic. We deliberately don't use the JDK 1.4 regex features, so
    // that this class can also be used under JDK 1.3
    // The option we use for matching
    private static final int MATCH_MASK = Perl5Compiler.CASE_INSENSITIVE_MASK
                                          | Perl5Compiler.MULTILINE_MASK;
    // The HTML elements we are interested in. This must match exactly the
    // elements listed in TAGS
    private static final String ELEMS_RE = "(a|img|link)";
    // Regexp to find a tag
    private static final String TAG_RE = "\\<\\s*" + ELEMS_RE
                                         + "\\s*([^>]*?)\\s*/?\\>";
    // Regexp to find an attribute/value pair within a tag
    private static final String ATTR_RE =
                                "([a-z][a-z0-9]*)\\s*=\\s*([^\\s'\"]+|'[^']*'|\"[^\"]*\")";
    // These two variables will be initialized with precompiled patterns
    private static final Pattern TAG_PAT;
    private static final Pattern ATTR_PAT;
    // The tags that we are interested in. The regexp ELEMS_RE must match
    // exactly these tags.
    // REF_ATTR contains for each of the TAGS which of their attribute holds
    // the link that we are going to alter.
    private static final String[] TAGS = {"img", "link", "a"};
    private static final String[] REF_ATTR = {"src", "href", "href"};
    // The special oid attribute we use to figure out which tags we need to
    // futz with.
    private static final String OID_ATTR = "oid";
    // FIXME: These are references to what should be displayed when tag
    // rewriting can't find the live target item. These values only work for
    // DP.
    private static final String[] NOT_FOUND = {"/notfound.jpg",
                                               "/notfound.html",
                                               "/notfound.html"};

    static {
        s_log.debug("Static initializer is starting...s");
        Perl5Compiler comp = new Perl5Compiler();
        try {
            TAG_PAT = comp.compile(TAG_RE, MATCH_MASK);
        } catch (MalformedPatternException e) {
            s_log.error(String.format("Failed to compile regex tag '%s' "
                                      + "with match mask '%s':",
                                      TAG_RE,
                                      MATCH_MASK),
                        e);
            throw new RuntimeException("Failed to compile \n'" + TAG_RE + "' " + e.
                    getMessage());
        }
        try {
            ATTR_PAT = comp.compile(ATTR_RE, MATCH_MASK);
        } catch (MalformedPatternException e) {
            s_log.error(String.format("Failed to compile regex tag '%s' "
                                      + "with match mask '%s':",
                                      ATTR_RE,
                                      MATCH_MASK),
                        e);
            throw new RuntimeException("Failed to compile \n'" + ATTR_RE + "' " + e.
                    getMessage());
        }
        s_log.debug("Static initalizer finshed.");
    }
    private String m_content;
    private TagRef[] m_tags;

    public LinkScanner(String html) {
        Assert.exists(html);
        m_content = html;
        m_tags = parse();
    }

    public void transform(Writer out)
            throws IOException {

        if (m_tags == null || m_tags.length == 0) {
            // Nothing to do
            out.write(m_content);
            return;
        }

        TagRef ta;
        int i = 0;

        out.write(m_content.substring(0, m_tags[0].tagStart));
        for (i = 0; i < m_tags.length; i++) {
            ta = m_tags[i];
            if (i > 0) {
                out.write(m_content.substring(m_tags[i - 1].tagEnd, ta.tagStart));
            }
            ta.replaceReference(out);
        }
        out.write(m_content.substring(m_tags[m_tags.length - 1].tagEnd));
    }

    public int size() {
        return (m_tags == null) ? 0 : m_tags.length;
    }

    public ContentItem getTarget(int i) {
        return m_tags[i].getItem();
    }

    protected BigDecimal getItemOID(int i) {
        return m_tags[i].oid;
    }

    public void setTargetURL(int i, String url) {
        m_tags[i].url = url;
    }

    private TagRef[] parse() {
        ArrayList foundTags = new ArrayList();

        PatternMatcherInput body = new PatternMatcherInput(m_content);
        PatternMatcherInput tag = new PatternMatcherInput(body.getBuffer());
        PatternMatcher tagMatcher = new Perl5Matcher();
        PatternMatcher attrMatcher = new Perl5Matcher();

        while (tagMatcher.contains(body, TAG_PAT)) {
            MatchResult t = tagMatcher.getMatch();
            int tagType = getTagType(t.group(1));
            int tagStart = t.beginOffset(2);
            int tagEnd = t.endOffset(2);
            tag.setBeginOffset(tagStart);
            tag.setEndOffset(tagEnd);
            BigDecimal oid = null;
            int linkStart = -1;
            int linkEnd = -1;
            while (attrMatcher.contains(tag, ATTR_PAT)) {
                MatchResult a = attrMatcher.getMatch();
                if (OID_ATTR.equalsIgnoreCase(a.group(1))) {
                    oid = extractOID(a.beginOffset(2), a.endOffset(2));
                } else if (REF_ATTR[tagType].equalsIgnoreCase(a.group(1))) {
                    linkStart = a.beginOffset(2);
                    linkEnd = a.endOffset(2);
                }
            }
            if (oid != null) {
                TagRef ref = new TagRef(tagType, tagStart, tagEnd, linkStart,
                                        linkEnd,
                                        oid);
                foundTags.add(ref);
            }
        }
        int size = foundTags.size();
        return (size > 0 ? (TagRef[]) foundTags.toArray(new TagRef[size]) : null);
    }

    private BigDecimal extractOID(int begin, int end) {
        String val = null;
        if (m_content.charAt(begin) == '"'
            || m_content.charAt(begin) == '\'') {
            val = m_content.substring(begin + 1, end - 1);
        } else {
            val = m_content.substring(begin, end);
        }
        return new BigDecimal(val);
    }

    private int getTagType(String tag) {
        for (int i = 0; i < TAGS.length; i++) {
            if (TAGS[i].equalsIgnoreCase(tag)) {
                return i;
            }
        }
        throw new IllegalStateException(
                "Tag '" + tag
                + "' found, but no information registered for it in TAGS. This is a programming error.");
    }

    /***
     * Class for keeping track of tag references
     ***/
    private class TagRef {

        int tagType;
        int tagStart;
        int tagEnd;
        int linkStart;
        int linkEnd;
        String url;
        BigDecimal oid;
        private ContentItem m_item;

        public TagRef(int type, int start, int end, int lstart, int lend,
                      BigDecimal oid) {
            tagType = type;
            tagStart = start;
            tagEnd = end;
            linkStart = lstart;
            linkEnd = lend;
            this.oid = oid;
        }

        public void replaceReference(Writer out)
                throws IOException {
            String target = (url == null) ? NOT_FOUND[tagType] : url;

            if (linkStart == -1) {
                // There was no link attribute on the original tag. Insert one at
                // the end.
                out.write(m_content.substring(tagStart, tagEnd));
                out.write(" ");
                out.write(REF_ATTR[tagType]);
                out.write("='");
                out.write(target);
                out.write("'");
            } else {
                out.write(m_content.substring(tagStart, linkStart));
                out.write('\'');
                out.write(target);
                out.write("' ");
                if (tagEnd > linkEnd) {
                    out.write(m_content.substring(linkEnd + 1, tagEnd));
                }
            }
        }

        public ContentItem getItem() {
            if (m_item == null) {
                m_item = Utilities.getContentItemOrNull(oid);
                // FIXME: The page with editor will contain references to draft
                // items. Fixing that requires parsing the HTML of the page
                // separately. Here, we just kludge around it by getting the live
                // version of referenced items. [lutter]
                if (m_item != null) {
                    m_item = m_item.getLiveVersion();
                }
            }
            return m_item;
        }
    }
}
