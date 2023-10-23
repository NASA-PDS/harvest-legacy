#!/bin/sh

# Copyright 2019, California Institute of Technology ("Caltech").
# U.S. Government sponsorship acknowledged.
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice,
# this list of conditions and the following disclaimer.
# * Redistributions must reproduce the above copyright notice, this list of
# conditions and the following disclaimer in the documentation and/or other
# materials provided with the distribution.
# * Neither the name of Caltech nor its operating division, the Jet Propulsion
# Laboratory, nor the names of its contributors may be used to endorse or
# promote products derived from this software without specific prior written
# permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

# Bourne Shell script that allows easy execution of the Registry Installer
# without the need to set the CLASSPATH or having to type in that long java
# command (java gov.nasa.pds.search.RegistryInstaller ...)

# Expects the Registry jar file to be in the ../lib directory.

# Check if the JAVA_HOME environment variable is set.
if [ -z "${JAVA_HOME}" ]; then
   JAVA_CMD=`which java`
   if [ $? -ne 0 ]; then
     echo "JAVA_HOME is not set as an environment variable"
     exit 1
   fi
else
   JAVA_CMD="${JAVA_HOME}"/bin/java
fi

function print_usage() {
  echo ""
  echo "Usage: $0 COMMAND"
  echo ""
  echo "Commands:"
  echo "  install     Install registry into standalone Solr"
  echo "  uninstall   Uninstall all registry components from standalone Solr"
  echo ""
}

# Check number of command line parameters
if [[ $# -eq 0 ]]; then
  print_usage
  exit 1
fi

if [ "$1" == "install" ]; then
	PARAMS="--install"
elif [ "$1" == "uninstall" ]; then
	PARAMS="--uninstall"
else
	  print_usage
	  exit 1
fi

# Setup environment variables.
SCRIPT_DIR=`cd "$( dirname $0 )" && pwd`
PARENT_DIR=`cd ${SCRIPT_DIR}/.. && pwd`
LIB_DIR=${PARENT_DIR}/dist
EXTRA_LIB_DIR=${PARENT_DIR}/lib

REGISTRY=${PARENT_DIR}

# Create Registry Solr Doc Directory
mkdir -p ${REGISTRY}/../registry-data/solr-docs

# Check for dependencies.
if [ ! -f ${LIB_DIR}/registry*.jar ]; then
    echo "Cannot find Registry jar file in ${LIB_DIR}" 1>&2
    exit 1
fi

# Finds the jar file in LIB_DIR and sets it to REGISTRY_JAR.
REGISTRY_JAR=`ls ${LIB_DIR}/registry-*.jar`
EXTRA_LIB_JAR=`ls ${EXTRA_LIB_DIR}/*.jar`
EXTRA_LIB_JAR=`echo ${EXTRA_LIB_JAR} | sed 'y/ /:/'`
#echo $REGISTRY_JAR
#echo $EXTRA_LIB_JAR
CLASSPATH=$REGISTRY_JAR:$EXTRA_LIB_JAR export CLASSPATH

REGISTRY_INSTALLER_PRESET_FILE=`ls ${SCRIPT_DIR}/registry.properties` export REGISTRY_INSTALLER_PRESET_FILE
REGISTRY_VER=`cat ${PARENT_DIR}/VERSION.txt` export REGISTRY_VER

# Executes Registry Installer via the executable jar file
# Arguments are passed in to the tool via '$@'
"${JAVA_HOME}"/bin/java gov.nasa.pds.search.RegistryInstaller $PARAMS
