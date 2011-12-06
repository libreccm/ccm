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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.contenttypes.ContentTypeInitializer;
import com.arsdigita.xml.XML;

import org.apache.log4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses XML file definition of content types and loads them to the
 * database.
 *
 * @see ContentTypeInitializer
 * @author Nobuko Asakai <nasakai@redhat.com> */
public class XMLContentTypeHandler extends DefaultHandler {

    private static Logger s_log =
            Logger.getLogger(XMLContentTypeHandler.class.getName());
    private ArrayList m_types = new ArrayList();
    private ContentTypeHelper m_type;
    private ContentType m_contentType;
    private AuthoringKit m_authoringKit;
    private int m_nextOrder;
    private boolean m_including;

    public List getContentTypes() {
        return m_types;
    }

    @Override
    public void startElement(String uri, String name,
            String qName, Attributes atts) {
        if (name.equals("content-types")) {
            s_log.debug("matched content-types");
        } else if (name.equals("content-type")) {
            s_log.debug("matched content-type");
            String parentType = atts.getValue("parentType");
            if (parentType != null) {
                m_type = new UDCTHelper();
                s_log.debug("Creating UDCT");
            } else {
                m_type = new ContentTypeHelperImpl();
                s_log.debug("Creating regular content type");
            }

            // for now, the bundle and key are not used for the content
            // type but they could easily be added if the content type
            // object is changed.
            m_type.setLabel(atts.getValue("label"));
            m_type.setLabelBundle(atts.getValue("labelBundle"));
            m_type.setLabelKey(atts.getValue("labelKey"));
            m_type.setDescription(atts.getValue("description"));
            m_type.setDescriptionBundle(atts.getValue("descriptionBundle"));
            m_type.setDescriptionKey(atts.getValue("descriptionKey"));
            m_type.setObjectType(atts.getValue("objectType"));
            m_type.setClassName(atts.getValue("classname"));

            String mode = atts.getValue("mode");
            if (mode != null && !mode.isEmpty()) {
                m_type.setMode(mode.trim());
            } else {
                m_type.setMode("default");
            }

            // UDCT stuff
            m_type.setParentType(parentType);
            m_type.setName(atts.getValue("name"));

            m_contentType = m_type.createType();
            m_types.add(m_contentType);
        } else if (name.equals("authoring-kit")) {
            if (!m_including) {
                s_log.debug("matched authoring-kit");
                if (atts.getValue("createComponent") != null) {
                    m_type.setCreateComponent(atts.getValue("createComponent"));
                }
                m_authoringKit = m_type.createAuthoringKit();
                m_nextOrder = 1;
            }
        } else if (name.equals("authoring-step")) {
            String label = atts.getValue("label");
            String labelKey = atts.getValue("labelKey");
            String labelBundle = atts.getValue("labelBundle");
            String description = atts.getValue("description");
            String descriptionKey = atts.getValue("descriptionKey");
            String descriptionBundle = atts.getValue("descriptionBundle");
            if (labelKey == null) {
                labelKey = label;
            }
            if (descriptionKey == null) {
                descriptionKey = description;
            }
            m_type.addAuthoringStep(labelKey, labelBundle,
                    descriptionKey, descriptionBundle,
                    atts.getValue("component"),
                    new BigDecimal(m_nextOrder++));
        } else if (name.equals("include")) {
            String file = atts.getValue("href");
            m_including = true;
            XML.parseResource(file, this);
            m_including = false;
        } else {
            s_log.error("None of the elements match! name: " + name
                    + " qName: " + qName + " URI: " + uri);
        }
    }

    public void endElement(String uri, String name,
            String qName, Attributes atts) {
        if (name.equals("content-type")) {
            // reset the helper
            m_contentType.save();
            m_authoringKit.save();
            m_type.saveType();
            m_type = null;
        }
    }
}
