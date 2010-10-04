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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.InProceedings;

/**
 *
 * @author Jens Pelzetter
 */
public class InProceedingsPropertyForm
        extends PublicationPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private InProceedingsPropertiesStep m_step;
    public static final String ID = "InProceedingsEdit";

    public InProceedingsPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public InProceedingsPropertyForm(ItemSelectionModel itemModel,
                                     InProceedingsPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();
        
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.pages_from").localize()));
        ParameterModel pagesFromParam =
                       new IntegerParameter(InProceedings.PAGES_FROM);
        TextField pagesFrom = new TextField(pagesFromParam);
        add(pagesFrom);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.pages_to").localize()));
        ParameterModel pagesToParam =
                       new IntegerParameter(InProceedings.PAGES_TO);
        TextField pagesTo = new TextField(pagesToParam);
        add(pagesTo);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        InProceedings inProceedings =
                      (InProceedings) super.initBasicWidgets(fse);
     
        data.put(InProceedings.PAGES_FROM,
                 inProceedings.getPagesFrom());
        data.put(InProceedings.PAGES_TO,
                 inProceedings.getPagesTo());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        InProceedings inProceedings =
                      (InProceedings) super.processBasicWidgets(fse);

        if ((inProceedings != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {           
            inProceedings.setPagesFrom(
                    (Integer) data.get(InProceedings.PAGES_FROM));
            inProceedings.setPagesTo(
                    (Integer) data.get(InProceedings.PAGES_TO));

            inProceedings.save();
        }     
    }
}
