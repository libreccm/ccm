/*
 * Copyright (C) 2006 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum.categorised;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

// import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
// import com.arsdigita.categorization.ui.ACSObjectCategoryForm;
import com.arsdigita.categorization.ui.ACSObjectCategorySummary;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.web.RedirectSignal;

/**
 * 
 * 
 *
 * @author Chris Gilbert
 * @version $Id: $
 */
public class AssignCategoryView extends SimpleContainer implements Constants {

	private ACSObjectCategorySummary m_summary;
    private SimpleComponent m_add;
    private BigDecimalParameter m_root;
    private StringParameter m_mode;
    Logger s_log = Logger.getLogger(AssignCategoryView.class);

    /**
     * Constructor.
     * 
     */
    public AssignCategoryView() {
        super();

        m_root = new BigDecimalParameter("root");
        m_mode = new StringParameter("mode");

        m_summary = new ForumCategorySummary();
        s_log.debug("m_summary in the constructor is : " + m_summary.toString());
        
        m_summary.registerAction(ACSObjectCategorySummary.ACTION_ADD,
                                 new AddActionListener("plain"));
        m_summary.registerAction(ACSObjectCategorySummary.ACTION_ADD_JS,
                                 new AddActionListener("javascript"));
        m_add = new ForumTermPicker(m_root, m_mode); 
        
        m_add.addCompletionListener(new ResetListener());
        
        add(m_summary);
        add(m_add);
    }

    /**
     *
     * @param p
     */
    @Override
    public void register(Page p) {
        super.register(p);
        
        p.setVisibleDefault(m_add, false);
		
        p.addGlobalStateParam(m_root);
        p.addGlobalStateParam(m_mode);
    }

    /**
     * 
     * @param state
     */
    public void reset(PageState state) {
        state.setValue(m_root, null);
        state.setValue(m_mode, null);
        
        m_summary.setVisible(state, true);
        m_add.setVisible(state, false);        
    }

    /** 
     * 
     */
    private class AddActionListener implements ActionListener {
        private String m_mode;

        public AddActionListener(String mode) {
            m_mode = mode;
        }

        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            
            state.setValue(m_root,
                           new BigDecimal(state.getControlEventValue()));

            state.setValue(AssignCategoryView.this.m_mode,
                           m_mode);
			s_log.debug("m_summary in the action perform is : " + m_summary.toString());
            m_summary.setVisible(state, false);
            m_add.setVisible(state, true);
        }
    }
    
    /** 
     * 
     */
    private class ResetListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            reset(state);
            throw new RedirectSignal(state.toURL(), true);
        }
    }
	

}
