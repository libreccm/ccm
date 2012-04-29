package com.arsdigita.london.util.cmd;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.util.cmd.Program;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class MoveFolder extends Program {

    public MoveFolder() {
        super("MoveFolder",
              "1.0.0",
              "");
    }

    public static void main(final String[] args) {
        new MoveFolder().run(args);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {

        final String[] args = cmdLine.getArgs();

        if (args.length != 2) {
            System.err.println("Usage:");
            System.err.println("MoveFolder sourcecontentsection/source/folder "
                               + "targetcontentsection/target/");
            System.exit(-1);
        }

        System.out.printf("Moving folder '%s' to '%s'...\n",
                          args[0],
                          args[1]);

        final String[] sourcePathTokens = args[0].split("/");
        final String[] targetPathTokens = args[1].split("/");

        final ContentSection sourceContentSection =
                             getContentSection(sourcePathTokens[0]);
        final ContentSection targetContentSection =
                             getContentSection(targetPathTokens[0]);

        if (sourceContentSection == null) {
            System.err.printf("Failed to find source content section '%s'.",
                              sourcePathTokens[0]);
        }

        if (targetContentSection == null) {
            System.err.printf("Failed to find target content section '%s'.",
                              targetPathTokens[0]);
        }

        final Folder sourceFolder = getFolder(Arrays.copyOfRange(
                sourcePathTokens,
                1,
                sourcePathTokens.length),
                                              sourceContentSection);
        final Folder targetFolder = getFolder(Arrays.copyOfRange(
                targetPathTokens,
                1,
                targetPathTokens.length),
                                              targetContentSection);                

        System.out.println("Depublishing all items in folder...");
        depublish(sourceFolder);

        System.out.println("Setting content section for folder to remove...");
        sourceFolder.setContentSection(targetContentSection);
        sourceFolder.save();
        targetContentSection.save();
        System.out.println("Setting parent to target folder...");
        sourceFolder.setParent(targetFolder);
        sourceFolder.save();
        targetFolder.save();

        System.out.println("Setting content sections on items...");
        setContentSection(sourceFolder, targetContentSection);

        System.out.println("Done.");

    }

    private ContentSection getContentSection(final String name) {
        final ContentSectionCollection sections =
                                       ContentSection.getAllSections();

        sections.addFilter(String.format("title = '%s'", name));

        if (sections.isEmpty()) {
            return null;
        } else {
            sections.next();
            final ContentSection result = sections.getContentSection();
            sections.close();
            return result;
        }
    }

    private Folder getFolder(final String[] path,
                             final ContentSection fromSection) {
        final Folder root = fromSection.getRootFolder();
        Folder folder = root;
        for (int i = 0; i < path.length; i++) {
            folder = getSubFolder(root, path[i]);

            if (folder == null) {
                break;
            }
        }

        return folder;
    }

    private Folder getSubFolder(final Folder parent,
                                final String name) {
        final Folder.ItemCollection items = parent.getItems();
        items.addFolderFilter(true);
        items.addFilter(String.format("name = '%s'", name));

        if (items.isEmpty()) {
            return null;
        } else {
            items.next();
            final Folder folder = (Folder) items.getContentItem();
            items.close();
            return folder;
        }
    }

    private void depublish(final Folder folder) {
        final Folder.ItemCollection items = folder.getItems();

        ContentItem item;
        while (items.next()) {
            item = items.getContentItem();
            System.out.printf("Depublishing item '%s' (%s)...\n",
                              item.getName(),
                              item.getOID().toString());

            if (item instanceof Folder) {
                depublish((Folder) item);
            } else if(item instanceof ContentBundle) {
                depublish((ContentBundle) item);
            } else {
                item.unpublish();
            }
        }
    }
    
    private void depublish(final ContentBundle bundle) {
        final ItemCollection items = bundle.getInstances();
        
        while(items.next()) {
            items.getContentItem().unpublish();
        }
    }

    private void setContentSection(final Folder folder,
                                   final ContentSection contentSection) {
        final Folder.ItemCollection items = folder.getItems();

        folder.setContentSection(contentSection);
        folder.save();

        ContentItem item;
        while (items.next()) {
            item = items.getContentItem();
            System.out.printf("Setting content section on item '%s' (%s)...\n",
                              item.getName(),
                              item.getOID().toString());

            if (item instanceof Folder) {
                setContentSection((Folder) item, contentSection);
            } else if (item instanceof ContentBundle) {
                setContentSection((ContentBundle) item, contentSection);
            } else {
                item.setContentSection(contentSection);
                item.save();
            }
        }
    }

    private void setContentSection(final ContentBundle bundle,
                                   final ContentSection contentSection) {
        final ItemCollection items = bundle.getInstances();
        
        while(items.next()) {
            items.getContentItem().setContentSection(contentSection);
        }
    }
}
