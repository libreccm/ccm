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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class InternetArticleOrganizationForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "internetArticleOrga";
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public InternetArticleOrganizationForm(final ItemSelectionModel itemModel) {
        super("InternetArticleOrganizationForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.select_organization")));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                          ContentType.findByAssociatedObjectType(
                GenericOrganizationalUnit.class.getName()));
        if ((config.getDefaultOrganizationsFolder() != null) && (config.getDefaultOrganizationsFolder() != 0)) {
            itemSearch.setDefaultCreationFolder(new Folder(new BigDecimal(config.getDefaultOrganizationsFolder())));
        }

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
        InternetArticle article = (InternetArticle) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericOrganizationalUnit orga = (GenericOrganizationalUnit) data.get(ITEM_SEARCH);
            orga = (GenericOrganizationalUnit) orga.getContentBundle().
                    getInstance(article.getLanguage());

            article.setOrganization(orga);
            itemSearch.publishCreatedItem(data, orga);
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
                    "publications.ui.internetarticle.select_organization.no_orga_selected"));
            return;
        }

        InternetArticle article = (InternetArticle) getItemSelectionModel().
                getSelectedObject(state);
        GenericOrganizationalUnit orga = (GenericOrganizationalUnit) data.get(
                ITEM_SEARCH);
        if (!(orga.getContentBundle().hasInstance(article.getLanguage(), Kernel.getConfig().languageIndependentItems()))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.internetarticle.select_organization.no_suitable_language_variant"));
            return;
        }
    }

}
