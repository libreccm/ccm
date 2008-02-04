package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.jsp.DefineComponent;
import com.arsdigita.bebop.parameters.ParameterModel;

import javax.servlet.jsp.JspException;

import java.math.BigDecimal;

/**
 * A tag handler class for the view asset component
 *
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen<a/>
 **/
public class DefineAssetView extends DefineComponent {

    private FreeformAssetView m_assetView;
    private BigDecimal m_itemId;
    private String m_assetId;
    private String m_rank;
    private String m_mimeType;

    public int doStartTag() throws JspException {
        m_assetView = new FreeformAssetView();
        m_assetView.setIdAttr(getName());

        AssetSelectionModel asm = m_assetView.getAssetSelectionModel();

        if (m_itemId != null) {
            ParameterModel pm = asm.getItemModel().getStateParameter();
            pm.setDefaultValue(m_itemId);
            pm.setDefaultOverridesNull(true);
        }

        if (m_assetId != null) {
            ParameterModel pm = asm.getAssetModel().getStateParameter();
            pm.setDefaultValue(m_assetId);
            pm.setDefaultOverridesNull(true);
        }

        if (m_rank != null) {
            ParameterModel pm = asm.getRankModel().getStateParameter();
            pm.setDefaultValue(m_rank);
            pm.setDefaultOverridesNull(true);
        }

        if (m_mimeType != null) {
            ParameterModel pm = asm.getMimeTypeModel().getStateParameter();
            pm.setDefaultValue(m_mimeType);
            pm.setDefaultOverridesNull(true);
        }

        return super.doStartTag();
    }

    protected Component getComponent() {
        return m_assetView;
    }
    
    public final void setItemId(String s) {
        m_itemId = new BigDecimal(s);
    }

    public final void setAssetId(String s) {
        m_assetId = s;
    }

    public final void setRank(String s) {
        m_rank = s;
    }

    public final void setMimeType(String s) {
        m_mimeType = s;
    }

}
