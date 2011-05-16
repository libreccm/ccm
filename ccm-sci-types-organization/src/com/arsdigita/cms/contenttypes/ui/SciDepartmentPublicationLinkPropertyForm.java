package com.arsdigita.cms.contenttypes.ui;


import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.SciDepartmentPublicationLink;
import com.arsdigita.cms.contentassets.ui.RelatedLinkPropertyForm;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.util.Assert;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentPublicationLinkPropertyForm extends RelatedLinkPropertyForm {
    
    public SciDepartmentPublicationLinkPropertyForm(ItemSelectionModel itemModel,
                                                    LinkSelectionModel linkModel,
                                                    String linkListName) {
        this(itemModel, linkModel, linkListName, null);
    }
    
    public SciDepartmentPublicationLinkPropertyForm(ItemSelectionModel itemModel,
                                                    LinkSelectionModel linkModel,
                                                    String linkListName,
                                                    ContentType contentType) {
        super(itemModel, linkModel, linkListName, contentType);
    }
    
    @Override
    protected Link createLink(PageState state) {
        ContentItem item = getContentItem(state);
        Assert.exists(item, ContentItem.class);
        SciDepartmentPublicationLink link = new SciDepartmentPublicationLink();
        
        link.setLinkOwner(item);
        
        return link;
    }
    
}
