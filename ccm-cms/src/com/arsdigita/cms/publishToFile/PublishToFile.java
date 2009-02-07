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
package com.arsdigita.cms.publishToFile;

import com.arsdigita.cms.Asset;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionServlet;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateCollection;
import com.arsdigita.cms.TemplateManager;
import com.arsdigita.cms.TemplateManagerFactory;
import com.arsdigita.cms.dispatcher.TemplateResolver;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.servlet.HttpHost;
import com.arsdigita.util.servlet.HttpResourceLocator;
import com.arsdigita.web.Host;
import com.arsdigita.web.Web;
import com.arsdigita.web.WebConfig;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Methods for writing content (as static pages) to the file
 * system when an item is published, and removing the files when the
 * item is unpublished.
 *
 * @author Jeff Teeters (teeters@arsdigita.com)
 * @author <a href="mailto:dlutter@redhat.com">David Lutterkort</a>
 * @version $Revision: #32 $ $DateTime: 2004/08/17 23:15:09 $
 */

public class PublishToFile implements PublishToFileListener {

    public static final int DEFAULT_TIMEOUT = 60;

    private static Logger s_log =
        Logger.getLogger(PublishToFile.class);

    // Queue entry timeout
    private static int s_timeout = DEFAULT_TIMEOUT;
    private static Map s_destinations = new HashMap();

    private final PublishedHTMLProvider m_provider;

    /**
     * Default Constructor
     */
    public PublishToFile() {
        m_provider = new HttpHTMLProvider(s_timeout);
    }

    public PublishToFile(PublishedHTMLProvider provider) {
        m_provider = provider;
    }

    /**
     * Set the timeout for requests fetching content
     *
     * @param timeout the timeout in seconds
     */
    public static void setRequestTimeout(int timeout) {
        s_timeout = timeout;
    }


    /**
     * Registers a destination stub for an object type
     * @param objectType the type to register the stub for
     * @param dest the destination stub to register
     */
    static void addDestination(String objectType,
                               DestinationStub dest) {
        addDestination(MetadataRoot.getMetadataRoot()
                       .getObjectType(objectType),
                       dest);
    }

    /**
     * Registers a destination stub for an object type
     * @param type the type to register the stub for
     * @param dest the destination stub to register
     */
    static void addDestination(ObjectType type,
                               DestinationStub dest) {
        Assert.exists(type, ObjectType.class);
        Assert.exists(dest, DestinationStub.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding destination " + dest + " for " + type.getQualifiedName());
        }

        s_destinations.put(type,
                           dest);
    }

    /**
     * Gets the destination stub for an object type.
     * If no destination stub is available, gets the destination
     * stub for the parent object type. Recursively. If
     * no destination stub can be matched at all, returns null
     * @param objectType the object type to find a destination stub for
     * @return the best matching destination stub, or null
     */
    public static DestinationStub getDestination(String objectType) {
        return getDestination(MetadataRoot.getMetadataRoot()
                              .getObjectType(objectType));
    }

    /**
     * Gets the destination stub for an object type.
     * If no destination stub is available, gets the destination
     * stub for the parent object type. Recursively. If
     * no destination stub can be matched at all, returns null
     * @param type the object type to find a destination stub for
     * @return the best matching destination stub, or null
     */
    public static DestinationStub getDestination(ObjectType type) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Searching for destination for " + type.getQualifiedName());
        }
        ObjectType current = type;
        while (current != null) {
            if (s_destinations.containsKey(current)) {
                DestinationStub dest = (DestinationStub)s_destinations.get(current);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Got destination " + dest);
                }
                return dest;
            }

            current = current.getSupertype();
            if (s_log.isDebugEnabled()) {
                if (current != null) {
                    s_log.debug("Trying parent instead " + current.getQualifiedName());
                } else {
                    s_log.debug("Trying parent which is null");
                }
            }
        }
        s_log.debug("No destination found");
        return null;
    }

    /**
     * Gets the source URL for retrieving the item with the
     * specified path.
     * @param path the item path, relative to the servlet root (ie, no /ccm)
     * @return the source URL for fetching the item
     */
    public static URL getSource(final String path) {
        if (Assert.isEnabled()) {
            Assert.exists(path, String.class);
            Assert.truth(path.startsWith("/"), "Path starts with '/'");
        }

        final WebConfig config = Web.getConfig();
        final HttpHost host = Web.getConfig().getHost();

        // def scheme, no query str

        final HttpResourceLocator hrl = new HttpResourceLocator
            (host,
             config.getDispatcherContextPath(),
             config.getDispatcherServletPath(),
             path,
             null);

        return hrl.toURL();
    }

    /***
     * Strip the source base from a url.  Example: url_in = "/acs/test"
     * sourceBase = "/acs".  Returned value is: "/test".
     * @param url url that may start with source base.
     * @return input url but with source base removed.
     ***/
    // FIXME: Move this method out of this class [lutter]
    static String stripSourceBase (String url) {
        String sourceBase = getSource("/").toString();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Maybe strip " + sourceBase + " from " + url);
        }
        return (url.startsWith(sourceBase) ?
                url.substring(sourceBase.length()) :
                url);
    }


    /***
     * Called by the queue manager for each task. Subclasses should override
     * the methods {@link #publish publish}, {@link #unpublish unpublish},
     * {@link #republish republish}, and {@link #move move} to be notified of
     * the particular task to be performed.
     */
    public final boolean doTask(QueueEntry qe) throws PublishToFileException {
        if ( qe.isPublishTask() )
            return publish(qe);
        else if ( qe.isUnpublishTask() )
            return unpublish(qe);
        else if ( qe.isRepublishTask() )
            return republish(qe);
        else if ( qe.isMoveTask() )
            return move(qe);
        else {
            throw new PublishToFileException("Unsupported  " + qe + " task passed to the queue");
        }
    }


    /**
     * Called just before processing of one block of queue entries
     * starts. Does nothing.
     */
    public void transactionStart() {
    }

    /**
     * Called just after processing of one block of queue entries
     * has finished. Does nothing.
     */
    public void transactionEnd() {
    }

    /***
     * Process move task (for moving an item or folder to another folder).
     * @param qe QueueEntry for move task.
     ***/
    protected boolean move(QueueEntry qe) {
        ContentItem liveItem = qe.getItem();
        ContentItem dstFolder= Utilities.getContentItemOrNull(new BigDecimal(qe.getDestination()) ) ;
        if ( liveItem == null || dstFolder == null )
            return false; // there is nothing to do

        Utilities.updateContentSection(liveItem, dstFolder.getContentSection());
        return true;
    }

    /***
     * Publish the page in the QueueEntry to the file system.
     * @param qe QueueEntry for item to publish.
     ***/
    protected boolean publish(QueueEntry qe) {
        ContentItem item = qe.getItem();
        if ( item == null ) {
            // can't publish, item no longer exists.  Ignore queue entry
            s_log.warn( "Item to publish no longer exists. ID = " + qe.getItemId() );
            return false;
        }
        Assert.assertTrue( !(item instanceof ContentBundle) );

        if ( item instanceof Template ) {
            Template template = (Template) item;
            ContentSection section = template.getContentSection();
            if (section != null) {
                TemplateResolver resolver = section.getTemplateResolver();
                String fileName = null;
                if (template.getMimeType() != null &&
                    Template.XSL_MIME_TYPE.equals(template.getMimeType().getMimeType())) {
                    fileName = resolver.getTemplateXSLPath(template);
                } else {
                    fileName = resolver.getTemplatePath(template);
                }

                // The 'templateRoot' bit is specified in enterprise.init as the 'destination'
                // for the Template object type, so we must strip it off here, otherwise it
                // gets double appended
                String templateRoot = ContentSection.getConfig().getTemplateRoot();
                Assert.truth(fileName.startsWith(templateRoot), "filename starts with templateRoot");

                PublishedFile f = PublishedFile.loadOrCreate(
                    template,
                    fileName.substring(templateRoot.length()),
                    qe.getHost());
                writeAsset(template, f);
                f.save();
            }
            return true;
        } else if ( item instanceof Asset ) {
            // FIXME: This writes out a global asset, and relies on global assets
            // being subclasses of Asset and that they are directly contained
            // within a folder [lutter]
            Asset asset = (Asset) item;
            ACSObject parent = asset.getParent();
            String fileName;
            if (parent == null) {
                fileName = Utilities.getAssetPath(asset);
            } else if (parent instanceof Folder) {
                fileName = Utilities.getAssetPath(asset, (Folder) parent);
            } else {
                throw new PublishToFileException
                    ("can not independently publish asset: " + asset
                     + " that has parent " + asset.getParent()
                     + " that is not a folder");
            }

            PublishedFile f = PublishedFile.loadOrCreate
                (asset,
                 fileName,
                 qe.getHost());
            writeAsset(asset, f);
            f.save();

            return true;
        } else {
            ContentBundle bundle = (ContentBundle)item.getParent();
            Folder folder = (Folder)bundle.getParent();
            return publishPage(item, folder, qe.getHost());
        }
    }

    /***
     * Republish (unpublish then publish) the page in the QueueEntry to the file
     * system.
     * @param qe QueueEntry for item to publish.
     ***/
    protected boolean republish(QueueEntry qe) {
        return ( unpublish(qe) && publish(qe) );
    }

    /***
     * Unpublish item.  Removes from file system files that were
     * written as part of publishing item.
     * @param qe The QueueEntry of the item to unpublish.
     ***/
    protected boolean unpublish( QueueEntry qe ) {
        BigDecimal itemId = qe.getItemId();
        ContentItem item = qe.getItem();
        PublishedFile.deleteAll(itemId, qe.getHost());
        return true;
    }

    /**
     * Returns an extension given a content type. If the content-type is null
     * assume "html".
     * @param contentType Content-Type returned from the request,
     *        e.g. text/html;charset=ISO-8859-1
     **/
    private static String getFileExtension(String contentType) {
        if ( contentType == null || contentType == "" )
            return ".html";

        // remove any extra information in contentType string, e.g. charset
        int i = contentType.indexOf(';');
        contentType = i > 0 ? contentType.substring(0, i) : contentType;

        MimeType type = MimeType.loadMimeType(contentType);

        if (type == null) {
            s_log.error("Unknown content type in published item: " +
                        contentType + " assuming extension 'html'");
            return ".html";
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("File extension for " + contentType + " is " +
                            type.getFileExtension());
            }
            return "." + type.getFileExtension();
        }
    }


    /***
     * Publish the page that is at url source to the file system and also any
     * streamed assets that it references.
     * @param item ContentItem being published.
     * @param where Location Folder, where item will be published
     ***/
    private boolean publishPage(ContentItem item, Folder where, Host host) {
        String url = getSource(Utilities.getItemURL(item)).toString();

        s_log.info("Publishing page from URL '" + url + "' item = " + item);

        // Read 'public' Template in HTML format and write to FS
        // It can be either 'deault' or specified template
        String publicUrl = url + '?' + ContentSectionServlet.MEDIA_TYPE + '=' +
                           TemplateManager.PUBLIC_CONTEXT;
        if ( !publishPageAtDocRoot(readHTML(publicUrl), where, item, null, host) )
            return false;

        publishOtherTemplates(url, item,where, host);
        return true;
    }

    /**
     * Publishes all non-public templates.
     * @param url - item url
     * @param item - actuall ContentItem to be published
     * @param where - destination folder to be published
     */
    private void publishOtherTemplates(String url, ContentItem item, Folder where, Host host) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("publishAllTemplates url " + url);
            s_log.debug("item is " + item.getName() + " with id " + item.getID());
            s_log.debug("folder where is " + where.getName() + " with id " + where.getID());

        }
        ContentItem draft = item.getWorkingVersion();

        TemplateManager manager = TemplateManagerFactory.getInstance();
        TemplateCollection coll = manager.getUseContexts(draft);
        if (s_log.isDebugEnabled()) {
            s_log.debug("getting template collection for draft item with id " + draft.getID());
            s_log.debug("templateCollection coll has size " + coll.size());
        }

        try {
            while ( coll.next() ) {
                final String context = coll.getUseContext();

                // Already published this as the default
                if ( TemplateManager.PUBLIC_CONTEXT.equals(context) ) {
                    continue;
                }

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Publishing Template " + context);
                }
                publishPageAtDocRoot(readHTML(url + '?' + ContentSectionServlet.MEDIA_TYPE + '=' + context), where, item, context, host);
            }
        } finally {
            coll.close();
        }

    }


    /***
     * Read the content of a page from a url.
     * @param location url to get the page from
     * @return Body (content) and content-type of page at the url.
     ***/
    private RetrievedFile readHTML(String location) {
        return m_provider.fetchHTML(location);
    }



    /***
     * Publish a page at a document root.
     * @param rf - Retrieved File Information
     * @param parent Folder where Item must be published
     * @param item ContentItem, which will be published
     * used when transforming page.
     ***/
    protected boolean publishPageAtDocRoot(RetrievedFile rf,
                                           Folder parent,
                                           ContentItem item, String media,
                                           Host host) {
        final String fileExt = getFileExtension(rf.contentType);
        final String html = rf.body;
        if (s_log.isDebugEnabled()) {
            s_log.debug("content item before getting bundle is " + item.getName() +
                        " with id " + item.getID());

        }

        final ContentBundle bundle = (ContentBundle) item.getParent();
        Assert.exists(bundle, ContentBundle.class);

        String sLangExt = item.getLanguage();
        s_log.debug("item language is " + sLangExt);
        if (sLangExt != null && sLangExt.length() > 0) {
            sLangExt = "." + sLangExt;
        } else {
            sLangExt = "";
        }

        final String path = Utilities.getItemLocation(bundle)
                + Utilities.getMediaIndicator(media)
                + sLangExt + fileExt;

        if (s_log.isDebugEnabled()) {
            s_log.debug("media is " + media);
            s_log.debug
                ("MediaIndicator is " + Utilities.getMediaIndicator(media));
            s_log.debug("item.getName is " + item.getName());
            s_log.debug("item language is " + item.getLanguage());
            s_log.debug("fileExt is " + fileExt);
            s_log.debug("path for publishPage is " + path);
        }

        // Parse html to get tags that need reformatting
        LinkScanner scanner = new LinkScanner(html);
        if (s_log.isDebugEnabled()) {
            s_log.debug(("first time t.size() is " + scanner.size()));
        }

        for (int i=0; i < scanner.size(); i++) {
            final ContentItem target = scanner.getTarget(i);
            final String targetURL = getTargetURL(target);
            if (s_log.isDebugEnabled()) {
                s_log.debug("i is " + i + "; t.getTarget(i) is " + target +
                            "; getTargetURL is " + targetURL);
            }

            scanner.setTargetURL(i, targetURL);
        }

        PublishedFile f = PublishedFile.loadOrCreate(
            item,
            path,
            host);
        try {
            // FIXME: Do we need to specify a charset here ? [lutter]
            Writer out = new OutputStreamWriter(f.getOutputStream());
            scanner.transform(out);
            out.close();
        } catch ( IOException ex ) {
            throw new PublishToFileException("Unable to write item " + item +
                                             " to filesystem.", ex);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("t.size() is " + scanner.size());
        }

        for ( int i = 0; i < scanner.size(); i ++ ) {
            if (scanner.getTarget(i) != null && isLocal(scanner.getTarget(i)) ) {
                Asset asset = (Asset) scanner.getTarget(i);
                String fileName = Utilities.getAssetPath(asset);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("asset filename is: " + fileName);
                }
                PublishedFile ass = PublishedFile.loadOrCreate(
                    asset,
                    fileName,
                    host);
                File af = ass.getFile();
                writeAsset(asset, ass);
                ass.save();
            }
        }

        f.save();
        return true;
    }

    /***
     * publish asset (such as an image) to file system.
     ***/
    private void  writeAsset(Asset asset, PublishedFile f) {
        Assert.assertTrue(ContentItem.LIVE.equals(asset.getVersion()));

        File fsf = f.getFile();
        try {
            asset.writeToFile(fsf);
        } catch ( IOException io ) {
            throw new PublishToFileException("Failed to write asset " + asset
                    + " to " + fsf.getPath(), io);
        }
    }

    /**
     * Return the URL of <code>target</code> on the destination live server.
     *
     * @param target the item whose URL should be returned
     * @return the URL of the item on the live server
     */
    protected String getTargetURL(ContentItem target) {
        String itemPath = null;
        if ( target == null ) {
            return null;
        }

        if ( target instanceof Asset ) {
            itemPath = Utilities.getAssetPath((Asset)target);
        } else {
            itemPath = Utilities.getItemLocation(target);
        }
        Assert.truth(itemPath.startsWith("/"), "item path starts with /");

        DestinationStub stub = PublishToFile.getDestination(target.getSpecificObjectType());
        String stubURL = stub.getURLStub();
        if (stubURL.endsWith("/")) {
            stubURL = stubURL.substring(0, stubURL.length() - 1);
        }

        return stubURL + itemPath;
    }

    /**
     * Determine if <code>asset</code> is local, and should therefore be
     * written to the file system as part of writing the item that contains
     * it. If this method returns false, the asset is not written. Such
     * assets need to be scheduled separately into the queue.
     *
     * @param asset an asset found during processing an item
     * @return <code>true</code> if the asset is local and should be written
     * to the file system
     */
    protected boolean isLocal(ContentItem asset) {
        return ( asset instanceof Asset );
    }
}
