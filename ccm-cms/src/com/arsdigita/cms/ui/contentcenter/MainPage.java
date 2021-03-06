/*
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
package com.arsdigita.cms.ui.contentcenter;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ui.CMSApplicationPage;
import com.arsdigita.cms.ui.GlobalNavigation;
import com.arsdigita.cms.ui.IdSearchTab;
import com.arsdigita.cms.ui.ItemSearch;
import com.arsdigita.cms.ui.WorkspaceContextBar;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.ui.DebugPanel;

import org.apache.log4j.Logger;


//  ////////////////////////////////////////////////////////////////////////////
//
//  Developer's Note:
//  Replaces the (old) Dispatcher based Code cms.ui.CMSPageWorkspacePage
//  Note should be removed as soon as the migration process is competed (in-
//  cluding content section pages).
//
//  ////////////////////////////////////////////////////////////////////////////


/**
 * <p>The Content Center main page. </p>
 * 
 * The page contains the general header and footer, the breadcrumb, and the
 * complete content page including the tab bar, the sections/tasks page, the
 * search page, and the listener to switch between the tabs. 
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Peter Boy (pboy@uni-bremen.de)
 */
public class MainPage extends CMSApplicationPage implements ActionListener {

    private static final Logger s_log = Logger.getLogger(MainPage.class);

    private final static String XSL_CLASS = "CMS Admin";

    private TabbedPane m_tabbedPane;

    private TasksPanel m_tasks;
    private ItemSearch m_search;
    private IdSearchTab m_IdSearch;
    private ACSObjectSelectionModel m_typeSel;
    private ACSObjectSelectionModel m_sectionSel;

    public static final String CONTENT_TYPE = "type_id";
    public static final String CONTENT_SECTION = "section_id";

    /**
     * Construct a new MainPage.
     * 
     * Creates the complete page ready to be included in the page cache of
     * ContentCenterServlet. 
     */
    public MainPage() {
        
        super(new Label( GlobalizationUtil.globalize
                         ("cms.ui.content_center")),
              new SimpleContainer());

        /* Set the class attribute value (down in SimpleComponent).           */
        setClassAttr("cms-admin");
        
        BigDecimalParameter typeId = new BigDecimalParameter(CONTENT_TYPE);
        addGlobalStateParam(typeId);
        m_typeSel = new ACSObjectSelectionModel(
                                            ContentType.class.getName(), 
                                            ContentType.BASE_DATA_OBJECT_TYPE, 
                                            typeId
                                               );

        BigDecimalParameter sectionId = new BigDecimalParameter(CONTENT_SECTION);
        addGlobalStateParam(sectionId);
        m_sectionSel = new ACSObjectSelectionModel(
                                          ContentSection.class.getName(), 
                                          ContentSection.BASE_DATA_OBJECT_TYPE, 
                                          sectionId
                                                  );

        add( new WorkspaceContextBar() );
        add( new GlobalNavigation()    );

        m_tasks = getTasksPane(m_typeSel, m_sectionSel);
        m_search = getSearchPane();
        m_IdSearch = getIdSearchPane();
        
        m_tabbedPane = createTabbedPane();
        m_tabbedPane.setIdAttr("page-body");
        add(m_tabbedPane);

        add(new DebugPanel());
        
    }

    /**
     * Creates, and then caches, the Tasks pane. Overriding this
     * method to return null will prevent this tab from appearing.
     */
    protected TasksPanel getTasksPane(ACSObjectSelectionModel typeModel, 
                                      ACSObjectSelectionModel sectionModel) {
        if (m_tasks == null) {
            m_tasks = new TasksPanel(typeModel,sectionModel);
        }
        return m_tasks;
    }

    /**
     * Creates, and then caches, the Search pane. Overriding this
     * method to return null will prevent this tab from appearing.
     **/
    protected ItemSearch getSearchPane() {
        if (m_search == null) {
            m_search = new ItemSearch(ContentItem.DRAFT);
        }

        return m_search;
    }
    
    protected IdSearchTab getIdSearchPane(){
           if (m_IdSearch == null) {
            m_IdSearch = new IdSearchTab("idsearch");
        }

        return m_IdSearch;
    }

    /**
     * Created the TabbedPane to use for this page. Sets the class
     * attribute for this tabbed pane. The default implementation uses a
     * {@link com.arsdigita.bebop.TabbedPane} and sets the class
     * attribute to "CMS Admin." This implementation also adds tasks,
     * content sections, and search panes.
     *
     * Developers can override this method to add only the tabs they want,
     * or to add additional tabs after the default CMS tabs are added.
     **/
    protected TabbedPane createTabbedPane() {
        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.setClassAttr(XSL_CLASS);
        Label taskLabel = new Label(GlobalizationUtil
                                    .globalize("cms.ui.contentcenter.mainpage.taskssections"));
        Label searchLabel = new Label(GlobalizationUtil
                                    .globalize("cms.ui.contentcenter.mainpage.search"));
        Label IdsearchLabel = new Label("ID Search");
        
        addToPane(tabbedPane, 
                  taskLabel,
                  getTasksPane(m_typeSel, m_sectionSel));
        addToPane(tabbedPane, 
        //        searchLabel, 
                  new Label(GlobalizationUtil.globalize(
                            "cms.ui.contentcenter.mainpage.search")),
                  getSearchPane());
        addToPane(tabbedPane, 
                  IdsearchLabel,
                  getIdSearchPane());
        
        tabbedPane.addActionListener(this);
        return tabbedPane;
    }


//  /**
//   * Adds the specified component, with the specified tab name, to the
//   * tabbed pane only if it is not null.
//   *
//   * @param pane The pane to which to add the tab
//   * @param tabName The name of the tab if it's added
//   * @param comp The component to add to the pane
//   * @deprecated  refactor to use addToPane(Label, Component) instead to
//   *              enable localized tab strips.
//   */
//  protected void addToPane(TabbedPane pane, String tabName, Component comp) {
//      if (comp != null) {
//          pane.addTab(tabName, comp);
//      }
//  }

    /**
     * Adds the specified component, with the specified Label as tab name, 
     * to the tabbed pane only if it is not null.
     *
     * @param pane The pane to which to add the tab
     * @param tabName The name of the tab if it's added
     * @param comp The component to add to the pane
     */
    protected void addToPane(TabbedPane pane, Label tabName, Component comp) {
        if (comp != null) {
            pane.addTab(tabName, comp);
        }
    }


    /**
     * When a new tab is selected, reset the state of the
     * formerly-selected pane.
     *
     * @param event The event fired by selecting a tab
     */
    public void actionPerformed(ActionEvent event) {
        PageState state = event.getPageState();
        Component pane = m_tabbedPane.getCurrentPane(state);

        if ( pane == m_tasks ) {
            m_tasks.reset(state);
        } else if ( pane == m_search ) {
            m_search.reset(state);
        } else if ( pane == m_IdSearch ) {
            m_IdSearch.reset(state);
    }
    }

}
