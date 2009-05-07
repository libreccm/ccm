==================================
E C D C
Easy CCM Development Configuration
==================================

Development environment for CCM / APLAWS with minimal prerequisites and minimal
installation requirements, i.e. a simplification of the "ccm" environment.

Created for the 'new' CCM / APLAWS with standard compliant deployment and
execution context.

Based on great work by Terry. Many thanks!


=========================================
  CURRENT STATUS:   WORK IN PROGRESS !!
Use it with care!
Some functions do not work as they should
=========================================


1. LICENSING

See http://aplaws.org

2. PREREQUISITES

Java 6.x
Ant 1.7.x
Oracle 10g RDBMS or PostgresQL up to version 8.2

3. HOW TO USE

(a)  Change to the development base directory (usually "trunk") and
     check out the source tree:
     svn co http://svn.fedorahosted.org/svn/aplaws/trunk/

(b)  Copy files *.xml and *.properties of THIS directory (i.e. tools-ng/ecdc)
     into your development base directory ("trunk" above)

(c)  If you don't already have a project.xml file in place, copy the file
     project.xml.complete, too, and rename it to project.xml
     Comment out those modules to don't need. Keep the included modules in sync
     with the bundles application file! (see later).
     Specify name, version, release as you need, don't alter the other
     attributes (exp. ccmVersion and webapps)!

(d)  Edit the file local.ccm.properties and replace the specified bundle name
     by your working bundle. Keep the list of modules in sync with
     project.xml!

(e)  ant install-tomcat 
     will create a runtime directory and install Tomcat 6 for testing. 

(f)  ant configure 
     will create the compile and deploy tasks

(g)  ant deploy 
     will compile and deploy into your test environment (runtime)

As a temporary measure:
     copy ${CATALINA_HOME}/lib/system/ccm-core*  
       to ${JAVA_HOME}/jre/lib/ext

     otherwise you will get a malformedURL exception during server startup

(h)  ant load-bundle 
     will load the database. May take several minutes!

(i)  ant start  
     will start tomcat

(j)  Open your browser:
         http://localhost:8080/
         http://localhost:8080/ccm/content-center/

     Be aware: the previous ccm-scripts based environment used
     port 9000 by default. May be still specified in your
     bundle file (integration.properties). Wrong port number will
     result in a stylesheet compilation error during server startup!

(k)  ant create-war 
     will create a war file from the deployed project for
     installation in any Tomcat 6 compliant servlet container. 

(l)  ant package
     will create a repository of binary modules which may serve
     as a base for distribution and user install & maintenance



4. NOTES

Configuration:
	ccm-ldn-aplaws/bundles/* ("devel" bundle is default)
	local.build.properties
	local.runtime.properties
	local.ccm.properties

Not implemented: 
	ccm set|get|load|run|stop
	Other?
	
Limitations:
	NOT TESTED MUCH!
	localhost:8080
	
Other changes:
	Eclipse .classpath is generated	
	ant install-tomcat replaces host-init (sort-of)
	
Eclipse:
	Import 'aplaws' folder into a workspace
	Use File > Import to import aplaws.launch 
		(if aplaws doesn't appear in Debug configurations)
	Debug > Debug configurations > aplaws > Debug
	
5. CONTACTS

Send any queries to the "Developing on APLAWS+" forum at 
http://sourceforge.net/forum/forum.php?forum_id=368401
