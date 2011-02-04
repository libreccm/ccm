package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jensp
 */
public class BibTeXConverters {

    private Map<String, BibTeXConverter> converters =
                                         new HashMap<String, BibTeXConverter>();

    private BibTeXConverters() {
    }

    private static class Instance {

        private static BibTeXConverters INSTANCE = new BibTeXConverters();
    }

    public static BibTeXConverters getInstance() {
        return Instance.INSTANCE;
    }

    public String convert(final Publication publication) {
        BibTeXConverter converter;

        converter = converters.get(publication.getClass().getName());

        if (converter == null) {
            if (publication instanceof PublicationWithPublisher) {
                converter = converters.get(PublicationWithPublisher.class.
                        getName());
            } else {
                converter = converters.get(Publication.class.getName());

            }
        }

        return converter.convert(publication);
    }
}
