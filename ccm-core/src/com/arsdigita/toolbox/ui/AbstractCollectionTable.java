/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Component;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * 
 * This provides a base table to show the elements of a DomainCollection
 *
 *
 * @author Randy Graebner
 * @version $Revision: #12 $ $Date: 2004/08/16 $
 */

public abstract class AbstractCollectionTable extends Table
    implements PaginationModelBuilder {

    private RequestLocal m_collection;
    private ArrayList m_columnOrder;
    private RequestLocal m_querySize;
    private StringParameter m_dirParam;

    public static final String DIRECTION = "direction";
    public static final String ASCENDING = "asc";
    public static final String DESCENDING = "desc";

    /**
     *  This expects to receive a RequestLocal that will return
     *  the appropriate DomainCollection to be displayed
     */
    public AbstractCollectionTable(RequestLocal collection) {
        m_collection = collection;
        m_columnOrder = new ArrayList();
        m_querySize = new RequestLocal();

        m_dirParam = new StringParameter(DIRECTION);
        setDefaultOrderDirection(ASCENDING);

        setModelBuilder(new TableModelBuilder() {
                public TableModel makeModel(Table t, PageState ps) {
                    return makeTableModel(ps);
                }

                public void lock() {}
                public boolean isLocked() { return true; }
            });

        addTableActionListener(new TableActionListener() {
                public void cellSelected(TableActionEvent evt) {
                    tableCellSelected(evt.getPageState(),
                                      evt.getRowKey(),
                                      evt.getColumn().intValue());
                }

                public void headSelected(TableActionEvent evt) {
                    tableHeadSelected(evt.getPageState(),
                                      evt.getColumn().intValue());
                }
            });
        setClassAttr("abstractCollectionTable");
    }

    /**
     * Register the ordering parameter
     */
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_dirParam);
    }

    /**
     *  The default sort order
     *  @param direction The direction (either ASCENDING or DESCENDING)
     *  @pre ASCENDING.equals(direction) || DESCENDING.equals(direction)
     */
    public void setDefaultOrderDirection(String direction) {
        Assert.assertTrue(ASCENDING.equals(direction) ||
                          DESCENDING.equals(direction), "The order must " +
                          "be either ascending or descending");
        m_dirParam.setDefaultValue(direction);
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
     * Set the key of the default column which will be
     * used to sort the entries.  This requires that the
     * passed in attribute is an attribute that has already
     * been used for an added column.
     *
     * @param attribute the default attribute to sort by
     */
    public void setDefaultOrder(String attribute) {
        Assert.assertNotLocked(this);
        Assert.assertTrue(m_columnOrder.contains(attribute),
                          "The passed in attribute '" + attribute +
                          "' is not the name of a column.");
        getColumnSelectionModel().getStateParameter()
            .setDefaultValue(new Integer(m_columnOrder.indexOf(attribute)));
    }


    /**
     * Get the key of the default column which will be
     * used to sort the entries
     *
     * @return the default attribute to sort by, or null if
     *   no default has been set
     */
    public String getDefaultOrder() {
        return (String)m_columnOrder.get(((Integer)getColumnSelectionModel()
                                          .getStateParameter().getDefaultValue())
                                         .intValue());
    }


    /**
     *  This adds the column to the table use the default table cell
     *  renderer
     */
    protected TableColumn addColumn(String label, String attribute,
                                    boolean isSortable, String type) {
        return addColumn(label, attribute, isSortable,
                         new DefaultTableCellRenderer(), type);
    }


    /**
     *  This adds the column to the table.
     *
     *  @param label The label for the header of the column
     *  @param attribute The corresponding attribute in the table
     *  @param renderer The cell renderer to use to display the column
     *  @param type The type of the column so that we can later
     *              look up the index of the column
     */
    protected TableColumn addColumn(String label, String attribute,
                                    boolean isSortable,
                                    TableCellRenderer renderer, String type) {
        TableColumnModel cols = getColumnModel();
        TableColumn col = new TableColumn(cols.size(), label, attribute);
        col.setCellRenderer(renderer);
        col.setHeaderRenderer(new AbstractTableCellRenderer(isSortable));
        cols.add(col);
        m_columnOrder.add(type);
        return col;
    }


    public BigDecimal getSelectedTask(PageState ps) {
        SingleSelectionModel ss = getRowSelectionModel();
        return new BigDecimal((String) ss.getSelectedKey(ps));
    }

    protected void tableCellSelected(PageState ps, Object key, int index) {
        // do nothing
    }

    protected void tableHeadSelected(PageState ps, int index) {
        // do nothing
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
        DomainCollection collection = getDomainCollection(s);

        // we hide the value in a RequestLocal to cache the result
        // since this is called several times
        BigDecimal size = (BigDecimal)m_querySize.get(s);
        if (size == null) {
            size = new BigDecimal(collection.size());
            m_querySize.set(s, size);
        }

        DomainServiceInterfaceExposer.getDataCollection(collection)
            .setRange(new Integer(paginator.getFirst(s)),
                      new Integer(paginator.getLast(s) + 1));
        return size.intValue();
    }


    /**
     *   This returns the collection that is displayed by this table.
     */
    public DomainCollection getDomainCollection(PageState state) {
        return (DomainCollection) m_collection.get(state);
    }


    /**
     *  @param index  This is the index to look up
     *  @return this returns the string type that was passed in when
     *          the column was created
     */
    protected String getColumnType(int index) {
        return (String)m_columnOrder.get(index);
    }


    /**
     *  This method should return the table model to use.  In most
     *  cases, you are going to just want to iterate through your
     *  DomianCollection, returning the correctly values in getElementAt
     */
    protected abstract TableModel makeTableModel(PageState ps);

    // Export the current order
    public void generateExtraXMLAttributes(PageState state, Element element) {
        Integer index = (Integer) getColumnSelectionModel().getSelectedKey(state);
        if (index != null) {
            element.addAttribute("order", index.toString());
        }
        String dir = getOrderDirection(state);
        if (dir != null) {
            element.addAttribute("direction", dir);
        }
    }


    /**
     *  Displays the table headers such that if the item is sortable
     *  then a link is always shown and if it is not sortable then
     *  a link is never shown
     */
    private class AbstractTableCellRenderer extends LockableImpl
        implements TableCellRenderer {

        private boolean m_active;
        private ThreadLocal m_label;
        private ThreadLocal m_controlLink;

        AbstractTableCellRenderer(boolean isActive) {
            m_active = isActive;
            m_label = new ThreadLocal() {
                    protected Object initialValue() {
                        return new Label("");
                    }
                };
            m_controlLink = new ThreadLocal() {
                    protected Object initialValue() {
                        return new ControlLink((Label) m_label.get());
                    }
                };
        }

        /**
         * Return the component that should be used to render the given
         * <code>value</code>.  Returns a
         * {@link com.arsdigita.bebop.ControlLink} if the column is sortable
         * and a {@link com.arsdigita.bebop.Label} if the column is not
         *
         * @pre table == null || table != null
         */
        public Component getComponent(Table table, PageState state,
                                      Object value, boolean isSelected,
                                      Object key, int row, int column) {
            if ( ! isLocked() && table != null && table.isLocked() ) {
                lock();
            }

            // this ensures that it happens on the first column
            // and that the rest of the columns are all consistent
            // we may want to change this so that all but the selected
            // column have ASCENDING
            if (column == 0) {
                setOrderDirection(state, toggleOrderDirection(state));
            }
            Label l;
            if ( value instanceof com.arsdigita.bebop.Component ) {
                return (com.arsdigita.bebop.Component) value;
            } else {
                l = (Label) m_label.get();

                if ( value == null ) {
                    l.setLabel( (String) GlobalizationUtil.globalize("toolbox.ui.").localize());
                    l.setOutputEscaping(false);
                } else {
                    l.setLabel(value.toString());
                    l.setOutputEscaping(true);
                }
            }
            l.setFontWeight( (isSelected && m_active) ? Label.BOLD : null );
            if (m_active ) {
                return (ControlLink) m_controlLink.get();
            } else {
                return l;
            }
        }
    }
}
