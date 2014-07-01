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
package com.arsdigita.cms.ui.templates;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.CategoryTemplateMapping;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.TemplateCollection;
import com.arsdigita.cms.TemplateMapping;
import com.arsdigita.cms.ui.category.CategoryRequestLocal;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataObject;

/**
 * Displays all templates assigned to a content item.
 * Allows the ability to unassign templates. Provides a link which
 * should lead to some UI that will assign a new template to an item in
 * the specified use context; it is up to the container of this class
 * to actually implement this UI.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: CategoryTemplatesListing.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class CategoryTemplatesListing extends TemplatesListing {

    private CategoryRequestLocal m_category;
    private TableColumn m_typeCol;

    private RequestLocal m_currentContext;

    public CategoryTemplatesListing(CategoryRequestLocal category) {
        super(new MappingSelectionModel());
        m_category = category;

        setDataQueryBuilder(new AbstractQueryBuilder());

        m_typeCol = addColumn("Content Type", CategoryTemplateMapping.CONTENT_TYPE,
                              false, new TypeCellRenderer());
        addRemoveColumn();
    }

    /**
     * Retrieve the "assign" column
     */
    public final TableColumn getTypeColumn() {
        return m_typeCol;
    }

    /**
     * Get the templates for the current content section and type
     */
    protected TemplateCollection getTemplateCollection(PageState s) {
        Category category = m_category.getCategory(s);
        return CategoryTemplateMapping.getTemplates(category);
    }

    /**
     * Remove the current template
     */
    protected void removeTemplate(PageState s, TemplateMapping m) {
        // Could be null if the "remove" link was somehow clicked
        // on an empty row
        if(m == null) return;

        m.delete();
    }

    private static class MappingSelectionModel extends ACSObjectSelectionModel {

        public static final String MAPPING = "m";

        public MappingSelectionModel() {
            super(new ParameterSingleSelectionModel(new BigDecimalParameter(MAPPING)));
        }

  }


    /**
     * Render the "content type" label
     */
    protected static class TypeCellRenderer implements TableCellRenderer {

        public TypeCellRenderer() {}

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            ContentType type =
                (ContentType)DomainObjectFactory.newInstance((DataObject)value);
            return new Label(type.getName());
        }
    }
}
