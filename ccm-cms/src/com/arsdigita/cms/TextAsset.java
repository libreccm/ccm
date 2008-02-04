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
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;


/**
 * A Text object.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 *
 * @version $Revision: #14 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class TextAsset extends Asset {

    public static final String versionId = "$Id: TextAsset.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.TextAsset";

    public static final String CONTENT = "content";

    /**
     * Default constructor. This creates a new text asset.
     **/
    public TextAsset() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public TextAsset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>TextAsset.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public TextAsset(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public TextAsset(DataObject obj) {
        super(obj);
    }

    public TextAsset(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Reads a stream of character data into the content object.
     * Character data is always written to the object as UTF-8.
     *
     * @param reader A character input stream
     * @return the number of bytes read
     */

    //*****
    // NOTE: readText is no longer used (except in a test) because it
    // does no input validation.  Processing for loading files is now done
    // in TextPageBody.java.
    //*****
    public long readText(Reader reader) throws IOException {
        //this does not work since get(CONTENT) returns a null value
        // if the the TextAsset is new
        /*
          CLOB clob = (CLOB)get(CONTENT);

          long previousTotal = clob.length();

          DataOutputStream out = new DataOutputStream(clob.getAsciiOutputStream());
          Writer writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

          int size = clob.getBufferSize();
          char[] buffer = new char[size];
          int length = -1;

          while ((length = reader.read(buffer)) != -1) {
          writer.write(buffer, 0, length);
          }

          reader.close();
          writer.close();

          long total = out.size();
          return total;
        */

        StringWriter writer = new StringWriter();
        char[] buffer = new char[8];
        int length = -1;
        while ((length = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, length);
        }

        String content = writer.toString();
        setText(content);

        reader.close();
        writer.close();

        long total = content.length();
        return total;
    }

    /**
     * Writes character data from the content object to the specified
     * character output stream.
     *
     * @param writer The character output stream
     * @return the number of bytes written
     */
    public long writeText(Writer writer) throws IOException {

        // We don't know whether content is going to be a string or a lob
        // if it's a string, just write it;
        // if it's a lob, stream it out to avoid running out of memory

        String text = null;
        Object content = get(CONTENT);
        if(content == null) {
            return (long)0;
	}

	text = (String)content;
	writer.write(text);
	writer.close();
	return (long)text.length();
	
    }

    /**
     * Write the image asset content to a file.
     *
     * @param file      The file on the server to write to.
     */
    public void writeToFile(File file)
        throws IOException {
        FileOutputStream fs = new FileOutputStream(file);
        try {
            OutputStreamWriter writer = new OutputStreamWriter(fs);
            writeText(writer);

        } finally {
            if( null != fs ) {
                fs.close();
            }
        }

    }


    /**
     * Get the text content
     */
    public String getText() {

        Object content = get(CONTENT);
	return (String)content;

    }

    /**
     * Set the text content
     *
     * @param text String to write in CLOB
     */
    public void setText(String text) {
        set(CONTENT, text);
    }

    /**
     * Overrides default behavior in {@link com.arsdigita.versioning.VersionedACSObject}
     * so that changes are recorded even if the object is new
     *
     * @return <code>true</code> if the modifications to the current
     *   object should be recorded; <code>false</code> otherwise
     */
    public boolean recordChanges() {
        return (ContentItem.DRAFT.equals(getVersion()));
    }

}
