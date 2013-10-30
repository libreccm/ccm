/*
 * Copyright (c) 2013 Jens Pelzetter
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
 *
 */
package com.arsdigita.atoz.ui.admin;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.atoz.AtoZProviderType;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;
import com.arsdigita.util.Assert;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class AtoZAdminPane extends SimpleContainer {

    private final ACSObjectSelectionModel providerSelectionModel;
    private final BigDecimalParameter providerParam;
    private final Map providerCreateMap;
    private final Map providerAdminMap;
    private final AtoZProviderTable providerTable;
    private final ProviderCreateForm createForm;

    public AtoZAdminPane(final ApplicationInstanceAwareContainer parent,
                         final BigDecimalParameter providerParam) {
        super();
        
        this.providerParam = providerParam;

        providerSelectionModel = new ACSObjectSelectionModel(providerParam);
        providerSelectionModel.addChangeListener(new ProviderEditStart());

        providerTable = new AtoZProviderTable(providerSelectionModel, parent);
        add(providerTable);

        createForm = new ProviderCreateForm();
        createForm.addCompletionListener(new ProviderCreateComplete());
        add(createForm);

        AtoZProviderType[] providerTypes = AtoZ.getProviderTypes();
        providerCreateMap = new HashMap();
        providerAdminMap = new HashMap();

        for (AtoZProviderType providerType : providerTypes) {
            final ProviderAdmin admin = providerType.createProviderAdmin(providerSelectionModel);
            admin.addCompletionListener(new ProviderAdminComplete(admin));
            providerAdminMap.put(providerType.getProvider(), admin);
            add(admin);

            final AbstractProviderForm create = providerType.createProviderCreate(
                    providerSelectionModel, parent);
            create.addCompletionListener(new ProviderAdminComplete(create));
            providerCreateMap.put(providerType.getProvider(), create);
            add(create);

        }
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(providerParam);
        
        Iterator providers = providerAdminMap.values().iterator();
        while (providers.hasNext()) {
            final ProviderAdmin admin = (ProviderAdmin) providers.next();
            page.setVisibleDefault(admin, false);
        }

        providers = providerCreateMap.values().iterator();
        while (providers.hasNext()) {
            final AbstractProviderForm create = (AbstractProviderForm) providers.next();
            page.setVisibleDefault(create, false);
        }
    }

    private class ProviderEditStart implements ChangeListener {

        public ProviderEditStart() {
            //Nothing
        }

        @Override
        public void stateChanged(final ChangeEvent event) {
            PageState state = event.getPageState();

            AtoZProvider provider = (AtoZProvider) providerSelectionModel.getSelectedObject(state);

            if (provider == null) {
                return;
            }

            ProviderAdmin admin = (ProviderAdmin) providerAdminMap.get(provider.getClass());
            Assert.exists(admin, ProviderAdmin.class);

            admin.setVisible(state, true);
            createForm.setVisible(state, false);
            //m_providerList.setVisible(state, false);
            providerTable.setVisible(state, false);
        }

    }

    private class ProviderCreateComplete implements ActionListener {

        public ProviderCreateComplete() {
            //Nothing
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            final PageState state = event.getPageState();

            final Class provider = createForm.getProviderType(state);
            Assert.exists(provider, Class.class);

            final AbstractProviderForm create = (AbstractProviderForm) providerCreateMap.get(
                    provider);
            Assert.exists(create, AbstractProviderForm.class);

            create.setVisible(state, true);
            createForm.setVisible(state, false);
            //m_providerList.setVisible(state, false);
            providerTable.setVisible(state, false);
        }

    }

    private class ProviderAdminComplete implements ActionListener {

        private final Component admin;

        public ProviderAdminComplete(final Component admin) {
            this.admin = admin;
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            PageState state = event.getPageState();
            admin.setVisible(state, false);
            createForm.setVisible(state, true);
            //m_providerList.setVisible(state, true);
            providerTable.setVisible(state, true);
            providerSelectionModel.clearSelection(state);
        }

    }
}
