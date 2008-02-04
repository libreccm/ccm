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

package com.arsdigita.london.atoz;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainCollection;

import com.arsdigita.web.Application;

import com.arsdigita.xml.Element;

import com.arsdigita.util.Assert;

import java.util.List;
import java.util.ArrayList;

public class AtoZ extends Application {
    
    public static final String BASE_DATA_OBJECT_TYPE 
        = "com.arsdigita.london.atoz.AtoZ";

    private static final AtoZConfig s_config = new AtoZConfig();

    static {
        s_config.load();
    }
    
    public static final AtoZConfig getConfig() {
        return s_config;
    }
    
    
    public static final String PROVIDERS = "atozProviders";
    public static final String SORT_KEY = "sortKey";

    public AtoZ(DataObject obj) {
        super(obj);
    }

    public AtoZ(OID oid) {
        super(oid);
    }
    
    public String getContextPath() {
        return "/ccm-ldn-atoz";
    }
    
    public String getServletPath() {
        return "/files";
    }

    public void addProvider(AtoZProvider provider) {
        DataObject link = add(PROVIDERS, provider);
        // a little insert even magic generates this
        //link.set(SORT_KEY, new Integer(1));
    }

    public void removeProvider(AtoZProvider provider) {
        remove(PROVIDERS, provider);
    }

    public DomainCollection getProviders() {
        DataCollection providers = (DataCollection)get(PROVIDERS);
        providers.addOrder("link." + SORT_KEY);
        return new DomainCollection(providers);
    }

    public AtoZGenerator[] getGenerators() {
        DataCollection providers = (DataCollection)get(PROVIDERS);
        
        List generators = new ArrayList();
        while (providers.next()) {
            AtoZProvider provider = (AtoZProvider)DomainObjectFactory
                .newInstance(providers.getDataObject());
            generators.add(provider.getGenerator());
        }
        
        return (AtoZGenerator[])generators.toArray(
            new AtoZGenerator[generators.size()]);
    }


    public static Element newElement(String name) {
        Assert.truth(name.indexOf(":") == -1, "name does not contain :");
        return new Element("atoz:" + name,
                           "http://xmlns.redhat.com/atoz/1.0");
    }
}

