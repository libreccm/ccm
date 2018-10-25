package org.libreccm.export;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.WebConfig;

import com.fasterxml.jackson.core.JsonEncoding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class ExportManager {

    private static final ExportManager INSTANCE = new ExportManager();

    private List<AbstractDomainObjectsExporter<?>> exporters;

    private ExportManager() {
        exporters = new ArrayList<>();
    }

    public static ExportManager getInstance() {
        return INSTANCE;
    }

    public void exportData(final Path targetDirPath) {

        final Set<String> types = exporters
            .stream()
            .map(exporter -> exporter.convertsToType())
            .collect(Collectors.toSet());
        for (final String type : types) {
            try {
                final Path typeDirPath = targetDirPath.resolve(type);
                Files.createDirectories(typeDirPath);
            } catch (IOException ex) {
                throw new UncheckedWrapperException(ex);
            }
        }

        if (!Files.isDirectory(targetDirPath)) {
            throw new IllegalArgumentException(String.format(
                "Path %s does not point to a directory.",
                targetDirPath));
        }

        if (!Files.isWritable(targetDirPath)) {
            throw new IllegalArgumentException(String.format(
                "Path %s is not writable.",
                targetDirPath));
        }

        final Map<String, List<String>> exportedEntities = new HashMap<>();
        for (final AbstractDomainObjectsExporter< ?> exporter : exporters) {

            System.out.printf("Exporting entities of type \"%s\" and "
                                  + "converting them to \"%s\"...%n",
                              exporter.exportsBaseDataObjectType(),
                              exporter.convertsToType());
            final List<String> uuids = exporter
                .exportDomainObjects(targetDirPath);
            exportedEntities.put(exporter.convertsToType(), uuids);
            System.out.printf("Exported %d entities of type \"%s\".%n",
                              uuids.size(),
                              exporter.convertsToType());
        }

        final Path manifestFilePath = targetDirPath.resolve("ccm-export.json");
        final JsonFactory jsonFactory = new JsonFactory();
        try (final JsonGenerator manifestGenerator = jsonFactory
            .createGenerator(manifestFilePath.toFile(), JsonEncoding.UTF8)) {

            manifestGenerator.writeStartObject();

            manifestGenerator.writeStartObject();
            manifestGenerator.writeStringField(
                "created",
                LocalDateTime.now(ZoneId.of("UTC")).toString());
            manifestGenerator.writeStringField(
                "onServer",
                WebConfig.getInstanceOf().getSiteName());
            manifestGenerator.writeArrayFieldStart("types");

            for (final String type : exportedEntities.keySet()) {

                manifestGenerator.writeString(type);
            }

            manifestGenerator.writeEndObject();

            manifestGenerator.writeObjectFieldStart("entities");

            for (final Map.Entry<String, List<String>> entities
                     : exportedEntities.entrySet()) {

                manifestGenerator.writeArrayFieldStart(entities.getKey());

                for (final String uuid : entities.getValue()) {
                    manifestGenerator.writeString(uuid);
                }

                manifestGenerator.writeEndArray();

            }

            manifestGenerator.writeEndObject();

            manifestGenerator.writeEndObject();
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

    }

    public void registerExporter(final AbstractDomainObjectsExporter<?> exporter) {
        exporters.add(exporter);
    }

//    public void exportEntities(final List<DomainO> entities,
//                               final String exportName,
//                               final String targetDir) {
//
//        final Path targetDirPath = Paths.get(targetDir);
//
//        final Set<String> types = entities
//            .stream()
//            .map(entity -> entity.getClass().getName())
//            .collect(Collectors.toSet());
//
//        if (!Files.isDirectory(targetDirPath)) {
//            throw new IllegalArgumentException(String.format(
//                "Path %s does not point to a directory.",
//                targetDir));
//        }
//
//        if (!Files.isWritable(targetDirPath)) {
//            throw new IllegalArgumentException(String.format(
//                "Path %s is not writable.",
//                targetDir));
//        }
//
//        final Path exportDir = targetDirPath.resolve(exportName);
//        final Path exportDirPath;
//        try {
//            exportDirPath = Files.createDirectory(exportDir);
//        } catch (IOException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//        final Path manifestFilePath = exportDirPath.resolve("ccm-export.json");
//
//        final JsonFactory factory = new JsonFactory();
//        final Map<String, List<Exportable>> typeEntityMap;
//        try (final JsonGenerator manifestGenerator = factory
//            .createGenerator(manifestFilePath.toFile(),
//                             JsonEncoding.UTF8)) {
//
//            manifestGenerator.writeStartObject();
//            manifestGenerator.writeStringField(
//                "created",
//                LocalDateTime.now(ZoneId.of("UTC")).toString());
//            manifestGenerator.writeStringField(
//                "onServer",
//                WebConfig.getInstanceOf().getSiteName());
//            manifestGenerator.writeFieldName("types");
//            manifestGenerator.writeStartArray();
//            typeEntityMap = new HashMap<>();
//            for (final String type : types) {
//
//                final Path typePath = exportDirPath.resolve(type);
//                Files.createDirectory(typePath);
//                manifestGenerator.writeString(type);
//
//                final List<Exportable> entitiesOfType = entities
//                    .stream()
//                    .filter(entity -> entity.getClass().getName().equals(type))
//                    .collect(Collectors.toList());
//
//                typeEntityMap.put(type, entitiesOfType);
//            }
//
//            manifestGenerator.writeEndArray();
//
//            manifestGenerator.writeFieldName("entities");
//            manifestGenerator.writeStartObject();
//
//            for (final Map.Entry<String, List<Exportable>> entry : typeEntityMap
//                .entrySet()) {
//
//                final String type = entry.getKey();
//                final List<Exportable> entitiesOfType = entry.getValue();
//                final Path typeDirPath = exportDirPath.resolve(type);
//                Files.createDirectory(typeDirPath);
//                final List<String> exportedEntities = createExportedEntities(
//                    targetDirPath, type, entitiesOfType);
//
//                manifestGenerator.writeFieldName(type);
//                manifestGenerator.writeStartArray();
//                for (final String exportedEntity : exportedEntities) {
//                    manifestGenerator.writeString(exportedEntity);
//                }
//                manifestGenerator.writeEndArray();
//            }
//
//            manifestGenerator.writeEndObject();
//            manifestGenerator.writeEndObject();
//        } catch (IOException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//    }
//
//    private List<String> createExportedEntities(
//        final Path targetDir,
//        final String type,
//        final List<Exportable> entities) {
//
//        final List<String> exportedEntites = new ArrayList<>();
//
//        for (final Exportable entity : entities) {
//
//            exportEntity(targetDir, entity);
//            exportedEntites.add(entity.getUuid());
//        }
//
//        return exportedEntites;
//    }
//
//    private void exportEntity(final Path targetDir,
//                              final Exportable entity) {
//
////        if (EXPORTERS.containsKey(entity.getClass().getName())) {
////            throw new IllegalArgumentException(String.format(
////                "No exporter for type \"%s\" available.",
////                entity.getClass().getName()));
////        }
//        final Path entityFilePath = targetDir
//            .resolve(String.format("%s.json", entity.getUuid()));
//
////        final AbstractExporter<?> exporter = EXPORTERS
////            .get(entity.getClass().getName());
////        final String data = exporter.exportEntity(entity);
//        try {
//            final String data = objectMapper.writeValueAsString(entity);
//
//            Files.write(entityFilePath, data.getBytes(StandardCharsets.UTF_8));
//        } catch (IOException ex) {
//            throw new UncheckedWrapperException(ex);
//        }
//    }
}
