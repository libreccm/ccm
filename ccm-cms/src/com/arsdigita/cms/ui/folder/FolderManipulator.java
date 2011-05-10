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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.tree.TreeCellRenderer;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Browse folders and manipulate them with various actions
 * (move/copy/delete).
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @version $Id: FolderManipulator.java 1940 2009-05-29 07:15:05Z terry $
 */
public class FolderManipulator extends SimpleContainer
        implements FormProcessListener, FormValidationListener,
                   FormSubmissionListener, Resettable {

    private static Logger s_log =
                          Logger.getLogger(FolderManipulator.class);
    public static final String RESOURCE_BUNDLE =
                               "com.arsdigita.cms.ui.folder.CMSFolderResources";
    private static final String MOVE = "Move";
    private static final String COPY = "Copy";
    private static final BigDecimal[] EMPTY_ARRAY = new BigDecimal[0];
    private ArrayParameter m_sources;
    private StringParameter m_action;
    // The folder in which the source items live
    private FolderSelectionModel m_srcFolderSel;
    private ItemView m_itemView;
    private TargetSelector m_targetSelector;
    private FilterForm m_filterForm;
    private StringParameter m_filter = new StringParameter("filter");

    public FolderManipulator(FolderSelectionModel folderSel) {
        m_sources = new ArrayParameter(new BigDecimalParameter("srcs"));
        m_action = new StringParameter("act");
        m_srcFolderSel = folderSel;

        //s_log.debug("Adding filter form...");
        //m_filterForm = new FilterForm();
        //add(m_filterForm);

        m_itemView = new ItemView();
        m_itemView.addProcessListener(this);
        m_itemView.addValidationListener(this);
        add(m_itemView);

        m_targetSelector = new TargetSelector();
        add(m_targetSelector);
        m_targetSelector.addProcessListener(this);
        m_targetSelector.addValidationListener(this);
        m_targetSelector.addSubmissionListener(this);        
                
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.setVisibleDefault(m_targetSelector, false);
        p.setVisibleDefault(m_filterForm, true);
        p.addComponentStateParam(this, m_sources);
        p.addComponentStateParam(this, m_action);
        p.addComponentStateParam(this, m_filter);               
    }

    public final BigDecimal[] getSources(PageState s) {
        BigDecimal[] result = (BigDecimal[]) s.getValue(m_sources);
        return (result == null) ? EMPTY_ARRAY : result;
    }

    public final FolderSelectionModel getSrcFolderSel() {
        return m_srcFolderSel;
    }

    public final Folder getTarget(PageState s) {
        return m_targetSelector.getTarget(s);
    }

    protected final boolean isMove(PageState s) {
        return MOVE.equals(getAction(s));
    }

    protected final boolean isCopy(PageState s) {
        return COPY.equals(getAction(s));
    }

    private String getAction(PageState s) {
        return (String) s.getValue(m_action);
    }

    protected void moveItems(Folder target, BigDecimal[] items) {
        s_log.debug("moving to folder: " + target + " items: " + items);
        for (int i = 0; i < items.length; i++) {
            try {
                ContentItem item = new ContentItem(items[i]);
                item.setParent(target);
                item.save();
            } catch (DataObjectNotFoundException e) {
                s_log.warn("object not found in content move", e);
                throw new IllegalStateException((String) globalize(
                        "cms.ui.folder.no_such_item").localize());
            }
        }
    }

    protected void copyItems(final Folder target,
                             final BigDecimal[] items) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Copying items " + Arrays.asList(items) + " to "
                        + target);
        }


        for (int i = 0; i < items.length; i++) {
            ContentItem item =
                        (ContentItem) DomainObjectFactory.newInstance(new OID(
                    ContentItem.BASE_DATA_OBJECT_TYPE, items[i]));

            Assert.exists(item, ContentItem.class);

            final ACSObject parent = item.getParent();

            if (parent instanceof ContentBundle) {
                item = (ContentBundle) parent;
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("Copying item " + item);
            }

            final ContentItem newItem = item.copy(target, true);
            Assert.isEqual(target, newItem.getParent());
        }
    }

    /**
     * Returns the form that contains the folder browser and the move/copy
     * dropdown.
     */
    public final Form getItemView() {
        return m_itemView;
    }

    /**
     * Returns the form to choose the target folder for move/copy
     */
    public final Form getTargetSelector() {
        return m_targetSelector;
    }

    /**
     * Return the browser contained in the ItemView form
     */
    public final FolderBrowser getBrowser() {
        return m_itemView.getBrowser();
    }

    @Override
    public void process(FormSectionEvent e) {
        PageState s = e.getPageState();
        if (e.getSource() == m_itemView) {
            m_itemView.setVisible(s, false);
            m_targetSelector.setVisible(s, true);
            m_targetSelector.expose(s);
        } else if (e.getSource() == m_targetSelector) {
            m_itemView.setVisible(s, true);
            m_targetSelector.setVisible(s, false);
            Folder f = m_targetSelector.getTarget(s);
            BigDecimal[] items = getSources(s);
            if (isCopy(s)) {
                copyItems(f, items);
            } else if (isMove(s)) {
                moveItems(f, items);
            }
            reset(s);
        }
    }

    @Override
    public void validate(FormSectionEvent e) {
        ContentItem item;
        String name;
        ItemCollection items;
        PageState s = e.getPageState();
        FormData d = e.getFormData();
        BigDecimal[] sources = getSources(s);
        boolean hasSources = sources.length > 0;
        SecurityManager sm = Utilities.getSecurityManager(s);

        if (e.getSource() == m_itemView) {
            if (!hasSources) {
                d.addError(globalize("cms.ui.folder.must_select_item"));
            }
        } else if (e.getSource() == m_targetSelector) {
            // The source items must have gotten lost somehow
            if (!hasSources) {
                throw new IllegalStateException((String) GlobalizationUtil.
                        globalize("cms.ui.folder.no_source_items_specified").
                        localize());
            }
            Folder target = m_targetSelector.getTarget(s);
            if (target == null) {
                d.addError(globalize("cms.ui.folder.need_select_target_folder"));
            }
            if (target.equals(m_srcFolderSel.getSelectedObject(s))) {
                d.addError(globalize("cms.ui.folder.not_within_same_folder"));
            }
            // check create item permission
            User user = Web.getContext().getUser();
            if (!sm.canAccess(user, SecurityManager.NEW_ITEM, target)) {
                d.addError(globalize("cms.ui.folder.no_permission_for_item"));
            }
            // Per-item checks
            for (int i = 0; i < sources.length; i++) {
                try {
                    item = new ContentItem(sources[i]);
                    name = item.getName();
                    items = target.getItems();
                    items.addNameFilter(name);
                    if (items.next()) {
                        // there is an item in the target folder that already has this name
                        d.addError((String) globalize(
                                "cms.ui.folder.item_already_exists").localize()
                                   + name);
                    }
                    items.close();

                    if (item.isLive() && isMove(s)) {
                        d.addError(name + (String) globalize(
                                "cms.ui.folder.item_is_live").localize());
                    }
                    if ((!sm.canAccess(user, SecurityManager.DELETE_ITEM, item))
                        && isMove(
                            s)) {
                        d.addError((String) globalize(
                                "cms.ui.folder.no_permission_for_item").localize()
                                   + name + ".");
                    }
                } catch (DataObjectNotFoundException exc) {
                    s_log.warn("object not found in validation", exc);
                    throw new IllegalStateException((String) globalize(
                            "cms.ui.folder.no_such_item").localize());
                }
            }
        }
    }

    @Override
    public void submitted(FormSectionEvent e)
            throws FormProcessException {
        PageState s = e.getPageState();
        if (m_targetSelector.isCancelled(s)) {
            reset(s);
            throw new FormProcessException((String) GlobalizationUtil.globalize(
                    "cms.ui.folder.cancelled").localize());
        }
    }

    @Override
    public void reset(PageState s) {
        m_itemView.setVisible(s, true);
        m_itemView.reset(s);
        m_targetSelector.setVisible(s, false);
        m_targetSelector.reset(s);
        s.setValue(m_action, null);
        s.setValue(m_sources, null);      
        s.setValue(m_filter, null);
    }

    // The form containing the tree to select the target folder from
    private class TargetSelector extends Form
            implements Resettable {

        private FolderSelectionModel m_targetSel;
        private FolderTree m_folderTree;
        private Submit m_cancel;

        public TargetSelector() {
            super("targetSel", new BoxPanel());
            setMethod(GET);
            m_targetSel = new FolderSelectionModel("target");
            m_folderTree = new FolderTree(m_targetSel);
            m_folderTree.setCellRenderer(new FolderTreeCellRenderer());

            Label l = new Label(new PrintListener() {

                @Override
                public void prepare(PrintEvent e) {
                    PageState s = e.getPageState();
                    Label t = (Label) e.getTarget();
                    int n = getSources(s).length;
                    Folder f = (Folder) m_srcFolderSel.getSelectedObject(s);
                    String msg = n + "&nbsp;" + (String) globalize(
                            "cms.ui.folder.items").localize() + "&nbsp;"
                                 + (String) globalize(
                            "cms.ui.folder.from").localize() + "&nbsp;/" + f.
                            getPathNoJsp() + "&nbsp;" + (String) globalize(
                            "cms.ui.folder.to").localize();
                    if (isMove(s)) {
                        t.setLabel((String) globalize("cms.ui.folder.move").
                                localize() + "&nbsp;" + msg);
                    } else if (isCopy(s)) {
                        t.setLabel((String) globalize("cms.ui.folder.copy").
                                localize() + "&nbsp;" + msg);
                    }
                }
            });
            l.setOutputEscaping(false);
            add(l);
            add(m_folderTree);
            add(new FormErrorDisplay(this));
            SaveCancelSection scs = new SaveCancelSection();
            m_cancel = scs.getCancelButton();
            add(scs);
        }

        @Override
        public void register(Page p) {
            super.register(p);
            p.addComponentStateParam(this, m_targetSel.getStateParameter());
        }

        // Set things up the first time the selector gets visible
        public void expose(PageState s) {
            Folder f = (Folder) m_srcFolderSel.getSelectedObject(s);
            m_targetSel.clearSelection(s);
            if (f != null) {
                ItemCollection i = f.getPathInfo(true);
                while (i.next()) {
                    m_folderTree.expand(i.getID().toString(), s);
                }
                i.close();
            }
        }

        @Override
        public void reset(PageState s) {
            m_folderTree.clearSelection(s);
            // FIXME: add a reset method to Tree and call that instead of this
            // hack
            s.setValue(m_folderTree.getSelectionModel().getStateParameter(),
                       null);
        }

        public Folder getTarget(PageState s) {
            return (Folder) m_targetSel.getSelectedObject(s);
        }

        public boolean isCancelled(PageState s) {
            return m_cancel.isSelected(s);
        }
    }

    // The form containing the browser and the drop down for selecting an
    // action
    private class ItemView extends Form implements Resettable {

        private FolderBrowser m_browser;
        private Paginator m_paginator;
        private OptionGroup m_checkboxGroup;
        private SingleSelect m_actionSel;
        private Submit m_submit;

        public ItemView() {
            super("itemView", new SimpleContainer());
            setMethod(GET);

            final ActionGroup group = new ActionGroup();
            add(group);

            final GridPanel panel = new GridPanel(1);
            group.setSubject(panel);

            m_browser = new FolderBrowser(m_srcFolderSel);
            m_browser.setFilterParameter(m_filter);
            m_browser.setFilterForm(m_filterForm);
            m_paginator = new Paginator((PaginationModelBuilder) m_browser.
                    getModelBuilder(),
                                        ContentSection.getConfig().
                    getFolderBrowseListSize());
            panel.add(m_paginator);
            panel.add(m_browser);

            s_log.debug("Adding filter form...");
            m_filterForm = new FilterForm((FilterFormModelBuilder) m_browser.
                    getModelBuilder());
            FolderManipulator.this.add(m_filterForm);

            m_checkboxGroup = new CheckboxGroup(m_sources);
            panel.add(m_checkboxGroup);

            panel.add(new FormErrorDisplay(this));

            final Container c = new SimpleContainer();
            group.addAction(c);

            c.add(new Label(globalize("cms.ui.folder.edit_selection")));
            m_actionSel = new SingleSelect(m_action);
            m_actionSel.addOption(new Option(COPY, new Label(globalize(
                    "cms.ui.folder.copy"))));
            m_actionSel.addOption(new Option(MOVE, new Label(globalize(
                    "cms.ui.folder.move"))));
            c.add(m_actionSel);
            m_submit = new Submit("Go", globalize("cms.ui.folder.go"));
            c.add(m_submit);

            // Add a new first column to the table
            TableColumn tc = new TableColumn();
            tc.setCellRenderer(new CheckboxRenderer());
            m_browser.getColumnModel().add(0, tc);
        }

        public final FolderBrowser getBrowser() {
            return m_browser;
        }

        @Override
        public void reset(PageState s) {
            m_checkboxGroup.setValue(s, null);
            m_actionSel.setValue(s, null);
            m_paginator.reset(s);
            s.setValue(m_filter, null);
        }

        // The renderer for the first column in the itemView table
        private class CheckboxRenderer implements TableCellRenderer {

            @Override
            public Component getComponent(Table table, PageState state,
                                          Object value,
                                          boolean isSelected, Object key,
                                          int row, int column) {
                BigDecimal n = (BigDecimal) key;
                Option result =
                       new Option(m_sources.marshalElement(n.abs()), "");
                result.setGroup(m_checkboxGroup);
                return result;
            }
        }
    }

    protected class FilterForm extends Form implements FormProcessListener,
                                                       FormInitListener,
                                                       FormSubmissionListener {

        private SimpleContainer panel;
        private boolean visible;
        private FilterFormModelBuilder modelBuilder;
        private TextField filterField;

        public FilterForm(FilterFormModelBuilder modelBuilder) {
            super("folderFilterForm");

            s_log.debug("Creating filter form...");

            this.modelBuilder = modelBuilder;

            addProcessListener(this);
            addInitListener(this);          
            addSubmissionListener(this);

            panel = new BoxPanel(BoxPanel.HORIZONTAL);

            ActionLink allLink = new ActionLink((String) globalize(
                    "cms.ui.folder.filter.all").localize());
            allLink.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    event.getPageState().setValue(m_filter, "");
                }
            });
            panel.add(allLink);

            for (char c = 'A';
                 c <= 'Z'; c++) {
                final char lowerCaseChar = Character.toLowerCase(c);
                ActionLink link = new ActionLink(Character.toString(c));
                link.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent event) {
                        event.getPageState().setValue(m_filter,
                                                      Character.toString(
                                lowerCaseChar));
                    }
                });
                panel.add(link);
            }

            panel.add(new Label((String) globalize("cms.ui.folder.filter").localize()));
            filterField = new TextField(m_filter);
            panel.add(filterField);
            panel.add(
                    new Submit("filterFolderSubmit",
                               (String) globalize("cms.ui.folder.filter_do").localize()));

            add(panel);

        }

        public TextField getFilterField() {
            return filterField;
        }
        
        public void process(FormSectionEvent fse) throws FormProcessException {
        }

        public void init(FormSectionEvent fse) throws FormProcessException {
            fse.getPageState().setValue(FolderManipulator.this.m_filter, null);
            filterField.setValue(fse.getPageState(), null);
        }

        public void submitted(FormSectionEvent fse) throws FormProcessException {
        }

        @Override
        public boolean isVisible(PageState state) {
            if (super.isVisible(state)
                && (modelBuilder.getFolderSize(state) >= CMSConfig.getInstance().
                    getFolderAtoZShowLimit())) {
                return true;
            } else {
                return false;
            }
        }
    }

    protected interface FilterFormModelBuilder {

        public long getFolderSize(PageState state);
    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     * @pre ( key != null )
     */
    public static GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, RESOURCE_BUNDLE);
    }

    private class FolderTreeCellRenderer implements TreeCellRenderer {

        private RequestLocal m_invalidFolders = new RequestLocal();

        /**
         * Render the folders appropriately.  The selected folder is a bold
         * label.  Invalid folders are plain labels.  Unselected, valid folders
         * are control links.  Invalid folders are: the parent folder of the
         * sources, any of the sources, and any subfolders of the sources.
         */
        @Override
        public Component getComponent(Tree tree, PageState state, Object value,
                                      boolean isSelected, boolean isExpanded,
                                      boolean isLeaf, Object key) {

            // Get the list of invalid folders once per request.
            ArrayList invalidFolders = (ArrayList) m_invalidFolders.get(state);

            if (invalidFolders == null) {
                // The list of invalid folders has not been set for this
                // request.  Setting now.
                invalidFolders = new ArrayList();

                DataCollection collection =
                               SessionManager.getSession().retrieve(
                        ContentItem.BASE_DATA_OBJECT_TYPE);
                CompoundFilter filter = collection.getFilterFactory().or();
                // The sources themselves are not valid.
                BigDecimal[] sources = getSources(state);
                for (int i = 0; i < sources.length; i++) {
                    invalidFolders.add(sources[i].toString());

                    Filter temp = filter.addFilter("id = :id" + i);
                    temp.set("id" + i, sources[i]);
                }
                collection.addFilter(filter);

                DataCollection folders =
                               SessionManager.getSession().retrieve(
                        Folder.BASE_DATA_OBJECT_TYPE);
                folders.addEqualsFilter(Folder.IS_DELETED, Boolean.FALSE);

                filter = collection.getFilterFactory().or();
                int count = 0;
                while (collection.next()) {
                    filter.addFilter(Folder.ANCESTORS + " like :ancestors"
                                     + count + " || '%'");
                    filter.set("ancestors" + count,
                               collection.get(ContentItem.ANCESTORS));
                    count++;
                }
                folders.addFilter(filter);

                while (folders.next()) {
                    invalidFolders.add(folders.get(Folder.ID).toString());
                }

                // Get all subfolders of the sources.  These are also not valid.
                /*
                DataQuery dq = SessionManager.getSession().retrieveQuery("com.arsdigita.cms.FoldersAndAllSubFolders");
                dq.setParameter("item_list", invalidFolders);
                
                while (dq.next()) {
                invalidFolders.add (dq.get("folder_id").toString());
                }
                 */

                // The folder from which the sources are being moved/copied is
                // not allowed.
                invalidFolders.add(
                        m_srcFolderSel.getSelectedKey(state).toString());

                // Save the invalid folder list
                m_invalidFolders.set(state, invalidFolders);
            }

            Label l = new Label(value.toString());

            if (invalidFolders.contains(key.toString())) {
                return l;
            }

            // Bold if selected
            if (isSelected) {
                l.setFontWeight(Label.BOLD);
                return l;
            }

            return new ControlLink(l);
        }
    }
}
