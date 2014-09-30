/*
 * Copyright (C) 2014 Jens Pelzetter
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
package com.arsdigita.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;

/**
 * This class acts as a facade for the {@link DocumentBuilderFactory} implementation configured
 * in {@link XMLConfig}. The current API in the Java Standard API does not allow to configure
 * the implementation of {@link DocumentBuilderFactory} to use at runtime. 
 * Therefore we are setting this facade as implementation to 
 * use via {@code META-INF/services/javax.parsers.DocumentBuilderFactory}. This class
 * uses {@link DocumentBuilderFactory#newInstance(java.lang.String, java.lang.ClassLoader)} to 
 * create an instance of the configured {@link DocumentBuilderFactory} implementation and delegates
 * all calls to it.
 * 
 * Note: For yet unknown reasons setting this class as DocumentBuilderFactory causes crazy errors.
 * Has the be investigated. In the meantime this class is not used.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class CCMDocumentBuilderFactory extends DocumentBuilderFactory {

    private static final Logger LOGGER = Logger.getLogger(CCMDocumentBuilderFactory.class);
    private final DocumentBuilderFactory factory;

    public CCMDocumentBuilderFactory() {
        super();

        final XMLConfig config = XMLConfig.getConfig();
        final String classname = config.getDOMBuilderFactoryClassname();

        if (classname == null || classname.isEmpty()) {
            //To make this class errorprone we check for null and empty string. Normally this
            //is not possible, but to be sure, we check the classname provided by XMLConfig and
            //fallback to the default value if the string is null or empty.
            LOGGER.warn("DOMBuilderFactory classname provided by XMLConfig is null or empty. "
                            + "This indicates a invalid configuration. Check your configuration! "
                            + "Falling back to default.");
            factory = DocumentBuilderFactory.newInstance(
                config.getDefaultDOMBuilderFactoryClassname(), null);
        } else {
            factory = DocumentBuilderFactory.newInstance(classname, null);
        }
    }

    @Override
    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return factory.newDocumentBuilder();
    }

    @Override
    public void setAttribute(final String name, 
                             final Object value) throws IllegalArgumentException {
        factory.setAttribute(name, value);
    }

    @Override
    public Object getAttribute(final String name) throws IllegalArgumentException {
        return factory.getAttribute(name);
    }

    @Override
    public void setFeature(final String name, 
                           final boolean value) throws ParserConfigurationException {
        factory.setFeature(name, value);
    }

    @Override
    public boolean getFeature(final String name) throws ParserConfigurationException {
        return factory.getFeature(name);
    }

}
