/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.xmlutil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.JDOMFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class DocImporter {

    public DocImporter(final boolean isValidating) {
        m_builder = new SAXBuilder(isValidating);
    }


    public void setJDOMFactory(JDOMFactory f) {
       m_builder.setFactory(f);
    }
    /**
     * This method takes a list of JDOM Elements that refer to some XML document.
     * The exact type of the element does not matter, so long as it has an attriube
     * named 'resource', which gives a path to some XML file.
     *
     * The method inputs each XML document and appends them to a list which is
     * returned to the caller.
     *
     * @param docRefs List of JDOM Elements referring to XML documents.
     * @return List of JDOM Documents.
     *
     * @throws JDOMException on parse error.
     *
     */
    public List getDocuments(List docRefs) throws JDOMException {
        List documents = new ArrayList( docRefs.size() );
        for (Iterator iterator = docRefs.iterator(); iterator.hasNext();) {
            Element fileRef = (Element) iterator.next();
            String resource = fileRef.getAttributeValue("resource");
            Document doc = getDocumentAsResource(resource);
            documents.add(doc);
        }
        return documents;
    }

    /**
     * Imports a document.
     *
     * @param resource The path to the resource. This must be on the classpath.
     *
     */
    public Document getDocumentAsResource(String resource) throws JDOMException {
        s_log.warn("Loading " + resource);
        InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
        if( null == is ) {
            throw new JDOMException("Couldn't find test definition file: " + resource);
        }
        Document doc = importDocument(is, resource);

        return doc;
    }

    public Document getDocumentAsFile(String filename) throws JDOMException {
        FileInputStream is = null;
        try {
            is = new FileInputStream(filename);
            Document doc = importDocument(is, filename);
            return doc;

        } catch (FileNotFoundException e) {
            throw new JDOMException("No such file: " + filename);
        } finally {
            if ( is != null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    s_log.error("Problem closing file: " + filename, e);
                }
            }
        }

    }


    private Document importDocument(InputStream is, String resource) throws JDOMException {
        Document doc;
        try {
            doc = m_builder.build(is);
        } catch(IOException e) {
            throw new JDOMException("Error loading document: " + resource, e);
        }

        logDocument(doc);
        return doc;
    }

    /**
     * Outputs the document to a log, if debugging is enabled.
     *
     * @param doc The document to output
     */
    private void logDocument(Document doc) {
        if ( s_log.isDebugEnabled() ) {
            XMLOutputter xmlOut = new XMLOutputter();
            StringWriter writer = new StringWriter();
            try {
                xmlOut.output(doc, writer);
                s_log.debug(writer.toString());
            } catch (IOException e) {
                s_log.error("Problem outputting document.", e);
            }
        }
    }
    private SAXBuilder m_builder;
    private static Logger s_log = Logger.getLogger(DocImporter.class);
}
