/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.cms.contentassets.util.FileAttachmentGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DataObjectNotFoundException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 *
 * A Table for displaying File attachments
 *
 * @author Scott Seago (sseago@redhat.com)
 * @version $Id: FileAttachmentsTable.java 1592 2007-06-21 16:48:55Z lbcfrancois
 * $
 *
 *
 */
public class FileAttachmentsTable extends Table {

    private static final Logger s_log = Logger.getLogger(FileAttachmentsTable.class);

    public static final String[] s_tableHeaders = {"File", "Description/Caption", " ", " ", " ", " "};

    private static final String DELETE_EVENT = "delete";
    private static final String UP_EVENT = "up";
    private static final String DOWN_EVENT = "down";
    private static final String EDIT_EVENT = "edit";

    private ItemSelectionModel m_model;
    private FileAttachmentSelectionModel m_fileModel;
    private RequestLocal m_size;
    private RequestLocal m_editor;

    public FileAttachmentsTable(ItemSelectionModel model, FileAttachmentSelectionModel fileModel) {
        super(new FileAttachmentModelBuilder(model), s_tableHeaders);
        m_model = model;
        m_fileModel = fileModel;
        setRowSelectionModel(m_fileModel);
        getColumn(0).setCellRenderer(new FileLinkCellRenderer());
        getColumn(1).setCellRenderer(new DescriptionCellRenderer());
        getColumn(2).setCellRenderer(new EditLinkCellRenderer());
        getColumn(3).setCellRenderer(new DeleteLinkCellRenderer());
        getColumn(4).setCellRenderer(new MoveUpLinkCellRenderer());
        getColumn(5).setCellRenderer(new MoveDownLinkCellRenderer());

        m_size = new RequestLocal();
        m_editor = new RequestLocal() {

            public Object initialValue(PageState state) {
                SecurityManager sm = Utilities.getSecurityManager(state);
                ContentItem item = (ContentItem) m_model
                        .getSelectedObject(state);
                Boolean val = new Boolean(sm.canAccess(
                        state.getRequest(),
                        SecurityManager.EDIT_ITEM,
                        item
                ));
                return val;
            }
        };
        setWidth("100%");
    }

    /* Code responding to events*/
    public void respond(PageState state) throws ServletException {
        String event = state.getControlEventName();
        String value = state.getControlEventValue();

        m_fileModel.clearSelection(state);
        if (Boolean.TRUE.equals(m_editor.get(state))) {
            if (DELETE_EVENT.equals(event)) {
                try {
                    FileAttachment attachment = new FileAttachment(new BigDecimal(value));
                    attachment.delete();
                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (UP_EVENT.equals(event)) {
                try {
                    FileAttachment attachment = new FileAttachment(new BigDecimal(value));
                    attachment.swapWithPrevious();
                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (DOWN_EVENT.equals(event)) {
                try {
                    FileAttachment attachment = new FileAttachment(new BigDecimal(value));
                    attachment.swapWithNext();
                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (EDIT_EVENT.equals(event)) {
                try {
                    // FileAttachment attachment = new FileAttachment(new BigDecimal(value));
                    m_fileModel.setSelectedKey(state, new BigDecimal(value));
                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class DeleteLinkCellRenderer implements TableCellRenderer {

        public Component getComponent(final Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int column) {
            SimpleContainer sc = new SimpleContainer();

            final String modKey = (String) key;

            if (Boolean.TRUE.equals(m_editor.get(state))) {
                ControlLink delLink = new ControlLink(new Label(FileAttachmentGlobalizationUtil.globalize(
                        "cms.contentassets.file_attachment.table_delete"))) {

                            public void setControlEvent(PageState s) {
                                s.setControlEvent(table, DELETE_EVENT, modKey);
                            }

                        };
                sc.add(delLink);
            }
            return sc;

        }

    }

    private class EditLinkCellRenderer implements TableCellRenderer {

        public Component getComponent(final Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int column) {
            SimpleContainer sc = new SimpleContainer();

            final String modKey = (String) key;

            if (Boolean.TRUE.equals(m_editor.get(state))) {
                if (isSelected) {
                    sc.add(new Label("edit", Label.BOLD));
                } else {
                    ControlLink delLink = new ControlLink(new Label(FileAttachmentGlobalizationUtil.globalize(
                            "cms.contentassets.file_attachment.table_edit"))) {

                                public void setControlEvent(PageState s) {
                                    s.setControlEvent(table, EDIT_EVENT, modKey);
                                }
                            };
                    sc.add(delLink);
                }
            }
            return sc;
        }
    }

    private class MoveUpLinkCellRenderer implements TableCellRenderer {

        public Component getComponent(final Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int column) {
            boolean isFirst = (row == 0);
            SimpleContainer sc = new SimpleContainer();

            final String modKey = (String) key;

            if (!isFirst && Boolean.TRUE.equals(m_editor.get(state))) {
                ControlLink delLink = new ControlLink(new Label(FileAttachmentGlobalizationUtil.globalize(
                        "cms.contentassets.file_attachment.table_up"))) {

                            public void setControlEvent(PageState s) {
                                s.setControlEvent(table, UP_EVENT, modKey);
                            }

                        };
                sc.add(delLink);
            }
            return sc;

        }

    }

    private class MoveDownLinkCellRenderer implements TableCellRenderer {

        public Component getComponent(final Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int column) {
            if (m_size.get(state) == null) {
                m_size.set(state,
                        new Long(((FileAttachmentModelBuilder.FileAttachmentTableModel) table
                                .getTableModel(state)).size()));
            }
            boolean isLast = (row == ((Long) m_size.get(state)).intValue() - 1);
            SimpleContainer sc = new SimpleContainer();

            final String modKey = (String) key;

            if (!isLast && Boolean.TRUE.equals(m_editor.get(state))) {
                ControlLink delLink = new ControlLink(new Label(FileAttachmentGlobalizationUtil.globalize(
                        "cms.contentassets.file_attachment.table_down"))) {

                            public void setControlEvent(PageState s) {
                                s.setControlEvent(table, DOWN_EVENT, modKey);
                            }

                        };
                sc.add(delLink);
            }

            return sc;

        }

    }

    private class FileLinkCellRenderer implements TableCellRenderer {

        public Component getComponent(final Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int column) {
            final String downloadKey = (String) key;
            FileAttachment attachment = new FileAttachment(new BigDecimal(downloadKey));

            String cap = attachment.getAdditionalInfo();
            if (cap != null) {
                if (attachment.getAdditionalInfo().equals("caption")) {

                    if (attachment.getDisplayName() != null && attachment.getDisplayName().equals("iscaption")) {
                        return new Label(" ");
                    }
                    Label label = new Label(attachment.getDisplayName());
                    label.setOutputEscaping(false);
                    return label;
                }
            }

            return new Link(attachment.getDisplayName(),
                    Utilities.getAssetURL(attachment));
        }
    }

    private class DescriptionCellRenderer implements TableCellRenderer {

        public Component getComponent(final Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int column) {
            final String downloadKey = (String) key;
            FileAttachment attachment = new FileAttachment(new BigDecimal(downloadKey));

            Label label = new Label(attachment.getDescription());
            label.setOutputEscaping(false);
            return label;
        }
    }

}
