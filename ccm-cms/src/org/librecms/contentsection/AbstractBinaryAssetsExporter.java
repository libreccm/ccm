package org.librecms.contentsection;


import com.arsdigita.cms.BinaryAsset;
import com.arsdigita.kernel.KernelConfig;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractBinaryAssetsExporter<T extends BinaryAsset> 
    extends AbstractAssetsExporter<T>{

    @Override
    protected final void exportAssetProperties(
        final T asset, final JsonGenerator jsonGenerator) throws IOException{

        jsonGenerator.writeObjectFieldStart("description");
        jsonGenerator.writeStringField(
            KernelConfig.getConfig().getDefaultLanguage(),
            asset.getDescription());
        jsonGenerator.writeEndObject();
        
        jsonGenerator.writeStringField("mimeType", 
                                       asset.getMimeType().toString());
        
        jsonGenerator.writeStringField("fileName", asset.getName());
        
        jsonGenerator.writeBinaryField("data", asset.getContent());
        
        jsonGenerator.writeNumberField("size", asset.getSize());
        
        exportBinaryAssetProperties(asset, jsonGenerator);
    }
    
    protected abstract void exportBinaryAssetProperties(
    final T asset, final JsonGenerator jsonGenerator) throws IOException;
    
}
