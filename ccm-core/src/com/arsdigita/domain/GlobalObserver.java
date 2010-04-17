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
package com.arsdigita.domain;

/**
 * The GlobalObserver interface can be used in conjunction with the
 * GlobalObserverManager to register an observer that will observe every
 * single observable domain object. This should be used with extreme caution
 * since global observers can be called for every single operation that
 * happens on any domain object. If one of the global observers is slow or
 * error prone this could cause the system to become slow and/or extremely non
 * robust.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: GlobalObserver.java 287 2005-02-22 00:29:02Z sskracic $
 */

public interface GlobalObserver extends DomainObjectObserver {

    /**
     * This method should return a value indicating whether or not this
     * GlobalObserver wishes to observe the given domain object.
     *
     * @param dobj The domain object that is a candidate for observation.
     **/

    boolean shouldObserve(DomainObject dobj);

}
