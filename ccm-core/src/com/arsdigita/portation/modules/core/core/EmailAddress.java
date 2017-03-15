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
package com.arsdigita.portation.modules.core.core;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
public class EmailAddress {

    private String address;
    private boolean bouncing;
    private boolean verified;

    public EmailAddress(final com.arsdigita.kernel.EmailAddress
                                trunkEmailAddress) {
        this.address = trunkEmailAddress.getEmailAddress();

        this.bouncing = trunkEmailAddress.isBouncing();
        this.verified = trunkEmailAddress.isVerified();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public boolean isBouncing() {
        return bouncing;
    }

    public void setBouncing(final boolean bouncing) {
        this.bouncing = bouncing;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(final boolean verified) {
        this.verified = verified;
    }
}
