package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jensp
 */
public class ProceedingsBuilder extends AbstractBibTeXBuilder {

    private final BibTeXField[] mandatoryFields = {BibTeXField.TITLE,
                                                   BibTeXField.YEAR};
    private final BibTeXField[] supportedFields = {BibTeXField.TITLE,
                                                   BibTeXField.YEAR,
                                                   BibTeXField.EDITOR,
                                                   BibTeXField.VOLUME,
                                                   BibTeXField.NUMBER,
                                                   BibTeXField.SERIES,
                                                   BibTeXField.ADDRESS,
                                                   BibTeXField.MONTH,
                                                   BibTeXField.ORGANIZATION,
                                                   BibTeXField.PUBLISHER,
                                                   BibTeXField.NOTE};

    @Override
    protected List<BibTeXField> getMandatoryFields() {
        return Arrays.asList(mandatoryFields);
    }

    @Override
    protected boolean isFieldSupported(BibTeXField name) {
        return Arrays.asList(supportedFields).contains(name);
    }

    @Override
    public String getBibTeXType() {
        return "proceedings";
    }

    @Override
    public String toString() {
        return toBibTeX();
    }
}
