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
package com.arsdigita.persistence;

/**
 * DataEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: DataEvent.java 287 2005-02-22 00:29:02Z sskracic $
 **/

abstract class DataEvent {

    DataObjectImpl m_object;

    public DataEvent(DataObjectImpl object) {
        m_object = object;
    }

    void invoke(DataObserver observer) {
        if (DataObjectImpl.s_log.isDebugEnabled()) {
            DataObjectImpl.s_log.debug(this);
        }
        doInvoke(observer);
    }

    abstract void doInvoke(DataObserver observer);

    abstract String getName();

    final void schedule() { m_object.scheduleObserver(this); }

    final void fire() { m_object.fireObserver(this); }

    public String toString() {
        return "observer event: " + m_object + " " + getName();
    }

    public abstract boolean equals(Object o);
    public abstract int hashCode();
}

abstract class PropertyEvent extends DataEvent {

    String m_property;

    public PropertyEvent(DataObjectImpl object, String property) {
        super(object);
        m_property = property;
    }

    public int hashCode() {
        return m_object.hashCode() + m_property.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof PropertyEvent) {
            PropertyEvent other = (PropertyEvent) o;
            return this.m_object.equals(other.m_object) &&
                this.m_property.equals(other.m_property);
        }

        return false;
    }
}

class SetEvent extends PropertyEvent {

    private Object m_old;
    private Object m_new;

    public SetEvent(DataObjectImpl object, String property, Object oldValue,
                    Object newValue) {
        super(object, property);
        m_old = oldValue;
        m_new = newValue;
    }

    void doInvoke(DataObserver observer) {
        observer.set(m_object, m_property, m_old, m_new);
    }


    String getName() { return " set " + m_property; }
}

class AddEvent extends PropertyEvent {

    private DataObject m_value;

    public AddEvent(DataObjectImpl object, String property, DataObject value) {
        super(object, property);
        m_value = value;
    }

    void doInvoke(DataObserver observer) {
        observer.add(m_object, m_property, m_value);
    }

    String getName() { return " add " + m_property; }
}

class RemoveEvent extends PropertyEvent {

    private DataObject m_value;

    public RemoveEvent(DataObjectImpl object, String property,
                       DataObject value) {
        super(object, property);
        m_value = value;
    }

    void doInvoke(DataObserver observer) {
        observer.remove(m_object, m_property, m_value);
    }

    String getName() { return " remove " + m_property; }
}

class ClearEvent extends PropertyEvent {

    public ClearEvent(DataObjectImpl object, String property) {
        super(object, property);
    }

    void doInvoke(DataObserver observer) {
        observer.clear(m_object, m_property);
    }

    String getName() { return " clear " + m_property; }
}

interface BeforeEvent {
    // returns the corresponding after event
    ObjectDataEvent getAfter();
}

interface AfterEvent {
    // returns the corresponding before event
    ObjectDataEvent getBefore();
}

abstract class ObjectDataEvent extends DataEvent {

    public ObjectDataEvent(DataObjectImpl object) { super(object); }

    public int hashCode() { return m_object.hashCode(); }

    public boolean equals(Object o) {
        if (o instanceof ObjectDataEvent) {
            ObjectDataEvent other = (ObjectDataEvent) o;
            return this.m_object.equals(other.m_object) &&
                this.getClass().equals(other.getClass());
        }

        return false;
    }
}

class BeforeSaveEvent extends ObjectDataEvent implements BeforeEvent {

    public BeforeSaveEvent(DataObjectImpl object) {
        super(object);
    }

    public ObjectDataEvent getAfter() {
        return new AfterSaveEvent(m_object);
    }

    void doInvoke(DataObserver observer) {
        if (!m_object.isDeleted()) {
            observer.beforeSave(m_object);
        }
    }

    String getName() { return " before save"; }
}

class AfterSaveEvent extends ObjectDataEvent implements AfterEvent {

    public AfterSaveEvent(DataObjectImpl object) {
        super(object);
    }

    public ObjectDataEvent getBefore() {
        return new BeforeSaveEvent(m_object);
    }

    void doInvoke(DataObserver observer) {
        if (!m_object.isDeleted()) {
            observer.afterSave(m_object);
        }
    }

    String getName() { return " after save"; }
}

class BeforeDeleteEvent extends ObjectDataEvent implements BeforeEvent {

    public BeforeDeleteEvent(DataObjectImpl object) {
        super(object);
    }

    public ObjectDataEvent getAfter() {
        return new AfterDeleteEvent(m_object);
    }

    void doInvoke(DataObserver observer) {
        observer.beforeDelete(m_object);
    }

    String getName() { return " before delete"; }
}

class AfterDeleteEvent extends ObjectDataEvent implements AfterEvent {

    public AfterDeleteEvent(DataObjectImpl object) {
        super(object);
    }

    public ObjectDataEvent getBefore() {
        return new BeforeDeleteEvent(m_object);
    }

    void doInvoke(DataObserver observer) {
        observer.afterDelete(m_object);
    }

    String getName() { return " after delete"; }
}
