This directory contains all CSS files for all of the theme variants.

The conf/css-files.xml file points invariably to a file named "style.less"
in this directory. This naming must not be modified, otherwise the theme 
publishing process will fail!

The theme is based on the Less CSS precompiler (http://lesscss.org/) for 
creating the CSS files and we provide the source Less files. These files 
are automatically compiled into the approprioate css file(s) when you use 
the theme.  In preview /development mode these files are compiled on the fly. 
When you publish a theme the css files are stored in the directory and 
copied into the published-themedir subdirectory. 

The file 'style.less' is a starter file which just includes the less files
containing the concrete styling.

The distribution provides a default variant of the theme in the file
libre-blue.less file. It provides a set of configuration parameters and 
its default values. The theme details are defined in the less subdir.

To customise the theme make a copy of the default theme file and name it
appropriately, e.g. 'libre-blue-mytheme'. Make your customizations in
'libre-blue-mytheme'. Modify the style.less file to include your
customization instead of the default one by commenting in / out the
appropriate files. 

