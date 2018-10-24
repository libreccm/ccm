/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.portation.modules.contentsection.util;

import com.arsdigita.cms.portation.modules.contentsection.ContentItemVersion;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 2/21/18
 */
public class ContentItemVersionMapper {
    public static ContentItemVersion mapContentItemVersion(final String
                                                                   version) {
        switch (version) {
            case "DRAFT":
                return ContentItemVersion.DRAFT;
            case "PENDING":
                return ContentItemVersion.PENDING;
            case "LIVE":
                return ContentItemVersion.LIVE;
            default:
                return ContentItemVersion.DRAFT;
        }
    }
}
