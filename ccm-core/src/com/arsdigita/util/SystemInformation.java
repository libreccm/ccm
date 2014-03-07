/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.util;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author Jens Pelzetter
 */
public class SystemInformation extends AbstractConfig { //implements Lockable {

    private Parameter sysInfoParam = new StringParameter(
        "ccm.systeminformation",
        Parameter.REQUIRED,
        "version::2.x.y; appname::LibreCCM; apphomepage::http://www.libreccm.org;");

    private Map<String, String> systemInformation;// = new HashMap<String, String>();
    //private boolean locked = false;
    /**
     * The one and only instance of this class
     */
    private static SystemInformation INSTANCE;// = new SystemInformation();

    public SystemInformation() {
        register(sysInfoParam);

        loadInfo();
    }

    /**
     * @return The instance of this class.
     */
    public static SystemInformation getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SystemInformation();
            INSTANCE.load();
        }

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
    final public String get(String key) throws IllegalArgumentException {
        if (systemInformation == null) {
            loadMap();
        }

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Parameter key must not be null or empty.");
        }
        return systemInformation.get(key);
    }

    /**
     * Get iterator of this map.
     *
     * @return iterator of map
     */
    final public Iterator<Map.Entry<String, String>> iterator() {
        if (systemInformation == null) {
            loadMap();
        }

        return (systemInformation.entrySet()).iterator();
    }

    /**
     *
     * @return
     */
    final public boolean isEmpty() {
        if (systemInformation == null) {
            loadMap();
        } 
            
        return systemInformation.isEmpty();
        
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
    private void loadMap() {
        systemInformation = new HashMap<String, String>();

        final String[] tokens = ((String) get(sysInfoParam)).split(";");
        for (String token : tokens) {
            processToken(token);
        }
    }

    private void processToken(final String token) {
        final String[] parts = token.split("::");
        if (2 == parts.length) {
            systemInformation.put(parts[0].trim(), parts[1].trim());
        }
    }

}
