/*
 * Copyright (c) 2010 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.EditshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.ui.panels.SelectFilter;
import com.arsdigita.cms.contenttypes.ui.panels.TextFilter;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SeriesExtraXmlGenerator implements ExtraXMLGenerator {

    private boolean listMode = false;
    private static final String YEAR_PARAM = "yearOfPublication";
    private static final String TITLE_PARAM = "title";
    private static final String AUTHOR_PARAM = "author";
    private final SelectFilter yearFilter = new SelectFilter(YEAR_PARAM,
                                                             YEAR_PARAM,
                                                             true,
                                                             true,
                                                             false,
                                                             true,
                                                             true);
    private final TextFilter titleFilter = new TextFilter(TITLE_PARAM,
                                                          ContentPage.TITLE);
    private final TextFilter authorFilter;

    public SeriesExtraXmlGenerator() {
        super();

        authorFilter = new TextFilter(AUTHOR_PARAM, "authorsStr");
    }

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
        //final VolumeInSeriesCollection volumes = series.getVolumes();
        final DataQuery volumes = getData(series);
        if ((volumes == null)) {
            return;
        }

        final HttpServletRequest request = state.getRequest();
        final String yearValue = Globalization.decodeParameter(request, YEAR_PARAM);
        final String titleValue = Globalization.decodeParameter(request, TITLE_PARAM);
        final String authorValue = Globalization.decodeParameter(request, AUTHOR_PARAM);

        final Element filtersElem = parent.newChildElement("filters");

        yearFilter.setDataQuery(volumes, YEAR_PARAM);        
        applyYearFilter(volumes, request);
        applyTitleFilter(volumes, request);
        applyAuthorFilter(volumes, request);
        
        yearFilter.generateXml(filtersElem);
        titleFilter.generateXml(filtersElem);
        authorFilter.generateXml(filtersElem);

        if (volumes.isEmpty()) {
            return;
        }

        final Element volumesElem = parent.newChildElement("volumes");
        while (volumes.next()) {
//            createVolumeXml(volumes.getPublication(GlobalizationHelper.
//                    getNegotiatedLocale().getLanguage()),
//                            volumes.getVolumeOfSeries(),
//                            volumesElem,
//                            state);
            createVolumeXml((BigDecimal) volumes.get("id"),
                            series.getSeriesBundle().getID(),
                            (String) volumes.get("objectType"),
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

    private void createVolumeXml(final BigDecimal publicationId,
                                 final BigDecimal seriesId,
                                 final String objectType,
                                 final Element parent,
                                 final PageState state) {
        final Publication publication = (Publication) DomainObjectFactory.
                newInstance(new OID(objectType, publicationId));

        final DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getVolumeOfSeries");
        query.setParameter("seriesId", seriesId);
        query.setParameter("publicationId", publication.getPublicationBundle().getID());

        query.next();
        final String volume = (String) query.get("volumeOfSeries");
        query.close();

        createVolumeXml(publication, volume, parent, state);
    }

    private void createVolumeXml(final Publication publication,
                                 final String volume,
                                 final Element volumesElem,
                                 final PageState state) {
        final XmlGenerator generator = new XmlGenerator(publication);
        generator.setItemElemName("publication", "");
        if (volume != null) {
            generator.addItemAttribute("volumeNr", volume);
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

    private void applyYearFilter(final DataQuery publications,
                                 final HttpServletRequest request) {
        final String yearValue = Globalization.decodeParameter(request, YEAR_PARAM);
        if ((yearValue != null) && !(yearValue.trim().isEmpty())) {
            yearFilter.setValue(yearValue);
        }

        if ((yearFilter.getFilter() != null)
            && !(yearFilter.getFilter().isEmpty())) {
            publications.addFilter(yearFilter.getFilter());
        }
    }

    private void applyTitleFilter(final DataQuery publications,
                                  final HttpServletRequest request) {
        final String titleValue = Globalization.decodeParameter(request, TITLE_PARAM);
        if ((titleValue != null) && !(titleValue.trim().isEmpty())) {
            titleFilter.setValue(titleValue);
        }

        if ((titleFilter.getFilter() != null)
            && !(titleFilter.getFilter().isEmpty())) {
            publications.addFilter(titleFilter.getFilter());
        }
    }

    private void applyAuthorFilter(final DataQuery publications,
                                   final HttpServletRequest request) {
        final String authorValue = Globalization.decodeParameter(request, AUTHOR_PARAM);
        if ((authorValue != null) && !(authorValue.trim().isEmpty())) {
            authorFilter.setValue(authorValue);
        }

        if ((authorFilter.getFilter() != null)
            && !(authorFilter.getFilter().isEmpty())) {
            publications.addFilter(authorFilter.getFilter());
        }
    }

    private DataCollection getData(final Series series) {
        final DataQuery publicationBundlesQuery = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationsForSeries");

        publicationBundlesQuery.setParameter("seriesId", series.getSeriesBundle().getID().toString());

        final StringBuilder filterBuilder = new StringBuilder();
        while (publicationBundlesQuery.next()) {
            if (filterBuilder.length() > 0) {
                filterBuilder.append(',');
            }
            filterBuilder.append(publicationBundlesQuery.get("publicationId").toString());
        }
        final DataCollection publicationsQuery = SessionManager.getSession().retrieve(Publication.BASE_DATA_OBJECT_TYPE);

        if (filterBuilder.length() == 0) {
            //No publications return null to indicate
            return null;
        }

        publicationsQuery.addFilter(String.format("parent.id in (%s)", filterBuilder.toString()));

        if (Kernel.getConfig().languageIndependentItems()) {
            final FilterFactory filterFactory = publicationsQuery.getFilterFactory();
            final Filter filter = filterFactory.or().
                    addFilter(filterFactory.equals("language", GlobalizationHelper.getNegotiatedLocale().getLanguage())).
                    addFilter(filterFactory.and().
                    addFilter(filterFactory.equals("language", GlobalizationHelper.LANG_INDEPENDENT)).
                    addFilter(filterFactory.notIn("parent", "com.arsdigita.navigation.getParentIDsOfMatchedItems").set(
                    "language", GlobalizationHelper.getNegotiatedLocale().getLanguage())));
            publicationsQuery.addFilter(filter);
        } else {
            publicationsQuery.addEqualsFilter("language", GlobalizationHelper.getNegotiatedLocale().getLanguage());
        }

        return publicationsQuery;
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
