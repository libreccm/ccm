package com.arsdigita.cms.contenttypes;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.Comparator;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciPublicationAuthorComparator implements Comparator<Publication> {

    private Logger logger = Logger.getLogger(
            SciPublicationAuthorComparator.class);

    public int compare(Publication publication1, Publication publication2) {    
        if ((publication1.getAuthors() == null)
            || publication1.getAuthors().size() == 0) {
            logger.debug("publication1 has no authors, returning -1");
            return -1;
        } else if ((publication2.getAuthors() == null)
                   || publication2.getAuthors().size() == 0) {
            logger.debug("publication2 has no authors, returning ");
            return 1;
        } else {
            logger.debug("Both publication have authors, comparing authors...");
            int ret = 0;

            AuthorshipCollection authors1 = publication1.getAuthors();
            AuthorshipCollection authors2 = publication2.getAuthors();

            while ((ret == 0) && authors1.next() && authors2.next()) {
                GenericPerson author1 = authors1.getAuthor();
                GenericPerson author2 = authors2.getAuthor();
                logger.debug(String.format(
                        "Comparing surnames: author1.surname = '%s'; author2.surname = '%s'",
                        author1.getSurname(),
                        author2.getSurname()));
                ret = author1.getSurname().compareTo(author2.getSurname());

                if (ret == 0) {
                    logger.debug(String.format(
                            "Surnames are identical, comparing given names:"
                            + "author1.givenName = '%s'; author2.givenName = '%s'",
                            author1.getGivenName(),
                            author2.getGivenName()));
                    ret = author1.getGivenName().compareTo(
                            author2.getGivenName());
                }
                logger.debug(String.format("ret = %d", ret));
            }

            return ret;
        }
    }
}
