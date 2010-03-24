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
package com.arsdigita.formbuilder.installer;

import com.arsdigita.formbuilder.BebopObjectType;
import com.arsdigita.formbuilder.MetaObject;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.kernel.BaseInitializer;
import java.util.Iterator;
import java.util.List;

import com.arsdigita.formbuilder.PersistentDataQuery;
import com.arsdigita.formbuilder.PersistentComponent;

/**
 * Initializer
 *
 * @author <a href="mailto:berrange@redhat.com">Daniel Berrange</a>
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */
public class Initializer extends BaseInitializer {

    public static String WIDGET_TYPES = "widgetTypes";
    public static String PROCESS_LISTENER_TYPES = "processListenerTypes";
    public static String DATA_QUERIES = "dataQueries";
    private Configuration m_conf = new Configuration();

    public Initializer() throws InitializationException {
        m_conf.initParameter(WIDGET_TYPES,
                "The persistent widget types",
                List.class);
        m_conf.initParameter(PROCESS_LISTENER_TYPES,
                "The persistent process listener types",
                List.class);
        m_conf.initParameter(DATA_QUERIES,
                "The queries for the data driven select box",
                List.class);
    }

    /* Quasimodo: BEGIN */
    /**
     * Initializer
     *
     * A new Initializer, which doesn't use the Configuration and the enterprise
     * init. This initializer can be called during DomainInitEvents, so it is
     * ready for the new Initializer framework. Configuration will be read from
     * parameters (for now - maybe there's wil be a better way in the future).
     *
     * @throws InitializationException
     */
    public Initializer(List widgets, List processListeners, List dataQueries) throws InitializationException {
        TransactionContext txn = SessionManager.getSession().getTransactionContext();
        txn.beginTxn();

        // Load widgets, processListeners and dataQueries from parameters
        loadMetaObjects(widgets, PersistentComponent.class);
        loadMetaObjects(processListeners, PersistentProcessListener.class);
        loadDataQueries(dataQueries);

        txn.commitTxn();
    }

    /**
     * Returns the configuration object used by this initializer.
     **/
    public Configuration getConfiguration() {
        return m_conf;
    }

    /**
     * Called on startup. Note. As you can not find a call
     * to this method in enterprise.ini, this method
     * may appear to execute mysteriously.
     * However, the process that runs through enterprise.ini
     * automitically calls the startup() method of any
     * class that implements com.arsdigita.util.initializer.Initializer
     * present in enterprise.ini
     *
     **/
    protected void doStartup() {

        TransactionContext txn = SessionManager.getSession().getTransactionContext();
        txn.beginTxn();

        List widgets = (List) m_conf.getParameter(WIDGET_TYPES);
        loadMetaObjects(widgets, PersistentComponent.class);

        List listeners = (List) m_conf.getParameter(PROCESS_LISTENER_TYPES);
        loadMetaObjects(listeners, PersistentProcessListener.class);

        List queries = (List) m_conf.getParameter(DATA_QUERIES);
        loadDataQueries(queries);

        txn.commitTxn();
    }

    /**
     * Called on shutdown. It's probably not a good idea to depend on this
     * being called.
     **/
    protected void doShutdown() {
    }

    public BebopObjectType getObjectType(String name,
            Class type) {
        BebopObjectType objectType = null;
        try {
            objectType = BebopObjectType.findByClass(name,
                    type);
        } catch (DataObjectNotFoundException ex) {
            objectType = BebopObjectType.create(name,
                    type);
            objectType.save();
        }
        return objectType;
    }

    protected void loadMetaObjects(List objects, Class type)
            throws InitializationException {

        // If the objects list is not null, load the object list into the database
        if (objects != null) {

            // XXX we don't yet delete types which are no longer in the list
            Iterator objects_i = objects.iterator();
            while (objects_i.hasNext()) {
                List object = (List) objects_i.next();

                String appName = (String) object.get(0);
                String prettyName = (String) object.get(1);
                String prettyPlural = (String) object.get(2);
                String className = (String) object.get(3);
                String propertiesForm = (String) object.get(4);

                try {
                    Class.forName(className);
                } catch (ClassNotFoundException ex) {
                    throw new InitializationException("cannot find class " + className);
                }
                try {
                    Class.forName(propertiesForm);
                } catch (ClassNotFoundException ex) {
                    throw new InitializationException("cannot find class " + propertiesForm);
                }

                try {
                    MetaObject mo = MetaObject.findByClassName(getObjectType(appName,
                            type),
                            className);

                    mo.setPrettyName(prettyName);
                    mo.setPrettyPlural(prettyPlural);
                    mo.setWidgetClassName(className);
                    mo.setPropertiesFormName(propertiesForm);

                    mo.save();
                } catch (DataObjectNotFoundException ex) {
                    MetaObject mo = MetaObject.create(getObjectType(appName,
                            type),
                            prettyName,
                            prettyPlural,
                            className,
                            propertiesForm);
                    mo.save();
                }
            }
        }
    }

    protected void loadDataQueries(List objects)
            throws InitializationException {

        // If the objects list is not null, load the object list into the database
        if (objects != null) {

            // XXX we don't yet delete types which are no longer in the list
            Iterator objects_i = objects.iterator();
            while (objects_i.hasNext()) {
                List object = (List) objects_i.next();

                String appName = (String) object.get(0);
                String name = (String) object.get(1);
                String description = (String) object.get(2);

                try {
                    PersistentDataQuery q = PersistentDataQuery.findByName(getObjectType(appName,
                            PersistentDataQuery.class),
                            name);

                    q.setName(name);
                    q.setDescription(description);

                    q.save();
                } catch (DataObjectNotFoundException ex) {
                    PersistentDataQuery q = PersistentDataQuery.create(getObjectType(appName,
                            PersistentDataQuery.class),
                            description, name);
                    q.save();
                }
            }
        }
    }
}
