package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublishedAssociation extends DomainObject {

    private static final String DRAFT_A = "draftA";
    private static final String DRAFT_B = "draftA";
    private static final String PROPERTY_A = "propertyA";
    private static final String PROPERTY_B = "propertyB";
    //private static final String PENDING_A = "pendingA";
    //private static final String PENDING_B = "pendingA";
    private static final String ATTRIBUTES = "associationAttributes";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.PublishedAssociation";

    protected PublishedAssociation() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    protected PublishedAssociation(final DataObject dobj) {
        super(dobj);
    }

    protected PublishedAssociation(final OID oid) {
        super(oid);
    }

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    protected static PublishedAssociation create(final ContentItem draftA,
                                                 final ContentItem draftB,
                                                 final String propertyA,
                                                 final String propertyB) {
        //Check if the association is already saved.
        final Session session = SessionManager.getSession();
        final DataCollection assocsA = session.retrieve(BASE_DATA_OBJECT_TYPE);
        assocsA.addEqualsFilter(DRAFT_A + ".id", draftA.getID());
        assocsA.addEqualsFilter(DRAFT_B + ".id", draftB.getID());
        assocsA.addEqualsFilter(PROPERTY_A, propertyA);
        assocsA.addEqualsFilter(PROPERTY_B, propertyB);

        if (assocsA.size() == 1) {
            assocsA.next();
            final DataObject dobj = assocsA.getDataObject();
            assocsA.close();
            return new PublishedAssociation(dobj);
        } else if (assocsA.size() > 1) {
            throw new UncheckedWrapperException(
                    "Something very strange has occurred. There is more than "
                    + "one PublishedAssociation for a association.");
        }

        //Maybe draftA and draftB are switched         
        final DataCollection assocsB = session.retrieve(BASE_DATA_OBJECT_TYPE);
        assocsB.addEqualsFilter(DRAFT_A + ".id", draftB.getID());
        assocsB.addEqualsFilter(DRAFT_B + ".id", draftA.getID());
        assocsB.addEqualsFilter(PROPERTY_A, propertyB);
        assocsB.addEqualsFilter(PROPERTY_B, propertyA);

        if (assocsB.size() == 1) {
            assocsB.next();
            final DataObject dobj = assocsB.getDataObject();
            assocsB.close();
            return new PublishedAssociation(dobj);
        } else if (assocsB.size() > 1) {
            throw new UncheckedWrapperException(
                    "Something very strange has occurred. There is more than "
                    + "one PublishedAssociation for a association.");
        }

        //No existing entry found, crate new one.

        final PublishedAssociation assoc = new PublishedAssociation();
        assoc.set(DRAFT_A, draftA);
        assoc.set(DRAFT_B, draftB);
        assoc.set(PROPERTY_A, propertyA);
        assoc.set(PROPERTY_B, PROPERTY_B);

        if (draftA.getObjectType().getProperty(propertyA).isCollection()) {
            final DataCollection coll = (DataCollection) draftA.get(
                    propertyA + "@link");

            while (coll.next()) {
                DataObject linkObj = coll.getDataObject();

                if (draftB.getOID().equals(((DataObject) linkObj.getOID().get(
                                            propertyA)).getOID())) {
                    assoc.saveAssociationAttributes(linkObj);
                    coll.close();
                }
            }
        }

        assoc.save();

        return assoc;
    }

    protected static void updateLiveAssociations(final ContentItem item) {
        final Session session = SessionManager.getSession();
        final ContentItem draftItem = item.getDraftVersion();
        final ContentItem liveItem = draftItem.getLiveVersion();

        final DataCollection assocsA = session.retrieve(BASE_DATA_OBJECT_TYPE);
        assocsA.addEqualsFilter(DRAFT_A + ".id", draftItem.getID());
        processAssociations(assocsA, liveItem);

        final DataCollection assocsB = session.retrieve(BASE_DATA_OBJECT_TYPE);
        assocsB.addEqualsFilter(DRAFT_B + ".id", draftItem.getID());
        processAssociations(assocsB, liveItem);
    }

    private static void processAssociations(final DataCollection associations,
                                            final ContentItem liveItem) {
        while (associations.next()) {
            processAssociation(new PublishedAssociation(
                    associations.getDataObject()), liveItem);
        }
    }

    private static void processAssociation(
            final PublishedAssociation association,
            final ContentItem liveItem) {
        final ContentItem otherDraft = (ContentItem) association.get(DRAFT_B);
        final ContentItem otherLive = otherDraft.getLiveVersion();

        if (otherLive != null) {
            createAssociation(liveItem,
                              (String) association.get(PROPERTY_A),
                              otherLive,
                              (byte[]) association.get(ATTRIBUTES));
        }
    }

    private static void createAssociation(final ContentItem itemA,
                                          final String propertyA,
                                          final ContentItem itemB,
                                          final byte[] associationAttributes) {
        final DataObject association = itemA.add(propertyA, itemB);
        setAttributesForLiveAssociation(association, associationAttributes);
    }

    protected static void refreshOnUnPublish(final ContentItem item) {
        //Nothing to do
    }

    private void saveAssociationAttributes(final DataObject assocObj) {
        final Iterator properties = assocObj.getObjectType().
                getDeclaredProperties();
        final Map<String, Object> assocAttrs = new HashMap<String, Object>();

        while (properties.hasNext()) {
            processAttribute(assocObj, assocAttrs, (Property) properties.next());
        }

        if (!assocAttrs.isEmpty()) {
            final ByteArrayOutputStream data = new ByteArrayOutputStream();
            try {
                final ObjectOutputStream out = new ObjectOutputStream(data);
                out.writeObject(assocAttrs);
            } catch (IOException ex) {
                throw new UncheckedWrapperException(ex);
            }

            set(ATTRIBUTES, data.toByteArray());
        }
    }

    private void processAttribute(final DataObject assocObj,
                                  final Map<String, Object> assocAttrs,
                                  final Property property) {
        final String name = property.getName();

        // Teste Property: Es darf kein Key und muß ein simples Attribute sein
        if (property.isAttribute() && !property.isKeyProperty()) {
            final Object value = assocObj.get(name);
            assocAttrs.put(name, value);
        }
    }

    private static void setAttributesForLiveAssociation(
            final DataObject association, byte[] attributes) {
        if (attributes != null) {
            final ByteArrayInputStream data;
            final ObjectInputStream in;
            final Map<String, Object> assocAttrs;

            data = new ByteArrayInputStream(attributes);
            try {
                in = new ObjectInputStream(data);
                assocAttrs = (Map<String, Object>) in.readObject();
            } catch (IOException ex) {
                throw new UncheckedWrapperException(ex);
            } catch (ClassNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }

            if (assocAttrs != null) {
                for(Map.Entry<String, Object> entry : assocAttrs.entrySet()) {
                    if(association.getObjectType().hasDeclaredProperty(entry.getKey())
                            && association.getSession() != null) {
                        association.set(entry.getKey(), entry.getValue());
                    }
                    
                }
            }
        }
    }
}
