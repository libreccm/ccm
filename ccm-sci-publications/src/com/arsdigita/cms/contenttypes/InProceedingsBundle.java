package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class InProceedingsBundle extends PublicationBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.InProceedingsBundle";
    public static final String PROCEEDINGS = "proceedings";

    public InProceedingsBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public InProceedingsBundle(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public InProceedingsBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public InProceedingsBundle(final DataObject dobj) {
        super(dobj);
    }

    public InProceedingsBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();

        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final InProceedingsBundle inProceedingsBundle =
                                      (InProceedingsBundle) source;

            if (PROCEEDINGS.equals(attribute)) {
                final DataCollection proceedings =
                                     (DataCollection) inProceedingsBundle.get(
                        PROCEEDINGS);

                while (proceedings.next()) {
                    createProceedingsAssoc(proceedings);
                }

                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    private void createProceedingsAssoc(final DataCollection proceedings) {
        final ProceedingsBundle draftProceedings =
                                (ProceedingsBundle) DomainObjectFactory.
                newInstance(proceedings.getDataObject());
        final ProceedingsBundle liveProceedings =
                                (ProceedingsBundle) draftProceedings.
                getLiveVersion();

        if (liveProceedings != null) {
            final DataObject link = add(PROCEEDINGS, liveProceedings);

            link.set(Proceedings.PAPER_ORDER,
                     proceedings.get(InProceedingsCollection.LINKORDER));

            link.save();
        }
    }

    public ProceedingsBundle getProceedings() {
        final DataCollection collection = (DataCollection) get(PROCEEDINGS);

        if (collection.size() == 0) {
            return null;
        } else {
            final DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();
            collection.close();

            return (ProceedingsBundle) DomainObjectFactory.newInstance(dobj);
        }
    }
    
    public void setProceedings(final Proceedings proceedings) {
        final ProceedingsBundle oldProceedings = getProceedings();
        
        if (oldProceedings != null) {
            remove(PROCEEDINGS, oldProceedings);
        }
        
        if (proceedings != null) {
            Assert.exists(proceedings, Proceedings.class);
            
            final DataObject link = add(PROCEEDINGS, proceedings.getProceedingsBundle());
            link.set(ProceedingsBundle.PAPER_ORDER, 
                     Integer.valueOf((int) proceedings.getPapers().size()));
            link.save();
        }
    } 
}
