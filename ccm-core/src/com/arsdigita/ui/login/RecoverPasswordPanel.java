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
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ElementComponent;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.security.CredentialEncodingException;
// import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.kernel.security.RecoveryLoginModule;
import com.arsdigita.ui.UI;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.mail.Mail;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * A sequence of Forms and Panels that allows a user to recover from a
 * forgotten password.  First form requests the user's email address, and is
 * pre-filled if the user is logged in.  Second form presents the user's
 * password question and requires the correct answer.  Third panel informs
 * the user that a mail has been sent that will allow them to change
 * their password.
 *
 * @author Sameer Ajmani
 **/
public class RecoverPasswordPanel extends SimpleContainer
                                  implements LoginConstants {

    private static final Logger s_log =
        Logger.getLogger(RecoverPasswordPanel.class.getName());

    private static ParameterModel DISPLAY_PARAM =
        new StringParameter("display");
    private static ParameterModel USERID_PARAM =
        new BigDecimalParameter("userID");

    private EnterEmailForm m_enterEmailForm;
    private AnswerQuestionForm m_answerQuestionForm;
    private MailSentPane m_mailSentPane;
    private MailFailedPane m_mailFailedPane;

    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, DISPLAY_PARAM);
        p.addComponentStateParam(this, USERID_PARAM);
        p.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    String display = (String)state.getValue(DISPLAY_PARAM);
                    s_log.debug("display == "+display);
                    boolean visible = false;
                    visible |= setVisible(state, display, m_enterEmailForm);
                    visible |= setVisible(state, display, m_answerQuestionForm);
                    visible |= setVisible(state, display, m_mailSentPane);
                    visible |= setVisible(state, display, m_mailFailedPane);
                    if (!visible) {
                        // default: show first form
                        m_enterEmailForm.setVisible(state, true);
                    }
                }
                private boolean setVisible(PageState state,
                                           String display,
                                           Component component) {
                    boolean visible = component.getClass().getName()
                        .equals(display);
                    component.setVisible(state, visible);
                    return visible;
                }
            });
    }

    public RecoverPasswordPanel() {
        m_enterEmailForm = new EnterEmailForm();
        add(m_enterEmailForm);

        m_answerQuestionForm = new AnswerQuestionForm();
        add(m_answerQuestionForm);

        m_mailSentPane = new MailSentPane();
        add(m_mailSentPane);

        m_mailFailedPane = new MailFailedPane();
        add(m_mailFailedPane);
    }

    private static class EnterEmailForm extends Form
        implements FormValidationListener, FormProcessListener {

        public EnterEmailForm() {
            super("enter-email", new BoxPanel());
            addValidationListener(this);
            addProcessListener(this);
            add(new Label(PRIMARY_EMAIL));
            TextField email = new TextField
                (new EmailParameter(FORM_EMAIL));
            email.addValidationListener(new NotNullValidationListener());
            addInitListener(new EmailInitListener
                            ((EmailParameter)email.getParameterModel()));
            add(email);
            add(new Submit(SUBMIT));
        }

        public void validate(FormSectionEvent event)
            throws FormProcessException  {
            FormData data = event.getFormData();
            if (!data.isValid()) {
                // data already has errors
                return;
            }
            String email = ((InternetAddress)data.get(FORM_EMAIL)).getAddress();
			BigDecimal userID = null;
            try {
                // TODO: save UserAuthentication object in PageState
                // to avoid repeated database queries
                UserAuthentication auth =
                    UserAuthentication.retrieveForLoginName(email);
                userID = auth.getUser().getID();
                event.getPageState().setValue(USERID_PARAM, userID);
            } catch (DataObjectNotFoundException e) {
                data.addError(FORM_EMAIL,
                              (String)ERROR_BAD_EMAIL.localize
                              (event.getPageState().getRequest()));
            }
            // if the user exists, we need to make sure they are not banned.
            if(userID!=null) {
				User user = User.retrieve(userID);
				if(user.isBanned()) {
					data.addError(FORM_EMAIL,(String)ERROR_BANNED_EMAIL.localize(event.getPageState().getRequest()));

				}
            }
        }

        public void process(FormSectionEvent event)
            throws FormProcessException  {
            event.getPageState().setValue
                (DISPLAY_PARAM, AnswerQuestionForm.class.getName());
        }
    }

    private static class AnswerQuestionForm extends Form
        implements FormValidationListener, FormProcessListener {
        private TextField m_answer;

        public AnswerQuestionForm() {
            super("answer-question", new BoxPanel());
            addValidationListener(this);
            addProcessListener(this);
            add(new Label(PASSWORD_QUESTION));
            Label question = new Label();
            question.setIdAttr( "question" );
            question.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent event) {
                        Label label = (Label)event.getTarget();
                        BigDecimal userID = (BigDecimal)
                            event.getPageState().getValue(USERID_PARAM);
                        if (userID == null) {
                            throw new IllegalStateException
                                ("userID must not be null");
                        }
                        try {
                            String theQuestion = UserAuthentication
                                .retrieveForUser(userID)
                                .getPasswordQuestion();
                            if (theQuestion == null) {
                                throw new IllegalStateException
                                    ("password question must not be null "
                                     +"(userID == "+userID+")");
                            }
                            label.setLabel(theQuestion);
                        } catch (DataObjectNotFoundException e) {
                            throw new IllegalStateException
                                ("userID must be a valid user");
                        }
                    }
                });
            add(question);
            add(new Label(PASSWORD_ANSWER));
            m_answer = new TextField
                (new StringParameter(FORM_PASSWORD_ANSWER));
            m_answer.addValidationListener
                (new NotNullValidationListener());
            add(m_answer);
            add(new Submit(SUBMIT));
        }

        public void validate(FormSectionEvent event)
            throws FormProcessException  {
            FormData data = event.getFormData();
            if (!data.isValid()) {
                // data already has errors
                return;
            }
            String answer = (String)data.get(FORM_PASSWORD_ANSWER);
            // FIXME: same as code above; instead save UserAuth object
            BigDecimal userID = (BigDecimal)
                event.getPageState().getValue(USERID_PARAM);
            if (userID == null) {
                throw new IllegalStateException
                    ("userID must not be null");
            }
            try {
                if (!UserAuthentication
                    .retrieveForUser(userID)
                    .isValidAnswer(answer)) {
                    data.addError(FORM_PASSWORD_ANSWER,
                                  (String)ERROR_BAD_ANSWER.localize
                                  (event.getPageState().getRequest()));
                }
            } catch (DataObjectNotFoundException e) {
                throw new IllegalStateException
                    ("userID must be a valid user");
            }
        }

        public void process(FormSectionEvent event)
            throws FormProcessException {
            HttpServletRequest req =
                event.getPageState().getRequest();

            // FIXME: save UserAuth object to avoid duplicating above code
            BigDecimal userID = (BigDecimal)
                event.getPageState().getValue(USERID_PARAM);

            if (userID == null) {
                throw new IllegalStateException
                    ("userID must not be null");
            }

            User user;
            try {
                user = UserAuthentication.retrieveForUser(userID).getUser();
            } catch (DataObjectNotFoundException e) {
                throw new IllegalStateException
                    ("userID must be a valid user");
            }

            if (user.getPrimaryEmail() == null) {
                mailFailed(event, "null email, user ID: " + user.getID());
                return;
            }

            String to =
                user.getPrimaryEmail().toString();
            String from =
		Mail.getConfig().getDefaultFrom();
	    // AFAICT this value below is hard coded to "" !
	    //KernelHelper.getSystemAdministratorEmailAddress();
            String subject =
                LoginHelper.localize("login.recoverPassword.mailSubject", req);
            String body =
                getNotification(user, event, req);

            // send the message and set next panel to "mail sent" page
            try {
                Mail.send (to, from, subject, body);
                event.getPageState().setValue
                    (DISPLAY_PARAM, MailSentPane.class.getName()); 
            } catch (MessagingException e) {
                mailFailed(event, e.toString());
            }
        }
    }

    /**
     * Log a failure to notify the user and set the next panel to the
     * "mail failed" page.
     */

    private static void mailFailed (FormSectionEvent event,
                                    String reason)
    {
        s_log.warn("Could not notify user for recovery: "+reason);
        event.getPageState().setValue
            (DISPLAY_PARAM, MailFailedPane.class.getName());
    }

    /**
     * Displays a message that password recovery information has been
     * sent.
     **/
    private static class MailSentPane extends ElementComponent {
        public MailSentPane() {
            super("subsite:recoverPasswordMailSent",
                  LoginServlet.SUBSITE_NS_URI);
        }
    }

    /**
     * Constructs the notification to send users when recovering a
     * password.
     **/
    private static String getNotification (User user,
                                           FormSectionEvent event,
                                           HttpServletRequest req) {
        String name = user.getPersonName().getGivenName();

        URL url;
        try {
            ParameterMap map = new ParameterMap();
            map.setParameter
                (RecoveryLoginModule.getParamName(),
                 RecoveryLoginModule.getParamValue(user.getID()));
            
            url = com.arsdigita.web.URL.dynamicHostThere
                (req, 
//                 LegacyInitializer.getFullURL
//                 (LegacyInitializer.CHANGE_PAGE_KEY, req),
                 UI.getRecoverPasswordPageURL(),
                 map);
        } catch (CredentialEncodingException e) {
            throw new UncheckedWrapperException
                ("Could not create URL", e);
        }

        return LoginHelper.localize("login.recoverPassword.mailBody",
                                    new Object[] { name, url.getURL() },
                                    req);
    }


    /**
     * Displays a message that password recovery information couldn't
     * be sent.  There must be a better way to do this!
     **/
    private static class MailFailedPane extends ElementComponent {
        public MailFailedPane() {
            super("subsite:recoverPasswordMailFailed",
                  LoginServlet.SUBSITE_NS_URI);
        }
    }
}
