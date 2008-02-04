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

package com.arsdigita.docmgr.ui;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.docmgr.File;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.versioning.TransactionCollection;
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
        "$Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/ui/FilePropertiesPanel.java#3 $" +
        "$Author: jparsons $" +
        "$DateTime: 2003/04/23 17:06:19 $";

    private static final Logger s_log = Logger.getLogger
        (FilePropertiesPanel.class);

    public void generateXML(PageState state, Element parent) {
        // Get file id.
        BigDecimal id = (BigDecimal) state.getValue(FILE_ID_PARAM);
        Element element = parent.newChildElement("docs:file-info", DOCS_XML_NS);

        try {
            // Retrieve resource properties.
            File file = new File(id);

            Element nameElement =
                element.newChildElement("docs:name", DOCS_XML_NS);
            nameElement.setText(file.getName());

            Element descriptionElement =
                element.newChildElement("docs:description", DOCS_XML_NS);
            String description = file.getDescription();
            if (description != null) {
                descriptionElement.setText(description);
            }

            Element sizeElement =
                element.newChildElement("docs:size", DOCS_XML_NS);
            sizeElement.setText
                (DMUtils.FileSize.formatFileSize(file.getSize(), state));

            Element typeElement =
                element.newChildElement("docs:type", DOCS_XML_NS);
            // Retrieve pretty name for a mime type.
            MimeType mimeType = MimeType.loadMimeType(file.getContentType());

            typeElement.setText(mimeType.getLabel());

            Element lastModifiedElement =
                element.newChildElement("docs:last-modified", DOCS_XML_NS);
            lastModifiedElement.setText
                (DMUtils.DateFormat.format(file.getLastModifiedDate()));

            Element revisionElement =
                element.newChildElement("docs:revision", DOCS_XML_NS);

            TransactionCollection tc =
                file.getTransactions();
            long numRevs = tc.size();
            revisionElement.setText(numRevs + "");

            // Must allow for the possibility that not author is available.

            Element authorElement =
                element.newChildElement("docs:author", DOCS_XML_NS);
            com.arsdigita.kernel.User author = file.getCreationUser();
            if (null != author) {
                authorElement.setText(author.getName());
            } else {
                authorElement.setText("Unknown");
            }

            Element uriElement =
                element.newChildElement("docs:uri", DOCS_XML_NS);
            uriElement.setText(makeFileURL(file, state));

        } catch (DataObjectNotFoundException exc) {
            Element notfoundElement =
                element.newChildElement("docs:notfound", DOCS_XML_NS);
        }
    }

    private static String makeFileURL(File file, PageState state) {
        final HttpServletRequest req = state.getRequest();

        final ParameterMap params = new ParameterMap();
        params.setParameter(FILE_ID_PARAM.getName(), file.getID());

        return URL.here(req, "/download/" + file.getName(), params).toString();
    }
}
