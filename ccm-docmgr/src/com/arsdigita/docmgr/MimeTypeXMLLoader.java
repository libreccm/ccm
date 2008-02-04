/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.docmgr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.MimeTypeExtension;
import com.arsdigita.util.StringUtils;

/**
 * Initializer helper class which loades more Mime types into the
 * database from an xml file.
 *
 * @author <a href="mailto:stefan@arsdigita.com">Stefan Deusch</a>
 */

public final class MimeTypeXMLLoader {

    // XML constants here

    private static final String XML_MIME_ROOT = "mimetypes";
    private static final String XML_MIME_NODE = "mimetype";
    private static final String XML_MIME_NAME = "name";
    private static final String XML_MIME_EXTENSION = "extension";
    private static final String XML_MIME_LABEL = "label";
    private static final String XML_MIME_CLASS = "class";


    /**
     * Parse the input stream and load the extracted XML source.
     *
     * @param is InputStream to read the document from
     */

    public static void parse(InputStream is)
        throws IOException,
               InvalidMimeTypeFormatException
    {
        InputSource inputSrc = new InputSource(is);
        DOMParser parser = new DOMParser();

        try {
            parser.parse(inputSrc);
        } catch (SAXException e) {
            throw new InvalidMimeTypeFormatException(e);
        }

        loadMimeTypes(parser.getDocument());
    }

    /**
     * Load MIME types from an XML configuration document into the
     * database.
     *
     * @param doc XML MimeType configuration document
     */

    private static void loadMimeTypes(org.w3c.dom.Document doc)
        throws InvalidMimeTypeFormatException
    {
        Element root = doc.getDocumentElement();
        if (root.getTagName().compareTo(XML_MIME_ROOT) != 0) {
            throw new InvalidMimeTypeFormatException
                ("Invalid MIME type document");
        }

        NodeList nodeList = root.getElementsByTagName(XML_MIME_NODE);
        for (int j=0; j < nodeList.getLength(); j++) {
            assertMimeType((Element)nodeList.item(j));
        }
    }

    /**
     * Checks whether the MIME type defined by element
     * is available in the database. If not, it is created.
     *
     * @param element DOM element decribing a MIME type
     */

    private static void assertMimeType(Element element) {

        // Get inline attributes

        String name = element.getAttribute(XML_MIME_NAME);
        String extensions = element.getAttribute(XML_MIME_EXTENSION);
        String label = element.getAttribute(XML_MIME_LABEL);
        String objectType = element.getAttribute(XML_MIME_CLASS);

        // If MIME class was not specified, set up the correct
        // default.

        if (null == objectType || "".equals(objectType) ) {
            objectType = "MimeType";
        }
        String javaClass = "com.arsdigita.mimetypes." + objectType;

        String objType = "com.arsdigita.cms." + objectType;

        // Set up an array of property values

        ArrayList props = new ArrayList(5);
        props.add(name);
        props.add(label);
        props.add(extensions);
        props.add(objectType);
        props.add("0");

        // Try to load the type.  If it doesn't exist in the database
        // we will create it.  If it does exist we update its
        // properties so that the database is in sync with the loaded
        // configuration file.

        MimeType type = MimeType.loadMimeType(name);

        if (null == type) {
            type = MimeType.createMimeType(name, javaClass, objType);
        } else {
            type.setLabel(label);
            type.setJavaClass(javaClass);
            type.setSpecificObjectType(objType);
        }

        updateMimeProperties(type, props);
        updateFileExtensions(type, extensions);
    }

    /**
     * Update MIME type properties.
     */

    private static void updateMimeProperties(MimeType type, ArrayList props) {
	
//         if (type instanceof TextMimeType)
//             MimeTypeInitializer.setTextMimeProperties
//                 ((TextMimeType)type, props);
//         else if (type instanceof ImageMimeType)
//             MimeTypeInitializer.setImageMimeProperties
//                 ((ImageMimeType)type, props);
//         else
//             MimeTypeInitializer.setMimeProperties
//                 (type, props);


	type.setLabel((String)props.get(1));
	type.setFileExtension((String)props.get(2));
	type.setSpecificObjectType((String)props.get(3));
        type.save();
    }

    /**
     * Update MIME type extensions list.
     */

    private static void updateFileExtensions(MimeType type,
                                             String extensions)
    {
        String [] ext = StringUtils.split(extensions, ',');

        for (int j = 0; j < ext.length; j++) {
            MimeTypeExtension me;
	    me = MimeTypeExtension.create(ext[j], type);
            me.save();
        }
    }

}
