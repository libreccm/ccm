/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Copyright (C) 2003 Runtime Collective
 */

package com.arsdigita.cms.contenttypes.xmlfeed.listener;

import com.arsdigita.cms.contenttypes.xmlfeed.XMLFeed;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;



import com.arsdigita.web.ParameterMap;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.cms.FileAsset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

/**
 * <p>
 * The <code>RetrieveListener</code> forms part of the query form required by
 * the {@link XmlFeed Xmlfeed} content type. That class will create and add an
 * instance of this class to all query forms it creates.
 * </p>
 *
 * <p>
 * The process listener in this class handles the retrieval of the external
 * XML feed and transforms it with the {@link XmlFeed.getXsl XSL} supplied in
 * the XmlFeed object.
 * </p>
 *
 * @see XmlFeed
 * @author <a href="mailto:miles@runtime-collective.com">Miles Barr</a>
 * @since 05-03-2003
 * @version $Id: RetrieveListener.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class RetrieveListener implements FormProcessListener{

    private static final Logger s_log = Logger.getLogger(RetrieveListener.class);

    // ===== Constants ======================================================= //
    
    /** The key that the results are stored in the request under. */
    public static final String REQUEST_RESULTS_KEY = "XML Feed Results";
    
    private XMLFeed m_feed;

    // ===== Constructors ==================================================== //
    
    public RetrieveListener(XMLFeed feed) {
        m_feed = feed;
    }
    
    
    // ===== Data Access Method ============================================== //
    
    /** Get the XmlFeed domain object associated with this listener. */
    public XMLFeed getXMLFeed() {
        return m_feed;
    }

    
    /**
     * <p>
     * The process method of this listener brings together three main actions:
     * </p>
     *
     * <ol>
     *   <li>The retrival of the external XML.
     *   <li>Transforming the XML with the feed's XSL file.
     *   <li>Updating the display component.
     * </ol>
     *
     * @param e a <code>FormSectionEvent</code> value
     * @exception FormProcessException if an error occurs
     */
    public void process(FormSectionEvent e) throws FormProcessException {
        // Step 1: Retrive the XML
        PageState state = e.getPageState();
        FormData data   = e.getFormData();

        // Retrieve feed from parent class.
        XMLFeed xmlFeed = getXMLFeed();
        
        ParameterMap params = new ParameterMap();

        // Add all fields in the form as query parameters for the URL.
        //      Set fields = data.keySet();
        for (Iterator it = xmlFeed.getFormFieldNames(); it.hasNext(); ) {
            String field = (String) it.next();
            String value = (String) data.get(field);
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("Addng parameter " + field + " -> " + value);
            }
            params.setParameter(field.trim(), value.trim());
        }
        String query = xmlFeed.getURL() + params.toString();
        if (s_log.isDebugEnabled()) {
            s_log.info("Final query is " + query);
        }
        

        StringWriter errors = new StringWriter();
        PrintWriter writer = new PrintWriter(errors);

        writer.println("Errors:");

        // Get the XML.
        InputStream content = getContent(query, writer);

        if (null == content) {
            state.getRequest().setAttribute(REQUEST_RESULTS_KEY, 
                                            errors.toString());
        } else {
            // Step 2: Transform the XML.
          
            // Prepare the XSL file for the transformation process.
            // NOTE: Should we make this a variable to be set in enterprise.init?
            byte[] xslFile = null;
            FileAsset file = xmlFeed.getXSLFile();
            if (file != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    file.writeBytes(os);
                    os.flush();
                } catch (IOException ex) {
                    throw new UncheckedWrapperException("cannot write file", ex);
                }
                s_log.debug("Got XSL file as byte array");
                xslFile = os.toByteArray();
                s_log.debug("XSL is " + new String(xslFile));
                //   Do the transformation.
                String result = transform(xslFile, content);
                s_log.debug("Result is " + result);
                // Store the results in the request.
                state.getRequest().setAttribute(REQUEST_RESULTS_KEY, result);
            }
        }
    }


    /**
     * This method contacts the remote server, submits the query and returns
     * an <code>InputStream</code> containing the XML.
     *
     * @param query The URL query that points to the XML.
     * @param writer The object to print error messages to.
     * @return The inputstream or null if there was an error.
     */
    private InputStream getContent(String query, PrintWriter writer) {
        try {
            URL url = new URL(query);
            URLConnection connection = url.openConnection();
            connection.connect();

            return new BufferedInputStream(connection.getInputStream());
        }
        catch (MalformedURLException e) {
            writer.print("Query string: ");
            writer.print(query);
            writer.println(" is not valid.");

            return null;
        }
        catch (IOException e) {
            writer.println("Cannot contact remote server.");

            return null;
        }
    }


    /**
     * Transforms the XML with the supplied XSL.
     *
     * @param xsl A <code>File</code> object that represents the XSL file.
     * @param xml An <code>InputStream</code> that contains the XML.
     * @returns The transformed XML.
     * @throws FormProcessException if the transformer cannot be loaded or
     *   there is a problem during the transformation.
     */
    private String transform(byte[] xslFile, InputStream xml)
        throws FormProcessException {

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(xslFile);
            
            // 1. Instantiate a TransformerFactory.
            TransformerFactory tFactory = TransformerFactory.newInstance();

            // 2. Use the TransformerFactory to process the stylesheet Source and
            //    generate a Transformer.
            Transformer transformer =
                tFactory.newTransformer(new StreamSource(is));

            // 3. Use the Transformer to transform an XML Source and send the
            //    output to a Result object.
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult();
            result.setWriter(writer);
            transformer.transform(new StreamSource(xml), result);

            return writer.toString();
        }
        catch (TransformerConfigurationException e) {
            throw new FormProcessException("Cannot create XSL transformer.", e);
        }
        catch (TransformerException e) {
            throw new FormProcessException("Error during XSL transform.", e);
        }
    }
}
