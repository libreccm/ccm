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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.cms.util.GlobalizationUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a template.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 *
 * @version $Id: Template.java 1942 2009-05-29 07:53:23Z terry $
 */
public class Template extends TextAsset {

    private static Logger s_log = Logger.getLogger(Template.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.Template";

    public static final String LABEL = "label";
    public static final String IS_PUBLISHABLE = "isPublishable";

    /**
     * The default mime-type for templates
     */
    public static final String JSP_MIME_TYPE = "text/x-jsp";
    public static final String XSL_MIME_TYPE = "text/xml";

    /**
     *  This is a map of all mime types with the key being the
     *  string representation of the mime type (such as 'text/x-jsp')
     *  and the value being a GlobalizedMessage that is the pretty name
     *  of the mime type.  This is somewhat of a hack since it is not
     *  using the actual label of the MimeType object but that label
     *  is not globalized and is not correctly for the XSL type.
     */
    public static final Map SUPPORTED_MIME_TYPES = new HashMap();

    static {
        SUPPORTED_MIME_TYPES.put(JSP_MIME_TYPE, 
                                 GlobalizationUtil.globalize("mime_type_jsp"));
        SUPPORTED_MIME_TYPES.put(XSL_MIME_TYPE, 
                                 GlobalizationUtil.globalize("mime_type_xsl"));
    }

    /**
     * Default constructor. This creates a new content item.
     */
    public Template()  {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Template(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Load a template with the given ID
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     */
    public Template(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Load a Template by encapsulating the given data object
     */
    public Template(DataObject obj)  {
        super(obj);
    }

    /**
     * Create a new Template with the given type
     */
    public Template(String type) {
        super(type);
        MimeType mime = MimeType.loadMimeType(JSP_MIME_TYPE);
        setMimeType(mime); // could be null

        // Pre-set the content type - is this right ???
        ContentType t = null;
         t = ContentType.findByAssociatedObjectType
             (BASE_DATA_OBJECT_TYPE);

        setContentType(t);
    }

    public void initialize() {
        super.initialize();
        if (get(IS_PUBLISHABLE) == null) {
            setPublishable(true);
        }
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Get the user-readable label for the template
     */
    public String getLabel() {
        return (String)get(LABEL);
    }

    public void setLabel(String label) {
        set(LABEL, label);
    }

    /**
     * Return the publically viewable name of this template
     */
    public String getDisplayName() {
        String result = getLabel();
        return (result != null) ? result : super.getDisplayName();
    }

    /**
     * Determine whether it is possible to write the items with this template
     * to the filesystem (as static HTML files).
     *
     * @return true if the items may be written to the filesystem, false if the
     *   template is designed for highly dynamic items which should always be
     *   served from the database.
     */
    public Boolean isPublishable() {
        return (Boolean)get(IS_PUBLISHABLE);
    }

    /**
     * Set whether it is possible to write the items with this template
     * to the filesystem (as static HTML files).
     *
     * @param isPublishable true if the items may be written to the filesystem, 
     *   false if the template is designed for highly dynamic items which should
     *   always be served from the database.
     */
    public void setPublishable(Boolean isPublishable) {
        set(IS_PUBLISHABLE, isPublishable);
    }

    /**
     * Set whether it is possible to write the items with this template
     * to the filesystem (as static HTML files).
     *
     * @param isPublishable true if the items may be written to the filesystem, false if the
     *   template is designed for highly dynamic items which should always be
     *   served from the database.
     */
    public void setPublishable(boolean isPublishable) {
        set(IS_PUBLISHABLE, new Boolean(isPublishable));
    }

    /**
     * Return the path for the template. This will normally be just
     * the template's name with ".jsp" added
     *
     *
     * <p>
     * Note that the name of the root folder of the content section where
     * the item resides is not included in the path.
     *
     * @return the path from the item's root to the item
     */
    public String getPath() {
        String path = getPathNoJsp();
        // add .jsp if not already there (templates will already have .jsp)
        if (!path.endsWith(".jsp")) {
            path = path + ".jsp";
        }
        return path;
    }
}
