package org.librecms.assets;

import com.arsdigita.cms.contentassets.FileAttachment;

import com.fasterxml.jackson.core.JsonGenerator;
import org.librecms.contentsection.AbstractBinaryAssetsExporter;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FileAssetsExporter 
    extends AbstractBinaryAssetsExporter<FileAttachment> {

    @Override
    protected void exportBinaryAssetProperties(
        final FileAttachment asset,
        final JsonGenerator jsonGenerator)
        throws IOException {

        // Nothing
    }

    @Override
    public Class<FileAttachment> exportsType() {

        return FileAttachment.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return FileAttachment.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {

        return "org.librecms.assets.FileAsset";
    }

}
