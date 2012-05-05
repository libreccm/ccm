/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.simplesurvey;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;

/**
 * <p><strong>Experimental</strong></p>
 *
 * @see Survey
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: SurveyCollection.java 1940 2009-05-29 07:15:05Z terry $
 */
public class SurveyCollection extends DomainCollection {

    protected SurveyCollection(DataCollection dataCollection) {
        super(dataCollection);
    }
    
    public Survey getSurvey() {
        DataObject dataObject = m_dataCollection.getDataObject();
 
        Survey survey = Survey.retrieve(dataObject);

        Assert.exists(survey, Survey.class);

        return survey;
    }
}
