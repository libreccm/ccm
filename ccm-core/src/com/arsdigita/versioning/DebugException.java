/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.versioning;

// new versioning

/**
 * <p>This class should only be used in debugging as a means of non-local control
 * transfer.  Checked in code should not make use of this class.</p>
 *
 * <p>Suppose you are debugging an exceptional condition that can only be
 * detected in method <code>foo()</code>.  However, the available context inside
 * of <code>foo()</code> is insufficient to determine the real cause of the
 * exception condition.  You have to backtrack and examine the context of the
 * caller's of <code>foo()</code>.  You can use this class as a means of
 * non-local control transfer. Throw a <code>DebugException</code> from within
 * <code>foo()</code> and catch somewhere down the stack, where more information
 * can be gathered regarding the real cause of the error. </p>
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-09-15
 * @version $Revision: #4 $ $DateTime: 2004/08/16 18:10:38 $
 **/
class DebugException extends VersioningException {

    public DebugException() {
        super((String) null);
    }

    public DebugException(String msg) {
        super(msg);
    }

    public DebugException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

    public DebugException(Throwable rootCause) {
        super(rootCause);
    }
}
