/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms;

import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.dispatcher.AssetURLFinder;
import com.arsdigita.cms.dispatcher.ItemDelegatedURLPatternGenerator;
import com.arsdigita.cms.dispatcher.ItemTemplatePatternGenerator;
import com.arsdigita.cms.dispatcher.ItemURLFinder;
import com.arsdigita.cms.publishToFile.PublishToFileListener;
import com.arsdigita.cms.publishToFile.QueueManager;
import com.arsdigita.cms.search.AssetMetadataProvider;
import com.arsdigita.cms.search.CMSContentSectionFilterType;
import com.arsdigita.cms.search.ContentTypeFilterType;
import com.arsdigita.cms.search.CreationDateFilterType;
import com.arsdigita.cms.search.CreationUserFilterType;
import com.arsdigita.cms.search.IntermediaQueryEngine;
import com.arsdigita.cms.search.LastModifiedDateFilterType;
import com.arsdigita.cms.search.LastModifiedUserFilterType;
import com.arsdigita.cms.search.LaunchDateFilterType;
import com.arsdigita.cms.search.LuceneQueryEngine;
import com.arsdigita.cms.search.VersionFilterType;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.cms.workflow.TaskEventURLGenerator;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.xml.TraversalHandler;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.URLFinderNotFoundException;
import com.arsdigita.kernel.URLService;
import com.arsdigita.mimetypes.image.ImageSizerFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.LegacyInitializer;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.IndexerType;
import com.arsdigita.search.MetadataProviderRegistry;
import com.arsdigita.search.QueryEngineRegistry;
import com.arsdigita.search.filters.CategoryFilterType;
import com.arsdigita.search.filters.ContentSectionFilterType;
import com.arsdigita.search.filters.ObjectTypeFilterType;
import com.arsdigita.search.filters.PermissionFilterType;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;

import com.arsdigita.xml.XML;
import com.arsdigita.templating.PatternStylesheetResolver;

import com.arsdigita.cms.util.LanguageUtil;

/**
 * The CMS initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Initializer.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class Initializer extends CompoundInitializer {

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-cms.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));

        // add(new LegacyInitializer("com/arsdigita/cms/publishToFile/enterprise.init"));

        add(new com.arsdigita.cms.publishToFile.Initializer());
        add(new com.arsdigita.cms.lifecycle.Initializer());

        add(new LegacyInitializer("com/arsdigita/cms/enterprise.init"));
    }
    /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     *
     * This starts up the search threads according to the values in the
     * properties file
     */
    public void init(DomainInitEvent e) {
        super.init(e);
        LanguageUtil.setSupportedLanguages(
            ContentSection.getConfig().getLanguages());

        URLService.registerFinder(ContentPage.BASE_DATA_OBJECT_TYPE,
                                  new ItemURLFinder());
        URLService.registerFinder(ContentBundle.BASE_DATA_OBJECT_TYPE,
                                  new ItemURLFinder());
        URLService.registerFinder(Template.BASE_DATA_OBJECT_TYPE,
                                  new ItemURLFinder());
        URLService.registerFinder(Asset.BASE_DATA_OBJECT_TYPE,
                                  new AssetURLFinder());

        URLService.registerFinder(
            Link.BASE_DATA_OBJECT_TYPE,
            new URLFinder() {
                public String find(OID oid, String context)
                    throws NoValidURLException {

                    return find(oid);
                }
                public String find( OID oid )
                    throws NoValidURLException {

                    Link link;
                    try {
                        link = (Link) DomainObjectFactory.newInstance( oid );
                    } catch( DataObjectNotFoundException ex ) {
                        throw new NoValidURLException
                            ( "Cannot find an object with oid: " + oid );
                    }

                    if (Link.EXTERNAL_LINK.equals(link.getTargetType())) {
                        return link.getTargetURI();
                    } else {
                        ContentItem target = link.getTargetItem();

                        try {
                            return URLService.locate( target.getOID() );
                        } catch( URLFinderNotFoundException ex ) {
                            throw new UncheckedWrapperException( ex );
                        }
                    }
                }
            }
        );

        ImageSizerFactory.initialize();
        registerInstantiators(e.getFactory());

        registerLuceneEngine();
        registerIntermediaEngine();
        registerPatternGenerators();
        
        // cg - register Task Retrieval engine
        
        Engine.registerEngine(CMSEngine.CMS_ENGINE_TYPE, new CMSEngine());

        // register item adapters
        XML.parse(ContentSection.getConfig().getItemAdapters(),
                  new TraversalHandler());

        // Just set the class implementing methods run when for publishing
        // or unpublishing to file. No initialisation of the class here.
        try {
            QueueManager.setListener((PublishToFileListener)
                                     ContentSection.getConfig()
                                     .getPublishToFileClass().newInstance());
        } catch (InstantiationException ex) {
            throw new UncheckedWrapperException
                ("Failed to instantiate the listener class", ex);
        } catch (IllegalAccessException ex) {
            throw new UncheckedWrapperException
                ("Couldn't access the listener class", ex);
        }

        MetadataProviderRegistry.registerAdapter(
            FileAsset.BASE_DATA_OBJECT_TYPE,
            new AssetMetadataProvider());
    }

    /**
     *  Registers stylesheet pattern generators
     */
    private void registerPatternGenerators() {
        PatternStylesheetResolver.registerPatternGenerator(
            "item_template_oid",
            new ItemTemplatePatternGenerator()
        );

        PatternStylesheetResolver.registerPatternGenerator(
            "item_delegated_url",
            new ItemDelegatedURLPatternGenerator()
        );
    }


    /**
     * Registers object instantiators
     */
     private void registerInstantiators(DomainObjectFactory f) {

        // Register the CMSTaskInstaniator
         f.registerInstantiator
             (CMSTask.BASE_DATA_OBJECT_TYPE,
              new ACSObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                      return new CMSTask(dataObject);
                  }
              });
         f.registerInstantiator
             (CMSTaskType.BASE_DATA_OBJECT_TYPE,
              new DomainObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                      return new CMSTaskType(dataObject);
                  }
              });
         f.registerInstantiator
             (TaskEventURLGenerator.BASE_DATA_OBJECT_TYPE,
              new DomainObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                      return new TaskEventURLGenerator(dataObject);
                  }
              });
         
         
         
         f.registerInstantiator
             (Workflow.BASE_DATA_OBJECT_TYPE,
              new ACSObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                      return new Workflow(dataObject);
                  }
              });

         f.registerInstantiator
             (WorkflowTemplate.BASE_DATA_OBJECT_TYPE,
              new ACSObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                      return new WorkflowTemplate(dataObject);
                  }
              });

         f.registerInstantiator
             (TemplateContext.BASE_DATA_OBJECT_TYPE,
              new DomainObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                      return new TemplateContext(dataObject);
                  }
                  public DomainObjectInstantiator
                      resolveInstantiator(DataObject obj) {
                      return this;
                  }
              });
     }

    private void registerLuceneEngine() {

        QueryEngineRegistry.registerEngine(IndexerType.LUCENE,
                                           new FilterType[] {
                                               new CategoryFilterType(),
                                               new ContentSectionFilterType(),
                                               new CMSContentSectionFilterType(),
                                               new ContentTypeFilterType(),
                                               new CreationDateFilterType(),
                                               new CreationUserFilterType(),
                                               new LastModifiedDateFilterType(),
                                               new LastModifiedUserFilterType(),
                                               new ObjectTypeFilterType(),
                                               new PermissionFilterType(),
                                               new VersionFilterType()
                                           },
                                           new LuceneQueryEngine());
    }

    private void registerIntermediaEngine() {

        QueryEngineRegistry.registerEngine(IndexerType.INTERMEDIA,
                                           new FilterType[] {
                                               new CategoryFilterType(),
                                               new ContentSectionFilterType(),
                                               new CMSContentSectionFilterType(),
                                               new ContentTypeFilterType(),
                                               new CreationDateFilterType(),
                                               new CreationUserFilterType(),
                                               new LastModifiedDateFilterType(),
                                               new LastModifiedUserFilterType(),
                                               new LaunchDateFilterType(),
                                               new ObjectTypeFilterType(),
                                               new PermissionFilterType(),
                                               new VersionFilterType()
                                           },
                                           new IntermediaQueryEngine());
    }
}
