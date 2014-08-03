package com.arsdigita.london.cms.dublin.ui;

import java.util.Date;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.DateTime;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.DateTimeParameter;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.london.cms.dublin.DublinCoreItem;
import com.arsdigita.london.terms.Domain;

public abstract class DublinCoreFormSection extends FormSection
    implements FormInitListener, FormProcessListener {

    private static final Logger s_log = Logger.getLogger(DublinCoreFormSection.class);

    private final Widget m_audience;

    private Widget m_ccn_portal_instance;

    // private TextField m_contributor; // obviously not used anywhere, nevertheless
                                        // a db field! Not part of the UI
    private final Widget m_coverageSpatial;

    private final TextField m_coveragePostcode;

    private final Widget m_coverageUnit;

    private final DateTime m_temporalBegin;

    private final DateTime m_temporalEnd;

    private final TextArea m_creatorOwner;

    private final TextField m_creatorContact;

    private final TextArea m_description;

    // private TextField m_identifier;
    // private TextField m_location;
    // private TextField m_preservation;
    private final TextArea m_publisher;

    // private TextField m_relation;
    private final TextArea m_rights;

    // private TextField m_source;
    private final TextField m_coverageSpatialRef;

    private final TextField m_dateValid;

    private final TextField m_disposalReview;

    private final TextField m_keywords;

    private final boolean editableDescription;
    
    private final Submit m_cancel;


    public DublinCoreFormSection(boolean editableDescription) {

        this.editableDescription = editableDescription;
        m_audience = createControlledList("audience", 
                                          DublinCoreItem.getConfig()
                                                        .getAudienceDomain());

        m_coverageSpatial = createControlledList("coverageSpatial",
                DublinCoreItem.getConfig().getCoverageSpatialDomain());
        m_coverageUnit = createControlledList("coverageUnit", DublinCoreItem
                .getConfig().getCoverageUnitDomain());

        if (DublinCoreItem.getConfig().getUseCCNPortalMetadata()) {
            SingleSelect ss = new SingleSelect("ccnPortalInstance");
            ss.addOption(new Option("", "(none)"));
            ss.addOption(new Option("browse", "Browse"));
            m_ccn_portal_instance = ss;
        }

        m_coveragePostcode = new TextField(new StringParameter(
                "coveragePostcode"));
        m_coveragePostcode
                .addValidationListener(new StringLengthValidationListener(20));
        m_coveragePostcode.setSize(10);

        m_coverageSpatialRef = new TextField(new StringParameter(
                "coverageSpatialRef"));
        m_coverageSpatialRef
                .addValidationListener(new StringLengthValidationListener(20));
        m_coverageSpatialRef.setSize(10);

        m_temporalBegin = new DateTime(new DateTimeParameter("temporalBegin"));
        m_temporalEnd = new DateTime(new DateTimeParameter("temporalEnd"));

        m_dateValid = new TextField(new StringParameter("dateValid"));
        m_dateValid.addValidationListener(new StringLengthValidationListener(
                100));

        m_disposalReview = new TextField(new StringParameter("disposalReview"));
        m_disposalReview
                .addValidationListener(new StringLengthValidationListener(100));

        m_creatorOwner = new TextArea(new StringParameter("creatorOwner"));
        m_creatorOwner
                .addValidationListener(new StringLengthValidationListener(300));
        m_creatorOwner.setCols(50);
        m_creatorOwner.setRows(3);

        m_creatorContact = new TextField(new StringParameter("creatorContact"));
        m_creatorContact
                .addValidationListener(new StringLengthValidationListener(120));
        m_creatorContact.setSize(50);

        m_description = new TextArea(new StringParameter("description"));
        if (editableDescription) {
            m_description.addValidationListener(new StringLengthValidationListener(
                    4000));
            m_description.setCols(50);
            m_description.setRows(10);
            
        } else {
            m_description.setReadOnly(); 
        }

        m_publisher = new TextArea(new StringParameter("publisher"));
        m_publisher.addValidationListener(new StringLengthValidationListener(
                4000));
        m_publisher.setCols(50);
        m_publisher.setRows(5);

        m_rights = new TextArea(new StringParameter("rights"));
        m_rights
                .addValidationListener(new StringLengthValidationListener(4000));
        m_rights.setCols(50);
        m_rights.setRows(10);

        m_keywords = new TextField(new TrimmedStringParameter("keywords"));
        m_keywords.addValidationListener(new StringLengthValidationListener(
                4000));
        m_keywords.addValidationListener(new KeywordsValidationListener());
        m_keywords.setHint("Enter a list of keywords, separated with commas");
        m_keywords.setSize(50);

        add(new Label("Audience:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_audience);

        add(new Label("Coverage:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_coverageSpatial);

        add(new Label("Coverage (postcode):", Label.BOLD), ColumnPanel.RIGHT);
        add(m_coveragePostcode);

        add(new Label("Coverage (spatial reference number):", Label.BOLD),
                ColumnPanel.RIGHT);
        add(m_coverageSpatialRef);

        add(new Label("Coverage (unit):", Label.BOLD), ColumnPanel.RIGHT);
        add(m_coverageUnit);

        add(new Label("Coverage (temporal begin):", Label.BOLD),
                ColumnPanel.RIGHT);
        add(m_temporalBegin);

        add(new Label("Coverage (temporal end):", Label.BOLD),
                ColumnPanel.RIGHT);
        add(m_temporalEnd);

        add(new Label("Date (valid):", Label.BOLD), ColumnPanel.RIGHT);
        add(m_dateValid);

        add(new Label("Disposal Review", Label.BOLD), ColumnPanel.RIGHT);
        add(m_disposalReview);

        add(new Label("Creator (owner):", Label.BOLD), ColumnPanel.RIGHT);
        add(m_creatorOwner);

        add(new Label("Creator (contact):", Label.BOLD), ColumnPanel.RIGHT);
        add(m_creatorContact);

        add(new Label("Description:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_description);

        add(new Label("Publisher:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_publisher);

        add(new Label("Rights:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_rights);

        add(new Label("Keywords:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_keywords);

        if (DublinCoreItem.getConfig().getUseCCNPortalMetadata()) {
            add(new Label("Include page in portal:", Label.BOLD),
                    ColumnPanel.RIGHT);
            add(m_ccn_portal_instance);
        }

        SaveCancelSection saveCancel = new SaveCancelSection();
        m_cancel = saveCancel.getCancelButton();
        add(saveCancel, ColumnPanel.FULL_WIDTH);

        addInitListener(this);
        addProcessListener(this);
    }

    public Submit getCancelButton() {
        return m_cancel;
    }
    
    protected Widget createControlledList(String name, String key) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Creating controlled list with " + name + " key "
                        + key);
        }
        Domain domain = null;
        if (key != null) {
            try {
                domain = Domain.retrieve(key);
            } catch (DataObjectNotFoundException ex) {
                s_log.warn("Cannot find controlled list key " + key
                        + " for field " + name);
                // nada
            }
        }

        if (domain == null) {
            TextField widget = new TextField(name);
            widget.setSize(40);
            return widget;
        } else {
            ControlledList widget = new ControlledList(name, domain);
            return widget;
        }
    }

    /**
     * Checks to see if a default has been configured using
     * <code>com.arsdigita.london.cms.dublin.owner_contact_default</code>
     * if not uses the email of the logged in party.
     */
    private String getDefaultCreatorContact(Party party) {
        if (DublinCoreItem.getConfig().getOwnerContactDefault() != null) {
            return DublinCoreItem.getConfig().getOwnerContactDefault();
        } else {
            return party.getPrimaryEmail().toString();
        }
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {

        PageState state = fse.getPageState();
        ContentItem item = getSelectedItem(state);
        if (item == null) {
            return;
        }
        DublinCoreItem dcItem = DublinCoreItem.findByOwner(item);
        if (dcItem == null) {
            m_creatorOwner.setValue(state, DublinCoreItem.getConfig()
                    .getOwnerDefault());
            Party party = Kernel.getContext().getParty();
            m_creatorContact.setValue(state, getDefaultCreatorContact(party));
            m_publisher.setValue(state, DublinCoreItem.getConfig()
                    .getPublisherDefault());
            m_rights.setValue(state, DublinCoreItem.getConfig()
                    .getRightsDefault());
            m_description.setValue(state, getInitialDescription(item));
            return;
        }

        m_audience.setValue(state, dcItem.getAudience());

        m_coverageSpatial.setValue(state, dcItem.getCoverage());
        m_coveragePostcode.setValue(state, dcItem.getCoveragePostcode());
        m_coverageSpatialRef.setValue(state, dcItem.getCoverageSpatialRef());
        m_coverageUnit.setValue(state, dcItem.getCoverageUnit());

        m_temporalBegin.setValue(state, dcItem.getTemporalBegin());
        m_temporalEnd.setValue(state, dcItem.getTemporalEnd());

        m_dateValid.setValue(state, dcItem.getDateValid());
        m_disposalReview.setValue(state, dcItem.getDisposalReview());

        m_creatorOwner.setValue(state,
                dcItem.getCreatorOwner() == null ? DublinCoreItem.getConfig()
                        .getOwnerDefault() : dcItem.getCreatorOwner());

        Party party = Kernel.getContext().getParty();
        m_creatorContact
                .setValue(
                        state,
                        dcItem.getCreatorContact() == null ? getDefaultCreatorContact(party)
                                : dcItem.getCreatorContact());

        m_publisher.setValue(state,
                dcItem.getPublisher() == null ? DublinCoreItem.getConfig()
                        .getPublisherDefault() : dcItem.getPublisher());
        m_rights.setValue(state, dcItem.getRights() == null ? DublinCoreItem
                .getConfig().getRightsDefault() : dcItem.getRights());
        m_keywords.setValue(state, dcItem.getKeywords());
        if (editableDescription) {
            m_description.setValue(state, dcItem.getDescription());
        } else {
            m_description.setValue(state, getInitialDescription(item));
        }
        if (DublinCoreItem.getConfig().getUseCCNPortalMetadata()) {
            m_ccn_portal_instance
                    .setValue(state, dcItem.getCcnPortalInstance());
        }
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {

        PageState state = fse.getPageState();
        ContentItem item = getSelectedItem(state);
        if (item == null) {
            return;
        }
        DublinCoreItem dcItem = DublinCoreItem.findByOwner(item);
        if (dcItem == null) {
            dcItem = DublinCoreItem.create(item);
        }

        dcItem.setAudience((String) m_audience.getValue(state));

        dcItem.setCoverage((String) m_coverageSpatial.getValue(state));
        dcItem.setCoveragePostcode((String) m_coveragePostcode.getValue(state));
        dcItem.setCoverageSpatialRef((String) m_coverageSpatialRef
                .getValue(state));
        dcItem.setCoverageUnit((String) m_coverageUnit.getValue(state));

        dcItem.setTemporalBegin((Date) m_temporalBegin.getValue(state));
        dcItem.setTemporalEnd((Date) m_temporalEnd.getValue(state));
        dcItem.setDateValid((String) m_dateValid.getValue(state));

        dcItem.setDisposalReview((String) m_disposalReview.getValue(state));

        dcItem.setCreatorOwner((String) m_creatorOwner.getValue(state));
        dcItem.setCreatorContact((String) m_creatorContact.getValue(state));

        dcItem.setPublisher((String) m_publisher.getValue(state));
        dcItem.setRights((String) m_rights.getValue(state));

        dcItem.setKeywords((String) m_keywords.getValue(state));
        if (editableDescription) {
            saveDescription((String) m_description.getValue(state), item, dcItem);
        }

        if (DublinCoreItem.getConfig().getUseCCNPortalMetadata()) {
            dcItem.setCcnPortalInstance((String) m_ccn_portal_instance
                    .getValue(state));
        }
        dcItem.save();
    }

    protected abstract ContentItem getSelectedItem(PageState state);
    
    protected String getInitialDescription(ContentItem item) {
        return "";
    }
    
    protected void saveDescription(String description, ContentItem item, DublinCoreItem dcItem ) {
        dcItem.setDescription(description);
    }

}
