package com.arsdigita.cms.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ItemSearchFlatBrowsePane extends Form implements FormInitListener, FormProcessListener {

    private static final String QUERY_PARAM = "query";
    public static final String WIDGET_PARAM = "widget";
    public static final String SEARCHWIDGET_PARAM = "searchWidget";
    private final Table resultsTable;
    private final StringParameter queryParam;
    

    public ItemSearchFlatBrowsePane(final String name) {
        super(name);

        queryParam = new StringParameter(QUERY_PARAM);

        final BoxPanel boxPanel = new BoxPanel(BoxPanel.HORIZONTAL);
        boxPanel.add(new Label(GlobalizationUtil.globalize("cms.ui.item_search.flat.filter")));
        final TextField filter = new TextField(new StringParameter(QUERY_PARAM));                   
        boxPanel.add(filter);
        add(boxPanel);

        resultsTable = new ResultsTable();
        add(resultsTable);

        addInitListener(this);
        addProcessListener(this);
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addComponentStateParam(this, queryParam);
    }

    public void init(final FormSectionEvent fse) throws FormProcessException {        
        
    }

    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();

        state.setValue(queryParam, data.get(QUERY_PARAM));
    }

    private class ResultsTable extends Table {

        private static final String TABLE_COL_TITLE = "title";
        private static final String TABLE_COL_PLACE = "place";
        private static final String TABLE_COL_TYPE = "type";

        public ResultsTable() {
            super();
            setEmptyView(new Label(GlobalizationUtil.globalize("cms.ui.item_search.flat.no_items")));

            final TableColumnModel columnModel = getColumnModel();
            columnModel.add(new TableColumn(0,
                                            GlobalizationUtil.globalize("cms.ui.item_search.flat.title").localize(),
                                            TABLE_COL_TITLE));
            columnModel.add(new TableColumn(1,
                                            GlobalizationUtil.globalize("cms.ui.item_search.flat.place").localize(),
                                            TABLE_COL_PLACE));
            columnModel.add(new TableColumn(2,
                                            GlobalizationUtil.globalize("cms.ui.item_search.flat.type").localize(),
                                            TABLE_COL_TYPE));

            setModelBuilder(new ResultsTableModelBuilder());
            
            columnModel.get(0).setCellRenderer(new TitleCellRenderer());
        }

    }

    private class ResultsTableModelBuilder extends LockableImpl implements TableModelBuilder {

        public TableModel makeModel(final Table table, final PageState state) {
            return new ResultsTableModel(table, state);
        }

    }

    private class ResultsTableModel implements TableModel {

        private final Table table;
        private final DataCollection collection;
        private ContentItem currentItem;

        public ResultsTableModel(final Table table, final PageState state) {
            this.table = table;
            final Session session = SessionManager.getSession();
            final BigDecimal typeId = (BigDecimal) state.getValue(new BigDecimalParameter(ItemSearch.SINGLE_TYPE_PARAM));
            if (typeId == null) {
                collection = session.retrieve(ContentPage.BASE_DATA_OBJECT_TYPE);
            } else {
                final ContentType type = new ContentType(typeId);
                collection = session.retrieve(type.getClassName());
            }

            final String query = (String) state.getValue(queryParam);
            if ((query != null) && !query.isEmpty()) {
                collection.addFilter(String.format("(lower(%s) like lower('%%%s%%')) or (lower(%s) like lower('%%%s%%'))",
                                                   ContentItem.NAME, query,
                                                   ContentPage.TITLE, query));
            }
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((collection != null) && collection.next()) {
                currentItem = (ContentItem) DomainObjectFactory.newInstance(collection.getDataObject());
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    if (currentItem instanceof ContentPage) {
                        return ((ContentPage) currentItem).getTitle();
                    } else {
                        return currentItem.getName();
                    }
                case 1:
                    return getItemPath(currentItem);
                case 2:
                    return currentItem.getContentType().getLabel();
                default:
                    return null;
            }
        }

        private String getItemPath(final ContentItem item) {
            final StringBuilder path = new StringBuilder(item.getName());

            ContentItem current = item;

            while (current.getParent() != null) {
                if (current.getParent() instanceof ContentBundle) {
                    current = (ContentBundle) current.getParent();
                } else if (current.getParent() instanceof Folder) {
                    current = (Folder) current.getParent();
                    if (!current.getName().equals("/")) {
                        path.insert(0, '/');
                        path.insert(0, current.getName());
                    }
                }
            }

            path.insert(0, ":/");
            path.insert(0, item.getContentSection().getName());

            return path.toString();
        }

        public Object getKeyAt(final int columnIndex) {
            return currentItem.getID();
        }
    }
    
    private class TitleCellRenderer extends LockableImpl implements TableCellRenderer {

        public Component getComponent(final Table table, 
                                      final PageState state, 
                                      final Object value, 
                                      final boolean isSelected, 
                                      final Object key,
                                      final int row, 
                                      final int column) {
            final Link link = new Link(value.toString(), "");
            
            final String widget = (String) state.getValue(new StringParameter(WIDGET_PARAM));
            final String searchWidget = (String) state.getValue(new StringParameter(SEARCHWIDGET_PARAM));
            
            final ContentPage page = new ContentPage((BigDecimal) key);
            
            link.setOnClick(String.format(
                    "window.opener.document.%s.value=\"%s\";window.opener.document.%s.value=\"%s\";self.close();return false;",
                                          widget,
                                          key.toString(),
                                          searchWidget,
                                          page.getTitle()));
                        
            return link;
        }
        
        
        
    }
}
