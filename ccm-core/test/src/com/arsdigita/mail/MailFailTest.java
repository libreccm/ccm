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

import javax.mail.MessagingException;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * More unit test for ACS Mail to see how it behaves in case the server fails.
 *
 * SimpleServer generates error messages according to the SMTP protocol.
 * These tests demonstrate how the underlying SMTP implementation
 * of javax.mail deals with runtime exceptions from the server side.
 *
 * @author Stefan Deusch 
 * @version $Id: MailFailTest.java 749 2005-09-02 12:11:57Z sskracic $
 */

public class MailFailTest extends TestCase {

    public static final String versionId = "$Id: MailFailTest.java 749 2005-09-02 12:11:57Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static int msgNumber = 0;
    private static Logger s_log = Logger.getLogger(MailFailTest.class);

    public MailFailTest(String name) {
        super(name);
    }

    // allocate only one simple kind of mail here

    private static Mail simpleMail() {

        msgNumber++;

        String to      = "to@somedomain.net";
        String from    = "from@somedomain.net";
        String subject = "Fail Test " + msgNumber;
        String text    = "This message should not send because the Server fails.";

        return new Mail(to,from,subject,text);
    }

    protected void setUp() {
        SimpleServer.startup();
    }

    /**
     * Server in NORMAL mode
     */
    public static void testFail001 () {
        try {
            Mail msg = simpleMail();
            msg.setBody("Normal mail test");
            MailTest.assertSend(msg);
            s_log.debug("Test 1: NORMAL mode works correctly");
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Server in INTERRUPT mode
     */
    public static void testFail002() {
        SimpleServer.setMode(SimpleServer.INTERRUPT);
        try {
            Mail msg = simpleMail();
            msg.send();
            fail("send should've failed b/c server is in INTERRUPT mode");
        } catch (MessagingException e) {
            s_log.debug("Test 2: ", e);
        }
    }

    /**
     * Server in HANGING mode
     */
    public static void testFail003() {
        /*
          SimpleServer.setMode(SimpleServer.HANGING);
          s_log.debug("\n\n\n\t\tPatience, this test waits for the socket "+
          "to time out, takes 25s, ...\n\n");
          try {
          Mail msg = simpleMail();
          msg.send();
          fail("send should've timed out b/c server is in HANGING mode");
          } catch (Exception e) {
          s_log.debug("Test 3: ", e);
          }
        */
    }

    /**
     * Server in UNAVAILABLE mode
     */
    public static void testFail004() {
        SimpleServer.setMode(SimpleServer.UNAVAILABLE);
        try {
            Mail msg = simpleMail();
            msg.send();
            fail("send should have failed b/c server is in UNAVAILABLE mode");
        } catch (MessagingException e) {
            s_log.debug("Test 4: ",e);
        }
    }


    /**
     * Server in TRANSACTIONABORT mode
     */
    public static void testFail005() {
        SimpleServer.setMode(SimpleServer.TRANSACTIONABORT);
        try {
            Mail msg = simpleMail();
            msg.send();
            fail("send should have failed b/c server is in TRANSACTIONABORT mode");
        } catch (MessagingException e) {
            s_log.debug("Test 6: ",e);
        }
    }

    /**
     * Server in INSUFFICIENTMEM mode
     */
    public static void testFail006() {
        SimpleServer.setMode(SimpleServer.INSUFFICIENTMEM);
        Mail msg;
        try {
            msg = simpleMail();
            msg.send();
            fail("send should've failed b/c server is in INSUFFICIENTMEM mode");
        } catch (MessagingException e) {
            s_log.debug("Test 7: ",e);
            msg = null;
        }
    }

    /**
     * Server in UNRECOGNIZEDCMD mode
     */
    public static void testFail007() {
        SimpleServer.setMode(SimpleServer.UNRECOGNIZEDCMD);
        try {
            Mail msg = simpleMail();
            msg.send();
            fail("send should've failed b/c server is in UNRECOGNIZED mode");
        } catch (MessagingException e) {
            s_log.debug("Test 8: ",e);
        }
    }

    /**
     * Server in SYNTAXERROR mode
     */
    public static void testFail008() {
        SimpleServer.setMode(SimpleServer.SYNTAXERROR);
        try {
            Mail msg = simpleMail();
            msg.send();
            fail("send should've failed b/c server is in SYNTAXERROR mode");
        } catch (MessagingException e) {
            s_log.debug("Test 9: ",e);
        }
    }

    /**
     * Server in UNSUPPORTEDMETH mode
     */
    public static void testFail009() {
        SimpleServer.setMode(SimpleServer.UNSUPPORTEDMETH);
        try {
            Mail msg = simpleMail();
            msg.send();
            fail("send should've failed b/c server is in UNSUPPORTEDMETH mode");
        } catch (MessagingException e) {
            s_log.debug("Test 10: ",e);
        }
    }

    /**
     * Server in TRANSACTIONFAILED mode
     */

    public static void testFail010() {
        SimpleServer.setMode(SimpleServer.TRANSACTIONFAILED );
        try {
            Mail msg = simpleMail();
            msg.send();
            fail("send should've failed b/c server is in TRANSACTIONFAILED mode ");
        } catch (MessagingException e) {
            s_log.debug("Test 12: ",e);
        }
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(MailFailTest.class);
    }

}
