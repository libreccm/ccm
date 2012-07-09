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
        target.addOption(new Option(null, ""));

        final ContentSection section = CMS.getContext().getContentSection();
        //final String sectionName = section.getName();
        final Folder root = section.getRootFolder();

        final String path = ""; // String.format("%s:", sectionName);

        //addFolders(target, path, root.getChildren().addEqualsFilter("objectType", Folder.BASE_DATA_OBJECT_TYPE));
        addFolder(target, path, root);


//        DataCollection terms = SessionManager.getSession()
//            .retrieve(Term.BASE_DATA_OBJECT_TYPE);
//        terms.addPath("model.id");
//        terms.addPath("model.objectType");
//        terms.addPath("model.name");
//        terms.addPath("domain.title");
//        terms.addOrder("domain.title");
//        terms.addOrder("model.name");
//        
//        target.addOption(new Option(null, "-- pick one --"));
//        while (terms.next()) {
//            target.addOption(
//                new Option(new OID((String)terms.get("model.objectType"),
//                                   terms.get("model.id")).toString(),
//                           terms.get("domain.title") + " -> " + 
//                           terms.get("model.name")));
//        }
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

        target.addOption(new Option(folder.getID().toString(), path));

        final ItemCollection children = folder.getChildren();
        children.addEqualsFilter("objectType", Folder.BASE_DATA_OBJECT_TYPE);

        if (!children.isEmpty()) {
            addFolders(target, path, children);
        }
    }

}
