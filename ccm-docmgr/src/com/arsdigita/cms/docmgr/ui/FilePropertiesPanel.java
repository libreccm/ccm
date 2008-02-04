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

package com.arsdigita.cms.docmgr.ui;

import java.math.BigDecimal;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.versioning.TransactionCollection;
import com.arsdigita.versioning.Versions;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

/**
 * A simple custom bebop component that summarizes the properties of a
 * file in tabular form.
 *
 * @author StefanDeusch@computer.org, ddao@arsdigita.com
 */
class FilePropertiesPanel extends SimpleComponent implements DMConstants {
    public static final String versionId =
        "$Id: FilePropertiesPanel.java,v 1.4 2004/12/09 14:46:39 pkopunec Exp $" +
        "$Author: pkopunec $" +
        "$DateTime: 2003/09/19 15:46:00 $";

    private static final Logger s_log = Logger.getLogger
        (FilePropertiesPanel.class);

    private FileInfoPropertiesPane m_parent;

    public FilePropertiesPanel(FileInfoPropertiesPane parent) {
        super();
        m_parent = parent;        
    }

    public void generateXML(PageState state, Element parent) {
        // Get doc id.
        BigDecimal id = (BigDecimal) state.getValue(m_parent.getFileIDParam());
        Element element = parent.newChildElement("docs:file-info", DOCS_XML_NS);

        try {
            // Retrieve resource properties.
            Document doc = new Document(id);
            s_log.debug("doc raw content: "+doc.getSearchRawContent());
            //s_log.debug("doc xml content: "+doc.getSearchXMLContent());

            Element titleElement =
                element.newChildElement("docs:title", DOCS_XML_NS);
            titleElement.setText(doc.getTitle());

            Element nameElement =
                element.newChildElement("docs:name", DOCS_XML_NS);
            nameElement.setText(doc.getName());

            Element descriptionElement =
                element.newChildElement("docs:description", DOCS_XML_NS);
            String description = doc.getDescription();
            if (description != null) {
                descriptionElement.setText(description);
            }

            Element sizeElement =
                element.newChildElement("docs:size", DOCS_XML_NS);
            sizeElement.setText
                (DMUtils.FileSize.formatFileSize(doc.getSize()));

            Element typeElement =
                element.newChildElement("docs:type", DOCS_XML_NS);
            // Retrieve pretty name for a mime type.
            typeElement.setText(doc.getPrettyMimeType());

            Element lastModifiedElement =
                element.newChildElement("docs:last-modified", DOCS_XML_NS);
            lastModifiedElement.setText
                (null != doc.getLastModifiedLocal() ? 
		 DMUtils.DateFormat.format(doc.getLastModifiedLocal()) :
		 "");

            Element revisionElement =
                element.newChildElement("docs:revision", DOCS_XML_NS);

            TransactionCollection tc =
                Versions.getTaggedTransactions(doc.getOID());
            long numRevs = tc.size();
            revisionElement.setText(numRevs + "");

            // Must allow for the possibility that not author is available.

            Element authorElement =
                element.newChildElement("docs:author", DOCS_XML_NS);
            authorElement.setText(doc.getImpliedAuthor());
            
            Element uriElement =
                element.newChildElement("docs:uri", DOCS_XML_NS);
            uriElement.setText(makeFileURL(doc, state));
            
            Element catsElement =
                element.newChildElement("docs:categories", DOCS_XML_NS);
            // Iterator cats = doc.getCategories();
            CategoryCollection cats = doc.getCategoryCollection();
            Category cat;
            Element catElement;
            while (cats.next()) {
                cat = cats.getCategory();
                catElement = catsElement.newChildElement("docs:category", DOCS_XML_NS);
                catElement.addAttribute("id", cat.getID().toString());
                catElement.addAttribute("name", cat.getName());
            }
        } catch (DataObjectNotFoundException exc) {
            Element notfoundElement =
                element.newChildElement("docs:notfound", DOCS_XML_NS);
        }
    }

    private static String makeFileURL(Document doc, PageState state) {
        final HttpServletRequest req = state.getRequest();

        final ParameterMap params = new ParameterMap();
        params.setParameter(FILE_ID_PARAM_NAME, doc.getID());

        return URL.here(req, "/download/" + doc.getName(), params).toString();
    }
}
