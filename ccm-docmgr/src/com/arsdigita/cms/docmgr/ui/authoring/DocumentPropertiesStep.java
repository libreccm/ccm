package com.arsdigita.cms.docmgr.ui.authoring;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.web.Web;


/**
 * Authoring step to edit the simple attributes of the Document content
 * type (and its subclasses). The attributes edited are 'name', 'title',
 * 'document date', 'location', 'lead', 'main contributor', 'document type', 'map link', and 'cost'. This authoring step replaces
 * the <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 */
public class DocumentPropertiesStep
    extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    public DocumentPropertiesStep( ItemSelectionModel itemModel,
                                  AuthoringKitWizard parent ) {
        super( itemModel, parent );

        BasicPageForm editSheet;

        editSheet = new DocumentPropertyForm( itemModel );
        add( EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel),
             editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent( getDocumentPropertySheet( itemModel ) );
    }

    /**
     * Returns a component that displays the properties of the
     * Document specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getDocumentPropertySheet( ItemSelectionModel
                                                     itemModel ) {
        //ItemPropertySheet sheet = new ItemPropertySheet( itemModel );
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel,
                                                                false);
        sheet.add( "Name:", Document.NAME );
        sheet.add( "Title:", Document.TITLE );
        sheet.add( "Description:", Document.DESCRIPTION );
        sheet.add("File:", Document.FILE,
                  new FileFormatter());
 
        return sheet;
    }

    public static class FileFormatter
        implements DomainObjectPropertySheet.AttributeFormatter {

        private String m_default;

        public FileFormatter() {
            this("<i>no file</i>");
        }

        public FileFormatter(String def) {
            m_default = def;
        }

        public String getDefaultString() {
            return m_default;
        }

        public String format (DomainObject obj,
                              String attribute,
                              PageState state) {

            DataObject fileDO = (DataObject) ((ContentItem)obj).get(attribute);
            if (fileDO == null) {
                return getDefaultString();
            } else {
                FileAsset file = new FileAsset(fileDO);
                Document doc = (Document) file.getParent();

                String alt = "";
                String altStr = "";
                String altVerb = "unknown";
                if (doc != null) {
                    alt = doc.getDescription();
                    altStr = " alt=\"" + alt + "\" ";
                    altVerb = doc.getTitle();
                }

                StringBuffer sbuf = new StringBuffer();
                sbuf.append("<a href=\"");
                sbuf.append(Web.getConfig().getDispatcherServletPath());
                sbuf.append(Utilities.getAssetURL(file));
                sbuf.append("\" ");
                sbuf.append(altStr);
                sbuf.append(">");
                sbuf.append(altVerb);
                sbuf.append("</a>");

                if (file.getMimeType() != null) {
                    //sbuf.append("<br />Mime Type: ");                                                                                                     sbuf.append(" (");
                    sbuf.append(" (");
                    sbuf.append(file.getMimeType().getLabel());
                    sbuf.append(")");
                }

                return sbuf.toString();
            }
        }
    }

}
