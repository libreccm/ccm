/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.ui;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 *  Displays Documents for a given Category
 *
 * @author Crag Wolfe
 */
public class CategoryItemsBrowser extends DataTable implements DMConstants {

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(CategoryItemsBrowser.class);

    private RequestLocal m_resolver;
    private ContentSection m_section;

    private String m_context;
    private static final SimpleDateFormat simpleDateFormatter
        = new SimpleDateFormat();

    /**
     * Construct a new CategoryItemsBrowser
     * <p>
     * The {@link SingleSelectionModel} which will provide the
     * current category
     *
     * @param sel the {@link ACSObjectSelectionModel} which will maintain
     *   the current category
     *
     * @param numCols the number of columns in the browser
     *
     * @param context the context for the retrieved items. Should be
     *   {@link com.arsdigita.cms.ContentItem#DRAFT} or {@link com.arsdigita.cms.ContentItem#LIVE}
     */
    public CategoryItemsBrowser(CategoryDocModelBuilder cdmb,
                                ACSObjectSelectionModel selCategory, 
                                String context, ContentSection cs) {
        super(cdmb);
        m_context = context;
        m_section = cs;

        s_log.debug("m_section is "+m_section.getName());

        //setEmptyView(new Label(GlobalizationUtil.globalize
        //                       ("cms.ui.category.item.none")));

        addColumn("Document Name", "name", true, new NameCellRenderer());
        addColumn("Type",new MimeTypeCellRenderer());
        addColumn("Size", "fileSize", true, new FileSizeCellRenderer());
        addColumn("Author", "authorSortKey", true, new AuthorCellRenderer());
        addColumn("Last Modified", "lastModifiedDate", true, new DateCellRenderer());
        addColumn("Workspace", "workspaceName", true, new WorkspaceCellRenderer());
        setCellPadding("3");
        setCellSpacing("3");
    }

    /**
     * @return the current context
     */
    public String getContext() {
        return m_context;
    }

    /**
     * @param context the new context for the items. Should be
     *   {@link com.arsdigita.cms.ContentItem#DRAFT} or {@link com.arsdigita.cms.ContentItem#LIVE}
     */
    public void setContext(String context) {
        Assert.isUnlocked(this);
        m_context = context;
    }


    private class DateCellRenderer
        implements TableCellRenderer {
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                  int row, int column) {
            if (value == null) {
                return new Label("unknown");
            }
            return new Label(simpleDateFormatter.format((Date)value));
        }
    }

    private class NameCellRenderer
        implements TableCellRenderer {
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                  int row, int column) {
            s_log.debug("mime: key is "+key.toString());
            String fileID = ((BigDecimal) key).toString();
            Document doc = new Document((BigDecimal) key);
            User user = Web.getWebContext().getUser();
            if (!PermissionService.checkPermission
                (new PermissionDescriptor(PrivilegeDescriptor.READ,
                                          doc, user))) {
                s_log.debug("no read permission for file id"+fileID);
                return new Label(doc.getTitle());
            }
            return new Link(URLDecoder.decode(value.toString()),
                            "file?"+FILE_ID_PARAM_NAME+"="+fileID);
        }
    }

    private class FileSizeCellRenderer
        implements TableCellRenderer {
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                  int row, int column) {
            s_log.debug("filesize: key is "+key.toString());
            return new Label(DMUtils.FileSize.formatFileSize(
                             (BigDecimal) value));
        }
    }

    private class MimeTypeCellRenderer
        implements TableCellRenderer {
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                  int row, int column) {
            s_log.debug("mime: key is "+key.toString());
            Document doc = new Document((BigDecimal) key);
            return new Label(doc.getPrettyMimeType());
        }
    }
        
    private class AuthorCellRenderer
        implements TableCellRenderer {
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                  int row, int column) {
            // can't use the value because it is may be mangled for sort
            // purposes.  see the query.
            //String author = "";
            //if (value != null) {
            //    author = (String) value;
            //}
            //s_log.debug("author: author is "+author);
            //s_log.debug("author: key is "+key.toString());
            //if (! "".equals(author)) {
            //    return new Label(author);
            //}
            String author = "unknown";
            if (key != null) {
                author = (new Document((BigDecimal) key)).getImpliedAuthor();
            }
            return new Label(author);
        }
    }
        
    private class WorkspaceCellRenderer
        implements TableCellRenderer {
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                  int row, int column) {
            s_log.debug("workspace: key is "+key.toString());
            Document doc = new Document((BigDecimal) key);
            Application parentWorkspace = doc.getRepository().getParentApplication();
            User user = Web.getWebContext().getUser();
            if (!PermissionService.checkPermission
                (new PermissionDescriptor(PrivilegeDescriptor.READ,
                                          parentWorkspace,user))) {
                s_log.debug("no permission for "+parentWorkspace.getTitle());
                return new Label("");
            }
            return new Link(parentWorkspace.getTitle(),
                            parentWorkspace.getPath());
        }
    }

    /**
     * Renders a ContentItem in preview mode
     */
    private class ItemSummaryCellRenderer
        implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

            if(value == null)
                return new Label("&nbsp;", false);

            DomainObject d = DomainObjectFactory.newInstance((DataObject)value);

            Assert.isTrue(d instanceof ContentPage);
            ContentPage p = (ContentPage)d;

            Label l = new Label(p.getName() + 
                                "(id is "+p.getID().toString()+")");
            return l;
        }
    }
}
