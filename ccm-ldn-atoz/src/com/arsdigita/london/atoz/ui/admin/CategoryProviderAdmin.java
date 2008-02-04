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

package com.arsdigita.london.atoz.ui.admin;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Component;

public class CategoryProviderAdmin extends ProviderAdmin {

    private CategoryProviderForm m_detailsForm;

    private ActionLink m_editDetails;

    private ProviderDetails m_details;

    private CategoryProviderBlackList m_blackList;

    private CategoryProviderBlockForm m_blockForm;

    private ActionLink m_addBlock;

    private CategoryProviderContentTypeBlacklist m_ct_blackList;

    private CategoryProviderContentTypeBlockForm m_ct_blockForm;

    private ActionLink m_addCTBlock;

    private CategoryProviderAliasList m_aliasList;

    private CategoryProviderAliasForm m_aliasForm;

    private ActionLink m_addAlias;

    public CategoryProviderAdmin(ACSObjectSelectionModel provider) {
        super("categoryProviderAdmin", provider);

        m_details = new ProviderDetails(provider);
        m_detailsForm = new CategoryProviderForm(provider);
        m_editDetails = new ActionLink("Edit details");
        m_editDetails.setIdAttr("edit");

        add(m_details);
        add(m_detailsForm);
        add(m_editDetails);

        m_blackList = new CategoryProviderBlackList(provider);
        m_blockForm = new CategoryProviderBlockForm(provider);
        m_addBlock = new ActionLink("Add blocked category");
        m_addBlock.setIdAttr("addBlock");

        add(m_blackList);
        add(m_blockForm);
        add(m_addBlock);

        m_ct_blackList = new CategoryProviderContentTypeBlacklist(provider);
        m_ct_blockForm = new CategoryProviderContentTypeBlockForm(provider);
        m_addCTBlock = new ActionLink("Add blocked Content Types");
        m_addCTBlock.setIdAttr("addContentTypeBlock");

        add(m_ct_blackList);
        add(m_ct_blockForm);
        add(m_addCTBlock);

        m_aliasList = new CategoryProviderAliasList(provider);
        m_aliasForm = new CategoryProviderAliasForm(provider);
        m_addAlias = new ActionLink("Add category alias");
        m_addAlias.setIdAttr("addAlias");

        add(m_aliasList);
        add(m_aliasForm);
        add(m_addAlias);

        m_editDetails.addActionListener(new CategoryProviderEditStart());
        m_detailsForm.addCompletionListener(new CategoryProviderEditComplete());

        m_addBlock.addActionListener(new CategoryBlockStart());
        m_blockForm.addCompletionListener(new CategoryBlockComplete());

        m_addCTBlock.addActionListener(new ContentTypeBlockStart());
        m_ct_blockForm.addCompletionListener(new ContentTypeBlockComplete());

        m_addAlias.addActionListener(new CategoryAliasStart());
        m_aliasForm.addCompletionListener(new CategoryAliasComplete());
    }

    public void register(Page p) {
        super.register(p);

        p.setVisibleDefault(m_detailsForm, false);
        p.setVisibleDefault(m_blockForm, false);
        p.setVisibleDefault(m_ct_blockForm, false);
        p.setVisibleDefault(m_aliasForm, false);
    }

    private void switchMode(PageState state, Component form, boolean active) {
        form.setVisible(state, active);

        m_details.setVisible(state, !active);
        m_editDetails.setVisible(state, !active);

        m_blackList.setVisible(state, !active);
        m_addBlock.setVisible(state, !active);

        m_ct_blackList.setVisible(state, !active);
        m_addCTBlock.setVisible(state, !active);

        m_aliasList.setVisible(state, !active);
        m_addAlias.setVisible(state, !active);
    }

    private class CategoryProviderEditStart implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_detailsForm, true);
        }
    }

    private class CategoryProviderEditComplete implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_detailsForm, false);
        }
    }

    private class CategoryBlockStart implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_blockForm, true);
        }
    }

    private class CategoryBlockComplete implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_blockForm, false);
        }
    }

    private class CategoryAliasStart implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_aliasForm, true);
        }
    }

    private class CategoryAliasComplete implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_aliasForm, false);
        }
    }

    private class ContentTypeBlockStart implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_ct_blockForm, true);
        }
    }

    private class ContentTypeBlockComplete implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            switchMode(state, m_ct_blockForm, false);

        }
    }
}
