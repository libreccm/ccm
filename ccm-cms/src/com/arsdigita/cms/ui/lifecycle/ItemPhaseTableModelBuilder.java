/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.lifecycle.Phase;
import com.arsdigita.cms.lifecycle.PhaseCollection;
import com.arsdigita.cms.util.GlobalizationUtil;

import java.text.DateFormat;

/**
 * @author Xixi D'Moon &lt;xdmoon@arsdigita.com&gt;
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ItemPhaseTableModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $
 */
class ItemPhaseTableModelBuilder extends AbstractTableModelBuilder {
    public static final String versionId =
        "$Id: ItemPhaseTableModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private final LifecycleRequestLocal m_lifecycle;

    public ItemPhaseTableModelBuilder(final LifecycleRequestLocal lifecycle) {
        m_lifecycle = lifecycle;
    }

    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        return new Model(m_lifecycle.getLifecycle(state).getPhases());
    }

    private static class Model implements TableModel {
        private final PhaseCollection m_phases;
        private Phase m_phase;

        public Model(final PhaseCollection phases) {
            m_phases = phases;
        }

        public final int getColumnCount() {
            return 4;
        }

        public final boolean nextRow() {
            if (m_phases.next()) {
                m_phase = m_phases.getPhase();

                return true;
            } else {
                m_phases.close();

                return false;
            }
        }

        public final Object getElementAt(final int column) {
            final DateFormat format = DateFormat.getDateTimeInstance
                (DateFormat.FULL, 
                 ContentSection.getConfig().getHideTimezone() ? DateFormat.SHORT : DateFormat.FULL);

            switch (column) {
            case 0:
                return m_phase.getLabel();
            case 1:
                return m_phase.getPhaseDefinition().getDescription();
            case 2:
                final java.util.Date startDate = m_phase.getStartDate();
                return format.format(startDate);
            case 3:
                final java.util.Date endDate = m_phase.getEndDate();

                if (endDate == null) {
                    return lz("cms.ui.lifecycle.forever");
                } else {
                    return format.format(endDate);
                }
            default:
                throw new IllegalArgumentException();
            }
        }

        public final Object getKeyAt(final int column) {
            return m_phase.getPhaseDefinition().getID();
        }
    }

    protected final static String lz(final String key) {
        return (String) GlobalizationUtil.globalize(key).localize();
    }
}
