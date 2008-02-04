/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder;

import com.arsdigita.formbuilder.PersistentLabel;
import com.arsdigita.formbuilder.PersistentWidget;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * This class is used to associate {@link com.arsdigita.formbuilder.PersistentLabel}s
 * with {@link com.arsdigita.formbuilder.PersistentWidget}s.
 *
 * This association facilitates the UI processes for
 * adding, deleting and moving widgets on forms.
 */
public class WidgetLabel extends PersistentLabel {
    private static final Logger s_log = Logger.getLogger( WidgetLabel.class );

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.WidgetLabel";

    public static String WIDGET = "widget";
    public static String WIDGET_LABEL = "widgetLabel";

    /**
     * Constructor. Creates a new widget label.
     */
    public WidgetLabel() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. Used by subclasses to specify
     * a different base data object type
     *
     * @param typeName the base data object type
     */
    public WidgetLabel(String typeName) {
        super(typeName);
    }

    /**
     * Constructor. Used by subclasses to specify
     * a different base data object type
     *
     * @param type the object type
     */
    public WidgetLabel(com.arsdigita.persistence.metadata.ObjectType type) {
        super(type);
    }

    /**
     * Constructor. Instantiates a widget label
     * from a previously retrieved data object.
     *
     * @param obj the data object
     */
    public WidgetLabel(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor. Instantiates a widget label
     * retrieving the the data object with the
     * specified id
     *
     * @param id the id of the widget label to retrieve
     */
    public WidgetLabel(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. Used by subclasses to retrieve
     * an existing widget label matching the
     * supplied oid.
     *
     * @param oid the oid of the widget label to retrieve
     */
    public WidgetLabel(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    /**
     * This method creates a new widget and initialises
     * all the required attributes
     *
     * @param widget the persistent widget associated with the label
     * @param label the text for the label
     */
    public static WidgetLabel create(PersistentWidget widget,
                                     String label) {
        WidgetLabel l = new WidgetLabel();

        l.setLabel(label);
        l.setWidget(widget);

        return l;
    }

    /**
     * Retrieves the widget label class associated with
     * the specified persistent widget.
     *
     * @param widget the persistent widget whose label to find
     * @throws com.arsdigita.domain.DataObjectNotFoundException if the
     * there is no label associated with the widget
     */
    public static WidgetLabel findByWidget(PersistentWidget widget) {
        DataObject obj = (DataObject)
            ( DomainServiceInterfaceExposer.get( widget, WIDGET_LABEL ) );
        if( null == obj ) return null;

        return (WidgetLabel) DomainObjectFactory.newInstance( obj );
    }

    /**
     * Sets the persistent widget associated with this label
     */
    public void setWidget(PersistentWidget widget) {
        set(WIDGET, widget);
    }

    /**
     * Retrieves the persistent widget object associated
     * with this label. Using this method is not very
     * desirable since it has to do one query to find out
     * the default domain class, and another to actually
     * retrieve the object.
     */
    public PersistentWidget getWidget() {
        // XXX how the fsck do I retireve an object without knowing is data type ?
        DataObject obj = (DataObject) get( WIDGET );
        return (PersistentWidget) DomainObjectFactory.newInstance( obj );
    }
}
