package com.arsdigita.cms.scimember.navigation;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SciMemberInitializer extends CompoundInitializer {

    public SciMemberInitializer() {

        super();

        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource(
            "empty.pdl.mf",
            new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    @Override
    public void init(final DomainInitEvent event) {

        super.init(event);
    }

}
