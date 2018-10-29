package org.librecms.contentsection;

import com.arsdigita.cms.Asset;
import com.arsdigita.kernel.KernelConfig;

import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.core.AbstractCcmObjectsExporter;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractAssetsExporter<T extends Asset>
    extends AbstractCcmObjectsExporter<T> {

    @Override
    protected final void exportObjectProperties(final T asset,
                                                final JsonGenerator jsonGenerator)
        throws IOException {

        jsonGenerator.writeObjectFieldStart("title");
        jsonGenerator.writeStringField(
            KernelConfig.getConfig().getDefaultLanguage(),
            asset.getName());
        jsonGenerator.writeEndObject();

        exportAssetProperties(asset, jsonGenerator);
    }

    protected abstract void exportAssetProperties(T asset,
                                                  JsonGenerator jsonGenerator)
        throws IOException;

}
