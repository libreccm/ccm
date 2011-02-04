package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Review;

/**
 *
 * @author jensp
 */
public class ReviewConverter extends ArticleInJournalConverter {

    @Override
    public String getCcmType() {
        return Review.class.getName();
    }

}
