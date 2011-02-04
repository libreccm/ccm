package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class BibTeXBuilders {

    private static final Logger logger = Logger.getLogger(BibTeXBuilders.class);
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

    public BibTeXBuilder getBibTeXBuilderForType(final String type) {
        if (builders.containsKey(type)) {
            try {
                return builders.get(type).getClass().newInstance();
            } catch (InstantiationException ex) {
                logger.warn(String.format("Failed to create BibTeXBuilder "
                                          + "for type '%s'.", type),
                            ex);
                return null;
            } catch (IllegalAccessException ex) {
                logger.warn(String.format("Failed to create BibTeXBuilder "
                                          + "for type '%s'.", type),
                            ex);
                return null;
            }
        } else {
            return null;
        }
    }
}
