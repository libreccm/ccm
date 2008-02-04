package com.arsdigita.cms.docmgr.ui.authoring;

import java.math.BigDecimal;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.docmgr.DocLink;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 * Form to edit the basic properties of an document. This form can be
 * extended to create forms for Document subclasses.
 */
public class DocLinkPropertyForm extends BasicPageForm implements FormProcessListener, FormValidationListener, FormInitListener {

    public static final String TARGET_DOC_ID = "targetLinkID";
    public static final String EXTERNAL_URL = "externalURL";
	public static final String DESCRIPTION = "description";

    /**
     * Creates a new form to edit the Document object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Document to work on
     */
    public DocLinkPropertyForm( ItemSelectionModel itemModel ) {
        super( ID, itemModel );
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

        add( new Label( "Target Document Link ID:" ) );
        ParameterModel targetDocParam
            = new BigDecimalParameter( TARGET_DOC_ID );
        TextField targetDoc = new TextField( targetDocParam );
        add( targetDoc );

        add(new Label("External URL"));
        ParameterModel externalUrlParam
            = new StringParameter( EXTERNAL_URL );
        TextField externalURL = new TextField( externalUrlParam );
        add( externalURL );

		add(new Label("Description:"));
		ParameterModel descriptionParam = new StringParameter(DESCRIPTION);
		TextArea description = new TextArea(descriptionParam);
		description.setCols(40);
		description.setRows(5);
		add(description);
    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init( FormSectionEvent fse ) {
        // Do some initialization hook stuff
        FormData data = fse.getFormData();
        DocLink docLink
            = (DocLink) super.initBasicWidgets( fse );

        if (docLink.getTarget() != null) {
            data.put( TARGET_DOC_ID,      docLink.getTarget().getID() );
        }
        data.put( EXTERNAL_URL,      docLink.getExternalURL() );
		data.put(DESCRIPTION, docLink.getDescription());
	}

	/** Form validation hook. Validate data. */
	public void validate(FormSectionEvent fse) throws FormProcessException {
		if (getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {
			FormData data = fse.getFormData();
			
			String url = data.getString(EXTERNAL_URL);
			if (url != null && url.length() > 4000) {
				data.addError(EXTERNAL_URL, "This parameter is longer than 4000 characters.");
			}
			
			String desc = data.getString(DESCRIPTION);
			if (desc != null && desc.length() > 4000) {
				data.addError(DESCRIPTION, "This parameter is longer than 4000 characters.");
			}
		}
    }

    /** Form processing hook. Saves Event object. */
    public void process( FormSectionEvent fse ) throws FormProcessException {
        FormData data = fse.getFormData();

        DocLink docLink
            = (DocLink) super.processBasicWidgets( fse );

        // save only if save button was pressed
        if( docLink != null
            && getSaveCancelSection().getSaveButton()
            .isSelected( fse.getPageState() ) ) {

            if (data.get(TARGET_DOC_ID) != null) {
                docLink.setTarget(new Document((BigDecimal)data.get(TARGET_DOC_ID)));
            }
			docLink.setExternalURL(data.getString(EXTERNAL_URL));
			docLink.setDescription(data.getString(DESCRIPTION));
            docLink.save();
        }
        
    }
}
