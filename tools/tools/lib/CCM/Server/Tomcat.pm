# CCM::Server::Tomcat
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
# $Id: Tomcat.pm 1122 2006-04-26 21:17:49Z apevec $

=pod

=head1 NAME

CCM::Server::Tomcat

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 METHODS

=over 4

=cut

package CCM::Server::Tomcat;

use strict;
use vars qw(@ISA);
use CCM::Server;
use CCM::Util;
use File::Spec;

@ISA = qw(CCM::Server);

sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my $self = $class->SUPER::new(@_);

    $self->{bootstrap_class} = 'com.arsdigita.tools.catalina.startup.Bootstrap';
    $self->{verbose} = 0;

    bless $self, $class;
    return $self;
}

sub name {
    my $self = shift;
    return "tomcat";
}

sub windowsInstall {
    my $self = shift;
    return if $self->windowsServiceInstalled();
    my $config = File::Spec->catfile($self->getCCMHome(), 'conf', 'server.xml');
    my $command = File::Spec->catfile ($self->getTomcatHome(), 'bin', 'tomcat.exe');
    $command .= " -install " . $self->getServiceName();
    $command .= " " . File::Spec->catfile($self->getJavaHome(), 'jre', 'bin', 'server', 'jvm.dll');
    $command .= " -Djava.class.path=" . CCM::Util::catpath(File::Spec->catfile($self->getTomcatHome(),'bin','bootstrap.jar'),
							   File::Spec->catfile($self->getJavaHome(),'lib','tools.jar'),
							   File::Spec->catdir($self->getCCMToolsHome(),'server','tomcat','classes'));
    $command .= " " . $self->getSystemProperties();
    $command .= " -start $self->{bootstrap_class} -params -config $config start";
    $command .= " -stop $self->{bootstrap_class} -params -config $config stop";
    $command .= " -out " . File::Spec->catfile($self->getCCMHome(), 'logs', 'catalina-stdout.log');
    $command .= " -err " . File::Spec->catfile($self->getCCMHome(), 'logs', 'catalina-stderr.log');
    $self->run($command);
    open (OUT, "> " . $self->windowsServiceLockFile())
        or CCM::Util::error("could not open " . $self->windowsServiceLockFile());
    print OUT $self->getServiceName();
    close OUT;
}

sub windowsUninstall {
    my $self = shift;
    return unless $self->windowsServiceInstalled();
    my $command = File::Spec->catfile ($self->getTomcatHome(), 'bin', 'tomcat.exe');
    $command .= " -uninstall " . $self->getServiceName();
    $self->windowsStop();
    $self->run($command);
    unlink $self->windowsServiceLockFile();
}

sub start {
    my $self = shift;
    my $command = $self->getCommandLine() . " start";
    if (@{$self->args()}) {
        $command .= " " . join(" ", @{$self->args()});
    }
    if (@_) {
        $command .= " " . join(" ", @_);
    }
    $command .= " >> " . File::Spec->catfile($self->getCCMHome(), 'logs', 'catalina-stdout.log');
    $command .= " 2>> " . File::Spec->catfile($self->getCCMHome(), 'logs', 'catalina-stderr.log');
    $command .= " &";
    CCM::Util::setuser();
    $self->runExec($command);
}

sub stop {
    my $self = shift;
    my $command = $self->getCommandLine() . " stop";
    if (@{$self->args()}) {
        $command .= " " . join(" ", @{$self->args()});
    }
    if (@_) {
        $command .= " " . join(" ", @_);
    }
    $command .= " >> " . File::Spec->catfile($self->getCCMHome(), 'logs', 'catalina-stdout.log');
    $command .= " 2>> " . File::Spec->catfile($self->getCCMHome(), 'logs', 'catalina-stderr.log');
    CCM::Util::setuser();
    $self->runExec($command);
}

sub getCommandLine {
    my $self = shift;
    my $log_dir = File::Spec->catdir($self->getCCMHome(), 'logs');
    my $server_root = $self->getCCMHome();
    my $classpath = $self->getClassPath();
    my $tomcat_home = $self->getTomcatHome();
    my $system_properties = $self->getSystemProperties();
    my $catalina_opts = defined $ENV{'CATALINA_OPTS'} ? $ENV{'CATALINA_OPTS'} : "";
    my $java_opts = defined $ENV{'JAVA_OPTS'} ? $ENV{'JAVA_OPTS'} :
        ( defined $ENV{'JRE_ARGS'} ?
          $ENV{'JRE_ARGS'} :
          "" );
    my $command = File::Spec->catfile ($self->getJavaHome(), 'bin', 'java');
    if ($classpath ne "") {
        $command .= " -classpath $classpath";
    }
    $command .= " $java_opts $catalina_opts $system_properties $self->{bootstrap_class}";
    $command .= " -config " . File::Spec->catfile($self->getCCMHome(), 'conf', 'server.xml');
    return $command;
}

sub getTomcatHome {
    my $self = shift;
    if (!defined $self->{tomcat_home}) {
        $self->{tomcat_home} = $ENV{'TOMCAT_HOME'};
        if (!defined $self->{tomcat_home}) {
            if ( -d '/usr/share/tomcat' ) {
                $self->{tomcat_home} = '/usr/share/tomcat';
            } elsif ( -d '/opt/tomcat/latest' ) {
                $self->{tomcat_home} = '/opt/tomcat/latest';
            } else {
                $self->{tomcat_home} = (reverse CCM::Util::versionSort (grep { -d $_ || -l $_ } glob("/opt/tomcat/4*")))[0];
            }
            if (!defined $self->{tomcat_home}) {
                CCM::Util::error ("TOMCAT_HOME not set and no Tomcat installation found", 3);
            }
        }
    }
    return $self->{tomcat_home};
}

sub getClassPath {
    my $self = shift;
    my $classpath = $self->SUPER::getClassPath();
    $classpath = CCM::Util::catpath($classpath, File::Spec->catfile($self->getTomcatHome(), 'bin', 'bootstrap.jar'));
    $classpath = CCM::Util::catpath($classpath, File::Spec->catdir($self->getCCMToolsHome(), 'server', 'tomcat', 'classes'));
    return $classpath;
}

sub getSystemProperties {
    my $self = shift;
    my $props = $self->SUPER::getSystemProperties();
    $props .= " -Djava.endorsed.dirs=" . File::Spec->catdir($self->getTomcatHome(), 'common', 'endorsed');
    $props .= " -Dcatalina.base=" . $self->getTomcatHome();
    $props .= " -Dcatalina.home=" . $self->getTomcatHome();
    return $props;
}

1 # So that the require or use succeeds.

__END__

=back 4

=head1 AUTHORS

=head1 COPYRIGHT

=head1 SEE ALSO

L<perl(1)>

=cut
