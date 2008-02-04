# CCM::Server
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
# $Id: Server.pm 288 2005-02-22 00:55:45Z sskracic $

=pod

=head1 NAME

CCM:Server

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 METHODS

=over 4

=cut

package CCM::Server;

use vars qw(@ISA);
use CCM::Runtime;
use CCM::Util;

@ISA = qw(CCM::Runtime);

sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my $self = $class->SUPER::new(@_);
    $self->{verbose} = 0;
    $self->{args} = [];
    bless $self, $class;
    return $self;
}

sub name {
    my $self = shift;
    return "";
}

sub args {
    my $self = shift;
    if (@_) {
        $self->{args} = shift;
    }
    return $self->{args};
}

sub verbose {
    my $self = shift;
    if (@_) {
        $self->{verbose} = shift;
    }
    return $self->{verbose};
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
    CCM::Util::setuser();
    $self->run($command);
}

sub getServiceName {
    my $self = shift;
    if (!defined $self->{service_name}) {
	if (open (IN, $self->windowsServiceLockFile())) {
	    $self->{service_name} = <IN>;
	    close IN;
	} else {
	    $self->{service_name} = `ccm get --value waf.web.site_name`;
	    $self->{service_name} =~ s/://g;
	    chomp($self->{service_name});
	    if ($self->name()) {
		$self->{service_name} = $self->name() . "-" . $self->{service_name};
	    }
	}
    }
    return $self->{service_name};
}

sub windowsStart {
    my $self = shift;
    $self->windowsInstall() unless $self->windowsServiceInstalled();
    my $service_name = $self->getServiceName();
    $self->run("net start $service_name");
}

sub windowsStop {
    my $self = shift;
    my $service_name = $self->getServiceName();
    $self->run("net stop $service_name");
}

sub windowsReinstall {
    my $self = shift;
    $self->windowsUninstall(@_);
    $self->windowsInstall(@_);
}

sub windowsServiceLockFile {
    my $self = shift;
    if (!defined $self->{service_lock_file}) {
        $self->{service_lock_file} = File::Spec->catfile($self->getCCMHome(),'data',$self->name() . '_service');
    }
    return $self->{service_lock_file};
}

sub windowsServiceInstalled {
    my $self = shift;
    return (-f $self->windowsServiceLockFile());
}

sub getClassPath {
    my $self = shift;
    my $classpath = $self->SUPER::getClassPath();
    $classpath = CCM::Util::catpath($classpath, File::Spec->catfile($self->getJavaHome(),'lib','tools.jar'));
    return $classpath;
}

sub getServer {
    my $sc = shift;
    if ( ! defined $sc ) {
        $sc = $ENV{'CCM_SERVLET_CONTAINER'};
        if ( ! defined $sc ) {
            CCM::Util::error("servlet container not specified");
        }
    }
    $sc = uc (substr($sc,0,1)) . lc(substr($sc,1));
    my $server = eval "use CCM::Server::$sc; CCM::Server::$sc->new()";
    if ($@) {
        CCM::Util::error("servlet container '$sc' not recognized");
    }
    return $server;
}

sub run {
    my $self = shift;
    if ($self->{verbose} > 0) {
        print "@_\n";
    }
    system @_;
}

sub runExec {
    my $self = shift;
    if ($self->{verbose} > 0) {
        print "@_\n";
    }
    exec @_;
}

1 # So that the require or use succeeds.

__END__

=back 4

=head1 AUTHORS

=head1 COPYRIGHT

=head1 SEE ALSO

L<perl(1)>

=cut
