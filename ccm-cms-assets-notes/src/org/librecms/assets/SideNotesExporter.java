package org.librecms.assets;

import com.arsdigita.cms.contentassets.Note;
import com.arsdigita.util.UncheckedWrapperException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.core.AbstractCcmObjectsExporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SideNotesExporter
    extends AbstractCcmObjectsExporter<Note> {

    @Override
    public Class<Note> exportsType() {
        return Note.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Note.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.librecms.assets.SideNote";
    }

    @Override
    protected void exportObjectProperties(final Note note,
                                          final JsonGenerator jsonGenerator)
        throws IOException {

        jsonGenerator.writeObjectFieldStart("title");
        jsonGenerator.writeStringField(
            note.getOwner().getLanguage(), 
            note.getDisplayName());
        jsonGenerator.writeEndObject();
        
        jsonGenerator.writeObjectFieldStart("text");
        jsonGenerator.writeStringField(
            note.getOwner().getLanguage(), 
            note.getContent());
        jsonGenerator.writeEndObject();
    }

}
