#! /bin/sh
#
# Make sure the various tests for a successful commit pass.
#

ENABLE_ACLS=yes

#
# Does the committer have access to the repository?
#
if test "x-$ENABLE_ACLS" = "x-yes" ; then
    $CVSROOT/CVSROOT/cvs_acls.pl ${1+"$@"} || exit 1
fi

#
# Tests pass, make the patch ready to commit and report.
#
$CVSROOT/CVSROOT/commit_prep.pl ${1+"$@"} || exit 2

#
# Bridge to cvsspam
#
$CVSROOT/CVSROOT/record_lastdir.rb ${2} || exit 2

exit 0
