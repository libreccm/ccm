package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import com.arsdigita.cms.contenttypes.GenericPerson;
import java.util.List;

/**
 *
 * @author jensp
 */
public interface BibTeXBuilder {

    void addAuthor(GenericPerson author);

    void addEditor(GenericPerson editor);

    void setField(BibTeXField name, String value) throws UnsupportedFieldException;

    String getBibTeXType();

    String toBibTeX();
    
}
