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
package com.arsdigita.docrepo;

import java.text.ChoiceFormat;

/**
 * Exception thrown to indicate that a resource name (or path)
 * is in an invalid state.
 *
 * @author stefan@arsdigita.com
 * @version $Id: InvalidNameException.java  pboy $
 */
public class InvalidNameException extends ResourceException {

    /**
     * Error messages for path validation methods
     * @see ResourceImpl.isValidPath
     */
    public static final int ZERO_CHARACTERS_ERROR = 1;
    public static final int LEADING_FILE_SEPARATOR_ERROR = 2;
    public static final int TRAILING_FILE_SEPARATOR_ERROR = 3;
    public static final int INVALID_CHARACTER_ERROR = 4;
    public static final int LEADING_CHARACTER_ERROR = 5;

    /*
      TODO: Error messages eventually internationalized
      read from a resource file.
    */
    protected static ChoiceFormat s_validPathErrorMessages =
        new ChoiceFormat(
                         new double[] {ZERO_CHARACTERS_ERROR,
                                       LEADING_FILE_SEPARATOR_ERROR,
                                       TRAILING_FILE_SEPARATOR_ERROR,
                                       INVALID_CHARACTER_ERROR,
                                       LEADING_CHARACTER_ERROR},
                         new String[] {"Empty string is no valid pathname.",
                                       "Path name cannot begin with a file separation character.",
                                       "Path name cannot end with a file separation character.",
                                       "Path name contains invalid characters, " +
                                       "only [a-z][A-Z][0-9][-., ] are allowed.",
                                       "Resource names cannot begin with a \".\"."});

    /**
     * Creates a new exception with a given error message.
     * @param message the error message
     */

    public InvalidNameException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the error message
     * corresponding to the error message codes above.
     * @param error code
     */

    public InvalidNameException(int messageCode) {
        super(s_validPathErrorMessages.format(messageCode));
    }
}
