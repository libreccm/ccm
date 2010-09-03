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
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.ui.ItemSearchWidget;

/**
 *
 * @author Jens Pelzetter
 */
public class UnPublishedPropertyForm
        extends PublicationPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private UnPublishedPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "organization";
    public static final String ID = "UnPublishedEdit";

    public UnPublishedPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public UnPublishedPropertyForm(ItemSelectionModel itemModel,
                                   UnPublishedPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.place").localize()));
        ParameterModel placeParam =
                       new StringParameter(InternetArticle.PLACE);
        TextField place = new TextField(placeParam);
        add(place);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.organization").localize()));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                            ContentType.
                findByAssociatedObjectType(
                GenericOrganizationalUnit.class.getName()));
        add(m_itemSearch);

          add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.number").localize()));
        ParameterModel numberParam =
                       new StringParameter(UnPublished.NUMBER);
        TextField number = new TextField(numberParam);
        add(number);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.number_of_pages").localize()));
        ParameterModel numberOfPagesParam =
                       new IntegerParameter(UnPublished.NUMBER_OF_PAGES);
        TextField numberOfPages = new TextField(numberOfPagesParam);
        add(numberOfPages);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        UnPublished unpublished = (UnPublished) initBasicWidgets(fse);

        data.put(UnPublished.PLACE, unpublished.getPlace());
        data.put(ITEM_SEARCH, unpublished.getOrganization());
        data.put(UnPublished.NUMBER, unpublished.getNumber());
        data.put(UnPublished.NUMBER_OF_PAGES, unpublished.getNumberOfPages());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        UnPublished unpublished = (UnPublished) processBasicWidgets(fse);

        if ((unpublished != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            unpublished.setPlace((String) data.get(UnPublished.PLACE));
            unpublished.setOrganization(
                    (GenericOrganizationalUnit) data.get(ITEM_SEARCH));
            unpublished.setNumber((String) data.get(UnPublished.NUMBER));
            unpublished.setNumberOfPages(
                    (Integer) data.get(UnPublished.NUMBER_OF_PAGES));

            unpublished.save();        
        }
    }
}
