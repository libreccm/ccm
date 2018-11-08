package org.librecms.assets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.WebConfig;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FileAssetsExporter
    extends AbstractDomainObjectsExporter<ContentItem> {

    @Override
    public Class<ContentItem> exportsType() {
        return ContentItem.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return ContentItem.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.librecms.assets.FileAsset";
    }

    @Override
    protected boolean includeSubTypes() {
        return true;
    }
    
    

    @Override
    protected List<String> exportDomainObject(
        final ContentItem item,
        final Path targetDir) {

        final DataCollection fileAttachments = FileAttachment
            .getAttachments(item);

        final List<String> uuids = new ArrayList<>();
        while (fileAttachments.next()) {

            final String uuid = exportFileAsset(
                fileAttachments.getDataObject(),
                targetDir);
            uuids.add(uuid);
        }

        return uuids;
    }

    private String exportFileAsset(final DataObject dataObj,
                                   final Path targetDir) {

        final FileAttachment fileAttachment = new FileAttachment(dataObj);
        final String uuid = generateFileAssetUuid(fileAttachment);

        final Path targetFilePath = generateTargetFilePath(targetDir, uuid);
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            jsonGenerator.writeStartObject();
            
            jsonGenerator.writeNumberField("objectId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            jsonGenerator.writeStringField("displayName",
                                           fileAttachment.getDisplayName());

            jsonGenerator.writeObjectFieldStart("description");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                fileAttachment.getDescription());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeStringField(
                "mimeType",
                fileAttachment.getMimeType().toString());

            jsonGenerator.writeStringField("fileName",
                                           fileAttachment.getName());

            jsonGenerator.writeObjectFieldStart("title");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                fileAttachment.getName());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeBinaryField("data", fileAttachment.getContent());

            jsonGenerator.writeNumberField("size", fileAttachment.getSize());

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return uuid;
    }

    protected static String generateFileAssetUuid(
        final FileAttachment fileAttachment) {

        final byte[] uuidSource = String.format(
            "%s/files/%s",
            WebConfig.getInstanceOf().getSiteName(),
            fileAttachment.getOID().toString())
            .getBytes(StandardCharsets.UTF_8);
        final String uuid = UUID.nameUUIDFromBytes(uuidSource).toString();

        return uuid;
    }

//    extends AbstractBinaryAssetsExporter<FileAttachment> {
//
//    @Override
//    protected void exportBinaryAssetProperties(
//        final FileAttachment asset,
//        final JsonGenerator jsonGenerator)
//        throws IOException {
//
//        // Nothing
//    }
//
//    @Override
//    public Class<FileAttachment> exportsType() {
//
//        return FileAttachment.class;
//    }
//
//    @Override
//    public String exportsBaseDataObjectType() {
//
//        return FileAttachment.BASE_DATA_OBJECT_TYPE;
//    }
//
//    @Override
//    public String convertsToType() {
//
//        return "org.librecms.assets.FileAsset";
//    }
}
