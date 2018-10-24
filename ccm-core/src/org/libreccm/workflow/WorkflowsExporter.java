package org.libreccm.workflow;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class WorkflowsExporter extends AbstractDomainObjectsExporter<Workflow> {

    @Override
    public Class<Workflow> exportsType() {

        return Workflow.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return Workflow.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {

        return "org.libreccm.workflow.Workflow";
    }

    @Override
    protected List<String> exportDomainObject(
        final Workflow domainObject, final Path targetDir) {

        final String uuid = generateUuid(domainObject);
        final Path targetFilePath = targetDir
            .resolve("org.libreccm.workflow.Workflow")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("workflowId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            jsonGenerator.writeBooleanField(
                "abstractWorkflow",
                domainObject instanceof WorkflowTemplate);
            
            jsonGenerator.writeObjectFieldStart("name");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(), 
                domainObject.getDisplayName());
            jsonGenerator.writeEndObject();
            
            jsonGenerator.writeObjectFieldStart("description");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(), 
                domainObject.getDescription());
            jsonGenerator.writeEndObject();
            
            jsonGenerator.writeStringField("state", 
                                           domainObject.getStateString());
            
            jsonGenerator.writeBooleanField("active", domainObject.isActive());
            
            final ACSObject object = domainObject.getObject();
            final String objectUuid = generateUuid(object);
            jsonGenerator.writeStringField("object", objectUuid);
            
            jsonGenerator.writeEndObject();

        } catch (IOException ex) {

        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
