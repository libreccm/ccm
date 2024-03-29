/*
 * Copyright (C) 2012 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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

package com.arsdigita.webdevsupport;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.ListPanel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.webdevsupport.ui.ConfigParameterList;
import com.arsdigita.webdevsupport.ui.CategoryPanel; 
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Web Developer Support Application Servlet class, central entry point  to 
 * create and process the applications UI.
 * 
 * We should have subclassed BebopApplicationServlet but couldn't overwrite
 * doService() method to add permission checking. So we use our own page
 * mapping. The general logic is the same as for BebopApplicationServlet.
 * {@see com.arsdigita.bebop.page.BebopApplicationServlet}
 * 
 * @author pb
 */
public class WebDevSupportServlet extends BaseApplicationServlet {

    private static final Logger s_log = Logger.getLogger(
                                        WebDevSupportServlet.class.getName());

    public static final String APP_NAME = "ds";

    private static boolean s_showDSPages = false;
    /** URL (pathinfo) -> Page object mapping. Based on it (and the http
     * request url) the doService method to selects a page to display        */
    private final Map m_pages = new HashMap();


    /**
     * User extension point, overwrite this method to setup a URL - page mapping
     * 
     * @throws ServletException 
     */
    @Override
    public void doInit() throws ServletException {

        addPage("/", buildIndexPage());      // index page at address ~/ds
    //  addPage("/index.jsp", buildIndexPage()); // index page at address ~/ds

        addPage("/log4j", buildLog4jPage());   // Logger Adjuster at addr. ~/ds/log4j
        addPage("/config", buildConfigPage()); // config browser @ ~/ds/config
        // cache table browser @ ~/ds/cache-table
        addPage("/cache-table", buildCacheTablePage());

        // XXXX!!
        // QueryLog is a class of its own in webdevsupport, based upon
        // dispatcher.Disp and prints out all queries in a request
        //  put("query-log",  new QueryLog());

        addPage("/request-info",  buildRequestInfoPage());
        addPage("/query-info",  buildQueryInfoPage());
        addPage("/query-plan",  buildQueryPlanPage());

    }


    /**
     * Central service method, checks for required permission, determines the
     * requested page and passes the page object to PresentationManager.
     */
    public final void doService(HttpServletRequest sreq,
                                HttpServletResponse sresp,
                                Application app)
                      throws ServletException, IOException {

        // /////// Some preparational steps                     ///////////////

        /* Determine access privilege: only logged in users may access DS   */
        Party party = Kernel.getContext().getParty();
        if (party == null) {
            throw new LoginSignal(sreq);
        }
        /* Determine access privilege: Admin privileges must be granted     */
        PermissionDescriptor admin = new PermissionDescriptor
            (PrivilegeDescriptor.ADMIN, app, party);
        if (!PermissionService.checkPermission(admin)) {
            throw new AccessDeniedException("User is not an administrator");
        }
        /* Want ds to always show the latest stuff...                       */
        DispatcherHelper.cacheDisable(sresp);


        // /////// Everything OK here - DO IT                   ///////////////

        String pathInfo = sreq.getPathInfo();
        Assert.exists(pathInfo, "String pathInfo");
        if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
            /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
             * start with a '/' character. It currently carries a 
             * trailing '/' if a "virtual" page, i.e. not a real jsp, but 
             * result of a servlet mapping. But Application requires url 
             * NOT to end with a trailing '/' for legacy free applications.  */
            pathInfo = pathInfo.substring(0, pathInfo.length()-1);
        }

        final Page page = (Page) m_pages.get(pathInfo);

        if (page != null) {

            final Document doc = page.buildDocument(sreq, sresp);

            PresentationManager pm = Templating.getPresentationManager();
            pm.servePage(doc, sreq, sresp);

        } else {
            if (pathInfo.equals("/query-log")) {
 
                // special solution for query log to continue to use the
                // dispatcher based creation of a new page.
                // Should be refactored asap to use a PageFactory instead!
                RequestContext ctx = DispatcherHelper.getRequestContext(sreq);
                new QueryLog().dispatch(sreq, sresp, ctx);

            } else {

                sresp.sendError(404, "No such page for path " + pathInfo);

            }
        }
        
    }


    /**
     * Adds one Url-Page mapping to the internal mapping table.
     * 
     * @param pathInfo url stub for a page to display
     * @param page Page object to display
     */
    private void addPage(final String pathInfo, final Page page) {

        Assert.exists(pathInfo, String.class);
        Assert.exists(page, Page.class);
        // Current Implementation requires pathInfo to start with a leading '/'
        // SUN Servlet API specifies: "PathInfo *may be empty* or will start
        // with a '/' character."
        Assert.isTrue(pathInfo.startsWith("/"), "path starts not with '/'");

        m_pages.put(pathInfo, page);
    }

    /**
     * 
     * @return index Page object
     */
    private Page buildIndexPage() {

        Page p = PageFactory.buildPage(APP_NAME, "Web Developer Support");


        // Create a group of 4 permanent links, the first 3 to additional pages
        // which handle status of logger instances, config properties and cache 
        // tables 1 or 2 to add additional information about requests to this
        // page.
        BoxPanel links = new BoxPanel(BoxPanel.VERTICAL);

        links.add(new Link("Log4j Logger Adjuster", "/ds/log4j"));
        links.add(new Link("Config Browser", "/ds/config"));
        links.add(new Link("Cache Table Browser", "/ds/cache-table"));


        // Creates an (page internal) action link to toggle reqest logging
        // on/off. If ON additional page content is generated: a list of
        // available requests (table view)
        ActionLink enable = new ActionLink("Enable request logging") {
            @Override
            public boolean isVisible(PageState state) {
                return !DeveloperSupport.containsListener(
                                         WebDevSupportListener.getInstance())
                        && super.isVisible(state);
            }
        };
        enable.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DeveloperSupport.addListener(WebDevSupportListener.getInstance());
                    throw new RedirectSignal(URL.request(e.getPageState().getRequest(),
                                                         null), true);
                }

            });
        links.add(enable);   // add ActionLink to the link group 

        ActionLink disable = new ActionLink("Disable request logging") {
            @Override
            public boolean isVisible(PageState state) {
                return DeveloperSupport.containsListener(
                                        WebDevSupportListener.getInstance())
                       && super.isVisible(state);
            }
        };
        disable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DeveloperSupport.removeListener(WebDevSupportListener.getInstance());
                WebDevSupportListener.getInstance().clearRequestHistory();
                throw new RedirectSignal(URL.request(e.getPageState().getRequest(),
                                                     null), true);
                }
            });
        links.add(disable);   // add ActionLink to the link group, only visible
                              // if link enable is toggled ON and no longer visible!



        // Create a box panel to show additional information about requests
        // on a on demand base (must ge toggled ON by a link on the link panel)
        // Visibility depends on the toggle
        BoxPanel logs = new BoxPanel(BoxPanel.VERTICAL) {
            @Override
            public boolean isVisible(PageState state) {
                return DeveloperSupport
                        .containsListener(WebDevSupportListener.getInstance())
                        && super.isVisible(state);
                }
            };

        // Adding info line about number of requests to store and show
        logs.add(new Label("") {
            @Override
            public String getLabel(PageState ps) {
                return "Currently storing the last " +
                        WebDevSupportListener.getInstance().getMaxRequests() + 
                        " requests";
                }
            });

        // Add aditional toggle whether or not to include hits to ds 
        Label toggle = new Label("") {
            @Override
            public String getLabel(PageState ps) {
                return s_showDSPages ? "Hide hits to developer support" :
                       "Show hits to developer support";
            }
        };
        ControlLink cl = new ControlLink(toggle) {
            @Override
            public void respond(PageState s) {
                s_showDSPages = !s_showDSPages;
            }
            @Override
            public void setControlEvent(PageState s) {
                s.setControlEvent(this);
            }
        };

        // Add created widgets / controls to the log pannel
        logs.add(cl);
        logs.add(new Label("<h3>Available Request Information</h3>", false));
        logs.add(makeRequestTable());

        // Add all panels to the page and lock
        p.add(links);
        p.add(logs);
        p.lock();

        return p;    // page ready to display
    }


    /**
     * Create a separate page to show logger instances and its state.
     * Controlled by the 1. Link on the web developer support index page.
     * 
     * @return log4j Page object 
     */
    private Page buildLog4jPage() {

        Page p = PageFactory.buildPage(APP_NAME, "Log4j Logger Adjuster");

        p.add(new CategoryPanel());
        p.lock();

        return p;
    }

    /**
     * Create a separate page to show the status of configuration parameters
     * for every installed package (module).
     * Controlled by the 2. Link on the web developer support index page.
     * 
     * @return config Page object 
     */
    private Page buildConfigPage() {

        Page p = PageFactory.buildPage(APP_NAME, "Registry Config");

        p.add(new ConfigParameterList());
        p.lock();

        return p;
    }

    /**
     * Create a separate page to show all cache tables in use. 
     * Controlled by the 3. Link on the web developer support page.
     * @return 
     */
    private Page buildCacheTablePage() {

        Page p = PageFactory.buildPage(APP_NAME, "Cache Table Browser");

        p.add(new CacheTableBrowser());
        p.lock();

        return p;
    }
    
    /**
     * Create an additional component for the web developer support index page.
     * Controlled by the 4. link (a toggle) to show request information.
     * 
     * @return 
     */
    private Table makeRequestTable() {
        final String[] headers = { "Time", "Duration", "Queries", "IP",
                                   "Request", "Extra" };

        TableModelBuilder b = new AbstractTableModelBuilder() {
                public TableModel makeModel(Table t, PageState s) {
                    return new TableModel() {
                            ListIterator iter =
                                WebDevSupportListener.getInstance().getRequestsReverse();
                            private RequestInfo current = null;

                            public int getColumnCount() {
                                return 6;
                            }

                            public boolean nextRow() {
                                while (iter.hasPrevious()) {
                                    current = (RequestInfo) iter.previous();
                                    boolean isdevsupp = current.isDevSupportRequest();
                                    if (s_showDSPages || !isdevsupp) {
                                        return true;
                                    }
                                }
                                return false;
                            }
                            static final int MAXSTR = 35;
                            public Object getElementAt(int columnIndex) {
                                switch (columnIndex) {
                                case 0: return current.getTime();
                                case 1: return current.getDuration();
                                case 2: return current.getNumQueries()+"";
                                case 3: return current.getIP();
                                case 4: {
                                    String req = current.getRequest();
                                    if (req.length() > MAXSTR) {
                                        return req.substring(0, MAXSTR)+"...";
                                    } else {
                                        return req;
                                    }
                                }
                                case 5:
                                    return "[query log]";
                                default: return null;
                                }
                            }

                            public Object getKeyAt(int columnIndex) {
                                return new Integer(current.getID());
                            }
                        };
                }
            };

        Table result = new Table(b, headers);
        result.getColumn(4).setCellRenderer(new
                                            DefaultTableCellRenderer(true));
        result.getColumn(5).setCellRenderer(new
                                            DefaultTableCellRenderer(true));
        result.addTableActionListener(new TableActionAdapter() {
            @Override
                public void cellSelected(TableActionEvent e) {
                    final ParameterMap params = new ParameterMap();
                    params.setParameter("request_id", e.getRowKey());

                    if (e.getColumn().intValue() == 4) {
                            throw new RedirectSignal(URL.getDispatcherPath() +
                                               "/ds/request-info" + params, true);
                    } else if (e.getColumn().intValue() == 5) {
                        throw new RedirectSignal(URL.getDispatcherPath() +
                                               "/ds/query-log" + params, true);
                    }
                }
            });
        Label l = new Label("None");
        l.setFontWeight(Label.ITALIC);
        l.setStyleAttr("padding-left: 3em");
        result.setEmptyView(l);
        result.setWidth("100%");
        return result;
    }


    private ParameterModel m_request_id = new IntegerParameter("request_id");

    /**
     * 
     * @return info Page object 
     */
    private Page buildRequestInfoPage() {

        Page p = PageFactory.buildPage(APP_NAME, "Request Information");

        p.addGlobalStateParam(m_request_id);
        p.add(new RequestInfoOverviewHeaderComponent());
        p.add(makeDatabaseRequestComponent());
        p.lock();

        return p;
    }

    private ParameterModel m_query_id = new IntegerParameter("query_id");
    private ParameterModel m_query_request_id = new IntegerParameter("request_id");

    /**
     * 
     * 
     * @return queryInfo Page object 
     */
    private Page buildQueryInfoPage() {
        Page p = PageFactory.buildPage(APP_NAME, "Query Information");
        p.addGlobalStateParam(m_query_request_id);
        p.addGlobalStateParam(m_query_id);
        p.add(new QueryInfoComponent());
        p.lock();
        return p;
    }


    private RequestLocal m_scoreboard = new RequestLocal() {
        @Override
        protected Object initialValue(PageState state) {
            // queryInfo.getID() => {textRepeats, queryRepeats}
            return new HashMap();
        }
    };

    // Used below in makeDatabaseRequestComponent to generate a query
    // scoreboard.
    private void incrementScore(final PageState state, final QueryInfo key,
                                final int index) {
        Map map = (Map) m_scoreboard.get(state);

        Integer[] row = (Integer[]) map.get(new Integer(key.getID()));

        if (row == null) {
            row = new Integer[] { new Integer(0), new Integer(0) };
            map.put(new Integer(key.getID()), row);
        }

        row[index] = new Integer(row[index].intValue() + 1);
    }


    private Component makeDatabaseRequestComponent() {
        final String[] headers =
            { "ID", "Duration (execution)", "Conn", "Command", "Exception" };

        TableModelBuilder b = new AbstractTableModelBuilder() {
                public TableModel makeModel(Table t, final PageState s) {
                    Integer request_id = (Integer)s.getValue(m_request_id);
                    RequestInfo ri =
                        WebDevSupportListener.getInstance().getRequest(request_id.intValue());
                    final Iterator iter = (ri == null) ? new ArrayList().iterator() :
                        ri.getQueries();

                    return new TableModel() {
                            private HashMap seenQueryAndValues = new HashMap();
                            private HashMap seenQuery = new HashMap();
                            private boolean duplicateQueryAndValues = false;
                            private boolean duplicateQuery = false;
                            private QueryInfo current = null;

                            public int getColumnCount() {
                                return 5;
                            }

                            public boolean nextRow() {
                                if (iter.hasNext()) {
                                    current = (QueryInfo) iter.next();

                                    duplicateQueryAndValues =
                                        seenQueryAndValues.containsKey(current);
                                    duplicateQuery =
                                        seenQuery.containsKey(current.getQuery());

                                    if (!duplicateQueryAndValues) {
                                        seenQueryAndValues.put(current, current);
                                    }

                                    if (!duplicateQuery) {
                                        seenQuery.put(current.getQuery(), current);
                                    }

                                    incrementScore
                                        (s, (QueryInfo) seenQueryAndValues.get(current),
                                         0);
                                    incrementScore
                                        (s, (QueryInfo) seenQuery.get(current.getQuery()),
                                         1);

                                    return true;
                                } else {
                                    return false;
                                }
                            }

                            public Object getElementAt(int columnIndex) {
                                switch (columnIndex) {
                                case 0: {
                                    StringBuffer result = new StringBuffer();
                                    result.append(current.getID());

                                    if (duplicateQueryAndValues) {
                                        result.append(
                                            " duplicates #" +
                                            ((QueryInfo)seenQueryAndValues.get(
                                                  current)).getID()
                                            );
                                    }

                                    if (duplicateQuery) {
                                        result.append(
                                            " duplicates text of #" +
                                            ((QueryInfo)seenQuery.get(
                                                  current.getQuery())).getID()
                                            );
                                    }

                                    return result.toString();
                                }
                                case 1: return (current.isClosed() ? 
                                                current.getTotalTime() + " ms" :
                                                "unknown")
                                                + "<br/> (" + current.getTime() + " ms)";
                                case 2: return current.getConnectionID();
                                case 3: return "<blockquote><pre>" +
                                            current.getQuery() +
                                            "</pre><br>BINDS: " +
                                            current.getBindvars() +
                                            "</blockquote>";
                                case 4: return current.getSQLE();
                                default: return null;
                                }
                            }

                            public Object getKeyAt(int columnIndex) {
                                return new Integer(current.getID());
                            }
                        };
                }
            };

        Table table = new Table(b, headers);
        table.setBorder("1");
        table.getColumn(1).setCellRenderer(new NonEscapedTableCellRenderer(false));
        table.getColumn(3).setCellRenderer(new NonEscapedTableCellRenderer());
        table.getColumn(0).setCellRenderer(new
                                           DefaultTableCellRenderer(true));
        table.addTableActionListener(new TableActionAdapter() {
            @Override
            public void cellSelected(TableActionEvent e) {
                PageState s = e.getPageState();
                Integer request_id = (Integer) s.getValue(m_query_request_id);

                final ParameterMap params = new ParameterMap();

                params.setParameter("request_id", request_id);
                params.setParameter("query_id", e.getRowKey());

                if (e.getColumn().intValue() == 0) {
                   throw new RedirectSignal(URL.getDispatcherPath() +
                                            "/ds/query-info" + params, true);
                } else {
                    throw new RedirectSignal(URL.getDispatcherPath() +
                                                 "/ds/query-log" + params, true);
                }
            }
        });

        Label l = new Label("None");
        l.setFontWeight(Label.ITALIC);
        l.setStyleAttr("padding-left: 3em");
        table.setEmptyView(l);
        table.setWidth("100%");

        BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        panel.add(table);

        TableModelBuilder scoreBuilder = new AbstractTableModelBuilder() {
                public TableModel makeModel(Table t, final PageState state) {
                    return new TableModel() {
                            Map map = (Map) m_scoreboard.get(state);
                            Iterator iter = map.keySet().iterator();
                            Integer key;
                            Integer[] row;

                            public int getColumnCount() {
                                return 3;
                            }

                            public boolean nextRow() {
                                if (iter.hasNext()) {
                                    key = (Integer) iter.next();
                                    row = (Integer[]) map.get(key);
                                    return true;
                                } else {
                                    return false;
                                }
                            }

                            public Object getElementAt(int column) {
                                switch (column) {
                                case 0:
                                    return key;
                                case 1:
                                    return row[0];
                                case 2:
                                    return row[1];
                                default:
                                    return null;
                                }
                            }

                            public Object getKeyAt(int column) {
                                return key;
                            }
                        };
                }
            };

        final String[] scoreHeaders = {"ID", "Duplicates", "Text-Duplicates"};

        Table scoreTable = new Table(scoreBuilder, scoreHeaders);
        panel.add(scoreTable);

        return panel;
    }

    String dashes(int depth) {
        StringBuilder sb = new StringBuilder();
        while (depth-- > 0) sb.append("--");
        return sb.toString();
    }



    private ParameterModel m_query_p_id = new IntegerParameter("query_id");
    private ParameterModel m_query_p_request_id =
        new IntegerParameter("request_id");

    private Page buildQueryPlanPage() {
        Page p = PageFactory.buildPage(APP_NAME, "Query Execution Plan");
        p.addGlobalStateParam(m_query_p_request_id);
        p.addGlobalStateParam(m_query_p_id);
        p.add(new QueryPlanComponent());
        p.lock();
        return p;
    }

    /**
     * Internal class to create a page component to display summerized
     * information about a request. It is displayed as a header / summary
     * paragraph in the top part of the "request Information" page, build by
     * buildRequestInfoPage method.
     */
    class RequestInfoOverviewHeaderComponent extends com.arsdigita.bebop.SimpleComponent {

        /**
         * Constructor 
         */
        public RequestInfoOverviewHeaderComponent() {
            super();
        }

        /**
         * 
         * @param state
         * @param parent 
         */
        @Override
        public void generateXML(PageState state, Element parent) {
            Integer request_id = (Integer)state.getValue(m_request_id);
            RequestInfo ri =
                WebDevSupportListener.getInstance().getRequest(request_id.intValue());
            if (ri != null) {
                Container param_list;
                Container form_list;
                Container comment_list;
                Container headers_list;
                Container stages_list;
                BoxPanel bp = new BoxPanel();
                bp.add(new Label("<h3>Parameters</h3>", false));
                bp.add(param_list = new ColumnPanel(2));
                param_list.add(new Label("Request Start Time:"));
                param_list.add(new Label(ri.getTime()));
                param_list.add(new Label("Request Completion Time: "));
                param_list.add(new Label(ri.getEndTime()));
                param_list.add(new Label("Request Duration: "));
                param_list.add(new Label(ri.getDuration()));
                param_list.add(new Label("IP: "));
                param_list.add(new Label(ri.getIP()));
                param_list.add(new Label("Method: "));
                param_list.add(new Label(ri.getMethod()));
                param_list.add(new Label("URL: "));
                param_list.add(new Label(ri.getURL()));
                param_list.add(new Label("Query: "));
                param_list.add(new Label(StringUtils.quoteHtml( ri.getQuery())));
                param_list.add(new Label("Request Parameters: "));
                param_list.add(form_list = new ColumnPanel(2));
                Iterator iter = ri.getParameterNames();
                while (iter.hasNext()) {
                    String param = (String)iter.next();
                    form_list.add(new Label(param + ":"));
                    form_list.add(new Label(ri.getParameter(param)));
                }

                bp.add(new Label("<h3>Headers</h3>", false));
                bp.add(headers_list = new ColumnPanel(2));
                iter = ri.headerKeys();
                while (iter.hasNext()) {
                    String header = (String)iter.next();
                    headers_list.add(new Label(header +": "));
                    headers_list.add(new Label(ri.getHeader(header)));
                }
                //Doesn't appear to be possible to get the output headers
                //bp.add(new Label("Output Headers"));
                if (ri.numComments() > 0) {
                    bp.add(new Label("<h3>Comments</h3>", false));
                    bp.add(comment_list = new ListPanel(false));
                    iter = ri.getComments();
                    while (iter.hasNext()) {
                        comment_list.add(new Label((String)iter.next()));
                    }
                }
                if (ri.numStages() > 0) {
                    bp.add(new Label("<h3>Stages</h3>", false));
                    bp.add(stages_list = new ColumnPanel(4));
                    iter = ri.getStages();
                    stages_list.add(new Label("Stage"));
                    stages_list.add(new Label("Time"));
                    stages_list.add(new Label("Queries"));
                    stages_list.add(new Label("Processing"));
                    while (iter.hasNext()) {
                        StageInfo si = (StageInfo)iter.next();
                        String leaf = si.leaf() ? "*" : "";
                        int numqueries = si.numQueries();
                        long time = si.time();
                        stages_list.add(new Label(dashes(si.depth()) + si.getName()));
                        stages_list.add(new Label(time + " ms" + leaf));
                        if (numqueries != 0) {
                            long queryTime = si.queryTime(ri.getQueries());
                            long non_queryTime = time-queryTime;
                            stages_list.add(new Label(queryTime + " ms" + leaf +
                                                 " ("+numqueries+" queries)"));
                            stages_list.add(new Label(non_queryTime + " ms" + leaf));
                        } else {
                            stages_list.add(new Label(""));
                            stages_list.add(new Label(time + " ms"+ leaf));
                        }
                    }
                }
                bp.add(new Label("<h3>Database Requests</h3>", false));
                int query_count = 0;
                long total_time = 0;
                long total_execute = 0;
                int unclosed_count = 0;
                iter = ri.getQueries();
                while (iter.hasNext()) {
                    QueryInfo qi = (QueryInfo)iter.next();
                    query_count++;
                    total_execute += qi.getTime();
                    total_time += qi.getTotalTime();
                    if (!qi.isClosed()) {
                        unclosed_count++;
                    }
                }
                bp.add(new Label("Total Queries: " + query_count + 
                                 (unclosed_count > 0 ? " (" + unclosed_count +
                                  " unclosed)" : "")));
                bp.add(new Label("Total Time: " + total_time +
                                 " ms  (execution: " + total_execute + " ms)"));
                bp.generateXML(state, parent);
            }
        }
    }

    /**
     * Class
     */
    class QueryInfoComponent extends com.arsdigita.bebop.SimpleComponent {

        /**
         * 
         */
        public QueryInfoComponent() {
            super();
        }

        /**
         * 
         * @param state
         * @param parent 
         */
        @Override
        public void generateXML(PageState state, Element parent) {
            Integer request_id = (Integer)state.getValue(m_query_request_id);
            Integer query_id = (Integer)state.getValue(m_query_id);
            RequestInfo ri =
                WebDevSupportListener.getInstance().getRequest(request_id.intValue());
            if (ri != null) {
                QueryInfo qi = ri.getQuery(query_id.intValue());
                if (qi != null) {
                    ListPanel info_list;
                    BoxPanel bp = new BoxPanel();
                    bp.add(info_list = new ListPanel(false));
                    info_list.add(new Label("Total query duration: " +
                                            qi.getTotalTime() + " ms"));
                    info_list.add(new Label("Query execution time: " +
                                            qi.getTime() + " ms"));
                    info_list.add(new Label("Statement closed: " +
                                            qi.isClosed()));
                    info_list.add(new Label("Connection ID: " +
                                            qi.getConnectionID()));
                    info_list.add(new Label("Type: " +
                                            qi.getType()));
                    info_list.add(new Label("Query: <br/><pre>" +
                                            StringUtils.quoteHtml(
                                            qi.getQuery() ) + "</pre>", false));
                    info_list.add(new Label("Bindvars: " +
                                            qi.getBindvars()));

                    Link l = new Link ("Query Execution Plan",
                                        "../query-plan/");
                    l.setVar("request_id", request_id.toString());
                    l.setVar("query_id", query_id.toString());
                    info_list.add(l);


                    info_list.add(new Label("Exception: " +
                                            qi.getSQLE()));
                    info_list.add(new Label("StackTrace: <br/><pre>" +
                                            StringUtils.quoteHtml(qi.getStackTrace()) +
                                            "</pre>", false));
                    bp.generateXML(state, parent);
                }
            }
        }
    }


}

// We need NonEscapedTableCellRenderer
// Currently part of (thisPackage) Dispatcher.java
// When the class will we deleted we must copy first!

/**
 * Public class 
 * 
 */
class NonEscapedTableCellRenderer implements TableCellRenderer {
    private boolean m_controlLink;
    public NonEscapedTableCellRenderer(boolean controlLink) {
        super();
        m_controlLink = controlLink;
    }
    public NonEscapedTableCellRenderer() {
        this(true);
    }

    public Component getComponent(Table table, PageState state, Object value,
                                  boolean isSelected, Object key,
                                  int row, int column) {
        SimpleContainer c = new SimpleContainer();
        Label l = new Label((String)value);
        l.setOutputEscaping(false);
        c.add(l);
        if (m_controlLink) {
            c.add(new ControlLink("download"));
        }
        return c;
    }
}
