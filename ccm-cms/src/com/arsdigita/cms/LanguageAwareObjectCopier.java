package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.Property;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class LanguageAwareObjectCopier extends ObjectCopier {

    private static final Logger logger =
                                Logger.getLogger(LanguageAwareObjectCopier.class);
    private String language;

    public LanguageAwareObjectCopier(String language) {
        super();
        this.language = language;
    }

    @Override
    protected void copyCollection(final DomainObject source,
                                  final DomainObject target,
                                  final Property prop) {
        if (source instanceof ContentPage) {

            if (logger.isDebugEnabled()) {
                logger.debug("Copying collection " + prop);
            }

            final String name = prop.getName();

            final DataAssociation sass = (DataAssociation) get(source, name);
            final DataAssociationCursor scursor = sass.cursor();            
            final Property reverse = prop.getAssociatedProperty();

            while (scursor.next()) {
                final DomainObject selem = domain(scursor.getDataObject());

                m_traversed.add(selem, reverse);

                DomainObject telem = copy(source, target, selem, prop);
                if ((telem instanceof ContentPage)
                    && ((ContentPage) telem).getContentBundle() != null) {
                    telem = ((ContentPage) telem).getContentBundle().getInstance(
                            language);
                }                             
                
                DataObject tgtLink = null;

                // removing this assert since copy will return null in the
                // case of deferred association creation in VersionCopier
                //Assert.exists(telem, DomainObject.class);

                if (telem != null) {
                    tgtLink = add(target, name, telem);
                }
                if (tgtLink != null) {
                    // Copy link attributes as well
                    copyData(new WrapperDomainObject(scursor.getLink()),
                             new WrapperDomainObject(tgtLink));
                }

            }

        } else {
            //Use old behaviour
            super.copyCollection(source, target, prop);
        }

    }
}
