This directory contains all CSS files for the theme. We use the Less CSS
precompiler (http://lesscss.org/) for creating our CSS files. The source Less
files are also included. 

If you want to customise the stylesheet we strongly recommanded to use Less. 

to customise to styles make a copy of the Less file(s) you want to customize
and create a new configuration file pointing to your files in conf.

To make editing of the Less file(s) 
easier this theme also includes the Less compiler in the development mode. For
production mode you have to compile the Less files to CSS as described in the 
iLess documentation. 

The styles for this theme have been split into two Less files. The styles.less
file contains all styles for the layout. The scicms.less file contains several 
variables which define several properties for the theme. Most of them are
colours. At the end, the file includes the style.less file. 


