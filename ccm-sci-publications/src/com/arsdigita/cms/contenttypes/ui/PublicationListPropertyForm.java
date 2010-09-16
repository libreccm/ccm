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
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicationList;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationListPropertyForm
        extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private PublicationListPropertiesStep m_step;
    public static final String ID = "PublicationList_edit";

    public PublicationListPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public PublicationListPropertyForm(ItemSelectionModel itemModel,
                                       PublicationListPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publicationlist.title").localize()));
        ParameterModel titleParam = new StringParameter(PublicationList.TITLE);
        TextField title = new TextField(titleParam);
        add(title);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publicationlist.description").localize()));
        ParameterModel descParam =
                       new StringParameter(PublicationList.DESCRIPTION);
        TextArea desc = new TextArea(descParam);
        desc.setCols(60);
        desc.setRows(16);
        add(desc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PublicationList list = (PublicationList) super.initBasicWidgets(fse);

        data.put(PublicationList.TITLE, list.getTitle());
        data.put(PublicationList.DESCRIPTION, list.getDescription());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PublicationList list = (PublicationList) super.processBasicWidgets(fse);

        if ((list != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            list.setTitle((String) data.get(PublicationList.TITLE));
            list.setDescription((String) data.get(PublicationList.DESCRIPTION));

            list.save();
        }
    }

    @Override
    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if ((m_step != null) && getSaveCancelSection().getCancelButton().
                isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }

    }
}
