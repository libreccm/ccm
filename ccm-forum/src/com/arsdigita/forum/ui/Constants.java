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
package com.arsdigita.forum.ui;

import java.math.BigDecimal;

/**
 * This interface holds constants used in bboard:
 * XML namespaces, URLs, URL variable names, etc.
 *
 *  @author <a href="mailto:teadams@arsdigita.com">Tracy Adams</a>
 *  @version $Revision: 1.3 $ $Date: 2006/03/08 15:38:33 $
 *  @since ACS 4.7
 */

public interface Constants {

    static final String FORUM_XML_PREFIX = "forum";
    static final String FORUM_XML_NS = "http://www.arsdigita.com/forum/1.0";

    static final String FORUM_MODE_VIEW = "view";
    static final String FORUM_MODE_POST = "post";
    
    static final String MODE_PARAM = "mode";
    static final String THREAD_PARAM = "thread";
    static final String TOPIC_PARAM = "topic";
    static final String PAGINATOR_PARAM = "page";

    static final BigDecimal TOPIC_ANY = new BigDecimal(-1);
    static final BigDecimal TOPIC_NONE = new BigDecimal(-2);


}
