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
package com.arsdigita.globalization;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * <p>
 * Extends java.util.ResourceBundle to have it do two things:
 * </p>
 * <p>
 *   <ul>
 *     <li>
 *       If there is a PropertyResourceBundle with the same name as this one
 *       load it into this one.
 *     </li>
 *     <li>
 *       Load any Resources from the database maintained MessageCatalog that
 *       have the same resource bundle name as this one.
 *     </li>
 *   </ul>
 * </p>
 *
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 * @version $Id: MixedResourceBundle.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class MixedResourceBundle extends java.util.ResourceBundle {

    private static final Logger s_cat =
        Logger.getLogger(MixedResourceBundle.class.getName());

    private Map m_contents = null;
    private Date m_lastLoaded = null;
    private Date m_lastModified = null;

    /**
     * <p>
     * Implement ResourceBundle.handleGetObject(). Reloads if contents have
     * changed.
     * </p>
     *
     * @param key The key to the object to retrieve.
     *
     * @return Object the object retrieved.
     */
    public final Object handleGetObject(String key) {
        if (m_contents == null || isModified()) {
            loadContents();
        }
        return m_contents.get(key);
    }

    /**
     * <p>
     * Implement ResourceBundle.getKeys(). Reloads if contents have changed.
     * </p>
     *
     * @return Enumeration of keys.
     */
    public Enumeration getKeys() {
        Enumeration keys = null;

        if (m_contents == null || isModified()) {
            loadContents();
        }

        if (parent == null) {
            keys = (new Hashtable(m_contents)).keys();
        } else {
            final Enumeration myKeys = (new Hashtable(m_contents)).keys();
            final Enumeration parentKeys = parent.getKeys();

            keys = new Enumeration() {
                    Object element = null;

                    public boolean hasMoreElements() {
                        if (element == null) {
                            nextElement();
                        }

                        return element != null;
                    }

                    public Object nextElement() {
                        Object rv = element;

                        if (myKeys.hasMoreElements()) {
                            element = myKeys.nextElement();
                        } else {
                            element = null;

                            while (element == null && parentKeys.hasMoreElements()) {
                                element = parentKeys.nextElement();

                                if (m_contents.containsKey(element)) {
                                    element = null;
                                }
                            }
                        }

                        return rv;
                    }
                };
        }

        return keys;
    }

    /**
     * <p>
     * Mix all static (PropertiesResourceBundle) and dynamic (MessageCatalog)
     * resources into one two dimensional array of key, value pairs.
     * </p>
     */
    private void loadContents() {
        Map contents = new HashMap();

        setLastLoaded();

        contents.putAll(loadPropertyResources());
        contents.putAll(loadMessageCatalogResources());

        setLastLoaded(lastModified());

        if (s_cat.isDebugEnabled()) {
            s_cat.debug("Loaded " + contents.size() + " resources.");
        }

        m_contents = contents;
    }

    /**
     * <p>
     * Look for a ".properties" file with the same name as the class instance
     * that extends this class (MixedResourceBundle). If we find one, then:
     * </p>
     * <p>
     *   <ul>
     *     <li>load it from disk</li>
     *     <li>return it as a map of key, value pairs.</li>
     *   </ul>
     * </p>
     *
     * @return Map A map representing all the entries from the properties file.
     */
    private Map loadPropertyResources() {
        Map contents = new HashMap();
        String className = getClassName().replace('.', '/');
        URL propertiesURL = getClass().getClassLoader().getResource(
                                                                    className + ".properties"
                                                                    );

        if (s_cat.isDebugEnabled()) {
            s_cat.debug("Starting to load static resources.");
        }

        try {
            Properties propertiesFile = new Properties();

            if (s_cat.isDebugEnabled()) {
                s_cat.debug(
                            "Searching for a properties file called " +
                            className + ".properties"
                            );
            }

            if (propertiesURL != null) {
                propertiesFile.load(propertiesURL.openStream());
            } else {
                throw new FileNotFoundException();
            }

            Enumeration properties = propertiesFile.propertyNames();

            String key;
            while (properties.hasMoreElements()) {
                key = (String) properties.nextElement();
                contents.put(key, propertiesFile.getProperty(key));
            }

            setLastModified(
                            new Date((new File(propertiesURL.getFile())).lastModified())
                            );
        } catch (FileNotFoundException fnfe) {
            // in case the file was removed
            setLastModified(new Date());
            java.io.File file = new java.io.File(className + ".properties");
            if (s_cat.isDebugEnabled()) {
                s_cat.debug("Did not find " + file.getAbsolutePath());
            }
        } catch (java.io.IOException ioe) {
            // in case the file was removed
            setLastModified(new Date());
            if (s_cat.isDebugEnabled()) {
                s_cat.debug("Unable to load " + className + ".properties");
            }
        }

        if (s_cat.isInfoEnabled()) {
            s_cat.info(
                       "Loaded " + contents.size() +
                       " resources from " + className + ".properties"
                       );
        }

        return contents;
    }

    /**
     * </p>
     * Look for a MessageCatalog with the same name as the class instance
     * that extends this class (MixedResourceBundle). If we find one, then:
     * </p>
     * <p>
     *   <ul>
     *     <li>load it</li>
     *     <li>return it as a map of key, value pairs.</li>
     *   </ul>
     * </p>
     *
     * @return Map A map representing all the entries from the MessageCatalog.
     */
    private Map loadMessageCatalogResources() {
        Map contents = null;
        String className = getClassName();
        String bundleName = getBundleName();
        Locale locale = getLocaleFromClassName();
        MessageCatalog catalog = null;

        if (s_cat.isDebugEnabled()) {
            s_cat.debug("Starting to load dynamic resources.");
        }

        // figure out which MessageCatalog constructor to call.
        try {
            if (locale == null) {
                // MessageCatalog for the current default Locale
                catalog = MessageCatalog.retrieve(bundleName);
            } else {
                // MessageCatalog for a particular Locale
                catalog = MessageCatalog.retrieve(bundleName, locale);
            }
        } catch (GlobalizationException ge) {
            // in case it was there before and it was deleted.
            setLastModified(new Date());
            return new HashMap();
        }

        contents = catalog.getMap();
        setLastModified(catalog.getLastModified());

        if (s_cat.isDebugEnabled()) {
            s_cat.debug(
                        "Loaded " + contents.size() +
                        " resources from the " + className + " MessageCatalog"
                        );
        }

        return contents;
    }

    private boolean isModified() {
        return lastModified().compareTo(lastLoaded()) > 0 ? true : false;
    }

    private Date lastLoaded() {
        return m_lastLoaded;
    }

    private void setLastLoaded() {
        m_lastLoaded = new Date();
    }

    private void setLastLoaded(Date date) {
        m_lastLoaded = date;
    }

    private Date lastModified() {
        return m_lastModified;
    }

    private void setLastModified(Date date) {
        if (
            m_lastModified == null ||
            m_lastModified.compareTo(date) < 0
            ) {
            m_lastModified = date;
        }
    }

    /**
     * <p>
     * Get the name of the instantiating class.
     * </p>
     *
     * @return String representing the instantiating class
     */
    private String getClassName() {
        if (s_cat.isDebugEnabled()) {
            s_cat.debug(
                        "This ResourceBundle is named " +
                        this.getClass().getName()
                        );
        }

        return this.getClass().getName();
    }

    /**
     * <p>
     * Parse the instantiating class' name and return the name of the
     * ResourceBundle/MessageCatalog that we are looking for. Note that we
     * assume that the class name will not contain any underscores ("_")
     * except for those that are used to denote the associated Locale. For
     * example, all the following are valid:
     * </p>
     * <p>
     *   <ul>
     *     <li>HelloWorldResources.class</li>
     *     <li>HelloWorldResources.properties</li>
     *     <li>HelloWorldResources_es.class</li>
     *     <li>HelloWorldResources_es.properties</li>
     *     <li>HelloWorldResources_es_CO.class</li>
     *     <li>HelloWorldResources_es_CO.properties</li>
     *   </ul>
     * </p>
     * <p>
     * while the follwing examples will cause this code to fail and are
     * therefore not allowed (by convention):
     * </p>
     * <p>
     *   <ul>
     *     <li>Hello_World_Resources.class</li>
     *     <li>Hello_World_Resources.properties</li>
     *     <li>Hello_World_Resources_es.class</li>
     *     <li>Hello_World_Resources_es.properties</li>
     *   </ul>
     * </p>
     *
     * @param className representing the instantiating class
     *
     * @return String representing the ResourceBundle/MessageCatalog name
     */
    private String getBundleName() {
        // split the className on "_" (underscores). Resource class names are
        // not allowed to have "_" in the name except for the specification of
        // the Locale, such as: HelloWorldResources_fr_CA
        StringTokenizer st = new StringTokenizer(getClassName(), "_");

        // if there is at least 1 token, return the first token (it should be
        // the ResourceBundle name).
        if (st.hasMoreTokens()) {
            return st.nextToken();
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * <p>
     * Parse the instantiating class's name and return the Locale associated
     * with the ResourceBundle/MessageCatalog we are looking for. Note that we
     * assume that the class name will not contain any underscores ("_")
     * except for those that are used to denote the associated Locale. For
     * example, all the following are valid:
     * </p>
     * <p>
     *   <ul>
     *     <li>HelloWorldResources.class</li>
     *     <li>HelloWorldResources.properties</li>
     *     <li>HelloWorldResources_es.class</li>
     *     <li>HelloWorldResources_es.properties</li>
     *     <li>HelloWorldResources_es_CO.class</li>
     *     <li>HelloWorldResources_es_CO.properties</li>
     *   </ul>
     * </p>
     * <p>
     * while the follwing examples will cause this code to fail and are
     * therefore not allowed (by convention):
     * </p>
     * <p>
     *   <ul>
     *     <li>Hello_World_Resources.class</li>
     *     <li>Hello_World_Resources.properties</li>
     *     <li>Hello_World_Resources_es.class</li>
     *     <li>Hello_World_Resources_es.properties</li>
     *   </ul>
     * </p>
     *
     * @param className representing the instantiating class
     *
     * @return Locale representing the locale associated with this
     *         ResourceBundle/MessageCatalog
     */
    private Locale getLocaleFromClassName() {
        // split the className on "_" (underscores). Resource class names are
        // not allowed to have "_" in the name except for the specification of
        // the Locale, such as: HelloWorldResources_fr_CA
        StringTokenizer st = new StringTokenizer(getClassName(), "_");

        // if there are at least 2 tokens, remove the first one and return the
        // rest of the tokens as a String[]
        Locale locale = null;
        int n_tokens = st.countTokens();
        if (n_tokens > 1) {
            st.nextToken();
            if (n_tokens == 2) {
                locale = new Locale(
                                    st.nextToken(),
                                    ""
                                    );
            } else if (n_tokens == 3) {
                locale = new Locale(
                                    st.nextToken(),
                                    st.nextToken()
                                    );
            } else if (n_tokens == 4) {
                locale = new Locale(
                                    st.nextToken(),
                                    st.nextToken(),
                                    st.nextToken()
                                    );
            }
        }

        return locale;
    }
}
