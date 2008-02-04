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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 * Represents a step in an {@link com.arsdigita.cms.AuthoringKit
 * authoring kit}.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #13 $ $Date: 2004/08/17 $
 */
public class AuthoringStep extends ACSObject {

    public static final String versionId = "$Id: AuthoringStep.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";


    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.AuthoringStep";

    protected static final String LABEL = "label";
    protected static final String DESCRIPTION = "description";

    protected static final String LABEL_KEY = "labelKey";
    protected static final String DESCRIPTION_KEY = "descriptionKey";

    protected static final String LABEL_BUNDLE = "labelBundle";
    protected static final String DESCRIPTION_BUNDLE = "descriptionBundle";

    protected static final String COMPONENT = "component";


    /**
     * Default constructor. This creates a new authoring step.
     **/
    public AuthoringStep() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public AuthoringStep(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>AuthoringStep.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public AuthoringStep(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public AuthoringStep(DataObject obj) {
        super(obj);
    }

    protected AuthoringStep(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this step. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     *  @deprecated use setLabelKey and setLabelBundle instead
     */
    public String getLabel() {
        String label = (String) get(LABEL);
        if (label == null) {
            label = getLabelKey();
        }
        return label;
    }

    /**
     *  @deprecated use setLabelKey and setLabelBundle instead
     */
    public void setLabel(String label) {
        set(LABEL, label);
    }

    public String getLabelKey() {
        return (String) get(LABEL_KEY);
    }

    public void setLabelKey(String labelKey) {
        set(LABEL_KEY, labelKey);
    }

    public String getLabelBundle() {
        return (String) get(LABEL_BUNDLE);
    }

    public void setLabelBundle(String labelBundle) {
        set(LABEL_BUNDLE, labelBundle);
    }

    public String getDescription() {
        String description = (String) get(DESCRIPTION);
        if (description == null) {
            description = getDescriptionKey();
        }
        return description;
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getDescriptionKey() {
        return (String) get(DESCRIPTION_KEY);
    }

    public void setDescriptionKey(String descriptionKey) {
        set(DESCRIPTION_KEY, descriptionKey);
    }

    public String getDescriptionBundle() {
        return (String) get(DESCRIPTION_BUNDLE);
    }

    public void setDescriptionBundle(String descriptionBundle) {
        set(DESCRIPTION_BUNDLE, descriptionBundle);
    }

    public String getComponent() {
        return (String) get(COMPONENT);
    }

    public void setComponent(String component) {
        set(COMPONENT, component);
    }

    /**
     * Add this Step to an AuthoringKit.  If the step is already added
     * to the Kit, the ordering will be updated.
     * @param kit the kit to add to
     * @param ordering An ordering for this step in the kit. Lower number
     *   appears in the beginning of the kit.
     * @return true is step is added and false if ordering is updated
     */
    public boolean addToKit(AuthoringKit kit, BigDecimal ordering) {
        return kit.addStep(this, ordering);
    }

    /**
     * Remove this step from a kit.
     * @return true is the step is removed, false otherwise.
     */
    public boolean removeFromKit(AuthoringKit kit) {
        return kit.removeStep(this);
    }

    /**
     * Get the ordering of this step for a kit
     * @return the ordering, or null if this step is not associated to the
     *   kit
     */
    public BigDecimal getOrdering(AuthoringKit kit) {
        return kit.getOrdering(this);
    }


}
