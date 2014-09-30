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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * This class acts as a facade for the {@link SAXParserFactory} implementation configured
 * in {@link XMLConfig}. The current API in the Java Standard API does not allow to configure
 * the implementation of {@link SAXParserFactory} to use at runtime. 
 * Therefore we are setting this facade as implementation to 
 * use via {@code META-INF/services/javax.xml.transform.SAXParserFactory}. This class
 * uses {@link SAXParserFactory#newInstance(java.lang.String, java.lang.ClassLoader)} to 
 * create an instance of the configured {@link SAXParserFactory} implementation and delegates
 * all calls to it.
 * 
 *  Note: For yet unknown reasons setting this class as SAXBuilderFactory causes crazy errors.
 * Has the be investigated. In the meantime this class is not used.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class CCMSAXParserFactory extends SAXParserFactory {

    private static final Logger LOGGER = Logger.getLogger(CCMSAXParserFactory.class);
    private final SAXParserFactory factory;

    public CCMSAXParserFactory() {
        super();
        
        final XMLConfig config = XMLConfig.getConfig();
        final String classname = config.getSAXParserFactoryClassname();

        if (classname == null || classname.isEmpty()) {
            //To make this class errorprone we check for null and empty string. Normally this
            //is not possible, but to be sure, we check the classname provided by XMLConfig and
            //fallback to the default value if the string is null or empty.
            LOGGER.warn("SAXParserFactory classname provided by XMLConfig is null or empty. "
                            + "This indicates a invalid configuration. Check your configuration! "
                            + "Falling back to default.");
            factory = SAXParserFactory.newInstance(
                config.getDefaultSAXParserFactoryClassname(), null);
        } else {
            factory = SAXParserFactory.newInstance(classname, null);
        }
    }

    @Override
    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        return factory.newSAXParser();
    }

    @Override
    public void setFeature(final String name, final boolean value)
        throws ParserConfigurationException,
               SAXNotRecognizedException,
               SAXNotSupportedException {
        factory.setFeature(name, value);
    }

    @Override
    public boolean getFeature(final String name) throws ParserConfigurationException,
                                                  SAXNotRecognizedException,
                                                  SAXNotSupportedException {
        return factory.getFeature(name);
    }

}
