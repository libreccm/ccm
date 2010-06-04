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

import com.arsdigita.kernel.Kernel;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * <p>
 * Represents a key into a ResourceBundle, a target ResourceBundle, and
 * possibly an array of arguments to interpolate into the retrieved message
 * using the MessageFormat class.
 * </p>
 * <p>
 * This class should be used in any situation where the application needs to
 * output localizeable objects.
 * </p>
 *
 * @see java.text.MessageFormat
 * @see java.util.Locale
 * @see java.util.ResourceBundle
 *
 * @version $Id: GlobalizedMessage.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class GlobalizedMessage {

    private static final Logger s_cat =
        Logger.getLogger(GlobalizedMessage.class.getName());

    private String m_key = "";
    private String m_bundleName = "";
    private Object[] m_args = null;

    /**
     * <p>
     * Constructor. Takes in a key to be used to look up a message in the
     * ResourceBundle for the current running application. The base name of
     * the ResourceBundle to do the lookup in is retrieved from the
     * ApplicationContext.
     * </p>
     *
     * @param key The key to use to look up a message in the ResourceBundle.
     */
    public GlobalizedMessage(String key) {
        setKey(key);
        setBundleName();
    }

    /**
     * <p>
     * Constructor. Takes in a key to be used to look up a message in the
     * ResourceBundle specified.
     * </p>
     *
     * @param key The key to use to look up a message in the ResourceBundle.
     * @param bundleName The base name of the target ResourceBundle.
     */
    public GlobalizedMessage(String key, String bundleName) {
        setKey(key);
        setBundleName(bundleName);
    }

    /**
     * <p>
     * Constructor. Takes in a key to be used to look up a message in the
     * ResourceBundle for the current running application. The base name of
     * the ResourceBundle to do the lookup in is retrieved from the
     * ApplicationContext. Also takes in an Object[] of arguments to
     * interpolate into the retrieved message using the MessageFormat class.
     * </p>
     *
     * @param key The key to use to look up a message in the ResourceBundle.
     * @param args An Object[] of arguments to interpolate into the retrieved
     *             message.
     */
    public GlobalizedMessage(String key, Object[] args) {
        this(key);
        setArgs(args);
    }

    /**
     * <p>
     * Constructor. Takes in a key to be used to look up a message in the
     * ResourceBundle specified. Also takes in an Object[] of arguments to
     * interpolate into the retrieved message using the MessageFormat class.
     * </p>
     *
     * @param key The key to use to look up a message in the ResourceBundle.
     * @param bundleName The base name of the target ResourceBundle.
     * @param args An Object[] of arguments to interpolate into the retrieved
     *             message.
     */
    public GlobalizedMessage(String key, String bundleName, Object[] args) {
        this(key, bundleName);
        setArgs(args);
    }

    /**
     * <p>
     * Get the key for this GlobalizedMessage.
     * </p>
     *
     * @return String The key for this GlobalizedMessage.
     */
    public final String getKey() {
        return m_key;
    }

    /**
     * 
     * @param key
     */
    private void setKey(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("key cannot be empty.");
        }

        m_key = key;
    }

    private String getBundleName() {
        return m_bundleName;
    }

    private void setBundleName() {
        // setBundleName(ApplicationContext.get().getTargetBundle());
        setBundleName("com.arsdigita.dummy.DummyResources");
    }

    private void setBundleName(String bundleName) {
        if (bundleName == null || bundleName.length() == 0) {
            throw new IllegalArgumentException("bundleName cannot be empty.");
        }

        m_bundleName = bundleName;
    }

    private void setArgs(Object[] args) {
        m_args = args;
    }

    /**
     * <p>
     * <br>
     * Localize this message. If no message is found the key is returned as
     * the message. This is done so that developers or translators can see the
     * messages that still need localization.
     * </p>
     * <p>
     * Any arguments this message has are interpolated into it using the
     * java.text.MessageFormat class.
     * </p>
     *
     * @return Object Represents the localized version of this
     *                message. The reason this method returns an Object and
     *                not a String is because we might want to localize
     *                resources other than strings, such as icons or sound
     *                bites. Maybe this class should have been called
     *                GlobalizedObject?
     */
    public Object localize() {
        return localize(Kernel.getContext().getLocale());
    }

    /**
     * <p>
     * <br>
     * Localize this message according the specified request. If no message is
     * found the key is returned as the message. This is done so that
     * developers or translators can see the messages that still need
     * localization.
     * </p>
     * <p>
     * Any arguments this message has are interpolated into it using the
     * java.text.MessageFormat class.
     * </p>
     *
     * @param req The current running request.
     *
     * @return Object Represents the localized version of this
     *                message. The reason this method returns an Object and
     *                not a String is because we might want to localize
     *                resources other than strings, such as icons or sound
     *                bites. Maybe this class should have been called
     *                GlobalizedObject?
     */
    public Object localize(HttpServletRequest request) {
        return localize(Kernel.getContext().getLocale());
    }

    /**
     * <p>
     * Localize this message with the provided locale. If no message is
     * found the key is returned as the message. This is done so that
     * developers or translators can see the messages that still need
     * localization.
     * </p>
     * <p>
     * Any arguments this message has are interpolated into it using the
     * java.text.MessageFormat class.
     * </p>
     *
     * @param locale The locale to try to use to localize this message.
     *
     * @return Object Represents the localized version of this
     *                message. The reason this method returns an Object and
     *                not a String is because we might want to localize
     *                resources other than strings, such as icons or sound
     *                bites. Maybe this class should have been called
     *                GlobalizedObject?
     */
    public Object localize(Locale locale) {
        Object message = getKey();
        ResourceBundle rb = null;

        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null.");
        }

        try {
            rb = ResourceBundle.getBundle(getBundleName(), locale);
        } catch (MissingResourceException e) {
            if (s_cat.isDebugEnabled()) {
                s_cat.debug(
                            "ResourceBundle " + getBundleName() + " was not found."
                            );
            }
        }

        try {
            if (rb != null) {
                message = rb.getObject(getKey());
            } else {
                if (s_cat.isDebugEnabled()) {
                    s_cat.debug("No ResourceBundle available");
                }
            }
        } catch (MissingResourceException e2) {
            if (s_cat.isDebugEnabled()) {
                s_cat.debug(getKey() + " was not found in the ResourceBundle.");
            }
        }

        if (m_args != null && m_args.length > 0 && message instanceof String) {
            Object[] args = new Object[m_args.length];
            System.arraycopy(m_args, 0, args, 0, m_args.length);

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof GlobalizedMessage) {
                    args[i] = ((GlobalizedMessage) args[i]).localize(locale);
                }
            }

            message = MessageFormat.format((String) message, args);
        }

        return message;
    }

    /**
     * <p>
     * For debugging, not for localizing.
     * </p>
     *
     * @return The contents in String form for debugging.
     */
    public String toString() {
        return getBundleName() + "#" + getKey();
    }
}
