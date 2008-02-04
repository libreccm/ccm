package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.util.LockableImpl;

/**
 * A pagination table model builder. This class, along with {@link
 * PaginationTable}, is designed to help reduce the amount of code
 * that is needed to use a {@link Paginator} with a {@link Table}
 * where the generated rows are produced by a {@link
 * DataQuery}. Subclasses will need to implement the {@link
 * #makeModel(Table, PageState)} and the {@link
 * #makeDataQuery(Paginator, PageState)}
 *
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 **/
public abstract class PaginationTableModelBuilder extends LockableImpl
    implements TableModelBuilder, PaginationModelBuilder {

    // $Change: 20745 $
    // $Revision: #1 $
    // $DateTime: 2002/09/10 05:47:07 $
    // $Author: sskracic $

    private PaginationTable m_table; 
    private RequestLocal m_query;
    private RequestLocal m_size;

    /**
     * Constructor. 
     **/
    public PaginationTableModelBuilder() {
        super();
        m_query = new RequestLocal();
        m_size = new RequestLocal();
    }

    /**
     * Sets the {@link PaginationTable} that uses this builder. This
     * is called by the {@link PaginationTable} when this builder is
     * added to it and should not be called otherwise.
     *
     * @param The {@link PaginationTable} that uses this builder.
     **/
    protected void setPaginationTable(PaginationTable table) {
        m_table = table;
    }

    /**
     * Returns the {@link PaginationTable} that uses this builder.
     *
     * @return The {@link PaginationTable} that usrs this builder.
     **/
    public PaginationTable getPaginationTable() {
        return m_table;
    }

    /**
     * Returns a table model specific to the current
     * request. Subclasses will typically pass the results of {@link
     * #getFilteredDataQuery(Paginator, PageState)} to the returned
     * {@link TableModel}. This method satisfies the {@link
     * TableModelBuilder} interface requirements.
     * 
     * @param table The table to build the model for.
     * @param state Represents the current state of the request.
     **/
    public abstract TableModel makeModel(Table table, PageState state);

    /**
     * Returns a {@link DataQuery} specific to the currect request
     * which is used to generate rows for the table. Subclasses will
     * typically return the same {@link DataQuery} that would be used
     * in a regular {@link TableModelBuilder}.
     *
     * @param paginator The object used to paginate results.
     * @param state Represents the current state of the request.
     **/
    public abstract DataQuery makeDataQuery(Paginator paginator, 
                                            PageState state);

    /**
     * Returns the {@link DataQuery} filtered by QueryListeners from
     * the {@link PaginationTable} with a range set by the selected
     * page view from the {@link Paginator}.
     *
     * @param paginator The object used to paginate results.
     * @param state Represents the current state of the request.
     **/
    public DataQuery getFilteredDataQuery(Paginator paginator, 
                                          PageState state) {
        DataQuery query = getDataQuery(paginator, state);
        if (query != null && paginator != null) {
            query.setRange(new Integer(paginator.getFirst(state)), 
                           new Integer(paginator.getLast(state) + 1));
        }
        return query;
    }
   
    /**
     * Returns total number of results for the table. This method
     * satisfies the {@link PaginationModelBuilder} interface
     * requirements.
     *
     * @param paginator The object used to paginate results.
     * @param state Represents the current state of the request.
     **/
    public int getTotalSize(Paginator paginator, PageState state) {
        Integer size = (Integer) m_size.get(state);

        if (size == null) {            
            DataQuery query = getDataQuery(paginator, state);
            if (query == null) {
                return 0;
            } 

            size = new Integer((int) query.size());                
            m_size.set(state, size);
        }

        return size.intValue();
    }

    /**
     * Returns the {@link DataQuery} after applying QueryListeners
     * from the {@link PaginationTable} on the {@link DataQuery}
     * returned by {@link makeDataQuery(Paginator, PageState)}. This
     * convenience method caches the resulting <code>DataQuery</code>
     * in a {@link RequestLocal} to improve performance.
     *
     * @param paginator The object used to paginate results.
     * @param state Represents the current state of the request.
     **/
    private DataQuery getDataQuery(Paginator paginator,
                                   PageState state) {
        DataQuery query = (DataQuery) m_query.get(state);

        if (query == null) {
            query = makeDataQuery(paginator, state);
            if (query == null) {
                return null;
            }

            if (m_table != null) {
                m_table.fireQueryPending(query, state);
            }
            m_query.set(state, query);
        }

        return query;
    }

}
