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
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateCollection;
import com.arsdigita.cms.TemplateMapping;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.toolbox.ui.DataQueryBuilder;
import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;

import org.apache.log4j.Logger;

/**
 * List all templates for a content section, along with links to
 * make templates default and remove template mappings. Child classes
 * need to override the {@link #getTemplateCollection(PageState)} method
 * in order to fill this class with data. In addition, child classes need to
 * override the {@link removeTemplate(PageState, Template)} method in order to
 * support the "Remove" link.
 * <p>
 * The table looks somewhat like this:
 * <blockquote><pre><code>
 * +----------+-------+---------+
 * | Name     | Label | Context |
 * +----------+-------+---------+
 * | bar      | Bar   | public  |
 * | foo      | Foo   | summary |
 * | ...      |       |         |
 * +----------+-------+---------+
 * </code></pre></blockquote>
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: TemplatesListing.java 287 2005-02-22 00:29:02Z sskracic $
 */
abstract class TemplatesListing extends DataTable {

    private TableColumn m_removeCol;
    private ACSObjectSelectionModel m_mappingModel;

    private static final String MAPPING_ID = "mid";

    private static final Logger s_log =
        Logger.getLogger(TemplatesListing.class);

    /**
     * Construct a new <code>TemplatesListing</code>
     *
     * @param mappingModel the <code>ACSObjectSelectionModel</code> that will select
     *   the current mapping
     */
    public TemplatesListing(ACSObjectSelectionModel mappingModel) {
        super(new AbstractQueryBuilder());

        addColumn("Context", TemplateCollection.USE_CONTEXT, false);
        addColumn("Name", TemplateCollection.TEMPLATE, false,
                  new TemplateNameCellRenderer());
        addColumn("Label", TemplateCollection.TEMPLATE + "." + Template.LABEL,
                  false);
        addColumn("Mime Type", TemplateCollection.TEMPLATE, false,
                  new MimeTypeCellRenderer());

        m_mappingModel = mappingModel;
        setRowSelectionModel(m_mappingModel);

        setEmptyView(new Label(GlobalizationUtil.globalize("cms.ui.templates.no_templates")));
    }

    /**
     * Construct a new <code>TemplatesListing</code>
     */
    public TemplatesListing() {
        this(new ACSObjectSelectionModel(MAPPING_ID));
    }

    /**
     * Child classes should override this method in order to populate the
     * listing with data
     *
     * @param state the current page state
     * @return the collection of all templates in the listing
     * @post return != null
     */
    protected abstract TemplateCollection getTemplateCollection(PageState s);

    /**
     * Child classes should override this method in order to handle the
     * "Remove" link
     *
     * @param mapping the template mapping to be removed
     * @param s the current page state
     * @see #addRemoveColumn
     */
    protected void removeTemplate(PageState s, TemplateMapping mapping) {
        throw new UnsupportedOperationException( (String) GlobalizationUtil.globalize("cms.ui.templates.not_implemented").localize());
    }

    /**
     * Return the <code>ACSObjectSelectionModel</code> used to select the
     * current mapping
     */
    protected final ACSObjectSelectionModel getMappingModel() {
        return m_mappingModel;
    }

    /**
     * Return the <code>TableColumn</code> which will contain the
     * "remove" link.
     *
     * @return the column that contains the "remove" link, or null
     *   if {@link #addRemoveColumn} has not been called yet
     * @see #addRemoveColumn
     */
    public final TableColumn getRemoveColumn() {
        return m_removeCol;
    }

    /**
     * Append a "remove" column to this table. Child classes can call
     * this method at construction time to add the "remove" column.
     * The column is not added by default since this component can
     * potentially be used for purely informational purposes.
     * <p>
     * The "remove" column will contain the current template as the value.
     */
    protected final void addRemoveColumn() {
        Assert.isUnlocked(this);
        Assert.isTrue(m_removeCol == null,
                          "The \"remove\" column already exists");

        m_removeCol = addColumn("Remove", TemplateCollection.TEMPLATE, false,
                                new RemoveCellRenderer());

        // Add a listener that will actually remove the template
        addTableActionListener(new TableActionAdapter() {
                public void cellSelected(TableActionEvent e) {
                    PageState s = e.getPageState();
                    TemplatesListing l = (TemplatesListing)e.getSource();
                    int i = e.getColumn().intValue();
                    TableColumn c = l.getColumnModel().get(i);

                    if(m_removeCol.equals(c)) {
                        removeTemplate(s, (TemplateMapping)m_mappingModel.getSelectedObject(s));
                    }
                }
            });
    }

    /**
     * Builds up a DataQuery from the collection
     */
    protected static class AbstractQueryBuilder extends LockableImpl
        implements DataQueryBuilder {

        public AbstractQueryBuilder() {
            super();
        }

        public DataQuery makeDataQuery(DataTable t, PageState s) {
            TemplatesListing l = (TemplatesListing)t;
            return l.getTemplateCollection(s).getDataCollection();
        }

        public String getKeyColumn() {
            return ACSObject.ID;
        }
    }

    /**
     * Renders the "remove" link
     */
    private static class RemoveCellRenderer implements TableCellRenderer {

        // Static instantiation is safe since these components
        // will never ever change
        private ControlLink m_link;
        private Label m_label;

        public RemoveCellRenderer() {
            m_link = new ControlLink( (String) GlobalizationUtil.globalize("cms.ui.templates.remove").localize());
            m_label = new Label("&nbsp;", false);
            m_link.setConfirmation("Are you sure you want to " +
                                   "remove this template ?");
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            if (value == null) {
                return m_label;
            } else {
                return m_link;
            }
        }
    }

    /**
     * Renders the name of the template as a link
     */
    protected static class TemplateNameCellRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            if(value == null)
                return new Label("&nbsp;", false);

            Template item =
                (Template)DomainObjectFactory.newInstance((DataObject)value);

            ContentSection sec = CMS.getContext().getContentSection();

            ItemResolver res = sec.getItemResolver();
            String url = res.generateItemURL(state, item, sec, ContentItem.DRAFT);
            return new Link(item.getName(), url);
        }
    }


    /**
     * Renders the name of the template as a link
     */
    protected class MimeTypeCellRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, 
                                      Object value, boolean isSelected, 
                                      Object key, int row, int column) {

            // The Template value is passed in
            MimeType mimeType = null;
            if (value != null) {
                Template item = (Template)DomainObjectFactory
                    .newInstance((DataObject)value);
                mimeType = item.getMimeType();
            } else {
                try {
                    DataObject object = (DataObject)getDataQuery(state)
                        .get(Template.MIME_TYPE);
                    if (object != null) {
                        mimeType = (MimeType)DomainObjectFactory
                            .newInstance(object);
                    }
                } catch (DataObjectNotFoundException donfe) {
                    // we did not have a mimeType attribute in the data query
                    // which is not a problem since we are just trying to
                    // display the cell and we will just leave it blank.
                    s_log.debug("No Mime Type for cell with value of " + 
                                value + " and key of " + key, donfe);
                }
            }

            if (mimeType != null) {
                GlobalizedMessage mimeTypeMessage = 
                    (GlobalizedMessage)Template.SUPPORTED_MIME_TYPES.get
                    (mimeType.getMimeType());
                if (mimeTypeMessage != null) {
                    return new Label(mimeTypeMessage, false);
                } else {
                    return new Label(mimeType.getLabel(), false);
                }
            } else {
                return new Label("&nbsp;", false);
            }
        }
    }
}
