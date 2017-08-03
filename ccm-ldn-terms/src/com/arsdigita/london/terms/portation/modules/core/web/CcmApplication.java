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
package com.arsdigita.london.terms.portation.modules.core.web;

import com.arsdigita.london.terms.portation.conversion.NgCoreCollection;
import com.arsdigita.london.terms.portation.modules.core.categorization.DomainOwnership;
import com.arsdigita.london.terms.portation.modules.core.core.Resource;
import com.arsdigita.portation.Portable;
import com.arsdigita.web.Application;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/27/17
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = CcmApplicationIdResolver.class,
                  property = "uuid")
public class CcmApplication extends Resource implements Portable {

    private String applicationType;
    private String primaryUrl;
    @JsonIgnore
    private List<DomainOwnership> domains;


    public CcmApplication(Application trunkObject) {
        super(trunkObject);

        this.applicationType = trunkObject.getApplicationType().toString();
        this.primaryUrl = trunkObject.getPrimaryURL();

        this.domains = new ArrayList<>();

        NgCoreCollection.ccmApplications.put(getObjectId(), this);
    }


    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(final String applicationType) {
        this.applicationType = applicationType;
    }

    public String getPrimaryUrl() {
        return primaryUrl;
    }

    public void setPrimaryUrl(final String primaryUrl) {
        this.primaryUrl = primaryUrl;
    }

    public List<DomainOwnership> getDomains() {
        return domains;
    }

    public void setDomains(final List<DomainOwnership> domains) {
        this.domains = domains;
    }
}
