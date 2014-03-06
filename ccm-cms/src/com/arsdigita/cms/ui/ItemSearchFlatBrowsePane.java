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
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ItemSearchFlatBrowsePane extends SimpleContainer {

    private static final String QUERY_PARAM = "queryStr";
    public static final String WIDGET_PARAM = "widget";
    public static final String SEARCHWIDGET_PARAM = "searchWidget";
    public static final String FILTER_SUBMIT = "filterSubmit";
    private final Table resultsTable;
    private final Paginator paginator;
    private final StringParameter queryParam;
    private final QueryFieldsRequestLocal queryFields = new QueryFieldsRequestLocal();
    //private final List<String> queryFields = new ArrayList<String>();
    //private final Submit submit;
    private final static CMSConfig CMS_CONFIG = CMSConfig.getInstanceOf();

    public ItemSearchFlatBrowsePane() {
        //super(name);
        super();

        setIdAttr("itemSearchFlatBrowse");

        //final BoxPanel mainPanel = new BoxPanel(BoxPanel.VERTICAL);
        final LayoutPanel mainPanel = new LayoutPanel();

        queryParam = new StringParameter(QUERY_PARAM);

//        final BoxPanel boxPanel = new BoxPanel(BoxPanel.HORIZONTAL);
//        boxPanel.add(new Label(GlobalizationUtil.globalize("cms.ui.item_search.flat.filter")));
//        final TextField filter = new TextField(new StringParameter(QUERY_PARAM));
//        boxPanel.add(filter);
//        submit = new Submit(FILTER_SUBMIT,
//                            GlobalizationUtil.globalize("cms.ui.item_search.flat.filter.submit"));
//        boxPanel.add(submit);
//        mainPanel.add(boxPanel);
        
        //mainPanel.add(new FilterForm());
        mainPanel.setLeft(new FilterForm());

        resultsTable = new ResultsTable();
        paginator = new Paginator((PaginationModelBuilder) resultsTable.getModelBuilder(),
                                  CMS_CONFIG.getItemSearchFlatBrowsePanePageSize());
        //mainPanel.add(paginator);
        final BoxPanel body = new BoxPanel(BoxPanel.VERTICAL);
        body.add(paginator);

        //mainPanel.add(resultsTable);
        body.add(resultsTable);

        mainPanel.setBody(body);
        add(mainPanel);

//        addInitListener(this);
//        addProcessListener(this);
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addComponentStateParam(this, queryParam);
    }

//    public void init(final FormSectionEvent fse) throws FormProcessException {
//        final PageState state = fse.getPageState();
//        final FormData data = fse.getFormData();
//
//        final String query = (String) data.get(QUERY_PARAM);
//        if ((query == null) || query.isEmpty()) {
//            data.setParameter(QUERY_PARAM,
//                              new ParameterData(queryParam, state.getValue(new StringParameter(
//                    ItemSearchPopup.QUERY))));
//            state.setValue(queryParam, data.getParameter(QUERY_PARAM).getValue());
//        }
//    }

//    public void process(final FormSectionEvent fse) throws FormProcessException {
//        final FormData data = fse.getFormData();
//        final PageState state = fse.getPageState();
//
//        state.setValue(queryParam, data.get(QUERY_PARAM));
//        state.setValue(new StringParameter(ItemSearchPopup.QUERY), data.get(QUERY_PARAM));
//    }

    public void addQueryField(final String queryField) {
        queryFields.addQueryField(queryField);
    }
    
    void resetQueryFields() {
        queryFields.reset();
    }

    private class ResultsTable extends Table {

        private static final String TABLE_COL_TITLE = "title";
        private static final String TABLE_COL_PLACE = "place";
        private static final String TABLE_COL_TYPE = "type";

        public ResultsTable() {
            super();
            setEmptyView(new Label(GlobalizationUtil.globalize("cms.ui.item_search.flat.no_items")));
            setClassAttr("dataTable");

            final TableColumnModel columnModel = getColumnModel();
            columnModel.add(new TableColumn(0,
                                            GlobalizationUtil.globalize(
                    "cms.ui.item_search.flat.title").localize(),
                                            TABLE_COL_TITLE));
            columnModel.add(new TableColumn(1,
                                            GlobalizationUtil.globalize(
                    "cms.ui.item_search.flat.place").localize(),
                                            TABLE_COL_PLACE));
            columnModel.add(new TableColumn(2,
                                            GlobalizationUtil.globalize(
                    "cms.ui.item_search.flat.type").localize(),
                                            TABLE_COL_TYPE));

            setModelBuilder(new ResultsTableModelBuilder());

            columnModel.get(0).setCellRenderer(new TitleCellRenderer());
        }

    }

    private class ResultsTableModelBuilder extends LockableImpl implements TableModelBuilder,
                                                                           PaginationModelBuilder {

        //private DataCollection collection;
        private RequestLocal collection = new RequestLocal();

        public TableModel makeModel(final Table table, final PageState state) {

            if (collection.get(state) == null) {
                query(state);
            }

            ((DataCollection) collection.get(state)).setRange(paginator.getFirst(state), paginator.
                    getLast(state) + 1);

            return new ResultsTableModel(table, state, (DataCollection) collection.get(state));
        }

        public int getTotalSize(final Paginator paginator, final PageState state) {
            if (collection.get(state) == null) {
                query(state);
            }

            //((DataCollection)collection.get(state)).setRange(paginator.getFirst(state), paginator.getLast(state) + 1);

            return (int) ((DataCollection) collection.get(state)).size();
        }

        public boolean isVisible(PageState state) {
            return true;
        }

        private void query(final PageState state) {
            final Session session = SessionManager.getSession();
            final BigDecimal typeId = (BigDecimal) state.getValue(new BigDecimalParameter(
                    ItemSearch.SINGLE_TYPE_PARAM));
            if (typeId == null) {
                collection.set(state, session.retrieve(ContentPage.BASE_DATA_OBJECT_TYPE));
            } else {
                final ContentType type = new ContentType(typeId);
                collection.set(state, session.retrieve(type.getClassName()));
            }
            ((DataCollection) collection.get(state)).addFilter("version = 'draft'");
            ((DataCollection) collection.get(state)).addFilter("section is not null");

            final String query = (String) state.getValue(queryParam);
            if ((query != null) && !query.isEmpty()) {
                final StringBuffer buffer = new StringBuffer(String.format(
                        "((lower(%s) like lower('%%%s%%')) or (lower(%s) like lower('%%%s%%'))",
                        ContentItem.NAME, query,
                        ContentPage.TITLE, query));
                for (String field : queryFields.getQueryFields()) {
                    buffer.append(String.
                            format(" or (lower(%s) like lower('%%%s%%'))", field, query));
                }
                buffer.append(')');

                ((DataCollection) collection.get(state)).addFilter(buffer.toString());

//                ((DataCollection) collection.get(state)).addFilter(String.format(
//                        "((lower(%s) like lower('%%%s%%')) or (lower(%s) like lower('%%%s%%')))",
//                        ContentItem.NAME, query,
//                        ContentPage.TITLE, query));
            }

            ((DataCollection) collection.get(state)).addOrder("title asc, name asc");
        }

    }

    private class ResultsTableModel implements TableModel {

        private final Table table;
        private final DataCollection collection;
        private ContentItem currentItem;

        public ResultsTableModel(final Table table, final PageState state,
                                 final DataCollection collection) {
            this.table = table;

            this.collection = collection;

        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((collection != null) && collection.next()) {
                currentItem = (ContentItem) DomainObjectFactory.newInstance(collection.
                        getDataObject());
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

            if (value == null) {
                return new Label("???");
            }

            final Link link = new Link(value.toString(), "");

            final String widget = (String) state.getValue(new StringParameter(WIDGET_PARAM));
            final String searchWidget = (String) state.getValue(new StringParameter(
                    SEARCHWIDGET_PARAM));

            final ContentPage page = new ContentPage((BigDecimal) key);

            link.setOnClick(String.format(
                    "window.opener.document.%s.value=\"%s\";"
                    + "window.opener.document.%s.value=\"%s\";"
                    + "self.close();"
                    + "return false;",
                    widget,
                    key.toString(),
                    searchWidget,
                    page.getTitle().replace("\"", "\\\"")));

            return link;
        }

    }

//    protected Submit getSubmit() {
//        return submit;
//    }

    private class FilterForm extends Form implements FormInitListener, FormProcessListener {

        private final Submit submit;
        
        public FilterForm() {
            super("ItemSearchFlatBrowsePane");
            
            add(new Label(GlobalizationUtil.globalize("cms.ui.item_search.flat.filter")));
            final TextField filter = new TextField(new StringParameter(QUERY_PARAM));
            add(filter);

            submit = new Submit(FILTER_SUBMIT,
                                GlobalizationUtil.globalize("cms.ui.item_search.flat.filter.submit"));
            add(submit);
            
            addInitListener(this);
            addProcessListener(this);
        }

        public void init(final FormSectionEvent fse) throws FormProcessException {
            final PageState state = fse.getPageState();
            final FormData data = fse.getFormData();

            final String query = (String) data.get(QUERY_PARAM);
            if ((query == null) || query.isEmpty()) {
                data.setParameter(QUERY_PARAM,
                                  new ParameterData(queryParam, state.getValue(new StringParameter(
                        ItemSearchPopup.QUERY))));
                state.setValue(queryParam, data.getParameter(QUERY_PARAM).getValue());
            }
        }

        public void process(final FormSectionEvent fse) throws FormProcessException {
            final FormData data = fse.getFormData();
            final PageState state = fse.getPageState();

            state.setValue(queryParam, data.get(QUERY_PARAM));
            state.setValue(new StringParameter(ItemSearchPopup.QUERY), data.get(QUERY_PARAM));
        }

    }
    
    private class QueryFieldsRequestLocal extends RequestLocal {
        
        private List<String> queryFields = new ArrayList<String>();
        
        @Override
        protected Object initialValue(final PageState state) {
            return new ArrayList<String>();
        }
        
        public List<String> getQueryFields() {
            return queryFields;
        }
        
        public void setQueryFields(final List<String> queryFields) {
            this.queryFields = queryFields;
        }
        
        public void addQueryField(final String queryField) {
            queryFields.add(queryField);
        }
        
        public void reset() {
            queryFields = new ArrayList<String>();
        }
    }
}
