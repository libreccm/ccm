package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.EditshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.VolumeInSeriesCollection;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SeriesExtraXmlGenerator implements ExtraXMLGenerator {

    private boolean listMode = false;

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof Series)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    Series.class.getName()));
        }

        final Series series = (Series) item;
        createEditorsXml(series, element, state);
        if (!listMode) {
            createVolumesXml(series, element, state);
        }
    }

    private void createEditorsXml(final Series series,
                                  final Element parent,
                                  final PageState state) {
        final EditshipCollection editors = series.getEditors();
        if ((editors == null) || editors.isEmpty()) {
            return;
        }

        final Element editorsElem = parent.newChildElement("editors");
        while (editors.next()) {
            createEditorXml(editors.getEditor(),
                            editors.getEditorOrder(),
                            editors.getFrom(),
                            editors.getFromSkipMonth(),
                            editors.getFromSkipDay(),
                            editors.getTo(),
                            editors.getToSkipMonth(),
                            editors.getToSkipDay(),
                            editorsElem,
                            state);
        }
    }

    private void createEditorXml(final GenericPerson editor,
                                 final Integer order,
                                 final Date from,
                                 final Boolean fromSkipMonth,
                                 final Boolean fromSkipDay,
                                 final Date to,
                                 final Boolean toSkipMonth,
                                 final Boolean toSkipDay,
                                 final Element editorsElem,
                                 final PageState state) {
        final XmlGenerator generator = new XmlGenerator(editor);
        generator.setItemElemName("editor", "");
        generator.addItemAttribute("order", order.toString());
        if (from != null) {
            createDateAttr(generator, from, "from");
            generator.addItemAttribute("fromSkipMonth", fromSkipMonth.toString());
            generator.addItemAttribute("fromSkipDay", fromSkipDay.toString());
        }
        if (to != null) {
            createDateAttr(generator, to, "to");
            generator.addItemAttribute("toSkipMonth", toSkipMonth.toString());
            generator.addItemAttribute("toSkipDay", toSkipDay.toString());
        }
        generator.setListMode(listMode);
        generator.generateXML(state, editorsElem, "");
    }

    private void createVolumesXml(final Series series,
                                  final Element parent,
                                  final PageState state) {
        final VolumeInSeriesCollection volumes = series.getVolumes();
        if ((volumes == null) || volumes.isEmpty()) {
            return;
        }

        final Element volumesElem = parent.newChildElement("volumes");
        while (volumes.next()) {
            createVolumeXml(volumes.getPublication(GlobalizationHelper.
                    getNegotiatedLocale().getLanguage()),
                            volumes.getVolumeOfSeries(),
                            volumesElem,
                            state);
        }
    }

    private void createDateAttr(final XmlGenerator generator,
                                final Date date,
                                final String prefix) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        generator.addItemAttribute(String.format("%sYear", prefix),
                                   Integer.toString(cal.get(Calendar.YEAR)));
        generator.addItemAttribute(String.format("%sMonth", prefix),
                                   Integer.toString(cal.get(Calendar.MONTH)));
        generator.addItemAttribute(String.format("%sDay", prefix),
                                   Integer.toString(cal.get(
                Calendar.DAY_OF_MONTH)));

        final Locale locale = GlobalizationHelper.getNegotiatedLocale();
        final DateFormat dateFormat = DateFormat.getDateInstance(
                DateFormat.MEDIUM, locale);
        final DateFormat longDateFormat = DateFormat.getDateInstance(
                DateFormat.LONG, locale);
        generator.addItemAttribute(String.format("%sDate", prefix),
                                   dateFormat.format(date));
        generator.addItemAttribute(String.format("%sLongDate", prefix),
                                   longDateFormat.format(date));
        generator.addItemAttribute(String.format("%sMonthName", prefix),
                                   cal.getDisplayName(Calendar.MONTH,
                                                      Calendar.LONG, 
                                                      locale));

    }

    private void createVolumeXml(final Publication publication,
                                 final Integer volume,
                                 final Element volumesElem,
                                 final PageState state) {
        final XmlGenerator generator = new XmlGenerator(publication);
        generator.setItemElemName("publication", "");
        if (volume != null) {
            generator.addItemAttribute("volumeNr", volume.toString());
        }
        generator.setListMode(true);
        generator.generateXML(state, volumesElem, "");
    }

    public void addGlobalStateParams(final Page page) {
        //nothing
    }

    public void setListMode(final boolean listMode) {
        this.listMode = listMode;
    }

    private class XmlGenerator extends SimpleXMLGenerator {

        private final ContentItem item;

        public XmlGenerator(final ContentItem item) {
            super();
            this.item = item;
        }

        @Override
        protected ContentItem getContentItem(final PageState state) {
            return item;
        }
    }
}
