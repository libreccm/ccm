/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.lifecycle;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;


/**
 * Definition for a phase in a publication life lifecycle.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Xixi D'Moon (xdmoon@arsdigita.com)
 * @version $Revision: #11 $ $Date: 2004/08/17 $
 * @version $Id: PhaseDefinition.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PhaseDefinition extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.PhaseDefinition";

    private static final int HOUR = 60;
    private static final int DAY  = 60*24;

    protected static final String LABEL                = "label";
    protected static final String DESCRIPTION          = "description";
    protected static final String DEFAULT_DELAY        = "defaultDelay";
    protected static final String DEFAULT_DURATION     = "defaultDuration";
    protected static final String DEFAULT_LISTENER     = "defaultListener";
    protected static final String LIFECYCLE_DEFINITION = "lifecycleDefinition";

    private static final Integer ZERO = new Integer(0);

    /**
     * If this constructor is used, the lifecycle definition needs to be set
     * with the <code>setLifecycleDefinition</code> method.
     */
    protected PhaseDefinition() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved from the
     * persistent storage mechanism with an <code>OID</code> specified by
     * <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public PhaseDefinition(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved from the
     * persistent storage mechanism with an <code>OID</code> specified by
     * <i>id</i> and <code>PhaseDefinition.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public PhaseDefinition(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PhaseDefinition(DataObject obj) {
        super(obj);
    }

    protected PhaseDefinition(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this definition. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getLabel() {
        return (String) get(LABEL);
    }

    public void setLabel(String label) {
        set(LABEL, label);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    /**
     * Get the default delay for the start of this phase definition in minutes
     * relative to the publish date.
     *
     * <p><strong>Warning:</strong>Before 5.2, the return value used to be in
     * <em>milliseconds</em>, rather than minutes.</p>
     */
    public Integer getDefaultDelay() {
        Integer dd = (Integer) get(DEFAULT_DELAY);
        return dd == null ? ZERO : dd;
    }

    /**
     * Set the default delay for the start of this phase definition in minutes
     * relative to the publish date. A null value is the same as 0.
     *
     * <p><strong>Warning:</strong>Before 5.2, the passed in parameter used to
     * be in <em>milliseconds</em>, rather than minutes.</p>
     *
     * <p>2002-12-06 <strong>Note</strong>: If you are running on Postgres, the
     * value <code>minutes</code> must fit in an <code>int</code>, which limits
     * your maximum default delay to 4081 years, give or take a few months.
     */
    public void setDefaultDelay(Integer minutes) {
        set(DEFAULT_DELAY, minutes);
    }


    private static Integer convertToMinutes(Integer days,
                                            Integer hours,
                                            Integer minutes) {

        Integer dd = days == null ? ZERO : days;
        Integer hh = hours == null ? ZERO : hours;
        Integer mm = minutes == null ? ZERO : minutes;
        return new Integer( (dd.intValue() * DAY) + 
                            (hh.intValue() * HOUR) +
                            mm.intValue() );
    }

    /**
     * Set the default delay for the start of this phase definition relative to
     * the publish date. A null value is the same as 0.  This is a convenience
     * wrapper around {@link #setDefaultDelay(Integer)}.
     *
     * @param days     number of days
     * @param hours    number of hours
     * @param minutes  number of minutes
     */
    public void setDefaultDelay(Integer days,
                                Integer hours,
                                Integer minutes) {
        setDefaultDelay(convertToMinutes(days, hours, minutes));
    }

    /**
     * Get the default duration for this phase definition in minutes. A null
     * value is returned if this phase definition never ends.
     *
     * <p><strong>Warning:</strong>Before 5.2, the return value used to be in
     * <em>milliseconds</em>, rather than minutes.</p>
     */
    public Integer getDefaultDuration() {
        return (Integer) get(DEFAULT_DURATION);
    }

    /**
     * Set the default duration for this phase definition.  Pass in null if this
     * phase definition never ends.
     *
     * <p><strong>Warning:</strong>Before 5.2, the passed in parameter used to
     * be in <em>milliseconds</em>, rather than minutes.</p>
     *
     * <p>2002-12-06 <strong>Note</strong>: If you are running on Postgres, the
     * value <code>minutes</code> must fit in an <code>int</code>, which limits
     * your maximum default duration to 4081 years, give or take a few months.
     */
    public void setDefaultDuration(Integer minutes) {
        set(DEFAULT_DURATION, minutes);
    }

    /**
     * Set the default duration for this phase definition.  Pass in nulls if
     * this phase definition never ends.  This is a convenience wrapper around
     * {@link #setDefaultDuration(Integer)}.
     *
     * @param days     number of days
     * @param hours    number of hours
     * @param minutes  number of minutes
     */
    public void setDefaultDuration(Integer days,
                                   Integer hours,
                                   Integer minutes) {

        if ( days==null && hours==null && minutes==null ) {
            setDefaultDuration(null);
        } else {
            setDefaultDuration(convertToMinutes(days, hours, minutes));
        }
    }

    public String getDefaultListener() {
        return (String) get(DEFAULT_LISTENER);
    }

    public void setDefaultListener(String listener) {
        set(DEFAULT_LISTENER, listener);
    }

    public LifecycleDefinition getLifecycleDefinition() {
        DataObject lcd = (DataObject) get(LIFECYCLE_DEFINITION);
        return lcd == null ? null : new LifecycleDefinition(lcd);
    }


    /**
     * Update the associated {@link LifecycleDefinition}.  Every
     * <code>PhaseDefinition</code> needs to be associated with a
     * <code>LifecycleDefinition</code>.  If this phase definition does not
     * belong to any lifecycle definition, then this phase definition should be
     * removed by calling the {@link #delete()} method.
     */
    protected void setLifecycleDefinition(LifecycleDefinition lifecycleDefinition) {
        setAssociation(LIFECYCLE_DEFINITION, lifecycleDefinition);
    }

}
