/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel;

import com.arsdigita.util.StringUtils;
import com.arsdigita.util.Assert;
/**
 * UserFactory is a utility class for creating
 */
public final class UserFactory {

    /**
     * Creates a new user, sets up authentication, and adds to the system. Any parameter
     * not marked Optional must be non-null.
     *
     * @param primaryEmail - The user's primary email
     * @param givenName - First name
     * @param familyName - Last name
     * @param password - The user's password
     * @param passwordQuestion - The question asked of the user if he forgets his password
     * @param passwordAnswer - The answer to the question. Password reset on correct answer
     * @param screenName - AOL, IRC, etc screen name. Optional
     * @param uri - The URI for the user's web page. Optional
     * @param additionalEmail - An additional email address for the user. Optional
     *
     * @return User The user created.
     */
    public static User newUser(EmailAddress primaryEmail,
                               String givenName,
                               String familyName,
                               String password,
                               String passwordQuestion,
                               String passwordAnswer,
                               String screenName,
                               String uri,
                               EmailAddress additionalEmail) {

        Assert.assertNotNull(primaryEmail, "primaryEmail");
        Assert.assertNotNull(givenName, "givenName");
        Assert.assertNotNull(familyName, "familyName");
        Assert.assertNotNull(password, "password");
        Assert.assertNotNull(passwordQuestion, "passwordQuestion");
        Assert.assertNotNull(passwordAnswer, "passwordAnswer");

        User user = new User();
        user.setPrimaryEmail(primaryEmail);

        PersonName name = user.getPersonName();
        name.setGivenName(givenName);
        name.setFamilyName(familyName);

        user.setScreenName(screenName);

        // Check to see if the value has changed from the
        // default.  If not just leave this set to null.

        if (StringUtils.emptyString(uri) == false) {
            user.setURI(uri);
        }

        // Add optional additional email address
        if (null != additionalEmail) {
            user.addEmailAddress(additionalEmail);
        }

        // Make new user persistent
        user.save();
        // Save user authentication credentials.
        UserAuthentication auth =
            UserAuthentication.createForUser(user);

        auth.setPassword(password);
        auth.setPasswordQuestion(passwordQuestion);
        auth.setPasswordAnswer(passwordAnswer);
        auth.save();

        return user;
    }

}
