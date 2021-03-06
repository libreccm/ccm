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

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.bebop.Label;


/**
 * Constants used by Login UI.
 *
 */
public interface LoginConstants {

    public final static GlobalizedMessage SUBMIT = LoginHelper.getMessage(
        "login.submit");
    public final static GlobalizedMessage LOGIN = LoginHelper.getMessage(
        "login.login");
    public final static GlobalizedMessage PRIMARY_EMAIL = LoginHelper
        .getMessage("login.primaryEmail");
    public final static GlobalizedMessage ADDITIONAL_EMAIL = LoginHelper
        .getMessage("login.additionalEmail");
    public final static GlobalizedMessage SCREEN_NAME = LoginHelper.getMessage(
        "login.screenName");
    public final static GlobalizedMessage FIRST_NAME = LoginHelper.getMessage(
        "login.firstName");
    public final static GlobalizedMessage LAST_NAME = LoginHelper.getMessage(
        "login.lastName");
    public final static GlobalizedMessage PASSWORD = LoginHelper.getMessage(
        "login.password", new Object[]{
            new Integer(PasswordValidationListener.MIN_LENGTH)});
    public final static GlobalizedMessage PASSWORD_CONFIRMATION = LoginHelper
        .getMessage("login.passwordConfirm");
    public final static GlobalizedMessage PASSWORD_QUESTION = LoginHelper
        .getMessage("login.passwordQuestion");
    public final static GlobalizedMessage PASSWORD_ANSWER = LoginHelper
        .getMessage("login.passwordAnswer");
    public final static GlobalizedMessage URL = LoginHelper.getMessage(
        "login.url");
    public final static GlobalizedMessage BIO = LoginHelper.getMessage(
        "login.bio");

    public final static GlobalizedMessage ERROR_DUPLICATE_SN = LoginHelper
        .getMessage("login.error.duplicateScreenName");
    public final static GlobalizedMessage ERROR_DUPLICATE_EMAIL = LoginHelper
        .getMessage("login.error.duplicateEmail");
    public final static GlobalizedMessage ERROR_MISMATCH_PASSWORD = LoginHelper
        .getMessage("login.error.mismatchPassword");
    public final static GlobalizedMessage ERROR_BAD_PASSWORD = LoginHelper
        .getMessage("login.error.badPassword");

    public final static GlobalizedMessage ERROR_LOGIN_FAIL = LoginHelper
        .getMessage("login.error.loginFail");

    public final static GlobalizedMessage ERROR_BAD_ANSWER = LoginHelper
        .getMessage("login.error.badAnswer");
    public final static GlobalizedMessage ERROR_BAD_EMAIL = LoginHelper
        .getMessage("login.error.badEmail");
    public final static GlobalizedMessage ERROR_BANNED_EMAIL = LoginHelper
        .getMessage("login.error.bannedEmail");
    public final static GlobalizedMessage ERROR_NO_EMAIL = LoginHelper
        .getMessage("login.error.noEmail");

    public final static Label USER_FORM_LABEL_SSO = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.ssologinname",
            "com.arsdigita.ui.admin.AdminResources"));

    public final static String FORM_EMAIL = "emailAddress";
    public final static String FORM_SCREEN_NAME = "screenName";

    // Should not really be named email. Kept this way due to external tests 
    // depending on this value.
    public final static String FORM_LOGIN = "email";

    public final static String FORM_ADDITIONAL_EMAIL = "additional_email";
    public final static String FORM_FIRST_NAME = "firstname";
    public final static String FORM_LAST_NAME = "lastname";
    public final static String FORM_PASSWORD = "password";
    public final static String FORM_PASSWORD_CONFIRMATION
                                   = "password_confirmation";
    public final static String FORM_PASSWORD_QUESTION = "question";
    public final static String FORM_PASSWORD_ANSWER = "answer";
    public final static String FORM_URL = "url";
    public final static String FORM_URL_DEFAULT = "http://";
    public final static String FORM_BIO = "biography";
    public final static String FORM_TIMESTAMP = "timestamp";
    public final static String FORM_PERSISTENT_LOGIN_P = "persistentCookieP";
    public final static String FORM_PERSISTENT_LOGIN_P_DEFAULT = "1";

    public final static String USER_FORM_INPUT_SSO = "sso_login";

    public final static int TIMESTAMP_LIFETIME_SECS = 300;
    public static final int MAX_NAME_LEN = 60;

}
