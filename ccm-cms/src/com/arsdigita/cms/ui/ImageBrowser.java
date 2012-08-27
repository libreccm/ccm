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
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.Service;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.image.ImageSizer;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.URL;
import java.awt.Dimension;
import java.math.BigDecimal;
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
 * @author Sören Bernstein (quasimodo) <sbernstein@quasiweb.de>
 * @version $Id: ImageBrowser.java 1940 2009-05-29 07:15:05Z terry $
 */
public class ImageBrowser extends Table {

    private ImageBrowserModelBuilder m_builder;
    private static final String[] HEADERS = {"Thumbnail", "Name", "Size", "Type", "Action", ""};
    private static final int THUMB = 0;
    private static final int NAME = 1;
    private static final int SIZE = 2;
    private static final int TYPE = 3;
    private static final int LINK = 4;
    private static final int DELETE = 5;
    private int m_numColumns = -1;
    private int m_mode;
    private Dimension m_thumbSize;
    private static final Logger s_log = Logger.getLogger(ImageBrowser.class);

    /**
     * Construct a new ImageBrowser with default mode.
     *
     * @param builder the {@link ImageBrowserModelBuilder} that will supply this
     * component with its {@link ImageBrowserModel} during each request
     */
    public ImageBrowser(ImageBrowserModelBuilder b) {

        this(b, ImageComponent.ATTACH_IMAGE);
    }

    /**
     * Construct a new ImageBrowser with requested mode.
     *
     * @param builder the {@link ImageBrowserModelBuilder} that will supply this
     * component with its {@link ImageBrowserModel} during each request
     * @param mode the component mode (see {@link ImageComponent})
     */
    public ImageBrowser(ImageBrowserModelBuilder b, int mode) {
        super(new BuilderAdapter(b), HEADERS);
        m_mode = mode;
        setThumbnailSize(CMS.getConfig().getImageBrowserThumbnailMaxWidth(),
                CMS.getConfig().getImageBrowserThumbnailMaxHeight());
        m_builder = b;

        getHeader().setDefaultRenderer(new DefaultTableCellRenderer(false));
        addColumn(new ThumbnailCellRenderer());
        addColumn(new DefaultTableCellRenderer(false));
        addColumn(new DefaultTableCellRenderer(false));
        addColumn(new DefaultTableCellRenderer(false));
        addColumn(new SelectCellRenderer());
        addColumn(new DeleteCellRenderer());

        setCellPadding("4");
        setBorder("1");

        setClassAttr("imageBrowser");
    }

    private void addColumn(TableCellRenderer renderer) {
        getColumn(++m_numColumns).setCellRenderer(renderer);
    }

    public int getNumColumns() {
        return  m_numColumns;
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
     * @return the {@link ImageBrowserModel} used in the current request
     */
    public ImageBrowserModel getImageBrowserModel(PageState state) {
        return ((ImageModelAdapter) getTableModel(state)).getModel();
    }

    /**
     * An action listener that only gets fired when the "select" link is
     * clicked. Child classes should override the linkClicked method.
     */
    public static abstract class LinkActionListener
            extends TableActionAdapter {

        @Override
        public void cellSelected(TableActionEvent e) {
            int c = e.getColumn().intValue();
            if (c == LINK) {
                linkClicked(e.getPageState(), new BigDecimal((String) e.getRowKey()));
            } else if (c == DELETE) {
                deleteClicked(e.getPageState(), new BigDecimal((String) e.getRowKey()));
            }
        }

        public abstract void linkClicked(PageState state, BigDecimal imageId);

        public abstract void deleteClicked(PageState state, BigDecimal imageId);
    }

    // Renders a static image for the current asset
    private class ThumbnailCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(Table table, PageState state, Object value,
                boolean isSelected, Object key,
                int row, int column) {
            ImageAsset a = (ImageAsset) value;

            String url = Service.getImageURL(a);
            String resizeParam = "&width=" +  new Double(m_thumbSize.getWidth()).intValue() + "&height=" + new Double(m_thumbSize.getHeight()).intValue();

            Image img = new Image(URL.getDispatcherPath() + url + resizeParam, a.getName());
            img.setBorder("0");

            return new Link(img, url);
        }
    }

    // Renders the select link if the mode needs one
    private class SelectCellRenderer extends DefaultTableCellRenderer {

        public SelectCellRenderer() {
            super(true);
        }

        @Override
        public Component getComponent(Table table, PageState state, Object value,
                boolean isSelected, Object key,
                int row, int column) {

            if (m_mode == ImageComponent.SELECT_IMAGE || m_mode == ImageComponent.ATTACH_IMAGE) {
                return super.getComponent(table, state, value, isSelected, key, row, column);
            }

            return new Label("");
        }
    }

    // Renders the delete link if the user has permission to delete
    // the asset and it's not used in an article.
    private class DeleteCellRenderer extends DefaultTableCellRenderer {

        public DeleteCellRenderer() {
            super(true);
        }

        @Override
        public Component getComponent(Table table, PageState state, Object value,
                boolean isSelected, Object key,
                int row, int column) {

            // Only show delete link in admin mode
            if (m_mode == ImageComponent.ADMIN_IMAGES) {

                boolean canDelete = false;
                // SecurityManager sm = Utilities.getSecurityManager(state);
                SecurityManager sm = CMS.getSecurityManager(state);
                if (sm.canAccess(state.getRequest(), SecurityManager.DELETE_IMAGES)) {
                    try {
                        ImageAsset asset = (ImageAsset) DomainObjectFactory.newInstance(new OID(ImageAsset.BASE_DATA_OBJECT_TYPE, (BigDecimal) key));
//XXX Find a new way to figure out, if this image is used by any CI so we can decide if it can be deleted
//                    if (!GenericArticleImageAssociation.imageHasAssociation(asset)) {
//                        canDelete = true;
//                    }
                    } catch (DataObjectNotFoundException e) {
                        // can't find asset, can't delete it
                    }

                }
                
                // can delete image because it's not in use
                if (canDelete) {
                    return super.getComponent(table, state, value, isSelected, key, row, column);
                }
            }
            return new Label("");
        }
    }

    // Converts an ImageBrowserModelBuilder to a TableModelBuilder
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

    // Converts an ImageBrowserModel to a TableModel
    private static class ImageModelAdapter implements TableModel {

        private ImageBrowserModel m_model;

        public ImageModelAdapter(ImageBrowserModel m) {
            m_model = m;
        }

        @Override
        public int getColumnCount() {
            return ((ImageBrowser)m_model).getNumColumns();
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
                    return a.getName();

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

                case ImageBrowser.LINK:
                        return "select";

                case ImageBrowser.DELETE:
                    return "delete";

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
