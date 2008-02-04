/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.forum.ui.admin;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.Group;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.event.FormSectionEvent;
import java.math.BigDecimal;

public abstract class GroupMemberPicker extends UserPicker {
    
    private StringParameter m_restrictParam;

    private SingleSelect m_restrict;
    private Option m_both;
    private Option m_users;
    private Option m_groups;

    public GroupMemberPicker() {
    }

    protected void addWidgets() {
        m_restrictParam = new StringParameter("restrict");

        m_restrict = new SingleSelect("restrict1");
        m_both = new Option("both", "Users & Groups");
        m_users = new Option("users", "Users Only");
        m_groups = new Option("groups", "Groups Only");
        
        m_restrict.addOption(m_both);
        m_restrict.addOption(m_users);
        m_restrict.addOption(m_groups);
        m_restrict.setOptionSelected(m_users);
        
        BoxPanel opt = new BoxPanel(BoxPanel.HORIZONTAL);
        opt.add(new Label("Search for"));
        opt.add(m_restrict);
        add(opt);

        super.addWidgets();
    }

    public void register( Page p ) {
        super.register( p );

        p.addGlobalStateParam(m_restrictParam);
    }

    public void init( FormSectionEvent e ) {
        PageState ps = e.getPageState();

        super.init(e);
        
        m_restrict.setValue(ps, ps.getValue(m_restrictParam));
    }

    public void process( FormSectionEvent e ) {
        PageState ps = e.getPageState();
        
        super.process(e);

        ps.setValue(m_restrictParam, m_restrict.getValue(ps));
    }

    protected abstract Group getGroup(PageState ps);

    protected DataQuery getUsers( PageState ps, String search ) {
        String bdot = Party.BASE_DATA_OBJECT_TYPE;
        if (m_users.getValue().equals(ps.getValue(m_restrictParam))) {
            bdot = User.BASE_DATA_OBJECT_TYPE;
        } else if (m_groups.getValue().equals(ps.getValue(m_restrictParam))) {
            bdot = Group.BASE_DATA_OBJECT_TYPE;
        }

        DataCollection parties = SessionManager.getSession()
            .retrieve(bdot);
        
        CompoundFilter or = parties.getFilterFactory().or();

        Filter nameFilter = parties.getFilterFactory()
            .simple("lower(displayName) like lower(:term)");
        nameFilter.set("term", "%" + search + "%");
        Filter emailFilter = parties.getFilterFactory()
            .simple("lower(primaryEmail) like lower(:term)");
        emailFilter.set("term", "%" + search + "%");
        
        or.addFilter(nameFilter);
        or.addFilter(emailFilter);
        parties.addFilter(or);
        parties.addOrder(ACSObject.DISPLAY_NAME);

        return parties;
    }

    protected String getDisplayName( DataQuery q ) {
        DataObject party = ((DataCollection)q).getDataObject();
        return (String)party.get(ACSObject.DISPLAY_NAME);
    }

    protected String getKey( DataQuery q ) {
        DataObject party = ((DataCollection)q).getDataObject();
        return party.get(ACSObject.ID).toString();
    }

    protected void addUser( PageState ps, BigDecimal userID ) {
        Party party = null;
        
        try {
            party = (Party)DomainObjectFactory.newInstance(
                new OID(Party.BASE_DATA_OBJECT_TYPE,
                        userID));
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("cannot find user", ex);
        }

        Group group = getGroup(ps);
        group.addMemberOrSubgroup(party);
        group.save();
    }
}
