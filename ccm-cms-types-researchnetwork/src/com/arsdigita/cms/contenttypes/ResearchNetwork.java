/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center for Social Policy Research of the University of Bremen
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
package src.com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetwork extends ContentPage {

    private static final Logger s_log = Logger.getLogger(ResearchNetwork.class);
    public static final String RESEARCHNETWORK_TITLE = "researchNetworkTitle";
    public static final String RESEARCHNETWORK_DIRECTION = "researchNetworkDirection";
    public static final String RESEARCHNETWORK_COORDINATION = "researchNetworkCoordination";
    public static final String RESEARCHNETWORK_DESCRIPTION = "researchNetworkDescription";
    public static final String RESEARCHNETWORK_WEBSITE = "researchNetworkWebsite";
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.ResearchNetwork";

    public ResearchNetwork() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public ResearchNetwork(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ResearchNetwork(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public ResearchNetwork(DataObject obj) {
        super(obj);
    }

    public ResearchNetwork(String type) {
        super(type);
    }

    /* accessors ************************************************************/
    public String getResearchNetworkTitle() {
        return (String) get(RESEARCHNETWORK_TITLE);
    }

    public void setResearchNetworkTitle(String title) {
        set(RESEARCHNETWORK_TITLE, title);
    }

    public String getResearchNetworkDirection() {
        return (String) get(RESEARCHNETWORK_DIRECTION);
    }

    public void setResearchNetworkDirection(String direction) {
        set(RESEARCHNETWORK_DIRECTION, direction);
    }

    public String getResearchNetworkCoordination() {
        return (String) get(RESEARCHNETWORK_COORDINATION);
    }

    public void setResearchNetworkCoordination(String coordination) {
        set(RESEARCHNETWORK_COORDINATION, coordination);
    }

    public String getResearchNetworkDescription() {
        return (String) get(RESEARCHNETWORK_DESCRIPTION);
    }

    public void setResearchNetworkDescription(String description) {
        set(RESEARCHNETWORK_DESCRIPTION, description);
    }

    public String getResearchNetworkWebsite() {
        return (String) get(RESEARCHNETWORK_WEBSITE);
    }

    public void setResearchNetworkWebsite(String website) {
        set(RESEARCHNETWORK_WEBSITE, website);
    }

    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }
}
