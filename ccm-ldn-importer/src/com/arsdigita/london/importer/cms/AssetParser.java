package com.arsdigita.london.importer.cms;

import java.io.File;

import com.arsdigita.cms.Asset;
import com.arsdigita.cms.CMS;
import com.arsdigita.london.importer.DomainObjectMapper;

/**
 *   Asset importer, handling the &lt;cms:asset&gt; XML subblock.
 *
 *  @see com.arsdigita.london.importer
 */
public class AssetParser extends ItemParser {

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


