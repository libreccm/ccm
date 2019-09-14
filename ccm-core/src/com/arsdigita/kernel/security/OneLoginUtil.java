/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.kernel.security;

import com.arsdigita.ui.login.LoginServlet;
import com.arsdigita.web.URL;

import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility functions for integrating the OneLogin SAML implementation into CCM.
 *
 * @see https://github.com/onelogin/java-saml
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class OneLoginUtil {

    private OneLoginUtil() {
        // Nothing
    }

    public static Saml2Settings buildSettings(
        final HttpServletRequest request) {

        final SecurityConfig securityConfig = SecurityConfig.getConfig();

        final Map<String, Object> settings = new HashMap<>();
        settings.put("onelogin.saml2.strict",
                     securityConfig.getOneLoginSaml2Strict());
        settings.put("onelogin.saml2.debug",
                     securityConfig.getOneLoginSaml2Debug());
        settings.put("onelogin.saml2.sp.entityid",
                     securityConfig.getOneLoginSaml2SpEntityId());
        settings.put("onelogin.saml2.sp.assertation.consumer_service.url",
                     URL.there(request, "/" + LoginServlet.APPLICATION_NAME));
        settings.put(
            "onelogin.saml2.sp.assertation_consumer_service.binding",
            securityConfig.getOneLoginSaml2SpAssertationConsumerServiceBinding());
        settings.put("onelogin.saml2.sp.single_logout_service.url",
                     URL.there(request, LoginServlet.getLogoutPageURL()));
        settings.put(
            "onelogin.saml2.sp.single_logout_service.binding",
            securityConfig.getOneLoginSaml2SpSingleLogoutServiceBinding());
        settings.put("onelogin.saml2.sp.nameidformat",
                     securityConfig.getOneLoginSaml2SpNameIdFormat());

        settings.put("onelogin.saml2.idp.entityid",
                     securityConfig.getOneLoginSaml2IdpEntityId());
        settings.put("onelogin.saml2.idp.single_sign_on_service.url",
                     securityConfig.getOneLoginSaml2IdpSingleSignOnServiceUrl());
        settings.put("onelogin.saml2.idp.single_sign_on_service.binding",
                     securityConfig
                         .getOneLoginSaml2IdpSingleSignOnServiceBinding());
        settings.put("onelogin.saml2.ipd.single_logout_service.url",
                     securityConfig.getOneLoginSaml2IdpSingleLogoutServiceUrl());
        settings.put(
            "onelogin.saml2.idp.single_logout_service.response.url",
            securityConfig.getOneLoginSaml2IdpSingleLogoutServiceResponseUrl());
        settings.put(
            "onelogin.saml2.idp.single_logout_service.binding",
            securityConfig.getOneLoginSaml2IdpSingleLogoutServiceBinding());

        final SettingsBuilder builder = new SettingsBuilder();

        return builder.fromValues(settings).build();
    }

}
