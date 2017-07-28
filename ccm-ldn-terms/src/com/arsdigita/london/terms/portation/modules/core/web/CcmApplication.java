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

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.london.terms.portation.modules.core.categorization.DomainOwnership;
import com.arsdigita.london.terms.portation.modules.core.core.Resource;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/27/17
 */
public class CcmApplication extends Resource {

    private String applicationType;
    private String primaryUrl;
    private List<DomainOwnership> domains;


    public CcmApplication(ACSObject trunkObject) {
        super(trunkObject);
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
