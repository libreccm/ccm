package org.libreccm.export;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.WebConfig;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for exporters for migration to LibreCCM 7.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The subclass of {@link DomainObject} the implementation handles.
 */
public abstract class AbstractDomainObjectsExporter<T extends DomainObject> {

    final WebConfig webConfig = WebConfig.getInstanceOf();

    /**
     * Provides the class of the domain object which is handled by the
     * implementation.
     *
     * @return
     */
    public abstract Class<T> exportsType();

    /**
     * Provides the base data object type of the domain object handled by the
     * implementation. This information is used by
     * {@link #exportDomainObjects(java.nio.file.Path)} to retrieve the domain
     * objects to export.
     *
     * @return
     */
    public abstract String exportsBaseDataObjectType();

    /**
     * Provides the fully qualified class name of the type to which the domain
     * objects handled by the implementation are converted.
     *
     * @return
     */
    public abstract String convertsToType();

    /**
     * The implementation of this method is supposed to do the export for a
     * single entity. To generate the path of the target file
     * {@link #generateTargetFilePath(java.nio.file.Path, java.lang.String)} can
     * be used. A single domain object can be split into several objects by the
     * exporter.
     *
     * @param domainObject The domain object to export.
     * @param targetDir    The target directory for the export
     *
     * @return The UUIDs of the exported objects.
     */
    protected abstract List<String> exportDomainObject(final T domainObject,
                                                       final Path targetDir);

    /**
     * Helper for method for generating an UUID for an entity. In LibreCCM 7
     * UUIDs are used for identifying entities. To avoid collision are the keep
     * the UUID the same for between runs of the export we use name based UUIDs
     * (variant 3) here. The UUID is generated from the OID of the domain object
     * to export. More exactly first a string containing the name of the site as
     * returned by {@link WebConfig#getSiteName()} and the string representation
     * of the OID, separated by {@code '/'} is created. From this string the
     * UUID is generated.
     *
     * Exporter for association objects like
     * {@code org.librecms.categorization.Categorization} should not use this
     * method. Instead they should generate the UUID from the site name and the
     * OIDs of the entities associated by the object.
     *
     * @param forDomainObject The {@link DomainObject} for which the UUID is
     *                        generated.
     *
     * @return The UUID of the {@link DomainObject}.
     */
    protected final String generateUuid(final DomainObject forDomainObject) {

        final String uuidSource = String.format(
            "%s/%s",
            webConfig.getSiteName(),
            forDomainObject.getOID().toString());
        final byte[] uuidSourceBytes = uuidSource
            .getBytes(StandardCharsets.UTF_8);
        return UUID.nameUUIDFromBytes(uuidSourceBytes).toString();
    }

    /**
     * Helper method for generating the target file path for the export of an
     * entity.
     *
     * @param targetDir The target directory.
     * @param uuid      The UUID of the entity.
     *
     * @return The path for target file.
     */
    protected final Path generateTargetFilePath(final Path targetDir,
                                                final String uuid) {

        return generateTargetFilePath(targetDir, convertsToType(), uuid);
    }

    protected final Path generateTargetFilePath(final Path targetDir,
                                                final String targetType,
                                                final String uuid) {

        return targetDir
            .resolve(targetType)
            .resolve(String.format("%s.json", uuid));
    }

    /**
     * Retrieves all {@link DomainObject}s of the type returned by
     * {@link #exportsBaseDataObjectType()} and calls
     * {@link #exportDomainObject(com.arsdigita.domain.DomainObject, java.nio.file.Path)}
     * for each of them.
     * 
     * @param targetDir target directory for the export.
     * @return The list of uuids of the the exported entites.
     */
    @SuppressWarnings("unchecked")
    public final List<String> exportDomainObjects(final Path targetDir) {

        final Session session = SessionManager.getSession();
        final DataCollection dataCollection = session
            .retrieve(exportsBaseDataObjectType());

        final List<T> domainObjects = new ArrayList<>();
        while (dataCollection.next()) {

            final DataObject dataObject = dataCollection.getDataObject();
            final DomainObject domainObject = DomainObjectFactory
                .newInstance(dataObject);

            if (!(exportsType().isAssignableFrom(domainObject.getClass()))) {
                throw new ExportException(String.format(
                    "DomainObject is not of type \"%s\" but of type \"%s\".",
                    exportsType().getName(),
                    domainObject.getClass().getName()));
            }

            domainObjects.add((T) domainObject);
        }

        final List<String> uuids = new ArrayList<>();
        for (final T domainObject : domainObjects) {
            final List<String> createdUuids = exportDomainObject(domainObject,
                                                                 targetDir);
            uuids.addAll(createdUuids);
        }

        return uuids;
    }

}
