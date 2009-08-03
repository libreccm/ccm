/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.URL;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkMembership extends ACSObject {

    private static final Logger s_log = Logger.getLogger(ResearchNetwork.class);
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.ResearchNetworkMembership";
    public static final String MEMBER_OWNER = "membershipOwner";
    public static final String TARGET_ITEM = "targetItem";

    public ResearchNetworkMembership() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public ResearchNetworkMembership(DataObject obj) {
        super(obj);
    }

    public ResearchNetworkMembership(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ResearchNetworkMembership(OID oid) {
        super(oid);
    }

    public ResearchNetworkMembership(String type) {
        super(type);
    }

    public ResearchNetwork getResearchNetworkMembershipOwner() {
        DataObject obj = (DataObject) get(MEMBER_OWNER);
        if (obj == null) {
            return null;
        } else {
            return (ResearchNetwork) DomainObjectFactory.newInstance(obj);
        }
    }

    public void setResearchNetworkMembershipOwner(ResearchNetwork rn) {
        Assert.exists(rn, ResearchNetwork.class);
        s_log.debug("Setting member owner to " + rn.getResearchNetworkTitle());
        setAssociation(MEMBER_OWNER, rn);
    }

    public Person getTargetItem() {
        DataObject obj = (DataObject) get(TARGET_ITEM);
        return (Person) DomainObjectFactory.newInstance(obj);
    }

    public void setTargetItem(Person person) {
        Assert.exists(person, Person.class);
        setAssociation(TARGET_ITEM, person);
    }

    public String getURI(PageState state) {
        Person person = this.getTargetItem();

        if (person == null) {
            s_log.error(getOID() + " is a link between a ResearchNetwork and a Person, but the associated person is null");
            return "";
        }

        ContentSection section = person.getContentSection();
        ItemResolver resolver = section.getItemResolver();
        String url = resolver.generateItemURL(state, person, section, person.getVersion());

        return URL.there(state.getRequest(), url).toString();
    }

    public static DataCollection getReferingPersons(Person person) {
        Session session = SessionManager.getSession();
        DataCollection members = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter = members.addInSubqueryFilter("id", "com.arsdigita.cms.contenttypes.getReferingPersons");
        filter.set("itemID", person.getID());

        return members;
    }

    public static DataCollection getMemberships(ResearchNetwork rn) {
        Session session = SessionManager.getSession();
        DataCollection dc = session.retrieve(BASE_DATA_OBJECT_TYPE);
        dc.addEqualsFilter(MEMBER_OWNER + ".id", rn.getID());
        return dc;
    }
}
