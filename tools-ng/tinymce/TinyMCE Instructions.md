# TinyMCE Instructions

## How to switch from Xhina-Editor to TinyMCE

To be able to switch to TinyMCE, you need to add a few lines in the `integration.properties` file in your bundles `cfg` directory:

```properties
com.arsdigita.cms.dhtml_editor_config=/assets/tinymce/tinymce_cms_config.js
waf.bebop.dhtml_editor=TinyMCE
waf.bebop.dhtml_editor_src=/assets/tinymce/js/tinymce/tinymce.min.js
```

This change will only take effect after you've run `ant load-bundle` again and restarted your installation.

## How to compile an individual plugin

After you changed a plugin in `tools-ng/tinymce/plugins`, you may want to see this changes reflected in your installation for testing. To do this, you need to change into the directory of the plugin and execute `npm run build`. Of course you need to have `npm` installed to do this.

After you've done this, you need to recompile your code. You can do this by running `ant deploy`.