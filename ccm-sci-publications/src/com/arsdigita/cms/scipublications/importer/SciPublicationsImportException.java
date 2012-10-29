package com.arsdigita.cms.scipublications.importer;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsImportException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>SciPublicationsImportException</code> without detail message.
     */
    public SciPublicationsImportException() {
        super();
    }


    /**
     * Constructs an instance of <code>SciPublicationsImportException</code> with the specified detail message.
     *
     * @param msg The detail message.
     */
    public SciPublicationsImportException(final String msg) {
        super(msg);
    }

    /**
      * Constructs an instance of <code>SciPublicationsImportException</code> which wraps the 
      * specified exception.
      *
      * @param exception The exception to wrap.
      */
    public SciPublicationsImportException(final Exception exception) {
        super(exception);
    }

    /**
      * Constructs an instance of <code>SciPublicationsImportException</code> with the specified message which also wraps the 
      * specified exception.
      *
      * @param msg The detail message.
      * @param exception The exception to wrap.
      */
    public SciPublicationsImportException(final String msg, final Exception exception) {
        super(msg, exception);
    }
}
