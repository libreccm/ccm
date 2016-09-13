/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.modules.core.security;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.core.EmailAddress;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 31.05.16
 */
public class User extends Party {

    private String givenName;
    private String familyName;

    private EmailAddress primaryEmailAddress;
    private List<EmailAddress> emailAddresses;

    private boolean banned;
    private String password;
    private boolean passwordResetRequired;

    private Set<GroupMembership> groupMemberships;

    public User(final com.arsdigita.kernel.User trunkUser) {
        super(trunkUser);

        this.givenName = trunkUser.getPersonName().getGivenName();
        this.familyName = trunkUser.getPersonName().getFamilyName();

        this.primaryEmailAddress = new EmailAddress(trunkUser.getPrimaryEmail());
        this.emailAddresses = new ArrayList<>();
        Iterator it = trunkUser.getEmailAddresses();
        while (it.hasNext()) {
            com.arsdigita.kernel.EmailAddress trunkEmail = (com.arsdigita
                    .kernel.EmailAddress) it.next();
            this.emailAddresses.add(new EmailAddress(trunkEmail));
        }

        this.banned = trunkUser.isBanned();
        this.password = "";
        this.passwordResetRequired = true;

        this.groupMemberships = new HashSet<>();

        NgCollection.users.put(this.getPartyId(), this);
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new UserMarshaller();
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(final String familyName) {
        this.familyName = familyName;
    }

    public EmailAddress getPrimaryEmailAddress() {
        return primaryEmailAddress;
    }

    public void setPrimaryEmailAddress(final EmailAddress primaryEmailAddress) {
        this.primaryEmailAddress = primaryEmailAddress;
    }

    public List<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(final List<EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public void addEmailAddress(final EmailAddress emailAddress) {
        emailAddresses.add(emailAddress);
    }

    public void removeEmailAddress(final EmailAddress emailAddress) {
        emailAddresses.remove(emailAddress);
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(final boolean banned) {
        this.banned = banned;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isPasswordResetRequired() {
        return passwordResetRequired;
    }

    public void setPasswordResetRequired(final boolean passwordResetRequired) {
        this.passwordResetRequired = passwordResetRequired;
    }

    public Set<GroupMembership> getGroupMemberships() {
        return groupMemberships;
    }

    public void setGroupMemberships(final Set<GroupMembership>
                                            groupMemberships) {
        this.groupMemberships = groupMemberships;
    }

    public void addGroupMembership(final GroupMembership groupMembership) {
        groupMemberships.add(groupMembership);
    }

    public void removeGroupMembership(final GroupMembership groupMembership) {
        groupMemberships.remove(groupMembership);
    }
}
