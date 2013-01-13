package com.arsdigita.cms.scipublications.importer.ris.converters.utils;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisFieldUtil {

    private static final String PAGES_FROM = "pagesFrom";
    private static final String PAGES_TO = "pagesTo";
    private final boolean pretend;

    public RisFieldUtil(final boolean pretend) {
        this.pretend = pretend;
    }

    @SuppressWarnings("PMD.ConfusingTernary")
    public void processTitle(final RisDataset dataset,
                             final Publication publication,
                             final PublicationImportReport report) {
        final String title;

        if ((dataset.getValues().get(RisField.TI) != null) && !dataset.getValues().get(RisField.TI).isEmpty()) {
            title = dataset.getValues().get(RisField.TI).get(0);
        } else if ((dataset.getValues().get(RisField.BT) != null) && !dataset.getValues().get(RisField.BT).isEmpty()) {
            title = dataset.getValues().get(RisField.BT).get(0);
        } else {
            title = "Unknown";
        }

        if (!pretend) {
            publication.setTitle(title);
            String name = normalizeString(title);
            if (name.length() > 200) {
                name = name.substring(0, 200);
            }
            publication.setName(name);            
        }

        report.setTitle(title);
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


    public void processField(final RisDataset dataset,
                             final RisField field,
                             final Publication publication,
                             final String targetField,
                             final PublicationImportReport report) {
        final List<String> values = dataset.getValues().get(field);
        if ((values != null) && !values.isEmpty()) {
            if (!pretend) {
                publication.set(targetField, values.get(0));
            }
            report.addField(new FieldImportReport(targetField, values.get(0)));
        }
    }

    public void processIntField(final RisDataset dataset,
                                final RisField field,
                                final Publication publication,
                                final String targetField,
                                final PublicationImportReport report) {
        final List<String> values = dataset.getValues().get(field);
        if ((values != null) && !values.isEmpty()) {
            final String valueStr = values.get(0);
            try {
                final int value = Integer.parseInt(valueStr);
                if (!pretend) {
                    publication.set(targetField, value);
                }
                report.addField(new FieldImportReport(targetField, valueStr));
            } catch (NumberFormatException ex) {
                report.addMessage(String.format("Failed to parse value of field '%s' into an integer for dataset "
                                                + "starting on line %d.",
                                                field,
                                                dataset.getFirstLine()));
            }
        }
    }

    public void processDateField(final RisDataset dataset,
                                 final RisField field,
                                 final Publication publication,
                                 final String targetField,
                                 final PublicationImportReport report) {
        final List<String> values = dataset.getValues().get(field);
        if ((values != null) && !values.isEmpty()) {
            final String valueStr = values.get(0);
            final String[] tokens = valueStr.split("/");
            final Calendar calendar = Calendar.getInstance();
            int year = 0;
            int month = 1;
            int day = 1;
            try {
                if (tokens.length >= 1) {
                    year = Integer.parseInt(tokens[0]);
                }

                if (tokens.length >= 2) {
                    month = Integer.parseInt(tokens[1]);
                }

                if (tokens.length >= 3) {
                    day = Integer.parseInt(tokens[2]);
                }

                calendar.clear();
                calendar.set(year, month - 1, day); // month - 1 because month values of the Calendar are starting with 0

                if (!pretend) {
                    publication.set(targetField, calendar.getTime());
                }
                report.addField(new FieldImportReport(targetField, String.format("%d-%d-%d",
                                                                                 calendar.get(Calendar.YEAR),
                                                                                 calendar.get(Calendar.MONTH),
                                                                                 calendar.get(Calendar.DAY_OF_MONTH))));

            } catch (NumberFormatException ex) {
                report.addMessage(String.format("Failed to parse value of field '%s' into an date for dataset "
                                                + "starting on line %d.",
                                                field,
                                                dataset.getFirstLine()));
            }


        }
    }

    public void processPages(final RisDataset dataset,
                             final RisField field,
                             final Publication publication,
                             final PublicationImportReport report) {
        final List<String> values = dataset.getValues().get(field);
        final String pages = values.get(0);
        final String[] tokens = pages.split("-");
        if (tokens.length == 2) {
            try {
                final int pagesFrom = Integer.parseInt(tokens[0]);
                final int pagesTo = Integer.parseInt(tokens[1]);
                if (!pretend) {
                    publication.set(PAGES_FROM, pagesFrom);
                    publication.set(PAGES_TO, pagesTo);
                }
                report.addField(new FieldImportReport(PAGES_FROM, Integer.toString(pagesFrom)));
                report.addField(new FieldImportReport(PAGES_TO, Integer.toString(pagesTo)));
            } catch (NumberFormatException ex) {
                report.addMessage(String.format("Failed to parse pages value in dataset starting at line %d. "
                                                + "On of the values given is not an integer.", dataset.getFirstLine()));
            }
        } else if (tokens.length == 1) {
            try {
                final int pagesFrom = Integer.parseInt(tokens[0]);
                if (!pretend) {
                    publication.set(PAGES_FROM, pagesFrom);
                }
                report.addField(new FieldImportReport(PAGES_FROM, Integer.toString(pagesFrom)));
            } catch (NumberFormatException ex) {
                report.addMessage(String.format("Failed to parse pages value in dataset starting at line %d. "
                                                + "Value is not an integer.", dataset.getFirstLine()));
            }
        } else if (tokens.length > 2) {
            report.addMessage(String.format("Failed to parse pages value in dataset starting at line %d. "
                                            + "Invalid format", dataset.getFirstLine()));
        }
    }

}
