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
package com.arsdigita.portation.categories.core.Party;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.categories.core.Utils.CollectionConverter;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 01.06.16
 */
public class Party implements Identifiable {

    private String trunkClass;

    private long id;
    private String name;
    private String displayName;
    private String primaryMailAddress;
    private List<String> mailAddresses;

    public Party(com.arsdigita.kernel.Party sysParty) {
        this.trunkClass = sysParty.getClass().getName();

        this.id = sysParty.getID().longValue();
        this.name = sysParty.getName();
        this.displayName =  sysParty.getDisplayName();
        if (sysParty.getPrimaryEmail() != null)
                this.primaryMailAddress = sysParty.getPrimaryEmail().getEmailAddress();
        this.mailAddresses = CollectionConverter.convertMailAddresses(sysParty.getAlternateEmails());
    }

    @Override
    public String getTrunkClass() {
        return trunkClass;
    }

    @Override
    public void setTrunkClass(String trunkClass) {
        this.trunkClass = trunkClass;
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new PartyMarshaller();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPrimaryMailAddress() {
        return primaryMailAddress;
    }

    public void setPrimaryMailAddress(String primaryMailAddress) {
        this.primaryMailAddress = primaryMailAddress;
    }

    public List<String> getMailAddresses() {
        return mailAddresses;
    }

    public void setMailAddresses(List<String> mailAddresses) {
        this.mailAddresses = mailAddresses;
    }
}
