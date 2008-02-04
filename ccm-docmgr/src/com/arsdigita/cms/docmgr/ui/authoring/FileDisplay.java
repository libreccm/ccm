package com.arsdigita.cms.docmgr.ui.authoring;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.xml.Element;


/**
 */
public class FileDisplay extends SimpleComponent {

    private FileAsset m_asset;
    
    /**
     * Construct a new FileDisplay
     * 
     * @param m The {@link ItemSelectionModel} which will supply
     *   this component with the {@link FileAsset}
     */
    public FileDisplay() {
        super();
    }
      
    public void setFileAsset(FileAsset asset) {
        m_asset = asset;
    }

    public void generateXML(PageState state, Element parent) {
      if ( isVisible(state) && m_asset != null ) {
  
        Element element = new Element("cms:FileDisplay", CMSPage.CMS_XML_NS);
  
        generateFilePropertiesXML(m_asset, state, element);
  
        exportAttributes(element);
        parent.addContent(element);
      }
    }
  
    protected void generateFilePropertiesXML(FileAsset asset,
                                              PageState state,
                                              Element element) {

        element.addAttribute("src", Utilities.getAssetURL(asset));
      
        MimeType mimeType = asset.getMimeType();
        if ( mimeType != null ) {
            element.addAttribute("mime_type", mimeType.getLabel());
        }

        String alt_text = asset.getDescription();
        if (alt_text != null && !alt_text.equals("")) {
            element.addAttribute("alt_text", alt_text);
        }
    }

    public void setName(String name) {
	setAttribute("name", name);
    }
      
}
