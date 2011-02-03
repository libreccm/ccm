package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jensp
 */
public class BookBuilder extends AbstractBibTeXBuilder {

    private final BibTeXField[] mandatoryFields = {BibTeXField.AUTHOR,
                                                   BibTeXField.TITLE,
                                                   BibTeXField.PUBLISHER,
                                                   BibTeXField.YEAR};
    private final BibTeXField[] supportedFields = {BibTeXField.AUTHOR,
                                                   BibTeXField.TITLE,
                                                   BibTeXField.PUBLISHER,
                                                   BibTeXField.YEAR,
                                                   BibTeXField.VOLUME,
                                                   BibTeXField.NUMBER,
                                                   BibTeXField.SERIES,
                                                   BibTeXField.ADDRESS,
                                                   BibTeXField.EDITION,
                                                   BibTeXField.EDITOR,
                                                   BibTeXField.MONTH,
                                                   BibTeXField.NOTE,
                                                   BibTeXField.ISBN};

    public BookBuilder() {
    }

    @Override
    public String getBibTeXType() {
        return "book";
    }

    @Override
    protected List<BibTeXField> getMandatoryFields() {
        return Arrays.asList(mandatoryFields);
    }

    @Override
    protected boolean isFieldSupported(BibTeXField name) {
        return Arrays.asList(supportedFields).contains(name);
    }

    @Override
    public String toString() {
        return toBibTeX();
    }
}
