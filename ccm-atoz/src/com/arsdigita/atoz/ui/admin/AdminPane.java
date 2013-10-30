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

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.parameters.BigDecimalParameter;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.util.Assert;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.atoz.AtoZProviderType;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @depcreated UI integrated into Application tab at /ccm/admin. This class is now obsolete 
 * and has been replaced by {@link AtoZAdminPane}. This class is kept here for now, but will be 
 * removed in a further release, together with the Admin UI at /ccm/atoz/admin.
 */
public class AdminPane extends SimpleContainer {

    private static final String XMLNS = "http://xmlns.redhat.com/atoz/1.0";
    private ACSObjectSelectionModel m_provider;
    private Map m_providerCreateMap;
    private Map m_providerAdminMap;
    //private ProviderList m_providerList;
    private AtoZProviderTable m_providerTable;
    private ProviderCreateForm m_createForm;

    
    public AdminPane(BigDecimalParameter provider) {
        super("atoz:adminPane", XMLNS);

        m_provider = new ACSObjectSelectionModel(provider);
        m_provider.addChangeListener(new ProviderEditStart());

//        m_providerList = new ProviderList(m_provider);
//        add(m_providerList);

        m_providerTable = new AtoZProviderTable(m_provider);
        add(m_providerTable);

        m_createForm = new ProviderCreateForm();
        m_createForm.addCompletionListener(new ProviderCreateComplete());
        add(m_createForm);

        AtoZProviderType[] providers = AtoZ.getProviderTypes();
        m_providerCreateMap = new HashMap();
        m_providerAdminMap = new HashMap();

        for (int i = 0; i < providers.length; i++) {
            ProviderAdmin admin = providers[i].createProviderAdmin(m_provider);
            admin.addCompletionListener(new ProviderAdminComplete(admin));
            m_providerAdminMap.put(providers[i].getProvider(), admin);
            add(admin);

            AbstractProviderForm create = providers[i].createProviderCreate(m_provider);
            create.addCompletionListener(new ProviderAdminComplete(create));
            m_providerCreateMap.put(providers[i].getProvider(), create);
            add(create);
        }
    }

    @Override
    public void register(Page p) {
        super.register(p);

        Iterator providers = m_providerAdminMap.values().iterator();
        while (providers.hasNext()) {
            ProviderAdmin admin = (ProviderAdmin) providers.next();
            p.setVisibleDefault(admin, false);
        }

        providers = m_providerCreateMap.values().iterator();
        while (providers.hasNext()) {
            AbstractProviderForm create = (AbstractProviderForm) providers.next();
            p.setVisibleDefault(create, false);
        }
    }

    private class ProviderEditStart implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            PageState state = e.getPageState();

            AtoZProvider provider = (AtoZProvider) m_provider.getSelectedObject(state);

            if (provider == null) {
                return;
            }

            ProviderAdmin admin = (ProviderAdmin) m_providerAdminMap.get(provider.getClass());
            Assert.exists(admin, ProviderAdmin.class);

            admin.setVisible(state, true);
            m_createForm.setVisible(state, false);
            //m_providerList.setVisible(state, false);
            m_providerTable.setVisible(state, false);
        }

    }

    private class ProviderCreateComplete implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            Class provider = m_createForm.getProviderType(state);
            Assert.exists(provider, Class.class);

            AbstractProviderForm create = (AbstractProviderForm) m_providerCreateMap.get(provider);
            Assert.exists(create, AbstractProviderForm.class);

            create.setVisible(state, true);
            m_createForm.setVisible(state, false);
            //m_providerList.setVisible(state, false);
            m_providerTable.setVisible(state, false);
        }

    }

    private class ProviderAdminComplete implements ActionListener {

        private Component m_admin;

        public ProviderAdminComplete(Component admin) {
            m_admin = admin;
        }

        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            m_admin.setVisible(state, false);
            m_createForm.setVisible(state, true);
            //m_providerList.setVisible(state, true);
            m_providerTable.setVisible(state, true);
            m_provider.clearSelection(state);
        }

    }
}
