/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.portation.modules.contenttypes;

import com.arsdigita.cms.portation.conversion.NgCmsCollection;
import com.arsdigita.cms.portation.modules.contentsection.ContentItem;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;

import java.util.Date;
import java.util.Locale;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/3/18
 */
public class Event extends ContentItem implements Portable {
    private LocalizedString text;
    private Date startDate;
    private Date endDate;
    private LocalizedString eventDate;
    private LocalizedString location;
    private LocalizedString mainContributor;
    private LocalizedString eventType;
    private String mapLink;
    private LocalizedString cost;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkEvent the trunk object
     */
    public Event(final com.arsdigita.cms.contenttypes.Event trunkEvent) {
        super(trunkEvent);

        final Locale locale = Locale.getDefault();
        this.text = new LocalizedString();
        this.text.addValue(locale, trunkEvent.getTextAsset().getText());

        this.startDate = trunkEvent.getStartDate();
        this.endDate = trunkEvent.getEndDate();

        this.eventDate = new LocalizedString();
        this.eventDate.addValue(locale, trunkEvent.getEventDate());

        this.location = new LocalizedString();
        this.location.addValue(locale, trunkEvent.getLocation());

        this.mainContributor = new LocalizedString();
        this.mainContributor.addValue(locale, trunkEvent.getMainContributor());

        this.eventType = new LocalizedString();
        this.eventType.addValue(locale, trunkEvent.getEventType());

        this.mapLink = trunkEvent.getMapLink();

        this.cost = new LocalizedString();
        this.cost.addValue(locale, trunkEvent.getCost());

        NgCmsCollection.events.put(this.getObjectId(), this);
    }

    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        this.text = text;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    public LocalizedString getEventDate() {
        return eventDate;
    }

    public void setEventDate(final LocalizedString eventDate) {
        this.eventDate = eventDate;
    }

    public LocalizedString getLocation() {
        return location;
    }

    public void setLocation(final LocalizedString location) {
        this.location = location;
    }

    public LocalizedString getMainContributor() {
        return mainContributor;
    }

    public void setMainContributor(final LocalizedString mainContributor) {
        this.mainContributor = mainContributor;
    }

    public LocalizedString getEventType() {
        return eventType;
    }

    public void setEventType(final LocalizedString eventType) {
        this.eventType = eventType;
    }

    public String getMapLink() {
        return mapLink;
    }

    public void setMapLink(final String mapLink) {
        this.mapLink = mapLink;
    }

    public LocalizedString getCost() {
        return cost;
    }

    public void setCost(final LocalizedString cost) {
        this.cost = cost;
    }
}
