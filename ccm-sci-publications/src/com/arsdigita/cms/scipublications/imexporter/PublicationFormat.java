/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.scipublications.imexporter;

import javax.activation.MimeType;

/**
 * Describes the format provided by an implementation of 
 * {@link SciPublicationsExporter} or {@link SciPublicationsImporter}.
 *
 * @author Jens Pelzetter
 */
public class PublicationFormat {

    /**
     * Name of the format.
     */
    private String name;
    /**
     * MimeType to use for files of the format.
     */
    private MimeType mimeType;
    /**
     * File extension for files of the format.
     */
    private String fileExtension;

    public PublicationFormat() {
        super();
    }

    public PublicationFormat(final String name,
                             final MimeType mimeType,
                             final String fileExtension) {
        this.name = name;
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(final String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(final MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PublicationFormat)) {
            return false;
        }
        
        final PublicationFormat other = (PublicationFormat) obj;
        if ((name == null) && other.getName() != null) {
            return false;
        }  else if((name != null) && other.getName() == null) {
            return false;
        } else {
            return name.equals(other.getName());
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        if (this.name == null) {
            hash *= 37;
        } else {
            hash = 37 * hash + this.name.hashCode();
        }        
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("PublicationFormat = {name = \"%s\"; "
                             + "mimeType = {%s}; "
                             + "fileExtension = \"\"}",
                             name,
                             mimeType.toString(),
                             fileExtension);
    }
}
