/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.libreccm.export;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ExportException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>ExportException</code> without detail message.
     */
    public ExportException() {
        super();
    }


    /**
     * Constructs an instance of <code>ExportException</code> with the specified detail message.
     *
     * @param msg The detail message.
     */
    public ExportException(final String msg) {
        super(msg);
    }

    /**
      * Constructs an instance of <code>ExportException</code> which wraps the 
      * specified exception.
      *
      * @param exception The exception to wrap.
      */
    public ExportException(final Exception exception) {
        super(exception);
    }

    /**
      * Constructs an instance of <code>ExportException</code> with the specified message which also wraps the 
      * specified exception.
      *
      * @param msg The detail message.
      * @param exception The exception to wrap.
      */
    public ExportException(final String msg, final Exception exception) {
        super(msg, exception);
    }
}
