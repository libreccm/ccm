package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.toolbox.ui.QueryEvent;
import com.arsdigita.toolbox.ui.QueryListener;

import java.util.ArrayList;

/**
 * A paginated table. This class, along with {@link
 * PaginationTableModelBuilder}, is designed to help reduce the amount
 * of code that is needed to use a {@link Paginator} with a {@link
 * Table} where the generated rows are produced by a {@link
 * DataQuery}. Since the <code>PaginationTableModelBuilder</code>
 * exposes the <code>DataQuery</code> used in the <code>TableModel</code>,
 * methods have been added to support filtering of the
 * <code>DataQuery</code> by adding {@link QueryListener}s. 
 *  
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 * @version $Id: PaginationTable.java 753 2005-09-02 13:22:34Z sskracic $
 **/
public class PaginationTable extends Table {

    // $Change: 20745 $
    // $Revision: #1 $
    // $DateTime: 2002/09/10 05:47:07 $
    // $Author: sskracic $

    private PaginationTableModelBuilder m_builder;
    private Paginator m_paginator;
    private ArrayList m_listeners;

    /**
     * Constructor.
     *
     * @param builder The {@link PaginationTableModelBuilder} which
     * returns the <code>TableModel</code> for each request.
     **/
    public PaginationTable(PaginationTableModelBuilder builder) {
        this(builder, new DefaultTableColumnModel());
    }

    /**
     * Constructor.
     *
     * @param builder The {@link PaginationTableModelBuilder} which
     * returns the <code>TableModel</code> for each request.
     * @param columnModel The {@link TableColumnModel} that will
     * maintain the columns and headers for this table.
     **/
    public PaginationTable(PaginationTableModelBuilder builder, 
                           TableColumnModel columnModel) {
        super(builder, columnModel);
        m_builder = builder;
        m_builder.setPaginationTable(this);
        m_listeners = new ArrayList();
    }
    
    /**
     * Returns the {@link Paginator} that is used with this table.
     *
     * @return The {@link Paginator} that is used with this table.
     **/
    public Paginator getPaginator() {
        return m_paginator;
    }

    /**
     * Sets the {@link Paginator} to use with this table. Since the
     * {@link Paginator} needs the {@link PaginationModelBuilder} from
     * this table, This method needs to be called after the {@link
     * Paginator} has been instantiated.
     *
     * @param paginator The {@link Paginator} to use with this table.
     **/
    public void setPaginator(Paginator paginator) {
        m_paginator = paginator;
    }

    /**
     * Returns the {@link PaginationModelBuilder} to use in a {@link
     * Paginator} component for this table.
     *
     * @return The {@link PaginationModelBuilder} to use in a {@link
     * Paginator} component for this table.
     **/
    public PaginationModelBuilder getPaginationModelBuilder() {
        return m_builder;
    }
    
    /**
     * Adds a {@link QueryListener} to this table. All QueryListeners
     * will have an opportunity to modify or filter the DataQuery used
     * for this table before the xml is generated.
     *
     * @param listener The query listener to add.
     **/
    public void addQueryListener(QueryListener listener) {
        m_listeners.add(listener);
    }

    /**
     * Removes the {@link QueryListener} specified by <i>listener</i>.
     *
     * @param listener The query listener to remove.
     **/
    public void removeQueryListner(QueryListener listener) {
        int idx = m_listeners.indexOf(listener);
        if (idx != -1) {
            m_listeners.remove(idx);
        }
    }
    
    /**
     * Fires all {@link QueryListener}s on this table to filter the
     * {@link DataQuery} used to generate rows. 
     *
     * @param state Represents the current state of the request.
     * @param query The {@link DataQuery} used to generate rows for
     * this table.
     **/
    public void fireQueryPending( DataQuery query, PageState state) {
        QueryEvent event = new QueryEvent(this, state, query);
        for (int i=0; i<m_listeners.size(); i++) {
            ((QueryListener) m_listeners.get(i)).queryPending(event);
        }
    }

}
