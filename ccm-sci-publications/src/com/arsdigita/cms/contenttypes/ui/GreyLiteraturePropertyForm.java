package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GreyLiterature;

/**
 *
 * @author Jens Pelzetter
 */
public class GreyLiteraturePropertyForm
        extends UnPublishedPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private GreyLiteraturePropertiesStep m_step;
    public static final String ID = "GreyLiteratureEdit";

    public GreyLiteraturePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public GreyLiteraturePropertyForm(ItemSelectionModel itemModel,
                                      GreyLiteraturePropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.greyliterature.pages_from").
                localize()));
        ParameterModel fromParam = new IntegerParameter(
                GreyLiterature.PAGES_FROM);
        TextField pagesFrom = new TextField(fromParam);
        add(pagesFrom);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.greyliterature.pages_to").
                localize()));
        ParameterModel toParam = new IntegerParameter(
                GreyLiterature.PAGES_TO);
        TextField pagesTo = new TextField(toParam);
        add(pagesTo);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.greyliterature.url").localize()));
        ParameterModel urlParam = new StringParameter(
                GreyLiterature.URL);
        TextField url = new TextField(urlParam);
        add(url);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        GreyLiterature grey = (GreyLiterature) initBasicWidgets(fse);

        data.put(GreyLiterature.PAGES_FROM, grey.getPagesFrom());
        data.put(GreyLiterature.PAGES_TO, grey.getPagesTo());
        data.put(GreyLiterature.URL, grey.getUrl());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        GreyLiterature grey = (GreyLiterature) processBasicWidgets(fse);

        if ((grey != null) && getSaveCancelSection().getSaveButton().isSelected(fse.
                getPageState())) {
            grey.setPagesFrom((Integer) data.get(GreyLiterature.PAGES_FROM));
            grey.setPagesTo((Integer) data.get(GreyLiterature.PAGES_TO));
            grey.setUrl((String) data.get(GreyLiterature.URL));

            grey.save();
        }       
    }
}
