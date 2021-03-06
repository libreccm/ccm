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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.ui.UI;
import com.arsdigita.web.Web;
import com.arsdigita.web.URL;
import com.arsdigita.web.ReturnSignal;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.security.RecoveryLoginModule;
import com.arsdigita.mail.Mail;
import com.arsdigita.util.UncheckedWrapperException;
import java.lang.reflect.Array;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * A Form that allows a user to change their password by entering their old
 * password, a new password, and a confirmation of their new password. Requires
 * that the user is logged in. Requires that new password differ from old and
 * meet strong password requirements. If the user is recovering from a lost
 * password (UserContext.isRecovering() is true), does not require or display
 * the old password parameter and does not require that new password differ from
 * old. Mails the user to notify of password change. Redirects user to workspace
 * or return_url if set.
 *
 * @author Sameer Ajmani
 *
 */
public class ChangePasswordForm extends Form
        implements FormProcessListener,
        FormValidationListener {

    private static final Logger s_log =
            Logger.getLogger(ChangePasswordForm.class.getName());
    final static String CHANGE_PASSWORD_FORM_NAME = "change-password";
    final static String OLD_PASSWORD_PARAM_NAME = "old-password";
    final static String NEW_PASSWORD_PARAM_NAME = "new-password";
    final static String CONFIRM_PASSWORD_PARAM_NAME = "confirm-password";
    final static String RETURN_URL_PARAM_NAME =
            LoginHelper.RETURN_URL_PARAM_NAME;
    private UserAuthenticationListener m_listener =
            new UserAuthenticationListener();
    private Hidden m_returnURL;
    private Hidden m_recovery;
    private Label m_oldPasswordLabel;
    private Password m_oldPassword;
    private Password m_newPassword;
    private Password m_confirmPassword;

    public ChangePasswordForm() {
        this(new BoxPanel());
    }

    public void register(Page p) {
        super.register(p);
        p.addRequestListener(m_listener);
        p.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                PageState state = e.getPageState();
                boolean visible = !isRecovering();
                m_oldPasswordLabel.setVisible(state, visible);
                m_oldPassword.setVisible(state, visible);
            }
        });
    }

    public ChangePasswordForm(Container panel) {
        super(CHANGE_PASSWORD_FORM_NAME, panel);

        setMethod(Form.POST);
        addValidationListener(this);
        addProcessListener(this);

        // save the recovery credential as a hidden parameter
        m_recovery = new Hidden(new StringParameter(RecoveryLoginModule.getParamName()));
        m_recovery.setPassIn(true);
        add(m_recovery);

        // save the return URL as a hidden parameter
        m_returnURL = new Hidden(new URLParameter(RETURN_URL_PARAM_NAME));
        m_returnURL.setPassIn(true);
        add(m_returnURL);


        Party party = Kernel.getContext().getParty();

        if (party != null && party instanceof User) {
            User user = (User) party;
            user.getPersonName().toString();

            // Add a describtive text and the user name to the form for user comfort
            Label greeting = new Label(
                    LoginHelper.getMessage("login.changePasswortForm.greeting",
                                            new Object[] { user.getPersonName().toString() } )
                    );
            
            greeting.setFontWeight(Label.BOLD);
            greeting.setClassAttr("greeting");
            add(greeting);
        }        

        add(new Label(LoginHelper.getMessage("login.changePasswortForm.introText")));

        // old password
        m_oldPasswordLabel = new Label(LoginHelper.getMessage("login.changePasswordForm.oldPasswordLabel"));
        add(m_oldPasswordLabel);
        m_oldPassword = new Password(OLD_PASSWORD_PARAM_NAME);
        // don't use NotNullValidationListener because
        // old password may be null during password recovery
        add(m_oldPassword);

        // new password
        Object[] params = new Object[]{
            new Integer(PasswordValidationListener.MIN_LENGTH)};
        add(new Label(LoginHelper.getMessage("login.changePasswordForm.newPasswordLabel", params)));
        m_newPassword = new Password(NEW_PASSWORD_PARAM_NAME);
        m_newPassword.addValidationListener(new PasswordValidationListener());
        add(m_newPassword);

        // confirm new password
        add(new Label(LoginHelper.getMessage("login.changePasswordForm.confirmPasswordLabel")));
        m_confirmPassword = new Password(CONFIRM_PASSWORD_PARAM_NAME);
        // don't use PasswordValidationListener to avoid duplicate errors
        m_confirmPassword.addValidationListener(new NotNullValidationListener());
        add(m_confirmPassword);

        // submit
        add(new Submit(LoginHelper.getMessage("login.changePasswordForm.submit")),
                ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);
    }

    public void validate(FormSectionEvent event)
            throws FormProcessException {
        PageState state = event.getPageState();
        FormData data = event.getFormData();
        try {
            // get user object
            if (!m_listener.isLoggedIn(state)) {
                // this error should never appear
                data.addError(LoginHelper.localize("login.changePasswordForm.noUserError",
                        state.getRequest()));
                return;
            }
            User user = m_listener.getUser(state);

            // get parameter values
            String oldPassword = (String) m_oldPassword.getValue(state);
            String newPassword = (String) m_newPassword.getValue(state);
            String confirmPassword = (String) m_confirmPassword.getValue(state);

            // check old password unless recovering
            if (!isRecovering()) {
                try {
                    // The old password can never be null or contain leading or
                    // trailing slashes.
                    if (oldPassword == null
                            || !oldPassword.trim().equals(oldPassword)) {
                        data.addError(OLD_PASSWORD_PARAM_NAME, LoginHelper.localize("login.changePasswordForm.badPasswordError",
                                state.getRequest()));
                        return;
                    }

                    // TODO: avoid loading UserAuthentication in both
                    // validate() and process() stages
                    UserAuthentication auth =
                            UserAuthentication.retrieveForUser(user);
                    if (!auth.isValidPassword(oldPassword)) {
                        data.addError(OLD_PASSWORD_PARAM_NAME, LoginHelper.localize("login.changePasswordForm.badPasswordError",
                                state.getRequest()));
                        return;
                    }
                } catch (DataObjectNotFoundException e) {
                    throw new UncheckedWrapperException("Could not get UserAuthentication", e);
                }
            }

            // check new password
            if (newPassword.equals(oldPassword)) {
                data.addError(NEW_PASSWORD_PARAM_NAME, LoginHelper.localize("login.changePasswordForm.mustDifferError",
                        state.getRequest()));
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                data.addError(CONFIRM_PASSWORD_PARAM_NAME, LoginHelper.localize("login.changePasswordForm.mustMatchError",
                        state.getRequest()));
                return;
            }
        } finally {
            if (!data.isValid()) {
                // clear passwords from form data
                m_oldPassword.setValue(state, "");
                m_newPassword.setValue(state, "");
                m_confirmPassword.setValue(state, "");
            }
        }
    }

    public void process(FormSectionEvent event)
            throws FormProcessException {
        PageState state = event.getPageState();
        FormData data = event.getFormData();

        // get user object
        if (!m_listener.isLoggedIn(state)) {
            // this error should never appear (checked in validate)
            data.addError(LoginHelper.localize("login.changePasswordForm.noUserError",
                    state.getRequest()));
            return;
        }
        User user = m_listener.getUser(state);

        // set new password
        try {
            UserAuthentication auth =
                    UserAuthentication.retrieveForUser(user);
            String newPassword = (String) m_newPassword.getValue(state);
            auth.setPassword(newPassword);
            s_log.debug("committing password change");
            auth.save();
        } catch (DataObjectNotFoundException e) {
            throw new UncheckedWrapperException("Could not get UserAuthentication", e);
        }

        // mail report to user

        if (user.getPrimaryEmail() != null) {

            HttpServletRequest req = state.getRequest();

            String to =
                    user.getPrimaryEmail().toString();
            String from =
                    Kernel.getSecurityConfig().getAdminContactEmail();
            String name =
                    user.getPersonName().getGivenName();
            String subject =
                    LoginHelper.localize("login.changePasswordForm.mailSubject", req);
            String body =
                    LoginHelper.localize("login.changePasswordForm.mailBody",
                    new Object[]{name},
                    req);

            // try to send the message, but don't throw the exception
            // if it fails so that the password change is comitted
            // anyway.

            try {
                Mail.send(to, from, subject, body);
            } catch (javax.mail.MessagingException e) {
                s_log.error("Could not notify user of password change", e);
            }
        } else {
            s_log.debug("Could not notify user of password change: "
                    + "null email, user ID: "
                    + user.getID());
        }

        final HttpServletRequest req = state.getRequest();

        final String path = UI.getWorkspaceURL(req);

        final URL fallback = URL.there(req, path);

        throw new ReturnSignal(req, fallback);
    }

    private static boolean isRecovering() {
        return Web.getUserContext().isRecovering();
    }
}
