package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfilesInitializer extends CompoundInitializer {

    public PublicPersonalProfilesInitializer() {
        final String jdbcUrl = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(jdbcUrl);

        add(new PDLInitializer(new ManifestSource("empty.pdl.mf",
                                                  new NameFilter(DbHelper.
                getDatabaseSuffix(database), "pdl"))));
    }

    @Override
    public void init(DomainInitEvent event) {
        super.init(event);

        DomainObjectFactory.registerInstantiator(
                PublicPersonalProfiles.BASE_DATA_OBJECT_TYPE,
                                                 new ACSObjectInstantiator() {

            @Override
            public DomainObject doNewInstance(
                    final DataObject dataObject) {
                return new PublicPersonalProfiles(dataObject);
            }
        });
    }
}
