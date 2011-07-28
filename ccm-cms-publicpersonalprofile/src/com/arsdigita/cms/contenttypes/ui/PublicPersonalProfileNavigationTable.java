package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItem;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItemCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavigationTable
        extends Table
        implements TableActionListener {

    private static final Logger logger = Logger.getLogger(
            PublicPersonalProfileNavigationTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_TARGET = "table_col_target";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;
    private SimpleEditStep editStep;

    public PublicPersonalProfileNavigationTable(ItemSelectionModel itemModel,
                                                SimpleEditStep editStep) {
        super();
        this.itemModel = itemModel;
        this.editStep = editStep;

        setEmptyView(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.empty")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.target").localize(),
                TABLE_COL_EDIT));

        colModel.add(new TableColumn(
                1,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.targetitem").localize(),
                TABLE_COL_TARGET));

        colModel.add(new TableColumn(
                2,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.nav.remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(new PublicPersonalProfileNavigationTableModelBuilder(
                itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new TargetCellRenderer());
        colModel.get(2).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class PublicPersonalProfileNavigationTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public PublicPersonalProfileNavigationTableModelBuilder(
                ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            PublicPersonalProfile profile = (PublicPersonalProfile) itemModel.
                    getSelectedObject(state);
            return new PublicPersonalProfileNavigationTableModel(
                    table, state, profile);
        }
    }

    private class PublicPersonalProfileNavigationTableModel
            implements TableModel {

        private Table table;
        private String[] mockNav = new String[]{"Allgemein", "Beruflich",
                                                "Forschung", "Lehre", "Projekte",
                                                "Publikationen"};
        private int index = -1;
        private String mockNavItem;
        private PublicPersonalProfile profile;
        private DataCollection linkCollection;
        private PublicPersonalProfileNavItemCollection navItems;

        public PublicPersonalProfileNavigationTableModel(
                Table table, PageState state, PublicPersonalProfile profile) {
            this.table = table;
            linkCollection = RelatedLink.getRelatedLinks(
                    profile, PublicPersonalProfile.LINK_LIST_NAME);
            navItems = new PublicPersonalProfileNavItemCollection();
        }

        @Override
        public int getColumnCount() {
            return mockNav.length;
        }

        @Override
        public boolean nextRow() {
            /*if (index < (mockNav.length - 1)) {
            index++;
            return true;
            } else {
            return false;
            }*/
            return linkCollection.next();
        }

        @Override
        public Object getElementAt(int columnIndex) {
            RelatedLink link = new RelatedLink(linkCollection.getDataObject());
            String key = link.getTitle();
            ContentItem targetItem = link.getTargetItem();
            PublicPersonalProfileNavItem navItem;

            navItem = navItems.getNavItem(key, DispatcherHelper.
                    getNegotiatedLocale().
                    getLanguage());
            navItems.reset();

            switch (columnIndex) {
                case 0:
                    //return mockNav[index];
                    return navItem.getLabel();
                case 1:
                    if (navItem.getGeneratorClass() == null) {
                        return targetItem;
                    } else {
                        return null;
                    }
                case 2:
                    return PublicPersonalProfileGlobalizationUtil.globalize(
                            "publicpersonalprofile.ui.nav.remove").localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            RelatedLink link = new RelatedLink(linkCollection.getDataObject());
            return link.getID();
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int col) {
            //com.arsdigita.cms.SecurityManager securityManager = Utilities.getSecurityManager(state);

            return new Label((String) value);
        }
    }

    private class TargetCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            if (value == null) {
                return new Label("");
            } else {
                final ContentPage item = (ContentPage) value;

                com.arsdigita.cms.SecurityManager securityManager = Utilities.
                        getSecurityManager(state);

                final boolean canEdit = securityManager.canAccess(
                        state.getRequest(),
                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                        item);

                if (canEdit) {
                    final ContentSection section = CMS.getContext().
                            getContentSection();
                    ItemResolver resolver = section.getItemResolver();

                    Link link = new Link(item.getTitle(),
                                         resolver.generateItemURL(
                            state, item, section, item.getVersion()));

                    return link;
                } else {
                    return new Label(item.getTitle());
                }
            }
        }
    }

    private class DeleteCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int col) {
            com.arsdigita.cms.SecurityManager securityManager =
                                              Utilities.getSecurityManager(state);
            PublicPersonalProfile profile = (PublicPersonalProfile) itemModel.
                    getSelectedObject(
                    state);

            boolean canDelete = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                    profile);

            if (canDelete) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicPersonalProfileGlobalizationUtil.
                        globalize("publicpersonalprofile.ui.nav.remove.confirm").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());


        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            final BigDecimal linkId = new BigDecimal(
                    event.getRowKey().toString());

            RelatedLink link = new RelatedLink(linkId);

            link.delete();
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do here.
    }
}
