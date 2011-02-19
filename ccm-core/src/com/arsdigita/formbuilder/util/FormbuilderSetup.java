/*
 * Copyright (C) 2010 pboy (pboy@barkhof.uni-bremen.de) All Rights Reserved.
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

package com.arsdigita.formbuilder.util;


import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.BebopObjectType;
import com.arsdigita.formbuilder.MetaObject;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.formbuilder.PersistentDataQuery;
import com.arsdigita.formbuilder.PersistentComponent;
//__import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.ConfigError;

import java.util.Iterator;

import java.util.List;

import org.apache.log4j.Logger;


/**
 *
 * @author pb
 */
public class FormbuilderSetup {

    /** A logger instance.  */
    private static final Logger s_log = Logger.getLogger(FormbuilderSetup.class);
    
    /**
     * Used by package loader of an application which uses formbuilder 
     * infrastructure to load its set of widget descriptions, process listeners,
     * and dataqueries into database.
     */
    public void setup(List widgets,
                      List processListeners,
                      List dataQueries)       {

        // No transaction permitted here, handled by parent class!
        // (usually PackageLoader or a child thereof)
        loadMetaObjects(widgets, PersistentComponent.class);
        loadMetaObjects(processListeners, PersistentProcessListener.class);
        loadDataQueries(dataQueries);

    }

    public BebopObjectType getObjectType(String name,
                                         Class type) {
        BebopObjectType objectType = null;
        try {
            objectType = BebopObjectType.findByClass(name, type);
        } catch (DataObjectNotFoundException ex) {
            objectType = BebopObjectType.create(name, type);
            objectType.save();
        }
        return objectType;
    }

    protected void loadMetaObjects(List objects, Class type)
              throws ConfigError {

        // If the objects list is not null, load the object list into the database
        // pboy: Obviously, this is a Loader task.
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
                    throw new ConfigError("cannot find class " + className);
                }
                try {
                    Class.forName(propertiesForm);
                } catch (ClassNotFoundException ex) {
                    throw new ConfigError("cannot find class " + propertiesForm);
                }

                try {
                    MetaObject mo = MetaObject.findByClassName(
                                        getObjectType(appName, type),
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
            throws ConfigError {

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
                    PersistentDataQuery q = 
                            PersistentDataQuery.findByName(
                                    getObjectType(appName,
                                                  PersistentDataQuery.class),
                                    name);

                    q.setName(name);
                    q.setDescription(description);

                    q.save();
                } catch (DataObjectNotFoundException ex) {
                    PersistentDataQuery q = 
                            PersistentDataQuery.create(
                                    getObjectType(appName,
                                                  PersistentDataQuery.class),
                                    description, name);
                    q.save();
                }
            }
        }
    }

}
