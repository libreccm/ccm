package com.arsdigita.cms.contenttypes;

import java.util.Comparator;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciPublicationComparator implements Comparator<Publication> {

    public int compare(Publication publication1, Publication publication2) {
        return publication1.getTitle().compareTo(publication2.getTitle());
    }
    
    
    
}
