package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationWithPublisherSetPublisherForm
        extends BasicItemForm
        implements FormInitListener,
                   FormProcessListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "setPublisher";
      private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }


    public PublicationWithPublisherSetPublisherForm(
            final ItemSelectionModel itemModel) {
        super("PublicationWithPublisherSetPublisher", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.with_publisher.publisher").localize()));
        itemSearch =
        new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType(Publisher.class.getName()));
        if ((config.getDefaultPublisherFolder() != null) && config.getDefaultPublisherFolder() != 0) {
            itemSearch.setDefaultCreationFolder(new Folder(new BigDecimal(config.getDefaultPublisherFolder())));            
        }
        itemSearch.setEditAfterCreate(false);
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        PublicationWithPublisher publication =
                                 (PublicationWithPublisher) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            Publisher publisher = (Publisher) data.get(ITEM_SEARCH);
            publisher = (Publisher) publisher.getContentBundle().getInstance(publication.getLanguage());

            publication.setPublisher(publisher);
            itemSearch.publishCreatedItem(data, publisher);
        }

        init(fse);
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.with_publisher.publisher.no_publisher_selected"));
            return;
        }

        PublicationWithPublisher publication =
                                 (PublicationWithPublisher) getItemSelectionModel().
                getSelectedObject(state);
        Publisher publisher = (Publisher) data.get(ITEM_SEARCH);
        if (!(publisher.getContentBundle().hasInstance(publication.getLanguage(),
                                                       Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.with_publisher.publisher.no_suitable_language_variant"));            
        }
    }

}
