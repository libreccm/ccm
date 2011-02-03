package com.arsdigita.cms.scipublications.exporter;

import javax.activation.MimeType;

/**
 *
 * @author jensp
 */
public class PublicationFormat {

    private String name;
    private MimeType mimeType;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PublicationFormat other = (PublicationFormat) obj;
        if ((this.name == null) ? (other.name != null)
            : !this.name.equals(other.name)) {
            return false;
        }
        if (this.mimeType != other.mimeType && (this.mimeType == null || !this.mimeType.
                                                equals(other.mimeType))) {
            return false;
        }
        if ((this.fileExtension == null) ? (other.fileExtension != null)
            : !this.fileExtension.equals(other.fileExtension)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
