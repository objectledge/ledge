#!/bin/sh

if [ $# -lt 2 ]; then
    echo "usage: live_assembly.sh <project name> <includes> [<excludes>]"
    exit 1
fi

SAVE=`pwd`
cd `dirname $0`/../..
WORKSPACE=`pwd`
cd $SAVE

TARGETS=`realpath ${WORKSPACE}`-targets
PROJECT=$1
INCLUDES=$2
EXCLUDES=$3

INCLUDES="^\\(`echo $INCLUDES | sed -e 's/|/\\\\|/g'`\\)"
if [ -n "$EXCLUDES" ]; then
    EXCLUDES="^\\(`echo $EXCLUDES | sed -e 's/|/\\\\|/g'`\\)"
else
    EXCLUDES="NOTMATCHED"
fi

WEBAPP_SRC_PATH=src/main/webapp
WEBAPP_PROJECT_PATH=

WEBAPP_SRC=$WORKSPACE/$PROJECT/$WEBAPP_SRC_PATH
WEBAPP_PROJECT_DIR=${WORKSPACE}/$PROJECT/$WEBAPP_PROJECT_PATH
WEBAPP_DIR=$TARGETS/$PROJECT/${PROJECT}-live
WEBAPP_PROJECT=$WEBAPP_PROJECT_DIR/project.xml
CLASSES_DIR=$WEBAPP_DIR/WEB-INF/classes
LIB_SRC=$TARGETS/$PROJECT/$PROJECT/WEB-INF/lib
LIB_DIR=$WEBAPP_DIR/WEB-INF/lib
LIB_TIMESTAMP=$LIB_DIR/.updated

mkdir -p $CLASSES_DIR
for DIR in `find $WORKSPACE -maxdepth 1`; do
    if basename $DIR | grep $INCLUDES | grep -v $EXCLUDES > /dev/null; then
        if [ -d $DIR/bin ]; then
            cp -ua $DIR/bin/* $CLASSES_DIR >/dev/null 2>&1
        fi
        if [ -d $DIR/src/main/resources ]; then
            cp -ua $DIR/src/main/resources/* $WEBAPP_DIR >/dev/null 2>&1
        fi
    fi
done

if [ ! -f $LIB_TIMESTAMP -o ! -d $LIB_SRC -o $WEBAPP_PROJECT -nt $LIB_TIMESTAMP ]; then
    maven -o -d $WEBAPP_PROJECT_DIR war:webapp
    mkdir -p $LIB_DIR
    for LIB in `find $LIB_SRC -name \*.jar`; do
	if ! basename $LIB | grep $INCLUDES > /dev/null; then
	    cp $LIB $LIB_DIR
        fi
    done
    touch $LIB_TIMESTAMP
fi

if [ -d $WEBAPP_SRC ]; then
    cp -a $WEBAPP_SRC/* $WEBAPP_DIR >/dev/null 2>&1
fi

if [ -d $WEBAPP_SRC/config.local ]; then
    cp $WEBAPP_SRC/config.local/*.xml $WEBAPP_DIR/config >/dev/null 2>&1
fi
