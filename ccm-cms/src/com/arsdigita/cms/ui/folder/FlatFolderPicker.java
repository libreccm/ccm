/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class FlatFolderPicker extends AbstractFolderPicker {

    public FlatFolderPicker(String name) {
        super(name);
    }

    protected void addOptions(PageState state, SingleSelect target) {
        target.addOption(new Option("", ""));

        final ContentSection section = CMS.getContext().getContentSection();        
        final Folder root = section.getRootFolder();

        final String path = ""; 
        
        addFolder(target, path, root);

    }

    private void addFolders(final SingleSelect target, final String path, ItemCollection folders) {
        while (folders.next()) {
            addFolder(target, path, (Folder) folders.getContentItem());
        }
    }

    private void addFolder(final SingleSelect target, final String prefix, final Folder folder) {
        final String path;
        if ("/".equals(folder.getName()) || (prefix == null) || prefix.isEmpty() || prefix.endsWith("/")) {
            path = String.format("%s%s", prefix, folder.getName());
        } else {
            path = String.format("%s/%s", prefix, folder.getName());
        }

        target.addOption(new Option(folder.getOID().toString(), path));

        final ItemCollection children = folder.getChildren();
        children.addEqualsFilter("objectType", Folder.BASE_DATA_OBJECT_TYPE);

        if (!children.isEmpty()) {
            addFolders(target, path, children);
        }
    }

}
