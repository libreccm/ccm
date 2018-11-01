package org.librecms.contentsection;

import com.arsdigita.cms.ContentItem;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractAttachmentListsExporter
    extends AbstractDomainObjectsExporter<ContentItem> {

//    public static String generateFileAttachmentsListUuid(
//        final ContentItem item) {
//
//        return generateListUuid(item, "files");
//    }
//
//    public static String generateImagesListUuid(
//        final ContentItem item) {
//
//        return generateListUuid(item, "images");
//    }
//
//    public static String generateLinksListUuid(
//        final ContentItem item) {
//
//        return generateListUuid(item, "links");
//    }
//
//    public static String generateSideNotesListUuid(
//        final ContentItem item) {
//
//        return generateListUuid(item, "side-notes");
//    }

    public final static String generateListUuid(final ContentItem item,
                                           final String listName) {

        final byte[] uuidSource = String.format(
            "%s/%s-%s",
            WebConfig.getInstanceOf().getSiteName(),
            Objects.requireNonNull(item).getOID().toString(),
            listName)
            .getBytes(StandardCharsets.UTF_8);

        return UUID.nameUUIDFromBytes(uuidSource).toString();
    }

    @Override
    public Class<ContentItem> exportsType() {
        return ContentItem.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return ContentItem.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    protected boolean includeSubTypes() {
        return true;
    }

    @Override
    public String convertsToType() {
        return "org.librecms.contentsection.AttachmentList";
    }
    
    protected abstract String getListName();
    
    protected abstract boolean hasList(ContentItem contentItem);

    @Override
    protected final List<String> exportDomainObject(
        final ContentItem contentItem, final Path targetDir) {

        if (!hasList(contentItem)) {
            return Collections.emptyList();
        }
        
        final String uuid = generateListUuid(contentItem, getListName());
        final Path listTargetFilePath = generateTargetFilePath(targetDir, uuid);
        final File listTargetFile = listTargetFilePath.toFile();
        
        final JsonFactory jsonFactory = new JsonFactory();
        try(JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(listTargetFile, JsonEncoding.UTF8)) {
            
        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("listId",
                                       IdSequence.getInstance().nextId());
        jsonGenerator.writeStringField("uuid", uuid);

        jsonGenerator.writeStringField("contentItem", generateUuid(contentItem));

        jsonGenerator.writeStringField("name", getListName());

        jsonGenerator.writeNumberField("order", 1);

        jsonGenerator.writeEndObject();
            
        } catch(IOException ex) {
            throw new UncheckedWrapperException(ex);
        }
        
        return Arrays.asList(new String[]{uuid});
        
//        final String fileAttachmentsListUuid = generateFileAttachmentsListUuid(
//            contentItem);
//        final String imagesListUuid = generateImagesListUuid(contentItem);
//        final String linksListUuid = generateLinksListUuid(contentItem);
//        final String sideNotesListUuid = generateSideNotesListUuid(contentItem);
//
//        final Path filesListTargetFilePath = generateTargetFilePath(
//            targetDir, fileAttachmentsListUuid);
//        final Path imagesListTargetFilePath = generateTargetFilePath(
//            targetDir, imagesListUuid);
//        final Path linksListTargetFilePath = generateTargetFilePath(
//            targetDir, linksListUuid);
//        final Path sideNotesListTargetFilePath = generateTargetFilePath(
//            targetDir, sideNotesListUuid);
//
//        final JsonFactory jsonFactory = new JsonFactory();
//
//        final File filesListTargetFile = filesListTargetFilePath.toFile();
//        try (JsonGenerator jsonGenerator = jsonFactory
//            .createGenerator(filesListTargetFile, JsonEncoding.UTF8)) {
//
//            writeAttachementsList(
//                fileAttachmentsListUuid,
//                "files",
//                1,
//                jsonGenerator,
//                contentItem);
//
//        } catch (IOException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//
//        final File imagesListTargetFile = imagesListTargetFilePath.toFile();
//        try (JsonGenerator jsonGenerator = jsonFactory
//            .createGenerator(imagesListTargetFile, JsonEncoding.UTF8)) {
//
//            writeAttachementsList(
//                imagesListUuid,
//                "images",
//                1,
//                jsonGenerator,
//                contentItem);
//
//        } catch (IOException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//
//        final File linksListTargetFile = linksListTargetFilePath.toFile();
//        try (JsonGenerator jsonGenerator = jsonFactory
//            .createGenerator(linksListTargetFile, JsonEncoding.UTF8)) {
//
//            writeAttachementsList(
//                linksListUuid,
//                "links",
//                1,
//                jsonGenerator,
//                contentItem);
//
//        } catch (IOException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//
//        final File sideNotesListTargetFile = sideNotesListTargetFilePath
//            .toFile();
//        try (JsonGenerator jsonGenerator = jsonFactory
//            .createGenerator(sideNotesListTargetFile, JsonEncoding.UTF8)) {
//
//            writeAttachementsList(
//                sideNotesListUuid,
//                "sideNotes",
//                1,
//                jsonGenerator,
//                contentItem);
//
//        } catch (IOException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//
//        return Arrays.asList(new String[]{
//            fileAttachmentsListUuid,
//            imagesListUuid,
//            linksListUuid,
//            sideNotesListUuid
//        });
    }

//    private void writeAttachementsList(final String listUuid,
//                                       final String name,
//                                       final long order,
//                                       final JsonGenerator jsonGenerator,
//                                       final ContentItem item)
//        throws IOException {
//
//        jsonGenerator.writeStartObject();
//
//        jsonGenerator.writeNumberField("listId",
//                                       IdSequence.getInstance().nextId());
//        jsonGenerator.writeStringField("uuid", listUuid);
//
//        jsonGenerator.writeStringField("contentItem", generateUuid(item));
//
//        jsonGenerator.writeStringField("name", name);
//
//        jsonGenerator.writeNumberField("order", order);
//
//        jsonGenerator.writeEndObject();
//    }

}
