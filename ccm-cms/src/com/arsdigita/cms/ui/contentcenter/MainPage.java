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
import com.arsdigita.cms.ui.GlobalNavigation;
import com.arsdigita.cms.ui.ItemSearch;
import com.arsdigita.cms.ui.WorkspaceContextBar;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.ui.DebugPanel;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;


/**
 * <p>The Content Center main page. </p>
 * 
 * The page contains the general header and footer, the breadcrumb, and the
 * complete content page including the tab bar, the sections/tasks page, the
 * search page, and the listener to switch between the tabs. 
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Peter Boy (pboy@barkhof.uni-bremen.de)
 * @version $Id: MainPage.java pboy $
 */
public class MainPage extends Page implements ActionListener {

    private static final Logger s_log = Logger.getLogger(MainPage.class);

    private final static String XSL_CLASS = "CMS Admin";

    private TabbedPane m_tabbedPane;

    private TasksPanel m_tasks;
    private ItemSearch m_search;
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

        m_tabbedPane = createTabbedPane();
        m_tabbedPane.setIdAttr("page-body");
        add(m_tabbedPane);

        add(new DebugPanel());
        
        /* Page complete, locked now.                                         */
        lock();
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

/*
    private SimpleContainer makeHeader() {
        PrintListener l = new PrintListener() {
                public void prepare(PrintEvent event) {
                    PageState state = event.getPageState();
                    Link link = (Link) event.getTarget();

                    User user = KernelHelper.getCurrentUser(state.getRequest());

                    link.setChild(new Label(user.getName()));
                    link.setTarget("/pvt/");
                }
            };

        SimpleContainer sc = new SimpleContainer();
        Label welcomeLabel = new Label(GlobalizationUtil.globalize("cms.ui.welcome"));
        Link nameLink = new Link(l);

        sc.add(welcomeLabel);
        sc.add(nameLink);
        return sc;

    }
*/

    /**
     * Created the TabbedPane to use for this page. Sets the class
     * attribute for this tabbed pane. The default implementation uses a
     * {@link com.arsdigita.bebop.TabbedPane} and sets the class
     * attribute to "CMS Admin." This implementation also adds tasks,
     * content sections, and search panes.
     *
     *<p>
     *
     * Developers can override this method to add only the tabs they
     * want, or to add additional tabs after the default CMS tabs are
     * added.
     **/
    protected TabbedPane createTabbedPane() {
        TabbedPane pane = new TabbedPane();
        pane.setClassAttr(XSL_CLASS);
        addToPane(pane, "Tasks/Sections", getTasksPane(m_typeSel, m_sectionSel));
        addToPane(pane, "Search", getSearchPane());
        pane.addActionListener(this);
        return pane;
    }


    /**
     * Adds the specified component, with the specified tab name, to the
     * tabbed pane only if it is not null.
     *
     * @param pane The pane to which to add the tab
     * @param tabName The name of the tab if it's added
     * @param comp The component to add to the pane
     **/
    protected void addToPane(TabbedPane pane, String tabName, Component comp) {
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
        }
    }

    /**
     * Overwrites bebop.Page#generateXMLHelper to add the name of the user
     * logged in to the page (displayed as part of the header).
     * @param ps
     * @param parent
     * @return 
     */
    // ToDo: This code fragment is used by several pages of CMS package. It
    // should be factored out into a kind of CMSBasePage, as it had been in
    // the deprecated CMSPage.
    // Should be checked when refactoring the content section pages to work
    // as bebop pages without dispatcher mechanism and in new style application.
    @Override
    protected Element generateXMLHelper(PageState ps, Document parent) {

        /* Retain elements already included.                                  */
        Element page = super.generateXMLHelper(ps,parent);

        /* Add name of user logged in.                                        */
        User user = Web.getContext().getUser();
        if ( user != null ) {
            page.addAttribute("name",user.getDisplayName());
        }

        return page;
    }

}
