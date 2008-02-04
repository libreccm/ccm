# CCM::Server::Resin
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
# $Id: Resin.pm 288 2005-02-22 00:55:45Z sskracic $

=pod

=head1 NAME

CCM::Server::Resin

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 METHODS

=over 4

=cut

package CCM::Server::Resin;

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

    $self->{verbose} = 0;

    bless $self, $class;
    return $self;
}

sub name {
    my $self = shift;
    return "resin";
}

sub windowsInstall {
    my $self = shift;
    return if $self->windowsServiceInstalled();
    my $service_name = $self->getServiceName();
    my $command = $self->getCommandLine();
    $self->run("$command -install-as $service_name");
    open (OUT, "> " . $self->windowsServiceLockFile())
        or CCM::Util::error("could not open " . $self->windowsServiceLockFile());
    print OUT $self->getServiceName();
    close OUT;
}

sub windowsUninstall {
    my $self = shift;
    return unless $self->windowsServiceInstalled();
    my $service_name = $self->getServiceName();
    my $command = $self->getCommandLine();
    $self->windowsStop();
    $self->run("$command -remove-as $service_name");
    unlink $self->windowsServiceLockFile();
}

sub getCommandLine {
    my $self = shift;
    my $log_dir = File::Spec->catdir($self->getCCMHome(), 'logs');
    my $server_root = $self->getCCMHome();
    my $classpath = $self->getClassPath();
    my $resin_home = $self->getResinHome();
    my $system_properties = $self->getSystemProperties();
    my $java_opts = defined $ENV{'JAVA_OPTS'} ? $ENV{'JAVA_OPTS'} :
        ( defined $ENV{'JRE_ARGS'} ?
          $ENV{'JRE_ARGS'} :
          "" );
    my $command;
    if ($^O eq 'MSWin32') {
        $command = File::Spec->catfile ($self->getResinHome(), 'bin', 'httpd');
    } else {
        $command = File::Spec->catfile ($self->getResinHome(), 'bin', 'httpd.sh');
        $command .= " -pid " . File::Spec->catfile($log_dir, 'resin.pid');
    }
    if ($classpath ne "") {
        $command .= " -classpath $classpath";
    }
    $command .= " -verbose";
    $command .= " -resin_home " . $self->getResinHome();
    $command .= " -server_root $server_root";
    $command .= " -conf " . File::Spec->catfile($self->getCCMHome(), 'conf', 'resin.conf');
    $command .= " -stdout " . File::Spec->catfile($log_dir, 'resin-stdout.log');
    $command .= " -stderr " . File::Spec->catfile($log_dir, 'resin-stderr.log');
    $command .= " $system_properties $java_opts";
    return $command;
}

sub getResinHome {
    my $self = shift;
    if (!defined $self->{resin_home}) {
        my $resin_home = $ENV{'RESIN_HOME'};
        if (!defined $resin_home) {
            if ( -d '/opt/resin/latest' ) {
                $resin_home = '/opt/resin/latest';
            } else {
                $resin_home = (reverse CCM::Util::versionSort (grep { -d $_ || -l $_ } glob("/opt/resin/2.1*")))[0];
            }
            if (!defined $resin_home) {
                CCM::Util::error ("RESIN_HOME not set and no Resin installation found", 3);
            }
        }
        $self->{resin_home} = $resin_home;
    }
    return $self->{resin_home};
}

1 # So that the require or use succeeds.

__END__

=back 4

=head1 AUTHORS

=head1 COPYRIGHT

=head1 SEE ALSO

L<perl(1)>

=cut
