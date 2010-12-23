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
 *
 */
package com.arsdigita.ui.admin;


import com.arsdigita.ui.util.GlobalizationUtil ; 

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Constructs the panel for administration of groups.
 *
 * @author David Dao
 *
 */
class GroupAdministrationTab extends BoxPanel
    implements AdminConstants,
                       ChangeListener {

    private static final Logger s_log =
        Logger.getLogger(GroupAdministrationTab.class);

    private Tree m_tree;
    private SearchAndList m_subMemberSearch;
    private ActionLink m_addSubmemberLink;

    private Component m_groupInfoPanel;
    private Component m_subGroupPanel;
    private Component m_subMemberPanel;
    private Component m_extremeActionPanel;
    private Component m_groupAddPanel;
    private Component m_groupEditPanel;
    private Component m_groupDeleteFailedPanel;
    private Component m_existingGroupAddPanel;
    private ExistingGroupAddPane m_existingGroupAdd;
    private ArrayList m_panelList = new ArrayList();

    private RequestLocal m_group;

    public void register(Page p) {
        for (int i = 0; i < m_panelList.size(); i++) {
            p.setVisibleDefault((Component) m_panelList.get(i), false);
        }

        p.setVisibleDefault(m_groupAddPanel, true);
    }

    public Group getGroup(PageState ps) {
        return (Group) m_group.get(ps);
    }

    public void setGroup(PageState ps, Group group) {
        String id = group.getID().toString();
        m_group.set(ps, group);
        m_tree.setSelectedKey(ps, id);

        if ( !id.equals("-1") ) {
            expandGroups (ps, group);
            m_tree.expand("-1", ps);
        }
    }

    private void expandGroups (PageState ps, Group group) {
        m_tree.expand(group.getID().toString(), ps);

        GroupCollection superGroups = group.getSupergroups();
        Group superGroup = null;
        while (superGroups.next()) {
            superGroup = (Group)superGroups.getDomainObject();
            expandGroups (ps, superGroup);
        }
    }

    public GroupAdministrationTab() {
        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title", "Groups");

        m_group = new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    String key = (String) m_tree.getSelectedKey(ps);

                    Group group = null;

                    if (key != null) {
                        BigDecimal id = new BigDecimal(key);

                        try {
                            group = new Group(id);
                        } catch(DataObjectNotFoundException exc) {
                            // Silently ignore if group does not
                            // exist.
                        }
                    }
                    return group;
                }
            };

        BoxPanel c = new BoxPanel();
        c.setClassAttr("navbar");

        m_tree = new Tree(new GroupTreeModel());
        m_tree.addChangeListener(this);
        c.add(m_tree);

        add(c);

        SegmentedPanel rightSide = new SegmentedPanel();
        rightSide.setClassAttr("main");

        m_groupInfoPanel = buildGroupInfoPanel(rightSide);
        m_panelList.add(m_groupInfoPanel);

        m_groupEditPanel = buildGroupEditPanel(rightSide);
        m_panelList.add(m_groupEditPanel);

        m_subGroupPanel = buildSubGroupPanel(rightSide);
        m_panelList.add(m_subGroupPanel);

        m_groupAddPanel = buildGroupAddPanel(rightSide);
        m_panelList.add(m_groupAddPanel);
	
	m_existingGroupAddPanel = buildExistingGroupAddPanel(rightSide);
	m_panelList.add(m_existingGroupAddPanel);

        m_subMemberPanel = buildMemberListPanel(rightSide);
        m_panelList.add(m_subMemberPanel);

        m_extremeActionPanel = buildExtremeActionPanel(rightSide);
        m_panelList.add(m_extremeActionPanel);

        m_groupDeleteFailedPanel = buildGroupDeleteFailedPanel(rightSide);
        m_panelList.add(m_groupDeleteFailedPanel);

        add(rightSide);
    }

    public void displayAddGroupPanel(PageState ps) {
        hideAll(ps);
        m_groupAddPanel.setVisible(ps, true);
    }
    
    private void displayAddExistingGroupPanel(PageState ps) {
	hideAll(ps);
	m_existingGroupAddPanel.setVisible(ps, true);
    }

    public void displayEditPanel(PageState ps) {
        hideAll(ps);
        m_groupEditPanel.setVisible(ps, true);
    }

    public void displayGroupInfoPanel(PageState ps) {
        showAll(ps);
        m_groupEditPanel.setVisible(ps, false);
        m_groupAddPanel.setVisible(ps, false);
        m_groupDeleteFailedPanel.setVisible(ps, false);
	m_existingGroupAddPanel.setVisible(ps, false);
    }

    public void displayDeleteFailedPanel(PageState ps) {
        hideAll(ps);
        m_groupDeleteFailedPanel.setVisible(ps, true);
    }

    public void stateChanged(ChangeEvent e) {

        PageState ps = e.getPageState();
        String key = (String) m_tree.getSelectedKey(ps);
	// added cg - reset existing group add panel to the search screen when a new group is selected from the tree
	m_existingGroupAdd.showSearch(ps);
        if (key == null || key.equals("-1")) {
            /**
             * If root node is selected then display add
             * panel only.
             */
            displayAddGroupPanel(ps);
        } else {
            displayGroupInfoPanel(ps);
        }
        ps.setValue(GROUP_ID_PARAM, new BigDecimal(key));
    }

    /**
     * Build a panel to display group basic information.
     */
    private Component buildGroupInfoPanel(SegmentedPanel main) {
        BoxPanel body = new BoxPanel();

        body.add(new GroupInfo(this));
        ActionLink link = new ActionLink(EDIT_GROUP_LABEL);
        link.setClassAttr("actionLink");
        link.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    displayEditPanel(ps);
                }
            });
        body.add(link);


        return main.addSegment(GROUP_INFORMATION_HEADER, body);

    }

    /**
     * Build group edit form.
     */
    private Component buildGroupEditPanel(SegmentedPanel main) {

        return main.addSegment(GROUP_EDIT_HEADER, new GroupEditForm(this));
    }

    /**
     * Build panel to display direct subgroup information.
     */
    private Component buildSubGroupPanel(SegmentedPanel main) {
        BoxPanel body = new BoxPanel();
        BoxPanel labelStatus = new BoxPanel(BoxPanel.HORIZONTAL);
        labelStatus.add(SUBGROUP_COUNT_LABEL);

        Label countLabel = new Label("");
        countLabel.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState ps = e.getPageState();

                    Label t = (Label) e.getTarget();
                    Group g = getGroup(ps);
                    if (g != null) {
                        t.setLabel(String.valueOf(g.countSubgroups()));
                    }
                }
            });

        ActionLink status = new ActionLink(countLabel);
        status.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    String key = (String) m_tree.getSelectedKey(ps);
                    m_tree.expand(key, ps);
                }
            });
        labelStatus.add(status);


        body.add(labelStatus);


        List subGroupList = new List(new SubGroupListModelBuilder(this));
        subGroupList.setCellRenderer(new ListCellRenderer() {
	    public Component getComponent(
		List list,
		PageState state,
		Object value,
		String key,
		int index,
		boolean isSelected) {
		BoxPanel b = new BoxPanel(BoxPanel.HORIZONTAL);
		b.add(new Label(((Group) value).getName()));
		ControlLink removeLink = new ControlLink(REMOVE_SUBGROUP_LABEL);
		removeLink.setClassAttr("actionLink");
		b.add(removeLink);
		return b;
	    }
	});
	subGroupList.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		PageState ps = e.getPageState();
		String key = (String) ((List) e.getSource()).getSelectedKey(ps);

		if (key != null) {
	 	    BigDecimal groupID = new BigDecimal(key);
		    try {
			Group group = new Group(groupID);
			Group parent = getGroup(ps);
			if (parent != null) {
			    parent.removeSubgroup(group);
			    parent.save();
			}
		    } catch (DataObjectNotFoundException exc) {
		    }
	        }
	    }
	});
	body.add(subGroupList);

        ActionLink addLink = new ActionLink(ADD_SUBGROUP_LABEL);
        addLink.setClassAttr("actionLink");
        addLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();

                    displayAddGroupPanel(ps);
                }
            });

        body.add(addLink);
	
	// new actionlink and anonymous ActionListener class added cg
	ActionLink addExistingLink = new ActionLink(ADD_EXISTING_GROUP_TO_SUBGROUPS_LABEL);
	addExistingLink.setClassAttr("actionLink");
	addExistingLink.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		s_log.debug("Add existing group link pressed");
		PageState ps = e.getPageState();
		displayAddExistingGroupPanel(ps);
	    }
 
	});

	body.add(addExistingLink);
        return main.addSegment(SUBGROUP_HEADER, body);

    }

    /**
     * Build group add form.
     */
    private Component buildGroupAddPanel(SegmentedPanel main) {

        return main.addSegment(ADD_GROUP_LABEL, new GroupAddForm(m_tree, this));
    }

    private Component buildExistingGroupAddPanel(SegmentedPanel main) {
	m_existingGroupAdd = new ExistingGroupAddPane(m_tree, this);
	return main.addSegment(ADD_EXISTING_GROUP_TO_SUBGROUPS_LABEL, m_existingGroupAdd);
    }

    /**
     * Build group's member panel.
     */
    private Component buildMemberListPanel(SegmentedPanel main) {

        BoxPanel body = new BoxPanel() {
                public void register(Page p) {
                    p.setVisibleDefault(m_subMemberSearch, false);
                }
            };
        body.add(new SubMemberPanel(this));

        m_addSubmemberLink = new ActionLink(ADD_SUBMEMBER_LABEL);
        m_addSubmemberLink.setClassAttr("actionLink");
        m_addSubmemberLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    m_addSubmemberLink.setVisible(ps, false);
                    m_subMemberSearch.setVisible(ps, true);
                }
            });

        m_subMemberSearch = new SearchAndList("searchsubmember");
        m_subMemberSearch.setListModel(new UserSearchAndListModel());
        m_subMemberSearch.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    PageState ps = e.getPageState();

                    String key = (String) m_subMemberSearch.getSelectedKey(ps);
                    if (key != null) {
                        BigDecimal userID = new BigDecimal(key);

                        Group group = getGroup(ps);

                        if (group != null) {
                            try {
                                User user = User.retrieve(userID);
                                group.addMember(user);
                                group.save();
                            } catch (DataObjectNotFoundException exc) {
                                // Ignore if user id is not valid
                            } catch (PersistenceException pexc) {
                                // Display error message that user
                                // already existed in group.
                            }
                        }
                    }
                    m_subMemberSearch.reset(ps);
                    m_subMemberSearch.setVisible(ps, false);
                    m_addSubmemberLink.setVisible(ps, true);
                }
            });

        body.add(m_subMemberSearch);
        body.add(m_addSubmemberLink);
        return main.addSegment(SUBMEMBER_HEADER, body);

    }

    /**
     * Build extreme action panel.
     */
    private Component buildExtremeActionPanel(SegmentedPanel main) {
        BoxPanel body = new BoxPanel();

        ActionLink deleteLink = new ActionLink(DELETE_GROUP_LABEL);
        deleteLink.setClassAttr("actionLink");
        deleteLink.setConfirmation(GROUP_DELETE_CONFIRMATION);
        deleteLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    PageState ps = e.getPageState();

                    Group g = (Group) m_group.get(ps);
                    if (g != null) {
                        try {
                            g.delete();
                            m_tree.setSelectedKey(ps, "-1");
                        } catch(PersistenceException exc) {
                            s_log.warn("Error deleting subgroup", exc);
                            displayDeleteFailedPanel(ps);
                        }
                    }
                    // Select root node

                }
            });
        body.add(deleteLink);
        return main.addSegment(GROUP_EXTREME_ACTIONS_HEADER,
                               body);

    }

    /**
     * Build a panel to display an error message when unable to delete
     * group.
     */
    private Component buildGroupDeleteFailedPanel(SegmentedPanel main) {
        ActionLink link = new ActionLink(GROUP_ACTION_CONTINUE);
        link.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    displayGroupInfoPanel(ps);
                }
            });
        link.setClassAttr("actionLink");

        Label label = new Label(GROUP_DELETE_FAILED_MSG);
        label.setClassAttr("deleteFailedMessage");

        BoxPanel p = new BoxPanel();
        p.add(label);
        p.add(link);

        return main.addSegment(GROUP_DELETE_FAILED_HEADER, p);
    }

    /**
     * Hides all components of the in preparation for
     * turning selected components back on.
     */
    private void hideAll(PageState ps) {
        for (int i = 0; i < m_panelList.size(); i++) {
            ((Component) m_panelList.get(i)).setVisible
                (ps, false);
        }
    }
    /**
     * Show all components of the in preparation for
     * turning visibility of selected components off .
     */
    private void showAll(PageState ps) {
        for (int i = 0; i < m_panelList.size(); i++) {
            ((Component) m_panelList.get(i)).setVisible
                (ps, true);
        }
    }

}

class SubGroupListModelBuilder extends LockableImpl
    implements ListModelBuilder {

    private GroupAdministrationTab m_parent;
    public SubGroupListModelBuilder(GroupAdministrationTab parent) {
        m_parent = parent;
    }

    public ListModel makeModel(List l, PageState state) {
        Group group = m_parent.getGroup(state);

        if (group != null) {
            return new SubGroupListModel(group.getSubgroups());
        }

        return new SubGroupListModel(null);
    }
}

class SubGroupListModel implements ListModel {
    private GroupCollection m_coll;

    public SubGroupListModel(GroupCollection coll) {
        m_coll = coll;
        m_coll.addOrder("lower(" + Group.DISPLAY_NAME + ") asc");
    }

    public Object getElement() {
        return m_coll.getGroup();
    }

    public String getKey() {
        return m_coll.getID().toString();
    }

    public boolean next() {
        if (m_coll != null) {
            return m_coll.next();
        }

        return false;
    }
}
