/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 *
 * @author Sören Bernstein (quasimodo) <sbernstein@zes.uni-bremen.de>
 */
public class FlatFolderPicker extends AbstractFolderPicker {
    
    public FlatFolderPicker(String name) {
        super(name);
    }
    
    protected void addOptions(PageState state, SingleSelect target) {            
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
}
