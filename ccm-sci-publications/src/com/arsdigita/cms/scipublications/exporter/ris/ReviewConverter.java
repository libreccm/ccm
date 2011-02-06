package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Review;

/**
 *
 * @author Jens Pelzetter
 */
public class ReviewConverter extends ArticleInJournalConverter {

    @Override
    public String getCcmType() {
        return Review.class.getName();
    }
}
