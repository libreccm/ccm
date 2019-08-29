/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.ui.login;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.kernel.security.OneLoginUtil;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.SettingsException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SamlLoginForm
    extends Form
    implements LoginConstants, FormProcessListener {

    final static String FORM_NAME = "saml-login";

    private Submit submitLogin;

    public SamlLoginForm() {

        this(new BoxPanel());
    }

    public SamlLoginForm(final Container panel) {

        super(FORM_NAME, panel);
        setMethod(Form.POST);
        addProcessListener(this);

        submitLogin = new Submit(
            LoginGlobalizationUtil.globalize("login.saml.submit")
        );
        add(submitLogin);
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (submitLogin.isSelected(state)) {
            final HttpServletRequest request = state.getRequest();
            final HttpServletResponse response = state.getResponse();

            try {
                final Auth auth = new Auth(OneLoginUtil.buildSettings(request),
                                           request,
                                           response);
                auth.login();
            } catch (IOException | SettingsException ex) {
                throw new FormProcessException(ex);
            }
        }
    }

}
