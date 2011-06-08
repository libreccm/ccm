package com.arsdigita.cms.contenttypes;

import java.util.Comparator;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciPublicationYearDescComparator implements Comparator<Publication> {

    public int compare(Publication publication1, Publication publication2) {
        if (publication1.getYearOfPublication() == null) {
            return 1;
        } else if (publication2.getYearOfPublication() == null) {
            return -1;
        } else {
            int ret =  publication2.getYearOfPublication().compareTo(publication1.
                    getYearOfPublication());
            if (ret == 0) {
                SciPublicationTitleComparator titleComparator = new SciPublicationTitleComparator();
                ret = titleComparator.compare(publication1, publication2);                                
            }
            
            return ret;
        }

    }
}
