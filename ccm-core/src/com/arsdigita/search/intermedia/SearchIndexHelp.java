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
package com.arsdigita.search.intermedia;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainService;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;
import java.util.HashSet;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

// Support for Logging.
import org.apache.log4j.Logger;

/**
 * This class is now deprecated, use the generic search API
 * in the com.arsdigita.search package instead.
 *
 * @deprecated see the com.arsdigita.search package
 * @see com.arsdigita.search.intermedia.SearchIndexHelpCustomize
 **/

public class SearchIndexHelp extends DomainService {
    public static final String versionId = "$Id: SearchIndexHelp.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // Creates a s_logging category with name = to the full name of class
    private static final Logger s_log =
        Logger.getLogger( SearchIndexHelp.class.getName() );

    // For storing retrieved xml and raw content.
    private String m_xml;
    private byte[] m_raw;

    /**
     * A {@link Searchable} object may return this in its
     * <code>getSearchXMLContent</code> or <code>getSearchRawContent</code>
     * in order to use the helper
     */
    public static final String USE_INDEX_HELP = "use_SearchIndexHelp";

    // for storing list of fields to retrieve
    private class FieldInfo {
        String name;
        String code; // "XML" or "RAW"

        FieldInfo(String n, String c) {
            name = n;
            code = c;
        }
    }

    /**
     * Retrieve content that should be indexed for object obj and
     * any composite objects.  If present, the SearchIndexHelpCustomize
     * interface is used to determine which fields should be indexed and
     * how they should be indexed, otherwise the default behavior is to
     * index all text fields as an XML document.
     */
    public void retrieveContent(DomainObject obj) {
        HashSet found = new HashSet();
        // Get any XML recursively.  Store raw as its found.
        ByteArrayOutputStream raw = new ByteArrayOutputStream();
        Element el = getXMLe(obj, found, raw);
        m_xml = "";  // in case no XML content found or error
        if (el != null) {
            // Some XML content was returned
            try {
                m_xml = (new Document(el)).toString();
                m_xml = stripHeader(m_xml);
            } catch (Exception e) {
                s_log.error("Error when creating document to index.");
                e.printStackTrace();
            }
        }
        m_raw = raw.toByteArray();
    }


    /***
     * If present, strip off the <?xml version="1.0" encoding="UTF-8"?>
     * part from the front of the document.  The interMedia examples don't
     * have that.
     ***/
    private String stripHeader(String xml) {
        final String HEAD_PATTERN = "<?xml version=\"1.0\" encoding=\"";
        if (xml.startsWith(HEAD_PATTERN)) {
            int head_end = xml.indexOf('>');
            return xml.substring(head_end + 1);
        }
        else
            return xml;
    }


    /***
     * Get content to be indexed for object and composite
     * objects.  Content is in either xml or raw format.
     ***/
    private Element getXMLe(DomainObject obj, HashSet found, ByteArrayOutputStream raw) {
        // flag this object found so its not processed again
        found.add(obj);
        // Get attribute fields to index
        List fields;
        if (obj instanceof SearchIndexHelpCustomize) {
            // User specifying how fields indexed
            fields = getIndexHelpFields(obj);
        } else {
            // Default, index all fields using xml format
            fields = getObjectAttributeFields(obj);
        }
        // process attributes
        boolean hasXML = false;
        OID oid = obj.getOID();
        ObjectType ot = oid.getObjectType();
        Element top_el = new Element(ot.getName());
        Iterator it = fields.iterator();
        while (it.hasNext()) {
            FieldInfo fi = (FieldInfo)it.next();
            String name = fi.name;
            String code = fi.code;  // XML or RAW
            Object value = get(obj, name);   // was obj.pubGet(name);
            if (code.equals("XML")) {
                hasXML = true;
                Element attr_el = new Element(name);
                if (value != null) {
                    attr_el.setText(value.toString());
                }
                top_el.addContent(attr_el);
            } else {
                // must be raw, append to raw content field
                if (raw.size() > 0) {
                    raw.write((int) ' ');  // append a space
                }
                // convert raw content to string, then byte[].
                // There may be a more efficient way to do this
                if(value != null) {
                    byte[] value_b = value.toString().getBytes();
                    raw.write(value_b, 0, value_b.length);
                }
            }
        }
        // Get Composite objects
        it = ot.getProperties();
        while (it.hasNext()) {
            Property prop = (Property)it.next();
            if (prop.isAttribute()) continue;
            // Set flag if is a composite association
            boolean isComposition = prop.isComponent();
            String rr_name = prop.getName();
            // System.err.println("Role reference " + rr_name +
            //                    " composition=" + isComposition +
            //                    ".  Getting...");
            s_log.debug("Retrieving role reference '" + rr_name + "' for '" + obj + "'");
            Object rr_da =  get(obj, rr_name); // was obj.pubGet(rr_name);
            if (rr_da == null) {
                // System.err.println("null");
                continue;
            }
            DataAssociation da;
            try {
                da = (DataAssociation) rr_da;
            } catch (Exception e) {
                // Not a DataAssociation.  Ignore it.
                // System.err.println("Gave error when casting.");
                continue;
            }
            // System.err.println(rr_da + " class=" + rr_da.getClass());
            DataObject dataObject;
            DomainObject domainObject;
            // Get cursor of objects in this association
            DataAssociationCursor da_cursor = da.getDataAssociationCursor();
            while (da_cursor.next()) {
                if (!isComposition) {
                    // Not a composition, need to check for link.  TO DO...
                    s_log.debug("Found non-composition: " + rr_name);
                    continue;
                }
                dataObject = da_cursor.getDataObject();
                // System.err.println("DATA OBJECT TYPE: " + dataObject.getObjectType().getQualifiedName());
                domainObject = DomainObjectFactory.newInstance(dataObject);
                // Only process if this domainObject not already found
                if (!found.contains(domainObject)) {
                    // Call this method recursively to get objects in it
                    Element objEl = getXMLe(domainObject, found, raw);
                    if (objEl != null) {
                        hasXML = true;
                        top_el.addContent(objEl);
                    }
                }
            }
        }
        // All done, return null if no xml content indexed
        if (hasXML)
            return top_el;
        else
            return null;
    }


    /***
     * Get field names from object attributes
     ***/
    private ArrayList getObjectAttributeFields(DomainObject obj) {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        // Get field names from object attributes
        ArrayList fieldInfo = new ArrayList();
        OID oid = obj.getOID();
        ObjectType ot = oid.getObjectType();
        Iterator iter = ot.getProperties();
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            if (prop.isRole()) continue;
            DataType dt = prop.getType();
            // only index character data
            if (dt.equals(root.getPrimitiveType("Character")) ||
                dt.equals(root.getPrimitiveType("Clob")) ||
                dt.equals(root.getPrimitiveType("String"))) {
                String name = prop.getName();
                // don't include objectType field or defaultDomainClass
                if (!name.equals("objectType") &&
                    !name.equals("defaultDomainClass")) {
                    fieldInfo.add(new FieldInfo(name, "XML"));
                }
            }
        }
        return fieldInfo;
    }


    /***
     * Get field names and indexing method specified by developer
     * Done if field names Specified by searchIndexHelpFields
     * function (SearchIndexHelpCustomize interface).
     ***/
    private ArrayList getIndexHelpFields(DomainObject obj) {
        ArrayList fieldInfo = new ArrayList();
        final String errMsg = "Invalid field information returned by" +
            " SearchIndexHelpFields must have name with 'XML' or 'RAW' appended...";
        // Get list of all the fields.  Must have XML or RAW suffix
        String fieldList[] = ((SearchIndexHelpCustomize)obj).searchIndexHelpFields();
        for (int i = 0; i < fieldList.length; i++) {
            String nameAndCode = fieldList[i];  // e.g. titleXML or titleRAW
            int len = nameAndCode.length();
            String name, code;
            try {
                name = nameAndCode.substring(0, len-3);  // e.g. title
                code = nameAndCode.substring(len-3);    // e.g. XML or RAW
            } catch (IndexOutOfBoundsException e) {
                s_log.error(errMsg + nameAndCode);
                continue;
            }
            // Make sure field name and code valid
            if (name.length() == 0 || (!code.equals("XML") &&
                                       !code.equals("RAW"))) {
                s_log.error(errMsg + nameAndCode);
                continue;
            }
            // Store fieldInfo
            fieldInfo.add(new FieldInfo(name, code));
        }
        return fieldInfo;
    }


    /**
     * Returns the XML content to be indexed.
     */
    public String xmlContent() {
        return m_xml;
    }

    /**
     * Returns the Raw content to be indexed.
     */
    public byte[] rawContent() {
        return m_raw;
    }
}
