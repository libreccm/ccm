package com.arsdigita.cms.contenttypes;

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

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectBundle extends GenericOrganizationalUnitBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciProjectBundle";
    public static final String SPONSORS = "sponsors";
    public static final String SPONSOR_ORDER = "sponsorOrder";
    public static final String SPONSOR_FUNDING_CODE = "sponsorFundingCode";

    public SciProjectBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        super.setName(primary.getName());
    }

    public SciProjectBundle(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public SciProjectBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciProjectBundle(final DataObject dobj) {
        super(dobj);
    }

    public SciProjectBundle(final String type) {
        super(type);
    }

    public SciProject getProject() {
        return (SciProject) getPrimaryInstance();
    }

    public SciProject getProject(final String language) {
        return (SciProject) getInstance(language);
    }

    public SciProjectSponsorCollection getSponsors() {
        return new SciProjectSponsorCollection((DataCollection) get(SPONSORS));
    }

    public void addSponsor(final GenericOrganizationalUnit sponsor) {
        addSponsor(sponsor, null);
    }

    public void addSponsor(final GenericOrganizationalUnit sponsor, final String fundingCode) {
        Assert.exists(sponsor, GenericOrganizationalUnit.class);

        final DataObject link = add(SPONSORS, sponsor.getGenericOrganizationalUnitBundle());

        link.set(SPONSOR_ORDER, Integer.valueOf((int) getSponsors().size()));
        link.set(SPONSOR_FUNDING_CODE, fundingCode);

        link.save();
    }

    public void removeSponsor(final GenericOrganizationalUnit sponsor) {
        Assert.exists(sponsor, GenericOrganizationalUnit.class);

        remove(SPONSORS, sponsor.getGenericOrganizationalUnitBundle());
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final SciProjectBundle projectBundle = (SciProjectBundle) source;

            if (SPONSORS.equals(property.getName())) {
                final DataCollection sponsors = (DataCollection) projectBundle.get(SPONSORS);

                while (sponsors.next()) {
                    createSponsorAssoc(sponsors);
                }

                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    private void createSponsorAssoc(final DataCollection sponsors) {
        final GenericOrganizationalUnitBundle sponsorDraft =
                                              (GenericOrganizationalUnitBundle) DomainObjectFactory.
                newInstance(sponsors.getDataObject());
        final GenericOrganizationalUnitBundle sponsorLive =
                                              (GenericOrganizationalUnitBundle) sponsorDraft.
                getLiveVersion();

        if (sponsorLive != null) {
            final DataObject link = add(SPONSORS, sponsorLive);

            link.set(SPONSOR_ORDER, sponsors.get("link." + SPONSOR_ORDER));

            link.save();
        }
    }

    @Override
    public boolean copyReverseProperty(final CustomCopy source,
                                       final ContentItem liveItem,
                                       final Property property,
                                       final ItemCopier copier) {
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            if ("sponsoredProjects".equals(property.getName())
                && (source instanceof GenericOrganizationalUnitBundle)) {
                final GenericOrganizationalUnitBundle sponsorBundle =
                                                      (GenericOrganizationalUnitBundle) source;
                final DataCollection sponsoredProjects = (DataCollection) sponsorBundle.get(
                        "sponsoredProjects");

                while (sponsoredProjects.next()) {
                    createSponsorProjectAssoc(sponsoredProjects,
                                              (GenericOrganizationalUnitBundle) liveItem);
                }

                return true;
            } else {
                return super.copyReverseProperty(source, liveItem, property, copier);
            }
        } else {
            return super.copyReverseProperty(source, liveItem, property, copier);
        }
    }

    private void createSponsorProjectAssoc(final DataCollection projects,
                                           final GenericOrganizationalUnitBundle sponsor) {
        final SciProjectBundle draftProject = (SciProjectBundle) DomainObjectFactory.newInstance(
                projects.getDataObject());
        final SciProjectBundle liveProject = (SciProjectBundle) draftProject.getLiveVersion();

        if (liveProject != null) {
            final DataObject link = sponsor.add("sponsoredProjects", liveProject);

            link.set(SPONSOR_ORDER, projects.get("link." + SPONSOR_ORDER));

            link.save();

            XMLDeliveryCache.getInstance().removeFromCache(liveProject.getOID());
        }
    }

}
