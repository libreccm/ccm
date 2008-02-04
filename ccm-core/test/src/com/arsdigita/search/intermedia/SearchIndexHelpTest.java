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

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TestTransaction;
import com.arsdigita.persistence.TransactionContext;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * Tests for SearchIndexHelpTest Class, used in search.
 *
 *
 * @author Jeff Teeters
 * @version 1.0
 **/
public class SearchIndexHelpTest extends TestCase {
    public static final String versionId = "$Id: SearchIndexHelpTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // Creates a s_logging category with name = to the full name of class
    private static Logger s_log =
        Logger.getLogger( SearchContent.class.getName() );

    private Session m_ssn;
    private TransactionContext m_txn;


    /**
     * Constructs a SearchIndexHelpTest with the specified name.
     *
     * @param name Test case name.
     **/
    public SearchIndexHelpTest( String name ) {
        super( name );
    }

    public static boolean initialized = false;

    public void setUp() {
        /* Register instantiators with DomainObjectFactory */
        // Should go in initializer
        SearchTestBook.setupDomainObjectFactory();
        SearchTestAuthor.setupDomainObjectFactory();
        SearchTestChapter.setupDomainObjectFactory();
        SearchTestBookH.setupDomainObjectFactory();
        SearchTestChapterH.setupDomainObjectFactory();
        m_ssn = SessionManager.getSession();
        m_txn = m_ssn.getTransactionContext();
        m_txn.beginTxn();
    }


    public void tearDown() {
        m_txn.abortTxn();
    }

    private void assertNotEquals(Object o1, Object o2) {
        assertTrue(o1 + " notequals " + o2, !(o1.equals(o2)));
    }


    private void printColl(Collection coll) {
        Iterator iter = coll.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
        }
    }


    // Used to log content returned to file.  See example call
    private void logContent (String name, SearchIndexHelp sh) {
        s_log.debug("===== " + name + " XML=" + sh.xmlContent()
                    + "\n   RAW=" + sh.rawContent());
    }


    /***
     * verify that xml and raw content retrieved by
     * ShearchIndexHelp matches what is expected.
     ***/
    private void checkSh(SearchIndexHelp sh,
                         String objectName,
                         String expected_xml,
                         String expected_raw,
                         String expected_raw_alt) {
        String found_xml = sh.xmlContent();
        // put returned xml in a known structure
        found_xml = sortXML(found_xml);
        assertTrue("SearchIndexHelp, obj=" + objectName +
                   ", XML returned.  \nExpected = "
                   + expected_xml + "\nFound=" + found_xml,
                   found_xml.equals(expected_xml));
        String found_raw = new String(sh.rawContent());
        assertTrue("SearchIndexHelp, obj=" + objectName +
                   ", RAW returned.  \nExpected = "
                   + expected_raw + "\nFound=" + found_raw,
                   (found_raw.equals(expected_raw) ||
                    found_raw.equals(expected_raw_alt)));
    }


    /**
     * TEST 2.
     * Verify that SearchIndexHelp works with composite
     * objects.  (Book and chapters).
     **/
    public void testComposite() throws Exception {
        final String TITLE = "Hello world.";
        final Integer CHAP5_NUM = new Integer(5);
        final String CHAP5_CONTENT = "Life is beautiful.";
        final Integer CHAP6_NUM = new Integer(6);
        final String CHAP6_CONTENT = "Happiness abounds.";
        final String CHAP_DISPLAY_NAME = "SearchTestChapter-DisplayName";
        final String BOOK_DISPLAY_NAME = "SearchTestBook-DisplayName";
        String expected_xml, expected_raw;

        SearchTestBook b = new SearchTestBook();
        b.setTitle(TITLE);
        SearchTestChapter c5 = new SearchTestChapter();
        c5.setChapterNum(CHAP5_NUM);
        c5.setContent(CHAP5_CONTENT);
        SearchTestChapter c6 = new SearchTestChapter();
        c6.setChapterNum(CHAP6_NUM);
        c6.setContent(CHAP6_CONTENT);

        // Make an association between the chapter and the book
        b.addChapter(c5);
        b.addChapter(c6);
        b.save();

        //****
        // Test using SearchIndexHelp to get content to index.
        //****

        // SearchIndexHelpCustomize is not used, so only xml
        // should be returned.
        SearchIndexHelp sh = new SearchIndexHelp();

        sh.retrieveContent(c5);
        // logContent ("ch5", sh);  // to log retrieved content
        String expected_c5_xml =
            "<SearchTestChapter>" +
            "<content>" + CHAP5_CONTENT + "</content>" +
            "<displayName>" + CHAP_DISPLAY_NAME + "</displayName>" +
            "</SearchTestChapter>";
        expected_raw = "";
        checkSh(sh,"c5", expected_c5_xml, expected_raw, "");

        sh.retrieveContent(c6);
        String expected_c6_xml =
            "<SearchTestChapter>" +
            "<content>" + CHAP6_CONTENT + "</content>" +
            "<displayName>" + CHAP_DISPLAY_NAME + "</displayName>" +
            "</SearchTestChapter>";
        expected_raw = "";
        checkSh(sh,"c6", expected_c6_xml, expected_raw, "");

        sh.retrieveContent(b);
        String expected_b_xml = "<SearchTestBook>" +
            expected_c6_xml + expected_c5_xml +
            "<displayName>" + BOOK_DISPLAY_NAME + "</displayName>" +
            "<title>" + TITLE + "</title>" +
            "</SearchTestBook>";
        expected_raw = "";
        checkSh(sh,"b", expected_b_xml, expected_raw, "");
    }

    /**
     * TEST 3.
     * Test SearchIndexHelpCustomize interface
     * (Allows developer to specify how fields are
     * indexed.
     **/
    public void testCustomize() throws Exception {
        final String TITLE = "Hello world.";
        final Integer CHAP5_NUM = new Integer(5);
        final String CHAP5_CONTENT = "Life is beautiful.";
        final Integer CHAP6_NUM = new Integer(6);
        final String CHAP6_CONTENT = "Happiness abounds.";
        final String BOOK_H_DISPLAY_NAME = "SearchTestBookH-DisplayName";

        String expected_raw, expected_raw_alt;
        SearchIndexHelp sh = new SearchIndexHelp();
        SearchTestBookH bh = new SearchTestBookH();
        bh.setTitle(TITLE);
        SearchTestChapterH ch5 = new SearchTestChapterH();
        ch5.setChapterNum(CHAP5_NUM);
        ch5.setContent(CHAP5_CONTENT);
        SearchTestChapterH ch6 = new SearchTestChapterH();
        ch6.setChapterNum(CHAP6_NUM);
        ch6.setContent(CHAP6_CONTENT);
        // Make an association between the chapter and the book
        bh.addChapter(ch5);
        bh.addChapter(ch6);
        bh.save();

        sh.retrieveContent(ch5);
        String expected_ch5_xml =
            "<SearchTestChapterH>" +
            "<chapterNum>5</chapterNum>" +
            "</SearchTestChapterH>";
        expected_raw = CHAP5_CONTENT;
        checkSh(sh,"ch5", expected_ch5_xml, expected_raw, "");

        sh.retrieveContent(ch6);
        String expected_ch6_xml =
            "<SearchTestChapterH>" +
            "<chapterNum>6</chapterNum>" +
            "</SearchTestChapterH>";
        expected_raw = CHAP6_CONTENT;
        checkSh(sh,"ch6", expected_ch6_xml, expected_raw, "");

        sh.retrieveContent(bh);
        String expected_bh_xml = "<SearchTestBookH>" +
            expected_ch5_xml + expected_ch6_xml +
            "<displayName>" + BOOK_H_DISPLAY_NAME + "</displayName>" +
            "<title>" + TITLE + "</title>" +
            "</SearchTestBookH>";
        expected_raw = CHAP5_CONTENT + " " + CHAP6_CONTENT;
        expected_raw_alt = CHAP6_CONTENT + " " + CHAP5_CONTENT;
        checkSh(sh,"bh", expected_bh_xml, expected_raw, expected_raw_alt);
    }

    /**
     * TEST 4.
     * Test non-composite association.  Make sure only
     * Link data is indexed.
     **/
    public void testNonComposite() throws Exception {
        final String TITLE = "Hello world.";
        final Integer CHAP5_NUM = new Integer(5);
        final String CHAP5_CONTENT = "Life is beautiful.";
        final Integer CHAP6_NUM = new Integer(6);
        final String CHAP6_CONTENT = "Happiness abounds.";
        final String AUTHOR5a = "Ernest Hemmingway";
        final String AUTHOR5b = "Robert Frost";
        final String AUTHOR6 = "Sandy Beach";
        final String BOOK_DISPLAY_NAME = "SearchTestBook-DisplayName";
        final String CHAP_DISPLAY_NAME = "SearchTestChapter-DisplayName";

        String expected_xml, expected_raw;

        SearchIndexHelp sh = new SearchIndexHelp();
        SearchTestBook b = new SearchTestBook();
        b.setTitle(TITLE);
        SearchTestChapter c5 = new SearchTestChapter();
        c5.setChapterNum(CHAP5_NUM);
        c5.setContent(CHAP5_CONTENT);
        SearchTestChapter c6 = new SearchTestChapter();
        c6.setChapterNum(CHAP6_NUM);
        c6.setContent(CHAP6_CONTENT);
        // Make an association between the chapter and the book
        b.addChapter(c5);
        b.addChapter(c6);
        // Create the authors
        SearchTestAuthor a5a = new SearchTestAuthor();
        a5a.setName(AUTHOR5a);
        SearchTestAuthor a5b = new SearchTestAuthor();
        a5b.setName(AUTHOR5b);
        SearchTestAuthor a6 = new SearchTestAuthor();
        a6.setName(AUTHOR6);
        // Make associations between chapters and authors
        a5a.addChapter(c5);
        a5b.addChapter(c5);
        c6.addAuthor(a6);
        // save everything
        c5.save();
        c6.save();
        a5a.save();
        a5b.save();
        a6.save();
        b.save();

        sh.retrieveContent(c5);
        // logContent ("ch5", sh);  // to log retrieved content
        String expected_c5_xml =
            "<SearchTestChapter>" +
            "<content>" + CHAP5_CONTENT + "</content>" +
            "<displayName>" + CHAP_DISPLAY_NAME + "</displayName>" +
            "</SearchTestChapter>";
        expected_raw = "";
        checkSh(sh,"c5", expected_c5_xml, expected_raw, "");

        sh.retrieveContent(c6);
        String expected_c6_xml =
            "<SearchTestChapter>" +
            "<content>" + CHAP6_CONTENT + "</content>" +
            "<displayName>" + CHAP_DISPLAY_NAME + "</displayName>" +
            "</SearchTestChapter>";
        expected_raw = "";
        checkSh(sh,"c6", expected_c6_xml, expected_raw, "");

        sh.retrieveContent(b);
        String expected_b_xml = "<SearchTestBook>" +
            expected_c6_xml + expected_c5_xml +
            "<displayName>" + BOOK_DISPLAY_NAME + "</displayName>" +
            "<title>" + TITLE + "</title>" +
            "</SearchTestBook>";
        expected_raw = "";
        checkSh(sh,"b", expected_b_xml, expected_raw, "");
    }


    /**
     * TEST 5.
     * Make sure SearchableObserver is using
     * SearchIndexHelp to get content to index.
     **/
    public void testIndexHelpCall() throws Exception {
        final String TITLE = "Hello world.";
        final Integer CHAP5_NUM = new Integer(5);
        final String CHAP5_CONTENT = "Life is beautiful.";
        final Integer CHAP6_NUM = new Integer(6);
        final String CHAP6_CONTENT = "Happiness abounds.";
        final String BOOK_H_DISPLAY_NAME = "SearchTestBookH-DisplayName";

        SearchIndexHelp sh = new SearchIndexHelp();
        SearchTestBookH bh = new SearchTestBookH();
        bh.setTitle(TITLE);
        SearchTestChapterH ch5 = new SearchTestChapterH();
        ch5.setChapterNum(CHAP5_NUM);
        ch5.setContent(CHAP5_CONTENT);
        SearchTestChapterH ch6 = new SearchTestChapterH();
        ch6.setChapterNum(CHAP6_NUM);
        ch6.setContent(CHAP6_CONTENT);
        // Make an association between the chapter and the book
        bh.addChapter(ch5);
        bh.addChapter(ch6);
        bh.save();

        // fake a commit so search content gets saved
        TestTransaction.testCommitTxn(m_txn);

        // Look at the searchContentTable
        BigDecimal bh_id = bh.getID();
        OID sc_oid = new OID("com.arsdigita.search.intermedia.SearchContent", bh_id);
        SearchContent sc = new SearchContent(sc_oid);
        String sc_xmlContent = sc.getXMLContent();
        byte[] sc_rawContent = sc.getRawContent();

        String found_xml = sc_xmlContent;
        found_xml = sortXML(found_xml);  // Put in known order
        String found_raw = new String(sc_rawContent);
        String expected_ch5_xml =
            "<SearchTestChapterH>" +
            "<chapterNum>5</chapterNum>" +
            "</SearchTestChapterH>";
        String expected_ch6_xml =
            "<SearchTestChapterH>" +
            "<chapterNum>6</chapterNum>" +
            "</SearchTestChapterH>";
        String expected_bh_xml = "<SearchTestBookH>" +
            expected_ch5_xml + expected_ch6_xml +
            "<displayName>" + BOOK_H_DISPLAY_NAME + "</displayName>" +
            "<title>" + TITLE + "</title>" +
            "</SearchTestBookH>";
        String expected_raw = CHAP5_CONTENT + " " + CHAP6_CONTENT;
        String expected_raw_alt = CHAP6_CONTENT + " " + CHAP5_CONTENT;

        assertTrue("SearchIndexHelp, saving in search_content table. " +
                   "XML.  \nExpected = "
                   + expected_bh_xml + "\nFound=" + found_xml,
                   found_xml.equals(expected_bh_xml));
        assertTrue("SearchIndexHelp, saving in search_content table. " +
                   ", RAW.  \nExpected = "
                   + expected_raw + "\nFound=" + found_raw,
                   found_raw.equals(expected_raw) ||
                   found_raw.equals(expected_raw_alt));
    }


    /***
     * Find a tag in a string, or return null if no tag found.
     ***/
    private String findTag(String xml, int startPos) {
        // require that always start with a tag
        // otherwise assume is no element
        if (xml.charAt(startPos) != '<')
            return null;
        int end_p = xml.indexOf('>', startPos);
        if (end_p == -1)
            return null;
        return xml.substring(startPos + 1, end_p);
    }


    /***
     * sortXML - rearrange the elements in an xml document so that they
     *    occur in a sorted order.  This is done on the xml documents
     *    returned from the SearchIndexHelp methods so they each can be compared
     *    to a known expected value.  Rearranging to a known order is
     *    necessary because the structure of the returned document is
     *    unpredictable. This method is recursive (works with nested elements).
     *  Example:
     *    input: "<pet>dog</pet><eat>vegi</eat><age>34</age><pet>cat</pet>";
     *    output:"<age>34</age><eat>vegi</eat><pet>cat</pet><pet>dog</pet>";
     *    See the testSortXML method for more examples.
     ***/

    private String sortXML(String xml) {
        xml = stripLineBreaks(xml);
        TreeSet elist = new TreeSet();
        int startPos = 0;
        String tag, content;
        while (startPos < xml.length()) {
            // Locate opening tag
            tag = findTag(xml, startPos);
            if (tag == null)
                return xml;    // return string if no element
            int begin_tag_loc = xml.indexOf("<"+tag+">", startPos);
            int end_tag_loc = xml.indexOf("</"+tag+">", startPos);
            if (begin_tag_loc == -1 || end_tag_loc == -1) {
                break;  // Tags not found
            }
            content = xml.substring(begin_tag_loc +
                                    tag.length() + 2, end_tag_loc);
            content = sortXML(content);  // The recursion
            elist.add(tag + "<" + content);
            startPos = end_tag_loc + tag.length() + 3; // </tag>
        }
        StringBuffer b = new StringBuffer();
        while (elist.size() > 0) {
            String elem = (String) elist.first();
            int i = elem.indexOf('<');
            tag = elem.substring(0,i);
            content = elem.substring(i+1);
            b.append("<" + tag + ">" + content + "</" + tag + ">");
            elist.remove(elem);
        }
        return b.toString();
    }


    /***
     * Strip line breaks from a string.  Used to make sure
     * returned xml is of expected format.
     ***/
    private String stripLineBreaks(String s) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isISOControl(c))
                b.append(c);
        }
        return new String(b);
    }


    public void testSortXML() throws Exception {
        String testXML = "<pet>dog</pet><eat>vegi</eat>" +
            "<age>34</age><pet>cat</pet>";
        String expectXML = "<age>34</age><eat>vegi</eat>" +
            "<pet>cat</pet><pet>dog</pet>";
        String foundXML = sortXML(testXML);
        assertEquals("Expected=" + expectXML + "\nfound="+foundXML,
                     expectXML, foundXML);

        // Test if works when no element defined.
        testXML = "no element here";
        foundXML = sortXML(testXML);
        assertEquals("Expected=" + testXML + "\nfound="+foundXML,
                     testXML, foundXML);

        // Test if works with nested elements
        testXML = "<info><pet><size>large</size><name>dog</name></pet>" +
            "<eat>vegi</eat><age>34</age>" +
            "<pet><size>small</size><name>cat</name></pet></info>";

        expectXML = "<info><age>34</age><eat>vegi</eat>" +
            "<pet><name>cat</name><size>small</size></pet>" +
            "<pet><name>dog</name><size>large</size></pet></info>";
        foundXML = sortXML(testXML);

        assertEquals("Expected=" + expectXML + "\nfound="+foundXML,
                     expectXML, foundXML);
    }


    public static Test suite() {
        //
        // Reflection is used here to add all
        // the testBLAH() methods to the suite.
        //
        return new TestSuite(SearchIndexHelpTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

}
