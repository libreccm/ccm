package com.arsdigita.london.importer.cms;

import com.arsdigita.london.importer.DomainObjectMapper;

import com.arsdigita.cms.Asset;
import com.arsdigita.cms.CMS;

import java.io.File;

import org.apache.log4j.Logger;

/**
 *   Asset importer, handling the &lt;cms:asset&gt; XML subblock.
 *
 *  @see com.arsdigita.london.importer
 */
public class AssetParser extends ItemParser {
    private static Logger s_log =
        Logger.getLogger(AssetParser.class);

    public AssetParser(File lobDir, DomainObjectMapper mapper) {
        this("asset", CMS.CMS_XML_NS, Asset.BASE_DATA_OBJECT_TYPE, lobDir, mapper);
    }

    public AssetParser(String tagName,
                       String tagURI,
                       String objectType,
                       File lobDir,
                       DomainObjectMapper mapper) {
        super(tagName, tagURI, objectType, lobDir, mapper);
    }
}


