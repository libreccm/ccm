package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Review;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Jens Pelzetter
 */
public class ReviewPropertyForm
        extends ArticleInJournalPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private ReviewPropertiesStep m_step;
    public static final String ID = "ReviewEdit";

    public ReviewPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public ReviewPropertyForm(ItemSelectionModel itemModel,
                              ReviewPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Review review = (Review) initBasicWidgets(fse);       
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Review review = (Review) processBasicWidgets(fse);      
    }
}
