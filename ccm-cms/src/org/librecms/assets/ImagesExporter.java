package org.librecms.assets;

import com.arsdigita.cms.ReusableImageAsset;

import com.fasterxml.jackson.core.JsonGenerator;
import org.librecms.contentsection.AbstractBinaryAssetsExporter;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ImagesExporter
    extends AbstractBinaryAssetsExporter<ReusableImageAsset> {

    @Override
    protected void exportBinaryAssetProperties(
        final ReusableImageAsset asset, final JsonGenerator jsonGenerator)
        throws IOException {

        jsonGenerator.writeNumberField("width", asset.getWidth());
        jsonGenerator.writeNumberField("height", asset.getHeight());
    }

    @Override
    public Class<ReusableImageAsset> exportsType() {

        return ReusableImageAsset.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return ReusableImageAsset.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {

        return "org.librecms.assets.Image";
    }

}
