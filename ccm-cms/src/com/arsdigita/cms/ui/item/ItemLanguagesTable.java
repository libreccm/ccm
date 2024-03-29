/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.item;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.*;
import com.arsdigita.cms.dispatcher.MultilingualItemResolver;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.util.LanguageUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.toolbox.ui.DataQueryBuilder;
import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Displays a list of all language instances of an item. 
 *
 */
public class ItemLanguagesTable extends DataTable {

    private static final Logger s_log = Logger.getLogger(ItemLanguagesTable.class);
    private ItemSelectionModel m_model;
    private TableColumn m_deleteColumn;

    /**
     * Construct a new
     * <code>ItemHistoryTable</code>
     *
     * @param model the ItemSelectionModel that supplies the current item
     */
    public ItemLanguagesTable(ItemSelectionModel model) {
        super(new LanguagesBuilder(model));
        m_model = model;

        addColumn("cms.ui.language.header", 
                  ContentPage.LANGUAGE, 
                  false,
                  new LanguageCellRenderer(m_model));
        addColumn("cms.ui.title", 
                  ContentPage.TITLE);
        m_deleteColumn = addColumn("cms.ui.action", new ActionCellRenderer(
                m_model));
        setResourceBundle(GlobalizationUtil.getBundleName());
        addTableActionListener(new InstanceDeleter(m_model));
    }

    /**
     * Builds the query for all the language instances in the current Bundle
     */
    private static class LanguagesBuilder extends LockableImpl
            implements DataQueryBuilder {

        ItemSelectionModel m_model;

        public LanguagesBuilder(ItemSelectionModel model) {
            super();
            m_model = model;
        }

        public DataQuery makeDataQuery(DataTable t, PageState s) {
            ContentPage multiLingual =
                    (ContentPage) m_model.getSelectedObject(s);
            DataQuery q = SessionManager.getSession().retrieveQuery(
                    "com.arsdigita.cms.getBundledItems");
            q.setParameter("bundleID", multiLingual.getContentBundle().getID());
            return q;
        }

        public String getKeyColumn() {
            return ContentPage.ID;
        }
    }

    /**
     * Renders the full language name.
     */
    private static class LanguageCellRenderer implements TableCellRenderer {

        private ItemSelectionModel m_model;

        public LanguageCellRenderer(ItemSelectionModel model) {
            m_model = model;
        }

        public Component getComponent(Table table, PageState state, Object value,
                boolean isSelected, Object key,
                int row, int column) {

            BigDecimal id = (BigDecimal) key;
            ContentPage cp;

            try {
                cp = new ContentPage(id);
            } catch (DataObjectNotFoundException ex) {
                // Content item was not found, return nothing
                return new Label();
            }

            ContentBundle bundle = cp.getContentBundle();

            if (bundle != null
                    && !(cp instanceof LanguageInvariantContentItem
                    && ((LanguageInvariantContentItem) cp).isLanguageInvariant())) {

                StringBuilder fontWeight = new StringBuilder(2);
                StringBuilder classes = new StringBuilder(20);

                if (cp.isLive()) {
                    fontWeight.append(Label.BOLD);
                    classes.append("live ");
                }
                if (bundle.getPrimaryInstance().equals(cp)) {
                    fontWeight.append(Label.ITALIC);
                    classes.append("primaryInstance");
                }

                String target = ContentItemPage.getItemURL(cp, ContentItemPage.AUTHORING_TAB);
                Label langLabel = new Label(LanguageUtil.getLangFull((String) value));

                langLabel.setFontWeight(fontWeight.toString().trim());
                langLabel.setClassAttr(classes.toString().trim());

                if (m_model.getSelectedKey(state).equals(key)) {
                    // Current instance: no link
                    return langLabel;
                } else {
                    return new Link(langLabel, target);
                }
            }

            return new Label();
        }
    }

    /**
     * Delete language instance action link.
     */
    private static class ActionCellRenderer implements TableCellRenderer {

        private static final Logger logger =
                Logger.getLogger(ActionCellRenderer.class);
        private static final Label s_noAction;
        private static final Label s_primary;
        private static final ControlLink s_link;

        static {
            logger.debug("Static initializer is starting...");
            s_noAction = new Label("&nbsp;", false);
            s_noAction.lock();
            s_primary = new Label(GlobalizationUtil.globalize(
                    "cms.ui.primary_instance"), false);
            s_primary.lock();
            s_link = new ControlLink(new Label(GlobalizationUtil.globalize(
                    "cms.ui.delete")));
            s_link.setConfirmation(GlobalizationUtil.globalize(
                    "cms.ui.delete_confirmation"));
            logger.debug("Static initalizer finished.");
        }
        private ItemSelectionModel m_model;

        public ActionCellRenderer(ItemSelectionModel model) {
            m_model = model;
        }

        public Component getComponent(Table table, PageState state, Object value,
                boolean isSelected, Object key,
                int row, int column) {
            // check if primary instance
            BigDecimal id = new BigDecimal(key.toString());
            OID oid = new OID(ContentPage.BASE_DATA_OBJECT_TYPE, id);
            try {
                ContentPage item = (ContentPage) DomainObjectFactory.newInstance(oid);
                if (item.getLanguage().equals(
                        item.getContentBundle().getDefaultLanguage())) {
                    return s_primary;
                } else if (item.isLive()) {
                    return s_noAction;
                }
            } catch (DataObjectNotFoundException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not get item with id " + id);
                }
                return s_noAction;
            }
            return s_link;
        }
    }

    // delete one language instance
    private class InstanceDeleter extends TableActionAdapter {

        private ItemSelectionModel m_model;

        public InstanceDeleter(ItemSelectionModel model) {
            m_model = model;
        }

        @Override
        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();

            if (m_deleteColumn != getColumn(col)) {
                return;
            }

            PageState s = e.getPageState();
            BigDecimal id = new BigDecimal(e.getRowKey().toString());

            OID oid = new OID(ContentPage.BASE_DATA_OBJECT_TYPE, id);
            try {
                ContentPage item =
                        (ContentPage) DomainObjectFactory.newInstance(oid);
                ContentBundle bundle = item.getContentBundle();
                bundle.removeInstance(item);
                item.delete();

                if (m_model.getSelectedKey(s).equals(id)) {
                    throw new RedirectSignal(
                        URL.there((new MultilingualItemResolver()
                                       .generateItemURL(s, 
                                                        bundle.getPrimaryInstance(), 
                                                        bundle.getContentSection(), 
                                                        ContentItem.DRAFT)), 
                            null), 
                        true);
                }
            } catch (com.arsdigita.domain.DataObjectNotFoundException ex) {
                // Object not found is ok, it has probably been deleted already
            }
            ((Table) e.getSource()).clearSelection(s);
        }
    }
}
