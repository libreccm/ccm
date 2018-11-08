package org.librecms.assets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.contentassets.ItemImageAttachment;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.WebConfig;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;
import org.librecms.contentsection.AbstractAttachmentListsExporter;

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
public class ImageAttachmentsExporter
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

        return "org.librecms.contentsection.ItemAttachment";
    }

    @Override
    protected boolean includeSubTypes() {
        return true;
    }

    @Override
    protected List<String> exportDomainObject(
        final ContentItem item, final Path targetDir) {

        final String listUuid = AbstractAttachmentListsExporter
            .generateListUuid(item, "images");

        final List<String> attachmentsUuids = new ArrayList<>();

        final DataCollection imageAttachments = ItemImageAttachment
            .getImageAttachments(item);

        long sortKey = 0;
        while (imageAttachments.next()) {

            sortKey++;
            final String uuid = exportImageAttachment(
                imageAttachments.getDataObject(),
                listUuid,
                sortKey,
                targetDir);
            attachmentsUuids.add(uuid);
        }

        return attachmentsUuids;
    }

    private String exportImageAttachment(final DataObject dataObj,
                                         final String listUuid,
                                         final long sortKey,
                                         final Path targetDir) {

        final ItemImageAttachment imageAttachment = new ItemImageAttachment(
            dataObj);
        final ReusableImageAsset imageAsset = imageAttachment.getImage();
        final String imageAssetUuid = generateUuid(imageAsset);

        final byte[] uuidSource = String.format(
            "%s/images/%s",
            WebConfig.getInstanceOf().getSiteName(),
            imageAttachment.getOID().toString())
            .getBytes(StandardCharsets.UTF_8);
        final String uuid = UUID.nameUUIDFromBytes(uuidSource).toString();

        final Path targetFilePath = generateTargetFilePath(targetDir, uuid);
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("attachmentId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            jsonGenerator.writeStringField("attachmentList", listUuid);
            jsonGenerator.writeStringField("asset", imageAssetUuid);

            jsonGenerator.writeNumberField("sortKey", sortKey);

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {

            throw new UncheckedWrapperException(ex);
        }

        return uuid;
    }

}
