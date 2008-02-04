package com.arsdigita.ui.admin;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Group;
import com.arsdigita.ui.admin.GroupAdministrationTab;

/**
 * Series of screens required for adding existing groups as subgroups - 
 * based on existing functionality for adding permissions to a folder in content/admin
 */
public class ExistingGroupAddPane extends SimpleContainer implements AdminConstants  {
	public static final String versionId =
		"$Id: ExistingGroupAddPane.java,v 1.4 2004/06/21 11:34:03 cgyg9330 Exp $ by $Author: cgyg9330 $, $DateTime: 2003/01/09 06:28:37 $";

	private static final Logger s_log = Logger.getLogger(ExistingGroupAddPane.class);
	
	private ParameterModel searchString = new StringParameter(SEARCH_QUERY);
    
	
	private GroupSearchForm groupSearchForm;
	private SimpleContainer selectGroupsPanel;
	private SimpleContainer noResultsPanel;
	private Tree groupTree;
	private GroupAdministrationTab parentPage;
	
	private RequestLocal parentGroup = new RequestLocal() {
				protected Object initialValue(PageState ps) {
					String key = (String) groupTree.getSelectedKey(ps);

					Group group = null;

					if (key != null) {
						BigDecimal id = new BigDecimal(key);

						try {
							group = new Group(id);
						} catch (DataObjectNotFoundException exc) {
							// Silently ignore if group does not
							// exist.
						}
					}
					return group;
				}
			};

	
	
	public ExistingGroupAddPane(Tree groupTree, GroupAdministrationTab parentPage) {
		this.groupTree = groupTree;
		this.parentPage = parentPage;

	}


	public void register(Page p) {
			super.register(p);
			add(getGroupSearchForm());
			add(getSelectGroupsPanel());
			add(getNoSearchResultPanel());

			// set initial visibility of components
			p.setVisibleDefault(getGroupSearchForm(), true);
			p.setVisibleDefault(getSelectGroupsPanel(), false);
			p.setVisibleDefault(getNoSearchResultPanel(),false);

		p.addGlobalStateParam(searchString);
        
	}



	


	public GroupSearchForm getGroupSearchForm() {
			if (groupSearchForm==null) {
				groupSearchForm = new GroupSearchForm(this);
			}
			return groupSearchForm;
		}

		/**
		 * Returns a panel with a set of checkboxes for groups 
		 * fulfilling search criteria
		 */
		public SimpleContainer getSelectGroupsPanel() {
			if (selectGroupsPanel==null) {
				SelectGroups selectGroups = new SelectGroups(this, getGroupSearchForm());
				selectGroupsPanel =  selectGroups.getPanel();
			}
			return  selectGroupsPanel;
		}

		
		/**
		 * Returns a bebop panel indicating that the user search
		 * yielded no results.
		 */

		public SimpleContainer getNoSearchResultPanel() {
			if (noResultsPanel==null) {
				Label errorMsg = GROUP_NO_RESULTS;
				errorMsg.setClassAttr("errorBullet");
				BoxPanel bp = new BoxPanel();
				bp.add(errorMsg);
				bp.add(new GroupSearchForm(this));
				noResultsPanel = new SegmentedPanel().addSegment(new Label(" "),bp);
			}
			return noResultsPanel;
		}


		/**
		*  Shows panel with no results to user search.
		*/

	   public void showNoResults(PageState s) {
		   getGroupSearchForm().setVisible(s, false);
		   getSelectGroupsPanel().setVisible(s,false);
		   getNoSearchResultPanel().setVisible(s, true);
	   }

	   /**
		* Show the select groups to add as subgroups panel
		*/

	   public void showGroups(PageState s) {
		getGroupSearchForm().setVisible(s, false);
		 getSelectGroupsPanel().setVisible(s,true);
		 getNoSearchResultPanel().setVisible(s, false);
	   }
	   /**
	    * 
	    * show the search form
	    */
	public void showSearch(PageState s) {
		   getGroupSearchForm().setVisible(s, true);
			getSelectGroupsPanel().setVisible(s,false);
			getNoSearchResultPanel().setVisible(s, false);
		  }

	public ParameterModel getSearchString() {
			return searchString;
		}
		
		public GroupAdministrationTab getParentPage() {
			return parentPage;
		}
		
	public Group getParentGroup(PageState ps) {
		return (Group) parentGroup.get(ps);
	}
	

}
