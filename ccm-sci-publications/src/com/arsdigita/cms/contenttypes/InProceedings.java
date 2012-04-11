/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.InProceedingsExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Jens Pelzetter
 */
public class InProceedings extends Publication {

    public static final String PAGES_FROM = "pagesFrom";
    public static final String PAGES_TO = "pagesTo";
    public static final String PROCEEDINGS = "proceedings";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.InProceedings";

    public InProceedings() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public InProceedings(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public InProceedings(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public InProceedings(DataObject dataObject) {
        super(dataObject);
    }

    public InProceedings(String type) {
        super(type);
    }

    public InProceedingsBundle getInProceedingsBundle() {
        return (InProceedingsBundle) getContentBundle();
    }

    public Integer getPagesFrom() {
        return (Integer) get(PAGES_FROM);
    }

    public void setPagesFrom(Integer pagesFrom) {
        set(PAGES_FROM, pagesFrom);
    }

    public Integer getPagesTo() {
        return (Integer) get(PAGES_TO);
    }

    public void setPagesTo(Integer pagesTo) {
        set(PAGES_TO, pagesTo);
    }

    public Proceedings getProceedings() {
        /*DataCollection collection;

         collection = (DataCollection) get(PROCEEDINGS);

         if (collection.size() == 0) {
         return null;
         } else {
         DataObject dobj;

         collection.next();
         dobj = collection.getDataObject();
         collection.close();

         return (Proceedings) DomainObjectFactory.newInstance(dobj);
         }*/

        final ProceedingsBundle bundle =
                                getInProceedingsBundle().getProceedings();

        if (bundle == null) {
            return null;
        } else {
            return (Proceedings) bundle.getPrimaryInstance();
        }
    }

    public Proceedings getProceedings(final String language) {
        final ProceedingsBundle bundle =
                                getInProceedingsBundle().getProceedings();

        if (bundle == null) {
            return null;
        } else {
            return (Proceedings) bundle.getInstance(language);
        }
    }

    public void setProceedings(final Proceedings proceedings) {
        /*Proceedings oldProceedings;

         oldProceedings = getProceedings();
         if (oldProceedings != null) {
         remove(PROCEEDINGS, oldProceedings);
         }

         if (proceedings != null) {
         Assert.exists(proceedings, Proceedings.class);
         DataObject link = add(PROCEEDINGS, proceedings);
         link.set(Proceedings.PAPER_ORDER,
         Integer.valueOf((int) proceedings.getPapers().size()));
         link.save();
         }*/

        getInProceedingsBundle().setProceedings(proceedings);
    }

    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new InProceedingsExtraXmlGenerator());
        return generators;
    }

    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.
                getExtraListXMLGenerators();
        generators.add(new InProceedingsExtraXmlGenerator());
        return generators;
    }
}
