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
package com.arsdigita.cms.lifecycle;

/**
 * This class registers the action to perform with a lifecycle/phase begins
 * or ends.  Typically, this class is associated with a Lifecycle or Phase
 * DomainObject.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: LifecycleListener.java 287 2005-02-22 00:29:02Z sskracic $  
 */


public interface LifecycleListener {

    /**
     * Invoked when an lifecycle or phase begins.  If any runtime exception is
     * thrown, the transaction will be aborted.
     */
    public void begin(LifecycleEvent e);

    /**
     * Invoked when an lifecycle or phase ends.  If any runtime exception is
     * thrown, the transaction will be aborted.
     */
    public void end(LifecycleEvent e);
}
