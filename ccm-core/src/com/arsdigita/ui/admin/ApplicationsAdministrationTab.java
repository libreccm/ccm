/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SplitPanel;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author pb
 */
public class ApplicationsAdministrationTab extends BoxPanel
                                   implements AdminConstants, ChangeListener {

    private GlobalizedMessage m_title;

    
    /**
     * Constructor
     */
    public ApplicationsAdministrationTab() {

        // m_title = "TEST für ein neues Pannel";
        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title", "Sitemap");
    //  m_componentList = new ArrayList();
    //  m_keys = new ArrayList();


        BoxPanel box = new BoxPanel();
        box.setClassAttr("main");

        SplitPanel panel = new SplitPanel();
        panel.setClassAttr("sidebarNavPanel");
        panel.setRightComponent(box);

    
    }
    
    /** 
     * 
     * @param e 
     */
    public void stateChanged(ChangeEvent e) {

        PageState ps = e.getPageState();
     // String key = (String) m_tree.getSelectedKey(ps);
        // added cg - reset existing group add panel to the search screen 
        // when a new group is selected from the tree
     // ps.setValue(GROUP_ID_PARAM, new BigDecimal(key));
     // int selectedIndex = Integer.parseInt((String) m_list.getSelectedKey(ps));
     // setTab(selectedIndex, ps);
    }



}
