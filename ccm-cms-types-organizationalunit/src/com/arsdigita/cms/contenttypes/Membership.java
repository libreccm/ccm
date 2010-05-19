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
import java.text.DateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Membership extends ACSObject {

    private final static Logger logger = Logger.getLogger(Membership.class);
    public final static String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Membership";
    public final static String STATUS = "status";
    public final static String FROM = "memberFrom";
    public final static String TO = "memberTo";
    public final static String MEMBERSHIP_OWNER = "membershipOwner";
    public final static String TARGET_ITEM = "targetItem";

    public Membership() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public Membership(DataObject obj) {
        super(obj);
    }

    public Membership(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Membership(OID oid) {
        super(oid);
    }

    public Membership(String type) {
        super(type);
    }

    public MembershipStatus getStatus() {
        DataObject obj = (DataObject) get(STATUS);
        if (obj != null) {
            return new MembershipStatus(obj);
        } else {
            return null;
        }
    }

    public void setStatus(MembershipStatus status) {
        setAssociation(STATUS, status);
    }

    public String getStatusName() {
        String name = "";
        if (getStatus() != null) {
            name = getStatus().getStatusName();
        }
        return name;
    }

    public String formatDate(Date date) {
        if (date != null) {
            return DateFormat.getDateInstance(DateFormat.LONG).format(date);
        } else {
            return null;
        }
    }

    public Date getFrom() {
        return (Date) get(FROM);
    }

    public String getDisplayFrom() {
        return formatDate(getFrom());
    }

    public void setFrom(Date from) {
        set(FROM, from);
    }

    public Date getTo() {
        return (Date) get(TO);
    }

    public String getDisplayTo() {
        return formatDate(getTo());
    }

    public void setTo(Date to) {
        set(TO, to);
    }

    public OrganizationalUnit getMembershipOwner() {
        DataObject obj = (DataObject) get(MEMBERSHIP_OWNER);
        if (obj == null) {
            return null;
        } else {
            return (OrganizationalUnit) DomainObjectFactory.newInstance(obj);
        }
    }

    public void setMembershipOwner(OrganizationalUnit ou) {
        Assert.exists(ou, OrganizationalUnit.class);
        logger.debug("Setting membership owner to " + ou.getOrganizationalUnitName());
        setAssociation(MEMBERSHIP_OWNER, ou);        
    }

    public Member getTargetItem() {
        DataObject obj = (DataObject) get(TARGET_ITEM);
        return (Member) DomainObjectFactory.newInstance(obj);
    }

    public void setTargetItem(Member person) {
        Assert.exists(person, Member.class);
        setAssociation(TARGET_ITEM, person);
    }

    public String getURI(PageState state) {
        Member person = this.getTargetItem();

        if (person == null) {
            logger.error(getOID() + " is a link between a OrganizationalUnit and a Person, but the associated Person is null");
            return "";
        }

        ContentSection section = person.getContentSection();
        ItemResolver resolver = section.getItemResolver();
        String url = resolver.generateItemURL(state, person, section, person.getVersion());

        return URL.there(state.getRequest(), url).toString();
    }

    public static DataCollection getReferingPersons(Member person) {
        Session session = SessionManager.getSession();
        DataCollection memberships = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter = memberships.addInSubqueryFilter("id", "com.arsdigita.cms.contenttypes.getReferingPersons");
        filter.set("itemID", person.getID());

        return memberships;
    }

    public static DataCollection getMemberships(OrganizationalUnit ou) {
        Session session = SessionManager.getSession();
        DataCollection dc = session.retrieve(BASE_DATA_OBJECT_TYPE);
        dc.addEqualsFilter(MEMBERSHIP_OWNER + ".id", ou.getID());
        return dc;
    } 
}
