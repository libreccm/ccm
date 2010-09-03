package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SeriesPropertyForm
        extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final Logger s_log =
                                Logger.getLogger(SeriesPropertyForm.class);
    private SeriesPropertiesStep m_step;
    public static final String ID = "Series_edit";

    public SeriesPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SeriesPropertyForm(ItemSelectionModel itemModel,
                              SeriesPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.series.title").localize()));
        ParameterModel titleParam = new StringParameter(Series.NAME);
        TextField title = new TextField(titleParam);
        add(title);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        Series series = (Series) super.initBasicWidgets(fse);

        data.put(Series.NAME, series.getTitle());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        Series series = (Series) super.processBasicWidgets(fse);

        if ((series != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            series.setTitle((String) data.get(Series.NAME));

            series.save();
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
