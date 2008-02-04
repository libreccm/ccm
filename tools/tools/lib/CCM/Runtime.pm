# CCM::Runtime
#
# Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
#
# The contents of this file are subject to the CCM Public
# License (the "License"); you may not use this file except in
# compliance with the License. You may obtain a copy of
# the License at http://www.redhat.com/licenses/ccmpl.html
#
# Software distributed under the License is distributed on an "AS
# IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
#
# Daniel Berrange <berrange@redhat.com>
# Dennis Gregorovic <dgregor@redhat.com>
#
# $Id: Runtime.pm 1354 2006-10-31 22:57:58Z sskracic $

=pod

=head1 NAME

CCM::Runtime

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 METHODS

=over 4

=cut

package CCM::Runtime;

use CCM::Util;
use File::Spec;

sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    $self->{verbose} = 0;
    bless $self, $class;
    return $self;
}

sub verbose {
    my $self = shift;
    if (@_) {
        $self->{verbose} = shift;
    }
    return $self->{verbose};
}

sub getCCMHome {
    my $self = shift;
    if (!defined $self->{ccm_home}) {
        $self->{ccm_home} = CCM::Util::getRequiredEnvVariable("CCM_HOME");
    }
    return $self->{ccm_home};
}

sub getCCMConf {
    my $self = shift;
    if (!defined $self->{ccm_conf}) {
        $self->{ccm_conf} = File::Spec->catdir($self->getCCMHome(), 'conf', 'registry');
    }
    return $self->{ccm_conf};
}

sub getCCMDevHome {
    my $self = shift;
    if (!defined $self->{ccm_dev_home}) {
        $self->{ccm_dev_home} = $ENV{'CCM_DEV_HOME'};
    }
    return $self->{ccm_dev_home};
}

sub getCCMToolsHome {
    my $self = shift;
    if (!defined $self->{ccm_tools_home}) {
        $self->{ccm_tools_home} = CCM::Util::getRequiredEnvVariable("CCM_TOOLS_HOME");
    }
    return $self->{ccm_tools_home};
}

sub getJavaHome {
    my $self = shift;
    if (!defined $self->{java_home}) {
        $self->{java_home} = $ENV{'JAVA_HOME'};
        if (!defined $self->{java_home}) {
            my @locs = ();
            if ($^O eq 'MSWin32') {
                push @locs, 'C:\jdk1.3.1_04';
            } else {
                @locs = ('/opt/IBMJava2-141',
                         '/usr/java/j2sdk1.4.2_01/',
                         '/opt/IBMJava2-131',
                         '/opt/IBMJava2-13',
                         '/usr/j2se',
                         '/usr/java/jdk1.3.1',
                         '/usr/local/jdk1.3.1',
                         '/opt/jdk1.3.1',
                         '/usr/java',
                         '/usr/local/java');
            }
            foreach (@locs) {
                if (-d) {
                    $self->{java_home} = $_;
                    last;
                }
            }
        }
        if (!defined $self->{java_home}) {
            CCM::Util::error("JAVA_HOME not set and no Java installation found");
        }
    }
    return $self->{java_home};
}

sub getJavaCommand {
    my $self = shift;
    if (!defined $self->{java_command}) {
        $self->{java_command} = File::Spec->catfile($self->getJavaHome(), "bin", "java");
	my $java_found = 0;
	if ( -x $self->{java_command} ) {
	    $java_found = 1;
	} else {
	    if ($^O eq 'MSWin32') {
		if (opendir(DIR, File::Spec->catdir($self->getJavaHome(), "bin"))) {
		    if (grep { /^java/s && -x File::Spec->catfile($self->getJavaHome(),"bin",$_) } (readdir(DIR))) {
			$java_found = 1;
		    }
		    closedir DIR;
		}
	    }
	}
	if (!$java_found) {
	    CCM::Util::error("Make sure that you have a Java Runtime Environment installed and JAVA_HOME('" . $self->getJavaHome() . "') set correctly.");
        }
    }
    return $self->{java_command};
}

sub getClassPath {
    my $self = shift;
    if (!defined $self->{classpath}) {
        my $classpath = defined $ENV{'CLASSPATH'} ? $ENV{'CLASSPATH'} : "";
        if ( defined $ENV{'ORACLE_HOME'} ) {
            my $jdbclib = File::Spec->catfile($ENV{'ORACLE_HOME'}, 'jdbc', 'lib');
            # prefer JDBC3 driver
            my $jar = File::Spec->catfile($jdbclib, 'ojdbc14.jar');
            if (! -f $jar) {
                $jar = File::Spec->catfile($jdbclib, 'classes12.jar');
            }
            $classpath = CCM::Util::catpath($classpath, $jar);
        }
        my $postgresql_jdbc = $ENV{'PG_JDBC2_LIB'};
        # try universal symlink first (pg8.0-FC4 rpm)
        my @pg_jdbc_locs = ( "/usr/share/java/postgresql.jar",
                             "/usr/share/java/postgresql-jdbc3.jar" );
        # then 8.1 (PGDG rpm)
        my @pg81jars = glob("/usr/share/java/postgresql-8.1*jdbc3.jar");
        push @pg_jdbc_locs, (pop @pg81jars)  if @pg81jars;
        # then 8.0 (PGDG rpm)
        my @pg80jars = glob("/usr/share/java/postgresql-8.0*jdbc3.jar");
        push @pg_jdbc_locs, (pop @pg80jars)  if @pg80jars;
        # then 7.4 (FC3 rpm)
        my @pg74jars = glob("/usr/share/java/pg74*jdbc3.jar");
        push @pg_jdbc_locs, (pop @pg74jars)  if @pg74jars;
        # then RHDB (RHEL3 rpm)
        push @pg_jdbc_locs, ("/usr/share/java/rh-postgresql3.jar",
                             "/usr/share/pgsql/java/rh-postgresql3.jar");
        for my $jar (@pg_jdbc_locs) {
            last if defined $postgresql_jdbc;
            $postgresql_jdbc = $jar if -f $jar;
        }
        if (defined $postgresql_jdbc) {
            $classpath = CCM::Util::catpath($classpath, $postgresql_jdbc);
        }
        $self->{classpath} = $classpath;
    }
    return $self->{classpath};
}

sub getServletJar {
    my $self = shift;
    my $version = shift || "2.3";

    if (!defined $self->{servletjar}->{$version}) {
        my $ROOT = File::Spec->rootdir();
        if ($^O eq 'MSWin32') {
            $ROOT = defined $ENV{'CCM_ZIP_ROOT'} ? $ENV{'CCM_ZIP_ROOT'} : "c:\\ccm\\";
        }
        if ($version eq "2.3") {
            foreach (File::Spec->catfile(${ROOT}, "usr", "share", "java", "ccm-servlet.jar"),
                     File::Spec->catfile(${ROOT}, "usr", "share", "java", "ccm-servlet-2.3.jar"),
                     File::Spec->catfile(${ROOT}, "usr", "share", "java", "servlet.jar"),
                     File::Spec->catfile(${ROOT}, "usr", "share", "java", "servlet-2.3.jar"),
                     File::Spec->catfile(${ROOT}, "usr", "share", "java", "servletapi4.jar"),
                     File::Spec->catfile(${ROOT}, "usr", "share", "java", "servletapi4-4.0.4.jar")) {
                if (-f $_ || -l $_) {
                    $self->{servletjar}->{$version} = $_;
                    last;
                }
            }
        }
    }
    return $self->{servletjar}->{$version};
}

sub getSystemProperties {
    my $self = shift;
    if (!defined $self->{sys_properties}) {
        my $ccm_home = $self->getCCMHome();
        my $ccm_conf = $self->getCCMConf();
        my $ccm_tools_home = $self->getCCMToolsHome();
        my $dbf = defined $ENV{'DOCUMENT_BUILDER_FACTORY'} ? $ENV{'DOCUMENT_BUILDER_FACTORY'} : "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
        my $spf = defined $ENV{'SAX_PARSER_FACTORY'} ? $ENV{'SAX_PARSER_FACTORY'} : "org.apache.xerces.jaxp.SAXParserFactoryImpl";
        my $jtf = defined $ENV{'JAVA_TRANSFORMER_FACTORY'} ? $ENV{'JAVA_TRANSFORMER_FACTORY'} : "com.icl.saxon.TransformerFactoryImpl";
        my $assert = defined $ENV{'CCM_ASSERT_ENABLED'} && $ENV{'CCM_ASSERT_ENABLED'} eq "0" ? "false" : "true";
        my $ccm_jre_ext = defined $ENV{'CCM_JRE_EXT'} ?
            $ENV{'CCM_JRE_EXT'} :
            "-Djava.ext.dirs=" . CCM::Util::catpath (File::Spec->catdir(&getJavaHome(), 'jre', 'lib', 'ext'),
                                                      File::Spec->catdir(&getJavaHome(), 'lib', 'ext'));
        foreach (File::Spec->catdir($ccm_home, 'webapps', 'WEB-INF', 'system')) {
            $ccm_jre_ext = CCM::Util::catpath($ccm_jre_ext, $_) if (-d $_);
        }
        my $log4j = defined $ENV{'LOG4J_PROPERTIES'} ? $ENV{'LOG4J_PROPERTIES'} : "";
        foreach (File::Spec->catfile($ccm_home, 'conf', 'log4j.xml'),
                 File::Spec->catfile($ccm_home, 'conf', 'log4j.properties')) {
	    if ($^O eq 'MSWin32') {
		$log4j = "-Dlog4j.configuration=file:///$_" if ($log4j eq "" && -f $_);
	    } else {
		$log4j = "-Dlog4j.configuration=file://$_" if ($log4j eq "" && -f $_);
	    }
        }
        my @props = ();
        push @props, "-Dccm.home=$ccm_home";
        push @props, "-Dccm.conf=$ccm_conf";
        push @props, "-Dcom.arsdigita.util.Assert.enabled=$assert";
        push @props, "-Djavax.xml.parsers.DocumentBuilderFactory=$dbf";
        push @props, "-Djavax.xml.parsers.SAXParserFactory=$spf";
        push @props, "-Djavax.xml.transform.TransformerFactory=$jtf";
        push @props, "-Djava.protocol.handler.pkgs=com.arsdigita.util.protocol";
        push @props, $ccm_jre_ext;
        push @props, $log4j;
        $self->{sys_properties} = join (' ', @props);
    }
    return $self->{sys_properties};
}

1 # So that the require or use succeeds.

__END__

=back 4

=head1 AUTHORS

=head1 COPYRIGHT

=head1 SEE ALSO

L<perl(1)>

=cut
