package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 *
 * @author jensp
 */
public class BibTeXBuilders {

    private Map<String, BibTeXBuilder> builders =
                                       new HashMap<String, BibTeXBuilder>();

    private BibTeXBuilders() {
        ServiceLoader<BibTeXBuilder> builderServices;

        builderServices = ServiceLoader.load(BibTeXBuilder.class);

        for (BibTeXBuilder builder : builderServices) {
            builders.put(builder.getBibTeXType(), builder);
        }
    }

    private static class Instance {

        private static final BibTeXBuilders INSTANCE = new BibTeXBuilders();
    }

    public static BibTeXBuilders getInstance() {
        return Instance.INSTANCE;
    }

    public BibTeXBuilder getBibTeXBuilderForCcmPublicationtType(final String type) {
        return builders.get(type);
    }
}
