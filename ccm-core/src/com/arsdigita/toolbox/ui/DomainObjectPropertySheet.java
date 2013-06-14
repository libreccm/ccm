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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainService;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ui.DomainObjectSelectionModel;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.toolbox.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Displays a list of label-value pairs, which represent the attributes
 * of a domain object.
 * <p>
 * Typical usage is
 * <blockquote><pre><code>
 * DomainObjectPropertySheet mySheet = 
 *         new DomainObjectPropertySheet(myDomainObjectSelectionModel);
 * mySheet.add("Name:", ContentPage.NAME);
 * mySheet.add("Title:", ContentPage.TITLE);
 * </code></pre></blockquote>
 * The first argument is the visible label for the property, and
 * the second argument is the name of the property as it appears in
 * the PDL file.
 * <p>
 * Instead of specifying the property directly, you may specify the "path"
 * to the property. For example,
 * <blockquote><pre><code>
 * mySheet.add("Address Line 1:", "user.address.street");
 * </code></pre></blockquote>
 * The code above tells the <code>DomainObjectPropertySheet</code> to look for
 * the child of the current object named "user"; then look for the child
 * of the user named "address", and finally to return the property of
 * the address named "street".
 * <p>
 * Note that, by default, <code>DomainObjectPropertySheet</code> retrieves
 * the values for its properties directly from the underlying {@link DataObject}
 * of the {@link DomainObject}. This means that the Java <code>getXXX</code>
 * methods of the <code>DomainObject</code> will never be called. Of course, 
 * it is always possible to create a custom {@link AttributeFormatter} that 
 * will call the appropriate methods.
 *
 * @author Stanislav Freidin
 * @version $Id: DomainObjectPropertySheet.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class DomainObjectPropertySheet extends PropertySheet {

    private List m_props;
    private DomainObjectSelectionModel m_objModel;
    private AttributeFormatter m_toStringFormatter;
    private AttributeFormatter m_recursiveFormatter;

    /**
     * Construct a new DomainObjectPropertySheet
     *
     * @param objModel The selection model which feeds domain objects to this
     *    property sheet.
     *
     */
    public DomainObjectPropertySheet(DomainObjectSelectionModel objModel) {
        this(objModel, false);
    }

    /**
     * Construct a new DomainObjectPropertySheet
     *
     * @param objModel The selection model which feeds domain objects to this
     *    property sheet
     * @param valueOutputEscape The value of the label-value pair(i.e.,
     *    column[1])'s output-escaping
     *
     */
    public DomainObjectPropertySheet(
            DomainObjectSelectionModel objModel, boolean valueOutputEscape) {
        super(new DomainObjectModelBuilder(), valueOutputEscape);

        m_objModel = objModel;
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
     * retrieve an attribute of the object and call toString() on it
     *
     * @param label The label for the attribute
     * @param attribute The name for the attribute. Could be a simple name
     *                  or a compound path, such as "foo.bar.baz" (usually a
     *                  PDL property)
     * @deprecated use add(GlobalizedMessage label, String attribute) instead
     */
    public void add(String label, String attribute) {
        add(GlobalizationUtil.globalize(label), attribute);
    }

    /**
     * Add a new property to the sheet. The sheet will automatically
     * retrieve an attribute of the object and call toString() on it
     *
     * @param label The label for the attribute
     * @param attribute The name for the attribute. Could be a simple name
     *                  or a compound path, such as "foo.bar.baz" (usually a
     *                  PDL property)
     */
    public void add(GlobalizedMessage label, String attribute) {
        // Determine if we are dealing with a simple string or a complex
        // path
        if (attribute.indexOf('.') == -1) {
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
     * @param attribute The name for the attribute. Could be a simple name
     *                  or a compound path, such as "foo.bar.baz" (usually a
     *                  PDL property)
     * @param formatter An instance of AttributeFormatter
     * 
     * @deprecated Use add(GlobalizedMessage label, String attribute, 
     *                      AttributeFormatter f) instead
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
     * @param attribute The name for the attribute. Could be a simple name
     *                  or a compound path, such as "foo.bar.baz" (usually a
     *                  PDL property)
     * @param formatter An instance of AttributeFormatter
     */
    public void add(GlobalizedMessage label, String attribute,
                    AttributeFormatter f) {
        m_props.add(new Property(label, attribute, f));
    }

    /**
     * @return The object selection model
     */
    public DomainObjectSelectionModel getObjectSelectionModel() {
        return m_objModel;
    }

    /**
     * @return The iterator over all properties
     */
    protected Iterator properties() {
        return m_props.iterator();
    }

    /**
     * An interface which can transform the value of a (domain) property to a 
     * string.
     * 
     * Most of the time, classes which implement this interface will just
     * return <code>object.get(attribute).toString()</code>
     * <p>In case of associations, however, more complicated processing
     * will be required.
     */
    public interface AttributeFormatter {

        /**
         * Formatter for the value of an attribute. It has to retrieve the value 
         * for the specified attribute of the object and format it as an string
         * if it is one already.
         * 
         * Note: the format method has to be executed at each page request. Take
         * care to properly adjust globalization and localization inside thes
         * method and not earlier in one of the classes using it!
         *
         * @param obj        Object containing the attribute to format.
         * @param attribute  Name of the attribute to retrieve and format
         * @param state      PageState of the request
         * @return           A String representation of the retrieved attribute
         *                   of the domain object.
         */
        String format(DomainObject obj, String attribute, PageState state);
    }

    /**
     * Associates a label with the attribute and the formatter.
     */
    protected static class Property {

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

        public GlobalizedMessage getGlobalizedLabel() {
            return m_label;
        }

        public String getAttribute() {
            return m_attr;
        }

        public AttributeFormatter getFormatter() {
            return m_formatter;
        }
    }

    // Build up the object properties model from the iterator over all properties
    private static class DomainObjectPropertiesModel implements
            PropertySheetModel {

        private DomainObject m_obj;
        private PageState m_state;
        private Iterator m_props;
        private Property m_current;
        private static String ERROR =
            "No current property. Make sure that nextRow() was called at least once.";

        public DomainObjectPropertiesModel(
                DomainObject obj, Iterator props, PageState state) {
            m_obj = obj;
            m_props = props;
            m_state = state;
            m_current = null;
        }

        public boolean nextRow() {
            if (!m_props.hasNext()) {
                return false;
            }

            m_current = (Property) m_props.next();
            return true;
        }

        /**
         *  @deprecated use getGlobalizedLabel() instead
         */
        public String getLabel() {
            return getGlobalizedLabel().getKey();
        }

        public GlobalizedMessage getGlobalizedLabel() {
            if (m_current == null) {
                throw new IllegalStateException(ERROR);
            }
            return m_current.getGlobalizedLabel();
        }

        public String getValue() {
            if (m_current == null) {
                throw new IllegalStateException(ERROR);
            }
            return m_current.getFormatter().format(m_obj,
                                                   m_current.getAttribute(),
                                                   m_state);
        }
    }

    // Builds an DomainObjectPropertiesModel
    private static class DomainObjectModelBuilder extends LockableImpl
            implements PropertySheetModelBuilder {

        public PropertySheetModel makeModel(PropertySheet sheet, PageState state) {
            DomainObjectPropertySheet s = (DomainObjectPropertySheet) sheet;
            return new DomainObjectPropertiesModel(
                    s.getObjectSelectionModel().getSelectedObject(state),
                    s.properties(),
                    state);
        }
    }

    // Abstract formatter which maintains a "default" string
    private static abstract class DefaultAttributeFormatter
            extends DomainService
            implements AttributeFormatter {

        private String m_default;

        public DefaultAttributeFormatter() {
            this((String)GlobalizationUtil.globalize("cms.ui.unknown").localize());
        }

        public DefaultAttributeFormatter(String def) {
            m_default = def;
        }

        public String getDefaultString() {
            return m_default;
        }
    }

    // A simple attribute formatter that calls get on the object with the
    // specified attribute
    private static class SimpleAttributeFormatter
            extends DefaultAttributeFormatter {

        public SimpleAttributeFormatter() {
            super();
        }

        public SimpleAttributeFormatter(String def) {
            super(def);
        }

        public String format(DomainObject obj, String attribute, PageState state) {
            if (obj == null) {
                return getDefaultString();
            }

            Object value = get(obj, attribute);

            if (value == null) {
                return getDefaultString();
            } else {
                return value.toString();
            }
        }
    }

    // A more advanced attribute formatter. Folows the path to the value
    // by following the names in the attribute string. For example, if
    // the string says "foo.bar.baz", the formatter will attempt to call
    // obj.get("foo").get("bar").get("baz");
    private static class RecursiveAttributeFormatter
            extends DefaultAttributeFormatter {

        public RecursiveAttributeFormatter() {
            super();
        }

        public RecursiveAttributeFormatter(String def) {
            super(def);
        }

        public String format(DomainObject obj, String attribute, PageState state) {
            if (obj == null) {
                return getDefaultString();
            }

            StringTokenizer tokenizer = new StringTokenizer(attribute, ".");
            String token = null;
            Object value = getDataObject(obj);

            while (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                // Null check
                value = ((DataObject) value).get(token);
                if (value == null) {
                    return getDefaultString();
                }
            }

            // Extract leaf value
            if (token == null || value == null) {
                return getDefaultString();
            }

            return value.toString();
        }
    }

   
}
