#!/bin/sh
#
# Copyright 2009-2019, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
# Any commercial use must be negotiated with the Office of Technology Transfer
# at the California Institute of Technology.
#
# This software is subject to U. S. export control laws and regulations
# (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
# is subject to U.S. export control laws and regulations, the recipient has
# the responsibility to obtain export licenses or other export authority as
# may be required before exporting such information to foreign countries or
# providing access to foreign nationals.
#
# $Id$

# Bourne Shell script that allows easy execution of the Search Service Installer
# without the need to set the CLASSPATH or having to type in that long java
# command (java gov.nasa.pds.search.SearchInstaller ...)

# Expects the Search Service jar file to be in the ../lib directory.

# Check if the JAVA_HOME environment variable is set.
if [ -z "${JAVA_HOME}" ]; then
   echo "The JAVA_HOME environment variable is not set." 1>&2
   exit 1
fi

# Setup environment variables.
SCRIPT_DIR=`dirname $0`
PARENT_DIR=`cd ${SCRIPT_DIR}/.. && pwd`
LIB_DIR=${PARENT_DIR}/dist
EXTRA_LIB_DIR=${PARENT_DIR}/lib

# Check for dependencies.
if [ ! -f ${LIB_DIR}/search-service*.jar ]; then
    echo "Cannot find Search Service jar file in ${LIB_DIR}" 1>&2
    exit 1
fi

# Finds the jar file in LIB_DIR and sets it to SEARCH_SERVICE_JAR.
SEARCH_SERVICE_JAR=`ls ${LIB_DIR}/search-service-*.jar`
EXTRA_LIB_JAR=`ls ${EXTRA_LIB_DIR}/*.jar`
EXTRA_LIB_JAR=`echo ${EXTRA_LIB_JAR} | sed 'y/ /:/'`
#echo $SEARCH_SERVICE_JAR
#echo $EXTRA_LIB_JAR
CLASSPATH=$SEARCH_SERVICE_JAR:$EXTRA_LIB_JAR export CLASSPATH

SEARCH_INSTALLER_PRESET_FILE=`ls ${SCRIPT_DIR}/search-service.properties` export SEARCH_INSTALLER_PRESET_FILE
SEARCH_VER=`cat ${PARENT_DIR}/VERSION.txt` export SEARCH_VER

# Executes Search Installer via the executable jar file
# Arguments are passed in to the tool via '$@'
"${JAVA_HOME}"/bin/java gov.nasa.pds.search.SearchInstaller -d 
