/*
 * Copyright (C) 2007 Red Hat Inc. All Rights Reserved.
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

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * A section of the Camden Decision Tree content type.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeSection extends ContentPage {

    public static final Logger s_log = Logger.getLogger(DecisionTreeSection.class);

    public static final String BASE_DATA_OBJECT_TYPE =
                        "com.arsdigita.cms.contenttypes.DecisionTreeSection";

    public static final String PARAMETER_NAME  = "parameterName";
    public static final String INSTRUCTIONS    = "instructions";
    public static final String SECTION_OPTIONS = "sectionOptions";
    public static final String TREE            = "tree";

    public DecisionTreeSection() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public DecisionTreeSection(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public DecisionTreeSection(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public DecisionTreeSection(DataObject obj) {
        super(obj);
    }

    public DecisionTreeSection(String type) {
        super(type);
    }

    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    @Override
    protected void beforeDelete() {
		super.beforeDelete();

		// Clear the DecisionTree's firstSection field if it points to
		// this TreeSection.
		DecisionTree tree = getTree();
		if (this.equals(tree.getFirstSection())) {
			tree.setFirstSection(null);
		}
		
		// Delete all OptionTargets which have this TreeSection as their target.
		DecisionTreeOptionTargetCollection targets = tree.getTargets();
		targets.addEqualsFilter("targetSection.id", getID());
		while (targets.next()) {
			targets.getTarget().delete();
		}
	}
    
    public String getParameterName() {
    	return (String) get(PARAMETER_NAME);
    }
    
    public void setParameterName(String value) {
    	set(PARAMETER_NAME, value);
    }

    public TextAsset getInstructions() {
    	DataObject dataObject = (DataObject)get(INSTRUCTIONS);
        if (dataObject == null) { return null; }
        return new TextAsset(dataObject);
    }
    
    public void setInstructions(TextAsset value) {
        setAssociation(INSTRUCTIONS, value);
    }
    
    public DecisionTreeSectionOptionCollection getOptions() {
    	return new DecisionTreeSectionOptionCollection(
                                      (DataCollection) get(SECTION_OPTIONS));
    }

    public DecisionTree getTree() {
    	return new DecisionTree((DataObject) get(TREE));
    }
    
    public int getMaxOptionRank() {
    	DecisionTreeSectionOptionCollection options = getOptions();
    	options.addOrder(DecisionTreeSectionOption.RANK + " desc");
    	
    	int maxRank = 0;
    	if (options.next())
    		maxRank = options.getOption().getRank().intValue();
    	options.close();
    	
    	return maxRank;
    }

    /**
     * Change the rank of the option with the given id within this object.
     * Sets the option rank to that given, and moves all other option ranks
     * as appropriate. If the new rank is greater than the current rank,
     * options in between will be moved to a numerically lower rank. If the
     * new rank is less than the current rank than options in between will be
     * moved to a higher rank.
     *
     * @param source the option to reorder
     * @param rank the new rank for the option. This must be between 1 and
     * the max section rank inclusively.
     */
    public void changeOptionRank(DecisionTreeSectionOption source, int destRank) {
        Integer r = source.getRank();
        if (r == null) {
            throw new IllegalStateException(source + " has null rank");
        }

        int curRank = r.intValue();

        if (s_log.isDebugEnabled()) {
            s_log.debug("*** changeSectionRank, section ID = " + source.getID() +
            		", curRank = " + curRank +
                    ", destRank = "  + destRank);
        }

        DecisionTreeSectionOptionCollection coll = getOptions();
        coll.addOrder(DecisionTreeSectionOption.RANK);
        if (curRank > destRank) {
            coll.setRange(new Integer(destRank), new Integer(curRank));
            int rank = destRank;
            while (coll.next()) {
                DecisionTreeSectionOption cur = coll.getOption();
                cur.setRank(new Integer(rank + 1));
                cur.save();
                rank++;
            }
            source.setRank(new Integer(destRank));
        } else if (curRank < destRank) {
            coll.setRange(new Integer(curRank + 1), new Integer(destRank + 1));
            int rank = curRank + 1;
            while (coll.next()) {
                DecisionTreeSectionOption cur = coll.getOption();
                cur.setRank(new Integer(rank - 1));
                cur.save();
                rank++;
            }
            source.setRank(new Integer(destRank));
        }
        coll.close();
    }

}