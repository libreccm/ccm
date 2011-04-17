/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.docrepo;

//import com.arsdigita.mimetypes.ImageMimeType;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.MimeTypeExtension;
//import com.arsdigita.mimetypes.TextMimeType;
//import com.arsdigita.mimetypes.MimeTypeInitializer;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xerces.parsers.DOMParser;
//import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.util.ArrayList;

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

    private static void updateMimeProperties(MimeType type,
                                             ArrayList props)
    {
        //  XXX  Has to be adopted to the curent CCM version!
/*      if (type instanceof TextMimeType)
            MimeTypeInitializer.setTextMimeProperties
                ((TextMimeType)type, props);
        else if (type instanceof ImageMimeType)
            MimeTypeInitializer.setImageMimeProperties
                ((ImageMimeType)type, props);
        else
            MimeTypeInitializer.setMimeProperties
                (type, props);

        type.save();   */
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
            try {
                me = new MimeTypeExtension
                    (new OID(MimeTypeExtension.MIME_TYPE, ext[j]));
            } catch(DataObjectNotFoundException e) {
             // me = new MimeTypeExtension();
             // me.setFileExtension(ext[j]);
                me = MimeTypeExtension.create(ext[j],MimeTypeExtension.MIME_TYPE);
            }
         // me.setMimeType(type.getMimeType());
            me.save();
        }
    }

}
