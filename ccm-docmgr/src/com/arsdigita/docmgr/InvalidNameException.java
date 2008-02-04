/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.docmgr;

import java.text.ChoiceFormat;

/**
 * Exception thrown to indicate that a resource name (or path)
 * is in an invalid state.
 *
 * @author stefan@arsdigita.com
 *
 * @version $Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/InvalidNameException.java#2 $
 */
public class InvalidNameException extends Exception {

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
