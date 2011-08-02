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
package com.arsdigita.cms.workflow;

import com.arsdigita.cms.ui.ContentItemPage;

import java.math.BigDecimal;

/**
 * Interface for generating a URL for a Task given the ID of the
 * ContentItem and the Task.
 *
 * @author Uday Mathur (umathur@arsdigita.com)
 * @version $Id: PreviewTaskURLGenerator.java 2090 2010-04-17 08:04:14Z pboy $
 * */

public class PreviewTaskURLGenerator extends FinishTaskURLGenerator {

    public PreviewTaskURLGenerator() {}
    public String generateURL(BigDecimal itemId, BigDecimal taskId) {
        return ContentItemPage.getItemURL(itemId, ContentItemPage.AUTHORING_TAB);
    }
}
