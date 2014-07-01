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
package com.arsdigita.cms.workflow;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * This class represents a task type in the CMS system. The task type has
 * attributes that impact on the behaviour of tasks of that type
 *
 *
 * @author chris.gilbert at westsussex.gov.uk
 * @version $Id: $
 */
public class CMSTaskType extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.workflow.CMSTaskType";
    private static Map s_taskURLGeneratorCache = new HashMap();
    // pdl attribute names
    public static final String ID = "taskTypeID";
    public static final String NAME = "name";
    public static final String DEFAULT_URL_GENERATOR_CLASS =
            "defaultUrlGeneratorClass";
    public static final String PRIVILEGE = "privilege";
    public static final String URL_GENERATORS = "generators";
    private static final Logger s_log = Logger.getLogger(CMSTaskType.class);
    // known task types
    public static final Integer AUTHOR = new Integer(1);
    public static final Integer EDIT = new Integer(2);
    public static final Integer DEPLOY = new Integer(3);
    // cache task type domain objects to save unnecessary db lookups
    public static Map<Integer, CMSTaskType> s_taskTypes = new HashMap<Integer, CMSTaskType>();
    private static Map<String, Integer> s_taskNameMap = new HashMap<String, Integer>();

    static {
        s_taskNameMap.put("Author", AUTHOR);
        s_taskNameMap.put("Edit", EDIT);
        s_taskNameMap.put("Deploy",DEPLOY);
    }
    
    public static CMSTaskType retrieve(Integer id) {

        if (id == null) {
            throw new IllegalArgumentException("Parameter id must not be null");
        }

        CMSTaskType type = s_taskTypes.get(id);
        if (type == null) {
            type = new CMSTaskType(id);
            s_taskTypes.put(id, type);
        }
        return type;

    }

    public static CMSTaskType retrieveByName(String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Parameter name must not be null or empty");
        }

        Integer id = s_taskNameMap.get(name);
        if (id == null) {
            for (CMSTaskType type : s_taskTypes.values()) {
                if (!s_taskNameMap.containsKey(type.getName())) {
                    s_taskNameMap.put(type.getName(), type.getID());
                }
                if (name.equals(type.getName())) {
                    id = type.getID();
                }
            }
        }
        return retrieve(id);

    }

    private CMSTaskType(Integer id) {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public CMSTaskType(DataObject obj) {
        super(obj);
    }

    /**
     * create a message object and associate it with the current application
     * instance
     *
     */
    public CMSTaskType() {
        super(BASE_DATA_OBJECT_TYPE);
        setID();

    }

    public Integer getID() {
        return ((Integer) get(ID));
    }

    /**
     * allocate a unique integer to the message
     *
     */
    private void setID() {
        DataCollection allTypes =
                SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        allTypes.addOrder(ID + " desc");
        Integer id = new Integer(1);
        if (allTypes.next()) {
            CMSTaskType taskType =
                    (CMSTaskType) DomainObjectFactory.newInstance(
                    allTypes.getDataObject());
            int currentMaxID = taskType.getID().intValue();
            id = new Integer(currentMaxID + 1);
            allTypes.close();
        }
        set(ID, id);
    }

    public String getName() {
        return (String) get(NAME);
    }

    public PrivilegeDescriptor getPrivilege() {
        return PrivilegeDescriptor.get((String) get(PRIVILEGE));
    }

    private TaskURLGenerator getDefaultTaskURLGenerator() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String generatorClassName = (String) get(DEFAULT_URL_GENERATOR_CLASS);
        Class URLGenerator = Class.forName(generatorClassName);
        return (TaskURLGenerator) URLGenerator.newInstance();

    }

    public TaskURLGenerator getURLGenerator(String event, ContentItem item) {
        String key = getID() + " " + event + " " + item.getContentType().getID();
        s_log.debug("looking up url generator for key " + key);
        TaskURLGenerator generator = (TaskURLGenerator) s_taskURLGeneratorCache.get(key);
        if (generator == null) {
            s_log.debug("no generator found in cache");
            DataAssociationCursor generators = ((DataAssociation) get(URL_GENERATORS)).cursor();
            generators.addEqualsFilter(TaskEventURLGenerator.EVENT, event);
            generators.addEqualsFilter(TaskEventURLGenerator.CONTENT_TYPE + "." + ContentType.ID, item.getContentType().getID());
            try {

                while (generators.next()) {
                    s_log.debug("specific generator found for " + event + " event on task type " + getName() + " for content type " + item.getContentType().getName());
                    // generator class available for this specific event and this specific content type
                    generator = ((TaskEventURLGenerator) DomainObjectFactory.newInstance(generators.getDataObject())).getGenerator();
                    generators.close();
                }
                if (generator == null) {
                    generators.reset();
                    generators.addEqualsFilter(TaskEventURLGenerator.EVENT, event);
                    generators.addEqualsFilter(TaskEventURLGenerator.CONTENT_TYPE, null);
                    while (generators.next()) {
                        s_log.debug("specific generator found for " + event + " event on task type " + getName() + " for any content type");
                        // generator class available for this specific event
                        generator = ((TaskEventURLGenerator) DomainObjectFactory.newInstance(generators.getDataObject())).getGenerator();
                        generators.close();
                    }
                }

                if (generator == null) {

                    s_log.debug("no specific generator for " + event + " event on task type " + getName() + ". Revert to default");
                    // fall back on default
                    generator = getDefaultTaskURLGenerator();
                }
                s_taskURLGeneratorCache.put(key, generator);
            } catch (Exception e) {
                throw new UncheckedWrapperException("Unable to retrieve a URL generator for event " + event + " on task type " + getID() + " ", e);

            }
        }

        return generator;
    }

    /**
     *
     * Should be used when UI is implemented for managing task types. If a
     * specific TaskURLGenerator is added or removed for an event, then the
     * cached TaskURLGenerator needs to be removed
     *
     * @param event
     */
    public void clearCachedEntry(String event) {
        String key = getID() + " " + event;
        s_taskURLGeneratorCache.remove(key);

    }
}
