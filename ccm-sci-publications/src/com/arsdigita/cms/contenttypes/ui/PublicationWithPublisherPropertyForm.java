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
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.ui.ItemSearchWidget;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationWithPublisherPropertyForm
        extends PublicationPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final Logger s_log = Logger.getLogger(
            PublicationWithPublisherPropertyForm.class);
    private PublicationWithPublisherPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "publisher";
    public static final String ID = "PublicationWithPublisherEdit";
private ItemSelectionModel m_itemModel;

    public PublicationWithPublisherPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public PublicationWithPublisherPropertyForm(
            ItemSelectionModel itemModel,
            PublicationWithPublisherPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.with_publisher.publisher").localize()));
        m_itemSearch =
        new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                Publisher.class.getName()));
        add(m_itemSearch);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.with_publisher.isbn").localize()));
        ParameterModel isbnParam = new StringParameter(
                PublicationWithPublisher.ISBN);
        TextField isbn = new TextField(isbnParam);
        add(isbn);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        PublicationWithPublisher publication =
                                 (PublicationWithPublisher) super.
                initBasicWidgets(fse);

        data.put(ITEM_SEARCH, publication.getPublisher());
        data.put(PublicationWithPublisher.ISBN, publication.getISBN());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        PublicationWithPublisher publication =
                                 (PublicationWithPublisher) super.
                processBasicWidgets(fse);

        if ((publication != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            publication.setPublisher((Publisher) data.get(ITEM_SEARCH));
            publication.setISBN((String) data.get(PublicationWithPublisher.ISBN));

            publication.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
