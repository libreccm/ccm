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
package com.arsdigita.formbuilder;


import com.arsdigita.bebop.parameters.EmailParameter;


// logging


/**
 * I am using this process listener to test how process listeners
 * can supply the Form Builder admin UI with information about what kind of
 * parameters that they are expecting.
 *
 * @author Peter Marklund
 * @version $Id: TestAttributeListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class TestAttributeListener extends TestProcessListener {

    public static final String versionId = "$Id: TestAttributeListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public AttributeMetaDataList getAttributeMetaData() {

        AttributeMetaDataList attributeList = new AttributeMetaDataList();

        // Email address - required email parameter
        EmailParameter emailParameter = new EmailParameter("emailAddress");
        AttributeType emailType = new AttributeType(emailParameter.getClass());
        AttributeMetaData emailAttribute = new AttributeMetaData("emailAddress",
                                                                 null,
                                                                 true,
                                                                 false,
                                                                 emailType);
        attributeList.add(emailAttribute);

        // Subject - required
        AttributeMetaData subjectAttribute = new AttributeMetaData("subject",
                                                                   true);

        attributeList.add(subjectAttribute);

        // Priority - multiple
        AttributeMetaData priorityAttribute = new AttributeMetaData("priority",
                                                                    false,
                                                                    true);

        attributeList.add(priorityAttribute);

        // Body - long text
        AttributeMetaData bodyAttribute = new AttributeMetaData("body");

        attributeList.add(bodyAttribute);

        return attributeList;
    }
}
