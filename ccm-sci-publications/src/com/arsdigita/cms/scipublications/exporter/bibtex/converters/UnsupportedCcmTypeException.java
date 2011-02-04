package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

/**
 *
 * @author jensp
 */
public class UnsupportedCcmTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>UnsupportedCcmType</code> without detail message.
     */
    public UnsupportedCcmTypeException() {
    }

    /**
     * Constructs an instance of <code>UnsupportedCcmType</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnsupportedCcmTypeException(final String msg) {
        super(msg);
    }

    public UnsupportedCcmTypeException(final Throwable cause) {
        super(cause);
    }

    public UnsupportedCcmTypeException(final String msg, Throwable cause) {
        super(msg, cause);
    }
}
