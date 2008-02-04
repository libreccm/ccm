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
package com.arsdigita.toolbox.ui;


import com.arsdigita.toolbox.util.GlobalizationUtil ; 

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.EventListenerList;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.Assert;

import com.arsdigita.globalization.GlobalizedMessage;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.xml.Element;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * <h4>General</h4>
 *
 * Wraps any {@link DataQuery} in a sortable Bebop {@link Table}.
 * The {@link DataQuery} is supplied by the {@link DataQueryBuilder} class,
 * which the user must implement. The <code>DataQueryBuilder</code> may
 * dynamically construct the query during each request, or return the
 * same named query for each request; the <code>DataTable</code> does
 * not care where the query comes from.
 * <p>
 *
 * This class may contain multiple {@link QueryListener}s. These
 * listeners will be fired whenever the query is about to
 * be performed, thus giving the user a chance to set
 * additional filters on the query.
 * <p>
 *
 * Columns may be added to the <code>DataTable</code> by calling the
 * {@link #addColumn} method. The user may choose to make the column
 * sortable or non-sortable; sortable columns will appear as links
 * on the Web page which, when clicked, will sort the table by the
 * specified column. See the documentation on the various
 * <code>addColumn</code> methods for more information.
 * <p>
 *
 * Note that any {@link com.arsdigita.domain.DomainCollection}
 * can be used with <code>DataTable</code>, since the
 * <code>DomainCollection</code> is merely a wrapper for a {@link
 * com.arsdigita.persistence.DataCollection}, which extends
 * <code>DataQuery</code>. The {@link
 * com.arsdigita.domain.DomainService} class may be used to retieve
 * the <code>DataCollection</code> for any given
 * <code>DomainCollection</code>.  <p>
 *
 * This class sets the XSL "class" attribute to "dataTable"
 * <p>
 *
 * <h4>Pagination</h4>
 *
 * <code>DataTable</code> also
 * implements {@link PaginationModelBuilder}. This means that
 * it could serve as the model builder for any {@link Paginator}
 * component. Pagination of the query occurs after all the sorting
 * and query events have finished. Consider a query which returns
 * the rows "A B C D E F". If the paginator displays 3 rows per page,
 * page 1 will contain "A B C" and page 2 will contain "D E F".
 * If the user then clicks on the header in the <code>DataTable</code>,
 * causing the query to be sorted in reverse order, page 1 will
 * contain "F E D" and page 2 will contain "C B A". In order for
 * pagination to work properly, the following pattern must
 * be used:
 *
 * <blockquote><pre><code>DataTable table = new DataTable(...);
 * Paginator paginator = new Paginator(table, ...);
 * table.setPaginator(paginator);</code></pre></blockquote>
 *
 * The <code>setPaginator</code> call is required due to a
 * design flaw in the <code>Paginator</code> component.
 * <p>
 *
 * <h4>Globalization</h4>
 *
 * The <code>DataTable</code> will ordinarily interpret the labels
 * of its column headers as plain text, and spit them out on
 * the screen verbatim. However, if <code>setResouceBundle</code>
 * is called, <code>DataTable</code> will instead interpret the
 * column header labels as keys into the specified resource bundle,
 * thus attempting to globalize the column headers at runtime.
 * <p>
 *
 * @author Stanislav Freidin 
 * @version $Id: DataTable.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DataTable extends Table implements PaginationModelBuilder {

    public static final String versionId = "$Id: DataTable.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(DataTable.class.getName());

    private DataQueryBuilder m_builder;
    private SingleSelectionModel m_orderModel;
    private StringParameter m_dirParam;
    private String m_resourceBundle;
    private RequestLocal m_querySize;
    private Paginator m_paginator;

    public static final String ORDER = "o";
    public static final String DIRECTION = "d";
    public static final String ASCENDING = "asc";
    public static final String DESCENDING = "desc";

    private EventListenerList m_queryListeners;

    /**
     * Construct a new DataTable.
     *
     * @param builder the {@link DataQueryBuilder} that will be used for
     *   this browser
     * @param orderModel the {@link SingleSelectionModel} that will be used
     *   to determine the column to order by
     * @param resourceBundle the name of the resource bundle that will be
     *   used to globalize the column labels. If null, column labels will
     *   be printed verbatim to the screen.
     */
    public DataTable (
                      DataQueryBuilder builder, SingleSelectionModel orderModel,
                      String resourceBundle
                      ) {
        super(new DataBuilderAdapter(), new DataTableColumnModel());
        m_builder = builder;
        m_resourceBundle = resourceBundle;

        setOrderSelectionModel(orderModel);
        addTableActionListener(new DataTableActionListener());
        m_queryListeners = new EventListenerList();

        m_dirParam = new StringParameter(DIRECTION);
        m_dirParam.setDefaultValue(ASCENDING);

        getHeader().setDefaultRenderer(new GlobalizedHeaderCellRenderer());

        m_querySize = new RequestLocal();
        m_paginator = null;

        setClassAttr("dataTable");
    }

    /**
     * Construct a new DataTable.
     *
     * @param builder the {@link DataQueryBuilder} that will be used for
     *   this browser
     * @param orderModel the {@link SingleSelectionModel} that will be used
     *   to determine the column to order by
     * @param resourceBundle the name of the resource bundle that will be
     *   used to globalize the column labels. If null, column labels will
     *   be printed verbatim to the screen.
     */
    public DataTable (
                      DataQueryBuilder builder, SingleSelectionModel orderModel
                      ) {
        this(builder, orderModel, null);
    }

    /**
     * Construct a new DataTable
     *
     * @param builder the {@link DataQueryBuilder} that will be used for
     *   this browser
     *
     */
    public DataTable(DataQueryBuilder builder) {
        this(builder,
             new ParameterSingleSelectionModel(new StringParameter(ORDER)));
    }

    /**
     * Register the ordering parameter
     */
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this,
                                 getOrderSelectionModel().getStateParameter());
        p.addComponentStateParam(this, m_dirParam);
    }

    /**
     * Set the key of the default column which will be
     * used to sort the entries
     *
     * @param attribute the default attribute to sort by
     */
    public void setDefaultOrder(String attribute) {
        Assert.assertNotLocked(this);
        getOrderSelectionModel().getStateParameter()
            .setDefaultValue(attribute);
    }

    /**
     * Get the key of the default column which will be
     * used to sort the entries
     *
     * @return the default attribute to sort by, or null if
     *   no default has been set
     */
    public String getDefaultOrder() {
        return (String)getOrderSelectionModel().getStateParameter()
            .getDefaultValue();
    }


    /**
     * Add a column to this table.
     *
     * @param label  The user-readable label for the column
     * @param attribute The name of the attribute in the <code>DataQuery</code>
     *   which will be used as the value for this column.
     * @param isSortable true if it is possible to sort using this column, false
     *   otherwise
     * @param renderer a {@link TableCellRenderer} that will be used to
     *   format the attribute as a string.
     * @return the newly added column
     */
    public TableColumn addColumn(String label, String attribute,
                                 boolean isSortable,
                                 TableCellRenderer renderer) {
        return addColumn(label, attribute, isSortable, renderer, null);
    }


    /**
     * Add a column to this table.
     *
     * @param label  The user-readable label for the column
     * @param attribute The name of the attribute in the <code>DataQuery</code>
     *   which will be used as the value for this column.
     * @param isSortable true if it is possible to sort using this column, false
     *   otherwise
     * @param renderer a {@link TableCellRenderer} that will be used to
     *   format the attribute as a string.
     * @param orderAttribute The name of the attribute which will be used
     *   as the column to order by. This key may be different from
     *   the <code>attribute</code> parameter.
     * @return the newly added column
     */
    public TableColumn addColumn(String label, String attribute,
                                 boolean isSortable, TableCellRenderer renderer, String orderAttribute) {
        DataTableColumnModel model = (DataTableColumnModel)getColumnModel();
        TableColumn column = new SortableTableColumn (
                                                      model.size(), label, attribute, isSortable, renderer
                                                      );

        model.add(column, orderAttribute);

        // Update the default sort order
        if(isSortable && getDefaultOrder() == null)
            setDefaultOrder((orderAttribute == null )? attribute :
                            orderAttribute);

        return column;
    }


    /**
     * Add a column to this table.
     *
     * @param label  The user-readable label for the column
     * @param attribute The name of the attribute in the <code>DataQuery</code>
     *   which will be used as the value for this column.
     * @param isSortable true if it is possible to sort using this column, false
     *   otherwise
     * @return the newly added column
     */
    public TableColumn addColumn(String label, String attribute,
                                 boolean isSortable) {
        return addColumn(label, attribute, isSortable,
                         new DefaultTableCellRenderer(false));
    }

    /**
     * Add a column to this table.
     *
     * @param label  The user-readable label for the column
     * @param attribute The name of the attribute in the <code>DataQuery</code>
     *   which will be used as the value for this column.
     * @return the newly added column
     */
    public TableColumn addColumn(String label, String attribute) {
        return addColumn(label, attribute, false);
    }


    /**
     * Add a column to this table. The value for the column will not
     * be supplied by the query; instead, it is the user's responsibility
     * to supply the value through a custom {@link TableModel} or render it
     * directly in the {@link TableCellRenderer}. Typically, this method
     * will be used to add {@link ControlLink}s to the table.
     *
     * @param label  The user-readable label for the column
     * @param renderer The cell renderer for the given column
     * @return the newly added column
     */
    public TableColumn addColumn(String label, TableCellRenderer renderer) {
        TableColumnModel m = getColumnModel();
        TableColumn c = new TableColumn(m.size(), label);
        c.setCellRenderer(renderer);
        c.setHeaderRenderer(new GlobalizedHeaderCellRenderer(false));
        m.add(c);
        return c;
    }

    /**
     * @return the {@link DataQueryBuilder} that creates
     *   a {@link DataQuery} for this table during each request
     */
    public DataQueryBuilder getDataQueryBuilder() {
        return m_builder;
    }

    /**
     * @param builder the new {@link DataQueryBuilder} for this table
     */
    public void setDataQueryBuilder(DataQueryBuilder builder) {
        Assert.assertNotLocked(this);
        m_builder = builder;
    }

    /**
     * Set the {@link SingleSelectionModel} that will determine the order
     * for the items in the tabke
     *
     * @param orderModel The new model
     */
    public void setOrderSelectionModel(SingleSelectionModel orderModel) {
        Assert.assertNotLocked(this);
        m_orderModel = orderModel;
    }

    /**
     * @return the {@link SingleSelectionModel} that will determine the order
     */
    public SingleSelectionModel getOrderSelectionModel() {
        return m_orderModel;
    }

    /**
     * Add a {@link QueryListener} to this table. The listener
     * will be fired whenever the query is about to be performed.
     *
     * @param l the new query listener
     */
    public void addQueryListener(QueryListener l) {
        Assert.assertNotLocked(this);
        m_queryListeners.add(QueryListener.class, l);
    }

    /**
     * Remove a {@link QueryListener} from this table.
     *
     * @param l the new query listener
     */
    public void removeQueryListener(QueryListener l) {
        Assert.assertNotLocked(this);
        m_queryListeners.remove(QueryListener.class, l);
    }

    /**
     * Fire the query event listeners to indicate that a query
     * is about to be performed
     *
     * @param state The page state
     * @param query The {@link DataQuery}
     */
    protected void fireQueryPending(PageState state, DataQuery query) {
        Iterator
            i = m_queryListeners.getListenerIterator(QueryListener.class);
        QueryEvent e = null;

        while (i.hasNext()) {
            if ( e == null ) {
                e = new QueryEvent(this, state, query);
            }
            ((QueryListener) i.next()).queryPending(e);
        }
    }

    /**
     * Set the column by which the table will be ordered
     *
     * @param s the page state
     * @param attr the attribute by which the table will be sorted
     */
    public void setOrder(PageState s, String attr) {
        getOrderSelectionModel().setSelectedKey(s, attr);
    }

    /**
     * @param s the page state
     * @return the column by which the table will be ordered
     */
    public String getOrder(PageState s) {
        return (String)getOrderSelectionModel().getSelectedKey(s);
    }

    /**
     * @param s the page state
     * @return the order by which the currently selected column
     *   will be sorted; will be either ASCENDING or DESCENDING
     */
    public String getOrderDirection(PageState s) {
        return (String)s.getValue(m_dirParam);
    }

    /**
     * Set the sort direction
     *
     * @param s the page state
     * @param dir the direction in which the current column
     *   should be sorted; either ASCENDING or DESCENDING
     */
    public void setOrderDirection(PageState s, String dir) {
        Assert.assertTrue(ASCENDING.equals(dir) || DESCENDING.equals(dir));
        s.setValue(m_dirParam, dir);
    }

    /**
     * Toggle the sort direction between ascending and descending
     *
     * @param s the page state
     * @return the new order direction; will be either ASCENDING or DESCENDING
     */
    public String toggleOrderDirection(PageState s) {
        String dir = getOrderDirection(s);
        dir = (ASCENDING.equals(dir)) ? DESCENDING : ASCENDING;
        setOrderDirection(s, dir);
        return dir;
    }

    /**
     * Return the {@link DataQuery} that will be used during the current
     *   request
     * @param s the page state for the current request
     * @return the current <code>DataQuery</code>
     */
    public DataQuery getDataQuery(PageState s) {
        return ((DataQueryTableModel)getTableModel(s)).getDataQuery();
    }

    /**
     * Paginate the query according to the paginator component.
     * This method will be automatically called by the {@link Paginator}
     * component to which this <code>DataTable</code> has been added
     * as the model builder.
     *
     * @param paginator the parent <code>Paginator</code>
     * @param s the current page state
     * @return the total number of rows in the query
     */
    public int getTotalSize(Paginator paginator, PageState s) {
        DataQuery q = getDataQuery(s);
        BigDecimal size = (BigDecimal)m_querySize.get(s);

        if(size == null) {
            size = new BigDecimal(q.size());
            m_querySize.set(s, size);
        }

        q.setRange(new Integer(paginator.getFirst(s)),
                   new Integer(paginator.getLast(s) + 1));
        return size.intValue();
    }

    /**
     * Return the paginator component used by this table, or null
     * if the table is not paginated.
     */
    public final Paginator getPaginator() {
        return m_paginator;
    }

    /**
     * Set the paginator component used by this table, or null
     * if the table should not be paginated.
     */
    public final void setPaginator(Paginator p) {
        Assert.assertNotLocked(this);
        m_paginator = p;
    }

    /**
     * Return the RequestLocal used for storing the
     * query size during the request
     */
    protected final RequestLocal getQuerySizeLocal() {
        return m_querySize;
    }

    /**
     * Lock this table
     */
    public void lock() {
        m_builder.lock();
        super.lock();
    }

    // Export the current order
    public void generateExtraXMLAttributes(PageState s, Element element) {
        String key = getOrder(s);
        if (key != null) {
            element.addAttribute("order",
                                 Integer.toString(getColumnModel().getIndex(key)));
        }
        String dir = getOrderDirection(s);
        if (dir != null) {
            element.addAttribute("direction", dir);
        }
    }

    /**
     * Globalizes the specified key.
     */
    public GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, m_resourceBundle);
    }

    /**
     * Return the resource bundle for globalization,
     * or null if no bundle was specified
     */
    public String getResourceBundle() {
        return m_resourceBundle;
    }

    /**
     * Set the resource bundle for globalization,
     * or null if no globalization is needed
     */
    public void setResourceBundle(String bundle) {
        Assert.assertNotLocked(this);
        m_resourceBundle = bundle;
    }

    /**
     * A {@link TableColumn} that could potentially be sorted
     */
    public static class SortableTableColumn extends TableColumn {

        private boolean m_sortable;
        private SingleSelectionModel m_orderModel;

        /**
         * Construct a new SortableTableColumn
         *
         * @param modelIndex the index of the column in the table model from
         * which to retrieve values.
         * @param value the value for the column header.
         * @param key the key for the column header.
         * @param isSortable whether the column is sortable or not
         * @param renderer the renderer which will be used to render this column
         */
        public SortableTableColumn(
                                   int modelIndex, Object value, Object key,
                                   boolean isSortable, TableCellRenderer renderer
                                   ) {

            super(modelIndex, value, key);
            setSortable(isSortable);
            setCellRenderer(renderer);
        }

        /**
         * Determine whether this column is sortable
         * @param isSortable if true, the column will be sortable
         */
        public void setSortable(boolean isSortable) {
            Assert.assertNotLocked(this);
            m_sortable = isSortable;
            setHeaderRenderer(new GlobalizedHeaderCellRenderer(isSortable));
        }

        /**
         * @return the {@link SingleSelectionModel} which is responsible
         *   for maintaining the sort order
         */
        public SingleSelectionModel getOrderSelectionModel() {
            return m_orderModel;
        }

        /**
         * @return true if this column is sortable, false otherwise
         */
        public boolean isSortable() {
            return m_sortable;
        }

    }

    /**
     * The action listener that will sort the {@link DataQuery}
     * for this table
     */
    private static class DataTableActionListener
        implements TableActionListener {

        public void cellSelected(TableActionEvent e) {}

        public void headSelected(TableActionEvent e) {
            PageState s = e.getPageState();
            DataTable t = (DataTable)e.getSource();

            int index = e.getColumn().intValue();
            SortableTableColumn c =
                (SortableTableColumn)t.getColumnModel().get(index);

            if (c != null) {
                if (c.isSortable()) {
                    DataTableColumnModel m =
                        (DataTableColumnModel)t.getColumnModel();
                    String oldOrder = t.getOrder(s);
                    String newOrder = (String)m.getColumnKey(c);
                    if(newOrder == null)
                        newOrder = (String)c.getHeaderKey();
                    if(oldOrder != null && oldOrder.equals(newOrder)) {
                        // Reverse direction
                        t.toggleOrderDirection(s);
                    } else {
                        t.setOrder(s, newOrder);
                        t.setOrderDirection(s, DataTable.ASCENDING);
                    }
                }
            }
        }
    }

    /**
     * Adapts a {@link DataQueryBuilder} into a {@link TableModelBuilder}.
     * Wraps the query returned by the builder in a DataQueryTableModel.
     *
     * @see com.arsdigita.toolbox.ui.DataTable.DataQueryTableModel
     */
    protected static class DataBuilderAdapter extends LockableImpl
        implements TableModelBuilder {

        /**
         * Create a new <code>DataBuilderAdapter</code>
         */
        public DataBuilderAdapter() {
            super();
        }

        /**
         * Obtain a {@link DataQuery} and apply query events to it.
         * The query events may add additional filters to the query,
         * among other things. Finally, retrieve the current sort
         * column from the parent {@link DataTable} and apply it to
         * the query
         *
         * @see com.arsdigita.toolbox.ui.DataTable.DataQueryTableModel
         * @param t the parent {@link DataTable}
         * @param s the current page state
         * @return the final {@link DataQuery}, which is now ready
         *   to be wrapped in a DataQueryTableModel
         */
        protected DataQuery createQuery(DataTable t, PageState s) {
            DataQuery d = t.getDataQueryBuilder().makeDataQuery(t, s);
            String o = t.getOrder(s);
            if (o != null) {
                String dir = t.getOrderDirection(s);
                if(dir != null) o += " " + dir;
                d.addOrder(o);
            }
            t.fireQueryPending(s, d);

            // Paginate the query if neccessary
            if(t.getPaginator() != null) {
                // Force the size to calculate before the range is set
                if(t.getQuerySizeLocal().get(s) == null)
                    t.getQuerySizeLocal().set(s, new BigDecimal(d.size()));

                // Paginate the query
                d.setRange(new Integer(t.getPaginator().getFirst(s)),
                           new Integer(t.getPaginator().getLast(s) + 1));
            }

            return d;
        }

        /**
         * Construct a DataQueryTableModel by wrapping the query.
         *
         * @param table the parent {@link DataTable}
         * @param s the current page state
         * @see com.arsdigita.toolbox.ui.DataTable.DataQueryTableModel
         * @return a DataQueryTableModel that will iterate through the
         * query
         */
        public TableModel makeModel(Table table, PageState s) {
            DataTable t = (DataTable)table;
            DataQuery d = createQuery(t, s);

            if(d == null)
                return Table.EMPTY_MODEL;

            return new DataQueryTableModel(t, d,
                                           t.getDataQueryBuilder().getKeyColumn());
        }
    }

    /**
     * A TableModel which gets its data from a DataQuery. This TableModel
     * is used in the {@link DataTable.DataBuilderAdapter} to iterate through the
     * query returned by the {@link DataQueryBuilder} and generate
     * rows for it on the screen.
     */
    protected static class DataQueryTableModel implements TableModel {

        private DataQuery m_data;
        private DataTableColumnModel m_cols;
        private String m_keyColumn;

        /**
         * Create a new <code>DataQueryTableModel</code>
         *
         * @param t the {@link DataTable} which needs this model
         * @param data the {@link DataQuery to be wrapped}
         * @param keyColumn the name of the column in the query which represents
         *   the primary key
         * @pre data != null
         * @pre keyColumn != null
         * @pre t != null
         * @pre t.getColumnModel() != null
         */
        public DataQueryTableModel(DataTable t, DataQuery data, String keyColumn) {
            m_data = data;
            m_cols = (DataTableColumnModel)t.getColumnModel();
            m_keyColumn = keyColumn;
        }

        public int getColumnCount() {
            return m_cols.size();
        }

        public boolean nextRow() {
            return m_data.next();
        }

        public Object getElementAt(int columnIndex) {
            String key = (String)m_cols.get(columnIndex).getHeaderKey();
            if (key != null) {
                return m_data.get(key);
            } else {
                return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            String key = m_cols.getKeyAt(columnIndex);
            if (key != null) {
                return m_data.get(key);
            } else {
                return m_data.get(m_keyColumn);
            }
        }

        /**
         * Return the original DataQuery. The query's cursor will be
         * "pointing" at the current row
         */
        public DataQuery getDataQuery() {
            return m_data;
        }
    }

    /**
     * Always renders the table header as a link. Thus, it
     * becomes possible to sort up and down by clicking
     * the table column over and over.<p>
     * Also, globalizes the column labels if possible.
     */
    protected static class GlobalizedHeaderCellRenderer
        implements TableCellRenderer {

        private boolean m_active;

        public GlobalizedHeaderCellRenderer(boolean isActive) {
            m_active = isActive;
        }

        public GlobalizedHeaderCellRenderer() {
            this(true);
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            DataTable t = (DataTable)table;
            Label label;

            if(value == null) {
                label = new Label("&nbsp;", false);
            } else {
                String str = value.toString();
                if(t.getResourceBundle() != null)
                    label = new Label(t.globalize(str));
                else
                    label = new Label(str);
            }

            if(m_active)
                return new ControlLink(label);
            else
                return label;
        }
    }

    /**
     * A special column model that maintains an alternate key
     * for each column. The alternate key will be passed
     * to the query in the <code>addOrder</code> method, thus
     * sorting the query by the given column - making it possible
     * to make the sort key differ from the attribute key for
     * any given column.
     * <p>
     * Note that each column ALREADY has a unique key, which
     * can be retrieved by calling <code>TableColumn.getHeaderKey()</code>.
     * This key will be used to provide the value for the column.
     */
    protected static class DataTableColumnModel extends DefaultTableColumnModel {

        // The column keys are a property of the table and column
        // combination so we store the values in the HashMap
        private Map m_columnKeys = new HashMap();

        public void add(TableColumn column, String columnKey) {
            super.add(column);
            setColumnKey(column, columnKey);
        }

        public void add(int columnIndex, TableColumn column, String columnKey) {
            super.add(columnIndex, column);
            setColumnKey(column, columnKey);
        }

        public String getColumnKey(TableColumn column) {
            return (String)m_columnKeys.get(column);
        }

        public String getKeyAt(int columnIndex) {
            return getColumnKey(get(columnIndex));
        }

        public void setColumnKey(TableColumn column, String columnKey) {
            m_columnKeys.put(column, columnKey);
        }

        public void setColumnKey(int columnIndex, String columnKey) {
            setColumnKey(get(columnIndex), columnKey);
        }

        public void remove(TableColumn column) {
            super.remove(column);
            m_columnKeys.remove(column);
        }
    }
}
