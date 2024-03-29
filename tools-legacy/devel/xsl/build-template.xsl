<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:common="http://exslt.org/common"
  xmlns:xalan="http://xml.apache.org/xslt"
  xmlns:ccm="http://ccm.redhat.com/ccm-project"
  exclude-result-prefixes="ccm common">

  <xsl:output method="xml"
    encoding="UTF-8"
    indent="yes"
    xalan:indent-amount="4"/>

  <xsl:template match="ccm:project">
    <xsl:call-template name="CheckDependencies"/>
    <project name="CCM" default="usage" basedir=".">
      <xsl:choose>
        <xsl:when test="/ccm:project/ccm:databases/ccm:database">
          <xsl:call-template name="Main">
            <xsl:with-param name="databases" select="/ccm:project/ccm:databases/ccm:database"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="defaultdatabases">
            <ccm:database name="postgres"/>
            <ccm:database name="oracle-se"/>
          </xsl:variable>
          <xsl:call-template name="Main">
            <xsl:with-param name="databases" select="common:nodeSet($defaultdatabases)//*"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </project>
  </xsl:template>

  <xsl:template name="Main">
    <xsl:param name="databases"/>
    <xsl:call-template name="SharedProperties"/>
    <xsl:call-template name="AppProperties"/>
    <xsl:call-template name="TargetClean"/>
    <xsl:call-template name="TargetBuild">
       <xsl:with-param name="databases" select="$databases"/>
    </xsl:call-template>
    <xsl:call-template name="TargetDeploy"/>
    <xsl:call-template name="TargetJavadoc"/>
    <xsl:call-template name="TargetVerify"/>
    <xsl:call-template name="TargetTest">
       <xsl:with-param name="databases" select="$databases"/>
    </xsl:call-template>
    <xsl:call-template name="TargetMisc"/>
  </xsl:template>

  <xsl:template name="SharedProperties">
    <xsl:variable name="name" select="@name"/>
    <xsl:variable name="prettyName" select="@prettyName"/>
    <property environment="env"/>
    <property value="${{env.CATALINA_HOME}}" name="catalina.home.dir"/>
    <property value="${{env.CCM_CONFIG_HOME}}" name="ccm.config.dir"/>
    <property value="${{env.CCM_CONFIG_LIB_DIR}}" name="ccm.config.lib.dir"/>
    <property value="${{env.CCM_HOME}}" name="ccm.home"/>
    <property value="${{env.CCM_SHARED_LIB_DIST_DIR}}" name="shared.lib.dist.dir"/>
    <property value="${{env.CCM_TOOLS_HOME}}" name="ccm.tools.dir"/>
    <property value="${{env.CCM_WEBAPP_DIST_DIR}}" name="webapp.dist.dir"/>
    <property value="ant.properties" name="property.file"/>
    <property file="${{property.file}}"/>
    <property name="app.name" value="{$name}"/>
    <property name="app.prettyname" value="{$prettyName}"/>
    <property value="build" name="build.dir"/>
    <property value="lib" name="lib.dir"/>
    <property value="etc/lib" name="etclib.dir"/>
    <property value="pdl" name="pdl.dir"/>
    <property value="sql" name="sql.dir"/>
    <property value="src" name="src.dir"/>
    <property value="web" name="web.dir"/>
    <property value="ddl" name="ddl.dir"/>
    <property value="bin" name="bin.dir"/>
    <property value="8300" name="test.remote.port"/>
    <property value="false" name="test.sql.verbose"/>
    <property value="true" name="test.sql.continue"/>
    <property value="test" name="test.dir"/>
    <property value="test/sql" name="test.sql.dir"/>
    <property value="test/pdl" name="test.pdl.dir"/>
    <property value="test/src" name="test.src.dir"/>
    <property value="${{build.dir}}/test" name="test.deploy.dir"/>
    <property value="${{build.dir}}/tmp" name="test.tmp.dir"/>
    <property value="${{build.dir}}/src" name="build.src.dir"/>
    <property value="${{build.dir}}/classes" name="build.classes.dir"/>
    <property value="${{build.dir}}/sql" name="build.sql.dir"/>
    <property value="${{build.dir}}/tests" name="build.test.dir"/>
    <property value="${{build.test.dir}}/classes" name="build.test.classes.dir"/>
    <property value="${{build.test.dir}}/sql" name="build.test.sql.dir"/>
    <property value="${{etclib.dir}}" name="javacc.home.dir"/>
    <property value="*Suite.class" name="junit.suite"/>
    <property value="" name="junit.test"/>
    <property value="off" name="junit.haltonfailure"/>
    <property value="off" name="junit.haltonerror"/>
    <property value="false" name="junit.usefail"/>
    <property value="false" name="junit.usecactus"/>
    <property value="plain" name="junit.formatter"/>
    <property value="xml" name="junit.formatter.extension"/>
    <property value="false" name="junit.showoutput"/>
    <property value="com.arsdigita.tools.junit.extensions.CoreInitializer" name="junit.initializer.classname"/>
    <property value="${{build.dir}}/api" name="javadoc.dir"/>
    <property value="${{build.dir}}/api-apps" name="app.javadoc.dir"/>
    <property value="" name="pdl.args"/>
    <property value="on" name="compile.debug"/>
    <property value="on" name="compile.optimize"/>
    <property value="off" name="compile.deprecation"/>
    <property value="off" name="compile.verbose"/>
    <property value="off" name="compile.nowarn"/>
    <property value="rulesets/design.xml,rulesets/imports.xml" name="pmd.rulesets"/>
    <property value="xml" name="pmd.format"/>
    <property value="${{build.dir}}/pmd" name="pmd.report.dir"/>
    <property value="pmd.${{pmd.format}}" name="pmd.report.file"/>
    <property value="${{shared.lib.dist.dir}}/jdo" name="jdo.lib.dir"/>
    <property value="com.arsdigita.persistence.pdl.PDL" name="ddl.generator.classname"/>
    <property value="com.arsdigita.persistence.pdl.TestPDLGenerator" name="test.ddl.generator.classname"/>
    <xsl:choose>
      <xsl:when test="@webxml">
        <property name="webxml.source.file">
          <xsl:attribute name="value">
            <xsl:value-of select="@webxml"/>
          </xsl:attribute>
        </property>
      </xsl:when>
      <xsl:otherwise>
        <property name="webxml.source.file" value="web.xml-default"/>
      </xsl:otherwise>
    </xsl:choose>
    <path id="ccm.base.classpath">
      <dirset dir="${{ccm.home}}">
        <include name="conf"/>
      </dirset>
      <pathelement path="${{java.class.path}}"/>
      <fileset dir="${{ccm.tools.dir}}">
        <include name="lib/security/*.jar"/>
      </fileset>
    </path>
    <taskdef resource="net/sf/antcontrib/antcontrib.properties">
      <classpath>
        <pathelement location="${{shared.lib.dist.dir}}/ant-contrib.jar"/>
      </classpath>
    </taskdef>
    <taskdef name="jdoenhance" classname="com.redhat.ccm.tools.ant.taskdefs.JDOEnhance">
      <classpath>
        <pathelement location="${{ccm.config.dir}}/classes"/>
      </classpath>
    </taskdef>
  </xsl:template>

  <xsl:template name="CheckDependencies">
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:variable name="appname" select="@name"/>
      <xsl:variable name="appfullname" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application/@name"/>
      <xsl:for-each select="/ccm:project/ccm:application[@name=$appfullname]/ccm:dependencies/ccm:requires">
        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="version" select="@version"/>
        <xsl:variable name="relation" select="@relation"/>
        <xsl:if test="count(/ccm:project/ccm:prebuilt/ccm:application[@name = $name]) + count(/ccm:project/ccm:application[@name = $name]) &lt; 1">
          <xsl:message terminate="yes"><xsl:value-of select="concat($appname,' depends on ',$name,'-',$version)"/></xsl:message>
        </xsl:if>
        <xsl:if test="count(/ccm:project/ccm:prebuilt/ccm:application[@name = $name]) + count(/ccm:project/ccm:application[@name = $name]) > 1">
          <xsl:message terminate="yes"><xsl:value-of select="concat('multiple applications provide ',$name)"/></xsl:message>
        </xsl:if>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="AppProperties">
    <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="location" select="@location"/>
      <xsl:variable name="version" select="@version"/>
      <xsl:choose>
        <xsl:when test="$location">
          <property value="{$location}" name="apps.{$name}.location"/>
        </xsl:when>
        <xsl:otherwise>
          <property value="${{shared.lib.dist.dir}}" name="apps.{$name}.location"/>
        </xsl:otherwise>
      </xsl:choose>
      <path id="{$name}.build.classpath">
        <fileset dir="${{apps.{$name}.location}}/">
          <include name="{$name}-{$version}/**"/>
          <include name="{$name}-{$version}.jar"/>
        </fileset>
      </path>
      <path id="{$name}.run.classpath">
        <path refid="{$name}.build.classpath"/>
      </path>
      <path id="{$name}.tests.classpath">
        <path refid="{$name}.build.classpath"/>
        <fileset dir="${{apps.{$name}.location}}/">
          <include name="{$name}-{$version}-tests.jar"/>
        </fileset>
      </path>
    </xsl:for-each>
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="app" select="document(concat($name,'/application.xml'),/ccm:project)/ccm:application"/>
      <xsl:variable name="appname" select="$app/@name"/>
      <xsl:variable name="appprettyname" select="$app/@prettyName"/>
      <xsl:variable name="appversion" select="$app/@version"/>
      <xsl:variable name="appwebapp" select="$app/@webapp"/>
      <xsl:variable name="requires" select="/ccm:project/ccm:application[@name=$appname]/ccm:dependencies/ccm:requires"/>
      <path id="apps.{$name}.pdl.path">
        <pathelement location="{$name}/${{pdl.dir}}"/>
        <path refid="apps.{$name}.pdl.path.internal"/>
      </path>
      <path id="apps.{$name}.pdl.path.internal">
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:variable name="name" select="@name"/>
          <xsl:variable name="fullname" select="document(concat($name,'/application.xml'),/ccm:project)/ccm:application/@name"/>
          <xsl:for-each select="/ccm:project/ccm:application[@name=$appname]/ccm:dependencies/ccm:requires">
            <xsl:variable name="requiredname" select="@name"/>
            <xsl:if test="$requiredname = $fullname">
              <path refid="apps.{$name}.pdl.path"/>
            </xsl:if>
          </xsl:for-each>
        </xsl:for-each>
      </path>
      <property value="{$appname}" name="apps.{$name}.name"/>
      <property value="{$appprettyname}" name="apps.{$name}.prettyname"/>
      <property value="{$appversion}" name="apps.{$name}.version"/>
      <xsl:choose>
        <xsl:when test="$appwebapp">
          <property value="{$appwebapp}" name="apps.{$name}.webapp.name"/>
        </xsl:when>
        <xsl:otherwise>
          <property value="{$appname}" name="apps.{$name}.webapp.name"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="AppPropertyClassPath">
        <xsl:with-param name="target" select="@name"/>
        <xsl:with-param name="type">run</xsl:with-param>
        <xsl:with-param name="requires" select="$requires"/>
      </xsl:call-template>
      <xsl:call-template name="AppPropertyClassPath">
        <xsl:with-param name="target" select="@name"/>
        <xsl:with-param name="type">build</xsl:with-param>
        <xsl:with-param name="requires" select="$requires"/>
      </xsl:call-template>
      <xsl:call-template name="AppPropertyClassPath">
        <xsl:with-param name="target" select="@name"/>
        <xsl:with-param name="type">tests</xsl:with-param>
        <xsl:with-param name="requires" select="$requires"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:call-template name="AppPropertyClassPath">
      <xsl:with-param name="target">server</xsl:with-param>
      <xsl:with-param name="type">build</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="AppPropertyClassPath">
      <xsl:with-param name="target">server</xsl:with-param>
      <xsl:with-param name="type">run</xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="AppPropertyClassPath">
      <xsl:with-param name="target">server</xsl:with-param>
      <xsl:with-param name="type">tests</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="AppPropertyClassPath_Requires">
    <xsl:param name="requires"/>
    <xsl:param name="type"/>
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="fullname" select="document(concat($name,'/application.xml'),/ccm:project)/ccm:application/@name"/>
      <xsl:for-each select="$requires">
        <xsl:variable name="requiredname" select="@name"/>
        <xsl:if test="$requiredname = $fullname">
          <path refid="{$name}.{$type}.classpath.internal"/>
          <xsl:call-template name="AppPropertyClassPath_Requires">
            <xsl:with-param name="requires" select="/ccm:project/ccm:application[@name=$requiredname]/ccm:dependencies/ccm:requires"/>
            <xsl:with-param name="type" select="$type"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="AppPropertyClassPath">
    <xsl:param name="target" select="'default-value'"/>
    <xsl:param name="type" select="'build'"/>
    <xsl:param name="requires" select="'all'"/>
    <path id="{$target}.{$type}.classpath">
      <pathelement path="${{ccm.config.dir}}/lib/xerces.jar"/>
      <xsl:if test="/ccm:project/ccm:build/ccm:application[@name = $target]">
        <path refid="{$target}.{$type}.classpath.internal"/>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="$requires = 'all'">
          <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
            <xsl:variable name="name" select="@name"/>
            <path refid="{$name}.{$type}.classpath.internal"/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="classPathRequires">
            <xsl:call-template name="AppPropertyClassPath_Requires">
              <xsl:with-param name="requires" select="$requires"/>
              <xsl:with-param name="type" select="$type"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:variable name="classPathRequiresNodeSet" select="common:nodeSet($classPathRequires)" />
          <xsl:copy-of select="$classPathRequiresNodeSet/path[not(@refid=following::path/@refid)]"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
        <xsl:variable name="name" select="@name"/>
        <path refid="{$name}.{$type}.classpath"/>
      </xsl:for-each>
      <path refid="ccm.base.classpath"/>
    </path>
    <xsl:if test="/ccm:project/ccm:build/ccm:application[@name = $target]">
      <path id="{$target}.{$type}.classpath.internal">
        <xsl:choose>
          <xsl:when test="$type = 'run'">
            <fileset dir="./{$target}">
              <include name="${{lib.dir}}/*.jar"/>
              <include name="${{lib.dir}}/*.zip"/>
            </fileset>
            <pathelement path="./{$target}/build/classes/"/>
            <pathelement path="./{$target}/build/sql/"/>
            <pathelement path="./{$target}/pdl"/>
          </xsl:when>
          <xsl:when test="$type = 'build'">
            <fileset dir="./{$target}">
              <include name="${{etclib.dir}}/*.jar"/>
              <include name="${{etclib.dir}}/*.zip"/>
            </fileset>
            <xsl:variable name="fullname" select="document(concat($target,'/application.xml'),/ccm:project)/ccm:application/@name"/>
            <xsl:variable name="buildRequires"
            select="/ccm:project/ccm:application[@name = $fullname]/ccm:dependencies/ccm:buildRequires"/>
            <xsl:if test="count($buildRequires) > 0">
              <fileset dir="${{ccm.config.lib.dir}}">
              <xsl:for-each select="$buildRequires">
                <xsl:variable name="name" select="@name"/>
                <xsl:variable name="version" select="@version"/>
                <include name="{$name}.jar"/>
                <xsl:if test="$version">
                  <include name="{$name}-{$version}.jar"/>
                </xsl:if>
              </xsl:for-each>
              </fileset>
            </xsl:if>
            <path refid="{$target}.run.classpath.internal"/>
          </xsl:when>
          <xsl:when test="$type = 'tests'">
            <pathelement path="./{$target}/build/tests/classes"/>
            <pathelement path="./{$target}/build/tests/sql"/>
            <pathelement path="./{$target}/test/pdl"/>
            <path refid="{$target}.build.classpath.internal"/>
          </xsl:when>
        </xsl:choose>
      </path>
    </xsl:if>
  </xsl:template>

  <xsl:template name="TargetClean">
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
      <xsl:variable name="buildhooks" select="$application/@buildHooks"/>
      <xsl:variable name="hastestdir" select="$application/ccm:directories/ccm:directory[@name='test'] or not($application/ccm:directories)"/>
      <target name="clean-{$name}" description="Cleans out the '{$name}' build environment and whole deployment directory">
        <xsl:attribute name="depends">
          <xsl:value-of select="'init'"/>
          <xsl:value-of select="',clean-deploy'"/>
        </xsl:attribute>
        <xsl:if test="$buildhooks">
          <ant target="clean-build-hook" dir="{$name}" inheritRefs="true">
            <xsl:attribute name="antfile">
              <xsl:value-of select="$buildhooks"/>
            </xsl:attribute>
          </ant>
          <ant target="clean-tests-hook" dir="{$name}" inheritRefs="true">
            <xsl:attribute name="antfile">
              <xsl:value-of select="$buildhooks"/>
            </xsl:attribute>
          </ant>
        </xsl:if>
        <delete dir="{$name}/${{build.dir}}"/>
        <delete>
          <fileset dir="{$name}">
            <include name="TEST-*.txt"/>
            <include name="TEST-*.xml"/>
          </fileset>
        </delete>
      </target>
    </xsl:for-each>
    <!-- The app clean deploy tasks -->
    <target name="clean-deploy" depends="init" description="Cleans out the deployment directory">
      <delete dir="${{deploy.shared.lib.dir}}"/>
      <delete dir="${{deploy.private.lib.dir}}"/>
      <delete dir="${{deploy.webapp.dir}}"/>
    </target>
    <xsl:call-template name="LocalGroupingTarget">
      <xsl:with-param name="targetname" select="'clean'"/>
      <xsl:with-param name="description" select="'Cleans out the build environment and deployment directory'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="TargetBuild">
    <xsl:param name="databases"/>
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
      <xsl:variable name="fullname" select="$application/@name"/>
      <xsl:variable name="buildhooks" select="$application/@buildHooks"/>
      <xsl:variable name="hassqldir" select="$application/ccm:directories/ccm:directory[@name='sql'] or not($application/ccm:directories)"/>
      <xsl:variable name="hassrcdir" select="$application/ccm:directories/ccm:directory[@name='src'] or not($application/ccm:directories)"/>
      <xsl:variable name="haspdldir" select="$application/ccm:directories/ccm:directory[@name='pdl'] or not($application/ccm:directories)"/>
      <xsl:variable name="jdodirs" select="$application/ccm:jdo/ccm:directory"/>
      <xsl:variable name="requires" select="/ccm:project/ccm:application[@name=$fullname]/ccm:dependencies/ccm:requires"/>
      <target name="compile-{$name}" description="Compiles the Java code for the '{$name}' application" depends="init">
        <xsl:if test="$buildhooks">
          <ant target="compile-hook" dir="{$name}" inheritRefs="true">
            <xsl:attribute name="antfile">
              <xsl:value-of select="$buildhooks"/>
            </xsl:attribute>
          </ant>
        </xsl:if>
        <xsl:if test="$hassrcdir">
          <mkdir dir="{$name}/${{build.src.dir}}"/>
          <xsl:if test="$jdodirs">
            <if>
              <not>
                <uptodate targetfile="{$name}/${{build.dir}}/.jdo-timestamp">
                  <srcfiles includes="${{src.dir}}/**/*.jdo" dir="{$name}"/>
                </uptodate>
              </not>
              <then>
                <xsl:for-each select="$jdodirs">
                  <xsl:variable name="jdodir" select="@name"/>
                  <delete dir="{$name}/${{build.classes.dir}}/{$jdodir}"/>
                </xsl:for-each>
                <delete file="{$name}/${{build.dir}}/.jdo-timestamp"/>
              </then>
            </if>
          </xsl:if>
          <path id="{$name}.compile.srcpath">
            <pathelement location="{$name}/${{src.dir}}"/>
            <pathelement location="{$name}/${{build.src.dir}}"/>
          </path>
          <xsl:call-template name="TargetJavaC">
            <xsl:with-param name="destdir" select="concat($name, '/${build.classes.dir}')"/>
            <xsl:with-param name="srcdir" select="concat($name,'/${src.dir}')"/>
            <xsl:with-param name="srcpathref" select="concat($name,'.compile.srcpath')"/>
            <xsl:with-param name="classpathref" select="concat($name,'.build.classpath')"/>
          </xsl:call-template>
        </xsl:if>
      </target>
      <xsl:if test="$jdodirs">
        <target name="jdo-{$name}" description="JOD enhances the Java classes for the '{$name}' application" depends="init,compile-{$name}">
          <jdoenhance destination="{$name}/${{build.classes.dir}}">
            <classpath>
              <fileset dir="${{jdo.lib.dir}}">
                <include name="jdo.jar"/>
                <include name="jdori.jar"/>
                <include name="jdori-enhancer.jar"/>
              </fileset>
              <fileset dir="${{ccm.config.lib.dir}}">
                <include name="xmlParserAPIs.jar"/>
              </fileset>
            </classpath>
            <srcpath>
              <path refid="{$name}.run.classpath"/>
              <path location="{$name}/${{src.dir}}"/>
            </srcpath>
            <dirset dir="{$name}/${{build.classes.dir}}">
              <xsl:for-each select="$jdodirs">
                <xsl:variable name="name" select="@name"/>
                <include name="{$name}"/>
              </xsl:for-each>
            </dirset>
            <mapper type="merge" to="${{basedir}}/{$name}/${{build.dir}}/.jdo-timestamp"/>
            <jdo>
              <pathelement location="{$name}/${{src.dir}}"/>
            </jdo>
          </jdoenhance>
          <echo message="jdo-timestamp" file="{$name}/${{build.dir}}/.jdo-timestamp"/>
        </target>
      </xsl:if>

      <xsl:call-template name="TargetManifest">
        <xsl:with-param name="target" select="$name"/>
        <xsl:with-param name="type" select="'web'"/>
        <xsl:with-param name="sourcedirectory" select="'web'"/>
      </xsl:call-template>
      <xsl:choose>
        <xsl:when test="$haspdldir">
          <!-- DDL generate task -->
          <target name="generate-ddl-{$name}" description="Generates DDL for the '{$name}' application">
            <xsl:attribute name="depends">
              <xsl:value-of select="concat('init,compile-',$name)"/>
              <xsl:if test="$hassqldir"><xsl:value-of select="concat(',copy-sql-',$name)"/></xsl:if>
              <xsl:if test="$hassqldir"><xsl:value-of select="concat(',copy-src-',$name)"/></xsl:if>
            </xsl:attribute>
            <xsl:variable name="sourcexml" select="/"/>
            <if>
              <not>
                <uptodate targetfile="{$name}/${{build.sql.dir}}/.ddl-timestamp">
                  <srcfiles includes="${{pdl.dir}}/**/*.pdl" dir="{$name}"/>
                </uptodate>
              </not>
              <then>
                <xsl:for-each select="$databases">
                  <xsl:call-template name="TargetGenerateDDL">
                    <xsl:with-param name="database" select="@name"/>
                    <xsl:with-param name="requires" select="$requires"/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="sourcexml" select="$sourcexml"/>
                  </xsl:call-template>
                </xsl:for-each>
              </then>
            </if>
            <mkdir dir="{$name}/${{build.dir}}"/>
            <echo message="ddl-timestamp" file="{$name}/${{build.sql.dir}}/.ddl-timestamp"/>
          </target>
          <xsl:call-template name="TargetManifest">
            <xsl:with-param name="target" select="$name"/>
            <xsl:with-param name="type" select="'pdl'"/>
            <xsl:with-param name="sourcedirectory" select="'pdl'"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <target name="manifest-pdl-{$name}">
            <mkdir dir="{$name}/${{build.classes.dir}}"/>
            <touch file="{$name}/${{build.classes.dir}}/${{apps.{$name}.name}}.pdl.mf"/>
          </target>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="$hassqldir">
        <target name="copy-sql-{$name}">
          <mkdir dir="{$name}/${{build.sql.dir}}"/>
          <copy todir="{$name}/${{build.sql.dir}}">
            <fileset dir="{$name}/${{sql.dir}}">
              <include name="**/*.sql"/>
            </fileset>
          </copy>
        </target>
      </xsl:if>
      <xsl:if test="$hassrcdir">
        <target depends="init" name="copy-src-{$name}">
          <copy todir="{$name}/${{build.classes.dir}}">
            <fileset dir="{$name}/${{src.dir}}">
              <exclude name="**/package.html"/>
              <exclude name="**/*.java"/>
            </fileset>
          </copy>
        </target>
      </xsl:if>
      <target name="jar-classes-{$name}" depends="init,build-{$name}">
        <jar destfile="{$name}/${{build.dir}}/${{apps.{$name}.name}}-${{apps.{$name}.version}}.jar" update="true">
          <fileset dir="{$name}/${{build.classes.dir}}"/>
          <xsl:choose>
            <xsl:when test="$haspdldir">
              <manifest>
                <attribute name="Class-Path" value="${{apps.{$name}.name}}-${{apps.{$name}.version}}-pdl.jar ${{apps.{$name}.name}}-${{apps.{$name}.version}}-sql.jar"/>
              </manifest>
            </xsl:when>
            <xsl:when test="$hassqldir">
              <manifest>
                <attribute name="Class-Path" value="${{apps.{$name}.name}}-${{apps.{$name}.version}}-sql.jar"/>
              </manifest>
            </xsl:when>
          </xsl:choose>
        </jar>
      </target>
      <xsl:if test="$haspdldir">
        <target name="jar-pdl-{$name}" depends="init,build-{$name}">
          <mkdir dir="{$name}/${{pdl.dir}}"/>
          <jar destfile="{$name}/${{build.dir}}/${{apps.{$name}.name}}-${{apps.{$name}.version}}-pdl.jar" update="true">
            <fileset dir="{$name}/${{pdl.dir}}"/>
          </jar>
        </target>
      </xsl:if>
      <xsl:if test="$haspdldir or $hassqldir">
        <target name="jar-sql-{$name}" depends="init,build-{$name}">
          <jar destfile="{$name}/${{build.dir}}/${{apps.{$name}.name}}-${{apps.{$name}.version}}-sql.jar" update="true">
            <fileset dir="{$name}/${{build.sql.dir}}">
              <exclude name=".ddl-timestamp"/>
            </fileset>
          </jar>
        </target>
      </xsl:if>
      <!-- Build tasks -->
      <xsl:call-template name="TargetBuildApp">
        <xsl:with-param name="target" select="@name"/>
        <xsl:with-param name="requires" select="$requires"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:call-template name="LocalGroupingTarget">
      <xsl:with-param name="targetname" select="'build'"/>
      <xsl:with-param name="description" select="'Builds all applications'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="TargetBuildApp">
    <xsl:param name="target"/>
    <xsl:param name="requires"/>
    <xsl:variable name="application" select="document(concat($target,'/application.xml'),/ccm:project)/ccm:application"/>
    <xsl:variable name="buildhooks" select="$application/@buildHooks"/>
    <xsl:variable name="hassqldir" select="$application/ccm:directories/ccm:directory[@name='sql'] or not($application/ccm:directories)"/>
    <xsl:variable name="hassrcdir" select="$application/ccm:directories/ccm:directory[@name='src'] or not($application/ccm:directories)"/>
    <xsl:variable name="haspdldir" select="$application/ccm:directories/ccm:directory[@name='pdl'] or not($application/ccm:directories)"/>
    <xsl:variable name="jdodirs" select="$application/ccm:jdo/ccm:directory"/>
    <target name="build-{$target}" description="Builds the '{$target}' application (compile, generate DDL, jar, etc)">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:variable name="name" select="@name"/>
          <xsl:variable name="fullname" select="document(concat($name,'/application.xml'),/ccm:project)/ccm:application/@name"/>
          <xsl:for-each select="$requires">
            <xsl:variable name="requiredname" select="@name"/>
            <xsl:if test="$requiredname = $fullname">
              <xsl:value-of select="concat(',build-',$name)"/>
            </xsl:if>
          </xsl:for-each>
        </xsl:for-each>
        <xsl:value-of select="concat(',compile-',$target)"/>
        <xsl:if test="$jdodirs"><xsl:value-of select="concat(',jdo-',$target)"/></xsl:if>
        <xsl:if test="$hassrcdir"><xsl:value-of select="concat(',copy-src-',$target)"/></xsl:if>
        <xsl:if test="$haspdldir"><xsl:value-of select="concat(',generate-ddl-',$target)"/></xsl:if>
        <xsl:value-of select="concat(',manifest-pdl-',$target)"/>
        <xsl:value-of select="concat(',manifest-web-',$target)"/>
      </xsl:attribute>
    </target>
  </xsl:template>

  <xsl:template name="TargetGenerateDDL">
    <xsl:param name="database"/>
    <xsl:param name="requires"/>
    <xsl:param name="name"/>
    <xsl:param name="sourcexml"/>
    <mkdir dir="{$name}/${{build.sql.dir}}/${{apps.{$name}.name}}/${{ddl.dir}}/{$database}"/>
    <java failonerror="yes" classname="${{ddl.generator.classname}}" fork="yes">
      <sysproperty key="java.ext.dirs" value="${{ccm.java.ext.dirs}}"/>
      <classpath refid="{$name}.build.classpath"/>
      <arg line="-quiet"/>
      <arg line="-generate-ddl {$name}/${{build.sql.dir}}/${{apps.{$name}.name}}/${{ddl.dir}}/{$database}"/>
      <arg line="-database {$database}"/>
      <arg line="-path {$name}/${{pdl.dir}}"/>
      <arg line="-sqldir {$name}/${{build.sql.dir}}"/>
      <xsl:call-template name="TargetPdlPath">
        <xsl:with-param name="target" select="$name"/>
        <xsl:with-param name="requires" select="$requires"/>
        <xsl:with-param name="sourcexml" select="$sourcexml"/>
      </xsl:call-template>
    </java>
  </xsl:template>

  <xsl:template name="TargetPdlPath">
    <xsl:param name="target"/>
    <xsl:param name="requires" select="'all'"/>
    <xsl:param name="includeself" select="'no'"/>
    <xsl:param name="pathType" select="'library-path'"/>
    <xsl:param name="sourcexml" select="/"/>
    <xsl:if test="($requires = 'all') or ($includeself = 'yes') or (count($requires) > 0) or (count($sourcexml//ccm:project/ccm:prebuilt/ccm:application) > 0)">
      <xsl:element name="arg">
        <xsl:attribute name="line">
          <xsl:value-of select="concat('-',$pathType,' ')"/>
          <xsl:choose>
            <xsl:when test="$requires = 'all'">
              <xsl:for-each select="$sourcexml//ccm:project/ccm:build/ccm:application">
                <xsl:value-of select="concat(@name, '/${pdl.dir}:')"/>
              </xsl:for-each>
            </xsl:when>
            <xsl:when test="$includeself = 'no'">
              <xsl:value-of select="concat('${apps.',$target,'.pdl.path.internal}:')"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="concat('${apps.',$target,'.pdl.path}:')"/>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:for-each select="$sourcexml//ccm:project/ccm:prebuilt/ccm:application">
            <xsl:value-of select="concat('${apps.',@name,'.location}/',@name,'-',@version,'-pdl.jar:')"/>
          </xsl:for-each>
        </xsl:attribute>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <xsl:template name="TargetJSPCompilerClasspath">
    <classpath>
      <fileset dir="${{catalina.home.dir}}">
        <include name="server/lib/*.jar"/>
        <include name="common/lib/*.jar"/>
      </fileset>
      <path refid="server.build.classpath"/>
    </classpath>
  </xsl:template>

  <xsl:template name="TargetVerify">
    <target name="verify-pdl" depends="init" unless="pdl.no.verify" description="Verifies that the PDL files compile">
      <java classname="${{ddl.generator.classname}}" failonerror="yes" fork="yes">
        <sysproperty key="java.ext.dirs" value="${{ccm.java.ext.dirs}}"/>
        <classpath refid="server.build.classpath"/>
        <arg line="${{pdl.args}}"/>
        <xsl:call-template name="TargetPdlPath">
          <xsl:with-param name="pathType">path</xsl:with-param>
        </xsl:call-template>
      </java>
      <echo message="PDL files compiled successfully."/>
    </target>
    <target name="jsp-compiler-check">
      <condition property="jsp.compiler.available">
        <and>
          <available file="${{catalina.home.dir}}" type="dir"/>
          <available classname="org.apache.jasper.JspC">
            <xsl:call-template name="TargetJSPCompilerClasspath"/>
          </available>
        </and>
      </condition>
    </target>
    <target name="compile-jsp" depends="init,deploy,jsp-compiler-check" if="jsp.compiler.available">
      <taskdef classname="org.apache.jasper.JspC" name="jasper2">
         <xsl:call-template name="TargetJSPCompilerClasspath"/>
      </taskdef>
      <jasper2
        verbose="1"
        uriroot="${{deploy.dir}}"
        webXmlFragment="${{build.dir}}/generated_web.xml"
        outputDir="${{build.dir}}/jsp-java" />
    </target>
    <target name="build-jsp" depends="init,compile-jsp" if="jsp.compiler.available">
      <javac
        debug="${{compile.debug}}"
        optimize="${{compile.optimize}}"
        deprecation="${{compile.deprecation}}"
        verbose="${{compile.verbose}}"
        nowarn="${{compile.nowarn}}"
        destdir="${{build.dir}}/jsp-classes">
        <src refid="${{build.dir}}/jsp-java"/>
        <xsl:call-template name="TargetJSPCompilerClasspath"/>
      </javac>
    </target>
    <target name="no-build-jsp" unless="jsp.compiler.available">
      <echo message="JSP verification skipped.  The CATALINA_HOME environment variable must point to a Tomcat 4.1 installation."/>
    </target>
    <target name="verify-jsp" depends="init,build-jsp,no-build-jsp" description="Verifies that JSP files compile (requires Tomcat 4.1)"/>
  </xsl:template>

  <xsl:template name="TargetDeploySystemJar">
    <xsl:param name="name"/>
    <xsl:param name="package"/>
    <if>
      <available file="{$name}/${{build.classes.dir}}/{$package}" type="dir"/>
      <then>
        <mkdir dir="${{deploy.system.jars.dir}}"/>
        <jar destfile="${{deploy.system.jars.dir}}/${{apps.{$name}.name}}-${{apps.{$name}.version}}-system.jar" update="true">
          <fileset dir="{$name}/${{build.classes.dir}}">
            <include name="{$package}**"/>
          </fileset>
        </jar>
      </then>
    </if>
  </xsl:template>

  <xsl:template name="TargetDeploy">
    <xsl:variable name="name" select="@name"/>
    <!-- Deploy hooks -->
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
      <xsl:variable name="buildhooks" select="$application/@buildHooks"/>
      <xsl:variable name="haspdldir" select="$application/ccm:directories/ccm:directory[@name='pdl'] or not($application/ccm:directories)"/>
      <xsl:variable name="hassqldir" select="$application/ccm:directories/ccm:directory[@name='sql'] or not($application/ccm:directories)"/>
      <xsl:variable name="hassrcdir" select="$application/ccm:directories/ccm:directory[@name='src'] or not($application/ccm:directories)"/>
      <xsl:if test="$application/@buildHooks">
        <target name="deploy-{$name}-hook" depends="init">
          <ant target="deploy-hook" dir="{$name}" inheritRefs="true">
            <xsl:attribute name="antfile">
              <xsl:value-of select="$application/@buildHooks"/>
            </xsl:attribute>
          </ant>
        </target>
      </xsl:if>
      <!-- Deploy War -->
      <target name="deploy-war-{$name}" depends="init,jar-classes-{$name}">
        <mkdir dir="${{deploy.war.dir}}"/>
        <mkdir dir="{$name}/${{lib.dir}}"/>
        <war destfile="${{deploy.war.dir}}/${{apps.{$name}.name}}-${{apps.{$name}.version}}.war"
             webxml="{$name}/${{web.dir}}/WEB-INF/web.xml">
          <fileset dir="{$name}/${{web.dir}}">
            <exclude name="WEB-INF/web.xml"/>
          </fileset>
          <lib dir="{$name}/${{lib.dir}}"/>
          <classes dir="{$name}/${{build.classes.dir}}"/>
        </war>
      </target>
      <!-- Deploy Jar -->
      <target name="deploy-jar-classes-{$name}" depends="init,jar-classes-{$name}">
        <mkdir dir="${{deploy.lib.dir.{$name}}}"/>
        <copy todir="${{deploy.lib.dir.{$name}}}" file="{$name}/${{build.dir}}/${{apps.{$name}.name}}-${{apps.{$name}.version}}.jar"/>
        <xsl:for-each select="$application/ccm:systemPackages/ccm:package">
          <xsl:call-template name="TargetDeploySystemJar">
            <xsl:with-param name="name" select="$name"/>
            <xsl:with-param name="package" select="@path"/>
          </xsl:call-template>
        </xsl:for-each>
      </target>
        <!-- Deploy classes -->
      <xsl:if test="$hassrcdir or $haspdldir">
        <target name="deploy-classes-{$name}" depends="init,build-{$name}">
          <mkdir dir="${{deploy.classes.dir.{$name}}}"/>
          <copy todir="${{deploy.classes.dir.{$name}}}">
            <fileset dir="{$name}/${{build.classes.dir}}"/>
          </copy>
          <xsl:for-each select="$application/ccm:systemPackages/ccm:package">
            <xsl:call-template name="TargetDeploySystemJar">
              <xsl:with-param name="name" select="$name"/>
              <xsl:with-param name="package" select="@path"/>
            </xsl:call-template>
          </xsl:for-each>
        </target>
      </xsl:if>
      <!-- Deploy libs -->
      <target name="deploy-lib-{$name}" depends="init">
        <mkdir dir="${{deploy.private.lib.dir}}"/>
        <copy todir="${{deploy.private.lib.dir}}">
          <fileset dir="{$name}">
            <include name="${{lib.dir}}/**"/>
          </fileset>
          <mapper type="glob" to="*" from="${{lib.dir}}${{file.separator}}*"/>
        </copy>
      </target>
        <!-- Deploy pdl -->
      <xsl:if test="$haspdldir">
        <target name="deploy-pdl-{$name}" depends="init,jar-pdl-{$name}">
          <mkdir dir="${{deploy.lib.dir.{$name}}}"/>
          <copy todir="${{deploy.lib.dir.{$name}}}">
            <fileset dir="{$name}/${{build.dir}}">
              <include name="*pdl.jar"/>
            </fileset>
          </copy>
        </target>
      </xsl:if>
        <!-- Deploy sql -->
      <xsl:if test="$hassqldir or $haspdldir">
        <target name="deploy-sql-{$name}" depends="init,generate-ddl-{$name},jar-sql-{$name}">
          <mkdir dir="${{deploy.lib.dir.{$name}}}"/>
          <copy todir="${{deploy.lib.dir.{$name}}}">
            <fileset dir="{$name}/${{build.dir}}">
              <include name="*sql.jar"/>
            </fileset>
          </copy>
        </target>
      </xsl:if>
      <!-- Deploy web -->
      <target name="deploy-web-{$name}" depends="init">
        <copy todir="${{deploy.dir.{$name}}}">
          <fileset dir="{$name}">
            <include name="${{web.dir}}/**"/>
          </fileset>
          <mapper type="glob" to="*" from="${{web.dir}}${{file.separator}}*"/>
        </copy>
      </target>
      <!-- Deploy bin -->
      <target name="deploy-bin-{$name}" depends="init">
        <copy todir="${{deploy.bin.dir.{$name}}}">
          <fileset dir="{$name}">
            <include name="${{bin.dir}}/**"/>
          </fileset>
          <mapper type="glob" to="*" from="${{bin.dir}}${{file.separator}}*"/>
        </copy>
      </target>
        <!-- Deploy Javadoc -->
      <xsl:if test="$hassrcdir">
        <target name="deploy-javadoc-{$name}" depends="init,javadoc-{$name}">
          <copy todir="${{deploy.api.dir.{$name}}}">
            <fileset dir="${{app.javadoc.dir}}/{$name}"/>
          </copy>
        </target>
      </xsl:if>
      <!-- Deploy app -->
      <target name="deploy-{$name}" description="Builds and deploys the '{$name}' application">
        <xsl:attribute name="depends">
          <xsl:text>init</xsl:text>
          <xsl:text>,build-</xsl:text><xsl:value-of select="@name"/>
          <xsl:if test="$buildhooks"><xsl:value-of select="concat(',deploy-',$name,'-hook')"/></xsl:if>
          <xsl:if test="$hassrcdir or $haspdldir"><xsl:value-of select="concat(',deploy-classes-',$name)"/></xsl:if>
          <xsl:value-of select="concat(',deploy-lib-',$name)"/>
          <xsl:if test="$haspdldir"><xsl:value-of select="concat(',deploy-pdl-',$name)"/></xsl:if>
          <xsl:if test="$hassqldir or $haspdldir"><xsl:text>,deploy-sql-</xsl:text><xsl:value-of select="@name"/></xsl:if>
          <xsl:value-of select="concat(',deploy-web-',$name)"/>
          <xsl:value-of select="concat(',deploy-bin-',$name)"/>
        </xsl:attribute>
        <echo message="deployed '{$name}' to ${{deploy.dir.{$name}}}"/>
      </target>
    </xsl:for-each>
    <xsl:call-template name="LocalGroupingTarget">
      <xsl:with-param name="targetname" select="'deploy-web'"/>
    </xsl:call-template>
    <xsl:call-template name="LocalGroupingTarget">
      <xsl:with-param name="targetname" select="'deploy-jar-classes'"/>
    </xsl:call-template>
    <!-- Deploy prebuilt apps -->
    <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="version" select="@version"/>
      <target name="deploy-{$name}" depends="init" description="Deploys the '{$name}' prebuilt application">
        <copy todir="${{deploy.shared.lib.dir}}" preservelastmodified="true">
          <fileset dir="${{apps.{$name}.location}}">
            <include name="{$name}-{$version}.jar"/>
            <include name="{$name}-{$version}-*.jar"/>
            <exclude name="{$name}-{$version}-system.jar"/>
          </fileset>
        </copy>
        <copy todir="${{deploy.system.jars.dir}}" preservelastmodified="true">
          <fileset dir="${{apps.{$name}.location}}">
            <include name="{$name}-{$version}-system.jar"/>
          </fileset>
        </copy>
        <copy todir="${{deploy.private.lib.dir}}" preservelastmodified="true">
          <fileset dir="${{apps.{$name}.location}}">
            <include name="{$name}-{$version}/**"/>
          </fileset>
          <mapper type="glob" to="*" from="{$name}-{$version}${{file.separator}}*"/>
        </copy>
        <copy todir="${{deploy.webapp.dir}}" preservelastmodified="true">
          <fileset dir="${{webapp.dist.dir}}">
            <include name="{$name}-{$version}/**"/>
          </fileset>
          <mapper type="glob" to="*" from="{$name}-{$version}${{file.separator}}*"/>
        </copy>
        <echo message="deployed '{$name}' to ${{deploy.webapp.dir}}"/>
      </target>
    </xsl:for-each>
    <target name="deploy-global">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
          <xsl:text>,deploy-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>
    <target name="deploy-local">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:variable name="name" select="@name"/>
          <xsl:text>,deploy-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>
    <target name="copy-webxml-init" depends="init">
      <available file="${{deploy.webapp.dir}}/ROOT/WEB-INF" type="dir" property="root.webapp.exists"/>
      <condition property="resolved.webxml.source.file" value="${{deploy.webapp.dir}}/ROOT/WEB-INF/${{webxml.source.file}}">
        <and>
          <isset property="root.webapp.exists"/>
          <available file="${{deploy.webapp.dir}}/ROOT/WEB-INF/${{webxml.source.file}}"/>
          <not>
            <available file="${{webxml.source.file}}"/>
          </not>
        </and>
      </condition>
      <condition property="resolved.webxml.source.file" value="${{webxml.source.file}}">
        <not><isset property="resolved.webxml.source.file"/></not>
      </condition>
    </target>
    <target name="copy-webxml" depends="init,copy-webxml-init" if="root.webapp.exists">
      <copy file="${{resolved.webxml.source.file}}" tofile="${{deploy.webapp.dir}}/ROOT/WEB-INF/web.xml"/>
    </target>
    <!-- Master deploy -->
    <target name="deploy" depends="init,deploy-global,deploy-local,copy-webxml">
      <xsl:attribute name="description">Builds and deploys all applications, also deploys prebuilt applications and config files</xsl:attribute>
    </target>
  </xsl:template>

  <xsl:template name="TargetCopyTestPDL">
    <xsl:param name="target" select="'default-value'"/>
    <xsl:param name="requires" select="'all'"/>
    <target depends="init" name="copy-test-pdl-{$target}">
      <mkdir dir="{$target}/${{build.test.classes.dir}}"/>
      <copy todir="{$target}/${{build.test.classes.dir}}">
        <fileset dir=".">
          <include name="{$target}/${{test.pdl.dir}}/**"/>
        </fileset>
        <mapper type="glob" to="*" from="{$target}/${{test.pdl.dir}}${{file.separator}}*"/>
      </copy>
      <copy todir="{$target}/${{build.test.classes.dir}}">
        <fileset dir=".">
          <include name="{$target}/${{pdl.dir}}/**"/>
        </fileset>
        <mapper type="glob" to="*" from="{$target}/${{pdl.dir}}${{file.separator}}*"/>
      </copy>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:variable name="name" select="@name"/>
        <xsl:choose>
          <xsl:when test="$requires = 'all'">
            <copy todir="{$target}/${{build.test.classes.dir}}">
              <fileset dir=".">
                <include name="{$name}/${{pdl.dir}}/**"/>
              </fileset>
              <mapper type="glob" to="*" from="{$name}/${{pdl.dir}}${{file.separator}}*"/>
            </copy>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="fullname" select="document(concat($name,'/application.xml'),/ccm:project)/ccm:application/@name"/>
            <xsl:for-each select="$requires">
              <xsl:variable name="requiredname" select="@name"/>
              <xsl:if test="$requiredname = $fullname">
                <copy todir="{$target}/${{build.test.classes.dir}}">
                  <fileset dir=".">
                    <include name="{$name}/${{pdl.dir}}/**"/>
                  </fileset>
                  <mapper type="glob" to="*" from="{$name}/${{pdl.dir}}${{file.separator}}*"/>
                </copy>
              </xsl:if>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </target>
  </xsl:template>

  <xsl:template name="TargetGenerateTestDDL">
    <xsl:param name="target"/>
    <xsl:param name="database"/>
    <xsl:param name="requires"/>
    <xsl:param name="sourcexml"/>
    <mkdir dir="{$target}/${{build.test.sql.dir}}/${{ddl.dir}}/{$database}"/>
    <java failonerror="yes" classname="${{test.ddl.generator.classname}}" fork="yes">
      <sysproperty key="java.ext.dirs" value="${{ccm.java.ext.dirs}}"/>
      <classpath refid="{$target}.build.classpath"/>
      <arg line="-quiet"/>
      <arg line="-generate-ddl {$target}/${{build.test.sql.dir}}/{$database}"/>
      <arg line="-database {$database}"/>
      <arg line="-path {$target}/${{test.pdl.dir}}/com"/>
      <xsl:call-template name="TargetPdlPath">
        <xsl:with-param name="target" select="$target"/>
        <xsl:with-param name="includeself" select="'yes'"/>
        <xsl:with-param name="requires" select="$requires"/>
        <xsl:with-param name="sourcexml" select="$sourcexml"/>
      </xsl:call-template>
    </java>
  </xsl:template>

  <xsl:template name="TargetBuildAppTest">
    <xsl:param name="target"/>
    <xsl:param name="requires"/>
    <target name="build-tests-{$target}" description="Build the tests for the '{$target}' application">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:variable name="name" select="@name"/>
          <xsl:variable name="fullname" select="document(concat($name,'/application.xml'),/ccm:project)/ccm:application/@name"/>
          <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
          <xsl:variable name="hastestdir" select="$application/ccm:directories/ccm:directory[@name='test'] or not($application/ccm:directories)"/>
          <xsl:for-each select="$requires">
            <xsl:variable name="requiredname" select="@name"/>
            <xsl:if test="($requiredname = $fullname) and $hastestdir">
              <xsl:value-of select="concat(',build-tests-',$name)"/>
            </xsl:if>
          </xsl:for-each>
        </xsl:for-each>
        <xsl:value-of select="concat(',compile-tests-',$target)"/>
        <xsl:value-of select="concat(',generate-test-ddl-',$target)"/>
        <xsl:value-of select="concat(',copy-test-src-',$target)"/>
        <xsl:value-of select="concat(',manifest-test.pdl-',$target)"/>
      </xsl:attribute>
    </target>
  </xsl:template>

  <xsl:template name="TargetTest">
    <xsl:param name="databases"/>
    <xsl:variable name="name" select="@name"/>
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
      <xsl:variable name="hastestdir" select="$application/ccm:directories/ccm:directory[@name='test'] or not($application/ccm:directories)"/>
      <xsl:variable name="requires" select="$application/ccm:dependencies/ccm:requires"/>
      <xsl:if test="$hastestdir">
        <xsl:call-template name="TargetBuildAppTest">
          <xsl:with-param name="target" select="@name"/>
          <xsl:with-param name="requires" select="$requires"/>
        </xsl:call-template>
        <target name="compile-tests-{$name}" depends="init,build-{$name}" if="{$name}.test.src.dir.exists">
          <path id="{$name}.tests.compile.srcpath">
            <pathelement location="{$name}/${{test.src.dir}}"/>
          </path>
          <xsl:call-template name="TargetJavaC">
            <xsl:with-param name="destdir" select="concat($name,'/${build.test.classes.dir}')"/>
            <xsl:with-param name="srcpathref" select="concat($name,'.tests.compile.srcpath')"/>
            <xsl:with-param name="classpathref" select="concat($name,'.tests.classpath')"/>
          </xsl:call-template>
        </target>
        <target depends="init" name="copy-test-src-{$name}" if="{$name}.test.src.dir.exists">
          <mkdir dir="{$name}/${{build.test.classes.dir}}"/>
          <copy todir="{$name}/${{build.test.classes.dir}}">
            <fileset dir="{$name}/${{test.src.dir}}">
              <exclude name="**/*.java"/>
            </fileset>
          </copy>
        </target>
        <xsl:call-template name="TargetManifest">
          <xsl:with-param name="target" select="$name"/>
          <xsl:with-param name="type" select="'test.pdl'"/>
          <xsl:with-param name="sourcedirectory" select="'test/pdl'"/>
        </xsl:call-template>
        <target depends="init" name="copy-test-sql-{$name}">
          <mkdir dir="{$name}/${{build.test.sql.dir}}"/>
          <copy todir="{$name}/${{build.test.sql.dir}}">
            <fileset dir="{$name}">
              <include name="${{test.sql.dir}}/**/*.sql"/>
            </fileset>
            <mapper type="glob" to="*" from="${{test.sql.dir}}${{file.separator}}*"/>
          </copy>
        </target>
        <target name="generate-test-ddl-{$name}">
          <xsl:attribute name="depends">
            <xsl:value-of select="concat('init,copy-test-sql-',$name)"/>
          </xsl:attribute>
          <if>
            <and>
              <available file="{$name}/test/pdl"/>
              <not>
                <uptodate targetfile="{$name}/${{build.test.sql.dir}}/.ddl-timestamp">
                  <srcfiles includes="${{test.pdl.dir}}/**/*.pdl" dir="{$name}"/>
                </uptodate>
              </not>
            </and>
            <then>
              <xsl:variable name="sourcexml" select="/"/>
              <xsl:for-each select="$databases">
                <xsl:call-template name="TargetGenerateTestDDL">
                  <xsl:with-param name="database" select="@name"/>
                  <xsl:with-param name="requires" select="$requires"/>
                  <xsl:with-param name="target" select="$name"/>
                  <xsl:with-param name="sourcexml" select="$sourcexml"/>
                </xsl:call-template>
              </xsl:for-each>
              <mkdir dir="{$name}/${{build.test.sql.dir}}"/>
              <echo message="ddl-timestamp" file="{$name}/${{build.test.sql.dir}}/.ddl-timestamp"/>
            </then>
          </if>
        </target>
        <xsl:call-template name="TargetCopyTestPDL">
          <xsl:with-param name="target" select="@name"/>
          <xsl:with-param name="requires" select="$requires"/>
        </xsl:call-template>
        <target name="deploy-test-webapp-{$name}">
          <antcall target="deploy">
            <param name="deploy.dir" value="${{test.deploy.dir}}"/>
            <param name="deploy.init.dir" value="${{test.deploy.dir}}/WEB-INF/resources"/>
            <param name="deploy.web.dir" value="${{test.deploy.dir}}/WEB-INF"/>
          </antcall>
        </target>
        <target name="runtests-{$name}" depends="init,build-tests-{$name},copy-test-pdl-{$name},generate-test-ddl-{$name},deploy-test-webapp-{$name}">
          <xsl:attribute name="description"><xsl:value-of select="concat('Runs the tests for the ',$name,' application')"/></xsl:attribute>
          <mkdir dir="{$name}/${{test.tmp.dir}}"/>
          <junit
            printsummary="yes"
            fork="yes"
            haltonfailure="${{junit.haltonfailure}}"
            haltonerror="${{junit.haltonerror}}"
            dir="{$name}"
            showoutput="${{junit.showoutput}}">
            <jvmarg line="${{junit.jvmargs}}"/>
            <xsl:choose>
              <xsl:when test="/ccm:project/ccm:options/ccm:option[@name='junitCustomFormatter']/@value = 1">
                <formatter classname="${{junit.formatter.classname}}" extension="${{junit.formatter.extension}}"/>
              </xsl:when>
              <xsl:otherwise>
                <formatter type="${{junit.formatter}}"/>
              </xsl:otherwise>
            </xsl:choose>
            <classpath refid="server.tests.classpath"/>
            <sysproperty key="ccm.home" value="${{ccm.home}}"/>
            <sysproperty value="${{junit.initializer.classname}}" key="waf.runtime.init"/>
            <sysproperty key="j2ee.webapp.dir" value="${{deploy.dir}}"/>
            <sysproperty key="junit.usefail" value="${{junit.usefail}}"/>
            <sysproperty key="junit.test" value="${{junit.test}}"/>
            <sysproperty key="junit.usecactus" value="${{junit.usecactus}}"/>
            <sysproperty key="sql.continue" value="${{test.sql.continue}}"/>
            <sysproperty key="sql.verbose" value="${{test.sql.verbose}}"/>
            <sysproperty key="test.base.dir" value="${{basedir}}/{$name}/${{build.test.classes.dir}}"/>
            <sysproperty key="test.initscript" value="${{test.initscript}}"/>
            <sysproperty key="test.server.url" value="${{test.server.url}}"/>
            <sysproperty key="test.sql.dir" value="${{basedir}}/{$name}/${{build.test.sql.dir}}"/>
            <sysproperty key="test.tmp.dir" value="${{basedir}}/{$name}/${{test.tmp.dir}}"/>
            <sysproperty key="test.webapp.dir" value="${{basedir}}/${{test.deploy.dir}}"/>
            <sysproperty key="log4j.configuration" value="${{log4j.configuration.sysproperty}}"/>
            <sysproperty value="${{apps.{$name}.name}}.test.pdl.mf" key="waf.runtime.test.pdl"/>
            <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
              <xsl:variable name="name" select="@name"/>
              <xsl:variable name="fullname" select="document(concat($name,'/application.xml'),/ccm:project)/ccm:application/@name"/>
              <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
              <xsl:variable name="hastestdir" select="$application/ccm:directories/ccm:directory[@name='test'] or not($application/ccm:directories)"/>
              <xsl:for-each select="$requires">
                <xsl:variable name="requiredname" select="@name"/>
                <xsl:if test="$requiredname = $fullname">
                  <sysproperty value="${{apps.{$name}.name}}.test.pdl.mf" key="waf.runtime.test.pdl"/>
                </xsl:if>
              </xsl:for-each>
            </xsl:for-each>
            <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
              <xsl:variable name="name" select="@name"/>
              <xsl:variable name="version" select="@version"/>
              <sysproperty value="${{apps.{$name}.name}}-{$version}.test.pdl.mf" key="waf.runtime.test.pdl"/>
            </xsl:for-each>
            <batchtest todir="{$name}">
              <fileset dir="{$name}/${{build.test.classes.dir}}">
                <include name="**/${{junit.suite}}"/>
              </fileset>
            </batchtest>
          </junit>
        </target>
      </xsl:if>
    </xsl:for-each>
    <target name="build-tests" description="Builds all applications and tests">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
          <xsl:variable name="hastestdir" select="$application/ccm:directories/ccm:directory[@name='test'] or not($application/ccm:directories)"/>
          <xsl:if test="$hastestdir">
            <xsl:value-of select="concat(',build-tests-',@name)"/>
          </xsl:if>
        </xsl:for-each>
      </xsl:attribute>
    </target>
    <target name="runtests" description="Builds all applications and then runs unit tests">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
          <xsl:variable name="hastestdir" select="$application/ccm:directories/ccm:directory[@name='test'] or not($application/ccm:directories)"/>
          <xsl:if test="$hastestdir">
            <xsl:value-of select="concat(',runtests-',@name)"/>
          </xsl:if>
        </xsl:for-each>
      </xsl:attribute>
    </target>
  </xsl:template>
  <xsl:template name="TargetManifest">
    <xsl:param name="target"/>
    <xsl:param name="type"/>
    <xsl:param name="sourcedirectory"/>
    <target name="manifest-{$type}-{$target}" depends="init">
      <if>
        <not>
          <uptodate targetfile="{$target}/${{build.classes.dir}}/${{apps.{$target}.name}}.{$type}.mf">
            <srcfiles includes="{$sourcedirectory}/**" dir="{$target}"/>
          </uptodate>
        </not>
        <then>
          <!-- The pathsep below is the newline character -->
          <pathconvert dirsep="/" pathsep="&#10;" property="{$type}.files.{$target}">
            <path>
              <fileset dir="{$target}">
                <include name="{$sourcedirectory}/**"/>
              </fileset>
            </path>
            <map from="${{basedir}}${{file.separator}}{$target}${{file.separator}}{$sourcedirectory}${{file.separator}}" to=""/>
          </pathconvert>
          <mkdir dir="{$target}/${{build.classes.dir}}"/>
          <echo message="${{{$type}.files.{$target}}}" file="{$target}/${{build.classes.dir}}/${{apps.{$target}.name}}.{$type}.mf"/>
        </then>
      </if>
    </target>
  </xsl:template>
  <xsl:template name="TargetJavadoc">
    <xsl:variable name="name" select="@name"/>
    <xsl:variable name="version" select="@version"/>

    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
      <xsl:variable name="requires" select="$application/ccm:dependencies/ccm:requires"/>
      <target name="javadoc-{$name}" description="Generates the API documentation for the '{$name}' application">
        <xsl:attribute name="depends">
          <xsl:text>init</xsl:text>
          <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
            <xsl:variable name="name" select="@name"/>
            <xsl:variable name="fullname" select="document(concat($name,'/application.xml'),/ccm:project)/ccm:application/@name"/>
            <xsl:for-each select="$requires">
              <xsl:variable name="requiredname" select="@name"/>
              <xsl:if test="$requiredname = $fullname">
                <xsl:value-of select="concat(',build-',$name)"/>
              </xsl:if>
            </xsl:for-each>
          </xsl:for-each>
        </xsl:attribute>
        <mkdir dir="${{app.javadoc.dir}}/{$name}"/>
        <javadoc
          maxmemory="256m"
          author="true"
          version="true"
          use="true"
          destdir="${{app.javadoc.dir}}/{$name}"
          bottom="
          &lt;i&gt;Copyright (c) ${{YEAR}} Red Hat, Inc.
          Corporation.  All Rights Reserved.&lt;/i&gt;
          Generated at ${{TODAY}}:${{TSTAMP}} UTC"
          windowtitle="${{apps.{$name}.prettyname}} API Documentation"
          doctitle="&lt;h1&gt;${{apps.{$name}.prettyname}} API Documentation&lt;/h1&gt;">
          <packageset dir="{$name}/${{src.dir}}"/>
          <classpath refid="server.build.classpath"/>
          <link href="http://java.sun.com/j2se/1.4/docs/api/"/>
          <link href="http://java.sun.com/j2ee/tutorial/api/"/>
        </javadoc>
      </target>
    </xsl:for-each>

    <target name="javadoc-combine-src" depends="init">
      <mkdir dir="${{build.src.dir}}"/>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:variable name="name" select="@name"/>
        <copy todir="${{build.src.dir}}">
          <fileset dir="{$name}/${{src.dir}}"/>
        </copy>
      </xsl:for-each>
    </target>

    <target name="javadoc" depends="init,javadoc-combine-src" description="Generates the combined API documentation">
      <mkdir dir="${{javadoc.dir}}"/>
      <javadoc
        maxmemory="384m"
        author="true"
        version="true"
        use="true"
        destdir="${{javadoc.dir}}"
        bottom="
        &lt;i&gt;Copyright (c) ${{YEAR}} Red Hat, Inc.
        Corporation.  All Rights Reserved.&lt;/i&gt;
        Generated at ${{TODAY}}:${{TSTAMP}} UTC"
        windowtitle="${{app.prettyname}} API Documentation"
        doctitle="&lt;h1&gt;${{app.prettyname}} API Documentation&lt;/h1&gt;">
        <packageset dir="${{build.src.dir}}"/>
        <classpath refid="server.build.classpath"/>
        <link href="http://java.sun.com/j2se/1.4/docs/api/"/>
        <link href="http://java.sun.com/j2ee/tutorial/api/"/>
      </javadoc>
    </target>

    <target name="deploy-test">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
          <xsl:variable name="hastestdir" select="$application/ccm:directories/ccm:directory[@name='test'] or not($application/ccm:directories)"/>
          <xsl:if test="$hastestdir">
            <xsl:text>,build-tests-</xsl:text><xsl:value-of select="@name"/>
          </xsl:if>
        </xsl:for-each>
      </xsl:attribute>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:variable name="name" select="@name"/>
        <mkdir dir="${{deploy.test.dir}}"/>
        <copy todir="${{deploy.test.dir}}">
          <fileset dir=".">
            <include name="{$name}/${{test.dir}}/**"/>
          </fileset>
          <mapper type="glob" to="*" from="{$name}/${{test.dir}}${{file.separator}}*"/>
        </copy>
        <copy todir="${{deploy.test.classes.dir}}">
          <fileset dir="{$name}/${{build.test.classes.dir}}"/>
        </copy>
      </xsl:for-each>
    </target>

  </xsl:template>

  <xsl:template name="TargetMisc">
    <xsl:variable name="name" select="@name"/>

    <target name="kaboom">
      <echo>
             _.-^^---....,,--_
         _--                  --_
        &lt;                        &gt;)
        |                         |
         \._                   _./
            ```--. . , ; .--'''
                  | |   |
               .-=||  | |=-.
               `-=#$%@%$#=-'
                  | ;  :|
         _____.,-#%@$@%#$#~,._____
      </echo>
    </target>

    <target name="larry">
      <!-- Taken from alt.ascii-art - author Tua Xiong -->
      <echo>
        Credits:
        -------

        All hail Larry the CCM Lizard!

          _.--~~~~~~~--._
         /~ ___           ~-._ __
        ( /~   &gt; /-._   _, o  )~\\
         \\    \&lt;    ~&lt;&lt;==\(\_| '''
         _)    '''     \\  `~'
        '''
        CCM Build Configuration tools were written and are
        maintained by :

        Daniel Berrange &lt;berrange@redhat.com&gt;
        Dennis Gregorovic &lt;dgregor@redhat.com&gt;

      </echo>
    </target>

    <target name="pmd-check">
      <available property="pmd.available" classname="net.sourceforge.pmd.ant.PMDTask" classpathref="ccm.base.classpath"/>
    </target>

    <target name="pmd" depends="init,pmd-check" if="pmd.available" description="Runs the PMD utility over the code">
      <mkdir dir="${{pmd.report.dir}}"/>
      <echo message="Generating ${{pmd.report.dir}}/${{pmd.report.file}} for *.java files"/>
      <taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask"  classpathref="ccm.base.classpath"/>
      <pmd rulesetfiles="${{pmd.rulesets}}">
        <formatter type="${{pmd.format}}" toFile="${{pmd.report.dir}}/${{pmd.report.file}}"/>
        <fileset dir=".">
          <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
            <xsl:variable name="name" select="@name"/>
            <include name="{$name}/src/**/*.java"/>
          </xsl:for-each>
        </fileset>
      </pmd>
    </target>

    <target name="init">
      <echo message="${{ant.file}}"/>
      <tstamp><format property="YEAR" pattern="yyyy"/></tstamp>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:variable name="name" select="@name"/>
        <available property="{$name}.test.src.dir.exists" file="{$name}/${{test.src.dir}}" type="dir"/>
        <pathconvert dirsep="/" pathsep=":" property="apps.{$name}.pdl.path" refid="apps.{$name}.pdl.path"/>
        <pathconvert dirsep="/" pathsep=":" property="apps.{$name}.pdl.path.internal" refid="apps.{$name}.pdl.path.internal"/>
      </xsl:for-each>
      <path id="ccm.java.ext.dirs">
        <dirset dir="${{ccm.tools.dir}}">
          <include name="lib/security"/>
        </dirset>
        <pathelement path="${{java.ext.dirs}}"/>
      </path>
      <pathconvert dirsep="/" pathsep=":" property="ccm.java.ext.dirs" refid="ccm.java.ext.dirs"/>
      <condition property="junit.jvmargs" value="-Xdebug -Xrunjdwp:transport=dt_socket,address=${{test.remote.port}},server=y,suspend=y -Xnoagent -Djava.compiler=NONE">
        <and>
          <not><isset property="junit.jvmargs"/></not>
          <isset property="debugtests"/>
        </and>
      </condition>
      <condition property="junit.jvmargs" value="-Djunit.debug=false">
        <not><isset property="junit.jvmargs"/></not>
      </condition>
      <condition property="deploy.dir" value="${{j2ee.webapp.dir}}">
        <and>
          <not><isset property="deploy.dir"/></not>
          <isset property="j2ee.webapp.dir"/>
        </and>
      </condition>
      <condition property="deploy.dir" value="deploy">
        <not><isset property="deploy.dir"/></not>
      </condition>
      <condition property="deploy.conf.dir" value="${{deploy.dir}}/conf">
        <not><isset property="deploy.conf.dir"/></not>
      </condition>
      <condition property="deploy.shared.lib.dir" value="${{deploy.dir}}/webapps/WEB-INF/lib">
        <not><isset property="deploy.shared.lib.dir"/></not>
      </condition>
      <condition property="deploy.private.lib.dir" value="${{deploy.dir}}/webapps/WEB-INF/lib">
        <not><isset property="deploy.private.lib.dir"/></not>
      </condition>
      <condition property="deploy.shared.classes.dir" value="${{deploy.dir}}/webapps/WEB-INF/classes">
        <not><isset property="deploy.shared.classes.dir"/></not>
      </condition>
      <condition property="deploy.system.jars.dir" value="${{deploy.dir}}/webapps/WEB-INF/system">
        <not><isset property="deploy.system.jars.dir"/></not>
      </condition>
      <condition property="deploy.webapp.dir" value="${{deploy.dir}}/webapps">
        <not><isset property="deploy.webapp.dir"/></not>
      </condition>
      <condition property="deploy.war.dir" value="${{deploy.dir}}/webapps">
        <not><isset property="deploy.war.dir"/></not>
      </condition>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="application" select="document(concat(@name,'/application.xml'),/ccm:project)/ccm:application"/>
        <xsl:variable name="shared" select="$application/@shared"/>
        <property value="${{deploy.webapp.dir}}/${{apps.{$name}.webapp.name}}" name="deploy.dir.{$name}"/>
        <xsl:choose>
          <xsl:when test="$shared = 'false'">
            <property value="${{deploy.dir.{$name}}}/WEB-INF/classes"       name="deploy.classes.dir.{$name}"/>
            <property value="${{deploy.dir.{$name}}}/WEB-INF/lib"           name="deploy.lib.dir.{$name}"/>
          </xsl:when>
          <xsl:otherwise>
            <property value="${{deploy.shared.classes.dir}}" name="deploy.classes.dir.{$name}"/>
            <property value="${{deploy.shared.lib.dir}}"     name="deploy.lib.dir.{$name}"/>
          </xsl:otherwise>
        </xsl:choose>
        <property value="${{deploy.dir.{$name}}}/WEB-INF/src"           name="deploy.src.dir.{$name}"/>
        <property value="${{deploy.dir.{$name}}}/WEB-INF/doc"           name="deploy.doc.dir.{$name}"/>
        <property value="${{deploy.dir.{$name}}}/WEB-INF/test"          name="deploy.test.dir.{$name}"/>
        <property value="${{deploy.test.dir.{$name}}}/classes"          name="deploy.test.classes.dir.{$name}"/>
        <property value="${{deploy.dir.{$name}}}/WEB-INF/api"           name="deploy.api.dir.{$name}"/>
        <property value="${{deploy.dir.{$name}}}/WEB-INF/bin"           name="deploy.bin.dir.{$name}"/>
      </xsl:for-each>
      <condition property="log4j.configuration.sysproperty" value="file://${{ccm.home}}/conf/log4j.xml">
        <and>
          <not><isset property="log4j.configuration.sysproperty"/></not>
          <available file="${{ccm.home}}/conf/log4j.xml"/>
        </and>
      </condition>
      <condition property="log4j.configuration.sysproperty" value="file://${{ccm.home}}/conf/log4j.properties">
        <and>
          <not><isset property="log4j.configuration.sysproperty"/></not>
          <available file="${{ccm.home}}/conf/log4j.properties"/>
        </and>
      </condition>
      <condition property="log4j.configuration.sysproperty" value="/log4j.properties">
        <and>
          <not><isset property="log4j.configuration.sysproperty"/></not>
        </and>
      </condition>
    </target>

    <target name="usage" description="Displays usage help">
      <echo message="Run 'ant -projecthelp' for a list of targets.  See ant.apache.org for more information on Ant."/>
    </target>

  </xsl:template>

  <xsl:template name="TargetJavaC">
    <xsl:param name="destdir"/>
    <xsl:param name="srcpathref"/>
    <xsl:param name="classpathref"/>
    <mkdir dir="{$destdir}"/>
    <javac
      debug="${{compile.debug}}"
      optimize="${{compile.optimize}}"
      deprecation="${{compile.deprecation}}"
      verbose="${{compile.verbose}}"
      nowarn="${{compile.nowarn}}"
      destdir="{$destdir}"
      classpathref="{$classpathref}">
      <src refid="{$srcpathref}"/>
    </javac>
  </xsl:template>

  <xsl:template name="LocalGroupingTarget">
    <xsl:param name="targetname"/>
    <xsl:param name="description"/>
    <target name="{$targetname}">
      <xsl:if test="$description">
        <xsl:attribute name="description"><xsl:value-of select="$description"/></xsl:attribute>
      </xsl:if>
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:value-of select="concat(',',$targetname,'-',@name)"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>
  </xsl:template>

  <xsl:template name="GlobalGroupingTarget">
    <xsl:param name="targetname"/>
    <target name="{$targetname}">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
          <xsl:value-of select="concat(',',$targetname,'-',@name)"/>
        </xsl:for-each>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:value-of select="concat(',',$targetname,'-',@name)"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>
  </xsl:template>

</xsl:stylesheet>
