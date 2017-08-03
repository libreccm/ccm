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
package com.arsdigita.london.terms.portation.modules.core.categorization;

import com.arsdigita.london.terms.portation.modules.core.web.CcmApplication;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.web.Application;

/**
 * Association class for the association between a {@link Domain} and a
 * {@link CcmObject}. Instances of this class should not be created manually.
 * Instead the methods provided by the {@code DomainManager} manager class
 * should be used.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/27/17
 */
public class DomainOwnership {

    private long ownershipId;
    private CcmApplication owner;
    private Domain domain;
    private String context;
    private long ownerOrder;
    private long domainOrder;

    public DomainOwnership() {
        
    }




    public long getOwnershipId() {
        return ownershipId;
    }

    public void setOwnershipId(final long ownershipId) {
        this.ownershipId = ownershipId;
    }

    public CcmApplication getOwner() {
        return owner;
    }

    public void setOwner(final CcmApplication owner) {
        this.owner = owner;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(final Domain domain) {
        this.domain = domain;
    }

    public String getContext() {
        return context;
    }

    public void setContext(final String context) {
        this.context = context;
    }

    public long getOwnerOrder() {
        return ownerOrder;
    }

    public void setOwnerOrder(final long ownerOrder) {
        this.ownerOrder = ownerOrder;
    }

    public long getDomainOrder() {
        return domainOrder;
    }

    public void setDomainOrder(final long domainOrder) {
        this.domainOrder = domainOrder;
    }
}
