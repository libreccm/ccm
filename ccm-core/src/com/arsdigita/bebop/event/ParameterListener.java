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
package com.arsdigita.bebop.event;

import com.arsdigita.bebop.FormProcessException;
import java.util.EventListener;

/**
 *  Defines the interface
 * for a class that validates the values of a single parameter.
 *
 *  @author Karl Goldstein 
 *  @author Uday Mathur 
 *  @version $Id: ParameterListener.java 287 2005-02-22 00:29:02Z sskracic $ */

public interface ParameterListener extends EventListener {

    public static final String versionId = "$Id: ParameterListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     *  Performs a validation check on the data objects associated with a
     *  specific parameter.  Validate should call
     *  ParameterData.addError() with a message regarding the nature
     *  of the error.
     */

    void validate(ParameterEvent e) throws FormProcessException;
}
