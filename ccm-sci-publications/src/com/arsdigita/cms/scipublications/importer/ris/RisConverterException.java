/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arsdigita.cms.scipublications.importer.ris;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class RisConverterException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>RisConverterException</code> without detail message.
     */
    public RisConverterException() {
        super();
    }


    /**
     * Constructs an instance of <code>RisConverterException</code> with the specified detail message.
     *
     * @param msg The detail message.
     */
    public RisConverterException(final String msg) {
        super(msg);
    }

    /**
      * Constructs an instance of <code>RisConverterException</code> which wraps the 
      * specified exception.
      *
      * @param exception The exception to wrap.
      */
    public RisConverterException(final Exception exception) {
        super(exception);
    }

    /**
      * Constructs an instance of <code>RisConverterException</code> with the specified message which also wraps the 
      * specified exception.
      *
      * @param msg The detail message.
      * @param exception The exception to wrap.
      */
    public RisConverterException(final String msg, final Exception exception) {
        super(msg, exception);
    }
}
