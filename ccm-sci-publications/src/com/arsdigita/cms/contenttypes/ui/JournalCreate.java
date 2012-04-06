package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.JournalBundle;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.PageCreate;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class JournalCreate extends PageCreate {

    public JournalCreate(final ItemSelectionModel itemModel,
                         final CreationSelector parent) {
        super(itemModel, parent);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final ContentSection section = m_parent.getContentSection(state);
        final Folder folder = m_parent.getFolder(state);

        final ContentPage item = createContentPage(state);
        item.setLanguage((String) data.get(LANGUAGE));
        item.setName((String) data.get(NAME));
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            item.setLaunchDate((Date) data.get(LAUNCH_DATE));
        }

        final JournalBundle bundle = createBundle(item);
        bundle.setParent(folder);
        bundle.setContentSection(section);
        bundle.save();

        m_workflowSection.applyWorkflow(state, item);

        m_parent.editItem(state, item);        
    }
    
    protected JournalBundle createBundle(final ContentItem primary) {
        return new JournalBundle(primary);
    }
}
