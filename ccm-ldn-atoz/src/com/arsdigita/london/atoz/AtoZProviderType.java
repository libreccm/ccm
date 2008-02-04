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


import com.arsdigita.london.atoz.ui.admin.ProviderForm;
import com.arsdigita.london.atoz.ui.admin.ProviderAdmin;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;

public class AtoZProviderType {
    
    private String m_title;
    private String m_description;
    private Class m_provider;
    private Class m_providerCreate;
    private Class m_providerAdmin;

    public AtoZProviderType(String title,
                            String description,
                            Class provider,
                            Class providerCreate,
                            Class providerAdmin) {
        Assert.truth(AtoZProvider.class.isAssignableFrom(provider),
                     "provider is a subclass of AtoZProvider");
        Assert.truth(ProviderAdmin.class.isAssignableFrom(providerAdmin),
                     "providerAdmin is a subclass of ProviderAdmin");
        Assert.truth(ProviderForm.class.isAssignableFrom(providerCreate),
                     "providerCreate is a subclass of ProviderForm");

        m_title = title;
        m_description = description;
        m_provider = provider;
        m_providerAdmin = providerAdmin;
        m_providerCreate = providerCreate;
    }

    public String getTitle() {
        return m_title;
    }
    
    public String getDescription() {
        return m_description;
    }
    
    public Class getProvider() {
        return m_provider;
    }

    public Class getProviderCreate() {
        return m_providerCreate;
    }
    
    public Class getProviderAdmin() {
        return m_providerAdmin;
    }
    
    public AtoZProvider createProvider() {
        return (AtoZProvider)Classes.newInstance(m_provider);
    }

    public ProviderForm createProviderCreate(ACSObjectSelectionModel provider) {
        return (ProviderForm)Classes
            .newInstance(m_providerCreate,
                         new Class[] { ACSObjectSelectionModel.class },
                         new Object[] { provider });
    }
    public ProviderAdmin createProviderAdmin(ACSObjectSelectionModel provider) {
        return (ProviderAdmin)Classes
            .newInstance(m_providerAdmin,
                         new Class[] { ACSObjectSelectionModel.class },
                         new Object[] { provider });
    }
}
