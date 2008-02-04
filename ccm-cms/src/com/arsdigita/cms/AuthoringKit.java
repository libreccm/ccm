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
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;

/**
 * An AuthoringKit contains a collection of {@link
 * com.arsdigita.cms.AuthoringStep authoring steps} that are used for
 * authoring a particular content type.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Revision: #17 $ $Date: 2004/08/17 $
 */
public class AuthoringKit extends ACSObject {

    public static final String versionId = "$Id: AuthoringKit.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.AuthoringKit";

    protected static final String CREATE_COMPONENT = "createComponent";
    protected static final String CONTENT_TYPE = "contentType";
    protected static final String STEPS = "authoringSteps";

    private static final String LAST_STEP_QUERY =
        "com.arsdigita.cms.getLastAuthoringStepOrder";
    private static final String LAST_STEP_ORDER = "stepOrder";
    private static final String LAST_STEP_KIT_ID = "kitId";

    /**
     * Default constructor. This creates a new authoring kit.
     **/
    public AuthoringKit() {
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
    public AuthoringKit(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>AuthoringKit.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public AuthoringKit(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public AuthoringKit(DataObject obj) {
        super(obj);
    }

    protected AuthoringKit(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this kit. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Get the java classname for the component to create the content
     * item using this kit.
     */
    public String getCreateComponent() {
        return (String) get(CREATE_COMPONENT);
    }

    /**
     * Set the java classname for the component to create the content
     * item using this kit.
     */
    public void setCreateComponent(String component) {
        set(CREATE_COMPONENT, component);
    }

    /**
     * Get the ContentType associated with this kit.
     */
    public ContentType getContentType() {
        DataObject type = (DataObject) get(CONTENT_TYPE);
        if (type == null) {
            return null;
        } else {
            return new ContentType(type);
        }
    }

    /**
     * Set the ContentType associated with this kit.
     */
    protected void setContentType(ContentType type) {
        setAssociation(CONTENT_TYPE, type);
    }

    /**
     *  @deprecated
     */
    public AuthoringStep createStep(String label, String description,
                                    String component, BigDecimal ordering) {
        return createStep(label, null, description, null, component, ordering);
    }

    /**
     * Create a Step for this AuthoringKit.  The Step created will
     * be saved.
     * @param labelKey Label Key for this step.  It is used to look up
     *                 the actual value of the label located in the LabelBundle
     * @param labelBundle The name of the ResourceBundle where the 
     *                    labelKey is located
     * @param descriptionKey Description Key for this step.  It is used to 
     *                       look up the actual value of the description 
     *                       located in the DescriptionBundle
     * @param descriptionBundle The name of the ResourceBundle where the 
     *                          descriptionKey is located
     * @param component The java classname of the component associated
     *   with this kit.
     * @param ordering An ordering for this step in the kit. Lower number
     *   appears in the beginning of the kit.
     */
    public AuthoringStep createStep(String labelKey, String labelBundle,
                                    String descriptionKey, String descriptionBundle,
                                    String component, BigDecimal ordering) {

        AuthoringStep step = new AuthoringStep();
        step.setLabelKey(labelKey);
        step.setLabelBundle(labelBundle);
        step.setDescriptionKey(descriptionKey);
        step.setDescriptionBundle(descriptionBundle);
        step.setComponent(component);
        //step needs to be saved in order to do the association (addStep).
        step.save();

        addStep(step, ordering);
        return step;
    }

    /**
     * Add a Step to this AuthoringKit.  If the step is already added
     * to the Kit, the ordering will be updated.
     *
     * @param step the step to add
     * @param ordering An ordering for this step in the kit. Lower number
     *   appears in the beginning of the kit.
     * @return true is step is added and false if ordering is updated
     */
    public boolean addStep(AuthoringStep step, BigDecimal ordering) {
        try {
            OID oid = new OID
                (AuthoringKitStepAssociation.BASE_DATA_OBJECT_TYPE);
            oid.set(AuthoringKitStepAssociation.KIT_ID, getID());
            oid.set(AuthoringKitStepAssociation.STEP_ID, step.getID());

            AuthoringKitStepAssociation assn = new AuthoringKitStepAssociation(oid);
            assn.setOrdering(ordering);
            assn.save();
            return false;

        } catch (DataObjectNotFoundException e) {
            //this association does not exist, so add it
            AuthoringKitStepAssociation assn = new AuthoringKitStepAssociation();
            assn.setKit(this);
            assn.setStep(step);
            assn.setOrdering(ordering);
            assn.save();
            return true;
        }
    }

    /**
     * Create a new Step for this AuthoringKit, and add it to the kit.
     * The new Step will automatically be saved.
     *
     * @param label Label for this step.
     * @param description Description for this step.
     * @param component The java classname of the component associated
     *   with this kit.
     * @return the new authoring step
     * @see #addStep(AuthoringStep, BigDecimal)
     */
    public AuthoringStep createStep(
                                    String label, String description, String component
                                    ) {
        BigDecimal order = new BigDecimal(getLastOrdering().intValue() + 1);
        return createStep(label, description, component, order);
    }

    /**
     * Get the ordering of a step for this kit
     * @return the ordering, or null if the step is not associated to this
     *   kit
     */
    public BigDecimal getOrdering(AuthoringStep step) {
        try {
            OID oid = new OID
                (AuthoringKitStepAssociation.BASE_DATA_OBJECT_TYPE);
            oid.set(AuthoringKitStepAssociation.KIT_ID, getID());
            oid.set(AuthoringKitStepAssociation.STEP_ID, step.getID());

            AuthoringKitStepAssociation assn = new AuthoringKitStepAssociation(oid);
            return assn.getOrdering();

        } catch (DataObjectNotFoundException e) {
            return null;
        }
    }

    /**
     * Get the ordering of the last step in the authoring kit. If the
     * kit contains no steps, return 0.
     */
    public BigDecimal getLastOrdering() {
        DataQuery q = SessionManager.getSession().retrieveQuery(LAST_STEP_QUERY);
        q.setParameter(LAST_STEP_KIT_ID, getID());
        q.next();
        BigDecimal i = (BigDecimal)q.get(LAST_STEP_ORDER);
        q.close();
        return i;
    }

    /**
     * Remove a step from this kit.
     * @return true is the step is removed, false otherwise.
     */
    public boolean removeStep(AuthoringStep step) {
        try {
            OID oid = new OID
                (AuthoringKitStepAssociation.BASE_DATA_OBJECT_TYPE);
            oid.set(AuthoringKitStepAssociation.KIT_ID, getID());
            oid.set(AuthoringKitStepAssociation.STEP_ID, step.getID());

            AuthoringKitStepAssociation assn = new AuthoringKitStepAssociation(oid);
            assn.delete();
            return true;

        } catch (DataObjectNotFoundException e) {
            //this association does not exist
            return false;
        }
    }

    /**
     * Get the steps for this kit sorted by the ordering
     */
    public AuthoringStepCollection getSteps() {
        DataAssociation da = (DataAssociation) get(STEPS);
        AuthoringStepCollection steps =
            new AuthoringStepCollection(da.cursor());
        steps.setOrder("link.ordering");
        return steps;
    }
}
