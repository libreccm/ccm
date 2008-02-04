package com.arsdigita.cms.webpage.ui.authoring;

import com.arsdigita.cms.webpage.Webpage;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.ItemPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.web.Web;

import java.text.DateFormat;


/**
 * Authoring step to edit the simple attributes of the Webpage content
 * type (and its subclasses).
 * This authoring step replaces
 * the <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 */
public class WebpagePropertiesStep
    extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    public WebpagePropertiesStep( ItemSelectionModel itemModel,
                                  AuthoringKitWizard parent ) {
        super( itemModel, parent );

        BasicPageForm editSheet;

        editSheet = new WebpagePropertiesForm( itemModel );
        add( EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel),
             editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent( getWebpagePropertySheet( itemModel ) );
    }

    /**
     * Returns a component that displays the properties of the
     * Webpage specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getWebpagePropertySheet( ItemSelectionModel
                                                     itemModel ) {
        //ItemPropertySheet sheet = new ItemPropertySheet( itemModel );
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel,
                                                                false);
        sheet.add( "Name:", Webpage.NAME );
        sheet.add( "Title:", Webpage.TITLE );
        sheet.add( "Description:", Webpage.DESCRIPTION );
        sheet.add("Body:", Webpage.BODY);
 
        return sheet;
    }
}
