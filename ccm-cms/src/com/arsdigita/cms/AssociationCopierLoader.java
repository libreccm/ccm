package com.arsdigita.cms;

import com.arsdigita.persistence.metadata.Property;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public final class AssociationCopierLoader {

    private static final AssociationCopierLoader INSTANCE =
                                                 new AssociationCopierLoader();
    private static final Logger logger =
                                Logger.getLogger(AssociationCopierLoader.class);
    private Map<String, AssociationCopier> copiers =
                                           new HashMap<String, AssociationCopier>();

    private AssociationCopierLoader() {
        final String[] assocCopierNames = CMSConfig.getInstance().
                getAssocCopiers();

        for (String assocCopierName : assocCopierNames) {
            loadAssocCopier(assocCopierName);
        }
    }

    private void loadAssocCopier(final String name) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException ex) {
            logger.warn(String.format("No class found for name '%s'. Skiping.",
                                      name),
                        ex);
            return;
        }

        if (clazz.isAssignableFrom(AssociationCopier.class)) {
            try {
                final AssociationCopier copier = (AssociationCopier) clazz.
                        newInstance();
                
                copiers.put(copier.forProperty(), copier);
            } catch (InstantiationException ex) {
                logger.warn(String.format(
                        "Failed to instaniate copier '%s'. Skiping.",
                        name),
                            ex);
            } catch (IllegalAccessException ex) {
                logger.warn(String.format(
                        "Failed to instaniate copier '%s'. Skiping.",
                        name),
                            ex);
            }
        } else {
            logger.warn(String.format("Class '%s' is not an implementation of "
                                      + "the AssociationCopier interface. "
                                      + "Skiping",
                                      name));
        }
    }

    public static AssociationCopierLoader getInstance() {
        return INSTANCE;
    }

    public AssociationCopier getAssociationCopierFor(final Property property,
                                                     final CustomCopy source) {
        return copiers.get(String.format("%s::%s",
                                         source.getClass().getName(),
                                         property.getName()));
    }
}
