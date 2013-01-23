/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.ui.sitemap;


import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.PackageTypeCollection;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.SiteNodeCollection;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.ui.util.GlobalizationUtil;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * The DisplayActions class does the following: It listens for
 * change events in the
 * Tree selection model, and when it detects one, it draws two box panels;
 * one above the other. The Top box panel is for SiteNode info and cfg, and the
 * bottom is for package instance cfg.
 *
 * Every time a selection event is heard, this class:
 * <ul>
 * <li>1) Gets the ID of the node
 * <li>2) creates a site node from the ID
 * <li>3) Extracts the Name of the node
 * <li>4) Builds a title for the top box panel with a call to this.makeTitle()
 * <li>5) Determines if there is a package instance mounted on the node.<br />
 *             If there is, the title for the bottom is set with a call to makelowertitle.
 * <li>6) sets visible the appropriate action links for the node, like this:
 *  <ul>
 *   <li>a) If Node has no instance, and is a leaf node, Offer "AddSubnode",
 *      "SetPermissions", and "RemoveNode" action options in the Top panel.
 *      "MountPackageInstance" is offered in the lower panel.
 *   <li>b) If node has no instance, but it has children, then offer "AddSubnode",
 *      and "SetPermissions" in the Top panel, and "MountPackageInstance"
 *      in the Lower Panel.
 *   <li>c) If Node has an instance, offer "SetPermissions" and "AddSubnode"
 *      in top panel, and "unmountInstance" in lower panel;
 *   </ul>
 * </ul>
 *
 *
 *
 * @author Jim Parsons
 **/

public class DisplayActions extends BoxPanel
    implements ActionListener, ChangeListener {

    public static final String versionId = "$Id: DisplayActions.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";


    private static final Logger s_log =
        Logger.getLogger(DisplayActions.class);


    private final SingleSelectionModel m_sitenodeselectionmodel;

    //First, the vars for the two panels
    BoxPanel m_nodeMaintenancePanel;
    BoxPanel m_pkgMaintenancePanel;

    //nodeMaintenancePanel links get their own panel...
    BoxPanel m_linkcontainer;

    //Now instances of all of the links, components, and forms necessary
    private Label m_nodetitle;
    private Label m_pkgtitle;
    private String m_nodetitleString;
    private String m_pkgtitleString;

    private ToggleLink m_addSubNodeLink;
    private ToggleLink m_setPermissionsLink;
    private ToggleLink m_removeNodeLink;
    private ToggleLink m_unmountPackageLink;
    private ToggleLink m_mountPackageLink;
    private RadioGroup m_grp;

    //Four forms are needed for this class. They are:
    //1) AddSubnode form, with textfield for name
    //2) removeNode form, with confirmation/cancel
    //3) unmountInstance form, with confirmation/cancel
    //4) mountInstance form, with selection box
    //
    //These forms and all listeners are included as inner classes...
    AddSubnodeForm m_addSubnodeForm;
    RemoveNodeForm m_removeNodeForm;
    UnmountInstanceForm m_unmountInstanceForm;
    MountInstanceForm m_mountInstanceForm;

    /**
     *
     * Constructor for DisplayActions
     *
     */
    public DisplayActions (SingleSelectionModel m) {
        super(VERTICAL, false);

        m_sitenodeselectionmodel = m;
        m_sitenodeselectionmodel.addChangeListener(this);

        //First, construct the two box panels...
        m_nodeMaintenancePanel = new BoxPanel(VERTICAL,true);
        m_pkgMaintenancePanel = new BoxPanel(VERTICAL,true);



        //Now add the title labels to the above panels
        m_nodetitle = new Label(GlobalizationUtil.globalize("ui.sitemap.site_node"));
        m_nodetitle.addPrintListener( new PrintListener()
            {
                public void prepare(PrintEvent e)
                {
                    Label t = (Label)e.getTarget();
                    t.setLabel("Site Node: " + m_nodetitleString);
                }
            });
        m_pkgtitle = new Label(GlobalizationUtil.globalize("ui.sitemap.mounted_instance"));
        m_pkgtitle.addPrintListener( new PrintListener()
            {
                public void prepare(PrintEvent e)
                {
                    Label t = (Label)e.getTarget();
                    t.setLabel("Mounted Instance: " + m_pkgtitleString);
                }
            });

        //Now create one more panel to hold the SiteNode action links
        m_linkcontainer = new BoxPanel(HORIZONTAL,false);

        m_nodeMaintenancePanel.add(m_nodetitle);
        m_nodeMaintenancePanel.add(m_linkcontainer);
        m_pkgMaintenancePanel.add(m_pkgtitle);

        //instance action links and add them to m_linkcontainer
        m_addSubNodeLink = new ToggleLink("[Add New Subnode]");
        m_setPermissionsLink = new ToggleLink("[Set Permissions]");
        m_removeNodeLink = new ToggleLink("[Remove This Node]");
        m_unmountPackageLink = new ToggleLink("[Unmount This Package Instance]");
        m_mountPackageLink = new ToggleLink("[Mount a new Package Instance]");


        //This must be constructed before the mountPackageForm
        m_grp = new RadioGroup("instancegroup");
        m_grp.setClassAttr("vertical");
        populateRadioGroup(m_grp);

        //Add link listeners here...
        m_addSubNodeLink.addActionListener(new addSubnodeListener());
        m_removeNodeLink.addActionListener(new removeNodeListener());
        m_unmountPackageLink.addActionListener(new unmountPackageListener());
        m_mountPackageLink.addActionListener(new mountPackageListener());

        //Set up Forms and add to proper containers...
        m_addSubnodeForm = new AddSubnodeForm();
        m_removeNodeForm = new RemoveNodeForm();
        m_unmountInstanceForm = new UnmountInstanceForm();
        m_mountInstanceForm = new MountInstanceForm();



        m_nodeMaintenancePanel.add(m_addSubnodeForm);
        m_nodeMaintenancePanel.add(m_removeNodeForm);
        m_pkgMaintenancePanel.add(m_unmountInstanceForm);
        m_pkgMaintenancePanel.add(m_mountInstanceForm);
        m_pkgMaintenancePanel.add(m_unmountPackageLink);
        m_pkgMaintenancePanel.add(m_mountPackageLink);


        m_linkcontainer.add(m_addSubNodeLink);
        m_linkcontainer.add(m_setPermissionsLink);
        m_linkcontainer.add(m_removeNodeLink);


        add(m_nodeMaintenancePanel);
        add(m_pkgMaintenancePanel);


    }

    // FIXME: what is the point of this?  -- 2002-11-26
    public void actionPerformed(ActionEvent e) {
        s_log.debug("In actionPerformed listener method.");
    }

    /**
     * The computeDefaultState method.
     * This method:
     * 1) gets key of selected tree node
     * 2) generates SiteNode from the key
     * 3) Determines if the node has any children
     * 4) Determines if an instance is mounted on the node
     */

    public NodeState computeDefaultState(PageState p) {
        NodeState n;
        BigDecimal bd =
            new BigDecimal((String) m_sitenodeselectionmodel.getSelectedKey(p));

        try {
            SiteNode sn;
            sn = new SiteNode(bd);

            m_nodetitleString = "Site Node: " + sn.getName();

            //Now we check for children and instances
            boolean hasChildren = false;
            boolean hasInstance = false;
            SiteNodeCollection snc = null;
            snc = sn.getChildren();
            //if(snc != null)
            if ( !snc.isEmpty() ) {
                hasChildren = true;
            }
            snc.close();

            PackageInstance pi = null;
            pi = sn.getPackageInstance();
            if(pi != null) {
                hasInstance = true;
                m_pkgtitleString = "Package Instance: " + pi.getName();
            } else {
                m_pkgtitleString = "Package Instance: None Mounted";
            }

            //Now we know what type of node we are dealing with...
            //All we need to do is set the proper components visible
            n = new NodeState(p, hasChildren, hasInstance);
            return n;
        } catch(com.arsdigita.domain.DataObjectNotFoundException ed) {
            s_log.warn("Problem in ComputeDefaultState");
        }

        return null;

    }
    /**
     * stateChanged listens for Tree select events.
     *
     */
    public void stateChanged(ChangeEvent e)
    {
        PageState s = e.getPageState();

        if ( e.getSource() == m_sitenodeselectionmodel )
            {
                //Since clearSelection() calls generate
                //events, we need to make sure there is
                //something to do here...
                if(m_sitenodeselectionmodel.isSelected(s))
                    {
                        NodeState n = computeDefaultState(s);
                        showActions(n);
                    }
            }
    }
    private void resetDefaults(PageState p)
    {
        //set background canvas
        m_addSubNodeLink.setVisible(p, false);
        m_addSubNodeLink.setSelected(p, false);
        m_setPermissionsLink.setVisible(p, false);
        m_setPermissionsLink.setSelected(p, false);
        m_unmountPackageLink.setVisible(p, false);
        m_unmountPackageLink.setSelected(p, false);
        m_mountPackageLink.setVisible(p, false);
        m_mountPackageLink.setSelected(p, false);
        m_removeNodeLink.setVisible(p, false);
        m_removeNodeLink.setSelected(p, false);
        m_mountInstanceForm.setVisible(p, false);
        m_unmountInstanceForm.setVisible(p, false);
        m_addSubnodeForm.setVisible(p, false);
        m_removeNodeForm.setVisible(p, false);

    }
    private void showActions(NodeState n)
    {
        boolean hasChildren = n.getHasChildren();
        boolean hasInstance = n.getHasInstance();
        PageState p = n.getPageState();

        //Lets freshen up the GUI first...
        resetDefaults(p);

        //There is a simple matrix for this method, with 4 conditions:
        if((hasChildren == false) && (hasInstance == false))
            {
                m_addSubNodeLink.setVisible(p, true);
                m_setPermissionsLink.setVisible(p, true);
                m_removeNodeLink.setVisible(p, true);
                m_mountPackageLink.setVisible(p, true);
            }
        else if((hasChildren == true) && (hasInstance == false))
            {
                m_addSubNodeLink.setVisible(p, true);
                m_setPermissionsLink.setVisible(p, true);
                m_mountPackageLink.setVisible(p, true);
            }
        else if((hasChildren == false) && (hasInstance == true))
            {
                m_addSubNodeLink.setVisible(p, true);
                m_setPermissionsLink.setVisible(p, true);
                m_unmountPackageLink.setVisible(p, true);
            }
        else //((hasChildren == true) && (hasInstance == true))
            {
                m_addSubNodeLink.setVisible(p, true);
                m_setPermissionsLink.setVisible(p, true);
                m_unmountPackageLink.setVisible(p, true);
            }

    }

    private void populateRadioGroup(RadioGroup grp) {
        grp.clearOptions();
        PackageTypeCollection collection = PackageType.retrieveAll();
        while(collection.next()) {
            grp.addOption( new Option(collection.getPackageType().getKey(),
                                      collection.getPackageType().getPrettyName()));
        }
    }
    ////////////////////////////////////
    ///////Inner Classes////////////////

    //This class is a utility class for passing state info around...
    public class NodeState
    {
        public boolean hasChildren = false;
        public boolean hasInstance = false;
        public PageState ps;

        public NodeState(PageState s, boolean children, boolean instance)
        {
            ps = s;
            hasChildren = children;
            hasInstance = instance;
        }

        public void setHasChildren(boolean val)
        {
            hasChildren = val;
        }
        public void setHasInstance(boolean val)
        {
            hasInstance = val;
        }
        public void setPageState(PageState s)
        {
            ps = s;
        }
        public boolean getHasChildren()
        {
            return hasChildren;
        }
        public boolean getHasInstance()
        {
            return hasInstance;
        }
        public PageState getPageState()
        {
            return ps;
        }
    }
    public class AddSubnodeForm extends Form implements FormProcessListener
    {
        private TextField nodeName;
        private Label instruction;
        private Submit button;

        public AddSubnodeForm()
        {
            super("addsubnodeform");
            instruction = new Label(GlobalizationUtil.globalize("ui.sitemap.enter_new_node_name_in_text_field"));
            nodeName = new TextField("nodeName");
            nodeName.setDefaultValue("New Node");
            nodeName.addValidationListener(new NotNullValidationListener("Choose a name for the new site node"));
            button = new Submit("Create Subnode");
            button.setButtonLabel("Create Subnode");
            add(instruction);
            add(nodeName);
            add(button);
            addProcessListener(this);
        }

        public void process(FormSectionEvent e)
        {
            SiteNode sn;
            PageState s = e.getPageState();
            //get the ID of the parent node, and create a site node
            BigDecimal bd =
                new BigDecimal((String) m_sitenodeselectionmodel.getSelectedKey(s));
            try
                {
                    sn = new SiteNode(bd);
                    SiteNode newnode = SiteNode.createSiteNode(nodeName.getValue(s).toString(),sn);
                    newnode.save();

                    //Now rebuild tree here...
                }catch(com.arsdigita.domain.DataObjectNotFoundException ed)
                    {
                        s_log.warn("Problem in AddSubnodeForm");
                    }
            NodeState n = computeDefaultState(s);
            showActions(n);
        }

    }

    public class RemoveNodeForm extends Form implements FormProcessListener
    {
        private Label instruction;
        private Submit button;

        public RemoveNodeForm()
        {
            super("removenodeform");
            instruction = new Label(GlobalizationUtil.globalize("ui.sitemap.are_you_sure_you_want_to_remove_this_node"));
            button = new Submit("Remove Subnode");
            button.setButtonLabel("Remove Subnode");
            add(instruction);
            add(button);
            addProcessListener(this);
        }

        public void process(FormSectionEvent e)
        {
            SiteNode sn;
            PageState s = e.getPageState();
            //get the ID of the parent node, and create a site node
            BigDecimal bd =
                new BigDecimal((String) m_sitenodeselectionmodel.getSelectedKey(s));
            try
                {
                    sn = new SiteNode(bd);

                    try
                        {
                            sn.delete();
                        } catch(PersistenceException pe)
                            {
                                s_log.warn("Something in the DisplayAction code has gone horribly wrong...");
                            }
                }catch(com.arsdigita.domain.DataObjectNotFoundException ed)
                    {
                        s_log.warn("Problem in RemoveInstanceForm");
                    }
            m_sitenodeselectionmodel.clearSelection(s);
            resetDefaults(s);
        }
    }

    public class UnmountInstanceForm extends Form implements FormProcessListener
    {
        private Label instruction;
        private Submit button;

        public UnmountInstanceForm()
        {
            super("unmountinstanceform");
            instruction = new Label(GlobalizationUtil.globalize("ui.sitemap.are_you_sure_you_want_to_umount_this_instance"));
            button = new Submit("Unmount");
            button.setButtonLabel("Unmount");
            add(instruction);
            add(button);
            addProcessListener(this);
        }

        public void process(FormSectionEvent e)
        {
            SiteNode sn;
            PageState s = e.getPageState();
            //get the ID of the parent node, and create a site node
            BigDecimal bd =
                new BigDecimal((String) m_sitenodeselectionmodel.getSelectedKey(s));
            try
                {
                    sn = new SiteNode(bd);
                    //XXX Ugly hack to prevent SiteNode from being thrown away...
                    sn.getParent();
                    sn.unMountPackage();
                    sn.save();
                }catch(com.arsdigita.domain.DataObjectNotFoundException ed)
                    {
                        s_log.warn("Problem in UnmountInstanceForm");
                    }
            NodeState n = computeDefaultState(s);
            showActions(n);
        }
    }

    public class MountInstanceForm extends Form implements FormProcessListener
    {
        private Label instruction;
        private Submit button;
        private TextField instanceName;

        public MountInstanceForm()
        {
            //Set up UI components...
            super("mountinstanveform");
            instruction = new Label(GlobalizationUtil.globalize("ui.sitemap.enter_name_for_new_package_instance_in_text_field_then_select_a_package_to_mount_from_list"));
            instanceName = new TextField("instanceName");
            instanceName.setDefaultValue("New Instance");
            instanceName.addValidationListener(new NotNullValidationListener("Choose a name for the new package instance"));
            button = new Submit("Mount Package");
            button.setButtonLabel("Mount Package");
            add(instanceName);
            add(instruction);
            add(button);
            m_grp.addValidationListener(new NotNullValidationListener("Selecting an Instance to mount"));
            add(m_grp);
            addProcessListener(this);
        }

        public void process(FormSectionEvent e)
        {
            SiteNode sn;
            String keyresult;
            PageState s = e.getPageState();
            BigDecimal bd =
                new BigDecimal((String) m_sitenodeselectionmodel.getSelectedKey(s));
            try
                {
                    sn = new SiteNode(bd);

                    //XXX Ugly hack to prevent SiteNode from being thrown away...
                    sn.getParent();

                    keyresult = (String)m_grp.getValue(s);
                    PackageType pt = PackageType.findByKey(keyresult);

                    PackageInstance pi =
                        pt.createInstance(instanceName.getValue(s).toString());
                    sn.mountPackage(pi);

                    sn.save();
                }catch(com.arsdigita.domain.DataObjectNotFoundException ed)
                    {
                        s_log.warn("Problem in MountInstanceForm");
                    }
            NodeState n = computeDefaultState(s);
            showActions(n);
        }

    }



    /////////////////////////////////////
    //The classes below are impl's of the ActionListener interface
    //and are for the ToggleLinks...

    class addSubnodeListener implements com.arsdigita.bebop.event.ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            //Here we have to turn off unnecessary links,
            //and turn on the appropriate form.
            PageState p = event.getPageState();
            //ResetDefaults(p);
            m_addSubnodeForm.setVisible(p, true);
            m_setPermissionsLink.setVisible(p, false);
            m_removeNodeLink.setVisible(p, false);
            m_unmountPackageLink.setVisible(p,false);
            m_unmountInstanceForm.setVisible(p,false);
        }
    }

    class removeNodeListener implements com.arsdigita.bebop.event.ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            //Here we have to turn off unnecessary links,
            //and turn on the appropriate form.
            PageState p = event.getPageState();
            //ResetDefaults(p);
            m_removeNodeForm.setVisible(p, true);
            m_setPermissionsLink.setVisible(p, false);
            m_addSubNodeLink.setVisible(p, false);
            m_unmountPackageLink.setVisible(p,false);
            m_unmountInstanceForm.setVisible(p,false);
        }
    }

    class mountPackageListener implements com.arsdigita.bebop.event.ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            //Here we have to turn off unnecessary links,
            //and turn on the appropriate form.
            PageState p = event.getPageState();
            m_removeNodeForm.setVisible(p, false);
            m_setPermissionsLink.setVisible(p, false);
            m_addSubNodeLink.setVisible(p, false);
            m_removeNodeLink.setVisible(p, false);
            m_unmountPackageLink.setVisible(p,false);
            m_unmountInstanceForm.setVisible(p,false);
            m_mountInstanceForm.setVisible(p,true);
        }
    }

    class unmountPackageListener implements com.arsdigita.bebop.event.ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            //Here we have to turn off unnecessary links,
            //and turn on the appropriate form.
            PageState p = event.getPageState();
            //ResetDefaults(p);
            m_removeNodeForm.setVisible(p, false);
            m_setPermissionsLink.setVisible(p, false);
            m_addSubNodeLink.setVisible(p, false);
            m_removeNodeLink.setVisible(p, false);
            m_unmountPackageLink.setVisible(p,false);
            m_unmountInstanceForm.setVisible(p,true);
            m_mountInstanceForm.setVisible(p,false);
        }
    }

}
