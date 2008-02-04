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
 */

package com.arsdigita.london.navigation.ui.portlet;

import com.arsdigita.london.navigation.portlet.ObjectListPortlet;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.portal.PortletConfigFormSection;

import com.arsdigita.categorization.Category;
import com.arsdigita.portal.Portlet;
import com.arsdigita.kernel.ACSObject;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.StringUtils;
import com.arsdigita.kernel.ResourceType;

import com.arsdigita.london.util.ui.CategoryPicker;
import com.arsdigita.london.util.ui.ApplicationCategoryPicker;

import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.common.Path;

import java.util.Iterator;
import java.util.Map;
import java.util.TooManyListenersException;

public class ObjectListPortletEditor extends PortletConfigFormSection {
    
    private Widget m_baseObjectType;
    private Widget m_restrictedObjectType;

    private CategoryPicker m_filterCategory;

    private OptionGroup m_descendCategories;
    private OptionGroup m_checkPermissions;
    private OptionGroup m_excludeIndexObjects;
    private OptionGroup m_properties;

    private Widget m_count;
    private Widget m_order;
    private Widget m_attributes;
    
    public ObjectListPortletEditor(ResourceType resType,
                                   RequestLocal parentAppRL) {
        super(resType, parentAppRL);
    }
    
    public ObjectListPortletEditor(RequestLocal application) {
        super(application);
    }

    protected void addWidgets() {
        super.addWidgets();
        
        m_baseObjectType = buildBaseObjectTypeWidget(new StringParameter("baseObjectType"));
        m_baseObjectType.addValidationListener(new NotNullValidationListener());
        add(new Label("Base object type:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_baseObjectType);

        m_restrictedObjectType = buildRestrictedObjectTypeWidget(new StringParameter("restrictedObjectType"));
        add(new Label("Restricted object type:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_restrictedObjectType);
        
        m_filterCategory = new ApplicationCategoryPicker("filterCategory");
        add(new Label("Filter by category:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_filterCategory);
        
        m_descendCategories = new RadioGroup(new BooleanParameter("descendCategories"));
        m_descendCategories.addValidationListener(new NotNullValidationListener());
        m_descendCategories.addOption(new Option(Boolean.TRUE.toString(),
                                                "Yes"));
        m_descendCategories.addOption(new Option(Boolean.FALSE.toString(),
                                                "No"));
        add(new Label("Descend categories:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_descendCategories);
        
        m_checkPermissions = new RadioGroup(new BooleanParameter("checkPermissions"));
        m_checkPermissions.addValidationListener(new NotNullValidationListener());
        m_checkPermissions.addOption(new Option(Boolean.TRUE.toString(),
                                                "Yes"));
        m_checkPermissions.addOption(new Option(Boolean.FALSE.toString(),
                                                "No"));
        add(new Label("Check permissions:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_checkPermissions);
        
        m_excludeIndexObjects = new RadioGroup(new BooleanParameter("excludeIndexObjects"));
        m_excludeIndexObjects.addValidationListener(new NotNullValidationListener());
        m_excludeIndexObjects.addOption(new Option(Boolean.TRUE.toString(),
                                                   "Yes"));
        m_excludeIndexObjects.addOption(new Option(Boolean.FALSE.toString(),
                                                   "No"));
        add(new Label("Exclude index objects:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_excludeIndexObjects);
        
        m_count = new TextField(new IntegerParameter("count"));
        m_count.addValidationListener(new NotNullValidationListener());
        add(new Label("Max number of objects:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_count);
        
        m_order = buildOrderWidget(new StringParameter("order"));
        m_order.addValidationListener(new NotNullValidationListener());
        add(new Label("Sort ordering:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_order);

        m_attributes = buildAttributesWidget(new StringParameter("attributes"));
        add(new Label("Output attributes:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_attributes);

        m_properties = new MultipleSelect( "properties" );
        add( new Label( "Output properties:", Label.BOLD ), ColumnPanel.RIGHT );
        add( m_properties );
    }

    protected PrintListener getBaseObjectTypes() {
        return new ObjectTypePrintListener();
    }
    
    private Widget buildBaseObjectTypeWidget(ParameterModel param) {
        SingleSelect widget = new SingleSelect(param);
        try {
            widget.addPrintListener(getBaseObjectTypes());
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
        return widget;
    }

    protected PrintListener getRestrictedObjectTypes() {
        return new ObjectTypePrintListener();
    }
    
    private Widget buildRestrictedObjectTypeWidget(ParameterModel param) {
        SingleSelect widget = new SingleSelect(param);
        try {
            widget.addPrintListener(getRestrictedObjectTypes());
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
        return widget;
    }

    protected Widget buildOrderWidget(ParameterModel param) {
        return new TextField(param);
    }

    protected Widget buildAttributesWidget(ParameterModel param) {
        return new TextField(param);
    }

    private class ObjectTypePrintListener implements PrintListener {
        
        public void prepare(PrintEvent ev) {
            OptionGroup target = (OptionGroup)ev.getTarget();
            
            MetadataRoot root = MetadataRoot.getMetadataRoot();
            Iterator types = root.getObjectTypes().iterator();
            target.addOption(new Option(null, "--select one--"));
            while (types.hasNext()) {
                ObjectType type = (ObjectType)types.next();
                
                if (!type.isSubtypeOf(ACSObject.BASE_DATA_OBJECT_TYPE)) {
                    continue;
                }

                target.addOption(new Option(type.getQualifiedName(),
                                            type.getQualifiedName()));
            }
        }
    }

    protected void initWidgets(PageState state,
                               Portlet portlet)
        throws FormProcessException {
        super.initWidgets(state, portlet);

        Iterator properties = ObjectListPortlet.getRegisteredProperties();
        while( properties.hasNext() ) {
            Map.Entry entry = (Map.Entry) properties.next();
            String key = entry.getKey().toString();
            String title = ((Object[]) entry.getValue())[0].toString();

            m_properties.addOption( new Option( key, title ), state );
        }
        
        if (portlet != null) {
            ObjectListPortlet myportlet = (ObjectListPortlet)portlet;
            
            m_baseObjectType.setValue(state, myportlet.getBaseObjectType());
            m_restrictedObjectType.setValue(state, myportlet.getRestrictedObjectType());

            m_count.setValue(state, new Integer(myportlet.getCount()));
            m_order.setValue(state, myportlet.getOrdering());
            m_attributes.setValue(state, myportlet.getAttributes());
            
            Category cat = myportlet.getFilterCategory();

            m_filterCategory.setCategory(state, myportlet.getFilterCategory());

            m_descendCategories.setValue(state, new Boolean(myportlet.isDescendingCategories()));
            m_checkPermissions.setValue(state, new Boolean(myportlet.isCheckingPermissions()));
            m_excludeIndexObjects.setValue(state, new Boolean(myportlet.isExludingIndexObjects()));

            m_properties.setValue( state, myportlet.getProperties() );
        } else {
            m_baseObjectType.setValue(state, null);
            m_restrictedObjectType.setValue(state, null);

            m_count.setValue(state, new Integer(5));
            m_order.setValue(state, "id");
            m_attributes.setValue(state, null);

            m_filterCategory.setCategory(state, null);

            m_descendCategories.setValue(state, Boolean.FALSE);
            m_checkPermissions.setValue(state, Boolean.FALSE);
            m_excludeIndexObjects.setValue(state, Boolean.TRUE);
        }
    }
    
    protected void validateWidgets(PageState state,
                                   Portlet portlet) 
        throws FormProcessException {
        
        String baseTypeName = (String)m_baseObjectType.getValue(state);
        ObjectType baseType = MetadataRoot.getMetadataRoot().getObjectType(baseTypeName);
        if (baseType == null) {
            throw new FormProcessException(
                "Cannot find object type '" + baseTypeName + "'");
        }

        if (!baseType.isSubtypeOf(ACSObject.BASE_DATA_OBJECT_TYPE)) {
            throw new FormProcessException(
                "The type '" + baseTypeName +
                "' is not a subtype of '" + 
                ACSObject.BASE_DATA_OBJECT_TYPE + "'");
        }

        String specificTypeName = (String)m_restrictedObjectType.getValue(state);
        ObjectType specificType = null;
        if (specificTypeName != null &&
            !"".equals(specificTypeName)) {
            specificType = MetadataRoot.getMetadataRoot().getObjectType(specificTypeName);
            if (specificType == null) {
                throw new FormProcessException(
                    "Cannot find object type '" + specificTypeName + "'");
            }
            
            if (!specificType.isSubtypeOf(baseType)) {
                throw new FormProcessException(
                    "The type '" + specificTypeName + 
                    "' is not a subtype of '" + baseTypeName + "'");
            }
        }

        ObjectType type = specificType == null ?
            baseType : specificType;

        String order = (String)m_order.getValue(state);
        validateFields(type, order);

        String attr = (String)m_attributes.getValue(state);
        if (attr != null) {
            validateFields(type, attr);
        }
    }

    private void validateFields(ObjectType type,
                                String str) 
        throws FormProcessException {

        Root root = MetadataRoot.getMetadataRoot().getRoot();
        com.redhat.persistence.metadata.ObjectType objType = 
            root.getObjectType(type.getQualifiedName());

        String[] fields = StringUtils.split(str, ',');
        for (int i = 0 ; i < fields.length ; i++) {
            String field = fields[i].trim();
            if (field.endsWith(" desc")) {
                field = field.substring(0, field.length() - 5);
            } else if (field.endsWith(" asc")) {
                field = field.substring(0, field.length() - 4);
            }

            // FR: for composite-properties (eg 'links.linkTitle'),
            // we only check the first chunk of the property name.
            // Aplaws used to check the whole name, which was failing every time.
            if (field.indexOf('.') > -1) {
                field = field.substring(0, field.indexOf('.'));
            }
            
            if (!objType.exists(Path.get(field))) {
                throw new FormProcessException(
                    "The type '" + type.getQualifiedName() +
                    "' does not have a property '" + field + "'");
            }
        }
    }

    protected void processWidgets(PageState state,
                                  Portlet portlet)
        throws FormProcessException {
        super.processWidgets(state, portlet);
        
        ObjectListPortlet myportlet = (ObjectListPortlet)portlet;
        
        myportlet.setBaseObjectType((String)m_baseObjectType.getValue(state));
        myportlet.setRestrictedObjectType((String)m_restrictedObjectType.getValue(state));

        myportlet.setCount(((Integer)m_count.getValue(state)).intValue());
        myportlet.setOrdering((String)m_order.getValue(state));
        myportlet.setAttributes((String)m_attributes.getValue(state));

        myportlet.setFilterCategory(m_filterCategory.getCategory(state));
        myportlet.setDescendCategories(((Boolean)m_descendCategories.getValue(state)).booleanValue());
        myportlet.setCheckPermissions(((Boolean)m_checkPermissions.getValue(state)).booleanValue());
        myportlet.setExcludeIndexObjects(((Boolean)m_excludeIndexObjects.getValue(state)).booleanValue());
        myportlet.setProperties( (Object[]) m_properties.getValue( state ) );
    }
}
