package com.arsdigita.cms.scipublications;

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
 */
public class SciPublicationsInitializer extends CompoundInitializer {

    public SciPublicationsInitializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource("empty.pdl.mf",
                                                  new NameFilter((DbHelper.
                                                                  getDatabaseSuffix(
                                                                  database)),
                                                                 "pdl"))));

    }

    @Override
    public void init(DomainInitEvent e) {
        super.init(e);

        DomainObjectFactory.registerInstantiator(
                SciPublications.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
            @Override
            public DomainObject doNewInstance(final DataObject dataObject) {
                return new SciPublications(dataObject);
            }

        });
    }

}
