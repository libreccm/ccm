/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class SystemInformation implements Lockable {

    private Map<String, String> systemInformation = new HashMap<String, String>();
    private boolean locked = false;
    /**
     * The one and only instance of this class
     */
    private static final SystemInformation INSTANCE = new SystemInformation();

    private SystemInformation() {
        // Nothing
    }

    /**
     * @return The instance of this class.
     */
    public static SystemInformation getInstance() {
        return SystemInformation.INSTANCE;
    }

    /**
     * Put system information into the map. If this instance is locked, this method
     * throw an {@link AssertionError}.
     * 
     * @param key Key for the Map. Also used as attribute name during output.
     * @param value Value
     * @throws IllegalArgumentException if key or value is null or empty
     */
    final public void put(String key, String value) throws IllegalArgumentException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Parameter key must not be null or empty.");
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Parameter value must not be null or empty.");
        }
        // Test if instance is not locked
        Assert.isUnlocked(this);
        systemInformation.put(key, value);
    }

    /**
     * Get system informations by key.
     * 
     * @param key Key for the map
     * @return value for key
     * @throws IllegalArgumentException if key is null or empty
     */
    final public String get(String key) throws IllegalArgumentException {
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
        return ((Set<Map.Entry<String, String>>) systemInformation.entrySet()).iterator();
    }
    
    /**
     * 
     * @return 
     */
    final public boolean isEmpty() {
        return systemInformation.isEmpty();
    }
    /**
     * Lock this instance to prevent further changes.
     */
    final public void lock() {
        locked = true;
    }

    /**
     * Test, if this instance is locked.
     * @return locked
     */
    final public boolean isLocked() {
        return locked;
    }
}
