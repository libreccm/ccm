/*
 * Copyright (C) 2005 Chris Gilbert  All Rights Reserved.
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

package com.arsdigita.portlet.news;

import com.arsdigita.domain.DomainCollection;

// Following code it part of 'personalized news' tree which is disabled for now
// because we currently (6.6) have no user preferences of something like that
// in the trunk code. West Sussex used to have a specialized module.

/**
 * Service is used by news portlet to display potentially useful articles 
 * to the user.
 * 
 * An implementation will require a mapping between content pages and some
 * attributes of the user. In the atomwide authentication module, user profile 
 * values are modelled as terms in domains mapped to /content/ and so authors
 * may specify which user groups their articles are to be promoted to
 * 
 * @author chris.gilbert@westsussex.gov.uk
 */
 public interface PersonalisedNewsTarget {
     
    /**
     * 
     * @return a domainCollection of com.arsdigita.cms.ContentPage objects
     */
    public DomainCollection getMyNews();

}
