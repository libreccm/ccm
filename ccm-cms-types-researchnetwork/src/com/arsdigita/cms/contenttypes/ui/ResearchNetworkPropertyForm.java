package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ResearchNetwork;
import com.arsdigita.cms.contenttypes.ResearchNetworkGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkPropertyForm extends BasicPageForm implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private final static Logger s_log = Logger.getLogger(ResearchNetworkPropertyForm.class);
    private ResearchNetworkPropertiesStep m_step;
    public final static String RESEARCHNETWORK_TITLE = ResearchNetwork.RESEARCHNETWORK_TITLE;
    public final static String RESEARCHNETWORK_DIRECTION = ResearchNetwork.RESEARCHNETWORK_DIRECTION;
    public final static String RESEARCHNETWORK_COORDINATION = ResearchNetwork.RESEARCHNETWORK_COORDINATION;
    public final static String RESEARCHNETWORK_DESCRIPTION = ResearchNetwork.RESEARCHNETWORK_DESCRIPTION;
    public final static String RESEARCHNETWORK_WEBSITE = ResearchNetwork.RESEARCHNETWORK_WEBSITE;
    public final static String ID = "ResearchNetwork_edit";

    public ResearchNetworkPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public ResearchNetworkPropertyForm(ItemSelectionModel itemModel, ResearchNetworkPropertiesStep step) {
        super(ID, itemModel);
        this.m_step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        ParameterModel researchNetworkTitleParam = new StringParameter(RESEARCHNETWORK_TITLE);
        TextField researchNetworkTitle = new TextField(researchNetworkTitleParam);
        researchNetworkTitle.addValidationListener(new NotNullValidationListener());
        researchNetworkTitle.setLabel(ResearchNetworkGlobalizationUtil.globalize(
                        "cms.contenttypes.researchnetwork.ui.title"));
        add(researchNetworkTitle);

        TextArea researchNetworkAreaDescription = new TextArea(RESEARCHNETWORK_DESCRIPTION);
        researchNetworkAreaDescription.setLabel(ResearchNetworkGlobalizationUtil
                .globalize("cms.contenttypes.researchnetwork.ui.description"));
        researchNetworkAreaDescription.setRows(10);
        researchNetworkAreaDescription.setCols(30);
        add(researchNetworkAreaDescription);

        TextArea researchNetworkDirection = new TextArea(RESEARCHNETWORK_DIRECTION);
        researchNetworkDirection.setLabel(ResearchNetworkGlobalizationUtil
                .globalize("cms.contenttypes.researchnetwork.ui.direction"));
        researchNetworkDirection.setRows(5);
        researchNetworkDirection.setCols(30);
        add(researchNetworkDirection);

        TextArea researchNetworkCoordination = new TextArea(RESEARCHNETWORK_COORDINATION);
        researchNetworkCoordination.setLabel(ResearchNetworkGlobalizationUtil
                .globalize("cms.contenttypes.researchnetwork.ui.coordination"));
        researchNetworkCoordination.setRows(5);
        researchNetworkCoordination.setCols(30);
        add(researchNetworkCoordination);

        ParameterModel researchNetworkWebsiteParam = new StringParameter(RESEARCHNETWORK_WEBSITE);
        TextField researchNetworkWebsite = new TextField(researchNetworkWebsiteParam);
        researchNetworkWebsite.setLabel(ResearchNetworkGlobalizationUtil
                .globalize("cms.contenttypes.researchnetwork.ui.website"));
        add(researchNetworkWebsite);
    }

    /**
     * 
     * @param e
     * @throws FormProcessException 
     */
    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        ResearchNetwork network = (ResearchNetwork) super.initBasicWidgets(e);

        data.put(RESEARCHNETWORK_TITLE, network.getResearchNetworkTitle());
        data.put(RESEARCHNETWORK_DIRECTION, network.getResearchNetworkDirection());
        data.put(RESEARCHNETWORK_COORDINATION, network.getResearchNetworkCoordination());
        data.put(RESEARCHNETWORK_WEBSITE, network.getResearchNetworkWebsite());
        data.put(RESEARCHNETWORK_DESCRIPTION, network.getResearchNetworkDescription());
    }
    
    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();

        ResearchNetwork network = (ResearchNetwork) super.processBasicWidgets(e);

        if ((network != null) && (getSaveCancelSection().getSaveButton().isSelected(e.getPageState()))) {
            network.setResearchNetworkTitle((String) data.get(RESEARCHNETWORK_TITLE));
            network.setResearchNetworkDirection((String) data.get(RESEARCHNETWORK_DIRECTION));
            network.setResearchNetworkCoordination(((String) data.get(RESEARCHNETWORK_COORDINATION)));
            network.setResearchNetworkWebsite((String) data.get(RESEARCHNETWORK_WEBSITE));
            network.setResearchNetworkDescription((String) data.get(RESEARCHNETWORK_DESCRIPTION));

            network.save();
        }

        if (this.m_step != null) {
            this.m_step.maybeForwardToNextStep(e.getPageState());
        }
    }

    @Override
    public void submitted(FormSectionEvent e) throws FormProcessException {
        if ((this.m_step != null) && (getSaveCancelSection().getCancelButton().isSelected(e.getPageState()))) {
            this.m_step.cancelStreamlinedCreation(e.getPageState());
        }
    }
}
