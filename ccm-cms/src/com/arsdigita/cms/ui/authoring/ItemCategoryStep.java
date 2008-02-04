/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.web.RedirectSignal;


import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class ItemCategoryStep extends SimpleContainer 
    implements Resettable{
    
    private static final Logger s_log = Logger.getLogger(ItemCategoryStep.class);

    private ItemCategorySummary m_summary;
    private SimpleComponent m_add;
    private SimpleComponent[] m_extensionSummaries;
    private SimpleComponent[] m_extensionForms;
    private int m_extensionsCount;
    private BigDecimalParameter m_root;
    private StringParameter m_mode;

    public ItemCategoryStep(ItemSelectionModel itemModel, 
                            AuthoringKitWizard parent) {
        super("cms:categoryStep", CMS.CMS_XML_NS);

        m_root = new BigDecimalParameter("root");
        m_mode = new StringParameter("mode");

        m_summary = new ItemCategorySummary();
        m_summary.registerAction(ItemCategorySummary.ACTION_ADD,
                new AddActionListener("plain"));
        m_summary.registerAction(ItemCategorySummary.ACTION_ADD_JS,
                new AddActionListener("javascript"));

        Class addForm = ContentSection.getConfig().getCategoryAuthoringAddForm();
        m_add = (SimpleComponent)
            Classes.newInstance(addForm,
                                new Class[] { BigDecimalParameter.class,
                                              StringParameter.class },
                                new Object[] { m_root, m_mode });
        m_add.addCompletionListener(new ResetListener());

        Class extensionClass = ContentSection.getConfig().getCategoryAuthoringExtension();
        ItemCategoryExtension extension = (ItemCategoryExtension)
            Classes.newInstance(extensionClass);
        
        m_extensionSummaries = extension.getSummary();
        m_extensionForms = extension.getForm();
        int nSummaries = m_extensionSummaries.length;
        int nForms= m_extensionForms.length;
        Assert.truth(nSummaries==nForms, "invalid CategoryStep extension");
        m_extensionsCount = nForms;
        for (int i=0;i<m_extensionsCount;i++) {
            m_extensionSummaries[i].addCompletionListener(new ExtensionListener(i));
            m_extensionForms[i].addCompletionListener(new ResetListener());
            add(m_extensionSummaries[i]);
            add(m_extensionForms[i]);
        }
        add(m_summary);
        add(m_add);
    }

    public void register(Page p) {
        super.register(p);
        
        p.setVisibleDefault(m_add, false);
        for (int i=0;i<m_extensionsCount;i++) {
            p.setVisibleDefault(m_extensionForms[i], false);    
        }
        p.addGlobalStateParam(m_root);
        p.addGlobalStateParam(m_mode);
    }

    public void reset(PageState state) {
        state.setValue(m_root, null);
        state.setValue(m_mode, null);
        
        m_summary.setVisible(state, true);
        m_add.setVisible(state, false);
        for (int i=0;i<m_extensionsCount;i++) {
            m_extensionSummaries[i].setVisible(state, true);
            m_extensionForms[i].setVisible(state, false);
        }
    }

    private class AddActionListener implements ActionListener {
        private String m_mode;

        public AddActionListener(String mode) {
            m_mode = mode;
        }

        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            
            state.setValue(m_root,
                           new BigDecimal(state.getControlEventValue()));

            state.setValue(ItemCategoryStep.this.m_mode,
                           m_mode);

            m_summary.setVisible(state, false);
            m_add.setVisible(state, true);
            for (int i=0;i<m_extensionsCount;i++) {
                m_extensionSummaries[i].setVisible(state, false);
                m_extensionForms[i].setVisible(state, false);
            }
        }
    }

    private class ResetListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            reset(state);
            throw new RedirectSignal(state.toURL(), true);
        }
    }

    private class ExtensionListener implements ActionListener {
        int extensionIndex;
        public ExtensionListener(int i) {
            extensionIndex = i;
        }
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            m_summary.setVisible(state, false);
            m_add.setVisible(state, false);
            for (int i=0;i<m_extensionsCount;i++) {
                m_extensionSummaries[i].setVisible(state, false);
            }
            m_extensionForms[extensionIndex].setVisible(state, true);
        }
    }

}
