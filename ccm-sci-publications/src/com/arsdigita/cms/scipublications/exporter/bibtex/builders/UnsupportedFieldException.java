package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

/**
 *
 * @author jensp
 */
public class UnsupportedFieldException extends Exception {

    /**
     * Creates a new instance of <code>UnsupportedFieldException</code> without detail message.
     */
    public UnsupportedFieldException() {
    }


    /**
     * Constructs an instance of <code>UnsupportedFieldException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnsupportedFieldException(String msg) {
        super(msg);
    }

    public UnsupportedFieldException(Throwable cause) {
        super(cause);
    }

    public UnsupportedFieldException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
