package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jensp
 */
public class InCollectionBuilder extends AbstractBibTeXBuilder {

    private final BibTeXField[] mandatoryFields = {BibTeXField.AUTHOR,
                                                   BibTeXField.TITLE,
                                                   BibTeXField.BOOKTITLE,
                                                   BibTeXField.PUBLISHER,
                                                   BibTeXField.YEAR};
    private final BibTeXField[] supportedFields = {BibTeXField.AUTHOR,
                                                   BibTeXField.TITLE,
                                                   BibTeXField.BOOKTITLE,
                                                   BibTeXField.PUBLISHER,
                                                   BibTeXField.YEAR,
                                                   BibTeXField.EDITOR,
                                                   BibTeXField.VOLUME,
                                                   BibTeXField.NUMBER,
                                                   BibTeXField.SERIES,
                                                   BibTeXField.TYPE,
                                                   BibTeXField.CHAPTER,
                                                   BibTeXField.PAGES,
                                                   BibTeXField.ADDRESS,
                                                   BibTeXField.EDITION,
                                                   BibTeXField.MONTH,
                                                   BibTeXField.NOTE
    };

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
        return "incollection";
    }

    @Override
    public String toString() {
        return toBibTeX();
    }
}
