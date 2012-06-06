package com.arsdigita.cms.webpage.ui.authoring;

import com.arsdigita.cms.util.GlobalizationUtil; 

import com.arsdigita.cms.webpage.Webpage;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

import java.sql.SQLException;

import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.webpage.installer.Initializer;
import com.arsdigita.cms.webpage.ui.CategoriesPrintListener;
import com.arsdigita.cms.webpage.ui.AuthorLabelPrinter;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Form to edit the basic properties of an webpage. This form can be
 * extended to create forms for Webpage subclasses.
 */
public class WebpagePropertiesForm
    extends BasicPageForm
    implements FormProcessListener, FormInitListener {
    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(WebpagePropertiesForm.class);

    public static final String DESCRIPTION = "description";
    public static final String BODY = "body";
    public static final String CATEGORIES = "categories";

    /**
     * Creates a new form to edit the Webpage object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Webpage to work on
     */
    public WebpagePropertiesForm( ItemSelectionModel itemModel ) {
        super( ID, itemModel );
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

	add(new Label(new AuthorLabelPrinter()));
	add(new TextField(Webpage.AUTHOR));
	
	add(new Label("Categories"));
	MultipleSelect catSelect
	    = new MultipleSelect(CATEGORIES);
	catSelect.setSize(20);
	try {
	    ContentSection section = Initializer.getConfig().getWebpageSection();
	    
	    catSelect.addPrintListener
		(new CategoriesPrintListener
		    (section));
	} catch (java.util.TooManyListenersException tmex) {
	    throw new UncheckedWrapperException(tmex.getMessage());
	}
	add(catSelect);
	
        add( new Label( "Description:" ) );
        ParameterModel descriptionParam
            = new StringParameter( DESCRIPTION );
        TextArea description = new TextArea( descriptionParam );
        description.setCols( 80 );
        description.setRows( 20 );
        add( description );

        add( new Label( "Body:" ) );
        ParameterModel bodyParam
            = new StringParameter( BODY );
        CMSDHTMLEditor body = new CMSDHTMLEditor( bodyParam );
        body.setCols( 40 );
        body.setRows( 5 );
        add( body );

    }

    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        super.validate(e);
    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init( FormSectionEvent fse ) {
        // Do some initialization hook stuff
        FormData data = fse.getFormData();
        Webpage webpage
            = (Webpage) super.initBasicWidgets( fse );

        data.put( DESCRIPTION, webpage.getDescription() );
        data.put( BODY, webpage.getBody() );
	data.put(Webpage.AUTHOR, webpage.getAuthor());
	
	data.put(Webpage.BODY,webpage.getBody());
	
	ArrayList assignedCats = new ArrayList();
	CategoryCollection cc = webpage.getCategoryCollection();
	while(cc.next()) {
	    String catID = cc.getCategory().getID().toString();
	    assignedCats.add(catID);
	}
	data.put(CATEGORIES, assignedCats.toArray());
    }

    /** Form processing hook. Saves Event object. */
    public void process( FormSectionEvent fse ) throws FormProcessException {
        FormData data = fse.getFormData();

        Webpage webpage
            = (Webpage) super.processBasicWidgets( fse );

        // save only if save button was pressed
        if( webpage != null
            && getSaveCancelSection().getSaveButton()
            .isSelected( fse.getPageState() ) ) {

            webpage.setDescription( (String) data.get( DESCRIPTION ) );
            webpage.setBody( (String) data.get( BODY ) );
	    webpage.setAuthor((String)data.get(Webpage.AUTHOR));
	    webpage.setCategories((String[]) data.get(CATEGORIES));

            webpage.save();
        }
    }
}
