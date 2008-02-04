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
package com.arsdigita.mail;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import junit.framework.TestCase;

/**
 * More Unit tests for ACS mail service. These tests probe
 * transmission of international and special characters in email. By
 * default, the SMTP protocol allows only 7-bit (128 chars) US-ASCII
 * encoding, anything beyond must be transport encoded.
 *
 * @author Stefan Deusch 
 * @author Ron Henderson 
 *
 * @version $Id: MailCharTest.java 748 2005-09-02 11:57:31Z sskracic $
 */

public class MailCharTest extends TestCase {

    public static final String versionId = "$Id: MailCharTest.java 748 2005-09-02 11:57:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // data for test cases
    final static String to    = "to@somedomain.net";
    final static String from  = "from@somedomain.net";

    // For now, test using ISO-8859-1 encoding only, others should be similar
    static String encoding = "iso-8859-1";

    // more data for test cases...
    static String text    = getContent(encoding, Mail.TEXT_PLAIN);
    static String html    = getContent(encoding, Mail.TEXT_HTML);
    static String subject = getSubject();

    public MailCharTest(String name) {
        super(name);
    }

    protected void setUp() {
        SimpleServer.startup();
    }

    /**
     * Get a string with international currencies and symbols
     */

    private static String getSymbols() {
        char[] currc = new char[32];
        for (int i=0; i<32; i++)
            currc[i] = (char)(160+i);
        return new String(currc);
    }

    /**
     * Get a string with uppercase international characters
     */

    private static String getUpperChars() {
        char[] upperc = new char[32];
        for (int i=0; i<32; i++)
            upperc[i] = (char)(192+i);
        return new String(upperc);
    }

    /**
     * Get a string with lowercase international characters
     */

    private static String getLowerChars() {
        char[] lowerc = new char[32];
        for(int i=0; i<32; i++)
            lowerc[i] =(char)(224+i);
        return new String(lowerc);
    }

    /*
     * Compose a String with special characters according to "encoding"
     *
     * @param encoding is the specified encoding
     * @param type is a flag for the desired MIME type of the content
     */

    private static String getContent (String encoding, String type) {

        String text;

        if (type.equalsIgnoreCase(Mail.TEXT_HTML)) {
            text =
                "<h3>Sample email with "+encoding+" special characters "+
                "and symbols</h3>\n" +
                "<p>\n<ol>\n"+
                "<li>currencies and symbols: "+ getSymbols() + "\n" +
                "<li>uppercase international characters: "+ getUpperChars() + "\n" +
                "<li>lowercase international characters: "+ getLowerChars() + "\n" +
                "</ol>";

        } else {
            text =
                "Sample email with "+encoding+" special characters "+
                "and symbols\n\n"+
                "currencies and symbols: "+ getSymbols() + "\n" +
                "uppercase international characters: "+ getUpperChars() + "\n" +
                "lowercase international characters: "+ getLowerChars() + "\n\n";
        }

        return text;
    }

    /**
     * compose a subject for the tests
     */

    private static String getSubject() {
        return "Test subject w/currencies and symbols: " + getSymbols();
    }

    /**
     * simple text message with character encoding set in the Mail constructor
     */

    public static void testMailChar001 () {

        try {
            Mail msg = new Mail(to, from, subject+"(test 1)", text, encoding);
            MailTest.assertSend(msg);
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }


    /**
     * variation of testMailChar001, encoding is set explicitly after constructor
     */

    public static void testMailChar002 () {
        try {
            Mail msg = new Mail(to, from, subject+"(test 2)");
            msg.setBody (text);
            msg.setEncoding(encoding);
            MailTest.assertSend(msg);
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }


    /**
     * Rich text message (HTML and plain text alternative)
     * with character encoding
     */

    public static void testMailChar003 () {
        try {
            Mail msg = new Mail(to, from, subject+"(test 3)");

            String body_txt = text;
            String body_html = html;
            msg.setBody (body_html, body_txt);
            msg.setEncoding(encoding);
            MailTest.assertSend(msg);
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }


    /**
     * attached message with international characters
     *
     * Note: the message is attached via Datahandler that writes to a
     * ByteArrayDataSource which can be set to the correct encoding.
     *
     * This is way to attach encoded documents!
     */

    public static void testMailChar004() {

        try {
            String hdoc = html;
            String type = Mail.TEXT_HTML;
            String body =
                "Attached HTML document with " + encoding +
                " special characters";

            DataHandler dataHandler = new DataHandler
                (new ByteArrayDataSource(hdoc, type, body, encoding));

            // here we attach a Mail object, but it can be any document
            Mail msg = new Mail(to, from, subject+"(test 4)");
            msg.setBody(body);
            msg.setEncoding(encoding);
            msg.attach(dataHandler);

            MailTest.assertSend(msg);

        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(MailCharTest.class);
    }

}
