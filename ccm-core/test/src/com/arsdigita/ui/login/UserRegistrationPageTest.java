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
package com.arsdigita.ui.login;

import com.arsdigita.db.Sequences;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.test.HttpUnitTestCase;
import com.arsdigita.ui.login.UserNewForm;
import com.arsdigita.ui.login.UserRegistrationForm;
import com.arsdigita.web.Web;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import java.math.BigDecimal;
import java.sql.SQLException;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * Tests basic functionality of /register pages
 *
 *
 * @author Roger Hsueh
 * @version $Id: UserRegistrationPageTest.java 750 2005-09-02 12:38:44Z sskracic $
 *
 **/
public class UserRegistrationPageTest extends HttpUnitTestCase
    implements LoginConstants {

    public static final String versionId = "$Id: UserRegistrationPageTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private Session m_ssn;
    private TransactionContext m_txn;

    private String m_existingPassword;
    private String m_existingEmail;
    private String m_webHostname;
    private String m_registrationURL;

    private WebForm loginForm;

    private static Logger s_log =
        Logger.getLogger(UserRegistrationPageTest.class.getName());

    /**
     * Constructs a UserRegistrationPageTest with the specified name.
     *
     * @param name Testcase name.
     **/
    public UserRegistrationPageTest( String name ) {
        super( name );
    }

    private void setupTestUser(String email, String password)
        throws PersistenceException {
        try {
            UserAuthentication auth =
                UserAuthentication.retrieveForLoginName(email);
            auth.setPassword(password);
            auth.save();
        } catch (Exception e) {
            // couldn't retrieve old user, create a new one.
            System.out.println("creating new user");
            User user = new User();
            user.getPersonName().setGivenName("blah");
            user.getPersonName().setFamilyName("blah");
            user.setPrimaryEmail(new EmailAddress(email));
            user.save();
            UserAuthentication auth = UserAuthentication.createForUser(user);
            auth.setPassword(password);
            auth.setPasswordQuestion("blahquestion");
            auth.setPasswordAnswer("blahanswer");
            auth.save();
            // commit the creation of the new user here
            m_txn.commitTxn();
            // new trnasaction context, to be committed by teardown()
            m_ssn = SessionManager.getSession();
            m_txn = m_ssn.getTransactionContext();
            m_txn.beginTxn();
        }
    }

    /**
     * setup the login form to test.
     * setup db connection and default login and password to test
     *
     **/
    public void setUp()  {
        try {
            m_ssn = SessionManager.getSession();
            m_txn = m_ssn.getTransactionContext();
            m_txn.beginTxn();

            m_webHostname = System.getProperty("test.server.url");
            // HttpUnitTest automatically prepends your test.server.url onto
            // the url here
            
            m_registrationURL = "/register";
            loginForm = getFormWithName(m_registrationURL,
                                        UserRegistrationForm.FORM_NAME);
            m_existingEmail = "10@10.com";
            m_existingPassword = "10";
            // setup the test user in db if necessary
            setupTestUser(m_existingEmail,m_existingPassword);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.getMessage()
                 + "\n ps: make sure your test server is running");
        }
    }

    public void tearDown() {
        try {
            if (m_txn.inTxn()) {
                m_txn.commitTxn();
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /*
     * Helper method to generate a new login
     **/
    private String getNewLogin() throws SQLException {
        BigDecimal idval = Sequences.getNextValue();
        return idval + "@" + idval + ".com";
    }

    /**
     * Tests
     **/
    public void testExistingUserLogin() throws Exception {
        assertEquals("loginForm.getName() != \"user-login\"",
                     UserRegistrationForm.FORM_NAME,
                     loginForm.getName());
        //verify default parameters
        assertEquals("default email param should be empty","",
                     loginForm.getParameterValue
                     (FORM_EMAIL));
        assertEquals("default password param should be empty","",
                     loginForm.getParameterValue
                     (FORM_PASSWORD));
        assertEquals("default return_url param should be empty","",
                     loginForm.getParameterValue
                     (UserContext.RETURN_URL_PARAM_NAME));
        assertEquals("default persistent_cookie_p param should be 1","1",
                     loginForm.getParameterValue
                     (FORM_PERSISTENT_LOGIN_P));

        WebRequest request = loginForm.getRequest();
        request.setParameter(FORM_EMAIL,
                             m_existingEmail);
        request.setParameter(FORM_PASSWORD,
                             m_existingPassword);

        WebResponse response = submit(request);
        assertEquals("response code: ", 200, response.getResponseCode());
        String correctResponseURL = getPersonalWkspUrl();
        final String responseURL = response.getURL().toString();
        if (responseURL.startsWith(correctResponseURL) == false) {
            fail("Incorrect redirect after login to: " + responseURL + " Instead of starting with: " + correctResponseURL );
        }
    }

    private String getPersonalWkspUrl() {
        String hostname = m_webHostname;
        if (hostname.endsWith("/")) {
            hostname = hostname.substring(0, hostname.length()-1);
        }
        final String personalURL = hostname + Web.getConfig().getDispatcherServletPath() + "/pvt/";

        return personalURL;
    }

    public void testNewUserCreation() throws Exception {
        // fake info
        String newbieEmail = getNewLogin();
        String newbiePassword = newbieEmail + "password";
        String newbieFirstName = newbieEmail + "firstName";
        String newbieLastName = newbieEmail + "lastName";
        String newbieQuestion = newbieEmail + "question";
        String newbieAnswer = newbieEmail + "Answer";

        assertEquals(UserRegistrationForm.FORM_NAME,
                     loginForm.getName());
        // first try to login with an email address that's not in db
        WebRequest request = loginForm.getRequest();
        request.setParameter(FORM_EMAIL,
                             newbieEmail);
        request.setParameter(FORM_PASSWORD,
                             newbiePassword);
        WebResponse response = submit(request);
        WebForm newUserForm  = response.getFormWithName(UserNewForm.FORM_NAME);
        request = newUserForm.getRequest();
        request.setParameter(FORM_PASSWORD,
                             newbiePassword);
        request.setParameter(FORM_PASSWORD_CONFIRMATION,
                             newbiePassword);
        request.setParameter(FORM_FIRST_NAME,
                             newbieFirstName);
        request.setParameter(FORM_LAST_NAME,
                             newbieLastName);
        request.setParameter(FORM_PASSWORD_QUESTION,
                             newbieQuestion);
        request.setParameter(FORM_PASSWORD_ANSWER,
                             newbieAnswer);
        response = submit(request);
        String correctResponseURL = getPersonalWkspUrl();
        assertTrue("redirected URL after successful login:",
                   response.getURL().toString().startsWith(correctResponseURL));
    }

    /**
     * test to make sure that we catch notnull parameters submitted empty.
     **/
    public void testEmptyParameters() throws Exception {
        // these are the parameters that shouldn't be empty.
        String[] notNullParams = {FORM_EMAIL,
                                  FORM_PASSWORD};
        WebRequest request = loginForm.getRequest();
        for (int i = 0;i < notNullParams.length; i++) {
            request.setParameter(notNullParams[i],"");
        }
        WebResponse response = submit(request);
        // now verify results; make sure we didn't get redirected elsewhere
        assertTrue("No redirection should take place if empty value(s) "
                   +"submitted for not-null params",
                   response.getText().indexOf("parameter is required") != -1);
        //XXX: further checks necessary?
    }
    /**
     * test email parameter to make sure it validates email input
     **/
    public void testValidEmailParam() throws Exception {
        // these are some of the bad inputs that the validator should catch
        String[] emailPatterns = {"blah@blah","blah blah","blah@ blah.com",
                                  "blah@@blah.com","blah@blah..com",
                                  "@blah.com","blah@"};
        for (int i = 0; i < emailPatterns.length; i++) {
            assertEquals("email pattern: " + emailPatterns[i]
                         + " shouldn't be valid",
                         true,testEmailPattern(emailPatterns[i]));
        }
    }
    /**
     * helper for testValidEmailParam
     **/
    private boolean testEmailPattern(String pattern) throws Exception {
        WebRequest request = loginForm.getRequest();
        // try to input this pattern into email parameter
        request.setParameter(FORM_EMAIL, pattern);
        WebResponse response = submit(request);
        return (response.getText().indexOf("valid email") != -1);
    }

    public static Test suite() {
        // Reflection is used here to add all
        // the testXXX() methods to the suite.
        return new TestSuite(UserRegistrationPageTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
