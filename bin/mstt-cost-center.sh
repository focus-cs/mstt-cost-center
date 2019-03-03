#!/bin/bash
#
# Copyright 2011 Sciforma. Tous droits réservés.
#
# run-sncf-application-report-charge.sh
#
#! /bin/bash
#
# Copyright (c) 2018 SciForma. All right reserved.
#
# Author Raffi JARIAN
#

# Get arguments
BATCH_PATH=$1
BATCH_MAIN=$2
SCIFORMA_URL=$3


# set folder path
ROOT_DIR=C:/API/mstt-cost-center
LIB_DIR=$ROOT_DIR/lib
CONF_DIR=$ROOT_DIR/conf

# Open lib directory
cd $LIB_DIR

# Delete PSClient librairies
if test -f PSClient* ; then
        rm -f PSClient*
fi

if test -f utilities* ; then
        rm -f utilities*
fi

# Get last Sciforma librairies
wget -O utilities.jar $SCIFORMA_URL/utilities.jar
wget -O PSClient_en.jar $SCIFORMA_URL/PSClient_en.jar
wget -O PSClient.jar $SCIFORMA_URL/PSClient.jar

# Open root directory
cd $ROOT_DIR

# Set Java arguments with log4j2 configuration file
JAVA_ARGS="-showversion"
JAVA_ARGS=${JAVA_ARGS}" -Djava.awt.headless=true"
JAVA_ARGS=${JAVA_ARGS}" -Dlog4j.overwrite=true"
JAVA_ARGS=${JAVA_ARGS}" -Dlog4j.configuration=%CONF_DIR%/log4j.properties"
JAVA_ARGS=${JAVA_ARGS}" -Xmx6000m"
JAVA_ARGS=${JAVA_ARGS}" -Dfile.encoding=UTF-8"
JAVA_ARGS=${JAVA_ARGS}" -Duse_description=true"
JAVA_ARGS=${JAVA_ARGS}" -cp $(echo lib/*.jar | tr ' ' ':')"

# Launch the application
java $JAVA_ARGS $BATCH_MAIN $ROOT_DIR $CONF_DIR/psconnect.properties $CONF_DIR/log4j.properties

