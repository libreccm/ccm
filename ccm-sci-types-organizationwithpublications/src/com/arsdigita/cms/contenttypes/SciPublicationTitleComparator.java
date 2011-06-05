package com.arsdigita.cms.contenttypes;

import java.util.Comparator;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciPublicationTitleComparator implements Comparator<Publication> {

    public int compare(Publication publication1, Publication publication2) {
        String title1;
        String title2;
        if (publication1.getTitle().startsWith("\"")) {
            title1 = publication1.getTitle().substring(1,
                                                       publication1.getTitle().
                    length() - 1);
        } else if (publication1.getTitle().startsWith("'")) {
            title1 = publication1.getTitle().substring(1,
                                                       publication1.getTitle().
                    length() - 1);
        } else {
            title1 = publication1.getTitle();
        }

        if (publication2.getTitle().startsWith("\"")) {
            title2 = publication2.getTitle().substring(2,
                                                       publication2.getTitle().
                    length() - 1);
        } else if (publication2.getTitle().startsWith("'")) {
            title2 = publication2.getTitle().substring(2,
                                                       publication2.getTitle().
                    length() - 1);
        } else {
            title2 = publication2.getTitle();
        }



        return title1.compareTo(title2);
        //return publication1.getTitle().compareTo(publication2.getTitle());
    }
}
