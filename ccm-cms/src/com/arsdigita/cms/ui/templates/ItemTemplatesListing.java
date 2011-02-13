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
package com.arsdigita.cms.ui.templates;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ItemTemplateMapping;
import com.arsdigita.cms.TemplateCollection;
import com.arsdigita.cms.TemplateManagerFactory;
import com.arsdigita.cms.TemplateMapping;
import com.arsdigita.cms.Template;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.util.Assert;
import com.arsdigita.kernel.ACSObjectCache;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.bebop.table.TableModel;
import java.math.BigDecimal;
import org.apache.log4j.Logger;


/**
 * Displays all templates assigned to a content item.
 * Allows the ability to unassign templates. Provides a link which
 * should lead to some UI that will assign a new template to an item in
 * the specified use context; it is up to the container of this class
 * to actually implement this UI.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemTemplatesListing.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class ItemTemplatesListing extends TemplatesListing {

    private ItemSelectionModel m_itemModel;
    private TableColumn m_assignCol;
    private TableColumn m_previewCol;

    private RequestLocal m_currentContext;

    public final static String FULL_KEY = "fullUniqueKey";
    public final static String SEP_CHAR = ";";

    /**
     * Construct a new <code>SectionTemplatesListing</code>
     *
     * @param itemModel The {@link ItemSelectionModel} that will supply the
     *   current content item to this component
     */
    public ItemTemplatesListing(ItemSelectionModel itemModel) {
        super(new MappingSelectionModel(itemModel));
        m_itemModel = itemModel;

        // Change the key column to "use_context"
        setDataQueryBuilder(new AbstractQueryBuilder() {
                public String getKeyColumn() {
                    return FULL_KEY;
                }
            });

        setModelBuilder(new ItemTemplatesListingModelBuilder());

        // Add the "remove template" column
        addRemoveColumn();
        m_assignCol = addColumn("Assign", new AssignCellRenderer());
        m_previewCol = addColumn("Preview", TemplateCollection.USE_CONTEXT, false,
                                 new PreviewCellRenderer(itemModel));

        // A RequestLocal that will get the current context

        // Switch to the template assignment UI when the "assign" link
        // is clicked
        addTableActionListener(new TableActionAdapter() {
                public void cellSelected(TableActionEvent e) {
                    PageState s = e.getPageState();
                    TemplatesListing l = (TemplatesListing)e.getSource();
                    int i = e.getColumn().intValue();
                    TableColumn c = l.getColumnModel().get(i);

                    // Safe to check pointer equality since the column is
                    // created statically
                    if(c == m_assignCol) {
                        String useContext = (String)getMappingModel().getSelectedKey(s);
                        ContentItem item = m_itemModel.getSelectedItem(s);
                        assignLinkClicked(s, item, useContext);
                    }
                }
            });
    }

    /**
     * Retrieve the "assign" column
     */
    public final TableColumn getAssignColumn() {
        return m_assignCol;
    }

    /**
     * Retrieve the "previvew" column
     */
    public final TableColumn getPreviewColumn() {
        return m_previewCol;
    }

    /**
     * Get the templates for the current content section and type
     */
    protected TemplateCollection getTemplateCollection(PageState s) {
        ContentItem item = m_itemModel.getSelectedItem(s);
        return TemplateManagerFactory.
            getInstance().getContextsWithTypes(item);
    }

    /**
     * Remove the current template
     */
    protected void removeTemplate(PageState s, TemplateMapping m) {
        // Could be null if the "remove" link was somehow clicked
        // on an empty row
        if(m == null) return;

        // Yes, this code will just call ItemTemplateMapping.delete,
        // but this seems safer
        MimeType mimeType = getMimeTypeFromKey((String)getMappingModel().getSelectedKey(s));
        TemplateManagerFactory.getInstance().removeTemplate 
            ((ContentItem)m.getParent(), m.getTemplate(), m.getUseContext());
    }

    /**
     * Render the "assign" link/label
     */
    protected static class AssignCellRenderer implements TableCellRenderer {

        // Static is safe since these components will never ever change
        private static ControlLink s_link;
        private static final Logger logger = Logger.getLogger(AssignCellRenderer.class);

        static {
            logger.debug("Static initializer is starting...");
            s_link = new ControlLink(new Label(GlobalizationUtil.globalize("cms.ui.templates.assign_template")));
            s_link.setClassAttr("assignTemplateLink");
            logger.debug("Static initializer finished.");
        }

        public AssignCellRenderer() {}

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            return s_link;
        }
    }

    /**
     * Redirect to some template assignment UI which will assign a new template
     * to the current item
     *
     * @param s the current page state
     * @param item the current item
     * @param context the current use context
     */
    protected abstract void assignLinkClicked (
                                               PageState s, ContentItem item, String useContext
                                               );

    // Selects the template mapping by a combination of item_id and use_context
    private static class MappingSelectionModel extends ACSObjectSelectionModel {

        private ItemSelectionModel m_itemSel;

        public static final String MAPPING_KEY = "c";

        public MappingSelectionModel(ItemSelectionModel itemSel) {
            super(new ParameterSingleSelectionModel(new StringParameter(MAPPING_KEY)));
            m_itemSel = itemSel;
        }


        // we have to override this because super.getSelectedObject
        // assumes that the key is a BigDecimal which is not true.
        public DomainObject getSelectedObject(PageState state) {
            
            Object key = getSelectedKey(state);
            BigDecimal id = null;
            if (key == null) {
                return null;
            }
            
            if (! isInitialized(state)) {
                // Attempt to load the object
                
                if (!isSelected(state)) {
                    return null;
                }

                ACSObject item = loadACSObject(state, key);
                if (item == null) {
                    return null;
                }
                // we have to add the next two lines to make sure that
                // the internal state of ACSObjectSelectionModel is correct
                setSelectedObject(state, item);
                setSelectedKey(state, key);

                ACSObjectCache.set(state.getRequest(), item);
                id = item.getID();
                return item;
                
            }
            return ACSObjectCache.get(state.getRequest(), id);
        }

        public ACSObject loadACSObject(PageState state, Object key) {
            String context = getUseContextFromKey((String)key);
            MimeType mimeType = getMimeTypeFromKey((String)key);
            ContentItem item = m_itemSel.getSelectedItem(state);
            return ItemTemplateMapping.getMapping(item, context, mimeType);
        }
    }

    /**
     * Render the "preview" link/label
     */
    protected class PreviewCellRenderer implements TableCellRenderer {

        private ItemSelectionModel m_itemSel;
        public PreviewCellRenderer(ItemSelectionModel itemSel) {
            m_itemSel = itemSel;
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            if(value == null)
                return new Label("&nbsp;", false);

            // if we have a template, use that.  Otherwise, use the mime type
            DataObject templateObject = 
                (DataObject)getDataQuery(state).get(TemplateCollection.TEMPLATE);
            MimeType mimeType = null;
            if (templateObject != null) {
                Template item =
                    (Template)DomainObjectFactory.newInstance(templateObject);
                mimeType = item.getMimeType();
            }

            if (mimeType == null) {
                DataObject object = 
                    (DataObject)getDataQuery(state).get(Template.MIME_TYPE);
                if (object != null) {
                    mimeType = (MimeType)DomainObjectFactory
                        .newInstance(object);
                }
            }

            if (mimeType != null && 
                !Template.JSP_MIME_TYPE.equals(mimeType.getMimeType())) {
                // we won't want to show the preview here because it
                // is not the jsp template.
                return new Label("&nbsp;", false);
            }
            
            String context = (String) value;

            ContentSection sec = CMS.getContext().getContentSection();
            ContentItem item = m_itemSel.getSelectedItem(state);
            Assert.exists(item, "item");

            ItemResolver res = sec.getItemResolver();
            String url = res.generateItemURL(state, item, sec,
                                             CMSDispatcher.PREVIEW, context);
            Link link = new Link(new Label(GlobalizationUtil.globalize("cms.ui.templates.preview").localize() + " " + context),  url);
            link.setIdAttr("previewInTable");

            return link;
        }
    }

    /**
     *  This takes in a key that is used to identify a row in the
     *  table and will return the encoded mime type or null if it
     *  is not specified
     */
    public static MimeType getMimeTypeFromKey(String key) {
        if (key == null) {
            return null;
        }
        int index = key.lastIndexOf(SEP_CHAR);
        String mimeString = null;
        if (index < 0) {
            mimeString = key;
        } else if (key.length() == (index + 1)){
            return null;
        } else {
            mimeString = key.substring(index + 1);
        }
        return MimeType.loadMimeType(mimeString);
    }

    /**
     *  This takes in a key that is used to identify a row in the
     *  table and will return the encoded object id or null if it
     *  is not specified
     */
    public static String getObjectIDFromKey(String key) {
        if (key == null) {
            return null;
        }
        int beginIndex = key.indexOf(SEP_CHAR);
        int endIndex = key.lastIndexOf(SEP_CHAR);
        if (beginIndex < 0) {
            return key;
        } else if (endIndex == beginIndex) {
            return null;
        } else {
            return key.substring(beginIndex, endIndex - beginIndex);
        }
    }

    /**
     *  This takes in a key that is used to identify a row in the
     *  table and will return the encoded useContext or null if it
     *  is not specified
     */
    public static String getUseContextFromKey(String key) {
        if (key == null) {
            return null;
        }
        int index = key.indexOf(SEP_CHAR);
        if (index < 0) {
            return key;
        } else {
            return key.substring(0, index);
        }
    }

    /**
     *  this builds the key that can be decoded using the getUseContextFromKey
     *  getObjectIDFromKey, and getMimeTypeFromKey methods
     */
    protected static String buildKey(String useContext, String id, 
                                     MimeType mimeType) {
        if (id == null) {
            id = "";
        }
        String mimeString = "";
        if (mimeType != null) {
            mimeString = mimeType.getMimeType();
        }
        return useContext + SEP_CHAR + id + SEP_CHAR + mimeString;
    }


    /**
     *  This class allows us to use a compound key of context plus
     *  mimeType instead of only context
     */
    protected static class ItemTemplatesListingModelBuilder extends DataBuilderAdapter {
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

            if(d == null) {
                return super.makeModel(table, s);
            }

            return new ItemTemplatesListingTableModel(t, d,
                                                      t.getDataQueryBuilder()
                                                      .getKeyColumn());
        }
    }

    protected static class ItemTemplatesListingTableModel extends DataQueryTableModel {
        private DataQuery m_data;
        private DataTableColumnModel m_cols;
        private String m_keyColumn;
        public ItemTemplatesListingTableModel(DataTable t, DataQuery data, String keyColumn) {
            super(t, data, keyColumn);
            m_data = data;
            m_cols = (DataTableColumnModel)t.getColumnModel();
            m_keyColumn = keyColumn;
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
            if (FULL_KEY.equals(key) || 
                (key == null && FULL_KEY.equals(m_keyColumn))) {
                return getIDString();
            } else if (key != null) {
                return m_data.get(key);
            } else {
                return super.getKeyAt(columnIndex);
            }
        }

        /**
         *  This returns the multiple valued key that we need.
         *  Specifically, we need to have the context and the mime type...
         *  the context alone is not unique.
         */ 
        private String getIDString() {
            // this means they want the unique key
            String useContext = 
                (String)m_data.get(TemplateCollection.USE_CONTEXT);
            String id = null;
            DataObject templateObject = 
                (DataObject)m_data.get(TemplateCollection.TEMPLATE);
            MimeType mimeType = null;
            if (templateObject != null) {
                Template item =
                    (Template)DomainObjectFactory.newInstance(templateObject);
                mimeType = item.getMimeType();
                id = item.getID().toString();
            }
            
            if (mimeType == null) {
                DataObject object = 
                    (DataObject)m_data.get(Template.MIME_TYPE);
                if (object != null) {
                    mimeType = (MimeType)DomainObjectFactory
                        .newInstance(object);
                }
            }
            
            return buildKey(useContext, id, mimeType);
        }
    }
}
