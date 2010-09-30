package com.arsdigita.cms.contenttypes;

/**
 *
 * @author Jens Pelzetter
 */
public class ProceedingsLoader extends AbstractContentTypeLoader {

     private static final String[] TYPES = {"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Proceedings.xml"};

     public ProceedingsLoader() {
         super();
     }

     public String[] getTypes() {
         return TYPES;
     }

}
