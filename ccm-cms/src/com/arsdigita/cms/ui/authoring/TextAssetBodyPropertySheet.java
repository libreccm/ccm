/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.cms.TextAsset;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import java.util.Iterator;

import com.arsdigita.kernel.ui.DomainObjectSelectionModel;

import com.arsdigita.globalization.GlobalizedMessage;

import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.Table;

/**
 *  This extends DomainObjectPropertySheet and has a lot of duplicate
 *  code from it because it uses so many private inner classes
 * @version $Id: TextAssetBodyPropertySheet.java 2090 2010-04-17 08:04:14Z pboy $ 
 */
public class TextAssetBodyPropertySheet extends DomainObjectPropertySheet {

    /**
     * Construct a new TextAssetBodyPropertySheet
     *
     * @param objModel The selection model which feeds domain objects to this
     *    property sheet.
     *
     */
    public TextAssetBodyPropertySheet(DomainObjectSelectionModel objModel) {
        super(objModel);
        setModelBuilder(new TMBAdapter(new DomainObjectModelBuilder()));
        getColumn(1).setCellRenderer(new TextAssetBodyLabelCellRenderer());
    }

    // Build up the object properties model from the iterator over all properties
    private static class TextAssetBodyPropertiesModel implements PropertySheetModel {

        public final static String MIME_TYPE_KEY = 
            TextAssetBodyLabelCellRenderer.MIME_TYPE_KEY;

        private TextAsset m_obj;
        private PageState m_state;
        private Iterator m_props;
        private Property m_current;
        private static String ERROR =
            "No current property. Make sure that nextRow() was called at least once.";

        public TextAssetBodyPropertiesModel(TextAsset obj, Iterator props, 
                                            PageState state) {
            m_obj = obj;
            m_props = props;
            m_state = state;
            m_current = null;
        }

        public boolean nextRow() {
            if(!m_props.hasNext()) {
                return false;
            }

            m_current = (Property)m_props.next();
            return true;
        }

        /**
         *  @deprecated use getGlobalizedLabel() instead
         */
        public String getLabel() {
            return getGlobalizedLabel().getKey();
        }

        public GlobalizedMessage getGlobalizedLabel() {
            if(m_current == null) {
                throw new IllegalStateException(ERROR);
            }
            return m_current.getGlobalizedLabel();
        }

        public String getValue() {
            return getObjectValue().toString();
        }

        public Object getObjectValue() {
            if(m_current == null) {
                throw new IllegalStateException(ERROR);
            }

            if ((TextAsset.CONTENT.equals(m_current.getAttribute()) ||
                MIME_TYPE_KEY.equals(m_current.getAttribute())) &&
                m_obj != null && m_obj.getText() != null 
                && m_obj.getMimeType() != null) {
                return m_obj;
            } else {
                return m_current.getFormatter()
                    .format(m_obj, m_current.getAttribute(), m_state);
            }
        }

        public String getAttribute() {
            return m_current.getAttribute();
        }
    }

    // Builds an TextAssetBodyPropertiesModel
    private static class DomainObjectModelBuilder extends LockableImpl
        implements PropertySheetModelBuilder {

        public PropertySheetModel makeModel(PropertySheet sheet, PageState state) {
            TextAssetBodyPropertySheet s = (TextAssetBodyPropertySheet)sheet;
            return new TextAssetBodyPropertiesModel 
                ((TextAsset)s.getObjectSelectionModel().getSelectedObject(state),
                 s.properties(), state );
        }
    }


    // These are both from PropertySheet

    // Convert a PropertySheetModelBuilder to a TableModelBuilder
    private static class TMBAdapter
        extends LockableImpl implements TableModelBuilder {

        private PropertySheetModelBuilder m_builder;

        public TMBAdapter(PropertySheetModelBuilder b) {
            m_builder = b;
        }

        public TableModel makeModel(Table t, PageState s) {
            return new TableModelAdapter
                ((TextAssetBodyPropertiesModel)m_builder.makeModel((PropertySheet)t, s));
        }

        public void lock() {
            m_builder.lock();
            super.lock();
        }
    }

    // Wraps a PropertySheetModel
    private static class TableModelAdapter implements TableModel {

        private TextAssetBodyPropertiesModel m_model;
        private int m_row;

        public TableModelAdapter(TextAssetBodyPropertiesModel model) {
            m_model = model;
            m_row = -1;
        }

        public int getColumnCount() { return 2; }

        public boolean nextRow() {
            m_row++;
            return m_model.nextRow();
        }

        public Object getElementAt(int columnIndex) {
            if(columnIndex == 0) {
                return m_model.getGlobalizedLabel();
            } else {
                return m_model.getObjectValue();
            }
        }

        public Object getKeyAt(int columnIndex) {
            return m_model.getAttribute();
        }

        public PropertySheetModel getPSModel() {
            return m_model;
        }
    }
}
