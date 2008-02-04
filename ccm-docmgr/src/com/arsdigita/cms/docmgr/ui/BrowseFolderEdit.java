package com.arsdigita.cms.docmgr.ui;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.docmgr.DocFolder;

/**
 * @author Peter Kopunec
 */
public class BrowseFolderEdit extends Form implements FormInitListener, FormValidationListener, FormProcessListener, DMConstants {
	
	private static final Logger s_log = Logger.getLogger(BrowseFolderEdit.class);
	
	private StringParameter m_FolderName;
	private StringParameter m_FolderDesc;
	private BrowsePane m_parent;
	private BigDecimalParameter m_folderID;
	private Submit m_submit;
	private TextField m_FolderNameTF;
	private TextArea m_FolderDescTA;
	
	public BrowseFolderEdit(BrowsePane parent, BigDecimalParameter folderID) {
		super("EditFolderForm", new ColumnPanel(2));
		
		m_parent = parent;
		m_folderID = folderID;
		
		add(FOLDER_NAME_LABEL);
		m_FolderName = new StringParameter(FOLDER_NAME);
//		m_FolderName.addParameterListener(new StringLengthValidationListener(200));
		m_FolderNameTF = new TextField(m_FolderName);
//		m_FolderNameTF.addValidationListener(new NotEmptyValidationListener());
		add(m_FolderNameTF);
		
		add(FOLDER_DESCRIPTION_LABEL);
		m_FolderDesc = new StringParameter(FOLDER_DESCRIPTION);
//		m_FolderDesc.addParameterListener(new StringLengthValidationListener(4000));
		m_FolderDescTA = new TextArea(m_FolderDesc);
		m_FolderDescTA.setRows(10);
		m_FolderDescTA.setCols(40);
		add(m_FolderDescTA);
		
		SimpleContainer sc = new SimpleContainer();
		m_submit = new Submit("submit");
		m_submit.setButtonLabel("Save");
		sc.add(m_submit);
		Submit cancel = new Submit(CANCEL);
		sc.add(cancel);
		
		add(new Label()); // spacer
		add(sc, ColumnPanel.LEFT);
		
		addInitListener(this);
		addValidationListener(this);
		addProcessListener(this);
	}
	
	public void init(FormSectionEvent e) {
		s_log.debug("init");
		PageState state = e.getPageState();
		BigDecimal folderID = (BigDecimal) state.getValue(m_folderID);
		DocFolder f = new DocFolder(folderID);
		if (s_log.isDebugEnabled()) {
			s_log.debug("init widgets: name=" + f.getTitle() + "; description=" + f.getDescription());
		}
		m_FolderNameTF.setValue(state, f.getTitle());
		m_FolderDescTA.setValue(state, f.getDescription());
//		state.setValue(m_FolderName, f.getName());
//		state.setValue(m_FolderDesc, f.getDescription());
	}

    /**
     * Validate the folder.
     */
    public void validate(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();
        if (m_submit.isSelected(state)) {
        	FormData data = event.getFormData();
            String fname = (String) data.get(FOLDER_NAME);
            if (fname == null || fname.trim().length() == 0) {
                data.addError(FOLDER_NAME, "This parameter is required");
            }
            if (fname != null && fname.length() > 200) {
            	data.addError(FOLDER_NAME, "This parameter is too long. It must be fewer than 200 characters.");
            }
            
            String fDesc = (String) data.get(FOLDER_DESCRIPTION);
            if (fDesc != null && fDesc.length() > 4000) {
            	data.addError(FOLDER_DESCRIPTION, "This parameter is too long. It must be fewer than 4000 characters.");
            }
        }
	}
	
	public void process(FormSectionEvent e) throws FormProcessException {
		PageState state = e.getPageState();
		if (m_submit.isSelected(state)) {
			FormData data = e.getFormData();
	        String fname = (String) data.get(FOLDER_NAME);
	        String fdesc = (String) data.get(FOLDER_DESCRIPTION);
			BigDecimal folderID = (BigDecimal) state.getValue(m_folderID);
			DocFolder f = new DocFolder(folderID);
			f.setTitle(fname);
			f.setDescription(fdesc);
			f.save();
		}
		m_parent.displayFolderContentPanel(state);
	}
}
