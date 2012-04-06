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
public class ArticleInCollectedVolumeBundle extends PublicationBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeBundle";
    public static final String COLLECTED_VOLUME = "collectedVolume";

    public ArticleInCollectedVolumeBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public ArticleInCollectedVolumeBundle(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public ArticleInCollectedVolumeBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ArticleInCollectedVolumeBundle(final DataObject dobj) {
        super(dobj);
    }

    public ArticleInCollectedVolumeBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final ArticleInCollectedVolumeBundle articleBundle = (ArticleInCollectedVolumeBundle) source;
            
            if (COLLECTED_VOLUME.equals(attribute)) {
                final DataCollection collectedVolumes = (DataCollection) articleBundle.get(COLLECTED_VOLUME);
                
                while(collectedVolumes.next()) {
                    createCollectedVolumeAssoc(collectedVolumes);
                }
                
                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }
    
    private void createCollectedVolumeAssoc(final DataCollection collectedVolumes) {
        final CollectedVolumeBundle draftCollVol = (CollectedVolumeBundle) DomainObjectFactory.newInstance(collectedVolumes.getDataObject());
        final CollectedVolumeBundle liveCollVol = (CollectedVolumeBundle) draftCollVol.getLiveVersion();
        
        if (liveCollVol != null) {
            final DataObject link = add(COLLECTED_VOLUME, liveCollVol);
            
            link.set(CollectedVolumeBundle.ARTICLE_ORDER,
                     collectedVolumes.get(ArticleInCollectedVolumeCollection.LINKORDER));
            
            link.save();
        }        
    }

    public CollectedVolumeBundle getCollectedVolume() {
        final DataCollection collection = (DataCollection) get(COLLECTED_VOLUME);

        if (collection.size() == 0) {
            return null;
        } else {
            final DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();
            collection.close();

            return (CollectedVolumeBundle) DomainObjectFactory.newInstance(dobj);
        }
    }

    public void setCollectedVolume(final CollectedVolume collectedVolume) {
        final CollectedVolumeBundle oldCollectedVolume = getCollectedVolume();

        if (oldCollectedVolume != null) {
            remove(COLLECTED_VOLUME, oldCollectedVolume);
        }

        if (collectedVolume != null) {
            Assert.exists(collectedVolume, CollectedVolume.class);

            final DataObject link = add(
                    COLLECTED_VOLUME,
                    collectedVolume.getCollectedVolumeBundle());
            link.set(CollectedVolumeBundle.ARTICLE_ORDER,
                     Integer.valueOf((int) collectedVolume.getArticles().size()));
            link.save();

        }
    }
}
