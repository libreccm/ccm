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

package com.arsdigita.atoz.ui.admin;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProviderType;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;

import com.arsdigita.bebop.parameters.NotNullValidationListener;

import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;

import com.arsdigita.util.Classes;
import com.arsdigita.util.Assert;


public class ProviderCreateForm extends Form {
    
    private OptionGroup m_providerType;
    
    public ProviderCreateForm() {
        super("providerCreate", new SimpleContainer());
        
        setRedirecting(true);

        m_providerType = new SingleSelect("providers");
        m_providerType.addValidationListener(new NotNullValidationListener());
        m_providerType.addOption(new Option(null, "--Select one--"));

        AtoZProviderType[] providers = AtoZ.getConfig().getProviderTypes();
        for (int i = 0 ; i < providers.length ; i++) {
            m_providerType.addOption(
                new Option(providers[i].getProvider().getName(),
                           providers[i].getTitle())
            );
        }

        add(m_providerType);
        add(new Submit("create", "Create"));
        addProcessListener(new ProviderCreateProcess());
    }

    public Class getProviderType(PageState state) {
        String providerName = (String)m_providerType.getValue(state);
        if (providerName != null &&
            !"".equals(providerName)) {
            Class provider = Classes.loadClass(providerName);
            Assert.exists(provider, Class.class);
            return provider;
        }
        return null;
    }

    private class ProviderCreateProcess implements FormProcessListener {
        public void process(FormSectionEvent e) {
            PageState state  = e.getPageState();
            
            if (getProviderType(state) != null) {
                fireCompletionEvent(state);
            }
        }
    }
}
