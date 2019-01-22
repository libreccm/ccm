package org.libreccm.categorization;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.web.WebConfig;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DomainOwnershipsExporter
    extends AbstractDomainObjectsExporter<Domain> {
    
    @Override
    public Class<Domain> exportsType() {
        return Domain.class;
    }
    
    @Override
    public String exportsBaseDataObjectType() {
        return Domain.BASE_DATA_OBJECT_TYPE;
    }
    
    @Override
    public String convertsToType() {
        return "org.libreccm.categorization.DomainOwnership";
    }
    
    @Override
    protected List<String> exportDomainObject(final Domain domainObject,
                                              final Path targetDir) {
        
        final DomainCollection useContexts = domainObject.getUseContexts();
        
        final List<String> uuids = new ArrayList<>();
        while (useContexts.next()) {
            
            final String uuid = generateDomainOwnership(useContexts
                .getDomainObject(),
                                                        domainObject,
                                                        targetDir);
            uuids.add(uuid);
        }
        
        return uuids;
    }
    
    private String generateDomainOwnership(final DomainObject useContext,
                                           final Domain domain,
                                           final Path targetDir) {
        
        final DomainObject owner = DomainObjectFactory
            .newInstance((DataObject) useContext.get("categoryOwner"));
        final String context = (String) useContext.get("useContext");
        
        final byte[] uuidSource = String.format(
            "%s/%s-%s-%s",
            WebConfig.getInstanceOf().getSiteName(),
            owner.getOID().toString(),
            useContext.getOID().toString(),
            domain.getOID().toString())
            .getBytes(StandardCharsets.UTF_8);
        final String uuid = UUID.nameUUIDFromBytes(uuidSource).toString();
                final Path targetFilePath = targetDir
            .resolve("org.libreccm.categorization.Categorization")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {
            
            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();
            
            jsonGenerator.writeNumberField("ownershipId", 
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            
            jsonGenerator.writeStringField("domain",
                                           generateUuid(domain));
            jsonGenerator.writeStringField("owner", generateUuid(owner));
            jsonGenerator.writeStringField("context", context);
            
            jsonGenerator.writeEndObject();
        } catch(IOException ex) {
            throw new UncheckedIOException(ex);
        }
        
        return uuid;
    }
    
}
