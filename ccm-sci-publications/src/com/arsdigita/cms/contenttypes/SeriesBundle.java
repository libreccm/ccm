package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.XMLDeliveryCache;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SeriesBundle extends ContentBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SeriesBundle";
    public static final String EDITORS = "editors";
    public static final String EDITOR_FROM = "dateFrom";
    public static final String EDITOR_TO = "dateTo";
    public static final String EDITOR_FROM_SKIP_MONTH = "dateFromSkipMonth";
    public static final String EDITOR_FROM_SKIP_DAY = "dateFromSkipDay";
    public static final String EDITOR_TO_SKIP_MONTH = "dateToSkipMonth";
    public static final String EDITOR_TO_SKIP_DAY = "dateToSkipDay";
    public static final String EDITOR_ORDER = "editor_order";
    public static final String PUBLICATIONS = "publications";
    public static final String VOLUME_OF_SERIES = "volumeOfSeries";

    public SeriesBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public SeriesBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SeriesBundle(final BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SeriesBundle(final DataObject dobj) {
        super(dobj);
    }

    public SeriesBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();

        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final SeriesBundle seriesBundle = (SeriesBundle) source;

            if (EDITORS.equals(attribute)) {
                final DataCollection editors =
                                     (DataCollection) seriesBundle.get(EDITORS);

                while (editors.next()) {
                    createEditorAssoc(editors);
                }

                return true;
            } else if (PUBLICATIONS.equals(attribute)) {
                final DataCollection publications =
                                     (DataCollection) seriesBundle.get(
                        PUBLICATIONS);

                while (publications.next()) {
                    createPublicationAssoc(publications);
                }

                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    private void createEditorAssoc(final DataCollection editors) {
        final GenericPersonBundle draftPerson =
                                  (GenericPersonBundle) DomainObjectFactory.
                newInstance(editors.getDataObject());
        final GenericPersonBundle livePerson =
                                  (GenericPersonBundle) draftPerson.
                getLiveVersion();

        if (livePerson != null) {
            final DataObject link = add(EDITORS, livePerson);

            link.set(EDITOR_FROM, editors.get("link." + EDITOR_FROM));
            link.set(EDITOR_FROM_SKIP_MONTH,
                     editors.get("link." + EDITOR_FROM_SKIP_MONTH));
            link.set(EDITOR_FROM_SKIP_DAY,
                     editors.get("link." + EDITOR_FROM_SKIP_DAY));
            link.set(EDITOR_TO, editors.get("link." + EDITOR_TO));
            link.set(EDITOR_TO_SKIP_MONTH,
                     editors.get("link." + EDITOR_TO_SKIP_MONTH));
            link.set(EDITOR_TO_SKIP_DAY,
                     editors.get("link." + EDITOR_TO_SKIP_DAY));
            link.set(EDITOR_ORDER, editors.get("link." + EDITOR_ORDER));

            link.save();
        }
    }

    private void createPublicationAssoc(final DataCollection publications) {
        final PublicationBundle draftPublication =
                                (PublicationBundle) DomainObjectFactory.
                newInstance(publications.getDataObject());
        final PublicationBundle livePublication =
                                (PublicationBundle) draftPublication.
                getLiveVersion();

        if (livePublication != null) {
            final DataObject link = add(PUBLICATIONS, livePublication);

            link.set(VOLUME_OF_SERIES,
                     publications.get("link." + VOLUME_OF_SERIES));

            link.save();
        }
    }

    @Override
    public boolean copyReverseProperty(final CustomCopy source,
                                       final ContentItem liveItem,
                                       final Property property,
                                       final ItemCopier copier) {
        final String attribute = property.getName();

        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            if (("series".equals(attribute))
                && (source instanceof GenericPersonBundle)) {

                final GenericPersonBundle editorBundle =
                                          (GenericPersonBundle) source;
                final DataCollection series = (DataCollection) editorBundle.get(
                        "series");

                while (series.next()) {
                    createSeriesAssoc(series,
                                      (GenericPersonBundle) liveItem);
                }

                return true;
            } else {
                return super.copyReverseProperty(source,
                                                 liveItem,
                                                 property,
                                                 copier);
            }
        } else {
            return super.copyReverseProperty(source, liveItem, property, copier);
        }
    }

    private void createSeriesAssoc(final DataCollection series,
                                   final GenericPersonBundle editor) {
        final SeriesBundle draftSeries = (SeriesBundle) DomainObjectFactory.
                newInstance(series.getDataObject());
        final SeriesBundle liveSeries = (SeriesBundle) draftSeries.
                getLiveVersion();

        if (liveSeries != null) {
            final DataObject link = editor.add("series", liveSeries);

            link.set(EDITOR_FROM, series.get("link." + EDITOR_FROM));
            link.set(EDITOR_FROM_SKIP_MONTH,
                     series.get("link." + EDITOR_FROM_SKIP_MONTH));
            link.set(EDITOR_FROM_SKIP_DAY,
                     series.get("link." + EDITOR_FROM_SKIP_DAY));
            link.set(EDITOR_TO, series.get("link." + EDITOR_TO));
            link.set(EDITOR_TO_SKIP_MONTH,
                     series.get("link." + EDITOR_TO_SKIP_MONTH));
            link.set(EDITOR_TO_SKIP_DAY,
                     series.get("link." + EDITOR_TO_SKIP_DAY));
            link.set(EDITOR_ORDER, series.get("link." + EDITOR_ORDER));

            link.save();
            
            XMLDeliveryCache.getInstance().removeFromCache(liveSeries.getOID());
        }
    }

    public EditshipCollection getEditors() {
        return new EditshipCollection((DataCollection) get(EDITORS));
    }

    public void addEditor(final GenericPerson editor,
                          final Date from,
                          final Boolean fromSkipMonth,
                          final Boolean fromSkipDay,
                          final Date to,
                          final Boolean toSkipMonth,
                          final Boolean toSkipDay) {
        Assert.exists(editor, GenericPerson.class);

        final DataObject link = add(EDITORS, editor.getGenericPersonBundle());

        link.set(EDITOR_FROM, from);
        link.set(EDITOR_FROM_SKIP_MONTH, fromSkipMonth);
        link.set(EDITOR_FROM_SKIP_DAY, fromSkipDay);
        link.set(EDITOR_TO, to);
        link.set(EDITOR_TO_SKIP_MONTH, toSkipMonth);
        link.set(EDITOR_TO_SKIP_DAY, toSkipDay);
        link.set(EDITOR_ORDER, Integer.valueOf((int) getEditors().size()));

        link.save();
    }

    public void removeEditor(final GenericPerson editor) {
        Assert.exists(editor, GenericPerson.class);

        remove(EDITORS, editor.getGenericPersonBundle());
    }

    public VolumeInSeriesCollection getVolumes() {
        return new VolumeInSeriesCollection((DataCollection) get(PUBLICATIONS));
    }

    public void addVolume(final Publication publication,
                          final Integer volume) {
        Assert.exists(publication, Publication.class);

        DataObject link = add(PUBLICATIONS, publication.getPublicationBundle());

        link.set(VOLUME_OF_SERIES, volume);
    }

    public void removeVolume(Publication publication) {
        Assert.exists(publication, Publication.class);
        remove(PUBLICATIONS, publication.getPublicationBundle());
    }
}
