#! /bin/sh

#
# capture log
#
TEMPFILE=/tmp/cvslog$$.tmp
cat > $TEMPFILE

#
# Invoke spackle
#
$CVSROOT/CVSROOT/log_accum.pl $USER "${1} ${2}" < $TEMPFILE

REPO=${1}
shift

#
# Invoke cvsspam
#
$CVSROOT/CVSROOT/collect_diffs.rb --from $USER $@ < $TEMPFILE

#
# Invoke DamageControl
#
ruby /home/damagecontrol/current/bin/requestbuild --url http://localhost:4712/private/xmlrpc --projectname `echo $REPO | cut -d / -f 1`

#
# clean up
#
rm $TEMPFILE
