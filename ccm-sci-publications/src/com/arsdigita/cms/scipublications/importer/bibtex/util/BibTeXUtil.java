package com.arsdigita.cms.scipublications.importer.bibtex.util;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.scipublications.importer.report.AuthorImportReport;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.AuthorData;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.LaTeXObject;
import org.jbibtex.LaTeXParser;
import org.jbibtex.LaTeXPrinter;
import org.jbibtex.ParseException;
import org.jbibtex.Value;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BibTeXUtil {

    private final ImporterUtil importerUtil;

    public BibTeXUtil(final ImporterUtil importerUtil) {
        super();
        this.importerUtil = importerUtil;
    }

    public void processTitle(final BibTeXEntry bibTeXEntry,
                             final Publication publication,
                             final PublicationImportReport importReport,
                             final boolean pretend) {
        try {
            final String title = toPlainString(bibTeXEntry.getField(BibTeXEntry.KEY_TITLE));

            if (!pretend) {
                publication.setTitle(title);
                String name = normalizeString(title);
                if (name.length() > 200) {
                    name = name.substring(0, 200);
                }
                publication.setName(name);
            }
            importReport.setTitle(title);
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse title for BibTeX entry '%s'. Using key as title. ",
                                          bibTeXEntry.getKey().getValue()),
                            ex,
                            importReport);

            if (!pretend) {
                publication.setTitle(bibTeXEntry.getKey().getValue());
                String name = normalizeString(bibTeXEntry.getKey().getValue());
                if (name.length() > 200) {
                    name = name.substring(0, 200);
                }
                publication.setName(name);

            }
            importReport.setTitle(bibTeXEntry.getKey().getValue());
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse title for BibTeX entry '%s'. Using key as title. ",
                                          bibTeXEntry.getKey().getValue()),
                            ex,
                            importReport);

            if (!pretend) {
                publication.setTitle(bibTeXEntry.getKey().getValue());
                String name = normalizeString(bibTeXEntry.getKey().getValue());
                if (name.length() > 200) {
                    name = name.substring(0, 200);
                }
                publication.setName(name);
            }
            importReport.setTitle(bibTeXEntry.getKey().getValue());
        }
    }

    public void processAuthors(final Key pubKey,
                               final Value authors,
                               final Publication publication,
                               final boolean editors,
                               final PublicationImportReport importReport,
                               final boolean pretend) {

        if (authors == null) {
            return;
        }

        final AuthorListParser authorListParser = new AuthorListParser();
        try {
            final List<Author> authorList = authorListParser.parse(toPlainString(authors));

            for (Author author : authorList) {
                final AuthorImportReport authorReport = importerUtil.processAuthor(publication,
                                                                                   createAuthorData(author, editors),
                                                                                   pretend);
                importReport.addAuthor(authorReport);
            }

        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to process author of publication '%s'.",
                                          pubKey.getValue()), ex, importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to process author of publication '%s'.",
                                          pubKey.getValue()), ex, importReport);
        }
    }

    private AuthorData createAuthorData(final Author author, final boolean editor) {
        final AuthorData authorData = new AuthorData();
        authorData.setEditor(editor);
        authorData.setSurname(String.format("%s %s %s",
                                            author.getPreLast(),
                                            author.getLast(),
                                            author.getSuffix()).trim());
        authorData.setGivenName(author.getFirst());

        return authorData;
    }

    public void processPublisher(final Key pubKey,
                                 final Value publisher,
                                 final Value address,
                                 final PublicationWithPublisher publication,
                                 final PublicationImportReport importReport,
                                 final boolean pretend) {
        if (publisher == null) {
            return;
        }

        try {
            if (address == null) {
                importReport.setPublisher(importerUtil.processPublisher(publication,
                                                                        "",
                                                                        toPlainString(publisher),
                                                                        pretend));
            } else {
                importReport.setPublisher(importerUtil.processPublisher(publication,
                                                                        toPlainString(address),
                                                                        toPlainString(publisher),
                                                                        pretend));
            }
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse publisher for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse publisher for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        }
    }

    public void processSeries(final Key pubKey,
                              final Value series,
                              final Publication publication,
                              final PublicationImportReport importReport,
                              final boolean pretend) {
        if (series == null) {
            return;
        }

        try {
            importReport.setSeries(importerUtil.processSeries(publication, toPlainString(series), pretend));
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse series for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse series for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        }
    }

    public void processJournal(final Key pubKey,
                               final Value journal,
                               final ArticleInJournal article,
                               final PublicationImportReport importReport,
                               final boolean pretend) {
        if (journal == null) {
            return;
        }
        try {
            importReport.setJournal(importerUtil.processJournal(article, toPlainString(journal), pretend));
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse journal for article '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse journal for article '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        }
    }

    public void processCollectedVolume(final BibTeXEntry bibTeXEntry,
                                       final ArticleInCollectedVolume article,
                                       final PublicationImportReport importReport,
                                       final boolean pretend) {
        final Value collectedVolume = bibTeXEntry.getField(BibTeXEntry.KEY_BOOKTITLE);

        if (collectedVolume == null) {
            return;
        }

        final Value yearValue = bibTeXEntry.getField(BibTeXEntry.KEY_YEAR);
        final Value editorValue = bibTeXEntry.getField(BibTeXEntry.KEY_EDITOR);
        final Value publisherValue = bibTeXEntry.getField(BibTeXEntry.KEY_PUBLISHER);
        final Value addressValue = bibTeXEntry.getField(BibTeXEntry.KEY_ADDRESS);
        final Value editonValue = bibTeXEntry.getField(BibTeXEntry.KEY_EDITION);

        try {
            final AuthorListParser authorListParser = new AuthorListParser();
            final List<Author> authors = authorListParser.parse(toPlainString(editorValue));
            final List<AuthorData> authorData = new ArrayList<AuthorData>();
            for (Author author : authors) {
                authorData.add(createAuthorData(author, false));
            }

            importReport.setCollectedVolume(importerUtil.processCollectedVolume(article,
                                                                                toPlainString(collectedVolume),
                                                                                toPlainString(yearValue),
                                                                                authorData,
                                                                                toPlainString(publisherValue),
                                                                                toPlainString(addressValue),
                                                                                toPlainString(editonValue),
                                                                                pretend));
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse collected volume for article '%s'.",
                                          bibTeXEntry.getKey().getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse collected volume for article '%s'.",
                                          bibTeXEntry.getKey().getValue()),
                            ex,
                            importReport);
        }
    }

    public void processProceedings(final BibTeXEntry bibTeXEntry,
                                   final InProceedings inProceedings,
                                   final PublicationImportReport importReport,
                                   final boolean pretend) {
        final Value proceedings = bibTeXEntry.getField(BibTeXEntry.KEY_BOOKTITLE);

        if (proceedings == null) {
            return;
        }

        final Value yearValue = bibTeXEntry.getField(BibTeXEntry.KEY_YEAR);
        final Value editorValue = bibTeXEntry.getField(BibTeXEntry.KEY_EDITOR);
        final Value publisherValue = bibTeXEntry.getField(BibTeXEntry.KEY_PUBLISHER);
        final Value addressValue = bibTeXEntry.getField(BibTeXEntry.KEY_ADDRESS);

        try {
            final AuthorListParser authorListParser = new AuthorListParser();
            final List<Author> authors = authorListParser.parse(toPlainString(editorValue));
            final List<AuthorData> authorData = new ArrayList<AuthorData>();
            for (Author author : authors) {
                authorData.add(createAuthorData(author, false));
            }

            importReport.setProceedings(importerUtil.processProceedings(inProceedings,
                                                                        toPlainString(proceedings),
                                                                        toPlainString(yearValue),
                                                                        "",
                                                                        authorData,
                                                                        toPlainString(publisherValue),
                                                                        toPlainString(addressValue),
                                                                        pretend));
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse proceedings for in proceedings '%s'.",
                                          bibTeXEntry.getKey().getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse proceedings for in proceedings '%s'.",
                                          bibTeXEntry.getKey().getValue()),
                            ex,
                            importReport);
        }
    }

    public void processOrganization(final Key pubKey,
                                    final Value organization,
                                    final UnPublished publication,
                                    final ImporterUtil importerUtil,
                                    final PublicationImportReport importReport,
                                    final boolean pretend) {
        if (organization == null) {
            return;
        }
        try {
            importReport.addOrgaUnit(importerUtil.processOrganization(publication,
                                                                      toPlainString(organization),
                                                                      pretend));
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse organization for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse organization for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        }
    }

    public void processOrganization(final Key pubKey,
                                    final Value organization,
                                    final InternetArticle article,
                                    final ImporterUtil importerUtil,
                                    final PublicationImportReport importReport,
                                    final boolean pretend) {
        if (organization == null) {
            return;
        }
        try {
            importReport.addOrgaUnit(importerUtil.processOrganization(article, toPlainString(organization), pretend));
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse organization for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse organization for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        }
    }

    public void processPages(final Key pubKey,
                             final Value pages,
                             final Publication publication,
                             final PublicationImportReport importReport,
                             final boolean pretend) {
        try {
            final String pagesStr = toPlainString(pages);

            final String[] tokens = pagesStr.split("-");
            if (tokens.length == 2) {
                try {
                    final int pagesFrom = Integer.parseInt(tokens[0]);
                    final int pagesTo = Integer.parseInt(tokens[1]);
                    if (!pretend) {
                        publication.set("pagesFrom", pagesFrom);
                        publication.set("pagesTo", pagesTo);
                    }
                    importReport.addField(new FieldImportReport("pagesFrom", Integer.toString(pagesFrom)));
                    importReport.addField(new FieldImportReport("pagesTo", Integer.toString(pagesTo)));
                } catch (NumberFormatException ex) {
                    importReport.addMessage(String.format("Failed to parse pages for publication '%s'. "
                                                          + "One of the values given is not an integer.",
                                                          pubKey.getValue()));
                }
            } else if (tokens.length == 1) {
                try {
                    final int pagesFrom = Integer.parseInt(tokens[0]);
                    if (!pretend) {
                        publication.set("pagesFrom", pagesFrom);
                    }
                    importReport.addField(new FieldImportReport("pagesFrom", Integer.toString(pagesFrom)));
                } catch (NumberFormatException ex) {
                    importReport.addMessage(String.format("Failed to parse pages for publication '%s'. "
                                                          + "Value is not an integer.",
                                                          pubKey.getValue()));
                }
            } else {
                importReport.addMessage(String.format("Failed to parse pages for publication '%s'.",
                                                      pubKey.getValue()));
            }

        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse pages for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse pages for publication '%s'.", pubKey.getValue()),
                            ex,
                            importReport);
        }
    }

    public void processIntField(final Key pubKey,
                                final Key fieldKey,
                                final Value value,
                                final String target,
                                final Publication publication,
                                final PublicationImportReport importReport,
                                final boolean pretend) {
        if (value == null) {
            return;
        }

        try {
            final String str = toPlainString(value);
            final int intValue = Integer.parseInt(str);

            if (!pretend) {
                publication.set(target, intValue);
            }
            importReport.addField(new FieldImportReport(target, str));
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse field '%s' for publication '%s'.",
                                          pubKey.getValue(),
                                          fieldKey.getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            addExceptionMsg(String.format("Failed to parse field '%s' for publication '%s'.",
                                          pubKey.getValue(),
                                          fieldKey.getValue()),
                            ex,
                            importReport);
        } catch (NumberFormatException ex) {
            addExceptionMsg(String.format("Failed to parse field '%s' for publication '%s'.",
                                          pubKey.getValue(),
                                          fieldKey.getValue()),
                            ex,
                            importReport);
        }

    }

    public void processField(final Key pubKey,
                             final Key fieldKey,
                             final Value value,
                             final String target,
                             final Publication publication,
                             final PublicationImportReport importReport,
                             final boolean pretend) {
        if (value == null) {
            return;
        }

        try {
            if (!pretend) {
                publication.set(target, toPlainString(value));
            }
            importReport.addField(new FieldImportReport(target, toPlainString(value)));
        } catch (IOException ex) {
            addExceptionMsg(String.format("Failed to parse value of field '%s' for publication '%s'.",
                                          pubKey.getValue(),
                                          fieldKey.getValue()),
                            ex,
                            importReport);
        } catch (ParseException ex) {
            Logger.getLogger(BibTeXUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String toPlainString(final Value value) throws IOException, ParseException {
        return toPlainString(value.toUserString());
    }

    private String toPlainString(final String str) throws IOException, ParseException {
        final List<LaTeXObject> objects = parseLaTeX(str);

        return printLaTeX(objects);
    }

    private List<LaTeXObject> parseLaTeX(final String string) throws IOException, ParseException {
        final Reader reader = new StringReader(string);

        try {
            final LaTeXParser parser = new LaTeXParser();

            return parser.parse(reader);
        } finally {
            reader.close();
        }
    }

    private String printLaTeX(final List<LaTeXObject> objects) {
        final LaTeXPrinter printer = new LaTeXPrinter();

        return printer.print(objects);
    }

    private String normalizeString(final String str) {
        if (str == null) {
            return "null";
        }
        return str.replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").
                replace(
                "Ä", "Ae").replace("Ü", "Ue").replace("Ö", "Oe").replace("ß",
                                                                         "ss").
                replace(" ", "-").
                replaceAll("[^a-zA-Z0-9\\-]", "").toLowerCase().trim();
    }

    private void addExceptionMsg(final String msg, final Exception exception, final PublicationImportReport importReport) {
        final StringWriter strWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(strWriter);

        exception.printStackTrace(writer);
        writer.flush();
        strWriter.flush();

        importReport.addMessage(String.format("%s Exeception was: %s.",
                                              msg,
                                              strWriter.toString()));
    }

}
