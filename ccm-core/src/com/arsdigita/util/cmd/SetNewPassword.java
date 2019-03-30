/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.util.cmd;

import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.UserCollection;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class SetNewPassword extends Program{

    public SetNewPassword() {
        super("SetNewPasswort", "1.0.0", "SetNewPassword screenname newpassword");
    }
    
    @Override
    protected void doRun(CommandLine cmdLine) {
        final String[] args = cmdLine.getArgs();
        
        if (args.length != 2) {
            System.out.println("Usage: SetNewPassword screenname newpassword");
            return;
        }
        
        final UserCollection users = User.retrieveAll();
        users.addEqualsFilter("screenName", args[0]);
        if (users.isEmpty()) {
            System.out.printf("User '%s' not found.\n", args[0]);
            return;
        }
        
        users.next();
        final User user = users.getUser();
        users.close();
        
        final UserAuthentication auth = UserAuthentication.retrieveForUser(user);
        auth.setPassword(args[1]);
        auth.save();
        
        System.out.println("Set new password");
        
    }
    
    public static void main(final String[] args) {
        new SetNewPassword().run(args);
    }
    
}
