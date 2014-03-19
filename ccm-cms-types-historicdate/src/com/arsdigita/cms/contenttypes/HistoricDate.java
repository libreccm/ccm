/*
 * Copyright (C) 2014 Jens Pelzetter All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class HistoricDate extends GenericArticle {

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.HistoricDate";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY_OF_MONTH = "dayOfMonth";
    public static final String DATE_IS_APPROX = "dateIsApprox";
    public static final String LEAD = "lead";

    public HistoricDate() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public HistoricDate(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public HistoricDate(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public HistoricDate(final DataObject dataObject) {
        super(dataObject);
    }

    public HistoricDate(final String type) {
        super(type);
    }

    public Integer getYear() {
        return (Integer) get(YEAR);
    }

    public void setYear(final Integer year) {
        set(YEAR, year);
    }

    public Integer getMonth() {
        return (Integer) get(MONTH);
    }

    public void setMonth(final Integer month) {
        set(MONTH, month);
    }

    public Integer getDayOfMonth() {
        return (Integer) get(DAY_OF_MONTH);
    }

    public void setDayOfMonth(final Integer dayOfMonth) {
        set(DAY_OF_MONTH, dayOfMonth);
    }

    public Boolean getDateIsApprox() {
        return (Boolean) get(DATE_IS_APPROX);
    }

    public void setDateIsApprox(final Boolean dateIsApprox) {
        set(DATE_IS_APPROX, dateIsApprox);
    }

    public String getLead() {
        return (String) get(LEAD);
    }

    public void setLead(final String lead) {
        set(LEAD, lead);
    }

    @Override
    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getLead(),
                                                             200,
                                                             true);
    }

}
