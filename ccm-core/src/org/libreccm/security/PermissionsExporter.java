package org.libreccm.security;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.kernel.permissions.Permission;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.WebConfig;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PermissionsExporter extends AbstractDomainObjectsExporter<Permission> {

    @Override
    public Class<Permission> exportsType() {
        return Permission.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Permission.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.security.Permission";
    }

    private String exportPermission(final String privilege,
                                    final OID roleOid,
                                    final OID objectOid,
                                    final Path targetDir) {

        final byte[] uuidSource = String.format(
            "%s/%s-%s-%s",
            WebConfig.getInstanceOf().getSiteName(),
            privilege,
            roleOid.toString(),
            objectOid.toString())
            .getBytes(StandardCharsets.UTF_8);
        final String uuid = UUID.nameUUIDFromBytes(uuidSource).toString();

        final byte[] roleUuidSource = String.format(
            "%s/%s",
            WebConfig.getInstanceOf().getSiteName(),
            roleOid.toString())
            .getBytes(StandardCharsets.UTF_8);
        final String roleUuid = UUID
            .nameUUIDFromBytes(roleUuidSource)
            .toString();

        final byte[] objectUuidSource = String.format(
            "%s/%s",
            WebConfig.getInstanceOf().getSiteName(),
            objectOid.toString())
            .getBytes(StandardCharsets.UTF_8);
        final String objectUuid = UUID
            .nameUUIDFromBytes(objectUuidSource).toString();

        final Path targetFilePath = generateTargetFilePath(
            targetDir, "org.libreccm.security.Permission", uuid);

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFilePath.toFile(), JsonEncoding.UTF8)) {

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumber(IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            jsonGenerator.writeStringField("grantedPrivilege", privilege);

            jsonGenerator.writeStringField("grantee", roleUuid);
            jsonGenerator.writeStringField("object", objectUuid);

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return uuid;
    }

    @Override
    protected List<String> exportDomainObject(final Permission domainObject,
                                              final Path targetDir) {

        final long partyId = ((Number) domainObject
                              .getPartyOID()
                              .get("id")).longValue();
        if (-204 == partyId || -300 == partyId || -200 == partyId) {
            // Skip internal permissions
            return Collections.emptyList();
        }

        final OID objectOid = domainObject.getACSObject();

        final OID granteeOid = domainObject.getPartyOID();
        final DomainObject granteeDomainObj = DomainObjectFactory
            .newInstance(granteeOid);

        final List<String> permissionUuids = new ArrayList<>();
        if (granteeDomainObj instanceof Group) {

            final Group group = (Group) granteeDomainObj;
            final RoleCollection roles = group.getRoles();

            final String privilege = domainObject.getPrivilege().getName();

            while (roles.next()) {

                final OID roleOid = roles.getRole().getOID();

                final String permissionUuid = exportPermission(privilege,
                                                               objectOid,
                                                               roleOid,
                                                               targetDir);
                permissionUuids.add(permissionUuid);
            }
        }

        return permissionUuids;
    }

}
