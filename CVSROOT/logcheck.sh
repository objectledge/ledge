#! /bin/sh

#
# capture log
#
TEMPFILE=/tmp/cvslog$$.tmp
cat > $TEMPFILE

#
# Invoke spackle
#
$CVSROOT/CVSROOT/log_accum.pl $USER "`echo ${1} | cut -d , -f 1`" < $TEMPFILE

#
# Invoke cvsspam
#
$CVSROOT/CVSROOT/collect_diffs.rb --from $USER --to objectledge-cvshtml@lists.caltha.pl "${1}" < $TEMPFILE

#
# Invoke DamageControl
#
ruby /home/damagecontrol/current/bin/requestbuild --url http://localhost:4712/private/xmlrpc --projectname `echo ${1} | cut -d/ -f1`

#
# clean up
#
rm $TEMPFILE