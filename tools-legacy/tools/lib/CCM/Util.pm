#
# CCM::Util
#
# Copyright (C) 2003 Red Hat, Inc.
#
# $Id: Util.pm 986 2005-11-08 16:47:00Z apevec $

=pod

=head1 NAME

CCM:Util

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 METHODS

=over 4

=cut

package CCM::Util;

use strict;
use vars qw(@ISA @EXPORT_OK);
use File::Spec;
require Exporter;

@ISA = qw(Exporter);
@EXPORT_OK = qw();  # symbols to export on request

sub catpath {
    return &cleanpath(&joinpath(@_));
}

sub splitpath {
    my $path_string = shift;
    if ($^O eq 'MSWin32') {
        return split(';', $path_string);
    } else {
        return split(':', $path_string);
    }
}

sub joinpath {
    my $separator = ':';
    if ($^O eq 'MSWin32') {
        $separator = ';';
    }
    @_ = grep {defined} @_;
    return join ($separator, @_);
}

sub cleanpath {
    my @path = &splitpath(@_);
    # weed out empty entries
    @path = grep (/./, @path);
    # weed out dup entries
    my %cp;
    @path = grep ( { $cp{$_}++ ; $cp{$_} == 1 } @path);
    return &joinpath(@path);
}

sub inpath {
    my $element = shift;
    my @path = &splitpath(@_);
    return grep { $_ eq $element } @path;
}

sub filename {
    my $path_string = shift;
    if ($^O eq 'MSWin32') {
        return (reverse split(/\\/, $path_string))[0];
    } else {
        return (reverse split(/\//, $path_string))[0];
    }
}

sub getRequiredEnvVariable {
    my $variablename = shift or die "no variable name given";

    if ( ! exists $ENV{$variablename} ) {
        &error("$variablename must be set first");
    }

    my $value = $ENV{$variablename};
    $value =~ /^(.*)$/;
    return $value;
}

sub error {
    my $msg = shift;
    my $code = shift || 1;

    print STDERR "$msg\n";

    exit $code;
}

sub warn {
    my $msg = shift;

    print STDERR "$msg\n";
}

sub validatePort {
    my $port = shift;
    my $portName = shift;

    &isNaturalNumber($port) or &error ("$portName value '$port' is not a valid number");
    &isSecurePort($port) and &error ("$portName value '$port' is less than 1024 and can only be used by root");
    &canConnectToPort($port) or &warn ("Cannot connect to port number '$port' of $portName: $!");
}

sub isNaturalNumber {
    my $arg = shift;

    return ( $arg =~ /^\d+$/ );
}

sub isSecurePort {
    my $port = shift;

    if ($^O eq 'MSWin32') {
        return 0;
    } else {
        return ( $port <= 1024 );
    }
}

sub canConnectToPort {
    my $port = shift;

    return (IO::Socket::INET->new( Listen => 1,
                                   Proto => 'tcp',
                                   LocalPort => $port));
}

sub setuser {
    my $user = shift || "servlet";

    if ($^O eq 'MSWin32' || $< != 0) {
        return;
    }
    my $ccm_user = $ENV{'CCM_USER'};
    if (defined $ccm_user) {
        $ccm_user =~ m/^(.*)$/;
        $ccm_user = $1;
    } else {
        $ccm_user = $user;
    }
    if (defined $ccm_user) {
        my ($name,$passwd,$ccm_user_id,$ccm_gid,
            $quota,$comment,$gcos,$dir,$shell,$expire)
          = getpwnam($ccm_user);
        if (! defined $ccm_user_id) {
            print STDERR "user: '$ccm_user' could not be found\n";
            exit 10;
        }
        if ($ccm_user_id == 0) {
            if ($ENV{'CCM_FORCE_RUN_AS_ROOT'} eq "1") {
                print STDERR "WARNING: runinng as root\n";
            } else {
                print STDERR "For security reasons, WAF cannot be run as the root user\n";
                exit 11;
            }
        }
        if ( $ccm_user_id != $< && $< != 0 ) {
            print STDERR "You cannot run WAF as a user other than yourself unless you are root\n";
            exit 12;
        }
        $( = $ccm_gid + 0;
        $) = "$ccm_gid $ccm_gid";
        $< = $ccm_user_id;
        $> = $ccm_user_id;
    }
}

sub versionSort {
    return map { $_->[0] } sort {
        foreach (1..$#{$a}) {
            my $val = $a->[$_] <=> $b->[$_];
            if ($val != 0) {
                return $val;
            }
        }
        $a->[0] cmp $b->[0];
    } map { [$_, split(/\./, (CCM::Util::filename($_)))]; } @_;
}

sub printEnvVars {
    while (@_) {
        my $key = shift;
        my $value = shift;
        if (defined $key) {
            $value = "" unless defined $value;
            $ENV{$key} = $value;
            if ($^O eq 'MSWin32') {
                print "set $key=$value\n";
            } else {
		$value =~ s/\'/\'\'/g;
                print "$key='$value'\n";
                print "export $key\n";
            }
        }
    }
}

1 # So that the require or use succeeds.

__END__

=back 4

=head1 AUTHORS

=head1 COPYRIGHT

=head1 SEE ALSO

L<perl(1)>

=cut
