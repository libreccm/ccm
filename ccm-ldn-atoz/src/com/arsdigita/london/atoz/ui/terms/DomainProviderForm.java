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

package com.arsdigita.london.atoz.ui.terms;

import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.atoz.ui.admin.ProviderForm;
import com.arsdigita.london.atoz.terms.DomainProvider;
import com.arsdigita.london.terms.Domain;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;

import java.util.TooManyListenersException;

/**
 * 
 */
public class DomainProviderForm extends ProviderForm {

    private SingleSelect m_domain;

    /**
     * Constructor
     */
    public DomainProviderForm(ACSObjectSelectionModel provider) {
        super("domainProvider", 
              DomainProvider.class, provider);

        setMetaDataAttribute("title", "Domain provider properties");
    }
        
    /**
     * 
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();
        
        m_domain = new SingleSelect("domain");
        m_domain.setMetaDataAttribute("label", "Domain");
        try {
            m_domain.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        SingleSelect s = (SingleSelect)e.getTarget();
                        
                        DataCollection domains = SessionManager.getSession()
                            .retrieve(Domain.BASE_DATA_OBJECT_TYPE);
                        s.addOption(new Option(null, "-- select one --"));
                        while (domains.next()) {
                            s.addOption(
                                new Option(
                                    domains.getDataObject().getOID().toString(),
                                    (String)domains.get(Domain.TITLE)));
                        }
                    }
                });
        } catch (TooManyListenersException ex) {
            throw new RuntimeException("cannot happen");
        }
        m_domain.addValidationListener(new NotNullValidationListener());
        add(m_domain);
    }
    
    /**
     * 
     * @param state
     * @param provider 
     */
    @Override
    protected void processWidgets(PageState state,
                                  AtoZProvider provider) {
        super.processWidgets(state, provider);
        
        DomainProvider myprovider = (DomainProvider)provider;
        
        String oid = (String)m_domain.getValue(state);
        myprovider.setDomain(
            (Domain)DomainObjectFactory.newInstance(
                OID.valueOf(oid)));
    }
   
    /**
     * 
     * @param state
     * @param provider 
     */
    @Override
    protected void initWidgets(PageState state,
                               AtoZProvider provider) {
        super.initWidgets(state, provider);

        DomainProvider myprovider = (DomainProvider)provider;
        if (provider != null) {
            
            Domain domain = myprovider.getDomain();
            m_domain.setValue(state, domain.getOID().toString());
        }
    }

}
