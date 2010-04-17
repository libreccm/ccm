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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateCollection;
import com.arsdigita.cms.TemplateManagerFactory;
import com.arsdigita.cms.TemplateMapping;
import com.arsdigita.cms.ui.ContentSectionRequestLocal;
import com.arsdigita.cms.ui.type.ContentTypeRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import org.apache.log4j.Logger;

/**
 * Displays all templates assigned to a content section, along with
 * their use context. Allows the ability to unassign templates, and to
 * make them default.
 *
 * @author Stanislav Freidin
 * @version $Id: SectionTemplatesListing.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SectionTemplatesListing extends TemplatesListing {

    private static final Logger s_log = Logger.getLogger
        (SectionTemplatesListing.class);

    private final TableColumn m_defaultCol;
    private final ContentSectionRequestLocal m_section;
    private final ContentTypeRequestLocal m_type;

    public SectionTemplatesListing(final ContentSectionRequestLocal section,
                                   final ContentTypeRequestLocal type) {
        m_section = section;
        m_type = type;

        m_defaultCol = addColumn("Default", TemplateMapping.IS_DEFAULT,
                                 false, new DefaultCellRenderer());

        addTableActionListener(new TableActionAdapter() {
                public void cellSelected(TableActionEvent e) {
                    PageState s = e.getPageState();
                    TemplatesListing l = (TemplatesListing)e.getSource();
                    int i = e.getColumn().intValue();
                    TableColumn c = l.getColumnModel().get(i);

                    // Safe to check pointer equality since the column is
                    // created statically
                    if (c == m_defaultCol)
                        setDefaultTemplate
                            (s, (TemplateMapping) getMappingModel
                             ().getSelectedObject(s));
                }
            });

        // Add the column with the "remove" link
        addRemoveColumn();
    }

    /**
     * Get the column that contains the "set default" link
     */
    public TableColumn getDefaultColumn() {
        return m_defaultCol;
    }

    /**
     * Get the templates for the current content section and type
     */
    protected TemplateCollection getTemplateCollection(final PageState state) {
        final ContentSection section = m_section.getContentSection(state);
        final ContentType type = m_type.getContentType(state);

        return TemplateManagerFactory.getInstance().getTemplates
            (section, type);
    }

    /**
     * Remove the current template
     */
    protected void removeTemplate(PageState s, TemplateMapping m) {
        Template t = m.getTemplate();

        // Yes, this code will just call SectionTemplateMapping.delete,
        // but this seems safer
        TemplateManagerFactory.getInstance().removeTemplate
            (m.getContentSection(), (ContentType)m.getParent(),
             t, m.getUseContext());

        // XXX domlay: perhaps of note, here the coordination of
        // publishing and template lifecycle stuff is in the UI code.

        //Delete the template permanently
        t.unpublish();
        t.delete();
    }

    /**
     * Set the template as default
     */
    protected void setDefaultTemplate(PageState s, TemplateMapping m) {
        // Yes, this code will just call
        // SectionTemplateMapping.setDefault but this seems safer.
        TemplateManagerFactory.getInstance().setDefaultTemplate
            (m.getContentSection(), (ContentType)m.getParent(),
             m.getTemplate(), m.getUseContext());
    }

    /**
     * Render the "set default" link/label
     */
    protected static class DefaultCellRenderer implements TableCellRenderer {
        // Static instantiation is safe since these components will
        // never ever change
        private ControlLink m_link;
        private Label m_label;

        public DefaultCellRenderer() {
            m_link = new ControlLink(new Label(GlobalizationUtil.globalize
                                     ("cms.ui.templates.set_as_default")));
            m_link.setClassAttr("setDefaultLink");
            m_label = new Label(GlobalizationUtil.globalize("cms.ui.templates.default"));
            m_label.setFontWeight(Label.BOLD);
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            if (((Boolean) value).booleanValue())
                return m_label;
            else
                return m_link;
        }
    }
}
