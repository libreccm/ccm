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
package com.arsdigita.cms.ui.authoringkit;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.AuthoringStepCollection;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;



/**
 * This class contains the component which displays the information
 * for a particular authoring kit.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #10 $ $Date: 2004/08/17 $
 */
public class KitInfo extends CMSContainer {

    public static final String versionId = "$Id: KitInfo.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private final SingleSelectionModel m_types;
    private final Table m_stepTable;

    private final RequestLocal m_authoringKit;


    public KitInfo(SingleSelectionModel m) {
        super();

        m_types = m;

        m_authoringKit = new RequestLocal();

        // The "authoring kit steps" table
        m_stepTable = makeStepTable();
        m_stepTable.setClassAttr("authoringStepsTable");
        add(m_stepTable);
    }

    /**
     * Get the authoring kit steps table.
     */
    public Table getStepTable() {
        return m_stepTable;
    }


    /**
     * Sets the m_authoringKit RequestLocal variable.
     */
    private void initializeAuthoringKit(PageState state) {
        Assert.isTrue(m_types.isSelected(state));

        BigDecimal typeId = new BigDecimal(m_types.getSelectedKey(state).toString());
        ContentType type = new ContentType(typeId);

        AuthoringKit kit = type.getAuthoringKit();
        // kit is null if an authoring kit does not exist for this content type,

        m_authoringKit.set(state, kit);
    }


    /**
     * Generate XML representing this component.
     *
     * <p> The XML generated has the form
     * <pre>
     *   &lt;cms:foo createComponentName"name" %bebopAttr;>
     *     &lt;bebop:table class="authoringStepsTable">
     *        ... XML generated for component returned by renderer ...
     *     &lt;/bebop:table>
     *   &lt;/cms:foo></pre>
     *
     * @param state the state of the current request
     * @param parent the element into which XML is generated
     * @pre state != null
     * @pre parent != null
     */
    public void generateXML(PageState state, Element parent) {
        if ( isVisible(state) ) {

            Element element = parent.newChildElement("cms:foo", CMS.CMS_XML_NS);
            exportAttributes(element);

            initializeAuthoringKit(state);
            AuthoringKit kit = (AuthoringKit) m_authoringKit.get(state);

            if (kit != null){
                element.addAttribute("createComponentName", kit.getCreateComponent());
                m_stepTable.generateXML(state, element);
            } else {
                element.addAttribute("createComponentName", "n/a - This is not a creatable Content Type");
            }
        }
    }


    /**
     * Builds the authoring kit steps table.
     */
    private Table makeStepTable() {
        final String[] headers = { "#","Label","Description","Component" };

        TableModelBuilder b = new TableModelBuilder () {
                private boolean m_locked;

                public TableModel makeModel(final Table t, final PageState s) {
                    Assert.isTrue(m_types.isSelected(s));

                    return new TableModel() {

                            AuthoringKit m_kit = getKit();
                            AuthoringStepCollection m_steps = null;
                            AuthoringStep m_currentStep = null;

                            private AuthoringKit getKit() {
                                initializeAuthoringKit(s);
                                return (AuthoringKit) m_authoringKit.get(s);
                            }

                            public int getColumnCount() {
                                return headers.length;
                            }

                            public boolean nextRow() {
                                boolean next;
                                if (m_steps == null) {
                                    m_steps = m_kit.getSteps();
                                    if (m_steps == null) {
                                        return false;
                                    }
                                }
                                next = m_steps.next();
                                if ( next ) {
                                    m_currentStep = m_steps.getAuthoringStep();
                                }

                                return next;
                            }

                            public Object getElementAt(int columnIndex) {
                                if ( m_currentStep == null ) {
                                    throw new IllegalArgumentException(
                                                                       "Current row does not exists");
                                }

                                switch (columnIndex) {
                                case 0:
                                    if (m_kit != null) {
                                        return m_kit.getOrdering(m_currentStep);
                                    } else {
                                        return "";
                                    }
                                case 1:
                                    return m_currentStep.getLabel();
                                case 2:
                                    return m_currentStep.getDescription();
                                case 3:
                                    return m_currentStep.getComponent();
                                default:
                                    throw new IllegalArgumentException(
                                                                       "columnIndex exceeds the number of columns available");
                                }
                            }

                            public Object getKeyAt(int columnIndex) {
                                if ( m_currentStep == null ) {
                                    throw new IllegalArgumentException(
                                                                       "Current row does not exists");
                                } else {
                                    return m_currentStep.getID();
                                }
                            }
                        };
                }

                public void lock() {
                    m_locked = true;
                }

                public boolean isLocked() {
                    return m_locked;
                }
            };

        return new Table(b, headers);
    }

}
