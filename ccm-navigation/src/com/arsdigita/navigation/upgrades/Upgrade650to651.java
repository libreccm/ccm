/*
 * Created on 30-Sep-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.navigation.upgrades;


import org.apache.commons.cli.CommandLine;

import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.web.Application;
import com.arsdigita.util.cmd.Program;


/**
 * create and grant permission to group who can then add quick
 * links under any category
 *
 * @author chris.gilbert@westsussex.gov.uk
 */
public class Upgrade650to651 extends Program {


    public Upgrade650to651() {
        super("Upgrade 6.5.0 -> 6.5.1", "1.0.0", "");


    }

    public void doRun(CommandLine cmdLine) {
        System.out.println("starting doRun");


        Transaction txn = new Transaction() {
            public void doRun() {
                Group editors = new Group();
                editors.setName("Quick Links Editors");
                editors.save();
                Application navigation = Application.retrieveApplicationForPath("/navigation/");
                PermissionDescriptor edit = new PermissionDescriptor(PrivilegeDescriptor.EDIT, navigation, editors);
                PermissionService.grantPermission(edit);


            }
        };

        try {
            txn.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        new Upgrade650to651().run(args);
    }
    /* (non-Javadoc)
     * @see com.arsdigita.london.util.Program#doRun(org.apache.commons.cli.CommandLine)
     */

}
