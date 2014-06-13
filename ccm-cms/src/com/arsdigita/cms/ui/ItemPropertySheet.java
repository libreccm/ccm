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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.domain.DomainService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.LockableImpl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * <span style="color:red">Deprecated</span>.
 * @deprecated Use {@link com.arsdigita.toolbox.ui.DomainObjectPropertySheet}
 */
public class ItemPropertySheet extends PropertySheet {

    private List m_props;
    private ItemSelectionModel m_itemModel;
    private AttributeFormatter m_toStringFormatter;
    private AttributeFormatter m_recursiveFormatter;

    /**
     * Construct a new ItemPropertySheet
     *
     * @param itemModel The item selection model which feeds items to this
     *    property sheet
     *
     * @deprecated
     */
    public ItemPropertySheet(ItemSelectionModel itemModel) {
        super(new ItemModelBuilder());

        m_itemModel = itemModel;
        m_props = new LinkedList();

        m_toStringFormatter = new SimpleAttributeFormatter();
        m_recursiveFormatter = new RecursiveAttributeFormatter();

        getColumn(0).setVAlign("top");
        getColumn(0).setAlign("left");
        getColumn(1).setVAlign("top");
        getColumn(1).setAlign("left");
    }

    /**
     * Construct a new ItemPropertySheet
     *
     * @param itemModel The item selection model which feeds items to this
     *    property sheet
     * @param valueOutputEscape The value of the label-value pair(i.e.,
     *    column[1])'s output-escaping
     *
     * @deprecated
     */
    public ItemPropertySheet(ItemSelectionModel itemModel, boolean valueOutputEscape) {

        super(new ItemModelBuilder(), valueOutputEscape);

        m_itemModel = itemModel;
        m_props = new LinkedList();

        m_toStringFormatter = new SimpleAttributeFormatter();
        m_recursiveFormatter = new RecursiveAttributeFormatter();

        getColumn(0).setVAlign("top");
        getColumn(0).setAlign("left");
        getColumn(1).setVAlign("top");
        getColumn(1).setAlign("left");
    }

    /**
     * Add a new property to the sheet. The sheet will automatically
     * retrieve an attribute of the item and call toString() on it
     *
     * @param label The label for the attribute
     * @param attribute The name for the attribute. Could be a simple name
     *   or a compound path, such as "foo.bar.baz"
     * @deprecated use add(GlobalizedMessage label, String attribute) instead
     */
    public void add(String label, String attribute) {
        add(GlobalizationUtil.globalize(label), attribute);
    }


    /**
     * Add a new property to the sheet. The sheet will automatically retrieve
     * an attribute of the item and call toString() on it
     *
     * @param label The label for the attribute
     * @param attribute The name for the attribute. Could be a simple name
     *   or a compound path, such as "foo.bar.baz"
     */
    public void add(GlobalizedMessage label, String attribute) {
        // Determine if we are dealing with a simple string or a complex
        // path
        if(attribute.indexOf('.') == -1) {
            add(label, attribute, m_toStringFormatter);
        } else {
            add(label, attribute, m_recursiveFormatter);
        }
    }

    /**
     * Add a new property to the sheet. The sheet will use an
     * AttributeFormatter to convert the value of the attribute
     * to a String.
     *
     * @param label The label for the attribute
     * @param attribute The name for the attribute
     * @param formatter An instance of AttributeFormatter
     * @deprecated
     */
    public void add(String label, String attribute, AttributeFormatter f) {
        add(GlobalizationUtil.globalize(label), attribute, f);
    }

    /**
     * Add a new property to the sheet. The sheet will use an
     * AttributeFormatter to convert the value of the attribute
     * to a String.
     *
     * @param label The label for the attribute
     * @param attribute The name for the attribute
     * @param formatter An instance of AttributeFormatter
     */
    public void add(GlobalizedMessage label, String attribute, AttributeFormatter f) {
        m_props.add(new Property(label, attribute, f));
    }

    /**
     * @return The item selection model
     * @deprecated
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }

    /**
     * @return The iterator over all properties
     * @deprecated
     */
    protected Iterator properties() {
        return m_props.iterator();
    }

    /**
     * An interface which can transform some property to a string.
     * Most of the time, classes which implement this interface will just
     * return <code>item.get(attribute).toString()</code>
     * <p>In case of associations, however, more complicated processing
     * will be required.
     * @deprecated
     */
    public interface AttributeFormatter {

        /**
         * Retrieve the string value for the specified attribute
         * of the item.
         *
         * @param item The item
         * @param attribute The name of the attribute to get
         * @deprecated
         */
        String format(ContentItem item, String attribute, PageState state);
    }

    // Associates a label with the attribute and the formatter
    /** @deprecated */
    private static class Property {

        private GlobalizedMessage m_label;
        private String m_attr;
        private AttributeFormatter m_formatter;

        public Property(GlobalizedMessage l, String a, AttributeFormatter f) {
            m_label = l;
            m_attr = a;
            m_formatter = f;
        }

        /**
         *  @deprecated use getGlobalizedLabel instead
         */
        public String getLabel() {
            return m_label.getKey();
        }

        public GlobalizedMessage getGlobalizedLabel() { return m_label; }

        public String getAttribute() { return m_attr; }

        public AttributeFormatter getFormatter() { return m_formatter; }
    }

    // Build up the item model from the iterator over all properties
    /** @deprecated */
    private static class ItemPropertiesModel implements PropertySheetModel {

        private ContentItem m_item;
        private PageState m_state;
        private Iterator m_props;
        private Property m_current;
        private final static String ERROR =
            "No current property. Make sure that nextRow() was called at least once.";

        /** @deprecated */
        public ItemPropertiesModel(ContentItem item, Iterator props, PageState state) {
            m_item = item;
            m_props = props;
            m_state = state;
            m_current = null;
        }

        /** @deprecated */
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

        /** @deprecated */
        public String getValue() {
            if(m_current == null) {
                throw new IllegalStateException(ERROR);
            }
            return m_current.getFormatter()
                .format(m_item, m_current.getAttribute(), m_state);
        }
    }

    // Builds an ItemPropertiesModel
    /** @deprecated */
    private static class ItemModelBuilder extends LockableImpl
        implements PropertySheetModelBuilder {

        public PropertySheetModel makeModel(PropertySheet sheet, PageState state) {
            ItemPropertySheet s = (ItemPropertySheet)sheet;
            return new ItemPropertiesModel (
                                            (ContentItem)s.getItemSelectionModel().getSelectedObject(state),
                                            s.properties(),
                                            state
                                            );
        }
    }

    // Abstract formatter which maintains a "default" string
    /** @deprecated */
    private static abstract class DefaultAttributeFormatter
        extends DomainService
        implements AttributeFormatter {

        private String m_default;

        /** @deprecated */
        public DefaultAttributeFormatter() {
            this("<i>unknown</i>");
        }

        /** @deprecated */
        public DefaultAttributeFormatter(String def) {
            m_default = def;
        }

        /** @deprecated */
        public String getDefaultString() {
            return m_default;
        }
    }

    // A simple attribute formatter that calls get on the item with the
    // specified attribute
    /** @deprecated */
    private static class SimpleAttributeFormatter
        extends DefaultAttributeFormatter {

        /** @deprecated */
        public SimpleAttributeFormatter() {
            super();
        }

        /** @deprecated */
        public SimpleAttributeFormatter(String def) {
            super(def);
        }

        /** @deprecated */
        public String format(ContentItem item, String attribute, PageState state) {
            Object value = get(item, attribute);

            if(value == null) {
                return getDefaultString();
            } else {
                return value.toString();
            }
        }
    }

    // A more advanced attribute formatter. Folows the path to the value
    // by following the names in the attribute string. For example, if
    // the string says "foo.bar.baz", the formatter will attempt to call
    // item.get("foo").get("bar").get("baz");

    /** @deprecated */
    private static class RecursiveAttributeFormatter
        extends DefaultAttributeFormatter {

        /** @deprecated */
        public RecursiveAttributeFormatter() {
            super();
        }

        /** @deprecated */
        public RecursiveAttributeFormatter(String def) {
            super(def);
        }

        /** @deprecated */
        public String format(ContentItem item, String attribute, PageState state) {
            StringTokenizer tokenizer = new StringTokenizer(attribute, ".");
            String token = null;
            Object value = getDataObject(item);

            while(tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                // Null check
                value = ((DataObject)value).get(token);
                if(value == null)
                    return getDefaultString();
            }

            // Extract leaf value
            if(token == null || value == null)
                return getDefaultString();

            return value.toString();
        }
    }

}
