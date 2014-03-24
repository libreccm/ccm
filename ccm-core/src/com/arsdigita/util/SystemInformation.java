/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.util;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Provides the system name of the CCM Spin off (eg aplaws or ScientificCMS) and the version number.
 * It's primary use is to provide the theme engine with that information for display. It is
 * currently stored as a (configurable) property, but should be retrieved from the build system in
 * the future.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author Jens Pelzetter
 */
public class SystemInformation { //extends AbstractConfig { //implements Lockable {

//    private Parameter sysInfoParam = new StringParameter(
//        "ccm.systeminformation",
//        Parameter.REQUIRED,
//        "version::2.x.y; appname::LibreCCM; apphomepage::http://www.libreccm.org;");

    private final Map<String, String> sysInfo = new HashMap<String, String>();
    //private boolean locked = false;
    /**
     * The one and only instance of this class
     */
    private final static SystemInformation INSTANCE = new SystemInformation();

    public SystemInformation() {

        //register(sysInfoParam);
        //loadInfo();

        final Properties properties = new Properties();
        try {
            properties.load(ResourceManager.getInstance().getResourceAsStream("/WEB-INF/systeminformation.properties"));
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }
        for (String key : properties.stringPropertyNames()) {
            sysInfo.put(key, properties.getProperty(key));
        }

    }

    /**
     * @return The instance of this class.
     */
    public static SystemInformation getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new SystemInformation();
//            INSTANCE.load();
//        }

        return INSTANCE;
    }

//    /**
//     * Put system information into the map. If this instance is locked, this method throw an
//     * {@link AssertionError}.
//     *
//     * @param key   Key for the Map. Also used as attribute name during output.
//     * @param value Value
//     *
//     * @throws IllegalArgumentException if key or value is null or empty
//     */
//    final public void put(final String key, final String value) throws IllegalArgumentException {
//        if (key == null || key.isEmpty()) {
//            throw new IllegalArgumentException("Parameter key must not be null or empty.");
//        }
//        if (value == null || value.isEmpty()) {
//            throw new IllegalArgumentException("Parameter value must not be null or empty.");
//        }
//        // Test if instance is not locked
//        Assert.isUnlocked(this);
//        systemInformation.put(key, value);
//    }
    /**
     * Get system informations by key.
     *
     * @param key Key for the map
     *
     * @return value for key
     *
     * @throws IllegalArgumentException if key is null or empty
     */
    final public String get(final String key) throws IllegalArgumentException {
//        if (sysInfo == null) {
//            loadMap();
//        }

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Parameter key must not be null or empty.");
        }
        return sysInfo.get(key);
    }

    /**
     * Get iterator of this map.
     *
     * @return iterator of map
     */
    final public Iterator<Map.Entry<String, String>> iterator() {
//        if (sysInfo == null) {
//            loadMap();
//        }

        return sysInfo.entrySet().iterator();
    }

    /**
     *
     * @return
     */
    final public boolean isEmpty() {
//        if (sysInfo == null) {
//            loadMap();
//        }

        return sysInfo.isEmpty();

    }

    /**
     * Lock this instance to prevent further changes.
     */
//    final public void lock() {
//        locked = true;
//    }
//
//    /**
//     * Test, if this instance is locked.
//     *
//     * @return locked
//     */
//    final public boolean isLocked() {
//        return locked;
//    }
//    private void loadMap() {
//        sysInfo = new HashMap<String, String>();
//
//        final String[] tokens = ((String) get(sysInfoParam)).split(";");
//        for (String token : tokens) {
//            processToken(token);
//        }
//    }
//
//    private void processToken(final String token) {
//        final String[] parts = token.split("::");
//        if (2 == parts.length) {
//            sysInfo.put(parts[0].trim(), parts[1].trim());
//        }
//    }

}
