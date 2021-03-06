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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.log4j.Logger;

/**
 * Common code for user new / add / edit forms.
 *
 * @author Admin UI Team
 * @version $Id: UserForm.java 1506 2007-03-21 19:05:47Z sskracic $
 *
 */
public abstract class UserForm extends Form
    implements LoginConstants, FormInitListener, FormValidationListener {

    private static final Logger s_log = Logger.getLogger(UserForm.class
        .getName());

    private boolean m_newUser;

    protected TextField m_firstName;
    protected TextField m_lastName;
    protected TextField m_email;
    protected TextField m_screenName;
    //protected TextField m_additional;
    protected Password m_password;
    protected Password m_confirm;
    protected TextField m_question;
    protected TextField m_answer;
    protected TextField m_url;
    protected TextField m_ssoLogin;

    protected Label m_securitySectionHeader = new Label(LoginHelper
        .getMessage("login.userNewForm.securitySectionHeader"), false);
    protected Label m_securityBlurb = new Label(LoginHelper
        .getMessage("login.userNewForm.securityBlurb"));
    protected Label m_passwordBlurb = new Label(LoginHelper
        .getMessage("login.userNewForm.passwordBlurb"));
    protected Label m_passwordLabel = new Label(PASSWORD);
    protected Label m_confirmationLabel = new Label(PASSWORD_CONFIRMATION);
    protected Label m_questionBlurb = new Label(LoginHelper
        .getMessage("login.userNewForm.questionBlurb"));
    protected Label m_questionLabel = new Label(PASSWORD_QUESTION);
    protected Label m_answerLabel = new Label(PASSWORD_ANSWER);
    protected PasswordValidationListener m_passwordValidationListener
                                         = new PasswordValidationListener();
    protected NotEmptyValidationListener m_confirmationNotEmptyValidationListener
                                         = new NotEmptyValidationListener();
    protected Submit m_submit = new Submit(SUBMIT);
    protected Label m_firstNameLabel = new Label(FIRST_NAME);
    protected Label m_lastNameLabel = new Label(LAST_NAME);
    protected Label m_urlLabel = new Label(URL);
    protected Label m_screenNameLabel = new Label(SCREEN_NAME);
    protected Label m_emailLabel = new Label(PRIMARY_EMAIL);

    protected Container m_profilePart = new FormSection();
    protected Container m_securityPart = new FormSection();
    protected Container m_submitPart = new FormSection();

    /**
     * Create a UserForm with the given name and panel.
     *
     */
    public UserForm(String name, Container panel, boolean newUser) {
        super(name, panel);

        m_newUser = newUser;

        setMethod(Form.POST);
        addInitListener(this);
        addValidationListener(this);

        if (m_newUser) {
            m_profilePart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.aboutYouSectionHeader"),
                                        false), ColumnPanel.FULL_WIDTH);
        }

        // SDM #163373: add length checking for first/last names.  We do
        // this with both maximum length parameters in the user/add form and
        // with validation of the value that come in for processing.
        m_firstName = new TextField(new StringParameter(FORM_FIRST_NAME));
        m_firstName.setMaxLength(MAX_NAME_LEN);
        m_firstName.setSize(20);
        m_firstName.addValidationListener(new NotEmptyValidationListener());
        m_firstName.addValidationListener(new StringLengthValidationListener(
            MAX_NAME_LEN));

        m_profilePart.add(m_firstNameLabel);
        m_profilePart.add(m_firstName);

        m_lastName = new TextField(new StringParameter(FORM_LAST_NAME));
        m_lastName.setMaxLength(MAX_NAME_LEN);
        m_lastName.setSize(25);
        m_lastName.addValidationListener(new NotEmptyValidationListener());
        m_lastName.addValidationListener(new StringLengthValidationListener(
            MAX_NAME_LEN));

        m_profilePart.add(m_lastNameLabel);
        m_profilePart.add(m_lastName);

        m_profilePart.add(m_screenNameLabel);
        m_screenName = new TextField(new StringParameter(FORM_SCREEN_NAME));
        m_screenName.addValidationListener(new NotEmptyValidationListener());
        m_profilePart.add(m_screenName);

        // Primary email address
        m_email = new TextField(new EmailParameter(FORM_EMAIL));
        m_email.addValidationListener(new NotEmptyValidationListener());

        m_profilePart.add(m_emailLabel);
        m_profilePart.add(m_email);

        // TODO: support additional emails
        // Additional email addresses
        //m_additional = new TextField(new EmailParameter
        //                             (FORM_ADDITIONAL_EMAIL));
        //add(new Label(ADDITIONAL_EMAIL));
        //add(m_additional);
        // URL
        m_url = new TextField(new URLParameter(FORM_URL));
        m_url.setSize(50);
        m_url.setDefaultValue(FORM_URL_DEFAULT);
        // *** FIXME *** move URLVL to bebop.params so can import
        //m_url.addValidationListener(new URLValidationListener());

        m_profilePart.add(m_urlLabel);
        m_profilePart.add(m_url);

        // SDM #162740: disable user bio for now, as there
        // is no support for User Bio in the kernel level.
        // add(new Label(BIO));
        // TextArea bioText = new TextArea(new StringParameter(FORM_BIO));
        // bioText.setCols(50);
        // bioText.setRows(10);
        // add(bioText);
        // add(new Label(""));
        if (m_newUser) {
            m_securityPart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.securitySectionHeader"),
                                         false), ColumnPanel.FULL_WIDTH);

            m_securityPart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.securityBlurb")),
                               ColumnPanel.FULL_WIDTH);

            m_securityPart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.passwordBlurb")),
                               ColumnPanel.FULL_WIDTH);

            // Password
            m_password = new Password(new StringParameter(FORM_PASSWORD));
            m_password.addValidationListener(new PasswordValidationListener());

            m_securityPart.add(m_passwordLabel);
            m_securityPart.add(m_password);

            // Password confirmation
            m_confirm = new Password(new StringParameter(
                FORM_PASSWORD_CONFIRMATION));
            m_confirm.addValidationListener(new NotEmptyValidationListener());

            m_securityPart.add(m_confirmationLabel);
            m_securityPart.add(m_confirm);

            m_securityPart.add(new Label(LoginHelper
                .getMessage("login.userNewForm.questionBlurb")),
                               ColumnPanel.FULL_WIDTH);

            // Password question
            m_question = new TextField(new StringParameter(
                FORM_PASSWORD_QUESTION));
            m_question.setSize(30);
            m_question.addValidationListener(new NotEmptyValidationListener());

            m_securityPart.add(m_questionLabel);
            m_securityPart.add(m_question);

            // Password answer
            m_answer = new TextField(new StringParameter(FORM_PASSWORD_ANSWER));
            m_answer.setSize(30);
            m_answer.addValidationListener(new NotEmptyValidationListener());

            m_securityPart.add(m_answerLabel);
            m_securityPart.add(m_answer);

            m_ssoLogin = new TextField(new StringParameter(USER_FORM_INPUT_SSO));
            m_ssoLogin.setSize(50);
            add(USER_FORM_LABEL_SSO);
            add(m_ssoLogin);
        }

        // Submit
        m_submitPart.add(m_submit, ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);

        add(m_profilePart);
        add(m_securityPart);
        add(m_submitPart);
    }

    /**
     * Initializes this form with data from the user.
     *
     */
    public void init(FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();

        User user = null;

        try {
            user = getUser(state);
        } catch (DataObjectNotFoundException e) {
            throw new FormProcessException(LoginGlobalizationUtil.globalize(
                "login.userForm.couldnt_load_user"));
        }

        if (user == null) {
            // don't load any data
            return;
        }

        PersonName name = user.getPersonName();
        m_firstName.setValue(state, name.getGivenName());
        m_lastName.setValue(state, name.getFamilyName());

        InternetAddress address;
        try {
            address = new InternetAddress(user.getPrimaryEmail().toString());
        } catch (AddressException e) {
            String[] errorMsg = new String[1];
            errorMsg[0] = user.getPrimaryEmail().toString();
            throw new FormProcessException(
                "Email address is bad: " + user.getPrimaryEmail(),
                LoginHelper.getMessage("login.error.badEmail", errorMsg)
            );
        }

        m_email.setValue(state, address);
        m_screenName.setValue(state, user.getScreenName());

        // TODO: init additional emails
        m_url.setValue(state, user.getURI());
        // TODO: support screen name in kernel
        //m_screenName.setValue(state, user.getScreenName());
    }

    /**
     * Gets the current user for initializing the form.
     *
     * @return the current user.
     *
     * @return null if the form should not be initialized with user data.
     *
     * @throws DataObjectNotFoundException if the user is not found.
     *
     */
    protected abstract User getUser(PageState state)
        throws DataObjectNotFoundException;

    /**
     * Validates this form. Verifies that the password and password-confirm
     * fields match. If not it adds an error to the password-confirm field. Also
     * verifies that primary email address and screen name are unique among all
     * users.
     *
     */
    public void validate(FormSectionEvent event)
        throws FormProcessException {
        PageState state = event.getPageState();
        FormData data = event.getFormData();
        try {
            if (m_newUser) {
                // Verify that password and confirmation match
                String password = (String) m_password.getValue(state);
                String confirm = (String) m_confirm.getValue(state);

                if ((password != null) && (confirm != null)
                        && !password.equals(confirm)) {
                    data.addError(FORM_PASSWORD_CONFIRMATION,
                                  (String) ERROR_MISMATCH_PASSWORD
                                      .localize(state.getRequest()));
                }
            }

            // Verify that primary email and screen name are unique
            DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.kernel.UserPrimaryEmail");
            //query.setParameter("excludeGroupId", null);

            String email = null;
            String lowerEmail = null;
            if (m_email.getValue(state) != null) {
                InternetAddress address = (InternetAddress) m_email
                    .getValue(state);
                email = address.getAddress();
                lowerEmail = email.toLowerCase();
            }

            String lowerScreenName = null;
            String screenName = null;
            screenName = (String) m_screenName.getValue(state);
            lowerScreenName = screenName.toLowerCase();

            String oldEmail = null;
            String oldScreenName = null;
            try {
                User user = getUser(state);
                if (user != null) {
                    oldEmail = user.getPrimaryEmail().toString();
                    oldScreenName = user.getScreenName();
                    if (null != oldScreenName) {
                        oldScreenName = oldScreenName.toLowerCase();
                    }
                }
            } catch (DataObjectNotFoundException e) {
                throw new UncheckedWrapperException(e);
            }

            // If this query returns with any rows we have a duplicate
            // screen name, email address, or both.  Check the results and
            // produce appropriate error messages.
            boolean checkPrimaryEmail = Kernel.getConfig()
                .emailIsPrimaryIdentifier();

            Filter filter = null;
            if (checkPrimaryEmail) {
                filter = query.addFilter(
                    "lowerPrimaryEmailAddress = :email or lowerScreenName = :sn");
                filter.set("email", lowerEmail);
            } else {
                filter = query.addFilter("lowerScreenName = :sn");
            }
            filter.set("sn", lowerScreenName);

            while (query.next()) {
                if ((lowerScreenName != null)
                        && !lowerScreenName.equals(oldScreenName)
                        && lowerScreenName.equals(query.get("lowerScreenName"))) {
                    data.addError(FORM_SCREEN_NAME, (String) ERROR_DUPLICATE_SN
                                  .localize(state.getRequest()));
                }
                if ((email != null) && checkPrimaryEmail
                        && !email.equals(oldEmail)
                        && lowerEmail.equals(query.get(
                        "lowerPrimaryEmailAddress"))) {
                    data.addError(FORM_EMAIL, (String) ERROR_DUPLICATE_EMAIL
                                  .localize(state.getRequest()));
                }
            }
            query.close();
        } finally {
            // if the form has errors, clear the password fields so we don't
            // send the passwords back over the network
            if (m_newUser && !data.isValid()) {
                m_password.setValue(state, "");
                m_confirm.setValue(state, "");
            }
        }
    }

}
