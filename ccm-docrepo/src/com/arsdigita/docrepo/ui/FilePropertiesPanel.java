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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.docrepo.File;
import com.arsdigita.domain.DataObjectNotFoundException;
//import com.arsdigita.web.Web;
import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.versioning.TransactionCollection;
import com.arsdigita.xml.Element;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * A simple custom bebop component that summarizes the properties of a
 * file in tabular form.
 *
 * @author StefanDeusch@computer.org, ddao@arsdigita.com
 * @version $Id: FilePropertiesPanel.java  pboy $
 */
class FilePropertiesPanel extends SimpleComponent implements DRConstants {

    private static final Logger s_log = Logger.getLogger
        (FilePropertiesPanel.class);

    @Override
    public void generateXML(PageState state, Element parent) {
        // Get file id.
        BigDecimal id = (BigDecimal) state.getValue(FILE_ID_PARAM);
        Element element = parent.newChildElement("docs:file-info", DOCS_XML_NS);

        try {
            // Retrieve resource properties.
            File file = new File(id);

            file.assertPrivilege(PrivilegeDescriptor.READ);

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
                (DRUtils.FileSize.formatFileSize(file.getSize(), state));

            Element typeElement =
                element.newChildElement("docs:type", DOCS_XML_NS);
            // Retrieve pretty name for a mime type.
            MimeType mimeType = MimeType.loadMimeType(file.getContentType());

            typeElement.setText(mimeType.getLabel());

            Element lastModifiedElement =
                element.newChildElement("docs:last-modified", DOCS_XML_NS);
            lastModifiedElement.setText
                (DRUtils.DateFormat.format(file.getLastModifiedDate()));

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
