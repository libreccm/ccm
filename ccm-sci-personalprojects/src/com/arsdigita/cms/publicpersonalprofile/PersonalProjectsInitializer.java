package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.db.DbHelper;
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
public class PersonalProjectsInitializer extends CompoundInitializer {
    
    public PersonalProjectsInitializer() {
        
        final String jdbcUrl = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(jdbcUrl);
        
        add(new PDLInitializer(new ManifestSource("empty.pdl.mf",
                new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));        
    }
    
    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);
    }
    
}
