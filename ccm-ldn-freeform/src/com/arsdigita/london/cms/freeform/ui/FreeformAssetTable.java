package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.london.cms.freeform.FreeformContentItem;
import com.arsdigita.cms.ItemSelectionModel;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;

import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;

/**
 * A table which displays the assets for a {@link FreeformContentItem}.
 * 
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 * @version $Revision: #1 $ 
 **/
public class FreeformAssetTable extends PaginationTable {

    // $Change: 20745 $
    // $Revision: #1 $
    // $DateTime: 2002/09/10 05:47:07 $
    // $Author: sskracic $
    
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_DESC = "Description";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_MOVEUP = "Up";
    public static final String COLUMN_MOVEDOWN = "Down";

    private static final String ID_NAME = "itemId";
    private static final String QUERY_NAME = 
        "com.arsdigita.london.cms.freeform.FreeformGetAssetAttributes";
    private static final String ASSET_ID = "id";
    private static final String ASSET_NAME = "name";
    private static final String ASSET_DESC = "description";
    private static final String ASSET_TYPE = "mimeType";
    private static final String ASSET_RANK = "rank";

    // The selection model that returns the FreeformContentItem that
    // the Assets being displayed belong to.
    private ItemSelectionModel m_itemModel;

    /**
     * Constructor.
     *
     * @param itemModel A selection model that returns the {@link
     * FreeformContentItem} which holds the assets to display.
     **/
    public FreeformAssetTable(ItemSelectionModel itemModel) {
        super(new FreeformAssetTMB(itemModel));

        ((FreeformAssetTMB) getPaginationModelBuilder()).setAssetTable( this );

        m_itemModel = itemModel;        

        // add columns to the table
        TableColumnModel model = getColumnModel();
        model.add(new TableColumn(0, COLUMN_NAME));
        model.add(new TableColumn(1, COLUMN_DESC));
        model.add(new TableColumn(2, COLUMN_TYPE));
        model.add(new TableColumn(3, COLUMN_MOVEUP));
        model.add(new TableColumn(4, COLUMN_MOVEDOWN));

        // set the name/down/up columns to be rendered as action links
        model.get(0).setCellRenderer(new AssetTableCellRenderer(true));
        model.get(3).setCellRenderer(new AssetTableCellRenderer(true));
        model.get(4).setCellRenderer(new AssetTableCellRenderer(true));

        // a table action listener to process the rank up/down column
        // links
        addTableActionListener(new TableActionListener() {
            public void cellSelected(TableActionEvent event) { 
                FreeformContentItem item = (FreeformContentItem) m_itemModel
                    .getSelectedObject(event.getPageState());
               
                TableColumn tc = getColumnModel()
                    .get(event.getColumn().intValue());
                String columnName = (String) tc.getHeaderValue();
                
                if (COLUMN_MOVEUP.equals(columnName)) {
                    item.changeAssetRank
                        (new BigDecimal((String)event.getRowKey()), true);
                } else if (COLUMN_MOVEDOWN.equals(columnName)) {
                    item.changeAssetRank
                        (new BigDecimal((String)event.getRowKey()), false);
                }
            }
            public void headSelected(TableActionEvent event) {}
        });        
    }

    
    /**o
     * The model responsible for generating a query and returning a
     * {@link TableModel} that will produce the necessary table data
     * for rendering the assets.
     **/
    private static class FreeformAssetTMB extends PaginationTableModelBuilder {

        private ItemSelectionModel m_itemModel;
        private FreeformAssetTable m_table;

        public FreeformAssetTMB(ItemSelectionModel itemModel) {
            super();
            m_itemModel = itemModel;
        }

        public void setAssetTable( FreeformAssetTable table ) {
            m_table = table;
        }

        public boolean isVisible( PageState ps ) {
            return m_table.isVisible( ps );
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            Paginator pgntr = ((PaginationTable)table).getPaginator();
            DataQuery query = getFilteredDataQuery(pgntr, state);
            return new FreeformAssetTM((PaginationTable)table, state, query);
        }

        public DataQuery makeDataQuery(Paginator pgntr, PageState state) {
            
            FreeformContentItem item = (FreeformContentItem)m_itemModel
                .getSelectedObject(state);
            DataQuery query = SessionManager.getSession().retrieveQuery(QUERY_NAME);
            query.setParameter(ID_NAME, item.getID());
            query.addOrder(ASSET_RANK);
            return query;
        }                                      
    }

    /**
     * The model used by this table for producing the data that will
     * be rendering within the table.
     **/
    private static class FreeformAssetTM implements TableModel {

        private TableColumnModel m_columnModel;
        private PageState m_state;
        private DataQuery m_query;

        private BigDecimal m_id = null;
        private String m_name = null;
        private String m_desc = null;
        private String m_type = null;

        private int m_pageNum;
        private int m_pageSize;
        private int m_totalSize;

        boolean m_isFirstPage;
        boolean m_isLastPage;
        private long m_valueCounter;

        public FreeformAssetTM(PaginationTable table, PageState state, 
                               DataQuery query) {

            m_columnModel = table.getColumnModel();
            m_state = state;
            m_query = query;

            Paginator pgntr = table.getPaginator();

            m_pageNum = pgntr.getSelectedPageNum(state);
            m_pageSize = pgntr.getPageSize(state);
            m_totalSize = table.getPaginationModelBuilder()
                .getTotalSize(pgntr, state);

            m_isFirstPage = (m_pageNum == 1);
            m_isLastPage = (m_pageNum * m_pageSize > m_totalSize);
            m_valueCounter = 0;
        }

        public int getColumnCount() {
            return m_columnModel.size();
        }

        public boolean nextRow() {
            if (m_query != null && m_query.next()) {

                m_id = (BigDecimal)m_query.get(ASSET_ID);
                m_name = (String)m_query.get(ASSET_NAME);
                m_desc = (String)m_query.get(ASSET_DESC);
                m_type = (String)m_query.get(ASSET_TYPE);
                m_valueCounter++;
                
                return true;
            }
            return false;
        }

        public Object getElementAt(int columnIndex) {
            if (m_columnModel == null) { return null; }
            TableColumn tc = m_columnModel.get(columnIndex);
            String columnName = (String) tc.getHeaderValue();

            if (COLUMN_NAME.equals(columnName)) {
                return m_name;
            } else if (COLUMN_DESC.equals(columnName)) {
                return m_desc;
            } else if (COLUMN_TYPE.equals(columnName)) {
                if (m_type == null) {
                    return "unknown";
                }
                return m_type;
            } else if (COLUMN_MOVEUP.equals(columnName)) {
                return (m_valueCounter == 1 && m_pageNum == 1)
                    ? null : "^";
            } else if (COLUMN_MOVEDOWN.equals(columnName)) {
                return ((m_pageNum - 1) * m_pageSize + 
                        m_valueCounter == m_totalSize)
                    ? null : "v";
            }

            return null;
        }

        public Object getKeyAt(int columnIndex) {
            return m_id;
        }
    }

}
