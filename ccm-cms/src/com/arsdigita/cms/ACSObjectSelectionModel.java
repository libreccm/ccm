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
package com.arsdigita.cms;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;

/**
 * <span style="color:red">Deprecated</span>.
 * @deprecated Use {@link com.arsdigita.toolbox.ui.ACSObjectSelectionModel}
 *
 * <p>
 * Loads a subclass of an ACSObject from the database.
 * By default, uses a BigDecimal state parameter in order to
 * store and retrieve the item ID.
 *
 * <p>The <code>getSelectedKey(PageState state)</code> method will
 * return the BigDecimal ID of the currently selected object. This
 * method will return the ID even if the object with this ID does not
 * actually exist.</p>
 *
 * <p>The <code>getSelectedObject(PageState state)</code> method will
 * return the object whose ID is getSelectedKey(PageState state). If
 * the object does not actually exist, the method will return
 * null.</p>
 *
 * <p>Thus, it is possible to implement the following pattern:</p>
 *
 * <blockquote><pre><code>ACSObjectSelectionModel model = new ACSObjectSelectionModel(....);
 * // ....
 * ACSObject obj;
 * if(model.isSelected(state)) {
 *   obj = (ACSObject) model.getSelectedObject(state);
 *   if (obj == null) {
 *     // Create a new object
 *     obj = model.createObject(model.getSelectedKey(state));
 *     model.setSelectedObject(state, obj);
 *   }
 * }</code></pre></blockquote>
 *
 * <p>The <code>createObject</code> method is merely a convenience
 * method for instantiating the right subclass of
 * <code>ACSObject</code>.</p>
 *
 * <p><b>Advanced Usage</b>: The <code>ACSObjectSelectionModel</code>
 * is actually just a wrapper for a {@link SingleSelectionModel} which
 * maintains the currently selected object's ID as a {@link
 * BigDecimal}. By default, a new {@link
 * ParameterSingleSelectionModel} is wrapped in this way; however, any
 * {@link SingleSelectionModel} may be wrapped.  Thus, it becomes
 * possible to use the <code>ACSObjectSelectionModel</code> even if
 * the currently selected ID is not stored in a state parameter.</p>
 *
 * @version $Id: ACSObjectSelectionModel.java 287 2005-02-22 00:29:02Z sskracic $
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @see SingleSelectionModel
 * @see ParameterSingleSelectionModel
 *
 */
public class ACSObjectSelectionModel implements DomainObjectSelectionModel {

    private static Logger s_log =
        Logger.getLogger(ACSObjectSelectionModel.class);

    private RequestLocal m_object;
    private RequestLocal m_loaded;
    private Class m_javaClass;
    private Constructor m_constructor;
    private String m_objectType;
    private SingleSelectionModel m_model;

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>
     *
     * @param javaClass The name of the Java class which represents
     *    the object. Must be a subclass of {@link ACSObject}. In
     *    addition, the class must have a constructor with a single
     *    {@link OID} parameter.
     * @param objectType The name of the persistence metadata object type
     *    which represents the ACS object. In practice, will often be
     *    the same as the javaClass.
     * @param parameterName The name of the state parameter which will
     *    be used to store the object ID.
     * @deprecated
     */
    public ACSObjectSelectionModel(
                                   String javaClass, String objectType, String parameterName
                                   ) {
        this(javaClass, objectType, new BigDecimalParameter(parameterName));
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>
     *
     * @param javaClass The name of the Java class which represents
     *    the object. Must be a subclass of {@link ACSObject}. In
     *    addition, the class must have a constructor with a single
     *    {@link OID} parameter.
     * @param objectType The name of the persistence metadata object type
     *    which represents the ACS object. In practice, will often be
     *    the same as the javaClass.
     * @param parameter The state parameter which should be used to store
     *    the object ID
     */
    public ACSObjectSelectionModel(
                                   String javaClass, String objectType, BigDecimalParameter parameter
                                   ) {
        this(javaClass, objectType,
             new ParameterSingleSelectionModel(parameter));
    }

    /**
     * Construct a new <code>ACSObjectSelectionModel</code>
     *
     * @param javaClass The name of the Java class which represents
     *    the object. Must be a subclass of {@link ACSObject}. In
     *    addition, the class must have a constructor with a single
     *    {@link OID} parameter.
     * @param objectType The name of the persistence metadata object type
     *    which represents the ACS object. In practice, will often be
     *    the same as the javaClass.
     * @param model The {@link SingleSelectionModel} which will supply
     *    a {@link BigDecimal} ID of the currently selected object
     * @deprecated
     */
    public ACSObjectSelectionModel(
                                   String javaClass, String objectType, SingleSelectionModel model
                                   ) {
        m_object = new RequestLocal();
        m_loaded = new RequestLocal() {
                protected Object initialValue(PageState state) {
                    return Boolean.FALSE;
                }
            };

        try {
            m_javaClass = Class.forName(javaClass);
            m_constructor = m_javaClass.getConstructor (
                                                        new Class[]{OID.class}
                                                        );
        } catch (Exception e) {
            s_log.error("Problem loading class " + javaClass, e);
            throw new UncheckedWrapperException
                ("Problem loading class " + javaClass);
        }

        m_objectType = objectType;
        m_model = model;
    }

    /**
     * Set the ID of the current object
     *
     * @param state The page state
     * @param key A {@link BigDecimal} primary key for the object,
     *   or a String representation of a BigDecimal, such as "42".
     * @deprecated
     */
    public void setSelectedKey(PageState state, Object key) {
        // Support both strings and BigDecimals
        BigDecimal id = null;
        if(key != null) {
            id = new BigDecimal(key.toString());
        }

        m_object.set(state, null);
        m_loaded.set(state, Boolean.FALSE);
        m_model.setSelectedKey(state, id);
    }

    /**
     * Return the object which was selected and loaded from the database,
     * using the values supplied in the page state. May return null
     * if no object was selected, or if the item was not found.
     *
     * @param state The page state
     * @return The item domain object, or null if no item is
     *         selected.
     *
     * @deprecated
     */
    public DomainObject getSelectedObject(PageState state) {

        if(! isInitialized(state)) {
            m_loaded.set(state, Boolean.TRUE);

            BigDecimal id = (BigDecimal)getSelectedKey(state);

            if(id == null) {
                return null;
            }

            // Load the item from the database
            try {
                OID oid = new OID(m_objectType, id);
                ACSObject item = (ACSObject)m_constructor.newInstance(
                                                                      new OID[]{oid}
                                                                      );
                m_object.set(state, item);
                return item;
            } catch (Exception ex) {
                s_log.warn("ACSObject " + id + " not found.", ex);
                return null;
            }
        } else {
            return (ACSObject)m_object.get(state);
        }
    }

    /**
     * Select the specified object.
     *
     * @param state The page state
     * @param object The content item to set
     * @deprecated
     */
    public void setSelectedObject(PageState state, DomainObject object) {
        ACSObject item = (ACSObject)object;

        m_object.set(state, item);

        if(item == null) {
            m_loaded.set(state, Boolean.FALSE);
            m_model.setSelectedKey(state, null);
        } else {
            m_loaded.set(state, Boolean.TRUE);
            m_model.setSelectedKey(state, item.getID());
        }

        //fireStateChanged(state);
    }

    /**
     * Determine if the attempt to load the selected object has
     * been made yet. Child classes may use this method to
     * perform request-local initialization.
     *
     * @param state the page state
     * @return true if the attempt to load the selected object has
     *   already been made, false otherwise
     * @deprecated
     */
    public boolean isInitialized(PageState state) {
        return ((Boolean)m_loaded.get(state)).booleanValue();
    }

    /**
     * A utility function which creates a new object with the given ID.
     * Uses reflection to create the instance of the class supplied
     * in the constructor to this <code>ACSObjectSelectionModel</code>.
     *
     * @param id The id of the new item
     * @return The newly created item
     * @post return != null
     * @deprecated
     */
    public ACSObject createACSObject(BigDecimal id) throws ServletException {
        s_log.debug ("Creating a new object: " + id);

        try {
            ACSObject item = (ACSObject)m_javaClass.newInstance();
            s_log.info("XXXXXXXX creating new object with id " + item.getID() + ";;; id should be " + id);
            // TODO item.setID(id);
            return item;
        } catch (Exception e) {
            s_log.error("Error creating ACSObject with id = " + id, e);
            throw new ServletException(e);
        }
    }

    /**
     * @return the Class of the content items which are produced
     *         by this model
     * @deprecated
     */
    public Class getJavaClass() {
        return m_javaClass;
    }

    /**
     * @return The name of the object type of the
     *         content items which are produced by this model
     * @deprecated
     */
    public String getObjectType() {
        return m_objectType;
    }

    /**
     * @return the underlying {@link SingleSelectionModel} which
     *   maintains the ID of the selected object
     * @deprecated
     */
    public SingleSelectionModel getSingleSelectionModel() {
        return m_model;
    }

    ////////////////////////
    //
    // Passthrough methods

    /**
     * Return <code>true</code> if there is a selected object.
     *
     * @param state represents the state of the current request
     * @return <code>true</code> if there is a selected component.
     * @deprecated
     */
    public boolean isSelected(PageState state) {
        return m_model.isSelected(state);
    }

    /**
     * Return the key that identifies the selected object.
     *
     * @param state the current page state
     * @return the {@link BigDecimal} ID of the currently selected
     *   object, or null if no object is selected
     * @deprecated
     */
    public Object getSelectedKey(PageState state) {
        return m_model.getSelectedKey(state);
    }

    /**
     * Clear the selection.
     *
     * @param state the current page state.
     * @post ! isSelected(state)
     * @post ! isInitialized(state)
     * @deprecated
     */
    public void clearSelection(PageState state) {
        m_model.clearSelection(state);
        m_object.set(state, null);
        m_loaded.set(state, Boolean.FALSE);
    }

    /**
     * Add a change listener to the model. The listener's
     * <code>stateChanged</code> is called whenever the selected key changes.
     *
     * @param l a listener to notify when the selected key changes
     * @deprecated
     */
    public void addChangeListener(ChangeListener l) {
        m_model.addChangeListener(l);
    }

    /**
     * Remove a change listener from the model.
     *
     * @param l the listener to remove.
     * @deprecated
     */
    public void removeChangeListener(ChangeListener l) {
        m_model.removeChangeListener(l);
    }

    /**
     * Return the state parameter which will be used to keep track
     * of the currently selected key. Most likely, the parameter will
     * be a {@link BigDecimalParameter}.
     *
     * @return The state parameter which should be used to keep
     *         track of the ID of the currently selected object, or null
     *         if the ID is computed in some other way
     * @see SingleSelectionModel#getStateParameter()
     * @deprecated
     */
    public ParameterModel getStateParameter() {
        return m_model.getStateParameter();
    }
}
