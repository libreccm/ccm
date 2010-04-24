package com.arsdigita.london.portal.upgrade;

import org.apache.commons.cli.CommandLine;

import com.arsdigita.london.navigation.portlet.NavigationTreePortlet;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;

public class CreateNavigationDirectoryPortlet extends Program
{
    private static final String PROGRAM_NAME = CreateNavigationDirectoryPortlet.class.getName().substring(
            CreateNavigationDirectoryPortlet.class.getName().lastIndexOf('.') + 1);

    public CreateNavigationDirectoryPortlet()
    {
        super(PROGRAM_NAME, "1.0.0", "");
    }

    public static void main(String[] args)
    {
        new CreateNavigationDirectoryPortlet().run(args);
    }

    protected void doRun(CommandLine cmdLine)
    {
        new Transaction()
        {
            protected void doRun()
            {
                NavigationTreePortlet.loadPortletType();
            }
        }.run();
    }
}
