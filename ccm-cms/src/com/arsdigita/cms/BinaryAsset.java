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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



/**
 * An abstract class for an asset which represents some binary data,
 * such as an image, an audio clip, etc. Contains utility methods for
 * reading and writing bytes. Child classes must override the {@link
 * #getBaseDataObjectType}, {@link #getContent} and {@link
 * #setContent} methods.
 *
 * @author Jack Chung
 * @author Stanislav Freidin
 *
 * @version $Id: BinaryAsset.java 2090 2010-04-17 08:04:14Z pboy $ 
 */
public abstract class BinaryAsset extends Asset {

    protected BinaryAsset(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    protected BinaryAsset(String type) {
        super(type);
    }

    public BinaryAsset(DataObject obj) {
        super(obj);
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    public abstract String getBaseDataObjectType();

    /**
     * Reads a stream of data into the content object.
     *
     * @param is A input stream
     * @return the number of bytes read
     */
    public long readBytes(InputStream is) throws IOException {
        //persistence does not support stream,
        //  so accumulate a byte array

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[8];
        int length = -1;
        while ((length = is.read(buffer)) != -1) {
            os.write(buffer, 0, length);
        }

        byte[] content = os.toByteArray();

        setContent(content);

        return (long)content.length;
    }


    /**
     * Writes data from the content object to the specified
     * output stream.
     *
     * @param os The output stream
     * @return the number of bytes written
     */
    public long writeBytes(OutputStream os) throws IOException {

        // Blob blob = getContent();
        //int blobLength = (int)blob.length();

        /*
          InputStream is = blob.getBinaryStream();
          int iWrote = 0;

          byte[] buffer = new byte[8];
          int length = -1;
          while ((length = is.read(buffer)) != -1) {
          os.write(buffer, 0, length);
          iWrote = iWrote + length;
          }

          return (long)iWrote;

        */
        //byte[] bytes = blob.getBytes(1L, blobLength);
        byte[] bytes = getContent();
        os.write(bytes);

        return (long)(bytes.length);
    }

    /**
     * All derived classes must implement this method.  This method retrieves
     * the Blob content.
     *
     * @return the Blob content
     */
    protected abstract byte[] getContent();

    /**
     * All derived classes must implement this method.  This method sets
     * the Blob content.
     *
     * @param content the binary data to be put into the Blob
     */
    protected abstract void setContent(byte[] content);


    /**
     * Fetch the size of the content.
     *
     * @return The size of the content
     */
    public long getSize() {
        long size = 0;
        byte[] content = getContent();
        if ( content != null ) {
            size = content.length;
        }
        return size;
    }

}
