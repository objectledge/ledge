#! /usr/bin/perl -w
#
# $Id: log_accum.pl.in,v 1.30 2002/12/16 05:37:35 coar Exp $
#
# Copyright (c) 2002 by Ken A L Coar.  All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted according to the terms of the Apache
# licence.
#

#
# Perl filter to handle the log messages from the checkin of files in
# a directory.  This script will group the lists of files by log
# message, and mail a single consolidated log message at the end of
# the commit.
#
# This file assumes a pre-commit checking program that leaves the
# names of the first and last commit directories in a temporary file.
#
# Contributed by David Hampton <hampton@cisco.com>
# Roy Fielding removed useless code and added log/mail of new files
# Ken Coar added special processing (i.e., no diffs) for binary files
#

use strict;
require 5.00503;
use vars qw($id $FROM $state $login @files);
use lib $ENV{'CVSROOT'} . '/CVSROOT';
use SpackleConf;

my $sc = new SpackleConf;
my $debug = $sc->param('Debug') || 0;
my $branch_tag;

#
# Setting DEBUG to a non-zero value will enable stackdumping on receipt
# of SIGUSR2, SIGHUP, or SIGTERM, as well as enabling some reports.  This
# sets the default for debugging, which means it can be overridden
# by the spackle.conf file.
#
use constant DEBUG => 0;

#
# Set some default values for overridable options.
#
my $maintainer_email = $sc->param('Maintainer_Email') || 'nobody';
my $IncludeRCSinfo   = $sc->param('IncludeRCSinfo') || 3;

#
# If possible, use MD5 checksums to uniquely identify a patch cluster.
#
use constant HAVE_DIGEST_MD5 =>
    eval {
        require Digest::MD5;
        import Digest::MD5 qw(md5);
    };
if (! HAVE_DIGEST_MD5) {
    use constant HAVE_MD5 =>
        eval {
            require MD5;
        };
}

#
# Enable the debugging stuff..
#
if ($debug) {
    eval {
        require Carp;
    };
    if (! $@) {
        $SIG{'USR2'} = $SIG{'HUP'} = $SIG{'TERM'} =
            sub {
                my $signame = @_;
                Carp::confess("caught signal $signame");
                Carp::cluck('exiting');
                exit(1000);
            };
    }
}

############################################################
#
# Constants
#
############################################################
my $STATE_NONE    = 0;
my $STATE_CHANGED = 1;
my $STATE_ADDED   = 2;
my $STATE_REMOVED = 3;
my $STATE_LOG     = 4;

my $TMPDIR        = $ENV{'TMPDIR'} || '/tmp';
my $FILE_PREFIX   = '#cvs.';

my $LAST_FILE     = "$TMPDIR/${FILE_PREFIX}lastdir";
my $CHANGED_FILE  = "$TMPDIR/${FILE_PREFIX}files.changed";
my $ADDED_FILE    = "$TMPDIR/${FILE_PREFIX}files.added";
my $REMOVED_FILE  = "$TMPDIR/${FILE_PREFIX}files.removed";
my $LOG_FILE      = "$TMPDIR/${FILE_PREFIX}files.log";
my $BRANCH_FILE   = "$TMPDIR/${FILE_PREFIX}files.branch";
my $SUMMARY_FILE  = "$TMPDIR/${FILE_PREFIX}files.summary";

my $CVSROOT       = $ENV{'CVSROOT'};

my $SENDMAIL      = '/usr/sbin/sendmail';
my $CVS           = 'cvs';
my $COMMITLOGS    = 'no';

############################################################
#
# Configurable options
#
############################################################

my $MAIL_TO       = '';
my $rcsidinfo     = $sc->param('IncludeRCSinfo') || 3;

#
# Read the commit message into an array so we can use its attributes
# to make this cluster unique.
#
my @INPUT = ();
while (<STDIN>) {
    chomp;
    push(@INPUT, $_);
}

#
# Figure out where mail should *really* be sent.
#
$MAIL_TO = $sc->mail_to($ARGV[1]);

############################################################
#
# Subroutines
#
############################################################

sub format_names {
    my($dir, @files) = @_;
    my(@lines, $file);

    $lines[0] = sprintf(' %-08s', $dir);
    foreach $file (@files) {
	if (length($lines[$#lines]) + length($file) > 60) {
	    $lines[++$#lines] = sprintf(' %8s', ' ');
	}
	$lines[$#lines] .= ' ' . $file;
    }
    @lines;
}

sub cleanup_tmpfiles {
    my(@files);

    opendir(DIR, $TMPDIR);
    push(@files, grep(/^${FILE_PREFIX}.*\.${id}$/, readdir(DIR)));
    closedir(DIR);
    foreach (@files) {
	unlink("$TMPDIR/$_");
    }
}

sub write_logfile {
    my ($filename, @lines) = @_;

    open(FILE, "> $filename") || die ("Cannot open log file $filename: $!\n");
    print(FILE join("\n", @lines), "\n");
    close(FILE);
}

sub append_to_file {
    my($filename, $dir, @files) = @_;

    if (@files) {
	my(@lines) = format_names($dir, @files);
	open(FILE, ">> $filename") || die ("Cannot open file $filename: $!\n");
	print(FILE join("\n", @lines), "\n");
	close(FILE);
    }
}

sub write_line {
    my($filename, $line) = @_;

    open(FILE, "> $filename") || die("Cannot open file $filename: $!\n");
    print(FILE $line, "\n");
    close(FILE);
}

sub append_line {
    my($filename, $line) = @_;

    open(FILE, ">> $filename") || die("Cannot open file $filename: $!\n");
    print(FILE $line, "\n");
    close(FILE);
}

sub read_line {
    my($filename) = @_;
    my($line);

    open(FILE, "< $filename") || die("Cannot open file $filename: $!\n");
    $line = <FILE>;
    close(FILE);
    chomp($line);
    $line;
}

sub read_file {
    my($filename, $leader) = @_;
    my(@text) = ();

    open(FILE, "< $filename") || return ();
    while (<FILE>) {
	chomp;
	push(@text, sprintf('  %-10s  %s', $leader, $_));
	$leader = '';
    }
    close(FILE);
    @text;
}

sub read_logfile {
    my($filename, $leader) = @_;
    my(@text) = ();

    open(FILE, "< $filename") || die ("Cannot open log file $filename: $!\n");
    while (<FILE>) {
	chomp;
	push(@text, $leader . $_);
    }
    close(FILE);
    @text;
}

#
# do an 'cvs -Qn status' on each file in the arguments, and extract info.
#
sub change_summary {
    my($out, @filenames) = @_;
    my(@revline);
    my($file, $rev, $rcsfile, $line, $delta, $diff);

    while (@filenames) {
	$file = shift @filenames;

	if ("$file" eq '') {
	    next;
	}

	open(RCS, '-|') || exec($CVS, '-Qn', 'status', $file);

	$rev = '';
	$delta = '';
	$rcsfile = '';


	while (<RCS>) {
	    if (/^\s*Repository revision/) {
		chomp;
		@revline = split(' ', $_);
		$rev = $revline[2];
		$rcsfile = $revline[3];
		$rcsfile =~ s,^$CVSROOT/,,;
		$rcsfile =~ s/,v$//;
	    }
	}
	close(RCS);


	if (($rev ne '') && ($rcsfile ne '')) {
	    open(RCS, '-|') || exec($CVS, '-Qn', 'log', "-r$rev", $file);
	    while (<RCS>) {
		if (/^date:/) {
		    chomp;
		    $delta = $_;
		    $delta =~ s/^.*;//;
		    $delta =~ s/^[\s]+lines://;
		}
	    }
	    close(RCS);
	}

	$diff = "\n\n";

	#
	# If this is a binary file, don't try to report a diff; not only is
	# it meaningless, but it also screws up some mailers.  We rely on
	# Perl's 'is this binary' algorithm; it's pretty good.  But not
	# perfect.
	#
	if (($file =~ /\.(?:pdf|gif|jpg|mpg|rtf)$/i) || (-B $file)) {
	    $diff .= "\t<<Binary file>>\n\n";
	}
	else {
	    #
	    # Get the differences between this and the previous revision,
	    # being aware that new files always have revision '1.1' and
	    # new branches always end in '.n.1'.
	    #
	    if ($rev =~ /^(.*)\.([0-9]+)$/) {
		my $prev = $2 - 1;
		my $prev_rev = $1 . '.' . $prev;

		$prev_rev =~ s/\.[0-9]+\.0$//;# Truncate if first rev on branch

		if ($rev eq '1.1') {
		    open(DIFF, '-|')
			|| exec($CVS, '-Qn', 'update', '-p', '-r1.1',
                                $file);
		    $diff .= "Index: $file\n=================================="
			. "=================================\n";
		}
		else {
		    open(DIFF, '-|')
			|| exec($CVS, '-Qn', 'diff', '-u',
                                "-r$prev_rev", "-r$rev", $file);
		}

		while (<DIFF>) {
		    $diff .= $_;
		}
		close(DIFF);
		$diff .= "\n\n";
	    }
	}

	append_line($out, sprintf('%-9s%-12s%s%s', $rev, $delta,
                                  $rcsfile, $diff));
    }
}


sub build_header {
    my($header);
    delete $ENV{'TZ'};
    my($sec, $min, $hour, $mday, $mon, $year) = localtime(time);

    $header = sprintf('%-8s    %02d/%02d/%02d %02d:%02d:%02d',
                      $login, $year%100, $mon+1, $mday,
                      $hour, $min, $sec);
}

# !!! Mailing-list and history file mappings here !!!
#
# If the path we're given begins with a slash, map it to the master
# user.  Otherwise, return undef.
#
sub mlist_map {
    my($path) = @_;

    if ($path =~ m:^([^/]+):) {
	return $1;
    }
    else {
	return undef;
    }
}

sub do_changes_file {
    return if ($COMMITLOGS ne 'yes');

    my($category, @text) = @_;
    my($changes);

    $changes = "$CVSROOT/CVSROOT/commitlogs/$category";

    if (open(CHANGES, ">> $changes")) {
        print(CHANGES join("\n", @text), "\n\n");
        close(CHANGES);
    }
    else {
        warn "Cannot open $changes: $!\n";
    }
}

sub mail_notification {
    my(@text) = @_;

    print 'Mailing the commit message'
        . ($debug ? " (to '$MAIL_TO')" : '')
        . "...\n";

    if (! open(MAIL, "| $SENDMAIL -oi -t")) {
        print "*** Unable to execute sendmail as '$SENDMAIL': $!\n"
            . "*** Commit mail not sent!\n";
    }
    else {
        my $subject = $sc->mail_subject($ARGV[1], $branch_tag);
        print(MAIL "From: $FROM\n");
        print(MAIL "To: $MAIL_TO\n");
        print(MAIL "Subject: $subject\n");
        print(MAIL "\n");
        print(MAIL join("\n", @text));
        close(MAIL);
    }
}

#############################################################
#
# Main Body
#
############################################################
#
# Setup environment
#
umask (002);

#
# Initialize basic variables
#

$state = $STATE_NONE;
$login = $ARGV[0]
    || $ENV{'USER'}
    || getlogin
    || (getpwuid($<))[0]
    || sprintf('uid#%d', $<);
#
# Try to form a unique signature for our temporary files from the
# committing user's name and attributes of the log message.
#
$id = $login . scalar(@INPUT);
if (HAVE_DIGEST_MD5) {
    $id .= md5_hex(@INPUT);
}
elsif (HAVE_MD5) {
    $id .= MD5->hexhash(join('', @INPUT));
}

#
# The following commented lines need examination; they don't do
# anything useful on SourceForge, and maybe the 'From' should be
# just the username in *all* cases..
#
#@pwent = getpwnam($login);
#$login_name = $pwent[5];
#$host = `hostname`;
#chomp($host);
#$FROM = "$login_name <$login\@$host>";
$FROM = $login;
my @files = split(' ', $ARGV[1]);
my @path = split('/', $files[0]);
my $repository = $path[0];
my $dir = ($#path != 0)
    ? join('/', @path[1..$#path])
    : '.';
#print('ARGV  - ', join(':', @ARGV), "\n");
#print('files - ', join(':', @files), "\n");
#print('path  - ', join(':', @path), "\n");
#print('dir   - ', $dir, "\n");
#print('id    - ', $id, "\n");

#
# Map the repository directory to a name for commitlogs.
#
my $mlist = mlist_map($files[0]);

##########################
#
# Make sure that at least commits to the CVSROOT get mailed *some*where..
# to the default owner if nowhere else.
#
if ((! defined($MAIL_TO)) && ($mlist eq 'CVSROOT')) {
    $MAIL_TO = ('nobody' || 'Ken.Coar@Golux.Com');
}

##########################
#
# Check for a new directory first.  This will always appear as a
# single item in the argument list, and an empty log message.
#
if ($ARGV[1] =~ /New directory/) {
    my $header = &build_header;
    my @text;
    push(@text, $header);
    push(@text, '');
    push(@text, '  ' . $ARGV[1]);
    do_changes_file($mlist, @text);
    mail_notification(@text) if defined($MAIL_TO);
    exit(0);
}

#
# Iterate over the body of the message collecting information.
#
my(@branch_lines, @log_lines,
   @changed_files, @added_files, @removed_files);
for (@INPUT) {
    if (/^Revision\/Branch:/) {
        s,^Revision/Branch:,,;
        push (@branch_lines, split);
        next;
    }
    if (/^\s+Tag:\s+(\S+)/ && ($state != $STATE_LOG))
                           { $branch_tag = $1;        next; }
    if (/^Modified Files/) { $state = $STATE_CHANGED; next; }
    if (/^Added Files/)    { $state = $STATE_ADDED;   next; }
    if (/^Removed Files/)  { $state = $STATE_REMOVED; next; }
    if (/^Log Message/)    { $state = $STATE_LOG;     next; }
    s/[\s\n]+$//;		# delete trailing space

    push (@changed_files, split) if ($state == $STATE_CHANGED);
    push (@added_files,   split) if ($state == $STATE_ADDED);
    push (@removed_files, split) if ($state == $STATE_REMOVED);
    if ($state == $STATE_LOG) {
	if (/^PR:$/i ||
	    /^Reviewed by:$/i ||
	    /^Submitted by:$/i ||
	    /^Obtained from:$/i) {
	    next;
	}
	push (@log_lines,     $_);
    }
}

#
# Strip leading and trailing blank lines from the log message.  Also
# compress multiple blank lines in the body of the message down to a
# single blank line.
# (Note, this only does the mail and changes log, not the rcs log).
#
my $i;
while ($#log_lines > -1) {
    last if ($log_lines[0] ne '');
    shift(@log_lines);
}
while ($#log_lines > -1) {
    last if ($log_lines[$#log_lines] ne '');
    pop(@log_lines);
}
for ($i = $#log_lines; $i > 0; $i--) {
    if (($log_lines[$i - 1] eq '') && ($log_lines[$i] eq '')) {
	splice(@log_lines, $i, 1);
    }
}

#
# Find the log file that matches this log message
#
for ($i = 0; ; $i++) {
    last if (! -e "$LOG_FILE.$i.$id");
    my @text = read_logfile("$LOG_FILE.$i.$id", '');
    last if ($#text == -1);
    last if (join(" ", @log_lines) eq join(" ", @text));
}

#
# Spit out the information gathered in this pass.
#
write_logfile("$LOG_FILE.$i.$id", @log_lines);
append_to_file("$BRANCH_FILE.$i.$id",  $dir, @branch_lines);
append_to_file("$ADDED_FILE.$i.$id",   $dir, @added_files);
append_to_file("$CHANGED_FILE.$i.$id", $dir, @changed_files);
append_to_file("$REMOVED_FILE.$i.$id", $dir, @removed_files);
if ($rcsidinfo != 0) {
    change_summary("$SUMMARY_FILE.$i.$id", (@changed_files, @added_files));
}

#
# Check whether this is the last directory.  If not, quit.
#
if (-e "$LAST_FILE.$id") {
    $_ = read_line("$LAST_FILE.$id");
    my $tmpfiles = $files[0];
    $tmpfiles =~ s,([^a-zA-Z0-9_/]),\\$1,g;
    if (! grep(/$tmpfiles$/, $_)) {
	print "More commits to come...\n";
	exit(0);
    }
}

#
# This is it.  The commits are all finished.  Lump everything together
# into a single message, fire a copy off to the mailing list, and drop
# it on the end of the Changes file.
#

my $header = build_header();

#
# Produce the final compilation of the log messages
#
my @text = ($header, '');
for ($i = 0; ; $i++) {
    last if (! -e "$LOG_FILE.$i.$id");
    push(@text, read_file("$BRANCH_FILE.$i.$id", "Branch:"));
    push(@text, read_file("$CHANGED_FILE.$i.$id", "Modified:"));
    push(@text, read_file("$ADDED_FILE.$i.$id", "Added:"));
    push(@text, read_file("$REMOVED_FILE.$i.$id", "Removed:"));
    push(@text, "  Log:");
    push(@text, read_logfile("$LOG_FILE.$i.$id", "  "));
    if ($rcsidinfo == 3) {
	if (-e "$SUMMARY_FILE.$i.$id") {
	    push(@text, "  ");
	    push(@text, "  Revision  Changes    Path");
	    push(@text, read_logfile("$SUMMARY_FILE.$i.$id", "  "));
	}
    }
    push(@text, '');
}
#
# Append the log message to the commitlogs/<module> file
#
do_changes_file($mlist, @text);
#
# Now generate the extra info for the mail message..
#
if ($rcsidinfo == 1) {
    my $revhdr = 0;
    for ($i = 0; ; $i++) {
	last if (! -e "$LOG_FILE.$i.$id");
	if (-e "$SUMMARY_FILE.$i.$id") {
	    if (!$revhdr++) {
		push(@text, "Revision  Changes    Path");
	    }
	    push(@text, read_logfile("$SUMMARY_FILE.$i.$id", ''));
	}
    }
    if ($revhdr) {
	push(@text, '');	# consistancy...
    }
}
#
# Mail out the notification.
#
mail_notification(@text) if defined($MAIL_TO);
cleanup_tmpfiles;
exit 0;
