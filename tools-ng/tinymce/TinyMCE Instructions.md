# TinyMCE Instructions

## How to switch from Xhina-Editor to TinyMCE

To be able to switch to TinyMCE, you need to add a few lines in the `integration.properties` file in your bundles `cfg` directory:

```properties
com.arsdigita.cms.dhtml_editor_config=TinyMCE.Config,/assets/tinymce/tinymce_cms_config.js
waf.bebop.dhtml_editor=TinyMCE
waf.bebop.dhtml_editor_src=/assets/tinymce/js/tinymce/tinymce.min.js
```

This change will only take effect after you've run `ant load-bundle` again and restarted your installation.

## How to compile the editor and the plugins

Use the handy `tinymce.sh` script for that. You can do the following things:

-   `./tinymce.sh` - Sets up the environment for compiling and builds the whole editor with all plugins. You can find the compiled files in `editor/js`
-   `./tinymce.sh build` - Builds everything (Editor, Plugins, Themes, ...)
-   `./tinymce.sh plugin <name>` - To compile a single plugin run this with the desired plugin name. The plugin will also be copied into `ccm-core`
-   `./tinymce.sh test <name>` - Similar to `plugin`, but without linting and copies the files into the current runtime for instant testing.
