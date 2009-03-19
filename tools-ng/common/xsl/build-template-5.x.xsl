<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xalan="http://xml.apache.org/xslt"
  xmlns:ccm="http://ccm.redhat.com/ccm-project"
  exclude-result-prefixes="ccm">

  <xsl:output method="xml"
    encoding="UTF-8"
    indent="yes"
    xalan:indent-amount="4"/>

  <xsl:template match="ccm:project">
    <project name="CCM" default="usage" basedir=".">
      <xsl:call-template name="SharedProperties"/>
      <xsl:call-template name="AppProperties"/>

      <xsl:choose>
        <xsl:when test="/ccm:project/@type != 'styling'">
          <xsl:call-template name="TargetClean"/>
          <xsl:call-template name="TargetBuild"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="TargetCleanLite"/>
          <xsl:call-template name="TargetBuildNoop"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="TargetDeploy"/>
      <xsl:call-template name="TargetJavadoc"/>
      <xsl:call-template name="TargetVerify"/>
      <xsl:choose>
        <xsl:when test="/ccm:project/@type != 'styling'">
          <xsl:call-template name="TargetTest"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="TargetTestNoop"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="TargetMisc"/>
    </project>
  </xsl:template>

  <xsl:template name="SharedProperties">
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:variable name="prettyName"><xsl:value-of select="@prettyName"/></xsl:variable>
    <xsl:variable name="extends"><xsl:value-of select="/ccm:project/@extends"/></xsl:variable>

    <property environment="env"/>
    <property value="${{env.CCM_DIST_HOME}}" name="ccm.dist.dir"/>
    <property value="${{ccm.dist.dir}}/applications" name="ccm.apps.dist.dir"/>
    <property value="${{ccm.dist.dir}}/projects" name="ccm.projects.dist.dir"/>
    <property value="${{env.CCM_CONFIG_HOME}}" name="ccm.config.dir"/>
    <property value="${{env.CCM_DEV_HOME}}" name="ccm.src.dir"/>
    <property value="ant.properties" name="property.file"/>
    <property file="${{property.file}}"/>
    <property name="app.name" value="{$name}"/>
    <property name="app.pretty.name" value="{$prettyName}"/>
    <xsl:choose>
      <xsl:when test="/ccm:project/@type = 'project'">
        <property name="project.name" value="{$name}"/>
      </xsl:when>
      <xsl:when test="/ccm:project/@extends">
        <property name="project.name" value="{$extends}"/>
      </xsl:when>
      <xsl:otherwise>
        <property name="project.name" value=""/>
      </xsl:otherwise>
    </xsl:choose>
    <property value="${{file.separator}}" name="slash"/>
    <property value="build" name="build.dir"/>
    <property value="${{j2ee.webapp.dir}}" name="deploy.dir"/>
    <property value="${{deploy.dir}}/WEB-INF/resources" name="deploy.init.dir"/>
    <property value="${{deploy.dir}}/WEB-INF" name="deploy.web.dir"/>
    <property value="${{deploy.dir}}/src" name="deploy.src.dir"/>
    <property value="${{deploy.dir}}/test" name="deploy.test.dir"/>
    <property value="${{deploy.test.dir}}/classes" name="deploy.test.classes.dir"/>
    <property value="${{env.CCM_CONFIG_LIB_DIR}}" name="ccm.config.lib.dir"/>
    <property value="lib" name="lib.dir"/>
    <property value="etc/lib" name="etclib.dir"/>
    <property value="pdl" name="pdl.dir"/>
    <property value="sql" name="sql.dir"/>
    <property value="src" name="src.dir"/>
    <property value="web" name="web.dir"/>
    <property value="ddl" name="ddl.dir"/>
    <property value="8300" name="test.remote.port"/>
    <property value="false" name="test.sql.verbose"/>
    <property value="true" name="test.sql.continue"/>
    <property value="test" name="test.dir"/>
    <property value="test/sql" name="test.sql.dir"/>
    <property value="test/pdl" name="test.pdl.dir"/>
    <property value="test/src" name="test.src.dir"/>
    <property value="${{build.dir}}/test" name="test.deploy.dir"/>
    <property value="${{build.dir}}/tmp" name="test.tmp.dir"/>
    <property value="${{build.dir}}" name="enterprise.build.dir"/>
    <property value="${{build.dir}}/src" name="build.src.dir"/>
    <property value="${{build.dir}}/classes" name="build.classes.dir"/>
    <property value="${{build.dir}}/sql" name="build.sql.dir"/>
    <property value="${{build.dir}}/tests" name="build.tests.dir"/>
    <property value="${{test.pdl.dir}}" name="build.tests.pdl.dir"/>
    <property value="${{build.tests.dir}}/sql" name="build.tests.sql.dir"/>
    <property value="${{build.tests.sql.dir}}/oracle-se" name="build.tests.sql.dir.db"/>
    <property value="${{etclib.dir}}" name="javacc.home.dir"/>
    <property value="*Suite.class" name="junit.suite"/>
    <property value="" name="junit.test"/>
    <property value="off" name="junit.test.haltonfailure"/>
    <property value="off" name="junit.test.haltonerror"/>
    <property value="false" name="junit.usefail"/>
    <property value="false" name="junit.usecactus"/>
    <property value="plain" name="junit.formatter"/>
    <property value="${{build.dir}}/api" name="javadoc.dir"/>
    <property value="${{build.dir}}/api-apps" name="app.javadoc.dir"/>
    <property value="${{build.dir}}/javadoc-src" name="javadoc.src.dir"/>
    <property value="api" name="javadoc.dist.dir"/>
    <property value="${{deploy.dir}}/api" name="deploy.api.dir"/>
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
    <property value="com.arsdigita.persistence.pdl.PDL" name="verify.pdl.classname"/>
    <property value="com.arsdigita.persistence.metadata.DDLWriter" name="ddl.generator.resource"/>
    <property value="com.arsdigita.persistence.pdl.PDL" name="ddl.generator.classname"/>
    <property value="com.arsdigita.persistence.pdl.TestPDLGenerator" name="test.ddl.generator.classname"/>

    <path id="ccm.base.classpath">
      <fileset dir="${{ccm.config.lib.dir}}">
        <include name="junit*.jar"/>
        <include name="httpunit*.jar"/>
        <include name="servletapi3*.jar"/>
        <include name="servlet-2.2.jar"/>
      </fileset>
      <pathelement path="${{java.class.path}}"/>
    </path>

  </xsl:template>

  <xsl:template name="AppProperties">
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>

      <xsl:call-template name="AppPropertyClassPath">
        <xsl:with-param name="target" select="@name"/>
        <xsl:with-param name="type">build</xsl:with-param>
        <xsl:with-param name="order" select="@buildOrder"/>
      </xsl:call-template>

      <xsl:call-template name="AppPropertyClassPath">
        <xsl:with-param name="target" select="@name"/>
        <xsl:with-param name="type">tests</xsl:with-param>
        <xsl:with-param name="order" select="@buildOrder"/>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:call-template name="AppPropertyClassPath">
      <xsl:with-param name="target">javadoc</xsl:with-param>
      <xsl:with-param name="type">build</xsl:with-param>
      <xsl:with-param name="order">10000</xsl:with-param>
    </xsl:call-template>

    <xsl:call-template name="AppPropertyClassPath">
      <xsl:with-param name="target">verify</xsl:with-param>
      <xsl:with-param name="type">build</xsl:with-param>
      <xsl:with-param name="order">10000</xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="AppPropertyClassPath">
    <xsl:param name="target" select="'default-value'"/>
    <xsl:param name="type" select="'build'"/>
    <xsl:param name="order" select="'default-value'"/>
    <xsl:variable name="extends"><xsl:value-of select="/ccm:project/@extends"/></xsl:variable>
    <xsl:variable name="extendsVersion"><xsl:value-of select="/ccm:project/@extendsVersion"/></xsl:variable>

    <path id="{$target}.{$type}.classpath">
      <pathelement path="${{ccm.config.dir}}/lib/xerces.jar"/>
      <xsl:if test="/ccm:project/@extends">
        <fileset dir="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}/dist/WEB-INF">
          <include name="${{lib.dir}}/*.jar"/>
          <include name="${{lib.dir}}/*.zip"/>
        </fileset>
        <pathelement path="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}/dist/WEB-INF/classes"/>
        <xsl:if test="$type = 'tests'">
          <pathelement path="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}/test/classes"/>
        </xsl:if>
      </xsl:if>

      <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
        <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>
        <fileset dir="${{ccm.apps.dist.dir}}/{$name}-{$version}/dist/WEB-INF">
          <include name="${{lib.dir}}/*.jar"/>
          <include name="${{lib.dir}}/*.zip"/>
        </fileset>
        <pathelement path="${{ccm.apps.dist.dir}}/{$name}-{$version}/dist/WEB-INF/classes"/>
        <xsl:if test="$type = 'tests'">
          <pathelement path="${{ccm.apps.dist.dir}}/{$name}-{$version}/test/classes"/>
        </xsl:if>
      </xsl:for-each>

      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

        <xsl:if test="number(@buildOrder) &lt;= number($order)">
          <fileset dir="./{$name}">
            <include name="${{lib.dir}}/*.jar"/>
            <include name="${{lib.dir}}/*.zip"/>
          </fileset>
          <pathelement path="./{$name}/build/classes/"/>
          <xsl:if test="$type = 'tests'">
            <pathelement path="./{$name}/build/tests/"/>
          </xsl:if>
          <fileset dir="./{$name}">
            <include name="${{etclib.dir}}/*.jar"/>
            <include name="${{etclib.dir}}/*.zip"/>
          </fileset>
        </xsl:if>
      </xsl:for-each>

      <path refid="ccm.base.classpath"/>

    </path>
  </xsl:template>

  <xsl:template name="TargetClean">
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

      <!-- The app clean build hooks -->
      <target depends="init" name="clean-build-{$name}-hook" if="build.hook.{$name}">
        <ant antfile="build-hooks.xml" target="clean-build-hook" dir="{$name}"/>
      </target>

      <!-- The app test hooks -->
      <target depends="init" name="clean-tests-{$name}-hook" if="build.hook.{$name}">
        <ant antfile="build-hooks.xml" target="clean-tests-hook" dir="{$name}"/>
      </target>

      <!-- The app clean build tasks -->
      <target name="clean-build-{$name}" depends="init,clean-build-{$name}-hook">
        <delete dir="{$name}/${{build.dir}}"/>
      </target>

      <!-- The app clean test tasks -->
      <target name="clean-tests-{$name}" depends="init,clean-tests-{$name}-hook">
        <delete>
          <fileset dir="{$name}">
            <include name="TEST-*.txt"/>
            <include name="TEST-*.xml"/>
          </fileset>
        </delete>
      </target>
    </xsl:for-each>

    <target name="clean-build">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,clean-build-</xsl:text>
          <xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>

    <target name="clean-tests">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,clean-tests-</xsl:text>
          <xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>

    <target name="clean-deploy">
      <delete dir="${{deploy.dir}}"/>
    </target>

    <target name="clean" depends="clean-build,clean-tests,clean-deploy"/>
  </xsl:template>

  <xsl:template name="TargetCleanLite">
    <target name="clean-deploy">
      <delete dir="${{deploy.dir}}"/>
    </target>
    <target name="clean" depends="clean-deploy"/>
  </xsl:template>

  <xsl:template name="TargetBuild">
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

    <!-- Compile hooks -->
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
      <target name="compile-{$name}-check">
        <available property="compile.{$name}" file="{$name}/src"/>
      </target>

      <target name="compile-{$name}-hook" if="build.hook.{$name}">
        <ant antfile="build-hooks.xml" target="compile-hook" dir="{$name}"/>
      </target>


      <!-- Compile task -->
      <target name="_compile-{$name}" if="compile.{$name}">
        <mkdir dir="{$name}/${{build.classes.dir}}"/>
        <mkdir dir="{$name}/${{build.src.dir}}"/>
        <javac
          debug="${{compile.debug}}"
          optimize="${{compile.optimize}}"
          deprecation="${{compile.deprecation}}"
          verbose="${{compile.verbose}}"
          nowarn="${{compile.nowarn}}"
          destdir="{$name}/${{build.classes.dir}}">
          <src path="{$name}/${{src.dir}}"/>
          <src path="{$name}/${{build.src.dir}}"/>
          <include name="**/*.java"/>
          <classpath refid="{$name}.build.classpath"/>
        </javac>
      </target>

      <target name="compile-{$name}" depends="init,compile-{$name}-check,compile-{$name}-hook,_compile-{$name}"/>

      <!-- DDL generate task -->
      <target name="generate-sql-{$name}-check">
        <condition property="generate.sql.{$name}">
          <and>
            <available classname="${{ddl.generator.resource}}" classpathref="{$name}.build.classpath"/>
            <available file="{$name}/pdl"/>
          </and>
        </condition>
        <uptodate property="generate.sql.{$name}.uptodate" targetfile="{$name}/${{build.dir}}/.ddl-timestamp">
          <srcfiles includes="${{pdl.dir}}/**/*.pdl" dir="{$name}"/>
        </uptodate>
      </target>

      <target name="copy-sql-{$name}">
        <mkdir dir="{$name}/${{build.sql.dir}}"/>
        <copy todir="{$name}/${{build.sql.dir}}">
          <fileset dir="{$name}">
            <include name="${{sql.dir}}/**/*.sql"/>
          </fileset>
          <mapper type="glob" from="${{sql.dir}}/*" to="*"/>
        </copy>
      </target>

      <target name="generate-sql-{$name}-timestamp">
        <mkdir dir="{$name}/${{build.dir}}"/>
        <echo message="ddl-timestamp" file="{$name}/${{build.dir}}/.ddl-timestamp"/>
      </target>

      <target name="generate-sql-{$name}" depends="init,compile-{$name},copy-sql-{$name},generate-sql-{$name}-check,generate-sql-{$name}-oracle-se,generate-sql-{$name}-postgres,generate-sql-{$name}-timestamp"/>

      <xsl:call-template name="TargetGenerateSQL">
        <xsl:with-param name="database">oracle-se</xsl:with-param>
        <xsl:with-param name="order" select="@buildOrder"/>
      </xsl:call-template>
      <xsl:call-template name="TargetGenerateSQL">
        <xsl:with-param name="database">postgres</xsl:with-param>
        <xsl:with-param name="order" select="@buildOrder"/>
      </xsl:call-template>

      <!-- Copy src task -->
      <target depends="init" name="copy-src-{$name}">
        <copy todir="{$name}/${{build.classes.dir}}">
          <fileset dir="{$name}">
            <include name="${{src.dir}}/**/*.properties"/>
          </fileset>
          <mapper type="glob" from="${{src.dir}}/*" to="*"/>
        </copy>
      </target>

      <!-- Build tasks -->
      <xsl:call-template name="TargetBuildApp">
        <xsl:with-param name="target" select="@name"/>
        <xsl:with-param name="order" select="@buildOrder"/>
      </xsl:call-template>
    </xsl:for-each>


    <target name="build">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,build-</xsl:text>
          <xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>
  </xsl:template>

  <xsl:template name="TargetBuildApp">
    <xsl:param name="target"/>
    <xsl:param name="order" select="'default-value'"/>
    <target name="build-{$target}">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
          <xsl:if test="number(@buildOrder) &lt; number($order)">
            <xsl:text>,build-</xsl:text>
            <xsl:value-of select="@name"/>
          </xsl:if>
        </xsl:for-each>
        <xsl:text>,compile-</xsl:text><xsl:value-of select="$target"/>
        <xsl:text>,generate-sql-</xsl:text><xsl:value-of select="$target"/>
        <xsl:text>,copy-src-</xsl:text><xsl:value-of select="$target"/>
      </xsl:attribute>
    </target>
  </xsl:template>

  <xsl:template name="TargetGenerateSQL">
    <xsl:param name="database"/>
    <xsl:param name="order" select="'default-value'"/>
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:variable name="extends"><xsl:value-of select="@extends"/></xsl:variable>
    <xsl:variable name="extendsVersion"><xsl:value-of select="@extendsVersion"/></xsl:variable>

    <target name="generate-sql-{$name}-{$database}" depends="init" if="generate.sql.{$name}" unless="generate.sql.{$name}.uptodate">
      <mkdir dir="{$name}/${{build.sql.dir}}/${{ddl.dir}}/{$database}"/>
      <java failonerror="yes" classname="${{ddl.generator.classname}}" fork="yes">
        <classpath refid="{$name}.build.classpath"/>
        <arg line="-quiet"/>
        <arg line="-generate-ddl {$name}/${{build.sql.dir}}/${{ddl.dir}}/{$database}"/>
        <arg line="-database {$database}"/>
        <arg line="-path {$name}/${{pdl.dir}}"/>
        <arg line="-sqldir {$name}/${{build.sql.dir}}"/>
        <xsl:call-template name="TargetPdlPath">
          <xsl:with-param name="order" select="$order"/>
        </xsl:call-template>
      </java>
    </target>
  </xsl:template>

  <xsl:template name="TargetPdlPath">
    <xsl:param name="order" select="'default-value'"/>
    <xsl:param name="pathType" select="'library-path'"/>
    <xsl:if test="(count(/ccm:project/ccm:build/ccm:application[number(@buildOrder) &lt; number($order)]) > 0) or (count(/ccm:project/ccm:prebuilt/ccm:application) > 0) or /ccm:project/@extends">
      <xsl:text disable-output-escaping="yes">
        &lt;arg line="</xsl:text>
      <xsl:text>-</xsl:text><xsl:copy-of select="$pathType"/><xsl:text> </xsl:text>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application[number(@buildOrder) &lt; number($order)]">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:value-of select="@name"/>
        <xsl:text>/${pdl.dir}</xsl:text>
        <xsl:if test="1">
          <xsl:text>:</xsl:text>
        </xsl:if>
      </xsl:for-each>
      <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:text>${ccm.apps.dist.dir}/</xsl:text>
        <xsl:value-of select="@name"/><xsl:text>-</xsl:text>
        <xsl:value-of select="@version"/>
        <xsl:text>/dist/WEB-INF/pdl</xsl:text>
        <xsl:if test="1">
          <xsl:text>:</xsl:text>
        </xsl:if>
      </xsl:for-each>
      <xsl:if test="/ccm:project/@extends">
        <xsl:text>${ccm.projects.dist.dir}/</xsl:text>
        <xsl:value-of select="/ccm:project/@extends"/><xsl:text>-</xsl:text>
        <xsl:value-of select="/ccm:project/@extendsVersion"/>
        <xsl:text>/dist/WEB-INF/pdl</xsl:text>
        <xsl:if test="1">
          <xsl:text>:</xsl:text>
        </xsl:if>
      </xsl:if>
      <xsl:text disable-output-escaping="yes">"/&gt;
      </xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template name="TargetBuildNoop">
    <target name="build"/>
  </xsl:template>

  <xsl:template name="TargetVerify">
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

    <target name="verify-pdl" depends="init" unless="pdl.no.verify">
      <java classname="${{verify.pdl.classname}}" failonerror="yes" fork="yes">
        <classpath refid="verify.build.classpath"/>
        <arg line="${{pdl.args}}"/>
        <xsl:call-template name="TargetPdlPath">
          <xsl:with-param name="order">10000</xsl:with-param>
          <xsl:with-param name="pathType">path</xsl:with-param>
        </xsl:call-template>
      </java>
      <echo message="PDL files compiled successfully."/>
    </target>

    <path id="jsp.compiler.classpath">
      <fileset dir="${{env.CATALINA_HOME}}">
        <include name="server/lib/*.jar"/>
        <include name="common/lib/*.jar"/>
      </fileset>
    </path>

    <target name="jsp-compiler-check">
      <condition property="jsp.compiler.available">
        <and>
          <available file="${{env.CATALINA_HOME}}" type="dir"/>
          <available classname="org.apache.jasper.JspC" classpathref="jsp.compiler.classpath"/>
        </and>
      </condition>
    </target>

    <target name="compile-jsp" depends="deploy,jsp-compiler-check" if="jsp.compiler.available">
      <taskdef classname="org.apache.jasper.JspC" name="jasper2">
        <classpath refid="jsp.compiler.classpath"/>
      </taskdef>
      <jasper2
        verbose="1"
        package="com.arsdigita"
        uriroot="${{deploy.dir}}"
        webXmlFragment="${{build.dir}}/generated_web.xml"
        outputDir="${{build.dir}}/jsp-java" />
    </target>

    <target name="build-jsp" depends="compile-jsp" if="jsp.compiler.available">
      <mkdir dir="${{build.dir}}/jsp-classes"/>
      <javac destdir="${{build.dir}}/jsp-classes" verbose="${{compile.verbose}}" deprecation="${{compile.deprecation}}" optimize="${{compile.optimize}}" debug="${{compile.debug}}">
        <src path="${{build.dir}}/jsp-java"/>
        <include name="**/*.java"/>
        <classpath refid="jsp.compiler.classpath"/>
        <classpath refid="verify.build.classpath"/>
      </javac>
    </target>

    <target name="no-build-jsp" unless="jsp.compiler.available">
      <echo message="JSP verification skipped.  The CATALINA_HOME environment variable must point to a Tomcat 4.1 installation."/>
    </target>

    <target name="verify-jsp" depends="build-jsp,no-build-jsp"/>

  </xsl:template>

  <!--
  Here is where we insert additional servlet mappings and declarations into
  web.xml.  First we check to see if the file exist.  Then we load their
  contents.  If the files exist but are empty, then the properties will not get
  set, which is why the loading is split out into a separate target.
  -->
  <xsl:template name="DeployConfigWeb">
    <xsl:param name="name"/>
    <xsl:param name="directory"/>
    <target name="deploy-check-webxml-{$name}">
      <condition property="deploy.webxml.{$name}">
        <and>
          <available file="{$directory}/servlet-declarations.xml"/>
          <available file="{$directory}/servlet-mappings.xml"/>
        </and>
      </condition>
    </target>
    <target name="deploy-load-webxml-{$name}" if="deploy.webxml.{$name}" depends="init,deploy-check-webxml-{$name}">
      <loadfile property="{$name}.webxml.servdec" srcfile="{$directory}/servlet-declarations.xml"/>
      <loadfile property="{$name}.webxml.servmap" srcfile="{$directory}/servlet-mappings.xml"/>
      <condition property="deploy.webxml.loaded.{$name}">
        <and>
          <isset property="{$name}.webxml.servdec"/>
          <isset property="{$name}.webxml.servmap"/>
        </and>
      </condition>
    </target>
    <target name="_deploy-config-web-{$name}" if="deploy.webxml.loaded.{$name}" depends="init,deploy-load-webxml-{$name}">
      <replace
        dir="${{deploy.web.dir}}"
        includes="web.xml.*"
        token="&lt;!-- ADDITIONAL SERVLET DECLARATIONS --&gt;"
        value="&lt;!-- ADDITIONAL SERVLET DECLARATIONS --&gt; ${{{$name}.webxml.servdec}}"/>
      <replace
        dir="${{deploy.web.dir}}"
        includes="web.xml.*"
        token="&lt;!-- ADDITIONAL SERVLET MAPPINGS --&gt;"
        value="&lt;!-- ADDITIONAL SERVLET MAPPINGS --&gt; ${{{$name}.webxml.servmap}}"/>
    </target>
  </xsl:template>

  <xsl:template name="TargetDeploy">
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

    <target name="deploy-web">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,deploy-web-</xsl:text>
          <xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>

    <!-- Deploy hooks -->
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

      <target name="deploy-{$name}-hook" if="build.hook.{$name}">
        <ant antfile="build-hooks.xml" target="deploy-hook" dir="{$name}"/>
      </target>

      <xsl:if test="/ccm:project/@type != 'styling'">
        <!-- Deploy classes -->
        <target name="_deploy-classes-{$name}" depends="build-{$name}" if="deploy.classes.{$name}">
          <copy todir="${{deploy.dir}}/WEB-INF/classes">
            <xsl:if test="@overwrite"><xsl:attribute name="overwrite">yes</xsl:attribute></xsl:if>
            <fileset dir="{$name}/${{build.classes.dir}}">
              <include name="**"/>
            </fileset>
          </copy>
        </target>

        <!-- Deploy libs -->
        <target name="_deploy-lib-{$name}" if="deploy.lib.{$name}">
          <copy todir="${{deploy.dir}}/WEB-INF/lib">
            <xsl:if test="@overwrite"><xsl:attribute name="overwrite">yes</xsl:attribute></xsl:if>
            <fileset dir="{$name}/${{lib.dir}}">
              <include name="**"/>
            </fileset>
          </copy>
        </target>

        <!-- Deploy pdl -->
        <target name="_deploy-pdl-{$name}" if="deploy.pdl.{$name}">
          <copy todir="${{deploy.dir}}/WEB-INF/pdl">
            <xsl:if test="@overwrite"><xsl:attribute name="overwrite">yes</xsl:attribute></xsl:if>
            <fileset dir="{$name}/${{pdl.dir}}">
              <include name="**"/>
            </fileset>
          </copy>
        </target>

        <!-- Deploy sql -->
        <target name="_deploy-sql-{$name}" if="deploy.sql.{$name}" depends="generate-sql-{$name}">
          <copy todir="${{deploy.dir}}/WEB-INF/sql/{$name}">
            <xsl:if test="@overwrite"><xsl:attribute name="overwrite">yes</xsl:attribute></xsl:if>
            <fileset dir="{$name}/${{build.sql.dir}}">
              <include name="**"/>
            </fileset>
          </copy>
        </target>
      </xsl:if>

      <!-- Deploy web -->
      <target name="_deploy-web-{$name}" if="deploy.web.{$name}">
        <copy todir="${{deploy.dir}}">
          <xsl:if test="@overwrite"><xsl:attribute name="overwrite">yes</xsl:attribute></xsl:if>
          <fileset dir="{$name}/${{web.dir}}">
            <include name="**"/>
            <exclude name="**/web.xml*"/>
          </fileset>
        </copy>
      </target>

      <xsl:if test="/ccm:project/@type != 'styling'">
        <!-- Deploy classes wrapper -->
        <target name="deploy-classes-{$name}" depends="deploy-check-{$name},_deploy-classes-{$name}"/>

        <!-- Deploy lib wrapper -->
        <target name="deploy-lib-{$name}" depends="deploy-check-{$name},_deploy-lib-{$name}"/>

        <!-- Deploy pdl wrapper -->
        <target name="deploy-pdl-{$name}" depends="deploy-check-{$name},_deploy-pdl-{$name}"/>

        <!-- Deploy sql wrapper -->
        <target name="deploy-sql-{$name}" depends="deploy-check-{$name},_deploy-sql-{$name}"/>
      </xsl:if>

      <!-- Deploy web wrapper -->
      <target name="deploy-web-{$name}" depends="deploy-check-{$name},_deploy-web-{$name}"/>

      <!-- Check which dirs to deploy -->
      <target name="deploy-check-{$name}">
        <xsl:if test="/ccm:project/@type != 'styling'">
          <available property="deploy.classes.{$name}" file="{$name}/src"/>
          <available property="deploy.pdl.{$name}" file="{$name}/pdl"/>
          <available property="deploy.sql.{$name}" file="{$name}/sql"/>
          <available property="deploy.lib.{$name}" file="{$name}/lib"/>
        </xsl:if>
        <available property="deploy.web.{$name}" file="{$name}/web"/>
      </target>

      <!-- Deploy app -->
      <target name="deploy-{$name}">
        <xsl:attribute name="depends">
          <xsl:text>init</xsl:text>
          <xsl:text>,build-</xsl:text><xsl:value-of select="@name"/>
          <xsl:text>,deploy-</xsl:text><xsl:value-of select="@name"/><xsl:text>-hook</xsl:text>
          <xsl:text>,deploy-check-</xsl:text><xsl:value-of select="@name"/>
          <xsl:if test="/ccm:project/@type != 'styling'">
            <xsl:text>,deploy-classes-</xsl:text><xsl:value-of select="@name"/>
            <xsl:text>,deploy-lib-</xsl:text><xsl:value-of select="@name"/>
            <xsl:text>,deploy-pdl-</xsl:text><xsl:value-of select="@name"/>
            <xsl:text>,deploy-sql-</xsl:text><xsl:value-of select="@name"/>
          </xsl:if>
          <xsl:text>,deploy-web-</xsl:text><xsl:value-of select="@name"/>
        </xsl:attribute>
      </target>
    </xsl:for-each>

    <target name="deploy-config-init-local" depends="init">
      <copy tofile="${{deploy.init.dir}}/enterprise.init" file="enterprise.init-local">
        <xsl:if test="@overwrite"><xsl:attribute name="overwrite">yes</xsl:attribute></xsl:if>
      </copy>
      <copy tofile="${{deploy.init.dir}}/enterprise.init.in" file="enterprise.init.in-local">
        <xsl:if test="@overwrite"><xsl:attribute name="overwrite">yes</xsl:attribute></xsl:if>
      </copy>
    </target>

    <target name="deploy-config-init" depends="init">
      <copy tofile="${{deploy.init.dir}}/enterprise.init" file="enterprise.init">
        <xsl:if test="@overwrite"><xsl:attribute name="overwrite">yes</xsl:attribute></xsl:if>
      </copy>
      <copy tofile="${{deploy.init.dir}}/enterprise.init.in" file="enterprise.init.in">
        <xsl:if test="@overwrite"><xsl:attribute name="overwrite">yes</xsl:attribute></xsl:if>
      </copy>
    </target>

    <target name="deploy-config-webxml-local" depends="init">
      <xsl:if test="/ccm:project/ccm:build/ccm:application">
        <mkdir dir="${{deploy.web.dir}}"/>
        <concat destfile="${{deploy.web.dir}}/servlet-declarations.xml">
          <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
            <xsl:sort select="@buildOrder" data-type="number"/>
            <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
            <fileset dir="{$name}/etc" includes="servlet-declarations.xml"/>
          </xsl:for-each>
        </concat>
        <concat destfile="${{deploy.web.dir}}/servlet-mappings.xml">
          <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
            <xsl:sort select="@buildOrder" data-type="number"/>
            <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
            <fileset dir="{$name}/etc" includes="servlet-mappings.xml"/>
          </xsl:for-each>
        </concat>
      </xsl:if>
    </target>

    <target name="deploy-config-webxml" depends="init,deploy-config-webxml-local">
      <mkdir dir="${{deploy.web.dir}}"/>
      <xsl:if test="/ccm:project/@extends or /ccm:project/ccm:prebuilt/ccm:application">
        <concat destfile="${{deploy.web.dir}}/servlet-declarations.xml" append="true">
          <xsl:if test="/ccm:project/@extends">
            <xsl:variable name="extends"><xsl:value-of select="@extends"/></xsl:variable>
            <xsl:variable name="extendsVersion"><xsl:value-of select="@extendsVersion"/></xsl:variable>
            <fileset dir="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}/etc" includes="servlet-declarations.xml"/>
          </xsl:if>
          <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
            <xsl:sort select="@buildOrder" data-type="number"/>
            <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
            <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>
            <fileset dir="${{ccm.apps.dist.dir}}/{$name}-{$version}/etc" includes="servlet-declarations.xml"/>
          </xsl:for-each>
        </concat>
        <concat destfile="${{deploy.web.dir}}/servlet-mappings.xml" append="true">
          <xsl:if test="/ccm:project/@extends">
            <xsl:variable name="extends"><xsl:value-of select="@extends"/></xsl:variable>
            <xsl:variable name="extendsVersion"><xsl:value-of select="@extendsVersion"/></xsl:variable>
            <fileset dir="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}/etc" includes="servlet-mappings.xml"/>
          </xsl:if>
          <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
            <xsl:sort select="@buildOrder" data-type="number"/>
            <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
            <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>
            <fileset dir="${{ccm.apps.dist.dir}}/{$name}-{$version}/etc" includes="servlet-mappings.xml"/>
          </xsl:for-each>
        </concat>
      </xsl:if>
    </target>

    <target name="_deploy-config-web" depends="init" if="ccm.servlet.engine">
      <copy tofile="${{deploy.web.dir}}/web.xml" file="${{deploy.web.dir}}/web.xml.${{ccm.servlet.engine}}"/>
    </target>

    <xsl:if test="/ccm:project/@extends">
      <xsl:call-template name="DeployConfigWeb">
        <xsl:with-param name="name"><xsl:value-of select="@extends"/></xsl:with-param>
        <xsl:with-param name="directory">
          <xsl:text>${ccm.projects.dist.dir}/</xsl:text>
          <xsl:value-of select="@extends"/>
          <xsl:text>-</xsl:text>
          <xsl:value-of select="@extendsVersion"/>
          <xsl:text>/etc</xsl:text>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:call-template name="DeployConfigWeb">
        <xsl:with-param name="name"><xsl:value-of select="@name"/></xsl:with-param>
        <xsl:with-param name="directory">
          <xsl:text>${ccm.apps.dist.dir}/</xsl:text>
          <xsl:value-of select="@name"/>
          <xsl:text>-</xsl:text>
          <xsl:value-of select="@version"/>
          <xsl:text>/etc</xsl:text>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:call-template name="DeployConfigWeb">
        <xsl:with-param name="name"><xsl:value-of select="@name"/></xsl:with-param>
        <xsl:with-param name="directory"><xsl:value-of select="@name"/><xsl:text>/etc</xsl:text></xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>

    <target name="_deploy-config-web-resetwebxml">
      <delete quiet="true">
        <fileset dir="${{deploy.web.dir}}">
          <include name="web.xml*"/>
        </fileset>
      </delete>
    </target>

    <target name="_deploy-config-web-redeploy-webxml-local">
      <mkdir dir="${{deploy.web.dir}}"/>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
        <copy todir="${{deploy.web.dir}}">
          <fileset dir="{$name}">
            <include name="${{web.dir}}/WEB-INF/web.xml.*"/>
          </fileset>
          <mapper type="glob" from="${{web.dir}}/WEB-INF/*" to="*"/>
        </copy>
      </xsl:for-each>
    </target>

    <target name="_deploy-config-web-redeploy-webxml" depends="_deploy-config-web-redeploy-webxml-local">
      <mkdir dir="${{deploy.web.dir}}"/>
      <xsl:if test="/ccm:project/@extends">
        <xsl:variable name="extends"><xsl:value-of select="@extends"/></xsl:variable>
        <xsl:variable name="extendsVersion"><xsl:value-of select="@extendsVersion"/></xsl:variable>
        <copy todir="${{deploy.web.dir}}">
          <fileset dir="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}/dist/WEB-INF/">
            <include name="web.xml.*"/>
          </fileset>
        </copy>
      </xsl:if>
      <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
        <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>
        <copy todir="${{deploy.web.dir}}">
          <fileset dir="${{ccm.apps.dist.dir}}/{$name}-{$version}/dist/WEB-INF/">
            <include name="web.xml.*"/>
          </fileset>
        </copy>
      </xsl:for-each>
    </target>

    <target name="deploy-config-web">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:text>,_deploy-config-web-resetwebxml</xsl:text>
        <xsl:text>,_deploy-config-web-redeploy-webxml</xsl:text>
        <xsl:if test="/ccm:project/@extends">
          <xsl:text>,_deploy-config-web-</xsl:text><xsl:value-of select="@extends"/>
        </xsl:if>
        <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,_deploy-config-web-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,_deploy-config-web-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
        <xsl:text>,_deploy-config-web</xsl:text>
      </xsl:attribute>
    </target>

    <target name="deploy-config-web-orig">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:text>,_deploy-config-web-resetwebxml</xsl:text>
        <xsl:text>,_deploy-config-web-redeploy-webxml</xsl:text>
      </xsl:attribute>
    </target>

    <target name="deploy-config-web-orig-local">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:text>,_deploy-config-web-resetwebxml</xsl:text>
        <xsl:text>,_deploy-config-web-redeploy-webxml-local</xsl:text>
      </xsl:attribute>
    </target>

    <!-- Deploy config -->
    <target name="deploy-config" depends="deploy-config-init,deploy-config-web"/>

    <target name="deploy-parent">
      <xsl:if test="/ccm:project/@extends">
        <xsl:variable name="extends"><xsl:value-of select="@extends"/></xsl:variable>
        <xsl:variable name="extendsVersion"><xsl:value-of select="@extendsVersion"/></xsl:variable>
        <copy todir="${{deploy.dir}}" preservelastmodified="true">
          <fileset dir="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}/dist">
            <include name="**"/>
          </fileset>
        </copy>
      </xsl:if>
    </target>

    <!-- Deploy prebuilt apps -->
    <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
      <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>

      <target name="deploy-{$name}">
        <copy todir="${{deploy.dir}}" preservelastmodified="true">
          <fileset dir="${{ccm.apps.dist.dir}}/{$name}-{$version}/dist">
            <include name="**"/>
          </fileset>
        </copy>
      </target>
    </xsl:for-each>

    <target name="deploy-global">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:if test="/ccm:project/@extends">
          <xsl:text>,deploy-parent</xsl:text>
        </xsl:if>
        <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,deploy-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>

    <target name="deploy-local">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,deploy-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>

    <!-- Master deploy -->
    <target name="deploy" depends="init,deploy-global,deploy-local,deploy-config"/>
  </xsl:template>

  <xsl:template name="TargetCopyTestPDL">
    <xsl:param name="target" select="'default-value'"/>
    <xsl:param name="order" select="'default-value'"/>
    <target depends="init" name="copy-test-pdl-{$target}">
      <mkdir dir="${{test.deploy.dir}}/WEB-INF/pdl"/>
      <copy todir="${{test.deploy.dir}}/WEB-INF/pdl">
        <fileset dir=".">
          <include name="{$target}/${{test.pdl.dir}}/**"/>
        </fileset>
        <mapper type="glob" to="*" from="{$target}/${{test.pdl.dir}}${{file.separator}}*"/>
      </copy>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application[number(@buildOrder) &lt;= number($order)]">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
        <copy todir="${{test.deploy.dir}}/WEB-INF/pdl">
          <fileset dir=".">
            <include name="{$name}/${{pdl.dir}}/**"/>
          </fileset>
          <mapper type="glob" to="*" from="{$name}/${{pdl.dir}}${{file.separator}}*"/>
        </copy>
      </xsl:for-each>
      <xsl:if test="/ccm:project/@extends">
        <xsl:variable name="extends"><xsl:value-of select="/ccm:project/@extends"/></xsl:variable>
        <xsl:variable name="extendsVersion"><xsl:value-of select="/ccm:project/@extendsVersion"/></xsl:variable>
        <copy todir="${{test.deploy.dir}}/WEB-INF/pdl">
          <fileset dir="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}/dist/WEB-INF">
            <include name="pdl/**"/>
          </fileset>
          <mapper type="glob" to="*" from="pdl${{file.separator}}*"/>
        </copy>
      </xsl:if>
      <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
        <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>
        <copy todir="${{test.deploy.dir}}/WEB-INF/pdl">
          <fileset dir="${{ccm.apps.dist.dir}}/{$name}-{$version}/dist/WEB-INF">
            <include name="pdl/**"/>
          </fileset>
          <mapper type="glob" to="*" from="pdl${{file.separator}}*"/>
        </copy>
      </xsl:for-each>
    </target>
  </xsl:template>

  <xsl:template name="TargetGenerateTestSQL">
    <xsl:param name="target" select="'default-value'"/>
    <xsl:param name="database" select="'default-value'"/>
    <xsl:param name="order" select="'default-value'"/>
    <target if="generate.test.sql.{$target}"
            unless="generate.test.sql.{$target}.uptodate"
            depends="copy-test-sql-{$target},generate-test-sql-{$target}-check"
            name="generate-test-ddl-{$target}-{$database}" >
      <mkdir dir="{$target}/${{build.tests.sql.dir}}/{$database}"/>
      <java failonerror="yes" classname="${{test.ddl.generator.classname}}" fork="yes">
        <classpath refid="{$target}.build.classpath"/>
        <arg line="-quiet"/>
        <arg line="-generate-ddl {$target}/${{build.tests.sql.dir}}/{$database}"/>
        <arg line="-database {$database}"/>
        <arg line="-path {$target}/${{test.pdl.dir}}/com"/>
        <xsl:call-template name="TargetPdlPath">
          <xsl:with-param name="order" select="$order + 1"/>
        </xsl:call-template>
      </java>
    </target>
  </xsl:template>

  <xsl:template name="TargetTest">
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

      <target name="compile-tests-{$name}" depends="init,build-{$name}" if="{$name}.test.src.dir.exists">
        <mkdir dir="{$name}/${{build.tests.dir}}"/>
        <javac
          debug="${{compile.debug}}"
          optimize="${{compile.optimize}}"
          deprecation="${{compile.deprecation}}"
          verbose="${{compile.verbose}}"
          destdir="{$name}/${{build.tests.dir}}">
          <src path="{$name}/${{test.src.dir}}"/>
          <include name="**/*.java"/>
          <classpath refid="{$name}.tests.classpath"/>
        </javac>
      </target>

      <target depends="init" name="copy-test-src-{$name}">
        <mkdir dir="{$name}/${{build.tests.dir}}"/>
        <copy todir="{$name}/${{build.tests.dir}}">
          <fileset dir=".">
            <include name="{$name}/${{test.src.dir}}/**"/>
            <exclude name="{$name}/${{test.src.dir}}/**/*.java"/>
          </fileset>
          <mapper type="glob" to="*" from="{$name}/${{test.src.dir}}${{file.separator}}*"/>
        </copy>
      </target>

      <target depends="init" name="copy-test-sql-{$name}">
        <mkdir dir="{$name}/${{build.tests.sql.dir}}"/>
        <copy todir="{$name}/${{build.tests.sql.dir}}">
          <fileset dir="{$name}">
            <include name="${{test.sql.dir}}/**/*.sql"/>
          </fileset>
          <mapper type="glob" to="*" from="${{test.sql.dir}}${{file.separator}}*"/>
        </copy>
      </target>

      <target name="generate-test-sql-{$name}-check">
        <condition property="generate.test.sql.{$name}">
          <and>
            <available classname="${{ddl.generator.resource}}" classpathref="{$name}.build.classpath"/>
            <available file="{$name}/test/pdl/com"/>
          </and>
        </condition>
        <uptodate property="generate.test.sql.{$name}.uptodate" targetfile="{$name}/${{build.tests.dir}}/.ddl-timestamp">
          <srcfiles includes="${{test.pdl.dir}}/com/**/*.pdl" dir="{$name}"/>
        </uptodate>
      </target>

      <target name="generate-test-sql-{$name}-timestamp">
        <mkdir dir="{$name}/${{build.tests.dir}}"/>
        <echo message="ddl-timestamp" file="{$name}/${{build.tests.dir}}/.ddl-timestamp"/>
      </target>

      <target name="generate-test-ddl-{$name}" depends="generate-test-ddl-{$name}-oracle-se,generate-test-ddl-{$name}-postgres,generate-test-sql-{$name}-timestamp"/>

      <xsl:call-template name="TargetGenerateTestSQL">
        <xsl:with-param name="target" select="$name"/>
        <xsl:with-param name="database">oracle-se</xsl:with-param>
        <xsl:with-param name="order" select="@buildOrder"/>
      </xsl:call-template>

      <xsl:call-template name="TargetGenerateTestSQL">
        <xsl:with-param name="target" select="$name"/>
        <xsl:with-param name="database">postgres</xsl:with-param>
        <xsl:with-param name="order" select="@buildOrder"/>
      </xsl:call-template>

      <xsl:call-template name="TargetCopyTestPDL">
        <xsl:with-param name="target" select="@name"/>
        <xsl:with-param name="order" select="@buildOrder"/>
      </xsl:call-template>

      <target name="build-tests-{$name}" depends="compile-tests-{$name},copy-test-src-{$name}"/>

      <target name="deploy-test-webapp-{$name}">
        <antcall target="deploy">
          <param name="deploy.dir" value="${{test.deploy.dir}}"/>
          <param name="deploy.init.dir" value="${{test.deploy.dir}}/WEB-INF/resources"/>
          <param name="deploy.web.dir" value="${{test.deploy.dir}}/WEB-INF"/>
        </antcall>
      </target>

      <target name="runtests-{$name}" depends="build-tests-{$name},copy-test-pdl-{$name},generate-test-ddl-{$name},deploy-test-webapp-{$name}">

        <mkdir dir="{$name}/${{test.tmp.dir}}"/>
        <junit
          printsummary="yes"
          fork="yes"
          haltonfailure="${{junit.test.haltonfailure}}"
          haltonerror="${{junit.test.haltonerror}}"
          dir="{$name}">
          <classpath refid="{$name}.tests.classpath"/>
          <classpath path="{$name}/${{build.tests.dir}}"/>
          <classpath path="{$name}/${{build.tests.pdl.dir}}"/>
          <formatter type="${{junit.formatter}}"/>
          <sysproperty key="j2ee.webapp.dir" value="${{deploy.dir}}"/>
          <sysproperty key="junit.usefail" value="${{junit.usefail}}"/>
          <sysproperty key="junit.test" value="${{junit.test}}"/>
          <sysproperty key="junit.usecactus" value="${{junit.usecactus}}"/>
          <sysproperty key="sql.continue" value="${{test.sql.continue}}"/>
          <sysproperty key="sql.verbose" value="${{test.sql.verbose}}"/>
          <sysproperty key="test.base.dir" value="${{env.CCM_DEV_HOME}}/{$name}/${{build.tests.dir}}"/>
          <sysproperty key="test.initscript" value="${{test.initscript}}"/>
          <sysproperty key="test.server.url" value="${{test.server.url}}"/>
          <sysproperty key="test.sql.dir" value="${{env.CCM_DEV_HOME}}/{$name}/${{build.tests.sql.dir.db}}"/>
          <sysproperty key="test.tmp.dir" value="${{env.CCM_DEV_HOME}}/{$name}/${{test.tmp.dir}}"/>
          <sysproperty key="test.webapp.dir" value="${{env.CCM_DEV_HOME}}/${{test.deploy.dir}}"/>
          <batchtest todir="{$name}">
            <fileset dir="{$name}/${{build.tests.dir}}">
              <include name="**/${{junit.suite}}"/>
            </fileset>
          </batchtest>
        </junit>
      </target>


      <target name="debugtests-{$name}" depends="build-tests-{$name},copy-test-pdl-{$name}">
        <mkdir dir="{$name}/${{test.tmp.dir}}"/>
        <junit
          printsummary="yes"
          fork="yes"
          haltonfailure="${{junit.test.haltonfailure}}"
          haltonerror="${{junit.test.haltonerror}}"
          dir="{$name}">
          <classpath refid="{$name}.build.classpath"/>
          <classpath path="{$name}/${{build.tests.dir}}"/>
          <classpath path="{$name}/${{build.tests.pdl.dir}}"/>
          <jvmarg value="-Xdebug"/>
          <jvmarg value="-Xrunjdwp:transport=dt_socket,address=${{test.remote.port}},server=y,suspend=y"/>
          <jvmarg value="-Xnoagent"/>
          <jvmarg value="-Djava.compiler=NONE"/>
          <formatter type="${{junit.formatter}}"/>
          <sysproperty key="j2ee.webapp.dir" value="${{deploy.dir}}"/>
          <sysproperty key="junit.usefail" value="${{junit.usefail}}"/>
          <sysproperty key="junit.test" value="${{junit.test}}"/>
          <sysproperty key="junit.usecactus" value="${{junit.usecactus}}"/>
          <sysproperty key="sql.continue" value="${{test.sql.continue}}"/>
          <sysproperty key="sql.verbose" value="${{test.sql.verbose}}"/>
          <sysproperty key="test.base.dir" value="{$name}/${{build.tests.dir}}"/>
          <sysproperty key="test.initscript" value="${{test.initscript}}"/>
          <sysproperty key="test.server.url" value="${{test.server.url}}"/>
          <sysproperty key="test.sql.dir" value="{$name}/${{build.tests.sql.dir.db}}"/>
          <sysproperty key="test.tmp.dir" value="{$name}/${{test.tmp.dir}}"/>
          <sysproperty key="test.webapp.dir" value="${{env.CCM_DEV_HOME}}/${{test.deploy.dir}}"/>
          <batchtest>
            <fileset dir="{$name}/${{build.tests.dir}}">
              <include name="**/${{junit.suite}}"/>
            </fileset>
          </batchtest>
        </junit>
      </target>

    </xsl:for-each>

    <target name="build-tests">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,build-tests-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>

    <target name="runtests">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,runtests-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>

    <target name="debugtests">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,debugtests-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>
    </target>
  </xsl:template>

  <xsl:template name="TargetTestNoop">
    <target name="runtests"/>
    <target name="debugtests"/>
  </xsl:template>


  <xsl:template name="TargetJavadoc">
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>
    <xsl:variable name="extends"><xsl:value-of select="/ccm:project/@extends"/></xsl:variable>
    <xsl:variable name="extendsVersion"><xsl:value-of select="/ccm:project/@extendsVersion"/></xsl:variable>

    <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
      <xsl:variable name="prettyName"><xsl:value-of select="@prettyName"/></xsl:variable>

      <target name="combine-src-{$name}" depends="init" if="{$name}.src.dir.exists">
        <mkdir dir="${{javadoc.src.dir}}/{$name}"/>
        <copy todir="${{javadoc.src.dir}}/{$name}">
          <fileset dir="{$name}/${{src.dir}}">
            <include name="**"/>
          </fileset>
        </copy>
      </target>

      <target name="_javadoc-{$name}" depends="combine-src-{$name}" if="{$name}.src.dir.exists">
        <mkdir dir="${{app.javadoc.dir}}/{$name}"/>
        <javadoc
          maxmemory="134217728"
          author="true"
          version="true"
          use="true"
          destdir="${{app.javadoc.dir}}/{$name}"
          bottom="
          &lt;i&gt;Copyright (c) ${{YEAR}} Red Hat, Inc.
          Corporation.  All Rights Reserved.&lt;/i&gt;
          Generated at ${{TODAY}}:${{TSTAMP}} UTC"
          windowtitle="{$prettyName} API Documentation"
          doctitle="&lt;h1&gt;{$prettyName} API Documentation&lt;/h1&gt;">
          <packageset dir="${{javadoc.src.dir}}/{$name}">
            <include name="**" />
          </packageset>
          <classpath refid="javadoc.build.classpath"/>
          <link href="http://java.sun.com/j2se/1.3/docs/api/"/>
          <link href="http://java.sun.com/j2ee/tutorial/api/"/>
        </javadoc>
      </target>

      <target name="__javadoc-{$name}" unless="{$name}.src.dir.exists">
        <echo message="Javadoc skipped because there are no source files"/>
      </target>

      <target name="javadoc-{$name}" depends="_javadoc-{$name},__javadoc-{$name}"/>
    </xsl:for-each>

    <target name="combine-src" depends="init">
      <mkdir dir="${{build.dir}}"/>
      <xsl:if test="/ccm:project/@type != 'application'">
        <xsl:if test="/ccm:project/@extends">
          <copy todir="${{build.dir}}">
            <fileset dir="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}">
              <include name="src/**"/>
            </fileset>
          </copy>
        </xsl:if>
        <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
          <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>
          <copy todir="${{build.dir}}">
            <fileset dir="${{ccm.apps.dist.dir}}/{$name}-{$version}">
              <include name="src/**"/>
            </fileset>
          </copy>
        </xsl:for-each>
      </xsl:if>
      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
        <copy todir="${{build.dir}}" includeEmptyDirs="false">
          <fileset dir="{$name}">
            <include name="${{src.dir}}/**"/>
          </fileset>
        </copy>
      </xsl:for-each>
      <available property="build.src.dir.exists" file="${{build.src.dir}}" type="dir"/>
    </target>

    <target name="_javadoc" depends="combine-src" if="build.src.dir.exists">
      <mkdir dir="${{javadoc.dir}}"/>
      <javadoc
        maxmemory="134217728"
        author="true"
        version="true"
        use="true"
        destdir="${{javadoc.dir}}"
        bottom="
        &lt;i&gt;Copyright (c) ${{YEAR}} Red Hat, Inc.
        Corporation.  All Rights Reserved.&lt;/i&gt;
        Generated at ${{TODAY}}:${{TSTAMP}} UTC">
        <xsl:attribute name="windowtitle">
          <xsl:value-of select="/ccm:project/@prettyName"/><xsl:text> API Documentation</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="doctitle">
          <xsl:text>&lt;h1&gt;</xsl:text><xsl:value-of select="/ccm:project/@prettyName"/><xsl:text> API Documentation&lt;/h1&gt;</xsl:text>
        </xsl:attribute>
        <packageset dir="${{build.src.dir}}">
          <include name="**" />
        </packageset>
        <classpath refid="javadoc.build.classpath"/>
        <link href="http://java.sun.com/j2se/1.3/docs/api/"/>
        <link href="http://java.sun.com/j2ee/tutorial/api/"/>
      </javadoc>
    </target>

    <target name="__javadoc" unless="build.src.dir.exists">
      <echo message="Javadoc skipped because there are no source files"/>
    </target>

    <target name="javadoc" depends="_javadoc,__javadoc"/>

    <target name="deploy-api-nodeps" depends="">
      <copy todir="${{deploy.api.dir}}">
        <fileset dir="${{javadoc.dir}}">
          <include name="**"/>
        </fileset>
      </copy>
    </target>

    <!-- Nasty hack, we have the -nodeps version so we can invoke
    it separately from javadoc without regenerating all the docs -->
    <target name="deploy-api" depends="javadoc,deploy-api-nodeps"/>

    <target name="deploy-src" depends="combine-src">
      <copy todir="${{deploy.src.dir}}">
        <fileset dir="${{build.src.dir}}">
          <include name="**"/>
        </fileset>
      </copy>
    </target>

    <target name="deploy-test">
      <xsl:attribute name="depends">
        <xsl:text>init</xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>
          <xsl:text>,build-tests-</xsl:text><xsl:value-of select="@name"/>
        </xsl:for-each>
      </xsl:attribute>


      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

        <mkdir dir="${{deploy.test.dir}}"/>

        <copy todir="${{deploy.test.dir}}">
          <fileset dir=".">
            <include name="{$name}/${{test.dir}}/**"/>
          </fileset>
          <mapper type="glob" to="*" from="{$name}/${{test.dir}}${{file.separator}}*"/>
        </copy>

        <copy todir="${{deploy.test.classes.dir}}">
          <fileset dir="{$name}/${{build.tests.dir}}">
            <include name="**"/>
          </fileset>
        </copy>
      </xsl:for-each>
    </target>

  </xsl:template>

  <xsl:template name="TargetMisc">
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>

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

    <target name="make-init-local">
      <java classname="com.redhat.ccm.config.MakeInit"
        classpath="${{ccm.config.dir}}/classes:${{ccm.config.dir}}/lib/jakarta-oro-2.0.4.jar">
        <arg value="config.vars"/>
        <arg value="enterprise.init-local"/>
        <arg value="enterprise.init.in-local"/>
        <xsl:call-template name="TargetMiscInitFiles">
          <xsl:with-param name="local" select="1"/>
        </xsl:call-template>
      </java>
    </target>

    <target name="make-init">
      <java classname="com.redhat.ccm.config.MakeInit"
        classpath="${{ccm.config.dir}}/classes:${{ccm.config.dir}}/lib/jakarta-oro-2.0.4.jar">
        <arg value="config.vars"/>
        <arg value="enterprise.init"/>
        <arg value="enterprise.init.in"/>
        <xsl:call-template name="TargetMiscInitFiles"/>
      </java>
    </target>

    <target name="backup-config" if="config.vars.exists" depends="init">
      <copy file="config.vars" tofile="config.vars.bak" failonerror="false" preservelastmodified="true"/>
    </target>

    <target name="make-config-local" depends="backup-config">
      <java classname="com.redhat.ccm.config.MakeConfig"
        classpath="${{ccm.config.dir}}/classes:${{ccm.config.dir}}/lib/jakarta-oro-2.0.4.jar">
	    <sysproperty key="projectVars" value="project-name,${{project.name}}"/>
        <arg value="config.vars"/>
        <xsl:call-template name="TargetMiscInitFiles">
          <xsl:with-param name="local" select="1"/>
        </xsl:call-template>
      </java>
    </target>

    <target name="make-config" depends="backup-config">
      <java classname="com.redhat.ccm.config.MakeConfig"
        classpath="${{ccm.config.dir}}/classes:${{ccm.config.dir}}/lib/jakarta-oro-2.0.4.jar">
	    <sysproperty key="projectVars" value="project-name,${{project.name}}"/>
        <arg value="config.vars"/>
        <xsl:call-template name="TargetMiscInitFiles"/>
      </java>
    </target>

    <target name="update-config">
      <move file="config.vars" tofile="config.vars.orig"/>
      <java classname="com.redhat.ccm.config.IntegrateConfig"
        classpath="${{ccm.config.dir}}/classes:${{ccm.config.dir}}/lib/jakarta-oro-2.0.4.jar">
	    <sysproperty key="projectVars" value="project-name,${{project.name}}"/>
        <arg value="config.vars.orig"/>
        <arg value="config.vars"/>
        <xsl:call-template name="TargetMiscInitFiles"/>
      </java>
    </target>

    <target name="pmd-check">
      <available property="pmd.available" classname="net.sourceforge.pmd.ant.PMDTask" classpathref="ccm.base.classpath"/>
    </target>

    <target name="pmd" depends="pmd-check" if="pmd.available">
      <mkdir dir="${{pmd.report.dir}}"/>
      <echo message="Generating ${{pmd.report.dir}}/${{pmd.report.file}} for *.java files"/>
      <taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask"  classpathref="ccm.base.classpath"/>
      <pmd rulesetfiles="${{pmd.rulesets}}">
        <formatter type="${{pmd.format}}" toFile="${{pmd.report.dir}}/${{pmd.report.file}}"/>
        <fileset dir=".">
          <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
            <xsl:sort select="@buildOrder" data-type="number"/>
            <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
            <include name="{$name}/src/**/*.java"/>
          </xsl:for-each>
        </fileset>
      </pmd>
    </target>

    <target name="fail-if-properties-not-set" unless="necessary.properties.set">
      <echo message="One or more necessary build properties were not set.  Make
        sure that the CCM_DEV_HOME environment variable is set correctly and that a
        valid ant.properties file exists there."/>
    </target>

    <target name="check-necessary-properties-set">
      <condition property="necessary.properties.set">
        <and>
          <isset property="j2ee.webapp.dir"/>
          <isset property="ccm.servlet.engine"/>
          <isset property="test.initscript"/>
          <isset property="test.server.url"/>
          <isset property="test.server.cactusURL"/>
        </and>
      </condition>
    </target>

    <target name="init" depends="check-necessary-properties-set,fail-if-properties-not-set">
      <echo message="${{ant.file}}"/>
      <tstamp>
        <format property="YEAR" pattern="yyyy"/>
      </tstamp>

      <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
        <available property="build.hook.{$name}" file="{$name}/build-hooks.xml"/>
        <available property="{$name}.test.src.dir.exists" file="{$name}/${{test.src.dir}}" type="dir"/>
        <available property="{$name}.src.dir.exists" file="{$name}/${{src.dir}}" type="dir"/>
      </xsl:for-each>
      <available property="config.vars.exists" file="config.vars"/>
    </target>

    <target name="usage">
      <echo>
        <xsl:text>
    ant [usage]      - ant with no arguments prints this message


Shared Targets

    ant build            - builds all java files across all applications
    ant deploy           - builds and deploys all applications, also deploys
                           prebuilt applications and config files
    ant deploy-config    - deploy configuration files
    ant deploy-prebuilt  - deploy prebuilt applications

    ant clean            - cleans out the build  environment and webapp dir
    ant runtests         - builds all applications and then runs unit tests

    ant make-config      - generate the master configuration file config.vars
    ant update-config    - add any new config options to config.vars
    ant make-init        - generate enterprise.init file substituting from config.vars

    ant verify-pdl       - Verify that the PDL files compile. Requires CCM Core 5.2 or greater.
    ant verify-jsp       - Verify that JSP files compile.  Requires Tomcat 4.1

    ant javadoc          - generate the combined API documentation.
    ant deploy-api       - deploy the API documentation to the webapp root

        </xsl:text>
        <xsl:for-each select="/ccm:project/ccm:build/ccm:application">
          <xsl:sort select="@buildOrder" data-type="number"/>

          <xsl:text>


          </xsl:text>
          <xsl:value-of select="@name"/><xsl:text> Targets</xsl:text>
          <xsl:text>

    ant compile-</xsl:text><xsl:value-of select="@name"/><xsl:text>
    ant build-</xsl:text><xsl:value-of select="@name"/><xsl:text>

    ant deploy-</xsl:text><xsl:value-of select="@name"/><xsl:text>
    ant deploy-classes-</xsl:text><xsl:value-of select="@name"/><xsl:text>
    ant deploy-pdl-</xsl:text><xsl:value-of select="@name"/><xsl:text>
    ant deploy-sql-</xsl:text><xsl:value-of select="@name"/><xsl:text>
    ant deploy-lib-</xsl:text><xsl:value-of select="@name"/><xsl:text>
    ant deploy-web-</xsl:text><xsl:value-of select="@name"/><xsl:text>

    ant clean-build-</xsl:text><xsl:value-of select="@name"/><xsl:text>
    ant clean-tests-</xsl:text><xsl:value-of select="@name"/><xsl:text>

    ant runtests-</xsl:text><xsl:value-of select="@name"/><xsl:text>
    ant debugtests-</xsl:text><xsl:value-of select="@name"/><xsl:text>

    ant javadoc-</xsl:text><xsl:value-of select="@name"/><xsl:text>

        </xsl:text>
      </xsl:for-each>
      </echo>
    </target>
  </xsl:template>

  <xsl:template name="TargetMiscInitFiles">
    <xsl:param name="local" select="0"/>
    <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:variable name="extends"><xsl:value-of select="/ccm:project/@extends"/></xsl:variable>
    <xsl:variable name="extendsVersion"><xsl:value-of select="/ccm:project/@extendsVersion"/></xsl:variable>
    <xsl:variable name="initFile"><xsl:value-of select="@initFile"/></xsl:variable>
    <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>

    <xsl:if test="not($local)">
      <xsl:if test="/ccm:project/@extends">
        <xsl:choose>
          <xsl:when test="not(/ccm:project/@initFile)">
            <arg value="${{ccm.projects.dist.dir}}/{$extends}-{$extendsVersion}/etc/enterprise.init.in"/>
          </xsl:when>
          <xsl:when test="@initFile != 'none'">
            <arg value="{$initFile}"/>
          </xsl:when>
        </xsl:choose>
      </xsl:if>
      <xsl:for-each select="/ccm:project/ccm:prebuilt/ccm:application" >
        <xsl:sort select="@buildOrder" data-type="number"/>
        <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
        <xsl:variable name="version"><xsl:value-of select="@version"/></xsl:variable>
        <xsl:variable name="initFile"><xsl:value-of select="@initFile"/></xsl:variable>
        <xsl:choose>
          <xsl:when test="not(@initFile)">
            <arg value="${{ccm.apps.dist.dir}}/{$name}-{$version}/etc/enterprise.init.in"/>
          </xsl:when>
          <xsl:when test="@initFile != 'none'">
            <arg value="{$initFile}"/>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
    </xsl:if>
    <xsl:for-each select="/ccm:project/ccm:build/ccm:application" >
      <xsl:sort select="@buildOrder" data-type="number"/>
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
      <xsl:variable name="initFile"><xsl:value-of select="@initFile"/></xsl:variable>
      <xsl:choose>
        <xsl:when test="not(@initFile)">
          <arg value="{$name}/etc/enterprise.init.in"/>
        </xsl:when>
        <xsl:when test="@initFile != 'none'">
          <arg value="{$initFile}"/>
        </xsl:when>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
