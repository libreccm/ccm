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
package com.arsdigita.bebop;

import java.util.Iterator;
import javax.servlet.ServletException;

import com.arsdigita.xml.Element;


/**
 * <p>
 * Provides a convenient method of creating components that are an
 * aggregation of other components and that should have the public
 * interface of a component (rather than that of a container, form,
 * and so on).  The methods of the Component interface are delegated to a
 * Container object, which is a SimpleContainer by default.  The
 * methods of the Container interface are present with protected
 * access.  This allows subclasses of CompoundComponent to alter the
 * structure of the component while preserving the simple Component
 * interface to users of the class.</p>
 *
 * <p>Users of this class should subclass CompoundComponent and add
 * components in the subclass' constructor, remembering to
 * first call super() or super( Container ). Note that the
 * super( Container ) method will use the passed Container in place
 * of the SimpleContainer to hold the added components.</p>
 *
 * @author Oliver Stewart 
 **/
public class CompoundComponent extends Completable
    implements Component {

    private Container m_container;


    /**
     * Creates a new compound component.
     **/
    public CompoundComponent() {
        m_container = new SimpleContainer();
    }

    /**
     * Creates a new compound component, using the specified container
     * to hold added components.
     *
     * @param container container to hold added components
     **/
    public CompoundComponent( Container container ) {
        m_container = container;
    }

    /**
     * Return the Container used to hold added components.
     *
     * @return Container object used to hold components
     **/
    protected Container getContainer() {
        return m_container;
    }

    /**
     * Adds a component to the container.
     *
     * @param c the component to add
     * @pre c != null
     * @see com.arsdigita.bebop.Container#add( Component )
     **/
    protected void add( Component c ) {
        m_container.add( c );
    }

    /**
     * Adds a component with the specified layout
     * constraints to the container.
     *
     * @param c the component to add to the container
     * @param constraints layout constraints (a
     * bitwise OR of static ints in the particular layout)
     * @see com.arsdigita.bebop.Container#add( Component, int )
     *
     * @pre c != null
     **/
    protected void add( Component c, int constraints ) {
        m_container.add( c, constraints );
    }

    /**
     * Returns <code>true</code> if this list contains the specified element.
     * More formally, returns
     * <code>true</code> if and only if this list contains at least
     * one element e such that (o==null ? e==null : o.equals(e)).
     * <P>
     * This method returns <code>true</code>only if the object has been
     * directly added to the container. If the container contains another
     * container that contains this object, this method returns
     * <code>false</code>.
     *
     * @param  o element whose presence in the container is to be tested
     * @return <code>true</code> if the container contains the specified
     * object directly; <code>false</code> otherwise.
     * @pre o != null
     * @see com.arsdigita.bebop.Container#contains( Object )
     **/
    protected boolean contains( Object o ) {
        return m_container.contains( o );
    }

    /**
     *  Gets the component
     * at the specified position. Each call to the add method increments
     * the index. Since the user has no control over the index of added
     * components (other than counting each call to the add method),
     * this method should be used in conjunction with indexOf.
     *
     * @param index the index of the item to be retrieved from this
     * container
     *
     * @return the component at the specified position in this container.
     *
     * @pre index >= 0 && index < size()
     * @post return != null
     **/
    protected Component get(int index) {
        return m_container.get( index );
    }

    /**
     * 
     *
     * @param c the component to search for
     *
     * @return the index in this list of the first occurrence of
     * the specified element, or -1 if this list does not contain this
     * element.
     *
     * @pre c != null
     * @post contains(c) implies (return >= 0 && return < size())
     * @post ! contains(c) implies return == -1
     * @see com.arsdigita.bebop.Container#indexOf( Component )
     **/
    protected int indexOf( Component c ) {
        return m_container.indexOf( c );
    }

    /**
     * Returns <code>true</code> if the container contains no components.
     *
     * @return <code>true</code> if the container contains no components;
     * <code>false</code> otherwise.
     * @post return == ( size() == 0 )
     * @see com.arsdigita.bebop.Container#isEmpty()
     **/
    protected boolean isEmpty() {
        return m_container.isEmpty();
    }

    /**
     * Returns the number of elements in the container. This method
     * does not recursively count the components indirectly contained
     * in the container.
     *
     * @return the number of components directly in this container.
     * @post size() >= 0
     * @see com.arsdigita.bebop.Container#size()
     **/
    protected int size() {
        return m_container.size();
    }

    /**
     * @see com.arsdigita.bebop.Component#children()
     **/
    public Iterator children() {
        return m_container.children();
    }

    /**
     * @see com.arsdigita.bebop.Component#generateXML( PageState, Element )
     **/
    public void generateXML( PageState state, Element parent ) {
        if (isVisible(state)) {
            m_container.generateXML( state, parent );
        }
    }

    /**
     * @see com.arsdigita.bebop.Component#getClassAttr()
     **/
    public String getClassAttr() {
        return m_container.getClassAttr();
    }

    /**
     * @see com.arsdigita.bebop.Component#getIdAttr()
     **/
    public String getIdAttr() {
        return m_container.getIdAttr();
    }

    /**
     * @see com.arsdigita.bebop.Component#getKey()
     **/
    public String getKey() {
        return m_container.getKey();
    }

    /**
     * @see com.arsdigita.bebop.Component#getStyleAttr()
     **/
    public String getStyleAttr() {
        return m_container.getStyleAttr();
    }

    /**
     * @see com.arsdigita.bebop.Component#isVisible( PageState )
     **/
    public boolean isVisible( PageState state ) {
        return state.isVisible( this );
    }

    /**
     * @see com.arsdigita.bebop.Component#register( Form, FormModel )
     **/
    public void register( Form f, FormModel m ) {
        m_container.register( f, m );
    }

    /**
     * @see com.arsdigita.bebop.Component#register( Page )
     **/
    public void register( Page p ) {
        m_container.register( p );
    }

    /**
     * @see com.arsdigita.bebop.Component#respond( PageState )
     **/
    public void respond( PageState state ) throws ServletException {
        if (isVisible(state)) {
            m_container.respond( state );
        }
    }

    /**
     * @see com.arsdigita.bebop.Component#setClassAttr( String )
     **/
    public void setClassAttr( String theClass ) {
        m_container.setClassAttr( theClass );
    }

    /**
     * @see com.arsdigita.bebop.Component#setIdAttr( String )
     **/
    public void setIdAttr( String id ) {
        m_container.setIdAttr( id );
    }

    /**
     * @see com.arsdigita.bebop.Component#setKey( String )
     **/
    public Component setKey( String key ) {
        return m_container.setKey( key );
    }

    /**
     * @see com.arsdigita.bebop.Component#setStyleAttr( String )
     **/
    public void setStyleAttr( String style ) {
        m_container.setStyleAttr( style );
    }

    /**
     * @see com.arsdigita.bebop.Component#setVisible( PageState, boolean )
     **/
    public void setVisible( PageState state, boolean v ) {
        state.setVisible( this, v );
    }

    /**
     * @see com.arsdigita.bebop.Component#lock()
     **/
    public void lock() {
        m_container.lock();
    }

    /**
     * @see com.arsdigita.bebop.Component#isLocked()
     **/
    public boolean isLocked() {
        return m_container.isLocked();
    }
}
