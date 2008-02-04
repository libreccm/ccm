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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;


/**
 * @deprecated (Use com.arsdigita.kernel.ui.ACSObjectSelectionModel)
 **/
public class ACSObjectSelectionModel
    extends com.arsdigita.kernel.ui.ACSObjectSelectionModel {

    public ACSObjectSelectionModel(BigDecimalParameter parameter) {
        super(parameter);
    }

    public ACSObjectSelectionModel(String parameterName) {
        super(new BigDecimalParameter(parameterName));
    }

    public ACSObjectSelectionModel(SingleSelectionModel model) {
        super(model);
    }

    public ACSObjectSelectionModel(String javaClass, String objectType,
                                   String parameterName) {
        super(javaClass, objectType, new BigDecimalParameter(parameterName));
    }

    public ACSObjectSelectionModel(String javaClass, String objectType,
                                   BigDecimalParameter parameter) {
        super(javaClass, objectType,
              new ParameterSingleSelectionModel(parameter));
    }

    public ACSObjectSelectionModel(String javaClass, String objectType,
                                   SingleSelectionModel model) {
        super(javaClass, objectType, model);
    }

}
