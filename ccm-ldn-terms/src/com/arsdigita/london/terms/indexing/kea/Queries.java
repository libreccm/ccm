/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.terms.indexing.kea;

import java.math.BigDecimal;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainQuery;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;

/**
 * Queries for performance optimisation of keyphrase extraction.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class Queries {

    static class TrainingItems extends DomainQuery {

        public static final String QUERY_NAME = "com.arsdigita.london.terms.indexing.getTrainingItems";
        public static final String ITEM_ID = ContentItem.ID;

        TrainingItems(Domain domain, String language) {
            super(QUERY_NAME);
            setParameter(Term.DOMAIN, domain.getKey());
            setParameter(ContentItem.LANGUAGE, language);
        }

        public BigDecimal getID() {
            return (BigDecimal) get(ITEM_ID);
        }
    }

    static class PreferredTerms extends DomainQuery {

        public static final String QUERY_NAME = "com.arsdigita.london.terms.indexing.getPreferredTerms";
        public static final String UNIQUE_ID = Term.UNIQUE_ID;
        public static final String NAME = Category.NAME;

        PreferredTerms(Domain domain) {
            super(QUERY_NAME);
            setParameter("domain", domain.getKey());
        }

        public String getUniqueID() {
            return String.valueOf(get(UNIQUE_ID));
        }

        public String getName() {
            return (String) get(NAME);
        }
    }

    static class NonPreferredTerms extends DomainQuery {

        public static final String QUERY_NAME = "com.arsdigita.london.terms.indexing.getNonPreferredTerms";
        public static final String UNIQUE_ID = Term.UNIQUE_ID;
        public static final String NAME = Category.NAME;
        public static final String PREFERRED_UNIQUE_ID = "preferredUniqueID";

        NonPreferredTerms(Domain domain) {
            super(QUERY_NAME);
            setParameter("domain", domain.getKey());
        }

        public String getUniqueID() {
            return String.valueOf(get(UNIQUE_ID));
        }

        public String getPreferredUniqueID() {
            return String.valueOf(get(PREFERRED_UNIQUE_ID));
        }

        public String getName() {
            return (String) get(NAME);
        }
    }

    static class RelatedTerms extends DomainQuery {

        public static final String QUERY_NAME = "com.arsdigita.london.terms.indexing.getRelatedTerms";
        public static final String UNIQUE_ID = Term.UNIQUE_ID;
        public static final String RELATED_UNIQUE_ID = "relatedUniqueID";
        public static final String RELATION_TYPE = Category.REL_TYPE;

        RelatedTerms(Domain domain) {
            super(QUERY_NAME);
            setParameter("domain", domain.getKey());
        }

        public String getUniqueID() {
            return String.valueOf(get(UNIQUE_ID));
        }

        public String getRelatedUniqueID() {
            return String.valueOf(get(RELATED_UNIQUE_ID));
        }

        public String getRelationType() {
            return (String) get(RELATION_TYPE);
        }

    }
}
