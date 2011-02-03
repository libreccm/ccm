package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
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

        return converter.convert(publication);
    }

}
