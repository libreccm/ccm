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
package com.arsdigita.mimetypes.converters;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;

/**
 * A utility class that provides a method for converting document formats.
 *
 * @author Jeff Teeters
 *
 * @version $Revision: #12 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class ConvertFormat {

    private static final Logger s_log = Logger.getLogger(ConvertFormat.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ConvertFormat";

    private ConvertFormat() {}

    /**
     * Converts a document to html format.  Uses INSO filtering that comes with
     * interMedia.  This allows converting Word, RTF and many other documents to
     * HTML.
     *
     * @param doc_in The document to be converted to html.
     *
     * @return The converted document or <code>null</code> if the conversion
     * could not be done.
     */
    public static String toHTML(byte [] doc_in) {
        // Save document in pre_convert_html table
        java.sql.Connection con = null;
        String returnValue = null;

        BigDecimal id = null;
        // Remove any previously stored entry in pre_convert_html table
        try {
            id = Sequences.getNextValue();
            try {
                new PreConvertHTML(new OID(PreConvertHTML.BASE_DATA_OBJECT_TYPE,
                                           id)).delete();
            } catch (DataObjectNotFoundException e) {
                // good, not found
            }

            // Store document in pre_convert_html and try to convert it.
            PreConvertHTML pre = new PreConvertHTML();
            pre.setId(id);
            pre.setContent(doc_in);
            pre.save();

            // Use interMedia ctx_doc.filter to convert to HTML.
            // See file cms/sql/oracle-se/convert-html.sql
            con = SessionManager.getSession().getConnection();
            CallableStatement funCall =
                con.prepareCall("{ ? = call convert_to_html(" + id + ")}");
            funCall.registerOutParameter(1, Types.VARCHAR);
            funCall.execute();
            returnValue = funCall.getString(1);
            funCall.close();
        } catch (SQLException ex) {
            s_log.error("PreConvertHTML.toHTML failed.", ex);
            return null;
        }

        if (returnValue != null && returnValue.length() > 0) {
            s_log.error("PreConvertHTML.toHTML was unable to convert " +
                        "document with id =" + id + ".  Perhaps its format " +
                        "is not supported.  Error message is: " + returnValue,
                        new Throwable());
        } else {
            try {
                PostConvertHTML pc = new PostConvertHTML
                    (new OID(PostConvertHTML.BASE_DATA_OBJECT_TYPE, id));
                String doc_out = pc.getContent();
                pc.delete();
                s_log.debug ("Read from postConvertHTML = " + doc_out);
                return doc_out;
            } catch (DataObjectNotFoundException ex) {
                s_log.error("PreConvertHTML.toHTML converted doc to html, " +
                            "but unable to retrieve it.  id=" + id, ex);
            }
        }
        return null;
    }
}
