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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.ui.login.PasswordValidationListener;
import com.arsdigita.util.StringUtils;

import java.math.BigDecimal;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

/**
 * Generic form for adding and editting user.
 *
 * @author David Dao
 * @version $Id: UserForm.java 1508 2007-03-22 00:04:22Z apevec $
 */
class UserForm extends Form implements FormValidationListener, AdminConstants {

    protected TextField m_firstName;
    protected TextField m_lastName;
    protected TextField m_primaryEmail;
    protected TextField m_additionalEmail;
    protected Password m_password;
    protected Password m_confirmPassword;
    protected TextField m_question;
    protected TextField m_answer;
    protected TextField m_url;
    protected TextField m_screenName;
    protected EmailList m_emailList;
    protected TextField m_ssoLogin;
    private final PasswordValidationListener m_pwListener;
    private final NotEmptyValidationListener m_notNullListener;
    private final SecurityConfig securityConfig = SecurityConfig.getConfig();

    public UserForm(String formName) {
        super(formName);

        m_pwListener = new PasswordValidationListener();
        m_notNullListener = new NotEmptyValidationListener();

        addValidationListener(this);

        // Bug #163373 add length checking for first/last names.  We
        // do this with both maximum length parameters in the user/add
        // form and with validation of the value that come in for
        // processing.
        int max = 60;

        m_firstName = new TextField(
            new StringParameter(USER_FORM_INPUT_FIRST_NAME));
        m_firstName.setMaxLength(max);
        m_firstName.setSize(20);
        m_firstName.addValidationListener(new NotEmptyValidationListener());
        m_firstName.addValidationListener(
            new StringLengthValidationListener(max));

        add(USER_FORM_LABEL_FIRST_NAME);
        add(m_firstName);

        m_lastName = new TextField(
            new StringParameter(USER_FORM_INPUT_LAST_NAME));
        m_lastName.setMaxLength(max);
        m_lastName.setSize(25);
        m_lastName.addValidationListener(new NotEmptyValidationListener());
        m_lastName
            .addValidationListener(new StringLengthValidationListener(max));

        add(USER_FORM_LABEL_LAST_NAME);
        add(m_lastName);

        // Password
        m_password = new Password(new StringParameter(USER_FORM_INPUT_PASSWORD));
        add(USER_FORM_LABEL_PASSWORD);
        add(m_password);

        // Password confirmation
        m_confirmPassword = new Password(new StringParameter(
            USER_FORM_INPUT_PASSWORD_CONFIRMATION));

        add(USER_FORM_LABEL_PASSWORD_CONFIRMATION);
        add(m_confirmPassword);

        // Password question
        m_question
        = new TextField(new StringParameter(USER_FORM_INPUT_QUESTION));
        m_question.setSize(50);

        if (securityConfig.getEnableQuestion()) {
            add(USER_FORM_LABEL_QUESTION);
            add(m_question);
        }

        // Password answer
        m_answer = new TextField(new StringParameter(USER_FORM_INPUT_ANSWER));
        m_answer.setSize(50);

        if (securityConfig.getEnableQuestion()) {
            add(USER_FORM_LABEL_ANSWER);
            add(m_answer);
        }

        // Primary email address
        m_primaryEmail = new TextField(new EmailParameter(
            USER_FORM_INPUT_PRIMARY_EMAIL));
        m_primaryEmail.addValidationListener(new NotEmptyValidationListener());
        m_primaryEmail.setSize(50);
        add(USER_FORM_LABEL_PRIMARY_EMAIL);
        add(m_primaryEmail);

        // Additional email addresses
        m_emailList = new EmailList();
        add(USER_FORM_LABEL_ADDITIONAL_EMAIL_LIST);
        add(m_emailList);

        m_additionalEmail = new TextField(new EmailParameter(
            USER_FORM_INPUT_ADDITIONAL_EMAIL));
        m_additionalEmail.setSize(50);
        add(USER_FORM_LABEL_ADDITIONAL_EMAIL);
        add(m_additionalEmail);

        // Screen name
        m_screenName = new TextField(new StringParameter(
            USER_FORM_INPUT_SCREEN_NAME));
        if (Kernel.getConfig().screenNameIsPrimaryIdentifier()) {
            m_screenName.addValidationListener(new NotEmptyValidationListener());
        }
        add(USER_FORM_LABEL_SCREEN_NAME);
        add(m_screenName);

        m_ssoLogin = new TextField(new StringParameter(USER_FORM_INPUT_SSO));
        m_ssoLogin.setSize(50);
        add(USER_FORM_LABEL_SSO);
        add(m_ssoLogin);
        
        // URL
        m_url = new TextField(new URLParameter(USER_FORM_INPUT_URL));
        m_url.setSize(50);
        m_url.setDefaultValue(USER_FORM_INPUT_URL_DEFAULT);
        m_url.addValidationListener(new URLValidationListener());
        add(USER_FORM_LABEL_URL);
        add(m_url);

        // Submit
        add(new Label(""));
        add(new Submit(USER_FORM_SUBMIT));
    }

    /**
     * Validate the form. Verifies that the password and password-confirm fields
     * match. If not it adds an error to the password-confirm field. Also
     * verifies that primary email address and screen name are unique amoung all
     * users.
     */
    @Override
    public void validate(FormSectionEvent event)
        throws FormProcessException {
        PageState ps = event.getPageState();
        FormData data = event.getFormData();
        HttpServletRequest req = ps.getRequest();

        // UserID will be null if this is an add form.
        BigDecimal userID = (BigDecimal) ps.getValue(USER_ID_PARAM);

        /**
         * Verify that password and confirmation match.
         */
        if (userID == null) {

            m_pwListener.validate(
                new ParameterEvent(event.getSource(),
                                   data.getParameter(
                                       USER_FORM_INPUT_PASSWORD)));
            m_notNullListener.validate(
                new ParameterEvent(event.getSource(),
                                   data.getParameter(
                                       USER_FORM_INPUT_PASSWORD_CONFIRMATION)));
            String password = (String) m_password.getValue(ps);
            String confirm = (String) m_confirmPassword.getValue(ps);

            if (!StringUtils.emptyString(password) && !StringUtils.emptyString(
                confirm)) {
                if (!password.equals(confirm)) {
                    data.addError(USER_FORM_INPUT_PASSWORD_CONFIRMATION,
                                  (String) USER_FORM_ERROR_PASSWORD_NOT_MATCH.
                                      localize(req));
                }
            }
        }

        if (securityConfig.getEnableQuestion()) {
            // If the password answer is anything but null, make sure it
            // contains some non-whitespace characters
            String answer = (String) m_answer.getValue(ps);
            if (userID == null) {
                // Check for add form.
                if (answer == null || answer.trim().length() == 0) {
                    data.addError(USER_FORM_INPUT_ANSWER,
                                  (String) USER_FORM_ERROR_ANSWER_NULL.localize(
                                      req));
                }
            } else {
                // Check for edit form
                if (answer != null && answer.length() > 0 && answer.trim().
                    length()
                                                                 == 0) {
                    data.addError(USER_FORM_INPUT_ANSWER,
                                  (String) USER_FORM_ERROR_ANSWER_NULL.localize(
                                      req));
                }
            }
        }

        /**
         * Verify that primary email and screen name are unique
         */
        DataQuery query = SessionManager.getSession().retrieveQuery(
            "com.arsdigita.kernel.RetrieveUsers");
        query.setParameter("excludeGroupId", new BigDecimal(0));

        String email = null;
        if (m_primaryEmail.getValue(ps) != null) {
            email = ((InternetAddress) m_primaryEmail.getValue(ps)).getAddress();
        }

        String screenName = (String) m_screenName.getValue(ps);

        Filter filter = query.addFilter(
            "primaryEmail = :email or screenName = :sn");
        filter.set("email", email);
        filter.set("sn", screenName);

        if (userID != null) {
            Filter userIDfilter = query.addFilter("userID != :userID");
            userIDfilter.set("userID", userID);
        }

        /**
         * If this query returns with any rows we have a duplicate screen name,
         * email address, or both. Check the results and produce appropriate
         * error messages.
         */
        while (query.next()) {
            if (screenName != null && screenName.equals(query.get("screenName"))) {
                data.addError(USER_FORM_INPUT_SCREEN_NAME,
                              (String) USER_FORM_ERROR_SCREEN_NAME_NOT_UNIQUE.
                                  localize(req));
            }

            if (email != null && email.equals(query.get("primaryEmail"))) {
                data.addError(USER_FORM_INPUT_PRIMARY_EMAIL,
                              (String) USER_FORM_ERROR_PRIMARY_EMAIL_NOT_UNIQUE.
                                  localize(req));

            }
        }

    }

    /**
     * Hide all security-related components
     */
    protected void hideSecurityInfo(PageState state) {
        setSecurityInfo(state, false);
    }

    /**
     * Show all security-related components
     */
    protected void showSecurityInfo(PageState state) {
        setSecurityInfo(state, true);
    }

    private void setSecurityInfo(PageState state, boolean isVisible) {

        USER_FORM_LABEL_PASSWORD.setVisible(state, isVisible);
        USER_FORM_LABEL_PASSWORD_CONFIRMATION.setVisible(state, isVisible);
        if (securityConfig.getEnableQuestion()) {
            USER_FORM_LABEL_QUESTION.setVisible(state, isVisible);
            USER_FORM_LABEL_ANSWER.setVisible(state, isVisible);
        }

        m_password.setVisible(state, isVisible);
        m_confirmPassword.setVisible(state, isVisible);
        if (securityConfig.getEnableQuestion()) {
            m_question.setVisible(state, isVisible);
            m_answer.setVisible(state, isVisible);
        }
    }

}
