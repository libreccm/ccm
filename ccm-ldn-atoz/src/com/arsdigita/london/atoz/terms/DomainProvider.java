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
 */

package com.arsdigita.london.atoz.terms;

import com.arsdigita.atoz.AtoZGenerator;
import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.london.terms.Domain;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * 
 * 
 */
public class DomainProvider extends AtoZProvider {
    
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.london.atoz.terms.DomainProvider";

    public static final String DOMAIN = "domain";

    public DomainProvider() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected DomainProvider(String type) {
        super(type);
    }
    
    public DomainProvider(DataObject obj) {
        super(obj);
    }

    public DomainProvider(OID oid) {
        super(oid);
    }

    public static DomainProvider create(String title,
                                        String description,
                                        Domain domain) {
        DomainProvider provider = new DomainProvider();
        provider.setup(title,
                       description,
                       domain);
        return provider;
    }

    protected void setup(String title,
                         String description,
                         Domain domain) {
        super.setup(title, description);
        setAssociation(DOMAIN, domain);
    }

    public Domain getDomain() {
        return (Domain)DomainObjectFactory.newInstance((DataObject)get(DOMAIN));
    }

    public void setDomain(Domain domain) {
        setAssociation(DOMAIN, domain);
    }
    
    public AtoZGenerator getGenerator() {
        return new DomainGenerator(this);
    }
    
}
