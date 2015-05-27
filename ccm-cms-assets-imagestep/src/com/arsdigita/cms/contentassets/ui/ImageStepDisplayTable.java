/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.Label;
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
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.Service;
import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.cms.contentassets.util.ImageStepGlobalizationUtil;
import com.arsdigita.cms.ui.ImageDisplay;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ImageStepDisplayTable extends Table {

    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_DEL = "table_col_del";
    private final static String TABLE_COL_UP = "table_col_up";
    private final static String TABLE_COL_DOWN = "table_col_down";
    private final static int TABLE_COL_INDEX_THUMBNAIL = 0;
    private final static int TABLE_COL_INDEX_PROPS = 1;
    //private final static int TABLE_COL_INDEX_NAME = 1;
    //private final static int TABLE_COL_INDEX_DIMENSIONS = 2;
    //private final static int TABLE_COL_INDEX_TYPE = 3;
//    private final static int TABLE_COL_INDEX_CAPTION = 4;
//    private final static int TABLE_COL_INDEX_EDIT = 5;
//    private final static int TABLE_COL_INDEX_UP = 6;
//    private final static int TABLE_COL_INDEX_DOWN = 7;
//    private final static int TABLE_COL_INDEX_DEL = 8;
    private final static int TABLE_COL_INDEX_CAPTION = 2;
    private final static int TABLE_COL_INDEX_EDIT = 3;
    private final static int TABLE_COL_INDEX_UP = 4;
    private final static int TABLE_COL_INDEX_DOWN = 5;
    private final static int TABLE_COL_INDEX_DEL = 6;
    private final static int UP = -1;
    private final static int DOWN = 1;
    private static final String CONTROL_LINK_FONT_SIZE = "font-size: 200%;";

    private final ImageStep imageStep;

    public ImageStepDisplayTable(final ImageStep imageStep) {
        super();
        this.imageStep = imageStep;
        setEmptyView(new Label(ImageStepGlobalizationUtil.globalize(
            "cms.contentassets.ui.image_step.no_image_attached")));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            TABLE_COL_INDEX_THUMBNAIL,
            ImageStepGlobalizationUtil.globalize(
                "cms.contentassets.ui.image_step.image_table.thumbnail")));

        columnModel.add(new TableColumn(
            TABLE_COL_INDEX_PROPS,
            ImageStepGlobalizationUtil
            .globalize("cms.contentassets.ui.image_step.image_table.properties")));

//        columnModel.add(new TableColumn(
//            TABLE_COL_INDEX_NAME,
//            GlobalizationUtil.globalize(
//                "cms.contentasset.image.ui.display.name")));
//
//        columnModel.add(new TableColumn(
//            TABLE_COL_INDEX_DIMENSIONS,
//            GlobalizationUtil.globalize("cms.contentasset.image.ui.display.dimensions")));
//
//        columnModel.add(new TableColumn(
//            TABLE_COL_INDEX_TYPE,
//            GlobalizationUtil.globalize(
//                "cms.contentasset.image.ui.display.type")));
        columnModel.add(new TableColumn(
            TABLE_COL_INDEX_CAPTION,
            GlobalizationUtil.globalize("cms.contentasset.image.ui.caption")));

        columnModel.add(new TableColumn(
            TABLE_COL_INDEX_EDIT,
            ImageStepGlobalizationUtil.globalize(
                "cms.contentassets.ui.image_step.table.edit_attached_image.header"),
            TABLE_COL_EDIT));

        columnModel.add(new TableColumn(
            TABLE_COL_INDEX_UP,
            ImageStepGlobalizationUtil.globalize(
                "cms.contentassets.ui.image_step.table.move_attached_image_up.header"),
            TABLE_COL_UP));

        columnModel.add(new TableColumn(
            TABLE_COL_INDEX_DOWN,
            ImageStepGlobalizationUtil.globalize(
                "cms.contentassets.ui.image_step.table.move_attached_image_down.header"),
            TABLE_COL_DOWN));

        columnModel.add(new TableColumn(
            TABLE_COL_INDEX_DEL,
            ImageStepGlobalizationUtil.globalize(
                "cms.contentassets.ui.image_step.table.remove_attached_image.header"),
            TABLE_COL_DEL));

        setModelBuilder(new ImageTableModelBuilder(imageStep));

        columnModel.get(TABLE_COL_INDEX_THUMBNAIL).setCellRenderer(
            new ThumbnailCellRenderer());
        columnModel.get(TABLE_COL_INDEX_PROPS).setCellRenderer(
            new PropertiesCellRenderer());
//        columnModel.get(TABLE_COL_INDEX_NAME).setCellRenderer(new NameCellRenderer());
//        columnModel.get(TABLE_COL_INDEX_DIMENSIONS).setCellRenderer(new DimensionsCellRenderer());
//        columnModel.get(TABLE_COL_INDEX_TYPE).setCellRenderer(new TypeCellRenderer());
        columnModel.get(TABLE_COL_INDEX_CAPTION).setCellRenderer(
            new CaptionCellRenderer());
        columnModel.get(TABLE_COL_INDEX_EDIT).setCellRenderer(
            new EditCellRenderer());
        columnModel.get(TABLE_COL_INDEX_UP)
            .setCellRenderer(new UpCellRenderer());
        columnModel.get(TABLE_COL_INDEX_DOWN).setCellRenderer(
            new DownCellRenderer());
        columnModel.get(TABLE_COL_INDEX_DEL).setCellRenderer(
            new DeleteCellRenderer());

        addTableActionListener(new ImageStepTableActionListener());

    }

    private class ImageTableModelBuilder extends LockableImpl implements
        TableModelBuilder {

        private final ImageStep imageStep;

        public ImageTableModelBuilder(final ImageStep imageStep) {
            this.imageStep = imageStep;
        }

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            return new ImageTableModel(table, state, imageStep.getItem(state));
        }

    }

    private class ImageTableModel implements TableModel {

        private final Table table;
        private final ContentItem item;
        private final DataCollection images;

        public ImageTableModel(final Table table,
                               final PageState state,
                               final ContentItem item) {
            this.table = table;
            this.item = item;
            images = ItemImageAttachment.getImageAttachments(item);
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            if (images == null) {
                return false;
            } else {
                return images.next();
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final ItemImageAttachment image
                                          = (ItemImageAttachment) DomainObjectFactory
                .newInstance(
                    images.getDataObject());

            switch (columnIndex) {
                case TABLE_COL_INDEX_THUMBNAIL:
                    return image;
                case TABLE_COL_INDEX_PROPS:
                    return image;
//                case TABLE_COL_INDEX_NAME:
//                    return image.getImage().getName();
//                case TABLE_COL_INDEX_DIMENSIONS:
//                    return String.format("%sx%s px", image.getImage().getWidth().toString(),
//                                         image.getImage().getHeight().toString());
//                case TABLE_COL_INDEX_TYPE:
//                    return image.getImage().getMimeType().getLabel();
                case TABLE_COL_INDEX_CAPTION:
                    return image.getCaption();
                case TABLE_COL_INDEX_EDIT:
                    return ImageStepGlobalizationUtil.globalize(
                        "cms.contentassets.ui.image_step.table.edit_attached_image");
                case TABLE_COL_INDEX_UP:
                    return ImageStepGlobalizationUtil.globalize(
                        "cms.contentassets.ui.image_step.table.move_attached_image_up");
                case TABLE_COL_INDEX_DOWN:
                    return ImageStepGlobalizationUtil.globalize(
                        "cms.contentassets.ui.image_step.table.move_attached_image_down");
                case TABLE_COL_INDEX_DEL:
                    return ImageStepGlobalizationUtil.globalize(
                        "cms.contentassets.ui.image_step.table.remove_attached_image");
                default:
                    return null;

            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return images.getDataObject().getOID().toString();
        }

    }

    private class ThumbnailCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final ItemImageAttachment image = (ItemImageAttachment) value;

            return new ImageStepImageDisplay(image.getImage());
        }

    }

    private class ImageStepImageDisplay extends ImageDisplay {

        private final ImageAsset image;

        public ImageStepImageDisplay(final ImageAsset image) {
            super(null);
            this.image = image;
        }

        @Override
        protected ImageAsset getImageAsset(final PageState state) {
            return image;
        }

        @Override
        protected void generateImagePropertiesXML(final ImageAsset image,
                                                  final PageState state,
                                                  final Element element) {
            final Integer thumbWidth = CMS.getConfig()
                .getImageBrowserThumbnailMaxWidth();
            final Integer thumbHeight = CMS.getConfig()
                .getImageBrowserThumbnailMaxHeight();

            final Double aspectRatio = image.getWidth().doubleValue() / image
                .getHeight().doubleValue();

            final Double imgWidth;
            final Double imgHeight;
            if (image.getWidth().doubleValue() > image.getHeight().doubleValue()) {
                imgWidth = (double) thumbWidth;
                imgHeight = imgWidth / aspectRatio;
            } else {
                imgHeight = (double) thumbHeight;
                imgWidth = imgHeight * aspectRatio;
            }

            element.addAttribute("name", image.getName());
            final String imgUrl = URL.getDispatcherPath()
                                            + Service.getImageURL(image);
            final String src;
            if (imgUrl.contains("?")) {
                 src = String.format("%s&maxWidth=%d&maxHeight=%d", 
                                     imgUrl, 
                                     imgWidth.intValue(), 
                                     imgHeight.intValue());
            } else {
                 src = String.format("%s?maxWidth=%d&maxHeight=%d", 
                                     imgUrl, 
                                     imgWidth.intValue(), 
                                     imgHeight.intValue());
            }
            element.addAttribute("src", src);
            
            //final BigDecimal width = image.getWidth();
            //if (width != null) {
                element.addAttribute("width", Integer.toString(imgWidth.intValue()));
            //}
            //final BigDecimal height = image.getHeight();
            //if (height != null) {
                element.addAttribute("height", Integer.toString(imgHeight.intValue()));
            //}
            element.addAttribute("plain", "true");
        }

    }

    private class PropertiesCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final ItemImageAttachment attachment = (ItemImageAttachment) value;
            final ReusableImageAsset image = attachment.getImage();

            final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);

            panel.add(new Label(image.getName()));
            panel.add(new Label(String.format("%sx%s px", image.getWidth()
                                              .toString(),
                                              image.getHeight().toString())));
            panel.add(new Label(image.getMimeType().getLabel()));

            return panel;
        }

    }

    private class NameCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            return new Label((String) value);
        }

    }

    private class DimensionsCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            return new Label((String) value);
        }

    }

    private class TypeCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            return new Label((String) value);
        }

    }

    private class CaptionCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            return new Label((String) value);
        }

    }

    private class EditCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS
                .getSecurityManager(state);
            final ContentItem item = imageStep.getItem(state);

            final boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                item);

            if (canEdit) {
                final ControlLink link = new ControlLink(new Label(
                    (GlobalizedMessage) value));
                link.setStyleAttr(CONTROL_LINK_FONT_SIZE);
                return link;
            } else {
                return new Label("");
            }
        }

    }

    private class UpCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            if (0 == row) {
                //First row, don't show up link
                return new Label("");
            } else {
                final ControlLink link = new ControlLink(new Label(
                    (GlobalizedMessage) value));
                link.setStyleAttr(CONTROL_LINK_FONT_SIZE);
                return link;
            }
        }

    }

    private class DownCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final ContentItem item = imageStep.getItem(state);
            final DataCollection images = ItemImageAttachment
                .getImageAttachments(item);

            if ((images.size() - 1) == row) {
                //Last row in table, don't show down link
                return new Label("");
            } else {
                final ControlLink link = new ControlLink(new Label(
                    (GlobalizedMessage) value));
                link.setStyleAttr(CONTROL_LINK_FONT_SIZE);
                return link;
            }
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements
        TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS
                .getSecurityManager(state);
            final ContentItem item = imageStep.getItem(state);

            final boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                item);

            if (canEdit) {
                final ControlLink link = new ControlLink(new Label(
                    (GlobalizedMessage) value));
                link.setConfirmation(ImageStepGlobalizationUtil.globalize(
                    "cms.contentassets.ui.image_step.remove_attached_image.confirm"));
                link.setStyleAttr(CONTROL_LINK_FONT_SIZE);
                return link;
            } else {
                return new Label("");
            }
        }

    }

    private class ImageStepTableActionListener implements TableActionListener {

        public ImageStepTableActionListener() {
            //Nothing
        }

        @Override
        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();
            final TableColumn column = getColumnModel().get(event.getColumn()
                .intValue());

            if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
                imageStep.setAttachment(state, ItemImageAttachment.retrieve(OID
                                        .valueOf(event
                                            .getRowKey().toString())));
                imageStep.showComponent(state, "edit");
            } else if (TABLE_COL_UP.equals(column.getHeaderKey().toString())) {
                move(OID.valueOf(event.getRowKey().toString()), UP, state);
            } else if (TABLE_COL_DOWN.equals(column.getHeaderKey().toString())) {
                move(OID.valueOf(event.getRowKey().toString()), DOWN, state);
            } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
                DomainObjectFactory.newInstance(OID.valueOf(event.getRowKey()
                    .toString())).delete();
                regenSortKeys(state);
            }

        }

        @Override
        public void headSelected(final TableActionEvent event) {
            //Nothing to do here.
        }

    }

    private void regenSortKeys(final PageState state) {
        final ContentItem item = imageStep.getItem(state);
        final DataCollection images = ItemImageAttachment.getImageAttachments(
            item);

        int pos = 0;
        while (images.next()) {
            pos++;
            final DomainObject domainObject = DomainObjectFactory
                .newInstance(images.getDataObject());

            if (domainObject instanceof ItemImageAttachment) {
                final ItemImageAttachment image
                                              = (ItemImageAttachment) domainObject;
                if (image.getSortKey() != pos) {
                    image.setSortKey(pos);
                    image.save();
                }
            }

        }
    }

    private void move(final OID imageOid, final int direction,
                      final PageState state) {
        final ContentItem item = imageStep.getItem(state);
        final DataCollection images = ItemImageAttachment.getImageAttachments(
            item);

        if (imageOid == null) {
            throw new IllegalArgumentException(
                "OID of ImageAttachment must not be null.");
        }

        // No move, nothing to do
        if (direction == 0) {
            return;
        }

        // Find the image in the collection
        while (images.next()) {
            if (images.getDataObject().getOID().equals(imageOid)) {
                break;
            }
        }

        // Throw an {@link IllegalArgumentxception} if the imageOid was not found
        if (!images.getDataObject().getOID().equals(imageOid)) {
            throw new IllegalArgumentException("OID " + imageOid
                                                   + " is not in collection");
        }

        // Get the image to move and test if it is really an ItemImageAttachment
        final DomainObject sortDomainObject = DomainObjectFactory
            .newInstance(images.getDataObject());
        int move = direction;
        if (sortDomainObject instanceof ItemImageAttachment) {

            // Change the sortKey of the ItemImageAttachment to the desired
            // value but respect bounds of the current list
            final int newSortKey = Math.max(1,
                                            Math.min((int) images.size(),
                                                     ((ItemImageAttachment) sortDomainObject)
                                                     .getSortKey() + move));
            ((ItemImageAttachment) sortDomainObject).setSortKey(newSortKey);
            sortDomainObject.save();

            // Now, move all the object between the original position and the 
            // new postition one step in the nessecary direction
            if (move < 0) {
                while (images.previous() && move < 0) {
                    final DomainObject domainObject = DomainObjectFactory
                        .newInstance(images.
                            getDataObject());
                    if (domainObject instanceof ItemImageAttachment) {
                        ((ItemImageAttachment) domainObject).setSortKey(
                            ((ItemImageAttachment) domainObject).getSortKey()
                                + 1);
                        domainObject.save();
                        move++;
                    }
                }
            }
            if (move > 0) {
                while (images.next() && move > 0) {
                    final DomainObject domainObject = DomainObjectFactory
                        .newInstance(images.
                            getDataObject());
                    if (domainObject instanceof ItemImageAttachment) {
                        ((ItemImageAttachment) domainObject).setSortKey(
                            ((ItemImageAttachment) domainObject).getSortKey()
                                - 1);
                        domainObject.save();
                        move--;
                    }
                }
            }
        }

        // close the collection manually to avimageOid warnings because the list
        // will not be closed automatically
        images.close();
    }

}
