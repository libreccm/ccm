(Some information here may be outdated.)

This holds files from your application that should be deployed directly
to the servlet container's "webapp" directory (like the document root
for a web server, the webapp root is the directory where the servlet 
container looks for Java classes, and for JSPs and any static files it 
needs to serve). Under "web/" you will find a "packages/" directory; 
in a complete application, you might also see a "STATIC/" or 
"assets/" directory for static files like graphics or CSS.

The "packages/" directory under "web/" deserves a few special words.
"packages/" is the location for the XSL for an application, and also 
for any JSPs you may want to include. Note that a ccm package is
*not* the same thing as a Java package; a CCM application can contain 
many CCM packages, which can contain many Java packages. The 
distinction is that a CCM package is represented by a row in a CCM 
system table, can be mounted at site nodes and can have its own 
dispatcher. A Java package is simply a unit of code organization, and 
is not tracked by CCM in any way.

XSL stylesheets for a package go under the "packages/package-name/xsl/"
directory. Custom JSPs go under the "packages/package-name/www/
directory.
