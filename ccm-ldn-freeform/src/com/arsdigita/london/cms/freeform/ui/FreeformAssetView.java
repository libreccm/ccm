package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.london.cms.freeform.FreeformContentItem;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.Asset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.util.LockableImpl;


/**
 * A component to view one asset which displays the name,
 * description, mimetype and a link to downlaod the asset.
 *
 * @author <a href="mailto:slater@arsdigita.com">Michael Slater</a>
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 **/
public class FreeformAssetView extends BoxPanel {
    
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_VALUE = "Value";

    private AssetSelectionModel m_assetModel;
    private Link m_download = new Link("Download Placeholder", "/foo/bar/baz");

    /**
     * Constructor.
     **/
    public FreeformAssetView() {
        super(BoxPanel.VERTICAL);
        m_assetModel = new AssetSelectionModel();
        
        Table t = new Table(new AssetPropertyTableBuilder(m_assetModel), 
                            new DefaultTableColumnModel());
        add(t);
        t.setHeader(null);
        t.setDefaultCellRenderer(new AssetTableCellRenderer(false));

        Label emptyView = new Label("No asset could be found");
        emptyView.setFontWeight(Label.ITALIC);
        t.setEmptyView(emptyView);

        TableColumnModel model = t.getColumnModel();
        model.add(new TableColumn(0, COLUMN_NAME));
        model.add(new TableColumn(1, COLUMN_VALUE));

        add(m_download);        
    }

    /**
     * Constructor.
     *
     * @param assetModel SingleSelectionModel holding the asset we are
     * displaying
     * @param itemModel ItemSelectionModel holding the
     * FreeformContentItem this asset belongs to
     **/
    public FreeformAssetView(SingleSelectionModel assetModel, ItemSelectionModel itemModel) {
        this();

        m_assetModel.setAssetModel(assetModel);
        m_assetModel.setItemModel(itemModel);        
    }

    /**
     * Returns the asset selection model.
     **/
    public AssetSelectionModel getAssetSelectionModel() {
        return m_assetModel;
    }

    /**
     * Registers the parameter models used for selecting the asset.
     **/
    public void register(Page p) {
        super.register(p);
        m_assetModel.register(p, this);
    }

    /**
     * The table builder for the asset property table.
     **/
    private static class AssetPropertyTableBuilder extends LockableImpl
        implements TableModelBuilder {

        AssetSelectionModel m_assetModel;
        
        public AssetPropertyTableBuilder(AssetSelectionModel assetModel) {
            m_assetModel = assetModel;
        }
        
        public TableModel makeModel(Table table, PageState state) {
            return new AssetPropertyTableModel(table, state, m_assetModel);
        }
    }

    /**
     * The table model for the asset property table.
     **/
    private static class AssetPropertyTableModel implements TableModel {
        
        private TableColumnModel m_columnModel;
        private PageState m_state;
        private AssetSelectionModel m_assetModel;
        private Asset m_asset;
        private int m_rowNum;

        public AssetPropertyTableModel(Table table, PageState state, 
                                       AssetSelectionModel assetModel) {
            m_columnModel = table.getColumnModel();
            m_state = state;
            m_assetModel = assetModel;
            m_asset = assetModel.getAsset(state);
            m_rowNum = 0;
        }
        
        public int getColumnCount() {
            return m_columnModel.size();
        }
        
        public boolean nextRow() {
            if (m_asset != null && m_rowNum < 4) {
                m_rowNum++;
                return true;
            }
            return false;
        }

        public Object getElementAt(int columnIndex) {
            if (m_columnModel == null) { return null; }
            TableColumn tc = m_columnModel.get(columnIndex);
            String columnName = (String) tc.getHeaderValue();

            if (COLUMN_NAME.equals(columnName)) {
                String key = decodeKey(columnIndex);
                if (key != null) {
                    Label l = new Label(key);
                    l.setFontWeight(Label.BOLD);
                    return l;
                }
                return null;
            } else if (COLUMN_VALUE.equals(columnName)) {
                return decodeValue(columnIndex);
            }
            
            return null;
        }

        public Object getKeyAt(int columnIndex) {
            return decodeKey(columnIndex);
        }

        private String decodeKey(int idx) {
            if (m_rowNum == 1) {
                return "Name:";
            } else if (m_rowNum == 2) {
                return "Description:";
            } else if (m_rowNum == 3) {
                return "MimeType:";
            } else if (m_rowNum == 4) {
                return "Rank:";
            }
            return null;
        }

        private String decodeValue(int idx) {
            if (m_rowNum == 1) {
                return m_asset.getName();
            } else if (m_rowNum == 2) {
                return m_asset.getDescription();
            } else if (m_rowNum == 3) {
                MimeType type = m_asset.getMimeType();
                if (type != null) {
                    return type.getMimeType();
                }
                return "unknown";
            } else if (m_rowNum == 4) {
                return m_assetModel.getRank(m_state);
            }
            return null;
        }
        
    }

       
    public Asset getAsset(PageState state) {
        return m_assetModel.getAsset(state);
    }
    
    public FreeformContentItem getFreeformContentItem(PageState state) {
        return m_assetModel.getFreeformContentItem(state);
    }
    
}
