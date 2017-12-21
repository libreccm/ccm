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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.Service;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.URL;
import com.arsdigita.util.Dimension;
import java.math.BigDecimal;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Displays a list of images in a Table. The table will look something like
 * this:
 *
 * <blockquote><pre><code>
 * +-----------+-------+-------+------+----------+
 * | Thumbnail | Name  | Size  | Type |          |
 * +-----------+-------+-------+------+----------+
 * |   .  .    |       |       |      |          |
 * | {   V  }  | smile | 20x42 | Jpeg | (select) |
 * |   ----    |       |       |      |          |
 * +-----------+-------+-------+------+----------+
 * |           |       |       |      |          |
 * </code></pre></blockquote>
 *
 * @author Stanislav Freidin
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @version $Id: ImageBrowser.java 1940 2009-05-29 07:15:05Z terry $
 */
public class ImageBrowser extends Table {

    private static final Logger s_log = Logger.getLogger(ImageBrowser.class);

    private ImageBrowserModelBuilder m_builder;

    // match columns by (symbolic) index, makes for easier reordering
    /**
     * Index into TableColumn for Thumb column
     */
    private static final int THUMB = 0;
    /**
     * Index into TableColumn for Name column
     */
    private static final int NAME = 1;
    /**
     * Index into TableColumn for Size column
     */
    private static final int SIZE = 2;
    /**
     * Index into TableColumn for Type column
     */
    private static final int TYPE = 3;
    /**
     * Index into TableColumn for Select link column
     */
    private static final int SELECT = 4;
    /**
     * Index into TableColumn for Delete link column
     */
    private static final int DELETE = 5;

    private int m_numColumns = -1;
    private int m_mode;

    private Dimension m_thumbSize;

    /**
     * Construct a new ImageBrowser with default mode.
     *
     * @param builder the {@link ImageBrowserModelBuilder} that will supply this
     *                component with its {@link ImageBrowserModel} during each
     *                request
     */
    public ImageBrowser(ImageBrowserModelBuilder b) {

        this(b, ImageComponent.ATTACH_IMAGE);
    }

    /**
     * Construct a new ImageBrowser table with requested mode.
     *
     * @param builder the {@link ImageBrowserModelBuilder} that will supply this
     *                component with its {@link ImageBrowserModel} during each
     *                request
     * @param mode    the component mode (see {@link ImageComponent})
     */
    public ImageBrowser(ImageBrowserModelBuilder b, int mode) {

        super();  // Create an empty table

        m_builder = b;
        m_mode = mode;

        setThumbnailSize(CMS.getConfig().getImageBrowserThumbnailMaxWidth(),
                         CMS.getConfig().getImageBrowserThumbnailMaxHeight());

        /* Add columns and column header to the yet empty table     */
        TableColumnModel model = getColumnModel();
        model.add(new TableColumn(
            THUMB,
            new Label(GlobalizationUtil.globalize(
                "cms.contentasset.image.ui.table.header_thumb")
            )));
        model.add(new TableColumn(
            NAME,
            new Label(GlobalizationUtil.globalize(
                "cms.contentasset.image.ui.table.header_name")
            )));
        model.add(new TableColumn(
            SIZE,
            new Label(GlobalizationUtil.globalize(
                "cms.contentasset.image.ui.table.header_size")
            )));
        model.add(new TableColumn(
            TYPE,
            new Label(GlobalizationUtil.globalize(
                "cms.contentasset.image.ui.table.header_type")
            )));
        model.add(new TableColumn(
            SELECT,
            new Label(GlobalizationUtil.globalize(
                "cms.contentasset.image.ui.table.header_action_select")
            )));
        model.add(new TableColumn( // Temporary not used due to consistency
            DELETE, null // probs with images probably in use
        //   new Label(GlobalizationUtil.globalize(
        //   "cms.contentasset.image.ui.table.header_action_delete"))
        ));

        model.get(THUMB).setCellRenderer(new ThumbnailCellRenderer());
        model.get(NAME).setCellRenderer(new DefaultTableCellRenderer(false));
        model.get(SIZE).setCellRenderer(new DefaultTableCellRenderer(false));
        model.get(TYPE).setCellRenderer(new DefaultTableCellRenderer(false));
        model.get(SELECT).setCellRenderer(new SelectCellRenderer());
        model.get(DELETE).setCellRenderer(new DeleteCellRenderer());
        setModelBuilder(new BuilderAdapter(b));

        setCellPadding("4");
        setBorder("1");

        setClassAttr("imageBrowser");
    }

    /**
     *
     * @return
     */
    public int getNumColumns() {
        return m_numColumns;
    }

    /**
     * @return the size, in pixels, of the thumbnail images
     */
    public Dimension getThumbnailSize() {
        return m_thumbSize;
    }

    /**
     * Set the thumbnail size
     *
     * @param size the size, in pixels, of the thumbnail images
     */
    public final void setThumbnailSize(int width, int height) {
        m_thumbSize = new Dimension(width, height);
    }

    /**
     * @return the {@link ImageBrowserModelBuilder}
     */
    public ImageBrowserModelBuilder getImageBrowserModelBuilder() {
        return m_builder;
    }

    /**
     * @param state The current page state
     *
     * @return the {@link ImageBrowserModel} used in the current request
     */
    public ImageBrowserModel getImageBrowserModel(PageState state) {
        return ((ImageModelAdapter) getTableModel(state)).getModel();
    }

    /**
     * Inner class action listener that only gets fired when the "select" link
     * is clicked. Child classes should override the linkClicked method.
     */
    public static abstract class LinkActionListener
        extends TableActionAdapter {

        /**
         *
         * @param e
         */
        @Override
        public void cellSelected(TableActionEvent e) {
            int c = e.getColumn().intValue();
            if (c == SELECT) {
                linkClicked(e.getPageState(),
                            new BigDecimal((String) e.getRowKey()));
            } else if (c == DELETE) {
                deleteClicked(e.getPageState(),
                              new BigDecimal((String) e.getRowKey()));
            }
        }

        public abstract void linkClicked(PageState state, BigDecimal imageId);

        public abstract void deleteClicked(PageState state, BigDecimal imageId);

    }

    /**
     * Inner private class renders a static image for the current asset.
     */
    private class ThumbnailCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            ImageAsset a = (ImageAsset) value;

            Double aspectRatio = a.getWidth().doubleValue() / a.getHeight()
                .doubleValue();

            Double width;
            Double height;

            if (a.getWidth().doubleValue() > a.getHeight().doubleValue()) {
                width = m_thumbSize.getWidth();
                height = m_thumbSize.getWidth() / aspectRatio;
            } else {
                height = m_thumbSize.getHeight();
                width = height * aspectRatio;
            }

            String url = Service.getImageURL(a);
            // Sets url paramter to resize the images server-side
            String resizeParam = "&maxWidth=" + new Double(m_thumbSize
                .getWidth()).intValue()
                                     + "&maxHeight="
                                     + new Double(m_thumbSize
                    .getHeight())
                    .intValue();

            Image img = new Image(URL.getDispatcherPath() + url + resizeParam,
                                  a.getName());
            img.setBorder("0");

//            final Double width = m_thumbSize.getWidth();
//            final Double height = m_thumbSize.getHeight();
            img.setWidth(Integer.toString(width.intValue()));
            img.setHeight(Integer.toString(height.intValue()));

            return new Link(img, url);
        }

    }

    /**
     * Inner private class renders the select link if the mode needs one
     */
    private class SelectCellRenderer extends DefaultTableCellRenderer {

        public SelectCellRenderer() {
            super(true);
        }

        /**
         *
         * @param table
         * @param state
         * @param value
         * @param isSelected
         * @param key
         * @param row
         * @param column
         *
         * @return
         */
        @Override
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

            if (m_mode == ImageComponent.SELECT_IMAGE
                    || m_mode == ImageComponent.ATTACH_IMAGE) {
                return super.getComponent(table, state, value,
                                          isSelected, key, row, column);
            }

            // return new Label("");  // this variant is deprecated!
            return new Label();
        }

    }

    /**
     * Inner private class renders the delete link if the user has permission to
     * delete the asset and it's not used in an article.
     */
    private class DeleteCellRenderer extends DefaultTableCellRenderer {

        public DeleteCellRenderer() {
            super(true);
        }

        /**
         *
         * @param table
         * @param state
         * @param value
         * @param isSelected
         * @param key
         * @param row
         * @param column
         *
         * @return
         */
        @Override
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

            // Only show delete link in admin mode
            if (m_mode == ImageComponent.ADMIN_IMAGES) {

                boolean canDelete = false;
                // SecurityManager sm = Utilities.getSecurityManager(state);
                SecurityManager sm = CMS.getSecurityManager(state);
                if (sm.canAccess(state.getRequest(),
                                 SecurityManager.DELETE_IMAGES)) {
                    try {

                        final ImageAsset asset = (ImageAsset) DomainObjectFactory
                            .newInstance(new OID(
                                ImageAsset.BASE_DATA_OBJECT_TYPE,
                                key));
                        if (asset instanceof ReusableImageAsset) {

                            final ReusableImageAsset reusable
                                                     = (ReusableImageAsset) asset;
                            canDelete = !reusable.isInUse();
                        } else {
                            canDelete = false;
                        }
                    } catch (DataObjectNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }

                }

                // can delete image because it's not in use
                if (canDelete) {
                    return super.getComponent(table,
                                              state,
                                              value,
                                              isSelected,
                                              key,
                                              row,
                                              column);
                }
            }
            //get all items that uses the image:
            try {
                final ImageAsset asset = (ImageAsset) DomainObjectFactory
                    .newInstance(new OID(
                        ImageAsset.BASE_DATA_OBJECT_TYPE,
                        key));
                if (asset instanceof ReusableImageAsset) {
                    final List list = ((ReusableImageAsset) asset)
                        .getImageUsers();

                    return createLabel(list, state);
                } else {
                    return new Label("");
                }
            } catch (DataObjectNotFoundException ex) {
                throw new RuntimeException(ex);
            }

//            return new Label("image is in use");
        }

    }

    /**
     * creates a BoxPanel with links to the items that are using the image
     *
     * @param list  list with the itemIDs
     * @param state PageState
     *
     * @return
     */
    private BoxPanel createLabel(List list, PageState state) {

        int size = list.size();
        BoxPanel links = new BoxPanel();

        if (size == 0) {
            links.add(new Label("currently not in use"));
            return links;
        }
        if (size >= 1) {
            links.add(createLink(state, list, 0));
        }
        if (size >= 2) {
            links.add(createLink(state, list, 1));
        }
        if (size >= 3) {
            links.add(createLink(state, list, 2));
        }
        if (size > 3) {
            int size2 = size - 3;
            links.add(new Label("and " + size2 + " more"));
        }
        return links;

    }

    /**
     * creates the link to an item of the list.
     *
     * @param state PageState
     * @param list  list with itemIDs
     * @param index
     *
     * @return
     */
    private Link createLink(PageState state, List list, int index) {

        ContentItem item = (ContentItem) DomainObjectFactory
            .newInstance(new OID(
                ContentItem.BASE_DATA_OBJECT_TYPE,
                new BigDecimal((Integer) list.get(index))));

        ContentSection section = item.getContentSection();
        ItemResolver resolver = section.getItemResolver();

        String displayName = item.getDisplayName();
        if (displayName.length() > 20) {
            displayName = displayName.substring(0, 15);
        }

        Link link = new Link(
            "used by " + displayName,
            resolver.generateItemURL(
                state,
                (item.getDraftVersion()),
                section,
                (item.getDraftVersion()).getVersion()));
        return link;
    }

    /**
     * Inner private class converts an ImageBrowserModelBuilder to a
     * TableModelBuilder
     */
    private static class BuilderAdapter extends LockableImpl
        implements TableModelBuilder {

        private ImageBrowserModelBuilder m_builder;

        public BuilderAdapter(ImageBrowserModelBuilder b) {
            m_builder = b;
        }

        @Override
        public TableModel makeModel(Table t, PageState s) {
            return new ImageModelAdapter(
                m_builder.makeModel((ImageBrowser) t, s));
        }

        @Override
        public void lock() {
            m_builder.lock();
            super.lock();
        }

    }

    /**
     * Inner private class converts an ImageBrowserModel to a TableModel.
     */
    private static class ImageModelAdapter implements TableModel {

        private ImageBrowserModel m_model;

        public ImageModelAdapter(ImageBrowserModel m) {
            m_model = m;
        }

        @Override
        public int getColumnCount() {
            return ((ImageBrowser) m_model).getNumColumns();
            //            return ImageBrowser.s_numColumns;
        }

        @Override
        public boolean nextRow() {
            return m_model.nextRow();
        }

        @Override
        public Object getElementAt(int columnIndex) {
            ImageAsset a = m_model.getImageAsset();

            switch (columnIndex) {
                case ImageBrowser.THUMB:
                    return a;

                case ImageBrowser.NAME:
                    String name = a.getName();
                    if (name.length() > 32) {
                        return name.substring(0, 31);

                    } else {
                        return name;
                    }

                case ImageBrowser.SIZE:
                    StringBuilder buf = new StringBuilder();
                    BigDecimal v;

                    v = a.getWidth();
                    if (v == null) {
                        buf.append("???");
                    } else {
                        buf.append(v.toString());
                    }
                    buf.append(" x ");

                    v = a.getHeight();
                    if (v == null) {
                        buf.append("???");
                    } else {
                        buf.append(v.toString());
                    }

                    return buf.toString();

                case ImageBrowser.TYPE:
                    MimeType m = a.getMimeType();
                    if (m == null) {
                        return "???";
                    }

                    return m.getMimeType();

                case ImageBrowser.SELECT:
                    // Due to current design has to be a string! Localisation
                    // works here nevertheless.
                    return (String) GlobalizationUtil.globalize(
                        "cms.contentasset.image.ui.table.link_select")
                        .localize();

                case ImageBrowser.DELETE:
                    // Due to current design has to be a string! Localisation
                    // works here nevertheless.
                    return (String) GlobalizationUtil.globalize(
                        "cms.contentasset.image.ui.table.link_delete")
                        .localize();

                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return m_model.getImageAsset().getID();
        }

        public ImageBrowserModel getModel() {
            return m_model;
        }

    }

}
